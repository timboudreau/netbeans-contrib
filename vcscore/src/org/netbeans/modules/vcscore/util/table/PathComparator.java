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
import java.io.File;

public class PathComparator implements TableInfoComparator {

    private String localPath;
    /** Creates new FileComparator */
    public PathComparator(String root) {
        localPath = root;
    }

    public String getDisplayValue(Object obj, Object rowObject) {
        if (obj == null) {
            return "";
        }
        File file = (File)obj;
        String path = file.getAbsolutePath().substring(localPath.length());
        if (path.length() == 0) {
            return "";
        }
        if (path.charAt(0) == File.separatorChar && path.length() > 1) {
            path = path.substring(1);
        }
        int lastInd = path.lastIndexOf(File.separatorChar);
        if (lastInd > 0) {
            path = path.substring(0, lastInd);
        }
        return path;
    }
    
    public int compare(java.lang.Object obj, java.lang.Object obj1) {
        if (obj == null || obj1 == null) {
            return 0;
        }
        String str1 = obj.toString();
        String str2 = obj1.toString();
        return str1.compareTo(str2);
    }
}
