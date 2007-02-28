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
 * Diff.java
 *
 * Created on September 18, 2004, 6:33 PM
 */

package org.netbeans.misc.diff;

import java.util.*;

/**
 * Represents the difference between two lists.  The <code>getChanges()</code> method returns a list of
 * <code>Change</code> objects, which, if applied to the old list in the order they are returned, will result in the new
 * list. Example code for such a transformation:
 * <pre>
 * List <T> list = new ArrayList (diff.getOld());
 * List <T> target = diff.getNew();
 * List <Change> changes = diff.getChanges();
 * for (Iterator <Change> iter=changes.iterator(); iter.hasNext();) {
 *     Change change = iter.next();
 *     int start = change.getStart();
 *     int end = change.getEnd();
 *     switch (change.getType()) {
 *       case Change.CHANGE :
 *         for (int i=start; i <= end; i++) {
 *             list.set (i, target.get(i));
 *         }
 *         break;
 *       case Change.INSERT :
 *         int ct = 0;
 *         for (int i=end; i >= start; i--) {
 *             Object o = target.get(i);
 *             list.add(start, o);
 *         }
 *         break;
 *       case Change.DELETE :
 *         for (int i=end; i >= start; i--) {
 *             list.remove(i);
 *         }
 *         break;
 *     }
 * }
 * </pre>
 * @param T The type the lists to be diffed will have
 * @author Tim Boudreau
 * @see Change
 */
public abstract class Diff <T> {
    /**
     * Get a list of Change objects in the order they need to be applied to construct the result of
     * <code>getNew()</code> by applying these changes to <code>getOld()</code>.
     * @return A list of Change objects
     */
    public abstract List <Change> getChanges ();

    /**
     * Get the former contents.
     * @return the list
     */
    public abstract List <T> getOld ();

    /**
     * Get the current contents.
     * @return The list
     */
    public abstract List <T> getNew ();
    
    /**
     * Create a diff of two lists using the iterative algorithm
     * @param T the type of the lists
     * @param old The former list contents
     * @param nue The current list contents
     * @return A diff
     */
    public static <T> Diff <T> create (List <T> old, 
            List <T> nue) {
        return new ListDiff <T> ( old, nue );
    }
    
    /**
     * Create a diff of two lists using the specified algorithm.
     * @param T the type of the lists
     * @param old The former list contents
     * @param nue The current list contents
     * @param algorithm The algorithm to use for performing the diff
     * @return A diff
     */ 
    public static <T> Diff <T> create (List <T> old, List <T> nue, Algorithm algorithm) {
        switch (algorithm) {
            case ITERATIVE :
                return create (old, nue);
            case LONGEST_COMMON_SEQUENCE :
                return new ListMatcherAdapter (old, nue);
            default :
                throw new AssertionError();
        }
    }
    
    /**
     * Create a Longest Common Sequence diff using the passed instance of 
     * Measure to compare the lists.
     */ 
    public static <T> Diff <T> create (List <T> old, List <T> nue, Measure measure) {
        return new ListMatcherAdapter (old, nue, measure);
    }
    

    /**
     * Create a diff of two lists with the specified contents.  Principally useful to indicate a change in a list where
     * the equality of the objects has not changed, but some property of some objects in the list has.
     * @param T the type of the lists
     * @param old The former list contents
     * @param nue The current list contents
     * @param changes A list of Change objects
     * @return A diff
     */
    public static <T> Diff <T> createPredefined (List <T> old, 
            List <T> nue, List <Change> changes) {
        assert old != null && nue != null && changes != null;
        ListDiff <T> result = new ListDiff <T> ( old, nue );
        result.changes = changes;
        return result;
    }
    
    /**
     * 
     * @param list 
     * @param changes 
     * @return 
     */
    public static <T> Diff <T> createPredefined (List <T> list, 
            List <Change> changes) {
        
        assert list != null && changes != null;
        ListDiff <T> result = new ListDiff <T> ( list, list );
        result.changes = changes;
        return result;
    }

    /**
     * Create a diff containing a single difference in a list
     * 
     * @param list 
     * @param start 
     * @param end 
     * @param changeType 
     * @return 
     */
    public static <T> Diff <T> create (List <T> list, int start, 
            int end, int changeType) {
        
        SimpleDiff <T> result = new SimpleDiff <T> (list, list, start, end, 
                changeType);

        return result;
    }
    
    /**
     * Algorithm used for diffing the lists.
     * 
     */
    public static enum Algorithm {
        /**
         * An algorithm based on iterating the two lists in parallel, pausing
         * one iterator when a difference is encountered.  Useful for lists with
         * small interspersed insertions or deletions.  This algorithm does not
         * tolerate duplicate elements in the lists.
         */
        ITERATIVE,
        /**
         * Implementation of the longest common sequence diff algorithm
         */
        LONGEST_COMMON_SEQUENCE,
    }
}
