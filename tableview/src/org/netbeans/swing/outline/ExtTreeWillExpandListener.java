/*
 * ExtTreeExpansionListener.java
 *
 * Created on February 1, 2004, 6:59 PM
 */

package org.netbeans.swing.outline;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;

/** A trivial extension to TreeWillExpandListener, to allow listeners to be
 * notified if another TreeWillExpandListener vetos a pending expansion.
 * If a TreeExpansionListener added to an instance of TreePathSupport implements
 * this interface, it will be notified by the TreePathSupport if some other
 * listener vetos expanding a node.
 * <p>
 * This interface is primarily used to avoid memory leaks if a TreeWillExpandListener
 * constructs some data structure (like a TableModelEvent that is a translation
 * of a TreeExpansionEvent) for use when the expansion actually occurs, to notify
 * it that the pending TableModelEvent will never be fired.  It is not of much
 * interest to the rest of the world.
 *
 * @author  Tim Boudreau
 */
public interface ExtTreeWillExpandListener extends TreeWillExpandListener {
    
    public void treeExpansionVetoed (TreeExpansionEvent event, 
        ExpandVetoException exception);
    
}
