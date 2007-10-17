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

import java.awt.Image;
import java.util.Iterator;
import java.util.Set;
import javax.swing.Action;
import org.netbeans.modules.masterfs.providers.AnnotationProvider;
import org.netbeans.modules.masterfs.providers.InterceptionListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;

/**
 * The VCS implementation of annotation provider
 * 
 * @author Martin Entlicher
 */
public class VcsAnnotationProvider extends AnnotationProvider {
    
    private static FileSystem getVcsFileSystem(Set files) {
        FileSystem vfs = null;
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            FileObject fo = (FileObject) it.next();
            FileSystem fs = (FileSystem) fo.getAttribute(VcsAttributes.VCS_NATIVE_FS);
            if (fs == null) {
                return null;
            }
            if (vfs == null) {
                vfs = fs;
            } else {
                if (vfs != fs) {
                    return null;
                }
            }
        }
        return vfs;
    }

    public String annotateName(String name, Set files) {
        FileSystem fs = getVcsFileSystem(files);
        if (fs == null) return name;
        return ((VcsFileSystem) fs).annotateName(name, files);
    }

    public Image annotateIcon(Image icon, int iconType, Set files) {
        FileSystem fs = getVcsFileSystem(files);
        if (fs == null) return icon;
        return ((VcsFileSystem) fs).annotateIcon(icon, iconType, files);
    }

    public String annotateNameHtml(String name, Set files) {
        FileSystem fs = getVcsFileSystem(files);
        if (fs == null) return name;
        return ((VcsFileSystem) fs).annotateNameHtml(name, files);
    }

    public Action[] actions(Set files) {
        FileSystem fs = getVcsFileSystem(files);
        if (fs == null) return new Action[0];
        return fs.getActions(files);
    }

    public InterceptionListener getInterceptionListener() {
        return null;
    }

}
