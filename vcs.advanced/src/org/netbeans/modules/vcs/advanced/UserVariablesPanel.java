/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.text.*;

import org.openide.explorer.*;
import org.openide.explorer.propertysheet.*;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.nodes.*;
import org.openide.util.*;

import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcs.advanced.VcsCustomizer;
import org.netbeans.modules.vcs.advanced.variables.*;

/** User variables panel.
 * 
 * @author Martin Entlicher
 */
//-------------------------------------------
public class UserVariablesPanel extends JPanel implements EnhancedCustomPropertyEditor,
                                                          ExplorerManager.Provider,
                                                          PropertyChangeListener {
    
    /** This property is fired when the variable CONFIG_INPUT_DESCRIPTOR is
     * defined/undefined with a meaningfull value */
    public static final String PROP_CONFIG_INPUT_DESCRIPTOR = "configInputDescriptor"; // NOI18N
    
    private Debug E=new Debug("UserVariablesPanel", true); // NOI18N
    //private Debug D=E;

    private UserVariablesEditor editor;
    private ExplorerManager manager = null;
    private Children.Array varCh = null;
    private BasicVariableNode basicRoot = null;
    private AccessoryVariableNode accessoryRoot = null;
    private Children basicChildren = null;
    private Children.SortedArray accessoryChildren = null;
    private Set filteredVariables = new HashSet();

    //-------------------------------------------
    static final long serialVersionUID =-4165869264994159492L;
    public UserVariablesPanel(UserVariablesEditor editor){
        this.editor = editor;
        initComponents();
        getExplorerManager().setRootContext(createNodes());
        ExplorerActions actions = new ExplorerActions();
        actions.attach(getExplorerManager());
        HelpCtx.setHelpIDString (this, "VCS_VariableEditor"); // NOI18N
    }

    //-------------------------------------------
    public void initComponents(){
        GridBagLayout gb=new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gb);
        //setBorder(new TitledBorder(g("CTL_Variables")));
        setBorder (new EmptyBorder (12, 12, 0, 11));
        
        PropertySheetView propertySheetView = new PropertySheetView();
        try {
            propertySheetView.setSortingMode(org.openide.explorer.propertysheet.PropertySheet.UNSORTED);
        } catch (java.beans.PropertyVetoException exc) {
            // The change was vetoed
        }
        org.openide.awt.SplittedPanel split = new org.openide.awt.SplittedPanel();
        split.setSplitType(org.openide.awt.SplittedPanel.HORIZONTAL);
        //split.add(new VariableTreeView(), org.openide.awt.SplittedPanel.ADD_LEFT);
        org.openide.explorer.view.BeanTreeView beanTreeView = new org.openide.explorer.view.BeanTreeView();
        beanTreeView.getAccessibleContext().setAccessibleName(g("ACS_UserCommandsTreeViewA11yName"));  // NOI18N
        beanTreeView.getAccessibleContext().setAccessibleDescription(g("ACS_UserCommandsTreeViewA11yDesc"));  // NOI18N
        ExplorerPanel explPanel = new ExplorerPanel();
        explPanel.add(beanTreeView);
        manager = explPanel.getExplorerManager();
        split.add(explPanel, org.openide.awt.SplittedPanel.ADD_LEFT);
        split.add(propertySheetView, org.openide.awt.SplittedPanel.ADD_RIGHT);
        //JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new CommandTreeView(), propertySheetView);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(split, c);
        getAccessibleContext().setAccessibleName(g("ACS_UserVariablesPanelA11yName"));  // NOI18N
        getAccessibleContext().setAccessibleDescription(g("ACS_UserVariablesPanelA11yDesc"));  // NOI18N
    }
    
    public static final boolean isConfigInputDescriptorVar(VcsConfigVariable var) {
        if (VcsCustomizer.VAR_CONFIG_INPUT_DESCRIPTOR.equals(var.getName())) {
            String value = var.getValue();
            if (value != null && value.length() > 0) {
                try {
                    VariableInputDescriptor.parseItems(value);
                } catch (VariableInputFormatException vifex) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }
    
    private static final Comparator getRootVarsComparator() {
        return new Comparator() {
            public int compare(Object o1, Object o2) {
                if (o1.equals(o2)) return 0;
                if ((o1 instanceof BasicVariableNode) && (o2 instanceof AccessoryVariableNode)) return -1;
                else return +1;
            }
            public boolean equals(Object obj) {
                return false;
            }
        };
    }
    
    private AbstractNode createNodes() {
        Children.SortedArray varChSorted = new Children.SortedArray();
        varChSorted.setComparator(getRootVarsComparator());
        varCh = varChSorted;
        AbstractNode varRoot = new AbstractNode(varCh);
        varRoot.setDisplayName(g("CTL_VariablesNodeName"));
        varRoot.setShortDescription(g("CTL_VariablesNodeDescription"));
        varRoot.setIconBase("org/netbeans/modules/vcs/advanced/variables/AccessoryVariables"); // NOI18N
        basicChildren = new Children.SortedArray();
        basicRoot = new BasicVariableNode(basicChildren);
        accessoryChildren = new Children.SortedArray();
        accessoryRoot = new AccessoryVariableNode(accessoryChildren);
        //basicRoot.addPropertyChangeListener(WeakListener.propertyChange(this, basicRoot));
        //accessoryRoot.addPropertyChangeListener(WeakListener.propertyChange(this, accessoryRoot));
        accessoryRoot.addVariablePropertyChangeListener(this);
        varCh.add(new Node[] { basicRoot, accessoryRoot });
        Vector variables = (Vector) editor.getValue();
        boolean disableBasic = false;
        for(Enumeration enum = variables.elements(); enum.hasMoreElements(); ) {
            VcsConfigVariable var = (VcsConfigVariable) enum.nextElement();
            String name = var.getName();
            if (isConfigInputDescriptorVar(var)) {
                disableBasic = true;
            }
            if (var.isBasic()) {
                basicChildren.add(new BasicVariableNode[] { new BasicVariableNode(var) });
            } else {
                if (name.indexOf(VcsFileSystem.VAR_ENVIRONMENT_PREFIX) == 0 ||
                    name.indexOf(VcsFileSystem.VAR_ENVIRONMENT_REMOVE_PREFIX) == 0 ||
                    "MODULE".equals(name)) {
                    filteredVariables.add(var);
                    continue;
                }
                accessoryChildren.add(new AccessoryVariableNode[] { new AccessoryVariableNode(var) });
            }
        }
        if (disableBasic) disableBasicVariables();
        return varRoot;
    }
    
    public void disableBasicVariables() {
        if (!basicRoot.isEnabled()) return ;
        Node[] nodes = basicChildren.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            BasicVariableNode varNode = (BasicVariableNode) nodes[i];
            VcsConfigVariable var = varNode.getVariable();
            var.setOrder(i);
            accessoryChildren.add(new AccessoryVariableNode[] { new AccessoryVariableNode(var) });
        }
        varCh.remove(new Node[] { basicRoot });
        basicChildren = Children.LEAF;
        basicRoot = new BasicVariableNode(basicChildren);
        basicRoot.setEnabled(false);
        varCh.add(new Node[] { basicRoot });
    }
    
    public void enableBasicVariables() {
        if (basicRoot.isEnabled()) return ;
        varCh.remove(new Node[] { basicRoot });
        basicChildren = new Children.SortedArray();
        basicRoot = new BasicVariableNode(basicChildren);
        varCh.add(new Node[] { basicRoot });
        Node[] nodes = accessoryChildren.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            AccessoryVariableNode varNode = (AccessoryVariableNode) nodes[i];
            VcsConfigVariable var = varNode.getVariable();
            if (var.isBasic()) {
                basicChildren.add(new BasicVariableNode[] { new BasicVariableNode(var) });
                accessoryChildren.remove(new Node[] { varNode });
            }
        }
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (PROP_CONFIG_INPUT_DESCRIPTOR.equals(propertyChangeEvent.getPropertyName())) {
            Object newValue = propertyChangeEvent.getNewValue();
            if (Boolean.TRUE.equals(newValue)) {
                disableBasicVariables();
            } else if (Boolean.FALSE.equals(newValue)) {
                enableBasicVariables();
            }
        }
    }
    
    public org.openide.explorer.ExplorerManager getExplorerManager() {
        synchronized(this) {
            if (manager == null) {
                manager = new ExplorerManager();
            }
        }
        return manager;
    }
    
    private Vector createVariables() {
        Vector vars = new Vector();
        Node[] nodes = basicChildren.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            BasicVariableNode varNode = (BasicVariableNode) nodes[i];
            VcsConfigVariable var = varNode.getVariable();
            var.setOrder(i);
            vars.add(var);
        }
        nodes = accessoryChildren.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            AccessoryVariableNode varNode = (AccessoryVariableNode) nodes[i];
            VcsConfigVariable var = varNode.getVariable();
            vars.add(var);
        }
        vars.addAll(filteredVariables);
        return vars;
    }
    
    public Object getPropertyValue() {
        //D.deb("getPropertyValue()");
        return createVariables();
        //return editor.getValue ();
    }


    //-------------------------------------------
    private String g(String s) {
        return NbBundle.getMessage(UserVariablesPanel.class, s);
    }
    
}
