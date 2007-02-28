/*
 * ListMatcherAdapter.java
 *
 * Created on February 27, 2007, 1:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.misc.diff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.netbeans.misc.diff.ListMatcher.ResultItem;

/**
 * Adapts Pavel Flaska's diff code from the java module to the Diff API.
 * 
 * @param E A type the lists will contain
 * @author Tim Boudreau
 */
class ListMatcherAdapter <E> extends Diff {
    final ListMatcher m;
    boolean unmatched = true;
    List <E> old;
    List <E> nue;
    /** Creates a new instance of ListMatcherAdapter 
     * @param old The original list
     * @param nue The transformed list
     */
    public ListMatcherAdapter(List <E> old, List <E> nue) {
        m = ListMatcher.<E>instance(old, nue);
        this.old = old;
        this.nue = nue;
    }
    
    public ListMatcherAdapter(List <E> old, List <E> nue, Measure measure) {
        m = ListMatcher.<E>instance(old, nue, measure);
        this.old = old;
        this.nue = nue;
    }
    
    public List getChanges() {
        if (unmatched) {
            m.match();
            unmatched = false;
        }
        ResultItem[] r = m.getTransformedResult();
        return new I(r).changes;
    }
    
    private class I {
        public final List <Change> changes = new ArrayList <Change> ();
        ListDiffChange currChange = null;
        int offset = 0;
        public I (ResultItem[] items) {
            int idx;
            for (int i = 0; i < items.length; i++) {
                proc (items[i], idx = i);
            }
            done();
        }
        
        private void proc (ResultItem i, int idx) {
            int type = i.getChangeType();
            addChange (type, idx);
        }
        
        private void addChange (int type, int idx) {
            if ( currChange == null ) {
                if (type != -1) {
                    currChange = new ListDiffChange ( idx + offset, type );
                }
            } else {
                if ( currChange.getType () == type ) {
                    currChange.inc ();
                } else {
                    writeChange ();
                    if (type == -1) {
                        currChange = null;
                    } else {
                        currChange = new ListDiffChange ( idx + offset, type );
                    }
                }
            }
        }
        
        private void done () {
            writeChange ();
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

    public List getOld() {
        return old;
    }

    public List getNew() {
        return nue;
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
