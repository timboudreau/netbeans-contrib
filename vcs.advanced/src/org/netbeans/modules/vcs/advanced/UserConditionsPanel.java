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

package org.netbeans.modules.vcs.advanced;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.openide.explorer.*;
import org.openide.explorer.propertysheet.*;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.nodes.*;
import org.openide.util.*;

import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcs.advanced.VcsCustomizer;
import org.netbeans.modules.vcs.advanced.variables.*;

/** User conditions panel.
 * 
 * @author Martin Entlicher
 */
public class UserConditionsPanel extends JPanel implements EnhancedCustomPropertyEditor, ExplorerManager.Provider {
    
    private UserConditionsEditor editor;
    private ExplorerManager manager = null;
    //private Children.Array varCh = null;
    //private BasicVariableNode basicRoot = null;
    //private AccessoryVariableNode accessoryRoot = null;
    //private Children basicChildren = null;
    //private Children.SortedArray accessoryChildren = null;
    //private Set filteredVariables = new HashSet();
    private Children conditionChildren;

    static final long serialVersionUID =-3274950173085259492L;
    
    public UserConditionsPanel(UserConditionsEditor editor) {
        this.editor = editor;
        initComponents();
        getExplorerManager().setRootContext(createNodes());
        ExplorerActions actions = new ExplorerActions();
        actions.attach(getExplorerManager());
    }

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
        beanTreeView.getAccessibleContext().setAccessibleName(g("ACS_UserConditionsTreeViewA11yName"));  // NOI18N
        beanTreeView.getAccessibleContext().setAccessibleDescription(g("ACS_UserConditionsTreeViewA11yDesc"));  // NOI18N
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
        getAccessibleContext().setAccessibleName(g("ACS_UserConditionsPanelA11yName"));  // NOI18N
        getAccessibleContext().setAccessibleDescription(g("ACS_UserConditionsPanelA11yDesc"));  // NOI18N
    }
    
    private static final Comparator getConditionsComparator() {
        return new Comparator() {
            public int compare(Object o1, Object o2) {
                if (o1.equals(o2)) return 0;
                if ((o1 instanceof ConditionNode) && (o2 instanceof ConditionNode)) {
                    return ((ConditionNode) o1).getCondition().getName().compareTo(((ConditionNode) o2).getCondition().getName());
                }
                else return 0;
            }
            public boolean equals(Object obj) {
                return false;
            }
        };
    }
    
    private AbstractNode createNodes() {
        Children.SortedArray conditionChildren = new Children.SortedArray();
        conditionChildren.setComparator(getConditionsComparator());
        this.conditionChildren = conditionChildren;
        //varCh = varChSorted;
        AbstractNode conditionRoot = new ConditionNode.Main(conditionChildren);
        Condition[] conditions = (Condition[]) editor.getValue();
        //Collection variables = ((ConditionedVariables) editor.getValue()).getUnconditionedVariables();
        for(int i = 0; i < conditions.length; i++) {
            conditionChildren.add(new ConditionNode[] { new ConditionNode(conditions[i]) });
        }
        return conditionRoot;
    }
    
    public org.openide.explorer.ExplorerManager getExplorerManager() {
        synchronized(this) {
            if (manager == null) {
                manager = new ExplorerManager();
            }
        }
        return manager;
    }
    
    public Object getPropertyValue() {
        Node[] conditionNodes = conditionChildren.getNodes();
        Condition[] conditions = new Condition[conditionNodes.length];
        for (int i = 0; i < conditionNodes.length; i++) {
            conditions[i] = ((ConditionNode) conditionNodes[i]).getCondition();
        }
        return conditions;
        //return editor.getValue ();
    }


    //-------------------------------------------
    private String g(String s) {
        return NbBundle.getMessage(UserConditionsPanel.class, s);
    }
    
}
