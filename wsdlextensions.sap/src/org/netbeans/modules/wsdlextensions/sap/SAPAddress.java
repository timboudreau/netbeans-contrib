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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.wsdlextensions.sap;

/**
 *
 * Represents the address element under the wsdl port for SAP binding
 * <sap:address applicationServerHostname="sap50uni" clientNumber="800" systemNumber="00" 
 * systemID="EUC" user="DEMO" password="DEMO" language="EN" enableABAPDebugWindow="No" 
 * isSAPSystemUnicode="Yes" gatewayHostname="sap50uni" gatewayService="sapgw00">
 *
 * @author Sun Microsystems
*/
public interface SAPAddress extends SAPComponent {

    public static final String SAPADDR_APPSERVERHOST = "applicationServerHostname";
    public static final String SAPADDR_CLIENTNUM = "clientNumber";
    public static final String SAPADDR_SYSNUM = "systemNumber";
    public static final String SAPADDR_SYSID = "systemID";
    public static final String SAPADDR_USER = "user";
    public static final String SAPADDR_PW = "password";
    public static final String SAPADDR_LANG = "language";
    public static final String SAPADDR_ABAPDEBUG = "enableABAPDebugWindow";
    public static final String SAPADDR_ISUNI = "isSAPSystemUnicode";
    public static final String SAPADDR_GWHOST = "gatewayHostname";
    public static final String SAPADDR_GWSERVICE = "gatewayService";
    public static final String SAPADDR_ROUTERSTR = "routerString";
    
    public void setApplicationServer(String applicationServer);
    
    public String getApplicationServer(); 
    
    public void setClientNumber(String clientNumber);

    public String getClientNumber();
    
    public void setSystemNumber(String systemNumber);

    public String getSystemNumber();
    
    public void setSystemId(String systemId);

    public String getSystemId();
    
    public void setUsername(String username);

    public String getUsername();
    
    public void setPassword(String password);

    public String getPassword();
    
    public void setLanguage(String language);

    public String getLanguage();
    
    public void setGateway(String gateway);

    public String getGateway();
    
    public void setGatewayService(String gatewayService);

    public String getGatewayService();
    
    public void setRouterString(String routerString);

    public String getRouterString();
    
    public void IsSapUnicode(Boolean isSapUnicode);

    public Boolean getIsSapUnicode();
    
    public void setEnableAbapDebugWindow(Boolean enableAbapDebugWindow);

    public Boolean getEnableAbapDebugWindow();
    
    
}
