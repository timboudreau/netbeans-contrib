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
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.Serializable;

import org.openide.filesystems.FileSystem;

/**
 * Information of a registered filesystem.
 *
 * @author  Martin Entlicher
 */
public interface FSInfo extends Serializable {
    
    public static final String PROP_ROOT = "fSRoot"; // NOI18N
    public static final String PROP_TYPE = "displayType"; // NOI18N
    public static final String PROP_ICON = "icon"; // NOI18N
    public static final String PROP_CONTROL="control";//NOI18N
    
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
     * Get the control state. True when filesystem is under vcs control.
     */
    public boolean isControl();    
    
    /**
     * Determine whether filesystem should be under vcs control or not.
     */
    public void setControl(boolean value);
   
    /**
     * Get the filesystem instance. This method should create the filesystem
     * if necessary. If the filesystem is still in use, return the same instance.
     */
    public FileSystem getFileSystem();
    
    /**
     * Destroy this filesystem info. This method is called when it's known
     * that the FSInfo is no longer needed and will be discarded.
     * This method should cleanup the filesystem, if necessary.
     */
    public void destroy();
    
    public void addPropertyChangeListener(PropertyChangeListener l);
    
    public void removePropertyChangeListener(PropertyChangeListener l);

}
