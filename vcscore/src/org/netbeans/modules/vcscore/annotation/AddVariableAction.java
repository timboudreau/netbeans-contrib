
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
import javax.swing.*;
import org.openide.awt.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;


public class AddVariableAction extends NodeAction {
    
    private static final long serialVersionUID = -7194584692531227095L;
    
    public AddVariableAction() {
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
        return NbBundle.getBundle(AddVariableAction.class).getString("ANNOT_ADD_VARIABLE");
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
    
    protected void performAction(org.openide.nodes.Node[] node) {
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
                AnnotPatternNode newNode = AnnotPatternNode.createInstance(AnnotPatternNode.TYPE_VARIABLE);
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
        JMenu menu=new JMenuPlus(getName()); // NOI18N

        String[] varArray = AnnotPatternNode.VARIABLES_ARRAY_DISP_NAMES;
        for (int i = 0; i < varArray.length; i++) {
            menu.add(createItem(varArray[i]));
        }

        return menu;
    }

    
}