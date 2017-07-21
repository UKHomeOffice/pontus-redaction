/*
    Copyright (c) Peter Sorotokin, 1998-2000
    See file "License.html" for terms of usage and
    redistribution.   
*/
package kmy.regex.tools;

import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.Enumeration;

import kmy.regex.util.Replacer;

public class Subst
{
  private static void processInput( BufferedReader in, Replacer rep )
    throws IOException
  {
    String line;
    while( (line = in.readLine()) != null )
      System.out.println( rep.process( line ) );
  }

  public static void main( String[] args )
  {
    int last = 0;
    int flags = 0;
    while( last < args.length && args[last].startsWith( "-" ) )
      {
	String option = args[last++];
	if( option.equals( "--" ) )
	  break;
	if( option.equals( "-g" ) || option.equals( "--global" ) )
	  flags |= Replacer.GLOBAL_FLAG;
	else
	  last = args.length; // to force help info
      }
    if( args.length == last )
      {
	System.err.println( "Usage: java kmy.regex.util.Subst regex rep [ file ... ]" );
	System.err.println( "\tuse perl syntax for regular expressions" );
	System.err.println();
	System.err.println( "Options:" );
	System.err.println( "\t-g  --global  - replace all occurrences, not only first in line" );
	return;
      }
    Replacer rep;
    try
      {
	String re = args[last++];
	String sub = args[last++];
	rep = new Replacer( re, sub, flags );
      }
    catch( Exception e )
      {
	e.printStackTrace();
	return;
      }

    boolean processStdin = last == args.length;

    do
      {
	try
	  {	
	    Reader input;
	    if( processStdin || args[last].equals( "-" ) )
	      input = new InputStreamReader( System.in );
	    else
	      {
		input = new FileReader( args[last] );
	      }
	    BufferedReader lineReader = new BufferedReader( input );
	    processInput( lineReader, rep );
	  }
	catch( IOException e )
	  {
	    System.err.println( "File reading error: " + e.getMessage() );
	  }
	last++;
      }
    while( last < args.length );
  }
}
