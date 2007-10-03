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
package org.netbeans.modules.vcscore.turbo;

import org.openide.util.NbBundle;
import org.netbeans.api.vcs.FileStatusInfo;

import java.util.Set;
import java.util.HashSet;
import java.awt.*;

/**
 * Enumerates statuses with special meaning for VcsFileSystem.
 * These well known statuses can be set directly by REFRESH
 * command to assure the file statuses for unversioned files.
 * <p>
 * These statuses are merged by CLIVCSFS with profile statuses into
 * one global status string => FileStatusInfo map.
 * <p>
 * It's believed that it simplifies profiles contact a little
 * bit because most VCSes does not care about local (including
 * ignored files), e.g. <tt>cvs st folder/</tt>.
 * <p>
 * @todo Reconsider to put full reponsibility on profiles over
 * (extended) FileStatusInfo statuses. There is possible clash
 * in <tt>status => FileStatusInfo</tt> caused by using same
 * status names by profile and turbo. 
 *
 * @author Petr Kuzel
 */
public final class Statuses {

    /**
     * The status was not reported yet. It can be caused by operation
     * in progress, network failure, etc.
     */
    public static final String STATUS_UNKNOWN = "Yet-Unknown"; // NOI18N

    /**
     * The status of ignored files. These are files, that are ignored by the
     * targed version control system. It's assigned by cache if such files do
     * not get VCS specific status. The REFRESH command can assign it too.
     */
    public static final String STATUS_IGNORED = "Ignored"; // NOI18N

    /**
     * The status of dead files. These are files, that were deleted in the
     * targed version control system, but their old revisions still exist.
     * If REFRESH command mark file using this sttaus it can be recognized
     * and treated specially by VcsFileSystem (e.g. hidden but why).
     */
    public static final String STATUS_DEAD = "Dead"; // NOI18N

    /**
     * The status for local non-ignored files.
     */
    public static String getLocalStatus() {
        return FileStatusInfo.LOCAL.getName();
    }

    /**
     * The status was not reported yet. It can be caused by operation
     * in progress, network failure, etc.
     */
    public static String getUnknownStatus() {
        return STATUS_UNKNOWN;
    }

    /**
     * Creates status infos assigned by turbo et.al. methods.
     * These cover statuses that are not returned by profile
     * <tt>LIST_SUB</tt> command i.e. for unversioned files
     * and dead files.
     */
    public static Set createTurboFileStatusInfos() {
        Set statusInfos = new HashSet();
        statusInfos.add(FileStatusInfo.LOCAL);
        statusInfos.add(FileStatusInfo.MODIFIED);
        statusInfos.add(new CacheFileStatusInfo(
            Statuses.STATUS_DEAD,
            NbBundle.getBundle(Statuses.class).getString("CTL_StatusDead"),
            null));
        statusInfos.add(createIgnoredFileInfo());
        statusInfos.add(new CacheFileStatusInfo(
            Statuses.STATUS_UNKNOWN,
            NbBundle.getBundle(Statuses.class).getString( "unknown"),
            null));

        return statusInfos;
    }

    /** TEmporary well known status definition missing in {@link FileStatusInfo} */
    public static FileStatusInfo createIgnoredFileInfo() {
        return new CacheFileStatusInfo(
            Statuses.STATUS_IGNORED,
            NbBundle.getBundle(Statuses.class).getString( "CTL_StatusIgnored"),
            null
        );
    }

    /**
     * The default implementation of FileStatusInfo.
     * The instances of this class are used as default constants.
     */
    private static class CacheFileStatusInfo extends FileStatusInfo {

        private String displayedStatus;
        private String iconResource;

        public CacheFileStatusInfo(String status, String displayedStatus, String iconResource) {
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
