/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
/*
/* Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
/*
/* The contents of this file are subject to the terms of either the GNU
/* General Public License Version 2 only ("GPL") or the Common
/* Development and Distribution License("CDDL") (collectively, the
/* "License"). You may not use this file except in compliance with the
/* License. You can obtain a copy of the License at
/* http://www.netbeans.org/cddl-gplv2.html
/* or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
/* specific language governing permissions and limitations under the
/* License.  When distributing the software, include this License Header
/* Notice in each file and include the License file at
/* nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
/* particular file as subject to the "Classpath" exception as provided
/* by Sun in the GPL Version 2 section of the License file that
/* accompanied this code. If applicable, add the following below the
/* License Header, with the fields enclosed by brackets [] replaced by
/* your own identifying information:
/* "Portions Copyrighted [year] [name of copyright owner]"
/*
/* Contributor(s):
 *
 * The Original Software is RemoteFS. The Initial Developer of the Original
/* Software is Libor Martinek. Portions created by Libor Martinek are
 * Copyright (C) 2000. All Rights Reserved.
/*
/* If you wish your version of this file to be governed by only the CDDL
/* or only the GPL Version 2, indicate your decision by adding
/* "[Contributor] elects to include this software in this distribution
/* under the [CDDL or GPL Version 2] license." If you do not indicate a
/* single choice of license, a recipient has the option to distribute
/* your version of this file under either the CDDL, the GPL Version 2 or
/* to extend the choice of license to its licensees as provided above.
/* However, if you add GPL Version 2 code and therefore, elected the GPL
/* Version 2 license, then the option applies only if the new code is
/* made subject to such option by the copyright holder.
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
 * @version 1.1
 */
public class PasswordEditor implements EnhancedPropertyEditor, ActionListener, FocusListener {

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
      field.addFocusListener(this);
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
  
  public void focusGained(final java.awt.event.FocusEvent p1) {
  }

  public void focusLost(final java.awt.event.FocusEvent p1) {
      setValue(new String(field.getPassword()));
  }

}

