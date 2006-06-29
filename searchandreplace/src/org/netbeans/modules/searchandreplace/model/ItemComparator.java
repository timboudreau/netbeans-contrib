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