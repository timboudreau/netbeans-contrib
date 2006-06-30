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

/**
 * Virtual file descriptor.
 *
 * @author Petr Kuzel
 */
public final class FolderEntry {

    private final String name;

    private final int mask;

    public FolderEntry(String name, boolean folder) {
        assert !name.endsWith("/") : "Name "+name+" mistakenly ends with a slash.";
        this.name = name;
        this.mask = folder ? RepositoryFiles.FOLDER_MASK : RepositoryFiles.FILE_MASK;
    }

    public String getName() {
        return name;
    }

    public boolean isFolder() {
        return (mask & RepositoryFiles.FOLDER_MASK) != 0;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FolderEntry)) return false;

        final FolderEntry folderEntry = (FolderEntry) o;

        if (name != null ? !name.equals(folderEntry.name) : folderEntry.name != null) return false;

        return true;
    }

    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }
}
