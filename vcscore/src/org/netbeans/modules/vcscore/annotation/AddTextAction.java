
/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
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