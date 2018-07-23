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

package org.netbeans.modules.vcscore.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A simple list wich holds only weak references to the original objects.
 * @author  Martin Entlicher
 */
public class WeakList extends AbstractList {

    private ArrayList items;

    /** Creates new WeakList */
    public WeakList() {
        items = new ArrayList();
    }

    public WeakList(Collection c) {
        items = new ArrayList();
        addAll(0, c);
    }
    
    public void add(int index, Object element) {
        items.add(index, new WeakReference(element));
    }
    
    public Iterator iterator() {
        return new WeakListIterator();
    }
    
    public int size() {
        removeReleased();
        return items.size();
    }    
    
    public java.lang.Object get(int index) {
        return ((WeakReference) items.get(index)).get();
    }
    
    private void removeReleased() {
        for (Iterator it = items.iterator(); it.hasNext(); ) {
            WeakReference ref = (WeakReference) it.next();
            if (ref.get() == null) items.remove(ref);
        }
    }
    
    private class WeakListIterator implements Iterator {
        
        private int n;
        private int i;
        
        public WeakListIterator() {
            n = size();
            i = 0;
        }
        
        public boolean hasNext() {
            return i < n;
        }
        
        public Object next() {
            return get(i++);
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }

}
