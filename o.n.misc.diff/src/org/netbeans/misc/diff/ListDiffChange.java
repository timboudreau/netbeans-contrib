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

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
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
final class ListDiffChange implements Change {

    private final int type;
    private final int start;
    private int end;

    /**
     * Create a new Change object with the given start, end and type
     */
    public ListDiffChange (int start, int end, int type) {
        this.type = type;
        this.start = start;
        this.end = end;

        //Sanity check
        if ( end < start ) {
            throw new IllegalArgumentException ( "Start " + start //NOI18N
                    + " > " + end ); //NOI18N
        }
        if ( end < 0 || start < 0 ) {
            throw new IllegalArgumentException ( "Negative start " + //NOI18N
                    start + " or end " + end ); //NOI18N
        }
        if ( type != DELETE && type != CHANGE && type != INSERT ) {
            throw new IllegalArgumentException ( "Unknown change type " + type ); //NOI18N
        }
    }

    /**
     * Constructor used by ListDiff
     */
    ListDiffChange (int start, int type) {
        this.start = start;
        end = start;
        this.type = type;
        assert ( type == DELETE || type == CHANGE || type == INSERT ) : "" + type;
    }

    /**
     * Grow the endpoint of the Change by one
     */
    void inc () {
        end++;
    }

    /**
     * Set the endpoint of the Change
     */
    void setEnd (int end) {
        assert end >= start;
        this.end = end;
    }

    /**
     * Get the change type
     */
    public final int getType () {
        return type;
    }

    /**
     * Get the start index
     *
     * @return the first affected index in the list
     */
    public final int getStart () {
        return start;
    }

    /**
     * Get the end index (inclusive)
     *
     * @return the last affected index in the list
     */
    public final int getEnd () {
        return end;
    }

    /**
     * Get a string representation of this change.
     *
     * @return a string
     */
    public final String toString () {
        StringBuffer sb = new StringBuffer ();
        switch ( type ) {
            case INSERT:
                sb.append ( "INSERT " ); //NOI18N
                break;
            case DELETE:
                sb.append ( "DELETE " ); //NOI18N
                break;
            case CHANGE:
                sb.append ( "CHANGE " ); //NOI18N
                break;
            default :
                assert false;
        }
        sb.append ( start );
        sb.append ( '-' ); //NOI18N
        sb.append ( end );
        return sb.toString ();
    }
}
