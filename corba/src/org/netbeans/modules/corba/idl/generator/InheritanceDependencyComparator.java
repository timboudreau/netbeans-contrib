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

import org.netbeans.modules.corba.utils.Pair;
import org.netbeans.modules.corba.utils.AssertionException;

import org.netbeans.modules.corba.idl.src.InterfaceElement;

/*
 * @author Karel Gardas
 */

public class InheritanceDependencyComparator extends Object implements Comparator {

    public int compare (Object __first, Object __second) {
	if ((!(__first instanceof Pair))
	    && (!(__second instanceof Pair)))
	    throw new AssertionException ();
	Pair __fp = (Pair)__first;
	Pair __sp = (Pair)__second;
	if ((!(__fp.first instanceof InterfaceElement))
	    && (!(__sp.first instanceof InterfaceElement)))
	    throw new AssertionException ();
	if ((!(__fp.second instanceof Collection))
	    && (!(__sp.second instanceof Collection)))
	    throw new AssertionException ();
	InterfaceElement __int1 = (InterfaceElement)__fp.first;
	Collection __col1 = (Collection)__fp.second;
	InterfaceElement __int2 = (InterfaceElement)__sp.first;
	Collection __col2 = (Collection)__sp.second;
	if ((!(__col1.contains (__int2))) && (!(__col2.contains (__int1))))
	    return 0;
	if (__col1.contains (__int2) && (!(__col2.contains (__int1))))
	    return 1;
	if ((!(__col1.contains (__int2))) && __col2.contains (__int1))
	    return -1;
	throw new AssertionException ();
    }

}

