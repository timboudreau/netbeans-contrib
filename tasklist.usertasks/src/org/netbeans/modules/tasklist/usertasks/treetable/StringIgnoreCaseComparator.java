package org.netbeans.modules.tasklist.usertasks.treetable;

import java.util.Comparator;

/**
 * Compares strings
 */
public class StringIgnoreCaseComparator implements Comparator {
    public int compare(java.lang.Object o1, java.lang.Object o2) {
        if (o1 == null && o2 == null)
            return 0;
        if (o1 == null)
            return -1;
        if (o2 == null)
            return 1;
        return ((String) o1).compareToIgnoreCase((String) o2);
    }
}
