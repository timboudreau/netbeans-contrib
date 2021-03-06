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

package org.netbeans.modules.vcs.advanced.variables;

import java.awt.datatransfer.*;
import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.actions.*;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.ActionPerformer;
import org.openide.util.datatransfer.NewType;

import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcs.advanced.conditioned.ConditionedString;
import org.netbeans.modules.vcs.advanced.conditioned.IfUnlessCondition;

/**
 *
 * @author  Martin Entlicher
 */
public class BasicVariableNode extends AbstractNode {
    
    // The nodes can not contain HTML any more!!!
    //private static final String DISABLED_PRE = "<html><font color=\"#999999\">"; // NOI18N
    //private static final String DISABLED_POST = "</font></html>"; // NOI18N
    
    private VcsConfigVariable var = null;
    //private Map valuesByConditions = null;
    private ConditionedString cs = null;
    private IfUnlessCondition mainCondition = null;
    //private Condition.Var mainConditionVarIf = null;
    //private Condition.Var mainConditionVarUnless = null;
    private boolean enabled = true;

    /** Creates new BasicVariableNode */
    public BasicVariableNode(Children list) {
        super(list);
        if (list instanceof Children.SortedArray) {
            Children.SortedArray sortedList = (Children.SortedArray) list;
            init(sortedList, null);
            sortedList.setComparator(getComparator());
            setShortDescription(g("CTL_BasicVarsDescription"));
        } else {
            setShortDescription(g("CTL_BasicVarsDisabledDescription"));
            setEnabled(false);
        }
        setDisplayName(g("CTL_BasicVarsName"));
    }
    
    private Comparator getComparator() {
        return new Comparator() {
            public int compare(Object o1, Object o2) {
                if (!(o1 instanceof BasicVariableNode) || !(o2 instanceof BasicVariableNode)) throw new IllegalArgumentException();
                VcsConfigVariable v1 = ((BasicVariableNode) o1).getVariable();
                VcsConfigVariable v2 = ((BasicVariableNode) o2).getVariable();
                if (v1 == null || v2 == null) return 0;
                return v1.getOrder() - v2.getOrder();
            }
            public boolean equals(Object obj) {
                if (BasicVariableNode.this.var == null) return false;
                return BasicVariableNode.this.var.equals(obj);
            }
        };
    }
    
    public BasicVariableNode(VcsConfigVariable var) {
        this(var, false);
    }
    
    public BasicVariableNode(VcsConfigVariable var, boolean enableConditions) {
        super(Children.LEAF);
        if (enableConditions) {
            Map valuesByConditions = new HashMap();
            valuesByConditions.put(null, var.getValue());
            cs = new ConditionedString(var.getName(), valuesByConditions);
            mainCondition = new IfUnlessCondition(null);
            mainCondition.setConditionName(var.getName());
        }
        init(null, var);
    }
    
    public BasicVariableNode(String name, Condition[] conditions, Map varsByConditions) {
        super(Children.LEAF);
        Condition[] subConditions = conditions[0].getConditions();
        Condition c = null;
        for (int i = 0; i < subConditions.length; i++) {
            if (conditions[0].isPositiveTest(subConditions[i])) {
                c = subConditions[i];
                break;
            }
        }
        Map valuesByConditions = new HashMap();
        VcsConfigVariable var = null;
        for (int i = 0; i < conditions.length; i++) {
            var = (VcsConfigVariable) varsByConditions.get(conditions[i]);
            String value = var.getValue();
            if (conditions[i].getVars().length == 0) {
            //if (subConditions.length > 0 && !conditions[i].isPositiveTest(subConditions[subConditions.length - 1])) {
                // No condition is applied to the <value>
                valuesByConditions.put(null, value);
            } else {
                valuesByConditions.put(conditions[i], value);
            }
        }
        if (c == null && valuesByConditions.size() == 1) {
            c = (Condition) valuesByConditions.keySet().iterator().next();
            var.setValue((String) valuesByConditions.get(c));
            //valuesByConditions = null;
            valuesByConditions.remove(c);
            valuesByConditions.put(null, var.getValue());
        }
        cs = new ConditionedString(var.getName(), valuesByConditions);
        mainCondition = new IfUnlessCondition(c);
        mainCondition.setConditionName(var.getName());
        /*
        if (mainCondition != null) {
            Condition.Var[] cvars = mainCondition.getVars();
            for (int i = 0; i < cvars.length; i++) {
                if (mainCondition.isPositiveTest(cvars[i])) { // true when compared to "" => unless
                    mainConditionVarUnless = cvars[i];
                } else { // false when compared to "" => if
                    mainConditionVarIf = cvars[i];
                }
            }
        }
         */
        init(null, var);
    }
    
    /*
    public BasicVariableNode(Children.SortedArray list, VcsConfigVariable var) {
        super(Children.LEAF);
        init(list, var);
        list.add(new BasicVariableNode[] { this });
    }
     */
    
    private void init(Children.SortedArray list, VcsConfigVariable var) {
        this.var = var;
        //this.list = list;
        getCookieSet().add(new VariablesIndex());
        setIconBase("org/netbeans/modules/vcs/advanced/variables/BasicVariables"); // NOI18N
        if (var != null) {
            setShortDescription(NbBundle.getMessage(BasicVariableNode.class, "CTL_BasicVarDescription", var.getLabel()));
        }
    }
    
    public void setName(String name) {
        if (var != null && !name.equals(var.getLabel())) {
            var.setLabel(name);
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
        if (var != null) {
            String label = var.getLabel();
            if (label == null || label.length() == 0) {
                label = var.getName();
            }
            return label;
        } else {
            return super.getName();
        }
    }
    
    /* The Nodes can not contain HTML any more!!!
    public String getDisplayName() {
        if (enabled) return super.getDisplayName();
        else return DISABLED_PRE + super.getDisplayName() + DISABLED_POST;
    }
     */
    
    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return ;
        this.enabled = enabled;
        if (enabled) setIconBase("org/netbeans/modules/vcs/advanced/variables/BasicVariables"); // NOI18N
        else setIconBase("org/netbeans/modules/vcs/advanced/variables/BasicVariablesGray"); // NOI18N
        firePropertyChange(Node.PROP_DISPLAY_NAME, null, null);
        firePropertyChange(Node.PROP_ICON, null, null);
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public VcsConfigVariable getVariable() {
        if (cs != null) {
            Map valuesByConditions = cs.getValuesByConditions();
            if (valuesByConditions.size() == 1 && valuesByConditions.keySet().iterator().next() == null) {
                var.setValue((String) valuesByConditions.values().iterator().next());
            }
        }
        return var;
    }
    
    public Map getVarsByConditions() {
        if (cs != null) {
            Condition mainC = mainCondition.getCondition();
            Map valuesByConditions = cs.getValuesByConditions();
            if (mainC == null && valuesByConditions.size() == 1 && valuesByConditions.keySet().iterator().next() == null) {
                return null;
            } else {
                Map varsByConditions = new HashMap();
                for (Iterator it = valuesByConditions.keySet().iterator(); it.hasNext(); ) {
                    Condition c = (Condition) it.next();
                    String value = (String) valuesByConditions.get(c);
                    VcsConfigVariable cvar = (VcsConfigVariable) var.clone();
                    cvar.setValue(value);
                    if (c == null) {
                        c = VariableIO.createComplementaryCondition(var.getName(), mainC, valuesByConditions.keySet());
                    } else if (mainC != null) {
                        c.addCondition(mainC, true);
                    }
                    varsByConditions.put(c, cvar);
                }
                return varsByConditions;
            }
        } else {
            return null;
        }
    }
    
    public Collection getAllBasicVariablesNames() {
        BasicVariableNode root = this;
        if (Children.LEAF.equals(this.getChildren())) {
            root = (BasicVariableNode) getParentNode();
        }
        ArrayList names = new ArrayList();
        for (Enumeration nodesEnum = root.getChildren().nodes(); nodesEnum.hasMoreElements(); ) {
            BasicVariableNode varNode = (BasicVariableNode) nodesEnum.nextElement();
            names.add(varNode.getVariable().getName());
        }
        return names;
    }

    static Collection getAllVariablesNames(Node varNode) {
        Node root = varNode;
        while (root.getParentNode() != null) root = root.getParentNode();
        ArrayList varNames = new ArrayList();
        fillVariableNames(root, varNames);
        return varNames;
    }
    
    private static void fillVariableNames(Node varNode, Collection varNames) {
        VcsConfigVariable var;
        if (varNode instanceof BasicVariableNode) {
            var = ((BasicVariableNode) varNode).getVariable();
        } else if (varNode instanceof AccessoryVariableNode) {
            var = ((AccessoryVariableNode) varNode).getVariable();
        } else {
            var = null;
        }
        if (var != null) {
            varNames.add(var.getName());
        }
        Node[] subNodes = varNode.getChildren().getNodes();
        for (int i = 0; i < subNodes.length; i++) {
            fillVariableNames(subNodes[i], varNames);
        }
    }
    
    public boolean canCopy() {
        return (Children.LEAF.equals(getChildren()));
    }
    
    public boolean canCut() {
        return (Children.LEAF.equals(getChildren()));
    }
    
    public boolean canDestroy() {
        return (Children.LEAF.equals(getChildren()));
    }
        
    public boolean canRename() {
        return (Children.LEAF.equals(getChildren()));
    }
    
    /** Copy this node to the clipboard.
     *
     * @return The transferable for VcsCommand
     * @throws IOException if it could not copy
     */
    public Transferable clipboardCopy() throws java.io.IOException {
        return new VariableCopySupport.VariableTransferable(
            VariableCopySupport.VARIABLE_COPY_FLAVOR, this);
    }

    /** Cut this node to the clipboard.
     *
     * @return {@link Transferable} with one flavor, {@link COMMAND_CUT_FLAVOR }
     * @throws IOException if it could not cut
     */
    public Transferable clipboardCut() throws java.io.IOException {
        return new VariableCopySupport.VariableTransferable(
            VariableCopySupport.VARIABLE_CUT_FLAVOR, this);
    }

    /** Accumulate the paste types that this node can handle
     * for a given transferable.
     * <P>
     * Obtain the paste types from the
     * {@link VariableCopySupport.VariablePaste transfer data} and inserts them into the set.
     *
     * @param t a transferable containing clipboard data
     * @param s a list of {@link PasteType}s that will have added to it all types
     *    valid for this node
     */
    protected void createPasteTypes(Transferable t, java.util.List s) {
        if (Children.LEAF.equals(this.getChildren()))
            return;

        boolean copy = t.isDataFlavorSupported(VariableCopySupport.VARIABLE_COPY_FLAVOR);
        boolean cut = t.isDataFlavorSupported(VariableCopySupport.VARIABLE_CUT_FLAVOR);

        if (copy || cut) { // copy or cut some command
            Node transNode = null;
            try {
                transNode = (Node) t.getTransferData(t.getTransferDataFlavors()[0]);
            }
            catch (UnsupportedFlavorException e) {} // should not happen
            catch (java.io.IOException e) {} // should not happen
            if (this.equals(transNode) || transNode == null)
                return;

            s.add(new VariableCopySupport.VariablePaste(t, this));
        }
    }

    protected SystemAction [] createActions() {
        ArrayList actions = new ArrayList();
        actions.add(SystemAction.get(MoveUpAction.class));
        actions.add(SystemAction.get(MoveDownAction.class));
        actions.add(null);
        if (Children.LEAF.equals(this.getChildren())) {
            actions.add(SystemAction.get(CutAction.class));
            actions.add(SystemAction.get(CopyAction.class));
        } else {
            actions.add(SystemAction.get(PasteAction.class));
        }
        if (Children.LEAF.equals(this.getChildren())) {
            actions.add(SystemAction.get(DeleteAction.class));
        } else {
            actions.add(null);
            actions.add(SystemAction.get(NewAction.class));
        }
        //actions.add(null);
        //actions.add(SystemAction.get(PropertiesAction.class));
        SystemAction[] array = new SystemAction [actions.size()];
        actions.toArray(array);
        return array;
    }
    
    public SystemAction[] getActions() {
        if (enabled) return super.getActions();
        else return new SystemAction[0];
    }

    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        if (var != null) //set.put(new PropertySupport.Name(this));
            createProperties(var, set);
        return sheet;
    }
    
    private void createProperties(final VcsConfigVariable var, final Sheet.Set set) {
        set.put(new PropertySupport.ReadWrite("vname", String.class, g("CTL_Name"), g("HINT_Name")) {
            public Object getValue() {
                return var.getName();
            }
            
            public void setValue(Object value) {
                var.setName((String) value);
                //cmd.fireChanged();
            }
        });
        set.put(new PropertySupport.ReadWrite(Node.PROP_NAME, String.class, g("CTL_Label"), g("HINT_Label")) {
                        public Object getValue() {
                            return var.getLabel();
                        }
                        
                        public void setValue(Object value) {
                            var.setLabel((String) value);
                            BasicVariableNode.this.fireNameChange(null, (String) value);
                            //cmd.fireChanged();
                        }
                });
        set.put(new PropertySupport.ReadWrite("labelMnemonic", Character.class, g("CTL_LabelMnemonic"), g("HINT_LabelMnemonic")) {
                        public Object getValue() {
                            Character value = var.getLabelMnemonic();
                            if (value == null) {
                                value = new Character((char) 0);
                            }
                            return value;
                        }
                        
                        public void setValue(Object value) {
                            var.setLabelMnemonic((Character) value);
                            //cmd.fireChanged();
                        }

                        public boolean supportsDefaultValue() {
                            return true;
                        }
                    
                        public void restoreDefaultValue() {
                            var.setLabelMnemonic(null);
                            firePropertyChange(this.getName(), null, null);
                        }
                });
        set.put(new PropertySupport.ReadWrite("a11yName", String.class, g("CTL_A11yName"), g("HINT_A11yName")) {
                        public Object getValue() {
                            return var.getA11yName();
                        }
                        
                        public void setValue(Object value) {
                            var.setA11yName((String) value);
                            //cmd.fireChanged();
                        }

                        public boolean supportsDefaultValue() {
                            return true;
                        }
                    
                        public void restoreDefaultValue() {
                            var.setA11yName(null);
                            firePropertyChange(this.getName(), null, null);
                        }
                });
        set.put(new PropertySupport.ReadWrite("a11yDescription", String.class, g("CTL_A11yDescription"), g("HINT_A11yDescription")) {
                        public Object getValue() {
                            return var.getA11yDescription();
                        }
                        
                        public void setValue(Object value) {
                            var.setA11yDescription((String) value);
                            //cmd.fireChanged();
                        }

                        public boolean supportsDefaultValue() {
                            return true;
                        }
                    
                        public void restoreDefaultValue() {
                            var.setA11yDescription(null);
                            firePropertyChange(this.getName(), null, null);
                        }
                });
        set.put(new PropertySupport.ReadOnly("order", String.class, g("CTL_Order"), g("HINT_Order")) {
                        public Object getValue() {
                            //System.out.println("getName: cmd = "+cmd);
                            //int order = ((Children.SortedArray) getChildren()).indexOf(this);
                            return Integer.toString(var.getOrder());
                        }
                });
        if (mainCondition != null) {
            set.put(new PropertySupport.ReadWrite("if", String.class, g("CTL_DefIf"), g("HINT_DefIf")) {
                public Object getValue() {
                    return mainCondition.getIf();
                }
                
                public void setValue(Object value) {
                    mainCondition.setIf((String) value);
                }
            });
            set.put(new PropertySupport.ReadWrite("unless", String.class, g("CTL_DefIfNot"), g("HINT_DefIfNot")) {
                public Object getValue() {
                    return mainCondition.getUnless();
                }
                
                public void setValue(Object value) {
                    mainCondition.setUnless((String) value);
                }
            });
        }
        if (cs != null) {
            set.put(new PropertySupport.ReadWrite("value", ConditionedString.class, g("CTL_Value"), g("HINT_Value")) {
                public Object getValue() {
                    return cs;
                }

                public void setValue(Object value) {
                    cs = (ConditionedString) value;
                    //cmd.fireChanged();
                }
                
                private PropertyEditor cachedPropertyEditor;
                
                public PropertyEditor getPropertyEditor() {
                    if (cachedPropertyEditor == null) {
                        cachedPropertyEditor = new ConditionedString.ConditionedStringPropertyEditor();
                    }
                    return cachedPropertyEditor;
                }
            });
        } else {
            set.put(new PropertySupport.ReadWrite("value", String.class, g("CTL_Value"), g("HINT_Value")) {
                public Object getValue() {
                    return var.getValue();
                }
                
                public void setValue(Object value) {
                    var.setValue((String) value);
                    //cmd.fireChanged();
                }
            });
        }
        set.put(new PropertySupport.ReadWrite("selector", String.class, g("CTL_Selector"), g("HINT_Selector")) {
            public Object getValue() {
                return var.getCustomSelector();
            }
            
            public void setValue(Object value) {
                var.setCustomSelector((String) value);
                //cmd.fireChanged();
            }
        });
        set.put(new PropertySupport.ReadWrite("localFile", Boolean.TYPE, g("CTL_LocalFile"), g("HINT_LocalFile")) {
            public Object getValue() {
                //System.out.println("getName: cmd = "+cmd);
                return var.isLocalFile() ? Boolean.TRUE : Boolean.FALSE;
            }
            
            public void setValue(Object value) {
                var.setLocalFile(((Boolean) value).booleanValue());
                //cmd.fireChanged();
            }
        });
        set.put(new PropertySupport.ReadWrite("localDir", Boolean.TYPE, g("CTL_LocalDir"), g("HINT_LocalDir")) {
            public Object getValue() {
                //System.out.println("getName: cmd = "+cmd);
                return var.isLocalDir() ? Boolean.TRUE : Boolean.FALSE;
            }
            
            public void setValue(Object value) {
                var.setLocalDir(((Boolean) value).booleanValue());
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
            return org.openide.util.NbBundle.getBundle(BasicVariableNode.class).getString("CTL_NewVariable_ActionName");
        }
        
        public void create() throws java.io.IOException {
            NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine(
                org.openide.util.NbBundle.getBundle(BasicVariableNode.class).getString("CTL_NewVariableName"),
                org.openide.util.NbBundle.getBundle(BasicVariableNode.class).getString("CTL_NewVariableTitle")
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
            VcsConfigVariable var = new VcsConfigVariable(name, labelName, "", true, false, false, null, BasicVariableNode.this.getChildren().getNodes().length);
            BasicVariableNode newVar = new BasicVariableNode(var);
            //CommandNode newCommand = new CommandNode(Children.LEAF, cmd);
            Children ch;
            if (Children.LEAF.equals(BasicVariableNode.this.getChildren())) {
                ch = BasicVariableNode.this.getParentNode().getChildren();
            } else {
                ch = BasicVariableNode.this.getChildren();
            }
            ch.add(new Node[] { newVar });
        }
    }
    
    private final class VariablesIndex extends org.openide.nodes.Index.Support {
        
        /**
         * Get the nodes sorted by this index.
         * @return the nodes
         */
        public Node[] getNodes() {
            return BasicVariableNode.this.getChildren().getNodes();
        }
        
        /** Get the node count.
         * @return the count
         */
        public int getNodesCount() {
            return getNodes().length;
        }
        
        /** Reorder by permutation.
         * @param perm the permutation
         */
        public void reorder(int[] perm) {
            Children children = BasicVariableNode.this.getChildren();
            /*
            if (children instanceof Index.ArrayChildren) {
                Index.ArrayChildren achildren = (Index.ArrayChildren) children;
                achildren.reorder(perm);
            }
             */
            Node[] nodes = children.getNodes();
            for (int i = 0; i < nodes.length; i++) {
                BasicVariableNode varNode = (BasicVariableNode) nodes[i];
                VcsConfigVariable var = varNode.getVariable();
                var.setOrder(perm[i]);
                varNode.firePropertyChange(Node.PROP_PROPERTY_SETS, null, null);
            }
            // reset the comparator to refresh node order
            ((Children.SortedArray) children).setComparator(getComparator());
        }
    }
    
    /**
     * Deletes the current variable.
     *
    public void delete() {
        try {
            destroy();
        } catch (java.io.IOException exc) {
            // silently ignored
        }
    }
     */
    
    private String g(String name) {
        return NbBundle.getMessage(BasicVariableNode.class, name);
    }

}
