/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * ViewService.java
 *
 * Created on February 17, 2005, 9:18 PM
 */

package org.netbeans.api.povproject;

import org.openide.filesystems.FileObject;

/**
 * Service which will open the image corresponding with a scene file.
 *
 * @author Timothy Boudreau
 */
public interface ViewService {
    /**
     * Determine if there is an existing render of this file.
     */
    public boolean isFileRendered (FileObject file);
    
    /**
     * Determine if the existing render of this file has a newer timestamp
     * than the file itself.
     */
    public boolean isUpToDate (FileObject file);
    
    /**
     * View the passed scene as an image, rendering it if no image 
     * exists (but not re-rendering if it exists but is out of date).
     */
    public void view (FileObject file);
}
