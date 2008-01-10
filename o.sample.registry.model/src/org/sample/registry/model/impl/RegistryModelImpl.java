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

import java.util.Set;
import javax.xml.namespace.QName;
import org.netbeans.modules.xml.xam.ComponentUpdater;
import org.netbeans.modules.xml.xam.ModelSource;
import org.netbeans.modules.xml.xam.dom.AbstractDocumentModel;
import org.sample.registry.model.Registry;
import org.sample.registry.model.RegistryCommon;
import org.sample.registry.model.RegistryComponent;
import org.sample.registry.model.RegistryComponentFactory;
import org.sample.registry.model.RegistryModel;
import org.w3c.dom.Element;

public class RegistryModelImpl extends AbstractDocumentModel<RegistryComponent> implements RegistryModel {
    private RegistryComponentFactory factory;
    private RegistryCommon registry;
    
    public RegistryModelImpl(ModelSource source) {
        super(source);
        factory = new RegistryComponentFactoryImpl(this);
    }
    
    public RegistryCommon getRootComponent() {
        return registry;
    }

    protected ComponentUpdater<RegistryComponent> getComponentUpdater() {
        return new SyncUpdateVisitor();
    }

    public RegistryComponent createComponent(RegistryComponent parent, Element element) {
        return getFactory().create(element, parent);
    }

    public RegistryCommon createRootComponent(Element root) {
        RegistryCommon newRegistry = (RegistryCommon) getFactory().create(root, null);
        if (newRegistry != null) {
            registry = newRegistry;
        }
        return newRegistry;
    }

    public RegistryComponentFactory getFactory() {
        return factory;
    }
    
    public Set<QName> getQNames() {
        return RegistryQNames.getMappedQNames();
    }
        
}
