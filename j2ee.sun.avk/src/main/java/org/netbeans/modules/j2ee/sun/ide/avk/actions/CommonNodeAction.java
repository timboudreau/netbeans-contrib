/*
 * CommonNodeAction.java
 *
 * Created on March 28, 2006, 11:33 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.j2ee.sun.ide.avk.actions;

import java.io.File;
import javax.enterprise.deploy.shared.ModuleType;
import javax.management.ObjectName;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.sun.api.SunDeploymentManagerInterface;
import org.netbeans.spi.project.ActionProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author bshankar@sun.com
 */
public abstract class CommonNodeAction extends NodeAction {
    
    public static String curResultsDir = null; // This variable is shared across all actions.
    
    protected boolean enable(Node[] node) {
        return true;
    }
    
    public String getName() {
        return "Common Node Action";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    protected SunDeploymentManagerInterface getDeploymentManager(Project project) {
        SunDeploymentManagerInterface sdm = null;
        InstanceProperties instProps = getInstanceProperties(project);
        sdm = (SunDeploymentManagerInterface)instProps.getDeploymentManager();
        return sdm;
    }
    
    protected InstanceProperties getInstanceProperties(Project project) {
        Lookup projectLookup = project.getLookup();
        J2eeModuleProvider moduleProvider = (J2eeModuleProvider)projectLookup.lookup(J2eeModuleProvider.class);
        InstanceProperties instProps = moduleProvider.getInstanceProperties();
        if(instProps == null) {
            instProps = InstanceProperties.getInstanceProperties(moduleProvider.getServerInstanceID());
        }
        return instProps;
        
    }
    protected ActionProvider getActionProvider(Project project) {
        Lookup projectLookup = project.getLookup();
        ActionProvider ap = (ActionProvider)projectLookup.lookup(ActionProvider.class);
        return ap;
    }
    
    protected Lookup getLookup(Node projectNode) {
        Lookup lookup = projectNode.getLookup();
        Project project = (Project)lookup.lookup(Project.class);
        Lookup projectLookup = project.getLookup();
        return projectLookup;
    }
    
    protected String getDeployDirFromDomainXml(Project project) {
        String deployDir = null;
        Lookup projectLookup = project.getLookup();
        J2eeModuleProvider moduleProvider = (J2eeModuleProvider)projectLookup.lookup(J2eeModuleProvider.class);
        String moduleName;
        try {
            moduleName = moduleProvider.getJ2eeModule().getArchive().toString();
            moduleName = moduleName.substring(moduleName.lastIndexOf('/')+1);
            moduleName = moduleName.substring(0,moduleName.lastIndexOf('.'));
        } catch(Exception ex) {
            moduleName = moduleProvider.getDeploymentName().toLowerCase();
            ex.printStackTrace();
        }
        String type = "j2ee-application";
        if(ModuleType.WAR.equals(moduleProvider.getJ2eeModule().getModuleType())) {
            type = "web-module";
        } else if(ModuleType.EJB.equals(moduleProvider.getJ2eeModule().getModuleType())) {
            type = "ejb-module";
        } else if(ModuleType.RAR.equals(moduleProvider.getJ2eeModule().getModuleType())) {
            type = "connector-module";
        }
        String objectName = "com.sun.appserv:type=" + type + ",name=" + moduleName + ",category=config";
        String attrName = "location";
        try {
            deployDir = (String)getDeploymentManager(project).getManagement().getMBeanServerConnection().getAttribute(new ObjectName(objectName), attrName);
            deployDir = stringReplace(deployDir, "${com.sun.aas.instanceRoot}", getDomainDir(project), true);
            deployDir = stringReplace(deployDir, "${com.sun.aas.installRoot}", getDeploymentManager(project).getPlatformRoot().getAbsolutePath(), true);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return deployDir;
    }
    
    private String stringReplace(String s, String oldString, String newString, boolean replaceAll) {
        int index = -1;
        while((index = s.indexOf(oldString)) != -1) {
            StringBuilder sb = new StringBuilder(s);
            sb.replace(index, oldString.length(), newString);
            s = sb.toString();
            if(!replaceAll) break;
        }
        return s;
    }
    
    protected String getDeployDirUsingNetbeansAPIs(Project project) {
        Lookup projectLookup = project.getLookup();
        J2eeModuleProvider moduleProvider = (J2eeModuleProvider)projectLookup.lookup(J2eeModuleProvider.class);
        String moduleName;
        try {
            moduleName = moduleProvider.getJ2eeModule().getArchive().toString();
            moduleName = moduleName.substring(moduleName.lastIndexOf('/'));
            moduleName = moduleName.substring(0,moduleName.lastIndexOf('.'));
        } catch(Exception ex) {
            moduleName = moduleProvider.getDeploymentName().toLowerCase();
            ex.printStackTrace();
        }
        String domainDir = getDomainDir(project);
        String moduleDir = null;
        if(ModuleType.EAR.equals(moduleProvider.getJ2eeModule().getModuleType())) {
            moduleDir = "j2ee-apps";
        } else {
            moduleDir = "j2ee-modules";
        }
        String deploymentDirName = domainDir + File.separator +
                "applications" + File.separator +
                moduleDir + File.separator +
                moduleName;
        if(!(new File(deploymentDirName).exists())) { // in-place deployment.
            try {
                deploymentDirName = FileUtil.toFile(moduleProvider.getJ2eeModule().getContentDirectory()).getAbsolutePath();
            } catch(Exception ex) {
                ex.printStackTrace();
                deploymentDirName = null;
            }
        }
        return deploymentDirName;
    }
    
    protected String getDeployDir(Project project) {
        return getDeployDirFromDomainXml(project);
        //return getDeployDirUsingNetbeansAPIs(project);
    }
    
    protected String getDomainDir(Project project) {
        InstanceProperties instProps = getInstanceProperties(project);
        return instProps.getProperty("LOCATION") + File.separator + instProps.getProperty("DOMAIN");
    }
    
    protected void deployProject(Project project) {
        
        final Lookup projectLookup = project.getLookup();
        final ActionProvider ap = getActionProvider(project);
        final String targetName = "redeploy";
        
        Thread t = new Thread() {
            public synchronized void run() {
                //AdminTasks.debug("Running build target = " + targetName);
                ap.invokeAction(targetName, projectLookup);
            }
        };
        t.start();
        try {
            t.join();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        //AdminTasks.debug("Done running build target = " + targetName + ", now waiting for it to complete...");
        
        Thread[] children = new Thread[t.activeCount()];
        t.enumerate(children);
        //AdminTasks.debug("children.length = " + children.length);
        
        for(Thread child : children) {
            try {
                //AdminTasks.debug("child = " + child.getName() + ", state = " + child.getState() + ", child.threadGroup = " + child.getThreadGroup());
                if(child.getName().indexOf("run-deploy") != -1) {
                    AdminTasks.debug("Waiting for thread = " + child.getName());
                    child.join();
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    protected void showError(String msgKey) {
        String msg = msgKey;
        try {
            msg = NbBundle.getMessage(CommonNodeAction.class, msgKey);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        NotifyDescriptor d = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(d);
    }
    
    private ProgressHandle handle = ProgressHandleFactory.createHandle("app-verification");
    
    protected void showProgress(String msgKey) {
        String msg = msgKey;
        try {
            msg = NbBundle.getMessage(CommonNodeAction.class, msgKey);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        handle.setDisplayName(msg);
        handle.start();
    }
    
    protected void hideProgress() {
        handle.finish();
    }
}
