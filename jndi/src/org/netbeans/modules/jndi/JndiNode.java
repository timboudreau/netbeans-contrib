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


/* Netbeans*/ 
import com.netbeans.ide.*;
import com.netbeans.ide.actions.*;
import com.netbeans.ide.filesystems.*;
import com.netbeans.ide.nodes.*;
import com.netbeans.ide.util.actions.*;
import com.netbeans.ide.util.datatransfer.NewType;


/*JDK*/
import java.util.Vector;
import java.util.Collection;
import java.util.Hashtable;
import java.io.IOException;
import java.awt.datatransfer.*;

public class JndiNode extends AbstractNode implements TemplateCreator
{
    boolean is_root;
    NewType[] jndinewtypes;   
    SystemAction[] jndiactions;

    //Constructor for creation of Top Level Directory
    public JndiNode(DirContext ctx) throws NamingException
    {
	super (new JndiChildren(ctx));
	is_root=true;	
	((JndiChildren)this.getChildren()).setOffset(new CompositeName(((String)ctx.getEnvironment().get(JndiRootNode.NB_ROOT))));
	setName((String)ctx.getEnvironment().get(JndiRootNode.NB_LABEL));
	((JndiChildren)this.getChildren()).prepareKeys();
	setIconBase(JndiIcons.ICON_BASE+JndiIcons.getIconName("javax.naming.Context"));
    }
    
    //Constructor of subdirectory 
    // ctx 	DirectoryCOntext
    // parent_name offset of directory i am in
    // my_name	name of this directory
    public JndiNode(DirContext ctx, CompositeName parent_name, String my_name) throws NamingException
    {
	super(new JndiChildren(ctx));
	is_root=false;
	parent_name.add(my_name);
	((JndiChildren)this.getChildren()).setOffset(parent_name);
	setName(my_name);
	((JndiChildren)this.getChildren()).prepareKeys();
	setIconBase(JndiIcons.ICON_BASE+JndiIcons.getIconName("javax.naming.Context"));
    }
    
    
    public boolean isRoot()
    {
	return this.is_root;
    }
    
    
    //This method creates template for accessing this node
    public String createTemplate() throws NamingException
    {
	return JndiObjectCreator.getCode(((JndiChildren)this.getChildren()).getContext(),((JndiChildren)this.getChildren()).getOffset());
    }
    
    
    public SystemAction[] getActions()
    {
	if (this.jndiactions==null) this.jndiactions=this.createActions();
	return this.jndiactions;
    }
    
    public NewType[] getNewTypes()
    {
	if (this.jndinewtypes==null)
	{
	    this.jndinewtypes= new NewType[]{new JndiDataType(this)};
	}
	return this.jndinewtypes;
    }
    
    public boolean canCopy()
    {
	return true;
    }
    
    
    public SystemAction[] createActions()
    {
	return new SystemAction[] {SystemAction.get(NewAction.class),
				   SystemAction.get(CopyAction.class),
				   SystemAction.get(PropertiesAction.class)};
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
    
}


