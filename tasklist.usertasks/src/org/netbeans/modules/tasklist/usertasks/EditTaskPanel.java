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

package org.netbeans.modules.tasklist.usertasks;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.Iterator;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.openide.ErrorManager;
import org.openide.TopManager;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerPanel;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;


class EditTaskPanel extends JPanel implements ActionListener {
    private ExplorerPanel explorerPanel;
    private BeanTreeView treeView;
    private Task parent = null;
    
    private static boolean appendDefault = Settings.getDefault().getAppend();
    
    /** Creates new form NewTodoItemPanel.
        @param parent A possible suggestion for a parent 
        @param item Item to edit. If null, create new.
     */
    EditTaskPanel(UserTaskList tlv, UserTask parent, UserTask item, boolean editing) {
        // Create a new item with the given suggested parent
        this.parent = parent;
        initComponents();
        
        // Initialize the Categories list
        Set categories = tlv.getCategories();
        if ((categories != null) && (categories.size() > 0)) {
            DefaultComboBoxModel model = new DefaultComboBoxModel();
            model.addElement(""); // Default to "nothing" -- make an option for default category?
            Iterator it = categories.iterator();
            while (it.hasNext()) {
                String category = (String)it.next();
                model.addElement(category);
            }
            categoryCombo.setModel(model);
        }
        
        // Let the user select a task to make this item a subtask of
        explorerPanel = new ExplorerPanel();
        treeView = new BeanTreeView();
        
        treeView.setRootVisible(false);
        
        explorerPanel.add(treeView, BorderLayout.CENTER);
        
        //JScrollPane jsp = new JScrollPane(explorerPanel);
        //subtaskPanel.add(jsp, java.awt.BorderLayout.CENTER);
        subtaskPanel.add(explorerPanel, BorderLayout.CENTER);
        
        // XXX todo -> set single selection
        
        
        treeView.setPopupAllowed(false);
        //treeView.setPreferredSize(new java.awt.Dimension(400,180));
        
        UserTask root = (UserTask)tlv.getRoot();
        if (root != null) {
            UserTaskNode rootNode = new UserTaskNode(root, root.getSubtasks());
            explorerPanel.getExplorerManager().setRootContext(rootNode);
            
            if (parent != null) {
                Node n = TaskNode.find(rootNode, parent);
		if (n != null) {
		    Node [] sel = new Node[] { n };
		    try {
			explorerPanel.getExplorerManager().
			    setSelectedNodes(sel);
		    } catch (PropertyVetoException e) {
			TopManager.getDefault().getErrorManager().
			    notify(ErrorManager.INFORMATIONAL, e);
		    }
		    /* Temporarily commented out -- I would often
		     * accidentally create a subtask when I didn't
		     * mean to, just because a todoitem was already
		     * selected in the todo view. So for now, force
		     * users to explicitly select this checkbox to
		     * make a subitem. Perhaps I can make a separate
		     * action for creating a subtask.
		     * subTaskCheckBox.setSelected(true);
		    */
		}
            }
        }
        
        // Trond: I removed this test since the user now may change the
        // "subtask of" field.
//        if ((parent == null) || (item != null)) {
//                remove(subTaskCheckBox);
//                remove(subtaskPanel);
//        }

        if (item != null) {
            if (item.getSummary() != null) {
                descriptionTextField.setText(item.getSummary());
            }
            priorityTextField.setText(Integer.toString(item.getPriorityNumber()));
            if (item.getFilename() != null) {
                fileTextField.setText(item.getFilename());
                fileCheckBox.setSelected(true);
            }
            if (item.getLineNumber() > 0) {
                lineTextField.setText(Integer.toString(item.getLineNumber()));
            }
            if (item.getCategory() != null) {
                categoryCombo.setSelectedItem(item.getCategory());
            }
            if (item.getDetails() != null) {
                detailsTextArea.setText(item.getDetails());
            }
            subTaskCheckBox.setSelected((item.getParent() != null) && 
                                        (item.getParent().getParent() != null));
        }
        
        addSourceButton.addActionListener(this);
        plusButton.addActionListener(this);
        minusButton.addActionListener(this);
        
        descriptionTextField.requestDefaultFocus();
        descriptionTextField.requestFocus();

        if (editing) {
            remove(addLabel);
            remove(beginningToggle);
            remove(endToggle);
            remove(addSourceButton);
        } else {
            boolean append = appendDefault;
            if (append) {
                endToggle.setSelected(true);
            } else {
                beginningToggle.setSelected(true);
            }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        prioGroup = new javax.swing.ButtonGroup();
        appendGroup = new javax.swing.ButtonGroup();
        descLabel = new javax.swing.JLabel();
        descriptionTextField = new javax.swing.JTextField();
        detailsLabel = new javax.swing.JLabel();
        detailsScrollPane = new javax.swing.JScrollPane();
        detailsTextArea = new javax.swing.JTextArea();
        subTaskCheckBox = new javax.swing.JCheckBox();
        subtaskPanel = new javax.swing.JPanel();
        prioLabel = new javax.swing.JLabel();
        priorityTextField = new javax.swing.JTextField();
        plusButton = new javax.swing.JButton();
        minusButton = new javax.swing.JButton();
        opt1Label = new javax.swing.JLabel();
        categoryLabel = new javax.swing.JLabel();
        categoryCombo = new javax.swing.JComboBox();
        opt2Label = new javax.swing.JLabel();
        fileCheckBox = new javax.swing.JCheckBox();
        fileTextField = new javax.swing.JTextField();
        lineLabel = new javax.swing.JLabel();
        lineTextField = new javax.swing.JTextField();
        addLabel = new javax.swing.JLabel();
        beginningToggle = new javax.swing.JRadioButton();
        endToggle = new javax.swing.JRadioButton();
        addSourceButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(12, 12, 12, 12)));
        setPreferredSize(new java.awt.Dimension(400, 300));
        descLabel.setLabelFor(descriptionTextField);
        descLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "Brief_Description")); // NOI18N);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 12);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    add(descLabel, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    gridBagConstraints.weightx = 1.0;
    add(descriptionTextField, gridBagConstraints);

    detailsLabel.setLabelFor(detailsScrollPane);
    detailsLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "DetailsLabel")); // NOI18N);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    add(detailsLabel, gridBagConstraints);

    detailsTextArea.setRows(5);
    detailsScrollPane.setViewportView(detailsTextArea);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    gridBagConstraints.weighty = 1.0;
    add(detailsScrollPane, gridBagConstraints);

    subTaskCheckBox.setText(NbBundle.getMessage(EditTaskPanel.class, "SubtaskOf")); // NOI18N);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    add(subTaskCheckBox, gridBagConstraints);

    subtaskPanel.setLayout(new java.awt.BorderLayout());

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    add(subtaskPanel, gridBagConstraints);

    prioLabel.setLabelFor(priorityTextField);
    prioLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "PriorityLabel")); // NOI18N);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    add(prioLabel, gridBagConstraints);

    priorityTextField.setColumns(20);
    priorityTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    priorityTextField.setText("3");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    add(priorityTextField, gridBagConstraints);

    plusButton.setText("+");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(12, 18, 0, 0);
    add(plusButton, gridBagConstraints);

    minusButton.setText("-");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
    add(minusButton, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    add(opt1Label, gridBagConstraints);

    categoryLabel.setLabelFor(categoryCombo);
    categoryLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "CategoryLabel")); // NOI18N);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    add(categoryLabel, gridBagConstraints);

    categoryCombo.setEditable(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    add(categoryCombo, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    add(opt2Label, gridBagConstraints);

    fileCheckBox.setText(NbBundle.getMessage(EditTaskPanel.class, "AssociatedFile")); // NOI18N);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    add(fileCheckBox, gridBagConstraints);

    fileTextField.setColumns(100);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 7;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    gridBagConstraints.weightx = 0.7;
    add(fileTextField, gridBagConstraints);

    lineLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "LineLabel")); // NOI18N);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    add(lineLabel, gridBagConstraints);

    lineTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    gridBagConstraints.weightx = 0.5;
    add(lineTextField, gridBagConstraints);

    addLabel.setText(NbBundle.getMessage(EditTaskPanel.class, "AddTo")); // NOI18N();
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    add(addLabel, gridBagConstraints);

    beginningToggle.setText(NbBundle.getMessage(EditTaskPanel.class, "BeginningList")); // NOI18N();
    appendGroup.add(beginningToggle);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    add(beginningToggle, gridBagConstraints);

    endToggle.setText(NbBundle.getMessage(EditTaskPanel.class, "EndList")); // NOI18N();
    appendGroup.add(endToggle);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    add(endToggle, gridBagConstraints);

    addSourceButton.setText(NbBundle.getMessage(EditTaskPanel.class, "AddToSource")); // NOI18N();
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 1.0;
    add(addSourceButton, gridBagConstraints);

    }//GEN-END:initComponents
    
    // TODO prioGroup is unused; get rid of it 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JPanel subtaskPanel;
    private javax.swing.JCheckBox fileCheckBox;
    private javax.swing.ButtonGroup appendGroup;
    private javax.swing.JLabel addLabel;
    private javax.swing.JButton minusButton;
    private javax.swing.JScrollPane detailsScrollPane;
    private javax.swing.JCheckBox subTaskCheckBox;
    private javax.swing.JTextArea detailsTextArea;
    private javax.swing.JTextField fileTextField;
    private javax.swing.ButtonGroup prioGroup;
    private javax.swing.JComboBox categoryCombo;
    private javax.swing.JLabel detailsLabel;
    private javax.swing.JLabel lineLabel;
    private javax.swing.JLabel opt1Label;
    private javax.swing.JLabel descLabel;
    private javax.swing.JButton plusButton;
    private javax.swing.JTextField priorityTextField;
    private javax.swing.JRadioButton beginningToggle;
    private javax.swing.JRadioButton endToggle;
    private javax.swing.JTextField lineTextField;
    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JLabel opt2Label;
    private javax.swing.JButton addSourceButton;
    private javax.swing.JLabel prioLabel;
    // End of variables declaration//GEN-END:variables
    
    String getSummary() {
        return descriptionTextField.getText().trim();
    }
    
    String getDetails() {
        return detailsTextArea.getText().trim();
    }
    
    String getCategory() {
        if (categoryCombo.getSelectedItem() == null) {
            return null;
        }
        return categoryCombo.getSelectedItem().toString().trim();
    }
    
    int getPrio() {
        try {
            int i = Integer.parseInt(priorityTextField.getText());
            return i;
        } catch (NumberFormatException e) {
        }
        return 3; // Default
    }
    
    boolean hasAssociatedFilePos() {
        return fileCheckBox.isSelected();
    }
    
    void setAssociatedFilePos(boolean set) {
        fileCheckBox.setSelected(set);
    }
    
    String getFilename() {
        return fileTextField.getText().trim();
    }
    
    void setFilename(String filename) {
        fileTextField.setText(filename);
        fileTextField.setCaretPosition(fileTextField.getText().length()-1);
    }
    
    int getLineNumber() {
        try {
            int i = Integer.parseInt(lineTextField.getText());
            return i;
        } catch (NumberFormatException e) {
        }
        return 0;
    }
    
    void setLineNumber(int lineno) {
        lineTextField.setText(Integer.toString(lineno));
    }
    
    // TODO - preserve this setting from run to run! (Unless you change
    // the default!)
    boolean getAppend() {
        appendDefault = endToggle.isSelected();
        return appendDefault;
    }

    Task getParentItem() {
        // User doesn't want to use it
        if (!subTaskCheckBox.isSelected()) {
            return null;
        }
        ExplorerManager mgr = explorerPanel.getExplorerManager();
        Node [] nodes = mgr.getSelectedNodes();
        if (nodes == null) {
            return parent;
        }
        
        for (int i = 0; i < nodes.length; i++) {
            Task item = TaskNode.getTask(nodes[i]);
            if (item != null) {
                return item;
            }
        }
        return parent;
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == addSourceButton) {
           HelpCtx help = new HelpCtx("NewTask"); // NOI18N
           TopManager.getDefault().showHelp(help);
        } else if (source == plusButton) {
            priorityTextField.setText(Integer.toString(getPrio()+1));
        } else if (source == minusButton) {
            int newprio = getPrio()-1;
            if (newprio < 0) {
                newprio = 0;
            }
            priorityTextField.setText(Integer.toString(newprio));
        }
    }
    
}
