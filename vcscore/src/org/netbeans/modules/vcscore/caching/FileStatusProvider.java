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

package org.netbeans.modules.vcscore.caching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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
public interface FileStatusProvider {

    /**
     * Get the table of the possible status strings. This table is used in search
     * service. The table contains the original statuses (obtained from the VCS tool)
     * as keys and localized statuses as values.
     *
    public HashMap getPossibleFileStatusesTable();
     */

    /**
     * Get the set of all possible FileStatusInfo objects. This set is used in search
     * service. The FileStatusInfo objects contains the original statuses
     * (obtained from the VCS tool) as names.
     */
    public Set getPossibleFileStatusInfos();
    
    /**
     * Get the table of icon badges, that are displayed on the data objects' node.
     * The table contains the original statuses (obtained from the VCS tool)
     * as keys and the icons of type <code>Image</code> as values.
     *
    public HashMap getStatusIconMap();
     */
    
    /**
     * Get the status that is displayed instead of the attribute value, when this
     * value differs for multiple files contained in the same data object file.
     */
    public String getNotInSynchStatus();
    
    /**
     * Get the status of a single file.
     * @param fullName the name of the file with respect to the filesystem root.
     * @return The original status (non-localized).
     */
    public String getFileStatus(String fullName);
    
    /**
     * Get the status info of a single file.
     * @param fullName the name of the file with respect to the filesystem root.
     */
    public FileStatusInfo getFileStatusInfo(String fullName);
    
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
