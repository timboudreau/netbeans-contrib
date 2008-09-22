/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.web;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.autoproject.spi.Cache;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.dd.api.web.WebAppMetadata;
import org.netbeans.modules.j2ee.dd.spi.MetadataUnit;
import org.netbeans.modules.j2ee.dd.spi.web.WebAppMetadataModelFactory;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleImplementation;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

@SuppressWarnings("deprecation") // getJavaSources
class WebModuleImpl implements WebModuleImplementation, J2eeModuleImplementation  {

    private FileObject docBase;
    private String root;
    private MetadataModel<WebAppMetadata> webAppMetadataModel;
    private ClassPathProviderImpl cpProvider;
    private WebModuleProviderImpl provider;
    
    public WebModuleImpl(FileObject docBase, String root, ClassPathProviderImpl cpProvider, WebModuleProviderImpl provider) {
        this.docBase = docBase;
        this.root = root;
        this.provider = provider;
    }

    public FileObject getDocumentBase() {
        return docBase;
    }

    public String getContextPath() {
        return "UNKNOWN"; // XXX: will have to ask user for context
    }

    public String getJ2eePlatformVersion() {
        return WebModule.JAVA_EE_5_LEVEL; // XXX: will have to ask user
    }

    public FileObject getWebInf() {
        return getFile(WebCacheConstants.WEBINF);
    }

    public FileObject getDeploymentDescriptor() {
        return getFile(WebCacheConstants.WEB_XML);
    }
    
    private FileObject getFile(String constantName) {
        String file = Cache.get(root + constantName);
        if (file != null) {
            return FileUtil.toFileObject(new File(file));
        }
        return null;
    }

    public FileObject[] getJavaSources() {
        // is deprecated in base class so perhaps not needed
        throw new UnsupportedOperationException(); // XXX
    }

    public MetadataModel<WebAppMetadata> getMetadataModel() {
        if (webAppMetadataModel == null) {
            File rt = new File(root);
            FileObject ddFO = getDeploymentDescriptor();
            File ddFile = ddFO != null ? FileUtil.toFile(ddFO) : null;
            MetadataUnit metadataUnit = MetadataUnit.create(
                cpProvider.findClassPathImpl(rt, ClassPath.BOOT),
                cpProvider.findClassPathImpl(rt, ClassPath.COMPILE),
                cpProvider.findClassPathImpl(rt, ClassPath.SOURCE),
                // XXX: add listening on deploymentDescriptor
                ddFile);
            webAppMetadataModel = WebAppMetadataModelFactory.createMetadataModel(metadataUnit, true);
        }
        return webAppMetadataModel;
    }

    public String getModuleVersion() {
        WebApp wapp = getWebApp ();
        String version = WebApp.VERSION_2_5;
        if (wapp != null)
            version = wapp.getVersion();
        return version;
    }

    private WebApp getWebApp () {
        try {
            FileObject deploymentDescriptor = getDeploymentDescriptor ();
            if(deploymentDescriptor != null) {
                return DDProvider.getDefault().getDDRoot(deploymentDescriptor);
            }
        } catch (java.io.IOException e) {
            org.openide.ErrorManager.getDefault ().log (e.getLocalizedMessage ());
        }
        return null;
    }

    public Object getModuleType() {
        return J2eeModule.WAR;
    }

    public String getUrl() {
        return ""; // XXX
    }

    public FileObject getArchive() throws IOException {
        return getFile(WebCacheConstants.WAR_FILE);
    }

    public Iterator getArchiveContents() throws IOException {
        // no incremental deployment
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public FileObject getContentDirectory() throws IOException {
        return null;
    }

    public <T> MetadataModel<T> getMetadataModel(Class<T> type) {
        if (type == WebAppMetadata.class) {
            @SuppressWarnings("unchecked") // NOI18N
            MetadataModel<T> model = (MetadataModel<T>)getMetadataModel();
            return model;
        }
        return null;
    }

    public File getResourceDirectory() {
        // XXX: do not have any
        throw new UnsupportedOperationException(); // XXX
    }

    public File getDeploymentConfigurationFile(String name) {
       if (name == null) {
            return null;
        }
        String path = provider.getConfigSupport().getContentRelativePath(name);
        if (path == null) {
            path = name;
        }
        if (path.startsWith("WEB-INF/")) { //NOI18N
            path = path.substring(8); //removing "WEB-INF/"

            FileObject webInf = getWebInf();
            if (webInf != null) {
                return new File(FileUtil.toFile(webInf), path);
            }
        } else {
            FileObject documentBase = getDocumentBase();
            if (documentBase != null) {
                return new File(FileUtil.toFile(documentBase), path);
            }
        }
        return null;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        // TODO
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        // TODO
    }

}
