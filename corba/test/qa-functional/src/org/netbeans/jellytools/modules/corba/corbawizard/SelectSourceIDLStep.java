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
 * SelectSourceIDLStep.java
 *
 * Created on 15.7.02 14:41
 */
package org.netbeans.jellytools.modules.corba.corbawizard;

import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "CORBA Wizard" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class SelectSourceIDLStep extends WizardOperator {

    /** Creates new SelectSourceIDLStep that can handle it.
     */
    public SelectSourceIDLStep() {
        super("CORBA Wizard");
        stepsWaitSelectedValue ("Source");
    }

    private JTreeOperator _tree;
    private JLabelOperator _lblIDLFileName;
    private JTextFieldOperator _txtIDLFileName;
    private JLabelOperator _lblPleaseSelectIDLFileToImplement;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find null TreeView$ExplorerTree in this dialog.
     * @return JTreeOperator
     */
    public JTreeOperator tree() {
        if (_tree==null) {
            _tree = new JTreeOperator(this);
        }
        return _tree;
    }

    /** Tries to find "IDL File Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblIDLFileName() {
        if (_lblIDLFileName==null) {
            _lblIDLFileName = new JLabelOperator(this, "IDL File Name:");
        }
        return _lblIDLFileName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtIDLFileName() {
        if (_txtIDLFileName==null) {
            _txtIDLFileName = new JTextFieldOperator(this);
        }
        return _txtIDLFileName;
    }

    /** Tries to find "Please select IDL file to implement:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblPleaseSelectIDLFileToImplement() {
        if (_lblPleaseSelectIDLFileToImplement==null) {
            _lblPleaseSelectIDLFileToImplement = new JLabelOperator(this, "Please select IDL file to implement:");
        }
        return _lblPleaseSelectIDLFileToImplement;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtIDLFileName
     * @return String text
     */
    public String getIDLFileName() {
        return txtIDLFileName().getText();
    }

    /** sets text for txtIDLFileName
     * @param text String text
     */
    public void setIDLFileName(String text) {
        txtIDLFileName().setText(text);
    }

    /** types text for txtIDLFileName
     * @param text String text
     */
    public void typeIDLFileName(String text) {
        txtIDLFileName().typeText(text);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of SelectSourceIDLStep by accessing all its components.
     */
    public void verify() {
        tree();
        lblIDLFileName();
        txtIDLFileName();
        lblPleaseSelectIDLFileToImplement();
    }

    /** Performs simple test of SelectSourceIDLStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new SelectSourceIDLStep().verify();
        System.out.println("SelectSourceIDLStep verification finished.");
    }
}

