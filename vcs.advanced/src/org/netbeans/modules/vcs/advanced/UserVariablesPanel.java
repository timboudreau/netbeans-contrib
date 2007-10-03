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
                                                          ExplorerManager.Provider {
    
    /** This property is fired when the variable CONFIG_INPUT_DESCRIPTOR is
     * defined/undefined with a meaningfull value */
    public static final String PROP_CONFIG_INPUT_DESCRIPTOR = "configInputDescriptor"; // NOI18N
    
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
    }

    //-------------------------------------------
    public void initComponents(){
        GridBagLayout gb=new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gb);
        //setBorder(new TitledBorder(g("CTL_Variables")));
        setBorder (new EmptyBorder (12, 12, 0, 11));
        
        javax.swing.JTextArea descriptionArea = new javax.swing.JTextArea();
        descriptionArea.getAccessibleContext().setAccessibleName(g("ACS_LBL_DescAreaVariablesView"));// NOI18N
        descriptionArea.getAccessibleContext().setAccessibleDescription(g("ACSD_LBL_DescAreaVariablesView"));// NOI18N
        descriptionArea.setText(g("LBL_ReadOnlyVariablesView"));//, profileDisplayName));
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(new javax.swing.JLabel().getBackground());
        descriptionArea.setFocusable(false);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.insets = new java.awt.Insets(0, 0, 11, 0);
        add(descriptionArea, c);
        
        c = new GridBagConstraints();
        PropertySheetView propertySheetView = new PropertySheetView();
        try {
            propertySheetView.setSortingMode(org.openide.explorer.propertysheet.PropertySheet.UNSORTED);
        } catch (java.beans.PropertyVetoException exc) {
            // The change was vetoed
        }
        org.openide.explorer.view.BeanTreeView beanTreeView = new org.openide.explorer.view.BeanTreeView();
        beanTreeView.getAccessibleContext().setAccessibleName(g("ACS_UserVariablesTreeViewA11yName"));  // NOI18N
        beanTreeView.getAccessibleContext().setAccessibleDescription(g("ACS_UserVariablesTreeViewA11yDesc"));  // NOI18N
        beanTreeView.setDefaultActionAllowed(false);
        ExplorerPanel explPanel = new ExplorerPanel();
        explPanel.getAccessibleContext().setAccessibleDescription(g("ACS_UserVariablesTreePanelDesc"));  // NOI18N
        explPanel.add(beanTreeView);
        manager = explPanel.getExplorerManager();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, explPanel, propertySheetView);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridy = 1;
        add(splitPane, c);
        getAccessibleContext().setAccessibleName(g("ACS_UserVariablesPanelA11yName"));  // NOI18N
        getAccessibleContext().setAccessibleDescription(g("ACS_UserVariablesPanelA11yDesc"));  // NOI18N
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
        accessoryRoot.setReadOnly(true);
        varCh.add(new Node[] { basicRoot, accessoryRoot });
        Vector variables = (Vector) editor.getValue();
        for(Enumeration enum = variables.elements(); enum.hasMoreElements(); ) {
            VcsConfigVariable var = (VcsConfigVariable) enum.nextElement();
            String name = var.getName();
            if (var.isBasic()) {
                basicChildren.add(new BasicVariableNode[] { new BasicVariableNode(var) });
            } else {
                if (name.indexOf(VcsFileSystem.VAR_ENVIRONMENT_PREFIX) == 0 ||
                    name.indexOf(VcsFileSystem.VAR_ENVIRONMENT_REMOVE_PREFIX) == 0 ||
                    "MODULE".equals(name) ||
                    "PASSWORD".equals(name)) {
                    filteredVariables.add(var);
                    continue;
                }
                AccessoryVariableNode accessoryNode = new AccessoryVariableNode(var);
                accessoryNode.setReadOnly(true);
                accessoryChildren.add(new AccessoryVariableNode[] { accessoryNode });
            }
        }
        return varRoot;
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
        return createVariables();
        //return editor.getValue ();
    }

    //-------------------------------------------
    private String g(String s) {
        return NbBundle.getMessage(UserVariablesPanel.class, s);
    }
    
    private String g(String s, Object obj) {
        return NbBundle.getMessage(UserVariablesPanel.class, s, obj);
    }
    
}
