/*
 * GeDeploymentFactory.java
 *
 */
package org.netbeans.modules.j2ee.geronimo2;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;



/**
 *
 * @author Max Sauer
 */
public class GeDeploymentFactory implements DeploymentFactory {
    
    /**
     * ge server root property
     */
    public static final String PROP_SERVER_ROOT = "geronimo_server_root"; // NOI18N
    
    public static final String URI_PREFIX = "deployer:geronimo:jmx"; // NOI18N
    private static DeploymentFactory instance;
    private HashMap<String, DeploymentManager> managers = new HashMap<String, DeploymentManager>();
    private HashMap<String, DeploymentFactory> factories = new HashMap<String, DeploymentFactory>();
    
    public static synchronized DeploymentFactory create() {
        if (instance == null) {
            instance = new GeDeploymentFactory();
            DeploymentFactoryManager.getInstance().registerDeploymentFactory(instance);
        }
        return instance;
    }
    
    public boolean handlesURI(String uri) {
        return uri != null && uri.startsWith(URI_PREFIX);
    }
    
    public DeploymentManager getDeploymentManager(String uri, String uname, String passwd) throws DeploymentManagerCreationException {
	if (!handlesURI(uri))
            throw new DeploymentManagerCreationException("Invalid URI:" + uri); // NOI18N
        
        DeploymentManager manager = managers.get(uri);
        
        if (null == manager) {
            manager = new GeDeploymentManager(uri, uname, passwd);
            managers.put(uri, manager);
        }
        
        return manager;
    }
    
    public DeploymentManager getDisconnectedDeploymentManager(String uri) throws DeploymentManagerCreationException {
	if (!handlesURI(uri))
            throw new DeploymentManagerCreationException("Invalid URI:" + uri); // NOI18N
        
        DeploymentManager manager = managers.get(uri);
        
        if (null == manager) {
            manager = new GeDeploymentManager(uri);
            managers.put(uri, manager);
        }
        
        return manager;
    }
    
    public String getProductVersion() {
        return "0.1"; // NOI18N
    }
    
    public String getDisplayName() {
        return NbBundle.getMessage(GeDeploymentFactory.class, "TXT_DisplayName"); // NOI18N
    }
    
    public DeploymentFactory getGeDeploymentFactory(String uri) {
        if (GeDebug.isEnabled())
            System.out.println("loadDeploymentFactory");
        
        DeploymentFactory factory = factories.get(uri);
        
        if (null == factory) {
            InstanceProperties ip = InstanceProperties.getInstanceProperties(uri);
            
            String serverRoot = null;
            
            if (null != ip)
                serverRoot = ip.getProperty(GePluginProperties.PROPERTY_GE_HOME);
            
            if (null == serverRoot)
                serverRoot = NbPreferences.forModule(GeDeploymentFactory.class).get(PROP_SERVER_ROOT, "");
            
            if (GeDebug.isEnabled())
                System.out.println("loadDeplomentFactory: serverRoot=" + serverRoot);

            GeClassLoader.getInstance(serverRoot).updateLoader();
            
            try {
                factory = (DeploymentFactory) GeClassLoader.getInstance(serverRoot).loadClass(
                        "org.apache.geronimo.deployment.plugin.factories.DeploymentFactoryImpl"). // NOI18N
                        newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Logger.getLogger(GeDeploymentFactory.class.getName()).log(Level.SEVERE, null, e);
            } catch (InstantiationException e) {
                e.printStackTrace();
                Logger.getLogger(GeDeploymentFactory.class.getName()).log(Level.SEVERE, null, e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Logger.getLogger(GeDeploymentFactory.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                GeClassLoader.getInstance(serverRoot).restoreLoader();
            }
            
            factories.put(uri, factory);
        }
        
        return factory;
    }
}
