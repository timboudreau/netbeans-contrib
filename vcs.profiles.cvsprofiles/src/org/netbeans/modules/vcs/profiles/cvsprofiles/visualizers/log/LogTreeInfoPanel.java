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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.log;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.tree.DefaultMutableTreeNode;

import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.*;
import org.openide.util.*;

/**
 *
 * Richard Gregor
 *
 */
public class LogTreeInfoPanel extends AbstractTreeInfoPanel {
    
    private LogInfoPanel logPanel;
    private LogInformation clearLogInfo;    
    private JTextField txFilter;
    private JLabel lblFilter;
    private JLabel lblTitle;
    private String filterString = null;
    private int totalCount = 0;
    private int selectedCount = 0;
    private javax.swing.JLabel lblCount;    

    /** 
     * Creates new LogTreeInfoPanel
     *
     *@param topDir The root directory
     */
    public LogTreeInfoPanel(File topDir) {
        super(topDir);        
        initPanelComponents();
        getAccessibleContext().setAccessibleName(NbBundle.getBundle(LogTreeInfoPanel.class).getString("ACS_LogTreeInfoPanel")); // NOI18N
        getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(LogTreeInfoPanel.class).getString("ACSD_LogTreeInfoPanel")); // NOI18N
        postInit();
    }
    
    private void initPanelComponents() {
        JPanel panel = getButtonPanel();
        panel.getAccessibleContext().setAccessibleName(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.btnPanel")); // NOI18N
        panel.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(LogTreeInfoPanel.class).getString("ACSD_LogTreeInfoPanel.btnPanel")); // NOI18N
        lblTitle = new javax.swing.JLabel();
        lblTitle.getAccessibleContext().setAccessibleName(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.lblTitle.text")); // NOI18N
        lblTitle.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.lblTitle.text")); // NOI18N
        lblFilter = new javax.swing.JLabel();
        lblFilter.getAccessibleContext().setAccessibleName(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.filterLog")); // NOI18N
        lblFilter.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.filterLog")); // NOI18N
        txFilter = new javax.swing.JTextField();
        txFilter.getAccessibleContext().setAccessibleName(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.filter")); // NOI18N
        txFilter.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.filter")); // NOI18N
        lblCount = new javax.swing.JLabel();
        lblCount.getAccessibleContext().setAccessibleName(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.lblCount")); // NOI18N
        lblCount.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.lblCount")); // NOI18N
        panel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        lblTitle.setText(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.lblTitle.text")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (12, 12, 0, 11);
        panel.add(lblTitle, gridBagConstraints1);
        
        lblFilter.setText(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.filterLog")); // NOI18N
        lblFilter.setDisplayedMnemonic(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.filterLog.mnemonic").charAt(0)); // NOI18N
        lblFilter.setLabelFor(txFilter);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets(5, 24, 11, 0);
        panel.add(lblFilter, gridBagConstraints1);

        txFilter.setText(""); // NOI18N
        txFilter.setMinimumSize(new java.awt.Dimension(100, 20));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(5, 12, 11, 11);
        panel.add(txFilter, gridBagConstraints1);
        
        lblCount.setText(NbBundle.getBundle(LogTreeInfoPanel.class).getString("LogTreeInfoPanel.lblCount")); // NOI18N
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets(5, 12, 2, 11);
        panel.add(lblCount, gridBagConstraints1);
        
        txFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                filterLogMessages();
            }    
        });      
    }    
    
    
  private void initClearInfo() {
      clearLogInfo = new LogInformation();
      clearLogInfo.setFile(new File(""));       // NOI18N
      clearLogInfo.setAccessList("");           // NOI18N
      clearLogInfo.setBranch("");               // NOI18N
      clearLogInfo.setDescription("");          // NOI18N
      clearLogInfo.setHeadRevision("");         // NOI18N
      clearLogInfo.setKeywordSubstitution("");  // NOI18N
      clearLogInfo.setLocks("");                // NOI18N
      clearLogInfo.setRepositoryFilename("");   // NOI18N
      clearLogInfo.setSelectedRevisions("");    // NOI18N
      clearLogInfo.setTotalRevisions("");       // NOI18N
  }
  
  
  protected void setPanel(Object infoData) {      
      LogInformation logData = (LogInformation)infoData;
      logPanel.setData(logData);
  }
  
  protected JComponent initPanel() {      
      initClearInfo();     
      logPanel = new LogInfoPanel(true);      
      setClearPanel();
      return logPanel;
  }
  
  protected void setClearPanel() {      
      logPanel.setData(clearLogInfo);
  }

  private void filterLogMessages() {
      String filter = txFilter.getText(); 
      if (filter.trim().equals("")) filterString = null; // NOI18N
      else filterString = filter;
      totalCount = 0;
      selectedCount = 0;
      recreateModel();
      Integer selCount = new Integer(selectedCount);
      Integer totCount = new Integer(totalCount);
      String txt = NbBundle.getMessage(LogTreeInfoPanel.class, "LogTreeInfoPanel.lblCount", // NOI18N
                                selCount.toString(), totCount.toString());
      lblCount.setText(txt);
  }    
  
  /** defines which InfoContainer instances will be added to the tree
   * By default adds all, can be overriden by subclasses to define filtering
 */
  protected void addFileNode(FileInfoContainer info,DefaultMutableTreeNode parent) {
      LogInformation logInfo = (LogInformation)info;
      totalCount = totalCount + 1;
      if (shouldAddLogInfo(logInfo)) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(info);
            parent.add(child);
            selectedCount = selectedCount + 1;
      }    
  }
  
  /** defines which InfoContainer instances will be added to the table
   * By default adds all, can be overriden by subclasses to define filtering
 */
  protected boolean addToList(FileInfoContainer info) {
      LogInformation logInfo = (LogInformation)info;
      return shouldAddLogInfo(logInfo);
  }
  
  private boolean shouldAddLogInfo(LogInformation info) {
      if (filterString == null) return true;
      List revList = info.getRevisionList();
      Iterator it = revList.iterator();
      while (it.hasNext()) {
          LogInformation.Revision revis = (LogInformation.Revision)it.next();
          if (revis.getMessage() != null && revis.getMessage().indexOf(filterString) >= 0) {
              return true;
          }    
      }    
      return false;
  }    
  
  /** in this method the displayer should use the data returned by the command to
   * produce it's own data structures/ fill in UI components
   * @param resultList - the data from the command. It is assumed the Displayer 
   * knows what command the data comes from and most important in what format. 
   * (which FileInfoContainer class is used).
 */
  public void setDataToDisplay(Collection resultList) {
      totalCount = 0;
      selectedCount = 0;
      super.setDataToDisplay(resultList);
      Integer selCount = new Integer(selectedCount);
      Integer totCount = new Integer(totalCount);
      String txt = NbBundle.getMessage(LogTreeInfoPanel.class, "LogTreeInfoPanel.lblCount", // NOI18N
                                selCount.toString(), totCount.toString());
      lblCount.setText(txt);
  }

  public JComponent getComponent() {
      return this;
  }
  
  public Object getComparisonData() {
      return topDirectory;
  }
  
   
}
