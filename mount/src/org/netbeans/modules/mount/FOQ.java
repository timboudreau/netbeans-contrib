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
