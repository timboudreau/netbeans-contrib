/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70TargetNode.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

import java.io.File;
import org.openide.windows.InputOutput;
import org.openide.windows.IOProvider;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import javax.swing.Action;
import java.awt.Image;
import org.openide.util.Utilities;
import org.openide.util.NbBundle;
import org.openide.ErrorManager;

import org.netbeans.modules.j2ee.sun.ws7.j2ee.ResourceType;
import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentManager;
import org.openide.util.actions.SystemAction;
import org.openide.actions.PropertiesAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.actions.StartStopServerAction;
import org.netbeans.modules.j2ee.sun.ws7.nodes.actions.ViewTargetServerLogAction;
import org.netbeans.modules.j2ee.sun.ws7.j2ee.WS70LogViewer;

/**
 *
 * @author Mukesh Garg
 */
public class WS70TargetNode extends AbstractNode implements Node.Cookie{
    WS70SunDeploymentManager manager;
    Target target;
    Lookup looup;
    String configName;
    boolean isConfigChanged = false;    
    /** Creates a new instance of WS70TargetNode */
    public WS70TargetNode(Lookup lookup){            
        super(new Children.Array());
        this.looup = lookup;
        target = (Target)lookup.lookup(Target.class);
        setName(target.getName());
        try{
            Method getConfigName = target.getClass().getDeclaredMethod("getConfigName", new Class[]{});
            configName = (String)getConfigName.invoke(target, new Object[]{});            

        }catch(Exception ex){
            ex.printStackTrace();            
        }                                
        this.manager = (WS70SunDeploymentManager)lookup.lookup(DeploymentManager.class);

        List jvmOptions = ((WS70SunDeploymentManager)manager).getJVMOptions(configName, Boolean.valueOf(false), null);        
        Map jvmProps = ((WS70SunDeploymentManager)manager).getJVMProps(configName);

        WS70JVMManagedObject jvm = new WS70JVMManagedObject(this.manager, configName, 
                                                            (HashMap)jvmProps, jvmOptions);


        setIconBaseWithExtension("org/netbeans/modules/j2ee/sun/ws7/resources/ConfigIcon.gif");
        getCookieSet().add(this);
        getChildren().add(new Node[] {new WS70WebModulesRootNode(lookup), 
                                      new WS70ResourcesRootNode(lookup, ResourceType.JDBC),
                                      new WS70ResourcesRootNode(lookup, ResourceType.JNDI),
                                      new WS70ResourcesRootNode(lookup, ResourceType.CUSTOM),
                                      new WS70ResourcesRootNode(lookup, ResourceType.MAIL),
                                      new WS70JVMNode(jvm)});
        setMyDisplayName();
        
    }
    public Action getPreferredAction() {
        return SystemAction.get(PropertiesAction.class);
    }  
    public Action[] getActions(boolean context) {
        return new SystemAction[] {               
            SystemAction.get(StartStopServerAction.class),            
            null,
            SystemAction.get(ViewTargetServerLogAction.class),
        };
    }
    
    public boolean isRunning(){
        return manager.isRunning(configName);
    }
    public void startTarget(){
        try{
            manager.startServer(configName);
        }catch(Exception ex){            
        }
        invokeLogViewer();        
    }
    public void stopTarget(){
        try{
            manager.stopServer(configName);
        }catch(Exception ex){            
        }
    }
    public void invokeLogViewer(){
        String location = manager.getServerLocation();
        location = location+File.separator+"https-"+configName+
                File.separator+"logs"+File.separator+"errors";
        String logName = NbBundle.getMessage(WS70TargetNode.class, "LBL_WS70_MANAGER_NODE_NAME")+
                "--"+configName;
        WS70LogViewer logViewer = new WS70LogViewer(new File(location));
        InputOutput io = IOProvider.getDefault().getIO(logName, false);
        try{
            logViewer.showLogViewer(io);
        }catch(Exception ex){
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
        
    }
    private void setMyDisplayName(){        
        this.setDisplayName(target.getName());
    }
    public void fireChangeIcon(){        
        isConfigChanged = true;
        this.fireIconChange();        
    }
    
    public Image getIcon(int type){
        Image img1 = this.getOpenedIcon(type);
        if(!isConfigChanged){
            return img1;
        }else{
            Image img2 = Utilities.loadImage("org/netbeans/modules/j2ee/sun/ws7/resources/ConfigChanged.gif");
            Image img3 = Utilities.mergeImages(img1, img2, 15, 8);
            return img3;
        }

    }

}
