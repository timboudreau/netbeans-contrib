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

package org.netbeans.modules.wsdlextensions.dcom.impl;

import org.netbeans.modules.xml.wsdl.model.WSDLModel;

import org.netbeans.modules.wsdlextensions.dcom.DCOMAddress;
import org.netbeans.modules.wsdlextensions.dcom.DCOMComponent;
import org.netbeans.modules.wsdlextensions.dcom.DCOMQName;

import org.w3c.dom.Element;

public class DCOMAddressImpl extends DCOMComponentImpl implements DCOMAddress {

    public DCOMAddressImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public DCOMAddressImpl(WSDLModel model){
        this(model, createPrefixedElement(DCOMQName.ADDRESS.getQName(), model));
    }
    
    public void accept(DCOMComponent.Visitor visitor) {
        visitor.visit(this);
    }

    public void setDCOMDOMAIN(String domain) {
        setAttribute(DCOMAddress.DCOM_DOMAIN, DCOMAttribute.DCOM_DOMAIN, domain);
    }

    public String getDCOMDOMAIN() {
        return getAttribute(DCOMAttribute.DCOM_DOMAIN);
    }

    public void setDCOMSERVER(String server) {
        setAttribute(DCOMAddress.DCOM_SERVER, DCOMAttribute.DCOM_SERVER, server);
    }

    public String getDCOMSERVER() {
        return getAttribute(DCOMAttribute.DCOM_SERVER);
    }

    public void setDCOMUSERNAME(String username) {
        setAttribute(DCOMAddress.DCOM_USERNAME, DCOMAttribute.DCOM_USERNAME, username);
    }

    public String getDCOMUSERNAME() {
        return getAttribute(DCOMAttribute.DCOM_USERNAME);
    }

    public void setDCOMPASSWORD(String password) {
        setAttribute(DCOMAddress.DCOM_PASSWORD, DCOMAttribute.DCOM_PASSWORD, password);
    }

    public String getDCOMPASSWORD() {
        return getAttribute(DCOMAttribute.DCOM_PASSWORD);
    }

}
