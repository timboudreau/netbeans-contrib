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

import org.netbeans.modules.corba.idl.src.OperationElement;
import org.netbeans.modules.corba.idl.src.AttributeElement;

import org.netbeans.modules.corba.utils.ObjectFilter;

/*
 * @author Karel Gardas
 */

public class OperationFilter implements ObjectFilter {

    public boolean is (Object __value) {
	if (__value instanceof OperationElement
	    || __value instanceof AttributeElement)
	    return true;
	else
	    return false;
    }
}



