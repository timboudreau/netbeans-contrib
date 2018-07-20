/*
 * AddServerLocationPanel.java
 *
 */

package org.netbeans.modules.j2ee.geronimo2;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.j2ee.deployment.impl.ServerInstance;
import org.netbeans.modules.j2ee.deployment.impl.ServerRegistry;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
/**
 *
 * @author Max Sauer
 */
public class AddServerLocationPanel implements WizardDescriptor.Panel, ChangeListener {
    
    private final static String PROP_ERROR_MESSAGE = "WizardPanel_errorMessage"; // NOI18   
    
    private GeInstantiatingIterator instantiatingIterator;
    private AddServerLocationVisualPanel component;
    private WizardDescriptor wizard;
    private transient Set <ChangeListener>listeners = new HashSet<ChangeListener>(1);

    public AddServerLocationPanel(GeInstantiatingIterator instantiatingIterator){
        this.instantiatingIterator = instantiatingIterator;
    }
    
    public void stateChanged(ChangeEvent ev) {
        fireChangeEvent(ev);
    }
    
    private void fireChangeEvent(ChangeEvent ev) {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        while (it.hasNext()) {
            ((ChangeListener)it.next()).stateChanged(ev);
        }
    }
    
    public Component getComponent() {
        if (component == null) {
            component = new AddServerLocationVisualPanel();
            component.addChangeListener(this);
        }
        return component;
    }
    
    public HelpCtx getHelp() {
        return new HelpCtx("j2eeplugins_registering_app_server_Ge_location"); //NOI18N
    }
    
    public boolean isValid() {
        String locationStr = ((AddServerLocationVisualPanel) getComponent()).getInstallLocation();
        if (!GePluginUtils.isGoodGeServerLocation(new File(locationStr))) {
            wizard.putProperty(PROP_ERROR_MESSAGE, NbBundle.getMessage(AddServerLocationPanel.class, "MSG_InvalidServerLocation")); // NOI18N
            return false;
        }

        //check if this is not a duplicate instance
        ServerInstance[] si = ServerRegistry.getInstance().getServerInstances();
        for (int i = 0; i < si.length; i++) {
            try {
                String property = si[i].getInstanceProperties().getProperty(GePluginProperties.PROPERTY_GE_HOME);

                if (property == null)
                    continue;

                String root = new File(property).getCanonicalPath();

                if (root.equals(new File(locationStr).getCanonicalPath())) {
                    wizard.putProperty(PROP_ERROR_MESSAGE, NbBundle.getMessage(AddServerLocationPanel.class, "MSG_InstanceExists"));
                    return false;
                }
            } catch (MissingResourceException ex) {
                // It's normal behaviour when si[i] is something else then Ge instance
                continue;
            } catch (IOException ex) {
                // It's normal behaviour when si[i] is something else then Ge instance
                continue;
            }
        }

        wizard.putProperty(PROP_ERROR_MESSAGE, null);
        instantiatingIterator.setGeHomeLocation(locationStr);
        instantiatingIterator.setUserName(component.getUserName());
        instantiatingIterator.setPassword(component.getPassword());
        
        instantiatingIterator.setHost(component.getHost());
        instantiatingIterator.setHttpPort(component.getPort());
        
        NbPreferences.forModule(GeDeploymentFactory.class).put(GeDeploymentFactory.PROP_SERVER_ROOT, locationStr);

        return true;
    }

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
    
    public void readSettings(Object settings) {
        if (wizard == null)
            wizard = (WizardDescriptor)settings;
    }
    
    public void storeSettings(Object settings) {
    }
    
}
