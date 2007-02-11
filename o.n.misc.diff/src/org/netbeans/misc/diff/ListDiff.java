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
final class ListDiff <T> extends Diff <T> {
    private final List <T> old;
    private final List <T> nue;
    List <Change> changes = null;

    ListDiff (List <T> old, List <T> nue) {
        this.old = old;
        this.nue = nue;
    }

    public List <T> getOld () {
        return old;
    }

    public List <T> getNew () {
        return nue;
    }

    public List <Change> getChanges () {
        if ( changes == null ) {
            changes = new ParallelIterator <T> ( old, nue ).getChanges ();
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
