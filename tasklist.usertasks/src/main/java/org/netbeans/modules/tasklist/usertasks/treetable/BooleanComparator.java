package org.netbeans.modules.tasklist.usertasks.treetable;

import java.util.Comparator;

/**
 * Compares Boolean values
 */
public class BooleanComparator implements Comparator {
    public int compare(java.lang.Object o1, java.lang.Object o2) {
        if (o1 == null && o2 == null)
            return 0;
        if (o1 == null)
            return -1;
        if (o2 == null)
            return 1;
        boolean b1 = ((Boolean) o1).booleanValue();
        boolean b2 = ((Boolean) o2).booleanValue();
        if (b1 == b2)
            return 0;
        if (b1)
            return 1;
        else
            return -1;
    }
}
