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

package org.shoppingcartconvertor;

import java.io.IOException;
import java.util.Iterator;
import org.netbeans.api.convertor.book.Book;
import org.netbeans.api.convertor.ConvertorDescriptor;
import org.netbeans.api.convertor.Convertors;
import org.netbeans.api.convertor.dvd.DVD;
import org.netbeans.api.convertor.shoppingcart.ShoppingCart;
import org.netbeans.spi.convertor.Convertor;
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
public class ShoppingCartConvertor implements Convertor {

    private static final String NAMESPACE = "http://www.netbeans.org/ns/shoppingcart";
    
    public ShoppingCartConvertor() {
    }

    public Object read(org.w3c.dom.Element element) {
        ShoppingCart sc = new ShoppingCart();
        // assert element == <shoppingcart>
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element)node;
                if (Convertors.canRead(e)) {
                    Object o = Convertors.read(e);
                    if (o instanceof Book) {
                      sc.addBook((Book)o);  
                    } else if (o instanceof DVD) {
                      sc.addDVD((DVD)o);  
                    } else {
                        //ErrorManager.getDefault().log(ErrorManager.WARNING, "Shopping cart contains unknown item: "+o);
                        System.err.println("Shopping cart contains unknown item: "+o);
                    }
                } else {
                    //ErrorManager.getDefault().log(ErrorManager.WARNING, "Shopping cart contains item which cannot be convertor: "+e);
                    System.err.println("Shopping cart contains item which cannot be convertor: "+e);
                }
            }
        }
        return sc;
    }
    
    public org.w3c.dom.Element write(Document doc, Object inst) {
        ShoppingCart sc = (ShoppingCart)inst;
        Element element = doc.createElementNS(NAMESPACE, "shoppingcart");

        Iterator it = sc.books.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            Element e = Convertors.write(doc, o);
            element.appendChild(e);
        }
        
        it = sc.dvds.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            Element e = Convertors.write(doc, o);
            element.appendChild(e);
        }
        
        return element;
    }
    
}
