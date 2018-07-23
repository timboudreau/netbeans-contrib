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

package org.netbeans.modules.corba.poasupport.tools;

import java.util.Vector;
import org.openide.NotifyDescriptor;
import org.netbeans.modules.corba.poasupport.POASupport;

/**
 *
 * @author Dusan Balek
 * @version
 */

public class ComboBoxSelector extends NotifyDescriptor {

    protected javax.swing.JComboBox comboBox;

    /** Creates new ComboBoxSelector */
    public ComboBoxSelector(final String text, final String label, final Vector values) {
        this(text, label, values, OK_CANCEL_OPTION, WARNING_MESSAGE);
    }

    public ComboBoxSelector(final String text, final String label, final Vector values, final int optionType, final int messageType) {
        super(null, getTitleForType(messageType), optionType, messageType, null, null);
        super.setMessage(createDesign(text, label, values));
    }
    
    public Object getSelectedItem () {
        return comboBox.getSelectedItem ();
    }
    
    protected java.awt.Component createDesign (final String text, final String label, final Vector values) {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        javax.swing.JTextArea textArea = new javax.swing.JTextArea(text);
        javax.swing.JLabel comboBoxLabel = new javax.swing.JLabel(label + POASupport.getString("LBL_Colon"));
        comboBox = new javax.swing.JComboBox(values);
        textArea.setEditable(false);
        textArea.setBorder(new javax.swing.border.EmptyBorder(0, 0, 6, 0));
        textArea.setForeground(java.awt.Color.black);
        textArea.setBackground(panel.getBackground());
        textArea.setFont(comboBoxLabel.getFont());
        comboBoxLabel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 6));
        comboBoxLabel.setForeground(java.awt.Color.black);
        panel.setLayout(new java.awt.BorderLayout());
        panel.add("North", textArea); // NOI18N
        panel.add("West", comboBoxLabel); // NOI18N
        panel.add("Center", comboBox); // NOI18N
        comboBox.requestFocus();
        return panel;
    }
}
