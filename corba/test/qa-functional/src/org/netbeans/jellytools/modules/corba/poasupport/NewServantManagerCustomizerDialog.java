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

package org.netbeans.jellytools.modules.corba.poasupport;

import org.netbeans.jemmy.operators.*;

public class NewServantManagerCustomizerDialog extends JDialogOperator {

    /** Creates new NewServantManagerCustomizerDialog that can handle it.
     */
    public NewServantManagerCustomizerDialog() {
        super("Customizer Dialog");
    }

    private JLabelOperator _lblVariable;
    private JTextFieldOperator _txtVariable;
    private JCheckBoxOperator _cbGenerateServantManagerInstantiationCode;
    private JLabelOperator _lblType;
    private JComboBoxOperator _cboType;
    private JLabelOperator _lblConstructor;
    private JComboBoxOperator _cboConstructor;
    private JButtonOperator _btClose;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Variable:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblVariable() {
        if (_lblVariable==null) {
            _lblVariable = new JLabelOperator(this, "Variable:");
        }
        return _lblVariable;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtVariable() {
        if (_txtVariable==null) {
            _txtVariable = new JTextFieldOperator(this);
        }
        return _txtVariable;
    }

    /** Tries to find "Generate servant manager instantiation code" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbGenerateServantManagerInstantiationCode() {
        if (_cbGenerateServantManagerInstantiationCode==null) {
            _cbGenerateServantManagerInstantiationCode = new JCheckBoxOperator(this, "Generate servant manager instantiation code");
        }
        return _cbGenerateServantManagerInstantiationCode;
    }

    /** Tries to find "Type:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblType() {
        if (_lblType==null) {
            _lblType = new JLabelOperator(this, "Type:");
        }
        return _lblType;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboType() {
        if (_cboType==null) {
            _cboType = new JComboBoxOperator(this);
        }
        return _cboType;
    }

    /** Tries to find "Constructor:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblConstructor() {
        if (_lblConstructor==null) {
            _lblConstructor = new JLabelOperator(this, "Constructor:");
        }
        return _lblConstructor;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboConstructor() {
        if (_cboConstructor==null) {
            _cboConstructor = new JComboBoxOperator(this, 1);
        }
        return _cboConstructor;
    }

    /** Tries to find "Close" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btClose() {
        if (_btClose==null) {
            _btClose = new JButtonOperator(this, "Close");
        }
        return _btClose;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtVariable
     * @return String text
     */
    public String getVariable() {
        return txtVariable().getText();
    }

    /** sets text for txtVariable
     * @param text String text
     */
    public void setVariable(String text) {
        txtVariable().setText(text);
    }

    /** types text for txtVariable
     * @param text String text
     */
    public void typeVariable(String text) {
        txtVariable().typeText(text);
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkGenerateServantManagerInstantiationCode(boolean state) {
        if (cbGenerateServantManagerInstantiationCode().isSelected()!=state) {
            cbGenerateServantManagerInstantiationCode().push();
        }
    }

    /** returns selected item for cboType
     * @return String item
     */
    public String getSelectedType() {
        return cboType().getSelectedItem().toString();
    }

    /** selects item for cboType
     * @param item String item
     */
    public void selectType(String item) {
        cboType().selectItem(item);
    }

    /** types text for cboType
     * @param text String text
     */
    public void typeType(String text) {
        cboType().typeText(text);
    }

    /** returns selected item for cboConstructor
     * @return String item
     */
    public String getSelectedConstructor() {
        return cboConstructor().getSelectedItem().toString();
    }

    /** selects item for cboConstructor
     * @param item String item
     */
    public void selectConstructor(String item) {
        cboConstructor().selectItem(item);
    }

    /** types text for cboConstructor
     * @param text String text
     */
    public void typeConstructor(String text) {
        cboConstructor().typeText(text);
    }

    /** clicks on "Close" JButton
     */
    public void close() {
        btClose().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of NewServantManagerCustomizerDialog by accessing all its components.
     */
    public void verify() {
        lblVariable();
        txtVariable();
        cbGenerateServantManagerInstantiationCode();
        lblType();
        cboType();
        lblConstructor();
        cboConstructor();
        btClose();
    }

    /** Performs simple test of NewServantManagerCustomizerDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new NewServantManagerCustomizerDialog().verify();
        System.out.println("NewServantManagerCustomizerDialog verification finished.");
    }
}

