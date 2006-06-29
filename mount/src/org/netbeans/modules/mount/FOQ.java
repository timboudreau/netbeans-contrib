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

package org.netbeans.modules.mount;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.FileOwnerQueryImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;

// XXX could also be handled by calling FOQ.markExternalRoots from DummyProject or MountSources constructor
// (but would then have to dynamically mark and unmark them)

/**
 * Marks any file listed in {@link MountList} as owned by a mount pseudo-project.
 * @author Jesse Glick
 */
public final class FOQ implements FileOwnerQueryImplementation {
    
    /** Default instance for lookup. */
    public FOQ() {}

    public Project getOwner(URI file) {
        try {
            FileObject fo = URLMapper.findFileObject(file.toURL());
            if (fo != null) {
                return getOwner(fo);
            }
        } catch (MalformedURLException e) {
            ErrorManager.getDefault().notify(e);
        }
        return null;
    }

    public Project getOwner(FileObject file) {
        assert file != null;
        // XXX could be more efficient than this (use TreeMap)
        FileObject[] roots = MountList.DEFAULT.getMounts();
        for (int i = 0; i < roots.length; i++) {
            assert roots[i] != null : Arrays.asList(roots);
            if (file == roots[i] || FileUtil.isParentOf(roots[i], file)) {
                return DummyProject.getInstance();
            }
        }
        return null;
    }
    
}
