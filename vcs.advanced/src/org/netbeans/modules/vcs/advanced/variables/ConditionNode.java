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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Iterator;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.actions.*;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.ActionPerformer;
import org.openide.util.datatransfer.NewType;

import org.netbeans.modules.vcscore.VcsConfigVariable;

import org.netbeans.modules.vcs.advanced.VcsCustomizer;
import org.netbeans.modules.vcs.advanced.UserVariablesPanel;
import org.openide.DialogDisplayer;
import org.openide.util.actions.NodeAction;

/**
 * The node, that represents the condition.
 *
 * @author  Martin Entlicher
 */
public class ConditionNode extends AbstractNode {

    private Condition condition = null;
    private OperatorNode operatorNode;
    private PropertyChangeSupport conditionChangeSupport = new PropertyChangeSupport(this);

    public ConditionNode(Condition condition) {
        super(new Children.Array());
        this.condition = condition;
        operatorNode = new OperatorNode(null, condition);
        setShortDescription(NbBundle.getMessage(ConditionNode.class, "CTL_ConditionDescription", condition.getName()));
        setIconBase("org/netbeans/modules/vcs/advanced/variables/ConditionIcon"); // NOI18N
        getChildren().add(new Node[] { operatorNode });
        operatorNode.buildConditionNodes(condition);
    }
    
    public void setName(String name) {
        if (!name.equals(condition.getName())) {
            condition.setName(name);
            setDisplayName(name);
            // Necessary to refresh the "Name" property
            firePropertyChange(Node.PROP_NAME, null, name);
            // Necessary to refresh the name of the Node
            fireNameChange(null, name);
        } else {
            super.setName(name);
        }
    }
    
    public String getName() {
        return condition.getName();
    }

    public Condition getCondition() {
        return condition;
    }
    
    static Collection getAllConditionsNames(Node conditionsNode) {
        ArrayList names = new ArrayList();
        Node[] nodes = conditionsNode.getChildren().getNodes();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof ConditionNode) {
                names.add(((ConditionNode) nodes[i]).getCondition().getName());
            }
        }
        return names;
    }
    
    public final void addConditionPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        conditionChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }
    
    public final void removeConditionPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        conditionChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }
    
    protected final void fireConditionPropertyChange(String propertyName, Object oldValue, Object newValue) {
        conditionChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    public boolean canCopy() {
        return true;
    }
    
    public boolean canCut() {
        return true;
    }
    
    public boolean canDestroy() {
        return true;
    }
        
    public boolean canRename() {
        return true;
    }
    
    /** Copy this node to the clipboard.
     *
     * @return The transferable for Condition
     * @throws IOException if it could not copy
     */
    public Transferable clipboardCopy() throws java.io.IOException {
        return new ConditionCopySupport.ConditionTransferable(
            ConditionCopySupport.CONDITION_COPY_FLAVOR, this);
    }

    /** Cut this node to the clipboard.
     *
     * @return {@link Transferable} with one flavor, {@link CONDITION_CUT_FLAVOR }
     * @throws IOException if it could not cut
     */
    public Transferable clipboardCut() throws java.io.IOException {
        return new ConditionCopySupport.ConditionTransferable(
            ConditionCopySupport.CONDITION_CUT_FLAVOR, this);
    }

    protected SystemAction [] createActions() {
        return new SystemAction[] {
            SystemAction.get(CutAction.class),
            SystemAction.get(CopyAction.class),
            null,
            SystemAction.get(DeleteAction.class)
        };
    }

    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        createProperties(condition, set);
        return sheet;
    }
    
    private void createProperties(final Condition c, final Sheet.Set set) {
        set.put(new PropertySupport.ReadWrite(Node.PROP_NAME, String.class, g("CTL_Name"), g("HINT_Name")) {
            public Object getValue() {
                return c.getName();
            }
            
            public void setValue(Object value) {
                c.setName((String) value);
                ConditionNode.this.fireNameChange(null, c.getName());
                //cmd.fireChanged();
            }
        });
    }
    
    /** Get the new types that can be created in this node.
     */
    public NewType[] getNewTypes() {
        //if (list == null) return new NewType[0];
        return new NewType[] { new NewVariable() };
    }
    
    private final class NewVariable extends NewType {

        public String getName() {
            return org.openide.util.NbBundle.getBundle(AccessoryVariableNode.class).getString("CTL_NewVariable_ActionName");
        }
        
        public void create() throws java.io.IOException {
            NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine(
                org.openide.util.NbBundle.getBundle(AccessoryVariableNode.class).getString("CTL_NewVariableName"),
                org.openide.util.NbBundle.getBundle(AccessoryVariableNode.class).getString("CTL_NewVariableTitle")
                );
            //input.setInputText(org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_NewCommandLabel"));
            if (DialogDisplayer.getDefault().notify(input) != NotifyDescriptor.OK_OPTION)
                return;

            String labelName = input.getInputText();
            String name = labelName.toUpperCase();
            /* TODO:
            if (existsVariableName(name)) {
                NotifyDescriptor.Message message = new NotifyDescriptor.Message(
                    org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_VariableNameAlreadyExists")
                );
                TopManager.getDefault().notify(message);
                return ;
            }
             */
            VcsConfigVariable var = new VcsConfigVariable(name, null, "", false, false, false, null);
            AccessoryVariableNode newVar = new AccessoryVariableNode(var);
            //CommandNode newCommand = new CommandNode(Children.LEAF, cmd);
            Children ch;
            /*
            if (Children.LEAF.equals(AccessoryVariableNode.this.getChildren())) {
                ch = AccessoryVariableNode.this.getParentNode().getChildren();
            } else {
                ch = AccessoryVariableNode.this.getChildren();
            }
            ch.add(new Node[] { newVar });
             */
        }
    }
    
    private static void negateNode(Node node) {
        Node parent = node.getParentNode();
        if (parent instanceof NegationNode) {
            Node grandParent = parent.getParentNode();
            parent.getChildren().remove(new Node[] { node });
            grandParent.getChildren().remove(new Node[] { parent });
            grandParent.getChildren().add(new Node[] { node });
        } else {
            NegationNode nn = new NegationNode();
            parent.getChildren().remove(new Node[] { node });
            parent.getChildren().add(new Node[] { nn });
            nn.getChildren().add(new Node[] { node });
        }
    }

    static class VariableNode extends AbstractNode {
        
        private static final String[] comparisonStrs = new String[] { g("CTL_ConditionComparisonEquals"), g("CTL_ConditionComparisonContains") };
        private static final int[] comparisonInts = new int[] { 0, 1 };
            
        private Condition.Var var;
        private Condition enclosingCondition;
        
        public VariableNode(Condition enclosingCondition, Condition.Var var) {
            super(Children.LEAF);
            this.var = var;
            this.enclosingCondition = enclosingCondition;
            setShortDescription(NbBundle.getMessage(ConditionNode.class, "CTL_ConditionVarDescription", var.getName()));
            setIconBase("org/netbeans/modules/vcs/advanced/variables/ConditionIcon"); // NOI18N
        }
        
        public String getName() {
            return var.getName();
        }
        
        public void setName(String name) {
            var.setName(name);
            setShortDescription(NbBundle.getMessage(ConditionNode.class, "CTL_ConditionVarDescription", var.getName()));
        }
        
        Condition.Var getVar() {
            return var;
        }
        
        Condition getEnclosingCondition() {
            return enclosingCondition;
        }
        
        public boolean canCopy() {
            return true;
        }

        public boolean canCut() {
            return true;
        }

        public boolean canDestroy() {
            return true;
        }
        
        public boolean canRename() {
            return true;
        }
    
        /** Copy this node to the clipboard.
         *
         * @return The transferable for Variable
         * @throws IOException if it could not copy
         */
        public Transferable clipboardCopy() throws java.io.IOException {
            return new ConditionCopySupport.ConditionTransferable(
                ConditionCopySupport.VAR_COPY_FLAVOR, this);
        }

        /** Cut this node to the clipboard.
         *
         * @return {@link Transferable} with one flavor, {@link VAR_CUT_FLAVOR }
         * @throws IOException if it could not cut
         */
        public Transferable clipboardCut() throws java.io.IOException {
            return new ConditionCopySupport.ConditionTransferable(
                ConditionCopySupport.VAR_CUT_FLAVOR, this);
        }

        protected SystemAction [] createActions() {
            return new SystemAction[] {
                SystemAction.get(NegationAction.class),
                null,
                SystemAction.get(CutAction.class),
                SystemAction.get(CopyAction.class),
                null,
                SystemAction.get(DeleteAction.class),
                null,
                SystemAction.get(PropertiesAction.class)
            };
        }

        public Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set set = sheet.get(Sheet.PROPERTIES);
            createProperties(set);
            return sheet;
        }

        private void createProperties(final Sheet.Set set) {
            set.put(new PropertySupport.ReadWrite(Node.PROP_NAME, String.class, g("CTL_Name"), g("HINT_Name")) {
                public Object getValue() {
                    return var.getName();
                }

                public void setValue(Object value) {
                    var.setName((String) value);
                    VariableNode.this.fireNameChange(null, var.getName());
                    //cmd.fireChanged();
                }
            });
            set.put(new PropertySupport.ReadWrite("comparison", Integer.TYPE, g("CTL_ConditionComparison"), g("HINT_ConditionComparison")) {
                public Object getValue() {
                    int cv = var.getCompareValue();
                    if (cv == Condition.COMPARE_VALUE_EQUALS || cv == Condition.COMPARE_VALUE_EQUALS_IGNORE_CASE) {
                        return new Integer(0);
                    } else {
                        return new Integer(1);
                    }
                }
                
                public void setValue(Object value) {
                    int cv = var.getCompareValue();
                    int nv = ((Integer) value).intValue();
                    if (nv == 0) {
                        if (cv == Condition.COMPARE_VALUE_CONTAINS) {
                            var.setCompareValue(Condition.COMPARE_VALUE_EQUALS);
                        } else if (cv == Condition.COMPARE_VALUE_CONTAINS_IGNORE_CASE) {
                            var.setCompareValue(Condition.COMPARE_VALUE_EQUALS_IGNORE_CASE);
                        }
                    } else {
                        if (cv == Condition.COMPARE_VALUE_EQUALS) {
                            var.setCompareValue(Condition.COMPARE_VALUE_CONTAINS);
                        } else if (cv == Condition.COMPARE_VALUE_EQUALS_IGNORE_CASE) {
                            var.setCompareValue(Condition.COMPARE_VALUE_CONTAINS_IGNORE_CASE);
                        }
                    }
                }
                
                public Object getValue(String attributeName) {
                    if ("stringKeys".equals(attributeName)) {
                        return comparisonStrs;
                    }
                    if ("intValues".equals(attributeName)) {
                        return comparisonInts;
                    }
                    return super.getValue(attributeName);
                }
                
                //public PropertyEditor getPropertyEditor() {
                //    return new ComparisonPropertyEditor(var);
                //}
            });
            set.put(new PropertySupport.ReadWrite("value", String.class, g("CTL_Value"), g("HINT_Value")) {
                public Object getValue() {
                    return var.getValue();
                }

                public void setValue(Object value) {
                    var.setValue((String) value);
                    //cmd.fireChanged();
                }
            });
            set.put(new PropertySupport.ReadWrite("ignoreCase", Boolean.TYPE, g("CTL_ConditionComparisonIgnoreCase"), g("HINT_ConditionComparisonIgnoreCase")) {
                public Object getValue() {
                    int cv = var.getCompareValue();
                    return (cv == Condition.COMPARE_VALUE_CONTAINS_IGNORE_CASE || cv == Condition.COMPARE_VALUE_EQUALS_IGNORE_CASE) ?
                            Boolean.TRUE : Boolean.FALSE;
                }
                
                public void setValue(Object value) {
                    boolean isIgnoreCase = ((Boolean) value).booleanValue();
                    int cv = var.getCompareValue();
                    if (isIgnoreCase) {
                        if (cv == Condition.COMPARE_VALUE_CONTAINS) {
                            var.setCompareValue(Condition.COMPARE_VALUE_CONTAINS_IGNORE_CASE);
                        } else if (cv == Condition.COMPARE_VALUE_EQUALS) {
                            var.setCompareValue(Condition.COMPARE_VALUE_EQUALS_IGNORE_CASE);
                        }
                    } else {
                        if (cv == Condition.COMPARE_VALUE_CONTAINS_IGNORE_CASE) {
                            var.setCompareValue(Condition.COMPARE_VALUE_CONTAINS);
                        } else if (cv == Condition.COMPARE_VALUE_EQUALS_IGNORE_CASE) {
                            var.setCompareValue(Condition.COMPARE_VALUE_EQUALS);
                        }
                    }
                }
            });
        }
        
        public void negate() {
            negateNode(this);
            enclosingCondition.addVar(var, !enclosingCondition.isPositiveTest(var));
        }
    }
    
    private static class OperatorNode extends AbstractNode {
        
        private static final String[] operatorStrs = new String[] { g("ConditionNode.operation.AND"), g("ConditionNode.operation.OR") };
        private static final String[] operatorDescStrs = new String[] { g("ConditionNode.operation.AND_Desc"), g("ConditionNode.operation.OR_Desc") };
        private static final int[] operatorInts = new int[] { 0, 1 };
        
        private int operation;
        private Condition c;
        private Condition enclosingCondition;
        
        public OperatorNode(Condition enclosingCondition, Condition c) {
            super(new Children.Array());
            this.operation = c.getLogicalOperation();
            this.c = c;
            this.enclosingCondition = enclosingCondition;
            setShortDescription(NbBundle.getMessage(ConditionNode.class, "CTL_ConditionOperatorDescription"));
            setIconBase("org/netbeans/modules/vcs/advanced/variables/ConditionIcon"); // NOI18N
        }
        
        public String getName() {
            return operatorStrs[operation];
        }
        
        public void buildConditionNodes(Condition condition) {
            Condition.Var[] vars = condition.getVars();
            Condition[] cnds = condition.getConditions();
            Node[] subNodes = new Node[vars.length + cnds.length];
            for (int i = 0; i < vars.length; i++) {
                VariableNode vn = new VariableNode(condition, vars[i]);
                if (condition.isPositiveTest(vars[i])) {
                    subNodes[i] = vn;
                } else {
                    NegationNode nn = new NegationNode();
                    nn.getChildren().add(new Node[] { vn });
                    subNodes[i] = nn;
                }
            }
            for (int i = 0; i < cnds.length; i++) {
                OperatorNode operatorNode = new OperatorNode(condition, cnds[i]);
                if (condition.isPositiveTest(cnds[i])) {
                    subNodes[vars.length + i] = operatorNode;
                } else {
                    NegationNode nn = new NegationNode();
                    nn.getChildren().add(new Node[] { operatorNode });
                    subNodes[vars.length + i] = nn;
                }
                operatorNode.buildConditionNodes(cnds[i]);
            }
            this.getChildren().add(subNodes);
        }

        public boolean canCopy() {
            return false;
        }

        public boolean canCut() {
            return false;
        }

        public boolean canDestroy() {
            return c.getName().length() > 0;
        }

        public boolean canRename() {
            return false;
        }
    
        protected SystemAction [] createActions() {
            return new SystemAction[] {
                SystemAction.get(NegationAction.class),
                null,
                SystemAction.get(PasteAction.class),
                null,
                SystemAction.get(NewAction.class)
            };
        }

        public Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set set = sheet.get(Sheet.PROPERTIES);
            createProperties(set);
            return sheet;
        }

        private void createProperties(final Sheet.Set set) {
            set.put(new PropertySupport.ReadWrite("operator", Integer.TYPE, g("CTL_ConditionOperator"), g("HINT_ConditionOperator")) {
                public Object getValue() {
                    return new Integer(operation);
                }

                public void setValue(Object value) {
                    c.setLogicalOperation(operation = ((Integer) value).intValue());
                    fireDisplayNameChange(null, null);
                }
                
                public Object getValue(String attributeName) {
                    if ("stringKeys".equals(attributeName)) {
                        return operatorStrs;
                    }
                    if ("intValues".equals(attributeName)) {
                        return operatorInts;
                    }
                    return super.getValue(attributeName);
                }
            });
        }
        
        protected void createPasteTypes(Transferable t, java.util.List s) {
            boolean vcopy = t.isDataFlavorSupported(ConditionCopySupport.VAR_COPY_FLAVOR);
            boolean vcut = t.isDataFlavorSupported(ConditionCopySupport.VAR_CUT_FLAVOR);
            boolean ccopy = t.isDataFlavorSupported(ConditionCopySupport.CONDITION_COPY_FLAVOR);
            boolean ccut = t.isDataFlavorSupported(ConditionCopySupport.CONDITION_CUT_FLAVOR);

            if (vcopy || vcut) { // copy or cut some variable
                Node transNode = null;
                try {
                    transNode = (Node) t.getTransferData(t.getTransferDataFlavors()[0]);
                }
                catch (UnsupportedFlavorException e) {
                    ErrorManager.getDefault().notify(e);
                } // should not happen
                catch (java.io.IOException e) {
                    ErrorManager.getDefault().notify(e);
                } // should not happen
                if (this.equals(transNode) || transNode == null)
                    return;

                s.add(new ConditionCopySupport.VarPaste(t, this));
            }
            if (ccopy || ccut) { // copy or cut some condition
                Node transNode = null;
                try {
                    transNode = (Node) t.getTransferData(t.getTransferDataFlavors()[0]);
                }
                catch (UnsupportedFlavorException e) {
                    ErrorManager.getDefault().notify(e);
                } // should not happen
                catch (java.io.IOException e) {
                    ErrorManager.getDefault().notify(e);
                } // should not happen
                if (this.equals(transNode) || transNode == null)
                    return;

                s.add(new ConditionCopySupport.ConditionPaste(t, this));
            }
        }

        public void negate() {
            if (enclosingCondition == null) return ;
            negateNode(this);
            enclosingCondition.addCondition(c, !enclosingCondition.isPositiveTest(c));
        }
        
        /** Get the new types that can be created in this node.
         */
        public NewType[] getNewTypes() {
            return new NewType[] { new NewVariable(), new NewOperatorAND(), new NewOperatorOR() };
        }

        private final class NewVariable extends NewType {

            public String getName() {
                return org.openide.util.NbBundle.getBundle(ConditionNode.class).getString("CTL_NewVariable_ActionName");
            }

            public void create() throws java.io.IOException {
                NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine(
                    org.openide.util.NbBundle.getBundle(ConditionNode.class).getString("CTL_NewVariableName"),
                    org.openide.util.NbBundle.getBundle(ConditionNode.class).getString("CTL_NewVariableTitle")
                    );
                if (DialogDisplayer.getDefault().notify(input) != NotifyDescriptor.OK_OPTION)
                    return;

                String name = input.getInputText();
                Condition.Var var = new Condition.Var(name, "true", Condition.COMPARE_VALUE_EQUALS);
                c.addVar(var, true);
                VariableNode node = new VariableNode(enclosingCondition, var);
                OperatorNode.this.getChildren().add(new Node[] { node });
            }
        }
        
        private final class NewOperatorAND extends NewType {

            public String getName() {
                return org.openide.util.NbBundle.getBundle(ConditionNode.class).getString("CTL_NewOperatorAND_ActionName");
            }

            public void create() throws java.io.IOException {
                Condition c = new Condition("");
                c.setLogicalOperation(Condition.LOGICAL_AND);
                OperatorNode.this.c.addCondition(c, true);
                OperatorNode node = new OperatorNode(enclosingCondition, c);
                OperatorNode.this.getChildren().add(new Node[] { node });
            }
        }
        
        private final class NewOperatorOR extends NewType {

            public String getName() {
                return org.openide.util.NbBundle.getBundle(ConditionNode.class).getString("CTL_NewOperatorOR_ActionName");
            }

            public void create() throws java.io.IOException {
                Condition c = new Condition("");
                c.setLogicalOperation(Condition.LOGICAL_OR);
                OperatorNode.this.c.addCondition(c, true);
                OperatorNode node = new OperatorNode(enclosingCondition, c);
                OperatorNode.this.getChildren().add(new Node[] { node });
            }
        }
    }
    
    private static class NegationNode extends AbstractNode {
        
        public NegationNode() {
            super(new Children.Array());
            setShortDescription(NbBundle.getMessage(ConditionNode.class, "CTL_ConditionNegationDescription"));
            setIconBase("org/netbeans/modules/vcs/advanced/variables/ConditionIcon"); // NOI18N
        }
        
        public String getName() {
            return g("ConditionNode.operation.NOT");
        }
        
    }
    
    private static class NegationAction extends NodeAction {
        
        protected boolean enable(Node[] activatedNodes) {
            for (int i = 0; i < activatedNodes.length; i++) {
                if (!(activatedNodes[i] instanceof VariableNode) &&
                    !(activatedNodes[i] instanceof OperatorNode)) {
                        return false;
                }
            }
            return activatedNodes.length > 0;
        }
        
        public org.openide.util.HelpCtx getHelpCtx() {
            return new org.openide.util.HelpCtx(NegationAction.class);
        }
        
        public String getName() {
            return g("CTL_ConditionNegationAction");
        }
        
        protected boolean asynchronous() {
            return false;
        }
        
        protected void performAction(Node[] activatedNodes) {
            for (int i = 0; i < activatedNodes.length; i++) {
                if (activatedNodes[i] instanceof VariableNode) {
                    ((VariableNode) activatedNodes[i]).negate();
                }
                if (activatedNodes[i] instanceof OperatorNode) {
                    ((OperatorNode) activatedNodes[i]).negate();
                }
            }
        }
        
    }
    
    public static class Main extends AbstractNode {
        
        public Main(Children c) {
            super(c);
            setDisplayName(g("CTL_ConditionsNodeName"));
            setShortDescription(g("CTL_ConditionsNodeDescription"));
            setIconBase("org/netbeans/modules/vcs/advanced/variables/ConditionIcon"); // NOI18N
        }
        
        public boolean canCopy() {
            return false;
        }

        public boolean canCut() {
            return false;
        }

        public boolean canDestroy() {
            return false;
        }

        public boolean canRename() {
            return false;
        }
    
        protected SystemAction [] createActions() {
            return new SystemAction[] {
                SystemAction.get(PasteAction.class),
                null,
                SystemAction.get(NewAction.class)
            };
        }

        protected void createPasteTypes(Transferable t, java.util.List s) {
            boolean copy = t.isDataFlavorSupported(ConditionCopySupport.CONDITION_COPY_FLAVOR);
            boolean cut = t.isDataFlavorSupported(ConditionCopySupport.CONDITION_CUT_FLAVOR);
            
            if (copy || cut) { // copy or cut some command
                Node transNode = null;
                try {
                    transNode = (Node) t.getTransferData(t.getTransferDataFlavors()[0]);
                }
                catch (UnsupportedFlavorException e) {
                    ErrorManager.getDefault().notify(e);
                } // should not happen
                catch (java.io.IOException e) {
                    ErrorManager.getDefault().notify(e);
                } // should not happen
                if (this.equals(transNode) || transNode == null)
                    return;

                s.add(new ConditionCopySupport.ConditionPaste(t, this));
            }
        }

        /** Get the new types that can be created in this node.
         */
        public NewType[] getNewTypes() {
            return new NewType[] { new NewCondition() };
        }

        private final class NewCondition extends NewType {

            public String getName() {
                return org.openide.util.NbBundle.getBundle(ConditionNode.class).getString("CTL_NewCondition_ActionName");
            }

            public void create() throws java.io.IOException {
                NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine(
                    org.openide.util.NbBundle.getBundle(ConditionNode.class).getString("CTL_NewConditionName"),
                    org.openide.util.NbBundle.getBundle(ConditionNode.class).getString("CTL_NewConditionTitle")
                    );
                if (DialogDisplayer.getDefault().notify(input) != NotifyDescriptor.OK_OPTION)
                    return;

                String conditionName = input.getInputText();
                /* TODO:
                if (existsVariableName(name)) {
                    NotifyDescriptor.Message message = new NotifyDescriptor.Message(
                        org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_VariableNameAlreadyExists")
                    );
                    TopManager.getDefault().notify(message);
                    return ;
                }
                 */
                Condition c = new Condition(conditionName);
                ConditionNode node = new ConditionNode(c);
                Main.this.getChildren().add(new Node[] { node });
            }
        }
    
    }
    
    private static String g(String name) {
        return NbBundle.getMessage(ConditionNode.class, name);
    }

}
