/*
    Copyright (c) Peter Sorotokin, 1998-2000
    See file "License.html" for terms of usage and
    redistribution.   
*/
package kmy.regex.form;

import java.io.Writer;

import kmy.jint.lang.CharString;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

public class DateTimeFormatterSpan extends FormatterSpan
{

  static DateFormat mediumFormat = 
          DateFormat.getDateTimeInstance( DateFormat.MEDIUM, DateFormat.MEDIUM );
  static DateFormat longFormat = 
          DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.LONG );

  DateFormat  javaFormat;

  public DateTimeFormatterSpan( int min, int max, int alignment,
	     char fillChar, int overflowChar, boolean longer, String javaFormatStr )
  {
    super( min, max, alignment, fillChar, overflowChar );
    if( javaFormatStr != null )
      javaFormat = new SimpleDateFormat( javaFormatStr );
    else
      javaFormat = (longer ? longFormat : mediumFormat);
  }

  public void printf( Writer out, int v )
  {
    printf( out, new Date(v) );
  }

  public void printf( Writer out, long v )
  {
    printf( out, new Date(v) );
  }

  public void printf( Writer out, Object obj )
  {
    super.printf( out, javaFormat.format(obj) );
  }

}
