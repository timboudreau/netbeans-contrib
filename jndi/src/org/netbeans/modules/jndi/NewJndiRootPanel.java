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

package com.netbeans.enterprise.modules.jndi;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import java.awt.GridBagConstraints;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.naming.Context;

import org.openide.TopManager;
import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;


/** Panel for dialog for adding new Context
 *
 *  @author Ales Novak, Tomas Zezula
 */
final class NewJndiRootPanel extends AbstractNewPanel implements ItemListener{
  /* this two static constants are for testing only
  in final version only valid constructor will be NewJndiRootPanel(String[],String[])*/

  /** ComboBox holding the factory*/
  JComboBox  factory;
  /** TextField holding the label*/
  JTextField label;
  
  /** Reference to Hashtable holding the Properties of providers */
  Hashtable providers;
  
  
  /** constructor takes as parameter array of factories and protocols
   * @param fcs array of factories
   * @param proto array of protocols
   */
  public NewJndiRootPanel(Hashtable providers) {
    super();
    String className = null;
    NotFoundPanel panel = null;
    this.providers=providers;
    java.util.Enumeration enum = this.providers.keys ();
    while (enum.hasMoreElements () ) {
      try{
        className = (String) enum.nextElement ();
        Class.forName (className);
        this.factory.addItem (className);
      }catch(ClassNotFoundException cnf){
        if (panel==null) panel = new NotFoundPanel();
        panel.add(className);
      }
    }
    if (panel != null){
      TopManager.getDefault().notify(new NotifyDescriptor.Message(panel,NotifyDescriptor.Message.WARNING_MESSAGE));
    }
  }

  /** Accessor for Factory
   *  @return String name of Factory
   */
  public String getFactory() {
    return (String) factory.getSelectedItem();
  }
  
  /** Accessor for Label
   *  @return String name of JndiRootNode
   */
  public String getLabel()  {
    return this.label.getText();
  }


  
  /** Synchronization of Factory and Protocol
   *  @param event ItemEvent
   */
  public void itemStateChanged(ItemEvent event) {

    if (event.getSource() == this.factory) {
     // this.properties.clear();
      Object item = factory.getSelectedItem();
      if (item != null){
        ProviderProperties p =(ProviderProperties)this.providers.get(item);
        if (p!=null){
          this.context.setText(p.getContext());
          this.authentification.setText(p.getAuthentification());
          this.principal.setText(p.getPrincipal());
          this.credentials.setText(p.getCredentials());
          this.properties.setData(p.getAdditionalSave());
          this.root.setText(p.getRoot());
        }
      }
    }
  }
  
  /** Creates a part of GUI, called grom createGUI */
  JPanel createSubGUI(){
    this.label = new JTextField();
    this.factory = new JComboBox();
    this.factory.setEditable(true);
    this.factory.setSize(this.label.getSize());
    this.factory.addItemListener(this);
    this.context = new JTextField();
    this.authentification = new JTextField();
    this.principal = new JTextField();
    this.credentials= new JTextField();
    this.root = new JTextField();
    JPanel p = new JPanel();
    p.setLayout ( new GridBagLayout());
    GridBagConstraints gridBagConstraints;

    JLabel label = new JLabel (JndiRootNode.getLocalizedString("TXT_ContextLabel"));
    gridBagConstraints = new java.awt.GridBagConstraints ();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    p.add (label, gridBagConstraints);
    label = new JLabel (JndiRootNode.getLocalizedString("TXT_Factory"));
    gridBagConstraints = new java.awt.GridBagConstraints ();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    p.add (label, gridBagConstraints);
    label = new JLabel (JndiRootNode.getLocalizedString("TXT_InitialContext"));
    gridBagConstraints = new java.awt.GridBagConstraints ();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    p.add (label, gridBagConstraints);
    label = new JLabel (JndiRootNode.getLocalizedString("TXT_Root"));
    gridBagConstraints = new java.awt.GridBagConstraints ();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    p.add (label, gridBagConstraints);
    label = new JLabel (JndiRootNode.getLocalizedString("TXT_Auth"));
    gridBagConstraints = new java.awt.GridBagConstraints ();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    p.add (label, gridBagConstraints);
    label = new JLabel (JndiRootNode.getLocalizedString("TXT_Principal"));
    gridBagConstraints = new java.awt.GridBagConstraints ();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    p.add (label, gridBagConstraints);
    label = new JLabel (JndiRootNode.getLocalizedString("TXT_Credentials"));
    gridBagConstraints = new java.awt.GridBagConstraints ();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    p.add (label, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints ();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    p.add (this.label, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints ();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    p.add (this.factory, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints ();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    p.add (this.context, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints ();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    p.add (this.root, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints ();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    p.add (this.authentification, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints ();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    p.add (this.principal, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints ();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    p.add (this.credentials, gridBagConstraints);
    
//    p.add(new JLabel(JndiRootNode.getLocalizedString("TXT_ContextLabel")),0,0,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,0,0,0,0,8,8,0,0);
//    p.add(this.label,1,0,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,0,0,1,0,8,8,0,8);
//    p.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Factory")),0,1,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,0,0,0,0,8,8,0,0);
//    p.add(this.factory,1,1,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,0,0,1,0,8,8,0,8);
//    p.add(new JLabel(JndiRootNode.getLocalizedString("TXT_InitialContext")),0,2,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,0,0,0,0,8,8,0,0);
//    p.add(this.context,1,2,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,0,0,1,0,8,8,0,8);
//    p.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Root")),0,3,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,0,0,0,0,8,8,0,0);
//    p.add(this.root,1,3,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,0,0,1,0,8,8,0,8);
//    p.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Auth")),0,4,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,0,0,0,0,8,8,0,0);
//    p.add(this.authentification,1,4,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,0,0,1,0,8,8,0,8);
//    p.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Principal")),0,5,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,0,0,0,0,8,8,0,0);
//    p.add(this.principal,1,5,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,0,0,1,0,8,8,0,8);
//   p.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Credentials")),0,6,1,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,0,0,0,0,8,8,0,0);
//    p.add(this.credentials,1,6,3,1,GridBagConstraints.HORIZONTAL,GridBagConstraints.NORTHWEST,0,0,1,0,8,8,0,8);
    return p;
  }

  
  
  
}
