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
