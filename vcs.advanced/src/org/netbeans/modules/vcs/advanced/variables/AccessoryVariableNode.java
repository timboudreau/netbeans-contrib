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

import org.netbeans.modules.vcs.advanced.VcsCustomizer;
import org.netbeans.modules.vcs.advanced.UserVariablesPanel;
import org.netbeans.modules.vcs.advanced.conditioned.ConditionedString;
import org.netbeans.modules.vcs.advanced.conditioned.IfUnlessCondition;

/**
 *
 * @author  Martin Entlicher
 */
public class AccessoryVariableNode extends AbstractNode {

    private VcsConfigVariable var = null;
    private ConditionedString cs = null;
    private IfUnlessCondition mainCondition = null;
    private Children.Array list = null;
    private PropertyChangeSupport variableChangeSupport = new PropertyChangeSupport(this);

    /** Creates new AccessoryVariableNode */
    public AccessoryVariableNode(Children.SortedArray list) {
        super(list);
        init(list, null);
        list.setComparator(new Comparator() {
            public int compare(Object o1, Object o2) {
                if (!(o1 instanceof AccessoryVariableNode) || !(o2 instanceof AccessoryVariableNode)) throw new IllegalArgumentException();
                VcsConfigVariable v1 = ((AccessoryVariableNode) o1).getVariable();
                VcsConfigVariable v2 = ((AccessoryVariableNode) o2).getVariable();
                if (v1 == null || v2 == null) return 0;
                return v1.getName().compareTo(v2.getName());
            }
            public boolean equals(Object obj) {
                if (AccessoryVariableNode.this.var == null) return false;
                return AccessoryVariableNode.this.var.getName().equals(((AccessoryVariableNode) obj).getName());
            }
        });
        setDisplayName(g("CTL_AccessoryVarsName"));
        setShortDescription(g("CTL_AccessoryVarsDescription"));
    }

    public AccessoryVariableNode(VcsConfigVariable var) {
        this(var, false);
    }
    
    public AccessoryVariableNode(VcsConfigVariable var, boolean enableConditions) {
        super(Children.LEAF);
        if (enableConditions) {
            Map valuesByConditions = new HashMap();
            valuesByConditions.put(null, var.getValue());
            cs = new ConditionedString(var.getName(), valuesByConditions);
        }
        init(null, var);
    }
    
    public AccessoryVariableNode(String name, Condition[] conditions, Map varsByConditions) {
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
                // No condition is applied to the <value>
                valuesByConditions.put(null, value);
            } else {
                valuesByConditions.put(conditions[i], value);
            }
        }
        if (c == null && valuesByConditions.size() == 1) {
            c = (Condition) valuesByConditions.keySet().iterator().next();
            var.setValue((String) valuesByConditions.get(c));
            valuesByConditions.remove(c);
            valuesByConditions.put(null, var.getValue());
        }
        cs = new ConditionedString(var.getName(), valuesByConditions);
        mainCondition = new IfUnlessCondition(c);
        mainCondition.setConditionName(var.getName());
        init(null, var);
    }
    
    /*
    public AccessoryVariableNode(Children.SortedArray list, VcsConfigVariable var) {
        super(Children.LEAF);
        init(list, var);
        list.add(new AccessoryVariableNode[] { this });
    }
     */
    
    private void init(Children.Array list, VcsConfigVariable var) {
        this.var = var;
        this.list = list;
        setIconBase("org/netbeans/modules/vcs/advanced/variables/AccessoryVariables"); // NOI18N
        if (var != null) {
            setShortDescription(NbBundle.getMessage(AccessoryVariableNode.class, "CTL_AccessoryVarDescription", var.getName()));
        }
    }
    
    public void setName(String name) {
        if (var != null && !name.equals(var.getName())) {
            var.setName(name);
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
            return var.getName();
        } else {
            return super.getName();
        }
    }

    public VcsConfigVariable getVariable() {
        return var;
    }
    
    public final void addVariablePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        variableChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }
    
    public final void removeVariablePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        variableChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }
    
    protected final void fireVariablePropertyChange(String propertyName, Object oldValue, Object newValue) {
        variableChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    public Collection getAllAccessoryVariablesNames() {
        AccessoryVariableNode root = this;
        if (Children.LEAF.equals(this.getChildren())) {
            root = (AccessoryVariableNode) getParentNode();
        }
        ArrayList names = new ArrayList();
        for (Enumeration nodesEnum = root.getChildren().nodes(); nodesEnum.hasMoreElements(); ) {
            AccessoryVariableNode varNode = (AccessoryVariableNode) nodesEnum.nextElement();
            names.add(varNode.getVariable().getName());
        }
        return names;
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
    
    public void destroy() throws java.io.IOException {
        if (VcsCustomizer.VAR_CONFIG_INPUT_DESCRIPTOR.equals(var.getName())) {
            ((AccessoryVariableNode) getParentNode()).fireVariablePropertyChange(
                UserVariablesPanel.PROP_CONFIG_INPUT_DESCRIPTOR,
                Boolean.TRUE, Boolean.FALSE);
        }
        super.destroy();
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
        if (Children.LEAF.equals(this.getChildren())) {
            actions.add(SystemAction.get(CutAction.class));
            actions.add(SystemAction.get(CopyAction.class));
        } else {
            actions.add(SystemAction.get(PasteAction.class));
        }
        actions.add(null);
        actions.add(SystemAction.get(NewAction.class));
        if (Children.LEAF.equals(this.getChildren())) {
            actions.add(SystemAction.get(DeleteAction.class));
        }
        //actions.add(null);
        //actions.add(SystemAction.get(PropertiesAction.class));
        SystemAction[] array = new SystemAction [actions.size()];
        actions.toArray(array);
        return array;
    }

    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        if (var != null) //set.put(new PropertySupport.Name(this));
            createProperties(var, set);
        return sheet;
    }
    
    private void createProperties(final VcsConfigVariable var, final Sheet.Set set) {
        set.put(new PropertySupport.ReadWrite(Node.PROP_NAME, String.class, g("CTL_Name"), g("HINT_Name")) {
            public Object getValue() {
                return var.getName();
            }
            
            public void setValue(Object value) {
                var.setName((String) value);
                AccessoryVariableNode.this.fireNameChange(null, var.getName());
                //cmd.fireChanged();
            }
        });
        if (cs != null) {
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
                    if (VcsCustomizer.VAR_CONFIG_INPUT_DESCRIPTOR.equals(var.getName())) {
                        ((AccessoryVariableNode) AccessoryVariableNode.this.getParentNode()).fireVariablePropertyChange(
                            UserVariablesPanel.PROP_CONFIG_INPUT_DESCRIPTOR,
                            Boolean.FALSE, UserVariablesPanel.isConfigInputDescriptorVar(var) ? Boolean.TRUE : Boolean.FALSE);
                    }
                    //cmd.fireChanged();
                }
                
                public PropertyEditor getPropertyEditor() {
                    return new ConditionedString.ConditionedStringPropertyEditor();
                }
            });
        } else {
            set.put(new PropertySupport.ReadWrite("value", String.class, g("CTL_Value"), g("HINT_Value")) {
                public Object getValue() {
                    return var.getValue();
                }
                
                public void setValue(Object value) {
                    var.setValue((String) value);
                    if (VcsCustomizer.VAR_CONFIG_INPUT_DESCRIPTOR.equals(var.getName())) {
                        ((AccessoryVariableNode) AccessoryVariableNode.this.getParentNode()).fireVariablePropertyChange(
                            UserVariablesPanel.PROP_CONFIG_INPUT_DESCRIPTOR,
                            Boolean.FALSE, UserVariablesPanel.isConfigInputDescriptorVar(var) ? Boolean.TRUE : Boolean.FALSE);
                    }
                    //cmd.fireChanged();
                }
            });
        }
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
            if (Children.LEAF.equals(AccessoryVariableNode.this.getChildren())) {
                ch = AccessoryVariableNode.this.getParentNode().getChildren();
            } else {
                ch = AccessoryVariableNode.this.getChildren();
            }
            ch.add(new Node[] { newVar });
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
        return NbBundle.getMessage(AccessoryVariableNode.class, name);
    }

}
