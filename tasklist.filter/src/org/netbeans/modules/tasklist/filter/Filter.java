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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import org.openide.util.NbBundle;


/**
 * This class implements a filter for a tasklist
 *
 * @author Tor Norbye
 */
public abstract class Filter {
    /** If true, all conditions in the filter must be true.
     *  If false, any condition in the filter can be true to
     *  make the node pass.
     */
    private boolean allTrue = false;
    
    /** List of conditions to evaluate the task with */
    private List appliedConditions = null;
    
    /** Use visible name of the filter */
    private String name = null;
    
    /** Flatten the hierarchy? When true, don't show parents */
    private boolean flattened;
    
    private PropertyChangeSupport pcs = null;
    protected PropertyChangeSupport getPCS() {
        if (pcs == null) pcs = new PropertyChangeSupport(this);
        return pcs;
    }
    protected boolean hasListeners() { return pcs != null;}

    private static final String NO_FILTER = NbBundle.getMessage(Filter.class, "no-filter");

    public static final String PROP_NAME = "PropName";
    public static final String PROP_ALLTRUE = "PropAllTrue";
    public static final String PROP_CONDITIONS = "PropConditions";
    public static final String PROP_FLATTENED = "PropFlattened";
    

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getPCS().addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getPCS().removePropertyChangeListener(listener);
    }
    
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        getPCS().addPropertyChangeListener(property, listener);
    }
    
    public void removePropertyChangeListener(String property, PropertyChangeListener listener) {
        getPCS().removePropertyChangeListener(property, listener);
    }
    /**
     * Creates an empty filter 
     *
     * @param name name of the filter
     */
    public Filter(String name) {
        this(name, true, new ArrayList(), false);
    }
    
    /**
     * Create a new filter.
     *
     * @param name (User visible) name of the filter
     * @param allTrue When true, all conditions must be true, when false, any
     *  condition can be true, to make a task pass through the filter.
     * @param conditions List of AppliedFilterCondition objects to use when filtering a task.
     * @param showParents Iff true this will cause parents (which do not match
     *   the filter) to be included if they have at least one child
     *   node which -does- match.
     */
    public Filter(String name, boolean allTrue, List conditions, boolean flattened) {
        this.name = name;
        this.allTrue = allTrue;
        this.appliedConditions = conditions;
        this.flattened = flattened;
    }
    
    /**
     * Copy constructor.
     */
    protected Filter(final Filter rhs) {
       this(rhs.name, rhs.allTrue, cloneConditions(rhs.appliedConditions), rhs.flattened);
    }


    /** for deconvertization **/
    protected Filter() {this.name = null; this.appliedConditions = null; };


    private static List cloneConditions(List conditions) {
        LinkedList l = new LinkedList();
        Iterator it = conditions.iterator();
        while (it.hasNext()) {
	  l.add(((AppliedFilterCondition)it.next()).clone());
        }
        
        return l;
    }
    
    public abstract Object clone();
    
    /**
     * Creates filter conditions (options) for the specified property
     * applied to the given property.
     *
     * @param property the property to get options for
     */
    public abstract AppliedFilterCondition[] createConditions(SuggestionProperty property);
    
    /**
     * Returns properties used for filtering by this filter.
     * <p>
     * Versioning consideration: you may not remove
     * any value to retain backward compatability.
     *
     * @return properties for searching
     */
    public abstract SuggestionProperty[] getProperties();
    
    
    /**
     * Removes all conditions from this filter
     */
    public void clear() {
        Vector oldc = null; 
        if (hasListeners()) oldc = new Vector(this.appliedConditions);
        getConditions().clear();
        if (hasListeners()) fireChange(PROP_CONDITIONS, oldc, Collections.EMPTY_LIST);;
    }
    
    /** 
     * When true, flatten the hierarchy such that only matching
     * tasks are shown 
     *
     * @return true = flattened, false = not matching parent tasks will be 
     * shown too
     */
    public boolean isFlattened() {
        return flattened;
    }
    
    /**
     * Changes the attribute "flattened"
     *
     * @param f true = the hierarchy will be flattened such that only matching 
     * tasks are shown
     */
    public void setFlattened(boolean f) {
        if (f != this.flattened) {
            this.flattened = f;
            fireChange(PROP_FLATTENED, Boolean.valueOf(!f), Boolean.valueOf(f));
        }
    }
    
    /** 
     * Return true iff the filter lets the task through 
     *
     * @param node object to be filtered
     */
    public boolean accept(Object node) {
        if (!hasConstraints()) {
            return true; // No need to create iterator object...
        }
        Iterator it = appliedConditions.iterator();
        boolean b = true;
        while (it.hasNext()) {
	    AppliedFilterCondition acond = (AppliedFilterCondition)it.next();
            b = acond.isTrue(node);
            if (b && !allTrue) {
                return true;
            } else if (!b && allTrue) {
                return false;
            }
        }
        return b;
    }

    /** 
     * Return true iff all conditions should be matched.
     *
     * @return true iff all conditions should be matched.
     */
    public boolean matchAll() {
        return allTrue;
    }

    /**
     * Return true iff the filter is not "empty" (meaning that
     * there are constraints on the view)
     */
    public boolean hasConstraints() {
        return (appliedConditions != null) && (appliedConditions.size() > 0);
    }

    /**
     * Should all conditions match?
     *
     * @param b true = all conditions should be matched, false = any
     */
    public void setMatchAll(boolean b) {
        if (this.allTrue != b) {
            this.allTrue = b;
            fireChange(PROP_ALLTRUE, Boolean.valueOf(!b), Boolean.valueOf(b));
        }
    }
    
    /** 
     * Return the list of conditions actually used for this filter.
     *
     * @return the list of AppliedFilterConditions for this filter.
     */
    public final List getConditions() {
        return appliedConditions;
    }
    
    /**
     * Sets new applied conditions used with this filter.
     *
     * @param new List[AppliedFilterCondition]
     */
    public final void setConditions(List conditions) {
        Vector oldc = null;
        if (hasListeners()) oldc = new Vector(conditions);
        this.appliedConditions = conditions;
        if (hasListeners()) fireChange(PROP_CONDITIONS, oldc, this.appliedConditions);
    }
    
    /**
     * Fires an event
     */
    private void fireChange(String property, Object oldv, Object newv) {
        if (pcs!=null) 
            getPCS().firePropertyChange(property, oldv, newv);
    }
    
    /**
     * Gets the name of this filter.
     * @return String name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets new name for this filter.
     * @param name newName
     */
     public void setName(String name) {
        String oldname = this.name;
        this.name = name;
	fireChange(PROP_NAME, oldname, name);
     }
    
    /** 
     * Print out the filter for debugging purposes.
     * Do NOT depend on its format or contents, it may change arbitrarily.
     * It is not localized.
     *
     * @return string representation of this object
     */
    public String toString() {
        Iterator it = appliedConditions.iterator();
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName() + "[name=" + name + ", "); // NOI18N
        sb.append(allTrue ? 
            "ALL of the following conditions" : // NOI18N
            "ANY of the following conditions"); // NOI18N
        sb.append(", "); // NOI18N
        while (it.hasNext()) {
            sb.append(it.next());
            sb.append(", "); // NOI18N
        }
        sb.append("]"); // NOI18N
        return sb.toString();
    }

    /**
     * Handy utility class to insert Filters into lists.
     */
    public static class ListModelElement {
       
        public Filter filter;
        public ListModelElement(Filter f) {
            filter = f;
        }
        
        public String toString() { return (filter != null) ? filter.getName() : NO_FILTER;}
        
        public int hashCode() {
	  return (filter != null) ? (filter.hashCode()) : 0;
        }
        
        public boolean equals(Object rhs) {
	  if (rhs instanceof ListModelElement) rhs = ((ListModelElement)rhs).filter;
	  return this.filter == rhs;
        }
    }


    /**
     * Lift of AppliedFilterCondition(property, - ) to list of
     * FilterConditions.
     * 
     * @param property SuggestionProperty to apply to every
     *                 FilterCondition in the list
     * @param conds    list of FilterConditions
     * @return list of AppliedFilterConditions , each applied to
     *         property and the corresponding element of conds. 
     */
    protected static AppliedFilterCondition [] applyConditions(SuggestionProperty property, FilterCondition [] conds) {
      if (conds == null) return null;
      else {
	AppliedFilterCondition [] applied = new AppliedFilterCondition[conds.length];
	for (int i = 0; i < conds.length; i++) {
	  applied[i] = new AppliedFilterCondition(property, conds[i]);
	}
	return applied;
      }
    }
        
}
