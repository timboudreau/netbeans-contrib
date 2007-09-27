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
