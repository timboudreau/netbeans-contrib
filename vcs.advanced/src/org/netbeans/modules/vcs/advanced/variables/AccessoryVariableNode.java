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
public class AccessoryVariableNode extends AbstractNode {

    private VcsConfigVariable var = null;
    private Children.Array list = null;

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
    }

    public AccessoryVariableNode(VcsConfigVariable var) {
        super(Children.LEAF);
        setName(var.getName());
        init(null, var);
        //list.add(new AccessoryVariableNode[] { this });
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
    }

    public VcsConfigVariable getVariable() {
        return var;
    }

    public boolean canDestroy() {
        return true;
    }
        
    protected SystemAction [] createActions() {
        ArrayList actions = new ArrayList();
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
        set.put(new PropertySupport.ReadWrite("name", String.class, g("CTL_Name"), g("HINT_Name")) {
            public Object getValue() {
                return var.getName();
            }
            
            public void setValue(Object value) {
                var.setName((String) value);
                //cmd.fireChanged();
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
     */
    public void delete() {
        try {
            destroy();
        } catch (java.io.IOException exc) {
            // silently ignored
        }
    }
    
    private String g(String name) {
        return org.openide.util.NbBundle.getBundle(AccessoryVariableNode.class).getString(name);
    }

}
