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

package org.netbeans.modules.clearcase.util;

import org.openide.*;
import org.netbeans.api.project.*;
import java.io.*;
import java.util.*;
/*
 *
 * @author Tomas Stupka
 */
import org.netbeans.modules.clearcase.Clearcase;
import org.netbeans.modules.versioning.spi.VCSContext;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;
public class Utils {
        
    /**
     * Tests parent/child relationship of files.
     *
     * @param parent file to be parent of the second parameter
     * @param file file to be a child of the first parameter
     * @return true if the second parameter represents the same file as the first parameter OR is its descendant (child)
     */
    // XXX versioning? - YES isAncestorOrEqual
    public static boolean isParentOrEqual(File parent, File file) {
        for (; file != null; file = file.getParentFile()) {
            if (file.equals(parent)) return true;
        }
        return false;
    }

    /**
     * Semantics is similar to {@link org.openide.windows.TopComponent#getActivatedNodes()} except that this
     * method returns File objects instead od Nodes. Every node is examined for Files it represents. File and Folder
     * nodes represent their underlying files or folders. Project nodes are represented by their source groups. Other
     * logical nodes must provide FileObjects in their Lookup.
     *
     * @return File [] array of activated files
     * @param nodes or null (then taken from windowsystem, it may be wrong on editor tabs #66700).
     */
    // XXX context vs VCSContext
    public static VCSContext getCurrentContext(Node[] nodes) {
        if (nodes == null) {
            nodes = TopComponent.getRegistry().getActivatedNodes();
        }
        return VCSContext.forNodes(nodes);
    }
        
    /**
     * Refreshes the status for files
     * 
     * @param files
     * @param includeParents if true all parents for the given files will be refreshed too
     */
    // XXX may be this should be somewere in the comand infrastructure
    public static void afterCommandRefresh(final File[] files, boolean includeParents) {    
        Set<File> refreshList = new HashSet<File>();
        for (File file : files) {
            File parent = file.getParentFile();
            if(parent != null) {
                refreshList.add(parent);
            }
            refreshList.add(file);
        }                        
        File[] refreshFiles = refreshList.toArray(new File[refreshList.size()]);
        Clearcase.getInstance().getFileStatusCache().refreshLater(refreshFiles);
        FileUtil.refreshFor(refreshFiles); 
    }    
}
