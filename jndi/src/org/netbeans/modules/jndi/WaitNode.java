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

package org.netbeans.modules.jndi;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/** 
 * This class represents na temporary node, that is shown when then 
 * folder data are transfered from remote host.
 * @author  tzezula
 * @version 
 */


public class WaitNode extends AbstractNode {

  /** Constant to the Icon lookup table*/
  public final static String WAIT_ICON="WAIT_NODE"; // no I18N
  
  /** Creates new WaitNode */
  public WaitNode() {
    super(Children.LEAF);
    this.setName(JndiRootNode.getLocalizedString("TITLE_WaitNode"));
    this.setIconBase(JndiIcons.ICON_BASE + JndiIcons.getIconName(WaitNode.WAIT_ICON));
  }
  
  /** Can not be copied
   *  @return boolean false
   */
  public boolean canCopy(){
    return false;
  }
  
  /** Can not be cut
   *  @return boolean false
   */
  public boolean canCut(){
    return false;
  }
  
  /** Can not be deleted
   *  @return boolean false
   */
  public boolean canDelete(){
    return false;
  }
  
  /** Can not be renamed
   *  @return boolean false
   */
  public boolean canRename(){
    return false;
  }
  
}