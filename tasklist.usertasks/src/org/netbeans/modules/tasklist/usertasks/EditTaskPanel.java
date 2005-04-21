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

package org.netbeans.modules.tasklist.usertasks;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import org.netbeans.modules.tasklist.usertasks.dependencies.DependenciesPanel;
import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.renderers.PriorityListCellRenderer;
import org.openide.ErrorManager;
import org.openide.awt.Mnemonics;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;

/**
 * Panel used to enter/edit a user task.
 * Please read comment at the beginning of initA11y before editing
 * this file using the form builder.
 *
 * @author Tor Norbye
 * @author tl
 */
public class EditTaskPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1;

    private static String[] PERCENTS = {
        "0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%", // NOI18N
        "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%" // NOI18N
    };
    
    /**
     * Parses values with % sign
     *
     * @param text text to parse
     * @return parsed value
     */
    private static int parsePercents(String text) throws NumberFormatException {
        text = text.trim();
        if (text.endsWith("%")) // NOI18N
            text = text.substring(0, text.length() - 1);
        int n = Integer.parseInt(text);
        if (n < 0 || n > 100)
            throw new NumberFormatException("Wrong range"); // NOI18N
        return n;
    }
    
    /**
     * InputVerifier for the combobox with percents
     */
    private static class PercentsInputVerifier extends InputVerifier {
        public boolean verify(javax.swing.JComponent input) {
            String p = ((JTextComponent) input).getText();
            try {
                parsePercents(p);
                return true;
            } catch (NumberFormatException e) {
                UTUtils.LOGGER.fine("wrong format"); // NOI18N
                return false;
            }
        }
    }
    
    /**
     * InputVerifier for a JTextField with an integer
     */
    private static class IntTextFieldInputVerifier extends InputVerifier {
        public boolean verify(javax.swing.JComponent input) {
            String p = ((JTextComponent) input).getText();
            try {
                Integer.parseInt(p);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
    
    private static boolean appendDefault = Settings.getDefault().getAppend();
    
    private SimpleDateFormat format;
    private ComboBoxModel prioritiesModel = 
        new DefaultComboBoxModel(new Integer[] {
            new Integer(UserTask.HIGH),
            new Integer(UserTask.MEDIUM_HIGH),
            new Integer(UserTask.MEDIUM),
            new Integer(UserTask.MEDIUM_LOW),
            new Integer(UserTask.LOW),
        });
    private ListCellRenderer priorityRenderer = new PriorityListCellRenderer();
    private DurationPanel durationPanel = new DurationPanel();
    private DependenciesPanel dp;
    //private com.toedter.calendar.JDateChooser dueChooser = 
    //    new com.toedter.calendar.JDateChooser();
    
    /** 
     * Creates new form NewTodoItemPanel.
     *
     * @param editing true = no append/prepend options
     */
    public EditTaskPanel(boolean editing) {
        initComponents();
        initA11y();
        
        priorityComboBox.setSelectedIndex(2);
        
        format = new SimpleDateFormat();
        
        addSourceButton.addActionListener(this);
        
        if (editing) {
            jPanelGeneral.remove(addLabel);
            jPanelGeneral.remove(beginningToggle);
            jPanelGeneral.remove(endToggle);
            jPanelGeneral.remove(addSourceButton);
        } else {
            boolean append = appendDefault;
            if (append) {
                endToggle.setSelected(true);
            } else {
                beginningToggle.setSelected(true);
            }
        }
        
        jComboBoxProgress.setModel(new DefaultComboBoxModel(PERCENTS));
        ((JComponent) jComboBoxProgress.getEditor().getEditorComponent()).
            setInputVerifier(new EditTaskPanel.PercentsInputVerifier());
        
        jPanelEffort.add(durationPanel, BorderLayout.CENTER);
        
        dp = new DependenciesPanel();
        dp.setBorder(new EmptyBorder(11, 11, 12, 12));
        jPanelDependencies.add(dp, BorderLayout.CENTER);
    }
    
    public void addNotify() {
        super.addNotify();
        descriptionTextField.requestFocus();
    }    
    
    /**
     * Transfers the focus into the summary text field
     */
    public void focusSummary() {
        jTabbedPane.setSelectedIndex(0);
        descriptionTextField.requestFocus();
    }
    
    /**
     * Fills the panel with the values of a user task
     *
     * @param item a user task
     */
    public void fillPanel(UserTask item) {
        if (item.getSummary() != null) {
            descriptionTextField.setText(item.getSummary());
        }
        priorityComboBox.setSelectedItem(new Integer(item.getPriority()));
        if (item.getLine() != null) {
            URL url = item.getUrl();
            if (url != null) {
                fileTextField.setText(url.toExternalForm());
                if (fileTextField.getText().length() > 0)
                    fileTextField.setCaretPosition(fileTextField.getText().length()-1);
                fileCheckBox.setSelected(true);
                lineTextField.setText(Integer.toString(item.getLineNumber() + 1));
            }
        } else {
            fileCheckBox.setSelected(false);
        }
        detailsTextArea.setText(item.getDetails());
        setDueDate(item.getDueDate());

        // Initialize the Categories list
        String[] categories = item.getList().getCategories();
        if (categories.length > 0) {
            DefaultComboBoxModel model = new DefaultComboBoxModel(categories);
            categoryCombo.setModel(model);
            UTUtils.LOGGER.fine("categories.size = " + categories.length); // NOI18N
        }
        categoryCombo.setSelectedItem(item.getCategory());
        
        jComboBoxProgress.setSelectedItem(item.getPercentComplete() + "%"); // NOI18N
        if (item.isProgressComputed())
            jRadioButtonComputeProgress.setSelected(true);
        else
            jRadioButtonProgress.setSelected(true);
        jRadioButtonProgressItemStateChanged(null);
        
        durationPanel.setDuration(item.getEffort());
        if (item.isEffortComputed())
            jRadioButtonComputeEffort.setSelected(true);
        else
            jRadioButtonEffort.setSelected(true);
        jRadioButtonEffortItemStateChanged(null);
        
        DateFormat df = DateFormat.getDateTimeInstance(
            DateFormat.LONG, DateFormat.LONG);
        jLabelCreated.setText(df.format(new Date(item.getCreatedDate())));
        jLabelLastEdited.setText(df.format(new Date(item.getLastEditedDate())));
        
        durationPanelSpent.setDuration(item.getSpentTime());
        if (item.isSpentTimeComputed())
            jRadioButtonComputeSpent.setSelected(true);
        else
            jRadioButtonSpent.setSelected(true);
        
        dp.fillPanel(item);
        
        String[] owners = item.getList().getOwners();
        if (owners.length > 0) {
            DefaultComboBoxModel model = new DefaultComboBoxModel(owners);
            jComboBoxOwner.setModel(model);
        }
        jComboBoxOwner.setSelectedItem(item.getOwner());
        
        jLabelCompleted.setText(df.format(new Date(item.getCompletedDate())));
        
        DefaultListModel dlm = new DefaultListModel();
        for (int i = 0; i < item.getWorkPeriods().size(); i++) {
            UserTask.WorkPeriod wp = (UserTask.WorkPeriod)
                item.getWorkPeriods().get(i);
            dlm.addElement(
                df.format(new Date(wp.getStart())) + ", " + // NOI18N
                new Duration(wp.getDuration(), 
                    Settings.getDefault().getHoursPerDay(),
                    Settings.getDefault().getDaysPerWeek()
                ).format()
            );
        }
        jListWorkPeriods.setModel(dlm);
        
        jLabelSpentTimeToday.setText(
            new Duration(item.getSpentTimeToday(),
                Settings.getDefault().getHoursPerDay(),
                Settings.getDefault().getDaysPerWeek()).format());
    }
    
    /**
     * Fills an object with the values from this panel
     *
     * @param task a user object
     */
    public void fillObject(UserTask task) {
        task.setSummary(descriptionTextField.getText().trim());
        task.setDetails(detailsTextArea.getText().trim());
        if (categoryCombo.getSelectedItem() == null)
            task.setCategory(""); // NOI18N
        else
            task.setCategory(categoryCombo.getSelectedItem().toString().trim());
        int p = ((Integer) priorityComboBox.getSelectedItem()).intValue();
        task.setPriority(p);
        if (fileCheckBox.isSelected()) {
            try {
                URL url = new URL(fileTextField.getText().trim());
                task.setUrl(url);
                try {
                    int lineno = Integer.parseInt(lineTextField.getText());
                    if (lineno > 0)
                        task.setLineNumber(lineno - 1);
                    else
                        ; // TODO: validation
                } catch (NumberFormatException e) {
                    // TODO validation
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                // TODO: validation
                e.printStackTrace();
            }
        } else {
            task.setLine(null);
        }
        
        task.setDueDate(getDueDate());
        task.setProgressComputed(jRadioButtonComputeProgress.isSelected());
        if (!task.isProgressComputed()) {
            task.setPercentComplete(
                parsePercents((String) jComboBoxProgress.getSelectedItem()));
        }
        
        task.setEffortComputed(jRadioButtonComputeEffort.isSelected());
        if (!task.isEffortComputed()) {
            task.setEffort(durationPanel.getDuration());
        }

        task.setSpentTimeComputed(jRadioButtonComputeSpent.isSelected());
        if (!task.isSpentTimeComputed()) {
            task.setSpentTime(durationPanelSpent.getDuration());
        }
        
        dp.fillObject();
        
        if (jComboBoxOwner.getSelectedItem() == null)
            task.setOwner(""); // NOI18N
        else
            task.setOwner(jComboBoxOwner.getSelectedItem().toString().trim());
    }
    
    /**
     * Returns the value of the due date
     *
     * @return due date
     */
    private Date getDueDate() {
        Date ret;
        if (dueCheckBox.isSelected()) {
            ret = jDateChooserDue.getDate();
            UTUtils.LOGGER.fine(ret.toString());
            Calendar c = Calendar.getInstance();
            c.setTime(ret);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            ret = c.getTime();
            UTUtils.LOGGER.fine("corrected:" + ret.toString()); // NOI18N
        } else {
            ret = null;
        }
        return ret;
    }
    
    /**
     * TODO - preserve this setting from run to run! (Unless you change
     * the default!)
     *
     * @return true = the task should be appended
     */
    public boolean getAppend() {
        appendDefault = endToggle.isSelected();
        return appendDefault;
    }
    
    /**
     * Set the due date field
     *
     * @param d the due date
     */
    private void setDueDate(Date d) {
        if (d != null) {
            jDateChooserDue.setDate(d);
            dueCheckBox.setSelected(true);
            enableDueChooser(true);
        } else {
            jDateChooserDue.setDate(new Date());
            dueCheckBox.setSelected(false);
            enableDueChooser(false);
        }
    }
    
    /**
     * Changes associated file position in the dialog
     *
     * @param n line number
     */
    public void setLineNumber(int n) {
        lineTextField.setText(Integer.toString(n + 1));
    }

    /**
     * Sets new URL.
     *
     * @param URL an URL or null
     */
    public void setUrl(URL url) {
        if (url != null)
            fileTextField.setText(url.toExternalForm());
        else
            fileTextField.setText(""); // NOI18N
    }
    
    public void setAssociatedFilePos(boolean set) {
        fileCheckBox.setSelected(set);
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == addSourceButton) {
            HelpCtx help = new HelpCtx("org.netbeans.modules.tasklist.usertasks.AddTask"); // NOI18N
            
            // This copied from openide/deprecated/.../TopManager.showHelp:
            
            // Awkward but should work.
            // XXX could instead just make tasklist-usertasks.jar
            // depend on javahelp-api.jar.
            ClassLoader systemClassLoader = (ClassLoader)Lookup.getDefault().
            lookup(ClassLoader.class);
            
            try {
                Class c = systemClassLoader.
                loadClass("org.netbeans.api.javahelp.Help"); // NOI18N
                Object o = Lookup.getDefault().lookup(c);
                if (o != null) {
                    Method m = c.getMethod("showHelp", // NOI18N
                        new Class[] {HelpCtx.class});
                    m.invoke(o, new Object[] {help});
                    return;
                }
            } catch (ClassNotFoundException cnfe) {
                // ignore - maybe javahelp module is not installed, not
                // so strange
            } catch (Exception e) {
                // potentially more serious
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
            // Did not work.
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        addButtonGroup = new javax.swing.ButtonGroup();
        effortButtonGroup = new javax.swing.ButtonGroup();
        buttonGroupProgress = new javax.swing.ButtonGroup();
        buttonGroupSpent = new javax.swing.ButtonGroup();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanelGeneral = new javax.swing.JPanel();
        descLabel = new javax.swing.JLabel();
        categoryCombo = new javax.swing.JComboBox();
        detailsScrollPane = new javax.swing.JScrollPane();
        detailsTextArea = new javax.swing.JTextArea();
        addLabel = new javax.swing.JLabel();
        beginningToggle = new javax.swing.JRadioButton();
        detailsLabel = new javax.swing.JLabel();
        endToggle = new javax.swing.JRadioButton();
        descriptionTextField = new javax.swing.JTextField();
        priorityComboBox = new javax.swing.JComboBox();
        fileCheckBox = new javax.swing.JCheckBox();
        categoryLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        fileTextField = new javax.swing.JTextField();
        lineTextField = new javax.swing.JTextField();
        lineLabel = new javax.swing.JLabel();
        addSourceButton = new javax.swing.JButton();
        prioLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxOwner = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        dueCheckBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabelLastEdited = new javax.swing.JLabel();
        jLabelCreated = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jRadioButtonComputeEffort = new javax.swing.JRadioButton();
        jRadioButtonEffort = new javax.swing.JRadioButton();
        jPanelEffort = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jComboBoxProgress = new javax.swing.JComboBox();
        jRadioButtonComputeProgress = new javax.swing.JRadioButton();
        jRadioButtonProgress = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jRadioButtonComputeSpent = new javax.swing.JRadioButton();
        jRadioButtonSpent = new javax.swing.JRadioButton();
        durationPanelSpent = new org.netbeans.modules.tasklist.usertasks.DurationPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabelCompleted = new javax.swing.JLabel();
        jDateChooserDue = new com.toedter.calendar.JDateChooser();
        jPanelDependencies = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListWorkPeriods = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jLabelSpentTimeToday = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        setPreferredSize(new java.awt.Dimension(400, 300));
        jPanelGeneral.setLayout(new java.awt.GridBagLayout());

        jPanelGeneral.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(11, 11, 12, 12)));
        descLabel.setLabelFor(descriptionTextField);
        /*
        org.openide.awt.Mnemonics.setLocalizedText(descLabel, NbBundle.getMessage(EditTaskPanel.class, "Brief_Description")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    jPanelGeneral.add(descLabel, gridBagConstraints);

    categoryCombo.setEditable(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    jPanelGeneral.add(categoryCombo, gridBagConstraints);

    detailsTextArea.setRows(5);
    detailsScrollPane.setViewportView(detailsTextArea);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    jPanelGeneral.add(detailsScrollPane, gridBagConstraints);

    addLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "AddTo")); // NOI18N();
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanelGeneral.add(addLabel, gridBagConstraints);

    addButtonGroup.add(beginningToggle);
    /*
    org.openide.awt.Mnemonics.setLocalizedText(beginningToggle, NbBundle.getMessage(EditTaskPanel.class, "BeginningList")); // NOI18N();
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanelGeneral.add(beginningToggle, gridBagConstraints);

    detailsLabel.setLabelFor(detailsTextArea);
    /*
    detailsLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "DetailsLabel")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanelGeneral.add(detailsLabel, gridBagConstraints);

    addButtonGroup.add(endToggle);
    /*
    org.openide.awt.Mnemonics.setLocalizedText(endToggle, NbBundle.getMessage(EditTaskPanel.class, "EndList")); // NOI18N();
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
    jPanelGeneral.add(endToggle, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    jPanelGeneral.add(descriptionTextField, gridBagConstraints);

    priorityComboBox.setModel(prioritiesModel);
    priorityComboBox.setRenderer(priorityRenderer);
    priorityComboBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            priorityComboBoxActionPerformed(evt);
        }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    jPanelGeneral.add(priorityComboBox, gridBagConstraints);

    /*
    fileCheckBox.setText(NbBundle.getMessage(EditTaskPanel.class, "AssociatedFile")); // NOI18N);
    */
    fileCheckBox.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 0)));
    fileCheckBox.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            fileCheckBoxItemStateChanged(evt);
        }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanelGeneral.add(fileCheckBox, gridBagConstraints);

    categoryLabel.setLabelFor(categoryCombo);
    /*
    categoryLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "CategoryLabel")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanelGeneral.add(categoryLabel, gridBagConstraints);

    jPanel2.setLayout(new java.awt.GridBagLayout());

    fileTextField.setColumns(100);
    fileTextField.setEditable(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.7;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
    jPanel2.add(fileTextField, gridBagConstraints);

    lineTextField.setEditable(false);
    lineTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 0.3;
    jPanel2.add(lineTextField, gridBagConstraints);

    lineLabel.setLabelFor(lineTextField);
    /*
    lineLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "LineLabel")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
    jPanel2.add(lineLabel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanelGeneral.add(jPanel2, gridBagConstraints);

    /*
    addSourceButton.setText(NbBundle.getMessage(EditTaskPanel.class, "AddToSource")); // NOI18N();
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 8;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    jPanelGeneral.add(addSourceButton, gridBagConstraints);

    prioLabel.setLabelFor(priorityComboBox);
    /*
    prioLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "PriorityLabel")); // NOI18N);
    */
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanelGeneral.add(prioLabel, gridBagConstraints);

    org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getBundle(EditTaskPanel.class).getString("OwnerLabel"));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanelGeneral.add(jLabel1, gridBagConstraints);

    jComboBoxOwner.setEditable(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
    jPanelGeneral.add(jComboBoxOwner, gridBagConstraints);

    jTabbedPane.addTab(org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "General"), jPanelGeneral);

    jPanel3.setLayout(new java.awt.GridBagLayout());

    jPanel3.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(11, 11, 12, 12)));
    /*
    dueCheckBox.setText(NbBundle.getMessage(EditTaskPanel.class, "DueDateCb")); // NOI18N();
    */
    dueCheckBox.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            dueCheckBoxItemStateChanged(evt);
        }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanel3.add(dueCheckBox, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    jPanel3.add(jPanel1, gridBagConstraints);

    jLabel6.setText(org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "LastEditedLabel")); // NOI18N);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanel3.add(jLabel6, gridBagConstraints);

    jLabel5.setText(org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "CreatedLabel")); // NOI18N);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanel3.add(jLabel5, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanel3.add(jLabelLastEdited, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanel3.add(jLabelCreated, gridBagConstraints);

    jPanel4.setLayout(new java.awt.GridBagLayout());

    jPanel4.setBorder(new javax.swing.border.TitledBorder(org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "Effort")));
    effortButtonGroup.add(jRadioButtonComputeEffort);
    jRadioButtonComputeEffort.setSelected(true);
    org.openide.awt.Mnemonics.setLocalizedText(jRadioButtonComputeEffort, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "ComputeEffortAutomatically"));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    jPanel4.add(jRadioButtonComputeEffort, gridBagConstraints);

    effortButtonGroup.add(jRadioButtonEffort);
    org.openide.awt.Mnemonics.setLocalizedText(jRadioButtonEffort, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "UseThisValue"));
    jRadioButtonEffort.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            jRadioButtonEffortItemStateChanged(evt);
        }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
    jPanel4.add(jRadioButtonEffort, gridBagConstraints);

    jPanelEffort.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    jPanel4.add(jPanelEffort, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanel3.add(jPanel4, gridBagConstraints);

    jPanel5.setLayout(new java.awt.GridBagLayout());

    jPanel5.setBorder(new javax.swing.border.TitledBorder(org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "Progress")));
    jComboBoxProgress.setEditable(true);
    jComboBoxProgress.setInputVerifier(new PercentsInputVerifier());
    jComboBoxProgress.setMinimumSize(new java.awt.Dimension(60, 21));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    jPanel5.add(jComboBoxProgress, gridBagConstraints);

    buttonGroupProgress.add(jRadioButtonComputeProgress);
    jRadioButtonComputeProgress.setSelected(true);
    org.openide.awt.Mnemonics.setLocalizedText(jRadioButtonComputeProgress, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "ComputeProgressAutomatically"));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    jPanel5.add(jRadioButtonComputeProgress, gridBagConstraints);

    buttonGroupProgress.add(jRadioButtonProgress);
    org.openide.awt.Mnemonics.setLocalizedText(jRadioButtonProgress, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "UseThisValue"));
    jRadioButtonProgress.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            jRadioButtonProgressItemStateChanged(evt);
        }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
    jPanel5.add(jRadioButtonProgress, gridBagConstraints);

    jPanel6.setPreferredSize(new java.awt.Dimension(0, 0));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    jPanel5.add(jPanel6, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanel3.add(jPanel5, gridBagConstraints);

    jPanel7.setLayout(new java.awt.GridBagLayout());

    jPanel7.setBorder(new javax.swing.border.TitledBorder("Spent time"));
    buttonGroupSpent.add(jRadioButtonComputeSpent);
    jRadioButtonComputeSpent.setSelected(true);
    org.openide.awt.Mnemonics.setLocalizedText(jRadioButtonComputeSpent, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "ComputeSpentTkme"));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    jPanel7.add(jRadioButtonComputeSpent, gridBagConstraints);

    buttonGroupSpent.add(jRadioButtonSpent);
    org.openide.awt.Mnemonics.setLocalizedText(jRadioButtonSpent, org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "UseThisValue"));
    jRadioButtonSpent.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            jRadioButtonSpentItemStateChanged(evt);
        }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
    jPanel7.add(jRadioButtonSpent, gridBagConstraints);

    durationPanelSpent.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    jPanel7.add(durationPanelSpent, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanel3.add(jPanel7, gridBagConstraints);

    org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getBundle(EditTaskPanel.class).getString("Completed"));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanel3.add(jLabel2, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanel3.add(jLabelCompleted, gridBagConstraints);

    jDateChooserDue.setEnabled(false);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 12);
    jPanel3.add(jDateChooserDue, gridBagConstraints);

    jTabbedPane.addTab(org.openide.util.NbBundle.getMessage(EditTaskPanel.class, "TimeRelated"), jPanel3);

    jPanelDependencies.setLayout(new java.awt.BorderLayout());

    jTabbedPane.addTab(org.openide.util.NbBundle.getBundle(EditTaskPanel.class).getString("LBL_DependenciesTab"), jPanelDependencies);

    jPanel8.setLayout(new java.awt.GridBagLayout());

    jScrollPane1.setViewportView(jListWorkPeriods);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 12);
    jPanel8.add(jScrollPane1, gridBagConstraints);

    jLabel3.setText(org.openide.util.NbBundle.getBundle(EditTaskPanel.class).getString("SpentTimeToday"));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 11, 12, 11);
    jPanel8.add(jLabel3, gridBagConstraints);

    org.openide.awt.Mnemonics.setLocalizedText(jLabelSpentTimeToday, "\"\"");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(11, 0, 12, 12);
    jPanel8.add(jLabelSpentTimeToday, gridBagConstraints);

    jTabbedPane.addTab(org.openide.util.NbBundle.getBundle(EditTaskPanel.class).getString("WordPeriods"), jPanel8);

    add(jTabbedPane, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    private void jRadioButtonSpentItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonSpentItemStateChanged
        durationPanelSpent.setEnabled(jRadioButtonSpent.isSelected());
    }//GEN-LAST:event_jRadioButtonSpentItemStateChanged

    private void jRadioButtonProgressItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonProgressItemStateChanged
        jComboBoxProgress.setEnabled(jRadioButtonProgress.isSelected());
    }//GEN-LAST:event_jRadioButtonProgressItemStateChanged

    private void jRadioButtonEffortItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonEffortItemStateChanged
        durationPanel.setEnabled(jRadioButtonEffort.isSelected());
    }//GEN-LAST:event_jRadioButtonEffortItemStateChanged

    private void priorityComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_priorityComboBoxActionPerformed
        // I don't know why JComboBox does not use my renderer to draw 
        // selected value
        int sel = priorityComboBox.getSelectedIndex();
        priorityComboBox.setForeground(PriorityListCellRenderer.COLORS[sel]);
    }//GEN-LAST:event_priorityComboBoxActionPerformed

    /** Initialize accessibility settings on the panel */
    private void initA11y() {
        /*
          I couldn't figure out how to use Mnemonics.setLocalizedText
          to set labels and checkboxes with a mnemonic using the
          form builder, so the closest I got was to use "/*" and "* /
          as code pre-init/post-init blocks, such that I don't actually
          execute the bundle lookup code - and then call it explicitly
          below. (I wanted to keep the text on the components so that
          I can see them when visually editing the GUI.
        */

        Mnemonics.setLocalizedText(descLabel, NbBundle.getMessage(
                 EditTaskPanel.class, "Brief_Description")); // NOI18N
        Mnemonics.setLocalizedText(detailsLabel, NbBundle.getMessage(
                    EditTaskPanel.class, "DetailsLabel")); // NOI18N
        Mnemonics.setLocalizedText(prioLabel, NbBundle.getMessage(
                 EditTaskPanel.class, "PriorityLabel")); // NOI18N
        Mnemonics.setLocalizedText(fileCheckBox, NbBundle.getMessage(
                 EditTaskPanel.class, "AssociatedFile")); // NOI18N
        Mnemonics.setLocalizedText(categoryLabel, NbBundle.getMessage(
                     EditTaskPanel.class, "CategoryLabel")); // NOI18N
        Mnemonics.setLocalizedText(lineLabel, NbBundle.getMessage(
                 EditTaskPanel.class, "LineLabel")); // NOI18N
        Mnemonics.setLocalizedText(dueCheckBox, NbBundle.getMessage(
                   EditTaskPanel.class, "DueDateCb")); // NOI18N
        Mnemonics.setLocalizedText(addLabel, NbBundle.getMessage(
                EditTaskPanel.class, "AddTo")); // NOI18N
        Mnemonics.setLocalizedText(beginningToggle, NbBundle.getMessage(
                       EditTaskPanel.class, "BeginningList")); // NOI18N
        Mnemonics.setLocalizedText(endToggle, NbBundle.getMessage(
                 EditTaskPanel.class, "EndList")); // NOI18N
        Mnemonics.setLocalizedText(addSourceButton, NbBundle.getMessage(
                       EditTaskPanel.class, "AddToSource")); // NOI18N

        this.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_NewTask")); // NOI18N
        descriptionTextField.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Brief_Description")); // NOI18N
        detailsTextArea.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Details")); // NOI18N
        priorityComboBox.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Priority")); // NOI18N
        categoryCombo.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Category")); // NOI18N
        fileTextField.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_File")); // NOI18N

        // We're using a checkbox to "label" the textfield - of course JCheckBox
        // doesn't have a setLabelFor (since it is itself an input component)
        // so we have to label the associated component ourselves
        fileTextField.getAccessibleContext().setAccessibleName(fileCheckBox.getText());
        jDateChooserDue.getAccessibleContext().setAccessibleName(dueCheckBox.getText());

        lineTextField.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Line")); // NOI18N
        jDateChooserDue.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Due")); // NOI18N
        fileCheckBox.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_FileCb")); // NOI18N
        dueCheckBox.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_DueCb")); // NOI18N
        beginningToggle.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_Beginning")); // NOI18N
        endToggle.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_End")); // NOI18N
        addSourceButton.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(EditTaskPanel.class, "ACSD_AddSource")); // NOI18N

        // Gotta set accessible name - no more that I've set label for?
        // gotta set accessible description "everywhere" ?
    }

    private void fileCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fileCheckBoxItemStateChanged
        boolean s = fileCheckBox.isSelected();
        fileTextField.setEditable(s);
        lineTextField.setEditable(s);
    }//GEN-LAST:event_fileCheckBoxItemStateChanged

    /**
     * Callback function to enable / disable the due-date fields
     * @param evt the callback event
     */
    private void dueCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_dueCheckBoxItemStateChanged
        if (evt.getID() == ItemEvent.ITEM_STATE_CHANGED) {
            boolean enable = false;
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                enable = true;
            }
            enableDueChooser(enable);
        }
    }//GEN-LAST:event_dueCheckBoxItemStateChanged

    /**
     * Bugfix for JDateChooser.setEnabled
     *
     * @param enable true = enabled
     */
    private void enableDueChooser(boolean enable) {
        for (int i = 0; i < jDateChooserDue.getComponentCount(); i++) {
            jDateChooserDue.getComponent(i).setEnabled(enable);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup addButtonGroup;
    private javax.swing.JLabel addLabel;
    private javax.swing.JButton addSourceButton;
    private javax.swing.JRadioButton beginningToggle;
    private javax.swing.ButtonGroup buttonGroupProgress;
    private javax.swing.ButtonGroup buttonGroupSpent;
    private javax.swing.JComboBox categoryCombo;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JLabel descLabel;
    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JLabel detailsLabel;
    private javax.swing.JScrollPane detailsScrollPane;
    private javax.swing.JTextArea detailsTextArea;
    private javax.swing.JCheckBox dueCheckBox;
    private org.netbeans.modules.tasklist.usertasks.DurationPanel durationPanelSpent;
    private javax.swing.ButtonGroup effortButtonGroup;
    private javax.swing.JRadioButton endToggle;
    private javax.swing.JCheckBox fileCheckBox;
    private javax.swing.JTextField fileTextField;
    private javax.swing.JComboBox jComboBoxOwner;
    private javax.swing.JComboBox jComboBoxProgress;
    private com.toedter.calendar.JDateChooser jDateChooserDue;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelCompleted;
    private javax.swing.JLabel jLabelCreated;
    private javax.swing.JLabel jLabelLastEdited;
    private javax.swing.JLabel jLabelSpentTimeToday;
    private javax.swing.JList jListWorkPeriods;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanelDependencies;
    private javax.swing.JPanel jPanelEffort;
    private javax.swing.JPanel jPanelGeneral;
    private javax.swing.JRadioButton jRadioButtonComputeEffort;
    private javax.swing.JRadioButton jRadioButtonComputeProgress;
    private javax.swing.JRadioButton jRadioButtonComputeSpent;
    private javax.swing.JRadioButton jRadioButtonEffort;
    private javax.swing.JRadioButton jRadioButtonProgress;
    private javax.swing.JRadioButton jRadioButtonSpent;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JLabel lineLabel;
    private javax.swing.JTextField lineTextField;
    private javax.swing.JLabel prioLabel;
    private javax.swing.JComboBox priorityComboBox;
    // End of variables declaration//GEN-END:variables
}
