/*                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version 
 * 1.0 (the "License"). You may not use this file except in compliance with 
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is RemoteFS. The Initial Developer of the Original
 * Code is Libor Martinek. Portions created by Libor Martinek are 
 * Copyright (C) 2000. All Rights Reserved.
 * 
 * Contributor(s): Libor Martinek. 
 */
 
package org.netbeans.modules.remotefs.ftpfs;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;

import org.openide.explorer.propertysheet.editors.*;

/** Password editor.
 *
 * @author  Libor Martinek
 * @version 1.0
 */
public class PasswordEditor implements EnhancedPropertyEditor, ActionListener {

  private String password;
  private PropertyChangeSupport support;
  transient private JPasswordField field;
  
  /** Creates new PasswordEditor. */
  public PasswordEditor() {
    support = new PropertyChangeSupport (this);
  }
  
  public boolean supportsEditingTaggedValues() {
    return false;
  }

  public java.lang.String[] getTags() {
    return new String[] {};
  }

  public boolean hasInPlaceCustomEditor() {
    return true;
  }

  public java.awt.Component getInPlaceCustomEditor() {
    if (field == null) {
      field = new JPasswordField();
      field.addActionListener(this);
    }
    if (password != null) {
      field.setText(password);
      field.setSelectionStart(0);
      field.setSelectionEnd(password.length());
    }
    return field;
  }

  public void setValue(final java.lang.Object p0) {
    if (p0 instanceof String) password = (String)p0;
    support.firePropertyChange ("", null, null);
  }

  public java.lang.Object getValue() {
    return password;
  }

  public java.lang.String getJavaInitializationString() {
    return "";
  }

  public boolean supportsCustomEditor() {
    return false;
  }

  public java.awt.Component getCustomEditor() {
    return null;
  }

  public boolean isPaintable() {
    return false;
  }

  public void paintValue(final java.awt.Graphics p0,final java.awt.Rectangle p1) {
  }

  public java.lang.String getAsText() {
    return "***********";
  }

  public void setAsText(java.lang.String p0) throws java.lang.IllegalArgumentException {
  }

  public void addPropertyChangeListener(final java.beans.PropertyChangeListener p0) {
    support.addPropertyChangeListener(p0);
  }

  public void removePropertyChangeListener(final java.beans.PropertyChangeListener p0) {
    support.removePropertyChangeListener(p0);
  }
  
  public void actionPerformed(ActionEvent ev) {
    setValue(new String(field.getPassword()));
  }
}

