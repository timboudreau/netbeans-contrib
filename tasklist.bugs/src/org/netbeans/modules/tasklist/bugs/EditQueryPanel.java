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

package org.netbeans.modules.tasklist.bugs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dialog;
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
import org.netbeans.modules.tasklist.bugs.bugzilla.BugzillaQueryPanel;

import org.openide.DialogDescriptor;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerPanel;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * This panel is the main panel show for a new query.  
 * The panel contains a dropdown box that contains the different bug engines
 * available to the user to search aginst.  It also contains another panel with 
 * the custom search parameters for each bug engine.  This panel will be pulled in
 * using reflection based on the name of the bugEngine picked.  For this to work
 * every new bug engine must implement a class with this naming scheme:<br>
 * <code><Engine-Name>QueryPanel</code> ex. <code>BugzillaQueryPanel</code><br>
 *
 * @todo get the reflection working
 * @todo have a default button they can click and set the query as a default query.  
 *       this might end up being an "Add To Queries" button that will add the 
 *       query to a list of saved queries
 * @todo Finish all the getting of data from the other panel.
 * @todo Add an action listener for the dropdown box to switch out the QueryPanel
 *
 * @author  serff
 */
public class EditQueryPanel extends JPanel {
    /** A panel at the top to hold the combobox and label */
    private JPanel mTopPanel;
    /** a panel that holds the query part */
    private JPanel mQueryPanel;
    /** A label for the ComboBox */
    private JLabel mEngineLabel;
    /** a combox box for the bug engine choices */
    private JComboBox mBugEngines;
    /** A button panel */
    private JPanel mButtonPanel;
    /** A done button */
    private JButton mDefaultButton;
    
    /** an instance of the query */
    private BugQuery mQuery;
    /** a flag to tell if we are editing this query or not */
    private boolean mEditing;
    
    /** Creates a new instance of EditQueryPanel */
    public EditQueryPanel(BugQuery query, boolean editing) {
        mEditing = editing;
        mQuery = query;
        initComponents();
    }
    
    private void initComponents() {
        mTopPanel = new JPanel();
        mQueryPanel = new JPanel();
        mEngineLabel = new JLabel();
        mBugEngines = new JComboBox();
        mButtonPanel = new JPanel();
        mDefaultButton = new JButton();
        
        setLayout(new BorderLayout());

        mEngineLabel.setText(NbBundle.getMessage(EditQueryPanel.class, "BugEngine_Label")); // NOI18N

        //Now i have to get the list of bug engines
        //FIXME Need a way to get the bug engines dynamically
        mBugEngines.addItem("Issuezilla");
        mBugEngines.addItem("Bugzilla");
        mBugEngines.setSelectedItem("Issuezilla");
        
        mTopPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        mTopPanel.add(mEngineLabel);
        mTopPanel.add(mBugEngines);
        
        //now get the query panel
        mQueryPanel = getQueryPanel((String)mBugEngines.getSelectedItem());

        //Do the button panel
        mDefaultButton.setText(NbBundle.getMessage(EditQueryPanel.class, "DefaultButton_Text"));
        mDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultButtonActionPerformed(evt);
            }
        });
        mButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        mButtonPanel.add(mDefaultButton);
        
        add(mTopPanel, BorderLayout.NORTH);
        add(mQueryPanel, BorderLayout.CENTER);
//        add(mButtonPanel, BorderLayout.SOUTH);
    }
    
    /** 
     * This method will use reflection to get the correct bug querypanel
     * @param engineName the name of the bug engine they are going to use
     */
    public JPanel getQueryPanel(String engineName) {
        //FIXME
        return new BugzillaQueryPanel(mQuery, mEditing);
    }
    
    private void defaultButtonActionPerformed(java.awt.event.ActionEvent evt) {
        //get everything and populate the query object
        mQuery.setBugEngine(((String)mBugEngines.getSelectedItem()));
        mQuery = ((QueryPanelIF)mQueryPanel).getQueryOptions(mQuery);
        
        //set this somewhere...
        
    }
    
    public BugQuery getQuery() {
        mQuery.setBugEngine(((String)mBugEngines.getSelectedItem()));
        mQuery = ((QueryPanelIF)mQueryPanel).getQueryOptions(mQuery);
        return mQuery;
    }
    
    public String getBugEngine() {
        return (String)mBugEngines.getSelectedItem();
    }
    
    public String getQueryString() {
        return ((QueryPanelIF)mQueryPanel).getQueryOptions(mQuery).getQueryString();
    }
    
    public String getBaseUrl() {
        return ((QueryPanelIF)mQueryPanel).getQueryOptions(mQuery).getBaseUrl();
    }
}
