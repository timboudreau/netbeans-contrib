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

package org.netbeans.modules.wsdlextensions.email.impl.imap;

import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.wsdlextensions.email.imap.IMAPAddress;
import org.netbeans.modules.wsdlextensions.email.imap.IMAPComponent;
import org.netbeans.modules.wsdlextensions.email.imap.IMAPQName;
//import org.netbeans.modules.wsdlextensions.email.validator.EMAILAddressURL;
import org.netbeans.modules.wsdlextensions.email.impl.EMAILAttribute;
import org.w3c.dom.Element;

/**
 *
 * @author Sainath Adiraju
 */

public class IMAPAddressImpl extends IMAPComponentImpl implements IMAPAddress {
    public IMAPAddressImpl(WSDLModel model, Element e) {
        super(model, e);
    }
    
    public IMAPAddressImpl(WSDLModel model){
        this(model, createPrefixedElement(IMAPQName.ADDRESS.getQName(), model));
    }
    
    public void accept(IMAPComponent.Visitor visitor) {
        visitor.visit(this);
    }

     
    public String getEMAILServer() {
        return getAttribute(IMAPAddress.ATTR_EMAILSERVER);
    }

    public void setEMAILServer(String val) {
        setAttribute(IMAPAddress.ATTR_EMAILSERVER, EMAILAttribute.EMAIL_SERVER_NAME, val);
    }
}
