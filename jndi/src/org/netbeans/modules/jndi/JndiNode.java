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
import javax.naming.NamingException;
import javax.naming.CompositeName;
import javax.naming.directory.DirContext;

import org.openide.TopManager;
import org.openide.actions.NewAction;
import org.openide.actions.CopyAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;

/** This class represents JNDI subdirectory */
final class JndiNode extends JndiObjectNode {

  private boolean isRoot;
  private NewType[] jndinewtypes;
  
  //Constructor for creation of Top Level Directory
  public JndiNode(DirContext ctx) throws NamingException {
    this (ctx, 
          new CompositeName(((String) ctx.getEnvironment().get(JndiRootNode.NB_ROOT))), 
          (String) ctx.getEnvironment().get(JndiRootNode.NB_LABEL));
    isRoot = true;
  }
  
  //Constructor of subdirectory 
  // ctx 	DirectoryCOntext
  // parent_name offset of directory i am in
  // my_name	name of this directory
  public JndiNode(DirContext ctx, CompositeName parentName, String myName) throws NamingException {
    super (new JndiChildren(ctx, parentName), myName);
    isRoot = false;
    setIconBase(JndiIcons.ICON_BASE + JndiIcons.getIconName("javax.naming.Context"));
  }

  public boolean isRoot() {
    return isRoot;
  }

  //This method creates template for accessing this node
  public String createTemplate() throws NamingException {
    return JndiObjectCreator.getCode(((JndiChildren)this.getChildren()).getContext(),((JndiChildren)this.getChildren()).getOffset());
  }  
  
  public NewType[] getNewTypes() {
    if (this.jndinewtypes == null) {
      this.jndinewtypes = new NewType[] {new JndiDataType(this)};
    }
    return this.jndinewtypes;
  }
  
  /** Destroys this node.
  * If this node is root then nothing more is done.
  * If this node is not root then represented Context is destroyed.
  *
  * @exception IOException
  */
  public void destroy() throws IOException {

    if (isRoot()) {
      super.destroy();
      return;
    } else {
      try {
        // destroy this context first
        JndiChildren children = (JndiChildren) getChildren();
        DirContext parentCtx = children.getContext();
        parentCtx.destroySubcontext(children.getOffset());
        // destroy node
        setRemoved();
        super.destroy();
      } catch (NamingException e) {
        JndiRootNode.notifyForeignException(e);
      }
    }
  }
  
  
  public SystemAction[] createActions() {
    return new SystemAction[] {
      SystemAction.get(CopyAction.class),
      null,
      SystemAction.get(NewAction.class),
      null,
      SystemAction.get(RefreshAction.class),
      SystemAction.get(DeleteAction.class),
      null,
      SystemAction.get(ToolsAction.class),
      SystemAction.get(PropertiesAction.class),
    };
  }
  
  /** Refreshes this node. */
  public void refresh() {
    try {
      ((JndiChildren) getChildren()).prepareKeys();
    } catch (NamingException e) {
      JndiRootNode.notifyForeignException(e);
    }
  }  
}


