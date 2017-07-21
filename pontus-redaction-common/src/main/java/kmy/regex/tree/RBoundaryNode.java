/*
    Copyright (c) Peter Sorotokin, 1998-2000
    See file "License.html" for terms of usage and
    redistribution.   
*/
package kmy.regex.tree;

public class RBoundaryNode extends RNode
{

  public int boundaryClass;

  public RBoundaryNode( int pos, int boundaryClass )
  {
    super( pos );
    this.boundaryClass = boundaryClass;
    minLength = 0;
    maxLength = 0;
  }

  public void prepare( int addMaxLeft, int addMinLeft )
  {
    if( boundaryClass == '$' )
      {
	addMaxLeft = 0;
	addMinLeft = 0;
	if( tail != null )
	  {
	    tail.prepare( addMaxLeft, addMinLeft );
	    if( tail.minLeft != 0 )
	      throw new RuntimeException( "$ can only be the last expression" );
	  }
      }
    super.prepare( addMaxLeft, addMinLeft );
  }

  public CharSet findPrefix( CharSet tailPrefix )
  {
    if( tail == null )
      prefix = tailPrefix;
    else
      {
	tail.findPrefix( tailPrefix );
	prefix = tail.prefix;
      }
    return prefix;
  }

  public boolean isStartAnchored()
  {
    return boundaryClass == '^';
  }
  
  public boolean isEndAnchored()
  {
    if( boundaryClass == '$' )
      return true;
    if( tail != null )
      return tail.isEndAnchored();
    return false;
  }
  
  public Object eval( RContext context )
  {
    return context.evalRBoundary( this );
  }
}
