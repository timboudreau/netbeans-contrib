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

package com.netbeans.enterprise.modules.corba.browser.ir.actions;

import org.openide.TopManager;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;
import com.netbeans.enterprise.modules.corba.browser.ir.Util;
import com.netbeans.enterprise.modules.corba.browser.ir.nodes.IRInterfaceDefNode;
import com.netbeans.enterprise.modules.corba.browser.ir.util.Generatable;


public class GenerateCodeAction extends NodeAction {

  protected void performAction (Node[] nodes) {
    if ( enable ( nodes) ){
      TopManager.getDefault().setStatusText(Util.getLocalizedString("MSG_GenerateWait"));
      ((Generatable)nodes[0].getCookie(Generatable.class)).generateCode();
      TopManager.getDefault().setStatusText(Util.getLocalizedString("MSG_GenerateDone"));
    }
  }
  
  protected boolean enable (Node[] nodes) {
      Node node;
      if (nodes != null && nodes.length == 1){
        if ((node = (Node) nodes[0].getCookie(Generatable.class))  == null) return false;
        if (node.getParentNode() instanceof IRInterfaceDefNode) return false;
        return true;
      }
      return false;
  }

  public String getName () {
    return Util.getLocalizedString("CTL_GenerateCode");
  }

  protected String iconResource () {
    return "GenerateCodeActionIcon.gif";
  }

  public HelpCtx getHelpCtx () {
    return HelpCtx.DEFAULT_HELP;
  }
  
}
