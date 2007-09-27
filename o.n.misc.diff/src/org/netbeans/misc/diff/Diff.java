/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
 * Note that the start/end offsets of Change objects in a diff take into account
 * earlier changes.  In other words, if element 1 of a list was changed and
 * element 0 of a list was removed, there will be a deletion at index 0 and
 * then a change at <i>index 0</i> (the former index 1, offset by the number of
 * elements deleted before it).
 * <p>
 * Note that there often may be a number of possible sets of transformations
 * which will transform one list into another - for example, a change is legally
 * considered a deletion and an insertion.  The way in which the differences
 * between two lists will be interpreted is algorithm dependent - a Diff which
 * fulfils the above contract is considered valid regardless of what set of
 * changes it uses to acheive the required transformation.
 * <p>
 * (To debug and validate that all diffs created obey the above contract upon
 * their creation, run with assertions enabled and the system property
 * <code>org.netbeans.misc.diff.validate</code> set to true).
 * 
 * @param T The type the lists to be diffed will have
 * @author Tim Boudreau
 * @see Change
 */
public abstract class Diff <T> {
    private static boolean VALIDATE = Boolean.getBoolean (
            "org.netbeans.misc.diff.validate");
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
     * Create a diff of two lists using the iterative algorithm.
     * @param T the type of the lists
     * @param old The former list contents
     * @param nue The current list contents
     * @return A diff
     */
    public static <T> Diff <T> create (List <T> old, 
            List <T> nue) {
        Diff result = new ListDiff <T> ( old, nue );
        if (VALIDATE) assert validDiff (result);
        return result;
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
        Diff result;
        switch (algorithm) {
            case ITERATIVE :
                result = create (old, nue);
                break;
            case LONGEST_COMMON_SEQUENCE :
                result = new ListMatcherAdapter (old, nue);
                break;
            default :
                throw new AssertionError();
        }
        if (VALIDATE && !validDiff (result)) {
            throw new IllegalStateException ("Invalid diff " + result);
        }
        return result;
    }
    
    /**
     * Create a Longest Common Sequence diff using the passed instance of 
     * Measure to compare the lists.
     * @param T the type of the lists
     * @param old The former list contents
     * @param nue The current list contents
     * @param measure Object which compares elements from the two lists
     * @return a diff
     */ 
    public static <T> Diff <T> create (List <T> old, List <T> nue, Measure measure) {
        Diff result = new ListMatcherAdapter (old, nue, measure);
        if (VALIDATE && !validDiff (result)) {
            throw new IllegalStateException ("Invalid diff " + result);
        }
        return result;
    }

    /**
     * Create a Diff with a predefined list of changes.
     * 
     * @param old The original contents
     * @param nue The new contents
     * @param changes A list of changes
     * @return 
     */
    public static <T> Diff <T> createPredefined (List <T> old, 
                List <T> nue, List <Change> changes) {
        if (old == null) {
            throw new NullPointerException ("Old list null");
        }
        if (nue == null) {
            throw new NullPointerException ("New list null");
        }
        if (changes == null) {
            throw new NullPointerException ("Change list null");
        }
        ListDiff <T> result = new ListDiff <T> ( old, nue );
        result.changes = changes;
        //Always validate manually created diffs
        if (!validDiff (result)) {
            throw new IllegalStateException ("Invalid diff " + result);
        }
        return result;
    }
    
    private static final boolean validDiff (Diff diff) {
        List <String> list = new ArrayList<String>(diff.getOld());
        List <String> target = diff.getNew();
        List <Change> changes = diff.getChanges();
        for (Iterator <Change> iter=changes.iterator(); iter.hasNext();) {
            Change change = iter.next();
            int start = change.getStart();
            int end = change.getEnd();
            switch (change.getType()) {
            case Change.CHANGE :
                for (int i=start; i <= end; i++) {
                    list.set(i, target.get(i));
                }
                break;
            case Change.INSERT :
                for (int i=end; i >= start; i--) {
                    Object o = target.get(i);
                    list.add(start, (String) o);
                }
                break;
            case Change.DELETE :
                for (int i=end; i >= start; i--) {
                    list.remove(i);
                }
                break;
            }
        }
        int max = target.size();
        boolean result = max == list.size();
        if (result) {
            for (int i=0; i < max; i++) {
                result &= (target.get(i) == null) == (list.get(i) == null);
                if (result && target.get(i) != null) {
                    result &= target.get(i).equals(list.get(i));
                }
                if (!result) {
                    break;
                }
            }
        }
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
