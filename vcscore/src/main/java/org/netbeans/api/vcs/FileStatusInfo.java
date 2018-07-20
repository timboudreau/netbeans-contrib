/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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

    // TODO consider introducing: CONFLICT, SCHEDULED_ADD, SCHEDULED_REMOVE, UNKNOWN, IGNORED  

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
