/*
    Copyright (c) Peter Sorotokin, 1998-2000
    See file "License.html" for terms of usage and
    redistribution.   
*/
package kmy.regex.jvm;

import kmy.regex.util.Regex;
import kmy.regex.util.RegexRefiller;

import java.util.Hashtable;
import java.util.Enumeration;

public abstract class JavaClassRegexStub extends Regex
{
  /**
   *  Character buffer which stores the string being matched.
   */
  protected char[]     string;

  /**
   *  Variable values. Earch variable is given two cells in this
   *  array: one stores index of the first character in the buffer
   *  and the other stores index of the last character in the buffer plus one.
   *  Note that variables that are only referenced (but not "picked") by
   *  this regex have their own buffers (stored in extCells).
   */
  protected int[]      cells;

  /**
   *  Index (in the buffer) of the first character of the string being matched.
   */
  protected int        start;

  /**
   *  Index (in the buffer) of the last character in the string being matched plus one.
   */
  protected int        end;

  /**
   *  This keeps information about forks in matching. Forks occur when several ways
   *  of matching are possible. Matching algorithm picks the first way and tries it
   *  out. If it eventually fails, it goes back to the fork point and tries the other
   *  way.
   */
  protected int[]      forks;

  /**
   *  Number of elements used in the <i>forks</i> array.
   */
  protected int        forkPtr;

  /**
   *  Starting index for the next search/match.
   */
  protected int        headStart;

  /**
   *  On successful match/search - index of the first character in matching string.
   */
  protected int        matchStart;

  /**
   *  On successful match/search - index of the last character in matching string plus one.
   */
  protected int        matchEnd;
  
  /**
   *  Character buffers for referenced variable values.
   */
  protected char[][]   extCells;

  /**
   *  True if searching is going on, false if matching.
   */
  protected boolean    searching;

  /**
   *  Maximum value for the start of the matching string.
   */
  protected int        maxStart;

  /**
   *  RegexRefiller to use when end of buffer is reached.
   */
  protected RegexRefiller refiller;

  protected JavaClassRegexStub()
  {
  }

  abstract protected boolean nextMatchInt();
  abstract protected Hashtable getVars();

  /**
   *  Debugging method to print regex state. Called if internal error happens.
   */
  public void dumpForks()
  {
    System.err.println( "**** State dump:" );
    System.err.println( "\tstart = " + start );
    System.err.println( "\tend = " + end ); 
    System.err.println( "\tmatchStart = " + matchStart );
    System.err.println( "\tmatchEnd = " + matchEnd );
    System.err.println( "\theadStart = " + headStart );
    System.err.print( "\tstring = \"" );
    if( start >= 0 )
      {
	for( int i = start ; i < end && i < string.length && i < start + 30 ; i++ )
	  {
	    char c = string[i];
	    if( c < ' ' )
	      System.err.print( "\\x" + Integer.toHexString( c ) );
	    else
	      System.err.print( c );
	  }
      }
    System.err.println( "\"" );
    System.err.println( "**** Fork stack dump [ptr=" + forkPtr + "]:" );
    for( int i = 0 ; i < forks.length && i < forkPtr+4; i++ )
      System.err.print( " " + forks[i] );
    System.err.println();
    Hashtable vars = getVars();
    if( vars == null || cells == null )
      return;
    System.err.println( "**** Variable dump:" );
    Object[] names = new Object[cells.length];
      Enumeration vlist = vars.keys();
    while( vlist.hasMoreElements() )
      {
	Object key = vlist.nextElement();
	int[] v = (int[])vars.get(key);
	names[v[0]] = key;
      }
    for( int i = 0 ; i < cells.length ; i++ )
      System.err.println( "\t" + cells[i] + ( names[i] != null ? "\t$" + names[i] : "" ) );
  }

  //------------------ Regexp implementation

  public int getVariableHandle( String var, boolean begin )
  {
    int[] entry = (int[])getVars().get(var);
    if( entry == null )
      return -1;
    return entry[begin?0:1];
  }

  public int getExtVariableHandle( String var )
  {
    int[] entry = (int[])getVars().get(var);
    if( entry == null )
      return -1;
    return entry[2];
  }

  public void setExtVariableBuffer( int extHandle, char[] arr )
  {
    extCells[extHandle] = arr;
  }

  public Enumeration variables()
  {
    return getVars().keys();
  }

  public char[] getCharBuffer( int extHandle )
  {
    if( extHandle < 0 )
      return string;
    else
      return extCells[extHandle];
  }

  public int getMatchStart()
  {
    return matchStart;
  }

  public int getMatchEnd()
  {
    return matchEnd;
  }

  public int getIndex( int handle )
  {
    return cells[handle];
  }

  public void setIndex( int handle, int value )
  {
    cells[handle] = value;
  }

  public void setRefiller( RegexRefiller r )
  {
    refiller = r;
    maxStart = end; // TODO: optimize it, need a method to calculate maxStart
  }

  public void setRefilledBuffer( char[] buf )
  {
    string = buf;
  }

  public boolean searchAgain()
  {
    searching = true;
    while( true )
      {
	while( headStart > maxStart )
	  {
	    if( refiller == null )
	      return false;
	    int r = refiller.refill( this, end );
	    if( r <= end )
	      return false;
	    end = r;
	    maxStart = r; // TODO: optimize it, need a method to calculate maxStart
	  }
	if( nextMatchInt() ) // modifies headStart on failure
	  {
	    headStart = matchStart+1; // so when we fail next time, we start at an offset
	    return true;
	  }
	forkPtr = 0; // it should be 0 anyway?
      }
  }

  public boolean search()
  {
    searching = true;
    while( true )
      {
	while( headStart > maxStart )
	  {
	    if( refiller == null )
	      return false;
	    int r = refiller.refill( this, end );
	    if( r <= end )
	      return false;
	    end = r;
	    maxStart = r; // TODO: optimize it, need a method to calculate maxStart
	  }
	if( nextMatchInt() ) // modifies headStart on failure
	  {
	    // next time start after the end of just matched substring
	    if( matchStart < matchEnd )
	      headStart = matchEnd;
	    else
	      headStart = matchEnd + 1;
	    forkPtr = 0;
	    return true;
	  }
	forkPtr = 0;
      }
  }

  public boolean matchWhole()
  {
    searching = false;
    while( nextMatchInt() )
      {
	if( matchEnd == end && refiller == null )
	  return true;
	if( forkPtr == 0 ) // no alternatives left
	  break; 
      }
    return false;
  }

  public boolean match()
  {
    searching = false;
    return nextMatchInt();
  }

  public Regex cloneRegex()
  {
    JavaClassRegexStub other = (JavaClassRegexStub)super.cloneRegex();
    other.string = null;
    if( cells != null )
      {
	other.cells = new int[cells.length];
	System.arraycopy( cells, 0, other.cells, 0, cells.length );
      }
    if( extCells != null )
      {
	other.extCells = new char[extCells.length][];
	System.arraycopy( extCells, 0, other.extCells, 0, extCells.length );
      }
    other.forks = new int[4];
    return other;
  }
}


