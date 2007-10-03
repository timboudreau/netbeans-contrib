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
 * WS70ExternalResourceBean.java
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.beans;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.WS70ExternalJndiResource;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.WS70Resources;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.PropertyElement;

import java.util.Vector;
import org.netbeans.modules.j2ee.sun.ide.editors.NameValuePair;

/**
 *
 * @author Administrator
 */
public class WS70ExternalResourceBean extends WS70BaseResourceBean implements java.io.Serializable{
    
    private String externalJndiName;
    private String resType;
    private String factoryClass;
    
    /**
     * Creates a new instance of WS70ExternalResourceBean
     */
    public WS70ExternalResourceBean() {
    }
    public String getExternalJndiName() {
        return externalJndiName;
    }
    public void setExternalJndiName(String value) {
        String oldValue = externalJndiName;
        this.externalJndiName = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("externalJndiName", oldValue, externalJndiName);//NOI18N
    }
    public String getResType() {
        return resType;
    }
    public void setResType(String value) {
        String oldValue = resType;
        this.resType = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("resType", oldValue, resType);//NOI18N
    }    
    public String getFactoryClass() {
        return factoryClass;
    }
    public void setFactoryClass(String value) {
        String oldValue = factoryClass;
        this.factoryClass = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("factoryClass", oldValue, factoryClass);//NOI18N
    }    

    public static WS70ExternalResourceBean createBean(WS70ExternalJndiResource externaljndiresource) {
        WS70ExternalResourceBean bean = new WS70ExternalResourceBean();
        //name attribute in bean is for studio display purpose. 
        //It is not part of the external-resource dtd.
        bean.setName(externaljndiresource.getJndiName());
        bean.setDescription(externaljndiresource.getDescription());
        bean.setJndiName(externaljndiresource.getJndiName());
        bean.setExternalJndiName(externaljndiresource.getExternalJndiName());
        bean.setFactoryClass(externaljndiresource.getFactoryClass());
        bean.setResType(externaljndiresource.getResType());
        bean.setIsEnabled(externaljndiresource.getEnabled());
           
        PropertyElement[] extraProperties = externaljndiresource.getPropertyElement();
        Vector vec = new Vector();       
        for (int i = 0; i < extraProperties.length; i++) {
            NameValuePair pair = new NameValuePair();
            pair.setParamName(extraProperties[i].getName());
            pair.setParamValue(extraProperties[i].getValue());
            vec.add(pair);
        }
        
        if (vec != null && vec.size() > 0) {
            NameValuePair[] props = new NameValuePair[vec.size()];
            bean.setExtraParams((NameValuePair[])vec.toArray(props));
        } 
        
        return bean;
    }
    
    public WS70Resources getGraph(){
        WS70Resources res = getResourceGraph();
        WS70ExternalJndiResource extresource = res.newWS70ExternalJndiResource();
        extresource.setDescription(getDescription());
        extresource.setJndiName(getJndiName());
        extresource.setExternalJndiName(getExternalJndiName());
        extresource.setResType(getResType());
        extresource.setFactoryClass(getFactoryClass());
        extresource.setEnabled(getIsEnabled());
        
        // set properties
        NameValuePair[] params = getExtraParams();
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                NameValuePair pair = params[i];
                PropertyElement prop = extresource.newPropertyElement();
                prop = populatePropertyElement(prop, pair);
                extresource.addPropertyElement(prop);
            }
        }  
        
        res.addWS70ExternalJndiResource(extresource);
        return res;
    }    
}
