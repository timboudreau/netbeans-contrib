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
import java.io.File;

public class PathComparator implements TableInfoComparator {

    private String localPath;
    /** Creates new FileComparator */
    public PathComparator(String root) {
        localPath = root;
    }
    
    public String getDisplayValue(Object obj, Object rowObject) {
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
        String str1 = obj.toString();
        String str2 = obj1.toString();
        return str1.compareTo(str2);
    }
}
