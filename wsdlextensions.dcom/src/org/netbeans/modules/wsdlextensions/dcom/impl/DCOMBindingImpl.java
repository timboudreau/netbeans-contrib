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

import org.netbeans.modules.wsdlextensions.dcom.DCOMBinding;
import org.netbeans.modules.wsdlextensions.dcom.DCOMComponent;
import org.netbeans.modules.wsdlextensions.dcom.DCOMQName;

import org.w3c.dom.Element;

public class DCOMBindingImpl extends DCOMComponentImpl implements DCOMBinding {
    public DCOMBindingImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public DCOMBindingImpl(WSDLModel model){
        this(model, createPrefixedElement(DCOMQName.BINDING.getQName(), model));
    }
    
    public void accept(DCOMComponent.Visitor visitor) {
        visitor.visit(this);
    }

	public void setDCOMUUID(String uuid) {
        setAttribute(DCOMBinding.DCOM_UUID, DCOMAttribute.DCOM_UUID, uuid);
    }

    public String getDCOMUUID() {
        return getAttribute(DCOMAttribute.DCOM_UUID);
    }
}
