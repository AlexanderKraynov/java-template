package edu.spbu.matrix;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix
{
  public double [][] MainMatrix;
  public int r;//ряды
  public int c;//колонны

  public DenseMatrix(String fileName)//конструктор по файлу
  {
      try {
        File f = new File(fileName);
        Scanner input = new Scanner(f);
        String[] line;
        ArrayList<Double[]> a = new ArrayList<Double[]>();
        Double[] tmp = {};
        while (input.hasNextLine()) {
          line = input.nextLine().split(" ");
          tmp = new Double[line.length];
          for (int i=0; i<tmp.length; i++) {
            tmp[i] = Double.parseDouble(line[i]);
          }
          a.add(tmp);
        }
        double[][] result = new double[a.size()][tmp.length];
        for (int i=0; i<result.length; i++) {
          for (int j=0; j<result[0].length; j++) {
            result[i][j] = a.get(i)[j];
          }
        }
        MainMatrix = result;
        this.r = result.length;
        this.c = result[0].length;
      } catch(IOException e) {
        e.printStackTrace();
      }

    }
  public DenseMatrix(double[][] matr)//конструктор по экземпляру
  {
      this.MainMatrix = matr;
      this.r=matr.length;
      this.c=matr[0].length;

    }
  @Override public Matrix mul(Matrix o)//однопоточное умножение
  {
    if (o instanceof DenseMatrix) {
      return mul((DenseMatrix) o);
    }
    if (o instanceof SparseMatrix) {
      return mul((SparseMatrix) o);
    } else return null;
  }
  private DenseMatrix mul(DenseMatrix Dmatr)//умножение на dense
  {
    int m = r;
    int n = Dmatr.c;
    int o = Dmatr.r;
    double[][] res = new double[m][n];



    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        for (int k = 0; k < o; k++) {
          res[i][j] += MainMatrix[i][k] * Dmatr.MainMatrix[k][j];
        }
      }
    }
  return new DenseMatrix(res);
  }
  private DenseMatrix mul(SparseMatrix Smatr) //умножение на sparse
  {
    SparseMatrix sT = Smatr.transpose();
    double[][] result = new double[r][Smatr.c];
    double sum = 0;
    for (int i = 0; i<r; i++){
      for (HashMap.Entry<Integer, HashMap<Integer, Double>> row2 : sT.MainMatrix.entrySet()) {
        for (int k = 0; k<c; k++) {
          if (row2.getValue().containsKey(k)) {
            sum += MainMatrix[i][k]*row2.getValue().get(k);
          }
        }
        result[i][row2.getKey()] = sum;
        sum = 0;
      }
    }
    return new DenseMatrix(result);
  }
  @Override public DenseMatrix dmul(Matrix o) //многопоточное умножение
  {
    DenseMatrix Dmatr = (DenseMatrix)o;
    class Dispatcher {
      int value = 0;
      public int next() {
        synchronized (this) {
          return value++;
        }
      }
    }

    final double[][] result = new double[MainMatrix.length][Dmatr.c];

    final Dispatcher dispatcher = new Dispatcher();

    class ParTR implements Runnable {
      Thread thread;

      public ParTR(String s) {
        thread = new Thread(this, s);
        thread.start();
      }
      public void run() {
        int i = dispatcher.next();
        for (int j=0; j<Dmatr.r; j++) {
          for (int k=0; k<Dmatr.c; k++) {
            result[i][j] += MainMatrix[i][k]*Dmatr.MainMatrix[k][j];
          }
        }
      }
    }

    ParTR one = new ParTR("one");
    ParTR two = new ParTR("two");
    ParTR three = new ParTR("three");
    ParTR four = new ParTR("four");

    try {
      one.thread.join();
      two.thread.join();
      three.thread.join();
      four.thread.join();

    } catch (InterruptedException e){
      e.printStackTrace();
    }

    return new DenseMatrix(result);
  }
  @Override public boolean equals(Object o) //сравнение
  {
    if (o instanceof DenseMatrix)//сравнение с dense
    {
      DenseMatrix Dmatr = (DenseMatrix)o;
      if ((r==Dmatr.r)&&(c==Dmatr.c))
      {
        for(int i = 0; i<r;i++)
        {
          for(int j = 0;j<c ;j++)
          {
            if (MainMatrix[i][j]!=Dmatr.MainMatrix[i][j])
            {
              return false;
            }
          }
        }
      }
      return true;
    }
    else if (o instanceof SparseMatrix)//сравнение со sparse
    {
      SparseMatrix tmp = (SparseMatrix)o;
      if (r == tmp.r && c == tmp.c) {
        for (int i = 0; i<tmp.r; i++) {
          if (tmp.MainMatrix.containsKey(i)) {
            for (int j = 0; j<tmp.c; j++) {
              if (tmp.MainMatrix.get(i).containsKey(j)) {
                if (tmp.MainMatrix.get(i).get(j) != MainMatrix[i][j]) {
                  return false;
                }
              } else {
                if (MainMatrix[i][j] != 0) {
                  return false;
                }
              }
            }
          } else {
            for (int j = 0; j < tmp.c; j++) {
              if (MainMatrix[i][j] != 0) {
                return false;
              }
            }
          }
        }
      } else {
        return false;
      }
    }
    return true;
  }

}