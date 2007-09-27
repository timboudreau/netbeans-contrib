/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
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

import java.io.IOException;
import java.util.*;

import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.dbschema.SchemaElement;
import org.netbeans.modules.erd.io.DocumentSave;
import org.netbeans.modules.erd.io.ERDContext;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.NotifyDescriptor;



import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import sun.net.www.protocol.doc.DocURLConnection;

/** Iterator implementation which can iterate through two
* panels which forms dbschema template wizard
*/
public class ERDWizard implements TemplateWizard.Iterator {

    static final long serialVersionUID = 9197272899287477324L;

    ResourceBundle bundle = NbBundle.getBundle("org.netbeans.modules.erd.wizard.Bundle"); //NOI18N
    private final String defaultName = bundle.getString("DefaultSchemaName");
    private WizardDescriptor.Panel panels[];
    private static String panelNames[];
    private static final int PANEL_COUNT = 2;
    private int panelIndex;
    private static ERDWizard instance;
    private TemplateWizard templateWizard;
    private boolean guiInitialized;
    private WizardContext wizardContext;
    public static String WIZARD_CONTEXT="wizard_context";

    public ERDWizard() {
        super();
        panelIndex = 0;
    }

    public static synchronized ERDWizard singleton() {
        if(instance == null)
            instance = new ERDWizard();

        return instance;
    }

    public Set instantiate(TemplateWizard wiz) throws IOException {
         
        
         
         String temp_new_erd_file = wiz.getTargetName();
         DataFolder folder = wiz.getTargetFolder();
         final FileObject fo = folder.getPrimaryFile();
         if (temp_new_erd_file == null || temp_new_erd_file.equals("")) //NOI18N
                temp_new_erd_file = FileUtil.findFreeFileName(fo, defaultName, "dbschema"); //NOI18N
            
         final String erd_file_name = temp_new_erd_file;
         FileObject fo1 = fo.createData(erd_file_name, "erd");
         ERDContext context=null;
         if(wizardContext.isConnection()){
             String url=wizardContext.getUrl();
            context= new ERDContext(fo1,url,ERDContext.DATASOURCETYPE.CONNECTION);        
         }
         else {
           
             String url=wizardContext.getUrl();
             context=new ERDContext(fo1,url,ERDContext.DATASOURCETYPE.SCHEMA);  
            
            
         }
         DocumentSave.save(context);
        return null;
    }
    
    
    public static WizardContext getWizardContext(WizardDescriptor wizardDescriptor) {
        return (WizardContext)wizardDescriptor.getProperty(WIZARD_CONTEXT);
    }
    
    public org.openide.WizardDescriptor.Panel current() {
        return panels[panelIndex];
    }

    public String name() {
        return panelNames[panelIndex];
    }

    public boolean hasNext() {
        boolean b= panelIndex < PANEL_COUNT - 1;
        return b;
    }

    public boolean hasPrevious() {
        return panelIndex > 0;
    }

    public void nextPanel() {
        if (panelIndex == 1) {//== connection panel
            //((DBSchemaConnectionPanel) panels[1].getComponent()).initData();
            
        }
        
        panelIndex++;
    }

    public void previousPanel() {
        panelIndex--;
    }

    public void addChangeListener(ChangeListener l) {
    }

    public void removeChangeListener(ChangeListener l) {
    }

    public void initialize(TemplateWizard templateWizard) {
        this.templateWizard = templateWizard;
        wizardContext = new WizardContext();
        setTargetFolder();
        String[] prop = (String[]) templateWizard.getProperty("WizardPanel_contentData"); // NOI18N
        String[] stepsNames = new String[] {
            templateWizard.targetChooser().getClass().toString().trim().equalsIgnoreCase("class org.openide.loaders.TemplateWizard2") ? bundle.getString("TargetLocation") :
                prop[0],
                bundle.getString("TargetLocation"),
                bundle.getString("DataSource")
               
        };
        templateWizard.putProperty("WizardPanel_autoWizardStyle", Boolean.TRUE); //NOI18N
        templateWizard.putProperty("WizardPanel_contentDisplayed", Boolean.TRUE); //NOI18N
        templateWizard.putProperty("WizardPanel_contentNumbered", Boolean.TRUE); //NOI18N
        templateWizard.putProperty("WizardPanel_contentData", stepsNames); //NOI18N
        
        if(!guiInitialized) {
            initialize();
           
            templateWizard.putProperty(WIZARD_CONTEXT, wizardContext);
            try {
                 wizardContext.setTargetFolder(templateWizard.getTargetFolder());
            } catch(Exception e){
                throw new RuntimeException(e.getMessage());
            }
            panels = new WizardDescriptor.Panel[PANEL_COUNT];            
            
            
            org.openide.WizardDescriptor.Panel targetPanel =templateWizard.targetChooser();

            java.awt.Component panel = targetPanel.getComponent();
            if (panel instanceof javax.swing.JComponent) {
                ((javax.swing.JComponent) panel).putClientProperty("WizardPanel_contentData", stepsNames); //NOI18N
                ((javax.swing.JComponent) panel).putClientProperty("WizardPanel_contentSelectedIndex", new Integer(0)); //NOI18N
            }
            
            panels[0] = targetPanel;
            panels[1] = new DataSourcePanelUI.DataSourcePanel();
        }
        
        panelIndex = 0;
    }
    
    
    
    
    public void uninitialize(TemplateWizard wiz) {
        if (wiz.getValue() == NotifyDescriptor.CANCEL_OPTION)
           // ((DataSourceUI) panels[2].getComponent()).uninit();
        
        panels = null;
        wizardContext = null;
        guiInitialized = false;
    }

    protected void initialize() {
        if(panelNames == null) {
            panelNames = new String[PANEL_COUNT];
            panelNames[0] = ""; //NOI18N
            panelNames[1] = ""; //NOI18N
        }
    }

    private void setTargetFolder() {
        FileObject targetFO;
        try {
            DataFolder target = templateWizard.getTargetFolder();
            targetFO = target.getPrimaryFile();
        } catch (IOException e) {
            targetFO = null;
        }
        
        Project targetProject = Templates.getProject(templateWizard);
        if (targetProject != null) {
            FileObject projectDir = targetProject.getProjectDirectory();
            if (targetFO == null || targetFO.equals(projectDir)) {
                FileObject newTargetFO = projectDir.getFileObject("src/conf"); // NOI18N
                if (newTargetFO == null || !newTargetFO.isValid()) {
                    newTargetFO = projectDir.getFileObject("src/META-INF"); // NOI18N
                    if (newTargetFO == null || !newTargetFO.isValid()) {
                        newTargetFO = projectDir.getFileObject("src"); // NOI18N
                        if (newTargetFO == null || !newTargetFO.isValid()) {
                            return;
                        }
                    }
                }
                wizardContext.setProjectDir(projectDir.getPath());
                DataFolder newTarget = DataFolder.findFolder(newTargetFO);
                templateWizard.setTargetFolder(newTarget);
                
            }
        }
    }
}
