/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
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
