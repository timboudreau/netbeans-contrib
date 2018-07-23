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

package org.dvdconvertor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.netbeans.api.convertor.ConvertorDescriptor;
import org.netbeans.spi.convertor.Convertor;
import org.netbeans.api.convertor.dvd.DVD;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 *
 * @author  David Konecny
 */
public class DVDConvertor implements Convertor {

    private static final String NAMESPACE = "http://www.netbeans.org/ns/dvd";
    
    public DVDConvertor() {
    }

    public Object read(org.w3c.dom.Element element) {
        int id = 0;
        String title = null;
        String publisher = null;
        int price = 0;
        // assert element == <dvd>
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element)node;
                if (e.getNodeName().equals("id")) {
                    id = Integer.parseInt(getTextValue(e));
                }
                if (e.getNodeName().equals("title")) {
                    title = getTextValue(e);
                }
                if (e.getNodeName().equals("publisher")) {
                    publisher = getTextValue(e);
                }
                if (e.getNodeName().equals("price")) {
                    price = Integer.parseInt(getTextValue(e));
                }
            }
        }
        return new DVD(id, title, publisher, price);
    }
    
    public org.w3c.dom.Element write(Document doc, Object inst) {
        DVD dvd = (DVD)inst;
        Element element = doc.createElementNS(NAMESPACE, "dvd");
        
        Element e = doc.createElementNS(NAMESPACE, "id");
        Text t = doc.createTextNode(Integer.toString(dvd.ID));
        e.appendChild(t);
        element.appendChild(e);
        
        e = doc.createElementNS(NAMESPACE, "title");
        t = doc.createTextNode(dvd.title);
        e.appendChild(t);
        element.appendChild(e);
        
        e = doc.createElementNS(NAMESPACE, "publisher");
        t = doc.createTextNode(dvd.publisher);
        e.appendChild(t);
        element.appendChild(e);
        
        e = doc.createElementNS(NAMESPACE, "price");
        t = doc.createTextNode(Integer.toString(dvd.price));
        e.appendChild(t);
        element.appendChild(e);
        
        return element;
    }

    // THIS IMPL IS NOT ROBUST. DO NOT REUSE IT!
    private String getTextValue(Element element) {
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                return ((Text)node).getData();
            }
        }
        return null;
    }
    
}
