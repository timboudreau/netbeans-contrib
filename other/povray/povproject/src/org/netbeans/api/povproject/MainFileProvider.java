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
 * MainFileProvider.java
 *
 * Created on February 16, 2005, 4:20 PM
 */

package org.netbeans.api.povproject;

import org.openide.filesystems.FileObject;

/**
 * Provides the last known main file (file used for rendering the whole
 * project - the main scene file).
 *
 * @author Timothy Boudreau
 */
public interface MainFileProvider {
    /** Get the file PovRay should be invoked against to render this project */
    public FileObject getMainFile();
    public void setMainFile (FileObject file);
}
