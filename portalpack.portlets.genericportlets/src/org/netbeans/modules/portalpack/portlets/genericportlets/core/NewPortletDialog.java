/*
  * The contents of this file are subject to the terms of the Common Development
  * and Distribution License (the License). You may not use this file except in
  * compliance with the License.
  *
  * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
  * or http://www.netbeans.org/cddl.txt.
  *
  * When distributing Covered Code, include this CDDL Header Notice in each file
  * and include the License file at http://www.netbeans.org/cddl.txt.
  * If applicable, add the following below the CDDL Header, with the fields
  * enclosed by brackets [] replaced by your own identifying information:
  * "Portions Copyrighted [year] [name of copyright owner]"
  *
  * The Original Software is NetBeans. The Initial Developer of the Original
  * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
  * Microsystems, Inc. All Rights Reserved.
  */

package org.netbeans.modules.portalpack.portlets.genericportlets.core;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class NewPortletDialog extends JDialog {
    private JButton buttonOK;
    private JButton buttonCancel;

    private JTextField classTf;
    private JTextField nametf;

    public NewPortletDialog() {
//        setContentPane(contentPane);

        setModal(true);
        init();

        getRootPane().setDefaultButton(buttonOK);


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
//        getContentPane().registerKeyboardAction(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK()
    {

        if(classTf.getText().trim().length() == 0)
        {
                JOptionPane.showMessageDialog(this,"Enter a Valid Class Name");
                return;
        }
        else if(nametf.getText().trim().length() == 0)
        {
                JOptionPane.showMessageDialog(this,"Enter a Valid Portlet Name");
                return;
        }

// add your code here
        dispose();
    }

    private void onCancel() {
        classTf.setText("");
        nametf.setText("");
// add your code here if necessary
        dispose();
    }

    public String getClassName()
    {
        if(classTf != null)
        {
            String className = classTf.getText();
            return className;
        }
        return "";
    }

    public String getPortletName()
    {
        if(nametf != null)
        {
            String name = nametf.getText();
            return name;
        }
        return "";
    }

    public void open()
    {
        setTitle("New Portlet Class");
        pack();
        setVisible(true);
//        setBounds(300,300,400,200);

    }

    public static void main(String[] args) {
        NewPortletDialog dialog = new NewPortletDialog();
        dialog.pack();
        dialog.show();
        System.exit(0);
    }


    private void init()
    {
        setResizable(false);
//        getContentPane().
                setLayout(new GridBagLayout());
        setTitle("Enter portlet details");
        setBounds(300, 300, 314, 130);

        final JLabel label = new JLabel();
        label.setText("Portlet Class");
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
//        getContentPane().
                add(label, gridBagConstraints);

        classTf = new JTextField(12);
        classTf.setAlignmentX(Component.LEFT_ALIGNMENT);
        final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.ipadx = 50;
        gridBagConstraints_1.gridy = 0;
        gridBagConstraints_1.gridx = 1;
//        getContentPane().
                add(classTf, gridBagConstraints_1);

        final JLabel label_1 = new JLabel();
        label_1.setText("Portlet Name");
        final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.gridy = 1;
        gridBagConstraints_2.gridx = 0;
//        getContentPane().
                add(label_1, gridBagConstraints_2);

        nametf = new JTextField(12);
        nametf.setAlignmentX(Component.LEFT_ALIGNMENT);
        final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
        gridBagConstraints_3.ipadx = 50;
        gridBagConstraints_3.gridy = 1;
        gridBagConstraints_3.gridx = 1;
//        getContentPane().
                add(nametf, gridBagConstraints_3);
        buttonOK = new JButton("OK");
        final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
        gridBagConstraints_4.gridy = 4;
        gridBagConstraints_4.gridx = 0;
//        getContentPane().
                add(buttonOK, gridBagConstraints_4);


        buttonCancel  = new JButton("Cancel");
        final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
        gridBagConstraints_5.gridy = 4;
        gridBagConstraints_5.gridx = 1;
//        getContentPane().
                add(buttonCancel, gridBagConstraints_5);
        buttonCancel.setText("Cancel");
    }




}
