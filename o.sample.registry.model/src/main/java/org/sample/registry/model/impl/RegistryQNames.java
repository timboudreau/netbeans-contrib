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
import org.sample.registry.model.Entries;
import org.sample.registry.model.KnownTypes;
import org.sample.registry.model.Registry;
import org.sample.registry.model.Service;
import org.sample.registry.model.ServiceProvider;
import org.sample.registry.model.ServiceType;

public enum RegistryQNames {
    REGISTRY("registry"),
    ENTRIES(Registry.ENTRIES_PROPERTY),
    SERVICE(Entries.SERVICE_PROPERTY),
    SERVICE_PROVIDER(Service.SERVICE_PROVIDER_PROPERTY),
    URL(ServiceProvider.URL_PROPERTY),
    KNOWN_TYPES(Registry.KNOWN_TYPES_PROPERTY),
    TYPE(KnownTypes.TYPE_PROPERTY),
    DEFINITION(ServiceType.DEFINITION_PROPERTY),
    DOCUMENTATION(ServiceType.DOCUMENTATION_PROPERTY),
    REGISTRY_09(null, "registry", null),
    SERVICE_09(null, Entries.SERVICE_PROPERTY, null);
    
    public static final String REGISTRY_NS = "http://www.samples.org/registry";
    public static final String REGISTRY_PREFIX = "reg";
    
    private static Set<QName> mappedQNames = new HashSet<QName>();
    static {
        mappedQNames.add(REGISTRY.getQName());
        mappedQNames.add(REGISTRY_09.getQName());
        mappedQNames.add(ENTRIES.getQName());
        mappedQNames.add(SERVICE.getQName());
        mappedQNames.add(SERVICE_09.getQName());
        mappedQNames.add(SERVICE_PROVIDER.getQName());
        //mappedQNames.add(URL.getQName());
        mappedQNames.add(KNOWN_TYPES.getQName());
        mappedQNames.add(TYPE.getQName());
        //mappedQNames.add(DEFINITION.getQName());
        //mappedQNames.add(DOCUMENTATION.getQName());
    }

    private QName qname;
    
    RegistryQNames(String localName) {
        this(REGISTRY_NS, localName, REGISTRY_PREFIX);
    }
    
    RegistryQNames(String namespace, String localName, String prefix) {
        if (prefix == null) {
            qname = new QName(namespace, localName);
        } else {
            qname = new QName(namespace, localName, prefix);
        }
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
