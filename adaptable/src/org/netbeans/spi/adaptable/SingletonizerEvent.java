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

import java.awt.Button;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;


/** Event that can be fired by {@link Singletonizer} to notify the
 * infrastrcuture that something has changed and that there is a need
 * to update itself.
 *
 * @author Jaroslav Tulach
 */
public final class SingletonizerEvent extends EventObject {
    /** initializes access to friend features for
     * the rest of the module.
     */
    static {
        org.netbeans.modules.adaptable.Accessor.SPI = new AccessorImpl ();
    }

    /** affected object or null */
    final Object obj;
    /** set of classes that changed their values */
    final Set<Class> affected;

    /** public factory methods are going to be better */
    private SingletonizerEvent(Singletonizer source, Object obj, Set<Class> affected) {
        super(source);
        this.obj = obj;
        this.affected = affected;
    }

    public static SingletonizerEvent anObjectChanged(Singletonizer s, Object obj) {
        return new SingletonizerEvent(s, obj, null);
    }

    public static SingletonizerEvent allObjectsChanged(Singletonizer s) {
        return new SingletonizerEvent(s, null, null);
    }

    public static SingletonizerEvent aValueOfObjectChanged(Singletonizer s, Object o, Class<?>... valueTypes) {
        return new SingletonizerEvent(s, o, new HashSet<Class>(Arrays.asList(valueTypes)));
    }
}
