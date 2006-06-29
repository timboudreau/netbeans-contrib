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


package org.netbeans.modules.adlookup;

import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.modules.adaptable.Accessor;
import org.netbeans.modules.adaptable.SingletonizerFactory;
import org.netbeans.spi.adaptable.Initializer;
import org.netbeans.spi.adaptable.Uninitializer;

/**
 *
 * @author Jaroslav Tulach
 */
public final class SingletonizerLookupFactory implements SingletonizerFactory {
    /**
     * Creates an Adaptor based on that support sinletonization.
     * @param classes the interfaces that we support
     * @param impl provider of the functionality
     * @param initCall initializer (or null) to be notified when a first call
     *    is made to an object's adaptable method
     * @param initListener initializer (or null) to be notified when a first
     *    listener is added to the Adaptable 
     * @param noListener deinitilizer (or null) that is supposed to be called
     *    when the last listener is removed from an adaptable
     * @param gc deinitilizer (or null) to be notified when an Adaptable is GCed and
     *    no longer in use 
     */
    public Adaptor create (
        Class[] classes, 
        org.netbeans.spi.adaptable.Singletonizer impl,
        Initializer initCall,
        Initializer initListener,
        Uninitializer noListener,
        Uninitializer gc
    ) {
        for (int i = 0; i < classes.length; i++) {
            if (!classes[i].isInterface()) {
                throw new IllegalArgumentException ("Works only on interfaces: " + classes[i].getName ()); // NOI18N
            }
        }
        SingletonizerLookupImpl single = new SingletonizerLookupImpl(classes, impl, initCall, initListener, noListener, gc);
        try {
            impl.addSingletonizerListener (single);
        } catch (java.util.TooManyListenersException ex) {
            throw new IllegalStateException ("addSingletonizerListener should not throw exception: " + impl); // NOI18N
        }
        return Accessor.API.createAspectProvider(single, null);
    }
}
