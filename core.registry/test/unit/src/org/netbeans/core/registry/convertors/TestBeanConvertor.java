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

package org.netbeans.core.registry.convertors;

import org.netbeans.api.convertor.ConvertorException;
import org.netbeans.spi.convertor.Convertor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestBeanConvertor implements Convertor {

    public static final String NS = "http://core.netbeans.org/registry/test/ns";

    public Object read(Element element) throws ConvertorException {
        if (!NS.equals(element.getNamespaceURI())) throw new ConvertorException("wrong ns");
        if (!"test-bean".equals(element.getLocalName())) throw new ConvertorException("wrong name");
        TestBean t = new TestBean();
        t.setProp1(element.getAttribute("prop1"));
        t.setProp2(element.getAttribute("prop2"));
        return t;
    }
    
    public Element write(Document doc, Object inst) {
        TestBean t = (TestBean)inst;
        Element element = doc.createElementNS(NS, "test-bean");
        element.setAttribute("prop1", t.getProp1());
        element.setAttribute("prop2", t.getProp2());
        return element;
    }
    
}
