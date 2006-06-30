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

package org.netbeans.jellytools.modules.corba.poasupport;

import org.netbeans.jemmy.operators.*;

public class QuestionDialog extends JDialogOperator {

    /** Creates new QuestionDialog that can handle it.
     */
    public QuestionDialog() {
        super("Question");
    }

    private JLabelOperator _lblLabel;
    private JLabelOperator _lblDefaultServantRegisteredWithThePOA;
    private JButtonOperator _btOK;
    private JButtonOperator _btCancel;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Setting the Request Processing policy to the value " JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblLabel() {
        if (_lblLabel==null) {
            _lblLabel = new JLabelOperator(this, "Setting the Request Processing policy to the value ");
        }
        return _lblLabel;
    }

    /** Tries to find "default servant registered with the POA." JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblDefaultServantRegisteredWithThePOA() {
        if (_lblDefaultServantRegisteredWithThePOA==null) {
            _lblDefaultServantRegisteredWithThePOA = new JLabelOperator(this, "default servant registered with the POA.");
        }
        return _lblDefaultServantRegisteredWithThePOA;
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

    /** Performs verification of QuestionDialog by accessing all its components.
     */
    public void verify() {
        lblLabel();
        lblDefaultServantRegisteredWithThePOA();
        btOK();
        btCancel();
    }

    /** Performs simple test of QuestionDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new QuestionDialog().verify();
        System.out.println("QuestionDialog verification finished.");
    }
}

