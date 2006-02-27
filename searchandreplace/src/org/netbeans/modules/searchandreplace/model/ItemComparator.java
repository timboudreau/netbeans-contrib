/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.searchandreplace.model;

import java.awt.Point;
import java.util.Comparator;

/**
 * Comparator which will sort an array of Items into the correct (file tail
 * first) order for calling replace().  
 */
public final class ItemComparator implements Comparator {
    //Inverse sorts search matches so we replace from the tail of the file up
    public int compare(Object a, Object b) {
        Item i1 = (Item) a;
        Item i2 = (Item) b;

        Point p1 = i1.getLocation() == null ?
                new Point() :
                i1.getLocation();
        Point p2 = i2.getLocation() == null ?
                    new Point() :
                    i2.getLocation();

        int result = i1.getFile().getPath().compareTo(
                    i2.getFile().getPath()) * 16384;

        return result + (p2.x - p1.x);
    }
}