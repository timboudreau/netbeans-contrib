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

/*
 * AbstractNewPanel.java
 *
 * Created on September 21, 1999, 4:39 PM
 */

package org.netbeans.modules.jndi;

import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import org.openide.TopManager;
import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;
import org.netbeans.modules.jndi.utils.SimpleListModel;

/**
 *
 * @author  tzezula
 * @version 
 */
abstract public class AbstractNewPanel extends JPanel implements ActionListener, ListSelectionListener{


    JList list;
    JTextField context;
    JTextField authentification;
    JTextField root;
    JTextField principal;
    JTextField credentials;
    SimpleListModel properties;
    NewPropertyPanel panel;
    Dialog dlg=null;
    JButton removeButton;
    JButton changeButton;
    JButton addButton;



    /** Creates new AbstractNewPanel */
    public AbstractNewPanel() {
        createGUI();
    }


    /** Accessor for Context
    *  @return String name of starting context
    */
    public String getContext() {
        return context.getText();
    }

    /** Accesor for Root
     *  @return String root
     */
    public String getRoot() {
        return this.root.getText();
    }

    /** Accessor for Autentification
     *  @return String autentification
     */
    public String getAuthentification() {
        return authentification.getText();
    }

    /** Accessor for principals
     *  @return String principals
     */
    public String getPrincipal() {
        return principal.getText();
    }

    /** Accessor for credentials
     *  @return String credentials
     */
    public String getCredentials() {
        return credentials.getText();
    }


    /** Accessor for additional properties
     *  @return Vector of type java.lang.String of format key=value
     */
    public java.util.Vector getAditionalProperties() {
        return properties.asVector();
    }


    /** Action handling
     *  @param ActionEvent event
     */
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("ADD")) {
            panel = new NewPropertyPanel();
            DialogDescriptor descriptor = new DialogDescriptor(panel,
                                          JndiRootNode.getLocalizedString("TITLE_Add_property"),
                                          true,
                                          DialogDescriptor.OK_CANCEL_OPTION,
                                          DialogDescriptor.OK_OPTION,
                                          new ActionListener() {
                                              public void actionPerformed(ActionEvent event2) {
                                                  if (event2.getSource() == DialogDescriptor.OK_OPTION) {
                                                      if ((panel.getName().length()==0) ||
                                                              (panel.getValue().length() == 0)) {
                                                          TopManager.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("EXC_Params"),NotifyDescriptor.Message.ERROR_MESSAGE));
                                                          return;
                                                      }
                                                      String pr = panel.getName() + "=" + panel.getValue();
                                                      properties.addElement(pr);
                                                      dlg.setVisible(false);
                                                      dlg.dispose();
                                                  } else if (event2.getSource() == DialogDescriptor.CANCEL_OPTION) {
                                                      dlg.setVisible(false);
                                                      dlg.dispose();
                                                  }
                                              }
                                          }
                                                              );
            dlg = TopManager.getDefault().createDialog(descriptor);
            dlg.setVisible(true);
        } else if (event.getActionCommand().equals("DEL")) {
            int index = AbstractNewPanel.this.list.getSelectedIndex();
            if (index < 0) {
                return;
            }
            AbstractNewPanel.this.properties.removeElementAt(index);
            this.removeButton.setEnabled(false);
            this.changeButton.setEnabled(false);
        } else if (event.getActionCommand().equals("CHANGE")) {
            panel = new NewPropertyPanel();
            int index = list.getSelectedIndex();
            if (index < 0) {
                return;
            }
            StringTokenizer tk = new StringTokenizer((String) properties.getElementAt(index), "=");
            if (tk.countTokens() != 2) return;
            panel.setName(tk.nextToken());
            panel.setValue(tk.nextToken());
            DialogDescriptor descriptor = new DialogDescriptor(panel,
                                          JndiRootNode.getLocalizedString("TITLE_Change_property"),
                                          true,
                                          DialogDescriptor.OK_CANCEL_OPTION,
                                          DialogDescriptor.OK_OPTION,
                                          new ActionListener() {
                                              public void actionPerformed(ActionEvent event2) {
                                                  if (event2.getSource() == DialogDescriptor.OK_OPTION) {
                                                      if ((panel.getName().length() == 0) ||
                                                              (panel.getValue().length() == 0)) {
                                                          TopManager.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("EXC_Params"),NotifyDescriptor.Message.ERROR_MESSAGE));
                                                          return;
                                                      }
                                                      properties.removeElementAt(list.getSelectedIndex());
                                                      String pr = panel.getName() + "=" + panel.getValue();
                                                      properties.addElement(pr);
                                                      AbstractNewPanel.this.removeButton.setEnabled(false);
                                                      AbstractNewPanel.this.changeButton.setEnabled(false);
                                                      dlg.setVisible(false);
                                                      dlg.dispose();
                                                  } else if (event2.getSource() == DialogDescriptor.CANCEL_OPTION) {
                                                      dlg.setVisible(false);
                                                      dlg.dispose();
                                                  }
                                              }
                                          }
                                                              );
            dlg = TopManager.getDefault().createDialog(descriptor);
            dlg.setVisible(true);
        }
    }

    /** Returns the name of the factory class
     * 
     */
    public abstract String getFactory();

    /** Creates modified part of GUI
     *  The subclasses differs in this part of gui
     */
    abstract javax.swing.JPanel createSubGUI();

    /** Creates an lover panel of dialog where notes are placed
     *  @return JPanel or null
     */
    abstract javax.swing.JPanel createNotesPanel();

    /** Creates GUI of Panel
     *  @param int mode for which the dialog is opening
     */
    final void createGUI(){
        this.setLayout ( new GridBagLayout());
        JPanel p = createSubGUI();
        this.properties = new SimpleListModel();
        this.list = new JList();
        this.list.addListSelectionListener(this);
        this.list.setModel(this.properties);
        this.list.setVisibleRowCount(8);
        this.list.setPrototypeCellValue("123456789012345678901234567890123456");
        JLabel label1 = new JLabel(JndiRootNode.getLocalizedString("TXT_OtherProps"));
        label1.setLabelFor (this.list);
        label1.setDisplayedMnemonic (JndiRootNode.getLocalizedString ("TXT_OtherProps_MNEM").charAt (0));
        
        GridBagConstraints gridBagConstraints = new GridBagConstraints ();
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add (p, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        add (label1, gridBagConstraints);
        
        
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 8, 0);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        add (new JScrollPane(this.list), gridBagConstraints);
        
        p = new javax.swing.JPanel();
        p.setLayout(new GridBagLayout());
        this.addButton = new JButton(JndiRootNode.getLocalizedString("TXT_Add"));
        this.addButton.setActionCommand("ADD");
        this.addButton.addActionListener(this);
        this.addButton.setMnemonic (JndiRootNode.getLocalizedString("TXT_Add_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets (0, 0, 8, 0);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        p.add (this.addButton, gridBagConstraints);
        
        this.changeButton = new JButton(JndiRootNode.getLocalizedString("TXT_Change"));
        this.changeButton.setEnabled(false);
        this.changeButton.setActionCommand("CHANGE");
        this.changeButton.addActionListener(this);
        this.changeButton.setMnemonic (JndiRootNode.getLocalizedString ("TXT_Change_MNEM").charAt (0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets (0, 0, 8, 0);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        p.add (this.changeButton, gridBagConstraints);
        
        this.removeButton = new JButton(JndiRootNode.getLocalizedString("TXT_Rem"));
        this.removeButton.setEnabled(false);
        this.removeButton.setActionCommand("DEL");
        this.removeButton.addActionListener(this);
        this.removeButton.setMnemonic (JndiRootNode.getLocalizedString ("TXT_Rem_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        p.add (removeButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 8, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        add (p, gridBagConstraints);
        
        p = createNotesPanel();
        if (p != null){
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.gridheight = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
            gridBagConstraints.insets = new java.awt.Insets (0, 8, 8, 8);
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 0.0;
            add (p, gridBagConstraints);
        }
        
        this.list.getAccessibleContext().setAccessibleDescription (JndiRootNode.getLocalizedString("AD_OtherProps"));
        this.context.getAccessibleContext().setAccessibleDescription(JndiRootNode.getLocalizedString("AD_Context"));
        this.authentification.getAccessibleContext().setAccessibleDescription(JndiRootNode.getLocalizedString("AD_Authentification"));
        this.root.getAccessibleContext().setAccessibleDescription(JndiRootNode.getLocalizedString("AD_Root"));
        this.principal.getAccessibleContext().setAccessibleDescription(JndiRootNode.getLocalizedString("AD_Principal"));
        this.credentials.getAccessibleContext().setAccessibleDescription(JndiRootNode.getLocalizedString("AD_Credentials"));
        this.removeButton.getAccessibleContext().setAccessibleDescription(JndiRootNode.getLocalizedString("AD_RemoveButton"));
        this.changeButton.getAccessibleContext().setAccessibleDescription(JndiRootNode.getLocalizedString("AD_ChangeButton"));
        this.addButton.getAccessibleContext().setAccessibleDescription(JndiRootNode.getLocalizedString("AD_AddButton"));
    }


    public void valueChanged ( ListSelectionEvent event){
        if (event.getSource() == this.list){
            if (this.list.getSelectedIndex()==-1){
                this.removeButton.setEnabled(false);
                this.changeButton.setEnabled(false);
            }
            else{
                this.removeButton.setEnabled(true);
                this.changeButton.setEnabled(true);
            }
        }


    }
}
