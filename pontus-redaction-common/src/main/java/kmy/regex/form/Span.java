/*
    Copyright (c) Peter Sorotokin, 1998-2000
    See file "License.html" for terms of usage and
    redistribution.   
*/
package kmy.regex.form;

import java.io.Serializable;
import java.io.Writer;

abstract public class Span implements Cloneable, Serializable
{

  abstract public void print( Writer out, int[] argPtr, Object[] args );

  public Object clone()
  {
    try
      {
	return super.clone();
      }
    catch( CloneNotSupportedException e )
      {
	throw new RuntimeException( 
	   "Internal error: Unexpected exception: " + e.getMessage() );
      }
  }
}
