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
 * ForwardDialog.java
 *
 * Created on 15.7.02 13:59
 */
package org.netbeans.jellytools.modules.corba.idldialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Create Forward Declaration" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class ForwardDialog extends JDialogOperator {

    /** Creates new ForwardDialog that can handle it.
     */
    public ForwardDialog(boolean customize) {
        super(customize ? "Customize" : "Create Forward Declaration");
    }

    private JLabelOperator _lblName;
    private JTextFieldOperator _txtName;
    private JRadioButtonOperator _rbInterface;
    private JRadioButtonOperator _rbValue;
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

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtName() {
        if (_txtName==null) {
            _txtName = new JTextFieldOperator(this);
        }
        return _txtName;
    }

    /** Tries to find "Interface" JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbInterface() {
        if (_rbInterface==null) {
            _rbInterface = new JRadioButtonOperator(this, "Interface");
        }
        return _rbInterface;
    }

    /** Tries to find "Value" JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbValue() {
        if (_rbValue==null) {
            _rbValue = new JRadioButtonOperator(this, "Value");
        }
        return _rbValue;
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

    /** clicks on "Interface" JRadioButton
     */
    public void pushInterface() {
        rbInterface().push();
    }

    /** clicks on "Value" JRadioButton
     */
    public void pushValue() {
        rbValue().push();
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

    /** Performs verification of ForwardDialog by accessing all its components.
     */
    public void verify() {
        lblName();
        txtName();
        rbInterface();
        rbValue();
        btOk();
        btCancel();
    }

    /** Performs simple test of ForwardDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new ForwardDialog(false).verify();
        System.out.println("ForwardDialog verification finished.");
    }
}

