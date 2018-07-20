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

package org.netbeans.modules.vcscore.commands;

import org.netbeans.modules.vcscore.ui.OutputPanel;
import org.openide.util.NbBundle;

/**
 * The interactive display of command output with an input field.
 *
 * @author  Martin Entlicher
 */
//class InteractiveCommandOutputPanel extends CommandOutputPanel {
class InteractiveCommandOutputPanel extends OutputPanel{

    private javax.swing.JLabel inputStringLabel;
    private javax.swing.JTextField inputStringTextField;
    private TextInput input;

    /** Creates a new instance of InteractiveCommandOutputPanel */
    public InteractiveCommandOutputPanel() {
        initComponents();
    }
    
    private void initComponents() {
        //javax.swing.JPanel ioPanel = new javax.swing.JPanel();
        //javax.swing.JPanel outputPanel = new javax.swing.JPanel();
        javax.swing.JPanel inputPanel = new javax.swing.JPanel();
        
        //ioPanel.setLayout(new java.awt.GridBagLayout());
        //outputPanel.setLayout(new java.awt.GridBagLayout());
        inputPanel.setLayout(new java.awt.GridBagLayout());
        
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        //remove(1);
        //addImpl(ioPanel, gridBagConstraints, 1);
        getOutputPanel().add(inputPanel, gridBagConstraints);
        
        inputStringLabel = new javax.swing.JLabel(NbBundle.getMessage(InteractiveCommandOutputPanel.class, "InteractiveCommandOutputPanel.Input.label"));
        inputStringLabel.setDisplayedMnemonic(NbBundle.getMessage(InteractiveCommandOutputPanel.class, "InteractiveCommandOutputPanel.Input.mnemonic").charAt(0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        inputPanel.add(inputStringLabel, gridBagConstraints);
        
        inputStringTextField = new javax.swing.JTextField();
        inputStringLabel.setLabelFor(inputStringTextField);
        inputStringTextField.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(InteractiveCommandOutputPanel.class, "InteractiveCommandOutputPanel.Input.acsd"));
        inputStringTextField.setToolTipText(NbBundle.getMessage(InteractiveCommandOutputPanel.class, "InteractiveCommandOutputPanel.Input.acsd"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        inputPanel.add(inputStringTextField, gridBagConstraints);
        
        inputStringTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (input == null) return ;
                String text = inputStringTextField.getText() + '\n';
                input.sendInput(text);
                inputStringTextField.setText("");
            }
        });
        
        /*
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        ioPanel.add(outputPanel, gridBagConstraints);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        ioPanel.add(inputPanel, gridBagConstraints);
         */
    }
    
    void setInput(TextInput input) {
        this.input = input;
    }
    
    public void commandFinished(boolean isFinished) {
    //    super.commandFinished(isFinished);
        if (isFinished) {
            inputStringLabel.setEnabled(false);
            inputStringTextField.setEnabled(false);
        }
    }
    
}
