/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced;

import java.io.*;
import java.util.*;
import java.beans.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.text.*;

import org.openide.*;
import org.openide.util.*;
import org.openide.filesystems.*;
import org.openide.loaders.DataObject;
import org.openide.loaders.XMLDataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;

import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.cmdline.*;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.*;

import org.netbeans.modules.vcs.advanced.commands.UserCommandIO;
import org.netbeans.modules.vcs.advanced.variables.VariableIO;
import org.netbeans.modules.vcs.advanced.variables.VariableIOCompat;

/** Customizer
 *
 * @author Michal Fadljevic, Martin Entlicher
 */

public class VcsCustomizer extends javax.swing.JPanel implements Customizer {
    private Debug E=new Debug("VcsCustomizer", true); // NOI18N
    private Debug D = E;

    /**
     * The name of the variable, that contains pairs of variables and commands.
     * When the variables listed here change their value, the corresponding command
     * is executed to fill values of remaining variables. This can be used to automatically
     * fill in VCS configuartion, when it can be obtained from local configuration files.
     */
    public static final String VAR_AUTO_FILL = "AUTO_FILL_VARS";
    
    /**
     * The name of a variable, that contains the string representation of variable
     * input descriptor, that is used to construct the panel with configuration
     * input components. If this variable is defined, the "basic" property of
     * variables has no effect.
     */
    public static final String VAR_CONFIG_INPUT_DESCRIPTOR = "CONFIG_INPUT_DESCRIPTOR";
    
    public static final String PROP_PROFILE_SELECTION_CHANGED = "profileSelectionChanged";

    private HashMap autoFillVars = new HashMap();
    private Map fsVarsByName = null;
    
    //private HashMap cache = new HashMap ();
    
    private String browseRoot = null;
    
    private Hashtable envVariables = new Hashtable();
    private Hashtable envVariablesRemoved = new Hashtable();
    
    private int numCreations = 0;
    
    private ProfilesCache cache;
    private String noProfileSelectedLabel;
    
    //private static transient FileLock configSaveLock = FileLock.NONE;

    static final long serialVersionUID = -8801742771957370172L;
    /** Creates new form VcsCustomizer */
    public VcsCustomizer () {
        changeSupport = new PropertyChangeSupport (this);
        initComponents ();
        postInitComponents();
        initAccessibility();
    }
    
    private void initAccessibility()
    {
        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizerDialogA11yName"));  // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizerDialogA11yDesc"));  // NOI18N
        configCombo.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.configComboBoxA11yName"));  // NOI18N
        configCombo.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.configComboBoxA11yDesc"));  // NOI18N
        jLabel2.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.jLabel2.textA11yDesc"));  // NOI18N
        rootDirTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.workingDirectoryTextField.textA11yName"));  // NOI18N
        browseButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.browseButton.textA11yDesc")); // NOI18N
        relMountLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.relMountLabel.textA11yDesc"));  // NOI18N
        relMountTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.relativeTextField.textA11yName"));  // NOI18N
        relMountButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.relMountButton.textA11yDesc")); // NOI18N
        jLabel1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_AdditionalProfilesTextA11yDesc"));  // NOI18N
        jLabel5.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.modesLabel.textA11yDesc"));  // NOI18N
        jLabel6.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.actionsLabel.textA11yDesc"));  // NOI18N
        promptEditLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.promptEditLabel.textA11yDesc"));  // NOI18N
        promptEditTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.promptTextField.textA11yName"));  // NOI18N
        promptLockLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.promptLockLabel.textA11yDesc"));  // NOI18N
        promptLockTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.lockTextField.textA11yName"));  // NOI18N
        jLabel7.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.otherLabel.textA11yDesc"));  // NOI18N
        compatibleOSLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.compatibleOSLabel.textA11yDesc"));  // NOI18N
        compatibleOSTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.compatibleTextField.textA11yName"));  // NOI18N
        uncompatibleOSLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.uncompatibleOSLabel.textA11yDesc"));  // NOI18N
        uncompatibleOSTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.uncompatibleTextField.textA11yName"));  // NOI18N
        currentOSLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.currentOSLabel.txtA11yDesc"));  // NOI18N
        userEnvLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.cmdButton.textA11yDesc"));  // NOI18N
        systemEnvLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.varButton.textA11yDesc"));  // NOI18N
        jTabbedPane1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizerTabbedPaneA11yName"));  // NOI18N
        jTabbedPane1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizerTabbedPaneA11yDesc"));  // NOI18N
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        configPanel = new javax.swing.JPanel();
        vcsPanel = new javax.swing.JPanel();
        configCombo = new javax.swing.JComboBox();
        saveAsButton = new javax.swing.JButton();
        removeConfigButton = new javax.swing.JButton();
        allProfilesCheckBox = new javax.swing.JCheckBox();
        propsPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        rootDirTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        relMountLabel = new javax.swing.JLabel();
        relMountTextField = new javax.swing.JTextField();
        relMountButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        linkLabel = new javax.swing.JLabel();
        advancedPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        advancedModeCheckBox = new javax.swing.JCheckBox();
        offLineCheckBox = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        editCheckBox = new javax.swing.JCheckBox();
        promptEditCheckBox = new javax.swing.JCheckBox();
        promptEditLabel = new javax.swing.JLabel();
        promptEditTextField = new javax.swing.JTextField();
        lockCheckBox = new javax.swing.JCheckBox();
        promptLockCheckBox = new javax.swing.JCheckBox();
        promptLockLabel = new javax.swing.JLabel();
        promptLockTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        debugCheckBox = new javax.swing.JCheckBox();
        compatibleOSLabel = new javax.swing.JLabel();
        compatibleOSTextField = new javax.swing.JTextField();
        uncompatibleOSLabel = new javax.swing.JLabel();
        uncompatibleOSTextField = new javax.swing.JTextField();
        currentOSLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        cmdButton = new javax.swing.JButton();
        varButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        environmentPanel = new javax.swing.JPanel();
        userEnvLabel = new javax.swing.JLabel();
        envScrollPane = new javax.swing.JScrollPane();
        envTable = new javax.swing.JTable();
        actionPanel = new javax.swing.JPanel();
        insertEnvButton = new javax.swing.JButton();
        deleteEnvButton = new javax.swing.JButton();
        systemEnvLabel = new javax.swing.JLabel();
        systemEnvScrollPane = new javax.swing.JScrollPane();
        systemEnvTable = new javax.swing.JTable();

        setLayout(new java.awt.GridBagLayout());

        configPanel.setLayout(new java.awt.GridBagLayout());

        configPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 11, 11)));
        vcsPanel.setLayout(new java.awt.GridBagLayout());

        vcsPanel.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), " " + java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.congifurationTitle.text") + " "));
        configCombo.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.configComboBoxA11yDesc"));
        configCombo.setNextFocusableComponent(saveAsButton);
        configCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                configComboItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 5, 0);
        vcsPanel.add(configCombo, gridBagConstraints);

        saveAsButton.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.saveAsButton.textA11yDesc"));
        saveAsButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.saveAsButton.text"));
        saveAsButton.setNextFocusableComponent(removeConfigButton);
        saveAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 5, 0);
        vcsPanel.add(saveAsButton, gridBagConstraints);

        removeConfigButton.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.removeConfigButton.textA11yDesc"));
        removeConfigButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.removeConfigButton.text"));
        removeConfigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeConfigButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 5, 5, 11);
        vcsPanel.add(removeConfigButton, gridBagConstraints);

        allProfilesCheckBox.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.allProfilesCheckBox.textA11yDesc"));
        allProfilesCheckBox.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.allProfilesCheckBox.text"));
        allProfilesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allProfilesCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 11);
        vcsPanel.add(allProfilesCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        configPanel.add(vcsPanel, gridBagConstraints);

        propsPanel.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.jLabel2.text"));
        jLabel2.setLabelFor(rootDirTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 12);
        propsPanel.add(jLabel2, gridBagConstraints);

        rootDirTextField.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.workingDirectoryTextField.textA11yDesc"));
        rootDirTextField.setColumns(15);
        rootDirTextField.setText(".");
        rootDirTextField.setNextFocusableComponent(browseButton);
        rootDirTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rootDirTextFieldActionPerformed(evt);
            }
        });

        rootDirTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                rootDirTextFieldFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        propsPanel.add(rootDirTextField, gridBagConstraints);

        browseButton.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.relMountButton.textA11yDesc"));
        browseButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.browseButton.text"));
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        propsPanel.add(browseButton, gridBagConstraints);

        relMountLabel.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.relMountLabel.text"));
        relMountLabel.setLabelFor(relMountTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 12);
        propsPanel.add(relMountLabel, gridBagConstraints);

        relMountTextField.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.relativeTextField.textA11yDesc"));
        relMountTextField.setMinimumSize(new java.awt.Dimension(165, 21));
        relMountTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relMountTextFieldActionPerformed(evt);
            }
        });

        relMountTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                relMountTextFieldFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        propsPanel.add(relMountTextField, gridBagConstraints);

        relMountButton.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.relMountButton.textA11yDesc"));
        relMountButton.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.relMountButton.text"));
        relMountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relMountButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        propsPanel.add(relMountButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        configPanel.add(propsPanel, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        jPanel1.add(jSeparator1, gridBagConstraints);

        jLabel1.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("AdditionalProfilesText"));
        jLabel1.setForeground(java.awt.Color.black);
        jLabel1.setLabelFor(linkLabel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabel1, gridBagConstraints);

        linkLabel.setText("http://vcsgeneric.netbeans.org/profiles/index.html");
        linkLabel.setForeground(new java.awt.Color(102, 102, 153));
        linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                linkLabelMouseReleased(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel1.add(linkLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        configPanel.add(jPanel1, gridBagConstraints);

        jTabbedPane1.addTab("Configuration", null, configPanel, "");

        advancedPanel.setLayout(new java.awt.GridBagLayout());

        advancedPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 11, 11)));
        jLabel5.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.modesLabel.text"));
        jLabel5.setLabelFor(advancedModeCheckBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        advancedPanel.add(jLabel5, gridBagConstraints);

        advancedModeCheckBox.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.advancedModeCheckBox.textA11yDesc"));
        advancedModeCheckBox.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.advancedModeCheckBox.text"));
        advancedModeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advancedModeCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        advancedPanel.add(advancedModeCheckBox, gridBagConstraints);

        offLineCheckBox.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.offLineCheckBox.textA11yDesc"));
        offLineCheckBox.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.offLineCheckBox.text"));
        offLineCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                offLineCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        advancedPanel.add(offLineCheckBox, gridBagConstraints);

        jLabel6.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.actionsLabel.text"));
        jLabel6.setLabelFor(editCheckBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 11);
        advancedPanel.add(jLabel6, gridBagConstraints);

        editCheckBox.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.editCheckBox.textA11yDesc"));
        editCheckBox.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.editCheckBox.text"));
        editCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        advancedPanel.add(editCheckBox, gridBagConstraints);

        promptEditCheckBox.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.promptEditCheckBox.textA11yDesc"));
        promptEditCheckBox.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.promptEditCheckBox.text"));
        promptEditCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                promptEditCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 24, 0, 0);
        advancedPanel.add(promptEditCheckBox, gridBagConstraints);

        promptEditLabel.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.promptEditLabel.text"));
        promptEditLabel.setLabelFor(promptEditTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 48, 5, 5);
        advancedPanel.add(promptEditLabel, gridBagConstraints);

        promptEditTextField.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.promptTextField.textA11yDesc"));
        promptEditTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                promptEditTextFieldActionPerformed(evt);
            }
        });

        promptEditTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                promptEditTextFieldFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        advancedPanel.add(promptEditTextField, gridBagConstraints);

        lockCheckBox.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.lockCheckBox.textA11yDesc"));
        lockCheckBox.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.lockCheckBox.text"));
        lockCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        advancedPanel.add(lockCheckBox, gridBagConstraints);

        promptLockCheckBox.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.promptEditCheckBox.textA11yDesc"));
        promptLockCheckBox.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.promptLockCheckBox.text"));
        promptLockCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                promptLockCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 24, 0, 0);
        advancedPanel.add(promptLockCheckBox, gridBagConstraints);

        promptLockLabel.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.promptLockLabel.text"));
        promptLockLabel.setLabelFor(promptLockTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 48, 12, 5);
        advancedPanel.add(promptLockLabel, gridBagConstraints);

        promptLockTextField.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.lockTextField.textA11yDesc"));
        promptLockTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                promptLockTextFieldActionPerformed(evt);
            }
        });

        promptLockTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                promptLockTextFieldFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        advancedPanel.add(promptLockTextField, gridBagConstraints);

        jLabel7.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.otherLabel.text"));
        jLabel7.setLabelFor(debugCheckBox);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 11);
        advancedPanel.add(jLabel7, gridBagConstraints);

        debugCheckBox.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.debugCheckBox.textA11yDesc"));
        debugCheckBox.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.debugCheckBox.text"));
        debugCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        advancedPanel.add(debugCheckBox, gridBagConstraints);

        compatibleOSLabel.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.compatibleOSLabel.text"));
        compatibleOSLabel.setLabelFor(compatibleOSTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        advancedPanel.add(compatibleOSLabel, gridBagConstraints);

        compatibleOSTextField.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.compatibleTextField.textA11yDesc"));
        compatibleOSTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                compatibleOSTextFieldFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        advancedPanel.add(compatibleOSTextField, gridBagConstraints);

        uncompatibleOSLabel.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.uncompatibleOSLabel.text"));
        uncompatibleOSLabel.setLabelFor(uncompatibleOSTextField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        advancedPanel.add(uncompatibleOSLabel, gridBagConstraints);

        uncompatibleOSTextField.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.uncompatibleTextField.textA11yDesc"));
        uncompatibleOSTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                uncompatibleOSTextFieldFocusLost(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        advancedPanel.add(uncompatibleOSTextField, gridBagConstraints);

        currentOSLabel.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.currentOSLabel.txt"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        advancedPanel.add(currentOSLabel, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        cmdButton.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.cmdButton.textA11yDesc"));
        cmdButton.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.cmdButton.text"));
        cmdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdButtonActionPerformed(evt);
            }
        });

        jPanel2.add(cmdButton);

        varButton.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.varButton.textA11yDesc"));
        varButton.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.varButton.text"));
        varButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                varButtonActionPerformed(evt);
            }
        });

        jPanel2.add(varButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
        advancedPanel.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        advancedPanel.add(jLabel8, gridBagConstraints);

        jTabbedPane1.addTab("Advanced", null, advancedPanel, "");

        environmentPanel.setLayout(new java.awt.GridBagLayout());

        environmentPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 11, 11)));
        userEnvLabel.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("userEnvLabel.text"));
        userEnvLabel.setLabelFor(envTable);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        environmentPanel.add(userEnvLabel, gridBagConstraints);

        envScrollPane.setPreferredSize(new java.awt.Dimension(10, 10));
        envTable.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_userEnvTable.textA11yDesc"));
        envTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Variable Name", "Variable Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        envTable.setPreferredScrollableViewportSize(new java.awt.Dimension(0, 0));
        envScrollPane.setViewportView(envTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 12);
        environmentPanel.add(envScrollPane, gridBagConstraints);

        actionPanel.setLayout(new java.awt.GridBagLayout());

        insertEnvButton.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_InsertEnvA11yDesc"));
        insertEnvButton.setText(org.openide.util.NbBundle.getMessage(VcsCustomizer.class, "LBL_InsertEnv"));
        insertEnvButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertEnvButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        actionPanel.add(insertEnvButton, gridBagConstraints);

        deleteEnvButton.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_DeleteEnvA11yDesc"));
        deleteEnvButton.setText(org.openide.util.NbBundle.getMessage(VcsCustomizer.class, "LBL_DeleteEnv"));
        deleteEnvButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEnvButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        actionPanel.add(deleteEnvButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        environmentPanel.add(actionPanel, gridBagConstraints);

        systemEnvLabel.setText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("systemEnvLabel.text"));
        systemEnvLabel.setLabelFor(systemEnvTable);
        systemEnvLabel.setAlignmentX(0.5F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        environmentPanel.add(systemEnvLabel, gridBagConstraints);

        systemEnvScrollPane.setPreferredSize(new java.awt.Dimension(32, 32));
        systemEnvTable.setToolTipText(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_systemEnvTable.textA11yDesc"));
        systemEnvTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Variable Name", "Variable Value", "Use in VCS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        systemEnvTable.setPreferredScrollableViewportSize(new java.awt.Dimension(0, 0));
        systemEnvScrollPane.setViewportView(systemEnvTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        environmentPanel.add(systemEnvScrollPane, gridBagConstraints);

        jTabbedPane1.addTab("Environment", null, environmentPanel, "");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jTabbedPane1, gridBagConstraints);

    }//GEN-END:initComponents

    private void compatibleOSTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_compatibleOSTextFieldFocusLost
        // Add your handling code here:
        String OSStr = compatibleOSTextField.getText().trim();
        Set OSSet;
        if (OSStr.length() == 0) {
            OSSet = Collections.EMPTY_SET;
        } else {
            String[] OSs = VcsUtilities.getQuotedStrings(OSStr);
            OSSet = new HashSet(Arrays.asList(OSs));
        }
        fileSystem.setCompatibleOSs(OSSet);
    }//GEN-LAST:event_compatibleOSTextFieldFocusLost

    private void uncompatibleOSTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_uncompatibleOSTextFieldFocusLost
        // Add your handling code here:
        String OSStr = uncompatibleOSTextField.getText().trim();
        Set OSSet;
        if (OSStr.length() == 0) {
            OSSet = Collections.EMPTY_SET;
        } else {
            String[] OSs = VcsUtilities.getQuotedStrings(OSStr);
            OSSet = new HashSet(Arrays.asList(OSs));
        }
        fileSystem.setUncompatibleOSs(OSSet);
    }//GEN-LAST:event_uncompatibleOSTextFieldFocusLost

    private void allProfilesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allProfilesCheckBoxActionPerformed
        // Add your handling code here:
        if (!updateConfigurations() && allProfilesCheckBox.isSelected()) {
            NotifyDescriptor.Confirmation nd = new NotifyDescriptor.Confirmation(g("DLG_DiscardAndShowCurrentOS"), NotifyDescriptor.Confirmation.OK_CANCEL_OPTION);
            if (NotifyDescriptor.Confirmation.OK_OPTION.equals(TopManager.getDefault().notify(nd))) {
                fileSystem.setConfig(null);
                promptForConfigComboChange = false;
                updateConfigurations();
                configCombo.setSelectedIndex(0);
            } else {
                allProfilesCheckBox.doClick(); // return to the previous state
            }
        }
    }//GEN-LAST:event_allProfilesCheckBoxActionPerformed

    private void promptEditTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_promptEditTextFieldFocusLost
        // Add your handling code here:
        VcsConfigVariable var = getFSVariable(Variables.MSG_PROMPT_FOR_AUTO_EDIT);
        var.setValue(promptEditTextField.getText());
    }//GEN-LAST:event_promptEditTextFieldFocusLost

    private void promptLockTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_promptLockTextFieldFocusLost
        // Add your handling code here:
        VcsConfigVariable var = getFSVariable(Variables.MSG_PROMPT_FOR_AUTO_LOCK);
        var.setValue(promptLockTextField.getText());
    }//GEN-LAST:event_promptLockTextFieldFocusLost

    private void promptLockTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_promptLockTextFieldActionPerformed
        // Add your handling code here:
        VcsConfigVariable var = getFSVariable(Variables.MSG_PROMPT_FOR_AUTO_LOCK);
        var.setValue(promptLockTextField.getText());
    }//GEN-LAST:event_promptLockTextFieldActionPerformed

    private void promptEditTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_promptEditTextFieldActionPerformed
        // Add your handling code here:
        VcsConfigVariable var = getFSVariable(Variables.MSG_PROMPT_FOR_AUTO_EDIT);
        var.setValue(promptEditTextField.getText());
    }//GEN-LAST:event_promptEditTextFieldActionPerformed

    private HashMap fsVars = new HashMap();
    private VcsConfigVariable getFSVariable(String varName) {
        VcsConfigVariable var = (VcsConfigVariable) fsVars.get(varName);
        if (var == null) {
            Vector vars = fileSystem.getVariables();
            for (int i = vars.size() - 1; i >= 0; i--) {
                VcsConfigVariable testVar = (VcsConfigVariable) vars.get(i);
                if (varName.equals(testVar.getName())) {
                    var = testVar;
                    break;
                }
            }
            if (var == null) {
                var = new VcsConfigVariable(varName, "", "", false, false, false, null);
            }
            fsVars.put(varName, var);
            vars.add(var);
            fileSystem.setVariables(vars);
        }
        return var;
    }
    
    private void deleteEnvButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEnvButtonActionPerformed
        // Add your handling code here:
        int row = envTable.getSelectedRow();
        if (row < 0) return ;
        String name = (String) envTableModel.getValueAt(row, 0);
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(NbBundle.getMessage(VcsCustomizer.class, "DLG_EnvVarDeleteConfirm", name));
        if (NotifyDescriptor.OK_OPTION.equals(TopManager.getDefault().notify(nd))) {
            row = envTableModel.getModelRow(row);
            ((javax.swing.table.DefaultTableModel) envTableModel.getModel()).removeRow(row);
            Vector vars = fileSystem.getVariables();
            VcsConfigVariable var = (VcsConfigVariable) envVariables.remove(name);
            vars.remove(var);
            fileSystem.setVariables(vars);
        }
    }//GEN-LAST:event_deleteEnvButtonActionPerformed

    private void insertEnvButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertEnvButtonActionPerformed
        // Add your handling code here:
        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine(NbBundle.getMessage(VcsCustomizer.class, "DLG_EnvVarName"), g ("DLG_EnvVarTitle"));
        if (NotifyDescriptor.OK_OPTION.equals(TopManager.getDefault().notify(nd))) {
            String name = (String) nd.getInputText();
            if (envVariables.containsKey(name)) {
                selectEnvVar(name);
                return ;
            }
            ((javax.swing.table.DefaultTableModel) envTableModel.getModel()).addRow(new String[] { name, "" });
            Vector vars = fileSystem.getVariables();
            VcsConfigVariable var = new VcsConfigVariable(VcsFileSystem.VAR_ENVIRONMENT_PREFIX + name,
                                           null, "", false, false, false, null);
            vars.add(var);
            fileSystem.setVariables(vars);
            envVariables.put(name, var);
            int row = envTableModel.getRowCount() - 1;
            int srow = envTableModel.getSorterRow(row);
            javax.swing.table.TableCellEditor editor = envTable.getCellEditor(row, 1);
            editor.addCellEditorListener(new EnvCellEditorListener(name, row, 1));
            envTable.clearSelection();
            envTable.addRowSelectionInterval(srow, srow);
            //envTable.getSelectionModel().setSelectionInterval(row, envTableModel.getRowCount());
        }
    }//GEN-LAST:event_insertEnvButtonActionPerformed

    private void selectEnvVar(String name) {
        for (int row = envTableModel.getRowCount() - 1; row >= 0; row--) {
            if (name.equals(envTable.getValueAt(row, 0))) {
                envTable.setRowSelectionInterval(row, row);
                break;
            }
        }
    }
    
    private void linkLabelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_linkLabelMouseReleased
        // Add your handling code here:
        try {
            TopManager.getDefault().showUrl(new java.net.URL(linkLabel.getText()));
        } catch (java.net.MalformedURLException exc) {
            TopManager.getDefault().notifyException(exc);
        }
    }//GEN-LAST:event_linkLabelMouseReleased

    private void cmdButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdButtonActionPerformed
        // Add your handling code here:
        UserCommandsEditor commandsEditor = new UserCommandsEditor();
        commandsEditor.setValue(fileSystem.getCommands());
        UserCommandsPanel advancedPanel = new UserCommandsPanel(commandsEditor);
        DialogDescriptor dd = new DialogDescriptor (advancedPanel, org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("TIT_CommandsEditor"));//, "Advanced Properties Editor");
        dd.setHelpCtx (new HelpCtx ("VCS_CommandEditor"));
        TopManager.getDefault ().createDialog (dd).setVisible(true);
        commandsEditor.setValue(advancedPanel.getPropertyValue());
        if(dd.getValue ().equals (DialogDescriptor.OK_OPTION)) {
            fileSystem.setCommands ((Node) commandsEditor.getValue ());
        }
    }//GEN-LAST:event_cmdButtonActionPerformed

    private void varButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_varButtonActionPerformed
        // Add your handling code here:
        UserVariablesEditor variableEditor= new UserVariablesEditor();
        variableEditor.setValue( fileSystem.getVariables() );
        UserVariablesPanel variablePanel = new UserVariablesPanel (variableEditor);

        DialogDescriptor dd = new DialogDescriptor (variablePanel, org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("TIT_VariablesEditor"));//, "Advanced Properties Editor");
        dd.setHelpCtx (new HelpCtx ("VCS_VariableEditor"));
        TopManager.getDefault ().createDialog (dd).setVisible(true);
        if(dd.getValue ().equals (DialogDescriptor.OK_OPTION)) {
            fileSystem.setVariables ((Vector) variablePanel.getPropertyValue());
        }
        initAdditionalComponents (true);
    }//GEN-LAST:event_varButtonActionPerformed

    private void offLineCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_offLineCheckBoxActionPerformed
        // Add your handling code here:
        fileSystem.setOffLine(offLineCheckBox.isSelected());
    }//GEN-LAST:event_offLineCheckBoxActionPerformed

    private void promptLockCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_promptLockCheckBoxActionPerformed
        // Add your handling code here:
        fileSystem.setPromptForLockOn(promptLockCheckBox.isSelected());
        promptLockLabel.setEnabled(promptLockCheckBox.isSelected());
        promptLockTextField.setEnabled(promptLockCheckBox.isSelected());
    }//GEN-LAST:event_promptLockCheckBoxActionPerformed

    private void lockCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockCheckBoxActionPerformed
        // Add your handling code here:
        promptLockCheckBox.setEnabled(lockCheckBox.isSelected());
        fileSystem.setLockFilesOn(lockCheckBox.isSelected());
        promptLockLabel.setEnabled(promptLockCheckBox.isEnabled() && promptLockCheckBox.isSelected());
        promptLockTextField.setEnabled(promptLockCheckBox.isEnabled() && promptLockCheckBox.isSelected());
    }//GEN-LAST:event_lockCheckBoxActionPerformed

    private void promptEditCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_promptEditCheckBoxActionPerformed
        // Add your handling code here:
        fileSystem.setPromptForEditOn(promptEditCheckBox.isSelected());
        promptEditLabel.setEnabled(promptEditCheckBox.isSelected());
        promptEditTextField.setEnabled(promptEditCheckBox.isSelected());
    }//GEN-LAST:event_promptEditCheckBoxActionPerformed

    private void editCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCheckBoxActionPerformed
        // Add your handling code here:
        promptEditCheckBox.setEnabled(editCheckBox.isSelected());
        fileSystem.setCallEditFilesOn(editCheckBox.isSelected());
        promptEditLabel.setEnabled(promptEditCheckBox.isEnabled() && promptEditCheckBox.isSelected());
        promptEditTextField.setEnabled(promptEditCheckBox.isEnabled() && promptEditCheckBox.isSelected());
    }//GEN-LAST:event_editCheckBoxActionPerformed

    private void debugCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugCheckBoxActionPerformed
        // Add your handling code here:
        fileSystem.setDebug(debugCheckBox.isSelected());
    }//GEN-LAST:event_debugCheckBoxActionPerformed

    private void advancedModeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advancedModeCheckBoxActionPerformed
        // Add your handling code here:
        fileSystem.setExpertMode(advancedModeCheckBox.isSelected());
    }//GEN-LAST:event_advancedModeCheckBoxActionPerformed

  private void relMountTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_relMountTextFieldFocusLost
// Add your handling code here:
        relMountPointChanged();
  }//GEN-LAST:event_relMountTextFieldFocusLost

  private void relMountTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relMountTextFieldActionPerformed
// Add your handling code here:
        relMountPointChanged();
  }//GEN-LAST:event_relMountTextFieldActionPerformed

  private void relMountButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relMountButtonActionPerformed
// Add your handling code here:
        String work = rootDirTextField.getText();
        String dir = work + File.separator + relMountTextField.getText();
        RelativeMountDialog mountDlg = new RelativeMountDialog();
        mountDlg.setDir(work, relMountTextField.getText());
        java.awt.Dialog dlg = TopManager.getDefault().createDialog(mountDlg);
        //VcsUtilities.centerWindow (mountDlg);
        //HelpCtx.setHelpIDString (dlg.getRootPane (), CvsCustomizer.class.getName ());
        dlg.setVisible(true);
        String selected = mountDlg.getRelMount();
        if (selected != null) {
            relMountTextField.setText(selected);
            relMountPointChanged();
        }
  }//GEN-LAST:event_relMountButtonActionPerformed

    private void rootDirTextFieldFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rootDirTextFieldFocusLost
        // Add your handling code here:
        rootDirChanged ();
    }//GEN-LAST:event_rootDirTextFieldFocusLost

    private void browseButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        // Add your handling code here:
        String rootDir = browseRoot;
        if (rootDir == null) rootDir = rootDirTextField.getText ();
        browseRoot = null;
        File rootDirFile = null;
        if (rootDir == null || rootDir.trim().equals(""))
        {
            try
            {
                File defDir = new File(System.getProperty("user.home"));  // NOI18N
                if (Utilities.isUnix())
                    rootDirFile = defDir;
                else if (Utilities.isWindows())
                {
                    do
                    {
                        defDir = defDir.getParentFile();
                    }
                    while (defDir != null && defDir.getParentFile() != null);
                    if (defDir != null)
                        rootDirFile = defDir;
                }
            }
            catch (Exception ex)
            {
                rootDirFile = null;
            }
        }
        else
            rootDirFile = new File(rootDir);
        if (rootDirFile == null)
            rootDirFile = new File("");
        ChooseDirDialog chooseDir=new ChooseDirDialog(new JFrame(), rootDirFile);
        VcsUtilities.centerWindow (chooseDir);
        chooseDir.show();
        String selected=chooseDir.getSelectedDir();
        if( selected==null ){
            //D.deb("no directory selected"); // NOI18N
            return ;
        }
        rootDirTextField.setText(selected);
        rootDirChanged();
        /*
        String module = getModuleValue();
        String moduleDir = module.equals ("") ? selected : selected + java.io.File.separator + module; // NOI18N
        File dir=new File(moduleDir);
        if( !dir.isDirectory() ){
          E.err("not directory "+dir);
          return ;
    }
        try{
          rootDirTextField.setText(selected);
          fileSystem.setRootDirectory(dir);
    }
        catch (PropertyVetoException veto){
          fileSystem.debug("I can not change the working directory");
          //E.err(veto,"setRootDirectory() failed");
    }
        catch (IOException e){
          E.err(e,"setRootDirectory() failed");
    }
        */
    }//GEN-LAST:event_browseButtonActionPerformed

    private void rootDirTextFieldActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rootDirTextFieldActionPerformed
        // Add your handling code here:
        rootDirChanged ();
    }//GEN-LAST:event_rootDirTextFieldActionPerformed

    private void removeConfigButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeConfigButtonActionPerformed
        // Add your handling code here:
        String label = (String) configCombo.getSelectedItem ();
        NotifyDescriptor.Confirmation nd = new NotifyDescriptor.Confirmation (g("DLG_DeleteConfig", label), NotifyDescriptor.Confirmation.OK_CANCEL_OPTION);
        if (!NotifyDescriptor.Confirmation.OK_OPTION.equals (TopManager.getDefault ().notify (nd))) return;
        FileObject file = fileSystem.getConfigRootFO();
        if (file != null) file = file.getFileObject(cache.getProfileName(label));//(String) configNamesByLabel.get (label));
        if (file != null) {
            try {
                file.delete(file.lock());
            } catch (IOException e) {
                return;
            }
            promptForConfigComboChange = false;
            if(configCombo.getSelectedIndex() == 0) {
                if (configCombo.getModel().getSize() > 1) configCombo.setSelectedIndex(1);
            } else {
                configCombo.setSelectedIndex(0);
            }
            promptForConfigComboChange = false;
            cache.removeProfile(label);
            updateConfigurations ();
        }
        /*
        File f = new File (fileSystem.getConfigRoot()+File.separator + configNamesByLabel.get (label) + ".properties"); // NOI18N
        if(f.isFile () && f.canWrite ()) {
          f.delete ();
          // set config to fileSystem, it will be rereaded after refresh in updateConfigurations ()
          promptForConfigComboChange = false;
          if(configCombo.getSelectedIndex ()==0) configCombo.setSelectedIndex (1);
          else configCombo.setSelectedIndex (0);
          promptForConfigComboChange = false;
          updateConfigurations ();
    }
        */
    }//GEN-LAST:event_removeConfigButtonActionPerformed

    private void saveAsButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsButtonActionPerformed
        // Add your handling code here:
        FileObject dir = fileSystem.getConfigRootFO();
        ConfigSaveAsDialog chooseFile = new ConfigSaveAsDialog(new JFrame(), true, dir);
        VcsUtilities.centerWindow (chooseFile);
        chooseFile.show();
        String selected=chooseFile.getSelectedFile ();
        if (selected == null) return;
        String configLabel = chooseFile.getSelectedConfigLabel();
        FileObject file = dir.getFileObject(selected, VariableIO.CONFIG_FILE_EXT);
        boolean configExists = false;
        //String profileName = selected + "." + VariableIO.CONFIG_FILE_EXT;
        if (file == null) {
            try {
                file = dir.createData(selected, VariableIO.CONFIG_FILE_EXT);
            } catch(IOException e) {
                TopManager.getDefault().notify(new NotifyDescriptor.Message(g("MSG_CanNotCreateFile", selected+"."+VariableIO.CONFIG_FILE_EXT)));
                //E.err("Can not create file '"+selected+"'");
                return;
            }
        } else {
            if (NotifyDescriptor.Confirmation.NO_OPTION.equals (
                TopManager.getDefault ().notify (new NotifyDescriptor.Confirmation (g("DLG_OverwriteSettings", file.getName()),
                                                 NotifyDescriptor.Confirmation.YES_NO_OPTION)))
            ) {
                return;
            }
            configExists = true;
        }
        Vector variables = fileSystem.getVariables ();
        Node commands = fileSystem.getCommands();
        if (configLabel == null || configLabel.length() == 0) configLabel = selected;
        cache.addProfile(selected, configLabel, variables, commands,
                         fileSystem.getCompatibleOSs(), fileSystem.getUncompatibleOSs(), true);
        fileSystem.setConfig (configLabel);
        fileSystem.setConfigFileName(file.getNameExt());
        if (!configExists) {
            promptForConfigComboChange = false;
            updateConfigurations ();
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    promptForConfigComboChange = true;
                }
            });
        }
    }//GEN-LAST:event_saveAsButtonActionPerformed

    private void configComboItemStateChanged (java.awt.event.ItemEvent evt) {//GEN-FIRST:event_configComboItemStateChanged
        // Add your handling code here:

        switch( evt.getStateChange() ){
        case ItemEvent.SELECTED:
            String selectedLabel=(String)evt.getItem();
            //updateVariables (selectedLabel);
            /*
            E.deb ("config state changed to:"+selectedLabel);
            if(selectedLabel.equalsIgnoreCase("empty")) { // NOI18N
                removeConfigButton.setEnabled (false);
                saveAsButton.setNextFocusableComponent (propsPanel);
            } else {
                removeConfigButton.setEnabled (true);
                saveAsButton.setNextFocusableComponent (removeConfigButton);
            }
             */
            int selectedIndex=configCombo.getSelectedIndex();
            if (selectedIndex > 0 && noProfileSelectedLabel != null
                && noProfileSelectedLabel.equals(configCombo.getItemAt(0))) {
                    
                configCombo.removeItemAt(0);
                promptForConfigComboChange = false;
                firePropertyChange(PROP_PROFILE_SELECTION_CHANGED, null, null);
            }

            if (oldSelectedLabel == null) {
                oldSelectedLabel = selectedLabel;
                return ;
            }
            if (oldSelectedLabel.equals(selectedLabel)) {
                return ;
            }
            
            if (!doConfigComboChange) break;

            boolean change;
            if (promptForConfigComboChange) {
                String msg=g("MSG_Do_you_really_want_to_discard_current_commands",selectedLabel); // NOI18N
                NotifyDescriptor nd = new NotifyDescriptor.Confirmation (msg, NotifyDescriptor.YES_NO_OPTION );
                if (TopManager.getDefault().notify( nd ).equals( NotifyDescriptor.YES_OPTION ) ) {
                    change = true;
                } else {
                    change = false;
                }
            } else {
                change = true;
            }
            if (change) {
                //D.deb("yes"); // NOI18N
                // just do not display prompt for the first change if config was not edited
                promptForConfigComboChange = true;
                loadConfig(selectedLabel);
                oldSelectedLabel = selectedLabel;
                allProfilesCheckBox.setEnabled(cache.isOSCompatibleProfile(selectedLabel));
                firePropertyChange(PROP_PROFILE_SELECTION_CHANGED, null, selectedLabel);
            } else {
                if (oldSelectedLabel == null) {
                    configCombo.setSelectedIndex(0);
                } else {
                    configCombo.setSelectedItem(oldSelectedLabel);
                }
            }
            break ;
            
            case ItemEvent.DESELECTED:
                break ;
        }
    }//GEN-LAST:event_configComboItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane envScrollPane;
    private javax.swing.JLabel promptEditLabel;
    private javax.swing.JCheckBox offLineCheckBox;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel linkLabel;
    private javax.swing.JLabel relMountLabel;
    private javax.swing.JScrollPane systemEnvScrollPane;
    private javax.swing.JPanel actionPanel;
    private javax.swing.JTextField rootDirTextField;
    private javax.swing.JPanel vcsPanel;
    private javax.swing.JButton saveAsButton;
    private javax.swing.JLabel systemEnvLabel;
    private javax.swing.JComboBox configCombo;
    private javax.swing.JCheckBox promptLockCheckBox;
    private javax.swing.JButton removeConfigButton;
    private javax.swing.JLabel currentOSLabel;
    private javax.swing.JTable envTable;
    private javax.swing.JLabel promptLockLabel;
    private javax.swing.JCheckBox debugCheckBox;
    private javax.swing.JCheckBox lockCheckBox;
    private javax.swing.JButton insertEnvButton;
    private javax.swing.JButton varButton;
    private javax.swing.JCheckBox advancedModeCheckBox;
    private javax.swing.JPanel advancedPanel;
    private javax.swing.JLabel uncompatibleOSLabel;
    private javax.swing.JButton relMountButton;
    private javax.swing.JLabel compatibleOSLabel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel configPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField compatibleOSTextField;
    private javax.swing.JButton deleteEnvButton;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JCheckBox allProfilesCheckBox;
    private javax.swing.JButton browseButton;
    private javax.swing.JTextField promptLockTextField;
    private javax.swing.JPanel propsPanel;
    private javax.swing.JLabel userEnvLabel;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JButton cmdButton;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JCheckBox promptEditCheckBox;
    private javax.swing.JTextField promptEditTextField;
    private javax.swing.JTextField relMountTextField;
    private javax.swing.JTextField uncompatibleOSTextField;
    private javax.swing.JTable systemEnvTable;
    private javax.swing.JPanel environmentPanel;
    private javax.swing.JCheckBox editCheckBox;
    // End of variables declaration//GEN-END:variables

    private static final double ADVANCED_DLG_WIDTH_RELATIVE = 0.9;
    private static final double ADVANCED_DLG_HEIGHT_RELATIVE = 0.6;

    private Vector varLabels = new Vector ();
    private Vector varTextFields = new Vector ();
    private Vector varButtons = new Vector();
    private Vector varVariables = new Vector ();
    private VariableInputDialog configInputPanel = null;
    private CommandLineVcsFileSystem fileSystem = null;
    private PropertyChangeSupport changeSupport = null;
    private Vector configLabels;
    private String oldSelectedLabel = null;
    private boolean promptForConfigComboChange = true;
    private boolean doConfigComboChange = true;

    // Entries in hashtables are maintained as a cache of properties read from disk
    // and are read only. Changes are applied only to fileSystem.variables (fileSystem.commands).
    //private Hashtable configVariablesByLabel;
    //private Hashtable configAdvancedByLabel;
    //private Hashtable configNamesByLabel;
    private boolean isRootNotSetDlg = true;
    private TableSorter envTableModel;
    private TableSorter systemEnvTableModel;

    /**
     * @return true if no profile is selected
     */
    public boolean isNoneProfileSelected() {
        return (noProfileSelectedLabel != null &&
                noProfileSelectedLabel.equals(configCombo.getItemAt(0)));
    }
    
    /**
     * @deprecated It's only for a temporary use by the wizard.
     */
    public JPanel getConfigPanel() {
        return configPanel;
    }
    
    /**
     * @deprecated It's only for a temporary use by the wizard.
     */
    public JPanel getAdvancedPanel() {
        return advancedPanel;
    }
    
    /**
     * @deprecated It's only for a temporary use by the wizard.
     */
    public JPanel getEnvironmentPanel() {
        return environmentPanel;
    }
    
    private void postInitComponents() {
        removeEnterFromKeymap ();

        jTabbedPane1.setTitleAt(0, NbBundle.getMessage(VcsCustomizer.class, "DLG_Tab_Configuration"));
        jTabbedPane1.setTitleAt(1, NbBundle.getMessage(VcsCustomizer.class, "DLG_Tab_Advanced"));
        jTabbedPane1.setTitleAt(2, NbBundle.getMessage(VcsCustomizer.class, "DLG_Tab_Environment"));

        //Configuration tab
        saveAsButton.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.saveAsButton.mnemonic").charAt (0));
        removeConfigButton.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.removeConfigButton.mnemonic").charAt (0));
        jLabel2.setDisplayedMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.jLabel2.mnemonic").charAt (0));
        jLabel2.setLabelFor (rootDirTextField);
        browseButton.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.browseButton.mnemonic").charAt (0));
        relMountLabel.setDisplayedMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.relMountLabel.mnemonic").charAt (0));
        relMountLabel.setLabelFor (relMountTextField);
        relMountButton.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.relMountButton.mnemonic").charAt (0));
        allProfilesCheckBox.setMnemonic (g ("VcsCustomizer.allProfilesCheckBox.mnemonic").charAt (0));
        //Advanced tab
        varButton.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.varButton.mnemonic").charAt (0));
        cmdButton.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.cmdButton.mnemonic").charAt (0));
        advancedModeCheckBox.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.advancedModeCheckBox.mnemonic").charAt (0));
        offLineCheckBox.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.offLineCheckBox.mnemonic").charAt (0));
        editCheckBox.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.editCheckBox.mnemonic").charAt (0));
        promptEditCheckBox.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.promptEditCheckBox.mnemonic").charAt (0));
        lockCheckBox.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.lockCheckBox.mnemonic").charAt (0));
        promptLockCheckBox.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.promptLockCheckBox.mnemonic").charAt (0));
        debugCheckBox.setMnemonic (java.util.ResourceBundle.getBundle ("org/netbeans/modules/vcs/advanced/Bundle").getString("VcsCustomizer.debugCheckBox.mnemonic").charAt (0));
        compatibleOSLabel.setLabelFor(compatibleOSTextField);
        compatibleOSLabel.setDisplayedMnemonic(g ("VcsCustomizer.compatibleOSLabel.mnemonic").charAt (0));
        //compatibleOSCheckBox.setMnemonic (g ("VcsCustomizer.compatibleOSCheckBox.mnemonic").charAt (0));
        uncompatibleOSLabel.setLabelFor(uncompatibleOSTextField);
        uncompatibleOSLabel.setDisplayedMnemonic(g ("VcsCustomizer.uncompatibleOSLabel.mnemonic").charAt (0));
        //uncompatibleOSCheckBox.setMnemonic (g ("VcsCustomizer.uncompatibleOSCheckBox.mnemonic").charAt (0));
        promptLockLabel.setDisplayedMnemonic (g ("VcsCustomizer.promptLockLabel.mnemonic").charAt (0));
        promptLockLabel.setLabelFor (promptLockTextField);
        promptEditLabel.setDisplayedMnemonic (g ("VcsCustomizer.promptEditLabel.mnemonic").charAt (0));
        promptEditLabel.setLabelFor (promptEditTextField);
        //Environment tab
        envTableModel = new TableSorter(envTable.getModel());
        envTableModel.sortByColumn(0, true);
        envTable.setModel(envTableModel);
        ((javax.swing.table.DefaultTableModel) envTableModel.getModel()).setColumnIdentifiers(new String[] { NbBundle.getMessage(VcsCustomizer.class, "LBL_VarNames"), NbBundle.getMessage(VcsCustomizer.class, "LBL_VarValues") });
        envTableModel.addMouseListenerToHeaderInTable(envTable);
        systemEnvTableModel = new TableSorter(systemEnvTable.getModel());
        systemEnvTableModel.sortByColumn(0, true);
        systemEnvTable.setModel(systemEnvTableModel);
        ((javax.swing.table.DefaultTableModel) systemEnvTableModel.getModel()).setColumnIdentifiers(new String[] { NbBundle.getMessage(VcsCustomizer.class, "LBL_VarNames"), NbBundle.getMessage(VcsCustomizer.class, "LBL_VarValues"), NbBundle.getMessage(VcsCustomizer.class, "LBL_VarUsed") });
        systemEnvTableModel.addMouseListenerToHeaderInTable(systemEnvTable);
        //envTable.getCellEditor().addCellEditorListener(new CellEditorListener() {
        //});
        deleteEnvButton.setMnemonic (g ("LBL_DeleteEnv.mnemonic").charAt (0));
        insertEnvButton.setMnemonic (g ("LBL_InsertEnv.mnemonic").charAt (0));
        userEnvLabel.setDisplayedMnemonic (g ("userEnvLabel.mnemonic").charAt (0));
        userEnvLabel.setLabelFor (envTable);
        systemEnvLabel.setDisplayedMnemonic (g ("systemEnvLabel.mnemonic").charAt (0));
        systemEnvLabel.setLabelFor (systemEnvTable);

        linkLabel.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        HelpCtx.setHelpIDString (this, VcsCustomizer.class.getName ());
    }

    private void setAutoFillVars(String autoFillVarsStr) {
        String[] varsCmds = VcsUtilities.getQuotedStrings(autoFillVarsStr);
        autoFillVars = new HashMap();
        for (int i = 0; (i + 1) < varsCmds.length; i += 2) {
            autoFillVars.put(varsCmds[i], varsCmds[i+1]);
        }
    }

    private void relMountPointChanged() {
        String module = relMountTextField.getText();
        try {
            fileSystem.setRelativeMountPoint(module);
        } catch (PropertyVetoException exc) {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (isRootNotSetDlg) {
                        isRootNotSetDlg = false;
                        TopManager.getDefault().notify(new NotifyDescriptor.Message(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.canNotChangeWD")));
                        isRootNotSetDlg = true;
                    }
                }
            });
            module = null;
        } catch (IOException ioexc) {
            module = null;
        }
        if (module == null) {
            relMountTextField.setText(fileSystem.getRelativeMountPoint());
        }
        String cmd = (String) autoFillVars.get("MODULE");
        if (cmd != null) autoFillVariables(cmd);
    }
    
    //-------------------------------------------
    private void loadConfig(String label){
        if(!label.equals (fileSystem.getConfig ())) {
            Vector variables = cache.getProfileVariables(label);//(Vector) configVariablesByLabel.get(label);
            Node commands = (Node) cache.getProfileCommands(label);//configAdvancedByLabel.get(label);
            if (variables != null && commands != null) {
                fileSystem.setVariables(variables);
                fileSystem.setCommands(commands);
                fileSystem.setConfig(label);
                fileSystem.setConfigFileName(cache.getProfileName(label));//(String) configNamesByLabel.get(label));
                fileSystem.setCompatibleOSs(cache.getProfileCompatibleOSs(label));
                fileSystem.setUncompatibleOSs(cache.getProfileUncompatibleOSs(label));
                String autoFillVarsStr = (String) fileSystem.getVariablesAsHashtable().get(VAR_AUTO_FILL);
                if (autoFillVarsStr != null) setAutoFillVars(autoFillVarsStr);
                else autoFillVars.clear();
            } else {
                fileSystem.setVariables(new Vector());
                fileSystem.setCommands(new VcsCommandNode(true, null));
                autoFillVars.clear();
            }
        }
        initAdditionalComponents (true);
    }

    //-------------------------------------------
    public static void main(java.lang.String[] args) {
        JDialog dialog = new JDialog(new Frame (), true );
        VcsCustomizer customizer = new VcsCustomizer();
        dialog.getContentPane().add(customizer);
        dialog.pack ();
        dialog.show();
    }


    //-------------------------------------------
    public void addPropertyChangeListener(PropertyChangeListener l) {
        //D.deb("addPropertyChangeListener()"); // NOI18N
        changeSupport.addPropertyChangeListener(l);
    }


    //-------------------------------------------
    public void removePropertyChangeListener(PropertyChangeListener l) {
        //D.deb("removePropertyChangeListener()"); // NOI18N
        changeSupport.removePropertyChangeListener(l);
    }

    private void removeEnterFromKeymap() {
        VcsUtilities.removeEnterFromKeymap(rootDirTextField);
    }

    /*
    //-------------------------------------------
    private void advancedConfiguration () {
        JPanel panel = new JPanel ();
        panel.setLayout (new java.awt.GridBagLayout ());

        java.awt.GridBagConstraints gridBagConstraints1;
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.insets = new java.awt.Insets (12, 12, 0, 11);
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1;
        gridBagConstraints1.weighty = 0.5;


        final UserVariablesEditor variableEditor= new UserVariablesEditor();
        variableEditor.setValue( fileSystem.getVariables() );
        UserVariablesPanel variablePanel = new UserVariablesPanel (variableEditor);
        panel.add (variablePanel, gridBagConstraints1);

        PropertyEditor advancedEditor = CommandLineVcsAdvancedCustomizer.getEditor (fileSystem);
        JPanel advancedPanel = CommandLineVcsAdvancedCustomizer.getPanel (advancedEditor);

        gridBagConstraints1.gridy = 1;
        panel.add (advancedPanel, gridBagConstraints1);

        Rectangle screenBounds = org.openide.util.Utilities.getUsableScreenBounds();
        Dimension screenSize = screenBounds.getSize();
        
        screenSize.setSize((int) (screenSize.width*ADVANCED_DLG_WIDTH_RELATIVE),
                           variablePanel.getPreferredSize().height+advancedPanel.getPreferredSize().height+16);
        panel.setPreferredSize(screenSize);

        DialogDescriptor dd = new DialogDescriptor (panel, "Advanced Properties Editor");
        TopManager.getDefault ().createDialog (dd).show ();
        if(dd.getValue ().equals (DialogDescriptor.OK_OPTION)) {
            fileSystem.setVariables ((Vector) variableEditor.getValue ());
            fileSystem.setCommands ((Node) advancedEditor.getValue ());
        }
        initAdditionalComponents ();
    }
     */

    private void initAdditionalComponents (boolean doAutoFillVars) {
        varVariables = new Vector ();
        while(varLabels.size ()>0) {
            propsPanel.remove ((JComponent) varLabels.get (0));
            propsPanel.remove ((JComponent) varTextFields.get (0));
            JComponent button = (JComponent) varButtons.get(0);
            if (button != null) propsPanel.remove (button);
            varLabels.remove (0);
            varTextFields.remove (0);
            varButtons.remove(0);
        }
        if (configInputPanel != null) {
            propsPanel.remove(configInputPanel.getVariableInputPanel());
            configInputPanel = null;
        }
        for (int i = envTableModel.getRowCount(); i > 0; ) {
            ((javax.swing.table.DefaultTableModel) envTableModel.getModel()).removeRow(--i);
        }
        for (int i = systemEnvTableModel.getRowCount(); i > 0; ) {
            ((javax.swing.table.DefaultTableModel) systemEnvTableModel.getModel()).removeRow(--i);
        }
        //Vector envVariablesVector = new Vector();
        //Vector envVariablesRemovedVector = new Vector();
        envVariables.clear();
        envVariablesRemoved.clear();
        Enumeration vars = fileSystem.getVariables ().elements ();
        final Hashtable fsVars = fileSystem.getVariablesAsHashtable();
        String autoFillVarsStr = (String) fsVars.get(VAR_AUTO_FILL);
        if (autoFillVarsStr != null) setAutoFillVars(autoFillVarsStr);
        else autoFillVars.clear();
        String configInputDescriptorStr = (String) fsVars.get(VAR_CONFIG_INPUT_DESCRIPTOR);
        VariableInputDescriptor configInputDescriptor = null;
        if (configInputDescriptorStr != null) {
            try {
                configInputDescriptor = VariableInputDescriptor.parseItems(configInputDescriptorStr);
            } catch (VariableInputFormatException vifex) {
                TopManager.getDefault().getErrorManager().notify(vifex);//TopManager.getDefault().getErrorManager().annotate(vifex, "
            }
        }
        if (configInputDescriptor != null) {
            //Hashtable dlgVars = new Hashtable(fsVars);
            VariableInputDialog dlg = new VariableInputDialog(new String[] { "" }, configInputDescriptor, false, fsVars);
            dlg.setVCSFileSystem(fileSystem, fsVars);
            dlg.setGlobalInput(null);
            dlg.showPromptEach(false);
            java.awt.GridBagConstraints gridBagConstraints;
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
            //gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1;
            gridBagConstraints.weighty = 1;
            propsPanel.add(dlg.getVariableInputPanel(), gridBagConstraints);
            configInputPanel = dlg;
            dlg.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    String name = evt.getPropertyName();
                    if (name.startsWith(VariableInputDialog.PROP_VAR_CHANGED)) {
                        String varName = name.substring(VariableInputDialog.PROP_VAR_CHANGED.length());
                        variableChanged(varName, (String) evt.getOldValue(), (String) evt.getNewValue(), fsVars);
                    }
                }
            });
        }
        jLabel2.setVisible(configInputDescriptor == null);
        rootDirTextField.setVisible(configInputDescriptor == null);
        browseButton.setVisible(configInputDescriptor == null);
        relMountLabel.setVisible(configInputDescriptor == null);
        relMountTextField.setVisible(configInputDescriptor == null);
        relMountButton.setVisible(configInputDescriptor == null);
        fsVarsByName = new HashMap();
        while (vars.hasMoreElements ()) {
            VcsConfigVariable var = (VcsConfigVariable) vars.nextElement ();
            fsVarsByName.put(var.getName(), var);
            if(configInputPanel == null && var.isBasic ()) {
                JLabel lb;
                JTextField tf;
                JButton button = null;
                lb = new JLabel ();
                tf = new JTextField ();
                varLabels.add (lb);
                varTextFields.add (tf);

                java.awt.GridBagConstraints gridBagConstraints1;
                gridBagConstraints1 = new java.awt.GridBagConstraints ();
                gridBagConstraints1.gridx = 0;
                gridBagConstraints1.gridy = varLabels.size () + 4;
                gridBagConstraints1.insets = new java.awt.Insets (0, 0, 5, 12);
                gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
                gridBagConstraints1.weightx = 0;
                propsPanel.add (lb, gridBagConstraints1);
                tf.addActionListener (new java.awt.event.ActionListener () {
                                          public void actionPerformed (java.awt.event.ActionEvent evt) {
                                              variableChanged (evt);
                                          }
                                      }
                                     );
                tf.addFocusListener (new java.awt.event.FocusAdapter () {
                                         public void focusLost (java.awt.event.FocusEvent evt) {
                                             variableChanged (evt);
                                         }
                                     }
                                    );

                gridBagConstraints1.gridx = 1;
                gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
                gridBagConstraints1.insets = new java.awt.Insets (0, 0, 5, 5);
                gridBagConstraints1.weightx = 1;
                propsPanel.add (tf, gridBagConstraints1);
                varVariables.add (var);
                String varLabel = var.getLabel ().trim ();
                if(!varLabel.endsWith (":")) varLabel += ":"; // NOI18N
                lb.setText (varLabel);
                tf.setText (var.getValue ());
                tf.setToolTipText(varLabel);
                tf.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(VcsCustomizer.class, "ACS_VcsCustomizer.varTextField.textA11yDesc", varLabel));
                lb.setLabelFor(tf);
                if (var.getLabelMnemonic() != null) {
                    lb.setDisplayedMnemonic(var.getLabelMnemonic().charValue());
                }
//                if (Boolean.getBoolean("netbeans.accessibility")) {
                    if (var.getA11yName() != null)
                        tf.getAccessibleContext().setAccessibleName(var.getA11yName());
                    if (var.getA11yDescription() != null)
                        tf.getAccessibleContext().setAccessibleDescription(var.getA11yDescription());
//                }
                if (var.isLocalFile ()) {
                    button = new JButton ();
                    button.addActionListener (new BrowseLocalFile (tf));
                    button.setText (org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.browseButton.text"));
                    button.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.browseButtonFile.textA11yDesc"));
                } else if (var.isLocalDir ()) {
                    button = new JButton ();
                    button.addActionListener (new BrowseLocalDir (tf));
                    button.setText (org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.browseButton.text"));
                    button.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.browseButtonDir.textA11yDesc"));
                }
                String selector = var.getCustomSelector();
                if (selector != null && selector.length() > 0) {
                    button = new JButton ();
                    button.addActionListener (new RunCustomSelector (tf, var));
                    button.setText (org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.selectButton.text"));
                    button.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("ACS_VcsCustomizer.selectButton.textA11yDesc"));
                }
                if (button != null) {
                    button.setToolTipText(button.getText());
                    gridBagConstraints1.gridx = 2;
                    gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
                    gridBagConstraints1.weightx = 0;
                    gridBagConstraints1.insets = new java.awt.Insets (0, 0, 5, 0);
                    propsPanel.add (button, gridBagConstraints1);
                }
                varButtons.add (button);
                VcsUtilities.removeEnterFromKeymap(tf);
            }
            if (var.getName().startsWith(VcsFileSystem.VAR_ENVIRONMENT_PREFIX)) {
                String name = var.getName().substring(VcsFileSystem.VAR_ENVIRONMENT_PREFIX.length());
                //Vector row = new Vector(2);
                //row.add(name);
                //row.add(var.getValue());
                //envVariablesVector.add(row);
                ((javax.swing.table.DefaultTableModel) envTableModel.getModel()).addRow(new String[] { name, var.getValue() });
                envVariables.put(name, var);
                int row = envTableModel.getRowCount() - 1;
                javax.swing.table.TableCellEditor editor = envTable.getCellEditor(row, 1);
                editor.addCellEditorListener(new EnvCellEditorListener(name, row, 1));
            }
            if (var.getName().startsWith(VcsFileSystem.VAR_ENVIRONMENT_REMOVE_PREFIX)) {
                String name = var.getName().substring(VcsFileSystem.VAR_ENVIRONMENT_REMOVE_PREFIX.length());
                //envTableModel.addRow(new String[] { name, var.getValue() });
                envVariablesRemoved.put(name, var);
                //int row = envTableModel.getRowCount() - 1;
                //javax.swing.table.TableCellEditor editor = envTable.getCellEditor(row, 1);
                //editor.addCellEditorListener(new EnvCellEditorListener(name, row, 1));
            }
        }
        Map systemEnvVars = VcsUtilities.getSystemEnvVars();
        int row = 0;
        Set envVariablesRemovedSet = envVariablesRemoved.keySet();
        for (Iterator envVars = systemEnvVars.keySet().iterator(); envVars.hasNext(); row++) {
            String name = (String) envVars.next();
            String value = (String) systemEnvVars.get(name);
            ((javax.swing.table.DefaultTableModel) systemEnvTableModel.getModel()).addRow(new Object[] { name, value, new Boolean(!envVariablesRemovedSet.contains(name)) });
            javax.swing.table.TableCellEditor editor = systemEnvTable.getCellEditor(row, 2);
            editor.addCellEditorListener(new SystemEnvCellEditorListener(name, row, 2));
        }
        envTableModel.sortByColumn(0, true);
        systemEnvTableModel.sortByColumn(0, true);
        java.awt.Component comp = configPanel;
        while (comp!=null && !(comp instanceof java.awt.Window)) comp = comp.getParent ();
        if(comp!=null) {
            ((java.awt.Window) comp).pack ();
        }
        if (configInputPanel != null) {
            configInputPanel.updateVariableValues(fsVars);
        }
        if (doAutoFillVars) {
            for (Iterator it = autoFillVars.values().iterator(); it.hasNext(); ) {
                String cmd = (String) it.next();
                autoFillVariables(cmd);
            }
        }
        updateAdvancedConfig();
    }
    
    private void variableChanged (String varName, String oldValue, String newValue, Hashtable fsVars) {
        VcsConfigVariable var = (VcsConfigVariable) fsVarsByName.get(varName);
        Vector vars = fileSystem.getVariables();
        //System.out.println("variable changed: "+varName+" = '"+newValue+"'");
        if (var == null) {
            var = new VcsConfigVariable(varName, null, newValue, false, false, false, null);
            vars.add(var);
            fsVarsByName.put(varName, var);
        } else {
            var.setValue(newValue);
        }
        fsVars.put(varName, newValue);
        fileSystem.setVariables(vars);
        if ("ROOTDIR".equals(varName)) {
            rootDirTextField.setText(newValue);
            changeRootDir(newValue);
            fsVars.put("MODULE", relMountTextField.getText());
        } else if ("MODULE".equals(varName)) {
            relMountTextField.setText(newValue);
            rootDirChanged();
        } else {
            String cmd = (String) autoFillVars.get(varName);
            if (cmd != null) autoFillVariables(cmd);
        }
    }

    private void variableChanged (java.awt.AWTEvent evt) {
        JTextField tf = (JTextField) evt.getSource ();
        VcsConfigVariable var=null;
        for(int i=0; i<varTextFields.size () && var==null; i++) {
            if(tf == varTextFields.get (i)) {
                var = (VcsConfigVariable) varVariables.get (i);
            }
        }
        if(var!=null){
            var.setValue (tf.getText ().trim());
            if (var.getName().equals("MODULE")) { // NOI18N
                /*
                String value = var.getValue();
                if (value.length() > 0 && !value.endsWith(File.separator)) value = value.concat(File.separator);
                var.setValue(value);
                */
                rootDirChanged();
            }
            // enable fs to react on change in variables
            fileSystem.setVariables(fileSystem.getVariables());
            //D.deb("variableChanged(): filesystemVariables = "+fileSystem.getVariables()); // NOI18N
            String cmd = (String) autoFillVars.get(var.getName());
            if (cmd != null) autoFillVariables(cmd);
        } else {
            E.deb ("Error setting variable:"+tf.getText ());
        }
    }
    
    private void autoFillVariables(String cmdName) {
        VcsCommand cmd = fileSystem.getCommand(cmdName);
        if (cmd == null) return ;
        final Hashtable vars = fileSystem.getVariablesAsHashtable();
        final VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        final CommandsPool pool = fileSystem.getCommandsPool();
        pool.startExecutor(vce, fileSystem);
        RequestProcessor.postRequest(new Runnable() {
            public void run() {
                try {
                    pool.waitToFinish(vce);
                } catch (InterruptedException iexc) {
                    return ;
                }
                int len = varTextFields.size();
                for (int i = 0; i < len; i++) {
                    VcsConfigVariable var = (VcsConfigVariable) varVariables.get(i);
                    String value = (String) vars.get(var.getName());
                    if (value != null) {
                        JTextField field = (JTextField) varTextFields.get(i);
                        field.setText(value);
                        var.setValue(value);
                    }
                }
                if (configInputPanel != null) {
                    configInputPanel.updateVariableValues(vars);
                    Vector variables = fileSystem.getVariables();
                    for (Iterator it = variables.iterator(); it.hasNext(); ) {
                        VcsConfigVariable var = (VcsConfigVariable) it.next();
                        String value = (String) vars.get(var.getName());
                        var.setValue(value);
                    }
                }
                // enable fs to react on change in variables
                fileSystem.setVariables(fileSystem.getVariables());
            }
        });
    }

    /**
    * Read configurations from disk.
    *
    //-------------------------------------------
    private void updateConfigurations(){
        //D.deb("configRoot = "+fileSystem.getConfigRoot()); // NOI18N
        ArrayList configNames = VariableIO.readConfigurations(fileSystem.getConfigRootFO());
        //Vector configNames = VcsConfigVariable.readConfigurations(fileSystem.getConfigRootFO());
        D.deb("configNames="+configNames); // NOI18N

        if (configCombo.getItemCount() > 0) {
            configCombo.removeAllItems();
        }
        
        // Clear all current settings
        if (this.cache.size() >0)
            this.cache.clear();
        configLabels = new Vector();
        configVariablesByLabel = new Hashtable();
        configAdvancedByLabel = new Hashtable();
        configNamesByLabel = new Hashtable();


        String selectedConfig = fileSystem.getConfig();
        int newIndex = 0;

        for(int i = 0; i < configNames.size(); i++){
            String name = (String) configNames.get(i);
            if (CommandLineVcsFileSystem.TEMPORARY_CONFIG_FILE_NAME.equals(name)) continue;
            String label;
            if (name.endsWith(VariableIOCompat.CONFIG_FILE_EXT)) {
                Properties props = VariableIOCompat.readPredefinedProperties
                                  (fileSystem.getConfigRootFO(), name);
                //( fileSystem.getConfigRoot()+File.separator+name+".properties"); // NOI18N
                label = props.getProperty("label", g("CTL_No_label_configured"));
                this.cache.put (label,props);
            } else {
                org.w3c.dom.Document doc = VariableIO.readPredefinedConfigurations
                                          (fileSystem.getConfigRootFO(), name);
                if (doc == null) continue;
                try {
                    label = VariableIO.getConfigurationLabel(doc);
                    this.cache.put (label, doc);
                } catch (org.w3c.dom.DOMException exc) {
                    org.openide.TopManager.getDefault().notifyException(exc);
//                    variables = new Vector();
//                    advanced = null;
                    label = g("CTL_No_label_configured");
                }
            }
            
            configNamesByLabel.put(label,name);
            
            if (label == null) label = "";
            configLabels.addElement(label);
            if (label.equals(selectedConfig)) {
                newIndex = i;
            }
            //configCombo.addItem(label);
        }
        String[] sortedLabels = (String[]) configLabels.toArray(new String[0]);
        Arrays.sort(sortedLabels);
        configLabels = new Vector(Arrays.asList(sortedLabels));
        for(int i = 0; i < configLabels.size(); i++) {
            String label = (String) configLabels.elementAt(i);
            if( label.equals(selectedConfig) ){
                newIndex=i;
            }
            configCombo.addItem(label);
        }

        if (configCombo.getItemCount() > 0)
            configCombo.setSelectedIndex( newIndex );
        //System.out.println("updateConfigurations() finished, promptForConfigComboChange = "+promptForConfigComboChange);
        //promptForConfigComboChange = false;
    }
     */
    
    /**
     * Read configurations from disk.
     * @return true, when the selected configuration is successfully loaded,
     *         false otherwise.
     */
    private boolean updateConfigurations(){
        if (configCombo.getItemCount() > 0) {
            configCombo.removeAllItems();
        }
        String[] configLabels = cache.getProfilesDisplayNames();

        String selectedConfig = fileSystem.getConfig();
        //System.out.println("selectedConfig = "+selectedConfig);
        int newIndex = -1;

        /*
        if (selectedConfig != null) {
            for(int i = 0; i < configLabels.length; i++){
                if (selectedConfig.equals(configLabels[i])) {
                    newIndex = i;
                }
            }
        }
         */
        Arrays.sort(configLabels);
        if (selectedConfig == null) {
            noProfileSelectedLabel = g("CTL_No_profile_selected");
            configCombo.addItem(noProfileSelectedLabel);
        }
        int j = 0;
        boolean configsForCurrenntOs = allProfilesCheckBox.isSelected();
        doConfigComboChange = false;
        boolean doRepeat = false;
        do {
            boolean isSelectedAmongAll = false;
            for(int i = 0; i < configLabels.length; i++) {
                if (configsForCurrenntOs && !cache.isOSCompatibleProfile(configLabels[i])) {
                    if (!isSelectedAmongAll && configLabels[i].equals(selectedConfig)) {
                        isSelectedAmongAll = true;
                    }
                    continue;
                }
                if (configLabels[i].equals(selectedConfig)) {
                    newIndex = j;
                }
                j++;
                configCombo.addItem(configLabels[i]);
            }
            if (configsForCurrenntOs && newIndex < 0 && isSelectedAmongAll) {
                configsForCurrenntOs = false;
                doRepeat = true;
            } else {
                doRepeat = false;
            }
        } while (doRepeat);
        doConfigComboChange = true;

        if (configCombo.getItemCount() > 0 && newIndex >= 0) {
            promptForConfigComboChange = false;
            configCombo.setSelectedIndex( newIndex );
        }
        if (configsForCurrenntOs != allProfilesCheckBox.isSelected()) {
            allProfilesCheckBox.setSelected(configsForCurrenntOs);
        }
        allProfilesCheckBox.setEnabled(selectedConfig == null || cache.isOSCompatibleProfile(selectedConfig));
        promptForConfigComboChange = true;
        return (selectedConfig == null || configCombo.getItemCount() > 0 && newIndex >= 0);
        //System.out.println("updateConfigurations() finished, promptForConfigComboChange = "+promptForConfigComboChange);
        //if (newIndex >= 0) promptForConfigComboChange = false;
    }
    
    /*
    private void updateVariables (String label) {
        System.out.println("updateVariables("+label+")");
        if (configVariablesByLabel.get(label) != null)
            return;         // Already done
        Object obj = this.cache.get (label);
        if (obj == null)
            throw new IllegalArgumentException (label);
        Vector variables;
        Object advanced;
        if (obj instanceof java.util.Properties) {
            java.util.Properties props = (java.util.Properties) obj;
            variables = VariableIOCompat.readVariables(props);
            advanced = CommandLineVcsAdvancedCustomizer.readConfig (props);
        }
        else {
            org.w3c.dom.Document doc = (org.w3c.dom.Document) obj;
            variables = VariableIO.readVariables(doc);
            System.out.println("readVariables() = "+variables);
            advanced = CommandLineVcsAdvancedCustomizer.readConfig (doc);
        }
        configVariablesByLabel.put(label,variables);
        configAdvancedByLabel.put(label, advanced);
    }
     */

    private void updateAdvancedConfig() {
        advancedModeCheckBox.setSelected(fileSystem.isExpertMode());
        debugCheckBox.setSelected(fileSystem.getDebug());
        editCheckBox.setSelected(fileSystem.isCallEditFilesOn());
        promptEditCheckBox.setSelected(fileSystem.isPromptForEditOn());
        promptEditCheckBox.setEnabled(editCheckBox.isSelected());
        promptEditLabel.setEnabled(editCheckBox.isSelected() && fileSystem.isPromptForEditOn());
        promptEditTextField.setEnabled(editCheckBox.isSelected() && fileSystem.isPromptForEditOn());
        lockCheckBox.setSelected(fileSystem.isLockFilesOn());
        promptLockCheckBox.setSelected(fileSystem.isPromptForLockOn());
        promptLockCheckBox.setEnabled(lockCheckBox.isSelected());
        promptLockLabel.setEnabled(lockCheckBox.isSelected() && fileSystem.isPromptForLockOn());
        promptLockTextField.setEnabled(lockCheckBox.isSelected() && fileSystem.isPromptForLockOn());
        offLineCheckBox.setSelected(fileSystem.isOffLine());
        boolean isEdit = fileSystem.isEnabledEditFiles();
        editCheckBox.setEnabled(isEdit);
        promptEditCheckBox.setEnabled(isEdit && editCheckBox.isSelected());
        String message = (String) fileSystem.getVariablesAsHashtable().get(Variables.MSG_PROMPT_FOR_AUTO_EDIT);
        promptEditTextField.setText((message == null) ? "" : message);
        boolean isLock = fileSystem.isEnabledLockFiles();
        lockCheckBox.setEnabled(isLock);
        promptLockCheckBox.setEnabled(isLock && lockCheckBox.isSelected());
        message = (String) fileSystem.getVariablesAsHashtable().get(Variables.MSG_PROMPT_FOR_AUTO_LOCK);
        promptLockTextField.setText((message == null) ? "" : message);
        Set OSSet = fileSystem.getCompatibleOSs();
        if (OSSet == null) {
            compatibleOSTextField.setText("");
        } else {
            String[] OSs = (String[]) new TreeSet(OSSet).toArray(new String[0]);
            compatibleOSTextField.setText((OSs.length > 0) ? VcsUtilities.arrayToQuotedStrings(OSs) : "");
        }
        OSSet = fileSystem.getUncompatibleOSs();
        if (OSSet == null) {
            uncompatibleOSTextField.setText("");
        } else {
            String[] OSs = (String[]) new TreeSet(OSSet).toArray(new String[0]);
            uncompatibleOSTextField.setText((OSs.length > 0) ? VcsUtilities.arrayToQuotedStrings(OSs) : "");
        }
    }
    
    private String lastRootDir = null;

    //-------------------------------------------
    public void setObject(Object bean){
        //Thread.currentThread().dumpStack();
        D.deb("setObject("+bean+")"); // NOI18N
        fileSystem=(CommandLineVcsFileSystem) bean;

        String defaultRoot = VcsFileSystem.substractRootDir (fileSystem.getRootDirectory ().toString (), fileSystem.getRelativeMountPoint());
        browseRoot = null;
        try {
            fileSystem.setRootDirectory(new File(defaultRoot));
        } catch (PropertyVetoException vetoExc) {
            browseRoot = defaultRoot;
            defaultRoot = "";
        } catch (IOException ioExc) {
            browseRoot = defaultRoot;
            defaultRoot = "";
        }
        if (browseRoot != null) {
            try {
                fileSystem.setRootDirectory(new File(""));
            } catch (PropertyVetoException vetoExc) {
            } catch (IOException ioExc) {}
        }
        cache = new ProfilesCache(fileSystem.getConfigRootFO());
        rootDirTextField.setText (defaultRoot);
        lastRootDir = defaultRoot;
        String module = fileSystem.getRelativeMountPoint();
        if (module == null) module = "";
        try {
            fileSystem.setRelativeMountPoint(module);
        } catch (PropertyVetoException exc) {
            module = "";
        } catch (IOException ioexc) {
            module = "";
        }
        allProfilesCheckBox.setSelected(true);
        relMountTextField.setText(module);
        oldSelectedLabel = fileSystem.getConfig();
        updateConfigurations();
        updateAdvancedConfig();
        initAdditionalComponents (false);
        currentOSLabel.setText(org.openide.util.NbBundle.getMessage(VcsCustomizer.class, "VcsCustomizer.currentOSLabel.txt", System.getProperty("os.name")));
        /*
            // find if this fs is in the repository
            boolean alreadyMounted = false;
            Enumeration en = TopManager.getDefault ().getRepository ().getFileSystems ();
            while (en.hasMoreElements ()) {
              if(fileSystem==en.nextElement ()) alreadyMounted = true;
            }
            System.out.println ("mounted:"+alreadyMounted);
            if(alreadyMounted) {
              String label = fileSystem.getConfig ();
              Object backupV = configVariablesByLabel.get (label);
              Object backupC = configAdvancedByLabel.get (label);
              
              // fake config in hashtables by values from fs
              configVariablesByLabel.put (label, fileSystem.getVariables ());
              configAdvancedByLabel.put (label, fileSystem.getAdvancedConfig ());
              oldIndex = -1;
              // let it read variables and commands
              configCombo.setSelectedItem (label);
              configVariablesByLabel.put (label, backupV);
              configAdvancedByLabel.put (label, backupC);
            }
        */
    }
    
    private void rootDirChanged () {
        // root dir set by hand
        final String selected= rootDirTextField.getText ();
        if( selected==null ){
            //D.deb("no directory selected"); // NOI18N
            return ;
        }
        RequestProcessor.postRequest(new Runnable() {
            public void run() {
                changeRootDir(selected);
            }
        });
    }
    
    private void changeRootDir(String selected) {
        File root = new File(selected);
        try{
            fileSystem.setRootDirectory(root);
            //rootDirTextField.setText(selected);
            String cmd = (String) autoFillVars.get("ROOTDIR");
            if (cmd != null) autoFillVariables(cmd);
            if (lastRootDir != null && !selected.equals(lastRootDir)) {
                lastRootDir = selected;
                if (!(new File(selected, relMountTextField.getText()).exists())) {
                    relMountTextField.setText("");
                    relMountPointChanged();
                }
            }
        } catch (PropertyVetoException veto){
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (isRootNotSetDlg) {
                        isRootNotSetDlg = false;
                        TopManager.getDefault().notify(new NotifyDescriptor.Message(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.canNotChangeWD")));
                        isRootNotSetDlg = true;
                    }
                }
            });
            //fileSystem.debug(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.canNotChangeWD"));
            //E.err(veto,"setRootDirectory() failed"); // NOI18N
            rootDirTextField.setText(VcsFileSystem.substractRootDir(fileSystem.getRootDirectory().toString(), fileSystem.getRelativeMountPoint()));
            lastRootDir = rootDirTextField.getText();
        } catch (IOException e){
            //E.err(e,"setRootDirectory() failed");
            final String badDir = root.toString();
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (isRootNotSetDlg) {
                        isRootNotSetDlg = false;
                        TopManager.getDefault().notify(new NotifyDescriptor.Message(MessageFormat.format(org.openide.util.NbBundle.getBundle(VcsCustomizer.class).getString("VcsCustomizer.cannotSetDirectory"), new Object[] { badDir } )));
                        isRootNotSetDlg = true;
                    }
                }
            });
            rootDirTextField.setText(VcsFileSystem.substractRootDir(fileSystem.getRootDirectory().toString(), fileSystem.getRelativeMountPoint()));
            lastRootDir = rootDirTextField.getText();
        }
    }

    //------------------------------
    private class BrowseLocalFile implements java.awt.event.ActionListener {

        private JTextField tf;

        public BrowseLocalFile(JTextField tf) {
            this.tf = tf;
        }

        public void actionPerformed (java.awt.event.ActionEvent evt) {
            ChooseFileDialog chooseFile=new ChooseFileDialog(new JFrame(), new File(tf.getText ()), false);
            VcsUtilities.centerWindow (chooseFile);
            chooseFile.show();
            String selected=chooseFile.getSelectedFile();
            if( selected==null ){
                //D.deb("no directory selected"); // NOI18N
                return ;
            }
            tf.setText(selected);
            variableChanged(new java.awt.event.ActionEvent(tf, 0, "")); // NOI18N
        }
    }

    //------------------------------
    private class BrowseLocalDir implements java.awt.event.ActionListener {

        private JTextField tf;

        public BrowseLocalDir(JTextField tf) {
            this.tf = tf;
        }

        public void actionPerformed (java.awt.event.ActionEvent evt) {
            ChooseDirDialog chooseDir = new ChooseDirDialog(new JFrame(), new File(tf.getText ()));
            VcsUtilities.centerWindow (chooseDir);
            chooseDir.show();
            String selected=chooseDir.getSelectedDir();
            if( selected==null ){
                //D.deb("no directory selected"); // NOI18N
                return ;
            }
            tf.setText(selected);
            variableChanged(new java.awt.event.ActionEvent(tf, 0, "")); // NOI18N
        }
    }

    private class RunCustomSelector implements java.awt.event.ActionListener {

        private JTextField tf;
        private VcsConfigVariable selector;

        public RunCustomSelector(JTextField tf, VcsConfigVariable selector) {
            this.tf = tf;
            this.selector = selector;
        }

        public void actionPerformed (java.awt.event.ActionEvent evt) {
            VcsCommand cmd = fileSystem.getCommand(selector.getCustomSelector());
            if (cmd == null) {
                NotifyDescriptor.Message nd = new NotifyDescriptor.Message (g("DLG_SelectorNotExist", selector.getCustomSelector()), NotifyDescriptor.WARNING_MESSAGE);
                TopManager.getDefault ().notify (nd);
                return ;
            }
            final Hashtable vars = fileSystem.getVariablesAsHashtable();
            VcsCommandExecutor executor = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
            final CommandsPool pool = fileSystem.getCommandsPool();
            final StringBuffer selection = new StringBuffer();
            pool.addCommandListener(new CommandListener() {
                
                public void commandPreprocessing(VcsCommandExecutor vce) {
                }
                
                public void commandPreprocessed(VcsCommandExecutor vce, boolean status) {
                }
                
                public void commandStarted(VcsCommandExecutor vce) {
                }
                
                public void commandDone(VcsCommandExecutor vce) {
                    if (selection.length() > 0) {
                        tf.setText(selection.toString());
                        variableChanged(new java.awt.event.ActionEvent(tf, 0, "")); // NOI18N
                    }
                    pool.removeCommandListener(this);
                }
            });
            executor.addDataOutputListener(new CommandDataOutputListener() {
                public void outputData(String[] elements) {
                    if (elements.length > 0) {
                        selection.append(elements[0]);
                    }
                }
            });
            pool.startExecutor(executor, fileSystem);
        }
    }
    
    private class EnvCellEditorListener implements javax.swing.event.CellEditorListener {
        
        private String name;
        private int row;
        private int col;
        
        EnvCellEditorListener(String name, int row, int col) {
            this.name = name;
            this.row = row;
            this.col = col;
        }
        
        public void editingCanceled(javax.swing.event.ChangeEvent e) {
        }
        public void editingStopped(javax.swing.event.ChangeEvent e) {
            //System.out.println("editingStopped("+name+", "+row+", "+col+")");
            VcsConfigVariable var = (VcsConfigVariable) envVariables.get(name);
            if (var != null) {
                String value = (String) envTableModel.getModel().getValueAt(row, col);
                //System.out.println("  value = "+value);
                var.setValue(value);
                fileSystem.setEnvironmentVar(name, value);
                fileSystem.variableChanged(var.getName());
            }
        }
    }

    private class SystemEnvCellEditorListener implements javax.swing.event.CellEditorListener {
        
        private String name;
        private int row;
        private int col;
        
        SystemEnvCellEditorListener(String name, int row, int col) {
            this.name = name;
            this.row = row;
            this.col = col;
        }
        
        public void editingCanceled(javax.swing.event.ChangeEvent e) {
        }
        public void editingStopped(javax.swing.event.ChangeEvent e) {
            //System.out.println("editingStopped("+name+", "+row+", "+col+")");
            Boolean remove = (Boolean) systemEnvTableModel.getModel().getValueAt(row, col);
            //System.out.println("  remove = "+remove);
            Vector vars = fileSystem.getVariables();
            if (Boolean.TRUE.equals(remove)) {
                VcsConfigVariable var = (VcsConfigVariable) envVariablesRemoved.remove(name);
                if (var == null) return ;
                vars.remove(var);
            } else {
                if (envVariablesRemoved.containsKey(name)) return ;
                VcsConfigVariable var = new VcsConfigVariable(VcsFileSystem.VAR_ENVIRONMENT_REMOVE_PREFIX + name,
                                               null, "", false, false, false, null);
                vars.add(var);
                envVariablesRemoved.put(name, var);
            }
            fileSystem.setVariables(vars);
        }
    }

    //-------------------------------------------
    String g(String s) {
        return NbBundle.getBundle
               ("org.netbeans.modules.vcs.advanced.Bundle").getString (s);
    }
    String  g(String s, Object obj) {
        return MessageFormat.format (g(s), new Object[] { obj });
    }
    String g(String s, Object obj1, Object obj2) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2 });
    }
    String g(String s, Object obj1, Object obj2, Object obj3) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2, obj3 });
    }


}

