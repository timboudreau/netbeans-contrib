
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

package org.netbeans.modules.vcscore.annotation;


import org.openide.nodes.*;
import org.openide.util.actions.NodeAction;
import org.openide.actions.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;


public class AddTextAction extends NodeAction {

    private static final long serialVersionUID = 1269035889550615963L;

    public AddTextAction() {
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
        return NbBundle.getBundle(AddTextAction.class).getString("ANNOT_ADD_TEXT");
    }
    
    public org.openide.util.HelpCtx getHelpCtx() {
        return null;
    }
    
    /**
     * @return false to run in AWT thread.
     */
    protected boolean asynchronous() {
        return false;
    }
    
    protected void performAction(org.openide.nodes.Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
//            System.out.println("nodeclass=" + actNodes[i].getClass().getName());
            if (nodes[i] instanceof AnnotPatternNode) {
                AnnotPatternNode annotNode = (AnnotPatternNode) nodes[i];
                AnnotPatternNode newNode = AnnotPatternNode.createInstance(AnnotPatternNode.TYPE_TEXT);
                annotNode.getChildren().add(new Node[] {newNode});
            }
        }
    }
    
}