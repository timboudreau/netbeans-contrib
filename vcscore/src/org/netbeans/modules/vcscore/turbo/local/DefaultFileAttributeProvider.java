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
package org.netbeans.modules.vcscore.turbo.local;

import org.openide.filesystems.FileObject;

/**
 * Default implementation that stores data to dedicated
 * file attribute database. It's dangerous because it
 * understands all requests so it's explictly managed by
 * the FileAttribute query and used as a fallback.
 * <p>
 * <b>Warning:</b> This is NOP implementation.
 *
 * @author Petr Kuzel
 */
final class DefaultFileAttributeProvider implements FileAttributeProvider {

    private static final FileAttributeProvider defaultInstance = new DefaultFileAttributeProvider();

    public static FileAttributeProvider getDefault() {
        return defaultInstance;
    }

    private DefaultFileAttributeProvider() {

    }

    public boolean recognizesAttribute(String name) {
        return true;
    }

    public boolean recognizesFileObject(FileObject fo) {
        return true;
    }

    public Object readAttribute(FileObject fo, String name, MemoryCache memoryCache) {
        return null;
    }

    public boolean writeAttribute(FileObject fo, String name, Object value) {
        return true;
    }

    public String toString() {
        return "DefaultFileAttributeProvider a NOP implementation";  // NOI18N
    }
}
