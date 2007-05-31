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

package org.netbeans.api.registry;


/** An event from context indicating that subcontext was added or removed.
 * The {@link #getType} can be used to distinguish type of the change.
 *
 * @author  David Konecny
 */
public final class SubcontextEvent extends ContextEvent {

    /** This event type is for added subcontext. */
    public static final int SUBCONTEXT_ADDED = 1;

    /** This event type is for removed subcontext. */
    public static final int SUBCONTEXT_REMOVED = 2;

    private int type;
    private String subcontextName;


    SubcontextEvent(Context source, String subcontextName, int type) {
        super(source);
        this.subcontextName = subcontextName;
        this.type = type;
    }
    
    /**
     * Name of subcontext which was removed or added.
     *
     * @return subcontext name
     */
    public String getSubcontextName() {
        return subcontextName;
    }

    public int getType() {
        return type;
    }
    
    public String toString() {
        return "ContextEvent: [subcontextName="+subcontextName+", type="+type+"] " + super.toString(); // NOI18N
    }

}
