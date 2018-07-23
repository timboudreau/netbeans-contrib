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
package org.sample.registry.model.v09.impl;

import java.util.Collection;
import org.sample.registry.model.RegistryVisitor;
import org.sample.registry.model.impl.RegistryComponentImpl;
import org.sample.registry.model.impl.RegistryModelImpl;
import org.sample.registry.model.impl.RegistryQNames;
import org.sample.registry.model.v09.Registry09;
import org.sample.registry.model.v09.Service09;
import org.w3c.dom.Element;

public class RegistryImpl extends RegistryComponentImpl.Named implements Registry09 {

    public RegistryImpl(RegistryModelImpl model, Element e) {
        super(model, e);
    }
    
    public RegistryImpl(RegistryModelImpl model) {
        this(model, createElementNS(model, RegistryQNames.REGISTRY_09));
    }
    
    public void accept(RegistryVisitor visitor) {
        visitor.visit(this);
    }
    
    /**
     * Accessing method for member 'services'
     */
    public Collection<Service09> getServices() {
        return super.getChildren(Service09.class);
    }
    public void removeService(Service09 service) {
        super.removeChild(SERVICE_PROPERTY, service);
    }
    public void addService(Service09 service) {
        super.appendChild(SERVICE_PROPERTY, service);
    }
    
}
