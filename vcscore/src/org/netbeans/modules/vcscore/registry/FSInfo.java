/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.registry;

import java.awt.Image;
import java.io.File;

import org.openide.filesystems.FileSystem;

/**
 * Information of a registered filesystem.
 *
 * @author  Martin Entlicher
 */
public interface FSInfo {
    
    /**
     * Get the root of the filesystem.
     */
    public File getFSRoot();
    
    /**
     * Get the type of the filesystem, that can be displayed as an additional
     * information.
     */
    public String getDisplayType();
    
    /**
     * Get the icon, that can be used to visually present the filesystem.
     */
    public Image getIcon();
    
    /**
     * Get the filesystem instance. This method should create the filesystem
     * if necessary. If the filesystem is still in use, return the same instance.
     */
    public FileSystem getFileSystem();
    
}
