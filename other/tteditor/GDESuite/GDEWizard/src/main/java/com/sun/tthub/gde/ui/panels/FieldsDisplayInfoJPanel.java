
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
import com.sun.tthub.gdelib.fields.FieldMetaDataProcessor;
import com.sun.tthub.gdelib.fields.SimpleEntryFieldDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldMetaData;
import com.sun.tthub.gdelib.fields.DataTypeNature;
import com.sun.tthub.gdelib.fields.UIComponentType;
import com.sun.tthub.gdelib.fields.SimpleDataTypes;
import com.sun.tthub.gdelib.fields.FieldInfo;
import com.sun.tthub.gdelib.fields.FieldDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldDataEntryNature;
import com.sun.tthub.gdelib.fields.SelectionFieldDisplayInfo;
import com.sun.tthub.gdelib.fields.ComplexEntryFieldDisplayInfo;

import com.sun.tthub.gde.logic.GDEAppContext;
import com.sun.tthub.gde.logic.GDEClassesManager;
import com.sun.tthub.gde.ui.MainWizardUI;
import java.awt.Component;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;
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
import javax.swing.event.ChangeEvent;
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
public class FieldsDisplayInfoJPanel extends WizardContentJPanel {
    
    /**
     * The map containing the list of fields as keys and the corresponding 
     * field display info as values. Everytime a user selects a field to edit,
     * this class will make a copy of the field display info of the corresponding
     * field stored in the map. The clone is stored in the variable 
     * curFieldInfo. The user will be dealing with this variable to make
     * changes to the field display info of the selected field.
     */
    protected Map initialFieldsInfo;
    
    /**
     * Stores a copy of the field display info of the selected field. 
     * This is useful when the user moves from one node to another. At this point,
     * this variable will contain the value of the previously selected node and 
     * the user can choose to save/discard this information to the previous node.
     * After processing the previous node, the value of this will be set to 
     * the current node. Will be null if no nodes are selected in the tree. 
     * Note that this is a copy of the field display info and not a reference 
     * to the field display info in the extended TTvalue display info object. 
     */
    protected FieldInfo curFieldInfo;

    /**
     * Stores the global class loader used througout this GDE application. This 
     * will be used extensively for reflection. This variable stores a reference
     * to the global classloader.
     */
    protected ClassLoader classLoader;
    /**
     * 
     * Creates new form FieldsDisplayInfo panel. The purpose of this panel is
     * to gather the display control information for a set of fields of 
     * a particular class. (This panel will be used by the 
     * TTFieldsDisplayInfoJPanel and the ComplexFieldDisplayInfoDlg using the 
     * extension and containment mechanisms). This panel expects an initial map, 
     * where the map contains the list of FieldInfo objects for all the 
     * fields of the class. This panel does not accept the class/interface name 
     * and the parsing of the fields to get the list of fields. Instead, it 
     * delegates this operation to the controller of this panel (may be the 
     * extended class or the contained class).
     */
    
    public FieldsDisplayInfoJPanel() {        
        initComponents();
        drawVariablePanel(PANELTYPE_SEL_ATTR);
        classLoader = GDEAppContext.getInstance().getClassLoader();
    }
        // Add the control to the Grid Layout.
    protected void drawVariablePanel(int panelType) {        
        /** try to remove the current component. If an arry index out of bounds
         * exception occurs, ignore it as this is because there is no component
         * currently in the panel.
         */
        try {
            pnlVariableCtrlAttr.remove(0);
        } catch(ArrayIndexOutOfBoundsException ex) {/** do nothing **/}  
        
        // draw the selection panel or the complex entry attr panel depending on
        // the parameter. If the panel type is 'simple entry', then the existing
        // panel is removed and it is not replaced with any other panel.
        if(panelType == PANELTYPE_SEL_ATTR) {
            pnlVariableCtrlAttr.add(selectionAttrPanel);
        } else if(panelType == PANELTYPE_COMPLEX_ENTRY_ATTR) {
            pnlVariableCtrlAttr.add(complexEntryAttrPanel);
        } 
        
        pnlVariableCtrlAttr.revalidate();
    }
        
    public void setFieldDisplayInfoMap(Map initialFieldsInfo) {
        if(initialFieldsInfo == null) {
            throw new InvalidArgumentException("The map passed to the panel " +
                    "FieldsDisplayInfoPanel should not be null.");
        }                
        this.initialFieldsInfo = initialFieldsInfo;
    }
    /**
     * Other components can use this function to get the map which is filled
     * with the field display info for each field.
     */
    public Map getFieldDisplayInfoMap() { return this.initialFieldsInfo; }
    
    /**
     * Creates the Image Icon from the image specified. The image should be 
     * present in the same physical folder as the class file. Otherwise this
     * method will return null for the image.
     */
    protected ImageIcon createImageIcon(String imgName) {
        try {
            InputStream imageInputStream = 
               FieldsDisplayInfoJPanel.class.getResourceAsStream(imgName);
            int noOfBytes = imageInputStream.available();
            byte bytes[] = new byte[noOfBytes];
            imageInputStream.read(bytes);
            return new ImageIcon(bytes);                    
        } catch(IOException ex) {
            /* Ignore the exception and return null to the user */
            return null;
        }
    }
    
    private void performInitialProcessing(FieldInfo fieldInfo) {
        FieldMetaData metaData = fieldInfo.getFieldMetaData();
        FieldDisplayInfo displayInfo = fieldInfo.getFieldDisplayInfo();
        
        if(metaData.getFieldDataTypeNature() == DataTypeNature.NATURE_COMPLEX) {
            optSingleSelectType.setEnabled(false);
            optMultiSelectType.setEnabled(false);
        } else if (metaData.getFieldDataType() == SimpleDataTypes.TYPE_BOOLEAN 
                || metaData.getFieldDataType() == 
                SimpleDataTypes.TYPE_BOOLEAN_OBJ) {
            optSingleSelectType.setEnabled(false);
            optMultiSelectType.setEnabled(false);
        } else {
            optSingleSelectType.setEnabled(true);
            optMultiSelectType.setEnabled(true);
        }
    }
    
    /**
     * This function takes care of filling the display control attributes of the
     * selected field.
     */
    private void fillFieldInfo(FieldInfo fieldInfo) {        
        FieldMetaData metaData = fieldInfo.getFieldMetaData();
        FieldDisplayInfo displayInfo = fieldInfo.getFieldDisplayInfo();
        
        this.txtDisplayName.setText(displayInfo.getFieldDisplayName());        
        
        if(displayInfo.getFieldDataEntryNature() == 
                            FieldDataEntryNature.TYPE_ENTRY)
            this.optEntryType.setSelected(true);
        else if(displayInfo.getFieldDataEntryNature() == 
                FieldDataEntryNature.TYPE_SINGLE_SELECT)
            this.optSingleSelectType.setSelected(true);
        else
            this.optMultiSelectType.setSelected(true);
        
        performInitialProcessing(fieldInfo);
        
        // Fill the combo box and set the selected value as specified in the
        // DisplayControlAttr.
        fillUIListCombo(metaData, displayInfo);
        
        if(displayInfo instanceof ComplexEntryFieldDisplayInfo) {       
            complexEntryAttrPanel.setFieldInfo(fieldInfo);
            drawVariablePanel(PANELTYPE_COMPLEX_ENTRY_ATTR);
            // Delegate the filling to the ComplexDisplayControlAttr panel.
        } else if(displayInfo instanceof SelectionFieldDisplayInfo) {
            selectionAttrPanel.setFieldInfo(fieldInfo);
            // Delegate the filling to the Selection Attr panel.
            drawVariablePanel(PANELTYPE_SEL_ATTR);
        } else {
            drawVariablePanel(PANELTYPE_SIMPLE_ENTRY_ATTR);
        }
    }
    
    private void fillUIListCombo(FieldMetaData metaData, 
                        FieldDisplayInfo fieldDisplayInfo) {
        ClassLoader classLoader = GDEAppContext.getInstance().getClassLoader();
        UIComponentType[] compArr = null;
        try {
            compArr = new FieldMetaDataProcessor(
                metaData, classLoader).getControlList(
                fieldDisplayInfo.getFieldDataEntryNature());
        } catch(GDEException ex) {
            throw new GDERuntimeException(ex);
        }
        this.cmbComponentTypes.removeAllItems();
        for(int i = 0; i < compArr.length; ++i) {
            this.cmbComponentTypes.addItem(compArr[i]); 
            
        }
        this.cmbComponentTypes.setSelectedItem(
                        fieldDisplayInfo.getUIComponentType());        
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
        if(fieldInfo == null)
            return;        
        
        FieldInfo oldFieldInfo = (FieldInfo) initialFieldsInfo.get(
                    fieldInfo.getFieldMetaData().getFieldName());
                
        // Replace the previous field info with the current one.
        initialFieldsInfo.put(
                fieldInfo.getFieldMetaData().getFieldName(), fieldInfo);
    }
    
    /**
     * This method will take care of filling the page with the values from the
     * TTValueFieldDisplayInfo parameter provided. The function is called when
     * ever the selected node in the tree changes. (i.e. when a new node is 
     * selected in the tree). The classes that extend this one should take care
     * to override this function in order to display additional field display
     * attributes on the page.
     */
    protected void fillFieldDisplayInfo(FieldInfo fieldInfo) {
        fillFieldInfo(fieldInfo);
    }
    
    /**
     * Inner class to handle the tree selection event. This class sets the 
     * values of the curFieldInfo and the prevFieldDisplayInfo variables.
     */
    protected class FieldTreeSelectionListener implements TreeSelectionListener {
 
        public void valueChanged(TreeSelectionEvent e) {
            // get a reference to the currently selected node of the tree.
            DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) 
                        treeFieldList.getLastSelectedPathComponent();
            String fieldName = (selNode != null) ? 
                    (String) selNode.getUserObject() : null;
            
            // If the selected node is same as the previous node, return from
            // the method. In this case, the previous selection need not be
            // processed.
            if(curFieldInfo != null &&  curFieldInfo.getFieldMetaData(
                            ).getFieldName().equals(fieldName))
                return;
            
            processTextBoxFocusEvent();
            // Process the previous selection, if any and  proceed.
            processModifiedSelection(curFieldInfo);            
            
            // If the current node is the root or the user has deselected the 
            // nodes of the tree, return from the method. The previous node in
            // which the changes are made is already processed.
            if(selNode == null || selNode.isRoot())
                return;
            
            FieldInfo info = (FieldInfo) initialFieldsInfo.get(
                        (String) selNode.getUserObject());
            try {
                // reset the curFieldInfo.
                curFieldInfo = (FieldInfo) info.clone();
            } catch (CloneNotSupportedException ex) {
                throw new GDERuntimeException(ex);
            }
            
            //display the information of the selected node in the panel. After
            // filling these values, the user is ready to use the page.
            fillFieldDisplayInfo(curFieldInfo);            
        }
    }
    
    /**
     * The inner class which represents the custom renderer. This class will
     * take care of painting a 'C' icon against all the complex fields
     * displayed in the JTree. This makes the user easy to identify the complex
     * data types.
     */    
    protected class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
        
        private Icon complexIcon;
        public CustomTreeCellRenderer(Icon icon) {
            complexIcon = icon;
        }

        public Component getTreeCellRendererComponent( JTree tree, Object value,
                    boolean sel, boolean expanded, boolean leaf, 
                    int row, boolean hasFocus) {            

            // Paint the label as sepcified in the base class. 
            super.getTreeCellRendererComponent(
                    tree, value, sel, expanded, leaf, row, hasFocus);                        
            setIcon(null);
            if(row == 1) {         
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;                            
                String fieldName = (String) node.getUserObject();                            
                FieldInfo info = (FieldInfo)
                                    initialFieldsInfo.get(fieldName);
                FieldMetaData data = info.getFieldMetaData();
                int dataTypeNature = data.getFieldDataTypeNature();
                if (dataTypeNature == DataTypeNature.NATURE_COMPLEX) {
                    setIcon(complexIcon);
                } 
            }            
            return this;        
        }
    }
    
   
    /**
     * The map should should be initialized with the required fields. Otherwise
     * a null pointer exception will result. When this panel is overridden to
     * add extended functionality, this function should be overridden and it is
     * the responsibility of that function to intialize the value of the map.
     * The same function should call the super.loadWizardContentPanel() to execute
     * this function. Otherwise the wizard panel will not be painted properly.
     */
    public void loadWizardContentPanel() {        
        paintPanel();
    }    
    
    public void paintPanel() {
        Collection fieldsMetaData = new ArrayList();
        Collection entrySet = this.initialFieldsInfo.entrySet();
        for(Iterator it = entrySet.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            FieldInfo info = (FieldInfo) entry.getValue();
            fieldsMetaData.add(info.getFieldMetaData());
          
        }
        // Fill the JTree control with the list of fields obtained and expand
        // the nodes of the JTree.        
        fillFieldList(fieldsMetaData, treeFieldList);        
    }
    
    /**
     * This method is used to fill the JTree component with the list of the
     * fields. The method will get all the fields in the extended trouble 
     * in the JTree. It uses the ExtendedTTValueDisplayInfo stored in the
     * controller to get the list of fields.
     */
    private void fillFieldList(Collection fields, JTree treeFieldLst) {
        DefaultTreeModel model = (DefaultTreeModel) treeFieldLst.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
        //reset rootNode
        rootNode.removeAllChildren();
        model.setRoot(rootNode);
        treeFieldLst.setModel(model);
        
        for(Iterator it = fields.iterator(); it.hasNext(); ) {
            FieldMetaData metaData = (FieldMetaData) it.next();
            model.insertNodeInto(new  DefaultMutableTreeNode(metaData.getFieldName()), 
                                rootNode, rootNode.getChildCount());
              
        }           
        // set the initial level expanded so that the user can view the list of
        // fields, when the page is loaded.
        treeFieldLst.expandRow(0);
    }
    
    
    public void processWizardContents(int wizardAction) throws GDEException {}
    
    /**
     * This function should invoke the processModifiedContents on the current
     * node, so that the last node is saved to the model.
     */
    public void preProcessWizardContents(int wizardAction) throws GDEException {        
        
        // This function is invoked before validation, in order to make sure that
        // the contents of the 'DisplayName' textbox is transferred to the 
        // field in the model (i.e. the curFieldInfo). In some cases, the lost
        // focus event of the text box does not get fired automatically. Calling
        // this function from the preProcessWizardContents will make sure that 
        // the model contains exactly the same data as that of the view.
        processTextBoxFocusEvent();
        
        // Get the current field and invoke the processModifiedSelection.
        // This is done to transfer the contents of the tree node selected in the
        // last step. If this is not done, the last modified tree node will not
        // reflect the contents.
        processModifiedSelection(curFieldInfo);        
    }
    
    
    public boolean validationFailed(GDEWizardPageValidationException ex) {        
        return false;
    }
    
    
    public void validateContents() throws GDEWizardPageValidationException {}
    
    
    public String getName() { 
        return "TTFieldsDisplayInfoJPanel"; 
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
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

        lblTitle.setFont(new java.awt.Font("Dialog", 1, 14));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Field Display Control Attributes");

        pnlDisplayCtrlAttr.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblDisplayName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDisplayName.setLabelFor(txtDisplayName);
        lblDisplayName.setText("Display Name:");

        txtDisplayName.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtDisplayName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDisplayNameFocusLost(evt);
            }
        });

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

        org.jdesktop.layout.GroupLayout pnlDataTypeNatureLayout = new org.jdesktop.layout.GroupLayout(pnlDataTypeNature);
        pnlDataTypeNature.setLayout(pnlDataTypeNatureLayout);
        pnlDataTypeNatureLayout.setHorizontalGroup(
            pnlDataTypeNatureLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlDataTypeNatureLayout.createSequentialGroup()
                .addContainerGap()
                .add(optEntryType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(optSingleSelectType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(optMultiSelectType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
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
                    .add(org.jdesktop.layout.GroupLayout.LEADING, txtDisplayName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlDataTypeNature)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, cmbComponentTypes, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE))
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
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pnlVariableCtrlAttr.setLayout(new java.awt.GridLayout(1, 0));

        pnlFields.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        scrlPaneFieldList.setViewportView(treeFieldList);

        org.jdesktop.layout.GroupLayout pnlFieldsLayout = new org.jdesktop.layout.GroupLayout(pnlFields);
        pnlFields.setLayout(pnlFieldsLayout);
        pnlFieldsLayout.setHorizontalGroup(
            pnlFieldsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlFieldsLayout.createSequentialGroup()
                .addContainerGap()
                .add(scrlPaneFieldList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlFieldsLayout.setVerticalGroup(
            pnlFieldsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlFieldsLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(scrlPaneFieldList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
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
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lblTitle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlVariableCtrlAttr, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE))
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
    // </editor-fold>//GEN-END:initComponents

    private void txtDisplayNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDisplayNameFocusLost
        processTextBoxFocusEvent();
    }//GEN-LAST:event_txtDisplayNameFocusLost

    protected void processTextBoxFocusEvent() {
        if(curFieldInfo == null)
            return;
        String displayName = this.txtDisplayName.getText();        
        curFieldInfo.getFieldDisplayInfo(
                ).setFieldDisplayName(displayName);                
    }
    
    private void cmbComponentTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbComponentTypesActionPerformed
        if(curFieldInfo == null)
            return;        
        UIComponentType uiType = (UIComponentType) 
                        this.cmbComponentTypes.getSelectedItem();
        if(uiType == null)
            return;        
        curFieldInfo.getFieldDisplayInfo().setUIComponentType(uiType);
    }//GEN-LAST:event_cmbComponentTypesActionPerformed

    /**
     *
     */
    protected class FieldDataEntryNatureSelectionHandler implements ActionListener {    

        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == optEntryType)
                entryActionPerformed(e);
            else if(e.getSource() == optSingleSelectType)
                singleSelectActionPerformed(e);
            else if(e.getSource() == optMultiSelectType)
                multiSelectActionPerformed(e);        
        }
        
        public void itemStateChanged(ActionEvent e) {
            if(e.getSource() == optEntryType)
                entryActionPerformed(e);
            else if(e.getSource() == optSingleSelectType)
                singleSelectActionPerformed(e);
            else if(e.getSource() == optMultiSelectType)
                multiSelectActionPerformed(e);
        }

        private void multiSelectActionPerformed(ActionEvent evt) {
            
            if(curFieldInfo ==  null)
                return;
            Object obj = evt.getSource();
            FieldMetaData metaData = curFieldInfo.getFieldMetaData();        
            try {
                FieldDisplayInfo fieldDisplayInfo = createSelFieldDisplayInfo(
                                    metaData, true);
                curFieldInfo.setFieldDisplayInfo(fieldDisplayInfo);                                
                // set the new fieldInfo Info to the selection attribute JPanel.
                selectionAttrPanel.setFieldInfo(curFieldInfo);
                selectionAttrPanel.lstDefaultSel.setSelectionMode(
                        ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                fillUIListCombo(metaData, fieldDisplayInfo);                
                // draw the SelectionAttrJPanel instead of the existing panel.
                drawVariablePanel(PANELTYPE_SEL_ATTR);            
                // replace the current field display control attribute info with the
                // newly created default one.                                                      
            } catch(GDEException ex) {
                throw new GDERuntimeException(ex);            
            }            
        }
        
        private void singleSelectActionPerformed(ActionEvent evt) {           
            if(curFieldInfo ==  null)
                return;
            Object obj = evt.getSource();
            FieldMetaData metaData = curFieldInfo.getFieldMetaData();        
            try {
                FieldDisplayInfo fieldDisplayInfo = createSelFieldDisplayInfo(
                                    metaData, false);
                curFieldInfo.setFieldDisplayInfo(fieldDisplayInfo);
                // set the new fieldDisplay Info to the selection attribute JPanel.
                selectionAttrPanel.setFieldInfo(curFieldInfo);
                selectionAttrPanel.lstDefaultSel.setSelectionMode(
                        ListSelectionModel.SINGLE_SELECTION);            
                fillUIListCombo(metaData, fieldDisplayInfo);                           
                // draw the SelectionAttrJPanel instead of the existing panel.
                drawVariablePanel(PANELTYPE_SEL_ATTR);            
                // replace the current field display control attribute info with the
                // newly created default one.                      
            } catch(GDEException ex) {
                throw new GDERuntimeException(ex);            
            }            
        }
        /**
         * When this option button is clicked, the panel has to be redrawn. Before
         * drawing check whether the current type is also optEntry. If so, do nothing,
         * otherwise, redraw the variable panel.
         */    
        private void entryActionPerformed(ActionEvent evt) {
            if(curFieldInfo == null)
                return;
            Object obj = evt.getSource();
            FieldMetaData metaData = curFieldInfo.getFieldMetaData();        
            FieldDisplayInfo fieldDisplayInfo =  null;        
            try {
                FieldMetaDataProcessor processor = new FieldMetaDataProcessor(
                    metaData, classLoader);   
                if(metaData.getFieldDataTypeNature() == 
                                        DataTypeNature.NATURE_SIMPLE) {
                    fieldDisplayInfo = 
                            processor.createDefaultSimpleEntryFieldDisplayInfo();
                    curFieldInfo.setFieldDisplayInfo(fieldDisplayInfo);     
                    fillUIListCombo(metaData, fieldDisplayInfo);                                    
                    drawVariablePanel(PANELTYPE_SIMPLE_ENTRY_ATTR);
                } else {
                    fieldDisplayInfo = 
                            processor.createDefaultComplexEntryFieldDisplayInfo();                
                    curFieldInfo.setFieldDisplayInfo(fieldDisplayInfo);                    
                    // set the values of the display control attribute to the 
                    // controls in the complex data entry panel
                    complexEntryAttrPanel.setFieldInfo(curFieldInfo);                    
                    fillUIListCombo(metaData, fieldDisplayInfo);                
                    // Display the complex entry panel instead of the 
                    // existing panel.                
                    drawVariablePanel(PANELTYPE_COMPLEX_ENTRY_ATTR);    

                }
            } catch(GDEException ex) {
                throw new GDERuntimeException(ex);
            }        
        }    
        /**
         * When this option is performed, a new SelectionFieldDisplayInfo has to be 
         * created and displayed.
         */    
        private FieldDisplayInfo createSelFieldDisplayInfo(
                            FieldMetaData metaData, boolean isMultiSelect) 
                            throws GDEException {
            FieldMetaDataProcessor processor = new FieldMetaDataProcessor(
                metaData, classLoader);   
            SelectionFieldDisplayInfo fieldDisplayInfo = (SelectionFieldDisplayInfo) 
                            processor.createDefaultSelFieldDisplayInfo();            
            fieldDisplayInfo.setIsMultiSelect(isMultiSelect);        
            return fieldDisplayInfo;
        }        
    }    
   
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.ButtonGroup btnGrpDataTypeNature;
    protected javax.swing.JComboBox cmbComponentTypes;
    protected javax.swing.JLabel lblComponentType;
    protected javax.swing.JLabel lblDatatypeNature;
    protected javax.swing.JLabel lblDisplayName;
    protected javax.swing.JLabel lblTitle;
    protected javax.swing.JRadioButton optEntryType;
    protected javax.swing.JRadioButton optMultiSelectType;
    protected javax.swing.JRadioButton optSingleSelectType;
    protected javax.swing.JPanel pnlDataTypeNature;
    protected javax.swing.JPanel pnlDisplayCtrlAttr;
    protected javax.swing.JPanel pnlFields;
    public javax.swing.JPanel pnlVariableCtrlAttr;
    protected javax.swing.JScrollPane scrlPaneFieldList;
    protected javax.swing.JTree treeFieldList;
    protected javax.swing.JTextField txtDisplayName;
    // End of variables declaration//GEN-END:variables
    
    // These variables determine which panel to be loaded.
    public static final int PANELTYPE_SEL_ATTR = 0;
    public static final int PANELTYPE_SIMPLE_ENTRY_ATTR = 1;    
    public static final int PANELTYPE_COMPLEX_ENTRY_ATTR = 2;
    
    
    protected SelectionAttrJPanel selectionAttrPanel = new SelectionAttrJPanel();
    protected ComplexEntryAttrJPanel complexEntryAttrPanel = new ComplexEntryAttrJPanel();    
    protected ImageIcon complexImage;    
}
