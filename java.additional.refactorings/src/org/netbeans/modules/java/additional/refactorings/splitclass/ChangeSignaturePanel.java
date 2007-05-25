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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.splitclass;

import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.netbeans.modules.refactoring.spi.ui.CustomRefactoringPanel;
import org.openide.awt.Mnemonics;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author  Tim Boudreau
 */
class ChangeSignaturePanel extends javax.swing.JPanel implements CustomRefactoringPanel, ListSelectionListener, DocumentListener {
    private final ChangeSignatureUI ui;
    public ChangeSignaturePanel(ChangeSignatureUI ui) {
        this.ui = ui;
        initComponents();
        jTable1.setEnabled(false);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.getSelectionModel().addListSelectionListener(this);
        Component[] c = getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof JButton) {
                Mnemonics.setLocalizedText((JButton) c[i],
                        ((JButton) c[i]).getText());
            }
        }
        jTable1.setAutoCreateRowSorter(false);
        jTable1.setFillsViewportHeight(true);
        jTable1.setSurrendersFocusOnKeystroke(true);
        Font f = jTable1.getFont();
        Font nue = new Font ("Monospaced", f.getStyle(), f.getSize()); //NOI18N
        jTable1.setFont (nue);
        methodNameField.getDocument().addDocumentListener(this);
        returnTypeField.getDocument().addDocumentListener(this);
    }
    
    String origMethodName;
    void setMethodName(final String s) {
        origMethodName = s;
        Runnable r = new Runnable() {
            public void run() {
                methodNameField.setText(s);
            }
        };
        Mutex.EVENT.readAccess(r);
    }
    
    String origMethodType;
    void setMethodType(final String s) {
        origMethodType = s;
        Runnable r = new Runnable() {
            public void run() {
                returnTypeField.setText(s);
            }
        };
        Mutex.EVENT.readAccess(r);
    }
    
    boolean anyChanges() {
        boolean result = false;
        if (jTable1.getModel() instanceof TM) {
            TM tm = (TM) jTable1.getModel();
            List <Parameter> params = tm.getParameters();
            result = originals.size() != params.size();
            if (!result) {
                int max = params.size();
                for (int i=0; i < max; i++) {
                    result |= originals.get(i) != params.get(i);
                    if (result) {
                        break;
                    }
                }
            }
            if (!result) {
                for (Parameter p : params) {
                    result |= p.isModified();
                    if (result) break;
                }
                if (!result && origMethodName != null) {
                    result |= !origMethodName.equals(
                            methodNameField.getText().trim());
                }
                if (!result && origMethodType != null) {
                    result |= !origMethodType.equals(
                            returnTypeField.getText().trim());
                }
            }
        }
        return result;
    }

    public void addNotify() {
        super.addNotify();
        ui.init();
    }

    String getProblemText() {
        return problemLabel.getText().trim().length() == 0 ? null :
            problemLabel.getText();
    }

    void setProblemText (String s) {
        boolean hadText = getProblemText() != null;
        String txt = s == null ? "   " : s;
        problemLabel.setText(txt);
        boolean hasText = getProblemText() != null;
        if (hadText != hasText) {
            ui.change();
        }
    }

    void setProgress(final int val) {
        Runnable r = new Runnable() {
            public void run() {
                progress.setValue(val);
            }
        };
        Mutex.EVENT.readAccess(r);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        problemLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        progress = new javax.swing.JProgressBar();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        methodNameLbl = new javax.swing.JLabel();
        methodNameField = new javax.swing.JTextField();
        returnTypeLbl = new javax.swing.JLabel();
        returnTypeField = new javax.swing.JTextField();

        problemLabel.setForeground(javax.swing.UIManager.getDefaults().getColor("nb.errorForeground"));
        problemLabel.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "problemLabel.text")); // NOI18N

        jScrollPane1.setEnabled(false);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        moveUpButton.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jButton1.text")); // NOI18N
        moveUpButton.setEnabled(false);
        moveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpButtonActionPerformed(evt);
            }
        });

        moveDownButton.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jButton2.text")); // NOI18N
        moveDownButton.setEnabled(false);
        moveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownButtonActionPerformed(evt);
            }
        });

        addButton.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jButton3.text")); // NOI18N
        addButton.setEnabled(false);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jButton4.text")); // NOI18N
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jLabel1.text")); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane2.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jTextArea1.setBackground(javax.swing.UIManager.getDefaults().getColor("control"));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(problemLabel.getFont());
        jTextArea1.setRows(5);
        jTextArea1.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jTextArea1.text")); // NOI18N
        jScrollPane2.setViewportView(jTextArea1);

        methodNameLbl.setLabelFor(methodNameField);
        methodNameLbl.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jLabel2.text")); // NOI18N

        methodNameField.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jTextField1.text")); // NOI18N

        returnTypeLbl.setLabelFor(returnTypeField);
        returnTypeLbl.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jLabel3.text")); // NOI18N

        returnTypeField.setText(org.openide.util.NbBundle.getMessage(ChangeSignaturePanel.class, "jTextField2.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(returnTypeLbl)
                            .addComponent(methodNameLbl))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(problemLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(returnTypeField, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(methodNameField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, Short.MAX_VALUE))))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(removeButton)
                                .addComponent(addButton))
                            .addComponent(moveUpButton)
                            .addComponent(moveDownButton))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addButton, moveDownButton, moveUpButton, removeButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(17, 17, 17)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(methodNameLbl)
                    .addComponent(methodNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(returnTypeLbl)
                    .addComponent(returnTypeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(moveUpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(moveDownButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(problemLabel)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void moveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownButtonActionPerformed
    if (jTable1.getModel() instanceof TM) {
        TM tm = (TM) jTable1.getModel();
        int ix = Math.max (0, jTable1.getSelectedRow());
        tm.moveDown (ix);
    }
}//GEN-LAST:event_moveDownButtonActionPerformed

private void moveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpButtonActionPerformed
        if (jTable1.getModel() instanceof TM) {
            TM tm = (TM) jTable1.getModel();
            int ix = Math.max (0, jTable1.getSelectedRow());
            tm.moveUp(ix);
        }
}//GEN-LAST:event_moveUpButtonActionPerformed

private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
    if (jTable1.getModel() instanceof TM) {
        TM tm = (TM) jTable1.getModel();
        int ix = Math.max (0, jTable1.getSelectedRow());
        Parameter nue = new Parameter ("param", "");
        tm.add (nue, ix);
    }
}//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        if (jTable1.getModel() instanceof TM) {
            TM tm = (TM) jTable1.getModel();
            int ix = Math.max (0, jTable1.getSelectedRow());
            tm.remove (ix);
        }
}//GEN-LAST:event_removeButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField methodNameField;
    private javax.swing.JLabel methodNameLbl;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JLabel problemLabel;
    private javax.swing.JProgressBar progress;
    private javax.swing.JButton removeButton;
    private javax.swing.JTextField returnTypeField;
    private javax.swing.JLabel returnTypeLbl;
    // End of variables declaration//GEN-END:variables

    public void initialize() {
        System.err.println("Initialize");
        //never called. curious.
//        ui.init();
    }

    public Component getComponent() {
        return this;
    }

    public List <Parameter> getNewParameters() {
        if (jTable1.getModel() instanceof TM) {
            TM tm = (TM) jTable1.getModel();
            return tm.getParameters();
        } else {
            return Collections.<Parameter>emptyList();
        }
    }

    private void change() {
        boolean problem = false;
        String mname = methodNameField.getText().trim();
        if (!Utilities.isJavaIdentifier(mname)) {
            setProblemText(NbBundle.getMessage(ChangeSignaturePanel.class,
                    "MSG_BAD_NAME", mname)); //NOI18N
            problem = true;
        }
        String type = returnTypeField.getText().trim();
        if (!problem && !Utilities.isJavaIdentifier(type) && !"void".equals(type) && !isPrimitiveTypeName(type)) {
            boolean qualName = !isQualifiedTypeName(type);
            if (!qualName || (qualName && !checkQualifiedTypeName(type))) {
                setProblemText (NbBundle.getMessage(ChangeSignaturePanel.class,
                        "MSG_BAD_TYPE", type)); //NOI18N
                problem = true;
            }
        }
        
        if (!problem && !(jTable1.getModel() instanceof TM)) {
            setProblemText (NbBundle.getMessage(ChangeSignaturePanel.class,
                    "MSG_INITIALIZING")); //NOI18N
        } else if (jTable1.getModel() instanceof TM) {
            TM tm = (TM) jTable1.getModel();
            Set <String> names = new HashSet <String>();
            List <Parameter> params = tm.getParameters();
            int ix = 0;
            for (Parameter p : params) {
                String nm = p.getName();
                if (names.contains(nm)) {
                    setProblemText (NbBundle.getMessage(ChangeSignaturePanel.class,
                            "MSG_DUPLICATE_NAMES", nm)); //NOI18N
                    problem = true;
                    break;
                }
                if (nm != null && !Utilities.isJavaIdentifier(nm)) {
                    setProblemText (NbBundle.getMessage(ChangeSignaturePanel.class,
                            "MSG_BAD_NAME", nm)); //NOI18N
                    problem = true;
                    break;
                }
                if (nm == null) {
                    setProblemText (NbBundle.getMessage(ChangeSignaturePanel.class,
                            "MSG_NO_NAME", ix)); //NOI18N
                    problem = true;
                    break;
                }
                if (p.isNew() && p.getDefaultValue() == null) {
                    setProblemText (NbBundle.getMessage(ChangeSignaturePanel.class,
                            "MSG_NO_DEFAULT_VALUE", nm)); //NOI18N
                    problem = true;
                    break;
                }
                String typeName = p.getTypeName();
                if (typeName == null && p.isNew()) {
                    setProblemText (NbBundle.getMessage(ChangeSignaturePanel.class,
                            "MSG_NO_TYPE", nm)); //NOI18N
                    problem = true;
                    break;
                }
                if (typeName != null && !Utilities.isJavaIdentifier(typeName) && !isPrimitiveTypeName(typeName) && !isQualifiedTypeName(typeName)) {
                    setProblemText (NbBundle.getMessage(ChangeSignaturePanel.class,
                            "MSG_BAD_TYPE", typeName)); //NOI18N
                    problem = true;
                    break;
                }
                String defValue = p.isNew() ? p.getDefaultValue() : null;
                if (defValue != null) {
                    if (!Utilities.isJavaIdentifier(defValue) && !"null".equals(defValue)) {
                        if (isPrimitiveTypeName(typeName) != isPrimitiveTypeEntry(typeName, defValue)) {
                            setProblemText (NbBundle.getMessage(ChangeSignaturePanel.class,
                                "MSG_BAD_DEFAULT_VALUE", defValue, nm)); //NOI18N
                            problem = true;
                            break;
                        }
                    }
                    if ("null".equals(defValue) && p.isNew() && isPrimitiveTypeEntry(typeName, defValue)) {
                        tm.setValueAt("-1", ix, 2); //NOI18N
                        //avoid looping
                        return;
                    }
                }
                names.add (nm);
                ix ++;
            }
        }
        if (!problem && !anyChanges()) {
            setProblemText (NbBundle.getMessage(ChangeSignaturePanel.class,
                    "MSG_NOTHING_TO_DO")); //NOI18N
            problem = true;
        }
        if (!problem) {
            setProblemText(null);
        }
        ui.change();
    }

    static boolean isPrimitiveTypeEntry (String typeName, String defValue) {
        if ("char".equals(typeName)) { //NOI18N
            return defValue.startsWith ("'") && defValue.endsWith ("'") &&
                    defValue.length() == 3;
        } else {
            String upc = defValue.toUpperCase();
            if (upc.startsWith("0x") || upc.endsWith("L") || upc.endsWith("F") || upc.endsWith("D")) { //NOI18N           
                upc = upc.substring(0, upc.length());
            }
            try {
                Integer.parseInt(upc);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    static boolean isPrimitiveTypeName (String s) {
        String[] prims = new String[] {
            "int", "short", "byte", "char", "float", "double", "long", //NOI18N           
        };
        return Arrays.asList (prims).indexOf (s) >= 0;
    }

    private boolean isQualifiedTypeName (String s) {
        return s.indexOf (".") >= 0;
    }

    List <Parameter> originals;
    void setParameters (final List <Parameter> descs) {
        Runnable r = new Runnable() {
            public void run() {
                originals = new ArrayList <Parameter> (descs);
                TM tm = new TM (descs);
                jTable1.setModel (tm);
                jTable1.setEnabled(true);
                boolean hasDescs = descs.size() > 0;
                if (hasDescs) {
                    jTable1.getSelectionModel().setAnchorSelectionIndex(0);
                    jTable1.getSelectionModel().setLeadSelectionIndex(0);
                }
                moveUpButton.setEnabled(hasDescs);
                moveDownButton.setEnabled(hasDescs);
                addButton.setEnabled(true);
                removeButton.setEnabled (hasDescs);
                progress.setVisible (false);
            }
        };
        Mutex.EVENT.readAccess(r);
    }

    private class TM implements TableModel {
        final List <Parameter> descs;
        TM (List <Parameter> descs) {
            this.descs = descs;
        }

        List <Parameter> getParameters() {
            return descs;
        }

        void add (Parameter param, int ix) {
            if (ix < descs.size()) ix++;
            descs.add(ix, param);
            TableModelEvent tme = new TableModelEvent (this, ix-1, descs.size());
            fire (tme);
            jTable1.getSelectionModel().setAnchorSelectionIndex(ix);
            jTable1.getSelectionModel().setLeadSelectionIndex(ix);
            jTable1.requestFocus();
            jTable1.editCellAt(ix, 1);
        }

        void remove (int ix) {
            descs.remove (ix);
            TableModelEvent tme = new TableModelEvent (this, ix, descs.size() + 1);
            fire (tme);
            if (descs.size() > 0) {
                ix = Math.max (0, ix);
                jTable1.getSelectionModel().setAnchorSelectionIndex(ix);
                jTable1.getSelectionModel().setLeadSelectionIndex(ix);
            }
        }

        void moveUp (int ix) {
            if (ix == 0) {
                return;
            }
            Parameter p = descs.remove (ix);
            descs.add (ix - 1, p);
            TableModelEvent evt = new TableModelEvent (this);
            fire (evt);
            jTable1.getSelectionModel().setAnchorSelectionIndex(ix - 1);
            jTable1.getSelectionModel().setLeadSelectionIndex(ix - 1);
        }

        void moveDown (int ix) {
            if (ix >= descs.size()) {
                return;
            }
            Parameter p = descs.remove (ix);
            descs.add (Math.min (descs.size(), ix + 1), p);
            TableModelEvent evt = new TableModelEvent (this);
            fire (evt);
            jTable1.getSelectionModel().setAnchorSelectionIndex(ix + 1);
            jTable1.getSelectionModel().setLeadSelectionIndex(ix + 1);
        }

        public int getRowCount() {
            return descs.size();
        }

        public int getColumnCount() {
            return 3;
        }

        public String getColumnName(int columnIndex) {
            String key = columnIndex == 0 ?
                    "LBL_NAMES" : columnIndex == 1 ? "LBL_TYPES"
                    : "LBL_DEFAULT_VALUE"; //NOI18N
            return NbBundle.getMessage(TM.class, key);
        }

        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? String.class :
                String.class;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 2 ? descs.get(rowIndex).isNew() : true;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex < 0 || rowIndex > descs.size()) {
                return null;
            }

            String result = columnIndex == 0 ? descs.get(rowIndex).getName() :
                columnIndex == 1 ?
                descs.get(rowIndex).getTypeName() : descs.get(rowIndex).getDefaultValue();
            if (result == null) {
                result = "";
            }
            return result;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            String s = aValue.toString();
            Parameter p = descs.get(rowIndex);
            switch (columnIndex) {
            case 0 :
                p.setName(s);
                break;
            case 1 :
                p.setTypeName(s);
                break;
            case 2:
                if (p.isNew()) {
                    p.setDefaultValue(s);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                break;
            default :
                throw new IllegalArgumentException("" + columnIndex);
            }
            fire(rowIndex, columnIndex);
            if (getProblemText() == null) {
                restoreOriginalsIfPossible(rowIndex);
            }
        }

        private void restoreOriginalsIfPossible(int ix) {
            //If the type name and type have become the same as one we originally
            //had, replce with that element
            Parameter p = descs.get(ix);
            Parameter orig = null;
            String typeName = p.getTypeName();
            String name = p.getName();
            if (name != null && typeName != null) {
                for (Parameter old : originals) {
                    if (name.equals(old.getName()) && typeName.equals(old.getTypeName())) {
                        orig = old;
                        break;
                    }
                }
            }
            if (orig != null) {
                descs.remove (ix);
                descs.add (ix, orig);
            }
        }

        private void fire (int row, int col) {
            TableModelEvent evt = new TableModelEvent(this, row, row, col);
            fire (evt);
        }

        private void fire (TableModelEvent evt) {
            TableModelListener[] l = listeners.toArray (new TableModelListener[0]);
            for (int i = 0; i < l.length; i++) {
                l[i].tableChanged(evt);
            }
            jTable1.invalidate();
            jTable1.revalidate();
            jTable1.repaint();
            change();
        }

        private List <TableModelListener> listeners =
                Collections.<TableModelListener>synchronizedList (new
                LinkedList<TableModelListener>());

        public void addTableModelListener(TableModelListener l) {
            listeners.add (l);
        }

        public void removeTableModelListener(TableModelListener l) {
            listeners.remove (l);
        }
    }
    
    public String getMethodName() {
        return methodNameField.getText().trim().length() == 0 ? null : 
            methodNameField.getText().trim();
    }
    
    public String getReturnType() {
        return returnTypeField.getText().trim().length() == 0 ? null : 
            returnTypeField.getText().trim();
    }

    public void valueChanged(ListSelectionEvent e) {
        int ix = jTable1.getSelectedRow();
        int max = jTable1.getRowCount();
        if (jTable1.getModel() instanceof TM) {
            moveUpButton.setEnabled (ix > 0);
            moveDownButton.setEnabled (ix >= 0 && ix < max - 1 && max > 1);
            removeButton.setEnabled (true);
            addButton.setEnabled (true);
        }
    }
    
    private boolean checkQualifiedTypeName (String nm) {
        String[] s = nm.split (".");        
        boolean result = true;
        for (int i = 0; i < s.length; i++) {
            result &= Utilities.isJavaIdentifier(s[i]);
            if (!result) break;
        }
        return result;
    }

    public void insertUpdate(DocumentEvent e) {
        change();
    }

    public void removeUpdate(DocumentEvent e) {
        change();
    }

    public void changedUpdate(DocumentEvent e) {
        change();
    }
}
