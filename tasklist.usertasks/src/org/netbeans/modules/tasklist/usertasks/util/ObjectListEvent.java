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

package org.netbeans.modules.tasklist.usertasks.util;

import java.util.EventObject;

/**
 * An event for changes in an ObjectList
 * 
 * @author tl
 */
public class ObjectListEvent extends EventObject {
    /** a range of items was removed */
    public static final int EVENT_REMOVED = 0;

    /** new items added */
    public static final int EVENT_ADDED = 1;

    /** the objects changed completely */
    public static final int EVENT_STRUCTURE_CHANGED = 2;

    /** objects were just reordered. Not removed and not added. */
    public static final int EVENT_REORDERED = 3;

    private int op;
    private int indices[];
    private Object objects[];

    /**
     * Constructor
     *
     * @param source source for this event. Typically an ObjectList
     * @param indices indices of the changed values. May be null if
     *   op == EVENT_REORDERED.
     * @param objects changed values. May be null if
     *   op == EVENT_REORDERED.
     * @param op Operation. One of the EVENT_* constants from this class
     */
    public ObjectListEvent(Object source, int op, int[] indices, 
            Object[] objects) {
        super(source);

        assert op == EVENT_ADDED || op == EVENT_REMOVED ||
            op == EVENT_REORDERED || op == EVENT_STRUCTURE_CHANGED : 
            "Wrong operation"; // NOI18N

        assert indices != null || op == EVENT_REORDERED;
        assert objects != null || op == EVENT_REORDERED;

        this.op = op;
        this.indices = indices;
        this.objects = objects;
    }

    /**
     * Returns the type of the event
     *
     * @return one of the EVENT_* constants from this class
     */
    public int getType() {
        return op;
    }

    /**
     * Returns changed objects
     * 
     * @return array of changed objects. May be null if
     *   op == EVENT_REORDERED
     */
    public Object[] getObjects() {
        return objects;
    }

    /**
     * Returns indices of changed objects
     * 
     * @return indices. May be null if op == EVENT_REORDERED
     */
    public int[] getIndices() {
        return indices;
    }
}
