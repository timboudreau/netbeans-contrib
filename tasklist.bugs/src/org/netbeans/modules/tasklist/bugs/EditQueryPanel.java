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

import javax.swing.*;

import org.netbeans.modules.tasklist.bugs.BugQuery;
import org.netbeans.modules.tasklist.bugs.bugzilla.BugzillaQueryPanel;
import org.netbeans.modules.tasklist.bugs.bugzilla.SourcePanel;

import org.openide.DialogDescriptor;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * This panel is the main panel show for a new query.  
 * The panel contains a dropdown box that contains the different bug engines
 * available to the user to search aginst.  It also contains another panel with 
 * the custom search parameters for each bug engine.  This panel will be pulled in
 * using reflection based on the name of the bugEngine picked.
 *
 * @todo have a default button they can click and set the query as a default query.
 *       this might end up being an "Add To Queries" button that will add the 
 *       query to a list of saved queries
 * @todo Finish all the getting of data from the other panel.
 *
 * @author  serff
 */
public final class EditQueryPanel extends JPanel {

    private static final long serialVersionUID = 1;

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
        mEngineLabel = new JLabel();
        mBugEngines = new JComboBox();
        mButtonPanel = new JPanel();
        mDefaultButton = new JButton();
        
        setLayout(new BorderLayout());

        mEngineLabel.setText(NbBundle.getMessage(EditQueryPanel.class, "BugEngine_Label")); // NOI18N

        //Now i have to get the list of bug engines
        String[] engines = BugEngines.list();
        for (int i= 0; i<engines.length; i++) {
            mBugEngines.addItem(engines[i]);
        }

        mTopPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));
        mTopPanel.add(mEngineLabel);
        mTopPanel.add(mBugEngines);
        
        //now prepare the query panel
        final JPanel hotSwap = new JPanel();
        mQueryPanel = getQueryPanel((String)mBugEngines.getSelectedItem());
        hotSwap.add(mQueryPanel);
        mBugEngines.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hotSwap.removeAll();
                mQueryPanel = getQueryPanel((String)mBugEngines.getSelectedItem());
                hotSwap.add(mQueryPanel);
                hotSwap.revalidate();
                EditQueryPanel.this.repaint();
            }
        });

        //Do the button panel
        mDefaultButton.setText(NbBundle.getMessage(EditQueryPanel.class, "DefaultButton_Text"));
        mDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultButtonActionPerformed(evt);
            }
        });
        mDefaultButton.setEnabled(false);
        mDefaultButton.setToolTipText("Not yet implemented");
        mButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        // mButtonPanel.add(mDefaultButton);
        
        add(mTopPanel, BorderLayout.NORTH);
        hotSwap.setBorder(BorderFactory.createEmptyBorder(0,6,0,6));
        add(hotSwap, BorderLayout.CENTER);
        add(mButtonPanel, BorderLayout.SOUTH);
    }
    
    /** 
     * This method will use reflection to get the correct bug querypanel
     * @param engineName the name of the bug engine they are going to use
     */
    public JPanel getQueryPanel(String engineName) {
        // XXX force all to return JPanel
        JPanel ret = null;
        BugEngine engine = BugEngines.get(getBugEngine());
        ret = (JPanel) engine.getQueryCustomizer(getQuery(), false);
        assert ret instanceof QueryPanelIF : "Engine " + engine + " returned " + ret;
        return ret;
    }
    
    private void defaultButtonActionPerformed(java.awt.event.ActionEvent evt) {
        //get everything and populate the query object
        mQuery.setBugEngine(((String)mBugEngines.getSelectedItem()));
        mQuery = ((QueryPanelIF)mQueryPanel).getQueryOptions(mQuery);
        
        //set this somewhere...
        
    }
    
    public BugQuery getQuery() {
        mQuery.setBugEngine(((String)mBugEngines.getSelectedItem()));
        if (mQueryPanel != null) {
            mQuery = ((QueryPanelIF)mQueryPanel).getQueryOptions(mQuery);
        }
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
