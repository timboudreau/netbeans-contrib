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
 * MemberDialog.java
 *
 * Created on 15.7.02 13:58
 */
package org.netbeans.jellytools.modules.corba.idldialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Create Member" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class MemberDialog extends JDialogOperator {

    /** Creates new MemberDialog that can handle it.
     */
    public MemberDialog(boolean customize) {
        super(customize ? "Customize" : "Create Member");
    }

    private JLabelOperator _lblName;
    private JLabelOperator _lblType;
    private JLabelOperator _lblLength;
    private JTextFieldOperator _txtName;
    private JTextFieldOperator _txtType;
    private JTextFieldOperator _txtLength;
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

    /** Tries to find "Type:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblType() {
        if (_lblType==null) {
            _lblType = new JLabelOperator(this, "Type:");
        }
        return _lblType;
    }

    /** Tries to find "Length:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblLength() {
        if (_lblLength==null) {
            _lblLength = new JLabelOperator(this, "Length:");
        }
        return _lblLength;
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
    public JTextFieldOperator txtType() {
        if (_txtType==null) {
            _txtType = new JTextFieldOperator(this, 1);
        }
        return _txtType;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtLength() {
        if (_txtLength==null) {
            _txtLength = new JTextFieldOperator(this, 2);
        }
        return _txtLength;
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

    /** gets text for txtType
     * @return String text
     */
    public String getType() {
        return txtType().getText();
    }

    /** sets text for txtType
     * @param text String text
     */
    public void setType(String text) {
        txtType().setText(text);
    }

    /** types text for txtType
     * @param text String text
     */
    public void typeType(String text) {
        txtType().typeText(text);
    }

    /** gets text for txtLength
     * @return String text
     */
    public String getLength() {
        return txtLength().getText();
    }

    /** sets text for txtLength
     * @param text String text
     */
    public void setLength(String text) {
        txtLength().setText(text);
    }

    /** types text for txtLength
     * @param text String text
     */
    public void typeLength(String text) {
        txtLength().typeText(text);
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

    /** Performs verification of MemberDialog by accessing all its components.
     */
    public void verify() {
        lblName();
        lblType();
        lblLength();
        txtName();
        txtType();
        txtLength();
        btOk();
        btCancel();
    }

    /** Performs simple test of MemberDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new MemberDialog(false).verify();
        System.out.println("MemberDialog verification finished.");
    }
}

