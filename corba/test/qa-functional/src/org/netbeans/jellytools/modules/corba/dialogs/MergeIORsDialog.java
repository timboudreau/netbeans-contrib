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
 * MergeIORsDialog.java
 *
 * Created on 16.7.02 16:19
 */
package org.netbeans.jellytools.modules.corba.dialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "New IOR File" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class MergeIORsDialog extends JDialogOperator {

    /** Creates new MergeIORsDialog that can handle it.
     */
    public MergeIORsDialog() {
        super("New IOR File");
    }

    private JLabelOperator _lblIORFileName;
    private JTextFieldOperator _txtIORFileName;
    private JTreeOperator _tree;
    private JLabelOperator _lblPackage;
    private JTextFieldOperator _txtPackage;
    private JButtonOperator _btOK;
    private JButtonOperator _btCancel;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "IOR File Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblIORFileName() {
        if (_lblIORFileName==null) {
            _lblIORFileName = new JLabelOperator(this, "IOR File Name:");
        }
        return _lblIORFileName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtIORFileName() {
        if (_txtIORFileName==null) {
            _txtIORFileName = new JTextFieldOperator(this);
        }
        return _txtIORFileName;
    }

    /** Tries to find null TreeView$ExplorerTree in this dialog.
     * @return JTreeOperator
     */
    public JTreeOperator tree() {
        if (_tree==null) {
            _tree = new JTreeOperator(this);
        }
        return _tree;
    }

    /** Tries to find "Package:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblPackage() {
        if (_lblPackage==null) {
            _lblPackage = new JLabelOperator(this, "Package:");
        }
        return _lblPackage;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtPackage() {
        if (_txtPackage==null) {
            _txtPackage = new JTextFieldOperator(this, 1);
        }
        return _txtPackage;
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

    /** gets text for txtIORFileName
     * @return String text
     */
    public String getIORFileName() {
        return txtIORFileName().getText();
    }

    /** sets text for txtIORFileName
     * @param text String text
     */
    public void setIORFileName(String text) {
        txtIORFileName().setText(text);
    }

    /** types text for txtIORFileName
     * @param text String text
     */
    public void typeIORFileName(String text) {
        txtIORFileName().typeText(text);
    }

    /** gets text for txtPackage
     * @return String text
     */
    public String getPackage() {
        return txtPackage().getText();
    }

    /** sets text for txtPackage
     * @param text String text
     */
    public void setPackage(String text) {
        txtPackage().setText(text);
    }

    /** types text for txtPackage
     * @param text String text
     */
    public void typePackage(String text) {
        txtPackage().typeText(text);
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

    /** Performs verification of MergeIORsDialog by accessing all its components.
     */
    public void verify() {
        lblIORFileName();
        txtIORFileName();
        tree();
        lblPackage();
        txtPackage();
        btOK();
        btCancel();
    }

    /** Performs simple test of MergeIORsDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new MergeIORsDialog().verify();
        System.out.println("MergeIORsDialog verification finished.");
    }
}

