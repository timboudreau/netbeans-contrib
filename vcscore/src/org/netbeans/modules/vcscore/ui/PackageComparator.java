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

package org.netbeans.modules.vcscore.ui;

/**
 *
 * @author  mkleint
 */
import java.util.Comparator;
import org.netbeans.modules.vcscore.util.table.*;
import org.openide.filesystems.FileObject;

import org.openide.util.NbBundle;

/**
 * a comparator for use in TableInfoModel instances.
 * It expects to get FileObjects as value to compare/display.
 */

public class PackageComparator implements TableInfoComparator {

    /** Creates new Package Comparator */
    public PackageComparator() {
    }
    
    public int compare(java.lang.Object obj, java.lang.Object obj1) {
        FileObject fo = (FileObject)obj;
        FileObject fo1 = (FileObject)obj1;
        return fo.getPackageName('/').compareTo(fo1.getPackageName('/'));
    }
    
    public String getDisplayValue(Object obj, Object rowObject) {
        if (obj != null) {
            FileObject fo = (FileObject)obj;
            return fo.getPackageName('/');
        }
        return "";
    }
}
