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

import java.util.Vector;
import java.awt.datatransfer.StringSelection;

import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;
import org.openide.*;


import com.netbeans.enterprise.modules.corba.*;

/*
 * @author Karel Gardas
 */
 
public class CopyClientCode extends NodeAction {

   public static final boolean DEBUG = false;
   //public static final boolean DEBUG = true;

   public CopyClientCode () {
      super ();
   }

   protected boolean enable (org.openide.nodes.Node[] nodes) {
      if (nodes == null || nodes.length != 1)
	 return false;
      return (nodes[0].getCookie (ObjectNode.class) != null);
   }

   public String getName() {
      return NbBundle.getBundle (ContextNode.class).getString ("CTL_CopyClientCode");
   }

   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP; // [PENDING]
   }

   protected void performAction (final Node[] activatedNodes) {
      if (DEBUG)
	 System.out.println ("CopyClientCode.java");
      Vector names = new Vector ();
      Node tmp_node = activatedNodes[0];
      ObjectNode on = (ObjectNode)tmp_node.getCookie (ObjectNode.class);
      names.add (on.getName ());
      names.add (on.getKind ());
      tmp_node = on.getParentNode ();
      while (tmp_node.getParentNode () != null) {
	 ContextNode cn = (ContextNode)tmp_node.getCookie (ContextNode.class);
	 tmp_node = tmp_node.getParentNode ();
	 names.add (cn.getName ());
	 names.add (cn.getKind ());
      }
      String paste = new String ("      String[] client_name_hierarchy = new String [] {");
      for (int i=names.size () - 6; i>=0; i=i-2) {
	 paste = paste + "\"" + (String)names.elementAt (i) + "\"" + ", ";
	 paste = paste + "\"" + (String)names.elementAt (i+1) + "\"" + ", ";
      }
      if (paste.substring (paste.length () - 2, paste.length ()).equals (", "))
	 paste = paste.substring (0, paste.length () - 2);
      paste = paste + "};\n";
      if (DEBUG)
	 System.out.println ("names: " + paste);
      StringSelection ss = new StringSelection (paste);
      TopManager.getDefault().getClipboard().setContents(ss, null);

   }

}


/*
 * $Log
 * $
 */
