/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.a11ychecker.output;

import java.awt.Dialog;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.netbeans.modules.a11ychecker.FormBroker;
import org.netbeans.modules.a11ychecker.FormHandler;
import org.netbeans.modules.a11ychecker.LabelForPropertyPanel;
import org.netbeans.modules.a11ychecker.PropertyAction;
import org.netbeans.modules.a11ychecker.PropertyPanel;
import org.netbeans.modules.a11ychecker.utils.A11YFormUtils;
import org.netbeans.modules.form.ComponentChooserEditor;
import org.netbeans.modules.form.FormDesigner;
import org.netbeans.modules.form.FormEditor;
import org.netbeans.modules.form.FormModel;
import org.netbeans.modules.form.FormProperty;
import org.netbeans.modules.form.FormSettings;
import org.netbeans.modules.form.RADComponent;
import org.netbeans.modules.form.RADProperty;
import org.netbeans.modules.form.RADVisualComponent;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node.Property;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel showing A11Y errors, used in A11Y output window
 * @author Max Sauer
 * @author Martin Novak
 */
public class ResultPanel extends javax.swing.JPanel implements TableModelListener {

    //auto resourcing constants
    /** auto resourcing property name */
    static final String PROP_AUTO_RESOURCING = "autoResourcing"; // NOI18N
    /** auto resourcing value, if off */
    static final int AUTO_OFF = 0;
    
    /** Dialog for property editor */
    private Dialog dialog;

    //
    private int rowIndex;
    
    //vectors for entries

    Vector errors = new Vector();
    Vector warnings = new Vector();
    Vector infos = new Vector();
    TableSorter sorter;

    /** Customized table model */
    public DefaultTableModel model = new DefaultTableModel(new String[]{"Type", "Rule", "Recommendation / Description", "Component"}, 0) {

        @Override
        public Class getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Icon.class : String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    };
            
    /** Creates new form ResultPanel */
    public ResultPanel() {
        sorter = new TableSorter(model);
        initComponents();
        setColumnWidths();
        messageTable.setRowHeight(19);
        messageTable.setToolTipText("Double click/Enter to fix");
        sorter.setTableHeader(messageTable.getTableHeader());
        sorter.getTableHeader().setReorderingAllowed(false);
        messageTable.getModel().addTableModelListener(this); //addingchengelistener
        messageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Ask to be notified of selection changes.
        ListSelectionModel rowSM = messageTable.getSelectionModel();

        rowSM.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                //no rows are selected
                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    //selectedRow is selected
                    String compName = (String) messageTable.getModel().getValueAt(selectedRow, 3); //fourth column is component name
                    FormDesigner formDesigner = new FormHandler(FormBroker.getDefault().findActiveEditor()).getFormDesigner();
                    try {
                        Method method = FormDesigner.class.getDeclaredMethod("setSelectedComponent", RADComponent.class);
                        if (method != null) {
                            method.setAccessible(true);
                            method.invoke(formDesigner, getComponetByName(compName, formDesigner));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        //register doubleclick -- brings up property editor
        messageTable.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    //determine roen number
                    int rowIndex = messageTable.rowAtPoint(new Point(e.getX(), e.getY()));
                    //perform action
                    performTableRowAction(rowIndex);
                }
            }
        });
        
        setUpAutoI18nCheckBox();
    }
    
    /**
     * Brings up property editor for an a11yeror/warning/info listed in output table
     * @param rowIndex a11yeror/warning/info rown number inside output table
     */
    private void performTableRowAction(int rowIndex) {
        String compName = (String) messageTable.getModel().getValueAt(rowIndex, 3);
        FormDesigner formDesigner = new FormHandler(FormBroker.getDefault().findActiveEditor()).getFormDesigner();
        RADVisualComponent comp = (RADVisualComponent) getComponetByName(compName, formDesigner);

        //mnemonic, description, name
        //AccessibleContext.accessibleDescription
        //AccessibleContext.accessibleName
        //mnemonic
        //labelFor
        final String tableValue = (String) messageTable.getModel().getValueAt(rowIndex, 1);
        Class bc = comp.getBeanClass();
        String propertyName = null;
        if (tableValue.equals("Mnemonic")) {
            if (bc.equals(JLabel.class)) {
                propertyName = "displayedMnemonic";
            } else {
                propertyName = "mnemonic";
            }
        }
        if (tableValue.equals("Description")) {
            propertyName = "AccessibleContext.accessibleDescription";
        }
        if (tableValue.equals("Name")) {
            propertyName = "AccessibleContext.accessibleName";
        }
        if (tableValue.equals("Label for")) {
            propertyName = "labelFor";
        }
        DialogDescriptor descriptor = null;
        final JPanel panel;
        Property prop;

        if (propertyName.equals("labelFor") && !bc.equals(JLabel.class)) {
            // components that no label is bound with
            FormHandler fh = new FormHandler(FormBroker.getDefault().findActiveEditor());
            LinkedList<RADComponent> ll = fh.getUnboundLabels();
            if (ll.isEmpty()) {
                //there is no unbound label inside current form, display warning
                NotifyDescriptor emptyLabelDescriptor = new NotifyDescriptor.Message(NbBundle.getBundle(ResultPanel.class).getString("MSG_EmptyLabelList")); // NOI18N
                DialogDisplayer.getDefault().notify(emptyLabelDescriptor);
            } else {
                panel = new LabelForPropertyPanel(ll);
                descriptor = createLabelForEditorDescriptor(formDesigner, (LabelForPropertyPanel) panel, "Choose label that should be bound with " + comp.getName(), comp);
                dialog = DialogDisplayer.getDefault().createDialog(descriptor);
                //                            TODO predvyplnit puvodni hodnotou pokud nejaka je
                dialog.setVisible(true);
                dialog = null; //do not remove this
            }
            //reinvoke check
            new FormHandler(FormBroker.getDefault().findActiveEditor()).check();
            return;
        }

        prop = comp.getPropertyByName(propertyName, comp.getPropertyByName(propertyName).getClass(), true); //funguje na mnemonic

        //try to invoke property action
        //obsolete -- had to remove this, several reflexion calls
        //would be necessary, problems with accessing i18n.form module

        //RADComponent rComp = formDesigner.getMetaComponent(compName);
        //showEditor(comp, propertyName);

        //construct different property editor for labelFor and others
        if (!propertyName.equals("labelFor")) {
            panel = new PropertyPanel();
            if (propertyName.toLowerCase().contains("mnemonic")) {
                Property mnemonic;
                if (bc.equals(JLabel.class)) {
                    mnemonic = comp.getPropertyByName("displayedMnemonic");
                } else {
                    mnemonic = comp.getPropertyByName("mnemonic");
                }
                if (mnemonic != null) {
                    Property text = comp.getPropertyByName("text");
                    String t = FormHandler.getPropertyString(text);
                    descriptor = createPropertyEditorDescriptor((PropertyPanel) panel, "Set " + tableValue + " property value for " + comp.getName() + " with text '" + t + "'", prop);
                    int code = FormHandler.getPropertyInteger(mnemonic);
                    if (code != 0) {
                        String s = "" + (char) code;
                        ((PropertyPanel) panel).setValueText(s);
                        ((PropertyPanel) panel).selectWholeText();
                    }
                }
            } else {
                ((PropertyPanel) panel).setValueText(FormHandler.getPropertyString(comp.getPropertyByName(propertyName)));
                ((PropertyPanel) panel).selectWholeText();
                descriptor = createPropertyEditorDescriptor((PropertyPanel) panel, "Set " + tableValue + " property value for " + comp.getName(), prop);
            }
        } else {
            panel = new LabelForPropertyPanel(formDesigner.getFormModel().getComponentList());
            descriptor = createLabelForEditorDescriptor(formDesigner, (LabelForPropertyPanel) panel, "Choose component " + comp.getName() + " should be bound with", prop);
        }

        dialog = DialogDisplayer.getDefault().createDialog(descriptor);
        //                            TODO predvyplnit puvodni hodnotou pokud nejaka je
//                            if (!propertyName.equals("labelFor")) {
//                                System.out.println(dialog.getComponents().length);
//                                prop = comp.getPropertyByName("text");
//                                String t = FormHandler.getPropertyString(prop);
//                                if (t != null) {
//                                    Component c = dialog.getComponent(1);
//                                    ((JTextField) c).setText(t);
//                                }
//                            }

        dialog.setSize(500, dialog.getHeight());
        dialog.setVisible(true);

        dialog = null; //do not remove this
        //reinvoke check
        new FormHandler(FormBroker.getDefault().findActiveEditor()).check();
    }

    private void setUpAutoI18nCheckBox() {
        FormHandler fh = new FormHandler(FormBroker.getDefault().findActiveEditor());
        FormDesigner designer = fh.getFormDesigner();
        if(designer != null) {
            FormModel formModel = fh.getFormDesigner().getFormModel();
            Integer autoMode = A11YFormUtils.getResourceAutoMode(formModel);
            autoI18nCheckBox.setSelected((autoMode == null || autoMode == 0) ? false : true);
        }
    }
    
    /**
     * Provides access to auto i18n checkbox loacted on output panel
     * @param s selection state
     */
    public void setAutoI18nCBSelected(boolean s) {
        autoI18nCheckBox.setSelected(s);
    }

    /**
     * XXX remove
     * obsolete
     * Shows property editor for a component
     * @param comp the component which property is edited
     * @param beanPropertyName which property
     */
    private void showEditor(RADComponent comp, String beanPropertyName) {
        try {
            RADProperty props[] = comp.getAllBeanProperties(); 
            RADProperty rProp = comp.getBeanProperty("icon");
            //java.util.List actionProps = comp.getActionProperties();
//            RADComponentNode rNode = new RADComponentNode(comp);
//            FormProperty fProp = rNode.getProperty(beanPropertyName);
//            Action[] actions = rNode.getActions(false);
            
//            java.util.List actionProps = comp.getActionProperties();
//                Iterator iter = actionProps.iterator();
//                while (iter.hasNext()) {
//                    final RADProperty prop = (RADProperty)iter.next();
//                    Action action = PropertyAction.createIfEditable(prop);
//                    if (action != null) {
//                        action.actionPerformed(null);
//                    }
//                }
            
//            ((Action)Array.get(actions, 17)).actionPerformed(null);
//            
//            for (int i = 0; i < actions.length; i++) {
//                Action action = actions[i];
//                if(action instanceof org.netbeans.modules.form.actions.PropertyAction) {
//                    System.out.println("### PrppertyAction " + action);
//                    ((PropertyAction) action).actionPerformed(null);
//                }
//            }

            
            FormProperty prop = comp.getBeanProperty(beanPropertyName);
            //new PropertyAction(prop).actionPerformed(null);

            Action action = PropertyAction.createIfEditable(prop);
            action.actionPerformed(null);
                
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /**
     * Sets up dialog for editing LabelFor
     */
    private DialogDescriptor createLabelForEditorDescriptor(final FormDesigner formDesigner, final LabelForPropertyPanel panel, final String header, final RADVisualComponent rvc) {
        DialogDescriptor descriptor;
        descriptor = new DialogDescriptor(panel, header, true, buttons(), DialogDescriptor.CANCEL_OPTION, DialogDescriptor.DEFAULT_ALIGN, HelpCtx.DEFAULT_HELP, new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        try {
                            String action = e.getActionCommand();
                            //                    todo
                            if (OK_COMMAND.equals(action)) {
                                RADVisualComponent comp = (RADVisualComponent) getComponetByName(panel.getSelectedComponentName(), formDesigner);
                                Property prop = comp.getPropertyByName("labelFor", comp.getPropertyByName("labelFor").getClass(), true);
                                ComponentChooserEditor editor = (ComponentChooserEditor) ((FormProperty) prop).getCurrentEditor();
                                editor.setValue(rvc);
                                prop.setValue(editor.getValue());
                            }
                            dialog.dispose();
                        } catch (Exception ex) {
                            NotifyDescriptor descriptor = new NotifyDescriptor.Message(NbBundle.getBundle(ResultPanel.class).getString("MSG_InvalidValue")); // NOI18N
                            DialogDisplayer.getDefault().notify(descriptor);
                        }
                    }
                });
        return descriptor;
    }

    /**
     * Sets up dialog for editing LabelFor
     */
    private DialogDescriptor createLabelForEditorDescriptor(final FormDesigner formDesigner, final LabelForPropertyPanel panel, final String header, final Property prop) {
        DialogDescriptor descriptor;
        descriptor = new DialogDescriptor(panel, header, true, buttons(), DialogDescriptor.CANCEL_OPTION, DialogDescriptor.DEFAULT_ALIGN, HelpCtx.DEFAULT_HELP, new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        try {
                            String action = e.getActionCommand();
                            //                    todo
                            if (OK_COMMAND.equals(action)) {
                                ComponentChooserEditor editor = (ComponentChooserEditor) ((FormProperty) prop).getCurrentEditor();
                                editor.setValue(panel.getSelectedComponent());
                                prop.setValue(editor.getValue());
                            }
                            dialog.dispose();
                        } catch (Exception ex) {
                            NotifyDescriptor descriptor = new NotifyDescriptor.Message(NbBundle.getBundle(ResultPanel.class).getString("MSG_InvalidValue")); // NOI18N
                            DialogDisplayer.getDefault().notify(descriptor);
                        }
                    }
                });
        return descriptor;
    }

    /**
     * Sets up dialog for editing all properties beside LabelFor
     *
     */
    private DialogDescriptor createPropertyEditorDescriptor(final PropertyPanel panel, final String header, final Property prop) {
        DialogDescriptor descriptor;
        descriptor = new DialogDescriptor(panel, header, true, buttons(), DialogDescriptor.CANCEL_OPTION, DialogDescriptor.DEFAULT_ALIGN, HelpCtx.DEFAULT_HELP, new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        try {
                            String action = e.getActionCommand();
                            if (OK_COMMAND.equals(action)) {
                                if (!panel.getValueText().equals("")) {
                                    String value = panel.getValueText();
                                    //                                    FIXME kdyz se bude komponenta jmenovat *mnemonic*, bude problem - asi vyreseno 
                                    if (!header.toLowerCase().contains("mnemonic property")) //mnemonic is an integer property - tohle sem nepochopil :)
                                    {
                                        prop.setValue(value);
                                    } else {
                                        if (value.length() > 1) {
                                            throw new IllegalArgumentException("Mnemonic > 1 char");
                                        } //NOI18N
                                        char mnemonic = value.charAt(0);
                                        prop.setValue((int) mnemonic);
                                    }
                                }
                            }
                            dialog.dispose();
                        } catch (Exception ex) {
                            NotifyDescriptor descriptor = new NotifyDescriptor.Message(NbBundle.getBundle(ResultPanel.class).getString("MSG_InvalidValue")); // NOI18N
                            DialogDisplayer.getDefault().notify(descriptor);
                        }
                    }
                });
        return descriptor;
    }

    /**
     * Returns component from currently opened form, specified by name
     */
    public RADComponent getComponetByName(String name, FormDesigner formDesigner) {
        FormModel model = formDesigner.getFormModel();
        //        XXX divne, v listu nejsou jmenu a jmenuitem, ale jsou jmenucheckbox/radio-item
        List<RADComponent> list = model.getComponentList();
        Iterator<RADComponent> compIterator = list.iterator();
        while (compIterator.hasNext()) {
            RADComponent curr = compIterator.next();
            if (curr.getName().equals(name)) {
                return curr;
            }
        }
        return null; //component with name 'name' has not been found
    }

    /**
     * What to do when the table model changes
     */
    public void tableChanged(TableModelEvent e) {
        setColumnWidths();
        errorsCountLabel.setText("" + errors.size());
        warnigsCountLabel.setText("" + warnings.size());
        infosCountLabel.setText("" + infos.size());
    }

    /**
     * Erases all entries from jTable
     */
    public synchronized void eraseAllEntries() {
        errors.clear();
        warnings.clear();
        infos.clear();
        setSelectedData();
    }

    /**
     * Adjust column widths
     */
    private void setColumnWidths() {
        TableColumn column = null;
        for (int i = 0; i < 4; i++) {
            column = messageTable.getColumnModel().getColumn(i);
            switch (i) {
                case 0:
                    column.setPreferredWidth(50);
                    column.setResizable(false);
                    break;
                case 1:
                    column.setPreferredWidth(100);
                    break;
                case 2:
                    column.setPreferredWidth(300);
                    break;
                case 3:
                    column.setPreferredWidth(150);
                    break;
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        messageTable = new javax.swing.JTable();
        errorCheckBox = new javax.swing.JCheckBox();
        warningCheckBox = new javax.swing.JCheckBox();
        infoCheckBox = new javax.swing.JCheckBox();
        errorsCountLabel = new javax.swing.JLabel();
        warnigsCountLabel = new javax.swing.JLabel();
        infosCountLabel = new javax.swing.JLabel();
        checkButton = new javax.swing.JButton();
        autoI18nCheckBox = new javax.swing.JCheckBox();

        messageTable.setModel(sorter);
        messageTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                messageTableKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(messageTable);
        messageTable.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.messageTable.AccessibleContext.accessibleName")); // NOI18N
        messageTable.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.messageTable.AccessibleContext.accessibleDescription")); // NOI18N

        errorCheckBox.setMnemonic('E');
        errorCheckBox.setSelected(true);
        errorCheckBox.setText("Errors:");
        errorCheckBox.setToolTipText("Show or hide errors");
        errorCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        errorCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        errorCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                errorCheckBoxActionPerformed(evt);
            }
        });

        warningCheckBox.setMnemonic('W');
        warningCheckBox.setSelected(true);
        warningCheckBox.setText("Warnings:");
        warningCheckBox.setToolTipText("Show or hide warnings");
        warningCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        warningCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        warningCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warningCheckBoxActionPerformed(evt);
            }
        });

        infoCheckBox.setMnemonic('I');
        infoCheckBox.setSelected(true);
        infoCheckBox.setText("Information:");
        infoCheckBox.setToolTipText("Show or hide infos");
        infoCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        infoCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        infoCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoCheckBoxActionPerformed(evt);
            }
        });

        errorsCountLabel.setDisplayedMnemonic('E');
        errorsCountLabel.setLabelFor(errorCheckBox);
        errorsCountLabel.setText("0");

        warnigsCountLabel.setDisplayedMnemonic('W');
        warnigsCountLabel.setLabelFor(warningCheckBox);
        warnigsCountLabel.setText("0");

        infosCountLabel.setDisplayedMnemonic('i');
        infosCountLabel.setLabelFor(infoCheckBox);
        infosCountLabel.setText("0");

        checkButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/netbeans/modules/a11ychecker/output/refreshIcon.png"))); // NOI18N
        checkButton.setMnemonic('r');
        checkButton.setToolTipText("Refresh table to reflect current state of designed form.");
        checkButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkButtonActionPerformed(evt);
            }
        });

        autoI18nCheckBox.setMnemonic('A');
        autoI18nCheckBox.setText("Automatic i18n");
        autoI18nCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoI18nCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 922, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(errorCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(errorsCountLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(warningCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(warnigsCountLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(infoCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(infosCountLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 462, Short.MAX_VALUE)
                        .add(autoI18nCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(checkButton)))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {errorsCountLabel, infosCountLabel, warnigsCountLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(errorCheckBox)
                        .add(warningCheckBox)
                        .add(errorsCountLabel)
                        .add(warnigsCountLabel)
                        .add(infosCountLabel)
                        .add(infoCheckBox)
                        .add(checkButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(autoI18nCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.jScrollPane1.AccessibleContext.accessibleName")); // NOI18N
        jScrollPane1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.jScrollPane1.AccessibleContext.accessibleDescription")); // NOI18N
        errorsCountLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.errorsCountLabel.AccessibleContext.accessibleDescription")); // NOI18N
        warnigsCountLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.warnigsCountLabel.AccessibleContext.accessibleDescription")); // NOI18N
        infosCountLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.infosCountLabel.AccessibleContext.accessibleDescription")); // NOI18N
        checkButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.checkButton.AccessibleContext.accessibleName")); // NOI18N
        autoI18nCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.autoI18nCheckBox.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ResultPanel.class, "ResultPanel.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void checkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkButtonActionPerformed
    new FormHandler(FormBroker.getDefault().findActiveEditor()).check();
}//GEN-LAST:event_checkButtonActionPerformed

    private void infoCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoCheckBoxActionPerformed
        setSelectedData();
    }//GEN-LAST:event_infoCheckBoxActionPerformed

    private void warningCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warningCheckBoxActionPerformed
        setSelectedData();
    }//GEN-LAST:event_warningCheckBoxActionPerformed

    /** Adds new Error to the list */
    public void addNewError(Vector v) {
        errors.add(v); //adds to the internal list
        //setSelectedData(); //repaint the table, with respect to checkboxes
    }

    /** Adds new Warning to the list */
    public void addNewWarning(Vector v) {
        warnings.add(v);
        //setSelectedData();
    }

    /** Adds new Info to the list */
    public void addNewInfo(Vector v) {
        infos.add(v);
        //setSelectedData();
    }

    /**
     * Set visible data in table
     * with respect to currently checked CB
     */
    public synchronized void setSelectedData() {
        Vector result = new Vector();
        if (errorCheckBox.isSelected()) {
            for (int i = 0; i < errors.size(); i++) {
                result.add(errors.get(i));
            }
        }
        if (warningCheckBox.isSelected()) {
            for (int i = 0; i < warnings.size(); i++) {
                result.add(warnings.get(i));
            }
        }
        if (infoCheckBox.isSelected()) {
            for (int i = 0; i < infos.size(); i++) {
                result.add(infos.get(i));
            }
        }
        //keep sorting
        int column = 0;
        int direction = 0;
        boolean sorting = sorter.isSorting();
        if (sorting) {
            for (int i = 0; i < model.getColumnCount(); i++) {
                if (sorter.getSortingStatus(i) != 0) {
                    column = i;
                    direction = sorter.getSortingStatus(i);
                }
            //		System.out.println("### Column: " + i + " Status: " + sorter.getSortingStatus(i));
            }
        }

        model.setDataVector(result, getColumnNames());
        model.fireTableDataChanged(); //necessary to propagate column width
        if (sorting) {
            sorter.setSortingStatus(column, direction);
        }
    }
    
    public synchronized void setSelectedRow() {
        if (messageTable.getRowCount() > rowIndex) {
            messageTable.setRowSelectionInterval(rowIndex, rowIndex);
        }      
    }
 
    private Vector getColumnNames() {
        Vector v = new Vector();
        for (int i = 0; i < model.getColumnCount(); i++) {
            v.add(model.getColumnName(i));
        }
        return v;
    }

    private void errorCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errorCheckBoxActionPerformed
        setSelectedData();
    }//GEN-LAST:event_errorCheckBoxActionPerformed

    /**
     * Triggers automatic i18n
     * Every text-like property will be stored inside apropriate Bundles
     * @param evt the ActionEvent
     */
    private void autoI18nCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoI18nCheckBoxActionPerformed
        FormHandler fh = new FormHandler(FormBroker.getDefault().findActiveEditor());
        //FormEditor fe = fh.getFormDesigner().getFormEditor();
        
        FormModel formModel = fh.getFormDesigner().getFormModel();
        FormSettings fs = formModel.getSettings();

        //setter for auto mode
        try {
            Method setResourceAutoModeMethod = FormSettings.class.getDeclaredMethod("setResourceAutoMode", int.class);
            assert setResourceAutoModeMethod != null;
            if (setResourceAutoModeMethod != null) {
                setResourceAutoModeMethod.setAccessible(true);
            }
        
            if (autoI18nCheckBox.isSelected()) {
                setResourceAutoModeMethod.invoke(fs, 1);            
            } else {
                setResourceAutoModeMethod.invoke(fs, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_autoI18nCheckBoxActionPerformed

    private void messageTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_messageTableKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                final int selectedRow = messageTable.getSelectedRow();
                rowIndex = selectedRow;
                performTableRowAction(selectedRow);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    setSelectedRow();
                }
                });
                //model.fireTableStructureChanged();
                //model.fireTableDataChanged();
                //
                //tohle funguje, ale nevim co s tim -- lita z toho CCE -- chytit? XXX HACK
                //model.fireTableDataChanged();
                //sorter.fireTableDataChanged();

            //model.fireTableStructureChanged();
            //model.fireTableDataChanged();
            //
            //tohle funguje, ale nevim co s tim -- lita z toho CCE -- chytit? XXX HACK
            //model.fireTableDataChanged();
            //sorter.fireTableDataChanged();
        }
}//GEN-LAST:event_messageTableKeyPressed

    /**
     * Inits buttons for property dialog
     */
    private Object[] buttons() {
        JButton okButton = new JButton();
        okButton.setActionCommand(OK_COMMAND);
        okButton.setText("OK");
        JButton cancelButton = new JButton();
        cancelButton.setActionCommand(CANCEL_COMMAND);
        cancelButton.setText("Cancel");
        return new Object[]{okButton, cancelButton};
    }
    private static final String OK_COMMAND = "OK"; // NOI18N

    private static final String CANCEL_COMMAND = "Cancel"; // NOI18N
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autoI18nCheckBox;
    private javax.swing.JButton checkButton;
    private javax.swing.JCheckBox errorCheckBox;
    private javax.swing.JLabel errorsCountLabel;
    private javax.swing.JCheckBox infoCheckBox;
    private javax.swing.JLabel infosCountLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable messageTable;
    private javax.swing.JLabel warnigsCountLabel;
    private javax.swing.JCheckBox warningCheckBox;
    // End of variables declaration//GEN-END:variables
}
