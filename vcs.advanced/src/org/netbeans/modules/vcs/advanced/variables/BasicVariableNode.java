/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.variables;

import java.util.Comparator;
import java.util.ArrayList;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.actions.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.ActionPerformer;
import org.openide.util.datatransfer.NewType;

import org.netbeans.modules.vcscore.VcsConfigVariable;

/**
 *
 * @author  Martin Entlicher
 */
public class BasicVariableNode extends AbstractNode {

    private VcsConfigVariable var = null;
    private Children.SortedArray list = null;

    /** Creates new BasicVariableNode */
    public BasicVariableNode(Children.SortedArray list) {
        super(list);
        init(list, null);
        list.setComparator(getComparator());
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
        super(Children.LEAF);
        setName(var.getLabel());
        init(null, var);
        //list.add(new BasicVariableNode[] { this });
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
        this.list = list;
        getCookieSet().add(new VariablesIndex());
    }
    
    public VcsConfigVariable getVariable() {
        return var;
    }
    
    public boolean canDestroy() {
        return true;
    }
        
    protected SystemAction [] createActions() {
        ArrayList actions = new ArrayList();
        actions.add(SystemAction.get(MoveUpAction.class));
        actions.add(SystemAction.get(MoveDownAction.class));
        actions.add(null);
        actions.add(SystemAction.get(NewAction.class));
        DeleteAction delete = (DeleteAction) SystemAction.get(DeleteAction.class);
        //delete.setEnabled(true);
        delete.setActionPerformer(new ActionPerformer() {
            public void performAction(SystemAction action) {
                delete();
            }
        });
        if (Children.LEAF.equals(getChildren())) {  // Delete not present on the root node.
            actions.add(delete);
        }
        actions.add(null);
        actions.add(SystemAction.get(PropertiesAction.class));
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
        set.put(new PropertySupport.ReadWrite("label", String.class, g("CTL_Label"), g("HINT_Label")) {
                        public Object getValue() {
                            return var.getLabel();
                        }
                        
                        public void setValue(Object value) {
                            var.setLabel((String) value);
                            BasicVariableNode.this.setName((String) value);
                            //cmd.fireChanged();
                        }
                });
        set.put(new PropertySupport.ReadWrite("name", String.class, g("CTL_Name"), g("HINT_Name")) {
            public Object getValue() {
                return var.getName();
            }
            
            public void setValue(Object value) {
                var.setName((String) value);
                //cmd.fireChanged();
            }
        });
        set.put(new PropertySupport.ReadOnly("order", String.class, g("CTL_Order"), g("HINT_Order")) {
                        public Object getValue() {
                            //System.out.println("getName: cmd = "+cmd);
                            //int order = ((Children.SortedArray) getChildren()).indexOf(this);
                            return Integer.toString(var.getOrder());
                        }
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
                return new Boolean(var.isLocalFile());
            }
            
            public void setValue(Object value) {
                var.setLocalFile(((Boolean) value).booleanValue());
                //cmd.fireChanged();
            }
        });
        set.put(new PropertySupport.ReadWrite("localDir", Boolean.TYPE, g("CTL_LocalDir"), g("HINT_LocalFile")) {
            public Object getValue() {
                //System.out.println("getName: cmd = "+cmd);
                return new Boolean(var.isLocalDir());
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
            if (TopManager.getDefault().notify(input) != NotifyDescriptor.OK_OPTION)
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
     */
    public void delete() {
        try {
            destroy();
        } catch (java.io.IOException exc) {
            // silently ignored
        }
    }
    
    private String g(String name) {
        return org.openide.util.NbBundle.getBundle(BasicVariableNode.class).getString(name);
    }

}
