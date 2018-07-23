/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
@org.openide.util.lookup.ServiceProvider(service=org.netbeans.spi.project.FileOwnerQueryImplementation.class)
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
