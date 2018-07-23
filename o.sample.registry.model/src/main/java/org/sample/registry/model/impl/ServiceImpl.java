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
import java.util.List;
import org.sample.registry.model.RegistryComponent;
import org.sample.registry.model.RegistryVisitor;
import org.sample.registry.model.Service;
import org.sample.registry.model.ServiceProvider;
import org.sample.registry.model.ServiceType;
import org.w3c.dom.Element;

public class ServiceImpl extends RegistryComponentImpl.Named implements Service {
    
    public ServiceImpl(RegistryModelImpl model, Element e) {
        super(model, e);
    }
    
    public ServiceImpl(RegistryModelImpl model) {
        this(model, createElementNS(model, RegistryQNames.SERVICE));
    }

    public Reference<ServiceType> getServiceType() {
        String type = getAttribute(RegistryAttributes.TYPE);
        if (type != null) {
            return new Reference<ServiceType>(type, ServiceType.class, this);
        } else {
            return null;        
        }
    }
    
    public void setServiceType(Reference<ServiceType> type) {
        String v = type == null ? null : type.getRefString();
        setAttribute(SERVICE_TYPE_PROPERTY, RegistryAttributes.TYPE, v);
    }
    
    public Integer getCapacity() {
        String v = getAttribute(RegistryAttributes.CAPACITY);
        if (v != null) {
            return Integer.valueOf(v);
        } else {
            return null;
        }
    }
    
    public void setCapacity(Integer capacity) {
        super.setAttribute(CAPACITY_PROPERTY, RegistryAttributes.CAPACITY, capacity);
    }
    
    public ServiceProvider getProvider() {
        return getChild(ServiceProvider.class);
    }
    
    public void setProvider(ServiceProvider provider) {
        List<Class<? extends RegistryComponent>> empty = Collections.emptyList();
        setChild(ServiceProvider.class, SERVICE_PROVIDER_PROPERTY, provider, empty);
    }

    public void accept(RegistryVisitor visitor) {
        visitor.visit(this);
    }
}
