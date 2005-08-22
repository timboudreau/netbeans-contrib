/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
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
