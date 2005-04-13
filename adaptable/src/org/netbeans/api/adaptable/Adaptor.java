/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
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
        return impl.createLookup (obj, data);
    }
}
