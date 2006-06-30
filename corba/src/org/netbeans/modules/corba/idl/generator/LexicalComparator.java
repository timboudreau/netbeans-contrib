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

import org.netbeans.modules.corba.utils.Assertion;

import org.netbeans.modules.corba.idl.src.IDLElement;

/*
 * @author Karel Gardas
 */

public class LexicalComparator extends Object implements Comparator {

    public int compare (Object __first, Object __second) {
	Assertion.myAssert (__first != null && __second != null
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

