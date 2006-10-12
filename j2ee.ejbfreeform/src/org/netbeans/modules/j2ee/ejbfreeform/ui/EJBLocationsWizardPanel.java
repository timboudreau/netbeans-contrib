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

package org.netbeans.modules.j2ee.ejbfreeform.ui;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.ant.freeform.spi.support.NewFreeformProjectSupport;
import org.netbeans.modules.java.freeform.spi.support.NewJavaFreeformProjectSupport;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;


/**
 * @author  Radko Najman
 */
public class EJBLocationsWizardPanel implements WizardDescriptor.Panel, ChangeListener {

    private EJBLocationsPanel component;
    private WizardDescriptor wizardDescriptor;
    private File baseFolder;

    public EJBLocationsWizardPanel() {
        getComponent().setName(NbBundle.getMessage(NewEJBFreeformProjectWizardIterator.class, "TXT_NewEJBFreeformProjectWizardIterator_EJBSources")); // NOI18N
    }

    public Component getComponent() {
        if (component == null) {
            component = new EJBLocationsPanel(this);
        }
        return component;
    }

    public HelpCtx getHelp() {
        return new HelpCtx( EJBLocationsWizardPanel.class );
    }

    public boolean isValid() {
        getComponent();
        return component.valid(wizardDescriptor);
    }

    private final Set/*<ChangeListener>*/ listeners = new HashSet(1);
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
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(ev);
        }
    }

    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        wizardDescriptor.putProperty("NewProjectWizard_Title", component.getClientProperty("NewProjectWizard_Title")); // NOI18N

        //guess EJB modules well-known locations and preset them
        File baseFolder = (File)wizardDescriptor.getProperty(NewFreeformProjectSupport.PROP_PROJECT_LOCATION);
        File nbProjectFolder = (File)wizardDescriptor.getProperty(NewFreeformProjectSupport.PROP_PROJECT_FOLDER);
        final String configFiles;
        final String srcPackages;
        if(baseFolder.equals(this.baseFolder)) {
            configFiles = component.getConfigFilesLocation().getAbsolutePath();
            srcPackages = component.getSrcPackagesLocation().getAbsolutePath();
        } else {
            this.baseFolder = baseFolder;
            FileObject fo = FileUtil.toFileObject(baseFolder);
            if (fo != null) {
                configFiles = guessConfigFiles(fo);
                srcPackages = guessJavaRoot(fo);
            } else {
                configFiles = ""; // NOI18N
                srcPackages = ""; // NOI18N
            }
        }
        component.setFolders(baseFolder, nbProjectFolder);
        component.setConfigFilesField(configFiles);
        component.setSrcPackages(srcPackages);
    }

    public void storeSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        wizardDescriptor.putProperty(NewEJBFreeformProjectWizardIterator.PROP_EJB_EJBMODULES, component.getEJBModules());
        
        List l = component.getJavaSrcFolder();
        wizardDescriptor.putProperty(NewJavaFreeformProjectSupport.PROP_EXTRA_JAVA_SOURCE_FOLDERS, l);
        
        wizardDescriptor.putProperty(NewEJBFreeformProjectWizardIterator.PROP_EJB_SOURCE_FOLDERS, component.getEJBSrcFolder());
        wizardDescriptor.putProperty(NewEJBFreeformProjectWizardIterator.PROP_EJB_RESOURCE_FOLDERS, component.getResourcesFolder());
        wizardDescriptor.putProperty(NewEJBFreeformProjectWizardIterator.SERVER_ID, component.getSelectedServerID());
        wizardDescriptor.putProperty(NewEJBFreeformProjectWizardIterator.J2EE_LEVEL, component.getSelectedJ2eeSpec());
        wizardDescriptor.putProperty("NewProjectWizard_Title", null); // NOI18N
    }

    // TODO: ma154696: change this algorithm from web pages to ejb DDs
    private String guessConfigFiles (FileObject dir) {
        Enumeration ch = dir.getChildren (true);
        while (ch.hasMoreElements ()) {
            FileObject f = (FileObject) ch.nextElement ();
            if (f.getNameExt().equals ("ejb-jar.xml")) { // NOI18N
                return FileUtil.toFile(f.getParent()).getAbsolutePath();
            }
        }
        return ""; // NOI18N
    }

    private String guessJavaRoot (FileObject dir) {
        Enumeration ch = dir.getChildren (true);
        try {
            while (ch.hasMoreElements ()) {
                FileObject f = (FileObject) ch.nextElement ();
                if (f.getExt ().equals ("java") && !f.isFolder()) { // NOI18N
                    String pckg = guessPackageName (f);
                    String pkgPath = f.getParent ().getPath ();
                    if (pckg != null && pkgPath.endsWith (pckg.replace ('.', '/'))) {
                        String rootName = pkgPath.substring (0, pkgPath.length () - pckg.length ());
                        return FileUtil.toFile(f.getFileSystem().findResource(rootName)).getAbsolutePath();
                    }
                }
            }
        } catch (FileStateInvalidException fsie) {
            ErrorManager.getDefault ().notify (ErrorManager.INFORMATIONAL, fsie);
        }
        return ""; // NOI18N
    }

    private String guessPackageName(FileObject f) {
        java.io.Reader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(f.getInputStream (), "utf-8")); //NOI18N
            boolean noPackage = false;
            for (;;) {
                String line = ((BufferedReader) r).readLine();
                if (line == null) {
                    if (noPackage)
                        return "";
                    else
                        break;
                }
                line = line.trim();
                //try to find package
                if (line.trim().startsWith("package")) { // NOI18N
                    int idx = line.indexOf(";");  // NOI18N
                    if (idx >= 0)
                        return line.substring("package".length(), idx).trim(); // NOI18N
                }
                //an easy check if it is class
                if (line.indexOf("class") != -1)
                    noPackage = true;
            }
        } catch (java.io.IOException ioe) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
        } finally {
            try {
                if (r != null)
                    r.close ();
            } catch (java.io.IOException ioe) {
                // ignore this
            }
        }
        
        return null;
    }

    public void stateChanged(ChangeEvent e) {
        fireChangeEvent();
    }
    
}
