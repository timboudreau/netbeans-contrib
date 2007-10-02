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

package org.netbeans.modules.wsdlextensions.exec.impl;

import org.netbeans.modules.wsdlextensions.exec.ExecAddress;
import org.netbeans.modules.wsdlextensions.exec.ExecQName;

import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.w3c.dom.Element;

/**
 *
 * ExecAddressImpl
 */
public class ExecAddressImpl extends ExecComponentImpl implements ExecAddress {

    public ExecAddressImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public ExecAddressImpl(WSDLModel model){
        this(model, createPrefixedElement(ExecQName.ADDRESS.getQName(), model));
    }

    public String getHostName() {
        return getAttribute(ExecAttribute.EXEC_ADDRESS_HOST_NAME);
    }
        
    public void setHostName(String val) {
        setAttribute(ExecAddress.ATTR_HOST_NAME, 
                     ExecAttribute.EXEC_ADDRESS_HOST_NAME,
                     val);
    }
    
    public String getUserName() {
        return getAttribute(ExecAttribute.EXEC_ADDRESS_USER_NAME);
    }
    
    public void setUserName(String val) {
        setAttribute(ExecAddress.ATTR_USER_NAME, 
                     ExecAttribute.EXEC_ADDRESS_USER_NAME,
                     val);                
    }

    public String getPassword() {
        return getAttribute(ExecAttribute.EXEC_ADDRESS_PASSWORD);        
    }
    
    public void setPassword(String val) {
        setAttribute(ExecAddress.ATTR_PASSWORD, 
                     ExecAttribute.EXEC_ADDRESS_PASSWORD,
                     val);        
    }
}
