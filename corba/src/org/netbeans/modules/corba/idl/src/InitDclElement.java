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

package org.netbeans.modules.corba.idl.src;

import java.util.ArrayList;

public class InitDclElement extends IDLElement {

    ArrayList _M_parameters;

    public InitDclElement(int id) {
        super(id);
	_M_parameters = new ArrayList ();
    }

    public InitDclElement(IDLParser p, int id) {
        super(p, id);
	_M_parameters = new ArrayList ();
    }

    public void setParams (ArrayList __params) {
	_M_parameters = __params;
    }

    public ArrayList getParams () {
	return _M_parameters;
    }

}
