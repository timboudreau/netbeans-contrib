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
 
package com.netbeans.enterprise.modules.jndi;

import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;
import javax.swing.*;

import org.openide.TopManager;
import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;

/** 
 *
 * @author  tzezula
 * @version 
 */
abstract public class AbstractNewPanel extends GridBagPanel implements ActionListener{
  

  JList list;
  JTextField context;
  JTextField authentification;
  JTextField principal;
  JTextField credentials;
  JPopupMenu menu;
  SimpleListModel properties;
  NewPropertyPanel panel;
  Dialog dlg=null;
  
  

  /** Creates new AbstractNewPanel */
  public AbstractNewPanel() {
    createGUI();
    createMenu();
  }
  
    
   /** Accessor for Context
   *  @return String name of starting context
   */
  public String getContext() {
    return context.getText();
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
          public void actionPerformed(ActionEvent event) {
            if (event.getSource() == DialogDescriptor.OK_OPTION) {
              if ((panel.getName().length()==0) ||
                  (panel.getValue().length() == 0)) {
                TopManager.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("EXC_Params"),NotifyDescriptor.Message.ERROR_MESSAGE));
                return;
              }
              String pr = panel.getName() + "=" + panel.getValue();
              properties.addElement(pr);
              dlg.setVisible(false);
              dlg.dispose();
            } else if (event.getSource() == DialogDescriptor.CANCEL_OPTION) {
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
          public void actionPerformed(ActionEvent event) {
            if (event.getSource() == DialogDescriptor.OK_OPTION) {
              if ((panel.getName().length() == 0) ||
                  (panel.getValue().length() == 0)) {
                TopManager.getDefault().notify(new NotifyDescriptor.Message(JndiRootNode.getLocalizedString("EXC_Params"),NotifyDescriptor.Message.ERROR_MESSAGE));
                return;
              }
              properties.removeElementAt(list.getSelectedIndex());
              String pr = panel.getName() + "=" + panel.getValue();
              properties.addElement(pr);
              dlg.setVisible(false);
              dlg.dispose();
            } else if (event.getSource() == DialogDescriptor.CANCEL_OPTION) {
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
  abstract short createSubGUI();
  
  
  /** Creates GUI of Panel
   *  @param int mode for which the dialog is opening
   */
  final void createGUI(){
    int height = createSubGUI();
    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_OtherProps")),5,1,2,1,0,0,0,0);
    this.properties = new SimpleListModel();
    this.list = new JList();
    this.list.setModel(this.properties);
    this.list.setVisibleRowCount(height);
    this.list.setPrototypeCellValue("123456789012345678901234567890");
    this.add(new JScrollPane(list),5,2,2,7);
  }
  
  /** Creates Menu of Panels components
   *
   */
  final void createMenu(){
    menu = new JPopupMenu();
    JMenuItem item = new JMenuItem(JndiRootNode.getLocalizedString("TXT_Add"));
    item.setActionCommand("ADD");
    item.addActionListener (this);				
    menu.add(item);
    item= new JMenuItem(JndiRootNode.getLocalizedString("TXT_Rem"));
    item.setActionCommand("DEL");
    item.addActionListener(this);
    menu.add(item);
    item = new JMenuItem(JndiRootNode.getLocalizedString("TXT_Change"));
    item.setActionCommand("CHANGE");
    item.addActionListener(this);
    menu.add(item);
    list.add(menu);
    list.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent event) {
        if ((event.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0) {
                 AbstractNewPanel.this.menu.show(AbstractNewPanel.this.list,event.getX(),event.getY());
        }
      }
    });
  }
  
  
  
  
}