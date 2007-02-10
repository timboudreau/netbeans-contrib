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
 * ListDiff.java
 *
 * Created on September 18, 2004, 7:27 PM
 */

package org.netbeans.misc.diff;

import java.util.*;

/**
 * A diff of two <code>java.util.List</code>s, which provides a list of transformations which, applied in order to the
 * old list, will result in the new list.
 *
 * @author Tim Boudreau
 */
public final class ListDiff implements Diff {
    private final List old;
    private final List nue;
    private List changes = null;

    /**
     * Create a diff of two lists
     */
    public static Diff createDiff (List old, List nue) {
        return new ListDiff ( old, nue );
    }

    /**
     * Create a diff of two lists with the specified contents.  Principally useful to indicate a change in a list where
     * the equality of the objects has not changed, but some property of some objects in the list has.
     */
    public static Diff createFixed (List old, List nue, List changes) {
        assert old != null && nue != null && changes != null;
        ListDiff result = new ListDiff ( old, nue );
        result.changes = changes;
        return result;
    }

    private ListDiff (List old, List nue) {
        this.old = old;
        this.nue = nue;
    }

    public List getOld () {
        return old;
    }

    public List getNew () {
        return nue;
    }

    public List getChanges () {
        if ( changes == null ) {
            changes = new ParallelIterator ( old, nue ).getChanges ();
        }
        return changes;
    }

    public String toString () {
        StringBuffer sb = new StringBuffer ();
        sb.append ( "Old:\n" );
        sb.append ( old );
        sb.append ( "\nNew:\n" );
        sb.append ( nue );
        sb.append ( "\nChanges\n" );
        sb.append ( getChanges () );
        return sb.toString ();
    }
}
