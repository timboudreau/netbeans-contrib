/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.j2ee.geronimo2.config;

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
import org.netbeans.modules.j2ee.geronimo2.config.gen.WebApp;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;


/**
 *
 * @author Max Sauer
 */
public class GeWarModuleConfiguration extends GeModuleConfiguration
        implements PropertyChangeListener, DeploymentPlanConfiguration, ContextRootConfiguration {

    //Deployment descriptor file
    private File geronimoWebFile;
    //geronimo-web.xml object
    private WebApp webApp;

    public GeWarModuleConfiguration(J2eeModule j2eeModule) {
        super(j2eeModule);
        geronimoWebFile = j2eeModule.getDeploymentConfigurationFile("WEB-INF/geronimo-web.xml");

        //init of geronimo-web.xml object
        getWebApp();
        
        if (deploymentDescriptorDO == null) {
            try {
                deploymentDescriptorDO = deploymentDescriptorDO.find(FileUtil.toFileObject(geronimoWebFile));
                deploymentDescriptorDO.addPropertyChangeListener(this);
            } catch(DataObjectNotFoundException donfe) {
                Exceptions.printStackTrace(donfe);
            }
        }

    }

    private WebApp genereateWebApp() {
        WebApp app = new WebApp();
        app.setContextRoot("");

        return app;
    }

    /**
     * Creates graph from geronimo-web.xml, if it not exists
     * 
     * @return generated bean graph
     */
    private WebApp getWebApp() {
        if (webApp == null) {
            try {
                if (geronimoWebFile.exists()) {
                    // load configuration if already exists
                    try {
                        webApp = WebApp.createGraph(geronimoWebFile);
                    } catch (IOException ioe) {
                        Exceptions.printStackTrace(ioe);
                    } catch (RuntimeException re) {
                    // geronimo-web.xml is not parseable, do nothing
                    }
                } else {
                    // create geronimo-web.xml if it does not exist yet
                    webApp = genereateWebApp();
                    GeResourceConfigurationUtil.writefile(geronimoWebFile, webApp);
                }
            } catch (ConfigurationException ce) {
                Exceptions.printStackTrace(ce);
            }
        }

        return webApp;
    }


    // ------------ Implements/Overrides

    //DeploymentPlanConfiguration
    public void save(OutputStream os) throws ConfigurationException {
        WebApp webApp = getWebApp();
        
        if (null == webApp)
            throw new ConfigurationException("Cannot read configuration, it is probably in an inconsistent state."); // NOI18N
        
        try {
            webApp.write(os);
        } catch (IOException ioe) {
            throw new ConfigurationException(ioe.getLocalizedMessage());
        }
    }

    //PropertyChangeListener
    public void propertyChange(PropertyChangeEvent evt) {
        if ((evt.getPropertyName() == DataObject.PROP_MODIFIED || evt.getPropertyName().equals(DataObject.PROP_MODIFIED))
                && evt.getNewValue() == Boolean.FALSE) {
            // dataobject has been modified, WebApp graph is out of sync
            webApp = null;
        }
    }

    //ContextRootConfiguration
    
    public String getContextRoot() throws ConfigurationException {
        // Get geronimo-web.xml object
        WebApp webApp = getWebApp();
        
        // Check for null
        if (null == webApp)
            throw new ConfigurationException("geronimo-web.xml is not parseable, cannot read the context path value."); // NOI18N
        
        // Return context root
        return webApp.getContextRoot();
    }

    public void setContextRoot(String contextPath) throws ConfigurationException {
        if (!isCorrectCP(contextPath)) {
            String ctxRoot = contextPath;
            java.util.StringTokenizer tok = new java.util.StringTokenizer(contextPath,"/"); //NOI18N
            StringBuffer buf = new StringBuffer(); //NOI18N
            
            while (tok.hasMoreTokens()) {
                buf.append("/"+tok.nextToken()); //NOI18N
            }
            
            ctxRoot = buf.toString();
            
            NotifyDescriptor desc = new NotifyDescriptor.Message(
                    NbBundle.getMessage(GeWarModuleConfiguration.class, "MSG_invalidCP", contextPath),
                    NotifyDescriptor.Message.INFORMATION_MESSAGE);
            
            DialogDisplayer.getDefault().notify(desc);
            contextPath = ctxRoot;
        }
        
        final String newContextPath = contextPath;
        
        modifyWebApp(new WebAppModifier() {
            public void modify(WebApp webApp) {
                webApp.setContextRoot(newContextPath);
            }
        });
    }
    
    //from webLogic
    /**
     * Perform webApp changes defined by the webApp modifier. Update editor
     * content and save changes, if appropriate.
     * 
     * @param modifier
     */
    private void modifyWebApp(WebAppModifier modifier) throws ConfigurationException {
        assert deploymentDescriptorDO != null : "DataObject has not been initialized yet"; // NIO18N
        try {
            // get the document
            EditorCookie editor = (EditorCookie) deploymentDescriptorDO.getCookie(EditorCookie.class);
            StyledDocument doc = editor.getDocument();
            if (doc == null) {
                doc = editor.openDocument();
            }
            
            // get the up-to-date model
            WebApp newWebApp = null;
            try {
                // try to create a graph from the editor content
                byte[] docString = doc.getText(0, doc.getLength()).getBytes();
                newWebApp = WebApp.createGraph(new ByteArrayInputStream(docString));
            } catch (RuntimeException e) {
                WebApp oldWebApp = getWebApp();
                if (oldWebApp == null) {
                    // neither the old graph is parseable, there is not much we can do here
                    // TODO: should we notify the user?
                    throw new ConfigurationException("Configuration data are not parseable cannot perform changes."); // NOI18N
                }
                // current editor content is not parseable, ask whether to override or not
                NotifyDescriptor notDesc = new NotifyDescriptor.Confirmation(
                        NbBundle.getMessage(GeWarModuleConfiguration.class, "MSG_geronimoXmlNotValid"),
                        NotifyDescriptor.OK_CANCEL_OPTION);
                Object result = DialogDisplayer.getDefault().notify(notDesc);
                if (result == NotifyDescriptor.CANCEL_OPTION) {
                    // keep the old content
                    return;
                }
                // use the old graph
                newWebApp = oldWebApp;
            }
            
            // perform changes
            modifier.modify(newWebApp);
            
            // save, if appropriate
            boolean modified = deploymentDescriptorDO.isModified();
            GeResourceConfigurationUtil.replaceDocument(doc, newWebApp);
            if (!modified) {
                SaveCookie cookie = (SaveCookie) deploymentDescriptorDO.getCookie(SaveCookie.class);
                cookie.save();
            }
            webApp = newWebApp;
        } catch (BadLocationException ble) {
            throw new ConfigurationException(ble.getMessage(), ble);
        } catch (IOException ioe) {
            throw new ConfigurationException(ioe.getMessage(), ioe);
        }
    }
    
    private boolean isCorrectCP(String contextPath) {
        boolean correct=true;
        if (!contextPath.equals("") && !contextPath.startsWith("/")) correct=false; //NOI18N
        else if (contextPath.endsWith("/")) correct=false; //NOI18N
        else if (contextPath.indexOf("//")>=0) correct=false; //NOI18N
        return correct;
    }
    
    // Helper interfaces
    
    private interface WebAppModifier {
        void modify(WebApp context);
    }
}
