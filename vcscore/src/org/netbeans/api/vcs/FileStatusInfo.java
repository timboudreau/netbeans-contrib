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

package org.netbeans.api.vcs;

import java.awt.Image;

import org.openide.util.NbBundle;

/**
 * The file status information class.
 *
 * @author  Martin Entlicher
 */
public abstract class FileStatusInfo extends Object {
    
    /**
     * The status of local files (files present in working directory, but
     * not in the version control repository).
     */
    public static final FileStatusInfo LOCAL = new DefaultFileStatusInfo("Local",
                                               NbBundle.getMessage(FileStatusInfo.class, "FileStatusInfo.local"),
                                               null);
    
    /**
     * The status of up-to-date files. The content of the file in working directory
     * is exactly the same as the content of the recent revision in the version control repository.
     */
    public static final FileStatusInfo UP_TO_DATE = new DefaultFileStatusInfo("Up To Date",
                                                    NbBundle.getMessage(FileStatusInfo.class, "FileStatusInfo.up_to_date"),
                                                    null);
    
    /**
     * The status of out-of-date files. The content of the file in working directory
     * is older, than the content of the recent revision in the version control repository.
     */
    public static final FileStatusInfo OUT_OF_DATE = new DefaultFileStatusInfo("Out Of Date",
                                                     NbBundle.getMessage(FileStatusInfo.class, "FileStatusInfo.out_of_date"),
                                                     null);
    
    /**
     * The status of out-of-date files. The content of the file in working directory
     * is modified with respect to the corresponding revision in the version control repository.
     */
    public static final FileStatusInfo MODIFIED = new DefaultFileStatusInfo("Modified",
                                                  NbBundle.getMessage(FileStatusInfo.class, "FileStatusInfo.modified"),
                                                  "org/netbeans/api/vcs/resources/badgeLocModified.gif");
    
    /**
     * The status of missing files (files present in the version control repository,
     * but not in the working directory).
     */
    public static final FileStatusInfo MISSING = new DefaultFileStatusInfo("Missing",
                                                 NbBundle.getMessage(FileStatusInfo.class, "FileStatusInfo.missing"),
                                                 null);
    
    /** The name of the status. */
    private String status;
    
    /**
     * Creates a new instance of FileStatusInfo
     * @param status The string representation of this status. This string should
     *        be different for different FileStatusInfo instances.
     */
    public FileStatusInfo(String status) {
        this.status = status;
    }
    
    /**
     * Get the string representation of this FileStatusInfo.
     * @return The string status representation.
     */
    public final String getName() {
        return status;
    }
    
    /*
    public void setDisplayedStatus(String displayedStatus) {
        this.displayedStatus = displayedStatus;
    }
     */
    
    /**
     * Get the localized string representation of this status info. Used for
     * displaying purposes.
     * @return The localized status representation.
     */
    public abstract String getDisplayName();
    
    /**
     * Get the icon for this status info.
     * @return The icon representing this status info or
     *         <code>null</code> when there is no icon.
     */
    public abstract Image getIcon();
    
    /**
     * Tell, whether this file status information is equal with another one.
     * They are equal when the string status representations are equal.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof FileStatusInfo)) return false;
        FileStatusInfo statusInfo = (FileStatusInfo) obj;
        return this.status.equals(statusInfo.status);
    }
    
    /**
     * Tell, whether this file status information represents just the same kind
     * of status as another one. This method can be used by version control systems,
     * that have more status information types, then the pre-defined constants.
     * Use this to find out e.g. whether this status info represents one of the
     * pre-defined status info.
     * @return Whether this file status information represents just the same kind
     * of status as another one.
     * The default implementation just returns the result of {@link #equals}.
     */
    public boolean represents(FileStatusInfo info) {
        return equals(info);
    }
    
    
    /**
     * The default implementation of FileStatusInfo.
     * The instances of this class are used as default constants.
     */
    private static final class DefaultFileStatusInfo extends FileStatusInfo {
        
        private String displayedStatus;
        private String iconResource;
        
        public DefaultFileStatusInfo(String status, String displayedStatus, String iconResource) {
            super(status);
            this.displayedStatus = displayedStatus;
            this.iconResource = iconResource;
        }
        
        /**
         * Get the localized string representation of this status info. Used for
         * displaying purposes.
         * @return The localized status representation.
         */
        public String getDisplayName() {
            return displayedStatus;
        }
        
        /**
         * Get the icon for this status info.
         * @return The icon representing this status info or
         *        <code>null</code> when there is no icon.
         */
        public Image getIcon() {
            return (iconResource != null) ?
                   org.openide.util.Utilities.loadImage(iconResource) : null;
        }
        
    }

}
