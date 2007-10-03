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
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
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
import org.netbeans.modules.j2ee.sun.ws7.nodes.actions.EditServerXmlAction;
import org.netbeans.modules.j2ee.sun.ws7.j2ee.WS70LogViewer;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.cookies.EditorCookie;

import org.openide.windows.IOProvider;
import org.openide.windows.OutputWriter;

/**
 *
 * @author Mukesh Garg
 */
public class WS70TargetNode extends AbstractNode implements Node.Cookie{
    private WS70SunDeploymentManager manager;
    private Target target;
    private Lookup looup;
    private String configName;
    private boolean isConfigChanged = false;
    private static Map serverXmlListeners = 
            Collections.synchronizedMap((Map)new HashMap(2,1));
    
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
            SystemAction.get(EditServerXmlAction.class),
            SystemAction.get(ViewTargetServerLogAction.class),
        };
    }
    
    public boolean isRunning(){
        return manager.isRunning(configName);
    }
    public boolean isLocalServer(){
        return manager.isLocalServer();
    }
    public void startTarget(){
        try{
            manager.startServer(configName);
        }catch(Exception ex){            
        }
        // If Webserver is a remote instance, can't see the logs
        //Issue# 75329
        if(manager.isLocalServer()){
            invokeLogViewer();
        }
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
    public void showServerXml(){
        String location = manager.getServerLocation()+File.separator+
                          "admin-server"+File.separator+"config-store"+ //NOI18N
                          File.separator+configName+ File.separator+//NOI18N
                          "config";//NOI18N
        File serverXmlFile = new File(location, "server.xml");//NOI18N
        FileObject fileObject = FileUtil.toFileObject(serverXmlFile);
        if (fileObject == null) {
            OutputWriter writer = IOProvider.getDefault().getStdOut();
            writer.println(NbBundle.getMessage(WS70TargetNode.class, 
                              "ERR_Server_XML_not_found", configName));            
            return;
        }
        DataObject dataObject = null;
        try {
            dataObject = DataObject.find(fileObject);
        } catch(DataObjectNotFoundException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }
        if (dataObject != null) {                        
            EditorCookie editorCookie = (EditorCookie)dataObject.getCookie(EditorCookie.class);
            if (editorCookie != null) {
                editorCookie.open();
                if(serverXmlListeners.get(location)==null){                    
                    ServerFileChangeListener listener = new ServerFileChangeListener(editorCookie);                    
                    fileObject.addFileChangeListener(listener);
                    serverXmlListeners.put(location, listener);
                }                
            } else {
                ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "Cannot find EditorCookie."); // NOI18N
            }
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
 private class ServerFileChangeListener extends FileChangeAdapter{
     private EditorCookie cookie;
     public ServerFileChangeListener(EditorCookie e){         
         cookie = e;
     }
     public void fileChanged(FileEvent fe){
         if(!cookie.isModified()){
            return; // server.xml was modified outside of IDE
         }
         OutputWriter writer = IOProvider.getDefault().getStdOut();
         
         try {
             FileObject obj = fe.getFile();            
             writer.println(NbBundle.getMessage(WS70TargetNode.class, 
                              "MSG_Deploying_Config", configName));
             manager.deployAndReconfig(configName);
             writer.println(NbBundle.getMessage(WS70TargetNode.class, 
                              "MSG_Config_Deployed", configName));
         } catch(Exception ex){
             writer.println(NbBundle.getMessage(WS70TargetNode.class, 
                              "ERR_Config_deployed_failed", ex.getMessage()));
         }
     }
 }
}
