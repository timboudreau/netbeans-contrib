/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbfreeform;

import java.util.Iterator;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJar;
import org.netbeans.modules.j2ee.dd.api.ejb.DDProvider;
import org.netbeans.modules.j2ee.dd.api.webservices.Webservices;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.ejbjarproject.ui.customizer.EjbJarProjectProperties;
import org.netbeans.modules.schema2beans.BaseBean;
import org.netbeans.modules.websvc.spi.webservices.WebServicesConstants;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author martin
 */
public class EJBFreeformModule implements J2eeModule {
    
    private Project project;
    private AntProjectHelper helper;
    private PropertyEvaluator evaluator;
    private EJBProjectNature.ProxyEjbJarImplementation ejbImpl;
    
    /** Creates a new instance of EJBFreeformModule */
    public EJBFreeformModule(Project project, AntProjectHelper helper, PropertyEvaluator evaluator) {
        this.project = project;
        this.helper = helper;
        this.evaluator = evaluator;
        this.ejbImpl = new EJBProjectNature.ProxyEjbJarImplementation(project, helper, evaluator);
    }
    
    public void setUrl(String url) {
    }
    
    public BaseBean getDeploymentDescriptor(String location) {
        if (J2eeModule.EJBJAR_XML.equals(location)){
            EjbJar webApp = getEjbJar();
            if (webApp != null) {
                //PENDING find a better way to get the BB from WApp and remove the HACK from DDProvider!!
                return DDProvider.getDefault().getBaseBean(webApp);
            }
        } else if(J2eeModule.EJBSERVICES_XML.equals(location)){
            Webservices webServices = getWebservices();
            if(webServices != null){
                return DDProvider.getDefault().getBaseBean(webServices);
            }
        }
        return null;
    }
    
    public void removeVersionListener(org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule.VersionListener listener) {
    }
    
    public void addVersionListener(org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule.VersionListener listener) {
    }
    
    public String getUrl() {
        EditableProperties ep =  helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
        String name = ep.getProperty(EjbJarProjectProperties.JAR_NAME);
        return name == null ? "" : ("/"+name); //NOI18N
    }
    
    public String getModuleVersion() {
        EjbJar ejbJar = getEjbJar();
        return ejbJar.getVersion().toString();
    }
    
    public Object getModuleType() {
        return J2eeModule.EJB;
    }
    
    public org.openide.filesystems.FileObject getContentDirectory() throws java.io.IOException {
        return getFileObject(EjbJarProjectProperties.BUILD_CLASSES_DIR); //NOI18N
    }
    
    public java.util.Iterator getArchiveContents() throws java.io.IOException {
        return new IT(getContentDirectory());
    }
    
    public org.openide.filesystems.FileObject getArchive() throws java.io.IOException {
        return getFileObject(EjbJarProjectProperties.DIST_JAR); //NOI18N
    }
    
    // private methods
    
    private EjbJar getEjbJar() {
        try {
            return DDProvider.getDefault().getDDRoot(ejbImpl.getDeploymentDescriptor());
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
        FileObject metaInfFo = ejbImpl.getMetaInf();
        if (metaInfFo==null) {
            DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message(NbBundle.getMessage(EJBFreeformModule.class,"MSG_ConfigFilesCorrupted"),
                    NotifyDescriptor.ERROR_MESSAGE));
            return null;
        }
        return ejbImpl.getMetaInf().getFileObject(WebServicesConstants.WEBSERVICES_DD, "xml");
    }
    
    private FileObject getFileObject(String propname) {
        String prop = helper.getStandardPropertyEvaluator().getProperty(propname);
        if (prop != null) {
            return helper.resolveFileObject(prop);
        }
        
        return null;
    }

    private static class IT implements Iterator {
        java.util.Enumeration ch;
        FileObject root;
        
        private IT(FileObject f) {
            this.ch = f.getChildren(true);
            this.root = f;
        }
        
        public boolean hasNext() {
            return ch.hasMoreElements();
        }
        
        public Object next() {
            FileObject f = (FileObject) ch.nextElement();
            return new FSRootRE(root, f);
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }

    private static final class FSRootRE implements J2eeModule.RootedEntry {
        FileObject f;
        FileObject root;
        
        FSRootRE(FileObject root, FileObject f) {
            this.f = f;
            this.root = root;
        }
        
        public FileObject getFileObject() {
            return f;
        }
        
        public String getRelativePath() {
            return FileUtil.getRelativePath(root, f);
        }
    }
}
