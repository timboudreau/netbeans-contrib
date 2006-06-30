/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
