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

package org.netbeans.modules.vcscore.registry;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
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
     * @throws IllegalStateException when the filesystem can not be enabled
     *         (e.g. it's module is disabled/not present).
     */
    public void setControl(boolean value) throws IllegalStateException;
   
    /**
     * Get the filesystem instance. This method should create the filesystem
     * if necessary. If the filesystem is still in use, return the same instance.
     * When null is returned, this FS info is discarded.
     * @return The filesystem instance or <code>null</code>, when the filesystem
     *         can not be retrieved or is no longer valid (e.g. it's setting was lost).
     */
    public FileSystem getFileSystem();
    
    /**
     * Get the existing filesystem instance. No instances are created, the existing
     * filesystem instance is retunted, if any, otherwise <code>null</code>.
     */
    public FileSystem getExistingFileSystem();
    
    /**
     * Destroy this filesystem info. This method is called when it's known
     * that the FSInfo is no longer needed and will be discarded.
     * This method should cleanup the filesystem, if necessary.
     */
    public void destroy();
    
    public void addVetoableChangeListener(VetoableChangeListener l);
    
    public void removeVetoableChangeListener(VetoableChangeListener l);
    
    public void addPropertyChangeListener(PropertyChangeListener l);
    
    public void removePropertyChangeListener(PropertyChangeListener l);

}
