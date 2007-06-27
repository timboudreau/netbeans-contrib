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
package org.netbeans.modules.sfsexplorer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Object representing one META-INF/service registration.
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com), David Strupl
 */
class MetaInfService {
    private String service;
    private List providers;

    /**
     * Constructor.
     * @param service Name of the class of the service.
     */
    MetaInfService(String service) {
        this.service = service;
        providers = new LinkedList();
    }

    /**
     * Service getter.
     * @return The represented service.
     */
    String getService() {
        return service;
    }

    /**
     * Adds additional implementation of the service
     * @param providerInfo Class name of the provider.
     */
    void addProvider(String providerInfo) {
        providers.add(providerInfo);
    }

    /**
     * Returns an unmodifiable collection of the providers.
     * @return All providers of this service.
     */
    List getProviders() {
        return Collections.unmodifiableList(providers);
    }
}