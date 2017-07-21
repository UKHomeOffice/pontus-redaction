/*
    Copyright (c) Peter Sorotokin, 1998-2000
    See file "License.html" for terms of usage and
    redistribution.   
*/
package kmy.regex.form;

import java.math.*;
import java.io.Writer;

import kmy.jint.lang.CharString;

import java.text.DecimalFormat;

public class NumberFormatterSpan extends FormatterSpan
{
  DecimalFormat  javaFormat;

  public NumberFormatterSpan( int min, int max, int alignment,
			 char fillChar, int overflowChar, String javaFormatStr )
  {
    super( min, max, alignment, fillChar, overflowChar );
    if( javaFormatStr != null )
      javaFormat = new DecimalFormat( javaFormatStr );
  }

  public NumberFormatterSpan( int min, int alignment )
  {
    super( min, alignment );
  }

  public void printf( Writer out, int v )
  {
    if( javaFormat != null )
      super.printf( out, javaFormat.format(v) );
    else
      super.printf( out, Integer.toString(v).toCharArray() );
  }

  public void printf( Writer out, long v )
  {
    if( javaFormat != null )
      super.printf( out, javaFormat.format(v) );
    else
      super.printf( out, Long.toString(v).toCharArray() );
  }

  public void printf( Writer out, char v )
  {
    if( javaFormat != null )
      super.printf( out, javaFormat.format((int)v) );
    else
      printf( out, (int)v );
  }

  public void printf( Writer out, char[] v )
  {
    super.printf( out, v );
  }

  public void printf( Writer out, double v )
  {
    if( javaFormat != null )
      super.printf( out, javaFormat.format(v) );
    else
      printf( out, (long)Math.round(v) );
  }

  public void printf( Writer out, float v )
  {
    if( javaFormat != null )
      super.printf( out, javaFormat.format(v) );
    else
      printf( out, (long)Math.round(v) );
  }

  public void printf( Writer out, CharString cs )
  {
    printf( out, cs.toString() );
  }

  public void printf( Writer out, String s )
  {
    printf( out, (new Double(s)).doubleValue() );
  }

  protected void printfString( Writer out, String s )
  {
    super.printf( out, s );
  }

  public void printf( Writer out, Object obj )
  {
    if( obj instanceof Number )
      super.printf( out, obj );
    else
      printf( out, obj.toString() );
  }

}
