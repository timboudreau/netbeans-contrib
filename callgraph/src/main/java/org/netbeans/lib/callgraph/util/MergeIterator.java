/* 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 1997-2015 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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

package org.netbeans.lib.callgraph.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Merges multiple iterators into one.
 *
 * @author Tim Boudreau
 * @param <T> The type
 */
public final class MergeIterator<T> implements Iterator<T> {

    private final LinkedList<Iterator<T>> iterators = new LinkedList<>();

    MergeIterator(Collection<Iterable<T>> iterables) {
        for (Iterable<T> coll : iterables) {
            this.iterators.add(coll.iterator());
        }
    }

    public static <T> Iterable<T> toIterable(final Collection<Iterable<T>> iterators) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new MergeIterator<T>(iterators);
            }
        };
    }

    private Iterator<T> iter() {
        if (iterators.isEmpty()) {
            return null;
        }
        Iterator<T> result = iterators.get(0);
        if (!result.hasNext()) {
            iterators.remove(0);
            return iter();
        }
        return result;
    }

    @Override
    public boolean hasNext() {
        Iterator<T> curr = iter();
        return curr == null ? false : curr.hasNext();
    }

    @Override
    public T next() {
        Iterator<T> iter = iter();
        if (iter == null) {
            throw new NoSuchElementException();
        }
        return iter.next();
    }

    @Override
    public void remove() {
        Iterator<T> iter = iter();
        if (iter == null) {
            throw new NoSuchElementException();
        }
        iter.remove();
    }
}
