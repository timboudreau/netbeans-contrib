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

import java.util.Collections;

import org.openide.WizardDescriptor;
import org.openide.cookies.InstanceCookie;
import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;

/**
 * The wizard iterator to mount the Generic VCS file system.
 *
 * @author  Martin Entlicher
 */
public class MountWizardIterator extends Object implements TemplateWizard.Iterator {

    private static MountWizardIterator instance;
    
    private WizardDescriptor.Panel[] panels;
    String[] names;
    private javax.swing.event.EventListenerList listenerList;
    private int currentIndex;
    //private int relativeIndex_;
    private MountWizardData data;
    
    private static final long serialVersionUID = 6804299241178632175L;
    
    //private TemplateWizard 

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
        return currentIndex < panels.length - 1;
    }
    
    public void initialize(org.openide.loaders.TemplateWizard templateWizard) {
        if (panels == null) {
            Object instance = null;
            try {
                instance = ((InstanceCookie) templateWizard.getTemplate().getCookie(InstanceCookie.class)).instanceCreate();
            } catch (java.io.IOException ioExc) {
                org.openide.TopManager.getDefault().notifyException(ioExc);
            } catch (ClassNotFoundException cnfExc) {
                org.openide.TopManager.getDefault().notifyException(cnfExc);
            }
            if (instance == null) return ;
            data = new MountWizardData(instance);
            setupPanels(templateWizard);
            listenerList = new javax.swing.event.EventListenerList();
        }
    }
    
    public java.util.Set instantiate(org.openide.loaders.TemplateWizard templateWizard) throws java.io.IOException {
        org.openide.loaders.DataObject dobj = data.getFileSystem().createInstanceDataObject(templateWizard.getTargetFolder());
        //org.openide.loaders.DataObject dobj = templateWizard.getTemplate();
        //dobj = dobj.createFromTemplate(templateWizard.getTargetFolder());
        return Collections.singleton(dobj);
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
        listenerList.remove(javax.swing.event.ChangeListener.class, changeListener);
    }
    
    public void addChangeListener(javax.swing.event.ChangeListener changeListener) {
        listenerList.add(javax.swing.event.ChangeListener.class, changeListener);
    }
    
    public String name() {
        return names[currentIndex];
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
        return this.panels[this.currentIndex];
    }
    
    private void setupPanels(TemplateWizard templateWizard) {
        this.panels = new WizardDescriptor.Panel[] {
            //templateWizard.targetChooser(),
            new ProfilePanel(),
            new AdvancedPanel(),
            new EnvironmentPanel(),
        };
        java.awt.Component panel = templateWizard.templateChooser().getComponent();
        this.names = new String[] {
            panel.getName(),
            NbBundle.getMessage(MountWizardIterator.class, "CTL_ProfilePanel"),
            NbBundle.getMessage(MountWizardIterator.class, "CTL_AdvancedPanel"),
            NbBundle.getMessage(MountWizardIterator.class, "CTL_EnvironmentPanel")
        };
        //System.out.println("target chooser panel = "+panel);
        //System.out.println(" panel is JComponent = "+(panel instanceof javax.swing.JComponent));
        templateWizard.putProperty ("WizardPanel_contentData", names);
        templateWizard.putProperty ("WizardPanel_contentSelectedIndex", new Integer (1)); // NOI18N
        this.currentIndex = 0;// = this.relativeIndex_ = 0;
    }
    
    /*
    private void setContentData () {
        ((javax.swing.JPanel)this.panels[this.currentIndex]).putClientProperty (
            "WizardPanel_contentSelectedIndex", new Integer(this.currentIndex));
    }
     */
    
}
