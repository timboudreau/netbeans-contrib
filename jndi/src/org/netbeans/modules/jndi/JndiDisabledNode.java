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

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.Context;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.openide.actions.DeleteAction;
import com.netbeans.enterprise.modules.jndi.utils.Refreshable;

/** This class represents a mounted Context which is from some 
 *  reason, e.g. the naming service is not running, not in progress.
 */
public class JndiDisabledNode extends JndiAbstractNode implements Refreshable, Node.Cookie{

  /** Icon name*/
  public static final String DISABLED_CONTEXT_ICON = "DISABLED_CONTEXT_ICON";
  
  /** Initial properties for externalization*/
  private Hashtable properties;
  
  /** Creates new JndiDisabledNode 
   *  @param Hashtable the properties that represents the root of naming system
   */
  public JndiDisabledNode(Hashtable properties) {
    super (Children.LEAF);
    this.getCookieSet().add(this);
    this.setName((String)properties.get(JndiRootNode.NB_LABEL));
    this.setIconBase(JndiIcons.ICON_BASE + JndiIcons.getIconName(DISABLED_CONTEXT_ICON));
    this.properties = properties;
  }
  
  /** Returns the properties of InitialDirContext
   *  @return Hashtable properties;
   */
  public Hashtable getInitialDirContextProperties() throws NamingException {
    return this.properties;
  }
  
  
  /** Can the node be destroyed 
   *  @return boolean, true if the node can be destroyed
   */
  public boolean canDestroy() {
    return true;
  }
  
  /** Creates SystemActions of this node
   *  @return SystemAction[] the actions
   */
  public SystemAction[] createActions() {
    return new SystemAction[] {
      SystemAction.get(RefreshAction.class),
      null,
      SystemAction.get(DeleteAction.class)
    };
  }
  
  /** Refreshs the node
   *  If the node is failed, and the preconditions required by the context
   *  of this node are satisfied, than change the node to JndiNode
   */
  public void refresh() {
    try{
      JndiRootNode root = JndiRootNode.getDefault();
      // We create the Context manually not by JndiRootNode factory
      // because we have to check if the context is in order,
      // if not than we do nothing.
      Context ctx = new JndiDirContext(this.properties);
      String startOffset = (String) this.properties.get(JndiRootNode.NB_ROOT);
      if (startOffset != null && startOffset.length() > 0){
        ctx  = (Context) ctx.lookup(startOffset);
       }else{
         // If we don't perform lookup
         // we should check the context
             ((JndiDirContext)ctx).checkContext();
       }
       root.addContext(ctx);
       this.destroy();
    }catch(NamingException ne){
      // If exception was thrown than we don't
      // remove the node
    }
    catch(java.io.IOException ioe){
      // Should never happen
    }
  }
}