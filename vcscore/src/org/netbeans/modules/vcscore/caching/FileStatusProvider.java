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

package org.netbeans.modules.vcscore.caching;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The provider of VCS attributes of the file. These describes the status
 * of the working file with respect to its repository version. Any method
 * that returns the string representation of an VCS attribute of a file
 * may return an empty string if that information is not available from
 * the given version control system.
 *
 * @author  Martin Entlicher
 */
public interface FileStatusProvider {

    /**
     * Get the table of the possible status strings. This table is used in search
     * service. The table contains the original statuses (obtained from the VCS tool)
     * as keys and localized statuses as values.
     */
    public HashMap getPossibleFileStatusesTable();

    /**
     * Get the table of icon badges, that are displayed on the data objects' node.
     * The table contains the original statuses (obtained from the VCS tool)
     * as keys and the icons of type <code>Image</code> as values.
     */
    public HashMap getStatusIconMap();
    
    /**
     * Get the status that is displayed instead of the attribute value, when this
     * value differs for multiple files contained in the same data object file.
     */
    public String getNotInSynchStatus();
    
    /**
     * Get the status of a single file.
     * @param fullName the name of the file with respect to the filesystem root.
     */
    public String getFileStatus(String fullName);
    
    /**
     * Get the locker of a single file.
     * @param fullName the name of the file with respect to the filesystem root.
     */
    public String getFileLocker(String fullName);

    /**
     * Get the revision of a file.
     * @param fullName the name of the file with respect to the filesystem root.
     */
    public String getFileRevision(String fullName);
    
    /**
     * Get the sticky information of a file (i.e. the current branch).
     * @param fullName the name of the file with respect to the filesystem root.
     */
    public String getFileSticky(String fullName);
    
    /**
     * Get an additional attribute to a file. This attribute can be specific
     * to the given version control system.
     * @param fullName the name of the file with respect to the filesystem root.
     */
    public String getFileAttribute(String fullName);
    
    /**
     * Get the size of a file as a string.
     * @param fullName the name of the file with respect to the filesystem root.
     */
    public String getFileSize(String fullName);
    
    /**
     * Get the date of the last modification of a file.
     * @param fullName the name of the file with respect to the filesystem root.
     */
    public String getFileDate(String fullName);
    
    /**
     * Get the time of the last modification of a file.
     * @param fullName the name of the file with respect to the filesystem root.
     */
    public String getFileTime(String fullName);

    public void setFileStatus(String path, String status);

    /** Should set the file as modified if it's version controlled. */
    public void setFileModified(String path);
    
    public String getLocalFileStatus();
    
    public void refreshDir(String path);
    public void refreshDirRecursive(String path);
}
