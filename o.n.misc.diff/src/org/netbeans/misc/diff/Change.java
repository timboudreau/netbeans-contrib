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
/*
 * Change.java
 *
 * Created on September 18, 2004, 6:42 PM
 */

package org.netbeans.misc.diff;

import javax.swing.event.*;

/**
 * Immutable class representing a single transformation to a data range in a list indicating the addition, removal or
 * modification of a range of indices.
 *
 * @author Tim Boudreau
 * @see Diff
 */
public interface Change {
    /**
     * Insertion type.  For convenience, this is the same value as ListDataEvent.INTERVAL_ADDED.
     */
    public static final int INSERT = ListDataEvent.INTERVAL_ADDED;
    /**
* Deletion type.  For convenience, this is the same value as ListDataEvent.INTERVAL_REMOVED.
     */
    public static final int DELETE = ListDataEvent.INTERVAL_REMOVED;
    /**
     * Change type.  For convenience, this is the same value as ListDataEvent.CONTENTS_CHANGED.
     */
    public static final int CHANGE = ListDataEvent.CONTENTS_CHANGED;

    /**
     * Get the change type
     * @return the type of change
     */
    int getType ();

    /**
     * Get the start index
     *
     * @return the first affected index in the list
     */
    int getStart ();

    /**
     * Get the end index (inclusive)
     *
     * @return the last affected index in the list
     */
    int getEnd ();
}
