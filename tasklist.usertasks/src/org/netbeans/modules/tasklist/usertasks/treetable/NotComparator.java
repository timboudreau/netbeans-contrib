/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.treetable;

import java.util.Comparator;

/**
 * Inverts results of another Comparator
 *
 * @author  Tim Lebedkov
 */
public class NotComparator implements Comparator {
    private Comparator c;
    
    /** 
     * Creates a new instance of NotComparator 
     * 
     * @param c a comparator 
     */
    public NotComparator(Comparator c) {
        this.c = c;
    }

    public int compare(Object o1, Object o2) {
        return -c.compare(o1, o2);
    }
}
