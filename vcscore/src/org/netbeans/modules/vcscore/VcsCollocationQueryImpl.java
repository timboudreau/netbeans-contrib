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
        
        // NPE HOTFIX: see the documentation of CollocationQueryImplementation.
        // It explicitely says that files might not exist. Please rewrite to
        // not depend on FileObjects. For now I at least check for null otherwise
        // I'm getting NPE.
        if (fo1 == null || fo2 == null) {
            return false;
        }
        
        Object vcsFS1 = fo1.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        Object vcsFS2 = fo2.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        return vcsFS1 != null && vcsFS2 != null && vcsFS1.equals(vcsFS2);
    }
    
    public File findRoot(File f) {
        FileObject fo = FileUtil.toFileObject(f);
        
        // NPE HOTFIX: see above
        if (fo == null) {
            return null;
        }
        
        VcsFileSystem vcsFS = (VcsFileSystem) fo.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        if (vcsFS == null) return null;
        else return vcsFS.getRootDirectory();
    }
    
}
