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
