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

/** This class represents Leaf Node (Not context) in JNDI tree
 *
 *  @author Ales Novak, Tomas Zezula
 */
final class JndiLeafNode extends JndiObjectNode {

  /** InitalDirContext*/
  protected DirContext ctx;
  /** Offset of this node relative to ctx*/
  protected CompositeName offset;
  /** The class name*/
  protected String className;
  
  /** Constructor
   *  @param ctx  initial context
   *  @param parentOffset offset of parent directory
   *  @param name name of this node
   *  @param classname name of Class represented by this name
   */ 
  public JndiLeafNode(DirContext ctx, CompositeName parentOffset, String name, String classname) throws NamingException {
    super(Children.LEAF, name);
    this.ctx = ctx;
    this.offset = parentOffset;
    this.className=classname;
    setIconBase(JndiIcons.ICON_BASE + JndiIcons.getIconName(classname));
  }
  
  /** Generates code for accessing object that is represented by this node
   *  @return String the java source code
   */
  public String createTemplate() throws NamingException {
    return JndiObjectCreator.getCode(ctx, offset, className);
  }
    
  /** Returns SystemAction  
   *  @return array of SystemAction
   */
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

  /** Returns initial directory context
   *  @return DirContext the initial dir context
   */
  public DirContext getContext(){
    return this.ctx;
  }

  /** Returns offset of the node in respect to InitialContext
   *  @return CompositeName the offset
   */
  public CompositeName getOffset(){
    return this.offset;
  }
  
  /** Returns class name
   *  @return String class name
   */
   public String getClassName(){
     return this.className;
   }
    
}
