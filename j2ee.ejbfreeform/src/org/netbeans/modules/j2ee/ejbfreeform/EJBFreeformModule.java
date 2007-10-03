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

package org.netbeans.modules.j2ee.ejbfreeform;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Iterator;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJarMetadata;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleImplementation;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.NotImplementedException;

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
  
// TODO MetadataModel: rewrite when MetadataModel is ready
//    public RootInterface getDeploymentDescriptor(String location) {
//        if (J2eeModule.EJBJAR_XML.equals(location)){
//            return getEjbJar();
//        } else if(J2eeModule.EJBSERVICES_XML.equals(location)){
//            return getWebservices();
//        }
//        return null;
//    }
//    
    public <T> MetadataModel<T> getMetadataModel(Class<T> type) {
        if (type == EjbJarMetadata.class) {
//            return (MetadataModel<T>) project.getAPIEjbJar().getMetadataModel();
            throw new NotImplementedException();
        } else {
        // TODO MetadataModel: rewrite when MetadataModel<WebservicesMode> is ready
        // } else if (type == WebservicesMetadata.class) {
        //     return getWebservices();
            throw new NotImplementedException();
        }
//        return null;
    }
    
    public String getUrl() {
        return "";
    }
    
    public String getModuleVersion() {
        // TODO MetadataModel: rewrite when MetadataModel is ready
//        EjbJar ejbJar = getEjbJar();
//        return ejbJar.getVersion().toString();
        throw new NotImplementedException();
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
    
// TODO MetadataModel: rewrite when MetadataModel is ready
//    private EjbJar getEjbJar() {
//        try {
//            return DDProvider.getDefault().getDDRoot(getEjbModule().getDeploymentDescriptor());
//        } catch (java.io.IOException e) {
//            org.openide.ErrorManager.getDefault().log(e.getLocalizedMessage());
//        }
//        return null;
//    }
//    
//    private Webservices getWebservices() {
//        FileObject wsdd = getDD();
//        if(wsdd != null) {
//            try {
//                return org.netbeans.modules.j2ee.dd.api.webservices.DDProvider.getDefault().getDDRoot(getDD());
//            } catch (java.io.IOException e) {
//                org.openide.ErrorManager.getDefault().log(e.getLocalizedMessage());
//            }
//        }
//        
//        return null;
//    }
    
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
// TODO MetadataModel: rewrite when MetadataModel is ready
//        EjbJar ejbJar = getEjbJar();
        synchronized (this) {
            if (propertyChangeSupport == null) {
                propertyChangeSupport = new PropertyChangeSupport(this);
//                if (ejbJar != null) {
//                    PropertyChangeListener l = (PropertyChangeListener) WeakListeners.create(PropertyChangeListener.class, this, ejbJar);
//                    ejbJar.addPropertyChangeListener(l);
//                }
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
