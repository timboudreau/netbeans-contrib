/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.enterprise.modules.jndi;


/*JNDI*/
import javax.naming.*;
import javax.naming.directory.*;


/*NetBeans*/
import com.netbeans.ide.*;
import com.netbeans.ide.nodes.*;

/*JDK*/
import java.util.Collection;
import java.util.Vector;

class JndiChildren extends Children.Keys
{
    public final static String CONTEXT_CLASS_NAME="javax.naming.Context";

    DirContext parent_context;	// Initial Directory context
    CompositeName offset;	// Offset in Initial Directory context
    
    
    //Constructor takes the initial context as its parameter
    public JndiChildren(DirContext parent_context)
    {
	this.parent_context=parent_context;
    }
    
    //Set actual offset in tree hierrarchy
    //This method shold be immediatelly called after JndiChildren is created
    public void setOffset(CompositeName offset)
    {
	this.offset=offset;
    }
    
    // Returns actual offset
    public CompositeName getOffset()
    {
	return this.offset;
    }
    
    // Returns context
    public DirContext getContext()
    {
	return this.parent_context;
    }
    
    
    //Set context
    public void setContext(DirContext context)
    {
	this.parent_context=context;
    }
    
    // this method creates keys and set them
    public void prepareKeys() throws NamingException
    {
	NamingEnumeration ne = parent_context.list(this.offset.toString());
	if (ne==null) return;
	Vector v = new Vector();
	while (ne.hasMore())
	  v.add(ne.next());
	this.setKeys(v);
    }
    
    // creates Node for key
    public Node[] createNodes(Object key)
    {
	try
	{
	    if (key==null) return null;
	    if (! (key instanceof NameClassPair)) return null;
	    NameClassPair np = (NameClassPair) key;
	    if (np.getClassName().indexOf("Context")>=0)
		 {
		     return new Node[] {new JndiNode(this.parent_context,((CompositeName)this.offset.clone()),np.getName())};
		 }
	    else 
		{
		    return new Node[] {new JndiLeafNode(this.parent_context,((CompositeName)this.offset.clone()),np.getName(),np.getClassName())};
		}
	}catch(NamingException ne){ return new Node[0];}
    }
    
}








