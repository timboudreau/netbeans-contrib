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

import org.netbeans.modules.xml.xam.NamedReferenceable;
import org.sample.registry.model.RegistryComponent;
import org.sample.registry.model.RegistryModel;
import org.sample.registry.model.RegistryVisitor;
import org.sample.registry.model.ServiceType;
import org.sample.registry.model.impl.RegistryQNames;
import org.w3c.dom.Element;

public class ServiceTypeImpl extends RegistryComponentImpl.Named
        implements ServiceType, NamedReferenceable<RegistryComponent> {

    public ServiceTypeImpl(RegistryModelImpl model, Element e) {
        super(model, e);
    }
    
    public ServiceTypeImpl(RegistryModelImpl model) {
        this(model, createElementNS(model, RegistryQNames.TYPE));
    }
    
    public String getDefinition() {
        return getChildElementText(RegistryQNames.DEFINITION.getQName());
    }
    
    public void setDefinition(String definition) {
        setChildElementText(DEFINITION_PROPERTY, definition, RegistryQNames.DEFINITION.getQName());
    }
    
    public String getDocumentation() {
        return getChildElementText(RegistryQNames.DOCUMENTATION.getQName());
    }
    
    public void setDocumentation(String documentation) {
        setText(DOCUMENTATION_PROPERTY, documentation);
    }

    public void accept(RegistryVisitor visitor) {
        visitor.visit(this);
    }
}
