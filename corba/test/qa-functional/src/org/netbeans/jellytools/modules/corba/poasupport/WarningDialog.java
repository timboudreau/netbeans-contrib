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

/** Class implementing all necessary methods for handling "Warning" NbPresenter.
 *
 * @author dave
 * @version 1.0
 */
public class WarningDialog extends JDialogOperator {

    /** Creates new WarningDialog that can handle it.
     */
    public WarningDialog() {
        super("Warning");
    }

    private JTextAreaOperator _txtJTextArea;
    private JLabelOperator _lblRequestProcessing;
    private JComboBoxOperator _cboJComboBox;
    public static final String ITEM_USE_DEFAULT_SERVANT = "USE_DEFAULT_SERVANT"; 
    public static final String ITEM_USE_SERVANT_MANAGER = "USE_SERVANT_MANAGER"; 
    private JButtonOperator _btOK;
    private JButtonOperator _btCancel;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find null JTextArea in this dialog.
     * @return JTextAreaOperator
     */
    public JTextAreaOperator txtJTextArea() {
        if (_txtJTextArea==null) {
            _txtJTextArea = new JTextAreaOperator(this);
        }
        return _txtJTextArea;
    }

    /** Tries to find "Request Processing:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblRequestProcessing() {
        if (_lblRequestProcessing==null) {
            _lblRequestProcessing = new JLabelOperator(this, "Request Processing:");
        }
        return _lblRequestProcessing;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboJComboBox() {
        if (_cboJComboBox==null) {
            _cboJComboBox = new JComboBoxOperator(this);
        }
        return _cboJComboBox;
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

    /** gets text for txtJTextArea
     * @return String text
     */
    public String getJTextArea() {
        return txtJTextArea().getText();
    }

    /** sets text for txtJTextArea
     * @param text String text
     */
    public void setJTextArea(String text) {
        txtJTextArea().setText(text);
    }

    /** types text for txtJTextArea
     * @param text String text
     */
    public void typeJTextArea(String text) {
        txtJTextArea().typeText(text);
    }

    /** returns selected item for cboJComboBox
     * @return String item
     */
    public String getSelectedJComboBox() {
        return cboJComboBox().getSelectedItem().toString();
    }

    /** selects item for cboJComboBox
     * @param item String item
     */
    public void selectJComboBox(String item) {
        cboJComboBox().selectItem(item);
    }

    /** types text for cboJComboBox
     * @param text String text
     */
    public void typeJComboBox(String text) {
        cboJComboBox().typeText(text);
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

    /** Performs verification of WarningDialog by accessing all its components.
     */
    public void verify() {
        txtJTextArea();
        lblRequestProcessing();
        cboJComboBox();
        btOK();
        btCancel();
    }

    /** Performs simple test of WarningDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new WarningDialog().verify();
        System.out.println("WarningDialog verification finished.");
    }
}

