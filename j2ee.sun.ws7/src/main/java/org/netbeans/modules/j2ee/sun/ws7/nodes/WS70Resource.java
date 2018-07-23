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
 * WS70Resource.java
 */

package org.netbeans.modules.j2ee.sun.ws7.nodes;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.TargetModuleID;
import org.netbeans.modules.j2ee.sun.ws7.Constants;
import org.openide.util.Lookup;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import java.beans.PropertyEditor;

import org.netbeans.modules.j2ee.sun.ws7.ide.editors.TaggedValue;
import org.netbeans.modules.j2ee.sun.ide.editors.NameValuePairsPropertyEditor;
import org.netbeans.modules.j2ee.sun.ide.editors.NameValuePair;
import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentManager;
import org.netbeans.modules.j2ee.sun.ws7.j2ee.ResourceType;
import org.netbeans.modules.j2ee.sun.ws7.ui.Util;


/**
 *
 * @author Administrator
 */
public class WS70Resource extends WS70ManagedObjectBase {
    private String jndiName;
    private ResourceType resType;
    private String configName;
    private HashMap attributes;
    private WS70SunDeploymentManager manager;
    private Map properties;
    
    /**
     * Creates a new instance of WS70Resource
     */
    public WS70Resource(DeploymentManager manager, String config, 
                        HashMap attributes, ResourceType type) {        
        Object name = attributes.get("jndi-name");
        if(name==null){
            this.jndiName =  "jndiname";
        }else{
            this.jndiName = (String)name;            
        }
        
        this.attributes = this.constructAttributes(attributes);        
        this.manager = (WS70SunDeploymentManager)manager;
        configName = config;      
        resType = type;
    }
    
    public String getJndiName(){
        return jndiName;        
    }
    public String getDisplayName(){
        return getJndiName();
    }
    public void setProperties(String propType, Map props){
        if(properties==null){
            properties = new HashMap();            
        }
        properties.put(propType, props);
    }
    public void deleteResource()throws Exception{        
        try{
            manager.deleteResource(resType, configName, jndiName);        
        }catch(Exception ex){
            throw ex;
        }
    }
    public Attribute setAttribute(String attribute, Object value) throws Exception{
        try{
            HashMap map = new HashMap();
            map.put(attribute, value.toString());
            manager.setResource(resType, configName, getJndiName(), map, true);
            if(attributes.containsKey(attribute)){
                attributes.put(attribute, value);
            }
        }catch(Exception ex){
            throw ex;
        }
        return new Attribute(attribute, value);        
    }
    private void setResourceProperty(String type, HashMap props, ArrayList list) throws Exception{
        try{ 
            manager.setUserResourceProp(configName, resType.toString(), 
                    getJndiName(), type, list, true);
 
        }catch(Exception ex){
            throw ex;
        }        
        properties.put(type, props);        
    }
    

    public Sheet updateSheet(Sheet sheet) {
        Sheet.Set ps = sheet.get(Sheet.PROPERTIES);
        try {            
            Set readOnlyResource = new HashSet(Arrays.asList(READ_ONLY_PROPS_RESOURCES));           
            for(Iterator itr = attributes.keySet().iterator(); itr.hasNext(); ) {
                Attribute a = (Attribute) itr.next();
                AttributeInfo attr = (AttributeInfo)attributes.get(a);
                String shortDescription = getShortDescription(attr);

                if (attr == null || ! attr.isReadable()) {
                    continue;
                }
               
                Class type = getSupportedType(attr.getType());
                                // make jndi-name/name of resource 
                                // read only
                if (readOnlyResource.contains(attr.getName())) {
                    ps.put(createReadOnlyProperty(a, attr, shortDescription));
                } else {
                    if (attr.isWritable()) {
                        if (a.getValue() instanceof String[]) {
                            String[] props = (String[])a.getValue();
                            if (props != null) {
                                ps.put(getStringArrayEditor(a, attr,
                                                            shortDescription,
                                                            props.getClass(),
                                                            true));
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
                        if (a.getValue() instanceof String[]) {
                            String[] props = (String[])a.getValue();

                            if (props != null) {
                                ps.put(getStringArrayEditor(a, attr, shortDescription,
                                                            props.getClass(), false));
                            } else {
                                ps.put(createReadOnlyProperty(a, attr,
                                                              shortDescription));
                            }
                        } else {
                            ps.put(createReadOnlyProperty(a, attr,
                                                          shortDescription));
                        }
                    }//attr is writable
                } //else of resource name or jndi-name
            } //for loop ends
            
            // Get all user properties if properties is not null
            if(properties!=null){
                HashMap  props = (HashMap)properties.get(Constants.RES_PROPERTY);
                if(props!=null){                
                    ps.put(this.createExtraProperties(Constants.RES_PROPERTY, props));
                }
                props = (HashMap)properties.get(Constants.JDBC_RES_CONN_LEASE_PROPERTY);
                if(props!=null){                
                    ps.put(this.createExtraProperties(Constants.JDBC_RES_CONN_LEASE_PROPERTY, props));
                }            
                props = (HashMap)properties.get(Constants.JDBC_RES_CONN_CREATION_PROPERTY);
                if(props!=null){
                    ps.put(this.createExtraProperties(Constants.JDBC_RES_CONN_CREATION_PROPERTY, props));
                }
            }
            
        } catch (Exception t) {
            t.printStackTrace();
        }

        return sheet;
    }
    PropertySupport createExtraProperties(final String name, final Map props) {
        return new PropertySupport.ReadWrite(
            name, 
            NameValuePairsPropertyEditor.class, name,
                NbBundle.getMessage(WS70Resource.class, "DSC_ExtParams")){//NOI18N 
            Map values = props;
            String type = name;
            public Object getValue() {                
                return values;
            }
              
            public void setValue(Object obj) {
                if(obj instanceof Object[]){
                    //Create a an array of updated properties
                    Object[] currentVal = (Object[])obj;
                    
                    HashMap propertyList = new HashMap();
                    ArrayList list = new ArrayList();
                    for(int i=0; i<currentVal.length; i++){
                        NameValuePair pair = (NameValuePair)currentVal[i];
                        propertyList.put(pair.getParamName(), pair.getParamValue());
                        list.add(pair.getParamName()+"="+pair.getParamValue());
                    }
                    try{
                        setResourceProperty(name, propertyList, list);
                        values = propertyList;                        
                    }catch(Exception ex){
                        Util.showError(ex.getLocalizedMessage());
                    }                    
                }                    
            }
            public PropertyEditor getPropertyEditor(){
                return new NameValuePairsPropertyEditor(values);         
            }
        };
    }//createExtraProperties
    private HashMap constructAttributes(HashMap attrMap){
        HashMap attributes = new HashMap();
        for(Iterator itr = attrMap.keySet().iterator(); itr.hasNext(); ) {
            String attrName = (String) itr.next();
            Object attrValue = attrMap.get(attrName);
            Attribute attr = null;
            AttributeInfo attrInfo = null;
            Set booleans =  new HashSet(Arrays.asList(RESOURCE_BOOLEAN_VALS));
            if(attrName.equals("jndi-name")){
                attr = new Attribute(attrName, attrValue);
                attrInfo = new AttributeInfo(attrName, "java.lang.String", null,
                                                       true, false, false); 
            }else if(attrName.equals("isolation-level")){
                Object obj = TransactionIsolation.getValue((String)attrValue);
                attr = new Attribute(attrName, obj);
                attrInfo = new AttributeInfo(attrName, obj.getClass().getName(),
                                                null, true, true, false);
                
            }/*else if(attrName.equals("isolation-level-guaranteed") || 
                        attrName.equals("fail-all-connections") ||                         
                        attrName.equals("enabled")){*/
            else if(booleans.contains(attrName)){
                
                Object obj = Boolean.getValue((java.lang.Boolean)attrValue);
                attr = new Attribute(attrName, obj);
                attrInfo = new AttributeInfo(attrName, obj.getClass().getName(),
                                                null, true, true, true);        
                
            }else if(attrName.equals("connection-validation")){
                Object obj = ValidationMethod.getValue((String)attrValue);
                attr = new Attribute(attrName, obj);
                attrInfo = new AttributeInfo(attrName, obj.getClass().getName(),
                                                null, true, true, false);        
                
            }else{
                attr = new Attribute(attrName, attrValue);
                attrInfo = new AttributeInfo(attrName, "java.lang.String",
                                                null, true, true, false);                   
                
            }
            attributes.put(attr, attrInfo);
        }        
        return attributes;
    }
    private NameValuePair[] getNameValuePairs(Object attrVal){
        java.util.Map attributeMap = (java.util.Map)attrVal;
        Set attributeKeys = attributeMap.keySet();
        java.util.Iterator it = attributeKeys.iterator();
        NameValuePair[] pairs = new NameValuePair[attributeKeys.size()];
        int i=0;
        while(it.hasNext()){
            NameValuePair pair = new NameValuePair();
            Object key = it.next();
            pair.setParamName(key.toString());
            pair.setParamValue(attributeMap.get(key).toString());
            pairs[i] = pair;
            i++;
        }
        return pairs;
    }
 
    public static class ValidationMethod extends TaggedValue {
        private String id;
        
        private ValidationMethod(String id) {
            this.id = id;
        }
        private static final ValidationMethod FALSE =
            new ValidationMethod("false"); // NOI18N        
        private static final ValidationMethod AUTO_COMMIT =
            new ValidationMethod("auto-commit"); // NOI18N
        private static final ValidationMethod META_DATA =
            new ValidationMethod("meta-data"); // NOI18N
        private static final ValidationMethod TABLE =
            new ValidationMethod("table"); // NOI18N

        private static final ValidationMethod[] values = 
            new ValidationMethod[]{ FALSE, AUTO_COMMIT, META_DATA, TABLE };
        
        public static TaggedValue getValue(String s) {
            for (int i = 0; i < values.length; i++) {
                if (values[i].getId().equalsIgnoreCase(s)) {
                    return values[i];
                } // end of if (values[i].getId().equalsIgnoreCase(s))
            } // end of for (int i = 0; i < values.length; i++)
            
            return null;
        }

        public static TaggedValue[] getChoices() {
            return values;
        }
        
        public String getId() {
            return id;
        }

        public String toString() {
            return id;
        }
    }
    
    public static class TransactionIsolation extends TaggedValue {
        private String id;
        
        private TransactionIsolation(String id) {
            this.id = id;
        }
        private static final TransactionIsolation DAFAULT =
            new TransactionIsolation("default"); // NOI18N        
        private static final TransactionIsolation READ_UNCOMMITTED =
            new TransactionIsolation("read-uncommitted"); // NOI18N
        private static final TransactionIsolation READ_COMMITTED =
            new TransactionIsolation("read-committed"); // NOI18N
        private static final TransactionIsolation REPEATABLE_READ =
            new TransactionIsolation("repeatable-read"); // NOI18N
        private static final TransactionIsolation SERIALIZABLE =
            new TransactionIsolation("serializable"); // NOI18N
        
        private static final TransactionIsolation[] values =
            new TransactionIsolation[]{ DAFAULT, READ_UNCOMMITTED,
                                        READ_COMMITTED,
                                        REPEATABLE_READ,
                                        SERIALIZABLE };
        
        public static TaggedValue getValue(String s) {
            for (int i = 0; i < values.length; i++) {
                if (values[i].getId().equalsIgnoreCase(s)) {
                    return values[i];
                } // end of if (values[i].getId().equalsIgnoreCase(s))
            } // end of for (int i = 0; i < values.length; i++)
            
            return null;
        }

        public static TaggedValue[] getChoices() {
            return values;
        }
        
        public String getId() {
            return id;
        }

        public String toString() {
            return id;
        }
    }  
}
