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

import java.util.Comparator;
import java.util.Collection;

import org.netbeans.modules.corba.utils.Assertion;

import org.netbeans.modules.corba.idl.src.IDLElement;

/*
 * @author Karel Gardas
 */

public class LexicalComparator extends Object implements Comparator {

    public int compare (Object __first, Object __second) {
	Assertion.assert (__first != null && __second != null
			  && __first instanceof IDLElement
			  && __second instanceof IDLElement);
	IDLElement __t_first = (IDLElement)__first;
	IDLElement __t_second = (IDLElement)__second;
	if (__t_first.getLine () < __t_second.getLine ())
	    return -1;
	if (__t_first.getLine () > __t_second.getLine ())
	    return 1;
	if (__t_first.getLine () == __t_second.getLine ()) {
	    if (__t_first.getColumn () < __t_second.getColumn ())
		return -1;
	    else
		return 1;
	}
	return 0;
    }

}

