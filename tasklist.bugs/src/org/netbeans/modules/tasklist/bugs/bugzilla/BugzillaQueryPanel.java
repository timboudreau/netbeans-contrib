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

package org.netbeans.modules.tasklist.bugs.bugzilla;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.netbeans.modules.tasklist.bugs.BugQuery;
import org.netbeans.modules.tasklist.bugs.QueryPanelIF;

import org.openide.DialogDescriptor;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * This panel has the specific query parameters for a bugzilla query.
 *
 * @todo add all the swing components to be just like the query.cgi page
 * 
 * @author serff
 */
public class BugzillaQueryPanel extends JPanel implements QueryPanelIF {
    /** A panel at the top to hold the combobox and label */
    private JPanel mTopPanel;
    /** a panel that holds the query part */
    private JPanel mQueryPanel;
    /** A label for the ComboBox */
    private JLabel mEngineLabel;
    /** a combox box for the bug engine choices */
    private JComboBox mBugEngines;
    /** a text field for the base url */
    private JTextField mBaseUrlField;
    /** a label for the base url field */
    private JLabel mBaseUrlLabel;
    /** a label for an example of a base url */
    private JLabel mBaseUrlExampleLabel;
    /** a panel to hold the label */
    private JPanel mBaseUrlExamplePanel;
    /** a text field for the query sting for now */
    private JTextField mQueryField;
    /** A label for the query field */
    private JLabel mQueryLabel;
    /** a label for an example of a query string */
    private JLabel mQueryExampleLabel;
    /** a panel to hold the label */
    private JPanel mQueryExamplePanel;
    /** A button panel */
    private JPanel mButtonPanel;
    /** A done button */
    private JButton mDoneButton;
    private JPanel mBaseUrlPanel;
    private JPanel mQueryStringPanel;
    
    /** an instance of the query */
    private BugQuery mQuery;
    /** a flag to tell if we are editing this query or not */
    private boolean mEditing;
    
    /** Creates a new instance of BugzillaQueryPanel */
    public BugzillaQueryPanel(BugQuery query, boolean editing) {
        mEditing = editing;
        mQuery = query;
        initComponents();
    }
    
    private void initComponents() {
        mTopPanel = new JPanel();
        mQueryPanel = new JPanel();
        mBaseUrlPanel = new JPanel();
        mBaseUrlField = new JTextField();
        mBaseUrlLabel = new JLabel();
        mBaseUrlExampleLabel = new JLabel();
        mBaseUrlExamplePanel = new JPanel();
        mQueryStringPanel = new JPanel();
        mQueryField = new JTextField();
        mQueryLabel = new JLabel();
        mQueryExampleLabel = new JLabel();
        mQueryExamplePanel = new JPanel();
        setLayout(new BorderLayout());
        
        mBaseUrlLabel.setText(NbBundle.getMessage(BugzillaQueryPanel.class, "BaseUrl_Label")); // NOI18N
        mBaseUrlField.setPreferredSize(new Dimension(300, 20));
        mBaseUrlExampleLabel.setText(NbBundle.getMessage(BugzillaQueryPanel.class, "BaseUrlExample_Label")); // NOI18N
        mBaseUrlExampleLabel.setForeground(new Color(153, 153, 153));
        
        mTopPanel.setLayout(new BorderLayout());
        mBaseUrlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        mBaseUrlPanel.add(mBaseUrlLabel);
        mBaseUrlPanel.add(mBaseUrlField);
        mTopPanel.add(mBaseUrlPanel, BorderLayout.CENTER);
        mBaseUrlExamplePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        mBaseUrlExamplePanel.add(mBaseUrlExampleLabel);
        mTopPanel.add(mBaseUrlExamplePanel, BorderLayout.SOUTH);
        
        
        mQueryLabel.setText(NbBundle.getMessage(BugzillaQueryPanel.class, "Query_Label")); // NOI18N
        mQueryField.setPreferredSize(new Dimension(400, 20));
        mQueryExampleLabel.setText(NbBundle.getMessage(BugzillaQueryPanel.class, "QueryExample_Label")); // NOI18N
        mQueryExampleLabel.setForeground(new Color(153, 153, 153));
        
        mQueryPanel.setLayout(new BorderLayout());
        mQueryStringPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        mQueryStringPanel.add(mQueryLabel);
        mQueryStringPanel.add(mQueryField);
        mQueryPanel.add(mQueryStringPanel, BorderLayout.CENTER);
        mQueryExamplePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        mQueryExamplePanel.add(mQueryExampleLabel);
        mQueryPanel.add(mQueryExamplePanel, BorderLayout.SOUTH);
        
        add(mTopPanel, BorderLayout.NORTH);
        add(mQueryPanel, BorderLayout.SOUTH);
    }
    
    public BugQuery getQueryOptions(BugQuery inQuery) {
        inQuery.setBaseUrl(mBaseUrlField.getText());
        inQuery.setQueryString(mQueryField.getText());
        return inQuery;
    }
    
}
