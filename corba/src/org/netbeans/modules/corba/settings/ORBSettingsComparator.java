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

package org.netbeans.modules.corba.settings;

import java.util.Comparator;

class ORBSettingsComparator implements Comparator {

    public ORBSettingsComparator () {
    }

    public int compare (Object __value1, Object __value2) {
	//System.out.println (__setting1 + ": " + ((ORBSettings)__setting1).getName ());
	//System.out.println (__setting2 + ": " + ((ORBSettings)__setting2).getName ());
	//return ((ORBSettings)__setting1).getName ().toLowerCase ().compareTo
	//    (((ORBSettings)__setting2).getName ().toLowerCase ());
	ORBSettings __setting1 = (ORBSettings)__value1;
	ORBSettings __setting2 = (ORBSettings)__value2;
	return __setting1.getName ().toLowerCase ().compareTo 
	    (__setting2.getName ().toLowerCase ());
    }
}
