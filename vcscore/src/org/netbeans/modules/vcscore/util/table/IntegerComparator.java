/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.util.table;

/**
 *
 * @author  mkleint
 * @version 
 */
import java.util.Comparator;
import java.util.StringTokenizer;

public class IntegerComparator implements TableInfoComparator {

    /** Creates new RevisionComparator */
    public IntegerComparator() {
    }
    
    public int compare(java.lang.Object obj, java.lang.Object obj1) {
       Integer int1 = (Integer)obj;
       Integer int2 = (Integer)obj1;
       int result =  int1.compareTo(int2);
       return result;
    }
    
    public String getDisplayValue(Object obj, Object rowObject) {
        return obj.toString();
    }
}
