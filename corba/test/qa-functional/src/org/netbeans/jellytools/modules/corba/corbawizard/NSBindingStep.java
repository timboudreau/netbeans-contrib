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
 * NSBindingStep.java
 *
 * Created on 15.7.02 14:53
 */
package org.netbeans.jellytools.modules.corba.corbawizard;

import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "CORBA Wizard" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class NSBindingStep extends WizardOperator {

    /** Creates new NSBindingStep that can handle it.
     */
    public NSBindingStep() {
        super("CORBA Wizard");
        stepsWaitSelectedValue ("Binding Details");
    }

    private JTreeOperator _treeTree;
    private JButtonOperator _btBindContext;
    private JButtonOperator _btNewContext;
    private JButtonOperator _btRefresh;
    private JLabelOperator _lblNamingContext;
    private JTextFieldOperator _txtNamingContext;
    private JLabelOperator _lblName;
    private JTextFieldOperator _txtName;
    private JLabelOperator _lblKind;
    private JTextFieldOperator _txtKind;
    private JLabelOperator _lblNamingServices;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find null TreeView$ExplorerTree in this dialog.
     * @return JTreeOperator
     */
    public JTreeOperator treeTree() {
        if (_treeTree==null) {
            _treeTree = new JTreeOperator(this);
        }
        return _treeTree;
    }

    /** Tries to find "Bind Context" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btBindContext() {
        if (_btBindContext==null) {
            _btBindContext = new JButtonOperator(this, "Bind Context");
        }
        return _btBindContext;
    }

    /** Tries to find "New Context" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btNewContext() {
        if (_btNewContext==null) {
            _btNewContext = new JButtonOperator(this, "New Context");
        }
        return _btNewContext;
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

    /** Tries to find "Naming Context:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblNamingContext() {
        if (_lblNamingContext==null) {
            _lblNamingContext = new JLabelOperator(this, "Naming Context:");
        }
        return _lblNamingContext;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtNamingContext() {
        if (_txtNamingContext==null) {
            _txtNamingContext = new JTextFieldOperator(this);
        }
        return _txtNamingContext;
    }

    /** Tries to find "Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblName() {
        if (_lblName==null) {
            _lblName = new JLabelOperator(this, "Name:");
        }
        return _lblName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtName() {
        if (_txtName==null) {
            _txtName = new JTextFieldOperator(this, 1);
        }
        return _txtName;
    }

    /** Tries to find "Kind:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblKind() {
        if (_lblKind==null) {
            _lblKind = new JLabelOperator(this, "Kind:");
        }
        return _lblKind;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtKind() {
        if (_txtKind==null) {
            _txtKind = new JTextFieldOperator(this, 2);
        }
        return _txtKind;
    }

    /** Tries to find "Naming Services:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblNamingServices() {
        if (_lblNamingServices==null) {
            _lblNamingServices = new JLabelOperator(this, "Naming Services:");
        }
        return _lblNamingServices;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** clicks on "Bind Context" JButton
     */
    public void bindContext() {
        btBindContext().push();
    }

    /** clicks on "New Context" JButton
     */
    public void newContext() {
        btNewContext().push();
    }

    /** clicks on "Refresh" JButton
     */
    public void refresh() {
        btRefresh().push();
    }

    /** gets text for txtNamingContext
     * @return String text
     */
    public String getNamingContext() {
        return txtNamingContext().getText();
    }

    /** sets text for txtNamingContext
     * @param text String text
     */
    public void setNamingContext(String text) {
        txtNamingContext().setText(text);
    }

    /** types text for txtNamingContext
     * @param text String text
     */
    public void typeNamingContext(String text) {
        txtNamingContext().typeText(text);
    }

    /** gets text for txtName
     * @return String text
     */
    public String getName() {
        return txtName().getText();
    }

    /** sets text for txtName
     * @param text String text
     */
    public void setName(String text) {
        txtName().setText(text);
    }

    /** types text for txtName
     * @param text String text
     */
    public void typeName(String text) {
        txtName().typeText(text);
    }

    /** gets text for txtKind
     * @return String text
     */
    public String getKind() {
        return txtKind().getText();
    }

    /** sets text for txtKind
     * @param text String text
     */
    public void setKind(String text) {
        txtKind().setText(text);
    }

    /** types text for txtKind
     * @param text String text
     */
    public void typeKind(String text) {
        txtKind().typeText(text);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of NSBindingStep by accessing all its components.
     */
    public void verify() {
        treeTree();
        btBindContext();
        btNewContext();
        btRefresh();
        lblNamingContext();
        txtNamingContext();
        lblName();
        txtName();
        lblKind();
        txtKind();
        lblNamingServices();
    }

    /** Performs simple test of NSBindingStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new NSBindingStep().verify();
        System.out.println("NSBindingStep verification finished.");
    }
}

