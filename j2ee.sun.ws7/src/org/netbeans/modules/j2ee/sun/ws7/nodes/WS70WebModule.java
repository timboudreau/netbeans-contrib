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
