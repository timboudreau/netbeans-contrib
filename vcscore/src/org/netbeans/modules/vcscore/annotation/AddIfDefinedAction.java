
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

package org.netbeans.modules.vcscore.annotation;


import org.openide.nodes.*;
import org.openide.util.actions.NodeAction;
import org.openide.actions.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;
import javax.swing.*;
import org.openide.awt.*;

public class AddIfDefinedAction extends NodeAction {

    private static final long serialVersionUID = -4147641295096858568L;

    public AddIfDefinedAction() {
    }

    protected boolean enable(org.openide.nodes.Node[] node) {
        boolean toReturn = true;
        for (int i = 0; i < node.length; i++) {
            if (node[i] instanceof AnnotPatternNode) {
                AnnotPatternNode annotNode = (AnnotPatternNode)node[i];
                if (!annotNode.getType().equals(AnnotPatternNode.TYPE_PARENT)) {
                    toReturn = false;
                }
            }
        }
        return toReturn;
    }
    
    public java.lang.String getName() {
        return NbBundle.getBundle(AddVariableAction.class).getString("ANNOT_ADD_IF_DEFINED");
    }
    
    public org.openide.util.HelpCtx getHelpCtx() {
        return null;
    }
    
    protected void performAction(org.openide.nodes.Node[] node) {
    }
    
    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        Node[] actNodes;
        if (source instanceof Node) {
            actNodes = new Node[] { (Node) source };
        } else {
            actNodes = AnnotPatternCustomEditor.getActiveTopComponent().getActivatedNodes();
        }
        for (int i = 0; i < actNodes.length; i++) {
            if (actNodes[i] instanceof AnnotPatternNode) {
                AnnotPatternNode annotNode = (AnnotPatternNode)actNodes[i];
                AnnotPatternNode newNode = AnnotPatternNode.createInstance(AnnotPatternNode.TYPE_CONDITION);
                AnnotPatternNode trueNode = AnnotPatternNode.createInstance(AnnotPatternNode.TYPE_PARENT);
                trueNode.setName(NbBundle.getBundle(AddIfDefinedAction.class).getString("ANNOT_NODE_NAME_TRUE"));
                AnnotPatternNode falseNode = AnnotPatternNode.createInstance(AnnotPatternNode.TYPE_PARENT);
                falseNode.setName(NbBundle.getBundle(AddIfDefinedAction.class).getString("ANNOT_NODE_NAME_FALSE"));
                newNode.getChildren().add(new Node[] {trueNode, falseNode}) ;
                newNode.setName(actionEvent.getActionCommand());
                annotNode.getChildren().add(new Node[] {newNode});
            }
        }
    }
    
    
    //-------------------------------------------
    private JMenuItem createItem(String name){
        JMenuItem item=null ;
        item=new JMenuItem(name);
        item.setActionCommand(name);
        item.addActionListener(this);
        return item;
    }    
    
    public javax.swing.JMenuItem getPopupPresenter() {
        JMenu menu=new JMenuPlus(getName());
	
        String[] varArray = AnnotPatternNode.VARIABLES_ARRAY_DISP_NAMES;
        for (int i = 0; i < varArray.length; i++) {
            menu.add(createItem(varArray[i]));
        }
	
	return menu;
    }    
    
}