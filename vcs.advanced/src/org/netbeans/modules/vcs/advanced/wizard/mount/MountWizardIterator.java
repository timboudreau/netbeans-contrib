/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
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
        return currentIndex < panels.size() - 1;
    }
    
    public void initialize(org.openide.loaders.TemplateWizard templateWizard) {
        if (panels == null) {
            Object instance = new org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem();/*null;                                                              */
            data = new MountWizardData(instance);
            data.addProfileChangeListener(this);
            setupPanels(templateWizard);
            listenerList = new javax.swing.event.EventListenerList();            
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
        autocmd = Variables.expand(data.getFileSystem().getVariablesAsHashtable(), autocmd, false);
        System.err.println("cmd: "+autocmd);
        if((autocmd != null)&&(autocmd.length() > 0)){
            final CommandSupport supp = data.getFileSystem().getCommandSupport(autocmd);
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                     Command cmd = supp.createCommand();
                     cmd.execute();
                }
            });
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
        data = null;
        listenerList = null;
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
        
        templateWizard.putProperty("WizardPanel_contentData", names);
        templateWizard.putProperty("WizardPanel_contentSelectedIndex", new Integer(1)); // NOI18N
        this.currentIndex = 0;// = this.relativeIndex_ = 0;
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent evt) {        
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
    
    private void setContentData () {
        String[] namesAr = (String[])names.toArray(new String[0]);
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
