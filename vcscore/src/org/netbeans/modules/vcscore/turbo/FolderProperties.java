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
