/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
import java.lang.ref.WeakReference;

import org.openide.awt.Actions;
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
    
    private transient WeakReference groupNodes;
    
    private static final long serialVersionUID = -7382933854093593819L;
    
    public VerifyGroupAction() {
        super();
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
        groupNodes = null;
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
        groupNodes = new WeakReference(nds);
        boolean retValue;
        retValue = super.enable(nds);
        return retValue;
    }    
   

   public VcsGroupNode[] getActivatedGroups() {
       if (groupNodes == null) {
           return null;
       }
       Object array = groupNodes.get();
       if (array != null) {
           VcsGroupNode[] toReturn = (VcsGroupNode[])array;
           return toReturn;
       }
       return null;
   }
    
}
