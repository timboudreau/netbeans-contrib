/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.jellytools.modules.jndi;

import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "New Context" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class NewContextDialog extends NbDialogOperator {

    /** Creates new NewContextDialog that can handle it.
     */
    public NewContextDialog() {
        super("New Context");
    }

    private JLabelOperator _lblContextName;
    private JTextFieldOperator _txtContextName;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Context Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblContextName() {
        if (_lblContextName==null) {
            _lblContextName = new JLabelOperator(this, "Context Name:");
        }
        return _lblContextName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtContextName() {
        if (_txtContextName==null) {
            _txtContextName = new JTextFieldOperator(this);
        }
        return _txtContextName;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtContextName
     * @return String text
     */
    public String getContextName() {
        return txtContextName().getText();
    }

    /** sets text for txtContextName
     * @param text String text
     */
    public void setContextName(String text) {
        txtContextName().setText(text);
    }

    /** types text for txtContextName
     * @param text String text
     */
    public void typeContextName(String text) {
        txtContextName().typeText(text);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of NewContextDialog by accessing all its components.
     */
    public void verify() {
        lblContextName();
        txtContextName();
    }

    /** Performs simple test of NewContextDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new NewContextDialog().verify();
        System.out.println("NewContextDialog verification finished.");
    }
}

