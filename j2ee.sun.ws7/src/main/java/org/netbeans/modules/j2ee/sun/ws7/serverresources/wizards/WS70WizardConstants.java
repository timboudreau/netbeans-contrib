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
 * WS70WizardConstants.java
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards;

import java.util.ResourceBundle;
import org.openide.util.NbBundle;

/**
 *
 * Code reused from Appserver common API module
 */
public interface WS70WizardConstants {

    //common
    public static final String __General = "general";
    public static final String __Properties = "properties";
    public static final String __PropertiesURL = "propertiesUrl";


    //jdbc-resource
    //Contains __Enabled, __Description
    public static final String __Name = "name";
    public static final String __DatasourceClassname = "datasource-class";
    public static final String __MinConnections = "min-connections";
    public static final String __MaxConnections = "max-connections";
    public static final String __IdleTimeout = "idle-timeout";
    public static final String __WaitTimeout = "wait-timeout";
    public static final String __IsolationLevel = "isolation-level";
    public static final String __IsolationLevelGuaranteed = "isolation-level-guaranteed";
    public static final String __ConnectionValidation = "connection-validation";
    public static final String __ConnectionValidationTableName = "connection-validation-table-name";
    public static final String __FailAllConnections = "fail-all-connections";
    public static final String __DatabaseVendor = "database-vendor";
    public static final String __DatabaseName = "databaseName";
    public static final String __Url = "URL";
    public static final String __User = "User";
    public static final String __Password = "Password";
    public static final String __NotApplicable = "NA";  
    
    public static final String __JdbcResource = "jdbc-resource";
    
    
    // common to resources
    public static final String __JndiName = "jndi-name";
    public static final String __Enabled = "enabled";
    public static final String __Description = "description";
    public static final String __ResType = "res-type";
    public static final String __FactoryClass = "factory-class";
    
    
    //mail-resource
    //Contains __JndiName and __Enabled and __Description  also
    public static final String __StoreProtocol = "store-protocol";
    public static final String __StoreProtocolClass = "store-protocol-class";
    public static final String __TransportProtocol = "transport-protocol";
    public static final String __TransportProtocolClass = "transport-protocol-class";
    public static final String __Host = "host";
    public static final String __MailUser = "user";
    public static final String __From = "from";
    public static final String __MailResource = "mail-resource";
    
    //custom-resource
    //Contains __JndiName, __ResType, __FactoryClass, and __Enabled and __Description   
    public static final String __CustomResource = "custom-resource";
    //external-jndi-resource
    //Contains __JndiName, _ResType, __FactoryClass, and __Enabled and __Description   
    public static final String __ExternalJndiResource = "external-jndi-resource";
    public static final String __ExternalJndiName = "external-jndi-name";
    
    
    //Default Names for the resources

    public static final String __JDBCResource = "jdbc";
    //public static final String __JMSResource = "jms";
    public static final String __MAILResource = "mail";
    public static final String __EXTERNALResource = "external";
    public static final String __CUSTOMResource = "custom"; 

    
    public static final String __SunResourceExt = "sun-ws7-resource";
    
    //First Step - temporary workaround
    public static final String __FirstStepChoose = "Choose ...";
    //Resource Folder
    public static final String __SunResourceFolder = "setup";   
       
}
