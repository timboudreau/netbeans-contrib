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
 * AddInterfaceRepositoryDialog.java
 *
 * Created on 15.7.02 13:40
 */
package org.netbeans.jellytools.modules.corba.dialogs;

import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "CORBA Panel" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class AddInterfaceRepositoryDialog extends JDialogOperator {

    /** Creates new AddInterfaceRepositoryDialog that can handle it.
     */
    public AddInterfaceRepositoryDialog() {
        super("CORBA Panel");
    }

    private JLabelOperator _lblName;
    private JTextFieldOperator _txtName;
    private JLabelOperator _lblURL;
    private JTextFieldOperator _txtURL;
    private JLabelOperator _lblIOR;
    private JTextFieldOperator _txtIOR;
    private JButtonOperator _btBrowse;
    private JRadioButtonOperator _rbUseIORFromURL;
    private JRadioButtonOperator _rbUseIOR;
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

    /** Tries to find "URL:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblURL() {
        if (_lblURL==null) {
            _lblURL = new JLabelOperator(this, "URL:");
        }
        return _lblURL;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtURL() {
        if (_txtURL==null) {
            _txtURL = new JTextFieldOperator(this, 1);
        }
        return _txtURL;
    }

    /** Tries to find "IOR:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblIOR() {
        if (_lblIOR==null) {
            _lblIOR = new JLabelOperator(this, "IOR:");
        }
        return _lblIOR;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtIOR() {
        if (_txtIOR==null) {
            _txtIOR = new JTextFieldOperator(this, 2);
        }
        return _txtIOR;
    }

    /** Tries to find "Browse..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btBrowse() {
        if (_btBrowse==null) {
            _btBrowse = new JButtonOperator(this, "Browse...");
        }
        return _btBrowse;
    }

    /** Tries to find "Use IOR from URL" JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbUseIORFromURL() {
        if (_rbUseIORFromURL==null) {
            _rbUseIORFromURL = new JRadioButtonOperator(this, "Use IOR from URL");
        }
        return _rbUseIORFromURL;
    }

    /** Tries to find "Use IOR" JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbUseIOR() {
        if (_rbUseIOR==null) {
            _rbUseIOR = new JRadioButtonOperator(this, "Use IOR", 1);
        }
        return _rbUseIOR;
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

    /** gets text for txtURL
     * @return String text
     */
    public String getURL() {
        return txtURL().getText();
    }

    /** sets text for txtURL
     * @param text String text
     */
    public void setURL(String text) {
        txtURL().setText(text);
    }

    /** types text for txtURL
     * @param text String text
     */
    public void typeURL(String text) {
        txtURL().typeText(text);
    }

    /** gets text for txtIOR
     * @return String text
     */
    public String getIOR() {
        return txtIOR().getText();
    }

    /** sets text for txtIOR
     * @param text String text
     */
    public void setIOR(String text) {
        txtIOR().setText(text);
    }

    /** types text for txtIOR
     * @param text String text
     */
    public void typeIOR(String text) {
        txtIOR().typeText(text);
    }

    /** clicks on "Browse..." JButton
     */
    public void browse() {
        btBrowse().push();
    }

    /** clicks on "Use IOR from URL" JRadioButton
     */
    public void useIORFromURL() {
        rbUseIORFromURL().push();
    }

    /** clicks on "Use IOR" JRadioButton
     */
    public void useIOR() {
        rbUseIOR().push();
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

    public void loadURL (String url) {
        useIORFromURL ();
        setURL (url);
    }
    
    public void loadIOR (String ior) {
        useIOR ();
        setIOR (ior);
    }
    
    /** Performs verification of AddInterfaceRepositoryDialog by accessing all its components.
     */
    public void verify() {
        lblName();
        txtName();
        lblURL();
        txtURL();
        lblIOR();
        txtIOR();
        btBrowse();
        rbUseIORFromURL();
        rbUseIOR();
        btOK();
        btCancel();
    }

    /** Performs simple test of AddInterfaceRepositoryDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new AddInterfaceRepositoryDialog().verify();
        System.out.println("AddInterfaceRepositoryDialog verification finished.");
    }
}

