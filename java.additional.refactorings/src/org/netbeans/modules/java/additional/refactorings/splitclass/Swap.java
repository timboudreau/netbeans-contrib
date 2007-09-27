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
