package org.netbeans.modules.portalpack.servers.core;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.shared.factories.DeploymentFactoryManager;
import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.enterprise.deploy.spi.exceptions.DeploymentManagerCreationException;
import javax.enterprise.deploy.spi.exceptions.TargetException;
import javax.enterprise.deploy.spi.factories.DeploymentFactory;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.enterprise.deploy.spi.status.ProgressListener;
import javax.enterprise.deploy.spi.status.ProgressObject;
import org.netbeans.modules.portalpack.servers.core.common.DeploymentException;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 */
public class JSR88DeploymentHandler {
    
    class DeploymentListener implements ProgressListener {
        
        JSR88DeploymentHandler driver;
        String warContext;
        
        DeploymentListener(JSR88DeploymentHandler driver, String warContext) {
            this.driver = driver;
            this.warContext = warContext;
        }
        
        public void handleProgressEvent(ProgressEvent event) {
            
            writer.println(event.getDeploymentStatus().getMessage());
            
            if (event.getDeploymentStatus().isCompleted()) {
                try {
                    TargetModuleID[] ids = getDeploymentManager().getNonRunningModules(ModuleType.WAR, getDeploymentManager().getTargets());
                    TargetModuleID[] myIDs = new TargetModuleID[1];
                    for (TargetModuleID id : ids) {
                        if (warContext.equals(id.getModuleID())) {
                            myIDs[0] = id;
                            ProgressObject startProgress = driver.getDeploymentManager().start(myIDs);
                            startProgress.addProgressListener(new ProgressListener() {
                                public void handleProgressEvent(ProgressEvent event) {
                                    
                                    writer.println(event.getDeploymentStatus().getMessage());
                                    
                                    if (event.getDeploymentStatus().isCompleted()) {
                                        driver.setError(false);
                                        driver.setAppStarted(true);
                                        
                                    }
                                }
                            });
                        }
                    }
                    driver.setError(false);
                    driver.setAppStarted(true);
                } catch (IllegalStateException ex) {
                    ex.printStackTrace(errWriter);
                    //errWriter.println(ex.getMessage());
                    driver.setError(true);
                    driver.setAppStarted(false);
                } catch (TargetException ex) {
                    ex.printStackTrace(errWriter);
                    //errWriter.println(ex.getMessage());
                    driver.setError(true);
                    driver.setAppStarted(false);
                }
            }else if(event.getDeploymentStatus().isFailed()){
                driver.setError(true);
                driver.setAppStarted(false);
                
            }
        }
    }
    
    
    class UnDeploymentListener implements ProgressListener {
        
        JSR88DeploymentHandler driver;
        String warContext;
        
        UnDeploymentListener(JSR88DeploymentHandler driver, String warContext) {
            this.driver = driver;
            this.warContext = warContext;
        }
        
        public void handleProgressEvent(ProgressEvent event) {
            
            writer.println(event.getDeploymentStatus().getMessage());
            
            if (event.getDeploymentStatus().isCompleted()) {
                driver.setError(false);
                driver.setAppUndeployed(true);
            }else if(event.getDeploymentStatus().isFailed()){
                driver.setError(true);
                driver.setAppUndeployed(false);
            }
        }
    }
    
    DeploymentManager deploymentManager;
    boolean appStarted;
    boolean appUndeployed;
    boolean isError = false;
    String warContext;
    String warFilename;
    String wsdlUrl;
    ClassLoader loader;
    OutputWriter writer;
    OutputWriter errWriter;
    
    synchronized void setError(boolean error) {
        isError = error;
    }
    synchronized void setAppStarted(boolean appStarted) {
        this.appStarted = appStarted;
        notifyAll();
    }
    
    synchronized void setAppUndeployed(boolean appUndeployed) {
        this.appUndeployed = appUndeployed;
        notifyAll();
    }
    
    private String getParam(String param) {
        return (null == deploymentProperties) ? null : deploymentProperties.getProperty(param);
    }
    
    public DeploymentManager getDeploymentManager() {
        if (null == deploymentManager) {
            DeploymentFactoryManager dfm = DeploymentFactoryManager.getInstance();
            try {
                Class dfClass = loader.loadClass(getParam("jsr88.df.classname"));
                DeploymentFactory dfInstance;
                dfInstance = (DeploymentFactory) dfClass.newInstance();
                dfm.registerDeploymentFactory(dfInstance);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace(errWriter);
               
            } catch (IllegalAccessException ex) {
                ex.printStackTrace(errWriter);
            } catch (InstantiationException ex) {
                ex.printStackTrace(errWriter);
            }
            try {
                deploymentManager =
                    dfm.getDeploymentManager(
                    getParam("jsr88.dm.id"),getParam("jsr88.dm.user"),getParam("jsr88.dm.passwd"));
            } catch (DeploymentManagerCreationException ex) {
                ex.printStackTrace(errWriter);
            }
        }
        return deploymentManager;
    }
    
    public void runApp(String warFilename, String warContext) throws DeploymentException
    {
        setAppStarted(false);
        
        boolean redeploy = false;
        TargetModuleID[] tmIds = new TargetModuleID[1];
        try {
            TargetModuleID[] ids = getDeploymentManager().getAvailableModules(ModuleType.WAR, getDeploymentManager().getTargets());
            TargetModuleID[] myIDs = new TargetModuleID[1];
            for (TargetModuleID id : ids) {
                if (warContext.equals(id.getModuleID())) {
                    myIDs[0] = id;
                    redeploy = true;
                    tmIds[0] = id;
                    break;
                }
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace(errWriter);
        } catch (TargetException ex) {
            ex.printStackTrace(errWriter);
        }
        
        ProgressObject deplProgress = null;
        
        if(!redeploy) {
            deplProgress = getDeploymentManager().distribute(getDeploymentManager().getTargets(),new File(warFilename), null);
        }else {
            deplProgress = getDeploymentManager().redeploy(tmIds,new File(warFilename), null);
        }
        deplProgress.addProgressListener(new DeploymentListener(this, warContext));
        waitForAppStart();
        if(isError)
            throw new DeploymentException("Deployment failed.");
    }
    
    public void undeployApp(String warContext) throws DeploymentException
    {
        setAppUndeployed(false);
        try {
            TargetModuleID[] ids = getDeploymentManager().getRunningModules(ModuleType.WAR, getDeploymentManager().getTargets());
            TargetModuleID[] myIDs = new TargetModuleID[1];
            for (TargetModuleID id : ids) {
                if (warContext.equals(id.getModuleID())) {
                    myIDs[0] = id;
                    ProgressObject startProgress = getDeploymentManager().undeploy(myIDs);
                    startProgress.addProgressListener(new UnDeploymentListener(this,warContext));
                    
                        /*new ProgressListener() {
                        public void handleProgressEvent(ProgressEvent event) {
                            System.out.println(event.getDeploymentStatus().getMessage());
                            if (event.getDeploymentStatus().isCompleted()) {
                                setError(false);
                                setAppUndeployed(true);
                            }else if(event.getDeploymentStatus().isFailed()){
                                setError(true);
                                setAppUndeployed(false);
                            }
                        }
                    });*/
                }
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace(errWriter);
        } catch (TargetException ex) {
            ex.printStackTrace(errWriter);
        }
        waitForAppUndeployment();
        if(isError)
            throw new DeploymentException("Undeployment failed.");
    }
    
    public void releaseDeploymentManager() {
        if (null != deploymentManager) {
            deploymentManager.release();
        }
    }
    
    
    synchronized void waitForAppStart() {
        while(!appStarted && !isError) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
    }
    
    synchronized void waitForAppUndeployment() {
        while(!appUndeployed && !isError) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
    }
    
    public Writer getWriter() {
        return writer;
    }
    public JSR88DeploymentHandler() {
    }
    
    public JSR88DeploymentHandler(ClassLoader loader,Properties props,InputOutput inOut) {
        this.loader = loader;
        this.writer = inOut.getOut();
        this.errWriter = inOut.getErr();
        setProperties(props);
    }
    
    private final static String SyntaxHelp = "syntax:\n\tdeploy <warfile>\n\tundeploy <webApp>";
    private final static String PropertiesFilename = "wardeployment.properties";
    private Properties deploymentProperties;
    
    private void setProperties(Properties props) {
       /* FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            deploymentProperties = new Properties();
            deploymentProperties.load(fis);
            fis.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
        
        this.deploymentProperties = props;
    }
    
    private static void printHelpAndExit() {
        System.out.println(SyntaxHelp);
        System.exit(1);
    }
    
    /**
     * @param args the command line arguments
     */
   /* public static void main(String[] args) {
        if (args.length < 1) {
            printHelpAndExit();
        }
        JSR88DeploymentHandler worker = new JSR88DeploymentHandler(PropertiesFilename);
        if ("deploy".equals(args[0])) {
            System.out.println("Deploying app...");
            worker.runApp(args[1], args[1].substring(0,args[1].length()-4));
            worker.releaseDeploymentManager();
        } else if ("undeploy".equals(args[0])) {
            System.out.println("Undeploying app...");
            worker.undeployApp(args[1]);
            worker.releaseDeploymentManager();
        }
    
    }*/
}
