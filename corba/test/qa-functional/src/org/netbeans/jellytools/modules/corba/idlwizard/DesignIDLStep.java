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
 * DesignIDLStep.java
 *
 * Created on 15.7.02 13:52
 */
package org.netbeans.jellytools.modules.corba.idlwizard;

import org.netbeans.jellytools.NewWizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "New Wizard - Empty" NbDialog.
 *
 * @author dave
 * @version 1.0
 */
public class DesignIDLStep extends NewWizardOperator {

    /** Creates new DesignIDLStep that can handle it.
     */
    public DesignIDLStep() {
        stepsWaitSelectedValue ("Design IDL");
    }

    private JTreeOperator _tree;
    private JComboBoxOperator _cboIDLTypes;
    public static final String ITEM_ALIAS = "Alias"; 
    public static final String ITEM_CONSTANT = "Constant"; 
    public static final String ITEM_ENUM = "Enum"; 
    public static final String ITEM_EXCEPTION = "Exception"; 
    public static final String ITEM_FORWARDDECLARATION = "Forward Declaration"; 
    public static final String ITEM_INTERFACE = "Interface"; 
    public static final String ITEM_MODULE = "Module"; 
    public static final String ITEM_STRUCTURE = "Structure"; 
    public static final String ITEM_UNION = "Union"; 
    public static final String ITEM_VALUEBOX = "ValueBox"; 
    public static final String ITEM_VALUETYPE = "Valuetype"; 
    private JButtonOperator _btCreate;
    private JButtonOperator _btEdit;
    private JButtonOperator _btUp;
    private JButtonOperator _btDown;
    private JButtonOperator _btRemove;
    private JLabelOperator _lblIDLFile;


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

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboIDLTypes() {
        if (_cboIDLTypes==null) {
            _cboIDLTypes = new JComboBoxOperator(this);
        }
        return _cboIDLTypes;
    }

    /** Tries to find "Create..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btCreate() {
        if (_btCreate==null) {
            _btCreate = new JButtonOperator(this, "Create...");
        }
        return _btCreate;
    }

    /** Tries to find "Edit..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btEdit() {
        if (_btEdit==null) {
            _btEdit = new JButtonOperator(this, "Edit...");
        }
        return _btEdit;
    }

    /** Tries to find "Up" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btUp() {
        if (_btUp==null) {
            _btUp = new JButtonOperator(this, "Up");
        }
        return _btUp;
    }

    /** Tries to find "Down" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btDown() {
        if (_btDown==null) {
            _btDown = new JButtonOperator(this, "Down");
        }
        return _btDown;
    }

    /** Tries to find "Remove..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btRemove() {
        if (_btRemove==null) {
            _btRemove = new JButtonOperator(this, "Remove...");
        }
        return _btRemove;
    }

    /** Tries to find "IDL File:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblIDLFile() {
        if (_lblIDLFile==null) {
            _lblIDLFile = new JLabelOperator(this, "IDL File:");
        }
        return _lblIDLFile;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** returns selected item for cboIDLTypes
     * @return String item
     */
    public String getSelectedIDLTypes() {
        return cboIDLTypes().getSelectedItem().toString();
    }

    /** selects item for cboIDLTypes
     * @param item String item
     */
    public void selectIDLTypes(String item) {
        cboIDLTypes().selectItem(item);
    }

    /** types text for cboIDLTypes
     * @param text String text
     */
    public void typeIDLTypes(String text) {
        cboIDLTypes().typeText(text);
    }

    /** clicks on "Create..." JButton
     */
    public void create() {
        btCreate().push();
    }

    /** clicks on "Edit..." JButton
     */
    public void edit() {
        btEdit().push();
    }

    /** clicks on "Up" JButton
     */
    public void up() {
        btUp().push();
    }

    /** clicks on "Down" JButton
     */
    public void down() {
        btDown().push();
    }

    /** clicks on "Remove..." JButton
     */
    public void remove() {
        btRemove().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of DesignIDLStep by accessing all its components.
     */
    public void verify() {
        tree();
        cboIDLTypes();
        btCreate();
        btEdit();
        btUp();
        btDown();
        btRemove();
        lblIDLFile();
    }

    /** Performs simple test of DesignIDLStep
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new DesignIDLStep().verify();
        System.out.println("DesignIDLStep verification finished.");
    }
}

