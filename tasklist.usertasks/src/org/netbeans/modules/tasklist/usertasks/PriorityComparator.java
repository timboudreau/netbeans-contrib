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
