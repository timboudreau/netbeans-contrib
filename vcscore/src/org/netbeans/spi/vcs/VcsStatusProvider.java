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
