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
 * RootInterfacesStep.java
 *
 * Created on 15.7.02 14:51
 */
package org.netbeans.jellytools.modules.corba.corbawizard;

import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "CORBA Wizard" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class RootInterfacesStep extends WizardOperator {

    /** Creates new RootInterfacesStep that can handle it.
     */
    public RootInterfacesStep() {
        super("CORBA Wizard");
        stepsWaitSelectedValue ("Root Interface(s)");
    }

    private JListOperator _lstAvailableInterfaces;
    private JLabelOperator _lblSelectedServerInterface;
    private JTextFieldOperator _txtSelectedServerInterface;
    private JLabelOperator _lblAvailableInterfaces;
    private JLabelOperator _lblAvailableCallBackInterfaces;
    private JListOperator _lstAvailableCallBackInterfaces;
    private JLabelOperator _lblSelectedCallBackInterface;
    private JTextFieldOperator _txtSelectedCallBackInterface;


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

    /** Tries to find "Available Call Back Interfaces:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblAvailableCallBackInterfaces() {
        if (_lblAvailableCallBackInterfaces==null) {
            _lblAvailableCallBackInterfaces = new JLabelOperator(this, "Available Call Back Interfaces:");
        }
        return _lblAvailableCallBackInterfaces;
    }

    /** Tries to find null JList in this dialog.
     * @return JListOperator
     */
    public JListOperator lstAvailableCallBackInterfaces() {
        if (_lstAvailableCallBackInterfaces==null) {
            _lstAvailableCallBackInterfaces = new JListOperator(this, 2);
        }
        return _lstAvailableCallBackInterfaces;
    }

    /** Tries to find "Selected Call-back Interface:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblSelectedCallBackInterface() {
        if (_lblSelectedCallBackInterface==null) {
            _lblSelectedCallBackInterface = new JLabelOperator(this, "Selected Call-back Interface:");
        }
        return _lblSelectedCallBackInterface;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtSelectedCallBackInterface() {
        if (_txtSelectedCallBackInterface==null) {
            _txtSelectedCallBackInterface = new JTextFieldOperator(this, 1);
        }
        return _txtSelectedCallBackInterface;
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

    /** gets text for txtSelectedCallBackInterface
     * @return String text
     */
    public String getSelectedCallBackInterface() {
        return txtSelectedCallBackInterface().getText();
    }

    /** sets text for txtSelectedCallBackInterface
     * @param text String text
     */
    public void setSelectedCallBackInterface(String text) {
        txtSelectedCallBackInterface().setText(text);
    }

    /** types text for txtSelectedCallBackInterface
     * @param text String text
     */
    public void typeSelectedCallBackInterface(String text) {
        txtSelectedCallBackInterface().typeText(text);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of RootInterfacesStep by accessing all its components.
     */
    public void verify() {
        lstAvailableInterfaces();
        lblSelectedServerInterface();
        txtSelectedServerInterface();
        lblAvailableInterfaces();
        lblAvailableCallBackInterfaces();
        lstAvailableCallBackInterfaces();
        lblSelectedCallBackInterface();
        txtSelectedCallBackInterface();
    }

    /** Performs simple test of RootInterfacesStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new RootInterfacesStep().verify();
        System.out.println("RootInterfacesStep verification finished.");
    }
}

