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

package org.netbeans.spi.vcs;

import java.util.Map;
import java.util.Collection;

import org.openide.filesystems.FileObject;

import org.netbeans.api.vcs.FileStatusInfo;

/**
 * The provider of VCS attributes of the file. These describes the status
 * of the working file with respect to its repository version. Any method
 * that returns the string representation of an VCS attribute of a file
 * may return an empty string if that information is not available from
 * the given version control system.
 *
 * @author  Martin Entlicher
 */
public abstract class VcsStatusProvider extends Object {
    
    /**
     * The name of FileObject attribute, that contains instance of VcsStatusProvider
     * on VCS filesystems.
     */
    private static final String FO_ATTRIBUTE = "org.netbeans.spi.vcs.VcsStatusProvider"; // NOI18N
    
    /**
     * Find the status provider for a FileObject.
     */
    public static VcsStatusProvider findProvider(FileObject file) {
        return (VcsStatusProvider) file.getAttribute(FO_ATTRIBUTE);
    }

    /** It should return all possible VCS states in which the files in the filesystem
     * can reside.
     */
    //public String[] getPossibleFileStatuses();
    
    /**
     * Get the table of the possible status strings. This table is used in search
     * service. The table contains the original statuses (obtained from the VCS tool)
     * as keys and localized statuses as values.
     *
    public abstract Map getFileStatusMap();
     */
    
    /**
     * Get the array of all possible file states.
     */
    public abstract FileStatusInfo[] getPossibleStates();

    /**
     * Get the table of icon badges, that are displayed on the data objects' node.
     * The table contains the original statuses (obtained from the VCS tool)
     * as keys and the icons of type <code>Image</code> as values.
     *
    public abstract Map getStatusIconMap();
     */
    
    /**
     * Get the status of a file.
     * @param filePath the path of the file from filesystem root.
     * @return The file status information object or <code>null</code>
     *         if the status is not known. Use refresh to update
     *         the status information.
     */
    public abstract FileStatusInfo getStatus(String filePath);
    
    /**
     * Get the locker of a file.
     * @param filePath the path of the file from filesystem root.
     *
    public abstract String getLocker(String filePath);
     */

    /**
     * Get the revision of a file.
     * @param filePath the path of the file from filesystem root.
     *
    public abstract String getRevision(String filePath);
     */
    
    /**
     * Get the sticky information of a file (i.e. the current branch).
     * @param filePath the path of the file from filesystem root.
     *
    public abstract String getSticky(String filePath);
     */
    
    /**
     * Get additional VCS attributes to a file. These attributes can be specific
     * to the given version control system.
     * @param filePath the path of the file from filesystem root.
     *
    public abstract String[] getAttributes(String filePath);
     */
    
    /**
     * Get the size of a file.
     * @param filePath the path of the file from filesystem root.
     *
    public abstract long getSize(String filePath);
     */
    
    /**
     * Get the date and time of the last modification of a file.
     * @param filePath the path of the file from filesystem root.
     *
    public abstract long getDate(String filePath);
     */
    
    /**
     * Find out whether the file is local (is not version controlled)
     * @param filePath the path of the file from filesystem root.
     */
    public abstract boolean isLocal(String filePath);
    
    /**
     * Get annotation of a file.
     * @param filePath the path of the file from filesystem root.
     *
    public abstract String getAnnotation(String filePath);
    
    /**
     * Get annotation of a set of files.
     * @param filePaths the set of file paths from filesystem root.
     *
    public abstract String getAnnotation(Collection filePaths);
     */

    /**
     * Set the file as modified if it's version controlled.
     * @param filePath the path of the file from filesystem root.
     *
    public abstract void setFileModified(String filePath);

    /**
     * Set the file as local (not version controlled). This method is usually called
     * after a new file creation.
     * @param filePath the path of the file from filesystem root.
     *
    public abstract void setFileLocal(String filePath);
     */
    
    /**
     * Refresh the file state.
     * @param path the path of the file from filesystem root.
     * @param recursive whether to perform a recursive refresh when called on a folder.
     */
    public abstract void refresh(String path, boolean recursive);
}
