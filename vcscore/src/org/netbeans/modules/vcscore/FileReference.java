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
package org.netbeans.modules.vcscore;

import org.openide.filesystems.FileObject;

import java.lang.ref.WeakReference;

/**
 * FileObject reference with properties. It has longer or
 * the same lifetime (defined by AbstractFileSystem & GC)
 * as referent FileObject.
 * <p>
 * It keeps last <code>virtual</code> property state. It allows VFS
 * to kick up loaders layer on the property change. This nasty
 * dependency on loaders layer cannot be resolved on loaders layer
 * itself because VFS.children() returns the same result, same
 * fileobjects. No FS API event for this property exists that
 * loaders follow.
 *
 * @author Petr Kuzel
 */
public final class FileReference extends WeakReference {

    private boolean virtual = false;

    public FileReference(FileObject referent) {
        super(referent);
    }

    /**
     * Return previous (when reported from VFS.children()) fileobject virtual property state .
     */
    public boolean wasVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

}
