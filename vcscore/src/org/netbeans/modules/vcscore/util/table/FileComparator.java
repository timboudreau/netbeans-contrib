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

public class FileComparator implements TableInfoComparator {

    /** Creates new FileComparator */
    public FileComparator() {
    }
    
    public String getDisplayValue(Object obj, Object rowObject) {
        if (obj == null) {
            return "";
        }
        File file = (File)obj;
        /*
        String path = file.getAbsolutePath().substring(localPath.length());
        if (path.charAt(0) == File.separatorChar && path.length() > 1) {
            path = path.substring(1);
        }
         */
        String path = file.getName();
        return path;
    }
    
    public int compare(java.lang.Object obj, java.lang.Object obj1) {
        if (obj == null || obj1 == null) {
            return 0;
        }
        File file1 = (File)obj;
        File file2 = (File)obj1;
        
        String str1 = file1.getName();
        String str2 = file2.getName();
        return str1.compareTo(str2);
    }
}
