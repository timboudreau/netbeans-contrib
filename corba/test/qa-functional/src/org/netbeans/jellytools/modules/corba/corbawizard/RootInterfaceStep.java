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
 * RootInterfaceStep.java
 *
 * Created on 15.7.02 14:52
 */
package org.netbeans.jellytools.modules.corba.corbawizard;

import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "CORBA Wizard" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class RootInterfaceStep extends WizardOperator {

    /** Creates new RootInterfaceStep that can handle it.
     */
    public RootInterfaceStep() {
        super("CORBA Wizard");
        stepsWaitSelectedValue ("Root Interface(s)");
    }

    private JListOperator _lstAvailableInterfaces;
    public static final String ITEM_SIMPLE = "Simple"; 
    private JLabelOperator _lblSelectedServerInterface;
    private JTextFieldOperator _txtSelectedServerInterface;
    private JLabelOperator _lblAvailableInterfaces;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find null JList in this dialog.
     * @return JListOperator
     */
    public JListOperator lstAvailableInterfaces() {
        if (_lstAvailableInterfaces==null) {
            _lstAvailableInterfaces = new JListOperator(this, 1);
        }
        return _lstAvailableInterfaces;
    }

    /** Tries to find "Selected Server Interface:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblSelectedServerInterface() {
        if (_lblSelectedServerInterface==null) {
            _lblSelectedServerInterface = new JLabelOperator(this, "Selected Server Interface:");
        }
        return _lblSelectedServerInterface;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtSelectedServerInterface() {
        if (_txtSelectedServerInterface==null) {
            _txtSelectedServerInterface = new JTextFieldOperator(this);
        }
        return _txtSelectedServerInterface;
    }

    /** Tries to find "Available Interfaces:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblAvailableInterfaces() {
        if (_lblAvailableInterfaces==null) {
            _lblAvailableInterfaces = new JLabelOperator(this, "Available Interfaces:");
        }
        return _lblAvailableInterfaces;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtSelectedServerInterface
     * @return String text
     */
    public String getSelectedServerInterface() {
        return txtSelectedServerInterface().getText();
    }

    /** sets text for txtSelectedServerInterface
     * @param text String text
     */
    public void setSelectedServerInterface(String text) {
        txtSelectedServerInterface().setText(text);
    }

    /** types text for txtSelectedServerInterface
     * @param text String text
     */
    public void typeSelectedServerInterface(String text) {
        txtSelectedServerInterface().typeText(text);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of RootInterfaceStep by accessing all its components.
     */
    public void verify() {
        lstAvailableInterfaces();
        lblSelectedServerInterface();
        txtSelectedServerInterface();
        lblAvailableInterfaces();
    }

    /** Performs simple test of RootInterfaceStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new RootInterfaceStep().verify();
        System.out.println("RootInterfaceStep verification finished.");
    }
}

