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

package org.netbeans.modules.portalpack.portlets.genericportlets.filetype.filters;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.FilterContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.ResultContext;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.codegen.WebDescriptorGenerator;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.portlets.genericportlets.core.util.NetbeansUtil;
import org.netbeans.spi.java.project.support.ui.templates.JavaTemplates;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;

public final class NewFilterWizardWizardIterator implements WizardDescriptor.InstantiatingIterator {
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private int index;
    
    private WizardDescriptor wizard;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor.Panel packageChooserPanel;
    private boolean notAllowed = false;
    
    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        
        Project project = Templates.getProject(wizard);
        List availablePortlets = new ArrayList();
        List availableFilters =  new ArrayList();
        if(panels == null){
       
        String webInfDir = NetbeansUtil.getWebInfDir(project);
        File portletXml = new File(webInfDir+File.separator+"portlet.xml");
        if(!portletXml.exists()) {
           wizard.putProperty("WizardPanel_errorMessage",
                    "Not a Portlet Application");
           panels =  new WizardDescriptor.Panel[]{
               new ErrorWizardPanel(wizard)
           };
           notAllowed = true;
           String[] steps = createSteps();
           return panels;
        }else{
            
            if(WebDescriptorGenerator.getPortletAppVersion(portletXml.getAbsolutePath()).equals(NetbeanConstants.PORTLET_1_0))
            {
                wizard.putProperty("WizardPanel_errorMessage",
                    "Filter is not allowed in Portlet 1.0 Application");
                panels =  new WizardDescriptor.Panel[]{
                      new ErrorWizardPanel(wizard)
                 };
                notAllowed = true;
                String[] steps = createSteps();
                return panels;
            }
            
            availablePortlets = WebDescriptorGenerator.getPortlets(portletXml);
            availableFilters = WebDescriptorGenerator.getFilters(portletXml);
            
        }
        }
        Sources sources = (Sources)project.getLookup().lookup(Sources.class);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        if (panels == null) {
            packageChooserPanel = JavaTemplates.createPackageChooser(project,groups,new NewFilterWizardWizardPanel1(wizard,availableFilters));
            panels = new WizardDescriptor.Panel[] {
                packageChooserPanel, new NewFilterWizardWizardPanel2(wizard,availablePortlets)
                        
            };
            
            
            String projPath = NetbeansUtil.getProjectAbsolutePath(project);
            logger.log(Level.FINEST,"Proj File: "+projPath);
            String projName = NetbeansUtil.getProjectName(project);
            //    NetbeansUtil.initializePluginContextIfRequired(project,projName);
            
            wizard.putProperty("PROJ_PATH",projPath);
            wizard.putProperty("PROJ_NAME",projName);
            
            
        /*if (panels == null) {
            panels = new WizardDescriptor.Panel[] {
                new NewFilterWizardWizardPanel1(),
                new NewFilterWizardWizardPanel2()
            };*/
            String[] steps = createSteps();
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                if (steps[i] == null) {
                    // Default step name to component name of panel. Mainly
                    // useful for getting the name of the target chooser to
                    // appear in the list of steps.
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }
    
    public Set instantiate() throws IOException {
        Set returnSet = new LinkedHashSet();
        if(notAllowed) return Collections.EMPTY_SET;
        Project project = Templates.getProject(wizard);
        String className = Templates.getTargetName(wizard);
        FileObject pkg = Templates.getTargetFolder(wizard);
        DataFolder targetFolder = DataFolder.findFolder(pkg);
        TemplateWizard template = (TemplateWizard)wizard;
        DataObject doTemplate = template.getTemplate();
        
        String projPath = (String)wizard.getProperty("PROJ_PATH");
        String projName = (String)wizard.getProperty("PROJ_NAME");
        
        logger.log(Level.FINEST,"Class Name::::::::::::::::::: "+className);
        String targetDir = NetbeansUtil.getAbsolutePath(pkg);
        
        logger.log(Level.FINEST,projPath+"   "+projName+"   "+targetDir+"    ");
        NewFilterCreateComponent component = new NewFilterCreateComponent(project);
        
        FilterContext context = (FilterContext)wizard.getProperty("context");
        
         ResultContext retVal = new ResultContext();
        try {
            component.doCreateFilterClass(targetDir, className, context,retVal);
        } catch (Exception ex) {
            logger.log(Level.SEVERE,"Error creating class file",ex);
        }
        
        //open the created class File in editor
        String filePath = (String)retVal.getAttribute(ResultContext.FILE_PATH);
        if(filePath != null && filePath.trim().length() != 0) {
            FileObject fob = FileUtil.toFileObject(new File(filePath));
            if (fob != null) {  //the process succeeded
                returnSet.add(fob);
                DataObject dob = DataObject.find(fob);
                OpenCookie oc = (OpenCookie) dob.getCookie(OpenCookie.class);
                if (oc != null) { //the Image module is installed
                    oc.open();
                }
            }
        }
        
        
        return returnSet;
        
    }
    
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }
    
    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }
    
    public WizardDescriptor.Panel current() {
        return getPanels()[index];
    }
    
    public String name() {
        return index + 1 + ". from " + getPanels().length;
    }
    
    public boolean hasNext() {
        return index < getPanels().length - 1;
    }
    
    public boolean hasPrevious() {
        return index > 0;
    }
    
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }
    
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }
    
    // If nothing unusual changes in the middle of the wizard, simply:
    public void addChangeListener(ChangeListener l) {}
    public void removeChangeListener(ChangeListener l) {}
    
    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then uncomment
    // the following and call when needed: fireChangeEvent();
    /*
    private Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    protected final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }
     */
    
    // You could safely ignore this method. Is is here to keep steps which were
    // there before this wizard was instantiated. It should be better handled
    // by NetBeans Wizard API itself rather than needed to be implemented by a
    // client code.
    private String[] createSteps() {
        String[] beforeSteps = null;
        Object prop = wizard.getProperty("WizardPanel_contentData");
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }
        
        if (beforeSteps == null) {
            beforeSteps = new String[0];
        }
        
        String[] res = new String[(beforeSteps.length - 1) + panels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeSteps.length - 1)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = panels[i - beforeSteps.length + 1].getComponent().getName();
            }
        }
        return res;
    }
    
}
