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

public class RevisionComparator implements TableInfoComparator {

    /** Creates new RevisionComparator */
    public RevisionComparator() {
    }
    
    public int compare(java.lang.Object obj, java.lang.Object obj1) {
        String str1 = obj.toString();
        String str2 = obj1.toString();
        StringTokenizer token1 = new StringTokenizer(str1, ".", false); //NOI18N
        StringTokenizer token2 = new StringTokenizer(str2, ".", false); //NOI18N
        int result = 0;
        while (token1.hasMoreTokens() && token2.hasMoreTokens()) {
            try {
                Integer int1 = new Integer(token1.nextToken());
                Integer int2 = new Integer(token2.nextToken());
                result =  int1.compareTo(int2);
                if (result != 0) return result;
            } catch (NumberFormatException exc) {
                return 0;
            }
        }
        if (token1.hasMoreTokens()) {
            return 1;
        } else if (token2.hasMoreTokens()) {
            return -1;
        } else {
            return result;
        }
    }
    
    public String getDisplayValue(Object obj, Object rowObject) {
        if (obj != null) {
            return obj.toString();
        }
        return ""; //NOI18N
    }
}
