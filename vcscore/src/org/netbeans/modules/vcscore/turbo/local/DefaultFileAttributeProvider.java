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
