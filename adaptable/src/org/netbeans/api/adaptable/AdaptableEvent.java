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
package org.netbeans.api.adaptable;

import java.util.EventObject;
import java.util.Set;

/** Event describing one change in an {@link Adaptable} object
 * delivered to {@link AdaptableListener}s.
 *
 * @author Jaroslav Tulach
 */
public final class AdaptableEvent extends EventObject {
    private Set<Class> affected;

    
    /** A usual trick to allow only our module to create these event objects
     */
    AdaptableEvent(Object source, Set<Class> affected) {
        super(source);
        this.affected = affected;
    }

    /** Delivers the list of changed classes. Those classes may have
     * been added, removed or just their value modified.
     * @return an unmodifiable set of classes that has been affected by this event
     */
    public final Set<Class> getAffectedClasses() {
        return affected;
    }
}
