
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