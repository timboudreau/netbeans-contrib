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
import javax.swing.text.DefaultEditorKit;

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
public class UserConditionedVariablesPanel extends JPanel implements EnhancedCustomPropertyEditor,
                                                                     ExplorerManager.Provider,
                                                                     PropertyChangeListener,
                                                                     Lookup.Provider {
    
    /** This property is fired when the variable CONFIG_INPUT_DESCRIPTOR is
     * defined/undefined with a meaningfull value */
    public static final String PROP_CONFIG_INPUT_DESCRIPTOR = "configInputDescriptor"; // NOI18N
    
    private UserConditionedVariablesEditor editor;
    private ExplorerManager manager;
    private Lookup lookup;
    private Children.Array varCh = null;
    private BasicVariableNode basicRoot = null;
    private AccessoryVariableNode accessoryRoot = null;
    private Children basicChildren = null;
    private Children.SortedArray accessoryChildren = null;
    private Set filteredVariables = new HashSet();

    //-------------------------------------------
    static final long serialVersionUID =-4165869264994159492L;
    public UserConditionedVariablesPanel(UserConditionedVariablesEditor editor){
        this.editor = editor;
        initComponents();
        getExplorerManager().setRootContext(createNodes());
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
        beanTreeView.getAccessibleContext().setAccessibleName(g("ACS_UserVariablesTreeViewA11yName"));  // NOI18N
        beanTreeView.getAccessibleContext().setAccessibleDescription(g("ACS_UserVariablesTreeViewA11yDesc"));  // NOI18N
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
        
        ActionMap map = getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true));

        InputMap keys = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        keys.put(KeyStroke.getKeyStroke("control c"), DefaultEditorKit.copyAction);
        keys.put(KeyStroke.getKeyStroke("control x"), DefaultEditorKit.cutAction);
        keys.put(KeyStroke.getKeyStroke("control v"), DefaultEditorKit.pasteAction);
        keys.put(KeyStroke.getKeyStroke("DELETE"), "delete");

        // initialize the lookup variable
        lookup = ExplorerUtils.createLookup (manager, map);
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
        // TODO handle the conditioned vars as well
        
        ConditionedVariables cvars = (ConditionedVariables) editor.getValue();
        Collection unconditionedVars = cvars.getUnconditionedVariables();
        //Collection variables = ((ConditionedVariables) editor.getValue()).getUnconditionedVariables();
        boolean disableBasic = false;
        for(Iterator it = unconditionedVars.iterator(); it.hasNext(); ) {
            VcsConfigVariable var = (VcsConfigVariable) it.next();
            String name = var.getName();
            if (isConfigInputDescriptorVar(var)) {
                disableBasic = true;
            }
            if (var.isBasic()) {
                basicChildren.add(new BasicVariableNode[] { new BasicVariableNode(var, true) });
            } else {
                /*
                if (name.indexOf(VcsFileSystem.VAR_ENVIRONMENT_PREFIX) == 0 ||
                    name.indexOf(VcsFileSystem.VAR_ENVIRONMENT_REMOVE_PREFIX) == 0 ||
                    "MODULE".equals(name)) {
                    filteredVariables.add(var);
                    continue;
                }
                 */
                accessoryChildren.add(new AccessoryVariableNode[] { new AccessoryVariableNode(var, true) });
            }
        }
        Map conditionsByVars = cvars.getConditionsByVariables();
        Map varsByConditions = cvars.getVariablesByConditions();
        for (Iterator it = conditionsByVars.keySet().iterator(); it.hasNext(); ) {
            String varName = (String) it.next();
            Condition[] conditions = (Condition[]) conditionsByVars.get(varName);
            //VcsConfigVariable[] vars = new VcsConfigVariable[conditions.length]);
            VcsConfigVariable var0 = (VcsConfigVariable) varsByConditions.get(conditions[0]);
            if (isConfigInputDescriptorVar(var0)) {
                disableBasic = true;
            }
            if (var0.isBasic()) {
                basicChildren.add(new BasicVariableNode[] { new BasicVariableNode(varName, conditions, varsByConditions) });
            } else {
                accessoryChildren.add(new AccessoryVariableNode[] { new AccessoryVariableNode(varName, conditions, varsByConditions) });
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
    
    // It is good idea to switch all listeners on and off when the
    // component is shown or hidden.
    public void addNotify() {
        super.addNotify();
        ExplorerUtils.activateActions(manager, true);
    }
    
    public void removeNotify() {
        ExplorerUtils.activateActions(manager, false);
        super.removeNotify();
    }
    
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    public Lookup getLookup() {
        return lookup;
    }
    
    private ConditionedVariables createVariables() {
        Collection unconditionedVars = new ArrayList();
        Map conditionsByVars = new HashMap();
        Map varsByConditions = new HashMap();
        Node[] nodes = basicChildren.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            BasicVariableNode varNode = (BasicVariableNode) nodes[i];
            VcsConfigVariable var = varNode.getVariable();
            Map cvars = varNode.getVarsByConditions();
            if (cvars != null) {
                Condition[] conditions = new Condition[cvars.size()];
                int j = 0;
                for (Iterator it = cvars.keySet().iterator(); it.hasNext(); j++) {
                    Condition c = (Condition) it.next();
                    var = (VcsConfigVariable) cvars.get(c);
                    var.setOrder(i);
                    conditions[j] = c;
                    varsByConditions.put(c, var);
                }
                conditionsByVars.put(var.getName(), conditions);
            } else {
                var.setOrder(i);
                unconditionedVars.add(var);
            }
        }
        nodes = accessoryChildren.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            AccessoryVariableNode varNode = (AccessoryVariableNode) nodes[i];
            VcsConfigVariable var = varNode.getVariable();
            Map cvars = varNode.getVarsByConditions();
            if (cvars != null) {
                Condition[] conditions = new Condition[cvars.size()];
                int j = 0;
                for (Iterator it = cvars.keySet().iterator(); it.hasNext(); j++) {
                    Condition c = (Condition) it.next();
                    var = (VcsConfigVariable) cvars.get(c);
                    conditions[j] = c;
                    varsByConditions.put(c, var);
                }
                conditionsByVars.put(var.getName(), conditions);
            } else {
                unconditionedVars.add(var);
            }
        }
        unconditionedVars.addAll(filteredVariables);
        return new ConditionedVariables(unconditionedVars, conditionsByVars, varsByConditions);
    }
    
    public Object getPropertyValue() {
        return createVariables();
    }


    //-------------------------------------------
    private String g(String s) {
        return NbBundle.getMessage(UserConditionedVariablesPanel.class, s);
    }
    
}
