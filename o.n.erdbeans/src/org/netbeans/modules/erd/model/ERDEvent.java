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

package org.netbeans.modules.erd.model;

import java.util.Set;
import java.util.HashMap;

/**
 * 
 * @author David Kaspar
 */
public final class ERDEvent {

    private final long eventID;

    
   

    private Set<ERDComponent> components;


    private boolean selectionChanged;

    private boolean structureChanged;

    ERDEvent (long eventID, Set<ERDComponent> components) {
        this.eventID = eventID;
        this.components = components;    
        
       

        this.structureChanged = ! (components.isEmpty ());
    }

    /**
     * Returns an event id. The id is increasing non-negative number.
     * @return the event id
     */
    public long getEventID () {
        return eventID;
    }

    /**
     * Returns a set of all components that have at least one property changed during a transaction.
     * @return the set of fully property-affected components
     */
    public Set<ERDComponent> geAffectedComponents () {
        return components;
    }

    public boolean isStructureChanged () {
        return structureChanged;
    }

    
    
    
}
