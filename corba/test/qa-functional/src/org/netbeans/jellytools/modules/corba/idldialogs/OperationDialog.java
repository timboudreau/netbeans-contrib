/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * OperationDialog.java
 *
 * Created on 15.7.02 14:03
 */
package org.netbeans.jellytools.modules.corba.idldialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Create Operation" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class OperationDialog extends JDialogOperator {

    /** Creates new OperationDialog that can handle it.
     */
    public OperationDialog(boolean customize) {
        super(customize ? "Customize" : "Create Operation");
    }

    private JLabelOperator _lblName;
    private JLabelOperator _lblReturnType;
    private JLabelOperator _lblParameters;
    private JLabelOperator _lblExceptions;
    private JLabelOperator _lblContext;
    private JTextFieldOperator _txtName;
    private JTextFieldOperator _txtReturnType;
    private JTextFieldOperator _txtParameters;
    private JTextFieldOperator _txtExceptions;
    private JTextFieldOperator _txtContext;
    private JCheckBoxOperator _cbOneway;
    private JButtonOperator _btOk;
    private JButtonOperator _btCancel;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblName() {
        if (_lblName==null) {
            _lblName = new JLabelOperator(this, "Name:");
        }
        return _lblName;
    }

    /** Tries to find "Return Type:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblReturnType() {
        if (_lblReturnType==null) {
            _lblReturnType = new JLabelOperator(this, "Return Type:");
        }
        return _lblReturnType;
    }

    /** Tries to find "Parameters:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblParameters() {
        if (_lblParameters==null) {
            _lblParameters = new JLabelOperator(this, "Parameters:");
        }
        return _lblParameters;
    }

    /** Tries to find "Exceptions:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblExceptions() {
        if (_lblExceptions==null) {
            _lblExceptions = new JLabelOperator(this, "Exceptions:");
        }
        return _lblExceptions;
    }

    /** Tries to find "Context:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblContext() {
        if (_lblContext==null) {
            _lblContext = new JLabelOperator(this, "Context:");
        }
        return _lblContext;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtName() {
        if (_txtName==null) {
            _txtName = new JTextFieldOperator(this);
        }
        return _txtName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtReturnType() {
        if (_txtReturnType==null) {
            _txtReturnType = new JTextFieldOperator(this, 1);
        }
        return _txtReturnType;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtParameters() {
        if (_txtParameters==null) {
            _txtParameters = new JTextFieldOperator(this, 2);
        }
        return _txtParameters;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtExceptions() {
        if (_txtExceptions==null) {
            _txtExceptions = new JTextFieldOperator(this, 3);
        }
        return _txtExceptions;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtContext() {
        if (_txtContext==null) {
            _txtContext = new JTextFieldOperator(this, 4);
        }
        return _txtContext;
    }

    /** Tries to find "oneway" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbOneway() {
        if (_cbOneway==null) {
            _cbOneway = new JCheckBoxOperator(this, "oneway");
        }
        return _cbOneway;
    }

    /** Tries to find "Ok" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btOk() {
        if (_btOk==null) {
            _btOk = new JButtonOperator(this, "Ok");
        }
        return _btOk;
    }

    /** Tries to find "Cancel" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btCancel() {
        if (_btCancel==null) {
            _btCancel = new JButtonOperator(this, "Cancel");
        }
        return _btCancel;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtName
     * @return String text
     */
    public String getName() {
        return txtName().getText();
    }

    /** sets text for txtName
     * @param text String text
     */
    public void setName(String text) {
        txtName().setText(text);
    }

    /** types text for txtName
     * @param text String text
     */
    public void typeName(String text) {
        txtName().typeText(text);
    }

    /** gets text for txtReturnType
     * @return String text
     */
    public String getReturnType() {
        return txtReturnType().getText();
    }

    /** sets text for txtReturnType
     * @param text String text
     */
    public void setReturnType(String text) {
        txtReturnType().setText(text);
    }

    /** types text for txtReturnType
     * @param text String text
     */
    public void typeReturnType(String text) {
        txtReturnType().typeText(text);
    }

    /** gets text for txtParameters
     * @return String text
     */
    public String getParameters() {
        return txtParameters().getText();
    }

    /** sets text for txtParameters
     * @param text String text
     */
    public void setParameters(String text) {
        txtParameters().setText(text);
    }

    /** types text for txtParameters
     * @param text String text
     */
    public void typeParameters(String text) {
        txtParameters().typeText(text);
    }

    /** gets text for txtExceptions
     * @return String text
     */
    public String getExceptions() {
        return txtExceptions().getText();
    }

    /** sets text for txtExceptions
     * @param text String text
     */
    public void setExceptions(String text) {
        txtExceptions().setText(text);
    }

    /** types text for txtExceptions
     * @param text String text
     */
    public void typeExceptions(String text) {
        txtExceptions().typeText(text);
    }

    /** gets text for txtContext
     * @return String text
     */
    public String getContext() {
        return txtContext().getText();
    }

    /** sets text for txtContext
     * @param text String text
     */
    public void setContext(String text) {
        txtContext().setText(text);
    }

    /** types text for txtContext
     * @param text String text
     */
    public void typeContext(String text) {
        txtContext().typeText(text);
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkOneway(boolean state) {
        if (cbOneway().isSelected()!=state) {
            cbOneway().push();
        }
    }

    /** clicks on "Ok" JButton
     */
    public void ok() {
        btOk().push();
    }

    /** clicks on "Cancel" JButton
     */
    public void cancel() {
        btCancel().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of OperationDialog by accessing all its components.
     */
    public void verify() {
        lblName();
        lblReturnType();
        lblParameters();
        lblExceptions();
        lblContext();
        txtName();
        txtReturnType();
        txtParameters();
        txtExceptions();
        txtContext();
        cbOneway();
        btOk();
        btCancel();
    }

    /** Performs simple test of OperationDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new OperationDialog(false).verify();
        System.out.println("OperationDialog verification finished.");
    }
}

