/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

/*
 * NewProviderPanel.java
 * Represents panel for adding new providers
 * Created on September 21, 1999, 5:41 PM
 */

package org.netbeans.modules.jndi;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeSupport;
import java.util.Vector;
/**
 *
 * @author  tzezula
 * @version
 */
public class NewProviderPanel extends AbstractNewPanel {

    JTextField factory;
    private PropertyChangeSupport listeners;

    /** Creates new NewProviderPanel */
    public NewProviderPanel() {
        super();
        this.getAccessibleContext().setAccessibleDescription(JndiRootNode.getLocalizedString("AD_NewProviderPanel"));
        this.listeners = new PropertyChangeSupport(this);
        javax.accessibility.AccessibleContext ac = this.getAccessibleContext();
        ac.setAccessibleName (JndiRootNode.getLocalizedString("TIP_Installation"));
    }

    /** Accessor for Factory
    *  @return String name of Factory
    */
    public String getFactory() {
        return (String) factory.getText();
    }

    /** Creates an part of GUI  called from createGUI*/
    JPanel createSubGUI(){
        this.factory = new JTextField();
        this.factory.getAccessibleContext().setAccessibleDescription ("AD_Factory");
        this.context = new JTextField();
        this.authentification = new JTextField();
        this.principal = new JTextField();
        this.credentials= new JTextField();
        this.root = new JTextField();
        JPanel p = new JPanel();
        p.setLayout ( new GridBagLayout());
        GridBagConstraints gridBagConstraints;
        JLabel label = new JLabel (JndiRootNode.getLocalizedString("TXT_Factory"));
        label.setLabelFor (this.factory);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString ("TXT_Factory_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_InitialContext"));
        label.setLabelFor (this.context);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString("TXT_InitialContext_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_Root"));
        label.setLabelFor (this.root);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString("TXT_Root_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_Auth"));
        label.setLabelFor (this.authentification);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString ("TXT_Auth_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_Principal"));
        label.setLabelFor (this.principal);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString("TXT_Principal_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_Credentials"));
        label.setLabelFor (this.credentials);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString("TXT_Credentials_MNEM").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
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
        return p;
    }


    javax.swing.JPanel createNotesPanel(){
        javax.swing.JPanel p = new javax.swing.JPanel();
        javax.swing.JTextArea area = new javax.swing.JTextArea(JndiRootNode.getLocalizedString("TIP_Installation"),2,66);
        area.setEnabled(false);
        area.setBackground(new javax.swing.JLabel().getBackground());
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        p.add(area);
        return p;
    }

}
