/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.modules.adaptable;

import org.netbeans.api.adaptable.Adaptor;
import org.netbeans.spi.adaptable.Initializer;
import org.netbeans.spi.adaptable.Uninitializer;

/**
 *
 * @author Jaroslav Tulach
 */
public interface SingletonizerFactory {
    /** Default implementation of this interface */
    public static final SingletonizerFactory DEFAULT = SingletonizerImpl.defaultFactory();

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
    );
}
