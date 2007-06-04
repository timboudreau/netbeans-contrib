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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.splitclass;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a change in position of an element in a list.
 *
 * @author Tim Boudreau
 */
final class Swap implements Comparable <Swap> {
    private final int oldPos;
    private final int newPos;
    public Swap(int oldPos, int newPos) {
        this.oldPos = Math.max(oldPos, newPos);
        this.newPos = Math.min(oldPos, newPos);
    }
    
    public boolean equals(Object o) {
        boolean result = o.getClass() == Swap.class;
        if (result) {
            result = o == this;
            if (!result) {
                Swap other = (Swap) o;
                result = (oldPos == other.oldPos && other.newPos == newPos) ||
                        (other.newPos == oldPos && other.oldPos == newPos);
                
            }
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public void swap (List l) {
        //XXX do this better
        Object[] o  = l.toArray();
        Object hold = o[oldPos];
        o[oldPos] = o[newPos];
        o[newPos] = hold;
        l.clear();
        l.addAll(Arrays.asList(o));
    }
    
    public @Override int hashCode() {
        return ((oldPos + 1) * (newPos + 1)) * 17;
    }
    
    public String toString() {
        return "Exchange " + oldPos + " with " + newPos;
    }

    public int compareTo(Swap o) {
        return o.oldPos - oldPos;
    }
}
