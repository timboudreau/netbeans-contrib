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
 * WS70JdbcResourceBeanBeanInfo.java
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.beans;

import java.beans.*;
import org.openide.util.NbBundle;
import org.netbeans.modules.j2ee.sun.ide.editors.BooleanEditor;
import org.netbeans.modules.j2ee.sun.ide.editors.Int0Editor;
import org.netbeans.modules.j2ee.sun.ide.editors.LongEditor;
import org.netbeans.modules.j2ee.sun.ide.editors.IsolationLevelEditor;
import org.netbeans.modules.j2ee.sun.ide.editors.DataSourceTypeEditor;
import org.netbeans.modules.j2ee.sun.ide.editors.ValidationMethodEditor;

/**
 * @author Administrator
 */
public class WS70JdbcResourceBeanBeanInfo extends SimpleBeanInfo {
    
    // Bean descriptor information will be obtained from introspection.//GEN-FIRST:BeanDescriptor
    private static BeanDescriptor beanDescriptor = null;
    private static BeanDescriptor getBdescriptor(){
//GEN-HEADEREND:BeanDescriptor
    BeanDescriptor beanDescriptor = new BeanDescriptor(WS70JdbcResourceBean.class, null );         
        // Here you can add code for customizing the BeanDescriptor.
        
        return beanDescriptor;     }//GEN-LAST:BeanDescriptor
    static private String getLabel(String key){
        return NbBundle.getMessage(WS70JdbcResourceBean.class,key);
    }    
    private static final int PROPERTY_jndiName = 0;
    private static final int PROPERTY_isEnabled = 1;
    private static final int PROPERTY_description = 2;    
    private static final int PROPERTY_dsClass = 3;
    private static final int PROPERTY_minConnections = 4;
    private static final int PROPERTY_maxConnections = 5;
    
    private static final int PROPERTY_idleTimeout = 6;
    private static final int PROPERTY_waitTimeout = 7;
    private static final int PROPERTY_tranxIsoLevel = 8;
    private static final int PROPERTY_isIsoLevGuaranteed = 9;
    private static final int PROPERTY_name = 10;
    private static final int PROPERTY_connValidation = 11;
    
    private static final int PROPERTY_validationTableName = 12;
    private static final int PROPERTY_failAllConns = 13;
    
    // Properties information will be obtained from introspection.//GEN-FIRST:Properties
    private static PropertyDescriptor[] properties = null;
    private static PropertyDescriptor[] getPdescriptor(){
//GEN-HEADEREND:Properties
    PropertyDescriptor[] properties = new PropertyDescriptor[14];
       try {
            properties[PROPERTY_jndiName] = new PropertyDescriptor ( "jndiName", WS70JdbcResourceBean.class, "getJndiName", "setJndiName" );
            properties[PROPERTY_jndiName].setDisplayName ( getLabel("LBL_JdbcJndiName") );
            properties[PROPERTY_jndiName].setShortDescription ( getLabel("DSC_JdbcJndiName") );
            
            properties[PROPERTY_description] = new PropertyDescriptor ( "description", WS70JdbcResourceBean.class, "getDescription", "setDescription" );
            properties[PROPERTY_description].setDisplayName ( getLabel("LBL_Description") );
            properties[PROPERTY_description].setShortDescription ( getLabel("DSC_Description") );            
       
            properties[PROPERTY_isEnabled] = new PropertyDescriptor ( "isEnabled", WS70JdbcResourceBean.class, "getIsEnabled", "setIsEnabled" );
            properties[PROPERTY_isEnabled].setDisplayName ( getLabel("LBL_Enabled") );
            properties[PROPERTY_isEnabled].setShortDescription ( getLabel("DSC_Enabled") );
            properties[PROPERTY_isEnabled].setPropertyEditorClass ( BooleanEditor.class );            
            

            properties[PROPERTY_dsClass] = new PropertyDescriptor ( "dsClass", WS70JdbcResourceBean.class, "getDsClass", "setDsClass" );
            properties[PROPERTY_dsClass].setDisplayName ( getLabel("LBL_DSClassName") );
            properties[PROPERTY_dsClass].setShortDescription ( getLabel("DSC_DSClassName") );
      
            properties[PROPERTY_minConnections] = new PropertyDescriptor ( "minConnections", WS70JdbcResourceBean.class, "getMinConnections", "setMinConnections" );
            properties[PROPERTY_minConnections].setDisplayName ( getLabel("LBL_min_connections") );
            properties[PROPERTY_minConnections].setShortDescription ( getLabel("DSC_min_connections") );
            properties[PROPERTY_minConnections].setPropertyEditorClass ( Int0Editor.class );

            properties[PROPERTY_maxConnections] = new PropertyDescriptor ( "maxConnections", WS70JdbcResourceBean.class, "getMaxConnections", "setMaxConnections" );
            properties[PROPERTY_maxConnections].setDisplayName ( getLabel("LBL_max_connections") );
            properties[PROPERTY_maxConnections].setShortDescription ( getLabel("DSC_max_connections") );
            properties[PROPERTY_maxConnections].setPropertyEditorClass ( Int0Editor.class );
            
       
            
            properties[PROPERTY_idleTimeout] = new PropertyDescriptor ( "idleTimeout", WS70JdbcResourceBean.class, "getIdleTimeout", "setIdleTimeout" );
            properties[PROPERTY_idleTimeout].setDisplayName ( getLabel("LBL_connection_idle_timeout") );
            properties[PROPERTY_idleTimeout].setShortDescription ( getLabel("DSC_connection_idle_timeout") );
            properties[PROPERTY_idleTimeout].setPropertyEditorClass ( LongEditor.class );
            
            properties[PROPERTY_waitTimeout] = new PropertyDescriptor ( "waitTimeout", WS70JdbcResourceBean.class, "getWaitTimeout", "setWaitTimeout" );
            properties[PROPERTY_waitTimeout].setDisplayName ( getLabel("LBL_connection_wait_timeout") );
            properties[PROPERTY_waitTimeout].setShortDescription ( getLabel("DSC_connection_wait_timeout") );
            properties[PROPERTY_waitTimeout].setPropertyEditorClass ( LongEditor.class );
            
            properties[PROPERTY_isIsoLevGuaranteed] = new PropertyDescriptor ( "isolationLevelGuaranteed", WS70JdbcResourceBean.class, "getIsolationLevelGuaranteed", "setIsolationLevelGuaranteed" );
            properties[PROPERTY_isIsoLevGuaranteed].setDisplayName ( getLabel("LBL_is_isolation_level_guaranteed") );
            properties[PROPERTY_isIsoLevGuaranteed].setShortDescription ( getLabel("DSC_is_isolation_level_guaranteed") );
            properties[PROPERTY_isIsoLevGuaranteed].setPropertyEditorClass ( BooleanEditor.class );
           
            properties[PROPERTY_tranxIsoLevel] = new PropertyDescriptor ( "isolationLevel", WS70JdbcResourceBean.class, "getIsolationLevel", "setIsolationLevel" );
            properties[PROPERTY_tranxIsoLevel].setDisplayName ( getLabel("LBL_transaction_isolation_level") );
            properties[PROPERTY_tranxIsoLevel].setShortDescription ( getLabel("DSC_transaction_isolation_level") );
            properties[PROPERTY_tranxIsoLevel].setPropertyEditorClass ( IsolationLevelEditor.class );

            
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", WS70JdbcResourceBean.class, "getName", "setName" );
            properties[PROPERTY_name].setHidden ( true );            

            properties[PROPERTY_connValidation] = new PropertyDescriptor ( "connectionValidation", WS70JdbcResourceBean.class, "getConnectionValidation", "setConnectionValidation" );
            properties[PROPERTY_connValidation].setDisplayName ( getLabel("LBL_conn_valid_method") );
            properties[PROPERTY_connValidation].setShortDescription ( getLabel("DSC_conn_valid_method") );
            properties[PROPERTY_connValidation].setPropertyEditorClass ( ValidationMethodEditor.class );

            properties[PROPERTY_validationTableName] = new PropertyDescriptor ( "connectionValidationTablename", WS70JdbcResourceBean.class, "getConnectionValidationTablename", "setConnectionValidationTablename" );
            properties[PROPERTY_validationTableName].setDisplayName ( getLabel("LBL_validation_table_name") );
            properties[PROPERTY_validationTableName].setShortDescription ( getLabel("DSC_validation_table_name") );
            properties[PROPERTY_failAllConns] = new PropertyDescriptor ( "failAllConnections", WS70JdbcResourceBean.class, "getFailAllConnections", "setFailAllConnections" );
            properties[PROPERTY_failAllConns].setDisplayName ( getLabel("LBL_fail_all_connections") );
            properties[PROPERTY_failAllConns].setShortDescription ( getLabel("DSC_fail_all_connections") );
            properties[PROPERTY_failAllConns].setPropertyEditorClass ( BooleanEditor.class );            
        }
        catch( Exception e){
            e.printStackTrace();
        }              
        // Here you can add code for customizing the properties array.
        
        return properties;     }//GEN-LAST:Properties
    
    // Event set information will be obtained from introspection.//GEN-FIRST:Events
    private static EventSetDescriptor[] eventSets = null;
    private static EventSetDescriptor[] getEdescriptor(){
//GEN-HEADEREND:Events
        
        // Here you can add code for customizing the event sets array.
        
        return eventSets;     }//GEN-LAST:Events
    
    // Method information will be obtained from introspection.//GEN-FIRST:Methods
    private static MethodDescriptor[] methods = null;
    private static MethodDescriptor[] getMdescriptor(){
//GEN-HEADEREND:Methods
        
        // Here you can add code for customizing the methods array.
        
        return methods;     }//GEN-LAST:Methods
    
    
    private static int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static int defaultEventIndex = -1;//GEN-END:Idx
    
    
//GEN-FIRST:Superclass
    
    // Here you can add code for customizing the Superclass BeanInfo.
    
//GEN-LAST:Superclass
    
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     *
     * @return BeanDescriptor describing the editable
     * properties of this bean.  May return null if the
     * information should be obtained by automatic analysis.
     */
    public BeanDescriptor getBeanDescriptor() {
        return getBdescriptor();
    }
    
    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        return getPdescriptor();
    }
    
    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     *
     * @return  An array of EventSetDescriptors describing the kinds of
     * events fired by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return getEdescriptor();
    }
    
    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     *
     * @return  An array of MethodDescriptors describing the methods
     * implemented by this bean.  May return null if the information
     * should be obtained by automatic analysis.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        return getMdescriptor();
    }
    
    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are
     * customizing the bean.
     * @return  Index of default property in the PropertyDescriptor array
     * 		returned by getPropertyDescriptors.
     * <P>	Returns -1 if there is no default property.
     */
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }
    
    /**
     * A bean may have a "default" event that is the event that will
     * mostly commonly be used by human's when using the bean.
     * @return Index of default event in the EventSetDescriptor array
     *		returned by getEventSetDescriptors.
     * <P>	Returns -1 if there is no default event.
     */
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }
}

