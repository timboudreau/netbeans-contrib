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

package org.netbeans.modules.corba.idl.generator;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.netbeans.modules.corba.idl.src.IDLElement;

/*
 * @author Karel Gardas
 * @version 0.01, April 11, 2001
 */

public class SymbolTable {

    //final private static boolean DEBUG=true;
    final private static boolean DEBUG=false;

    private HashMap _M_symbols;
    
    public SymbolTable () {
	_M_symbols = new HashMap ();
    }
    
    public IDLElement get_element (List __name_list) {
	ArrayList __name = new ArrayList (__name_list);
	if (DEBUG)
	    System.out.println ("SymbolTable::get_element (" + __name + ");");
	int __last = __name.size () - 1;
	String __simple_name = (String)__name.get (__last);
	__name.remove (__last);
	HashMap __current_map = _M_symbols;
	Iterator __iter = __name.iterator ();
	while (__iter.hasNext ()) {
	    String __t_name = (String)__iter.next ();
	    Object __t_object = __current_map.get (__t_name);
	    HashMap __t_map = null;
	    if (__t_object instanceof HashMap)
		__t_map = (HashMap)__current_map.get (__t_name);
	    else {
		// This is some IDL container
		__t_name = "__" + __t_name;
		__t_map = (HashMap)__current_map.get (__t_name);
	    }
	    if (__t_map == null)
		return null;
	    __current_map = __t_map;
	}
	Object __retval = __current_map.get (__simple_name);
	if (__retval != null) {
	    if (__retval instanceof IDLElement)
		return (IDLElement)__retval;
	    else {
                IDLElement __elem = new IDLElement (0);
                __elem.setName(__simple_name);
		return __elem; // We get HashMap for module name
            }
	}
	else {
	    return null;
	}
	//return (IDLElement)__current_map.get (__simple_name);
    }
    
    public IDLElement add_element (List __name_list, IDLElement __element) {
	ArrayList __name = new ArrayList (__name_list);
	if (DEBUG)
	    System.out.println ("SymbolTable::add_element (" + __name + ", "
				+ __element + ");");
	int __last = __name.size () - 1;
	String __simple_name = (String)__name.get (__last);
	__name.remove (__last);
	HashMap __current_map = _M_symbols;
	Iterator __iter = __name.iterator ();
	while (__iter.hasNext ()) {
	    String __t_name = (String)__iter.next ();
	    Object __t_object = __current_map.get (__t_name);
	    HashMap __tmp = null;
	    if (__t_object != null) {
		if (__t_object instanceof HashMap)
		    __tmp = (HashMap)__current_map.get (__t_name);
		else {
		    // This is some IDL container
		    __t_name = "__" + __t_name;
		    __tmp = (HashMap)__current_map.get (__t_name);
		}
	    }
	    if (__tmp == null) {
		if (DEBUG)
		    System.out.println ("creating map: " + __t_name);
		__tmp = new HashMap ();
		__current_map.put (__t_name, __tmp);
	    }
	    __current_map = __tmp;
	}
	Object __retval = __current_map.get (__simple_name);
	__current_map.put (__simple_name, __element);
	if (__retval != null) {
	    if (__retval instanceof IDLElement)
		return (IDLElement)__retval;
	    else {
                IDLElement __elem = new IDLElement (0);
                __elem.setName(__simple_name);
		return __elem; // We get HashMap for module name
            }
	}
	else {
	    return null;
	}
    }

    public String toString () {
	return _M_symbols.toString ();
    }
}

