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

import org.netbeans.modules.xml.xam.ComponentUpdater;
import org.netbeans.modules.xml.xam.ComponentUpdater.Operation;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentComponent;
import org.sample.registry.model.Entries;
import org.sample.registry.model.KnownTypes;
import org.sample.registry.model.Registry;
import org.sample.registry.model.RegistryComponent;
import org.sample.registry.model.RegistryVisitor;
import org.sample.registry.model.Service;
import org.sample.registry.model.ServiceProvider;
import org.sample.registry.model.ServiceType;

public class SyncUpdateVisitor extends RegistryVisitor.Default implements ComponentUpdater<RegistryComponent> {
    private RegistryComponent target;
    private Operation operation;
    private int index;
    
    public SyncUpdateVisitor() {
    }

    public void update(RegistryComponent target, RegistryComponent child, Operation operation) {
        update(target, child, -1 , operation);
    }

    public void update(RegistryComponent target, RegistryComponent child, int index, Operation operation) {
        assert target != null;
        assert child != null;
        this.target = target;
        this.index = index;
        this.operation = operation;
        child.accept(this);
    }

    private void insert(String propertyName, RegistryComponent component) {
        ((RegistryComponentImpl)target).insertAtIndex(propertyName, component, index);
    }
    
    private void remove(String propertyName, RegistryComponent component) {
        ((RegistryComponentImpl)target).removeChild(propertyName, component);
    }
    
    public void visit(ServiceProvider component) {
        if (target instanceof Service) {
            if (operation == Operation.ADD) {
                insert(Service.SERVICE_PROVIDER_PROPERTY, component);
            } else {
                remove(Service.SERVICE_PROVIDER_PROPERTY, component);
            }
        }
    }

    public void visit(Entries component) {
        if (target instanceof Registry) {
            if (operation == Operation.ADD) {
                insert(Registry.ENTRIES_PROPERTY, component);
            } else {
                remove(Registry.ENTRIES_PROPERTY, component);
            }
        }
    }

    public void visit(ServiceType component) {
        if (target instanceof KnownTypes) {
            if (operation == Operation.ADD) {
                insert(KnownTypes.TYPE_PROPERTY, component);
            } else {
                remove(KnownTypes.TYPE_PROPERTY, component);
            }
        }
    }

    public void visit(Service component) {
        if (target instanceof Entries) {
            if (operation == Operation.ADD) {
                insert(Entries.SERVICE_PROPERTY, component);
            } else {
                remove(Entries.SERVICE_PROPERTY, component);
            }
        }
    }

    public void visit(KnownTypes component) {
        if (target instanceof Registry) {
            if (operation == Operation.ADD) {
                insert(Registry.KNOWN_TYPES_PROPERTY, component);
            } else {
                remove(Registry.KNOWN_TYPES_PROPERTY, component);
            }
        }
    }
}
