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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;
import javax.swing.JTextField;
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
        }
      }
    }
  }
  
  /** Creates a part of GUI, called grom createGUI */
  short createSubGUI(){
    this.label = new JTextField(26);
    this.factory = new JComboBox();
    this.factory.setEditable(true);
    this.factory.setSize(this.label.getSize());
    this.factory.addItemListener(this);
    this.context = new JTextField(26);
    this.authentification = new JTextField(26);
    this.principal = new JTextField(26);
    this.credentials= new JTextField(26);
    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_ContextLabel")),1,2,2,1,7,5,0,5);
    this.add(this.label,3,2,2,1,7,0,0,5);
    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Factory")),1,3,2,1,0,5,0,5);
    this.add(this.factory,3,3,2,1,0,0,0,5);
    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_InitialContext")),1,4,2,1,0,5,0,5);
    this.add(this.context,3,4,2,1,0,0,0,5);
    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Auth")),1,5,2,1,0,5,0,5);
    this.add(this.authentification,3,5,2,1,0,0,0,5);
    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Principal")),1,6,2,1,0,5,0,5);
    this.add(this.principal,3,6,2,1,0,0,0,5);
    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Credentials")),1,7,2,1,0,5,0,5);
    this.add(this.credentials,3,7,2,1,0,0,0,5);
    return 7;
  }

  
  
  
}
