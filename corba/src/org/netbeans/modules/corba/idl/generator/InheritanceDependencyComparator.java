/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

