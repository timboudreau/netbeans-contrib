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
 * StartLocalDialog.java
 *
 * Created on 15.7.02 13:40
 */
package org.netbeans.jellytools.modules.corba.dialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Local Name Service" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class StartLocalDialog extends JDialogOperator {

    /** Creates new StartLocalDialog that can handle it.
     */
    public StartLocalDialog() {
        super("Local Name Service");
    }

    private JLabelOperator _lblName;
    private JTextFieldOperator _txtName;
    private JLabelOperator _lblKind;
    private JTextFieldOperator _txtKind;
    private JLabelOperator _lblNameServicePort;
    private JTextFieldOperator _txtNameServicePort;
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

    /** Tries to find "Name Service Port:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblNameServicePort() {
        if (_lblNameServicePort==null) {
            _lblNameServicePort = new JLabelOperator(this, "Name Service Port:");
        }
        return _lblNameServicePort;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtNameServicePort() {
        if (_txtNameServicePort==null) {
            _txtNameServicePort = new JTextFieldOperator(this, 2);
        }
        return _txtNameServicePort;
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

    /** gets text for txtNameServicePort
     * @return String text
     */
    public String getNameServicePort() {
        return txtNameServicePort().getText();
    }

    /** sets text for txtNameServicePort
     * @param text String text
     */
    public void setNameServicePort(String text) {
        txtNameServicePort().setText(text);
    }

    /** types text for txtNameServicePort
     * @param text String text
     */
    public void typeNameServicePort(String text) {
        txtNameServicePort().typeText(text);
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

    /** Performs verification of StartLocalDialog by accessing all its components.
     */
    public void verify() {
        lblName();
        txtName();
        lblKind();
        txtKind();
        lblNameServicePort();
        txtNameServicePort();
        btOK();
        btCancel();
    }

    /** Performs simple test of StartLocalDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new StartLocalDialog().verify();
        System.out.println("StartLocalDialog verification finished.");
    }
}

