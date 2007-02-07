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

import org.sample.registry.model.ReferenceableRegistryComponent;
import org.sample.registry.model.RegistryCommon;
import org.sample.registry.model.RegistryVisitor;
import org.sample.registry.model.Service;

/**
 * This visitor assume only components of ServiceType and Service has unique name 
 * among themselvelves within the same document.
 */

public class FindByNameAndTypeVisitor<T extends ReferenceableRegistryComponent>
        extends RegistryVisitor.Deep {
    
    private String name;
    private Class<T> type;
    private T found;
    
    public FindByNameAndTypeVisitor() {
    }

    public T find(RegistryCommon registry, String name, Class<T> type) {
        if (name == null || type == null) {
            return null;
        }
        
        this.name = name;
        this.type = type;
        registry.accept(this);
        return found;
    }

    public void visit(Service component) {
        if (name.equals(component.getName())) {
            found = type.cast(component);
            return;
        }
    }
}

