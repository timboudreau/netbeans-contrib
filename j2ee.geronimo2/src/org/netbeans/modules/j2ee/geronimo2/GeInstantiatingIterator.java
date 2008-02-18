/*
 * GeInstantiatingIterator.java
 *
 */

package org.netbeans.modules.j2ee.geronimo2;

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
 *
 * @author Max Sauer
 */
public class GeInstantiatingIterator implements WizardDescriptor.InstantiatingIterator, ChangeListener {
    
    private transient AddServerLocationPanel locationPanel = null;
    //private transient AddServerPropertiesPanel propertiesPanel = null;
    
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
        return "Ge Server AddInstanceIterator";  // NOI18N
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
        String url = GeDeploymentFactory.URI_PREFIX + "://" + host + ":" + adminPort;    // NOI18N
        
        try {
            //InstanceProperties ip = InstanceProperties.createInstanceProperties(url, "system", "manager", displayName);
            InstanceProperties ip = InstanceProperties.createInstanceProperties(url, userName, password, displayName);
            ip.setProperty(GePluginProperties.PROPERTY_ADMIN_PORT, Integer.toString(adminPort));
            ip.setProperty(GePluginProperties.PROPERTY_WEB_SITE, webSite);
            ip.setProperty(GePluginProperties.PROPERTY_GE_HOME, geHomeLoaction);
            ip.setProperty(InstanceProperties.HTTP_PORT_NUMBER, Integer.toString(httpPort));
            ip.setProperty(GePluginProperties.PROPERTY_HOST, host);
            result.add(ip);
            
            // Registering of the Oracle 10g JDBC driver
            //GePluginUtils.registerOracleJdbcDriver(oc4jHomeLocation);
        } catch (InstanceCreationException e){
            showInformation(e.getLocalizedMessage(), NbBundle.getMessage(GeInstantiatingIterator.class, "MSG_INSTANCE_REGISTRATION_FAILED")); //NOI18N
            Logger.getLogger("global").log(Level.SEVERE, e.getMessage());
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
            NbBundle.getMessage(GeInstantiatingIterator.class, "STEP_ServerLocation")  // NOI18N
            //NbBundle.getMessage(GeInstantiatingIterator.class, "STEP_Properties") 
        };    // NOI18N
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
        
//        if (propertiesPanel == null) {
//            propertiesPanel = new AddServerPropertiesPanel(this);
//            propertiesPanel.addChangeListener(this);
//        }
        
        return new WizardDescriptor.Panel[] {
            (WizardDescriptor.Panel)locationPanel
//            (WizardDescriptor.Panel)propertiesPanel
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
    private int adminPort = 1099;
    private String host;
    private String userName;
    private String password;
    private String geHomeLoaction;
    private String webSite;
    
    public void setHost(String host) {
        this.host = host;
    }
    
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
    
    public void setGeHomeLocation(String geHomeLocation) {
        this.geHomeLoaction = geHomeLocation;
    }
    
    public String getGeHomeLocation() {
        return geHomeLoaction;
    }
    
    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }
}