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

import java.io.File;

import org.netbeans.spi.queries.CollocationQueryImplementation;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * VCS collocation query implementation based on VCS filesystems.
 * Files are collocated if they reside on the same VCS filesystem.
 * The root of a file is defined as the root of the appropriate VCS filesystem.
 *
 * <p>XXX Working directory for test under XTest is <module>/test/work/user/
 * and that means all test files will be collocated because of this impl. 
 * Document this somewhere so that test authors requiring control over
 * file collocations are aware of that, e.g. ant/project tests hide this impl.
 *
 * @author Jesse Glick, Martin Entlicher
 */
public class VcsCollocationQueryImpl implements CollocationQueryImplementation {
    
    /** Do nothing */
    public VcsCollocationQueryImpl() {}
    
    public boolean areCollocated(File file1, File file2) {
        FileObject fo1 = FileUtil.toFileObject(file1);
        FileObject fo2 = FileUtil.toFileObject(file2);
        Object vcsFS1 = fo1.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        Object vcsFS2 = fo2.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        return vcsFS1 != null && vcsFS2 != null && vcsFS1.equals(vcsFS2);
    }
    
    public File findRoot(File f) {
        FileObject fo = FileUtil.toFileObject(f);
        VcsFileSystem vcsFS = (VcsFileSystem) fo.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        if (vcsFS == null) return null;
        else return vcsFS.getRootDirectory();
    }
    
}
