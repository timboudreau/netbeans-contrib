/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */

package org.netbeans.modules.erd.wizard;



import java.awt.Component;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.event.*;
import org.netbeans.api.db.explorer.ConnectionListener;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.db.explorer.JDBCDriver;
import org.netbeans.api.db.explorer.JDBCDriverManager;
import org.netbeans.api.db.explorer.support.DatabaseExplorerUIs;
import org.netbeans.api.project.Project;
import org.netbeans.modules.dbschema.SchemaElement;
import org.netbeans.modules.j2ee.common.DatasourceUIHelper;
import org.netbeans.modules.j2ee.deployment.common.api.Datasource;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author Andrei Badea
 */
public class DataSourcePanelUI extends javax.swing.JPanel implements ListDataListener{

    static final long serialVersionUID = 5364628520334696421L;

    private ArrayList dbconns;
    private ArrayList list=new ArrayList();
    private WizardContext data;
    private Node dbNode;
    private Node[] drvNodes;
    private DBSchemaManager dbschemaManager=new DBSchemaManager();
    private ChangeSupport changeSupport = new ChangeSupport(this);
    
    
    public DataSourcePanelUI() {
        
        dbconns = new ArrayList();
        

        putClientProperty("WizardPanel_contentSelectedIndex", new Integer(1)); //NOI18N
        setName(bundle.getString("DataSource")); //NOI18N

        initComponents ();
        resize();
        initAccessibility();

        
        
        
        
    }
    
    
    public void initialize(Project project, FileObject fileTarget){
      //  if (Util.isSupportedJavaEEVersion(project) || Util.isEjb21Module(project)) {
       //     initializeWithDatasources(project);
       // } else {
            initializeWithDbConnections();
       // }
        
        initializeWithSchema();
       
        datasourceComboBox.setSelectedItem(null);
        
        
        
        DBSchemaUISupport.connect(dbschemaComboBox, project, fileTarget);
        if (dbschemaComboBox.getItemCount() <= 0) {
           
          datasourceRadioButton.setSelected(true);
        } else {
           dbschemaRadioButton.setSelected(true);
        }
        try{ 
          updateSourceSchema();
        } catch(Exception e){
            
        }  
    }

    
    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }
    
    private void initializeWithDatasources(Project project) {
        org.openide.awt.Mnemonics.setLocalizedText(datasourceRadioButton, "Datasource");
        J2eeModuleProvider provider = (J2eeModuleProvider)project.getLookup().lookup(J2eeModuleProvider.class);
        DatasourceUIHelper.connect(provider, datasourceComboBox);
    }
    
    
    private void initializeWithDbConnections() {
        org.openide.awt.Mnemonics.setLocalizedText(datasourceRadioButton,bundle.getString("Connection"));
        DatabaseExplorerUIs.connect(datasourceComboBox, ConnectionManager.getDefault());
    }
    
    private void initializeWithSchema() {
        org.openide.awt.Mnemonics.setLocalizedText(dbschemaRadioButton,bundle.getString("DatabaseSchema"));
        DatabaseExplorerUIs.connect(dbschemaComboBox, ConnectionManager.getDefault());
    }
    
    public   javax.swing.JComboBox getComboBox(){
        return datasourceComboBox;
    }

    
    private void initAccessibility() {
        this.getAccessibleContext().setAccessibleDescription(bundle.getString("ACS_ConnectionPanelA11yDesc"));  // NOI18N
        datasourceComboBox.getAccessibleContext().setAccessibleName(bundle.getString("ACS_ExistingConnectionA11yName"));  // NOI18N
        datasourceComboBox.getAccessibleContext().setAccessibleDescription(bundle.getString("ACS_ExistingConnectionA11yDesc"));  // NOI18N
    }
    
    private void resize() {
        int width = 0;
        int height = 300;
        if (width < 675)
            width = 675;
        if (height < 390)
            height = 390;
        java.awt.Dimension dim = new java.awt.Dimension(width, height);
        setMinimumSize(dim);
        setPreferredSize(dim);
    }
    
                    

                                     

                 
    private javax.swing.JTextArea descriptionTextArea;
  
                     

    private final ResourceBundle bundle = NbBundle.getBundle("org.netbeans.modules.erd.wizard.Bundle"); //NOI18N

    public boolean isValid() {
       boolean datasource=datasourceComboBox.getSelectedItem() instanceof DatabaseConnection && datasourceRadioButton.isSelected();
       boolean schema=  dbschemaComboBox.getSelectedItem() instanceof FileObject && dbschemaRadioButton.isSelected();
       return datasource | schema; 
        
    }

    public void intervalAdded(final javax.swing.event.ListDataEvent p1) {
        fireChange(this);
    }

    public void intervalRemoved(final javax.swing.event.ListDataEvent p1) {
        fireChange(this);
    }

    public void contentsChanged(final javax.swing.event.ListDataEvent p1) {
        fireChange(this);
    }

    public void initData() {
     //   data.setExistingConn(true);
        Object selectedItem = datasourceComboBox.getSelectedItem();
        if (selectedItem instanceof DatabaseConnection) {
       //     data.setDatabaseConnection((DatabaseConnection)selectedItem);
        }
    }

    public void fireChange (Object source) {
        ChangeEvent event = new ChangeEvent(source);  
       changeSupport.fireChange(event);
    }
    
    private boolean isConnection;
    private String url;
    public void setUrl(String url,boolean isConnection){
        this.url=url;
        this.isConnection=isConnection;
    }
    
    public String getUrl(){
        return url;
    }
    
    public boolean isConnection(){
        return isConnection;
    }
    
    private void updateSourceSchema()  {
   
        DatabaseConnection dbconn=null;
        
        if (datasourceRadioButton.isSelected()) {
            Object item = datasourceComboBox.getSelectedItem();
            
            if (item instanceof DatabaseConnection) {
               dbconn = (DatabaseConnection)item;
                //try {
                    
                    //data.setSchemaElement(dbschemaManager.getSchemaElement(dbconn));
                    setUrl(dbconn.getDatabaseURL(),true);       
              //  } catch (SQLException e) {
               //     notify("ERR_DatabaseError");
               // }
            }else
            if (item instanceof Datasource) {
                Datasource ds = (Datasource)item;
                String drvClass = ds.getDriverClassName();
                if (drvClass == null) {
                    notify("ERR_NoDriverClassName");
                }
                else {
                    JDBCDriver[] drivers = JDBCDriverManager.getDefault().getDrivers(drvClass);
                    if (drivers.length == 0) {
                        notify("ERR_NoDriverError");
                    }
                    else {
                        JDBCDriver driver = JDBCDriverManager.getDefault().getDrivers(drvClass)[0];
                        DatabaseConnection tmpconn = DatabaseConnection.create(driver, ds.getUrl(), ds.getUsername(), null, ds.getPassword(), false);
                        dbconn = ConnectionManager.getDefault().getConnection(tmpconn.getName());
                        if (dbconn == null) {
                            try {
                                ConnectionManager.getDefault().addConnection(tmpconn);
                                dbconn = tmpconn;
                            } catch (DatabaseException ex) {
                                notify("ERR_CannotAddConnection");
                            }
                        }
                        if (dbconn != null) {
                          //  try {
                               // data.setSchemaElement(dbschemaManager.getSchemaElement(dbconn));
                           // } catch (SQLException e) {
                            //    notify("ERR_DatabaseError");
                           // }
                        }
                    }
                }
            } 
        } else if (dbschemaRadioButton.isSelected()) {
            FileObject dbschemaFile = (FileObject)dbschemaComboBox.getSelectedItem();
            
            if (dbschemaFile != null) { 
               String path=dbschemaFile.getPath();
                 
               
               setUrl(path,false);
            }
        }
        fireChange(this);
    }
   
     private static void notify(String msgName) {
        NotifyDescriptor nd = new NotifyDescriptor.Message(
                NbBundle.getMessage(DataSourcePanelUI.class, msgName), 
                NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
    }
  

  
  

 

    
    private void initComponents() {
        schemaSource = new javax.swing.ButtonGroup();
        datasourceRadioButton = new javax.swing.JRadioButton();
        datasourceComboBox = new javax.swing.JComboBox();
        dbschemaRadioButton = new javax.swing.JRadioButton();
        dbschemaComboBox = new javax.swing.JComboBox();

        schemaSource.add(datasourceRadioButton);
        datasourceRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                datasourceRadioButtonItemStateChanged(evt);
            }
        });

        datasourceComboBox.setEnabled(false);
        datasourceComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                datasourceComboBoxActionPerformed(evt);
            }
        });

        schemaSource.add(dbschemaRadioButton);
        dbschemaRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dbschemaRadioButtonItemStateChanged(evt);
            }
        });

        dbschemaComboBox.setEnabled(false);
        dbschemaComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbschemaComboBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datasourceRadioButton)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(dbschemaRadioButton)
                .add(67, 67, 67)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dbschemaComboBox, 0, 342, Short.MAX_VALUE)
                    .add(datasourceComboBox, 0, 342, Short.MAX_VALUE))
                .add(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(datasourceRadioButton)
                    .add(datasourceComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dbschemaRadioButton)
                    .add(dbschemaComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(351, Short.MAX_VALUE))
        );
    }

    private void dbschemaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {                                                 
       updateSourceSchema();
    }

    private void dbschemaRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {                                                     
       updateSourceSchemaComboBoxes();
        updateSourceSchema();
    }

    private void updateSourceSchemaComboBoxes() {
        datasourceComboBox.setEnabled(datasourceRadioButton.isSelected());
        dbschemaComboBox.setEnabled(dbschemaRadioButton.isSelected());
    }
    
    private void datasourceComboBoxActionPerformed(java.awt.event.ActionEvent evt) {                                                   
     
         updateSourceSchema();
    }

    private void datasourceRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {                                                       
       updateSourceSchemaComboBoxes();
        updateSourceSchema();
    }


  
    private javax.swing.JComboBox datasourceComboBox;
    private javax.swing.JRadioButton datasourceRadioButton;
    private javax.swing.JComboBox dbschemaComboBox;
    private javax.swing.JRadioButton dbschemaRadioButton;
    private javax.swing.ButtonGroup schemaSource;
    

    
    public static final class DataSourcePanel implements WizardDescriptor.Panel, ChangeListener {

        private DataSourcePanelUI component;
        private boolean componentInitialized;

        private WizardDescriptor wizardDescriptor;

        private ChangeSupport changeSupport = new ChangeSupport(this);

        public Component getComponent() {
            return getTypedComponent();
        }

        private DataSourcePanelUI getTypedComponent() {
            if (component == null) {
                component = new DataSourcePanelUI();
                component.addChangeListener(this);
            }
            return component;
        }

        public HelpCtx getHelp() {
            return new HelpCtx(DataSourcePanelUI.class);
        }

        public void addChangeListener(ChangeListener listener) {
            changeSupport.addChangeListener(listener);
        }

        public void removeChangeListener(ChangeListener listener) {
            changeSupport.removeChangeListener(listener);
        }

        public void readSettings(Object settings) {
            wizardDescriptor = (WizardDescriptor)settings;
            if (!componentInitialized) {
                componentInitialized = true;

                Project project = Templates.getProject(wizardDescriptor);
                WizardContext wizardContext = ERDWizard.getWizardContext(wizardDescriptor);
                DataObject configFilesFolder = wizardContext.getTargetFolder();
                
                getTypedComponent().initialize(project, configFilesFolder.getPrimaryFile());
            }
        }

        public boolean isValid() {
           
            return getTypedComponent().isValid();
        }

        public void storeSettings(Object settings) {
            WizardDescriptor wiz = (WizardDescriptor)settings;
            Object buttonPressed = wiz.getValue();
            if (buttonPressed.equals(WizardDescriptor.NEXT_OPTION) ||
                    buttonPressed.equals(WizardDescriptor.FINISH_OPTION)) {
                WizardContext wizardContext = ERDWizard.getWizardContext(wizardDescriptor);
                 
               wizardContext.setUrl(getTypedComponent().getUrl());
               wizardContext.setIsConnection(getTypedComponent().isConnection());
                
            }
        }

        public void stateChanged(ChangeEvent event) {
            changeSupport.fireChange(event);
        }

        private void setErrorMessage(String errorMessage) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", errorMessage); // NOI18N
        }
    }
    
   
}
