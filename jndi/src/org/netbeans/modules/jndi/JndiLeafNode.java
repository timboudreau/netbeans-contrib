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
import java.util.Hashtable;
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
  
  /** Returns the properties of InitialDirContext
   *  @return Hashtable properties;
   */
  public Hashtable getInitialDirContextProperties() throws NamingException {
    return this.ctx.getEnvironment();
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

/*
 * <<Log>>
 *  10   Gandalf   1.9         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  9    Gandalf   1.8         10/6/99  Tomas Zezula    
 *  8    Gandalf   1.7         7/9/99   Ales Novak      localization + code 
 *       requirements followed
 *  7    Gandalf   1.6         6/18/99  Ales Novak      redesigned + delete 
 *       action
 *  6    Gandalf   1.5         6/10/99  Ales Novak      gemstone support + 
 *       localizations
 *  5    Gandalf   1.4         6/9/99   Ales Novak      refresh action + 
 *       destroying subcontexts
 *  4    Gandalf   1.3         6/9/99   Ian Formanek    ToolsAction
 *  3    Gandalf   1.2         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  2    Gandalf   1.1         6/8/99   Ales Novak      sources beautified + 
 *       subcontext creation
 *  1    Gandalf   1.0         6/4/99   Ales Novak      
 * $
 */
