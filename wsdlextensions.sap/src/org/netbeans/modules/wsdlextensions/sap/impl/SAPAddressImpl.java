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

package org.netbeans.modules.wsdlextensions.sap.impl;

import java.lang.Boolean;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;

import org.netbeans.modules.wsdlextensions.sap.SAPAddress;
import org.netbeans.modules.wsdlextensions.sap.SAPComponent;
import org.netbeans.modules.wsdlextensions.sap.SAPQName;

import org.w3c.dom.Element;

public class SAPAddressImpl extends SAPComponentImpl implements SAPAddress {
    public SAPAddressImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public SAPAddressImpl(WSDLModel model){
        this(model, createPrefixedElement(SAPQName.ADDRESS.getQName(), model));
    }
    
    public void accept(SAPComponent.Visitor visitor) {
        visitor.visit(this);
    }

/*
    
    public void setSAPAddressClient(SAPAddressClient client) {
        mSAPAddressClient = client;
    }
    
    public SAPAddressClient getSAPAddressClient() {
        return mSAPAddressClient;
    }
    
    public void setSAPAddressServer(SAPAddressServer server) {
        mSAPAddressServer = server;
    }
    
    public SAPAddressServer getSAPAddressServer() {
        return mSAPAddressServer;
    }
*/    
    
    public void setApplicationServer(String applicationServer) {
        setAttribute(SAPAddress.SAPADDR_APPSERVERHOST, SAPAttribute.SAPADDR_APPSERVERHOST, applicationServer);
    }

    public String getApplicationServer() {
         return getAttribute(SAPAttribute.SAPADDR_APPSERVERHOST);
    }
    
    public void setClientNumber(String clientNumber) {
        setAttribute(SAPAddress.SAPADDR_CLIENTNUM, SAPAttribute.SAPADDR_CLIENTNUM, clientNumber);
    }

    public String getClientNumber() {
        return getAttribute(SAPAttribute.SAPADDR_CLIENTNUM);
    }
    
    public void setSystemNumber(String systemNumber) {
        setAttribute(SAPAddress.SAPADDR_SYSNUM, SAPAttribute.SAPADDR_SYSNUM, systemNumber);
    }

    public String getSystemNumber() {
         return getAttribute(SAPAttribute.SAPADDR_SYSNUM);
    }
    
    public void setSystemId(String systemId) {
        setAttribute(SAPAddress.SAPADDR_SYSID, SAPAttribute.SAPADDR_SYSID, systemId);
    }

    public String getSystemId() {
        return getAttribute(SAPAttribute.SAPADDR_SYSID);
    }
    
    public void setUsername(String username) {
        setAttribute(SAPAddress.SAPADDR_USER, SAPAttribute.SAPADDR_USER, username);
    }

    public String getUsername() {
        return getAttribute(SAPAttribute.SAPADDR_USER);
    }
    
    public void setPassword(String password) {
        setAttribute(SAPAddress.SAPADDR_PW, SAPAttribute.SAPADDR_PW, password);
    }

    public String getPassword() {
        return getAttribute(SAPAttribute.SAPADDR_PW);
    }
    
    public void setLanguage(String language) {
        setAttribute(SAPAddress.SAPADDR_LANG, SAPAttribute.SAPADDR_LANG, language);
    }

    public String getLanguage() {
        return getAttribute(SAPAttribute.SAPADDR_LANG);
    }
    
    public void setGateway(String gateway) {
        setAttribute(SAPAddress.SAPADDR_GWHOST, SAPAttribute.SAPADDR_GWHOST, gateway);
    }

    public String getGateway() {
        return getAttribute(SAPAttribute.SAPADDR_GWHOST);
    }
    
    public void setGatewayService(String gatewayService) {
        setAttribute(SAPAddress.SAPADDR_GWSERVICE, SAPAttribute.SAPADDR_GWSERVICE, gatewayService);
    }

    public String getGatewayService() {
        return getAttribute(SAPAttribute.SAPADDR_GWSERVICE);
    }
    
    public void setRouterString(String routerString) {
        setAttribute(SAPAddress.SAPADDR_ROUTERSTR, SAPAttribute.SAPADDR_ROUTERSTR, routerString);
    }

    public String getRouterString() {
        return getAttribute(SAPAttribute.SAPADDR_ROUTERSTR);
    }
    
    public void IsSapUnicode(Boolean isSapUnicode) {
        setAttribute(SAPAddress.SAPADDR_ISUNI, SAPAttribute.SAPADDR_ISUNI, isSapUnicode);
    }

    public Boolean getIsSapUnicode() {
        return new Boolean(getAttribute(SAPAttribute.SAPADDR_ISUNI));
    }
    
    public void setEnableAbapDebugWindow(Boolean enableAbapDebugWindow) {
        setAttribute(SAPAddress.SAPADDR_ABAPDEBUG, SAPAttribute.SAPADDR_ABAPDEBUG, enableAbapDebugWindow);
    }

    public Boolean getEnableAbapDebugWindow() {
         return new Boolean(getAttribute(SAPAttribute.SAPADDR_ABAPDEBUG));
   }
}
