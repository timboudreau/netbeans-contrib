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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.update;

/**
 *
 * @author  mkleint
 * @version 
 */
import java.util.Comparator;
import org.netbeans.modules.vcscore.util.table.*;
import org.netbeans.lib.cvsclient.command.DefaultFileInfoContainer;

import org.openide.util.NbBundle;

public class TypeComparator implements TableInfoComparator {

    /** Creates new TypeComparator */
    public TypeComparator() {
    }
    
    public int compare(java.lang.Object obj, java.lang.Object obj1) {
        String str1 = obj.toString();
        String str2 = obj1.toString();
        return str1.compareTo(str2);
    }
    
    public String getDisplayValue(Object obj, Object rowObject) {
        String val =  obj.toString();
        if (val.equals(DefaultFileInfoContainer.MERGED_FILE)) {
            return NbBundle.getBundle(TypeComparator.class).getString("TYPE_MERGED"); // NOI18N
        } 
        if (val.equals(DefaultFileInfoContainer.PERTINENT_STATE)) {
            return NbBundle.getBundle(TypeComparator.class).getString("TYPE_DELETED"); // NOI18N
        }
        return val;
    }
}
