/*
 * GenericVCSMountWizard.java
 *
 * Created on 1/15/04 1:30 PM
 */
package org.netbeans.jellytools.modules.vcsgeneric.wizard;

import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.NameComponentChooser;

/** Class implementing all necessary methods for handling "New Wizard - Generic VCS" NbDialog.
 *
 * @author dk102396
 * @version 1.0
 */
public class GenericVCSMountWizard extends WizardOperator {

    /** Creates new GenericVCSMountWizard that can handle it.
     */
    public GenericVCSMountWizard() {
        super("New Wizard - Generic VCS");
    }

    private JLabelOperator _lblProfile;
    private JComboBoxOperator _cboProfileComboBox;
    public static final String ITEM_SELECTAPROFILE = "< Select a Profile >";
    public static final String ITEM_CVS = "CVS";
    public static final String ITEM_EMPTY = "Empty";
    public static final String ITEM_PVCS = "PVCS";
    public static final String ITEM_VSS = "VSS";
    private JButtonOperator _btSaveAs;
    private JLabelOperator _lblWorkingDirectory;
    private JTextFieldOperator _txtWorkingDirectory;
    private JButtonOperator _btBrowse;
    private JLabelOperator _lblRelativeMountPoint;
    private JTextFieldOperator _txtRelativeMountPoint;
    private JButtonOperator _btSelect;
    private JLabelOperator _lblToGetAdditionalProfilesVisit;
    private JLabelOperator _lblLink;
    private JLabelOperator _lblJLabel;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Profile" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblProfile() {
        if (_lblProfile==null) {
            _lblProfile = new JLabelOperator(this, "Profile");
        }
        return _lblProfile;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboProfileComboBox() {
        if (_cboProfileComboBox==null) {
            _cboProfileComboBox = new JComboBoxOperator(this);
        }
        return _cboProfileComboBox;
    }

    /** Tries to find "Save As..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btSaveAs() {
        if (_btSaveAs==null) {
            _btSaveAs = new JButtonOperator(this, "Save As...");
        }
        return _btSaveAs;
    }

    /** Tries to find "Working Directory:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblWorkingDirectory() {
        if (_lblWorkingDirectory==null) {
            _lblWorkingDirectory = new JLabelOperator(this, "Working Directory:");
        }
        return _lblWorkingDirectory;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtWorkingDirectory() {
        if (_txtWorkingDirectory==null) {
            _txtWorkingDirectory = new JTextFieldOperator(this);
        }
        return _txtWorkingDirectory;
    }

    /** Tries to find "Browse..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btBrowse() {
        if (_btBrowse==null) {
            _btBrowse = new JButtonOperator(this, "Browse...");
        }
        return _btBrowse;
    }

    /** Tries to find "Relative Mount Point:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblRelativeMountPoint() {
        if (_lblRelativeMountPoint==null) {
            _lblRelativeMountPoint = new JLabelOperator(this, "Relative Mount Point:");
        }
        return _lblRelativeMountPoint;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtRelativeMountPoint() {
        if (_txtRelativeMountPoint==null) {
            _txtRelativeMountPoint = new JTextFieldOperator(this, 1);
        }
        return _txtRelativeMountPoint;
    }

    /** Tries to find "Select..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btSelect() {
        if (_btSelect==null) {
            _btSelect = new JButtonOperator(this, "Select...");
        }
        return _btSelect;
    }

    /** Tries to find "To get additional profiles, visit:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblToGetAdditionalProfilesVisit() {
        if (_lblToGetAdditionalProfilesVisit==null) {
            _lblToGetAdditionalProfilesVisit = new JLabelOperator(this, "To get additional profiles, visit:");
        }
        return _lblToGetAdditionalProfilesVisit;
    }

    /** Tries to find "http://vcsgeneric.netbeans.org/profiles/index.html" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblLink() {
        if (_lblLink==null) {
            _lblLink = new JLabelOperator(this, "http://vcsgeneric.netbeans.org/profiles/index.html");
        }
        return _lblLink;
    }

    /** Tries to find "  " JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblJLabel() {
        if (_lblJLabel==null) {
            _lblJLabel = new JLabelOperator(this, "  ");
        }
        return _lblJLabel;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** returns selected item for cboProfileComboBox
     * @return String item
     */
    public String getSelectedProfileComboBox() {
        return cboProfileComboBox().getSelectedItem().toString();
    }

    /** selects item for cboProfileComboBox
     * @param item String item
     */
    public void selectProfileComboBox(String item) {
        cboProfileComboBox().selectItem(item);
    }

    /** clicks on "Save As..." JButton
     */
    public void saveAs() {
        btSaveAs().push();
    }

    /** gets text for txtWorkingDirectory
     * @return String text
     */
    public String getWorkingDirectory() {
        return txtWorkingDirectory().getText();
    }

    /** sets text for txtWorkingDirectory
     * @param text String text
     */
    public void setWorkingDirectory(String text) {
        txtWorkingDirectory().setText(text);
    }

    /** types text for txtWorkingDirectory
     * @param text String text
     */
    public void typeWorkingDirectory(String text) {
        txtWorkingDirectory().typeText(text);
    }

    /** clicks on "Browse..." JButton
     */
    public void browse() {
        btBrowse().push();
    }

    /** gets text for txtRelativeMountPoint
     * @return String text
     */
    public String getRelativeMountPoint() {
        return txtRelativeMountPoint().getText();
    }

    /** sets text for txtRelativeMountPoint
     * @param text String text
     */
    public void setRelativeMountPoint(String text) {
        txtRelativeMountPoint().setText(text);
    }

    /** types text for txtRelativeMountPoint
     * @param text String text
     */
    public void typeRelativeMountPoint(String text) {
        txtRelativeMountPoint().typeText(text);
    }

    /** clicks on "Select..." JButton
     */
    public void select() {
        btSelect().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of GenericVCSMountWizard by accessing all its components.
     */
    public void verify() {
        lblProfile();
        cboProfileComboBox();
        btSaveAs();
        lblWorkingDirectory();
        txtWorkingDirectory();
        btBrowse();
        lblRelativeMountPoint();
        txtRelativeMountPoint();
        btSelect();
        lblToGetAdditionalProfilesVisit();
        lblLink();
        lblJLabel();
    }

    /** Performs simple test of GenericVCSMountWizard
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new GenericVCSMountWizard().verify();
        System.out.println("GenericVCSMountWizard verification finished.");
    }
}

