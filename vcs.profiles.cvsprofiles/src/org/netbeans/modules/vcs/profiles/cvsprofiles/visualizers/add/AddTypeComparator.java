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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.add;

import org.openide.util.NbBundle;
import org.netbeans.lib.cvsclient.command.DefaultFileInfoContainer;
import org.netbeans.modules.vcscore.util.table.*;

/**
 *
 * @author  mkleint
 * @version 
 */


public class AddTypeComparator implements TableInfoComparator {

    private String IS_DIRECTORY;
    private String IS_FILE;
    private String IS_RESURRECTED;
    private String IS_RESSURECTED_AND_MODIFIED;
    
    /** Creates new StatusComparator */
    public AddTypeComparator() {
          IS_DIRECTORY = NbBundle.getBundle(AddTypeComparator.class).getString("AddTableInfoModel.directory"); //NOI18N
          IS_FILE = NbBundle.getBundle(AddTypeComparator.class).getString("AddTableInfoModel.file"); //NOI18N
          IS_RESURRECTED = NbBundle.getBundle(AddTypeComparator.class).getString("AddTableInfoModel.resurrected"); //NOI18N
          IS_RESSURECTED_AND_MODIFIED = NbBundle.getBundle(AddTypeComparator.class).getString("AddTableInfoModel.resurrectedAndModified"); //NOI18N
    }

    public String getDisplayValue(Object obj, Object rowObject) {
        String isDir = (String)obj;
        DefaultFileInfoContainer info = (DefaultFileInfoContainer)rowObject;
        if (info.isDirectory()) return IS_DIRECTORY;
        if (isDir.equals("A")) { //NOI18N
            return IS_FILE;
        }
        if (isDir.equals("U")) { //NOI18N
            return IS_RESURRECTED;
        }
        if (isDir.equals("M")) {
            return IS_RESSURECTED_AND_MODIFIED;
        }
        return ""; //NOI18N
    }
    
    public int compare(java.lang.Object obj, java.lang.Object obj1) {
        String str1 = obj.toString();
        String str2 = obj1.toString();
        return str1.compareTo(str2);
    }
    
}
