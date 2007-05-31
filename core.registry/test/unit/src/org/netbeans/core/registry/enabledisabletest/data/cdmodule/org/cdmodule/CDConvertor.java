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

package org.cdmodule;

import org.netbeans.spi.convertor.Convertor;
import org.w3c.dom.*;


/**
 *
 * @author  David Konecny
 */
public class CDConvertor implements Convertor {

    private static final String NAMESPACE = "http://www.netbeans.org/ns/cd2";

    public CDConvertor() {
    }

    public Object read(org.w3c.dom.Element element) {
        String artist = null;
        String album = null;
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element)node;
                if (e.getNodeName().equals("artist")) {
                    artist = getTextValue(e);
                }
                if (e.getNodeName().equals("album")) {
                    album = getTextValue(e);
                }
            }
        }
        return new CD(artist, album);
    }
    
    public org.w3c.dom.Element write(Document doc, Object inst) {
        CD cd = (CD)inst;
        Element element = doc.createElementNS(NAMESPACE, "cd");
        
        Element e = doc.createElementNS(NAMESPACE, "artist");
        Text t = doc.createTextNode(cd.artist);
        e.appendChild(t);
        element.appendChild(e);
        
        e = doc.createElementNS(NAMESPACE, "album");
        t = doc.createTextNode(cd.album);
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
