/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * ProprietaryBindingStep.java
 *
 * Created on 15.7.02 15:17
 */
package org.netbeans.jellytools.modules.corba.corbawizard;

import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "CORBA Wizard" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class ProprietaryBindingStep extends WizardOperator {

    /** Creates new ProprietaryBindingStep that can handle it.
     */
    public ProprietaryBindingStep() {
        super("CORBA Wizard");
        stepsWaitSelectedValue ("Binding Details");
    }

    private JLabelOperator _lblServerName;
    private JTextFieldOperator _txtServerName;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Server name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblServerName() {
        if (_lblServerName==null) {
            _lblServerName = new JLabelOperator(this, "Server name:");
        }
        return _lblServerName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtServerName() {
        if (_txtServerName==null) {
            _txtServerName = new JTextFieldOperator(this);
        }
        return _txtServerName;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtServerName
     * @return String text
     */
    public String getServerName() {
        return txtServerName().getText();
    }

    /** sets text for txtServerName
     * @param text String text
     */
    public void setServerName(String text) {
        txtServerName().setText(text);
    }

    /** types text for txtServerName
     * @param text String text
     */
    public void typeServerName(String text) {
        txtServerName().typeText(text);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of ProprietaryBindingStep by accessing all its components.
     */
    public void verify() {
        lblServerName();
        txtServerName();
    }

    /** Performs simple test of ProprietaryBindingStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new ProprietaryBindingStep().verify();
        System.out.println("ProprietaryBindingStep verification finished.");
    }
}

