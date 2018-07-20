/*
 * VCSFilesystemCustomizerDialog.java
 *
 * Created on 1/15/04 2:45 PM
 */

package org.netbeans.jellytools.modules.vcsgeneric;

import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.NameComponentChooser;

/** Class implementing all necessary methods for handling "Customizer Dialog" NbDialog.
 *
 * @author dk102396
 * @version 1.0
 */
public class VCSFilesystemCustomizerDialog extends NbDialogOperator {

    /** Creates new VCSFilesystemCustomizerDialog that can handle it.
     */
    public VCSFilesystemCustomizerDialog() {
        super("Customizer Dialog");
    }

    private JTabbedPaneOperator _tbpCustomizerTabbedPane;
    private String _selectPageProfile = "Profile";
    private JComboBoxOperator _cboProfileComboBox;
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
    private JButtonOperator _btBrowse2;
    private JRadioButtonOperator _rbUseBuiltInCVSClient;
    private JRadioButtonOperator _rbUseCommandLineCVSClient;
    private JLabelOperator _lblCVSExecutable;
    private JTextFieldOperator _txtCVSExecutable;
    private JButtonOperator _btBrowse3;
    private JLabelOperator _lblRemoteShell;
    private JTextFieldOperator _txtRemoteShell;
    private JRadioButtonOperator _rbLoginToPserverAt;
    private JLabelOperator _lblPassword;
    private JPasswordFieldOperator _txtPassword;
    private JButtonOperator _btLogin;
    private JTextAreaOperator _txtJTextArea;
    private JRadioButtonOperator _rbSetOfflineMode;
    private JLabelOperator _lblToGetAdditionalProfilesVisit;
    private JLabelOperator _lblHttpVcsgenericNetbeansOrgProfilesIndexHtml;
    private String _selectPageAdvanced = "Advanced";
    private JLabelOperator _lblModes;
    private JCheckBoxOperator _cbAdvancedMode;
    private JCheckBoxOperator _cbOfflineMode;
    private JLabelOperator _lblActions;
    private JCheckBoxOperator _cbCallEDITCommandWhenEditingReadOnlyFiles;
    private JCheckBoxOperator _cbPromptForEDITCommandExecution;
    private JLabelOperator _lblMessage;
    private JTextFieldOperator _txtMessage;
    private JCheckBoxOperator _cbLockFilesWhenEditingCallLOCKCommand;
    private JCheckBoxOperator _cbPromptForLOCKCommandExecution;
    private JLabelOperator _lblMessage2;
    private JTextFieldOperator _txtMessage2;
    private JLabelOperator _lblOther;
    private JCheckBoxOperator _cbPrintCommandOutput;
    private JLabelOperator _lblCompatibleWithOS;
    private JTextFieldOperator _txtCompatibleWithOS;
    private JLabelOperator _lblIncompatibleWithOS;
    private JTextFieldOperator _txtIncompatibleWithOS;
    private JLabelOperator _lblYourCurrentOperatingSystemIsWindows2000;
    private JButtonOperator _btEditCommands;
    private JButtonOperator _btEditVariables;
    private String _selectPageEnvironment = "Environment";
    private JLabelOperator _lblUserDefinedEnvironmentVariables;
    private JTableOperator _tabUserDefinedEnvironmentVariables;
    private JButtonOperator _btInsert;
    private JButtonOperator _btDelete;
    private JLabelOperator _lblSystemEnvironmentVariables;
    private JTableOperator _tabSystemEnvironmentVariables;
    private JButtonOperator _btWindowsScrollBarUI$WindowsArrowButton;
    private JButtonOperator _btWindowsScrollBarUI$WindowsArrowButton2;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find null JTabbedPane in this dialog.
     * @return JTabbedPaneOperator
     */
    public JTabbedPaneOperator tbpCustomizerTabbedPane() {
        if (_tbpCustomizerTabbedPane==null) {
            _tbpCustomizerTabbedPane = new JTabbedPaneOperator(this);
        }
        return _tbpCustomizerTabbedPane;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboProfileComboBox() {
        if (_cboProfileComboBox==null) {
            _cboProfileComboBox = new JComboBoxOperator(selectPageProfile());
        }
        selectPageProfile();
        return _cboProfileComboBox;
    }

    /** Tries to find "Save As..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btSaveAs() {
        if (_btSaveAs==null) {
            _btSaveAs = new JButtonOperator(selectPageProfile(), "Save As...");
        }
        selectPageProfile();
        return _btSaveAs;
    }

    /** Tries to find "Working Directory:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblWorkingDirectory() {
        if (_lblWorkingDirectory==null) {
            _lblWorkingDirectory = new JLabelOperator(selectPageProfile(), "Working Directory:");
        }
        selectPageProfile();
        return _lblWorkingDirectory;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtWorkingDirectory() {
        if (_txtWorkingDirectory==null) {
            _txtWorkingDirectory = new JTextFieldOperator(selectPageProfile());
        }
        selectPageProfile();
        return _txtWorkingDirectory;
    }

    /** Tries to find "Browse..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btBrowse() {
        if (_btBrowse==null) {
            _btBrowse = new JButtonOperator(selectPageProfile(), "Browse...");
        }
        selectPageProfile();
        return _btBrowse;
    }

    /** Tries to find "Relative Mount Point:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblRelativeMountPoint() {
        if (_lblRelativeMountPoint==null) {
            _lblRelativeMountPoint = new JLabelOperator(selectPageProfile(), "Relative Mount Point:");
        }
        selectPageProfile();
        return _lblRelativeMountPoint;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtRelativeMountPoint() {
        if (_txtRelativeMountPoint==null) {
            _txtRelativeMountPoint = new JTextFieldOperator(selectPageProfile(), 1);
        }
        selectPageProfile();
        return _txtRelativeMountPoint;
    }

    /** Tries to find "Select..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btSelect() {
        if (_btSelect==null) {
            _btSelect = new JButtonOperator(selectPageProfile(), "Select...");
        }
        selectPageProfile();
        return _btSelect;
    }

    /** Tries to find "CVS Server Type:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblCVSServerType() {
        if (_lblCVSServerType==null) {
            _lblCVSServerType = new JLabelOperator(selectPageProfile(), "CVS Server Type:");
        }
        selectPageProfile();
        return _lblCVSServerType;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboCVSServerType() {
        if (_cboCVSServerType==null) {
            _cboCVSServerType = new JComboBoxOperator(selectPageProfile(), 1);
        }
        selectPageProfile();
        return _cboCVSServerType;
    }

    /** Tries to find "CVS Server Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblCVSServerName() {
        if (_lblCVSServerName==null) {
            _lblCVSServerName = new JLabelOperator(selectPageProfile(), "CVS Server Name:");
        }
        selectPageProfile();
        return _lblCVSServerName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtCVSServerName() {
        if (_txtCVSServerName==null) {
            _txtCVSServerName = new JTextFieldOperator(selectPageProfile(), 3);
        }
        selectPageProfile();
        return _txtCVSServerName;
    }

    /** Tries to find "Port:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblPort() {
        if (_lblPort==null) {
            _lblPort = new JLabelOperator(selectPageProfile(), "Port:");
        }
        selectPageProfile();
        return _lblPort;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtPort() {
        if (_txtPort==null) {
            _txtPort = new JTextFieldOperator(selectPageProfile(), 4);
        }
        selectPageProfile();
        return _txtPort;
    }

    /** Tries to find "User Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblUserName() {
        if (_lblUserName==null) {
            _lblUserName = new JLabelOperator(selectPageProfile(), "User Name:");
        }
        selectPageProfile();
        return _lblUserName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtUserName() {
        if (_txtUserName==null) {
            _txtUserName = new JTextFieldOperator(selectPageProfile(), 5);
        }
        selectPageProfile();
        return _txtUserName;
    }

    /** Tries to find "Repository Path:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblRepositoryPath() {
        if (_lblRepositoryPath==null) {
            _lblRepositoryPath = new JLabelOperator(selectPageProfile(), "Repository Path:");
        }
        selectPageProfile();
        return _lblRepositoryPath;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtRepositoryPath() {
        if (_txtRepositoryPath==null) {
            _txtRepositoryPath = new JTextFieldOperator(selectPageProfile(), 6);
        }
        selectPageProfile();
        return _txtRepositoryPath;
    }

    /** Tries to find "Browse..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btBrowse2() {
        if (_btBrowse2==null) {
            _btBrowse2 = new JButtonOperator(selectPageProfile(), "Browse...", 1);
        }
        selectPageProfile();
        return _btBrowse2;
    }

    /** Tries to find "Use Built-In CVS Client" JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbUseBuiltInCVSClient() {
        if (_rbUseBuiltInCVSClient==null) {
            _rbUseBuiltInCVSClient = new JRadioButtonOperator(selectPageProfile(), "Use Built-In CVS Client");
        }
        selectPageProfile();
        return _rbUseBuiltInCVSClient;
    }

    /** Tries to find "Use Command-Line CVS Client" JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbUseCommandLineCVSClient() {
        if (_rbUseCommandLineCVSClient==null) {
            _rbUseCommandLineCVSClient = new JRadioButtonOperator(selectPageProfile(), "Use Command-Line CVS Client");
        }
        selectPageProfile();
        return _rbUseCommandLineCVSClient;
    }

    /** Tries to find "CVS Executable:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblCVSExecutable() {
        if (_lblCVSExecutable==null) {
            _lblCVSExecutable = new JLabelOperator(selectPageProfile(), "CVS Executable:");
        }
        selectPageProfile();
        return _lblCVSExecutable;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtCVSExecutable() {
        if (_txtCVSExecutable==null) {
            _txtCVSExecutable = new JTextFieldOperator(selectPageProfile(), 7);
        }
        selectPageProfile();
        return _txtCVSExecutable;
    }

    /** Tries to find "Browse..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btBrowse3() {
        if (_btBrowse3==null) {
            _btBrowse3 = new JButtonOperator(selectPageProfile(), "Browse...", 2);
        }
        selectPageProfile();
        return _btBrowse3;
    }

    /** Tries to find "Remote Shell:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblRemoteShell() {
        if (_lblRemoteShell==null) {
            _lblRemoteShell = new JLabelOperator(selectPageProfile(), "Remote Shell:");
        }
        selectPageProfile();
        return _lblRemoteShell;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtRemoteShell() {
        if (_txtRemoteShell==null) {
            _txtRemoteShell = new JTextFieldOperator(selectPageProfile(), 8);
        }
        selectPageProfile();
        return _txtRemoteShell;
    }

    /** Tries to find "Login to Pserver at " JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbLoginToPserverAt() {
        if (_rbLoginToPserverAt==null) {
            _rbLoginToPserverAt = new JRadioButtonOperator(selectPageProfile(), "Login to Pserver at ");
        }
        selectPageProfile();
        return _rbLoginToPserverAt;
    }

    /** Tries to find "Password:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblPassword() {
        if (_lblPassword==null) {
            _lblPassword = new JLabelOperator(selectPageProfile(), "Password:");
        }
        selectPageProfile();
        return _lblPassword;
    }

    /** Tries to find null JPasswordField in this dialog.
     * @return JPasswordFieldOperator
     */
    public JPasswordFieldOperator txtPassword() {
        if (_txtPassword==null) {
            _txtPassword = new JPasswordFieldOperator(selectPageProfile());
        }
        selectPageProfile();
        return _txtPassword;
    }

    /** Tries to find "Login" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btLogin() {
        if (_btLogin==null) {
            _btLogin = new JButtonOperator(selectPageProfile(), "Login");
        }
        selectPageProfile();
        return _btLogin;
    }

    /** Tries to find null JTextArea in this dialog.
     * @return JTextAreaOperator
     */
    public JTextAreaOperator txtJTextArea() {
        if (_txtJTextArea==null) {
            _txtJTextArea = new JTextAreaOperator(selectPageProfile());
        }
        selectPageProfile();
        return _txtJTextArea;
    }

    /** Tries to find "Set Offline Mode" JRadioButton in this dialog.
     * @return JRadioButtonOperator
     */
    public JRadioButtonOperator rbSetOfflineMode() {
        if (_rbSetOfflineMode==null) {
            _rbSetOfflineMode = new JRadioButtonOperator(selectPageProfile(), "Set Offline Mode");
        }
        selectPageProfile();
        return _rbSetOfflineMode;
    }

    /** Tries to find "To get additional profiles, visit:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblToGetAdditionalProfilesVisit() {
        if (_lblToGetAdditionalProfilesVisit==null) {
            _lblToGetAdditionalProfilesVisit = new JLabelOperator(selectPageProfile(), "To get additional profiles, visit:");
        }
        selectPageProfile();
        return _lblToGetAdditionalProfilesVisit;
    }

    /** Tries to find "http://vcsgeneric.netbeans.org/profiles/index.html" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblHttpVcsgenericNetbeansOrgProfilesIndexHtml() {
        if (_lblHttpVcsgenericNetbeansOrgProfilesIndexHtml==null) {
            _lblHttpVcsgenericNetbeansOrgProfilesIndexHtml = new JLabelOperator(selectPageProfile(), "http://vcsgeneric.netbeans.org/profiles/index.html");
        }
        selectPageProfile();
        return _lblHttpVcsgenericNetbeansOrgProfilesIndexHtml;
    }

    /** Tries to find "Modes:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblModes() {
        if (_lblModes==null) {
            _lblModes = new JLabelOperator(selectPageAdvanced(), "Modes:");
        }
        selectPageAdvanced();
        return _lblModes;
    }

    /** Tries to find " Advanced Mode" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbAdvancedMode() {
        if (_cbAdvancedMode==null) {
            _cbAdvancedMode = new JCheckBoxOperator(selectPageAdvanced(), " Advanced Mode");
        }
        selectPageAdvanced();
        return _cbAdvancedMode;
    }

    /** Tries to find " Offline Mode" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbOfflineMode() {
        if (_cbOfflineMode==null) {
            _cbOfflineMode = new JCheckBoxOperator(selectPageAdvanced(), " Offline Mode");
        }
        selectPageAdvanced();
        return _cbOfflineMode;
    }

    /** Tries to find "Actions:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblActions() {
        if (_lblActions==null) {
            _lblActions = new JLabelOperator(selectPageAdvanced(), "Actions:");
        }
        selectPageAdvanced();
        return _lblActions;
    }

    /** Tries to find " Call EDIT Command When Editing Read-Only Files" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbCallEDITCommandWhenEditingReadOnlyFiles() {
        if (_cbCallEDITCommandWhenEditingReadOnlyFiles==null) {
            _cbCallEDITCommandWhenEditingReadOnlyFiles = new JCheckBoxOperator(selectPageAdvanced(), " Call EDIT Command When Editing Read-Only Files");
        }
        selectPageAdvanced();
        return _cbCallEDITCommandWhenEditingReadOnlyFiles;
    }

    /** Tries to find " Prompt for EDIT Command Execution" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbPromptForEDITCommandExecution() {
        if (_cbPromptForEDITCommandExecution==null) {
            _cbPromptForEDITCommandExecution = new JCheckBoxOperator(selectPageAdvanced(), " Prompt for EDIT Command Execution");
        }
        selectPageAdvanced();
        return _cbPromptForEDITCommandExecution;
    }

    /** Tries to find "Message:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblMessage() {
        if (_lblMessage==null) {
            _lblMessage = new JLabelOperator(selectPageAdvanced(), "Message:");
        }
        selectPageAdvanced();
        return _lblMessage;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtMessage() {
        if (_txtMessage==null) {
            _txtMessage = new JTextFieldOperator(selectPageAdvanced());
        }
        selectPageAdvanced();
        return _txtMessage;
    }

    /** Tries to find " Lock Files When Editing (call LOCK command)" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbLockFilesWhenEditingCallLOCKCommand() {
        if (_cbLockFilesWhenEditingCallLOCKCommand==null) {
            _cbLockFilesWhenEditingCallLOCKCommand = new JCheckBoxOperator(selectPageAdvanced(), " Lock Files When Editing (call LOCK command)");
        }
        selectPageAdvanced();
        return _cbLockFilesWhenEditingCallLOCKCommand;
    }

    /** Tries to find " Prompt for LOCK Command Execution" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbPromptForLOCKCommandExecution() {
        if (_cbPromptForLOCKCommandExecution==null) {
            _cbPromptForLOCKCommandExecution = new JCheckBoxOperator(selectPageAdvanced(), " Prompt for LOCK Command Execution");
        }
        selectPageAdvanced();
        return _cbPromptForLOCKCommandExecution;
    }

    /** Tries to find "Message:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblMessage2() {
        if (_lblMessage2==null) {
            _lblMessage2 = new JLabelOperator(selectPageAdvanced(), "Message:", 1);
        }
        selectPageAdvanced();
        return _lblMessage2;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtMessage2() {
        if (_txtMessage2==null) {
            _txtMessage2 = new JTextFieldOperator(selectPageAdvanced(), 1);
        }
        selectPageAdvanced();
        return _txtMessage2;
    }

    /** Tries to find "Other:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblOther() {
        if (_lblOther==null) {
            _lblOther = new JLabelOperator(selectPageAdvanced(), "Other:");
        }
        selectPageAdvanced();
        return _lblOther;
    }

    /** Tries to find " Print Command Output" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbPrintCommandOutput() {
        if (_cbPrintCommandOutput==null) {
            _cbPrintCommandOutput = new JCheckBoxOperator(selectPageAdvanced(), " Print Command Output");
        }
        selectPageAdvanced();
        return _cbPrintCommandOutput;
    }

    /** Tries to find "Compatible With OS:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblCompatibleWithOS() {
        if (_lblCompatibleWithOS==null) {
            _lblCompatibleWithOS = new JLabelOperator(selectPageAdvanced(), "Compatible With OS:");
        }
        selectPageAdvanced();
        return _lblCompatibleWithOS;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtCompatibleWithOS() {
        if (_txtCompatibleWithOS==null) {
            _txtCompatibleWithOS = new JTextFieldOperator(selectPageAdvanced(), 2);
        }
        selectPageAdvanced();
        return _txtCompatibleWithOS;
    }

    /** Tries to find "Incompatible With OS:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblIncompatibleWithOS() {
        if (_lblIncompatibleWithOS==null) {
            _lblIncompatibleWithOS = new JLabelOperator(selectPageAdvanced(), "Incompatible With OS:");
        }
        selectPageAdvanced();
        return _lblIncompatibleWithOS;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtIncompatibleWithOS() {
        if (_txtIncompatibleWithOS==null) {
            _txtIncompatibleWithOS = new JTextFieldOperator(selectPageAdvanced(), 3);
        }
        selectPageAdvanced();
        return _txtIncompatibleWithOS;
    }

    /** Tries to find "Your Current Operating System Is: Windows 2000" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblYourCurrentOperatingSystemIsWindows2000() {
        if (_lblYourCurrentOperatingSystemIsWindows2000==null) {
            _lblYourCurrentOperatingSystemIsWindows2000 = new JLabelOperator(selectPageAdvanced(), "Your Current Operating System Is: Windows 2000");
        }
        selectPageAdvanced();
        return _lblYourCurrentOperatingSystemIsWindows2000;
    }

    /** Tries to find "Edit Commands..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btEditCommands() {
        if (_btEditCommands==null) {
            _btEditCommands = new JButtonOperator(selectPageAdvanced(), "Edit Commands...");
        }
        selectPageAdvanced();
        return _btEditCommands;
    }

    /** Tries to find "Edit Variables..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btEditVariables() {
        if (_btEditVariables==null) {
            _btEditVariables = new JButtonOperator(selectPageAdvanced(), "Edit Variables...");
        }
        selectPageAdvanced();
        return _btEditVariables;
    }

    /** Tries to find "User-Defined Environment Variables:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblUserDefinedEnvironmentVariables() {
        if (_lblUserDefinedEnvironmentVariables==null) {
            _lblUserDefinedEnvironmentVariables = new JLabelOperator(selectPageEnvironment(), "User-Defined Environment Variables:");
        }
        selectPageEnvironment();
        return _lblUserDefinedEnvironmentVariables;
    }

    /** Tries to find null JTable in this dialog.
     * @return JTableOperator
     */
    public JTableOperator tabUserDefinedEnvironmentVariables() {
        if (_tabUserDefinedEnvironmentVariables==null) {
            _tabUserDefinedEnvironmentVariables = new JTableOperator(selectPageEnvironment());
        }
        selectPageEnvironment();
        return _tabUserDefinedEnvironmentVariables;
    }

    /** Tries to find "Insert" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btInsert() {
        if (_btInsert==null) {
            _btInsert = new JButtonOperator(selectPageEnvironment(), "Insert");
        }
        selectPageEnvironment();
        return _btInsert;
    }

    /** Tries to find "Delete" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btDelete() {
        if (_btDelete==null) {
            _btDelete = new JButtonOperator(selectPageEnvironment(), "Delete");
        }
        selectPageEnvironment();
        return _btDelete;
    }

    /** Tries to find "System Environment Variables:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblSystemEnvironmentVariables() {
        if (_lblSystemEnvironmentVariables==null) {
            _lblSystemEnvironmentVariables = new JLabelOperator(selectPageEnvironment(), "System Environment Variables:");
        }
        selectPageEnvironment();
        return _lblSystemEnvironmentVariables;
    }

    /** Tries to find null JTable in this dialog.
     * @return JTableOperator
     */
    public JTableOperator tabSystemEnvironmentVariables() {
        if (_tabSystemEnvironmentVariables==null) {
            _tabSystemEnvironmentVariables = new JTableOperator(selectPageEnvironment(), 1);
        }
        selectPageEnvironment();
        return _tabSystemEnvironmentVariables;
    }

    /** Tries to find null WindowsScrollBarUI$WindowsArrowButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btWindowsScrollBarUI$WindowsArrowButton() {
        if (_btWindowsScrollBarUI$WindowsArrowButton==null) {
            _btWindowsScrollBarUI$WindowsArrowButton = new JButtonOperator(selectPageEnvironment(), 2);
        }
        selectPageEnvironment();
        return _btWindowsScrollBarUI$WindowsArrowButton;
    }

    /** Tries to find null WindowsScrollBarUI$WindowsArrowButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btWindowsScrollBarUI$WindowsArrowButton2() {
        if (_btWindowsScrollBarUI$WindowsArrowButton2==null) {
            _btWindowsScrollBarUI$WindowsArrowButton2 = new JButtonOperator(selectPageEnvironment(), 3);
        }
        selectPageEnvironment();
        return _btWindowsScrollBarUI$WindowsArrowButton2;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** changes current selected tab
     * @param tabName String tab name */
    public void selectCustomizerTabbedPanePage(String tabName) {
        tbpCustomizerTabbedPane().selectPage(tabName);
    }

    /** changes current selected tab to "Profile"
     * @return JTabbedPaneOperator of parent tabbed pane
     */
    public JTabbedPaneOperator selectPageProfile() {
        tbpCustomizerTabbedPane().selectPage(_selectPageProfile);
        return tbpCustomizerTabbedPane();
    }

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
    public void browse2() {
        btBrowse2().push();
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
    public void browse3() {
        btBrowse3().push();
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

    /** gets text for txtJTextArea
     * @return String text
     */
    public String getJTextArea() {
        return txtJTextArea().getText();
    }

    /** sets text for txtJTextArea
     * @param text String text
     */
    public void setJTextArea(String text) {
        txtJTextArea().setText(text);
    }

    /** types text for txtJTextArea
     * @param text String text
     */
    public void typeJTextArea(String text) {
        txtJTextArea().typeText(text);
    }

    /** clicks on "Set Offline Mode" JRadioButton
     */
    public void setOfflineMode() {
        rbSetOfflineMode().push();
    }

    /** changes current selected tab to "Advanced"
     * @return JTabbedPaneOperator of parent tabbed pane
     */
    public JTabbedPaneOperator selectPageAdvanced() {
        tbpCustomizerTabbedPane().selectPage(_selectPageAdvanced);
        return tbpCustomizerTabbedPane();
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkAdvancedMode(boolean state) {
        if (cbAdvancedMode().isSelected()!=state) {
            cbAdvancedMode().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkOfflineMode(boolean state) {
        if (cbOfflineMode().isSelected()!=state) {
            cbOfflineMode().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkCallEDITCommandWhenEditingReadOnlyFiles(boolean state) {
        if (cbCallEDITCommandWhenEditingReadOnlyFiles().isSelected()!=state) {
            cbCallEDITCommandWhenEditingReadOnlyFiles().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkPromptForEDITCommandExecution(boolean state) {
        if (cbPromptForEDITCommandExecution().isSelected()!=state) {
            cbPromptForEDITCommandExecution().push();
        }
    }

    /** gets text for txtMessage
     * @return String text
     */
    public String getMessage() {
        return txtMessage().getText();
    }

    /** sets text for txtMessage
     * @param text String text
     */
    public void setMessage(String text) {
        txtMessage().setText(text);
    }

    /** types text for txtMessage
     * @param text String text
     */
    public void typeMessage(String text) {
        txtMessage().typeText(text);
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkLockFilesWhenEditingCallLOCKCommand(boolean state) {
        if (cbLockFilesWhenEditingCallLOCKCommand().isSelected()!=state) {
            cbLockFilesWhenEditingCallLOCKCommand().push();
        }
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkPromptForLOCKCommandExecution(boolean state) {
        if (cbPromptForLOCKCommandExecution().isSelected()!=state) {
            cbPromptForLOCKCommandExecution().push();
        }
    }

    /** gets text for txtMessage2
     * @return String text
     */
    public String getMessage2() {
        return txtMessage2().getText();
    }

    /** sets text for txtMessage2
     * @param text String text
     */
    public void setMessage2(String text) {
        txtMessage2().setText(text);
    }

    /** types text for txtMessage2
     * @param text String text
     */
    public void typeMessage2(String text) {
        txtMessage2().typeText(text);
    }

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkPrintCommandOutput(boolean state) {
        if (cbPrintCommandOutput().isSelected()!=state) {
            cbPrintCommandOutput().push();
        }
    }

    /** gets text for txtCompatibleWithOS
     * @return String text
     */
    public String getCompatibleWithOS() {
        return txtCompatibleWithOS().getText();
    }

    /** sets text for txtCompatibleWithOS
     * @param text String text
     */
    public void setCompatibleWithOS(String text) {
        txtCompatibleWithOS().setText(text);
    }

    /** types text for txtCompatibleWithOS
     * @param text String text
     */
    public void typeCompatibleWithOS(String text) {
        txtCompatibleWithOS().typeText(text);
    }

    /** gets text for txtIncompatibleWithOS
     * @return String text
     */
    public String getIncompatibleWithOS() {
        return txtIncompatibleWithOS().getText();
    }

    /** sets text for txtIncompatibleWithOS
     * @param text String text
     */
    public void setIncompatibleWithOS(String text) {
        txtIncompatibleWithOS().setText(text);
    }

    /** types text for txtIncompatibleWithOS
     * @param text String text
     */
    public void typeIncompatibleWithOS(String text) {
        txtIncompatibleWithOS().typeText(text);
    }

    /** clicks on "Edit Commands..." JButton
     */
    public void editCommands() {
        btEditCommands().push();
    }

    /** clicks on "Edit Variables..." JButton
     */
    public void editVariables() {
        btEditVariables().push();
    }

    /** changes current selected tab to "Environment"
     * @return JTabbedPaneOperator of parent tabbed pane
     */
    public JTabbedPaneOperator selectPageEnvironment() {
        tbpCustomizerTabbedPane().selectPage(_selectPageEnvironment);
        return tbpCustomizerTabbedPane();
    }

    /** clicks on "Insert" JButton
     */
    public void insert() {
        btInsert().push();
    }

    /** clicks on "Delete" JButton
     */
    public void delete() {
        btDelete().push();
    }

    /** clicks on null WindowsScrollBarUI$WindowsArrowButton
     */
    public void windowsScrollBarUI$WindowsArrowButton() {
        btWindowsScrollBarUI$WindowsArrowButton().push();
    }

    /** clicks on null WindowsScrollBarUI$WindowsArrowButton
     */
    public void windowsScrollBarUI$WindowsArrowButton2() {
        btWindowsScrollBarUI$WindowsArrowButton2().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of VCSFilesystemCustomizerDialog by accessing all its components.
     */
    public void verify() {
        tbpCustomizerTabbedPane();
        cboProfileComboBox();
        btSaveAs();
        lblWorkingDirectory();
        txtWorkingDirectory();
        btBrowse();
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
        btBrowse2();
        rbUseBuiltInCVSClient();
        rbUseCommandLineCVSClient();
        lblCVSExecutable();
        txtCVSExecutable();
        btBrowse3();
        lblRemoteShell();
        txtRemoteShell();
        rbLoginToPserverAt();
        lblPassword();
        txtPassword();
        btLogin();
        txtJTextArea();
        rbSetOfflineMode();
        lblToGetAdditionalProfilesVisit();
        lblHttpVcsgenericNetbeansOrgProfilesIndexHtml();
        lblModes();
        cbAdvancedMode();
        cbOfflineMode();
        lblActions();
        cbCallEDITCommandWhenEditingReadOnlyFiles();
        cbPromptForEDITCommandExecution();
        lblMessage();
        txtMessage();
        cbLockFilesWhenEditingCallLOCKCommand();
        cbPromptForLOCKCommandExecution();
        lblMessage2();
        txtMessage2();
        lblOther();
        cbPrintCommandOutput();
        lblCompatibleWithOS();
        txtCompatibleWithOS();
        lblIncompatibleWithOS();
        txtIncompatibleWithOS();
        lblYourCurrentOperatingSystemIsWindows2000();
        btEditCommands();
        btEditVariables();
        lblUserDefinedEnvironmentVariables();
        tabUserDefinedEnvironmentVariables();
        btInsert();
        btDelete();
        lblSystemEnvironmentVariables();
        tabSystemEnvironmentVariables();
        btWindowsScrollBarUI$WindowsArrowButton();
        btWindowsScrollBarUI$WindowsArrowButton2();
    }

    /** Performs simple test of VCSFilesystemCustomizerDialog
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new VCSFilesystemCustomizerDialog().verify();
        System.out.println("VCSFilesystemCustomizerDialog verification finished.");
    }
}

