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

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;
/** 
 *
 * @author  tzezula
 * @version 
 */
public class ProviderConnectAction extends NodeAction {

  /** Creates new ProviderConnectAction */
  public ProviderConnectAction() {
    super();
  }
  
  protected void performAction(Node[] nodes){
    if (this.enable(nodes)){
      ((ProviderNode)nodes[0].getCookie(ProviderNode.class)).connectUsing();
    }
  }
  
  /**
  */
  protected boolean enable (Node[] nodes){
    if (nodes != null && nodes.length!=1) return false;
    if (nodes[0].getCookie(ProviderNode.class) == null) return false;
    return true;
  }
  
  public String getName(){
    return JndiRootNode.getLocalizedString("CTL_ConnectUsing");
  }
  
  public HelpCtx getHelpCtx (){
    return HelpCtx.DEFAULT_HELP;
  }
  
}