/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
