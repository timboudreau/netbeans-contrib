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

package org.netbeans.modules.vcs.advanced;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
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
import org.netbeans.modules.vcs.advanced.variables.*;

/** User variables panel.
 * 
 * @author Martin Entlicher
 */
//-------------------------------------------
public class UserVariablesPanel extends JPanel implements EnhancedCustomPropertyEditor, ExplorerManager.Provider {
    private Debug E=new Debug("UserVariablesPanel", true); // NOI18N
    //private Debug D=E;

    private UserVariablesEditor editor;
    private ExplorerManager manager = null;

    //-------------------------------------------
    static final long serialVersionUID =-4165869264994159492L;
    public UserVariablesPanel(UserVariablesEditor editor){
        this.editor = editor;
        initComponents();
        getExplorerManager().setRootContext(createNodes());
        //setPreferredSize(screenSize);
    }

    //-------------------------------------------
    public void initComponents(){
        GridBagLayout gb=new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gb);
        setBorder(new TitledBorder(g("CTL_Variables")));

        PropertySheetView propertySheetView = new PropertySheetView();
        try {
            propertySheetView.setSortingMode(org.openide.explorer.propertysheet.PropertySheet.UNSORTED);
        } catch (java.beans.PropertyVetoException exc) {
            // The change was vetoed
        }
        org.openide.awt.SplittedPanel split = new org.openide.awt.SplittedPanel();
        split.setSplitType(org.openide.awt.SplittedPanel.HORIZONTAL);
        split.add(new VariableTreeView(), org.openide.awt.SplittedPanel.ADD_LEFT);
        split.add(propertySheetView, org.openide.awt.SplittedPanel.ADD_RIGHT);
        //JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new CommandTreeView(), propertySheetView);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(split, c);
    }
    
    private AbstractNode createNodes() {
        Children.Array varCh = new Children.Array();
        AbstractNode varRoot = new AbstractNode(varCh);
        varRoot.setDisplayName(g("CTL_VariablesNodeName"));
        Children.SortedArray basicChildren = new Children.SortedArray();
        //basicCh.add(new Node[] { node });
        AbstractNode basicRoot = new BasicVariableNode(basicChildren);
        Children.SortedArray accessoryChildren = new Children.SortedArray();
        AbstractNode accessoryRoot = new AccessoryVariableNode(accessoryChildren);
        varCh.add(new Node[] { basicRoot, accessoryRoot });
        Vector variables = (Vector) editor.getValue();
        for(Enumeration enum = variables.elements(); enum.hasMoreElements(); ) {
            VcsConfigVariable var = (VcsConfigVariable) enum.nextElement();
            if (var.isBasic()) {
                basicChildren.add(new BasicVariableNode[] { new BasicVariableNode(var) });
            } else {
                accessoryChildren.add(new AccessoryVariableNode[] { new AccessoryVariableNode(var) });
            }
        }
        return varRoot;
    }

    public org.openide.explorer.ExplorerManager getExplorerManager() {
        if (manager == null) {
            synchronized(this) {
                if (manager == null) {
                    manager = new ExplorerManager();
                }
            }
        }
        return manager;
    }
    
    public Object getPropertyValue() {
        //D.deb("getPropertyValue()");
        return editor.getValue ();
    }


    //-------------------------------------------
    String g(String s) {
        return NbBundle.getBundle
               ("org.netbeans.modules.vcs.advanced.Bundle").getString (s);
    }
    String  g(String s, Object obj) {
        return MessageFormat.format (g(s), new Object[] { obj });
    }
    String g(String s, Object obj1, Object obj2) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2 });
    }
    String g(String s, Object obj1, Object obj2, Object obj3) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2, obj3 });
    }
    
    //-------------------------------------------

}

/*
 * <<Log>>
 *  5    Jaga      1.2.1.1     3/9/00   Martin Entlicher Fix of long panel width.
 *  4    Jaga      1.2.1.0     3/7/00   Martin Entlicher 
 *  3    Gandalf   1.2         1/27/00  Martin Entlicher NOI18N
 *  2    Gandalf   1.1         11/27/99 Patrik Knakal   
 *  1    Gandalf   1.0         11/24/99 Martin Entlicher 
 * $
 */
