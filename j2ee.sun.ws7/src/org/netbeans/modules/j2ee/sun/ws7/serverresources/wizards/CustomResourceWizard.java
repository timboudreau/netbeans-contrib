/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * CustomResourceWizard.java
 *
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards;

import java.awt.Component;
import java.util.Set;
import javax.swing.JComponent;
import java.io.InputStream;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openide.util.NbBundle;
import org.openide.WizardDescriptor;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;

import org.netbeans.modules.j2ee.sun.sunresources.beans.Wizard;

import org.netbeans.modules.j2ee.sun.ws7.serverresources.beans.WS70ResourceUtils;

/**
 *
 * @author Mukesh Garg
 */
public class CustomResourceWizard extends AbstractResourceWizard{
    
    private static final String DATAFILE = "org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/CustomWizard.xml";  //NOI18N
    private Wizard wizardInfo;
    private ResourceConfigHelper helper;
    
    private transient WizardDescriptor wizard;
    //private transient WizardDescriptor.Panel[] panels;
    private transient String[] steps;
   // private transient int index;
    
    private static Project project;        
    
   
    public static CustomResourceWizard create(){
        return new CustomResourceWizard();
    }
    public void initialize(WizardDescriptor wizard){
        wizardInfo = getWizardInfo(DATAFILE);
        this.helper = new ResourceConfigHelperHolder().getCustomResourceHelper();
        
        this.wizard = wizard;
        wizard.putProperty("NewFileWizard_Title", 
                           NbBundle.getMessage(CustomResourceWizard.class, "Templates/SunWS70Resources/Custom_Resource")); //NOI18N
        index = 0;
                
        project = Templates.getProject(wizard);
        
        panels = createPanels();
        // Make sure list of steps is accurate.
        steps = createSteps();
        
        try{
            FileObject pkgLocation = project.getProjectDirectory();
            if (pkgLocation != null) {
                this.helper.getData().setTargetFileObject(pkgLocation);
            }
        }catch (Exception ex){
           //Unable to get project location
            ex.printStackTrace();
        }
        
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent)c;
                // Step #.
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i)); // NOI18N
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps); // NOI18N
            }
        }                               
    }
    public Set instantiate(){
        try{
            WS70ResourceUtils.saveCustomResourceDatatoXml(this.helper.getData());
        }catch (Exception ex){
            System.out.println("Error in instantiate of CustomResourceWizard");
        }
        return java.util.Collections.EMPTY_SET;    
    }
    public void uninitialize(WizardDescriptor wizard){
        
    }
    private Wizard getWizardInfo(){
        try{
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(DATAFILE);
            this.wizardInfo = Wizard.createGraph(in);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return this.wizardInfo;
    }
        
 
    public String name(){
        return NbBundle.getMessage(CustomResourceWizard.class, "Templates/SunWS70Resources/Custom_Resource"); //NOI18N
    }
   private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[] {
            new CommonGeneralFinishPanel(helper, wizardInfo, new String[] {"general"}), //NOI18N
            new CommonPropertyPanel(helper, wizardInfo)
        };
    }
    
    private String[] createSteps() {
        return new String[] {
            WS70WizardConstants.__FirstStepChoose,
            NbBundle.getMessage(CustomResourceWizard.class, "TITLE_GeneralAttributes_CUSTOM"), //NOI18N
            NbBundle.getMessage(CustomResourceWizard.class, "TITLE_UserProps_CUSTOM") //NOI18N            
        };
    }        
}
