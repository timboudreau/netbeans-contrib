/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.settings;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/*
 * @author Karel Gardas
 * @version 0.01, Jan 8 2001
 */

public class POAPolicyValueDescriptor {

    private String _M_name;
    private List _M_required_policies;
    private List _M_conflicts_with_policies;
    private List _M_disabled_actions;

    public POAPolicyValueDescriptor () {
	_M_required_policies = new LinkedList ();
	_M_conflicts_with_policies = new LinkedList ();
	_M_disabled_actions = new LinkedList ();
    }

    public String getName () {
	return _M_name;
    }

    public void setName (String __value) {
	_M_name = __value;
    }

    public List getConflictsPolicies () {
	return _M_conflicts_with_policies;
    }

    public void addConflictsPolicy (POAPolicySimpleDescriptor __value) {
	_M_conflicts_with_policies.add (__value);
    }

    public List getRequiredPolicies () {
	return _M_required_policies;
    }

    public void addRequiredPolicy (POAPolicySimpleDescriptor __value) {
	_M_required_policies.add (__value);
    }

    public List getDisabledActions () {
	return _M_disabled_actions;
    }

    public void addDisabledAction (String __value) {
	_M_disabled_actions.add (__value);
    }

    public String toString () {
	StringBuffer __buf = new StringBuffer ();
	__buf.append ("POAPolicyValueDescriptor: name: ");
	__buf.append (_M_name);
	__buf.append ("\n requires: ");
	Iterator __iter = this.getRequiredPolicies ().iterator ();
	while (__iter.hasNext ()) {
	    __buf.append ("`");
	    __buf.append ((POAPolicySimpleDescriptor)__iter.next ());
	    __buf.append ("', ");
	}
	__buf.append ("\n conflicts with: ");
	__iter = this.getConflictsPolicies ().iterator ();
	while (__iter.hasNext ()) {
	    __buf.append ("`");
	    __buf.append ((POAPolicySimpleDescriptor)__iter.next ());
	    __buf.append ("', ");
	}
	__buf.append ("\n disable actions: ");
	__iter = this.getDisabledActions ().iterator ();
	while (__iter.hasNext ()) {
	    __buf.append ((String)__iter.next ());
	    __buf.append ("', ");
	}
	return __buf.toString ();

    }
}

