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
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.openide.actions.DeleteAction;

/** This class represents a mounted Context which is from some 
 *  reason, e.g. the naming service is not running, not in progress.
 */
public class JndiDisabledNode extends JndiAbstractNode {

  /** Icon name*/
  public static final String DISABLED_CONTEXT_ICON = "DISABLED_CONTEXT_ICON";
  
  /** Initial properties for externalization*/
  private Hashtable properties;
  
  /** Creates new JndiDisabledNode 
   *  @param Hashtable the properties that represents the root of naming system
   */
  public JndiDisabledNode(Hashtable properties) {
    super (Children.LEAF);
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
      SystemAction.get(DeleteAction.class),
    };
  }
  
}