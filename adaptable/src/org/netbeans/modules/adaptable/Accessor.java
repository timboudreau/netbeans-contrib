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

package org.netbeans.modules.adaptable;

import org.netbeans.api.adaptable.*;

/** Class that allows "friend" calls to api package.
 *
 * @author Jaroslav Tulach
 */
public abstract class Accessor {
    /** instance to make calls to api package */
    public static Accessor API;

    static {
        // forces initialization of class Aspects that initializes
        // field API
        Class c = org.netbeans.api.adaptable.Adaptor.class;
        try {
            Class.forName (c.getName (), true, c.getClassLoader ());
        } catch (Exception ex) {
            // swallow
        }
        //org.netbeans.api.adaptable.Adaptor.init ();
        assert API != null : "We have to initilialize the API field"; // NOI18N
    }
 
    /**
     * Creates new instance of Adaptor
     * @param impl the impl to pass to the provider
     */
    public abstract Adaptor createAspectProvider (ProviderImpl impl, Object data);
    
    /** Gets the associated data */
    public abstract Object getData (Adaptor adaptor);
    /** Gets associated provider */
    public abstract ProviderImpl getProviderImpl (Adaptor adaptor);
    
}
