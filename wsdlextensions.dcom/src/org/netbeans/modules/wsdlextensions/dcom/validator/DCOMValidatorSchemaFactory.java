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

package org.netbeans.modules.wsdlextensions.dcom.validator;

import java.io.InputStream;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.netbeans.modules.xml.wsdl.validator.spi.ValidatorSchemaFactory;

/**
 * This class implements ValidatorSchemaFactory interface.
 *
 * @author Sun Microsystems
 */
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.modules.xml.wsdl.validator.spi.ValidatorSchemaFactory.class)
public class DCOMValidatorSchemaFactory extends ValidatorSchemaFactory{
    static final String dcomXSDUrl = "/org/netbeans/modules/wsdlextensions/dcom/resources/dcom-ext.xsd";
    
    public String getNamespaceURI() {
        return "http://schemas.sun.com/jbi/wsdl-extensions/dcom/";
    }
    
    public InputStream getSchemaInputStream() {
        return DCOMValidatorSchemaFactory.class.getResourceAsStream(dcomXSDUrl);
    }
    
     /**
     * Returns the Inputstream related to this schema
     */
    public Source getSchemaSource() {
        InputStream in = DCOMValidatorSchemaFactory.class.getResourceAsStream(dcomXSDUrl);
        Source s = new StreamSource(in);
        s.setSystemId(DCOMValidatorSchemaFactory.class.getResource(dcomXSDUrl).toString());
        return s;
    }
}
