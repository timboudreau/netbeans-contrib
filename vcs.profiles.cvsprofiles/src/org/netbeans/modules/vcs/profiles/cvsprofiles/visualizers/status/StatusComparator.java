/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.status;

import org.netbeans.modules.vcscore.util.table.*;
/**
 *
 * @author  mkleint
 * @version 
 */


public class StatusComparator implements TableInfoComparator {   
    
    /** Creates new StatusComparator */
    public StatusComparator() {
        
    }

    public String getDisplayValue(Object obj, Object rowObject) {
        String status = (String)obj;       
        return status;
    }
    
    public int compare(java.lang.Object obj, java.lang.Object obj1) {
        String stat1 = obj.toString();
        String stat2 = obj1.toString();
        return stat1.compareTo(stat2);
    }
    
}
