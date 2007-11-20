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

package org.netbeans.modules.j2ee.oc4j.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.ContextRootConfiguration;
import org.netbeans.modules.j2ee.deployment.plugins.spi.config.DeploymentPlanConfiguration;
import org.netbeans.modules.j2ee.oc4j.config.gen.OrionWebApp;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Web module deployment configuration handles creation and updating of the
 * orion-web.xml configuration file.
 *
 * @author Michal Mocnak
 */
public class OC4JWarModuleConfiguration extends OC4JModuleConfiguration
        implements ContextRootConfiguration, DeploymentPlanConfiguration, PropertyChangeListener {
    
    private static final String DEFAULT_CHARSET = "utf-8"; // NOI18N
    
    // deployment descriptor file
    private File orionWebFile;
    
    // orion-web.xml object
    private OrionWebApp orionWebApp;
    
    /**
     * Creates a new instance of WarDeploymentConfiguration
     */
    public OC4JWarModuleConfiguration(J2eeModule j2eeModule) {
        super(j2eeModule);
        
        orionWebFile = j2eeModule.getDeploymentConfigurationFile("WEB-INF/orion-web.xml");
        
        // Initialization of the orion-web.xml
        getOrionWebApp();
        
        if (deploymentDescriptorDO == null) {
            try {
                deploymentDescriptorDO = deploymentDescriptorDO.find(FileUtil.toFileObject(orionWebFile));
                deploymentDescriptorDO.addPropertyChangeListener(this);
            } catch(DataObjectNotFoundException donfe) {
                Exceptions.printStackTrace(donfe);
            }
        }
    }
    
    // ContextRootConfiguration Implementation
    
    /**
     * Return context root.
     *
     * @return context root or null, if the file is not parseable.
     */
    public String getContextRoot() throws ConfigurationException {
        // Get orion-web.xml object
        OrionWebApp orion = getOrionWebApp();
        
        // Check for null
        if (null == orion)
            throw new ConfigurationException("orion-web.xml is not parseable, cannot read the context path value."); // NOI18N
        
        // Return context root
        return orion.getContextRoot();
    }
    
    /**
     * Set context path.
     */
    public void setContextRoot(String contextPath) throws ConfigurationException {
        // TODO: this contextPath fix code will be removed, as soon as it will
        // be moved to the web project
        if (!isCorrectCP(contextPath)) {
            String ctxRoot = contextPath;
            java.util.StringTokenizer tok = new java.util.StringTokenizer(contextPath,"/"); //NOI18N
            StringBuffer buf = new StringBuffer(); //NOI18N
            
            while (tok.hasMoreTokens()) {
                buf.append("/"+tok.nextToken()); //NOI18N
            }
            
            ctxRoot = buf.toString();
            
            NotifyDescriptor desc = new NotifyDescriptor.Message(
                    NbBundle.getMessage(OC4JWarModuleConfiguration.class, "MSG_invalidCP", contextPath),
                    NotifyDescriptor.Message.INFORMATION_MESSAGE);
            
            DialogDisplayer.getDefault().notify(desc);
            contextPath = ctxRoot;
        }
        
        final String newContextPath = contextPath;
        
        modifyOrionWebApp(new OrionWebAppModifier() {
            public void modify(OrionWebApp orionWebApp) {
                orionWebApp.setContextRoot(newContextPath);
            }
        });
    }
    
    // DeploymentPlanConfiguration Implementation
    
    public void save(OutputStream os) throws ConfigurationException {
        OrionWebApp orionWebApp = getOrionWebApp();
        
        if (null == orionWebApp)
            throw new ConfigurationException("Cannot read configuration, it is probably in an inconsistent state."); // NOI18N
        
        try {
            orionWebApp.write(os);
        } catch (IOException ioe) {
            throw new ConfigurationException(ioe.getLocalizedMessage());
        }
    }
    
    // PropertyChangeListener Implementation
    
    /**
     * Listen to orion-web.xml document changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == DataObject.PROP_MODIFIED &&
                evt.getNewValue() == Boolean.FALSE) {
            // dataobject has been modified, orionWebApp graph is out of sync
            synchronized (this) {
                orionWebApp = null;
            }
        }
    }
    
    // Helper methods
    
    /**
     * Return OrionWebApp graph. If it was not created yet, load it from the file
     * and cache it. If the file does not exist, generate it.
     *
     * @return OrionWebApp graph or null if the orion-web.xml file is not parseable.
     */
    private synchronized OrionWebApp getOrionWebApp() {
        if (orionWebApp == null) {
            try {
                if (orionWebFile.exists()) {
                    // load configuration if already exists
                    try {
                        orionWebApp = OrionWebApp.createGraph(orionWebFile);
                    } catch (IOException ioe) {
                        Exceptions.printStackTrace(ioe);
                    } catch (RuntimeException re) {
                        // orion-web.xml is not parseable, do nothing
                    }
                } else {
                    // create orion-web.xml if it does not exist yet
                    orionWebApp = genereateOrionWebApp();
                    OC4JResourceConfigurationHelper.writefile(orionWebFile, orionWebApp);
                }
            } catch (ConfigurationException ce) {
                Exceptions.printStackTrace(ce);
            }
        }
        
        return orionWebApp;
    }
    
    /**
     * Perform webLogicWebApp changes defined by the webLogicWebApp modifier. Update editor
     * content and save changes, if appropriate.
     *
     * @param modifier
     */
    private void modifyOrionWebApp(OrionWebAppModifier modifier) throws ConfigurationException {
        assert deploymentDescriptorDO != null : "DataObject has not been initialized yet"; // NIO18N
        try {
            // get the document
            EditorCookie editor = (EditorCookie) deploymentDescriptorDO.getCookie(EditorCookie.class);
            StyledDocument doc = editor.getDocument();
            if (doc == null) {
                doc = editor.openDocument();
            }
            
            // get the up-to-date model
            OrionWebApp newOrionWebApp = null;
            try {
                // try to create a graph from the editor content
                byte[] docString = doc.getText(0, doc.getLength()).getBytes();
                newOrionWebApp = OrionWebApp.createGraph(new ByteArrayInputStream(docString));
            } catch (RuntimeException e) {
                OrionWebApp oldOrionWebApp = getOrionWebApp();
                if (oldOrionWebApp == null) {
                    // neither the old graph is parseable, there is not much we can do here
                    // TODO: should we notify the user?
                    throw new ConfigurationException("Configuration data are not parseable cannot perform changes."); // NOI18N
                }
                // current editor content is not parseable, ask whether to override or not
                NotifyDescriptor notDesc = new NotifyDescriptor.Confirmation(
                        NbBundle.getMessage(OC4JWarModuleConfiguration.class, "MSG_orionXmlNotValid"),
                        NotifyDescriptor.OK_CANCEL_OPTION);
                Object result = DialogDisplayer.getDefault().notify(notDesc);
                if (result == NotifyDescriptor.CANCEL_OPTION) {
                    // keep the old content
                    return;
                }
                // use the old graph
                newOrionWebApp = oldOrionWebApp;
            }
            
            // perform changes
            modifier.modify(newOrionWebApp);
            
            // save, if appropriate
            boolean modified = deploymentDescriptorDO.isModified();
            OC4JResourceConfigurationHelper.replaceDocument(doc, newOrionWebApp);
            if (!modified) {
                SaveCookie cookie = (SaveCookie) deploymentDescriptorDO.getCookie(SaveCookie.class);
                cookie.save();
            }
            synchronized (this) {
                orionWebApp = newOrionWebApp;
            }
        } catch (BadLocationException ble) {
            throw new ConfigurationException(ble.getMessage(), ble);
        } catch (IOException ioe) {
            throw new ConfigurationException(ioe.getMessage(), ioe);
        }
    }
    
    /**
     * Genereate Context graph.
     */
    private OrionWebApp genereateOrionWebApp() {
        OrionWebApp orionWebApp = new OrionWebApp();
        orionWebApp.setContextRoot(""); // NOI18N
        orionWebApp.setDevelopment("true"); // NOI18N
        orionWebApp.setDefaultCharset(DEFAULT_CHARSET);
        return orionWebApp;
    }
    
    // TODO: this contextPath fix code will be removed, as soon as it will
    // be moved to the web project
    private boolean isCorrectCP(String contextPath) {
        boolean correct=true;
        if (!contextPath.equals("") && !contextPath.startsWith("/")) correct=false; //NOI18N
        else if (contextPath.endsWith("/")) correct=false; //NOI18N
        else if (contextPath.indexOf("//")>=0) correct=false; //NOI18N
        return correct;
    }
    
    // Helper interfaces
    
    private interface OrionWebAppModifier {
        void modify(OrionWebApp context);
    }
}