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
 * WS70WebModule.java 
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.TargetModuleID;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import java.beans.PropertyEditor;
import java.util.List;
import java.lang.reflect.Method;

/**
 *
 * @author Administrator
 */
public class WS70WebModule extends WS70ManagedObjectBase implements Node.Cookie {
    
    private TargetModuleID moduleID;
    private DeploymentManager manager;
    private boolean enabled;
    private String path;
    /** Creates a new instance of WS70WebModule */
    public WS70WebModule(DeploymentManager manager, TargetModuleID moduleId){
        this.manager = manager;
        this.moduleID = moduleId;
        Class moduleClass = moduleID.getClass();
        try{
            Method method = moduleClass.getDeclaredMethod("isRunning", new Class[]{});
            java.lang.Boolean retVal = (java.lang.Boolean)method.invoke(moduleID, new Object[]{});
            enabled = retVal.booleanValue();
            method = moduleClass.getDeclaredMethod("getPath", new Class[]{});
            path = (String)method.invoke(moduleID, new Object[]{});
        }catch(Exception m){
            m.printStackTrace();
        }
        
    }
    public boolean isModuleEnabled(){
        return enabled;
    }
    public void setModuleEnabled(boolean enable) throws Exception{
        try{
            if(enable){
                manager.start(new TargetModuleID[]{this.moduleID});                
            }else{
                manager.stop(new TargetModuleID[]{this.moduleID});
            }
            enabled = enable;
            
        }catch(Exception ex){
            throw ex;
        }
        
    }
    
    public String getName(){
        return moduleID.getModuleID();
    }
    public String getPath(){
        return path;
    }
    public Sheet updateSheet(Sheet sheet) {
        Sheet.Set ps = sheet.get(Sheet.PROPERTIES);
        Attribute attr = null;
        AttributeInfo attrInfo = null;        
        attr = new Attribute("Module ID", moduleID.getModuleID());
        attrInfo = new AttributeInfo("Module ID", "java.lang.String", null,
                                                   true, false, false);
        ps.put(createReadOnlyProperty(attr, attrInfo, "shortDescription"));
        attr = new Attribute("Web URL", moduleID.getWebURL());
        attrInfo = new AttributeInfo("Web URL", "java.lang.String", null,
                                                   true, false, false);
        ps.put(createReadOnlyProperty(attr, attrInfo, "shortDescription")); 
        attr = new Attribute("Path", path);
        attrInfo = new AttributeInfo("Path", "java.lang.String", null,
                                                   true, false, false);
        ps.put(createReadOnlyProperty(attr, attrInfo, "shortDescription"));            
        
        return sheet;
    }
    public Attribute setAttribute(String attribute, Object value){
        return null;
    }
    public String getDisplayName(){
        return moduleID.getModuleID(); 
    }
    public void undeploy() throws Exception{
        try{
            manager.undeploy(new TargetModuleID[]{moduleID});
        }catch(Exception ex){
            throw ex;
        }
    }
}
