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
package org.sample.registry.model.impl;

import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.netbeans.modules.xml.xam.Nameable;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentComponent;
import org.netbeans.modules.xml.xam.dom.Attribute;
import org.sample.registry.model.RegistryComponent;
import org.sample.registry.model.RegistryModel;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class RegistryComponentImpl extends AbstractDocumentComponent<RegistryComponent> 
            implements RegistryComponent {
    
    public RegistryComponentImpl(RegistryModelImpl model, Element element) {
        super(model, element);
    }
    
    public RegistryModelImpl getModel() {
        return (RegistryModelImpl) super.getModel();
    }

    static public Element createElementNS(RegistryModel model, RegistryQNames rq) {
        QName q = rq.getQName();
        /*if (XMLConstants.NULL_NS_URI.equals(q.getNamespaceURI())) {
            return model.getDocument().createElement(q.getLocalPart());
        } else*/ {
            return model.getDocument().createElementNS(q.getNamespaceURI(), rq.getQualifiedName());
        }
    }
    
    protected Object getAttributeValueOf(Attribute attr, String stringValue) {
        return stringValue;
    }

    protected void populateChildren(List<RegistryComponent> children) {
        NodeList nl = getPeer().getChildNodes();
        if (nl != null){
            for (int i = 0; i < nl.getLength(); i++) {
                org.w3c.dom.Node n = nl.item(i);
                if (n instanceof Element) {
                    RegistryModel model = getModel();
                    RegistryComponent comp = (RegistryComponent) model.getFactory().create((Element)n, this);
                    if (comp != null) {
                        children.add(comp);
                    }
                }
            }
        }
    }
    
    public static abstract class Named extends RegistryComponentImpl implements Nameable<RegistryComponent> {
        public Named(RegistryModelImpl model, Element element) {
            super(model, element);
        }
        public String getName() {
            return super.getAttribute(RegistryAttributes.NAME);
        }
        
        public void setName(String name) {
            super.setAttribute(Nameable.NAME_PROPERTY, RegistryAttributes.NAME, name);
        }
    }
    
}
