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

package org.netbeans.modules.tasklist.filter;

import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import java.util.AbstractCollection;
import java.util.SortedSet;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Set of filters
 */
public final class FilterRepository implements List { 
    
  /** 
   * Property corresponding to the collection of filters in the
   * repository. The old/new object fired with the property are null.
   */
  public static final String PROP_FILTERS = "propFilters";
  
  /**
   * Property corresponding to the active filter.
   */
  public static final String PROP_ACTIVE_FILTER = "propActiveFilter";

  /** collection of listeners **/
  private PropertyChangeSupport pcs = null;


  /** the set of filters of this repository **/
  private LinkedList filters = new LinkedList();
  private int active = -1;   // index of the active filter

  /** listener for nested filters **/
  private PropertyChangeListener filterChangeListener = 
    new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
          fireFiltersChanged();
      }
    };
  
  /**
   * Constructor, default.
   */
  public FilterRepository() {
  }

  public void assign(final FilterRepository rhs) {
    if (rhs != this) {
      filters.clear();
      Iterator it = rhs.filters.iterator();
      while (it.hasNext()) {
	filters.add(((Filter)it.next()).clone());
      }

      active = rhs.active;
      fireFiltersChanged();
    }
  }

  public Object clone() {
    FilterRepository ret = new FilterRepository();
    ret.assign(this);
    return ret;
  }

  
  // Implementation of java.util.Set

  /** 
   * Adds a new filter to the collection, if it was not present
   * already.
   * @param f the Filter to be added
   * @return true iff it was not member before and was added
   */
  public boolean add(Object f) {
    if (filters.add(f)) {
        ((Filter)f).addPropertyChangeListener(filterChangeListener);
        fireFiltersChanged();
        return true;
    } else { 
        return false;
    }
  }

  public boolean addAll(Collection c) {
    boolean ret = filters.addAll(c);
    if (ret) {
        hookCollection(c);
        fireFiltersChanged();
    }
    return ret;
  }
  
  public void add(int param, Object obj) {
      filters.add(param, obj);
      ((Filter)obj).addPropertyChangeListener(filterChangeListener);
      fireFiltersChanged();
  }
  
  public boolean addAll(int param, java.util.Collection collection) {
      if (filters.addAll(param, collection)) {
          hookCollection(collection);
          fireFiltersChanged();
          return true;
      } else return false;
  }
  
  
  public void clear() {
    if (!filters.isEmpty()) {
      setActive(null);
      unhookCollection(filters);
      filters.clear();
      fireFiltersChanged();
    } 
  }

  /**
   * Returns true if this repository contains the specified filter.
   * @param f filter whose presence is to be tested
   * @return true if this repository contains the filter
   */
  public boolean contains(Object f) {
    return filters.contains(f);
  }


  /**
   * Remove the filter specified by parameter from the collection. 
   *
   * @param filter the Filter to remove
   * @return true iff the filter was found and removed
   */
  public boolean remove(Object filter) {
    if (filter == getActive()) setActive(null);
    if (filters.remove(filter)) {
        ((Filter)filter).removePropertyChangeListener(filterChangeListener);
        return true;
    } else return false;
  }

  public boolean isEmpty() {
    return filters.isEmpty();
  }

  public Iterator iterator() {
    return filters.iterator();
  }

  /**
   * Describe <code>removeAll</code> method here.
   *
   * @param collection a <code>Collection</code> value
   * @return a <code>boolean</code> value
   */
  public boolean removeAll(Collection collection) {
    if (collection.contains(getActive())) setActive(null);
    unhookCollection(collection);
    boolean ret = filters.removeAll(collection);
    if (ret) fireFiltersChanged();
    return ret;
  }
  

  public Object[] toArray() {
    return filters.toArray();
  }

  public Object[] toArray(Object[] objectArray) {
    return filters.toArray(objectArray);
  }

  /**
   * Describe <code>containsAll</code> method here.
   *
   * @param collection a <code>Collection</code> value
   * @return a <code>boolean</code> value
   */
  public boolean containsAll(Collection collection) {
    return filters.containsAll(collection);
  }

  /**
   * Describe <code>retainAll</code> method here.
   *
   * @param collection a <code>Collection</code> value
   * @return a <code>boolean</code> value
   */
  public boolean retainAll(Collection collection) {
    if (getActive() != null && (!collection.contains(getActive()))) setActive(null);

    unhookCollection(filters);
    boolean ret =  filters.retainAll(collection);
    hookCollection(filters);
    
    if (ret) fireFiltersChanged();
    return ret;
  }

  public int size() { return filters.size();}


  public boolean equals(Object o) {
      if (o instanceof FilterRepository && o != null) {
          FilterRepository fr = (FilterRepository)o;
          return filters.equals(fr.filters) && (active == fr.active);
      } else 
          return false;
  }

  public int hashCode() {
      return 31 * filters.hashCode() + (active == -1 ? 0 : getActive().hashCode());
  }

  // PROPERTY CHANGE implementation
  private PropertyChangeSupport getPCS() {
    if (pcs == null) pcs = new PropertyChangeSupport(this);
    return pcs;
  }

  /** 
   * Adds a PropertyChangeListener to the listener list.
   * @param l The listener to add.
   */
  public void addPropertyChangeListener(PropertyChangeListener l) {
    getPCS().addPropertyChangeListener(l);
  }
    
  /** 
   * Removes a PropertyChangeListener from the listener list.
   * @param l The listener to remove.
   */
  public void removePropertyChangeListener(PropertyChangeListener l) {
    pcs.removePropertyChangeListener(l);
  }
    
  private final void fireActiveChanged(Filter old, Filter nnew) {
      if (pcs!=null) getPCS().firePropertyChange(PROP_ACTIVE_FILTER, old, nnew);
  }
  
  /**
   * Fires a change event
   */
  private final void fireFiltersChanged() {
    if (pcs != null) getPCS().firePropertyChange(PROP_FILTERS, null, null);
  }
  
  /**
   * Returns a filter with the given name or null if not found.
   * @param name name of the filter to look up
   * @return Filter with name or null
   */
  public Filter getFilterByName(String name) {
    Iterator it = filters.iterator();
    while (it.hasNext()) {
        Filter f = (Filter)it.next();
        if (f.getName().equals(name)) return f;
    }
    return null;
  }
  
  public Filter getActive() {
    return (active == -1) ? null : ((Filter)filters.get(active));
  }
  
  public void setActive(Filter newactive) {
    if (newactive == null) {
        if (this.active != -1) {
            Filter oldactive = getActive();
            this.active = -1;
            fireActiveChanged(oldactive, null);
        }
    } else {
        int i = filters.indexOf(newactive);
        if (i != -1) {
            Filter oldactive = getActive();
            this.active = i;
            fireActiveChanged(oldactive, newactive);
        } else {
            throw new IllegalArgumentException("newactive not in collection");
        }
    }
  }
  
  
  public Object get(int param) {
      return filters.get(param);
  }
  
  public int indexOf(Object obj) {
      return filters.indexOf(obj);
  }
  
  public int lastIndexOf(Object obj) {
      return filters.lastIndexOf(obj);
  }
  
  public java.util.ListIterator listIterator() {
    return filters.listIterator();
  }
  
  public java.util.ListIterator listIterator(int param) {
      return filters.listIterator(param);
  }
  
  public Object remove(int param) {
      Object o = filters.remove(param);
      if (o != null) {
          ((Filter)o).removePropertyChangeListener(filterChangeListener);
          fireFiltersChanged();           
      }
      return o;
  }
  
  public Object set(int param, Object obj) {
      Object o = filters.set(param, obj);
      ((Filter)o).removePropertyChangeListener(filterChangeListener);
      ((Filter)obj).addPropertyChangeListener(filterChangeListener);

      fireFiltersChanged();
      return o;
  }
  
  public java.util.List subList(int param, int param1) {
    return filters.subList(param, param1);
  }

  private void unhookCollection(Collection c) {
      Iterator it = c.iterator();
      while (it.hasNext()) ((Filter)it.next()).removePropertyChangeListener(filterChangeListener);
  }

  private void hookCollection(Collection c) {
      Iterator it = c.iterator();
      while (it.hasNext()) ((Filter)it.next()).addPropertyChangeListener(filterChangeListener);
  }

}

