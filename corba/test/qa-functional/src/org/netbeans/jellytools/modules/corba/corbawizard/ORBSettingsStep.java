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
 * ORBSettingsStep.java
 *
 * Created on 15.7.02 14:50
 */
package org.netbeans.jellytools.modules.corba.corbawizard;

import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "CORBA Wizard" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class ORBSettingsStep extends WizardOperator {
    
    public static final String STEP_NAME = "ORB Settings";

    /** Creates new ORBSettingsStep that can handle it.
     */
    public ORBSettingsStep() {
        super("CORBA Wizard");
        stepsWaitSelectedValue (STEP_NAME);
    }

    private JLabelOperator _lblChooseORBImplementation;
    private JLabelOperator _lblChooseBindingMethod;
    private JComboBoxOperator _cboChooseORBImplementation;
    public static final String ITEM_J2EEORB = "J2EE ORB"; 
    public static final String ITEM_JDK13ORB = "JDK 1.3 ORB"; 
    public static final String ITEM_JDK14ORB = "JDK 1.4 ORB"; 
    public static final String ITEM_ORBACUSFORJAVA4X = "ORBacus for Java 4.x"; 
    public static final String ITEM_ORBACUSFORJAVA4XFORWINDOWS = "ORBacus for Java 4.x for Windows"; 
    public static final String ITEM_ORBIX20001XFORJAVA = "Orbix 2000 1.x for Java"; 
    public static final String ITEM_ORBIXWEB32 = "OrbixWeb 3.2"; 
    public static final String ITEM_VISIBROKER34FORJAVA = "VisiBroker 3.4 for Java"; 
    public static final String ITEM_VISIBROKER4XFORJAVA = "VisiBroker 4.x for Java"; 
    public static final String ITEM_EORB1XUNSUPPORTED = "eORB 1.x (unsupported)"; 
    public static final String ITEM_JACORB13XUNSUPPORTED = "JacORB 1.3.x (unsupported)"; 
    public static final String ITEM_JAVAORB22XUNSUPPORTED = "JavaORB 2.2.x (unsupported)"; 
    public static final String ITEM_JDK12ORBUNSUPPORTED = "JDK 1.2 ORB (unsupported)"; 
    public static final String ITEM_OPENORB1XUNSUPPORTED = "OpenORB 1.x (unsupported)"; 
    public static final String ITEM_ORBACUSFORJAVA3XUNSUPPORTED = "ORBacus for Java 3.x (unsupported)"; 
    public static final String ITEM_ORBACUSFORJAVA3XFORWINDOWSUNSUPPORTED = "ORBacus for Java 3.x for Windows (unsupported)"; 
    private JComboBoxOperator _cboChooseBindingMethod;
    public static final String ITEM_NAMINGSERVICE = "Naming Service"; 
    public static final String ITEM_IORTOFILE = "IOR to file"; 
    public static final String ITEM_IORTOSTANDARDOUTPUT = "IOR to standard output"; 
    public static final String ITEM_PROPRIETARYBINDER = "Proprietary Binder"; 


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Choose ORB implementation:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblChooseORBImplementation() {
        if (_lblChooseORBImplementation==null) {
            _lblChooseORBImplementation = new JLabelOperator(this, "Choose ORB implementation:");
        }
        return _lblChooseORBImplementation;
    }

    /** Tries to find "Choose Binding Method:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblChooseBindingMethod() {
        if (_lblChooseBindingMethod==null) {
            _lblChooseBindingMethod = new JLabelOperator(this, "Choose Binding Method:");
        }
        return _lblChooseBindingMethod;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboChooseORBImplementation() {
        if (_cboChooseORBImplementation==null) {
            _cboChooseORBImplementation = new JComboBoxOperator(this);
        }
        return _cboChooseORBImplementation;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboChooseBindingMethod() {
        if (_cboChooseBindingMethod==null) {
            _cboChooseBindingMethod = new JComboBoxOperator(this, 1);
        }
        return _cboChooseBindingMethod;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** returns selected item for cboChooseORBImplementation
     * @return String item
     */
    public String getSelectedChooseORBImplementation() {
        return cboChooseORBImplementation().getSelectedItem().toString();
    }

    /** selects item for cboChooseORBImplementation
     * @param item String item
     */
    public void selectChooseORBImplementation(String item) {
        cboChooseORBImplementation().selectItem(item);
    }

    /** types text for cboChooseORBImplementation
     * @param text String text
     */
    public void typeChooseORBImplementation(String text) {
        cboChooseORBImplementation().typeText(text);
    }

    /** returns selected item for cboChooseBindingMethod
     * @return String item
     */
    public String getSelectedChooseBindingMethod() {
        return cboChooseBindingMethod().getSelectedItem().toString();
    }

    /** selects item for cboChooseBindingMethod
     * @param item String item
     */
    public void selectChooseBindingMethod(String item) {
        cboChooseBindingMethod().selectItem(item);
    }

    /** types text for cboChooseBindingMethod
     * @param text String text
     */
    public void typeChooseBindingMethod(String text) {
        cboChooseBindingMethod().typeText(text);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of ORBSettingsStep by accessing all its components.
     */
    public void verify() {
        lblChooseORBImplementation();
        lblChooseBindingMethod();
        cboChooseORBImplementation();
        cboChooseBindingMethod();
    }

    /** Performs simple test of ORBSettingsStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new ORBSettingsStep().verify();
        System.out.println("ORBSettingsStep verification finished.");
    }
}

