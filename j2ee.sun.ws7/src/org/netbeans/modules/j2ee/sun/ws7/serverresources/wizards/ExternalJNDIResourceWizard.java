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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 * made subject to such option by the copyright holder.
 */

/*
 * ExternalJNDIResourceWizard.java
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
public class ExternalJNDIResourceWizard extends AbstractResourceWizard{
    
    private static final String DATAFILE = "org/netbeans/modules/j2ee/sun/ws7/serverresources/wizards/ExternalJNDIWizard.xml";  //NOI18N
    private Wizard wizardInfo;
    private ResourceConfigHelper helper;
    
    private transient WizardDescriptor wizard;    
    private transient String[] steps;   
    
    private static Project project;    
     
    public static ExternalJNDIResourceWizard create(){
        return new ExternalJNDIResourceWizard();
    }
    public void initialize(WizardDescriptor wizard){
        wizardInfo = getWizardInfo(DATAFILE);
        this.helper = new ResourceConfigHelperHolder().getExternalJndiResourceHelper();
        
        this.wizard = wizard;
        wizard.putProperty("NewFileWizard_Title", 
                           NbBundle.getMessage(ExternalJNDIResourceWizard.class, "Templates/SunWS70Resources/ExternalJNDI_Resource")); //NOI18N
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
            WS70ResourceUtils.saveExternalJndiResourceDatatoXml(this.helper.getData());
        }catch (Exception ex){
            System.out.println("Error in instantiate of ExternalResourceWizard ");
        }
        return java.util.Collections.EMPTY_SET;    }
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
        return NbBundle.getMessage(ExternalJNDIResourceWizard.class, "Templates/SunWS70Resources/ExternalJNDI_Resource"); //NOI18N
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
            NbBundle.getMessage(CustomResourceWizard.class, "TITLE_GeneralAttributes_EXTERNAL"), //NOI18N
            NbBundle.getMessage(CustomResourceWizard.class, "TITLE_UserProps_EXTERNAL") //NOI18N            
        };
    }    
}
