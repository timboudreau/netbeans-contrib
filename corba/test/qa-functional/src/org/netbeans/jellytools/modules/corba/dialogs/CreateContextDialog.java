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
 * CreateContextDialog.java
 *
 * Created on 15.7.02 13:43
 */
package org.netbeans.jellytools.modules.corba.dialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "CORBA Panel" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class CreateContextDialog extends JDialogOperator {

    /** Creates new CreateContextDialog that can handle it.
     */
    public CreateContextDialog() {
        super("CORBA Panel");
    }

    private JLabelOperator _lblName;
    private JTextFieldOperator _txtName;
    private JLabelOperator _lblKind;
    private JTextFieldOperator _txtKind;
    private JButtonOperator _btOK;
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

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtName() {
        if (_txtName==null) {
            _txtName = new JTextFieldOperator(this);
        }
        return _txtName;
    }

    /** Tries to find "Kind:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblKind() {
        if (_lblKind==null) {
            _lblKind = new JLabelOperator(this, "Kind:");
        }
        return _lblKind;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtKind() {
        if (_txtKind==null) {
            _txtKind = new JTextFieldOperator(this, 1);
        }
        return _txtKind;
    }

    /** Tries to find "OK" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btOK() {
        if (_btOK==null) {
            _btOK = new JButtonOperator(this, "OK");
        }
        return _btOK;
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

    /** gets text for txtKind
     * @return String text
     */
    public String getKind() {
        return txtKind().getText();
    }

    /** sets text for txtKind
     * @param text String text
     */
    public void setKind(String text) {
        txtKind().setText(text);
    }

    /** types text for txtKind
     * @param text String text
     */
    public void typeKind(String text) {
        txtKind().typeText(text);
    }

    /** clicks on "OK" JButton
     */
    public void oK() {
        btOK().push();
    }

    /** clicks on "Cancel" JButton
     */
    public void cancel() {
        btCancel().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of CreateContextDialog by accessing all its components.
     */
    public void verify() {
        lblName();
        txtName();
        lblKind();
        txtKind();
        btOK();
        btCancel();
    }

    /** Performs simple test of CreateContextDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new CreateContextDialog().verify();
        System.out.println("CreateContextDialog verification finished.");
    }
}

