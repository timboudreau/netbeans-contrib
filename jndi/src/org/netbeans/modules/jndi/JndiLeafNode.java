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

import java.io.IOException;
import javax.naming.NamingException;
import javax.naming.CompositeName;
import javax.naming.directory.DirContext;

import org.openide.TopManager;
import org.openide.actions.CopyAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.actions.DeleteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;

/** This class is Leaf Node in JNDI tree eg. File...*/
final class JndiLeafNode extends JndiObjectNode {

  protected DirContext ctx;
  protected CompositeName offset;
    
  public JndiLeafNode(DirContext ctx, CompositeName parentOffset, String name, String classname) throws NamingException {
    super(Children.LEAF, name);
    this.ctx = ctx;
    this.offset = parentOffset;
    setIconBase(JndiIcons.ICON_BASE + JndiIcons.getIconName(classname));
  }
  
  // Generates code for accessing object that is represented by this node
  public String createTemplate() throws NamingException {
    return JndiObjectCreator.getCode(ctx, offset);
  }
     
  public SystemAction[] createActions() {
    return new SystemAction[] {
      SystemAction.get(CopyAction.class),
      null,
      SystemAction.get(DeleteAction.class),
      null,
      SystemAction.get(PropertiesAction.class),
    };
  }

 
  /** Destroys this node.
  * If this node is root then nothing more is done.
  * If this node is not root then represented Context is destroyed.
  *
  * @exception IOException
  */
  public void destroy() throws IOException {

    try {
      // destroy this context first
      ctx.unbind(offset);
      setRemoved();
      super.destroy();
    } catch (NamingException e) {
      JndiRootNode.notifyForeignException(e);
    }
  }  
}