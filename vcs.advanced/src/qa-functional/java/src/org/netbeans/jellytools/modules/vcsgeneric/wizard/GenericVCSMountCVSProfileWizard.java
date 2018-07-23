/*
 * GenericVCSMountCVSProfileWizard.java
 *
 * Created on 1/15/04 1:33 PM
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
public class GenericVCSMountCVSProfileWizard extends WizardOperator {

    /** Creates new GenericVCSMountCVSProfileWizard that can handle it.
     */
    public GenericVCSMountCVSProfileWizard() {
        super("New Wizard - Generic VCS");
    }

    private JLabelOperator _lblProfile;
    private JComboBoxOperator _cboProfileComboBox;
    public static final String ITEM_CVS = "CVS";
    public static final String ITEM_EMPTY = "Empty";
    public static final String ITEM_PVCS = "PVCS";
    public static final String ITEM_VSS = "VSS";
    private JButtonOperator _btSaveAs;
    private JLabelOperator _lblWorkingDirectory;
    private JTextFieldOperator _txtWorkingDirectory;
    private JButtonOperator _btBrowseWorkingDirectory;
    private JLabelOperator _lblRelativeMountPoint;
    private JTextFieldOperator _txtRelativeMountPoint;
    private JButtonOperator _btSelect;
    private JLabelOperator _lblCVSServerType;
    private JComboBoxOperator _cboCVSServerType;
    public static final String ITEM_LOCAL = "local";
    public static final String ITEM_SERVER = "server";
    public static final String ITEM_PSERVER = "pserver";
    public static final String ITEM_EXT = "ext";
    private JLabelOperator _lblCVSServerName;
    private JTextFieldOperator _txtCVSServerName;
    private JLabelOperator _lblPort;
    private JTextFieldOperator _txtPort;
    private JLabelOperator _lblUserName;
    private JTextFieldOperator _txtUserName;
    private JLabelOperator _lblRepositoryPath;
    private JTextFieldOperator _txtRepositoryPath;
    private JButtonOperator _btBrowseRepositoryPath;
    private JRadioButtonOperator _rbUseBuiltInCVSClient;
    private JRadioButtonOperator _rbUseCommandLineCVSClient;
    private JLabelOperator _lblCVSExecutable;
    private JTextFieldOperator _txtCVSExecutable;
    private JButtonOperator _btBrowseCVSExecutable;
    private JLabelOperator _lblRemoteShell;
    private JTextFieldOperator _txtRemoteShell;
    private JRadioButtonOperator _rbLoginToPserverAt;
    private JLabelOperator _lblPassword;
    private JPasswordFieldOperator _txtPassword;
    private JButtonOperator _btLogin;
    private JTextAreaOperator _txtLoginStatus;
    private JRadioButtonOperator _rbSetOfflineMode;
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
    public JButtonOperator btBrowseWorkingDirectory() {
        if (_btBrowseWorkingDirectory==null) {
            _btBrowseWorkingDirectory = new JButtonOperator(this, "Browse...");
        }
        return _btBrowseWorkingDirectory;
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

    /** Tries to find "CVS Server Type:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblCVSServerType() {
        if (_lblCVSServerType==null) {
            _lblCVSServerType = new JLabelOperator(this, "CVS Server Type:");
        }
        return _lblCVSServerType;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboCVSServerType() {
        if (_cboCVSServerType==null) {
            _cboCVSServerType = new JComboBoxOperator(this, 1);
        }
        return _cboCVSServerType;
    }

    /** Tries to find "CVS Server Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblCVSServerName() {
        if (_lblCVSServerName==null) {
            _lblCVSServerName = new JLabelOperator(this, "CVS Server Name:");
        }
        return _lblCVSServerName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtCVSServerName() {
        if (_txtCVSServerName==null) {
            _txtCVSServerName = new JTextFieldOperator(this, 3);
        }
        return _txtCVSServerName;
    }

    /** Tries to find "Port:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblPort() {
        if (_lblPort==null) {
            _lblPort = new JLabelOperator(this, "Port:");
        }
        return _lblPort;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtPort() {
        if (_txtPort==null) {
            _txtPort = new JTextFieldOperator(this, 4);
        }
        return _txtPort;
    }

    /** Tries to find "User Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblUserName() {
        if (_lblUserName==null) {
            _lblUserName = new JLabelOperator(this, "User Name:");
        }
        return _lblUserName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtUserName() {
        if (_txtUserName==null) {
            _txtUserName = new JTextFieldOperator(this, 5);
        }
        return _txtUserName;
    }

    /** Tries to find "Repository Path:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblRepositoryPath() {
        if (_lblRepositoryPath==null) {
            _lblRepositoryPath = new JLabelOperator(this, "Repository Path:");
        }
        return _lblRepositoryPath;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtRepositoryPath() {
        if (_txtRepositoryPath==null) {
            _txtRepositoryPath = new JTextFieldOperator(this, 6);
        }
        return _txtRepositoryPath;
    }

    /** Tries to find "Browse..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btBrowseRepositoryPath() {
        if (_btBrowseRepositoryPath==null) {
            _btBrowseRepositoryPath = new JButtonOperator(this, "Browse...", 1);
        }
        return _btBrowseRepositoryPath;
    }

    /** Tries to find "Use Built-In CVS Client" JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbUseBuiltInCVSClient() {
        if (_rbUseBuiltInCVSClient==null) {
            _rbUseBuiltInCVSClient = new JRadioButtonOperator(this, "Use Built-In CVS Client");
        }
        return _rbUseBuiltInCVSClient;
    }

    /** Tries to find "Use Command-Line CVS Client" JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbUseCommandLineCVSClient() {
        if (_rbUseCommandLineCVSClient==null) {
            _rbUseCommandLineCVSClient = new JRadioButtonOperator(this, "Use Command-Line CVS Client");
        }
        return _rbUseCommandLineCVSClient;
    }

    /** Tries to find "CVS Executable:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblCVSExecutable() {
        if (_lblCVSExecutable==null) {
            _lblCVSExecutable = new JLabelOperator(this, "CVS Executable:");
        }
        return _lblCVSExecutable;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtCVSExecutable() {
        if (_txtCVSExecutable==null) {
            _txtCVSExecutable = new JTextFieldOperator(this, 7);
        }
        return _txtCVSExecutable;
    }

    /** Tries to find "Browse..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btBrowseCVSExecutable() {
        if (_btBrowseCVSExecutable==null) {
            _btBrowseCVSExecutable = new JButtonOperator(this, "Browse...", 2);
        }
        return _btBrowseCVSExecutable;
    }

    /** Tries to find "Remote Shell:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblRemoteShell() {
        if (_lblRemoteShell==null) {
            _lblRemoteShell = new JLabelOperator(this, "Remote Shell:");
        }
        return _lblRemoteShell;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtRemoteShell() {
        if (_txtRemoteShell==null) {
            _txtRemoteShell = new JTextFieldOperator(this, 8);
        }
        return _txtRemoteShell;
    }

    /** Tries to find "Login to Pserver at " JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbLoginToPserverAt() {
        if (_rbLoginToPserverAt==null) {
            _rbLoginToPserverAt = new JRadioButtonOperator(this, "Login to Pserver at ");
        }
        return _rbLoginToPserverAt;
    }

    /** Tries to find "Password:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblPassword() {
        if (_lblPassword==null) {
            _lblPassword = new JLabelOperator(this, "Password:");
        }
        return _lblPassword;
    }

    /** Tries to find null JPasswordField in this dialog.
     * @return JPasswordFieldOperator
     */
    public JPasswordFieldOperator txtPassword() {
        if (_txtPassword==null) {
            _txtPassword = new JPasswordFieldOperator(this);
        }
        return _txtPassword;
    }

    /** Tries to find "Login" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btLogin() {
        if (_btLogin==null) {
            _btLogin = new JButtonOperator(this, "Login");
        }
        return _btLogin;
    }

    /** Tries to find null JTextArea in this dialog.
     * @return JTextAreaOperator
     */
    public JTextAreaOperator txtLoginStatus() {
        if (_txtLoginStatus==null) {
            _txtLoginStatus = new JTextAreaOperator(this);
        }
        return _txtLoginStatus;
    }

    /** Tries to find "Set Offline Mode" JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbSetOfflineMode() {
        if (_rbSetOfflineMode==null) {
            _rbSetOfflineMode = new JRadioButtonOperator(this, "Set Offline Mode");
        }
        return _rbSetOfflineMode;
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
    public void browseWorkingDirectory() {
        btBrowseWorkingDirectory().push();
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

    /** returns selected item for cboCVSServerType
     * @return String item
     */
    public String getSelectedCVSServerType() {
        return cboCVSServerType().getSelectedItem().toString();
    }

    /** selects item for cboCVSServerType
     * @param item String item
     */
    public void selectCVSServerType(String item) {
        cboCVSServerType().selectItem(item);
    }

    /** types text for cboCVSServerType
     * @param text String text
     */
    public void typeCVSServerType(String text) {
        cboCVSServerType().typeText(text);
    }

    /** gets text for txtCVSServerName
     * @return String text
     */
    public String getCVSServerName() {
        return txtCVSServerName().getText();
    }

    /** sets text for txtCVSServerName
     * @param text String text
     */
    public void setCVSServerName(String text) {
        txtCVSServerName().setText(text);
    }

    /** types text for txtCVSServerName
     * @param text String text
     */
    public void typeCVSServerName(String text) {
        txtCVSServerName().typeText(text);
    }

    /** gets text for txtPort
     * @return String text
     */
    public String getPort() {
        return txtPort().getText();
    }

    /** sets text for txtPort
     * @param text String text
     */
    public void setPort(String text) {
        txtPort().setText(text);
    }

    /** types text for txtPort
     * @param text String text
     */
    public void typePort(String text) {
        txtPort().typeText(text);
    }

    /** gets text for txtUserName
     * @return String text
     */
    public String getUserName() {
        return txtUserName().getText();
    }

    /** sets text for txtUserName
     * @param text String text
     */
    public void setUserName(String text) {
        txtUserName().setText(text);
    }

    /** types text for txtUserName
     * @param text String text
     */
    public void typeUserName(String text) {
        txtUserName().typeText(text);
    }

    /** gets text for txtRepositoryPath
     * @return String text
     */
    public String getRepositoryPath() {
        return txtRepositoryPath().getText();
    }

    /** sets text for txtRepositoryPath
     * @param text String text
     */
    public void setRepositoryPath(String text) {
        txtRepositoryPath().setText(text);
    }

    /** types text for txtRepositoryPath
     * @param text String text
     */
    public void typeRepositoryPath(String text) {
        txtRepositoryPath().typeText(text);
    }

    /** clicks on "Browse..." JButton
     */
    public void browseRepositoryPath() {
        btBrowseRepositoryPath().push();
    }

    /** clicks on "Use Built-In CVS Client" JRadioButton
     */
    public void useBuiltInCVSClient() {
        rbUseBuiltInCVSClient().push();
    }

    /** clicks on "Use Command-Line CVS Client" JRadioButton
     */
    public void useCommandLineCVSClient() {
        rbUseCommandLineCVSClient().push();
    }

    /** gets text for txtCVSExecutable
     * @return String text
     */
    public String getCVSExecutable() {
        return txtCVSExecutable().getText();
    }

    /** sets text for txtCVSExecutable
     * @param text String text
     */
    public void setCVSExecutable(String text) {
        txtCVSExecutable().setText(text);
    }

    /** types text for txtCVSExecutable
     * @param text String text
     */
    public void typeCVSExecutable(String text) {
        txtCVSExecutable().typeText(text);
    }

    /** clicks on "Browse..." JButton
     */
    public void browseCVSExecutable() {
        btBrowseCVSExecutable().push();
    }

    /** gets text for txtRemoteShell
     * @return String text
     */
    public String getRemoteShell() {
        return txtRemoteShell().getText();
    }

    /** sets text for txtRemoteShell
     * @param text String text
     */
    public void setRemoteShell(String text) {
        txtRemoteShell().setText(text);
    }

    /** types text for txtRemoteShell
     * @param text String text
     */
    public void typeRemoteShell(String text) {
        txtRemoteShell().typeText(text);
    }

    /** clicks on "Login to Pserver at " JRadioButton
     */
    public void loginToPserverAt() {
        rbLoginToPserverAt().push();
    }

    /** sets text for txtPassword
     * @param text String text
     */
    public void setPassword(String text) {
        txtPassword().setText(text);
    }

    /** types text for txtPassword
     * @param text String text
     */
    public void typePassword(String text) {
        txtPassword().typeText(text);
    }

    /** clicks on "Login" JButton
     */
    public void login() {
        btLogin().push();
    }

    /** gets text for txtLoginStatus
     * @return String text
     */
    public String getLoginStatus() {
        return txtLoginStatus().getText();
    }

    /** sets text for txtLoginStatus
     * @param text String text
     */
    public void setLoginStatus(String text) {
        txtLoginStatus().setText(text);
    }

    /** types text for txtLoginStatus
     * @param text String text
     */
    public void typeLoginStatus(String text) {
        txtLoginStatus().typeText(text);
    }

    /** clicks on "Set Offline Mode" JRadioButton
     */
    public void setOfflineMode() {
        rbSetOfflineMode().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of GenericVCSMountCVSProfileWizard by accessing all its components.
     */
    public void verify() {
        lblProfile();
        cboProfileComboBox();
        btSaveAs();
        lblWorkingDirectory();
        txtWorkingDirectory();
        btBrowseWorkingDirectory();
        lblRelativeMountPoint();
        txtRelativeMountPoint();
        btSelect();
        lblCVSServerType();
        cboCVSServerType();
        lblCVSServerName();
        txtCVSServerName();
        lblPort();
        txtPort();
        lblUserName();
        txtUserName();
        lblRepositoryPath();
        txtRepositoryPath();
        btBrowseRepositoryPath();
        rbUseBuiltInCVSClient();
        rbUseCommandLineCVSClient();
        lblCVSExecutable();
        txtCVSExecutable();
        btBrowseCVSExecutable();
        lblRemoteShell();
        txtRemoteShell();
        rbLoginToPserverAt();
        lblPassword();
        txtPassword();
        btLogin();
        txtLoginStatus();
        rbSetOfflineMode();
        lblToGetAdditionalProfilesVisit();
        lblLink();
        lblJLabel();
    }

    /** Performs simple test of GenericVCSMountCVSProfileWizard
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new GenericVCSMountCVSProfileWizard().verify();
        System.out.println("GenericVCSMountCVSProfileWizard verification finished.");
    }
}

