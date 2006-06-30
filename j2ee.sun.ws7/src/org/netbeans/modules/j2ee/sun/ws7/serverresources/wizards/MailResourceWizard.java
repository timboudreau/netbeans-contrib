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

/*
 * MailResourceWizard.java
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
public class MailResourceWizard extends AbstractResourceWizard{
    private static final String DATAFILE = "org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/MailWizard.xml";  //NOI18N
    
    private Wizard wizardInfo;
    private ResourceConfigHelper helper;    
    private transient WizardDescriptor wizard;    
    private transient String[] steps;
    private static Project project;    
    
    public static MailResourceWizard create(){
        return new MailResourceWizard();
    }  
    
    public void initialize(WizardDescriptor wizard){
        wizardInfo = getWizardInfo(DATAFILE);
        this.helper = new ResourceConfigHelperHolder().getMailHelper();
        
        this.wizard = wizard;
        wizard.putProperty("NewFileWizard_Title", 
                           NbBundle.getMessage(MailResourceWizard.class, "Templates/SunWS70Resources/JavaMail_Resource")); //NOI18N
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
            WS70ResourceUtils.saveMailResourceDatatoXml(this.helper.getData());
        }catch (Exception ex){
            //System.out.println("Error in instantiate ");
        }
        return java.util.Collections.EMPTY_SET;
        
    }
    public void uninitialize(WizardDescriptor wizard){
        
    }

        
 
    public String name(){
        return NbBundle.getMessage(MailResourceWizard.class, "Templates/SunWS70Resources/JavaMail_Resource"); //NOI18N
    }
    
    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[] {
            new CommonGeneralFinishPanel(helper, wizardInfo, new String[] {"general", "advanced"}) //NOI18N
            
        };
    }
    
    private String[] createSteps() {    
        return new String[] {
            WS70WizardConstants.__FirstStepChoose,
            NbBundle.getMessage(MailResourceWizard.class, "TITLE_GeneralAttributes_MAIL"), //NOI18N            
        };
    }
    
}
