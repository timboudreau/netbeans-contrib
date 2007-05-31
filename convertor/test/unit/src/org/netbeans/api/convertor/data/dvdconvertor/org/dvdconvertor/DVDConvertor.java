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
