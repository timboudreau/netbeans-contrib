/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

import org.openide.util.NbBundle;

/**
 * The interactive display of command output with an input field.
 *
 * @author  Martin Entlicher
 */
class InteractiveCommandOutputPanel extends CommandOutputPanel {
    
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
        super.commandFinished(isFinished);
        if (isFinished) {
            inputStringLabel.setEnabled(false);
            inputStringTextField.setEnabled(false);
        }
    }
    
}
