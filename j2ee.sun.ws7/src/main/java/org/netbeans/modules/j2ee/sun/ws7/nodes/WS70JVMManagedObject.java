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
 * WS70JVMManagedObject.java
 *
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.openide.util.NbBundle;
import org.openide.execution.NbClassPath;

import org.netbeans.modules.j2ee.sun.ws7.ide.editors.TaggedValue;
import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentManager;
import org.netbeans.modules.j2ee.sun.ws7.ui.Util;

import javax.enterprise.deploy.spi.DeploymentManager;
/**
 *
 * @author Administrator
 */
public class WS70JVMManagedObject extends WS70ManagedObjectBase{
    private WS70SunDeploymentManager manager;
    private String configName;
    private HashMap attributes;
    private List jvmOptions;
    /** Creates a new instance of WS70JVMManagedObject */
    public WS70JVMManagedObject(DeploymentManager manager, String config, HashMap attrs,
                                List jvmOptions) {        
        this.manager = ((WS70SunDeploymentManager)manager);
        this.configName = config;
        this.attributes = this.constructJVMAttributes(attrs);
        this.jvmOptions = jvmOptions;
        Iterator it = attributes.keySet().iterator();
        while(it.hasNext()){
            Attribute a = (Attribute)it.next();
            if(a.getName().equals(JVM_DEBUG_OPTION)){
                String debugString = a.getValue().toString();
                this.manager.setDebugOptions(debugString);
            }
            if(a.getName().equals(JVM_DEBUG)){
                boolean isDebug = ((Boolean)a.getValue()).booleanValue();
                this.manager.setDebugModeEnabled(isDebug);
            }            
        }
 
    }
    public  String getDisplayName(){
        return NbBundle.getMessage(WS70JVMManagedObject.class, "LBL_JVM_NODE_NAME"); // NOI18N
    }    
   
    public Attribute setAttribute(String attribute, Object value) throws Exception{
        HashMap map = new HashMap();
        map.put(attribute, value.toString());        
        try{            
            manager.setJVMProps(configName, map);
            if(attributes.containsKey(attribute)){
                attributes.put(attribute, value);
            }
            Util.showInformation(
                    NbBundle.getMessage(WS70JVMManagedObject.class, "MSG_RESTART_SERVER")); // NOI18N);
        }catch(Exception ex){
            throw ex;
        }
        if(attribute.equals(JVM_DEBUG_OPTION)){
            // update DM about change in the debug options.
            this.manager.setDebugOptions(value.toString());
        }
        if(attribute.equals(JVM_DEBUG)){
            // update DM about change in the debug mode.
            this.manager.setDebugModeEnabled(((Boolean)value).booleanValue());
        }        
        return new Attribute(attribute, value);
    }
    private String setJVMOptions(List options) throws Exception{
        try{
            manager.setJVMOptions(configName, options, 
                                         java.lang.Boolean.FALSE, null);
            return NbBundle.getMessage(WS70JVMManagedObject.class, "MSG_RESTART_SERVER"); // NOI18N
        }catch(Exception ex){
            throw ex;
        }
        
    }
    public Sheet updateSheet(Sheet sheet) {
        Sheet.Set ps = sheet.get(Sheet.PROPERTIES);
        try {                                  
            for(Iterator itr = attributes.keySet().iterator(); itr.hasNext(); ) {
                Attribute a = (Attribute) itr.next();
                AttributeInfo attr = (AttributeInfo)attributes.get(a);
                String shortDescription = getShortDescription(attr);
                if (attr == null || ! attr.isReadable()) {
                    continue;
                }
               
                Class type = getSupportedType(attr.getType());
                if (attr.isWritable()) {
                    Set classpathAttrs =  new HashSet(Arrays.asList(JVM_STR_TO_ARR));                    
                    if(classpathAttrs.contains(a.getName())){
                        if (a.getValue()!= null) {                        
                            ps.put(createNetBeansClassPathProperty(a, attr,
                                                                shortDescription));
                        } else {
                            ps.put(createReadOnlyProperty(a, attr,
                                                          shortDescription));
                        }
                    } else if (type != null) {
                        if (a.getValue() instanceof TaggedValue) {
                            ps.put(createTaggedProperty(a, attr, shortDescription, type));
                        }else {
                            ps.put(createWritableProperty(a, attr, shortDescription, type)); 
                        } // end of else

                    } else {
                        ps.put(createReadOnlyProperty(a, attr, shortDescription));
                    }
                }else {
                    Set classpathAttrs =  new HashSet(Arrays.asList(JVM_STR_TO_ARR));                    
                    if(classpathAttrs.contains(a.getName())){
                        if (a.getValue()!= null) {                        
                            ps.put(createNetBeansClassPathProperty(a, attr,
                                                                shortDescription));
                        } else {
                            ps.put(createReadOnlyProperty(a, attr,
                                                          shortDescription));
                        }
                    } else {
                        ps.put(createReadOnlyProperty(a, attr,
                                                      shortDescription));
                    }
                }//attr is writable                
            } //for loop ends
            // for jvm-options            
            Object[] opts = jvmOptions.toArray();
            String[] values = new String[opts.length];
            for(int i = 0;i<opts.length;i++){
                values[i] = (String)opts[i];
            }
            String jvmOptionsName = NbBundle.getMessage(WS70JVMManagedObject.class, "LBL_JVM_OPTIONS"); // NOI18N
            Attribute attr =  new Attribute(jvmOptionsName, values);
            AttributeInfo  attrInfo = new AttributeInfo(jvmOptionsName, "java.lang.String[]",
                                                null, true, true, false);
            
            ps.put(createModifiedStringArrayWritableProperty(attr, attrInfo, ""));
            
        } catch (Exception t) {
            t.printStackTrace();
        }

        return sheet;
    }

    private HashMap constructJVMAttributes(HashMap attrMap){
        HashMap attributes = new HashMap();
        for(Iterator itr = attrMap.keySet().iterator(); itr.hasNext(); ) {
            String attrName = (String) itr.next();
            Object attrValue = attrMap.get(attrName);
            Attribute attr = null;
            AttributeInfo attrInfo = null;
            Set classpathAttrs =  new HashSet(Arrays.asList(JVM_STR_TO_ARR));
            Set booleans =  new HashSet(Arrays.asList(JVM_BOOLEAN_VALS));
  /*          if(classpathAttrs.contains(attrName)){
                String[] values = createClasspathArray(attrValue);
                attr = new Attribute(attrName, values);
                attrInfo = new AttributeInfo(attrName, "java.lang.String[]", null,
                                                       true, true, false);                  
            }else */if(booleans.contains(attrName)){
                Object obj = Boolean.getValue((java.lang.Boolean)attrValue);
                attr = new Attribute(attrName, obj);
                attrInfo = new AttributeInfo(attrName, obj.getClass().getName(),
                                                null, true, true, true);           
            }else{
                attr = new Attribute(attrName, attrValue);
                attrInfo = new AttributeInfo(attrName, "java.lang.String",
                                                null, true, true, false);                   
                
            }           

            attributes.put(attr, attrInfo); 
 
        }        
        return attributes;
    }
    PropertySupport createModifiedStringArrayWritableProperty(final Attribute a,
                                                              final AttributeInfo attr,
                                                              final String shortDescription) {
        return new PropertySupport.ReadWrite(attr.getName(),
                                             String[].class,
                                             attr.getName(),
                                             shortDescription) {                
                Attribute attribute = a;
                public Object getValue() {                    
                    return attribute.getValue();                    
                }
                public void setValue(Object value) {
                    try {                        
                        String[] values = (String[])value;
                        java.util.ArrayList list = new java.util.ArrayList();
                        if (values.length >= 1) {
                            for (int i = 0; i < values.length; i++) {
                                list.add(values[i]);
                            }
                            try{
                                String retval = setJVMOptions(list);
                                attribute.setValue(values);
                                Util.showInformation(retval, "JVM Options setting"); // doi18n
                            }catch(Exception ex){
                                Util.showError(ex.getMessage());
                            }                            
                        }
                    } catch (Exception e) {
                        Util.showError(e.getLocalizedMessage());
                    }
                }
                
            };
    }
    
    PropertySupport createNetBeansClassPathProperty(final Attribute a,
                                                    final AttributeInfo attr,
                                                    final String shortDescription) {
        return new PropertySupport.ReadWrite(attr.getName(),
                                             NbClassPath.class,
                                             attr.getName(),
                                             shortDescription) {
                Attribute attribute = a;

                public Object getValue() {
                    try {
                        if (attribute.getValue() != null) {
                            String x = attribute.getValue().toString();
                            return new NbClassPath(attribute.getValue().toString());
                        }
                        else {
                            return null; 
                        } // end of else
                    } catch (Exception ex) {
                        return null;
                    }
                }
            
                public void setValue(Object val) {
                    try {
                        String value = ((NbClassPath)val).getClassPath();                        
                        attribute = setAttribute(getName(), value);
                    } catch (Exception e) {
                        Util.showError(e.getLocalizedMessage());
                    }
                }
            };
    }
    
    static String[] createClasspathArray(Object cpath) {
        Vector path = new Vector();

        if (cpath != null) {
            String classPath = cpath.toString();
            char sepChar = getSeperationChar(classPath);

            while (classPath.indexOf(sepChar) != -1) {
                int index = classPath.indexOf(sepChar);
                String val = classPath.substring(0, index);
                path.add(val);
                classPath = classPath.substring(index + 1,
                                                classPath.length());
            }

            path.add(classPath);
        }

        if (path != null) {
            Object[] finalPath = (Object[])path.toArray();
            String[] value = new String[finalPath.length];

            for (int i = 0; i < finalPath.length; i++) {
                value[i] = finalPath[i].toString();
            }

            return value;
        }
        else {
            return null; 
        } // end of else
    }
    
    private static char getSeperationChar(String classPath){ 
        if((classPath.indexOf(":") != -1) && // NOI18N
           (classPath.indexOf(";") != -1)) { // NOI18N
            return ';';         // NOI18N
        }
        else {
            return ':';         // NOI18N
        } // end of else
    }
    
}