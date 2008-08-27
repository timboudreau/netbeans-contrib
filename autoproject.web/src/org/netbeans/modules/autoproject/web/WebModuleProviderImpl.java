/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.web;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.autoproject.spi.Cache;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleFactory;
import org.netbeans.modules.web.spi.webmodule.WebModuleProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

public class WebModuleProviderImpl implements WebModuleProvider {

    private static final Logger LOG = Logger.getLogger(WebModuleProviderImpl.class.getName());
    private static final Map<Project,Map<FileObject, WebModule>> webModules = new WeakHashMap<Project,Map<FileObject, WebModule>>();
    
    private Project p;
    private String root;

    public WebModuleProviderImpl(Project p) {
        this.p = p;
        root = FileUtil.toFile(p.getProjectDirectory()).getAbsolutePath();
    }
    
    public WebModule findWebModule (FileObject file) {
        assert p.equals(FileOwnerQuery.getOwner(file));

        String docBaseFolders = Cache.get(root + WebCacheConstants.DOCROOT);
        if (docBaseFolders == null) {
            return null;
        }
        FileObject docRoot = null;
        for (String piece : docBaseFolders.split("[:;]")) {
            FileObject aRoot = FileUtil.toFileObject(new File(piece));
            if (aRoot != null && (FileUtil.isParentOf(aRoot, file) || aRoot == file)) {
                docRoot = aRoot;
                break;
            }
        }
        if (docRoot == null) {
            LOG.log(Level.FINE, "Found no docroot for {0} in {1}", new Object[] {file, p.getProjectDirectory()});
            return null;
        }
        Map<FileObject, WebModule> projectWebModules = webModules.get(p);
        if (projectWebModules == null) {
            projectWebModules = new HashMap<FileObject, WebModule>();
            webModules.put(p, projectWebModules);
        }
        WebModule wm = projectWebModules.get(docRoot);
        if (wm == null) {
            wm = WebModuleFactory.createWebModule(new WebModuleImpl(docRoot, root));
            projectWebModules.put(docRoot, wm);
        }
        return wm;
    }
    
}
