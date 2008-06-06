/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

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

package org.netbeans.modules.encoder.ui.tester.impl;

import java.io.File;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.xml.namespace.QName;
import org.netbeans.modules.encoder.ui.basic.Utils;
import org.netbeans.modules.encoder.ui.tester.impl.EncoderTestPerformerImpl;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;

/**
 * Implementation of the Encoder tester form
 *
 * @author  Cannis Meng
 */
public class TesterPanel extends javax.swing.JPanel implements DocumentListener {

    private static final String[] CHARSET_NAMES_EXTRA = Utils.getCharsetNames(true);
    private static final ResourceBundle _bundle =
            ResourceBundle.getBundle("org/netbeans/modules/encoder/ui/tester/impl/Bundle");
    
    private static final String PREF_XSD_FILE = "xsd-file";  //NOI18N
    private static final String PREF_TOP_ELEM = "top-elem";  //NOI18N
    private static final String PREF_ACTION = "action";  //NOI18N
    private static final String PREF_INPUT = "in-file";  //NOI18N
    private static final String PREF_OUTPUT = "out-file";  //NOI18N
    private static final String PREF_OVERWRITE = "overwrite";  //NOI18N
    private static final String PREF_CHAR_BASED = "char_based"; //NOI18N
    private static final String PREF_DOC_CODING = "doc_coding"; //NOI18N
    private static final String ACTION_ENCODE = "encode";  //NOI18N
    private static final String ACTION_DECODE = "decode";  //NOI18N
    
    private String xsdFilePath;
    private Preferences mPrefs = Preferences.userNodeForPackage(this.getClass());
    
    /** Creates new form TesterPanel */
    public TesterPanel(String xsdFile) {
        xsdFilePath = xsdFile;
        initComponents();
        buttonGroup1.add(this.jRadioEncode);
        buttonGroup1.add(this.jRadioDecode);
        jTextField1.setText(xsdFile);
        jTextField1.setEditable(false);
        File f = new File(xsdFile);
        mFolderText.setText(f.getParent());
        this.mOutputResult.getDocument().addDocumentListener(this);
        this.mFolderText.getDocument().addDocumentListener(this);
        this.mCreatedFileText.setEditable(false);
        jRadioEncode.setSelected(true);
        if (xsdFilePath != null) {
            File file = new File(xsdFilePath);
            if (file.getParent() != null) {
                mFolderText.setText(file.getParent());
            }
        }
        setComboBoxList(jComboBoxResultCoding, CHARSET_NAMES_EXTRA);
        jComboBoxResultCoding.setSelectedIndex(-1);
        setComboBoxList(jComboBoxSourceCoding, CHARSET_NAMES_EXTRA);
        jComboBoxSourceCoding.setSelectedIndex(-1);
        applyPreferences();
        updateComponents();        
    }
    
    private void setComboBoxList(JComboBox comboBox, String[] list) {
        comboBox.removeAllItems();
        for (int i = 0; i < list.length; i++) {
            comboBox.addItem(list[i]);
        }
        comboBox.setSelectedIndex(-1);
    }
    
    private void applyPreferences() {
        if (sameAsPreviousLaunch()) {
            String value = mPrefs.get(PREF_ACTION, null);
            if (value == null) {
                return;
            }
            if (ACTION_DECODE.equals(value)) {
                jRadioDecode.setSelected(true);
            } else {
                jRadioEncode.setSelected(true);
            }
            value = mPrefs.get(PREF_INPUT, null);
            if (value == null) {
                return;
            }
            if (jRadioEncode.isSelected()) {
                mXMLSourceText.setText(value);
                value = mPrefs.get(PREF_CHAR_BASED, null);
                if (value != null) {
                    jCheckBoxToString.setSelected(Boolean.valueOf(value));
                }
                value = mPrefs.get(PREF_DOC_CODING, null);
                if (value != null) {
                    jComboBoxResultCoding.getEditor().setItem(value);
                }
            } else {
                mInputDataText.setText(value);
                value = mPrefs.get(PREF_CHAR_BASED, null);
                if (value != null) {
                    jCheckBoxFromString.setSelected(Boolean.valueOf(value));
                }
                value = mPrefs.get(PREF_DOC_CODING, null);
                if (value != null) {
                    jComboBoxSourceCoding.getEditor().setItem(value);
                }
            }
            value = mPrefs.get(PREF_OUTPUT, null);
            if (value == null) {
                return;
            }
            File outputFile = new File(value);
            if (outputFile.getName() != null) {
                int pos = outputFile.getName().lastIndexOf('.');
                if (pos >= 0) {
                    mOutputResult.setText(outputFile.getName().substring(0, pos));
                } else {
                    mOutputResult.setText(outputFile.getName());
                }
            }
            mFolderText.setText(outputFile.getParent());
            mOverwrite.setSelected(mPrefs.getBoolean(PREF_OVERWRITE, true));
        }
    }
    
    public void savePreferences() throws BackingStoreException {
        mPrefs.put(PREF_XSD_FILE, xsdFilePath);
        if (this.getActionType().equals(EncoderTestPerformerImpl.DECODE)) {
            mPrefs.put(PREF_ACTION, ACTION_DECODE);
            mPrefs.putBoolean(PREF_CHAR_BASED, isFromString());
            mPrefs.put(PREF_DOC_CODING, getPredecodeCoding());
        } else {
            mPrefs.put(PREF_ACTION, ACTION_ENCODE);
            mPrefs.putBoolean(PREF_CHAR_BASED, isToString());
            mPrefs.put(PREF_DOC_CODING, getPostencodeCoding());
        }
        if (getSelectedTopElementDecl() != null) {
            mPrefs.put(PREF_TOP_ELEM, getSelectedTopElementDecl().toString());
        }
        if (getProcessFile() != null) {
            mPrefs.put(PREF_INPUT, getProcessFile());
        }
        if (getOutputFile() != null) {
            mPrefs.put(PREF_OUTPUT, getOutputFile());
        }
        mPrefs.putBoolean(PREF_OVERWRITE, isOverwrite());
        mPrefs.flush();
    }

    private void updateComponents() {
        if (!jRadioEncode.isSelected()) {
            mXMLSourceText.setEditable(false);
            mXMLButton.setEnabled(false);
            jComboBoxResultCoding.setEnabled(false);
            jCheckBoxToString.setEnabled(false);
        } else {
            mXMLSourceText.setEditable(true);
            mXMLButton.setEnabled(true);
            jComboBoxResultCoding.setEnabled(true);
            jCheckBoxToString.setEnabled(true);
        }

        if (!jRadioDecode.isSelected()) {
            mInputDataText.setEditable(false);
            mDataButton.setEnabled(false);
            jComboBoxSourceCoding.setEnabled(false);
            jCheckBoxFromString.setEnabled(false);
        } else {
            mInputDataText.setEditable(true);
            mDataButton.setEnabled(true);
            jComboBoxSourceCoding.setEnabled(true);
            jCheckBoxFromString.setEnabled(true);
        }
        
        updateCreatedFolder();        
    }
    
    private boolean sameAsPreviousLaunch() {
        String prefXsdFile = mPrefs.get(PREF_XSD_FILE, null);
        if (prefXsdFile == null) {
            return false;
        }
        return new File(xsdFilePath).equals(new File(prefXsdFile));
    }

    public String getOutputFileName() {
        return mOutputResult.getText();
    }
    
    /**
     * Gets the generated output file in full path.
     *
     * @return output file path
     */
    public String getOutputFile() {
        return mCreatedFileText.getText();
    }

    /**
     * Gets the action type. ie. either encode or decode.
     *
     * @return action string
     */
    public String getActionType() {
        return jRadioEncode.isSelected() ? EncoderTestPerformerImpl.ENCODE : EncoderTestPerformerImpl.DECODE;
    }

    
    /**
     * Gets the process file. For encode, it will be an xml file; 
     * for decode, it will be any data file with the decoded string.
     *
     * @return process file full path
     */
    public String getProcessFile() {
        if (this.getActionType().equals(EncoderTestPerformerImpl.ENCODE)) {
            return mXMLSourceText.getText();
        } else {
            return mInputDataText.getText();
        }
    }

    /**
     * Overwrites the output file?
     */
    public boolean isOverwrite() {
        return mOverwrite.isSelected();
    }
    
    /**
     * Is encoding to string?
     */
    public boolean isToString() {
        return jCheckBoxToString.isSelected();
    }
    
    /**
     * Is decoding from string?
     */
    public boolean isFromString() {
        return jCheckBoxFromString.isSelected();
    }
    
    /**
     * Gets the pre-decoding coding
     */
    public String getPredecodeCoding() {
        Object obj = jComboBoxSourceCoding.getEditor().getItem();
        return obj == null ? "" : obj.toString();
    }

    /**
     * Gets the post-encoding coding
     */
    public String getPostencodeCoding() {
        Object obj = jComboBoxResultCoding.getEditor().getItem();
        return obj == null ? "" : obj.toString();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jRadioEncode = new javax.swing.JRadioButton();
        jRadioDecode = new javax.swing.JRadioButton();
        mXMLSourceText = new javax.swing.JTextField();
        mInputDataText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        mOutputResult = new javax.swing.JTextField();
        mOverwrite = new javax.swing.JCheckBox();
        mXMLButton = new javax.swing.JButton();
        mDataButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        mFolderText = new javax.swing.JTextField();
        mFolderBtn = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        mCreatedFileText = new javax.swing.JTextField();
        selectElementComboBox = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jCheckBoxToString = new javax.swing.JCheckBox();
        jLabelResultCoding = new javax.swing.JLabel();
        jComboBoxResultCoding = new javax.swing.JComboBox();
        jCheckBoxFromString = new javax.swing.JCheckBox();
        jLabelSourceCoding = new javax.swing.JLabel();
        jComboBoxSourceCoding = new javax.swing.JComboBox();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/encoder/ui/tester/impl/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("test_panel.lbl.xsd_file")); // NOI18N

        jRadioEncode.setText(bundle.getString("test_panel.lbl.encode")); // NOI18N
        jRadioEncode.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioEncode.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioEncode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioEncodeActionPerformed(evt);
            }
        });

        jRadioDecode.setText(bundle.getString("test_panel.lbl.decode")); // NOI18N
        jRadioDecode.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioDecode.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioDecode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioDecodeActionPerformed(evt);
            }
        });

        jLabel2.setText(bundle.getString("test_panel.lbl.output_file_name")); // NOI18N

        mOverwrite.setText(bundle.getString("test_panel.lbl.overwrite_output")); // NOI18N
        mOverwrite.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mOverwrite.setMargin(new java.awt.Insets(0, 0, 0, 0));

        mXMLButton.setText(bundle.getString("test_panel.lbl.browse2")); // NOI18N
        mXMLButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mXMLButtonActionPerformed(evt);
            }
        });

        mDataButton.setText(bundle.getString("test_panel.lbl.browse3")); // NOI18N
        mDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mDataButtonActionPerformed(evt);
            }
        });

        jLabel3.setText(bundle.getString("test_panel.lbl.input_data_file")); // NOI18N

        jLabel4.setText(bundle.getString("test_panel.lbl.xml_source")); // NOI18N

        jLabel5.setText(bundle.getString("test_panel.lbl.output_folder")); // NOI18N

        mFolderBtn.setText(bundle.getString("test_panel.lbl.browse3")); // NOI18N
        mFolderBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mFolderBtnActionPerformed(evt);
            }
        });

        jLabel6.setText(bundle.getString("test_panel.lbl.created_file")); // NOI18N

        selectElementComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel7.setText(bundle.getString("test_panel.lbl.select_an_element")); // NOI18N

        jCheckBoxToString.setText("To String");
        jCheckBoxToString.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxToString.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabelResultCoding.setText("Result Coding:");

        jComboBoxResultCoding.setEditable(true);
        jComboBoxResultCoding.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jCheckBoxFromString.setText("From String");
        jCheckBoxFromString.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxFromString.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabelSourceCoding.setText("Source Coding:");

        jComboBoxSourceCoding.setEditable(true);
        jComboBoxSourceCoding.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jRadioEncode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(jLabel6)
                                            .add(jLabel5))
                                        .add(22, 22, 22))
                                    .add(layout.createSequentialGroup()
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(layout.createSequentialGroup()
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                    .add(jLabel3)
                                                    .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
                                                .add(6, 6, 6))
                                            .add(layout.createSequentialGroup()
                                                .add(jLabel4)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                                            .add(layout.createSequentialGroup()
                                                .add(jLabelResultCoding, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                                            .add(layout.createSequentialGroup()
                                                .add(jRadioDecode)
                                                .add(67, 67, 67))
                                            .add(layout.createSequentialGroup()
                                                .add(jLabelSourceCoding)
                                                .add(47, 47, 47)))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                                .add(layout.createSequentialGroup()
                                    .add(jLabel2)
                                    .add(33, 33, 33)))
                            .add(layout.createSequentialGroup()
                                .add(jLabel1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jTextField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                            .add(mOutputResult, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                            .add(mCreatedFileText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                            .add(mOverwrite)
                            .add(mFolderText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                            .add(jComboBoxSourceCoding, 0, 327, Short.MAX_VALUE)
                            .add(mInputDataText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                            .add(jCheckBoxFromString)
                            .add(jComboBoxResultCoding, 0, 327, Short.MAX_VALUE)
                            .add(mXMLSourceText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                            .add(jCheckBoxToString)
                            .add(selectElementComboBox, 0, 327, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, mFolderBtn)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(mDataButton)
                                .add(mXMLButton)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(selectElementComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jCheckBoxToString)
                    .add(jRadioEncode))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mXMLSourceText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(mXMLButton)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(2, 2, 2)
                        .add(jLabelResultCoding)
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jRadioDecode)
                            .add(jCheckBoxFromString)))
                    .add(jComboBoxResultCoding, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(mInputDataText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(mDataButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabelSourceCoding)
                    .add(jComboBoxSourceCoding, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(21, 21, 21)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(mOutputResult, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mFolderBtn)
                    .add(jLabel5)
                    .add(mFolderText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(mOverwrite)
                        .add(28, 28, 28))
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel6)
                        .add(mCreatedFileText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void mFolderBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mFolderBtnActionPerformed
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
            
            public String getDescription() {
                return _bundle.getString("test_panel.lbl.all_directories");
            }
        };
        chooser.setFileFilter(fileFilter);
        String whereToLook;
        if (mFolderText.getText() != null && mFolderText.getText().length() != 0) {
            whereToLook = mFolderText.getText();
        } else {
            whereToLook = xsdFilePath;
        }
        chooser.setCurrentDirectory(new File(whereToLook));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        File selectedFile = null;
        if ( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
            selectedFile = chooser.getSelectedFile();
        }                
        if (selectedFile != null) {
            this.mFolderText.setText(selectedFile.getAbsolutePath());
            updateCreatedFolder();
        }
    }//GEN-LAST:event_mFolderBtnActionPerformed

    private void mDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mDataButtonActionPerformed
        String whereToLook;
        if (mInputDataText.getText() != null && mInputDataText.getText().length() != 0) {
            whereToLook = mInputDataText.getText();
        } else {
            whereToLook = mPrefs.get(PREF_INPUT, xsdFilePath);
        }
        File selectedFile = this.getFileFromChooser(whereToLook, null);        
        if (selectedFile == null) {
            return;
        }
        if (!selectedFile.exists()) {
            NotifyDescriptor desc = new NotifyDescriptor.Message(
                    _bundle.getString("test_panel.lbl.file_does_not_exist"),
                    NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(desc);
            return;
        }
        mInputDataText.setText(selectedFile.getAbsolutePath());
        if (mOutputResult.getText() == null || mOutputResult.getText().length() == 0) {
            String name = selectedFile.getName();
            name = name.indexOf('.') >= 0 ?
                name.substring(0, name.indexOf('.')) : name;
            mOutputResult.setText(name);
        }
    }//GEN-LAST:event_mDataButtonActionPerformed

    private void mXMLButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mXMLButtonActionPerformed
        
        String whereToLook;
        if (mXMLSourceText.getText() != null && mXMLSourceText.getText().length() != 0) {
            whereToLook = mXMLSourceText.getText();
        } else {
            whereToLook = mPrefs.get(PREF_INPUT, xsdFilePath);
        }
        File selectedFile = this.getFileFromChooser(whereToLook, new String[][] {
            {"xml", _bundle.getString("test_panel.lbl.xml_files") + " (*.xml)"} //NOI18N
        });
        if (selectedFile == null) {
            return;
        }
        if (!selectedFile.exists()) {
            NotifyDescriptor desc = new NotifyDescriptor.Message(
                    _bundle.getString("test_panel.lbl.xml_file_does_not_exist"),
                    NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(desc);
            return;
        }
        mXMLSourceText.setText(selectedFile.getAbsolutePath());
        if (mOutputResult.getText() == null || mOutputResult.getText().length() == 0) {
            String name = selectedFile.getName();
            name = name.indexOf('.') >= 0 ?
                name.substring(0, name.indexOf('.')) : name;
            mOutputResult.setText(name);
        }
    }//GEN-LAST:event_mXMLButtonActionPerformed

    private void jRadioDecodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioDecodeActionPerformed
        updateComponents();
    }//GEN-LAST:event_jRadioDecodeActionPerformed

    private void jRadioEncodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioEncodeActionPerformed
        updateComponents();
    }//GEN-LAST:event_jRadioEncodeActionPerformed

    /** Open the file chooser and return the file.
     *@param oldUrl url where to start browsing
     */
    private File getFileFromChooser(String oldUrl, final String[][] extensions) {        
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
                
        if (extensions != null) {
            FileFilter filter = new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    
                    String extension = FileUtil.getExtension(f.getAbsolutePath());
                    //String extension = getExtension(f);
                    if (extension != null) {
                        int size = extensions.length;
                        for (int i = 0; i < size; i++) {
                            if (extensions[i][0].equals(extension)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
                
                public String getDescription() {
                    if (extensions == null) {
                        return ""; //NOI18N
                    }
                    StringBuffer desc = new StringBuffer();
                    for (int i = 0; i < extensions.length; i++) {
                        if (i > 0) {
                            desc.append(", ");  //NOI18N
                        }
                        desc.append(extensions[i][1]);
                    }
                    return desc.toString();
                }
            };            
            chooser.setFileFilter(filter);
        }
        
        if (oldUrl!=null) {
            try {
                File file = new File(oldUrl);
                File parentDir = file.getParentFile();
                if (parentDir!=null && parentDir.exists()) {
                    chooser.setCurrentDirectory(parentDir);
                }
            } catch (java.lang.IllegalArgumentException x) {
                //Ignore
            }
        }
        File selectedFile=null;
        if ( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ) {
            selectedFile = chooser.getSelectedFile();
        }
        return selectedFile;
    }    
    
    /*
     * Get the extension of a file.
     */  
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
        
    private void updateCreatedFolder() {
        String expectedExtension = "";  //NOI18N
        if (getActionType().equals(EncoderTestPerformerImpl.DECODE)) {
            expectedExtension = ".xml";  //NOI18N
        } else if (getActionType().equals(EncoderTestPerformerImpl.ENCODE)) {
            expectedExtension = ".out";  //NOI18N
        }
        String folderName = mFolderText.getText().trim();
        String outputName = mOutputResult.getText().trim();
        
        File f = new File(xsdFilePath);
        String createdFileName = folderName + 
            ( folderName.endsWith("/") || folderName.endsWith( File.separator ) || folderName.length() == 0 ? "" : "/" ) + // NOI18N
            outputName + expectedExtension;
            
        mCreatedFileText.setText( createdFileName.replace( '/', File.separatorChar ) ); // NOI18N        
    }

    public void insertUpdate(DocumentEvent e) {
        updateCreatedFolder();
    }

    public void removeUpdate(DocumentEvent e) {
        updateCreatedFolder();
    }

    public void changedUpdate(DocumentEvent e) {
        updateCreatedFolder();
    }
    
    public void setTopElementDecls(QName[] elements, QName select) {
        if (select == null && sameAsPreviousLaunch()) {
            String topElem = mPrefs.get(PREF_TOP_ELEM, null);
            if (topElem != null) {
                select = QName.valueOf(topElem);
            }
        }
        selectElementComboBox.removeAllItems();
        DisplayQName selectDispQName = null;
        DisplayQName dispQName;
        for (int i = 0; i < elements.length; i++) {
            dispQName = new DisplayQName(elements[i]);
            if (elements[i].equals(select)) {
                selectDispQName = dispQName;
            }
            selectElementComboBox.addItem(dispQName);
        }
        if (selectDispQName != null) {
            selectElementComboBox.setSelectedItem(selectDispQName);
        }
    }
    
    public QName getSelectedTopElementDecl() {
        DisplayQName dispQName = (DisplayQName) selectElementComboBox.getSelectedItem();
        if (dispQName == null) {
            return null;
        }
        return dispQName.getQName();
    }
    
    @Override
    public boolean contains(int x, int y) {
        return true;
    }
        
    /**
     * Used for displaying qualified name using local part.
     */
    private static class DisplayQName {
        
        private final QName mQName;
        
        public DisplayQName(QName qName) {
            mQName = qName;
        }
        
        public QName getQName() {
            return mQName;
        }

        @Override
        public String toString() {
            return mQName.getLocalPart();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox jCheckBoxFromString;
    private javax.swing.JCheckBox jCheckBoxToString;
    private javax.swing.JComboBox jComboBoxResultCoding;
    private javax.swing.JComboBox jComboBoxSourceCoding;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelResultCoding;
    private javax.swing.JLabel jLabelSourceCoding;
    private javax.swing.JRadioButton jRadioDecode;
    private javax.swing.JRadioButton jRadioEncode;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField mCreatedFileText;
    private javax.swing.JButton mDataButton;
    private javax.swing.JButton mFolderBtn;
    private javax.swing.JTextField mFolderText;
    private javax.swing.JTextField mInputDataText;
    private javax.swing.JTextField mOutputResult;
    private javax.swing.JCheckBox mOverwrite;
    private javax.swing.JButton mXMLButton;
    private javax.swing.JTextField mXMLSourceText;
    private javax.swing.JComboBox selectElementComboBox;
    // End of variables declaration//GEN-END:variables

}
