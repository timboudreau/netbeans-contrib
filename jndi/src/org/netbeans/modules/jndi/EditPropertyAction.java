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

import org.openide.util.actions.NodeAction;
import org.openide.util.HelpCtx;
import org.openide.nodes.Node;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import com.netbeans.enterprise.modules.jndi.utils.AttributeManager;
/** 
 *
 * @author  tzezula
 * @version 
 */
public class EditPropertyAction extends NodeAction {

  /** Creates new CreatePropertyAction */
  public EditPropertyAction() {
    super();
  }
  
  
  public void performAction (Node[] nodes){
    if (enable(nodes)){
      ((AttributeManager)nodes[0].getCookie(JndiNode.class)).editAttribute();
    }
  }
  
  
  public boolean enable (Node[] nodes){
    if  (nodes == null || nodes.length!=1)
      return false;
     JndiNode node = (JndiNode)nodes[0].getCookie(JndiNode.class);
     if (node == null) return false;
     Context ctx = node.getContext();
    if (ctx == null || !(ctx instanceof DirContext))
      return false;
     return true;
  }
  
  
  public String getName(){
    return JndiRootNode.getLocalizedString("CTL_EditProperty");
  }
  
  
  public HelpCtx getHelpCtx(){
    return HelpCtx.DEFAULT_HELP;
  }
  
}