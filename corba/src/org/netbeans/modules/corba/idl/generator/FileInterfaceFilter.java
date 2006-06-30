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

import org.netbeans.modules.corba.idl.src.InterfaceElement;

import org.netbeans.modules.corba.utils.ObjectFilter;
import org.netbeans.modules.corba.utils.Assertion;

/*
 * @author Karel Gardas
 */

public class FileInterfaceFilter implements ObjectFilter {

    private String _M_file_name;

    public FileInterfaceFilter (String __file_name) {
	Assertion.myAssert (__file_name != null);
	_M_file_name = __file_name;
    }

    public boolean is (Object __value) {
	if (__value instanceof InterfaceElement
	    && ((InterfaceElement)__value).getFileName ().equals (_M_file_name))
	    return true;
	else
	    return false;
    }
}

