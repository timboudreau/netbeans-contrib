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

package org.netbeans.modules.portalpack.servers.core.ui;

import org.netbeans.modules.portalpack.servers.core.api.PSConfigPanelManager;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.portalpack.servers.core.PSPluginProperties;
import org.netbeans.modules.portalpack.servers.core.WizardPropertyReader;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;


/**
 *
 * @author Satya
 */
public class PSInstantiatingIterator implements WizardDescriptor.InstantiatingIterator {
    
    private Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private final static String PROP_DISPLAY_NAME = "ServInstWizard_displayName"; // NOI18N
    private int index;
    private WizardDescriptor wizard;
    private WizardDescriptor.Panel[] panels;
    private String psVersion;
    private String uriPrefix;
    private PSConfigPanelManager configManager;
    
    public PSInstantiatingIterator(String psVersion,String uriPrefix,PSConfigPanelManager configPanelManager)
    {
        this.psVersion = psVersion;
        this.uriPrefix = uriPrefix;
        this.configManager = configPanelManager;
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
    
    public String name() {
        return NbBundle.getMessage(PSInstantiatingIterator.class, "MSG_InstallerName");
    }
   
    
    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        
        if (panels == null) {
            panels = configManager.getInstallPanels(psVersion);
        }
        return panels;
    }
    
    public Set instantiate() throws IOException {
        Set result = new HashSet();
        String displayName = getDisplayName();
        WizardPropertyReader wr = new WizardPropertyReader(wizard);
        String suffix = "local";
        if(wr.isRemote())
            suffix = "remote";
        
        String serverInstallationHome = wr.getServerHome();
        
        if(!wr.isRemote() && serverInstallationHome != null 
                          && serverInstallationHome.trim().length() != 0) {
            
            suffix += ":" + serverInstallationHome;
            
        }
        
        String url         = uriPrefix+":"+suffix+":"+ wr.getHost()+ ":"+ wr.getPort(); // NOI18N
        String username    = wr.getAdminUser(); // NOI18N
        String password    = wr.getAdminPassWord(); // NOI18N
        try {
            PSConfigObject.getPSConfigObject(url).populate(wr);
            InstanceProperties ip = InstanceProperties.createInstanceProperties(
                    url, username, password, displayName);
            
            ip.setProperty(PSPluginProperties.PS_INSTALL_HOME,pshome);
            ip.setProperty(PSPluginProperties.PS_HOST,host);
            ip.setProperty(PSPluginProperties.PS_PORT,port);
           
            result.add(ip);           
            
            PSConfigObject.getPSConfigObject(url).save(wr);

        } catch (Exception ex) {
            logger.log(Level.SEVERE,"error",ex);
            
            PSConfigObject.getPSConfigObject(url).destroy();
                    
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    NbBundle.getMessage(PSInstantiatingIterator.class, "MSG_CreateFailed", displayName),
                    NotifyDescriptor.ERROR_MESSAGE));
        }
        return result;
    }
       
    private String pshome;
    private String host;
    private String port;
    
    public void setPSHome(String pHome)
    {
        pshome = pHome;
    }
    public void setHost(String h)
    {
        host = h;
        
    }
    
    public void setPort(String p)
    {
        port = p;
    }

    private String getDisplayName() {
        return (String)wizard.getProperty(PROP_DISPLAY_NAME);
    }    
}