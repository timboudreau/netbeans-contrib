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

package org.netbeans.api.adaptable;

/** Provider of Adaptable objects for other objects. It serves as a factory
 * and cache for obtaining Adaptables for any objects.
 *
 * @author Jaroslav Tulach
 */
public final class Adaptor extends java.lang.Object {
    /** initializes access to friend features for
     * the rest of the module.
     */
    static {
        org.netbeans.modules.adaptable.Accessor.API = new AccessorImpl ();
    }

    /**
     * implementation of Adaptor functionality
     */
    final org.netbeans.modules.adaptable.ProviderImpl impl;
    /** any data associated with the provider */
    final Object data;
    
    /**  */
    Adaptor(
        org.netbeans.modules.adaptable.ProviderImpl impl, Object data
    ) {
        this.impl = impl;
        this.data = data;
    }
    
    
    /** Creates or finds an Adaptable object for a given object in the context of 
     * this Adaptor.
     *
     * @param obj represented object
     */
    public Adaptable getAdaptable (Object obj) {
        return impl.createLookup (obj, this);
    }
}
