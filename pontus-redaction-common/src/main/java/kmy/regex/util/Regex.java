/*
    Copyright (c) Peter Sorotokin, 1998-2000
    See file "License.html" for terms of usage and
    redistribution.   
*/
package kmy.regex.util;

import java.util.Enumeration;
import java.io.FilenameFilter;
import java.io.File;

import kmy.jint.lang.CharString;

/**
 *  Regular expression interface definition and convenience methods.
 *  Interface focuses on efficiency and convenience methods can be used when
 *  simplicity is more important (most of the times).
 *  <p>
 *  Regex can be used either for <i>search</i> or for <i>matching</i>.
 *  Matching determines if string maches regex exactly and search determines
 *  if there is a substring of the given string that matches the regex.
 *  There is matchWhole and match methods; matchWhole is "pure" match,
 *  it tries to match the whole string; match tries to match beginning of
 *  the string.
 *  <p>
 *  Regex can contain embedded variables of two types. External variables
 *  are referenced with dollar sign ($), but not 'at' sign (@). They must be
 *  assigned prior to search or matching. Internal variables are either
 *  implicit (1, 2, etc) - those refer to unnamed subexpressions in brackets
 *  (for example regex (.)(.)=\1\2 contains two implicit variables: 1 and 2), or
 *  explicit - those are given names (with '@' constructions). Internal variables
 *  may be assigned values in the process of matching and their values can be
 *  obtained from the regex that was used for search or matching.
 *  <p>
 *  Regex contains some "working space" for the matching process. Therefore
 *  between the match/search call and till all information about match/search is
 *  obtained regex should be used only by a single thread. Use clone() or
 *  cloneRegex() to get a separate copy of it.
 *  <p>
 *  All 'end' or 'final' substring positions or indices in the buffer refer to the first
 *  character <b>after</b> substring.
 */
public abstract class Regex implements Cloneable, FilenameFilter
{
  private static RegexFactory factory;

  /**
   *  Returns variable index handle that can be used later to get or set
   *  variable value efficiently. If efficiency is not a concern, use
   *  get and set methods instead. Every variable has two index handles -
   *  'begin' and 'end' index handle.
   */
  abstract public int getVariableHandle( String var, boolean begin );

  /**
   *  Returns external variable buffer handle that can be used later to get or set
   *  variable value efficiently. If efficiency is not a concern, use
   *  get and set methods instead. Only external variables have buffer
   *  handle. This is different from index handles. For internal variables
   *  this method returs -1.
   */
  abstract public int getExtVariableHandle( String var );

  /**
   *  Enumerates all external and internal variables in this regex.
   */
  abstract public Enumeration variables();

  /**
   *  Prepares to match or search. Sets up an input buffer and a range of characters in it
   *  that is to be searched or matched.
   */
  abstract public void init( char[] arr, int off, int len );

  /**
   *  Search the buffer (that was set up by init method) for a substring that matches
   *  this regex. See description of this class for difference between search and match.
   *  On success do not discard record of all other possible matches, so if this method
   *  is called again, different way of matching can be found for already found substring
   *  or a substring overlapping already found substring. Most often, search(),
   *  not searchAgain() should be used.
   *  @returns value indicating if matching substring is found.
   */
  abstract public boolean searchAgain();

  /**
   *  Search the buffer (that was set up by init method) for a substring that matches
   *  this regex. See description of this class for difference between search and match.
   *  All records for other possible matches withing matching substring that was found
   *  are destroyed, so if this method is called again, search will start at the first
   *  position after previously found substring.
   *  @returns value indicating if matching substring is found.
   */
  abstract public boolean search();

  /**
   *  Match the buffer (that was set up by init method) against
   *  this regex. See description of this class for difference between search/match/matchWhole.
   *  @returns value indicating if matching was successful.
   */
  abstract public boolean matchWhole();

  /**
   *  Match the beginning of the buffer (that was set up by init method) against
   *  this regex. See description of this class for difference between search/match/matchWhole.
   *  @returns value indicating if matching was successful.
   */
  abstract public boolean match();

  /**
   *  Returns external or internal variable index using its index handle. Every variable
   *  has two indices - 'begin' and 'end'. Use 'begin' and 'end' handles to get them.
   *  These indices can be used to determine which part of the input buffer (set by init method)
   *  corresponding internal variable matched. An external variable 'begin' and 'end' indices
   *  point to its own buffer. They are assinged by setIndex and are never changed.
   */
  abstract public int getIndex( int handle );

  /**
   *  Returns internal variable index using its index handle. Every variable
   *  has two indices - 'begin' and 'end'. Use 'begin' and 'end' handles to assign them.
   *  These indices are needed to completely specify value of an external variable.
   */
  abstract public void setIndex( int handle, int value );

  /**
   *  Returns external variable buffer by its buffer handle. If -1 is passed as a handle
   *  the input buffer that was set up for matching (using init) is returned.
   */
  abstract public char[] getCharBuffer( int extHandle );

  /**
   *  After successiful search returns matching substring's initial position in
   *  the input buffer (that was set up by init method).
   */
  abstract public int getMatchStart();

  /**
   *  After successiful search returns matching substring's final position in
   *  the input buffer (that was set up by init method).
   */
  abstract public int getMatchEnd();

  /**
   *  Assignes external variable buffer by its buffer handle. 
   *  It is needed to completely specify value of an external variable.
   */
  abstract public void setExtVariableBuffer( int extHandle, char[] arr );

  abstract public void setRefiller( RegexRefiller refiller );

  /**
   *  Refiller can call this method after refilling the buffer,
   *  if buffer has been reallocated.
   */
  abstract public void setRefilledBuffer( char[] buffer );

  //--------- convenience methods ----------------

  public boolean matches( char[] arr, int off, int len )
  {
    init( arr, off, len );
    return match();
  }

  public boolean matches( String s )
  {
    char[] arr = s.toCharArray();
    init( arr, 0, arr.length );
    return match();
  }

  public boolean matchesWhole( char[] arr, int off, int len )
  {
    init( arr, off, len );
    return matchWhole();
  }

  public boolean matchesWhole( String s )
  {
    char[] arr = s.toCharArray();
    init( arr, 0, arr.length );
    return matchWhole();
  }

  public boolean searchOnce( char[] arr, int off, int len )
  {
    init( arr, off, len );
    return search();
  }

  public boolean searchOnce( String s )
  {
    char[] arr = s.toCharArray();
    init( arr, 0, arr.length );
    return search();
  }

  public boolean searchOnce( CharString s )
  {
    int f = s.first;
    init( s.buf, f, s.last-f );
    return search();
  }

  public boolean searchOnce( Object obj )
  {
    if( obj instanceof CharString )
      {
	CharString s = (CharString)obj;
	int f = s.first;
	init( s.buf, f, s.last-f );
      }
    else if( obj instanceof char[] )
      {
	char[] s = (char[])obj;
	init( s, 0, s.length );
      }
    else if( obj instanceof String )
      {
	char[] s = ((String)obj).toCharArray();
	init( s, 0, s.length );
      }
    else
      {
	char[] s = obj.toString().toCharArray();
	init( s, 0, s.length );
      }
    return search();
  }

  public void setExtVariable( int extHandle, int beginHandle, int endHandle, String v )
  {
    char[] carr = v.toCharArray();
    setExtVariableBuffer( extHandle, carr );
    setIndex( beginHandle, 0 );
    setIndex( endHandle, carr.length );
  }

  public void setExtVariable( int extHandle, int beginHandle, int endHandle, CharString v )
  {
    setExtVariableBuffer( extHandle, v.buf );
    setIndex( beginHandle, v.first );
    setIndex( endHandle, v.last );
  }

  public void setExtVariable( int extHandle, int beginHandle, int endHandle, char[] carr )
  {
    setExtVariableBuffer( extHandle, carr );
    setIndex( beginHandle, 0 );
    setIndex( endHandle, carr.length );
  }

  public void setExtVariable( int extHandle, int beginHandle, int endHandle, Object v )
  {
    if( v instanceof String )
      setExtVariable( extHandle, beginHandle, endHandle, (String)v );
    else if( v instanceof CharString )
      setExtVariable( extHandle, beginHandle, endHandle, (CharString)v );
    else if( v instanceof char[] )
      setExtVariable( extHandle, beginHandle, endHandle, (char[])v );
    else
      setExtVariable( extHandle, beginHandle, endHandle, v.toString() );
  }

  public CharString getMatch()
  {
    char[] cbuf = getCharBuffer( -1 );
    int start = getMatchStart();
    return new CharString( cbuf, start, getMatchEnd()-start );
  }

  public String getMatchString()
  {
    char[] cbuf = getCharBuffer( -1 );
    int start = getMatchStart();
    return new String( cbuf, start, getMatchEnd()-start );
  }

  public String get( String var )
  {
    int begin = getIndex( getVariableHandle( var, true ) );
    int end   = getIndex( getVariableHandle( var, false ) );
    if( begin < 0 || end < 0 || end < begin )
      return null;
    int ext   = getExtVariableHandle( var );
    char[] cbuf = getCharBuffer( ext );
    return new String( cbuf, begin, end-begin );    
  }

  public void set( String var, String val )
  {
    int ext   = getExtVariableHandle( var );
    if( ext < 0 )
      throw new IllegalArgumentException( "No external variable " + var );
    int begin = getVariableHandle( var, true );
    int end   = getVariableHandle( var, false );
    char[] arr = val.toCharArray();
    setExtVariableBuffer( ext, arr );
    setIndex( begin, 0 );
    setIndex( end, arr.length );
  }

  public Regex cloneRegex()
  {
    try
      {
	return (Regex)super.clone();
      }
    catch( CloneNotSupportedException e )
      {
	throw new RuntimeException( "Internal error: " + e );
      }
  }

  public void init( char[] arr )
  {
    init( arr, 0, arr.length );
  }

  public void init( String s )
  {
    char[] arr = s.toCharArray();
    init( arr, 0, arr.length );
  }

  public Object clone()
  {
    return cloneRegex();
  }

  public static Regex createRegex( String re )
  {
    if( factory == null )
      initFactory();
    return factory.createRegex( re );
  }

  public static Regex createRegex( String re, boolean ignoreCase )
  {
    if( factory == null )
      initFactory();
    return factory.createRegex( re, ignoreCase );
  }

  public static Regex createFilePattern( String re )
  {
    if( factory == null )
      initFactory();
    return factory.createFilePattern( re );
  }

  public static Regex createRegex( char[] arr, int off, int len )
  {
    if( factory == null )
      initFactory();
    return factory.createRegex( arr, off, len, false, false );
  }

  protected static Regex createLowerCaseRegex( String re )
  {
    if( factory == null )
      initFactory();
    return factory.createLowerCaseRegex( re );    
  }

  public static synchronized void setFactory( RegexFactory _factory )
  {
    factory = _factory;
  }

  private synchronized static void initFactory()
  {
    if( factory != null )
      return;
    String regexFactoryClass = null;
    String regexFactoryClass1 = null;
    try
      {
	regexFactoryClass = System.getProperty( "kmy.regex.factory" );
      }
    catch( Exception e )
      {
      }
    if( regexFactoryClass == null )
      {
	regexFactoryClass = "kmy.regex.jvm.JavaClassRegexFactory";
	regexFactoryClass1 = "kmy.regex.interp.InterpRegexFactory";
      }
    Class factoryClass;
    try
      {
        factoryClass = Class.forName( regexFactoryClass );
      }
    catch( ClassNotFoundException e )
      {
	factoryClass = null;
	if( regexFactoryClass1 != null )
	  try
	    {
	      factoryClass = Class.forName( regexFactoryClass1 );
	    }
	  catch( Exception e1 )
	    {
	    }
	if( factoryClass == null )
	  throw new IllegalArgumentException( 
			  "Cannot create default RegexFactory: class not found" );
      }
    try
      {
	factory = (RegexFactory)factoryClass.newInstance();
      }
    catch( RuntimeException e )
      {
	throw e;
      }
    catch( Exception e )
      {
	throw new IllegalArgumentException( 
	    "Cannot instantiate default RegexFactory: " + e.getMessage() );
      }
  }

  public boolean accept(File dir, String name)
  {
    return matchesWhole( name );
  }
}
