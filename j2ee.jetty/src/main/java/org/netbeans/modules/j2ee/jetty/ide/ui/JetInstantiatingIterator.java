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

package org.netbeans.modules.j2ee.jetty.ide.ui;

import org.netbeans.modules.j2ee.jetty.ide.JetPluginProperties;
import org.netbeans.modules.j2ee.jetty.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceCreationException;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 * Class responsible for creating instance of the server from given location
 * and initialize its instance properties
 * @author novakm
 */
public class JetInstantiatingIterator implements WizardDescriptor.InstantiatingIterator, ChangeListener {
    
    private transient AddServerLocationPanel locationPanel = null;
    private WizardDescriptor wizard;
    private transient int index = 0;
    private transient WizardDescriptor.Panel[] panels = null;
    private static final Logger LOGGER = Logger.getLogger(JetInstantiatingIterator.class.getName());    
    
    
    private transient Set <ChangeListener> listeners = new HashSet<ChangeListener>(1);
    
    /**
     * {@inheritDoc}
     * @param l
     */    
    public void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    
    /**
     * {@inheritDoc}
     * @param l
     */
    public void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    
    public void uninitialize(WizardDescriptor wizard) {
    }
    
    /**
     * {@inheritDoc}
     */
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }
     
    /**
     * {@inheritDoc}
     */   
    public void previousPanel() {
        index--;
    }
    
    /**
     * {@inheritDoc}
     */    
    public void nextPanel() {
        if (!hasNext()) throw new NoSuchElementException();
        index++;
    }
    
    /**
     * {@inheritDoc}
     */    
    public String name() {
        return "Jetty Server AddInstanceIterator";  // NOI18N
    }
    
    public static void showInformation(final String msg,  final String title){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE);
                d.setTitle(title);
                DialogDisplayer.getDefault().notify(d);
            }
        });
        
    }
    
    /**
     * {@inheritDoc}
     */        
    public Set instantiate() throws IOException {
        Set <InstanceProperties> result = new HashSet<InstanceProperties>();
        String displayName =  (String)wizard.getProperty("ServInstWizard_displayName"); // NOI18N
        String url = JetDeploymentFactory.URI_PREFIX + ":" + host + ":" + httpPort + ":" +jetHomeLocation;    // NOI18N
        
        try {
            InstanceProperties ip = InstanceProperties.createInstanceProperties(url, null, null, displayName);
            ip.setProperty(JetPluginProperties.PROPERTY_JET_HOME, jetHomeLocation);
            ip.setProperty(InstanceProperties.HTTP_PORT_NUMBER, Integer.toString(httpPort));
            ip.setProperty(JetPluginProperties.PROPERTY_HOST, host);
            ip.setProperty(JetPluginProperties.RMI_PORT_PROP, Integer.toString(JetPluginProperties.DEFAULT_RMI_PORT));
            result.add(ip);
        } catch (InstanceCreationException e){
           LOGGER.log(Level.SEVERE, "Instantiation failed: ", e);
           DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    NbBundle.getMessage(JetInstantiatingIterator.class, "MSG_InstantiationFailed", displayName),
                    NotifyDescriptor.ERROR_MESSAGE));
        }
        
        return result;
    }
    
    public boolean hasPrevious() {
        return index > 0;
    }
    
    public boolean hasNext() {
        return index < getPanels().length - 1;
    }
    
    protected String[] createSteps() {
        return new String[] {
            NbBundle.getMessage(JetInstantiatingIterator.class, "STEP_ServerLocation")};  // NOI18N
//            NbBundle.getMessage(JetInstantiatingIterator.class, "STEP_Properties") };    // NOI18N
    }
    
    protected final String[] getSteps() {
        if (steps == null) {
            steps = createSteps();
        }
        return steps;
    }
    
    protected final WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = createPanels();
        }
        return panels;
    }
    
    protected WizardDescriptor.Panel[] createPanels() {
        if (locationPanel == null) {
            locationPanel = new AddServerLocationPanel(this);
            locationPanel.addChangeListener(this);
        }

        return new WizardDescriptor.Panel[] {
            (WizardDescriptor.Panel)locationPanel
        };
    }
    
    private transient String[] steps = null;
    
    protected final int getIndex() {
        return index;
    }
    
    public WizardDescriptor.Panel current() {
        WizardDescriptor.Panel result = getPanels()[index];
        JComponent component = (JComponent)result.getComponent();
        component.putClientProperty("WizardPanel_contentData", getSteps());  // NOI18N
        component.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(getIndex()));// NOI18N
        return result;
    }
    
    public void stateChanged(javax.swing.event.ChangeEvent changeEvent) {
        fireChangeEvent();
    }
    
    protected final void fireChangeEvent() {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener) it.next()).stateChanged(ev);
        }
    }
    
    private int httpPort = 8080;
//    private int adminPort = 8080;
    private String host="localhost";
//    private String userName;
//    private String password;
    private String jetHomeLocation;
//    private String webSite;
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }
    
    public void setJetHomeLocation(String jetHomeLocation) {
        this.jetHomeLocation = jetHomeLocation;
    }
    
    public String getJetHomeLocation() {
        return jetHomeLocation;
    }
    
//    public void setWebSite(String webSite) {
//        this.webSite = webSite;
//    }
}