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

package org.netbeans.modules.primitivesettings;

import org.netbeans.api.convertor.ConvertorException;
import org.netbeans.spi.convertor.Convertor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author vita
 */
public class PrimitiveSettingConvertor implements Convertor {
    
    public static final String NAMESPACE = "http://www.netbeans.org/ns/primitiveSetting";
    public static final String ROOT = "primitive-setting";
    
    /** Creates a new instance of PrimitiveSettingConvertor */
    public PrimitiveSettingConvertor() {
    }

    public Element write(Document doc, Object inst) {
        Element element = doc.createElementNS(NAMESPACE, ROOT);
        Element propertyNode;
        
        if (inst == null) {
            propertyNode = doc.createElement(PrimitiveType.NULL.getTypeName());
        } else {
            PrimitiveType type = PrimitiveType.findPrimitiveType(inst.getClass());
            if (type != null) {
                propertyNode = doc.createElement(type.getTypeName());
                propertyNode.setTextContent(inst.toString());
            } else {
                throw new ConvertorException("The object " + inst + " is not a primitive type.");
            }
        }
        
        element.appendChild(propertyNode);
        return element;
    }

    public Object read(Element element) {
        Element propertyNode = getFirstSubElement(element);
        if (propertyNode != null) {
            String propertyTypeName = propertyNode.getNodeName();
            PrimitiveType type = PrimitiveType.findPrimitiveType(propertyTypeName);
            if (type != null) {
                return type.createFromString(propertyNode.getTextContent());
            } else {
                throw new ConvertorException("The property name " + propertyNode + " is not a primitive type.");
            }
        } else {
            // No property sub-node is equivalent to <nullValue/>
            return null;
        }
    }

    private Element getFirstSubElement(Element element) {
        NodeList nodeList = element.getChildNodes();
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return (Element) nodeList.item(i);
            }
        }
        
        return null;
    }
}
