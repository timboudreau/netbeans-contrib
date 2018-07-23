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

/**
 * Used for distance measuring of two elements.
 * todo (#pf): Describe mechanism.
 *
 * @author  Martin Matula
 * @author  Tomas Hurka
 * @author  Pavel Flaska
 */
public class Measure {
    
    /**
     * Default measure based on equals.
     */
    static final Measure DEFAULT = new Measure();
    
    /** 
     * Value representing infinite distance - any distance value equal
     * or greater than this is represented as infinite (i.e. indicates
     * that the compared objects are distinct).
     */
    static final int INFINITE_DISTANCE = 1000;
    
    /**
     * Objects perfectly matches, they are identical.
     */
    static final int OBJECTS_MATCH = 0;
    
    /**
     * Compares two objects and returns distance between 
     * them. (Value expressing how far they are.)
     *
     * @param first First object to be compared.
     * @param second Second object to be compared.
     * @return Distance between compared objects (0 = objects perfectly match,
     * <code>INFINITE_DISTANCE</code> = objects are completely different)
     */
    int getDistance(Object first, Object second) {
        assert first != null && second != null : "Shouldn't pass null value!";
        
        if (first == second || first.equals(second)) {
            // pefectly match
            return OBJECTS_MATCH;
        } else {
            if (first instanceof Comparable && second instanceof Comparable) {
                Comparable c1 = (Comparable) first;
                Comparable c2 = (Comparable) second;
                return c1.compareTo(c2);
            }
            return INFINITE_DISTANCE;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // private members
    ///////////////////////////////////////////////////////////////////////////
    public static final Measure STRING = new StringMeasure();
    
    // StringMeasure
    private static final class StringMeasure extends Measure {
        
        private static final int SAME = 0;
        private static final int CASE_SAME = 1;
        private static final int DIFFERENT = 10;
        
        /**
         * This method implements metrics on Strings.
         *
         * @param  first  first string
         * @param  second second string
         * @return value between 0 and 100, where 0 means strings are 
         *         identical, 100 means strings are completly different.
         */
        public final int getDistance(final Object first, final Object second) {
            if (first == second)
                return SAME;
            
            if (first == null || second == null)
                return INFINITE_DISTANCE;
            
            final String x = (String) first;
            final String y = (String) second;
            final int xlen = x.length();
            final int ylen = y.length();
            int errors = 0;
            int xindex = 0, yindex = 0;
            final char xarr[] = new char[xlen+1];
            final char yarr[] = new char[ylen+1];
            
            x.getChars(0, xlen, xarr, 0);
            y.getChars(0, ylen, yarr, 0);
            
            while (xindex < xlen && yindex < ylen) {
                final char xchar = xarr[xindex];
                final char ychar = yarr[yindex];
                final int cherr = compareChars(xchar, ychar);
                
                if (cherr != DIFFERENT) {
                    errors += cherr;
                    xindex++;
                    yindex++;
                    continue;
                }
                final char xchar1 = xarr[xindex+1];
                final char ychar1 = yarr[yindex+1];
                if (xchar1 != 0 && ychar1 != 0) {
                    final int cherr1 = compareChars(xchar1, ychar1);
                    
                    if (cherr1 != DIFFERENT) {
                        errors += DIFFERENT + cherr1;
                        xindex += 2;
                        yindex += 2;
                        continue;
                    }
                    final int xerr = compareChars(xchar, ychar1);
                    final int xerr1= compareChars(xchar1, ychar);
                    
                    if (xerr != DIFFERENT && xerr1 != DIFFERENT) {
                        errors += DIFFERENT + xerr + xerr1;
                        xindex += 2;
                        yindex += 2;
                        continue;
                    }
                }
                if (xlen-xindex > ylen-yindex) {
                    xindex++;
                } else if (xlen-xindex < ylen-yindex) {
                    yindex++;
                } else {
                    xindex++;
                    yindex++;
                }
                errors += DIFFERENT;
            }
            errors += (xlen-xindex+ylen-yindex) * DIFFERENT;
            return (INFINITE_DISTANCE*errors)/Math.max(ylen,xlen)/DIFFERENT;
        }
        
        private static final int compareChars(final char xc, final char yc) {
            if (xc == yc) 
                return SAME;
            
            char xlower = Character.toLowerCase(xc);
            char ylower = Character.toLowerCase(yc);
            
            return xlower == ylower ? CASE_SAME : DIFFERENT;
        }
    }
   
    private static final class OrderedArrayMeasure extends Measure {
        
        private final Measure measure;
        
        OrderedArrayMeasure(Measure elementsMeasure) {
            measure = elementsMeasure;
        }

        public int getDistance(Object first, Object second) {
            Object[] array1 = (Object[]) first;
            Object[] array2 = (Object[]) second;
            int minSize = Math.min(array1.length, array2.length);
            int difference = Math.abs(array1.length - array2.length);
            int result = 0;
            
            if (minSize == 0) {
                if (difference != 0)
                    result = INFINITE_DISTANCE;
                return result;
            }
            for (int i = 0; i < minSize; i++) {
                result += measure.getDistance(array1[i], array2[i]);
            }
            result += difference * INFINITE_DISTANCE;
            result /= (minSize+difference);
            return result > INFINITE_DISTANCE ? INFINITE_DISTANCE : result;
        }
    }
}
