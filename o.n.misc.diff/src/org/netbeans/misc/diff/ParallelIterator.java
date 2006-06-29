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
 * ParallelIterator.java
 *
 * Created on September 18, 2004, 6:37 PM
 */

package org.netbeans.misc.diff;

import java.util.*;

/**
 * Processor class which uses the iterator pattern to iterate two lists and build a list of Change objects representing
 * a set of transformations which, when applied in order, will generate the new list out of the old one.
 *
 * @author Tim Boudreau
 */
final class ParallelIterator {
    /**
     * The original list
     */
    private final List old;
    /**
     * The modified list
     */
    private final Listnue;
    /**
     * The iterator of the original list
     */
    private Iterator oi;
    /**
     * The iterator of the new list
     */
    private Iterator ni;
    /**
     * A HashSet of the old list's contents for fast containment checks
     */
    private Set h_old = null;
    /**
     * A HashSet of the new list's contents for fast containment checks
     */
    private Set h_new = null;
    /**
     * The list of changes
     */
    private final List changes = new ArrayList ( 5 );
    /**
     * Most recently read item from the old array
     */
    private Object lastOld;
    /**
     * Most recently read item from the new array
     */
    private Object lastNue;
    /**
     * Flag used to determine if we're processing the final old item
     */
    private boolean oiHadNext = true;
    /**
     * Flag set to true if all processing has been completed during a call to next()
     */
    private boolean done = false;
    /**
     * Amount we will add or subtract on the index of new changes to make sure they reflect the effect of previous
     * changes
     */
    private int offset = 0;
    /**
     * The current in the old array
     */
    private int index = 0;
    /**
     * The current change, which may in range as additional items are found
     */
    private Change currChange = null;


    ParallelIterator (List old, List nue) {
        this.old = old;
        this.nue = nue;
        oi = old.iterator ();
        ni = nue.iterator ();
    }

    private void go () {
        if ( oi == null ) {
            throw new IllegalStateException ( "Cannot reuse" );
        }
        //Optimizations
        if ( old.isEmpty () && nue.isEmpty () ) {
            //Both empty, no diff
            oi = null;
            ni = null;
            return;

        } else if ( !old.isEmpty () && nue.isEmpty () ) {
            //New is empty - one big deletion
            Change change = new Change ( 0, Change.DELETE );
            change.setEnd ( old.size () - 1 );
            changes.add ( change );
            oi = null;
            ni = null;
            return;

        } else if ( old.isEmpty () && !nue.isEmpty () ) {
            //Old is empty - one big addition
            Change change = new Change ( 0, Change.INSERT );
            change.setEnd ( nue.size () - 1 );
            changes.add ( change );
            oi = null;
            ni = null;
            return;

        } else {
            ensureInit ();
            while ( hasNext () ) {
                next ();
            }
            done ();
        }
    }

    /**
     * See if we've processed all items in both arrays
     */
    private boolean hasNext () {
        //We have another item even if both iterators are done, if
        //the handled() has not been called for the last read items
        return !done && ( ( oi.hasNext () || ni.hasNext () ) ||
                ( lastOld != null || lastNue != null ) );
    }

    /**
     * Called when an item has been processed with that item.  Will fetch the next item into lastOld/lastNew or null it
     * out if done.
     */
    private void handled (Object o, List src) {
        if ( src == old ) {
            lastOld = null;
            if ( oi.hasNext () ) {
                lastOld = oi.next ();
                index++;
            }
        } else {
            lastNue = null;
            if ( ni.hasNext () ) {
                lastNue = ni.next ();
            }
        }
    }

    /**
     * Handle the next items in the arrays
     */
    private void next () {
        //Flags if there are more items - we will put them into
        //oiHadNext/niHadNext at the end and use them to determine if
        //we're on the last item, which requires special handling
        boolean oiNext = oi.hasNext ();
        boolean niNext = ni.hasNext ();

        //See if the current items are equal
        boolean match = lastOld != null && lastNue != null && lastOld.equals ( lastNue );
        if ( match ) {
            writeChange ();
            handled ( lastOld, old );
            handled ( lastNue, nue );
        } else {
            //Make sure hash sets created
            ensureSets ();
            //See who knows about what
            boolean nueHasIt = h_new.contains ( lastOld );
            boolean oldHasIt = h_old.contains ( lastNue );

            if ( lastNue == null && lastOld != null ) {
                //We're off the end of the new array, handle trailing deletions and finish
                writeChange();
                Change last = new Change (index + offset, (old.size()-1) + offset, Change.DELETE);
                currChange = last;
                done = true;

            } else if ( lastOld == null && lastNue != null ) {
                //We're off the end of the old array, handle trailing insertions and finish
                for ( int i = index + 1; i < ( nue.size () - offset ); i++ ) {
                    //TODO : Don't need a loop to do this
                    addChange ( Change.INSERT, i );
                }
                done = true;

            } else if ( nueHasIt && !oldHasIt ) {
                //Not done, not in the old array - an insertion
                addChange ( Change.INSERT, index );
                handled ( lastNue, nue );

            } else if ( !nueHasIt && oldHasIt ) {
                //Not done, not in the new array - a deletion
                addChange ( Change.DELETE, index );
                handled ( lastOld, old );

            } else if ( nueHasIt && oldHasIt ) {
                //Not done, occurs in both arrays - a change
                addChange ( Change.CHANGE, index );
                handled ( lastOld, old );
                handled ( lastNue, nue );

            } else if ( !nueHasIt && !oldHasIt ) {
                //Not in either array - a change, or we may be almost done
                if ( oiNext || ( !oiNext && oiHadNext ) ) { //Next to last or last element
                    //Add a change
                    addChange ( Change.CHANGE, index );
                    handled ( lastOld, old );
                    handled ( lastNue, nue );
                    //If we're done, we won't be back - run out to the end to
                    //handle any remaining stuff

                    if ( !oiNext ) {
                        //Handle trailing insertions
                        for ( int i = index + 1; i < ( nue.size () - offset ); i++ ) {
                            //TODO : Don't need a loop to do this
                            addChange ( Change.INSERT, i );
                        }
                        //we're done
                        done = true;
                    } else if ( !niNext ) {
                        //Handle trailing deletions
                        for ( int i = index; i < ( old.size () - offset ); i++ ) {
                            //TODO : Don't need a loop to do this
                            addChange ( Change.DELETE, i );
                        }
                        //We're done
                        done = true;
                    }
                }
            }
        }
        //Update the flags
        oiHadNext = oiNext;
    }

    /**
     * Ensure the HashSets used to check for containment are created
     */
    private void ensureSets () {
        if ( h_old == null ) {
            h_old = new HashSet ( old );
            h_new = new HashSet ( nue );
            if ( h_old.size () != old.size () ) {
                throw new IllegalStateException ( "Duplicate elements - " +
                        "size of list does not match size of equivalent " +
                        "HashSet " + identifyDuplicates ( old ) );
            }
            if ( h_new.size () != nue.size () ) {
                throw new IllegalStateException ( "Duplicate elements - " +
                        "size of list does not match size of equivalent " +
                        "HashSet " + identifyDuplicates ( nue ) );
            }
        }
    }


    /**
     * If there are duplicate elements either the list, an exception will be thrown.  Get a diagnostic string saying
     * what was duplicated, for debugging.
     */
    private String identifyDuplicates (List l) {
        HashMap map = new HashMap ();
        for ( Iterator i = l.iterator (); i.hasNext (); ) {
            Object o = i.next ();
            Integer count = (Integer) map.get ( o );
            if ( count == null ) {
                count = new Integer ( 1 );
            } else {
                count = new Integer ( count.intValue () + 1 );
            }
            map.put ( o, count );
        }
        StringBuffer sb = new StringBuffer ( "Duplicates: " ); //NOI18N
        for ( Iterator i = map.keySet ().iterator (); i.hasNext (); ) {
            Object key = i.next ();
            Integer ct = (Integer) map.get ( key );
            if ( ct.intValue () > 1 ) {
                sb.append ( "[" + ct.intValue () + //NOI18N
                        " occurances of " + key + "]" ); //NOI18N
            }
        }
        return sb.toString ();
    }


    /**
     * Populates the initial values of lastOld and lastNue
     */
    private void ensureInit () {
        if ( lastOld == null ) {
            lastOld = oi.next ();
        }
        if ( lastNue == null ) {
            lastNue = ni.next ();
        }
    }

    /**
     * Called when all processing has been completed to write any pending changes from processing and clear state
     */
    private void done () {
        writeChange ();
        currChange = null;
        oi = null;
        ni = null;
        h_old = null;
        h_new = null;
    }

    /**
     * Get the list of changes
     */
    List getChanges () {
        if ( oi != null ) go ();
        return changes;
    }

    /**
     * Adds a change to the current change if the current change's type is the same (grow the current change), or write
     * it and create a new Change object if it has grown.
     */
    private void addChange (int type, int idx) {
        if ( currChange == null ) {
            currChange = new Change ( idx + offset, type );
        } else {
            if ( currChange.getType () == type ) {
                currChange.inc ();
            } else {
                writeChange ();
                currChange = new Change ( idx + offset, type );
            }
        }
    }

    /**
     * If any pending change, store it
     */
    private void writeChange () {
        if ( currChange == null ) {
            return;
        }
        changes.add ( currChange );
        int type = currChange.getType ();
        if ( type == Change.INSERT ) {
            offset += ( currChange.getEnd () - currChange.getStart () + 1 );
        } else if ( type == Change.DELETE ) {
            offset -= currChange.getEnd () - currChange.getStart () + 1;
        }
        assert currChange.getStart () <= currChange.getEnd () :
                "Start must be > end - " + currChange.getStart () + " < " + currChange.getEnd ();
        currChange = null;
    }
}
