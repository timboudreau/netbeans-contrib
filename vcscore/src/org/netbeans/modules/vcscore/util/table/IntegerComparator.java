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
