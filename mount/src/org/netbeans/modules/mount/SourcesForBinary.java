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

import java.net.URL;
import java.util.Arrays;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;

/**
 * Maps binary roots onto... themselves! Cool.
 * @author Jesse Glick
 */
final class SourcesForBinary implements SourceForBinaryQueryImplementation {
    
    public SourcesForBinary() {}

    public SourceForBinaryQuery.Result findSourceRoots(URL binaryRoot) {
        FileObject root = URLMapper.findFileObject(binaryRoot);
        if (root == null) {
            return null;
        }
        if (!Arrays.asList(MountList.DEFAULT.getMounts()).contains(root)) {
            return null;
        }
        return new R(root);
    }
    
    private static final class R implements SourceForBinaryQuery.Result {
        
        private final FileObject root;
        
        public R(FileObject root) {
            this.root = root;
        }

        public FileObject[] getRoots() {
            return new FileObject[] {root};
        }

        public void addChangeListener(ChangeListener l) {}

        public void removeChangeListener(ChangeListener l) {}
        
    }
    
}
