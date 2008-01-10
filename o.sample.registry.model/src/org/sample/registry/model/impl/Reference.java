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

import org.netbeans.modules.xml.xam.AbstractComponent;
import org.netbeans.modules.xml.xam.AbstractReference;
import org.sample.registry.model.ReferenceableRegistryComponent;
import org.sample.registry.model.RegistryComponent;

public class Reference<T extends ReferenceableRegistryComponent> extends AbstractReference<T> {
    
    /** Creates a new instance of RegistyComponentReference */
    public Reference(T referenced, Class<T> referencedType, AbstractComponent container) {
        super(referenced, referencedType, container);
    }

    public Reference(String refString, Class<T> referencedType, AbstractComponent container) {
        super(referencedType, container, refString);
    }
    
    protected RegistryComponent getReferencingComponent() {
        return (RegistryComponent) super.getParent();
    }
    
    public T get() {
        if (getReferenced() == null) {
            T ref = new FindByNameAndTypeVisitor<T>().find(
                getReferencingComponent().getModel().getRootComponent(), refString, getType());
            if (ref != null) {
                setReferenced(ref);
            }
        }
        return getReferenced();
    }
}
