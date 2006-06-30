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

import java.util.Iterator;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJar;
import org.netbeans.modules.j2ee.dd.api.ejb.DDProvider;
import org.netbeans.modules.j2ee.dd.api.webservices.Webservices;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.schema2beans.BaseBean;
import org.netbeans.modules.websvc.spi.webservices.WebServicesConstants;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
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
    private org.netbeans.modules.j2ee.api.ejbjar.EjbJar ejbImpl;
    
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
            return DDProvider.getDefault().getDDRoot(getEjbModule().getDeploymentDescriptor());
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
        return getEjbModule().getMetaInf().getFileObject(WebServicesConstants.WEBSERVICES_DD, "xml");
    }
    
    private FileObject getFileObject(String propname) {
        String prop = evaluator.getProperty(propname);
        if (prop != null) {
            return helper.resolveFileObject(prop);
        }
        
        return null;
    }

}
