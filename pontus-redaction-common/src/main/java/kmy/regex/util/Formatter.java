/*
    Copyright (c) Peter Sorotokin, 1998-2000
    See file "License.html" for terms of usage and
    redistribution.   
*/
package kmy.regex.util;

import java.io.Writer;
import java.io.StringWriter;
import java.io.Serializable;
import java.util.Vector;

import kmy.regex.form.*;

import kmy.jint.lang.ArgList;

/**
 *  Formatter is a simple general-purpose output formatter.
 *  It uses simple printf-like format specification (passed to its constructor)
 *  to convert a list of values into character string.
 */
public class Formatter implements Cloneable, Serializable
{
  Span[]    spans;
  String[]  vars;

  public Formatter( String form, boolean varsAllowed )
  {
    Vector varList;
    if( varsAllowed )
      varList = new Vector();
    else
      varList = null;
    spans = FParser.parse( form, varList );
    if( varsAllowed && varList.size() > 0 )
      {
	vars = new String[varList.size()];
	varList.copyInto( vars );
      }
  }

  public Formatter( String form )
  {
    this( form, false );
  }

  public String[] getVariables()
  {
    return vars;
  }

  public void printf( Writer out, ArgList argList )
  {
    Object[] list = ( argList == null ? null : argList.getList() );
    int[] ptr = { 0 };
    for( int i = 0 ; i < spans.length ; i++ )
      spans[i].print( out, ptr, list );
  }

  public static void printf( Formatter form, ArgList list )
  {
    System.out.println( sprintf( form, list ) );
  }

  public static void printf( String form, ArgList list )
  {
    System.out.println( sprintf( new Formatter(form), list ) );
  }

  public static String sprintf( Formatter form, ArgList list )
  {
    StringWriter sw = new StringWriter();
    form.printf( sw, list );
    return sw.toString();
  }

  public static String sprintf( String form, ArgList list )
  {
    return sprintf( new Formatter( form ), list );
  }

  public String sprintf( ArgList list )
  {
    StringWriter sw = new StringWriter();
    printf( sw, list );
    return sw.toString();
  }

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
