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

package org.netbeans.spi.adaptable;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.adaptable.SingletonizerFactory;

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
        return singletonizerFactory().create (classes, impl, null, null, null, null);
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
        return singletonizerFactory().create (classes, impl, initCall, initListener, noListener, gc);
    }

    /** Finds singletonizerFactory somewhere. Closely cooperates with
     * Adaptable Lookup Framework, if available.
     */
    private static SingletonizerFactory factory;
    static SingletonizerFactory singletonizerFactory() {
        if (factory != null) {
            return factory;
        }
        factory = SingletonizerFactory.DEFAULT;
        try {

            ClassLoader l = Thread.currentThread().getContextClassLoader();
            if (l == null) {
                l = Adaptors.class.getClassLoader();
            }
            String s = "org.netbeans.modules.adlookup.SingletonizerLookupFactory"; // NOI18N
            Class<? extends SingletonizerFactory> fact = l.loadClass(s).asSubclass(SingletonizerFactory.class); // NOI18N
            factory = fact.newInstance();
        } catch (ClassNotFoundException ex) {
            // this can happen
            Logger.getAnonymousLogger().log(Level.CONFIG, null, ex);
        } catch (InstantiationException ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, null, ex);
        }

        return factory;
    }
}
