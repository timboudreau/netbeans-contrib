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

package org.netbeans.modules.vcs.advanced.variables;

import java.awt.datatransfer.*;
import java.util.Collection;

import org.openide.cookies.InstanceCookie;
import org.openide.nodes.*;
import org.openide.util.datatransfer.PasteType;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.util.VcsUtilities;

class VariableCopySupport extends Object {

    public static DataFlavor VARIABLE_COPY_FLAVOR = new VariableDataFlavor(
        AbstractNode.class,
        "VARIABLE_COPY_FLAVOR"); // NOI18N

    public static DataFlavor VARIABLE_CUT_FLAVOR = new VariableDataFlavor(
        AbstractNode.class,
        "VARIABLE_COPY_FLAVOR"); // NOI18N

    static class VariableDataFlavor extends DataFlavor {
        VariableDataFlavor(Class representationClass, String name) {
            super(representationClass, name);
        }
    }

    // -----------

    public static class VariableTransferable implements Transferable {
        private AbstractNode var;
        private DataFlavor[] flavors;

        VariableTransferable(DataFlavor flavor, AbstractNode var) {
            this(new DataFlavor[] { flavor }, var);
        }

        VariableTransferable(DataFlavor[] flavors, AbstractNode var) {
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
        throws UnsupportedFlavorException, java.io.IOException
        {
            if (flavor instanceof VariableDataFlavor) {
                return var;
            }
            throw new UnsupportedFlavorException(flavor);
        }
    }

    // -----------

    /** Method for checking whether a component can be moved to a container
     * (the component should not be pasted to its own sub-container
     * or even to itself). *
    public static boolean canPasteCut(VcsCommand sourceCmd,
                                      FormModel targetForm,
                                      ComponentContainer targetContainer) {
        if (sourceComponent.getFormModel() != targetForm)
            return true;

        if (targetContainer == null
                || targetContainer == targetForm.getModelContainer())
            return targetForm.getModelContainer().getIndexOf(sourceComponent) < 0;

        RADComponent targetComponent = (RADComponent) targetContainer;

        return sourceComponent != targetComponent
               && sourceComponent.getParentComponent() != targetComponent
               && !sourceComponent.isParentComponent(targetComponent);
    } */

    // -----------

    /** Paste type for meta components.
     */
    public static class VariablePaste extends PasteType {
        private Transferable transferable;
        private AbstractNode targetNode;
        //private FormModel targetForm;

        public VariablePaste(Transferable t,
                             AbstractNode targetNode) {
            this.transferable = t;
            this.targetNode = targetNode;
        }

        public Transferable paste() throws java.io.IOException {
            boolean fromCut =
                transferable.isDataFlavorSupported(VARIABLE_CUT_FLAVOR);

            AbstractNode sourceNode = null;
            try {
                sourceNode = (AbstractNode)
                    transferable.getTransferData(fromCut ?
                        VARIABLE_CUT_FLAVOR : VARIABLE_COPY_FLAVOR);
            }
            catch (java.io.IOException e) { } // ignore - should not happen
            catch (UnsupportedFlavorException e) { } // ignore - should not happen

            //if (sourceCommand == null)
            //    return null;

            if (!fromCut) { // pasting copy of RADComponent
                copyVariables(sourceNode, targetNode);
                /*
                targetForm.getComponentCreator()
                    .copyComponent(sourceComponent, targetContainer);
                 */
                return null;
            }
            else { // pasting cut RADComponent (same instance)
                if (!targetNode.equals(sourceNode.getParentNode())) {
                    //CommandNode newNode = new CommandNode(Children.LEAF, sourceCommand.getCommand());
                    sourceNode.destroy();
                    copyVariables(sourceNode, targetNode);
                    //targetNode.getChildren().add(new Node[] { newNode });
                    return null;
                }
                /*
                FormModel sourceForm = sourceComponent.getFormModel();
                if (sourceForm != targetForm) { // taken from another form
                    Node sourceNode = sourceComponent.getNodeReference();
                    // delete component in the source
                    if (sourceNode != null)
                        sourceNode.destroy();
                    else throw new IllegalStateException();

                    sourceComponent.initialize(targetForm);
                }
                else { // moving within the same form
                    if (!canPasteCut(sourceComponent, targetForm, targetContainer))
                        return transferable; // ignore paste

                    // remove source component from its parent
                    sourceForm.removeComponent(sourceComponent);
                }
                 */

                // return new copy flavor, as the first one was used already
                return new VariableTransferable(VARIABLE_COPY_FLAVOR, sourceNode);
            }
        }
    }
    
    private static void copyVariables(AbstractNode sourceNode, AbstractNode targetNode) {
        VcsConfigVariable var;
        if (sourceNode instanceof BasicVariableNode) {
            var = ((BasicVariableNode) sourceNode).getVariable();
        } else if (sourceNode instanceof AccessoryVariableNode) {
            var = ((AccessoryVariableNode) sourceNode).getVariable();
        } else return ;
        if (var == null) return ;
        var = new VcsConfigVariable(var.getName(), var.getLabel(), var.getValue(),
                                    var.isBasic(), var.isLocalFile(), var.isLocalDir(),
                                    var.getCustomSelector(), var.getOrder());
        
        AbstractNode newNode;
        if (targetNode instanceof BasicVariableNode) {
            var.setBasic(true);
            String label = var.getLabel();
            if (label ==  null || label.length() == 0) var.setLabel(var.getName());
            newNode = new BasicVariableNode(var);
        } else if (targetNode instanceof AccessoryVariableNode) {
            var.setBasic(false);
            newNode = new AccessoryVariableNode(var);
        } else return ;
        Collection varNames = BasicVariableNode.getAllVariablesNames(targetNode);
        varNames.addAll(Variables.getContextVariablesNames());
        var.setName(VcsUtilities.createUniqueName(var.getName(), varNames));
        
        targetNode.getChildren().add(new Node[] { newNode });
    }

}
