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

package org.netbeans.modules.corba.browser.ns;

import java.util.Vector;
import java.awt.datatransfer.StringSelection;

import org.openide.nodes.*;
import org.openide.util.actions.*;
import org.openide.util.*;
import org.openide.*;


import org.netbeans.modules.corba.*;

/*
 * @author Karel Gardas
 */

public class CopyServerCode extends NodeAction {

    public static final boolean DEBUG = false;
    //public static final boolean DEBUG = true;

    static final long serialVersionUID =5790749245670472327L;
    public CopyServerCode () {
        super ();
    }

    protected boolean enable (org.openide.nodes.Node[] nodes) {
        if (nodes == null || nodes.length != 1)
            return false;
        return (nodes[0].getCookie (ContextNode.class) != null);
    }

    public String getName() {
        return NbBundle.getBundle (ContextNode.class).getString ("CTL_CopyServerCode");
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP; // [PENDING]
    }

    protected void performAction (final Node[] activatedNodes) {
        if (DEBUG)
            System.out.println ("CopyServerCode.java");
        org.openide.TopManager.getDefault().setStatusText(NbBundle.getBundle(CopyServerCode.class).getString ("TXT_GeneratingCode"));
        org.openide.util.RequestProcessor.postRequest ( new Runnable () {
            public void run () {
                Vector names = new Vector ();
                Node tmp_node = activatedNodes[0];
                while (tmp_node.getParentNode () != null) {
                    ContextNode cn = (ContextNode)tmp_node.getCookie (ContextNode.class);
                    tmp_node = tmp_node.getParentNode ();
                    names.add (cn.getName ());
                    names.add (cn.getKind ());
                }
                String paste = new String ("      String[] hierarchy_of_contexts = new String [] {");
                for (int i=names.size () - 6; i>=0; i=i-2) {
                    paste = paste + "\"" + GenerateSupport.correctCode((String)names.elementAt (i)) + "\"" + ", ";
                    paste = paste + "\"" + GenerateSupport.correctCode((String)names.elementAt (i+1)) + "\"" + ", ";
                }
                if (paste.substring (paste.length () - 2, paste.length ()).equals (", "))
                paste = paste.substring (0, paste.length () - 2);
                paste = paste + "};\n";
                // server name
                paste = paste + "      String[] name_of_server = new String [] {\"<name of server>\", ";
                paste = paste + "\"<kind of server>\"};\n";
                if (DEBUG)
                    System.out.println ("names: " + paste);
                StringSelection ss = new StringSelection (paste);
                TopManager.getDefault().getClipboard().setContents(ss, null);
                TopManager.getDefault().setStatusText(NbBundle.getBundle(CopyServerCode.class).getString("TXT_CodeGenerated"));
            }
        });

    }

}


/*
 * $Log
 * $
 */
