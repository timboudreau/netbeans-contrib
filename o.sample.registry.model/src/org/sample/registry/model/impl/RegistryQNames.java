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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;

public enum RegistryQNames {
    REGISTRY("registry"),
    ENTRIES("entries"),
    SERVICE("service"),
    SERVICE_PROVIDER("provider"),
    URL("url"),
    KNOWN_TYPES("known-types"),
    TYPE("type"),
    DEFINITION("definition"),
    DOCUMENTATION("documentation");
    
    public static final String REGISTRY_NS = "http://www.samples.org/registry";
    public static final String REGISTRY_PREFIX = "reg";
    
    private static Set<QName> mappedQNames = new HashSet<QName>();
    static {
        mappedQNames.add(REGISTRY.getQName());
        mappedQNames.add(ENTRIES.getQName());
        mappedQNames.add(SERVICE.getQName());
        mappedQNames.add(SERVICE_PROVIDER.getQName());
        //mappedQNames.add(URL.getQName());
        mappedQNames.add(KNOWN_TYPES.getQName());
        mappedQNames.add(TYPE.getQName());
        //mappedQNames.add(DEFINITION.getQName());
        //mappedQNames.add(DOCUMENTATION.getQName());
    }

    private QName qname;
    
    RegistryQNames(String localName) {
        qname = new QName(REGISTRY_NS, localName, REGISTRY_PREFIX);
    }
    
    public QName getQName() { 
        return qname; 
    }

    public String getLocalName() { 
        return qname.getLocalPart();
    }
    
    public String getQualifiedName() {
        return qname.getPrefix() + ":" + qname.getLocalPart();
    }
    
    public static Set<QName> getMappedQNames() {
        return Collections.unmodifiableSet(mappedQNames);
    }
}
