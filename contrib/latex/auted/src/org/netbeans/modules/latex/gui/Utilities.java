/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Jan Lahoda
 */
public class Utilities {
    
    /** Creates a new instance of Utilities */
    public Utilities() {
    }
    
    public static int showDialog(String name, JPanel panel) {
        JFrame parent = new JFrame();
        int result = showDialog(name, parent, panel);
        
        parent.hide();
        
        return result;
    }
    
    public static int showDialog(String name, JFrame parent, JPanel panel) {
        JDialog dialog = new JDialog(parent, name, true);
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        ActionListenerImpl l = new ActionListenerImpl(dialog);
        
        ok.setActionCommand("ok");
        ok.addActionListener(l);
        cancel.setActionCommand("cancel");
        cancel.addActionListener(l);
        
        GridBagConstraints gridBagConstraints;

        dialog.getContentPane().setLayout(new GridBagLayout());

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        dialog.getContentPane().add(panel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        dialog.getContentPane().add(ok, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        dialog.getContentPane().add(cancel, gridBagConstraints);

        dialog.pack();
        dialog.show();
        
        return l.getResult();
    }
    
    private static class ActionListenerImpl implements ActionListener {
        
        private int result = -1;
        private JDialog dialog;
        
        public ActionListenerImpl(JDialog dialog) {
            result = (-1);
            this.dialog = dialog;
        }
        
        public void actionPerformed(ActionEvent e) {
            if ("ok".equals(e.getActionCommand())) {
                result = JOptionPane.OK_OPTION;
                dialog.hide();
                return ;
            }
            
            if ("cancel".equals(e.getActionCommand())) {
                result = JOptionPane.CANCEL_OPTION;
                dialog.hide();
                return ;
            }
        }
        
        
        public int getResult() {
            return result;
        }
    }
    
    public static final void main(String[] args) {
        System.err.println(showDialog("State", new JFrame(), new StateProperties()));
    }
    
}
