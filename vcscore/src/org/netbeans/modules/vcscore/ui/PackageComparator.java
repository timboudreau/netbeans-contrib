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
        // XXX was getPackageName('/'), is this OK?
        return fo.getPath().compareTo(fo1.getPath());
    }
    
    public String getDisplayValue(Object obj, Object rowObject) {
        if (obj != null) {
            FileObject fo = (FileObject)obj;
            // XXX was getPackageName('/'), is this OK?
            return fo.getPath();
        }
        return "";
    }
}
