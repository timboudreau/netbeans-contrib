/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Contributor(s): Tim Boudreau
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.nodejs;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Set;
import java.util.Iterator;
import org.openide.util.WeakSet;
import org.openide.util.lookup.ServiceProvider;
import org.netbeans.api.project.ProjectManager.Result;
import org.netbeans.spi.project.ProjectFactory2;
import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;
import static org.netbeans.modules.nodejs.NodeJSProject.*;

/**
 *
 * @author Tim Boudreau
 */
@ServiceProvider(service = ProjectFactory2.class)
public class NodeJSProjectFactory implements ProjectFactory2 {

    private final Set<NodeJSProject> cache = 
            Collections.synchronizedSet(new WeakSet<NodeJSProject>());

    @Override
    public boolean isProject(FileObject fo) {
        FileObject metadataDir = fo.getFileObject(METADATA_DIR);
        FileObject metadataFile = metadataDir == null ? null
                : metadataDir.getFileObject(NodeJSProject.METADATA_PROPERTIES_FILE);
        return metadataFile != null;
    }

    NodeJSProject findOwner(FileObject fo) throws IOException {
        List<NodeJSProject> l = new ArrayList<NodeJSProject>(cache);
        //Sort by longest-path first, so the deepest directory which is 
        //a project gets the first chance to claim it in the case of nested
        //projects
        Collections.sort(l);
        for (NodeJSProject cached : l) {
            if (FileUtil.isParentOf(cached.getProjectDirectory(), fo)) {
                return cached;
            }
        }
        FileObject projectDir = fo;
        while (projectDir != null && (!projectDir.isFolder() || projectDir.getFileObject(NodeJSProject.METADATA_DIR) == null)) {
            projectDir = projectDir.getParent();
        }
        if (projectDir != null && projectDir.getFileObject(NodeJSProject.METADATA_DIR) != null) {
            Project p = ProjectManager.getDefault().findProject(projectDir);
            if (p != null) {
                return p.getLookup().lookup(NodeJSProject.class);
            }
        }
        return null;
    }

    NodeJSProject find(FileObject fo) {
        for (NodeJSProject prj : cache) {
            if (fo.equals(prj.getProjectDirectory())) {
                return prj;
            }
        }
        return null;
    }

    @Override
    public Project loadProject(FileObject fo, ProjectState ps) throws IOException {
        NodeJSProject result = new NodeJSProject(fo, ps);
        for (Iterator<NodeJSProject> i = cache.iterator(); i.hasNext();) {
            NodeJSProject p = i.next();
            if (fo.equals(p.getProjectDirectory())) {
                i.remove();
            }
        }
        cache.add(result);
        return result;
    }

    @Override
    public void saveProject(Project prjct) throws IOException, ClassCastException {
        NodeJSProject project = prjct.getLookup().lookup(NodeJSProject.class);
        if (project != null) {
            project.metadata().save();
        }
    }

    @Override
    public Result isProject2(FileObject fo) {
        return new ProjectManager.Result(ImageUtilities.loadImageIcon(
                NodeJSProjectFactory.class.getPackage().getName().replace('.', '/')
                + "project.png", false));
    }
}
