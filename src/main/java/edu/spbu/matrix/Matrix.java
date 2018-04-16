package edu.spbu.matrix;
import java.io.IOException;
/**
 *
 */
public interface Matrix
{

  Matrix mul(Matrix o);


  Matrix dmul(Matrix o);

}
