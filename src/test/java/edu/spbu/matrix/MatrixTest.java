package edu.spbu.matrix;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MatrixTest
{
  @Test
  public void mulSD()//тест для умножения Sparse на Dense 1 поток
  {
    Matrix m1 = new SparseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m2.txt");
    Matrix expected = new DenseMatrix("result.txt");
    assertEquals(expected, m1.mul(m2));
  }
  @Test
  public void mulDS() //тест для умножения Dense на Sparse 1 поток
  {
    Matrix m1 = new DenseMatrix("m1.txt");
    Matrix m2 = new SparseMatrix("m2.txt");
    Matrix expected = new DenseMatrix("result.txt");
    assertEquals(expected, m1.mul(m2));
  }
  @Test
  public void mulSS()//тест для  умножения Sparse на Sparse 1 поток
  {
    Matrix m1 = new SparseMatrix("m1.txt");
    Matrix m2 = new SparseMatrix("m2.txt");
    Matrix expected = new SparseMatrix("result.txt");
    assertEquals(expected, m1.mul(m2));
  }
  @Test
  public void mulDD()//тест для умножения Dense на Dense 1 поток
  {
    Matrix m1 = new DenseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m2.txt");
    Matrix expected = new DenseMatrix("result.txt");
    assertEquals(expected, m1.mul(m2));
  }
  @Test
  public void dmulDD() // dense на dense. Кастомное кол-во потоков
  {
    Matrix m1 = new DenseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m2.txt");
    Matrix expected = new DenseMatrix("result.txt");
    assertEquals(expected, m1.dmul(m2));
  }
  @Test
  public void dmulSS() // sparse на sparse. Кастомное кол-во потоков
  {
    Matrix m1 = new SparseMatrix("m1.txt");
    Matrix m2 = new SparseMatrix("m2.txt");
    Matrix expected = new SparseMatrix("result.txt");
    Matrix m3 = m1.dmul(m2);
    assertEquals(expected, m3);
  }
}
