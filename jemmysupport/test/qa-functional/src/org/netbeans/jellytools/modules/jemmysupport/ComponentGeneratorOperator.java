/*
 * ComponentGeneratorOperator.java
 *
 * Created on 7/11/02 2:47 PM
 */
package org.netbeans.jellytools.modules.jemmysupport;

import org.netbeans.jellytools.actions.Action;
import org.netbeans.jemmy.ComponentIsNotVisibleException;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Jemmy Component Generator" NbDialog.
 *
 * @author as103278
 * @version 1.0
 */
public class ComponentGeneratorOperator extends JDialogOperator {

    /** Creates new ComponentGeneratorOperator that can handle it.
     */
    public ComponentGeneratorOperator() {
        super("Jemmy Component Generator");
    }

    private JTreeOperator _treePackage;
    private JButtonOperator _btStart;
    private JButtonOperator _btStop;
    private JButtonOperator _btClose;
    private JCheckBoxOperator _cbCreateScreenshot;
    private JCheckBoxOperator _cbShowComponentsEditor;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find null TreeView$ExplorerTree in this dialog.
     * @return JTreeOperator
     */
    public JTreeOperator treePackage() {
        if (_treePackage==null) {
            _treePackage = new JTreeOperator(this);
        }
        return _treePackage;
    }

    /** Tries to find "Start" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btStart() {
        if (_btStart==null) {
            _btStart = new JButtonOperator(this, "Start");
        }
        return _btStart;
    }

    /** Tries to find "Stop" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btStop() {
        if (_btStop==null) {
            _btStop = new JButtonOperator(this, "Stop");
        }
        return _btStop;
    }

    /** Tries to find "Close" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btClose() {
        if (_btClose==null) {
            _btClose = new JButtonOperator(this, "Close");
        }
        return _btClose;
    }

    /** Tries to find " Create Screenshot" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbCreateScreenshot() {
        if (_cbCreateScreenshot==null) {
            _cbCreateScreenshot = new JCheckBoxOperator(this, " Create Screenshot");
        }
        return _cbCreateScreenshot;
    }

    /** Tries to find " Show Components Editor" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbShowComponentsEditor() {
        if (_cbShowComponentsEditor==null) {
            _cbShowComponentsEditor = new JCheckBoxOperator(this, " Show Components Editor");
        }
        return _cbShowComponentsEditor;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** clicks on "Start" JButton
     */
    public void start() {
        try {
            btStart().push();
        } catch (JemmyException e) {
            if (!(e.getInnerException() instanceof ComponentIsNotVisibleException))
                throw e;
        }
    }

    /** clicks on "Stop" JButton
     */
    public void stop() {
        try {
            btStop().push();
        } catch (JemmyException e) {
            if (!(e.getInnerException() instanceof ComponentIsNotVisibleException))
                throw e;
        }
    }

    /** clicks on "Close" JButton
     */
    public void close() {
        btClose().push();
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkCreateScreenshot(boolean state) {
        if (cbCreateScreenshot().isSelected()!=state) {
            cbCreateScreenshot().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkShowComponentsEditor(boolean state) {
        if (cbShowComponentsEditor().isSelected()!=state) {
            cbShowComponentsEditor().push();
        }
    }
    
    //*****************************************
    // High-level functionality definition part
    //*****************************************

    public void verifyStatus(String status) {
        new JLabelOperator(this, status);
    }

    /** Performs verification of ComponentGeneratorOperator by accessing all its components.
     */
    public void verify() {
        treePackage();
        btClose();
        cbCreateScreenshot();
        cbShowComponentsEditor();
    }

    public static ComponentGeneratorOperator invoke() {
        new Action("Tools|Jemmy Component Generator", null).performMenu();
        return new ComponentGeneratorOperator();
    }
}

