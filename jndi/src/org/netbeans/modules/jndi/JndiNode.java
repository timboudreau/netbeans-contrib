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

import java.util.Vector;
import java.util.Collection;
import java.util.Hashtable;
import java.io.IOException;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import javax.naming.NamingException;
import javax.naming.CompositeName;
import javax.naming.directory.DirContext;

import org.openide.TopManager;
import org.openide.actions.NewAction;
import org.openide.actions.CopyAction;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;

/** This class represents JNDI subdirectory */
final class JndiNode extends AbstractNode implements TemplateCreator {

  private boolean isRoot;
  private NewType[] jndinewtypes;
  private SystemAction[] jndiactions;

  //Constructor for creation of Top Level Directory
  public JndiNode(DirContext ctx) throws NamingException {
    super (new JndiChildren(ctx));
    isRoot = true;
    ((JndiChildren)this.getChildren()).setOffset(new CompositeName(((String)ctx.getEnvironment().get(JndiRootNode.NB_ROOT))));
    setName((String)ctx.getEnvironment().get(JndiRootNode.NB_LABEL));
    ((JndiChildren)this.getChildren()).prepareKeys();
    setIconBase(JndiIcons.ICON_BASE+JndiIcons.getIconName("javax.naming.Context"));
  }
  
  //Constructor of subdirectory 
  // ctx 	DirectoryCOntext
  // parent_name offset of directory i am in
  // my_name	name of this directory
  public JndiNode(DirContext ctx, CompositeName parentName, String myName) throws NamingException {
    super (new JndiChildren(ctx));
    isRoot = false;
    parentName.add(myName);
    ((JndiChildren)this.getChildren()).setOffset(parentName);
    setName(myName);
    ((JndiChildren)this.getChildren()).prepareKeys();
    setIconBase(JndiIcons.ICON_BASE + JndiIcons.getIconName("javax.naming.Context"));
  }

  public boolean isRoot() {
    return isRoot;
  }

  //This method creates template for accessing this node
  public String createTemplate() throws NamingException {
    return JndiObjectCreator.getCode(((JndiChildren)this.getChildren()).getContext(),((JndiChildren)this.getChildren()).getOffset());
  }

  public SystemAction[] getActions() {
    if (jndiactions == null) {
      jndiactions = createActions();
    }
    return jndiactions;
  }
  
  public NewType[] getNewTypes() {
    if (this.jndinewtypes == null) {
      this.jndinewtypes = new NewType[] {new JndiDataType(this)};
    }
    return this.jndinewtypes;
  }
  
  public boolean canCopy() {
      return true;
  }
  
  
  public SystemAction[] createActions() {
      return new SystemAction[] {SystemAction.get(NewAction.class),
                                 SystemAction.get(CopyAction.class),
                                 SystemAction.get(PropertiesAction.class)
      };
  }
  
  public Transferable clipboardCopy() throws IOException {
    try {
      return new StringSelection(this.createTemplate());
    } catch (NamingException ne) {
      TopManager.getDefault().notifyException(ne);
      return null;
    }
  }
}


