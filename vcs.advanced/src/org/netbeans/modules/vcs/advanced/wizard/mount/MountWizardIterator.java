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

package org.netbeans.modules.vcs.advanced.wizard.mount;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;
import org.netbeans.api.vcs.commands.Command;

import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.cookies.InstanceCookie;
import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.registry.FSRegistry;
import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;
import org.netbeans.modules.vcs.advanced.VcsCustomizer;
import org.netbeans.modules.vcs.advanced.recognizer.CommandLineVcsFileSystemInfo;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.spi.vcs.commands.CommandSupport;
import org.openide.util.RequestProcessor;

/**
 * The wizard iterator to mount the Generic VCS file system.
 *
 * @author  Martin Entlicher
 */
public class MountWizardIterator extends Object implements TemplateWizard.Iterator, PropertyChangeListener {

    private static MountWizardIterator instance;
    /* defines command executed after filesystem instantiation */
    public static final String VAR_AUTO_EXEC = "AUTO_EXEC";     //NOI18N
    //private WizardDescriptor.Panel[] panels;
    private RangeArrayList panels;
   // String[] names;
    RangeArrayList names;
    private javax.swing.event.EventListenerList listenerList;
    private int currentIndex;    
    private MountWizardData data;    
    private TemplateWizard templateWizard;
    private boolean isNext = true;
    
    private static final long serialVersionUID = 6804299241178632175L;
    
    /** Creates new MountWizardIterator */
    public MountWizardIterator() {
    }

    /** Returns JavaWizardIterator singleton instance. This method is used
     * for constructing the instance from filesystem.attributes.
     */
    public static synchronized MountWizardIterator singleton() {
        if (instance == null) {
            instance = new MountWizardIterator();
        }
        return instance;
    }
    
    public boolean hasNext() {
        return ((currentIndex < panels.size() - 1)&&(isNext));
    }
    
    public void initialize(org.openide.loaders.TemplateWizard templateWizard) {
        if (panels == null) {
            Object instance = new org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem();/*null;                                                              */
            listenerList = new javax.swing.event.EventListenerList();            
            data = new MountWizardData(instance);
            setupPanels(templateWizard);
            //data.addProfileChangeListener(this);
            data.addPropertyChangeListener(this);
        }
    }
    
    private static Vector removeVar(String varName, Vector vars) {
        for (int i = vars.size() - 1; i >= 0; i--) {
            VcsConfigVariable var = (VcsConfigVariable) vars.get(i);
            if (varName.equals(var.getName())) {
                vars.remove(i);
                break;
            }
        }
        return vars;
    }
    
    public java.util.Set instantiate(org.openide.loaders.TemplateWizard templateWizard) throws java.io.IOException {
        CommandLineVcsFileSystemInfo info = new CommandLineVcsFileSystemInfo(data.getFileSystem());
        FSRegistry registry = FSRegistry.getDefault();               
        registry.register(info);
        String autocmd = (String) data.getFileSystem().getVariablesAsHashtable().get(VAR_AUTO_EXEC);
        if (autocmd != null) {
            autocmd = Variables.expand(data.getFileSystem().getVariablesAsHashtable(), autocmd, false);
            //System.err.println("cmd: "+autocmd);
            if (autocmd.length() > 0) {
                final CommandSupport supp = data.getFileSystem().getCommandSupport(autocmd);
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                         Command cmd = supp.createCommand();
                         cmd.execute();
                    }
                });
            }
        }
        return Collections.EMPTY_SET;
    }
    
    public void previousPanel() {
        currentIndex--;
        //setContentData();
    }
    
    public void uninitialize(org.openide.loaders.TemplateWizard templateWizard) {
        panels = null;
        names = null;
        data.removePropertyChangeListener(this);
        data = null;
        listenerList = null;
        this.templateWizard = null;
    }
    
    public void removeChangeListener(javax.swing.event.ChangeListener changeListener) {
        if (listenerList != null) listenerList.remove(javax.swing.event.ChangeListener.class, changeListener);
    }
    
    public void addChangeListener(javax.swing.event.ChangeListener changeListener) {
        if (listenerList != null) listenerList.add(javax.swing.event.ChangeListener.class, changeListener);
    }
    
    public String name() {
        return (String)names.get(currentIndex);
    }
    
    public void nextPanel() {
        currentIndex++;
        //setContentData();
    }
    
    public boolean hasPrevious() {
        return currentIndex > 0;
    }
    
    public MountWizardData getData() {
        return data;
    }
    
    public org.openide.WizardDescriptor.Panel current() {
        return (WizardDescriptor.Panel)panels.get(this.currentIndex);
    }
    
    private void setupPanels(TemplateWizard templateWizard) { 
        this.templateWizard = templateWizard;
        this.panels = new RangeArrayList();
        this.names = new RangeArrayList();
        this.panels.add(new ProfilePanel(0));
        java.awt.Component panel = templateWizard.templateChooser().getComponent();
        this.names.add(panel.getName());
        this.names.add(NbBundle.getMessage(MountWizardIterator.class, "CTL_ProfilePanel"));
        VcsCustomizer customizer = data.getCustomizer();
        int num = customizer.getNumConfigPanels();
        for(int i = 1; i < num; i++){
            ProfilePanel pp = new ProfilePanel(i);
            panels.add(pp);
            names.add(customizer.getConfigPanelName(i));
        }
        
        templateWizard.putProperty("WizardPanel_contentData", names);
        templateWizard.putProperty("WizardPanel_contentSelectedIndex", new Integer(1)); // NOI18N
        this.currentIndex = 0;// = this.relativeIndex_ = 0;
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent evt) { 
       // System.err.println("porpoerty change Iterator:"+evt.getPropertyName());
        if(evt.getPropertyName().equals(VcsCustomizer.PROP_PROFILE_SELECTION_CHANGED)){            
            VcsCustomizer customizer = data.getCustomizer();
            if(panels.size() > 1){
                panels.removeRange(1, panels.size());
                names.removeRange(2, names.size());
            }
            int num = customizer.getNumConfigPanels();
            for(int i = 1; i < num; i++){
                ProfilePanel panel = new ProfilePanel(i);
                panels.add(panel);
                names.add(customizer.getConfigPanelName(i));
            }
            setContentData();
        }
        if(evt.getPropertyName().equals(VcsCustomizer.PROP_IS_FINISH_ENABLED_CHANGED)){            
            Boolean isFinishEnabled = (Boolean) evt.getNewValue();
            ProfilePanel panel = (ProfilePanel)current();
            if (isFinishEnabled.booleanValue()) {
                //System.err.println("isFinishEnabled - true");                                 
                panel.setFinish(true);                
                isNext = false;
                panel.fireChange();                
            } else {
                //System.err.println("isFinishEnabled - false");
                panel.setFinish(false);
                isNext = true;
                panel.fireChange();           
            }
            setContentData();
        }
        if (evt.getPropertyName().equals(VcsFileSystem.PROP_VARIABLES)) {
            ProfilePanel panel = (ProfilePanel) current();
            panel.fireChange(); // To validate the panel.
        }
    }
    
    private void setContentData () {
        String[] namesAr = (String[])names.toArray(new String[0]);
        if(!isNext){
            String[] newNames = new String[currentIndex+2];
            System.arraycopy(namesAr, 0, newNames, 0, newNames.length);
            namesAr = newNames;
        }
        
        //javax.swing.JPanel panel = (javax.swing.JPanel) this.panels.get(this.currentIndex);
        templateWizard.putProperty("WizardPanel_contentData", namesAr);
        //templateWizard.putProperty("WizardPanel_contentSelectedIndex", new Integer(1)); // NOI18N
        //panel.putClientProperty (CvsWizardData.SELECTED_INDEX, new Integer(this.relativeIndex_));
        //panel.putClientProperty (data.CONTENT_DATA, names);        
    }
    
    public class RangeArrayList extends ArrayList{
        RangeArrayList(){
            super();
        }
        public void removeRange(int fromIndex,int toIndex){
            super.removeRange(fromIndex,toIndex);
        }
    }
     
    
}
