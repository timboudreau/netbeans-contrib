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

package org.netbeans.modules.vcscore;

import java.util.Enumeration;

/**
 * The interface, that tells you about existing FileObjects.
 * This can be used only for file names from the appropriate filesystem.
 *
 * @author  Martin Entlicher
 */
public interface FileObjectExistence {
    
    /**
     * Returns all existing files in the file system.
     */
    public Enumeration getExistingFiles();
    
}
