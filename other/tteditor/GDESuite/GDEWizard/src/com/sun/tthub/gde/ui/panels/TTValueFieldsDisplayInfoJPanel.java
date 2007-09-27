
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * Copyright 2007 Sun Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder. *
 */

package com.sun.tthub.gde.ui.panels;


import com.sun.tthub.gdelib.GDEException;
import com.sun.tthub.gdelib.GDERuntimeException;
import com.sun.tthub.gdelib.InvalidArgumentException;

import com.sun.tthub.gdelib.fields.DataTypeNature;
import com.sun.tthub.gdelib.fields.FieldMetaDataProcessor;
import com.sun.tthub.gdelib.fields.SimpleEntryFieldDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldMetaData;
import com.sun.tthub.gdelib.fields.UIComponentType;
import com.sun.tthub.gdelib.fields.SimpleDataTypes;
import com.sun.tthub.gdelib.fields.FieldInfo;
import com.sun.tthub.gdelib.fields.FieldDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldDataEntryNature;
import com.sun.tthub.gdelib.fields.SelectionFieldDisplayInfo;

import com.sun.tthub.gdelib.logic.TTValueDisplayInfo;
import com.sun.tthub.gdelib.logic.TTValueFieldInfo;
import com.sun.tthub.gde.logic.GDEAppContext;
import com.sun.tthub.gde.logic.GDEClassesManager;
import com.sun.tthub.gde.ui.MainWizardUI;
import java.awt.Component;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * This panel accepts a map containing the list of fields for which the display
 * information is to be collected from the user. The elements of the map should
 * implement the clone() interface and should override the clone() method to 
 * provide a deep copy of it. When the user changes the state of the JPanel, 
 * while defining the field's display control attributes, the class will not 
 * alter the corresponding displaycontrol attribute in the map directly. Instead,
 * it will use the clone method to duplicate the existing display control 
 * attributes and will update the clone. It will transfer the details from the
 * clone to the original object in the map, only after gettting the confirmation
 * from the user.
 *
 * @author  Hareesh Ravindran
 */

public class TTValueFieldsDisplayInfoJPanel extends FieldsDisplayInfoJPanel {
    
    private WizardController controller;
    /**
     * This class extends the FieldDisplayInfoJPanel class. 
     */
    
    public TTValueFieldsDisplayInfoJPanel(WizardController controller) {    
        this.controller = controller;        
        initComponents();
        drawVariablePanel(PANELTYPE_SEL_ATTR);
        classLoader = GDEAppContext.getInstance().getClassLoader();
    }

    /**
     *
     * This method saves the fieldInfo details. This method will be called when
     * the selection of the JTree changes. At this point the contents of the 
     * modified field display info can be changed. The classes that extend
     * this one should take care to override this function to save the additional
     * information.
     *
     */
    protected void processModifiedSelection(FieldInfo fieldInfo) {        
        super.processModifiedSelection(fieldInfo);
    }
    
    public void loadWizardContentPanel() {
        TTValueDisplayInfo displayInfo = 
                        controller.getTTValueDisplayInfo();
        // set the map to the initialfields info map.
        this.initialFieldsInfo = displayInfo.getExtFieldInfoMap();
        super.loadWizardContentPanel();
    }    
    
    private void cmbComponentTypesActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        if(curFieldInfo == null)
            return;        
        UIComponentType uiType = (UIComponentType) 
                        this.cmbComponentTypes.getSelectedItem();
        if(uiType == null)
            return;        
        curFieldInfo.getFieldDisplayInfo().setUIComponentType(uiType);
    }      
    
    /**
     * Call the super.filleFieldDisplayInfo() to fill all the controls other
     * than the portlet appearance parameters. After that, fill the 
     * portlet appearance parameters in this function.
     */
    protected void fillFieldDisplayInfo(FieldInfo fieldInfo) {
        super.fillFieldDisplayInfo(fieldInfo);
        TTValueFieldInfo info = (TTValueFieldInfo) fieldInfo;
        this.chkIsRequired.setSelected(info.getIsRequired());
        this.chkIsSearchable.setSelected(info.getIsSearchable());
        this.chkIncludeInSearchResults.setSelected(
                    info.getIncludeInSearchResults());
    }
    
    public void processWizardContents() throws GDEException {}
    
    public boolean validationFailed(GDEWizardPageValidationException ex) {        
        return false;
    }    
    public void validateContents() throws GDEWizardPageValidationException {}
    
    
    public String getName() { 
        return "TTFieldsDisplayInfoJPanel"; 
    }
    
    private void txtDisplayNameFocusLost(java.awt.event.FocusEvent evt) {
        if(curFieldInfo == null)
            return;        
        
        String displayName = this.txtDisplayName.getText();
        curFieldInfo.getFieldDisplayInfo(
                        ).setFieldDisplayName(displayName);        
    }
    
    private class PortletAppearanceChangeHandler implements ItemListener {
        
        public void itemStateChanged(ItemEvent e) {
            TTValueFieldInfo info = (TTValueFieldInfo) curFieldInfo;
            if(e.getSource() == chkIsRequired) {
                info.setIsRequired(e.getStateChange() == ItemEvent.SELECTED);
            } else if (e.getSource() == chkIsSearchable) {
                info.setIsSearchable(e.getStateChange() == ItemEvent.SELECTED);                
            } else if (e.getSource() == chkIncludeInSearchResults) {
                info.setIncludeInSearchResults(
                            e.getStateChange() == ItemEvent.SELECTED);                
            }
        }
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
    private void initComponents() {
        btnGrpDataTypeNature = new javax.swing.ButtonGroup();
        lblTitle = new javax.swing.JLabel();
        pnlDisplayCtrlAttr = new javax.swing.JPanel();
        lblDisplayName = new javax.swing.JLabel();
        txtDisplayName = new javax.swing.JTextField();
        pnlDataTypeNature = new javax.swing.JPanel();
        optEntryType = new javax.swing.JRadioButton();
        optSingleSelectType = new javax.swing.JRadioButton();
        optMultiSelectType = new javax.swing.JRadioButton();
        lblDatatypeNature = new javax.swing.JLabel();
        lblComponentType = new javax.swing.JLabel();
        cmbComponentTypes = new javax.swing.JComboBox();
        pnlVariableCtrlAttr = new javax.swing.JPanel();
        pnlFields = new javax.swing.JPanel();
        scrlPaneFieldList = new javax.swing.JScrollPane();
        TreeNode rootNode = new DefaultMutableTreeNode("Fields");
        TreeModel treeModel = new DefaultTreeModel(rootNode);
        treeFieldList = new JTree(treeModel);

        treeFieldList.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeFieldList.setShowsRootHandles(true);
        treeFieldList.addTreeSelectionListener(this.new FieldTreeSelectionListener());
        complexImage = createImageIcon("complex.gif");
        // set the cell renderer.
        treeFieldList.setCellRenderer(this.new CustomTreeCellRenderer(complexImage));
        // Disable all panels till the user selects a field from the tree node.
        pnlPortletAppearanceAttr = new javax.swing.JPanel();
        chkIsRequired = new javax.swing.JCheckBox();
        chkIsSearchable = new javax.swing.JCheckBox();
        chkIncludeInSearchResults = new javax.swing.JCheckBox();

        lblTitle.setFont(new java.awt.Font("Dialog", 1, 14));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Field Display Control Attributes");

        pnlDisplayCtrlAttr.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblDisplayName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDisplayName.setLabelFor(txtDisplayName);
        lblDisplayName.setText("Display Name:");

        txtDisplayName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDisplayNameFocusLost(evt);
            }
        });
        
        txtDisplayName.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        pnlDataTypeNature.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnGrpDataTypeNature.add(optEntryType);
        optEntryType.setText("Field Entry");
        optEntryType.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        optEntryType.setMargin(new java.awt.Insets(0, 0, 0, 0));
        optEntryType.addActionListener(this.new FieldDataEntryNatureSelectionHandler());

        btnGrpDataTypeNature.add(optSingleSelectType);
        optSingleSelectType.setText("Single Select");
        optSingleSelectType.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        optSingleSelectType.setMargin(new java.awt.Insets(0, 0, 0, 0));
        optSingleSelectType.addActionListener(this.new FieldDataEntryNatureSelectionHandler());

        btnGrpDataTypeNature.add(optMultiSelectType);
        optMultiSelectType.setText("Multi Select");
        optMultiSelectType.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        optMultiSelectType.setMargin(new java.awt.Insets(0, 0, 0, 0));
        optMultiSelectType.addActionListener(this.new FieldDataEntryNatureSelectionHandler());

        // Set the listeners for the portlet appearance attributes.
        this.chkIsRequired.addItemListener(this.new 
                                PortletAppearanceChangeHandler());
        this.chkIsSearchable.addItemListener(this.new 
                                PortletAppearanceChangeHandler());
        this.chkIncludeInSearchResults.addItemListener(this.new 
                                PortletAppearanceChangeHandler());
                
        org.jdesktop.layout.GroupLayout pnlDataTypeNatureLayout = new org.jdesktop.layout.GroupLayout(pnlDataTypeNature);
        pnlDataTypeNature.setLayout(pnlDataTypeNatureLayout);
        pnlDataTypeNatureLayout.setHorizontalGroup(
            pnlDataTypeNatureLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlDataTypeNatureLayout.createSequentialGroup()
                .addContainerGap()
                .add(optEntryType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(optSingleSelectType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(optMultiSelectType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlDataTypeNatureLayout.setVerticalGroup(
            pnlDataTypeNatureLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlDataTypeNatureLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlDataTypeNatureLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(optSingleSelectType)
                    .add(optMultiSelectType))
                .addContainerGap(13, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlDataTypeNatureLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(optEntryType)
                .addContainerGap())
        );

        lblDatatypeNature.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDatatypeNature.setLabelFor(pnlDataTypeNature);
        lblDatatypeNature.setText("Datatype Nature:");

        lblComponentType.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblComponentType.setText("UI Comp. Type:");

        cmbComponentTypes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbComponentTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbComponentTypesActionPerformed(evt);
            }
        });        

        org.jdesktop.layout.GroupLayout pnlDisplayCtrlAttrLayout = new org.jdesktop.layout.GroupLayout(pnlDisplayCtrlAttr);
        pnlDisplayCtrlAttr.setLayout(pnlDisplayCtrlAttrLayout);
        pnlDisplayCtrlAttrLayout.setHorizontalGroup(
            pnlDisplayCtrlAttrLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlDisplayCtrlAttrLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlDisplayCtrlAttrLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblComponentType)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblDatatypeNature)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lblDisplayName))
                .add(20, 20, 20)
                .add(pnlDisplayCtrlAttrLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtDisplayName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlDataTypeNature)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, cmbComponentTypes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlDisplayCtrlAttrLayout.setVerticalGroup(
            pnlDisplayCtrlAttrLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlDisplayCtrlAttrLayout.createSequentialGroup()
                .add(24, 24, 24)
                .add(pnlDisplayCtrlAttrLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblDisplayName)
                    .add(txtDisplayName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(pnlDisplayCtrlAttrLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlDisplayCtrlAttrLayout.createSequentialGroup()
                        .add(14, 14, 14)
                        .add(lblDatatypeNature))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlDisplayCtrlAttrLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlDataTypeNature, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(pnlDisplayCtrlAttrLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlDisplayCtrlAttrLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cmbComponentTypes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlDisplayCtrlAttrLayout.createSequentialGroup()
                        .add(3, 3, 3)
                        .add(lblComponentType)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlVariableCtrlAttr.setLayout(new java.awt.GridLayout(1, 0));
        pnlFields.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        scrlPaneFieldList.setViewportView(treeFieldList);

        pnlPortletAppearanceAttr.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        chkIsRequired.setText("Display in Create-TT portlet (normal view) ?");
        chkIsRequired.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkIsRequired.setMargin(new java.awt.Insets(0, 0, 0, 0));

        chkIsSearchable.setText("Include as search param in Get-TT portlet ?");
        chkIsSearchable.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkIsSearchable.setMargin(new java.awt.Insets(0, 0, 0, 0));

        chkIncludeInSearchResults.setText("Include in search results in Get-TT portlet ?");
        chkIncludeInSearchResults.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkIncludeInSearchResults.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout pnlPortletAppearanceAttrLayout = new org.jdesktop.layout.GroupLayout(pnlPortletAppearanceAttr);
        pnlPortletAppearanceAttr.setLayout(pnlPortletAppearanceAttrLayout);
        pnlPortletAppearanceAttrLayout.setHorizontalGroup(
            pnlPortletAppearanceAttrLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlPortletAppearanceAttrLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlPortletAppearanceAttrLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, chkIsRequired, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, chkIncludeInSearchResults, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                    .add(chkIsSearchable, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlPortletAppearanceAttrLayout.setVerticalGroup(
            pnlPortletAppearanceAttrLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlPortletAppearanceAttrLayout.createSequentialGroup()
                .addContainerGap()
                .add(chkIsRequired)
                .add(25, 25, 25)
                .add(chkIsSearchable)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 32, Short.MAX_VALUE)
                .add(chkIncludeInSearchResults)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout pnlFieldsLayout = new org.jdesktop.layout.GroupLayout(pnlFields);
        pnlFields.setLayout(pnlFieldsLayout);
        pnlFieldsLayout.setHorizontalGroup(
            pnlFieldsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlFieldsLayout.createSequentialGroup()
                .addContainerGap()
                .add(scrlPaneFieldList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPortletAppearanceAttr)
                .addContainerGap())
        );
        pnlFieldsLayout.setVerticalGroup(
            pnlFieldsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlFieldsLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnlFieldsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlPortletAppearanceAttr, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(scrlPaneFieldList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlDisplayCtrlAttr)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlFields)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lblTitle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlVariableCtrlAttr, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .add(lblTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlFields, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlDisplayCtrlAttr)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlVariableCtrlAttr, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }
    // </editor-fold>

    protected javax.swing.JPanel pnlPortletAppearanceAttr = null;
    protected javax.swing.JCheckBox chkIsRequired = null;
    protected javax.swing.JCheckBox chkIsSearchable = null;
    protected javax.swing.JCheckBox chkIncludeInSearchResults = null;
}
