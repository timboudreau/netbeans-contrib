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
 * FinishIDLStep.java
 *
 * Created on 15.7.02 13:53
 */
package org.netbeans.jellytools.modules.corba.idlwizard;

import org.netbeans.jellytools.NewWizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "New Wizard - Empty" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class FinishIDLStep extends NewWizardOperator {

    /** Creates new FinishIDLStep that can handle it.
     */
    public FinishIDLStep() {
        stepsWaitSelectedValue ("Finish IDL");
    }

    private JCheckBoxOperator _cbContinueWithCORBAWizard;
    private JTextAreaOperator _txtJTextArea;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Continue with CORBA Wizard" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbContinueWithCORBAWizard() {
        if (_cbContinueWithCORBAWizard==null) {
            _cbContinueWithCORBAWizard = new JCheckBoxOperator(this, "Continue with CORBA Wizard");
        }
        return _cbContinueWithCORBAWizard;
    }

    /** Tries to find null JTextArea in this dialog.
     * @return JTextAreaOperator
     */
    public JTextAreaOperator txtJTextArea() {
        if (_txtJTextArea==null) {
            _txtJTextArea = new JTextAreaOperator(this);
        }
        return _txtJTextArea;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkContinueWithCORBAWizard(boolean state) {
        if (cbContinueWithCORBAWizard().isSelected()!=state) {
            cbContinueWithCORBAWizard().push();
        }
    }

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


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of FinishIDLStep by accessing all its components.
     */
    public void verify() {
        cbContinueWithCORBAWizard();
        txtJTextArea();
    }

    /** Performs simple test of FinishIDLStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new FinishIDLStep().verify();
        System.out.println("FinishIDLStep verification finished.");
    }
}

