/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.util;

import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;
import javax.swing.JTextField;
import javax.swing.event.*;

import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.spi.vcs.VcsCommandsProvider;

/**
 * Dialog that enables users to set variable values before running the command.
 *
 * @author  Martin Entlicher
 */
public class VariableInputDialog extends javax.swing.JPanel {

    //public static final String PROMPT_DIR = "_DIR";
    //public static final String PROMPT_FILE = "_FILE";
    //public static final String PROMPT_DATE_CVS = "_DATE_CVS";
    //public static final String PROMPT_DEFAULT_VALUE_SEPARATOR = "\"";
    
    /**
     * When a map of variables contains this one (with value of <code>true</code>)
     * after the selector command finish, the components are updated with values
     * of returned variables.
     */
    public static final String VAR_UPDATE_CHANGED_FROM_SELECTOR = "UPDATE CHANGED VARIABLES FROM SELECTOR";
    
    /**
     * This property (which name has the variable name appended) is fired
     * when a variable value has changed.
     */
    public static final String PROP_VAR_CHANGED = "varChanged"; // NOI18N
    
    /**
     * This property (which name has the variable name appended) is fired
     * when a variable value has changed.
     */
    public static final String PROP_VARIABLES_CHANGED = "variablesChanged"; // NOI18N
    
    private static final int TEXTFIELD_COLUMNS = 20;
    private static final int TEXTAREA_COLUMNS = 40;
    private static final int TEXTAREA_ROWS = 6;
    private static final int DEFAULT_INDENT = 20;
    private boolean validInput = false;
    private javax.swing.JLabel[] varPromptLabels = new javax.swing.JLabel[0];
    private javax.swing.JLabel[] filePromptLabels = new javax.swing.JLabel[0];
    private javax.swing.JLabel[] userPromptLabels = new javax.swing.JLabel[0];
    private String[]             userPromptLabelTexts = null;
    private javax.swing.JTextArea[] filePromptAreas = new javax.swing.JTextArea[0];
    private javax.swing.JTextField[] varPromptFields = new javax.swing.JTextField[0];
    private javax.swing.JTextField[] userPromptFields = new javax.swing.JTextField[0];
    private javax.swing.JCheckBox[] varAskCheckBoxes = new javax.swing.JCheckBox[0];
    private int labelOffset = 0;
    private String[] fileNames = new String[0];
    
    private VariableInputDialog.FilePromptDocumentListener docListener = null;
    private Object docIdentif = null;
    
    private CommandExecutionContext executionContext = null;
    private Hashtable vars = null;
    private boolean expert = false;
    private String exec = null;
    
    private VariableInputDescriptor inputDescriptor;
    private VariableInputDescriptor globalDescriptor = null;
    private ArrayList globalVars = null;
    private String globalLabel = null;
    
    private ArrayList actionList = new ArrayList();
    private ArrayList closeListeners = new ArrayList();
    private ArrayList historyListeners = new ArrayList();
    private ArrayList focusListenersToCallBeforeValidate = new ArrayList();
    private int historySize = 0;
    private int currentHistory = 0;
    private int promptAreaNum = 0;
    
    private HashMap awtComponentsByVars = new HashMap();
    private HashMap componentsByVars = new HashMap();
    /** The map of disabled components as keys and a set of variables
     *  that disabled them as values. */
    private HashMap disabledComponents = new HashMap();
    private java.awt.Component firstFocusedComponent;
    /**
     * The name of the variable, that contains pairs of variables and commands.
     * When the variables listed here change their value, the corresponding command
     * is executed to fill values of remaining variables. This can be used to automatically
     * fill in VCS configuartion, when it can be obtained from local configuration files.
     */
    public static final String VAR_AUTO_FILL = "AUTO_FILL_VARS";
    private HashMap autoFillVars = new HashMap();   
    private volatile org.openide.util.RequestProcessor.Task autoFillTask;
    private volatile String lastCommandName;
    
    static final long serialVersionUID = 8363935602008486018L;
    
    /** Creates new form VariableInputDialog. This JPanel should be used
     * with DialogDescriptor to get the whole dialog.
     * @param files the files to get the input for
     * @param inputDescriptor the input descriptor
     * @param expert the expert mode
     */
    public VariableInputDialog(String[] files, VariableInputDescriptor inputDescriptor, boolean expert) {
        this(files, inputDescriptor, expert, null);
    }

    /** Creates new form VariableInputDialog. This JPanel should be used
     * with DialogDescriptor to get the whole dialog.
     * @param files the files to get the input for
     * @param inputDescriptor the input descriptor
     * @param expert the expert mode
     * @param vars the filesystem variables
     */
    public VariableInputDialog(String[] files, VariableInputDescriptor inputDescriptor, boolean expert, Hashtable vars) {      
        initComponents();
        this.inputDescriptor = inputDescriptor;
        this.expert = expert;
        this.vars = vars;            
        setAutoFillVars(inputDescriptor.getAutoFillVars());
        firstFocusedComponent = initComponentsFromDescriptor(inputDescriptor, variablePanel);
        currentHistory = historySize;
        //System.out.println("currentHistory = "+currentHistory);
        prevButton.setEnabled(currentHistory > 0);
        nextButton.setEnabled(false);
        setMnemonics();
        //initFileLabel(files[0]);
        if (inputDescriptor != null) {
            setA11y(this, inputDescriptor);
        }
    }
     
    private void setAutoFillVars(String autoFillVarsStr) {
        if(autoFillVarsStr == null || autoFillVarsStr.length() == 0 )
            return;
        String[] varsCmds = VcsUtilities.getQuotedStrings(autoFillVarsStr);
        autoFillVars = new HashMap();
        for (int i = 0; (i + 1) < varsCmds.length; i += 2) {
            autoFillVars.put(varsCmds[i], varsCmds[i+1]);
        }
        
    }
    
    private void doAutofill(){  //todo -try to run only command for certain value - not always for all
        for (Iterator it = autoFillVars.values().iterator(); it.hasNext(); ) {
                String cmd = (String) it.next();
                autoFillVariables(cmd);
            }
    }
    
    private void autoFillVariables(String cmdName) {        
        VcsCommandsProvider provider = executionContext.getCommandsProvider();
        VcsDescribedCommand cmd = (VcsDescribedCommand)provider.createCommand(cmdName);
        if (cmd == null) return ;
        if (autoFillTask != null && cmdName.equals(lastCommandName)) {
            autoFillTask.schedule(100);
        } else {
            lastCommandName = cmdName;
            autoFillTask = org.openide.util.RequestProcessor.postRequest(new AutoFillRunner(cmd), 100);
        }
    }
    
    
    private void setMnemonics() {
        prevButton.setMnemonic(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.prevButton.mnemonic").charAt(0));
        prevButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.prevButton.a11yName"));
        prevButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.prevButton.a11yDescription"));
        nextButton.setMnemonic(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.nextButton.mnemonic").charAt(0));
        nextButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.nextButton.a11yName"));
        nextButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.nextButton.a11yDescription"));
        asDefaultButton.setMnemonic(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("asDefaultButton.mnemonic").charAt(0));
        asDefaultButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("asDefaultButton.a11yName"));
        asDefaultButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("asDefaultButton.a11yDescription"));
        getDefaultButton.setMnemonic(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("getDefaultButton.mnemonic").charAt(0));
        getDefaultButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("getDefaultButton.a11yName"));
        getDefaultButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("getDefaultButton.a11yDescription"));
        promptEachCheckBox.setMnemonic(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.promptEachCheckBox.mnemonic").charAt(0));
        promptEachCheckBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.promptEachCheckBox.a11yName"));
        promptEachCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.promptEachCheckBox.a11yDescription"));
        variableTabbedPane.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.variableTabbedPane.a11yName"));
        variableTabbedPane.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.variableTabbedPane.a11yDescription"));
    }

    public void setFilePromptDocumentListener(VariableInputDialog.FilePromptDocumentListener docListener) {
        this.docListener = docListener;
    }
    
    public void setFilePromptDocumentListener(VariableInputDialog.FilePromptDocumentListener docListener, Object docIdentif) {
        this.docListener = docListener;
        this.docIdentif = docIdentif;
    }
    
    public void setCmdName(String cmdDisplayName) {
        if (cmdDisplayName != null) {
            variableTabbedPane.setTitleAt(0, cmdDisplayName);
            jPanel1.getAccessibleContext().setAccessibleName(cmdDisplayName);
            jPanel1.getAccessibleContext().setAccessibleDescription(cmdDisplayName);
        }
    }
    
    public VariableInputDialog.FilePromptDocumentListener getFilePromptDocumentListener() {
        return this.docListener;
    }
    
    /**
     * Get the input decriptor from which this dialog was created.
     * @return The variable input descriptor or <code>null</code>.
     */
    public VariableInputDescriptor getInputDescriptor() {
        return this.inputDescriptor;
    }
    
    /**
     * Get the global input decriptor from which this dialog was created.
     * @return The global variable input descriptor or <code>null</code>.
     */
    public VariableInputDescriptor getGlobalInputDescriptor() {
        return this.globalDescriptor;
    }

    /**
     * Get the component, that should have the initial focus in this dialog.
     */
    public java.awt.Component getInitialFocusedComponent() {
        return firstFocusedComponent;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        navigationPanel = new javax.swing.JPanel();
        prevButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        asDefaultButton = new javax.swing.JButton();
        getDefaultButton = new javax.swing.JButton();
        variableTabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        variablePanel = new javax.swing.JPanel();
        pushPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        globalInputPanel = new javax.swing.JPanel();
        pushPanel2 = new javax.swing.JPanel();
        promptEachSeparator = new javax.swing.JSeparator();
        promptEachCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        navigationPanel.setLayout(new java.awt.GridBagLayout());

        prevButton.setText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.prevButton.text"));
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        navigationPanel.add(prevButton, gridBagConstraints);

        nextButton.setText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.nextButton.text"));
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        navigationPanel.add(nextButton, gridBagConstraints);

        asDefaultButton.setText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("asDefaultButton.text"));
        asDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                asDefaultButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        navigationPanel.add(asDefaultButton, gridBagConstraints);

        getDefaultButton.setText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("getDefaultButton.text"));
        getDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getDefaultButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        navigationPanel.add(getDefaultButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(navigationPanel, gridBagConstraints);

        variableTabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        variablePanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 100;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weighty = 1.0;
        variablePanel.add(pushPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        jPanel1.add(variablePanel, gridBagConstraints);

        variableTabbedPane.addTab("jPanel1", null, jPanel1, "");

        jPanel2.setLayout(new java.awt.GridBagLayout());

        globalInputPanel.setLayout(new java.awt.GridBagLayout());

        pushPanel2.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 100;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weighty = 1.0;
        globalInputPanel.add(pushPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        jPanel2.add(globalInputPanel, gridBagConstraints);

        variableTabbedPane.addTab("jPanel2", null, jPanel2, "");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(variableTabbedPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 12);
        add(promptEachSeparator, gridBagConstraints);

        promptEachCheckBox.setText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.promptEachCheckBox.text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 12);
        add(promptEachCheckBox, gridBagConstraints);

    }//GEN-END:initComponents

    private void getDefaultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getDefaultButtonActionPerformed
        // Add your handling code here:
        inputDescriptor.setDefaultValues();
        if (globalDescriptor != null) {
            globalDescriptor.setDefaultValues();
        }
        int lastHistory = currentHistory;
        currentHistory = historySize;
        changeHistory(lastHistory, currentHistory);
        prevButton.setEnabled(currentHistory > 0);
        nextButton.setEnabled(currentHistory < historySize);
    }//GEN-LAST:event_getDefaultButtonActionPerformed

    private void asDefaultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_asDefaultButtonActionPerformed
        // Add your handling code here:
        inputDescriptor.setValuesAsDefault();
        if (globalDescriptor != null) {
            globalDescriptor.setValuesAsDefault();
        }
    }//GEN-LAST:event_asDefaultButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        // Add your handling code here:
        if (currentHistory < historySize) {
            changeHistory(currentHistory, currentHistory + 1);
            currentHistory++;
        }
        if (currentHistory > 0) prevButton.setEnabled(true);
        if (currentHistory >= historySize) nextButton.setEnabled(false);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed
        // Add your handling code here:
        if (currentHistory > 0) {
            changeHistory(currentHistory, currentHistory - 1);
            currentHistory--;
        }
        if (currentHistory == 0) prevButton.setEnabled(false);
        if (currentHistory < historySize) nextButton.setEnabled(true);
    }//GEN-LAST:event_prevButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton asDefaultButton;
    private javax.swing.JButton getDefaultButton;
    private javax.swing.JPanel globalInputPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel navigationPanel;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton prevButton;
    private javax.swing.JCheckBox promptEachCheckBox;
    private javax.swing.JSeparator promptEachSeparator;
    private javax.swing.JPanel pushPanel1;
    private javax.swing.JPanel pushPanel2;
    private javax.swing.JPanel variablePanel;
    private javax.swing.JTabbedPane variableTabbedPane;
    // End of variables declaration//GEN-END:variables

    /**
     * Initialize variable input components from a variable input descriptor.
     * @param inputDescriptor the variable input descriptor
     * @param inputPanel the panel on which are the components created
     * @return The first component, that should be set as the initial focused component.
     */
    private java.awt.Component initComponentsFromDescriptor(VariableInputDescriptor inputDescriptor,
                                                            javax.swing.JPanel inputPanel) {
        int gridy = 0;
        java.awt.Component firstComponent = null;
        if (inputDescriptor != null) {
            java.awt.Component[] mainComponent_ptr = new java.awt.Component[1];
            VariableInputComponent[] components = inputDescriptor.components();
            if (components.length > 0) historySize = Integer.MAX_VALUE;
            HashMap varsToEnableDisable = new HashMap();
            HashMap[] componentVars = new HashMap[components.length];
            for (int i = 0; i < components.length; i++) {
                gridy = addComponent(components[i], gridy, inputPanel, 0,
                                     varsToEnableDisable, mainComponent_ptr);
                if (i == 0) {
                    firstComponent = mainComponent_ptr[0];
                }
                historySize = Math.min(historySize, components[i].getHistorySize());
                if (varsToEnableDisable.size() > 0) {
                    componentVars[i] = varsToEnableDisable;
                    varsToEnableDisable = new HashMap();
                }
            }
            for (int i = 0; i < componentVars.length; i++) {
                if (componentVars[i] != null) {
                    varsToEnableDisable = componentVars[i];
                    for (Iterator it = varsToEnableDisable.keySet().iterator(); it.hasNext(); ) {
                        String[] variables = (String[]) it.next();
                        boolean enable = ((Boolean) varsToEnableDisable.get(variables)).booleanValue();
                        enableComponents(variables, enable, components[i].getVariable());
                    }
                }
            }
        }
        labelOffset = gridy;
        return firstComponent;
    }
    
    private int addComponent(final VariableInputComponent component, int gridy,
                             javax.swing.JPanel inputPanel, int leftInset,
                             HashMap varsToEnableDisable,
                             java.awt.Component[] mainComponent_ptr) {
        if (VariableInputComponent.isVarConditionMatch(component.getVarConditions(), vars)) {
            if (expert || !component.isExpert()) {
                int componentId = component.getComponent();
                switch (componentId) {
                    case VariableInputDescriptor.INPUT_PROMPT_FIELD:
                        addVarPromptField(component, gridy, inputPanel, leftInset,
                                          false, mainComponent_ptr);
                        gridy++;
                        break;
                    case VariableInputDescriptor.INPUT_PROMPT_PASSWD:
                        addVarPromptField(component, gridy, inputPanel, leftInset,
                                          true, mainComponent_ptr);
                        gridy++;
                        break;
                    case VariableInputDescriptor.INPUT_PROMPT_AREA:
                        addVarPromptArea(component, gridy, promptAreaNum++,
                                         inputPanel, leftInset, mainComponent_ptr);
                        gridy += 2;
                        break;
                    case VariableInputDescriptor.INPUT_ASK:
                        addAskChBox(component, gridy, inputPanel, leftInset,
                                    varsToEnableDisable, mainComponent_ptr);
                        gridy++;
                        break;
                    case VariableInputDescriptor.INPUT_SELECT_RADIO:
                        gridy = addSelectRadio(component, gridy, inputPanel,
                                               leftInset, varsToEnableDisable,
                                               mainComponent_ptr);
                        break;
                    case VariableInputDescriptor.INPUT_SELECT_COMBO:
                        addSelectCombo(component, gridy, inputPanel, leftInset,
                                       false, varsToEnableDisable, mainComponent_ptr);
                        gridy++;
                        break;
                    case VariableInputDescriptor.INPUT_SELECT_COMBO_EDITABLE:
                        addSelectCombo(component, gridy, inputPanel, leftInset,
                                       true, varsToEnableDisable, mainComponent_ptr);
                        gridy++;
                        break;
                    case VariableInputDescriptor.INPUT_TEXT:
                        addTextComponent(component, gridy, inputPanel, leftInset,
                                         mainComponent_ptr);
                        gridy++;
                        break;
                    case VariableInputDescriptor.INPUT_GLOBAL:
                        setGlobalVars(component);
                        break;
                }
            } else {
                addActionToProcess(new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        if (vars != null) {
                            vars.put(component.getVariable(), component.getDefaultValue());
                        }
                    }
                });
            }
        }
        return gridy;
    }
    
    /**
     * Test if the input is valid and warn the user if it is not.
     * @return true if the input is valid, false otherwise
     */
    private boolean testValidInput() {
        if (inputDescriptor == null) return true;
        for (Iterator flIt = focusListenersToCallBeforeValidate.iterator(); flIt.hasNext(); ) {
            FocusListener fl = (FocusListener) flIt.next();
            fl.focusLost(null);
        }
        VariableInputValidator validator = inputDescriptor.validate();
        boolean valid = validator.isValid();
        if (!valid) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(validator.getMessage(), NotifyDescriptor.Message.WARNING_MESSAGE));
        }
        return valid;
    }
    
    public ActionListener getActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (ev.getID() == ActionEvent.ACTION_PERFORMED) {
                    if (NotifyDescriptor.OK_OPTION.equals(ev.getSource())) {
                        if (testValidInput()) {
                            validInput = true;
                            for (Iterator it = closeListeners.iterator(); it.hasNext(); ) {
                                ((ActionListener) it.next()).actionPerformed(ev);
                            }
                            closeListeners.clear();
                            //setVisible(false);
                        }
                        //writeFileContents();
                        //processActions(); -- do not do it now in AWT !
                    } else {
                        validInput = false;
                        freeReferences();
                    }
                }
            }
        };
    }
    
    public void addCloseListener(ActionListener closeListener) {
        this.closeListeners.add(closeListener);
    }
    
    private void freeReferences() {
        executionContext = null;
        docIdentif = null;
        docListener = null;
    }
    
    private void addActionToProcess(ActionListener l) {
        actionList.add(l);
    }
    
    public void processActions() {
        for (Iterator it = actionList.iterator(); it.hasNext(); ) {
            ActionListener listener = (ActionListener) it.next();
            listener.actionPerformed(null);
        }
        freeReferences();
    }
    
    private void addHistoryListener(HistoryListener l) {
        historyListeners.add(l);
    }
    
    private void changeHistory(int index1, int index2) {
        for (Iterator it = historyListeners.iterator(); it.hasNext(); ) {
            HistoryListener historyListener = (HistoryListener) it.next();
            historyListener.changeHistory(index1, index2);
        }
    }
    
    /**
     * Test, whether the input in this dialog is valid and variables can be assigned.
     * @return true if the input is valid, false otherwise
     */
    public boolean isValidInput() {
        return validInput;
    }

    /**
     * Set the VCS file system, that is needed to execute the selector command
     * and the variables table.
     */
    public void setExecutionContext(CommandExecutionContext executionContext, Hashtable vars) {
        this.executionContext = executionContext;
        this.vars = vars;
    }
    
    /**
     * Use this method to supply new variable values to the components.
     */
    public void updateVariableValues(Hashtable vars) {
        updateVariableValues(vars, true);
    }
    
    /**
     * Use this method to supply new variable values to the components.
     */
    private void updateVariableValues(Hashtable vars, boolean resetVars) {
        List propertyChangeEvents = new ArrayList(); // The list of properties that needs to be fired
        Set eventsToAdjust = new HashSet(); // The set of properties that needs to be adjusted
        for (Iterator it = vars.keySet().iterator(); it.hasNext(); ) {
            String varName = (String) it.next();
            String varValue = (String) vars.get(varName);
            if (!resetVars) this.vars.put(varName, varValue);
            VariableInputComponent inComponent = (VariableInputComponent) componentsByVars.get(varName);
            if (inComponent == null) {
                continue;
            }
            java.awt.Component[] components = (java.awt.Component[]) awtComponentsByVars.get(varName);
            
            String oldValue = inComponent.getValue();
            if (varValue != null && varValue.equals(oldValue)) {
                // The values equals, but radio buttons might need to be set
                // in order to clean up potential unselected buttons.
                if (components != null) {
                    for (int i = 0; i < components.length; i++) {
                        if (components[i] instanceof javax.swing.JRadioButton) {
                            javax.swing.JRadioButton button = (javax.swing.JRadioButton) components[i];
                            javax.swing.ButtonModel model = button.getModel();
                            if (model instanceof javax.swing.DefaultButtonModel) {
                                javax.swing.ButtonGroup group = ((javax.swing.DefaultButtonModel) model).getGroup();
                                String selectedValue = selectButton(varValue, inComponent.subComponents(), group);
                            }
                        }
                    }
                }
                continue;
            }
           
            //System.out.println("  VAR '"+varName+"' = '"+varValue+"'");
            inComponent.setValue(varValue);
            if (components != null) {
                for (int i = 0; i < components.length; i++) {
                    java.awt.Component component = components[i];
                    if (component instanceof javax.swing.text.JTextComponent) {
                        varValue = Variables.expand(vars, varValue, false);
                        //if (varValue != null && varValue.equals(oldValue)) { - do not check it here. We need to have the text set.
                        //    continue;
                        //}
                        inComponent.setValue(varValue);
                        ((javax.swing.text.JTextComponent) component).setText(varValue);
                        PropertyChangeEvent pcev = new PropertyChangeEvent(this, PROP_VAR_CHANGED+varName, oldValue, varValue);
                        if (inComponent.isExpandableDefaultValue()) {
                            //varValue = inComponent.getDefaultValue(); 
                            // Not to persistently store the expanded value!
                            pcev.setPropagationId(inComponent.getDefaultValue());
                        }
                        // setText does not fire anything, we need to do that later.
                        propertyChangeEvents.add(pcev);
                    } else if (component instanceof javax.swing.JCheckBox) {
                        javax.swing.JCheckBox chbox = (javax.swing.JCheckBox) component;
                        if (varValue == null) {
                            varValue = inComponent.getDefaultValue();
                        }
                        if (varValue != null) {
                            varValue = Variables.expand(vars, varValue, false);
                            inComponent.setValue(varValue);
                            String valueSelected = inComponent.getValueSelected();
                            boolean selected;
                            if (valueSelected != null) {
                                selected = varValue.equals(valueSelected);
                            } else {
                                selected = Boolean.TRUE.toString().equalsIgnoreCase(varValue);
                            }
                            if (selected != chbox.isSelected()) {
                                chbox.setSelected(selected);
                            }
                        }
                    } else if (component instanceof javax.swing.JRadioButton) {
                        javax.swing.JRadioButton button = (javax.swing.JRadioButton) component;
                        javax.swing.ButtonModel model = button.getModel();
                        if (model instanceof javax.swing.DefaultButtonModel) {
                            javax.swing.ButtonGroup group = ((javax.swing.DefaultButtonModel) model).getGroup();
                            inComponent.setValue(oldValue); // Set back the old value, we can not be sure, that the new value can be set.
                            String selectedValue = selectButton(varValue, inComponent.subComponents(), group);
                            //System.out.println("  selected value = '"+selectedValue+"', component value = '"+inComponent.getValue()+"', varValue = '"+varValue+"'");
                            // The value of inComponent can change by setting up of subsequent components.
                            // Therefore we'll adjust the property change event later
                            PropertyChangeEvent pcev = new PropertyChangeEvent(this, PROP_VAR_CHANGED+varName, oldValue, inComponent);
                            propertyChangeEvents.add(pcev);
                            eventsToAdjust.add(pcev);
                        }
                        break; // Skip the next radio buttons. We've already set the correct button.
                    } else if (component instanceof javax.swing.JComboBox) {
                        javax.swing.JComboBox comboBox = (javax.swing.JComboBox) component;
                        if (comboBox.isEditable()) {
                            if (!varValue.equals(comboBox.getSelectedItem())) {
                                comboBox.setSelectedItem(varValue);
                            }
                        } else {
                            VariableInputComponent[] subComponents = inComponent.subComponents();
                            int items = subComponents.length;
                            String[] values = new String[items];
                            for (int j = 0; j < items; j++) {
                                values[j] = subComponents[j].getDefaultValue();
                            }
                            int j;
                            if (varValue != null) {
                                for (j = 0; j < items; j++) {
                                    if (varValue.equals(values[j])) break;
                                }
                                if (j >= items) j = 0;
                            } else j = 0;
                            if (j != comboBox.getSelectedIndex()) {
                                comboBox.setSelectedIndex(j);
                            }
                        }
                    }
                }
            }
        }
        if (resetVars) this.vars = vars;
        for (Iterator it = eventsToAdjust.iterator(); it.hasNext(); ) {
            PropertyChangeEvent evt = (PropertyChangeEvent) it.next();
            VariableInputComponent inComponent = (VariableInputComponent) evt.getNewValue();
            propertyChangeEvents.remove(evt);
            PropertyChangeEvent newEvt = new PropertyChangeEvent(this, evt.getPropertyName(), evt.getOldValue(), inComponent.getValue());
            newEvt.setPropagationId(evt.getPropagationId());
            propertyChangeEvents.add(newEvt);
        }
        if (propertyChangeEvents.size() > 0) {
            firePropertyChange(PROP_VARIABLES_CHANGED, null, propertyChangeEvents);
        }
    }
    
    /**
     * Set the execution string.
     */
    public void setExec(String exec) {
        this.exec = exec;
    }
    
    private void removeGlobalInputTab() {
        remove(variableTabbedPane);
        java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(jPanel1, gridBagConstraints1, 1);
    }
    
    public void setGlobalInput(VariableInputDescriptor inputDescriptor) {
        if (inputDescriptor == null) {
            removeGlobalInputTab();
            return ;
        }
        ArrayList globalComponents = new ArrayList();
        VariableInputComponent[] components = inputDescriptor.components();
        for (int i = 0; i < components.length; i++) {
            String var = components[i].getVariable();
            if (globalVars == null || globalVars.contains(var)) {
                globalComponents.add(components[i]);
            }
        }
        if (globalLabel == null) globalLabel = inputDescriptor.getLabel();
        this.globalDescriptor = inputDescriptor;
        //VariableInputDescriptor globalDescriptor =
        //    VariableInputDescriptor.create(globalLabel, (VariableInputComponent[]) globalComponents.toArray(new VariableInputComponent[0]));
        if (globalLabel != null) {
            if (expert) {
                variableTabbedPane.setTitleAt(1, globalLabel);
                jPanel2.getAccessibleContext().setAccessibleName(globalLabel);
                jPanel2.getAccessibleContext().setAccessibleDescription(globalLabel);
            } else {
                removeGlobalInputTab();
                //variableTabbedPane.removeTabAt(1);
            }
            //globalInputLabel.setText(globalLabel);
            //globalInputLabel.setVisible(expert);
        }
        /*
        if (labelOffset > 0) {
            javax.swing.JSeparator sep = new javax.swing.JSeparator();
            java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
            gridBagConstraints1.gridwidth = 3;
            gridBagConstraints1.gridy = labelOffset;
            //gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.insets = new java.awt.Insets (4, 0, 0, 0);
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.weightx = 1.0;
            variablePanel.add(sep, gridBagConstraints1);
            sep.setVisible(expert);
        }
         */
        int historySizeOrig = historySize;
        int labelOffsetOrig = labelOffset;
        initComponentsFromDescriptor(globalDescriptor, globalInputPanel);
        historySize = historySizeOrig;
        labelOffset = labelOffsetOrig;
        if (expert) globalInputPanel.setVisible(true);
    }
    
    private void enableComponents(String[] vars, boolean enable, String variable) {
        if (vars.length == 0) return ;
        synchronized (disabledComponents) {
            //System.err.println("enableComponents("+VcsUtilities.arrayToString(vars)+", "+enable+", "+variable+")");
            //Thread.dumpStack();
            for (int i = 0; i < vars.length; i++) {
                java.awt.Component[] components = (java.awt.Component[]) awtComponentsByVars.get(vars[i]);
                //System.err.println(" components("+vars[i]+") = "+((components == null) ? "null" : java.util.Arrays.asList(components).toString()));
                if (components != null) {
                    for (int j = 0; j < components.length; j++) {
                        //System.out.println("  components["+j+"] = "+enable);
                        Set disablerVars = (Set) disabledComponents.get(components[j]);
                        if (disablerVars != null) {
                            if (enable) {
                                disablerVars.remove(variable);
                                if (disablerVars.size() == 0) {
                                    disablerVars = null;
                                    disabledComponents.remove(components[j]);
                                }
                            } else {
                                disablerVars.add(variable);
                            }
                        } else if (!enable) {
                            disablerVars = new HashSet();
                            disablerVars.add(variable);
                            disabledComponents.put(components[j], disablerVars);
                        }
                        //if (enable && disablerVars != null) enable = false;
                        //System.err.println("  components["+j+"] = "+enable);
                        //System.err.print("  "+components[j].getClass()+", "+components[j].hashCode());
                        /*
                        if (components[j] instanceof javax.swing.AbstractButton) {
                            System.err.println(" "+((javax.swing.AbstractButton) components[j]).getText());
                        } else {
                            System.err.println("");
                        }
                        System.out.println("  disabledComponents = "+disabledComponents.get(components[j]));
                         */
                        components[j].setEnabled(enable && disablerVars == null);
                        VariableInputComponent vic = (VariableInputComponent) componentsByVars.get(vars[i]);
                        if (vic != null) {
                            VariableInputComponent[] svic = vic.subComponents();
                            if (svic.length > 0) {
                                //String[] svars = new String[svic.length];
                                List svars = new ArrayList();
                                for (int k = 0; k < svic.length; k++) {
                                    String varName = svic[k].getVariable();
                                    if (svic[k].getComponent() == VariableInputDescriptor.INPUT_RADIO_BTN) {
                                        //String subValue = svic[k].getValue();
                                        //varName += "/"+((subValue == null) ? "" : subValue);
                                        VariableInputComponent[] ssvic = svic[k].subComponents();
                                        for (int l = 0; l < ssvic.length; l++) {
                                            svars.add(ssvic[l].getVariable());
                                        }
                                    } else if (!varName.equals(vars[i])) {
                                        svars.add(varName);
                                    }
                                }
                                if (svars.size() > 0) {
                                    enableComponents((String[]) svars.toArray(new String[0]), enable, variable);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /*
     * Set the file name.
     *
    private void initFileLabel(String file) {
        if (file == null || file.trim().length() == 0) return;
        javax.swing.JLabel label;
        if (file.endsWith(java.io.File.separator)) {
            file = file.substring(0, file.length() - 1);
            if (file.trim().length() == 0) return;
            label = new javax.swing.JLabel(
                java.text.MessageFormat.format(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.folderLabel"),
                                               new Object[] { file }));
        } else {
            label = new javax.swing.JLabel(
                java.text.MessageFormat.format(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.fileLabel"),
                                               new Object[] { file }));
        }
        java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
        //gridBagConstraints1.gridx = 0;
        //gridBagConstraints1.gridy = i + labelOffset;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (12, 12, 0, 12);
        this.add(label, gridBagConstraints1, 0);
        //pack();
    }
     */
    
    private static void setA11y(javax.swing.JComponent jComponent, VariableInputDescriptor descriptor) {
        setA11y(jComponent, descriptor.getA11yName(), descriptor.getA11yDescription());
    }
    
    private static void setA11y(javax.swing.JComponent jComponent, VariableInputComponent component) {
        setA11y(jComponent, component.getA11yName(), component.getA11yDescription());
    }
    
    private static void setA11y(javax.swing.JComponent jComponent, String a11yName, String a11yDescription) {
        if (a11yName == null) {
            a11yName = org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.noA11Y.a11yName");
        }
        jComponent.getAccessibleContext().setAccessibleName(a11yName);
        if (a11yDescription == null) {
            a11yDescription = org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.noA11Y.a11yDescription");
        } else {
            jComponent.setToolTipText(a11yDescription);
        }
        jComponent.getAccessibleContext().setAccessibleDescription(a11yDescription);
    }
    
    private void addVarPromptField(final VariableInputComponent component,
                                   int gridy, javax.swing.JPanel variablePanel,
                                   int leftInset, boolean password,
                                   java.awt.Component[] mainComponent_ptr) {
        String varLabel = component.getLabel();
        ArrayList componentList = new ArrayList();
        final javax.swing.JTextField field = (password) ? 
            new javax.swing.JPasswordField(TEXTFIELD_COLUMNS) :
            new javax.swing.JTextField(TEXTFIELD_COLUMNS);
        mainComponent_ptr[0] = field;        
        if (varLabel != null && varLabel.length() > 0) {
            String varLabelExpanded = Variables.expand(vars, varLabel, false);
            javax.swing.JLabel label = new javax.swing.JLabel(varLabelExpanded);
            if (!varLabel.equals(varLabelExpanded)) {
                addPropertyChangeListener(new TextUpdateListener(label, varLabel));
            }
            java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = gridy;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.insets = new java.awt.Insets (0, leftInset, 8, 8);
            variablePanel.add(label, gridBagConstraints1);
            componentList.add(label);
            label.setLabelFor(field);
            if (component.getLabelMnemonic() != null) {
                label.setDisplayedMnemonic(component.getLabelMnemonic().charValue());
            }
        }
        if (VariableInputDescriptor.STYLE_READ_ONLY.equals(component.getStyle())) {
            field.setEditable(false);
        }
        setA11y(field, component);
        String value;
        if (component.needsPreCommandPerform()) {
            value = component.getValue();
        } else {
            value = component.getDefaultValue();
        }
        if (value != null) {
            value = Variables.expand(vars, value, false);
            field.setText(value);
        }
        component.setValue(value);
        java.awt.GridBagConstraints gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = gridy;
        //gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.insets = new java.awt.Insets (0, 0, 8, 0);
        variablePanel.add(field, gridBagConstraints2);
        componentList.add(field);
        if (!password) VcsUtilities.removeEnterFromKeymap(field);
        String selector = component.getSelector();
        //System.out.println("Match selector '"+selector+"': ("+component.getSelectorVarConditions()[0]+", "+component.getSelectorVarConditions()[1]+")"+VariableInputComponent.isVarConditionMatch(component.getSelectorVarConditions(), vars));
        if (selector != null &&
            !VariableInputComponent.isVarConditionMatch(component.getSelectorVarConditions(), vars)
        ) {
            selector = null;
        }
        FocusListener l;
        field.addFocusListener(l = new FocusListener() {
            public void focusGained(FocusEvent fevt) {}
            public void focusLost(FocusEvent fevt) {
                doAutofill();
                Object oldValue = component.getValue();
                component.setValue(field.getText());
                firePropertyChange(PROP_VAR_CHANGED + component.getVariable(), oldValue, component.getValue());
            }
        });
        focusListenersToCallBeforeValidate.add(l);
        if (selector != null) {
            java.awt.Component awtComponent = null;
            if (VariableInputDescriptor.SELECTOR_DIR.equals(selector)) {
                awtComponent = addBrowseDir(variablePanel, field, gridy, l);
            } else if (VariableInputDescriptor.SELECTOR_FILE.equals(selector)) {
                awtComponent = addBrowseFile(variablePanel, field, gridy, l);
            } else if (VariableInputDescriptor.SELECTOR_DATE_CVS.equals(selector)) {
                awtComponent = addDateCVS(variablePanel, field, gridy, l);
            } else if (selector.indexOf(VariableInputDescriptor.SELECTOR_CMD) == 0) {
                awtComponent = addSelector(variablePanel, field, gridy,
                                           selector.substring(VariableInputDescriptor.SELECTOR_CMD.length()), l,
                                           component.getVariable());
            }
            if (awtComponent != null) componentList.add(awtComponent);
        }
        awtComponentsByVars.put(component.getVariable(), componentList.toArray(new java.awt.Component[0]));
        componentsByVars.put(component.getVariable(), component);
        //System.out.println("put("+component.getVariable()+", "+componentList.toArray(new java.awt.Component[0]));
        /*
        field.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent aevt) {
                System.out.println("action Performed: "+aevt);
                component.setValue(field.getText());
                if ((aevt.getID() & ActionEvent.KEY_EVENT_MASK) != 0) {
                    System.out.println("KEY Action !");
                    VariableInputDialog.this.dispatchEvent(new ActionEvent(aevt.getSource(), aevt.getID(), aevt.getActionCommand(), aevt.getModifiers()));
                }
            }
        });
        */
        /*
        field.getDocument().addDocumentListener(new DocumentListener() {
            
            public void insertUpdate(DocumentEvent ev) {
                System.out.println(component.getLabel()+": ev = "+ev);
                System.out.println("  length = "+ev.getLength());
                if (ev.getLength() > 1) { // The change is large enough
                    Object oldValue = component.getValue();
                    component.setValue(field.getText());
                    System.out.println("  firing: ("+PROP_VAR_CHANGED + component.getVariable()+", "+oldValue+", "+component.getValue()+")");
                    firePropertyChange(PROP_VAR_CHANGED + component.getVariable(), oldValue, component.getValue());
                }
            }
            
            public void removeUpdate(DocumentEvent ev) {}
            
            public void changedUpdate(DocumentEvent ev) {}
            
        });
         */
        /*
        field.addInputMethodListener(new InputMethodListener() {
            public void caretPositionChanged(InputMethodEvent event) {
            }
            public void inputMethodTextChanged(InputMethodEvent event) {
                Object oldValue = component.getValue();
                component.setValue(field.getText());
                firePropertyChange(PROP_VAR_CHANGED + component.getVariable(), oldValue, component.getValue());
            }
        });
         */
        addActionToProcess(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                component.setValue(field.getText());
                if (vars != null) {
                    if (field.isEnabled()) {
                        vars.put(component.getVariable(), component.getValue());
                    } else {
                        vars.remove(component.getVariable());
                    }
                }
            }
        });
        addHistoryListener(new VariableInputDialog.HistoryListener() {
            public void changeHistory(int index1, int index) {
                Object oldValue = component.getValue();
                field.setText(component.getHistoryValue(index));
                component.setValue(field.getText());
                firePropertyChange(PROP_VAR_CHANGED + component.getVariable(), oldValue, component.getValue());
            }
        });
    }
    
    private java.awt.Component addBrowseDir(final javax.swing.JPanel panel,
                                            final javax.swing.JTextField field,
                                            int y, final FocusListener l) {
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = y;
        //gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets (0, 8, 8, 0);
        gridBagConstraints.fill = gridBagConstraints.HORIZONTAL;
        javax.swing.JButton button = new javax.swing.JButton(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.Browse"));
        button.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.BrowseDir.a11yDesc"));
        panel.add(button, gridBagConstraints);
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                java.awt.Dialog[] dialog_ptr = new java.awt.Dialog[1];
                java.awt.Frame frame = VcsUtilities.getAncestor(getVariableInputPanel(), dialog_ptr);
                ChooseDirDialog chooseDir;
                if (frame != null) {
                    chooseDir = new ChooseDirDialog(frame, new File(field.getText ()));
                } else {
                    chooseDir = new ChooseDirDialog(dialog_ptr[0], new File(field.getText ()));
                }
                VcsUtilities.centerWindow (chooseDir);
                chooseDir.show();
                String selected = chooseDir.getSelectedDir();
                if (selected == null) {
                    //D.deb("no directory selected"); // NOI18N
                    return ;
                }
                field.setText(selected);
                l.focusLost(null);
            }
        });
        return button;
    }
    
    private java.awt.Component addBrowseFile(final javax.swing.JPanel panel,
                                             final javax.swing.JTextField field,
                                             int y, final FocusListener l) {
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = y;
        //gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets (0, 8, 8, 0);
        gridBagConstraints.fill = gridBagConstraints.HORIZONTAL;
        javax.swing.JButton button = new javax.swing.JButton(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.Browse"));
        button.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.BrowseFile.a11yDesc"));
        panel.add(button, gridBagConstraints);
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                java.awt.Dialog[] dialog_ptr = new java.awt.Dialog[1];
                java.awt.Frame frame = VcsUtilities.getAncestor(getVariableInputPanel(), dialog_ptr);
                ChooseFileDialog chooseFile;
                if (frame != null) {
                    chooseFile = new ChooseFileDialog(frame, new File(field.getText ()), false);
                } else {
                    chooseFile = new ChooseFileDialog(dialog_ptr[0], new File(field.getText ()), false);
                }
                VcsUtilities.centerWindow (chooseFile);
                chooseFile.show();
                String selected = chooseFile.getSelectedFile();
                if (selected == null) {
                    //D.deb("no directory selected"); // NOI18N
                    return ;
                }
                field.setText(selected);
                l.focusLost(null);
            }
        });
        return button;
    }
    
    private java.awt.Component addDateCVS(final javax.swing.JPanel panel,
                                          final javax.swing.JTextField field,
                                          int y, final FocusListener l) {
        field.setToolTipText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.DateCVS"));
        return null;
    }

    private java.awt.Component addSelector(final javax.swing.JPanel panel,
                                           final javax.swing.JTextField field,
                                           int y, String commandNameStr,
                                           final FocusListener l, final String variableName) {
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = y;
        //gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets (0, 8, 8, 0);
        gridBagConstraints.fill = gridBagConstraints.HORIZONTAL;
        String buttonText;
        if (commandNameStr.startsWith("(")) {
            int index = VcsUtilities.getPairIndex(commandNameStr, 1, '(', ')');
            if (index > 0) {
                buttonText = commandNameStr.substring(1, index);
                commandNameStr = commandNameStr.substring(index + 1);
            } else {
                buttonText = org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.Select");
            }
        } else {
            buttonText = org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.Select");
        }
        final String commandName = commandNameStr;
        javax.swing.JButton button = new javax.swing.JButton(buttonText);
        Mnemonics.setLocalizedText(button, buttonText);
        button.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.Select.a11yDesc"));
        panel.add(button, gridBagConstraints);
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        String selected = getSelectorText(commandName, field.getText(), variableName);
                        //System.out.println("selected = "+selected);
                        if (selected != null) {
                            field.setText(selected);
                            l.focusLost(null);
                        }
                    }
                });
            }
        });
        return button;
    }
    
    private String getSelectorText(String commandName, String oldText, String variableName) {
        CommandSupport cmdSupp = executionContext.getCommandSupport(commandName);
        if ("PASSWORD".equals(variableName)) executionContext.setPassword(oldText);
        //OutputContainer container = new OutputContainer(cmd);
        if (cmdSupp == null) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                org.openide.util.NbBundle.getMessage(VariableInputDialog.class, "VariableInputDialog.CommandDoesNotExist", commandName)));
            return oldText;
            //ErrorManager.getDefault().
        }
        Command command = cmdSupp.createCommand();
        if (!(command instanceof VcsDescribedCommand)) return null;
        VcsDescribedCommand cmd = (VcsDescribedCommand) command;
        // Remember the original variables for a while
        Hashtable origVars = vars;
        try {
            vars = new Hashtable(origVars);
            // Apply all actions to the copy of the original variables
            for (Iterator it = actionList.iterator(); it.hasNext(); ) {
                ActionListener listener = (ActionListener) it.next();
                listener.actionPerformed(null);
            }
            //Hashtable varsCopy = new Hashtable(vars);
            cmd.setAdditionalVariables(vars);
        } finally {
            // We have to reset the variables back!
            vars = origVars;
        }
        if (!VcsManager.getDefault().showCustomizer(cmd)) return null;
        //VcsCommandExecutor ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, varsCopy);
        //if (ec == null) return null;
        //ec.setErrorNoRegexListener(container);
        //ec.setOutputNoRegexListener(container);
        //ec.setErrorContainer(container);
        final StringBuffer selectorOutput = new StringBuffer();
        final boolean[] selectorMatched = new boolean[] { false };
        cmd.addRegexOutputListener(new RegexOutputListener() {
            public void outputMatchedGroups(String[] elements) {
                if (elements != null) {
                    selectorMatched[0] = true;
                    selectorOutput.append(VcsUtilities.array2string(elements).trim());
                }
            }
        });
        CommandTask task = cmd.execute();
        try {
            task.waitFinished(0);
        } catch (InterruptedException iexc) {
            return null;
        }
        if (task instanceof VcsDescribedTask) {
            Map commandVars = ((VcsDescribedTask) task).getVariables();
            //System.out.println("commandVars = "+commandVars);
            //System.out.println("  UPDATE ONLY CHANGED VARIABLES = '"+commandVars.get(VAR_UPDATE_CHANGED_FROM_SELECTOR)+"'");
            // We update variables changed by the selector when VAR_UPDATE_CHANGED_FROM_SELECTOR is set to "true".
            if ("true".equals(commandVars.get(VAR_UPDATE_CHANGED_FROM_SELECTOR))) {
                commandVars.remove(VAR_UPDATE_CHANGED_FROM_SELECTOR);
                Hashtable varsHashtable;
                if (commandVars instanceof Hashtable) {
                    varsHashtable = (Hashtable) commandVars;
                } else {
                    varsHashtable = new Hashtable(commandVars);
                }
                updateVariableValues(varsHashtable, false);
            }
        }
        if (task.getExitStatus() == task.STATUS_SUCCEEDED && selectorMatched[0]) {
            return selectorOutput.toString();
        } else return null;
    }
    
    private void addAskChBox(final VariableInputComponent component, int gridy,
                             javax.swing.JPanel variablePanel, int leftInset,
                             HashMap varsToEnableDisable,
                             java.awt.Component[] mainComponent_ptr) {
        String label = component.getLabel();
        String labelExpanded = Variables.expand(vars, label, false);
        final javax.swing.JCheckBox chbox = new javax.swing.JCheckBox(" "+labelExpanded);
        mainComponent_ptr[0] = chbox;
        if (!label.equals(labelExpanded)) {
            addPropertyChangeListener(new TextUpdateListener(chbox, " "+label));
        }
        //chbox.setBorder(new javax.swing.border.EmptyBorder(1, 0, 1, 0));
        String askDefault;
        if (component.needsPreCommandPerform()) {
            askDefault = component.getValue();
        } else {
            askDefault = component.getDefaultValue();
            component.setValue(askDefault);
        }
        if (askDefault != null) {
            askDefault = Variables.expand(vars, askDefault, false);
            component.setValue(askDefault);
            String valueSelected = component.getValueSelected();
            if (valueSelected != null) {
                chbox.setSelected(askDefault.equals(valueSelected));
            } else {
                chbox.setSelected(Boolean.TRUE.toString().equalsIgnoreCase(askDefault));
            }
        }
        if (component.getLabelMnemonic() != null) {
            chbox.setMnemonic(component.getLabelMnemonic().charValue());
        }
        setA11y(chbox, component);
        java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = gridy;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, leftInset, 8, 0);
        gridBagConstraints1.gridwidth = 2;
        variablePanel.add(chbox, gridBagConstraints1);
        chbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                boolean selected = chbox.isSelected();
                String valueSelected = component.getValueSelected();
                String valueUnselected = component.getValueUnselected();
                Object oldValue = component.getValue();
                if (selected && valueSelected != null) {
                    component.setValue(valueSelected);
                } else if (!selected && valueUnselected != null) {
                    component.setValue(valueUnselected);
                } else {
                    component.setValue(selected ? Boolean.TRUE.toString() : "");
                }
                firePropertyChange(PROP_VAR_CHANGED + component.getVariable(), oldValue, component.getValue());
            }
        });
        awtComponentsByVars.put(component.getVariable(), new java.awt.Component[] { chbox });
        componentsByVars.put(component.getVariable(), component);
        final String[] varsEnabled = (String[]) component.getEnable().toArray(new String[0]);
        final String[] varsDisabled = (String[]) component.getDisable().toArray(new String[0]);
        if (varsEnabled.length > 0) {
            varsToEnableDisable.put(varsEnabled, chbox.isSelected() ? Boolean.TRUE : Boolean.FALSE);
        }
        if (varsDisabled.length > 0) {
            varsToEnableDisable.put(varsDisabled, !chbox.isSelected() ? Boolean.TRUE : Boolean.FALSE);
        }
        chbox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ev) {
                enableComponents(varsEnabled, chbox.isSelected(), component.getVariable());
                enableComponents(varsDisabled, !chbox.isSelected(), component.getVariable());
            }
        });
        addActionToProcess(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                boolean selected = chbox.isSelected();
                String valueSelected = component.getValueSelected();
                String valueUnselected = component.getValueUnselected();
                if (selected && valueSelected != null) {
                    component.setValue(valueSelected);
                } else if (!selected && valueUnselected != null) {
                    component.setValue(valueUnselected);
                } else {
                    component.setValue(selected ? Boolean.TRUE.toString() : "");
                }
                if (vars != null) {
                    if (chbox.isEnabled()) {
                        vars.put(component.getVariable(), component.getValue());
                    } else {
                        vars.remove(component.getVariable());
                    }
                }
            }
        });
        addHistoryListener(new VariableInputDialog.HistoryListener() {
            public void changeHistory(int index1, int index) {
                String valueSelected = component.getValueSelected();
                String valueUnselected = component.getValueUnselected();
                String value = component.getHistoryValue(index);
                if (valueSelected != null && valueSelected.equals(value)) {
                    chbox.setSelected(true);
                } else if (valueUnselected != null && valueUnselected.equals(value)) {
                    chbox.setSelected(false);
                } else chbox.setSelected(Boolean.TRUE.toString().equals(value));
            }
        });
    }

    private void addVarPromptArea(final VariableInputComponent component, int gridy,
                                  final int promptAreaNum, javax.swing.JPanel variablePanel,
                                  int leftInset, java.awt.Component[] mainComponent_ptr) {
        String message = component.getLabel();
        String messageExpanded = Variables.expand(vars, message, false);
        javax.swing.JLabel label = new javax.swing.JLabel(messageExpanded);
        if (!message.equals(messageExpanded)) {
            addPropertyChangeListener(new TextUpdateListener(label, message));
        }
        java.awt.Dimension dimension = component.getDimension();
        if (dimension == null) dimension = new java.awt.Dimension(TEXTAREA_ROWS, TEXTAREA_COLUMNS);
        final javax.swing.JTextArea area = new javax.swing.JTextArea(dimension.width, dimension.height);
        mainComponent_ptr[0] = area;
        label.setLabelFor(area);
        if (component.getLabelMnemonic() != null) {
            label.setDisplayedMnemonic(component.getLabelMnemonic().charValue());
        }
        if (VariableInputDescriptor.STYLE_READ_ONLY.equals(component.getStyle())) {
            area.setEditable(false);
        }
        setA11y(area, component);
        javax.swing.JScrollPane scrollArea = new javax.swing.JScrollPane(area);
        //javax.swing.JTextField field = new javax.swing.JTextField(TEXTFIELD_COLUMNS);
        java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
        java.awt.GridBagConstraints gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = gridy;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, leftInset, 8, 0);
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = gridy + 1;
        //gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.insets = new java.awt.Insets (0, leftInset, 8, 0);
        gridBagConstraints2.gridwidth = 3;
        area.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        variablePanel.add(label, gridBagConstraints1);
        variablePanel.add(scrollArea, gridBagConstraints2);
        if (variablePanel.isAncestorOf(pushPanel1)) {
            variablePanel.remove(pushPanel1);
        } else if (variablePanel.isAncestorOf(pushPanel2)) {
            variablePanel.remove(pushPanel2);
        }
        //fileLabels.addElement(label);
        //areas.addElement(area);
        //VcsUtilities.removeEnterFromKeymap(field);
        //fileNames.add(filePrompts.get(message));
        String fileName;
        if (component.needsPreCommandPerform()) {
            fileName = component.getValue();
        } else {
            fileName = component.getDefaultValue();
        }
        //System.out.println("default file name = "+fileName);
        if (fileName != null && fileName.length() > 0) {
            fileName = Variables.expand(vars, fileName, false);
        }
        if (fileName == null || fileName.length() == 0) {
            try {
                fileName = java.io.File.createTempFile("tempVcsCmd", "input").getAbsolutePath();
            } catch (IOException exc) {
                ErrorManager.getDefault().notify(exc);
            }
        }
        //System.out.println("setting file name value = "+fileName);
        component.setValue(fileName);
        initArea(area, fileName);
        awtComponentsByVars.put(component.getVariable(), new java.awt.Component[] { label, area });
        addActionToProcess(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                //component.setValue(chbox.isSelected() ? Boolean.TRUE.toString() : "");
                writeFileContents(area, component.getValue(), promptAreaNum);
                if (vars != null) {
                    if (area.isEnabled()) {
                        Object value = component.getValue();
                        if (value != null) {
                            vars.put(component.getVariable(), value);
                        } else {
                            vars.remove(component.getVariable());
                        }
                    } else {
                        vars.remove(component.getVariable());
                    }
                }
            }
        });
        addHistoryListener(new VariableInputDialog.HistoryListener() {
            public void changeHistory(int index1, int index2) {
                if (index1 == historySize && index2 < index1) {
                    writeFileContents(area, component.getValue(), promptAreaNum);
                }
                initArea(area, component.getHistoryValue(index2));
            }
        });
    }

    private int addSelectRadio(final VariableInputComponent component, int gridy,
                               javax.swing.JPanel variablePanel, int leftInset,
                               HashMap varsToEnableDisable,
                               java.awt.Component[] mainComponent_ptr) {
        ArrayList componentList = new ArrayList();
        String message = component.getLabel();
        if (message != null && message.length() > 0) {
            String messageExpanded = Variables.expand(vars, message, false);
            javax.swing.JLabel label = new javax.swing.JLabel(messageExpanded);
            if (!message.equals(messageExpanded)) {
                addPropertyChangeListener(new TextUpdateListener(label, message));
            }
            java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = gridy;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.insets = new java.awt.Insets (0, leftInset, 4, 0);
            gridBagConstraints1.gridwidth = 2;
            variablePanel.add(label, gridBagConstraints1);
            gridy++;
            componentList.add(label);
        }
        final VariableInputComponent[] subComponents = component.subComponents();
        final javax.swing.ButtonGroup group = new javax.swing.ButtonGroup();
        String defValue;
        if (component.needsPreCommandPerform()) {
            defValue = component.getValue();
        } else {
            defValue = component.getDefaultValue();
            component.setValue(defValue);
        }
        for (int i = 0; i < subComponents.length; i++) {
            gridy = addRadioButton(component, subComponents[i], gridy, group,
                                   variablePanel, leftInset, defValue,
                                   varsToEnableDisable);
        }
        boolean setMainComponent = true;
        for (Enumeration enum = group.getElements(); enum.hasMoreElements(); ) {
            java.awt.Component c = (java.awt.Component) enum.nextElement();
            if (setMainComponent) {
                mainComponent_ptr[0] = c;
                setMainComponent = false;
            }
            componentList.add(c);
        }
        awtComponentsByVars.put(component.getVariable(), componentList.toArray(new java.awt.Component[0]));
        componentsByVars.put(component.getVariable(), component);
        selectButton(defValue, subComponents, group);
        addActionToProcess(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                /*
                int selected = 0;
                Enumeration enum = group.getElements();
                for (int i = 0; enum.hasMoreElements(); i++) {
                    javax.swing.JRadioButton radio = (javax.swing.JRadioButton) enum.nextElement();
                    if (radio.isSelected()) {
                        selected = i;
                        break;
                    }
                }
                component.setValue(subComponents[selected].getValue());
                 */
                if (vars != null) {
                    Object value = component.getValue();
                    if (value != null) {
                        vars.put(component.getVariable(), value);
                    } else {
                        vars.remove(component.getVariable());
                    }
                }
            }
        });
        addHistoryListener(new VariableInputDialog.HistoryListener() {
            public void changeHistory(int index1, int index) {
                selectButton(component.getHistoryValue(index), subComponents, group);
            }
        });
        return gridy;
    }
    
    /** The set of buttons, that were automatically unselected when they were disabled */
    private Set unselectedRadioButtons = new HashSet();
    
    private int addRadioButton(final VariableInputComponent superComponent,
                               final VariableInputComponent component, int gridy,
                               final javax.swing.ButtonGroup group,
                               javax.swing.JPanel variablePanel, int leftInset,
                               String defValue, HashMap varsToEnableDisable) {
        String label = component.getLabel();
        String labelExpanded = Variables.expand(vars, label, false);
        boolean firstSubLabelEmpty = false; // If the first sublabel is empty, put the first sub component to the same gridy as the button
        VariableInputComponent[] subComponents = component.subComponents();
        if (subComponents.length > 0) {
            String subLabel = subComponents[0].getLabel();
            firstSubLabelEmpty = subLabel == null || subLabel.length() == 0;
        }
        final javax.swing.JRadioButton button = new javax.swing.JRadioButton(labelExpanded);
        //System.out.println("!!   ADD RADIO : "+button.hashCode()+", "+labelExpanded);
        if (!label.equals(labelExpanded)) {
            addPropertyChangeListener(new TextUpdateListener(button, label));
        }
        if (component.getLabelMnemonic() != null) {
            button.setMnemonic(component.getLabelMnemonic().charValue());
        }
        setA11y(button, component);
        group.add(button);
        java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = gridy;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets (0, leftInset, 4, 0);
        gridBagConstraints1.gridwidth = (firstSubLabelEmpty) ? 1 : 2;
        variablePanel.add(button, gridBagConstraints1);
        gridy++;
        ArrayList componentVarsList = new ArrayList();
        for (int i = 0; i < subComponents.length; i++) {
            int inset;
            if (i == 0 && firstSubLabelEmpty) {
                gridy--;
                inset = 8;
            } else {
                inset = leftInset + DEFAULT_INDENT;
            }
            gridy = addComponent(subComponents[i], gridy, variablePanel, inset,
                                 varsToEnableDisable, new java.awt.Component[1]);
            componentVarsList.add(subComponents[i].getVariable());
        }
        String value = component.getValue();
        if (value == null) value = "";
        awtComponentsByVars.put(component.getVariable()+"/"+value, new java.awt.Component[] { button });
        final String[] componentVars = (String[]) componentVarsList.toArray(new String[0]);
        enableComponents(componentVars, false, component.getVariable());
        final String[] varsEnabled = (String[]) component.getEnable().toArray(new String[0]);
        final String[] varsDisabled = (String[]) component.getDisable().toArray(new String[0]);
        boolean enabled = defValue != null && defValue.equals(component.getValue()) || defValue == component.getValue();
        if (componentVars.length > 0) {
            varsToEnableDisable.put(componentVars, enabled ? Boolean.TRUE : Boolean.FALSE);
        }
        if (varsEnabled.length > 0) {
            varsToEnableDisable.put(varsEnabled, enabled ? Boolean.TRUE : Boolean.FALSE);
        }
        if (varsDisabled.length > 0) {
            varsToEnableDisable.put(varsDisabled, !enabled ? Boolean.TRUE : Boolean.FALSE);
        }
        button.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ev) {
                enableComponents(componentVars, button.isSelected(), component.getVariable());
                enableComponents(varsEnabled, button.isSelected(), component.getVariable());
                enableComponents(varsDisabled, !button.isSelected(), component.getVariable());
            }
        });
        button.addPropertyChangeListener("enabled", new PropertyChangeListener() { // NOI18N
            public void propertyChange(PropertyChangeEvent pchev) {
                //System.out.println("!!! BUTTON '"+button.getText()+"' ENABLED = "+button.isEnabled()+", SELECTED = "+button.isSelected());
                //System.out.println("    pchev = "+pchev+", NEW VALUE = "+pchev.getNewValue());
                if (!button.isEnabled()) { // The button was just disabled
                    if (button.isSelected()) {
                        Enumeration enum = group.getElements();
                        for (int i = 0; enum.hasMoreElements(); i++) {
                            javax.swing.JRadioButton radio = (javax.swing.JRadioButton) enum.nextElement();
                            if (radio.isEnabled()) {
                                //System.out.println("  Selecting button '"+radio.getText()+"' instead!!!");
                                radio.doClick(); // <-- to trigger an action event
                                superComponent.setValue(superComponent.subComponents()[i].getValue());
                                //System.out.println("  Setting value to '"+superComponent.getVariable()+"' component: '"+superComponent.subComponents()[i].getValue()+"'");
				unselectedRadioButtons.add(button);
                                break;
                            }
                        }
                        if (!unselectedRadioButtons.contains(button)) {
                            // No button was enabled. We need to select back the original.
                            enum = group.getElements();
                            for (int i = 0; enum.hasMoreElements(); i++) {
                                javax.swing.JRadioButton radio = (javax.swing.JRadioButton) enum.nextElement();
                                if (unselectedRadioButtons.contains(radio)) {
                                    radio.setSelected(true);
                                    unselectedRadioButtons.remove(radio);
                                    String newValue = superComponent.subComponents()[i].getValue();
                                    superComponent.setValue(newValue);
                                    firePropertyChange(PROP_VAR_CHANGED + superComponent.getVariable(), null, newValue);
                                    break;
                                }
                            }
                        }
                    }
                } else { // The buttom was just enabled
                    if (unselectedRadioButtons.contains(button)) {  // If it was unselected, select it again.
                        //System.out.println("  Selecting BACK the button '"+button.getText()+"'");
                        unselectedRadioButtons.remove(button);
                        
                        Object oldValue = superComponent.getValue();
                        superComponent.setValue(component.getValue());
                        firePropertyChange(PROP_VAR_CHANGED + superComponent.getVariable(), oldValue, superComponent.getValue());
                        //System.out.println("component '"+superComponent.getVariable()+"' setValue("+component.getValue()+"), old value = '"+oldValue+"'");
                        button.setSelected(true);
                        //button.doClick();
                        //button.setSelected(true);
                        //superComponent.setValue(component.getValue());
                    }
                }
            }
        });
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (button.isSelected()) {
                    Object oldValue = superComponent.getValue();
                    superComponent.setValue(component.getValue());
                    firePropertyChange(PROP_VAR_CHANGED + superComponent.getVariable(), oldValue, superComponent.getValue());
                    //System.out.println("component '"+superComponent.getVariable()+"' setValue("+component.getValue()+"), old value = '"+oldValue+"'");
                }
            }
        });
        return gridy;
    }
    
    private void removeUnselectedButtons(javax.swing.ButtonGroup group) {
        Enumeration enum = group.getElements();
        for (int i = 0; enum.hasMoreElements(); i++) {
            javax.swing.JRadioButton radio = (javax.swing.JRadioButton) enum.nextElement();
            unselectedRadioButtons.remove(radio);
        }
    }
    
    /** Select a radio button. Return the value of the actually selected component.
     */
    private String selectButton(String value, VariableInputComponent[] subComponents,
                                javax.swing.ButtonGroup group) {
        if (value == null && subComponents.length > 0) value = subComponents[0].getValue();
        Enumeration enum = group.getElements();
        javax.swing.JRadioButton toBeSelectedRadio = null;
        for (int i = 0; enum.hasMoreElements(); i++) {
            javax.swing.JRadioButton radio = (javax.swing.JRadioButton) enum.nextElement();
            if (value.equals(subComponents[i].getValue())) {
                if (radio.isEnabled()) {
                    if (!radio.isSelected()) {
                        radio.doClick(); // <-- to trigger an action event
                    }
                    removeUnselectedButtons(group);
                    //System.out.println("selectButton("+value+"), selected enabled '"+radio.getText()+"'");
                    return value;
                } else {
                    toBeSelectedRadio = radio;
                }
                break;
            }
        }
        //System.out.println("!!! SELECT BUTTON: The button that is to be selected is not enabled !!!!!!!");
        removeUnselectedButtons(group);
        if (toBeSelectedRadio != null) {
            //System.out.println("  Set unselected to to-be-selected: "+toBeSelectedRadio.getText());
            unselectedRadioButtons.add(toBeSelectedRadio);
        }
        // The button that is to be selected is not enabled => we need to find the seleced one
        // and return it's value
        synchronized (disabledComponents) {
            enum = group.getElements();
            for (int i = 0; enum.hasMoreElements(); i++) {
                javax.swing.JRadioButton radio = (javax.swing.JRadioButton) enum.nextElement();

                //System.out.println("  radio "+radio.hashCode()+", "+radio.getText()+", disabledComponents = "+disabledComponents.get(radio));
                if (disabledComponents.get(radio) == null) {
                    //System.out.println("  selected sub-component with def. value '"+subComponents[i].getValue()+"'");
                    return subComponents[i].getValue();
                }
            }
        }
        //System.out.println("  No button selected.");
        return value; // We should not get to this point, return the original value here.
    }

    private void addSelectCombo(final VariableInputComponent component, int gridy,
                                javax.swing.JPanel variablePanel, int leftInset,
                                final boolean editable, HashMap varsToEnableDisable,
                                java.awt.Component[] mainComponent_ptr) {
        ArrayList componentList = new ArrayList();
        String message = component.getLabel();
        final VariableInputComponent[] subComponents = component.subComponents();
        final int items = subComponents.length;
        final String[] labels = new String[items];
        final String[] values = new String[items];
        final String[][] varsEnabled = new String[items][0];
        final String[][] varsDisabled = new String[items][0];
        for (int i = 0; i < items; i++) {
            labels[i] = subComponents[i].getLabel();
            values[i] = subComponents[i].getDefaultValue();
            subComponents[i].setValue(subComponents[i].getDefaultValue());
            varsEnabled[i] = (String[]) subComponents[i].getEnable().toArray(new String[0]);
            varsDisabled[i] = (String[]) subComponents[i].getDisable().toArray(new String[0]);
            //System.out.println("SELECT_COMBO["+i+"]: ENABLE("+VcsUtilities.arrayToString(varsEnabled[i])+"), DISABLE("+VcsUtilities.arrayToString(varsDisabled[i])+")");
        }
        final javax.swing.JComboBox comboBox = new javax.swing.JComboBox(labels);
        mainComponent_ptr[0] = comboBox;
        comboBox.setEditable(editable);
        if (editable) { // Change the preferred size, so that it looks more like other text fields.
            comboBox.setPreferredSize(new javax.swing.JTextField().getPreferredSize());
        }
        if (message != null && message.length() > 0) {
            String messageExpanded = Variables.expand(vars, message, false);
            javax.swing.JLabel label = new javax.swing.JLabel(messageExpanded);
            if (!message.equals(messageExpanded)) {
                addPropertyChangeListener(new TextUpdateListener(label, message));
            }
            java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = gridy;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.insets = new java.awt.Insets (0, leftInset, 8, 8);
            gridBagConstraints1.gridwidth = 1;
            variablePanel.add(label, gridBagConstraints1);
            componentList.add(label);
            label.setLabelFor(comboBox);
            if (component.getLabelMnemonic() != null) {
                label.setDisplayedMnemonic(component.getLabelMnemonic().charValue());
            }
        }
        setA11y(comboBox, component);
        java.awt.GridBagConstraints gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = gridy;
        //gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.weightx = 1.0;
        //gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.insets = new java.awt.Insets (0, 0, 8, 0);
        gridBagConstraints2.gridwidth = 1;
        variablePanel.add(comboBox, gridBagConstraints2);
        componentList.add(comboBox);
        awtComponentsByVars.put(component.getVariable(), componentList.toArray(new java.awt.Component[0]));
        componentsByVars.put(component.getVariable(), component);
        String selected;
        if (component.needsPreCommandPerform()) {
            selected = component.getValue();
        } else {
            selected = component.getDefaultValue();
            component.setValue(selected);
        }
        int i;
        if (selected != null) {
            for (i = 0; i < items; i++) {
                if (selected.equals(values[i])) break;
            }
            if (i >= items) i = -1;
        } else i = 0;
        if (i >= 0) comboBox.setSelectedIndex(i);
        else comboBox.setSelectedItem(selected);
        if (i >= 0) {
            if (varsEnabled[i].length > 0) {
                varsToEnableDisable.put(varsEnabled[i], Boolean.TRUE);
            }
            if (varsDisabled[i].length > 0) {
                varsToEnableDisable.put(varsDisabled[i], Boolean.FALSE);
            }
        }
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String currentValue;
                if (editable) {
                    currentValue = (String) comboBox.getSelectedItem();
                } else {
                    int selected2 = comboBox.getSelectedIndex();
                    //System.out.println("Combo Action: selected = "+selected2+" = "+subComponents[selected2].getValue());
                    currentValue = subComponents[selected2].getValue();
                }
                if (currentValue != null) {
                    component.setValue(currentValue);
                    firePropertyChange(PROP_VAR_CHANGED + component.getVariable(), null, currentValue);
                }
            }
        });
        comboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ev) {
                boolean selected2 = (ItemEvent.SELECTED == ev.getStateChange());
                Object item = ev.getItem();
                int index;
                for (index = 0; index < items; index++) {
                    if (item.equals(comboBox.getItemAt(index))) break;
                }
                if (index < items) {
                    enableComponents(varsEnabled[index], selected2, component.getVariable());
                    enableComponents(varsDisabled[index], !selected2, component.getVariable());
                }
            }
        });
        addActionToProcess(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                int selected2 = comboBox.getSelectedIndex();
                component.setValue(subComponents[selected2].getValue());
                if (vars != null) {
                    if (comboBox.isEnabled()) {
                        Object value = component.getValue();
                        if (value != null) {
                            vars.put(component.getVariable(), value);
                        } else {
                            vars.remove(component.getVariable());
                        }
                    } else {
                        vars.remove(component.getVariable());
                    }
                }
            }
        });
        addHistoryListener(new VariableInputDialog.HistoryListener() {
            public void changeHistory(int index1, int index) {
                String value = component.getValue();
                String selected2 = component.getHistoryValue(index);
                if (selected2 != null) {
                    int i2;
                    for (i2 = 0; i2 < items; i2++) {
                        if (selected2.equals(values[i2])) break;
                    }
                    if (i2 < items) comboBox.setSelectedIndex(i2);
                } else comboBox.setSelectedIndex(0);
                component.setValue(value);
            }
        });
    }
    
    private void addTextComponent(VariableInputComponent component, int gridy,
                                  javax.swing.JPanel variablePanel, int leftInset,
                                  java.awt.Component[] mainComponent_ptr) {
        String varsStr = component.getVariable();
        String value = component.getValue();
        //System.out.println("addTextComponent(): varsStr = '"+varsStr+"', value = '"+value+"'");
        String valueExpanded;
        if (value != null) {
            valueExpanded = Variables.expand(vars, value, false);
        } else {
            valueExpanded = value;
        }
        component.setValue(valueExpanded);
        //System.out.println("  valueExpanded = '"+valueExpanded+"'");
        final javax.swing.JTextArea textArea = new javax.swing.JTextArea(valueExpanded);
        mainComponent_ptr[0] = textArea;
        if (value != null && !value.equals(valueExpanded)) {
            addPropertyChangeListener(new TextUpdateListener(textArea, value));
        }
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(component.isMultiLine());
        textArea.setEditable(false);
        textArea.setEnabled(false);
        textArea.setOpaque(false);
        java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints();
        java.awt.Dimension dimension = component.getDimension();
        gridBagConstraints1.gridx = dimension.width;
        gridBagConstraints1.gridy = gridy;
        gridBagConstraints1.gridwidth = dimension.height;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.weightx = 1.0;
        if (dimension.width > 0) {
            gridBagConstraints1.insets = new java.awt.Insets (0, 0, 8, 0);
        } else {
            gridBagConstraints1.insets = new java.awt.Insets(0, leftInset, 8, 8);
        }
        variablePanel.add(textArea, gridBagConstraints1);
        //componentList.add(textArea);
        awtComponentsByVars.put(component.getVariable(), new java.awt.Component[] { textArea });
        componentsByVars.put(component.getVariable(), component);
    }
    
    private void setGlobalVars(VariableInputComponent component) {
        String varsStr = component.getVariable();
        String[] vars = VcsUtilities.getQuotedStrings(varsStr);
        if (!(vars.length == 1 && VariableInputDescriptor.INPUT_STR_GLOBAL_ALL_VARS.equals(vars[0]))) {
            globalVars = new ArrayList();
            globalVars.addAll(Arrays.asList(vars));
        }
        globalLabel = component.getLabel();
    }

    /**
     * Create additional user labels and text fields.
     * @param varLabels Table of labels and default values.
     */
    public void setUserParamsPromptLabels(Table varLabels, String advancedName) {
        Vector labels = new Vector();
        Vector fields = new Vector();
	int i = 0;
	this.userPromptLabelTexts = new String[varLabels.size()];
        if (advancedName != null && varLabels.size() > 0) {
            javax.swing.JSeparator sep = new javax.swing.JSeparator();
            javax.swing.JLabel label = new javax.swing.JLabel(
                java.text.MessageFormat.format(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.advancedNameLabel"),
                                               new Object[] { advancedName }));
            java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
            java.awt.GridBagConstraints gridBagConstraints2 = new java.awt.GridBagConstraints ();
            gridBagConstraints1.gridwidth = 2;
            gridBagConstraints1.gridy = i + labelOffset;
            //gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.insets = new java.awt.Insets (0, 0, 8, 0);
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints2.gridwidth = 2;
            gridBagConstraints2.gridy = i + labelOffset + 1;
            //gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            //gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints2.insets = new java.awt.Insets (0, 0, 8, 0);
            if (labelOffset > 0) variablePanel.add(sep, gridBagConstraints1);
            variablePanel.add(label, gridBagConstraints2);
            labelOffset += 2;
        }
        for(Enumeration enum = varLabels.keys(); enum.hasMoreElements(); i++) {
            String labelStr = (String) enum.nextElement();
            this.userPromptLabelTexts[i] = labelStr;
            javax.swing.JLabel label = new javax.swing.JLabel(labelStr+":");
            javax.swing.JTextField field = new javax.swing.JTextField(TEXTFIELD_COLUMNS);            
            field.setText((String) varLabels.get(labelStr));
            java.awt.GridBagConstraints gridBagConstraints1 = new java.awt.GridBagConstraints ();
            java.awt.GridBagConstraints gridBagConstraints2 = new java.awt.GridBagConstraints ();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = i + labelOffset;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.insets = new java.awt.Insets (0, 0, 8, 8);
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = i + labelOffset;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new java.awt.Insets (0, 0, 8, 0);
            variablePanel.add(label, gridBagConstraints1);
            variablePanel.add(field, gridBagConstraints2);
            labels.addElement(label);
            fields.addElement(field);
            VcsUtilities.removeEnterFromKeymap(field);
        }
        labelOffset += varLabels.size();
        //pack();
        this.userPromptLabels = (javax.swing.JLabel[]) labels.toArray(new javax.swing.JLabel[0]);
        this.userPromptFields = (javax.swing.JTextField[]) fields.toArray(new javax.swing.JTextField[0]);
    }

    /**
     * Set whether to show check box for prompt on next file. When not called default is true.
     * @param show true to show, false not to show
     */
    public void showPromptEach(boolean show) {
        promptEachCheckBox.setVisible(show);
        promptEachSeparator.setVisible(show);
        //pack();
    }

    /**
     * Set whether the initial state of the check box for prompt on next file. When not called default is false.
     * @param prompt the initial state
     */
    public void setPromptEach(boolean prompt) {
        promptEachCheckBox.setSelected(prompt);
    }

    /**
     * Get the variable prompt values.
     */
    public String[] getVarPromptValues() {
        String[] varValues = new String[varPromptFields.length];
        for(int i = 0; i < varPromptFields.length; i++) {
            varValues[i] = varPromptFields[i].getText();
        }
        return varValues;
    }

    /**
     * Get the variable ask values.
     */
    public String[] getVarAskValues() {
        String[] varValues = new String[varAskCheckBoxes.length];
        for(int i = 0; i <  varAskCheckBoxes.length; i++) {
            varValues[i] = (varAskCheckBoxes[i].isSelected()) ? "true" : "";
        }
        return varValues;
    }
    
    /**
     * Getter for the variable input panel. Use this panel if you need
     * to use the panel in your own frame.
     * @return The panel with variable inpout components.
     */
    public javax.swing.JPanel getVariableInputPanel() {
        return variablePanel;
    }

    /**
     * Get the table of additional user variables labels and values.
     */
    public Hashtable getUserParamsValuesTable() {
        Hashtable result = new Hashtable();
        for(int i = 0; i < userPromptLabels.length; i++) {
            result.put(userPromptLabelTexts[i], userPromptFields[i].getText());
        }
        return result;
    }

    /**
     * Whether to prompt for variables for each file separately or use these variables for all files.
     */
    public boolean getPromptForEachFile() {
        return promptEachCheckBox.isSelected();
    }
    
    /**
     * Read content of input files into Text Areas.
     */
    private void initArea(javax.swing.JTextArea filePromptArea, String fileName) {
        //for(int i = 0; i < filePromptAreas.length; i++) {
        //    String name = fileNames[i];
        //System.out.println("initArea("+fileName+")");
            if (fileName.length() == 0) return ;
            File file = new File(fileName);
            if (file.exists() && file.canRead()) {
                FileReader reader = null;
                try {
                    reader = new FileReader(file);
                    filePromptArea.read(reader, null);
                } catch (FileNotFoundException exc) {
                    ErrorManager.getDefault().notify(exc);
                } catch (IOException exc) {
                    ErrorManager.getDefault().notify(exc);
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException ioexc) {}
                    }
                }
            }
        //}
    }
    
    private void writeFileContents(javax.swing.JTextArea filePromptArea, String fileName, int promptAreaNum) {
        //for(int i = 0; i < filePromptAreas.length; i++) {
            if (docListener != null) docListener.filePromptDocumentCleanup(filePromptArea, promptAreaNum, docIdentif);
            //String name = fileNames[i];
            if (fileName == null || fileName.length() == 0) return ;
            File file = new File(fileName);
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                filePromptArea.write(writer);
            } catch (IOException exc) {
                ErrorManager.getDefault().notify(exc);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ioexc) {}
                }
            }
        //}
    }
    
    /**
     * A listener that listens to variable changes and updates the expanded text
     * of a component.
     */
    private class TextUpdateListener extends Object implements PropertyChangeListener {
        
        private Object textComponent;
        private java.lang.reflect.Method setTextMethod;
        private String text;
        
        /**
         * Create a new text update listener.
         * @param textComponent The text component, that have <code>setText</code>
         *        method, that is called with the updated expanded text.
         * @param text The original, unexpanded text.
         */
        public TextUpdateListener(Object textComponent, String text) {
            this.textComponent = textComponent;
            try {
                setTextMethod = textComponent.getClass().getMethod("setText", new Class[] { String.class });
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
            this.text = text;
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            //System.out.println("TEXT UPDATE LISTENER: evt = "+evt);
            //System.out.println("  name = '"+evt.getPropertyName()+"', value = '"+evt.getNewValue()+"'");
            String propName = evt.getPropertyName();
            boolean multievent = false;
            if (vars != null && (propName.startsWith(PROP_VAR_CHANGED) || (multievent = propName.equals(PROP_VARIABLES_CHANGED)))) {                
                if (multievent) {
                    Collection changedProps = (Collection) evt.getNewValue();
                    for (Iterator it = changedProps.iterator(); it.hasNext(); ) {
                        PropertyChangeEvent evt2 = (PropertyChangeEvent) it.next();
                        String varName = evt2.getPropertyName().substring(PROP_VAR_CHANGED.length());
                        String varValue = (String) evt2.getNewValue();
                        if (varValue == null) return ;
                        varChanged(varName, varValue);
                    }
                } else {
                    String varName = propName.substring(PROP_VAR_CHANGED.length());
                    String varValue = (String) evt.getNewValue();
                    if (varValue == null) return ;
                    varChanged(varName, varValue);
                }
            }
        }
        
        private void varChanged(String varName, String varValue) {
            vars.put(varName, varValue);
            String textExpanded = Variables.expand(vars, text, false);
            try {
                setTextMethod.invoke(textComponent, new Object[] { textExpanded });
            } catch (Exception ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        
    }
    
    public interface FilePromptDocumentListener {
        public void filePromptDocumentCleanup(javax.swing.JTextArea ta, int promptNum, Object docIdentif);
    }
    
    private interface HistoryListener {
        public void changeHistory(int index1, int index2);
    }
    
    private class AutoFillRunner extends Object implements Runnable {
        
        private VcsDescribedCommand cmd;
        
        public AutoFillRunner(VcsDescribedCommand cmd) {
            this.cmd = cmd;
        }
        
        public void run() {                      
            Hashtable origVars = vars;
            try {
                vars = new Hashtable(origVars);
                // Apply all actions to the copy of the original variables
                for (Iterator it = actionList.iterator(); it.hasNext(); ) {
                    ActionListener listener = (ActionListener) it.next();
                    listener.actionPerformed(null);
            }            
            cmd.setAdditionalVariables(vars);
            } finally {
                // We have to reset the variables back!
                vars = origVars;
            }           
            CommandTask cmdTask = cmd.execute();
            cmdTask.waitFinished();
            if(cmdTask.getExitStatus() != 0)
                return;            
            VcsDescribedTask descTask = (VcsDescribedTask)cmdTask;            
            Hashtable varsAfterChange = new Hashtable(descTask.getVariables()); 
            updateVariableValues(varsAfterChange);           
        }
    }
   
}
