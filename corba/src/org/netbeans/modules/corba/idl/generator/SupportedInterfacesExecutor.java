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

import java.util.List;
import java.util.ArrayList;

import org.netbeans.modules.corba.idl.src.ValueElement;
import org.netbeans.modules.corba.utils.ParentsExecutor;

/*
 * @author Karel Gardas
 */

public class SupportedInterfacesExecutor implements ParentsExecutor {

    public List getParents (Object __element) {
	ArrayList __result = new ArrayList ();
	if (__element instanceof ValueElement) {
	    ValueElement __value = (ValueElement)__element;
	    __result.addAll (__value.getSupported ());
	}
	return __result;
    }
}
