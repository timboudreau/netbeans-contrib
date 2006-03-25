/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.spi.adaptable;

/** Factory for those that wish to create their own Adaptors.
 *
 * @author Jaroslav Tulach
 */
public final class Adaptors extends java.lang.Object {
    private Adaptors() {
    }

    /**
     * Creates an Adaptor based on that support for sinletonization.
     * @param classes the interfaces that we can support 
     * @param impl provider of the functionality
     */
    public static org.netbeans.api.adaptable.Adaptor singletonizer (Class[] classes, Singletonizer impl) {
        return org.netbeans.modules.adaptable.SingletonizerImpl.create (classes, impl, null, null, null, null);
    }

    /** Creates a new Adaptor backed by Singletonizer, with additional
     * life cycle manager.
     *
     * @param classes maximal set of classes that we can implement
     * @param impl singletonizer that handles the calls to created adaptables
     * @param initCall initializer (or null) to be notified when a first call
     *    is made to an object's adaptable method
     * @param initListener initializer (or null) to be notified when a first
     *    listener is added to the Adaptable 
     * @param noListener deinitilizer (or null) that is supposed to be called
     *    when the last listener is removed from an adaptable
     * @param gc deinitilizer (or null) to be notified when an Adaptable is GCed and
     *    no longer in use 
     */
    public static org.netbeans.api.adaptable.Adaptor singletonizer (
            Class[] classes, 
            Singletonizer impl,
            Initializer initCall,
            Initializer initListener,
            Uninitializer gc,
            Uninitializer noListener
    ) {
        return org.netbeans.modules.adaptable.SingletonizerImpl.create (classes, impl, initCall, initListener, noListener, gc);
    }
}
