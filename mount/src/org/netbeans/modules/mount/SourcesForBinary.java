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
