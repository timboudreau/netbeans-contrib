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
 * IRDesignIDLStep.java
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
public class IRDesignIDLStep extends NewWizardOperator {

    /** Creates new IRDesignIDLStep that can handle it.
     */
    public IRDesignIDLStep() {
        stepsWaitSelectedValue ("Design IDL");
    }

    private JLabelOperator _lblInterfaceRepositories;
    private JLabelOperator _lblInterfaceRepositoryId;
    private JTextFieldOperator _txtInterfaceRepositoryId;
    private JTreeOperator _tree;
    private JButtonOperator _btAddIR;
    private JButtonOperator _btRefresh;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Interface Repositories:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblInterfaceRepositories() {
        if (_lblInterfaceRepositories==null) {
            _lblInterfaceRepositories = new JLabelOperator(this, "Interface Repositories:");
        }
        return _lblInterfaceRepositories;
    }

    /** Tries to find "Interface Repository Id:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblInterfaceRepositoryId() {
        if (_lblInterfaceRepositoryId==null) {
            _lblInterfaceRepositoryId = new JLabelOperator(this, "Interface Repository Id:");
        }
        return _lblInterfaceRepositoryId;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtInterfaceRepositoryId() {
        if (_txtInterfaceRepositoryId==null) {
            _txtInterfaceRepositoryId = new JTextFieldOperator(this);
        }
        return _txtInterfaceRepositoryId;
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

    /** Tries to find "Add IR" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btAddIR() {
        if (_btAddIR==null) {
            _btAddIR = new JButtonOperator(this, "Add IR");
        }
        return _btAddIR;
    }

    /** Tries to find "Refresh" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btRefresh() {
        if (_btRefresh==null) {
            _btRefresh = new JButtonOperator(this, "Refresh");
        }
        return _btRefresh;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtInterfaceRepositoryId
     * @return String text
     */
    public String getInterfaceRepositoryId() {
        return txtInterfaceRepositoryId().getText();
    }

    /** sets text for txtInterfaceRepositoryId
     * @param text String text
     */
    public void setInterfaceRepositoryId(String text) {
        txtInterfaceRepositoryId().setText(text);
    }

    /** types text for txtInterfaceRepositoryId
     * @param text String text
     */
    public void typeInterfaceRepositoryId(String text) {
        txtInterfaceRepositoryId().typeText(text);
    }

    /** clicks on "Add IR" JButton
     */
    public void addIR() {
        btAddIR().push();
    }

    /** clicks on "Refresh" JButton
     */
    public void refresh() {
        btRefresh().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of IRDesignIDLStep by accessing all its components.
     */
    public void verify() {
        lblInterfaceRepositories();
        lblInterfaceRepositoryId();
        txtInterfaceRepositoryId();
        tree();
        btAddIR();
        btRefresh();
    }

    /** Performs simple test of IRDesignIDLStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new IRDesignIDLStep().verify();
        System.out.println("IRDesignIDLStep verification finished.");
    }
}

