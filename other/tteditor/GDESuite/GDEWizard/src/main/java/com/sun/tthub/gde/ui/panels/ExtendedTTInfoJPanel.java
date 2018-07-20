
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

import com.sun.tthub.gdelib.fields.DataTypeNature;
import com.sun.tthub.gdelib.fields.FieldInfo;
import com.sun.tthub.gdelib.fields.FieldMetaDataProcessor;
import com.sun.tthub.gdelib.fields.GenFieldUtil;
import com.sun.tthub.gdelib.fields.FieldDisplayInfo;
import com.sun.tthub.gdelib.fields.SimpleEntryFieldDisplayInfo;
import com.sun.tthub.gdelib.fields.FieldMetaData;
import com.sun.tthub.gdelib.fields.SimpleDataTypes;
import com.sun.tthub.gdelib.fields.UIComponentType;

import com.sun.tthub.gdelib.logic.TTValueDisplayInfo;
import com.sun.tthub.gdelib.logic.TTValueFieldInfo;

import com.sun.tthub.gde.logic.GDEAppContext;
import com.sun.tthub.gde.logic.GDEClassesManager;
import com.sun.tthub.gde.ui.GDEWizardUI;
import com.sun.tthub.gde.logic.GDEPreferences;
import com.sun.tthub.gde.logic.GDEPreferencesController;
import com.sun.tthub.gde.util.NetbeansUtilities;

import com.sun.tthub.gde.ui.*;
import java.awt.Dialog;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import java.awt.event.ItemEvent;




public final class ExtendedTTInfoJPanel extends WizardContentJPanel {
    
    private WizardController controller;
    /**
     * Creates new form ExtendedTTInfoJPanel
     */
    public ExtendedTTInfoJPanel(WizardController controller) {
        this.controller = controller;
        initComponents();
        radiobtnTTInterfaceClass.setSelected(true);
    }
    
    public void loadWizardContentPanel() {}
    
    public boolean validationFailed(GDEWizardPageValidationException ex) {
        String str = ex.getMessage();
        JOptionPane.showMessageDialog(this, str, "Validation Failed",
                JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    /**
     * Method from the WizardContentProcessor interface. Does nothing in this
     * panel.
     */
    public void preProcessWizardContents(int wizardAction) throws GDEException {}
    
    /**
     * This function is called after all validations are done in the page.
     * Set the names of the extended TroubleTicketValue interface and that of
     * the TroubleTicketValue class of the global display control parameters,
     * in this function.
     */
    public void processWizardContents(int wizardAction) throws GDEException {
        TTValueDisplayInfo displayInfo =
                controller.getTTValueDisplayInfo();
        if (radiobtnTTInterfaceClass.isSelected()==true) {
            processInterfaceClassContents(displayInfo);
        }else{
            processSchemaContents(displayInfo);
        }
    }
    
    private void processSchemaContents(final TTValueDisplayInfo displayInfo) throws GDEException {
        // Schema selected
        
        displayInfo.setExtTTValueSchema(txtTTSchema.getText());
        displayInfo.setExtTTValueImplClass("gde.generated.TroubleTicketValue");
        displayInfo.setExtTTValueInterface("gde.generated.TroubleTicketValue");
        generateSchemaJar(displayInfo.getExtTTValueSchema());
        resetFieldList();
    }
    private GDEPreferences getGDEPreferences() throws GDEException {
        GDEPreferencesController controller =
                GDEAppContext.getInstance().getGdePrefsController();
        return controller.retrievePreferences();
    }
    private void generateSchemaJar(String schemafile) throws GDEException{
        // invoke the ant script from the gde folder.
        String gdeFolder = getGDEPreferences().getGdeFolder();
       /*
        String antHome = getGDEPreferences().getAntHome();
       
        if(antHome == null || antHome.trim().equals("")) {
            throw new GDEException("Ant Home is not specified in the " +
                    " GDEPreferences.");
        }
        */
        try{
            
            java.util.Properties properties= new java.util.Properties();
            properties.setProperty( "gde-folder", gdeFolder);
            properties.setProperty( "schema-file",schemafile);
           
            properties.setProperty("jaxb-folder",NetbeansUtilities.getJAXBDir());
            
            java.io.File antFileObj=new java.io.File( gdeFolder, "build-schemajar-files.xml");
            
            int result = NetbeansUtilities.ExecuteAntTask(antFileObj, properties);
            
            if (result!=0)
                throw new GDEException("Unable to generate jar from schema");
            // AntUtil.executeSchemaAntScript(antHome,gdeFolder,"build-schemajar-files.xml",schemafile);
        } catch(Exception ex) {
            System.out.println(ex.toString());
            throw new GDEException("Exception occured while executing the" +
                    " ant script to build the schema jar.", ex);
            
        }
        
        try {
            
            GDEClassesManager mngr =
                    GDEAppContext.getInstance().getClassesManager();
            mngr.loadClassesFromJarFile("GDETroubleTicketValue.jar");
            
            
        } catch(GDEException ex) {
            ex.printStackTrace();
            String errorStr = ex.getMessage();
            JOptionPane.showMessageDialog(this, errorStr,
                    "Class Loading Failure", JOptionPane.ERROR_MESSAGE);
        }
        
        /*
        java.io.File file = new java.io.File(antHome);
        if(!file.exists() || !file.canRead()) {
            throw new GDEException("The specified ant home '" + antHome + "' " +
                    " is not a valid readable directory. Please check.");
        }
         
        String antFile = gdeFolder + "/build-schemajar-files.xml";
        java.io.File antFileObj = new java.io.File(antFile);
        if(!antFileObj.exists() || !antFileObj.canRead()) {
            throw new GDEException("The ant build file " +
                    "build-schemajar-files.xml does not exist in the GDE Folder");
        }
         
        String antExe = antHome + "/bin/ant";
        String cmdLine = antExe + " -f " + antFile +
                " -Dgde-folder=" + gdeFolder +
                " -Dschema-file="+schemafile;
        try {
            Runtime.getRuntime().exec(cmdLine);
        } catch(java.io.IOException ex) {
            System.out.println(ex.toString());
            throw new GDEException("Exception occured while executing the" +
                    " ant script to build the schema jar.", ex);
         
        }
         */
        
    }
    private void processInterfaceClassContents(final TTValueDisplayInfo displayInfo) throws GDEException {
        //Interface/Class Selected
        String oldInterfaceName = displayInfo.getExtTTValueInterface();
        String newInterfaceName = txtTTInterface.getText();
        // If the new interface name is not same as that of the interface name
        // stored in the ExtendedTTValueDisplayInfo instance in the controller,
        // reset all the field display info values in this object using the
        // new interface provided. If the old interface name is same, do nothing.
        if(!newInterfaceName.equals(oldInterfaceName)) {
            displayInfo.setExtTTValueInterface(newInterfaceName);
            resetFieldList();
        }
        
        // Set the value of the class name in ExtendedTTValueDisplayInfo to the
        // new class name.
        displayInfo.setExtTTValueImplClass(txtTTClass.getText());
    }
    
    
    
    /**
     * This method is used to fill the JTree component with the list of the
     * fields. The method will get all the fields in the extended trouble
     * ticket interface using the GDEClassesManager and will list these fields
     * in the JTree.
     */
    private void resetFieldList() throws GDEException {
        TTValueDisplayInfo displayInfo =
                controller.getTTValueDisplayInfo();
        
        
        String interfaceName = displayInfo.getExtTTValueInterface();
        Map map = GenFieldUtil.getDefaultFieldInfoMap(interfaceName,
                GDEAppContext.getInstance().getClassLoader());
        
        
        // The map returned by the above method will contain the generic field
        // info objects. These field info objects should be converted to
        // TTValueFieldInfo objects, as the next panel should display the
        // TTValueFieldInfo objects.
        Collection coll = map.entrySet();
        for(Iterator it = coll.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            entry.setValue(new TTValueFieldInfo(
                    (FieldInfo) entry.getValue(), false, false, false));
        }
        // Converted the values in the Map from generic FieldInfo objects to
        // TTValueFieldInfo objects.
        displayInfo.setExtFieldInfoMap(map);
    }
    
    public void validateContents()
    throws GDEWizardPageValidationException {
        
        if (radiobtnTTInterfaceClass.isSelected()==true) {
            validateInterfaceClassContents();
        }    else {
            //Validate Schema content
            String schemaName = txtTTSchema.getText();
            if(schemaName == null || schemaName.trim().equals("")) {
                throw new GDEWizardPageValidationException("The schema name/" +
                        "cannot be empty. Plese enter a valid schema name.");
            }
        }
        
        
    }
    
    private void  validateInterfaceClassContents() throws GDEWizardPageValidationException{
        String interfaceName = txtTTInterface.getText();
        String className = txtTTClass.getText();
        
        if(interfaceName == null || interfaceName.trim().equals("") ||
                className == null || className.trim().equals("")) {
            throw new GDEWizardPageValidationException("The interface name/" +
                    "class name cannot be empty. Plese enter a valid " +
                    "class/interface name.");
        }
        // check if the interface name is a valid one, i.e. it implements the
        // required interface. Use the GDEClassesManager to check for the
        // interface validity.
        GDEClassesManager mngr = GDEAppContext.getInstance(
                ).getClassesManager();
        try {
            boolean isAssignable = mngr.isAssignableToStdInterface(interfaceName);
            if(!isAssignable) {
                throw new GDEWizardPageValidationException("The Interface '" +
                        interfaceName + "' is not assignable to any GDE " +
                        "standard interfaces.");
            }
        } catch (GDEException ex) {
            // This exception occurs if the GDE Class loader is unable to load
            // the specified class. If such an exception occurs, throw a
            // GDEValidationException.
            throw new GDEWizardPageValidationException(ex);
        }
        
        // check if the class name specified can be loaded by the class loader
        // and can be assigned to the interface specified.
        
        try {
            // load the Class Object of the interface.
            Class interfaceCls = mngr.loadClass(interfaceName);
            Class classCls = mngr.loadClass(className);
            if(!interfaceCls.isAssignableFrom(classCls)) {
                throw new GDEWizardPageValidationException("The specified class '" +
                        className + "' is not assignable to the interface '" +
                        interfaceName + "'");
            }
        } catch (GDEException ex) {
            // This exception occurs if the specified class/interface cannot be
            // loaded by the GDE class loader. In this case, throw a
            // GDEWizardPageValidationException.
            throw new GDEWizardPageValidationException(ex);
        }
        
    }
    public String getName() { return "ExtendedTTInfoJPanel"; }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        btnTTValueGroup = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        lblTitle = new javax.swing.JLabel();
        lblTTInterface = new javax.swing.JLabel();
        txtTTInterface = new javax.swing.JTextField();
        lblTTClass = new javax.swing.JLabel();
        txtTTClass = new javax.swing.JTextField();
        btnChooseInterface = new javax.swing.JButton();
        btnChooseClass = new javax.swing.JButton();
        radiobtnTTInterfaceClass = new javax.swing.JRadioButton();
        radiobtnTTSchema = new javax.swing.JRadioButton();
        lblTTSchema = new javax.swing.JLabel();
        txtTTSchema = new javax.swing.JTextField();
        btnChooseSchema = new javax.swing.JButton();

        setName("");
        setPreferredSize(new java.awt.Dimension(220, 80));
        lblTitle.setFont(new java.awt.Font("Dialog", 1, 14));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Extended Trouble Ticket Value Information");

        lblTTInterface.setDisplayedMnemonic('I');
        lblTTInterface.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTTInterface.setText("Ex. TTValue Interface:");
        lblTTInterface.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txtTTInterface.setToolTipText("Name of the extended trouble ticket value interface");
        txtTTInterface.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtTTInterface.setEnabled(false);

        lblTTClass.setDisplayedMnemonic('C');
        lblTTClass.setText("Ex. TTValue Impl Class:");
        lblTTClass.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        txtTTClass.setToolTipText("Name of the impl class of the extended TTValue interface.");
        txtTTClass.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        txtTTClass.setEnabled(false);

        btnChooseInterface.setText("Choose...");
        btnChooseInterface.setEnabled(false);
        btnChooseInterface.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseInterfaceActionPerformed(evt);
            }
        });

        btnChooseClass.setText("Choose...");
        btnChooseClass.setEnabled(false);
        btnChooseClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseClassActionPerformed(evt);
            }
        });

        btnTTValueGroup.add(radiobtnTTInterfaceClass);
        radiobtnTTInterfaceClass.setFont(new java.awt.Font("Tahoma", 0, 12));
        radiobtnTTInterfaceClass.setText("Import Interface/Class");
        radiobtnTTInterfaceClass.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        radiobtnTTInterfaceClass.setMargin(new java.awt.Insets(0, 0, 0, 0));
        radiobtnTTInterfaceClass.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radiobtnTTInterfaceClassItemStateChanged(evt);
            }
        });

        btnTTValueGroup.add(radiobtnTTSchema);
        radiobtnTTSchema.setFont(new java.awt.Font("Tahoma", 0, 12));
        radiobtnTTSchema.setText("Import Schema");
        radiobtnTTSchema.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        radiobtnTTSchema.setMargin(new java.awt.Insets(0, 0, 0, 0));
        radiobtnTTSchema.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                radiobtnTTSchemaItemStateChanged(evt);
            }
        });

        lblTTSchema.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTTSchema.setText("Ex. TTValue Schema:");

        txtTTSchema.setEnabled(false);

        btnChooseSchema.setText("Choose...");
        btnChooseSchema.setEnabled(false);
        btnChooseSchema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseSchemaActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblTitle, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(32, 32, 32)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(lblTTSchema, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(lblTTInterface, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, radiobtnTTSchema)
                            .add(lblTTClass, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(txtTTSchema, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                            .add(txtTTClass, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                            .add(txtTTInterface, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(btnChooseSchema)
                            .add(btnChooseInterface)
                            .add(btnChooseClass)))
                    .add(layout.createSequentialGroup()
                        .add(29, 29, 29)
                        .add(radiobtnTTInterfaceClass)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(lblTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(13, 13, 13)
                .add(radiobtnTTInterfaceClass)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtTTInterface, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnChooseInterface)
                    .add(lblTTInterface))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblTTClass)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(txtTTClass, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(btnChooseClass)))
                .add(49, 49, 49)
                .add(radiobtnTTSchema)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtTTSchema, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblTTSchema, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnChooseSchema))
                .addContainerGap(61, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {btnChooseClass, lblTTClass, txtTTClass}, org.jdesktop.layout.GroupLayout.VERTICAL);

        layout.linkSize(new java.awt.Component[] {btnChooseInterface, lblTTInterface, txtTTInterface}, org.jdesktop.layout.GroupLayout.VERTICAL);

    }// </editor-fold>//GEN-END:initComponents
    
    private void radiobtnTTSchemaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radiobtnTTSchemaItemStateChanged
// TODO add your handling code here:
        int curState = evt.getStateChange();
        if(curState == ItemEvent.SELECTED) {
            txtTTSchema.setEnabled(true);
            btnChooseSchema.setEnabled(true);
            
        } else { // if the state of the button is 'DESELECTED'
            txtTTSchema.setEnabled(false);
            btnChooseSchema.setEnabled(false);
            
        }
    }//GEN-LAST:event_radiobtnTTSchemaItemStateChanged
    
    private void radiobtnTTInterfaceClassItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_radiobtnTTInterfaceClassItemStateChanged
// TODO add your handling code here:
        int curState = evt.getStateChange();
        if(curState == ItemEvent.SELECTED) {
            txtTTClass.setEnabled(true);
            btnChooseClass.setEnabled(true);
            txtTTInterface.setEnabled(true);
            btnChooseInterface.setEnabled(true);
        } else { // if the state of the button is 'DESELECTED'
            txtTTClass.setEnabled(false);
            btnChooseClass.setEnabled(false);
            txtTTInterface.setEnabled(false);
            btnChooseInterface.setEnabled(false);
            
        }
    }//GEN-LAST:event_radiobtnTTInterfaceClassItemStateChanged
    
    private void btnChooseSchemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseSchemaActionPerformed
// TODO add your handling code here:
        
        JFileChooser fileChooser = new JFileChooser();
        
        // Disable the all filel filters.
        fileChooser.setAcceptAllFileFilterUsed(false);
        // Set the selection to 'files only' so the user is not allowed to
        // choose the 'FILES'
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        //Set the filter so that the file chooser will display only the xsd files.
        fileChooser.setFileFilter(new CustomFileFilter("Xml Schema Files (*.xsd)", "xsd"));
        
        int retVal = fileChooser.showOpenDialog(this); // Show the 'Open File' dialog.
        if(retVal == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            String schemaName=file.getPath();
            
            // String schemaName= fileChooser.getSelectedFile().getName();
            if (schemaName !=null)
                txtTTSchema.setText(schemaName);
        }
    }//GEN-LAST:event_btnChooseSchemaActionPerformed
    
    private void btnChooseClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseClassActionPerformed
        MainWizardUI ui = (MainWizardUI)
        GDEAppContext.getInstance().getWizardUI();
        ExtendedTTInfoJDialog dialog = new ExtendedTTInfoJDialog(
                ui.getDialog(), true,
                ExtendedTTInfoJDialog.TYPE_CLASS);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        String clsName = dialog.getChosenClassName();
        if(clsName != null)
            txtTTClass.setText(clsName);
    }//GEN-LAST:event_btnChooseClassActionPerformed
    
    /**
     * Event handler for popping up the dialog to choose the files.
     */
    private void btnChooseInterfaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseInterfaceActionPerformed
        MainWizardUI ui = (MainWizardUI)
        GDEAppContext.getInstance().getWizardUI();
        ExtendedTTInfoJDialog dialog = new ExtendedTTInfoJDialog(
                ui.getDialog(), true,
                ExtendedTTInfoJDialog.TYPE_INTERFACE);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        String clsName = dialog.getChosenClassName();
        if(clsName != null)
            txtTTInterface.setText(clsName);
    }//GEN-LAST:event_btnChooseInterfaceActionPerformed
    /**
     * This function can be called to reset the states of the controls in this
     * panel. This function sets the setSelected method of the radio buttons to
     * false. The item state change events associated with the buttons will
     * perform the additional steps required.
     *
     */
    public void clearComponentStates() {
        this.radiobtnTTInterfaceClass.setSelected(false);
        this.radiobtnTTSchema.setSelected(false);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnChooseClass;
    public javax.swing.JButton btnChooseInterface;
    public javax.swing.JButton btnChooseSchema;
    public javax.swing.ButtonGroup btnTTValueGroup;
    public javax.swing.ButtonGroup buttonGroup1;
    public javax.swing.JLabel lblTTClass;
    public javax.swing.JLabel lblTTInterface;
    public javax.swing.JLabel lblTTSchema;
    public javax.swing.JLabel lblTitle;
    public javax.swing.JRadioButton radiobtnTTInterfaceClass;
    public javax.swing.JRadioButton radiobtnTTSchema;
    public javax.swing.JTextField txtTTClass;
    public javax.swing.JTextField txtTTInterface;
    public javax.swing.JTextField txtTTSchema;
    // End of variables declaration//GEN-END:variables
    
}

