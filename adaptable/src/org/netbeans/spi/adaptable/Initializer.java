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

/** Provides notifications about actions made on the Adaptable.
 * Implement when you want to be notified when a first call 
 * is made or listener is attached to an Adaptable.
 *
 * @see Adaptors
 */
public interface Initializer {
    /** Notifies that an object's Adaptable is now being used. Allows
     * the implementators to do neccessary actions - e.g. attach listeners,
     * pre-initialize additional data structures, etc.
     *
     * @param representedObject the object backed by an adaptable
     */
    public void initialize (Object representedObject);
}
