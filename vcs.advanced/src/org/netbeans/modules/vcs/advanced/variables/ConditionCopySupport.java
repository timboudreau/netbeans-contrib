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

package org.netbeans.modules.vcs.advanced.variables;

import java.awt.datatransfer.*;
import java.util.Collection;

import org.openide.cookies.InstanceCookie;
import org.openide.nodes.*;
import org.openide.util.datatransfer.PasteType;

import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.ErrorManager;

class ConditionCopySupport extends Object {

    public static DataFlavor CONDITION_COPY_FLAVOR = new ConditionDataFlavor(
        AbstractNode.class,
        "CONDITION_COPY_FLAVOR"); // NOI18N

    public static DataFlavor CONDITION_CUT_FLAVOR = new ConditionDataFlavor(
        AbstractNode.class,
        "CONDITION_CUT_FLAVOR"); // NOI18N

    public static DataFlavor VAR_COPY_FLAVOR = new ConditionDataFlavor(
        AbstractNode.class,
        "VAR_COPY_FLAVOR"); // NOI18N

    public static DataFlavor VAR_CUT_FLAVOR = new ConditionDataFlavor(
        AbstractNode.class,
        "VAR_CUT_FLAVOR"); // NOI18N

    static class ConditionDataFlavor extends DataFlavor {
        
        private static final long serialVersionUID = 2413511288102054407L;
        
        ConditionDataFlavor(Class representationClass, String name) {
            super(representationClass, name);
        }
    }

    public static class ConditionTransferable implements Transferable {
        private AbstractNode var;
        private DataFlavor[] flavors;

        ConditionTransferable(DataFlavor flavor, AbstractNode var) {
            this(new DataFlavor[] { flavor }, var);
        }

        ConditionTransferable(DataFlavor[] flavors, AbstractNode var) {
            this.flavors = flavors;
            this.var = var;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i] == flavor) { // comparison based on exact instances, as these are static in this node
                    return true;
                }
            }
            return false;
        }

        public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, java.io.IOException {
            if (flavor instanceof ConditionDataFlavor) {
                return var;
            }
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /** Paste type for meta components.
     */
    public static class ConditionPaste extends PasteType {
        private Transferable transferable;
        private AbstractNode targetNode;
        //private FormModel targetForm;

        public ConditionPaste(Transferable t,
                              AbstractNode targetNode) {
            this.transferable = t;
            this.targetNode = targetNode;
        }

        public Transferable paste() throws java.io.IOException {
            boolean fromCut =
                transferable.isDataFlavorSupported(CONDITION_CUT_FLAVOR);

            AbstractNode sourceNode = null;
            try {
                sourceNode = (AbstractNode)
                    transferable.getTransferData(fromCut ?
                        CONDITION_CUT_FLAVOR : CONDITION_COPY_FLAVOR);
            }
            catch (java.io.IOException e) {
                ErrorManager.getDefault().notify(e);
            } // ignore - should not happen
            catch (UnsupportedFlavorException e) {
                ErrorManager.getDefault().notify(e);
            } // ignore - should not happen
            if (!fromCut) { // pasting copy of RADComponent
                copyConditions(sourceNode, targetNode);
                return null;
            }
            else { // pasting cut RADComponent (same instance)
                if (!targetNode.equals(sourceNode.getParentNode())) {
                    sourceNode.destroy();
                    copyConditions(sourceNode, targetNode);
                    return null;
                }
                // return new copy flavor, as the first one was used already
                return new ConditionTransferable(CONDITION_COPY_FLAVOR, sourceNode);
            }
        }
    }
    
    /** Paste type for meta components.
     */
    public static class VarPaste extends PasteType {
        private Transferable transferable;
        private AbstractNode targetNode;
        //private FormModel targetForm;

        public VarPaste(Transferable t,
                        AbstractNode targetNode) {
            this.transferable = t;
            this.targetNode = targetNode;
        }

        public Transferable paste() throws java.io.IOException {
            boolean fromCut =
                transferable.isDataFlavorSupported(VAR_CUT_FLAVOR);

            AbstractNode sourceNode = null;
            try {
                sourceNode = (AbstractNode)
                    transferable.getTransferData(fromCut ?
                        VAR_CUT_FLAVOR : VAR_COPY_FLAVOR);
            }
            catch (java.io.IOException e) {
                ErrorManager.getDefault().notify(e);
            } // ignore - should not happen
            catch (UnsupportedFlavorException e) {
                ErrorManager.getDefault().notify(e);
            } // ignore - should not happen
            if (!fromCut) { // pasting copy of RADComponent
                copyVars(sourceNode, targetNode);
                return null;
            }
            else { // pasting cut RADComponent (same instance)
                if (!targetNode.equals(sourceNode.getParentNode())) {
                    sourceNode.destroy();
                    copyVars(sourceNode, targetNode);
                    return null;
                }
                // return new copy flavor, as the first one was used already
                return new ConditionTransferable(VAR_COPY_FLAVOR, sourceNode);
            }
        }
    }
    
    private static void copyConditions(AbstractNode sourceNode, AbstractNode targetNode) {
        Condition c;
        if (sourceNode instanceof ConditionNode) {
            c = ((ConditionNode) sourceNode).getCondition();
        } else return ;
        if (c == null) return ;
        c = (Condition) c.clone();
        Node newNode = new ConditionNode(c);
        Collection conditionNames = ConditionNode.getAllConditionsNames(targetNode);
        c.setName(VcsUtilities.createUniqueName(c.getName(), conditionNames));
        
        targetNode.getChildren().add(new Node[] { newNode });
    }

    private static void copyVars(AbstractNode sourceNode, AbstractNode targetNode) {
        Condition.Var var;
        if (sourceNode instanceof ConditionNode.VariableNode) {
            var = ((ConditionNode.VariableNode) sourceNode).getVar();
        } else return ;
        if (var == null) return ;
        var = (Condition.Var) var.clone();
        Node newNode = new ConditionNode.VariableNode(((ConditionNode.VariableNode) sourceNode).getEnclosingCondition(), var);
        //Collection conditionNames = ConditionNode.getAllConditionsNames(targetNode);
        //c.setName(VcsUtilities.createUniqueName(c.getName(), conditionNames));
        
        targetNode.getChildren().add(new Node[] { newNode });
    }

}
