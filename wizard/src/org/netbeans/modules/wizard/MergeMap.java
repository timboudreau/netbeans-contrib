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
 * MergeMap.java
 *
 * Created on February 22, 2005, 4:06 PM
 */

package org.netbeans.modules.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

/**
 * A map which proxies a collection of sub-maps each of which has a
 * unique id.  Submaps can be added or removed en banc.  Values from
 * removed maps are retained;  if push ("someKnownId") happens, the
 * values previously added to the map while that ID was active reappear.
 * <p>
 * This allows us to implement backward/forward semantics for wizards,
 * in which each pane (identified with a unique ID) can add its own
 * settings to the settings map, but if the user presses the Back 
 * button, the settings from the formerly active pane can disappear - 
 * but if the user moves forward again, they are not lost.
 * <p>
 * Calling remove("someKeyBelongingToAnEarlierId") will completely
 * remove that value;  calling put ("someKeyBelongingToAnEarlierId", "newValue")
 * replaces the earler value permanently.
 * <p>
 * <b><i><font color="red">This class is NOT AN API CLASS.  There is no
 * commitment that it will remain backward compatible or even exist in the
 * future.  The API of this library is in the packages <code>org.netbeans.api.wizard</code>
 * and <code>org.netbeans.spi.wizard</code></font></i></b>. 
 *
 * @author Tim Boudreau
 */
public class MergeMap implements Map {
    private Stack order = new Stack();
    private Map id2map = new HashMap();
    
    /** Creates a new instance of MergeMap */
    public MergeMap(String currID) {
        push (currID);
    }
    
    /**
     * Move to a different ID (meaning add a new named map to proxy which can be
     * calved off if necessary).
     */
    public Map push (String id) {
        assert !order.contains(id) : id + " already present";

        Map result = (Map) id2map.get(id);
        if (result == null) {
            result = new HashMap();
            id2map.put (id, result);
        }
        order.push (id);
        return result;
    }
    
    /**
     * Get the ID of the current sub-map being written into.
     */
    public String currID() {
        return (String) order.peek();
    }
    
    /**
     * Remove the current sub-map.  Removes all of its settings from the
     * MergedMap, but if push() is called with the returned value, the
     * values associated with the ID being removed will be restored.
     */
    public String popAndCalve() {
        if (order.size() == 1) {
            throw new NoSuchElementException ("Cannot back out past first entry");
        }
        //Get the current map
        String result = (String) order.peek();
        Map curr = (Map) id2map.get (result);
        order.pop();
        
        //Though unlikely, it is possible that a later step in a wizard
        //overwrote a key/value pair from a previous step of the wizard.
        //We do not want to revert that write, so iterate all the keys
        //we're removing, and if any of them are in steps lower on the 
        //stack, change those lower steps values to whatever was written
        //into the map we're calving off
        
        Set keysForCurr = curr.keySet();
        for (Iterator i=orderIterator(); i.hasNext();) {
            Map other = (Map) id2map.get(i.next());
            for (Iterator j=curr.keySet().iterator(); j.hasNext();) {
                Object key = j.next();
                if (other.containsKey(key)) {
                    other.put (key, curr.get(key));
                }
            }
        }
        return result;
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object obj) {
        for (Iterator i = orderIterator(); i.hasNext();) {
            Map curr = (Map) id2map.get(i.next());
            if (curr.containsKey(obj)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsValue(Object obj) {
        for (Iterator i = orderIterator(); i.hasNext();) {
            Map curr = (Map) id2map.get(i.next());
            if (curr.containsValue(obj)) {
                return true;
            }
        }
        return false;
    }

    public java.util.Set entrySet() {
        HashSet result = new HashSet();
        for (Iterator i = orderIterator(); i.hasNext();) {
            Map curr = (Map) id2map.get(i.next());
            result.addAll (curr.entrySet());
        }
        return result;
    }

    public Object get(Object obj) {
        for (Iterator i = orderIterator(); i.hasNext();) {
            String id = (String) i.next();
            Map curr = (Map) id2map.get(id);
            Object result = curr.get(obj);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Set keySet() {
        HashSet result = new HashSet();
        for (Iterator i = orderIterator(); i.hasNext();) {
            Map curr = (Map) id2map.get(i.next());
            result.addAll (curr.keySet());
        }
        return result;
    }

    public Object put(Object obj, Object obj1) {
        Map curr = (Map) id2map.get (order.peek());
        return curr.put (obj, obj1);
    }

    public void putAll(Map map) {
        Map curr = (Map) id2map.get (order.peek());
        curr.putAll (map);
    }

    private Object doRemove(Object obj) {
        Map curr = (Map) id2map.get (order.peek());
        Object result = curr.remove (obj);
        if (result == null) {
            for (Iterator i = orderIterator(); i.hasNext();) {
                curr = (Map) id2map.get(i.next());
                result = curr.remove (obj);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }
    
    public Object remove(Object obj) {
        //Ensure we remove any duplicates in upper arrays
        Object result = get(obj);
        while (get(obj) != null) {
            doRemove (obj);
        }
        return result;
    }

    public int size() {
        //using keySet() prunes duplicates
        return keySet().size();
    }

    public Collection values() {
        HashSet result = new HashSet();
        Set keys = keySet();
        for (Iterator i = keys.iterator(); i.hasNext();) {
            result.add (get(i.next()));
        }
        return result;
    }
    
    private Iterator orderIterator() {
        return new ReverseIterator(order);
    }
    
    private static final class ReverseIterator implements Iterator {
        private int pos;
        private List l;
        public ReverseIterator (Stack s) {
            pos = s.size()-1;
            l = new ArrayList(s);
        }
        
        public boolean hasNext() {
            return pos != -1;
        }
        
        public Object next() {
            if (pos < 0) {
                throw new NoSuchElementException();
            }
            Object result = l.get(pos);
            pos--;
            return result;
        } 
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
}
