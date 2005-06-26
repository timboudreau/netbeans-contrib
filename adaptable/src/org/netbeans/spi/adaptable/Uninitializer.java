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

/** Provides notifications about end of life of an Adaptable.
 * Implement when you want to be notified when the adaptable is 
 * garbage collected.
 *
 * @see Adaptors
 */
public interface Uninitializer {
    /** Notifies that an object's Adaptable is no longer now being used.
     * Usually called when the adaptable was garbage collected.
     *
     * @param representedObject the object backed by an adaptable
     */
    public void uninitialize(Object representedObject);
}
