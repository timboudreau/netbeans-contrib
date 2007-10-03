/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
