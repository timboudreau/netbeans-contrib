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

/*NetBeans*/
package com.netbeans.enterprise.modules.jndi;


/*JNDI*/
import javax.naming.*;
import javax.naming.directory.*;


/*NetBeans*/
import com.netbeans.ide.*;
import com.netbeans.ide.actions.*;
import com.netbeans.ide.filesystems.*;
import com.netbeans.ide.nodes.*;
import com.netbeans.ide.util.actions.*;


/*JDK*/
import java.awt.datatransfer.*;
import java.io.IOException;


public class JndiLeafNode extends AbstractNode implements TemplateCreator
{
    protected DirContext ctx;
    protected CompositeName offset;
    protected SystemAction[] actions;
    
    public JndiLeafNode(DirContext ctx, CompositeName parent_offset, String name, String classname) throws NamingException
    {
	super(Children.LEAF);
	setName(name);
	this.ctx=ctx;
	this.offset=(CompositeName)parent_offset.add(name);
	setIconBase(JndiIcons.ICON_BASE+JndiIcons.getIconName(classname));
    }
    
    // Generates code for accessing object that is represented by this node
    public String createTemplate() throws NamingException
    {
	return JndiObjectCreator.getCode(this.ctx,this.offset);
    }
    
    public boolean canCopy()
    {
	return true;
    }
    
    public SystemAction[] getActions()
    {
	if (this.actions==null) 
	    this.actions=createActions();
	return this.actions;
    }
    
    
    public Transferable clipboardCopy() throws IOException
    {
	try
	{
	    return new StringSelection(this.createTemplate());
	}catch(NamingException ne)
	{
	    TopManager.getDefault().notifyException(ne);
	    return null;
	}
    }
    
    public SystemAction[] createActions()
    {
	return new SystemAction[] {SystemAction.get(CopyAction.class),
				   SystemAction.get(PropertiesAction.class)};
    }
    
}