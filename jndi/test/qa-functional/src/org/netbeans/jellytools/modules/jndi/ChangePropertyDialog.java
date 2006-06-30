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

package org.netbeans.jellytools.modules.jndi;

import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Change Property" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class ChangePropertyDialog extends NbDialogOperator {

    /** Creates new ChangePropertyDialog that can handle it.
     */
    public ChangePropertyDialog() {
        super("Change Property");
    }

    private JLabelOperator _lblPropertyName;
    private JTextFieldOperator _txtPropertyName;
    private JLabelOperator _lblPropertyValue;
    private JTextFieldOperator _txtPropertyValue;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Property Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblPropertyName() {
        if (_lblPropertyName==null) {
            _lblPropertyName = new JLabelOperator(this, "Property Name:");
        }
        return _lblPropertyName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtPropertyName() {
        if (_txtPropertyName==null) {
            _txtPropertyName = new JTextFieldOperator(this);
        }
        return _txtPropertyName;
    }

    /** Tries to find "Property Value:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblPropertyValue() {
        if (_lblPropertyValue==null) {
            _lblPropertyValue = new JLabelOperator(this, "Property Value:");
        }
        return _lblPropertyValue;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtPropertyValue() {
        if (_txtPropertyValue==null) {
            _txtPropertyValue = new JTextFieldOperator(this, 1);
        }
        return _txtPropertyValue;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtPropertyName
     * @return String text
     */
    public String getPropertyName() {
        return txtPropertyName().getText();
    }

    /** sets text for txtPropertyName
     * @param text String text
     */
    public void setPropertyName(String text) {
        txtPropertyName().setText(text);
    }

    /** types text for txtPropertyName
     * @param text String text
     */
    public void typePropertyName(String text) {
        txtPropertyName().typeText(text);
    }

    /** gets text for txtPropertyValue
     * @return String text
     */
    public String getPropertyValue() {
        return txtPropertyValue().getText();
    }

    /** sets text for txtPropertyValue
     * @param text String text
     */
    public void setPropertyValue(String text) {
        txtPropertyValue().setText(text);
    }

    /** types text for txtPropertyValue
     * @param text String text
     */
    public void typePropertyValue(String text) {
        txtPropertyValue().typeText(text);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of ChangePropertyDialog by accessing all its components.
     */
    public void verify() {
        lblPropertyName();
        txtPropertyName();
        lblPropertyValue();
        txtPropertyValue();
    }

    /** Performs simple test of ChangePropertyDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new ChangePropertyDialog().verify();
        System.out.println("ChangePropertyDialog verification finished.");
    }
}

