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

import java.util.Set;
import java.util.Collections;

/**
 * Describes additional folder properties, namely
 * listings of repository side folders cached for
 * FS virtual files purpose and in future for
 * Versioning Explorer UI.
 * <p>
 * The attribute makes sence only for folders.
 *
 * @author Petr Kuzel
 */
public final class FolderProperties {

    /**
     * Attribute holding this object.
     */
    public static final String ID = "VCS-FolderProperties"; // NOI18N

    // XXX it's not writen to disc layer
    private IgnoreList ignoreList;

    /** Holds FolderEntries as reported from repository. */
    private Set folderListing;
    
    /** Whether the folder listing is complete. */
    private boolean complete;

    // frozen?
    private boolean canUpdate = true;

    /** For debuging purposes trace creation point. */
    private Exception origin;

    /**
     * Creates FolderProperties.
     * Caller should {@link #freeze} once it updates the value.
     */
    public FolderProperties() {
        origin = new Exception("<init> call stack");
    }

    /**
     * Clones FolderProperties inclusing hidden state.
     * Caller should {@link #freeze} once it updates the value.
     */
    public FolderProperties(FolderProperties fprops) {
        ignoreList = fprops.ignoreList;
        folderListing = fprops.folderListing;
        complete = fprops.complete;
        origin = new Exception("<init> call stack");
    }

    /** Clients must access using {@link IgnoreList#forFolder}.*/
    IgnoreList getIgnoreList() {
        return ignoreList;
    }

    void setIgnoreList(IgnoreList list) {
        ignoreList = list;
    }

    /**
     * Gets unmodifiable set of FolderEntries.
     */
    public Set getFolderListing() {
        return folderListing;
    }
    
    /**
     * Tells whether the folder listing is complete.
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Sets new repository folder listing. Caller must not
     * alter the collection content later on.
     */
    public void setFolderListing(Set listing) {
        assert canUpdate;
        if (listing == null) {
            folderListing = null;
        } else {
            folderListing = Collections.unmodifiableSet(listing);
        }
    }
    
    /**
     * Sets the completness of the folder listing.
     */
    public void setComplete(boolean complete) {
        assert canUpdate;
        this.complete = complete;
    }

    /**
     * Make object immutable, all setters throw exception.
     */
    public void freeze() {
        canUpdate = false;
    }

    /** For debugging purposes. */
    public String toString() {
        return "FolderProperties[" + folderListing + " allocated=" + (origin != null ? origin.getStackTrace()[1].toString() : "unknown") + "]";  // NOI18N
    }

}
