/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.grouping;

import java.awt.event.ActionEvent;
import java.util.*;
import javax.swing.JMenuItem;
import org.openide.awt.JMenuPlus;
import javax.swing.JMenu;
import javax.swing.event.*;
import java.io.*;

import org.openide.awt.Actions;
import org.openide.awt.JInlineMenu;
import org.openide.util.actions.*;
import org.openide.filesystems.FileObject;
import org.openide.*;
import org.openide.util.*;
import org.openide.nodes.*;
import org.openide.loaders.*;
import org.openide.filesystems.*;

import org.netbeans.modules.vcscore.actions.GeneralCommandAction;



/** Action sensitive to the node selection that does something useful.
 *
 * @author  builder
 */
public class VerifyGroupAction extends GeneralCommandAction {
    
    private transient VcsGroupNode[] groupNodes;
    
    private static final long serialVersionUID = -7382933854093593819L;
    
    public VerifyGroupAction() {
        this.delegateToAbstractAction(false);
    }


    public String getName () {
        return NbBundle.getMessage(VerifyGroupAction.class, "LBL_VerifyGroupAction"); //NOI18N
    }

    protected String iconResource () {
        return null;
    }

    public HelpCtx getHelpCtx () {
        //return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        return new HelpCtx (VerifyGroupAction.class);
    }
    
    /**
     * This method doesn't extract the fileobjects from the activated nodes itself, but rather
     * consults the AbstractCommandAction to get a list of supporters.
     * On each supporter then checks if if it enables the action.
     * All supporters need to come to a concensus in order for the action to be enabled.
     * *experimental* annotates the toolbar tooltip according to the supporter's requests.
     */
    protected boolean enable(Node[] nodes) {
        if (nodes == null) return false;
        Set nodeList = new HashSet();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof VcsGroupNode) {
                if (nodes[i].getChildren().getNodesCount() != 0) {
                    nodeList.add(nodes[i]);
                }
            }
            if (nodes[i] instanceof MainVcsGroupNode) {
                MainVcsGroupNode main = (MainVcsGroupNode)nodes[i];
                Enumeration en = main.getChildren().nodes();
                while (en.hasMoreElements()) {
                    Node nd = (Node)en.nextElement();
                    if (nd.getChildren().getNodes().length != 0) {
                        nodeList.add(nd);
                    }
                }
                
            }
        }
        if (nodeList.size() == 0) return false;
        VcsGroupNode[] nds = new VcsGroupNode[nodeList.size()];
        nds = (VcsGroupNode[])nodeList.toArray(nds);
        groupNodes = nds;
        boolean retValue;
        retValue = super.enable(nds);
        return retValue;
    }    
   

   public VcsGroupNode[] getActivatedGroups() {
       return groupNodes;
   }
    
}
