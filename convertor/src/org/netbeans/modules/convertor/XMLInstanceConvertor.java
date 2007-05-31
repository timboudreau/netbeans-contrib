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

package org.netbeans.modules.convertor;

import java.util.Properties;
import org.netbeans.api.convertor.ConvertorException;
import org.netbeans.spi.convertor.Convertor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author  David Konecny
 */

final public class XMLInstanceConvertor implements Convertor {

    public XMLInstanceConvertor() {
    }

    public Object read(Element element) {
        assert element.getNodeName().equals("instance") : "Element "+ // NOI18N
            element+" is not <instance> element"; // NOI18N
            
        String clazz = null;
        String method = null;
        Properties properties = null;
        
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element)node;
                if (e.getNodeName().equals("class")) { // NOI18N
                    clazz = PropertiesConvertor.getTextValue(e);
                }
                if (e.getNodeName().equals("method")) { // NOI18N
                    method = PropertiesConvertor.getTextValue(e);
                }
                if (e.getNodeName().equals("property")) { // NOI18N
                    if (properties == null) {
                        properties = new Properties();
                    }
                    String propName = e.getAttribute("name"); // NOI18N
                    String propVal = PropertiesConvertor.getTextValue(e);
                    properties.setProperty(propName, propVal);
                }
            }
        }

        if (clazz != null) {
            return InstanceUtils.newValue(clazz, properties);
        } else if (method != null) {
            return InstanceUtils.methodValue(method, properties);
        } else {
            throw new ConvertorException("Do not know how to create instance. " + // NOI18N
                "The <method> nor <class> element not found in "+element); // NOI18N
        }
    }
    
    public Element write(Document doc, Object inst) {
        throw new ConvertorException("InstanceConvertor does "+ // NOI18N
            "not support writing."); // NOI18N
    }

}
