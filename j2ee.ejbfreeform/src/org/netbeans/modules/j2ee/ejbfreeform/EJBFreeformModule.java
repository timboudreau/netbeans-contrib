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

package org.netbeans.modules.j2ee.ejbfreeform;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Iterator;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.dd.api.common.RootInterface;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJar;
import org.netbeans.modules.j2ee.dd.api.ejb.DDProvider;
import org.netbeans.modules.j2ee.dd.api.webservices.Webservices;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleImplementation;
import org.netbeans.modules.schema2beans.BaseBean;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

/**
 *
 * @author martin
 */
public class EJBFreeformModule implements J2eeModuleImplementation, PropertyChangeListener {
    
    private Project project;
    private AntProjectHelper helper;
    private PropertyEvaluator evaluator;
    private org.netbeans.modules.j2ee.api.ejbjar.EjbJar ejbImpl;
    private PropertyChangeSupport propertyChangeSupport;
    
    /** Creates a new instance of EJBFreeformModule */
    public EJBFreeformModule(Project project, AntProjectHelper helper, PropertyEvaluator evaluator) {
        this.project = project;
        this.helper = helper;
        this.evaluator = evaluator;
    }
    
    private org.netbeans.modules.j2ee.api.ejbjar.EjbJar getEjbModule () {
        if (ejbImpl == null) {
            org.netbeans.modules.j2ee.api.ejbjar.EjbJar modules[] = org.netbeans.modules.j2ee.api.ejbjar.EjbJar.getEjbJars(project);
            if (modules.length > 0) {
                ejbImpl = modules[0];
            }
        }
        return ejbImpl;
    }
    
    public void setUrl(String url) {
    }
    
    public RootInterface getDeploymentDescriptor(String location) {
        if (J2eeModule.EJBJAR_XML.equals(location)){
            return getEjbJar();
        } else if(J2eeModule.EJBSERVICES_XML.equals(location)){
            return getWebservices();
        }
        return null;
    }
    
    public String getUrl() {
        return "";
    }
    
    public String getModuleVersion() {
        EjbJar ejbJar = getEjbJar();
        return ejbJar.getVersion().toString();
    }
    
    public Object getModuleType() {
        return J2eeModule.EJB;
    }
    
    public FileObject getContentDirectory() throws java.io.IOException {
        return null;
    }
    
    public Iterator getArchiveContents() throws java.io.IOException {
        return null;
    }
    
    public FileObject getArchive() throws java.io.IOException {
        return null;
    }
    
    // private methods
    
    private EjbJar getEjbJar() {
        try {
            return DDProvider.getDefault().getMergedDDRoot(getEjbModule().getMetadataUnit());
        } catch (java.io.IOException e) {
            org.openide.ErrorManager.getDefault().log(e.getLocalizedMessage());
        }
        return null;
    }
    
    private Webservices getWebservices() {
        FileObject wsdd = getDD();
        if(wsdd != null) {
            try {
                return org.netbeans.modules.j2ee.dd.api.webservices.DDProvider.getDefault().getDDRoot(getDD());
            } catch (java.io.IOException e) {
                org.openide.ErrorManager.getDefault().log(e.getLocalizedMessage());
            }
        }
        
        return null;
    }
    
    public FileObject getDD() {
        FileObject metaInfFo = getEjbModule().getMetaInf();
        if (metaInfFo==null) {
            DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message(NbBundle.getMessage(EJBFreeformModule.class,"MSG_ConfigFilesCorrupted"),
                    NotifyDescriptor.ERROR_MESSAGE));
            return null;
        }
        return getEjbModule().getMetaInf().getFileObject("webservices", "xml"); //NOI18N
    }
    
    private FileObject getFileObject(String propname) {
        String prop = evaluator.getProperty(propname);
        if (prop != null) {
            return helper.resolveFileObject(prop);
        }
        
        return null;
    }

    public File getDeploymentConfigurationFile(String name) {
        FileObject moduleFolder = org.netbeans.modules.j2ee.api.ejbjar.EjbJar.getEjbJars(project)[0].getMetaInf();
        File configFolder = FileUtil.toFile(moduleFolder);
        return new File(configFolder, name);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChangeSupport().addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        synchronized (this) {
            if (propertyChangeSupport == null) {
                return;
            }
        }
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    private PropertyChangeSupport getPropertyChangeSupport() {
        EjbJar ejbJar = getEjbJar();
        synchronized (this) {
            if (propertyChangeSupport == null) {
                propertyChangeSupport = new PropertyChangeSupport(this);
                if (ejbJar != null) {
                    PropertyChangeListener l = (PropertyChangeListener) WeakListeners.create(PropertyChangeListener.class, this, ejbJar);
                    ejbJar.addPropertyChangeListener(l);
                }
            }
            return propertyChangeSupport;
        }
    }

    public File getResourceDirectory() {
        return getFile(EjbFreeformProperties.RESOURCE_DIR);
    }
    
    private File getFile(String propname) {
        String prop = evaluator.getProperty(propname);
        if (prop != null) {
            return helper.resolveFile(prop);
        }
        return null;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (EjbFreeformProperties.RESOURCE_DIR.equals(evt.getPropertyName())) {
            String oldValue = (String) evt.getOldValue();
            String newValue = (String) evt.getNewValue();
            getPropertyChangeSupport().firePropertyChange(
                    J2eeModule.PROP_RESOURCE_DIRECTORY, 
                    oldValue == null ? null : new File(oldValue),
                    newValue == null ? null : new File(newValue));
        }
    }
}
