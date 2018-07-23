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
import org.sample.registry.model.Entries;
import org.sample.registry.model.KnownTypes;
import org.sample.registry.model.Registry;
import org.sample.registry.model.RegistryComponent;
import org.sample.registry.model.RegistryVisitor;
import org.w3c.dom.Element;

public class RegistryImpl extends RegistryComponentImpl.Named implements Registry {

    public RegistryImpl(RegistryModelImpl model, Element e) {
        super(model, e);
    }
    
    public RegistryImpl(RegistryModelImpl model) {
        this(model, createElementNS(model, RegistryQNames.REGISTRY));
    }
    
    public void accept(RegistryVisitor visitor) {
        visitor.visit(this);
    }
    
    /**
     * Accessing methods for member 'knownTypes'
     */
    public KnownTypes getKnownTypes() {
        return getChild(KnownTypes.class);
    }
    public void setKnownTypes(KnownTypes types) {
        List<Class<? extends RegistryComponent>> empty = Collections.emptyList();
        setChild(KnownTypes.class, KNOWN_TYPES_PROPERTY, types, empty);
    }

    public void setEntries(Entries entries) {
        List<Class<? extends RegistryComponent>> empty = Collections.emptyList();
        setChild(KnownTypes.class, KNOWN_TYPES_PROPERTY, entries, empty);
    }

    public Entries getEntries() {
        return getChild(Entries.class);
    }
}
