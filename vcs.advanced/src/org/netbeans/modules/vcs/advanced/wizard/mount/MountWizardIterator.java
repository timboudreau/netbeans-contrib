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

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.cookies.InstanceCookie;
import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;
import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;

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
            Object instance = new org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem();/*null;
            try {
                instance = ((InstanceCookie) templateWizard.getTemplate().getCookie(InstanceCookie.class)).instanceCreate();
            } catch (java.io.IOException ioExc) {
                org.openide.TopManager.getDefault().notifyException(ioExc);
            } catch (ClassNotFoundException cnfExc) {
                org.openide.TopManager.getDefault().notifyException(cnfExc);
            }
            if (instance == null) return ;
                                                              */
            data = new MountWizardData(instance);
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
        CommandLineVcsFileSystem fs = data.getFileSystem();
        String multipleMountPointsStr = (String) fs.getVariablesAsHashtable().get("MULTIPLE_RELATIVE_MOUNT_POINTS");
        //System.out.println("  multipleMountPointsStr = '"+multipleMountPointsStr+"'");
        String[] multipleMountPoints = null;
        if (multipleMountPointsStr != null) {
            multipleMountPoints = VcsUtilities.getQuotedStrings(multipleMountPointsStr);
            fs.setVariables(removeVar("MULTIPLE_RELATIVE_MOUNT_POINTS", fs.getVariables()));
        }
        String config = fs.getConfigFileName();
        GeneralVcsSettings gvs = (GeneralVcsSettings) GeneralVcsSettings.findObject(GeneralVcsSettings.class, true);
        gvs.setDefaultProfile(config);
        if (multipleMountPoints == null || multipleMountPoints.length <= 1) {
            org.openide.loaders.DataObject dobj = fs.createInstanceDataObject(templateWizard.getTargetFolder());
            //org.openide.loaders.DataObject dobj = templateWizard.getTemplate();
            //dobj = dobj.createFromTemplate(templateWizard.getTargetFolder());
            return Collections.singleton(dobj);
        } else {
            Set dobjs = new LinkedHashSet();
            dobjs.add(fs.createInstanceDataObject(templateWizard.getTargetFolder()));
            java.io.File root = new java.io.File(VcsFileSystem.substractRootDir(fs.getRootDirectory().getAbsolutePath(), fs.getRelativeMountPoint()));
            //System.out.println("  root = '"+root+"'");
            for (int i = 1; i < multipleMountPoints.length; i++) {
                CommandLineVcsFileSystem fs1 = new CommandLineVcsFileSystem();
                fs1.readConfiguration(config);
                fs1.setConfigFileName(config);
                try {
                    fs1.setRootDirectory(root);
                } catch (PropertyVetoException pvex) {
                    ErrorManager.getDefault().notify(pvex);
                } catch (IOException ioex) {
                    ErrorManager.getDefault().notify(ioex);
                }
                fs1.setVariables(deepClone(fs.getVariables()));
                try {
                    fs1.setRelativeMountPoint(multipleMountPoints[i]);
                } catch (PropertyVetoException pvex) {
                    ErrorManager.getDefault().notify(pvex);
                } catch (IOException ioex) {
                    ErrorManager.getDefault().notify(ioex);
                }
                //System.out.println("  rmnt set: root = '"+fs1.getRootDirectory()+"', rel mount = '"+fs1.getRelativeMountPoint()+"'");
                dobjs.add(fs1.createInstanceDataObject(templateWizard.getTargetFolder()));
            }
            return dobjs;
        }
    }
    
    private static Vector deepClone(Vector v) {
        int n = v.size();
        Vector v1 = new Vector(n);
        for (int i = 0; i < n; i++) {
            VcsConfigVariable var = (VcsConfigVariable) v.get(i);
            v1.add(var.clone());
        }
        return v1;
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
            new ProfilePanel(0)
        };
        java.awt.Component panel = templateWizard.templateChooser().getComponent();
        this.names = new String[] {
            panel.getName(),
            NbBundle.getMessage(MountWizardIterator.class, "CTL_ProfilePanel"),
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
