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

package org.netbeans.modules.jndi.gui;

import org.netbeans.modules.jndi.JndiRootNode;
/**
 *
 * @author  tzezula
 * @version
 */
public class TimeOutPanel extends javax.swing.JPanel {

    /** Creates new form TimeOutPanel */
    public TimeOutPanel(String message, String note) {
        initComponents ();
        postInitComponents(message, note);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jLabel1 = new javax.swing.JLabel();
        jTextArea1 = new javax.swing.JTextArea();

        setLayout(new java.awt.GridLayout(1, 1));

        jLabel1.setText("jLabel1");
        add(jLabel1);

        jTextArea1.setEditable(false);
        jTextArea1.setPreferredSize(new java.awt.Dimension(64, 32));
        jTextArea1.setMinimumSize(new java.awt.Dimension(64, 32));
        add(jTextArea1);

    }//GEN-END:initComponents

    private void postInitComponents(String message, String note){
        this.jLabel1.setText(message);
        this.jTextArea1.setText(note);
        this.jTextArea1.setEnabled(false);
        this.jTextArea1.setBackground(this.getBackground());
        this.jTextArea1.setLineWrap(true);
        this.jTextArea1.setWrapStyleWord(true);
        javax.accessibility.AccessibleContext ac = this.getAccessibleContext ();
        ac.setAccessibleName (ac.getAccessibleName()+message+note);
        ac.setAccessibleDescription (JndiRootNode.getLocalizedString("AD_TimeOutPanel"));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

}