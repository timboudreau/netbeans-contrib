/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.util;

import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;

import org.openide.TopManager;
import org.openide.NotifyDescriptor;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.commands.*;

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
    
    private VcsFileSystem fileSystem = null;
    private Hashtable vars = null;
    private boolean expert = false;
    private String exec = null;
    
    private VariableInputDescriptor inputDescriptor;
    private VariableInputDescriptor globalDescriptor = null;
    private ArrayList globalVars = null;
    private String globalLabel = null;
    
    private ArrayList actionList = new ArrayList();
    private ActionListener closeListener = null;
    private ArrayList historyListeners = new ArrayList();
    private ArrayList focusListenersToCallBeforeValidate = new ArrayList();
    private int historySize = 0;
    private int currentHistory = 0;
    private int promptAreaNum = 0;
    
    private HashMap awtComponentsByVars = new HashMap();
    private HashMap componentsByVars = new HashMap();
    
    static final long serialVersionUID = 8363935602008486018L;
    
    /** Creates new form VariableInputDialog. This JPanel should be used
     * with DialogDescriptor to get the whole dialog.
     * @param files the files to get the input for
     * @param actionListener the listener to OK and Cancel buttons
     * @param expert the expert mode
     */
    public VariableInputDialog(String[] files, VariableInputDescriptor inputDescriptor, boolean expert) {
        this(files, inputDescriptor, expert, null);
    }

    /** Creates new form VariableInputDialog. This JPanel should be used
     * with DialogDescriptor to get the whole dialog.
     * @param files the files to get the input for
     * @param actionListener the listener to OK and Cancel buttons
     * @param expert the expert mode
     * @param vars the filesystem variables
     */
    public VariableInputDialog(String[] files, VariableInputDescriptor inputDescriptor, boolean expert, Hashtable vars) {
        initComponents();
        this.inputDescriptor = inputDescriptor;
        this.expert = expert;
        this.vars = vars;
        initComponentsFromDescriptor(inputDescriptor, variablePanel);
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
    
    private void setMnemonics() {
        prevButton.setMnemonic(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.prevButton.mnemonic").charAt(0));
        nextButton.setMnemonic(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.nextButton.mnemonic").charAt(0));
        asDefaultButton.setMnemonic(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("asDefaultButton.mnemonic").charAt(0));
        getDefaultButton.setMnemonic(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("getDefaultButton.mnemonic").charAt(0));
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
        }
    }
    
    public VariableInputDialog.FilePromptDocumentListener getFilePromptDocumentListener() {
        return this.docListener;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
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
        java.awt.GridBagConstraints gridBagConstraints1;
        
        navigationPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints2;
        
        prevButton.setText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.prevButton.text"));
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.insets = new java.awt.Insets(0, 0, 0, 5);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
        navigationPanel.add(prevButton, gridBagConstraints2);
        
        nextButton.setText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.nextButton.text"));
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.insets = new java.awt.Insets(0, 0, 0, 5);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
        navigationPanel.add(nextButton, gridBagConstraints2);
        
        asDefaultButton.setText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("asDefaultButton.text"));
        asDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                asDefaultButtonActionPerformed(evt);
            }
        });
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.insets = new java.awt.Insets(0, 0, 0, 5);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
        navigationPanel.add(asDefaultButton, gridBagConstraints2);
        
        getDefaultButton.setText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("getDefaultButton.text"));
        getDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getDefaultButtonActionPerformed(evt);
            }
        });
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints2.weightx = 1.0;
        navigationPanel.add(getDefaultButton, gridBagConstraints2);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 12);
        gridBagConstraints1.weightx = 1.0;
        add(navigationPanel, gridBagConstraints1);
        
        variableTabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jPanel1.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints3;
        
        variablePanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints4;
        
        gridBagConstraints4 = new java.awt.GridBagConstraints();
        gridBagConstraints4.gridy = 100;
        gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.weighty = 1.0;
        variablePanel.add(pushPanel1, gridBagConstraints4);
        
        gridBagConstraints3 = new java.awt.GridBagConstraints();
        gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints3.insets = new java.awt.Insets(12, 12, 12, 12);
        gridBagConstraints3.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.weighty = 1.0;
        jPanel1.add(variablePanel, gridBagConstraints3);
        
        variableTabbedPane.addTab("jPanel1", jPanel1);
        
        jPanel2.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints5;
        
        globalInputPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints6;
        
        pushPanel2.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints7;
        
        gridBagConstraints6 = new java.awt.GridBagConstraints();
        gridBagConstraints6.gridy = 100;
        gridBagConstraints6.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints6.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints6.weightx = 1.0;
        gridBagConstraints6.weighty = 1.0;
        globalInputPanel.add(pushPanel2, gridBagConstraints6);
        
        gridBagConstraints5 = new java.awt.GridBagConstraints();
        gridBagConstraints5.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints5.insets = new java.awt.Insets(12, 12, 12, 12);
        gridBagConstraints5.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints5.weightx = 1.0;
        gridBagConstraints5.weighty = 1.0;
        jPanel2.add(globalInputPanel, gridBagConstraints5);
        
        variableTabbedPane.addTab("jPanel2", jPanel2);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 12);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(variableTabbedPane, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(5, 12, 0, 12);
        add(promptEachSeparator, gridBagConstraints1);
        
        promptEachCheckBox.setText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.promptEachCheckBox.text"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 5;
        gridBagConstraints1.insets = new java.awt.Insets(0, 12, 0, 12);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(promptEachCheckBox, gridBagConstraints1);
        
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
    private javax.swing.JPanel navigationPanel;
    private javax.swing.JButton prevButton;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton asDefaultButton;
    private javax.swing.JButton getDefaultButton;
    private javax.swing.JTabbedPane variableTabbedPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel variablePanel;
    private javax.swing.JPanel pushPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel globalInputPanel;
    private javax.swing.JPanel pushPanel2;
    private javax.swing.JSeparator promptEachSeparator;
    private javax.swing.JCheckBox promptEachCheckBox;
    // End of variables declaration//GEN-END:variables

    /**
     * Initialize variable input components from a variable input descriptor.
     * @param inputDescriptor the variable input descriptor
     * @param inputPanel the panel on which are the components created
     */
    private void initComponentsFromDescriptor(VariableInputDescriptor inputDescriptor,
                                              javax.swing.JPanel inputPanel) {
        int gridy = 0;
        if (inputDescriptor != null) {
            VariableInputComponent[] components = inputDescriptor.components();
            if (components.length > 0) historySize = Integer.MAX_VALUE;
            for (int i = 0; i < components.length; i++) {
                gridy = addComponent(components[i], gridy, inputPanel, 0);
                historySize = Math.min(historySize, components[i].getHistorySize());
            }
        }
        labelOffset = gridy;
    }
    
    private int addComponent(final VariableInputComponent component, int gridy,
                             javax.swing.JPanel inputPanel, int leftInset) {
        if (VariableInputComponent.isVarConditionMatch(component.getVarConditions(), vars)) {
            if (expert || !component.isExpert()) {
                int componentId = component.getComponent();
                switch (componentId) {
                    case VariableInputDescriptor.INPUT_PROMPT_FIELD:
                        addVarPromptField(component, gridy, inputPanel, leftInset);
                        gridy++;
                        break;
                    case VariableInputDescriptor.INPUT_PROMPT_AREA:
                        addVarPromptArea(component, gridy, promptAreaNum++, inputPanel, leftInset);
                        gridy += 2;
                        break;
                    case VariableInputDescriptor.INPUT_ASK:
                        addAskChBox(component, gridy, inputPanel, leftInset);
                        gridy++;
                        break;
                    case VariableInputDescriptor.INPUT_SELECT_RADIO:
                        gridy = addSelectRadio(component, gridy, inputPanel, leftInset);
                        break;
                    case VariableInputDescriptor.INPUT_SELECT_COMBO:
                        addSelectCombo(component, gridy, inputPanel, leftInset);
                        gridy++;
                        break;
                    case VariableInputDescriptor.INPUT_GLOBAL:
                        setGlobalVars(component);
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
            TopManager.getDefault().notify(new NotifyDescriptor.Message(validator.getMessage(), NotifyDescriptor.Message.WARNING_MESSAGE));
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
                            if (closeListener != null) {
                                closeListener.actionPerformed(ev);
                                closeListener = null;
                            }
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
    
    public void setCloseListener(ActionListener closeListener) {
        this.closeListener = closeListener;
    }
    
    private void freeReferences() {
        fileSystem = null;
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
    public void setVCSFileSystem(VcsFileSystem fileSystem, Hashtable vars) {
        this.fileSystem = fileSystem;
        this.vars = vars;
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
    
    private void enableComponents(String[] vars, boolean enable) {
        //System.out.println("enableComponents("+VcsUtilities.arrayToString(vars)+", "+enable+")");
        for (int i = 0; i < vars.length; i++) {
            java.awt.Component[] components = (java.awt.Component[]) awtComponentsByVars.get(vars[i]);
            //System.out.println(" component("+vars[i]+") = "+components);
            if (components != null) {
                for (int j = 0; j < components.length; j++) {
                    //System.out.println("  components["+j+"] = "+enable);
                    components[j].setEnabled(enable);
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
        //if (Boolean.getBoolean("netbeans.accessibility")) {
            if (a11yName != null)
                jComponent.getAccessibleContext().setAccessibleName(a11yName);
            if (a11yDescription != null)
                jComponent.getAccessibleContext().setAccessibleDescription(a11yDescription);
        //}
    }
    
    private void addVarPromptField(final VariableInputComponent component,
                                   int gridy, javax.swing.JPanel variablePanel, int leftInset) {
        String varLabel = component.getLabel();
        ArrayList componentList = new ArrayList();
        final javax.swing.JTextField field = new javax.swing.JTextField(TEXTFIELD_COLUMNS);
        if (varLabel != null && varLabel.length() > 0) {
            javax.swing.JLabel label = new javax.swing.JLabel(varLabel);
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
        setA11y(field, component);
        String value;
        if (component.needsPreCommandPerform()) {
            value = component.getValue();
        } else {
            value = component.getDefaultValue();
        }
        if (value != null) {
            value = Variables.expand(vars, value, true);
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
        VcsUtilities.removeEnterFromKeymap(field);
        String selector = component.getSelector();
        //System.out.println("Match selector '"+selector+"': ("+component.getSelectorVarConditions()[0]+", "+component.getSelectorVarConditions()[1]+")"+VariableInputComponent.isVarConditionMatch(component.getSelectorVarConditions(), vars));
        if (selector != null &&
            !VariableInputComponent.isVarConditionMatch(component.getSelectorVarConditions(), vars)
        ) {
            selector = null;
        }
        if (selector != null) {
            java.awt.Component awtComponent = null;
            if (VariableInputDescriptor.SELECTOR_DIR.equals(selector)) {
                awtComponent = addBrowseDir(variablePanel, field, gridy);
            } else if (VariableInputDescriptor.SELECTOR_FILE.equals(selector)) {
                awtComponent = addBrowseFile(variablePanel, field, gridy);
            } else if (VariableInputDescriptor.SELECTOR_DATE_CVS.equals(selector)) {
                awtComponent = addDateCVS(variablePanel, field, gridy);
            } else if (selector.indexOf(VariableInputDescriptor.SELECTOR_CMD) == 0) {
                awtComponent = addSelector(variablePanel, field, gridy,
                                           selector.substring(VariableInputDescriptor.SELECTOR_CMD.length()));
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
        field.addInputMethodListener(new InputMethodListener() {
            public void caretPositionChanged(InputMethodEvent event) {
            }
            public void inputMethodTextChanged(InputMethodEvent event) {
                component.setValue(field.getText());
            }
        });
        FocusListener l;
        field.addFocusListener(l = new FocusListener() {
            public void focusGained(FocusEvent fevt) {}
            public void focusLost(FocusEvent fevt) {
                component.setValue(field.getText());
            }
        });
        focusListenersToCallBeforeValidate.add(l);
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
                field.setText(component.getHistoryValue(index));
            }
        });
    }
    
    private java.awt.Component addBrowseDir(final javax.swing.JPanel panel, final javax.swing.JTextField field, int y) {
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = y;
        //gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets (0, 8, 8, 0);
        javax.swing.JButton button = new javax.swing.JButton(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.Browse"));
        panel.add(button, gridBagConstraints);
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChooseDirDialog chooseDir = new ChooseDirDialog(new javax.swing.JFrame(), new File(field.getText ()));
                VcsUtilities.centerWindow (chooseDir);
                chooseDir.show();
                String selected = chooseDir.getSelectedDir();
                if (selected == null) {
                    //D.deb("no directory selected"); // NOI18N
                    return ;
                }
                field.setText(selected);
            }
        });
        return button;
    }
    
    private java.awt.Component addBrowseFile(final javax.swing.JPanel panel, final javax.swing.JTextField field, int y) {
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = y;
        //gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets (0, 8, 8, 0);
        javax.swing.JButton button = new javax.swing.JButton(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.Browse"));
        panel.add(button, gridBagConstraints);
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChooseFileDialog chooseFile = new ChooseFileDialog(new javax.swing.JFrame(), new File(field.getText ()), false);
                VcsUtilities.centerWindow (chooseFile);
                chooseFile.show();
                String selected = chooseFile.getSelectedFile();
                if (selected == null) {
                    //D.deb("no directory selected"); // NOI18N
                    return ;
                }
                field.setText(selected);
            }
        });
        return button;
    }
    
    private java.awt.Component addDateCVS(final javax.swing.JPanel panel, final javax.swing.JTextField field, int y) {
        field.setToolTipText(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.DateCVS"));
        return null;
    }

    private java.awt.Component addSelector(final javax.swing.JPanel panel, final javax.swing.JTextField field, int y,
                                           final String commandName) {
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = y;
        //gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets (0, 8, 8, 0);
        javax.swing.JButton button = new javax.swing.JButton(org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.Select"));
        panel.add(button, gridBagConstraints);
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                org.openide.util.RequestProcessor.postRequest(new Runnable() {
                    public void run() {
                        String selected = getSelectorText(commandName, field.getText());
                        //System.out.println("selected = "+selected);
                        if (selected != null) {
                            field.setText(selected);
                        }
                    }
                });
            }
        });
        return button;
    }
    
    private String getSelectorText(String commandName, String oldText) {
        VcsCommand cmd = fileSystem.getCommand(commandName);
        //OutputContainer container = new OutputContainer(cmd);
        Hashtable varsCopy = new Hashtable(vars);
        VcsCommandExecutor ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, varsCopy);
        if (ec == null) return null;
        //ec.setErrorNoRegexListener(container);
        //ec.setOutputNoRegexListener(container);
        //ec.setErrorContainer(container);
        final StringBuffer selectorOutput = new StringBuffer();
        final boolean[] selectorMatched = new boolean[] { false };
        ec.addDataOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] elements) {
                if (elements != null) {
                    selectorMatched[0] = true;
                    selectorOutput.append(VcsUtilities.array2string(elements).trim());
                }
            }
        });
        CommandsPool pool = fileSystem.getCommandsPool();
        int preprocessStatus = pool.preprocessCommand(ec, varsCopy, fileSystem);
        if (preprocessStatus != CommandsPool.PREPROCESS_DONE) return null;
        pool.startExecutor(ec);
        try {
            pool.waitToFinish(ec);
        } catch (InterruptedException iexc) {
            pool.kill(ec);
            return null;
        }
        if (ec.getExitStatus() == VcsCommandExecutor.SUCCEEDED
            && selectorMatched[0]) {
            return selectorOutput.toString();
        } else return null;
    }
    
    private void addAskChBox(final VariableInputComponent component, int gridy,
                             javax.swing.JPanel variablePanel, int leftInset) {
        String label = component.getLabel();
        final javax.swing.JCheckBox chbox = new javax.swing.JCheckBox(" "+label);
        //chbox.setBorder(new javax.swing.border.EmptyBorder(1, 0, 1, 0));
        String askDefault;
        if (component.needsPreCommandPerform()) {
            askDefault = component.getValue();
        } else {
            askDefault = component.getDefaultValue();
            component.setValue(askDefault);
        }
        if (askDefault != null) {
            askDefault = Variables.expand(vars, askDefault, true);
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
                if (selected && valueSelected != null) {
                    component.setValue(valueSelected);
                } else if (!selected && valueUnselected != null) {
                    component.setValue(valueUnselected);
                } else {
                    component.setValue(selected ? Boolean.TRUE.toString() : "");
                }
            }
        });
        awtComponentsByVars.put(component.getVariable(), new java.awt.Component[] { chbox });
        final String[] varsEnabled = (String[]) component.getEnable().toArray(new String[0]);
        final String[] varsDisabled = (String[]) component.getDisable().toArray(new String[0]);
        chbox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ev) {
                enableComponents(varsEnabled, chbox.isSelected());
                enableComponents(varsDisabled, !chbox.isSelected());
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
                                  final int promptAreaNum, javax.swing.JPanel variablePanel, int leftInset) {
        String message = component.getLabel();
        javax.swing.JLabel label = new javax.swing.JLabel(message);
        java.awt.Dimension dimension = component.getDimension();
        if (dimension == null) dimension = new java.awt.Dimension(TEXTAREA_ROWS, TEXTAREA_COLUMNS);
        final javax.swing.JTextArea area = new javax.swing.JTextArea(dimension.width, dimension.height);
        label.setLabelFor(area);
        if (component.getLabelMnemonic() != null) {
            label.setDisplayedMnemonic(component.getLabelMnemonic().charValue());
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
        if (fileName == null || fileName.length() == 0) {
            try {
                fileName = java.io.File.createTempFile("tempVcsCmd", "input").getAbsolutePath();
            } catch (IOException exc) {
                TopManager.getDefault().notifyException(exc);
            }
        } else {
            fileName = Variables.expand(vars, fileName, true);
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
                               javax.swing.JPanel variablePanel, int leftInset) {
        ArrayList componentList = new ArrayList();
        String message = component.getLabel();
        if (message != null && message.length() > 0) {
            javax.swing.JLabel label = new javax.swing.JLabel(message);
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
        for (int i = 0; i < subComponents.length; i++) {
            gridy = addRadioButton(component, subComponents[i], gridy, group, variablePanel, leftInset);
        }
        for (Enumeration enum = group.getElements(); enum.hasMoreElements(); ) {
            componentList.add(enum.nextElement());
        }
        awtComponentsByVars.put(component.getVariable(), componentList.toArray(new java.awt.Component[0]));
        String defValue;
        if (component.needsPreCommandPerform()) {
            defValue = component.getValue();
        } else {
            defValue = component.getDefaultValue();
            component.setValue(defValue);
        }
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
    
    private int addRadioButton(final VariableInputComponent superComponent, final VariableInputComponent component, int gridy,
                               javax.swing.ButtonGroup group, javax.swing.JPanel variablePanel, int leftInset) {
        String label = component.getLabel();
        boolean firstSubLabelEmpty = false; // If the first sublabel is empty, put the first sub component to the same gridy as the button
        VariableInputComponent[] subComponents = component.subComponents();
        if (subComponents.length > 0) {
            String subLabel = subComponents[0].getLabel();
            firstSubLabelEmpty = subLabel == null || subLabel.length() == 0;
        }
        final javax.swing.JRadioButton button = new javax.swing.JRadioButton(label);
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
            gridy = addComponent(subComponents[i], gridy, variablePanel, inset);
            componentVarsList.add(subComponents[i].getVariable());
        }
        final String[] componentVars = (String[]) componentVarsList.toArray(new String[0]);
        enableComponents(componentVars, false);
        final String[] varsEnabled = (String[]) component.getEnable().toArray(new String[0]);
        final String[] varsDisabled = (String[]) component.getDisable().toArray(new String[0]);
        button.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ev) {
                enableComponents(componentVars, button.isSelected());
                enableComponents(varsEnabled, button.isSelected());
                enableComponents(varsDisabled, !button.isSelected());
            }
        });
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (button.isSelected()) {
                    superComponent.setValue(component.getValue());
                    //System.out.println("component '"+superComponent.getLabel()+"' setValue("+component.getValue()+")");
                }
            }
        });
        return gridy;
    }
    
    private static void selectButton(String value, VariableInputComponent[] subComponents,
                                     javax.swing.ButtonGroup group) {
        if (value == null && subComponents.length > 0) value = subComponents[0].getValue();
        Enumeration enum = group.getElements();
        for (int i = 0; enum.hasMoreElements(); i++) {
            javax.swing.JRadioButton radio = (javax.swing.JRadioButton) enum.nextElement();
            if (value.equals(subComponents[i].getValue())) radio.setSelected(true);
        }
    }

    private void addSelectCombo(final VariableInputComponent component, int gridy,
                                javax.swing.JPanel variablePanel, int leftInset) {
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
        if (message != null && message.length() > 0) {
            javax.swing.JLabel label = new javax.swing.JLabel(message);
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
            if (i >= items) i = 0;
        } else i = 0;
        comboBox.setSelectedIndex(i);
        enableComponents(varsEnabled[i], true);
        enableComponents(varsDisabled[i], false);
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int selected2 = comboBox.getSelectedIndex();
                //System.out.println("Combo Action: selected = "+selected2+" = "+subComponents[selected2].getValue());
                component.setValue(subComponents[selected2].getValue());
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
                    enableComponents(varsEnabled[index], selected2);
                    enableComponents(varsDisabled[index], !selected2);
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
                    TopManager.getDefault().notifyException(exc);
                } catch (IOException exc) {
                    TopManager.getDefault().notifyException(exc);
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
                TopManager.getDefault().notifyException(exc);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ioexc) {}
                }
            }
        //}
    }
    
    public interface FilePromptDocumentListener {
        public void filePromptDocumentCleanup(javax.swing.JTextArea ta, int promptNum, Object docIdentif);
    }
    
    private interface HistoryListener {
        public void changeHistory(int index1, int index2);
    }
}
