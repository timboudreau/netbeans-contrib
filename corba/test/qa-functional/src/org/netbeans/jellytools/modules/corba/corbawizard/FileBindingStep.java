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
 * FileBindingStep.java
 *
 * Created on 15.7.02 15:15
 */
package org.netbeans.jellytools.modules.corba.corbawizard;

import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "CORBA Wizard" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class FileBindingStep extends WizardOperator {

    /** Creates new FileBindingStep that can handle it.
     */
    public FileBindingStep() {
        super("CORBA Wizard");
        stepsWaitSelectedValue ("Binding Details");
    }

    private JLabelOperator _lblIORFileName;
    private JTextFieldOperator _txtIORFileName;
    private JButtonOperator _btBrowse;


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

    /** Tries to find "Browse ..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btBrowse() {
        if (_btBrowse==null) {
            _btBrowse = new JButtonOperator(this, "Browse ...");
        }
        return _btBrowse;
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

    /** clicks on "Browse ..." JButton
     */
    public void browse() {
        btBrowse().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of FileBindingStep by accessing all its components.
     */
    public void verify() {
        lblIORFileName();
        txtIORFileName();
        btBrowse();
    }

    /** Performs simple test of FileBindingStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new FileBindingStep().verify();
        System.out.println("FileBindingStep verification finished.");
    }
}

