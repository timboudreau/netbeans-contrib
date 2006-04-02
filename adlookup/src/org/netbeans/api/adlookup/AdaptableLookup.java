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

package org.netbeans.api.adlookup;

import org.netbeans.api.adaptable.Adaptable;
import org.netbeans.api.adaptable.Adaptor;
import org.openide.util.Lookup;

/** Bridge between the {@link Lookup} and adaptable framework's {@link Adaptor}.
 * By using this class one gets the power of {@link Adaptable}s thru the
 * a bit more capable {@link Lookup} interface.
 *
 * @author Jaroslav Tulach
 */
public final class AdaptableLookup extends Object {
    /** No instances of this class */
    private AdaptableLookup () {
    }
    
    
    /** Finds a Lookup for a given object in the context of 
     * given provider.
     *
     * @param obj represented object
     * @param provider the provider of the aspect
     * @return lookup for the object with additional adaptable interfaces
     *   provided by the <code>provider</code>. The returned object implements
     *   also the <code>Adaptable</code> interface.
     */
    public static Lookup getLookup (Adaptor provider, Object obj) {
        Adaptable a = provider.getAdaptable(obj);
        assert a instanceof Lookup;
        return (Lookup)a;
    }
    
}
