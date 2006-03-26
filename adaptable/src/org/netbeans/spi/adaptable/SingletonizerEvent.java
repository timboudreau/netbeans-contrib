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
package org.netbeans.spi.adaptable;

import java.awt.Button;
import java.util.EventObject;
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
    final Object obj;

    /** public factory methods are going to be better */
    private SingletonizerEvent(Singletonizer source, Object obj) {
        super(source);
        this.obj = obj;
    }

    public static SingletonizerEvent anObjectChanged(Singletonizer s, Object obj) {
        return new SingletonizerEvent(s, obj);
    }

    public static SingletonizerEvent allObjectsChanged(Singletonizer s) {
        return new SingletonizerEvent(s, null);
    }
}
