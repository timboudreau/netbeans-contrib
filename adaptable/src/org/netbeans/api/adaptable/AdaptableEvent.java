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
