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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.j2ee.hk2.wizards;

import org.netbeans.modules.j2ee.hk2.Hk2DeploymentFactory;
import org.netbeans.modules.j2ee.hk2.ide.Hk2PluginProperties;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceCreationException;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;

import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;

/**
 * @author Ludo
 */
public class Hk2InstantiatingIterator implements WizardDescriptor.InstantiatingIterator, ChangeListener {
    
    private transient AddServerLocationPanel locationPanel = null;
    private transient AddServerPropertiesPanel propertiesPanel = null;
    
    private WizardDescriptor wizard;
    private transient int index = 0;
    private transient WizardDescriptor.Panel[] panels = null;
    
    
    private transient Set <ChangeListener> listeners = new HashSet<ChangeListener>(1);
    
    public void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    
    public void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    
    public void uninitialize(WizardDescriptor wizard) {
    }
    
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }
    
    public void previousPanel() {
        index--;
    }
    
    public void nextPanel() {
        if (!hasNext()) throw new NoSuchElementException();
        index++;
    }
    
    public String name() {
        return "GlassFish V3 Server AddInstanceIterator";  // NOI18N
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
    
    public Set instantiate() throws IOException {
        Set <InstanceProperties> result = new HashSet<InstanceProperties>();
        String displayName =  (String)wizard.getProperty("ServInstWizard_displayName"); // NOI18N
        String url = "["+glassFishHomeLocation+"]"+Hk2DeploymentFactory.URI_PREFIX + ":" + host + ":" + httpPort;    // NOI18N
        
        try {
            InstanceProperties ip = InstanceProperties.createInstanceProperties(url, userName, password, displayName);
            ip.setProperty(Hk2PluginProperties.PROPERTY_ADMIN_PORT, Integer.toString(adminPort));
            ip.setProperty(Hk2PluginProperties.PROPERTY_HK2_HOME, glassFishHomeLocation);
            ip.setProperty(InstanceProperties.HTTP_PORT_NUMBER, Integer.toString(httpPort));
            ip.setProperty(Hk2PluginProperties.PROPERTY_HOST, host);
            result.add(ip);

        } catch (InstanceCreationException e){
            showInformation(e.getLocalizedMessage(), NbBundle.getMessage(AddServerPropertiesVisualPanel.class, "MSG_INSTANCE_REGISTRATION_FAILED")); //NOI18N
            ErrorManager.getDefault().log(ErrorManager.EXCEPTION, e.getMessage());
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
            NbBundle.getMessage(Hk2InstantiatingIterator.class, "STEP_ServerLocation"),  // NOI18N
            NbBundle.getMessage(Hk2InstantiatingIterator.class, "STEP_Properties") };    // NOI18N
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
        if (propertiesPanel == null) {
            propertiesPanel = new AddServerPropertiesPanel(this);
            propertiesPanel.addChangeListener(this);
        }
        
        return new WizardDescriptor.Panel[] {
            (WizardDescriptor.Panel)locationPanel,
            (WizardDescriptor.Panel)propertiesPanel
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
    
    private String host = "localhost";
    private int httpPort;
    private int adminPort;
    private String userName;
    private String password;
    private String glassFishHomeLocation;
    
    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }
    
    public void setAdminPort(int adminPort) {
        this.adminPort = adminPort;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setHk2HomeLocation(String homeLocation) {
        this.glassFishHomeLocation = homeLocation;
    }
    
    public String getHk2HomeLocation() {
        return glassFishHomeLocation;
    }
    

}