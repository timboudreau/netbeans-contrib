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

package com.netbeans.enterprise.modules.corba.browser.ns;

import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;
import org.openide.*;


import com.netbeans.enterprise.modules.corba.*;

/*
 * @author Karel Gardas
 */
 
public class UnbindContext extends NodeAction {

   public static final boolean DEBUG = false;

  static final long serialVersionUID =-6981996552795143080L;
   public UnbindContext () {
      super ();
   }

   /*
   protected boolean enable (org.openide.nodes.Node[] nodes) {
      if (nodes == null)
	 return false;
      boolean is_all_context_node = true;
      for (int i=0; i<nodes.length; i++) 
	 if (nodes[i].getCookie (ContextNode.class) == null) {
	    is_all_context_node = false;
	    break;
	 }
      return is_all_context_node;
   }
   */
   protected boolean enable (org.openide.nodes.Node[] nodes) {
      if (nodes == null || nodes.length != 1)
         return false;
      return (nodes[0].getCookie (ContextNode.class) != null);
   }

   public String getName() {
      return NbBundle.getBundle (ContextNode.class).getString ("CTL_UnbindContext");
   }

   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP; // [PENDING]
   }

   protected void performAction (final Node[] activatedNodes) {
      if (DEBUG)
	 System.out.println ("UnbindContext.java");
      if (enable (activatedNodes)) {
	 for (int i=0; i<activatedNodes.length; i++) {
	    try {
	       ((ContextNode) activatedNodes[i].getCookie(ContextNode.class)).unbind ();
	    } catch (Exception e) {
	       if (DEBUG)
		  e.printStackTrace ();
	       TopManager.getDefault ().notify (new NotifyDescriptor.Exception 
						((java.lang.Throwable) e));
	    }
	 }
      }
   }

}


/*
 * $Log
 * $
 */
