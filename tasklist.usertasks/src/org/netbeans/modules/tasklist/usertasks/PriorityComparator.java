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

package org.netbeans.modules.tasklist.usertasks;

import java.util.Comparator;


/**
 * Compares priority values
 *
 * @author tl
 */
public class PriorityComparator implements Comparator {
    public int compare(java.lang.Object o1, java.lang.Object o2) {
        if (o1 == null && o2 == null)
           return 0;
        if (o1 == null)
           return -1;
        if (o2 == null)
          return 1;
        int p1 = ((Integer) o1).intValue();
        int p2 = ((Integer) o2).intValue();
        return p2 - p1;
    }
}
