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
 * ValuetypeDialog.java
 *
 * Created on 15.7.02 14:02
 */
package org.netbeans.jellytools.modules.corba.idldialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Create Valuetype" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class ValuetypeDialog extends JDialogOperator {

    /** Creates new ValuetypeDialog that can handle it.
     */
    public ValuetypeDialog(boolean customize) {
        super(customize ? "Customize" : "Create Valuetype");
    }

    private JLabelOperator _lblName;
    private JLabelOperator _lblBase;
    private JLabelOperator _lblSupports;
    private JTextFieldOperator _txtName;
    private JTextFieldOperator _txtBase;
    private JTextFieldOperator _txtSupports;
    private JCheckBoxOperator _cbCustom;
    private JCheckBoxOperator _cbAbstract;
    private JCheckBoxOperator _cbTruncatable;
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

    /** Tries to find "Base:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblBase() {
        if (_lblBase==null) {
            _lblBase = new JLabelOperator(this, "Base:");
        }
        return _lblBase;
    }

    /** Tries to find "Supports:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblSupports() {
        if (_lblSupports==null) {
            _lblSupports = new JLabelOperator(this, "Supports:");
        }
        return _lblSupports;
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
    public JTextFieldOperator txtBase() {
        if (_txtBase==null) {
            _txtBase = new JTextFieldOperator(this, 1);
        }
        return _txtBase;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtSupports() {
        if (_txtSupports==null) {
            _txtSupports = new JTextFieldOperator(this, 2);
        }
        return _txtSupports;
    }

    /** Tries to find "Custom" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbCustom() {
        if (_cbCustom==null) {
            _cbCustom = new JCheckBoxOperator(this, "Custom");
        }
        return _cbCustom;
    }

    /** Tries to find "Abstract" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbAbstract() {
        if (_cbAbstract==null) {
            _cbAbstract = new JCheckBoxOperator(this, "Abstract");
        }
        return _cbAbstract;
    }

    /** Tries to find "Truncatable" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbTruncatable() {
        if (_cbTruncatable==null) {
            _cbTruncatable = new JCheckBoxOperator(this, "Truncatable");
        }
        return _cbTruncatable;
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

    /** gets text for txtBase
     * @return String text
     */
    public String getBase() {
        return txtBase().getText();
    }

    /** sets text for txtBase
     * @param text String text
     */
    public void setBase(String text) {
        txtBase().setText(text);
    }

    /** types text for txtBase
     * @param text String text
     */
    public void typeBase(String text) {
        txtBase().typeText(text);
    }

    /** gets text for txtSupports
     * @return String text
     */
    public String getSupports() {
        return txtSupports().getText();
    }

    /** sets text for txtSupports
     * @param text String text
     */
    public void setSupports(String text) {
        txtSupports().setText(text);
    }

    /** types text for txtSupports
     * @param text String text
     */
    public void typeSupports(String text) {
        txtSupports().typeText(text);
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkCustom(boolean state) {
        if (cbCustom().isSelected()!=state) {
            cbCustom().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkAbstract(boolean state) {
        if (cbAbstract().isSelected()!=state) {
            cbAbstract().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkTruncatable(boolean state) {
        if (cbTruncatable().isSelected()!=state) {
            cbTruncatable().push();
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

    /** Performs verification of ValuetypeDialog by accessing all its components.
     */
    public void verify() {
        lblName();
        lblBase();
        lblSupports();
        txtName();
        txtBase();
        txtSupports();
        cbCustom();
        cbAbstract();
        cbTruncatable();
        btOk();
        btCancel();
    }

    /** Performs simple test of ValuetypeDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new ValuetypeDialog(false).verify();
        System.out.println("ValuetypeDialog verification finished.");
    }
}

