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

package org.netbeans.modules.vcscore.search;

/**
 * Once a filesystem implements this interface, it enables the Find by status  
 *  in the explorer
 * @author  mkleint
 * @version 
 */
public interface VcsSearchTypeFileSystem {

    /** It should return all possible VCS states in which the files in the filesystem
     * can reside.
     */
    public String[] getPossibleFileStatuses();
    
    /** returns the status for a dataobject. If it matches the status 
     * the user selected in the find dialog (list of all possible states), then
     * it's found and displayed.
     */
    public String getStatus(org.openide.loaders.DataObject dObject);
    
}

