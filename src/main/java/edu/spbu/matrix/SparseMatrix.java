package edu.spbu.matrix;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Разряженная матрица
 */
public class SparseMatrix implements Matrix
{

  public HashMap<Integer, HashMap<Integer, Double>> MainMatrix; // Матрица в формате COO
  public int r;
  public int c;
  public SparseMatrix(String fileName)//констуктор по файлу
  {
    try {
      r=0;
      c=0;
      HashMap<Integer, HashMap<Integer, Double>> res = new HashMap<Integer, HashMap<Integer, Double>>();
      File f= new File(fileName);
      Scanner input = new Scanner(f);
      String[] line = {};
      HashMap<Integer, Double> buf;
      while (input.hasNextLine()) {
        buf = new HashMap<Integer, Double>();
        line = input.nextLine().split(" ");
        for (int i=0; i<line.length; i++) {
          if (line[i]!="0") {
            buf.put(i, Double.parseDouble(line[i]));
          }
        }
        if (buf.size()!=0) {
          res.put(r++, buf);
        }
      }
      c = line.length;
      MainMatrix = res;


    }
    catch(IOException e)
    {
      e.printStackTrace();
    }

  }
  public SparseMatrix(HashMap<Integer, HashMap<Integer, Double>> matrix, int row, int column)//контструктор по экземпляру
  {
    this.MainMatrix = matrix;
    this.r = row;
    this.c = column;
  }
  @Override public Matrix mul(Matrix o)//однопоточное умножение
  {
    if (o instanceof DenseMatrix) {
      return mul((DenseMatrix) o);
    }
    if (o instanceof SparseMatrix) {
      return mul((SparseMatrix) o);
    }
    else {
      return null;
    }
  }
  public SparseMatrix transpose ()//транспонирование sparse
  {
    HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
    for (HashMap.Entry<Integer, HashMap<Integer, Double>> row : MainMatrix.entrySet()){
      for (HashMap.Entry<Integer, Double> elem : row.getValue().entrySet()) {
        if (!result.containsKey(elem.getKey())) {
          result.put(elem.getKey(), new HashMap<Integer, Double>());
        }
        result.get(elem.getKey()).put(row.getKey(), elem.getValue());
      }
    }
    return new SparseMatrix(result, c, r);
  }
  private SparseMatrix mul(SparseMatrix Smatr)//умножение на sparse
  {
    HashMap<Integer, HashMap<Integer, Double>> res = new HashMap<Integer, HashMap<Integer, Double>>();
    SparseMatrix sT = Smatr.transpose();
    double buf = 0;
    for (HashMap.Entry<Integer, HashMap<Integer, Double>> row1 : MainMatrix.entrySet()){
      for (HashMap.Entry<Integer, HashMap<Integer, Double>> row2 : sT.MainMatrix.entrySet()) {
        for (HashMap.Entry<Integer, Double> elem : row1.getValue().entrySet()) {
          if (row2.getValue().containsKey(elem.getKey())) {
            buf += elem.getValue()*row2.getValue().get(elem.getKey());
          }
        }

        if (buf != 0) {
          if (!res.containsKey(row1.getKey())) {
            res.put(row1.getKey(), new HashMap<Integer, Double>());
          }
          res.get(row1.getKey()).put(row2.getKey(), buf);
        }
        buf = 0;
      }
    }
    return new SparseMatrix(res, r, Smatr.c);
  }
  private DenseMatrix mul(DenseMatrix Dmatr) // умножение на dense
  {

    double[][] result = new double[r][Dmatr.r];
    double sum = 0;
    for (Map.Entry<Integer, HashMap<Integer, Double>> row1 : MainMatrix.entrySet()){
      for (int j = 0; j<Dmatr.r; j++) {
        for (HashMap.Entry<Integer, Double> elem : row1.getValue().entrySet()) {
          if (row1.getValue().containsKey(elem.getKey())) {
            sum += elem.getValue()*Dmatr.MainMatrix[elem.getKey()][j];
          }
        }
        result[row1.getKey()][j] = sum;
        sum = 0;
      }
    }
    return new DenseMatrix(result);
  }
  @Override  public Matrix dmul(Matrix o) // многопоточное умножение
  {
    SparseMatrix Smatr = (SparseMatrix)o;

    class Dispatcher {
      int value = 0;
      public int next() {
        synchronized (this) {
          return value++;
        }
      }
    }

    final  Dispatcher dispatcher = new Dispatcher();


    final SparseMatrix sT = Smatr.transpose();

    ConcurrentHashMap<Integer, HashMap<Integer, Double>> cmap = new ConcurrentHashMap<Integer, HashMap<Integer, Double>>();
    for (int i=0; i<r; i++) {
      if (MainMatrix.containsKey(i)) {
        cmap.put(i, new HashMap<Integer, Double>(MainMatrix.get(i)));
      }
    }

    final ConcurrentHashMap<Integer, HashMap<Integer, Double>> csT = new ConcurrentHashMap<Integer, HashMap<Integer, Double>>();
    for (int i=0; i<sT.r; i++) {
      if (sT.MainMatrix.containsKey(i)) {
        csT.put(i, sT.MainMatrix.get(i));
      }
    }

    HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
    final ConcurrentHashMap<Integer, HashMap<Integer, Double>> cresult = new ConcurrentHashMap<Integer, HashMap<Integer, Double>>();

    double sum = 0;
    class ParTR implements Runnable {

      Thread thread;
      HashMap<Integer, Double> buf = new HashMap<Integer, Double>();

      public ParTR(String s) {
        thread = new Thread(this, s);
        thread.start();//
      }
      public void run() {
        int i;
        while ((i = dispatcher.next()) < r) {
          double sum = 0;
          if (MainMatrix.containsKey(i)) {
            buf = new HashMap<Integer, Double>();
            for (int j = 0; j < sT.r; j++) {
              if (csT.containsKey(j)) {
                for (int k = 0; k < sT.c; k++) {
                  if (csT.get(j).containsKey(k) && MainMatrix.get(i).containsKey(k)) {
                    sum += csT.get(j).get(k) * MainMatrix.get(i).get(k);
                  }
                }
                if (sum != 0) {
                  buf.put(j, sum);
                }
                sum = 0;
              }
            }
            cresult.put(i, buf);
          }
        }
      }
    }

    ParTR one = new ParTR("one");
    ParTR two = new ParTR("two");
    ParTR three = new ParTR("three");
    ParTR four = new ParTR("four");

    try {
      one.thread.join();// ожидать завершение потока
      two.thread.join();
      three.thread.join();
      four.thread.join();

    } catch (InterruptedException e){
      e.printStackTrace();
    }
    HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();
    for (ConcurrentHashMap.Entry<Integer, HashMap<Integer, Double>> row1 : cresult.entrySet()){
      tmp = new HashMap<Integer, Double>();
      for (HashMap.Entry<Integer, Double> row2 : row1.getValue().entrySet()) {
        tmp.put(row2.getKey(), row2.getValue());
      }
      result.put(row1.getKey(), tmp);
    }

    return new SparseMatrix(result, r, Smatr.c);
  }
  @Override public boolean equals(Object o)//сравнение
  {
    if (o instanceof DenseMatrix) //сравнение  с dense
    {
      DenseMatrix tmp = (DenseMatrix)o;
      if (tmp.MainMatrix.length == r && tmp.MainMatrix[0].length == c) {
        for (int i = 0; i<r; i++) {
          if (MainMatrix.containsKey(i)) {
            for (int j = 0; j<c; j++) {
              if (MainMatrix.get(i).containsKey(j)) {
                if (MainMatrix.get(i).get(j) != tmp.MainMatrix[i][j]) {
                 return false;
                }
              } else {
                if (tmp.MainMatrix[i][j] != 0) {
                  return false;
                }
              }
            }
          } else {
            for (int j = 0; j < c; j++) {
              if (tmp.MainMatrix[i][j] != 0) {
               return  false;
              }
            }
          }
        }
      } else {
        return false;
      }
    } else if (o instanceof SparseMatrix) // сравнение с sparse
    {
      SparseMatrix tmp = (SparseMatrix) o;
      if (tmp.c == c && tmp.r == r) {
        for (int i = 0; i<r; i++) {
          if (MainMatrix.containsKey(i) && tmp.MainMatrix.containsKey(i))  {
            for (int j = 0; j<c; j++) {
              if (MainMatrix.get(i).containsKey(j) && tmp.MainMatrix.get(i).containsKey(j)) {
                if (MainMatrix.get(i).get(j).doubleValue() != tmp.MainMatrix.get(i).get(j).doubleValue()) {
                  return false;
                }
              } else if (MainMatrix.get(i).containsKey(j) || tmp.MainMatrix.get(i).containsKey(j)) {
                return false;
              }
            }
          } else if (MainMatrix.containsKey(i) || tmp.MainMatrix.containsKey(i)) {
            return false;
          }
        }
      } else {
      return false;
      }
    }
    return true;
  }
}