/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.assistant;

import org.netbeans.modules.assistant.ui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;
import java.lang.reflect.*;
import java.net.*;
import javax.accessibility.*;
import javax.swing.border.EmptyBorder;

import org.openide.*;
import org.openide.util.*;
/**
 *@author Richard Gregor
 *
 *Created on May 27, 2002, 6:14 PM
 */

public class AssistantView extends javax.swing.JPanel implements TreeSelectionListener{
    private ResourceBundle bundle;
    private JButton button;
    private String page = null;
    private String language = null;
    private JEditorPane jEditorPane1;
    private AssistantModel model;
    private JTree tree;
    private DefaultMutableTreeNode rootNode;
    private SearchPanel search;
    
    public AssistantView(AssistantModel model) {
        this.model = model;
        setLayout(new BorderLayout());
        rootNode = new DefaultMutableTreeNode();
        tree = new JTree(loadData());
        tree.addTreeSelectionListener(this);
        tree.setRootVisible(false);  
        tree.putClientProperty("JTree.lineStyle", "None");

        tree.setCellRenderer(new AssistantCellRenderer());        
        tree.setBorder(new EmptyBorder(6,6,6,6));
        language = Locale.getDefault().getLanguage();
        bundle = ResourceBundle.getBundle("org/netbeans/modules/assistant/Bundle");        
        setLayout(new BorderLayout());
        
        JScrollPane pane = new JScrollPane(tree);        
        add(pane,BorderLayout.CENTER);
        search = new SearchPanel();
        add(search,BorderLayout.SOUTH);
        setMinimumSize(new java.awt.Dimension(170,240));
        setPreferredSize(new java.awt.Dimension(170,240));               
       // initListeners();
        
    }
    private DefaultMutableTreeNode loadData(){
        if (rootNode == null)
            return rootNode;        
        Vector sections = model.getSections();        
        for(Enumeration en = sections.elements();en.hasMoreElements();)
            rootNode.add((AssistantSection)en.nextElement());
        return rootNode;
    }         
              
    /**
     * Called whenever the value of the selection changes.
     * @param e the event that characterizes the change.
     *
     */
    public void valueChanged(TreeSelectionEvent e) {
        debug("value changed");
        TreePath selectedTreePath = e.getNewLeadSelectionPath();
        Object obj = selectedTreePath.getLastPathComponent();
        if (obj != null){
            if(obj instanceof DefaultMutableTreeNode){
                debug("obj is DMTN");
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)obj;
                Object object = node.getUserObject();
                if((object != null) && (object instanceof AssistantItem)){
                    debug("item");
                    AssistantItem item = (AssistantItem)object;
                    if(item != null){
                        URL newURL = item.getURL();
                        if((newURL != null)&& (model != null)){
                            debug("item:"+item);
                            model.setCurrentURL(item.getURL());
                        }
                    }
                }
            }
        }
    }
    
    private boolean debug = false;
    private void debug(String msg){
        if(debug)
            System.err.println("View: "+msg);
    }
    
}
