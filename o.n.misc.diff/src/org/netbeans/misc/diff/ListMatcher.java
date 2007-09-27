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
package org.netbeans.misc.diff;

import java.util.*;

/**
 * Implementation of the Longest Common Subsequence algorithm.
 * That is, given two lists <tt>oldL</tt> and <tt>newL</tt>,
 * this class will find the longest sequence objects
 * that are common and ordered in <tt>oldL</tt> and <tt>newL</tt>.
 * 
 * @author Pavel Flaska
 */
public final class ListMatcher<E> {
    
    // old array of elements
    private final E[] oldL;
    
    // new array of elements
    private final E[] newL;
    
    // contains differences. Filled by compute() method
    private final Stack<ResultItem<E>> result;

    // contains method for distance-measuring
    private final Measure measure;
    
    // create ListMatcher instance
    private ListMatcher(List<? extends E> oldL, List<? extends E> newL, Measure measure) {
        this((E[]) oldL.toArray(), (E[]) newL.toArray(), measure);
    }
    
    // create ListMatcher instance
    private ListMatcher(List<? extends E> oldL, List<? extends E> newL) {
        this((E[]) oldL.toArray(), (E[]) newL.toArray());
    }
    
    // create ListMatcher instance
    private ListMatcher(E[] oldL, E[] newL) {
        this(oldL, newL, null);
    }
    
    private ListMatcher(E[] oldL, E[] newL, Measure measure) {
        this.oldL = oldL;
        this.newL = newL;
        this.measure = measure != null ? measure : Measure.DEFAULT;
        result = new Stack<ResultItem<E>>();
    }

    /**
     * Creates the instance of <tt>ListMatcher</tt> class.
     * 
     * @param  oldL  old array of elements to compare.
     * @param  newL  new array of elements to compare.
     * @return       created instance.
     */
    public static <T> ListMatcher<T> instance(List<? extends T> oldL, List<? extends T> newL) {
        return new ListMatcher<T>(oldL, newL);
    }

    /**
     * Creates the instance of <tt>ListMatcher</tt> class.
     * 
     * @param  oldL  old array of elements to compare.
     * @param  newL  new array of elements to compare.
     * @param  comparator  used for comparing elements.
     * @return       created instance.
     */
    public static <T> ListMatcher<T> instance(List<? extends T> oldL, List<? extends T> newL, Measure measure) {
        return new ListMatcher<T>(oldL, newL, measure);
    }
    /**
     * Creates the instance of <tt>ListMatcher</tt> class.
     * 
     * @param  oldL  old array of elements to compare.
     * @param  newL  new array of elements to compare.
     * @return       created instance.
     */
    public static <T> ListMatcher<T> instance(T[] oldL, T[] newL) {
        return new ListMatcher<T>(oldL, newL);
    }

    /**
     * Represents type of difference.
     */
    static enum Operation {
        /** Element was inserted. */
        INSERT("insert"),
        
        /** Element was modified. */
        MODIFY("modify"),
        
        /** Element was deleted. */
        DELETE("delete"),
        
        /** Element was not changed, left as it was. */
        NOCHANGE("nochange");
        
        private Operation(String name) {
            this.name = name;
        }
        
        private final String name;
        
        public String toString() {
            return name;
        }
    } // end Operation
    
    /**
     * Represents one difference in old and new list.
     */
    static final class ResultItem<S> {

        /** element which differs */
        public final S element;
        
        /** kind of operation */
        public final Operation operation;

        /**
         * Creates an instance.
         * 
         * @param  element    element which differs. New element when insert
         *                    or modify, old element when nochange or delete.
         * @param  operation  kind of operation, insert/delete/nochange/modify
         */
        public ResultItem(final S element, final Operation operation) {
            this.element = element;
            this.operation = operation;
        }
        
        public String toString() {
            StringBuffer sb = new StringBuffer(128);
            sb.append('{');
            sb.append(operation);
            sb.append("} ");
            sb.append(element);
            return sb.toString();
        }
    
        int getChangeType() {
            switch (operation) {
                case DELETE :
                    return Change.DELETE;
                case INSERT :
                    return Change.INSERT;
                case MODIFY :
                    return Change.CHANGE;
                case NOCHANGE :
                    return -1;
                default :
                    throw new AssertionError();
            }
        }
    };

    /**
     * Computes the lists differences. Just call this method after
     * instance is created.
     * 
     * @return  true, if there were at least one change in new list.
     */
    public boolean match() {
        final int NEITHER     = 0;
        final int UP          = 1;
        final int LEFT        = 2;
        final int UP_AND_LEFT = 3;
        final int UP_AND_LEFT_MOD = 4;
        
        int n = oldL.length;
        int m = newL.length;
        int S[][] = new int[n+1][m+1];
        int R[][] = new int[n+1][m+1];
        int ii, jj;
        
        // initialization
        for (ii = 0; ii <= n; ++ii) {
            S[ii][0] = 0;
            R[ii][0] = UP;
        }
        for (jj = 0; jj <= m; ++jj) {
            S[0][jj] = 0;
            R[0][jj] = LEFT;
        }
        
            // This is the main dynamic programming loop that computes the score and backtracking arrays.
        for (ii = 1; ii <= n; ++ii) {
            for (jj = 1; jj <= m; ++jj) {
                if (oldL[ii-1].equals(newL[jj-1])) {
                    S[ii][jj] = S[ii-1][jj-1] + 1;
                    R[ii][jj] = UP_AND_LEFT;
                } else {
                    int distance = measure.getDistance(oldL[ii-1], newL[jj-1]);
                    // if the distance is betwwen OBJECTS_MATCH and INFINITE_DISTANCE,
                    // old element was modified to new element.
                    if (distance > Measure.OBJECTS_MATCH && distance < Measure.INFINITE_DISTANCE) {
                        S[ii][jj] = S[ii-1][jj-1] + 1;
                        R[ii][jj] = UP_AND_LEFT_MOD;
                    } else {
                        S[ii][jj] = S[ii-1][jj-1] + 0;
                        R[ii][jj] = distance == Measure.OBJECTS_MATCH ? UP_AND_LEFT : NEITHER;
                    }
                }
                
                if (S[ii-1][jj] >= S[ii][jj]) {
                    S[ii][jj] = S[ii-1][jj];
                    R[ii][jj] = UP;
                }
                
                if (S[ii][jj-1] >= S[ii][jj]) {
                    S[ii][jj] = S[ii][jj-1];
                    R[ii][jj] = LEFT;
                }
            }
        }
        
        // The length of the longest substring is S[n][m]
        ii = n;
        jj = m;
        
        // collect result
        // ensure stack is empty
        if (result.empty() == false) result.clear();
        // Trace the backtracking matrix.
        while (ii > 0 || jj > 0) {
            if(R[ii][jj] == UP_AND_LEFT) {
                ii--;
                jj--;
                E element = oldL[ii];
                result.push(new ResultItem(element, Operation.NOCHANGE));
            } else if (R[ii][jj] == UP_AND_LEFT_MOD) {
                ii--;
                jj--;
                E element = newL[ii];
                result.push(new ResultItem(element, Operation.MODIFY));
            } else if (R[ii][jj] == UP) {
                ii--;
                E element = oldL[ii];
                result.push(new ResultItem(element, Operation.DELETE));
            } else if (R[ii][jj] == LEFT) {
                jj--;
                E element = newL[jj];
                result.push(new ResultItem(element, Operation.INSERT));
            }
        }
        return !result.empty();
    }
    
    /**
     * Returns a list of differences computed by <tt>compute()</tt> method.
     * Ensure that method <tt>compute()</tt> was called.
     * 
     * @return  array of differences.
     */
    public ResultItem<E>[] getResult() {
        int size = result.size();
        ResultItem<E>[] temp = new ResultItem[size];
        for (ResultItem<E> item : result) {
            temp[--size] = item;
        }
        return temp;
    }
    
    /**
     * Returns a list of differences computed by <tt>compute()</tt> method.
     * Moreover, it groups <b>remove</b> operation followed by <b>insert</b>
     * to one <b>modify</b> operation.
     * 
     * @return   array of differences.
     */
    public ResultItem<E>[] getTransformedResult() {
        Stack<ResultItem<E>> copy = (Stack<ResultItem<E>>) result.clone();
        ArrayList<ResultItem<E>> temp = new ArrayList<ResultItem<E>>(copy.size());
        while (!copy.empty()) {
            ResultItem<E> item = copy.pop();
            // when operation is remove, ensure that there is not following 
            // insert - in such case, we can merge these two operation to
            // modify operation.
            if (item.operation == Operation.DELETE && 
                !copy.empty() && copy.peek().operation == Operation.INSERT) 
            {
                // yes, it is modify operation.
                ResultItem nextItem = copy.pop();
                temp.add(new ResultItem(nextItem.element, Operation.MODIFY));
            } else {
                temp.add(item);
            }
        }
        return temp.toArray(new ResultItem[0]);
    }
    
    // for testing and debugging reasons.
    public String printResult(boolean transformed) {
        StringBuffer sb = new StringBuffer(128);
        ResultItem<E>[] temp = transformed ? getTransformedResult() : getResult();
        for (int i = 0; i < temp.length; i++) {
            sb.append(temp[i]).append('\n');
        }
        return sb.toString();
    }
}