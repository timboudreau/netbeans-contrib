/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.selenium;

import java.util.Enumeration;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Jindrich Sedek, Martin Fousek
 */
public abstract class ExtendedAction extends NodeAction {

    private static String INCLUDES_DELIMITER = ",";

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return true;
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        for (Node node : activatedNodes) {
            Project proj = getProjectForNode(node);
            if ((proj != null) && SeleniumSupport.hasSeleniumDir(proj) && findBuildXml(proj) != null) {
                return true;
            }
        }
        return false;
    }

    protected Project getProjectForNode(Node node) {
        Project proj = node.getLookup().lookup(Project.class);
        if (proj == null) {
            FileObject fo = node.getLookup().lookup(FileObject.class);
            if (fo != null) {
                proj = FileOwnerQuery.getOwner(fo);
            }
        }
        return proj;
    }

    protected FileObject findBuildXml(Project project) {
        return project.getProjectDirectory().getFileObject("build.xml");
    }

    static String listAllTestIncludes(FileObject file) {
        String result = null;
        // add files without any package if any
        if (file.getChildren().length > 0) {
            for (FileObject fileObject : file.getChildren()) {
                if ("java".equals(fileObject.getExt())) {
                    result = "*Test.java";
                    break;
                }
            }
        }

        // add all packages
        Enumeration<? extends FileObject> en = file.getFolders(true);

        while (en.hasMoreElements()) {
            FileObject next = en.nextElement();
            assert (next.isFolder());
            Enumeration<? extends FileObject> childrenData = next.getData(false);
            boolean containsJavaFile = false;
            while (childrenData.hasMoreElements()) {
                if ("java".equals(childrenData.nextElement().getExt())) {
                    containsJavaFile = true;
                }
            }
            if (containsJavaFile) {
                if (result == null) {
                    result = FileUtil.getRelativePath(file, next) + "/*Test.java";
                } else {
                    result = result + INCLUDES_DELIMITER + FileUtil.getRelativePath(file, next) + "/*Test.java";
                }
            }

        }
        return result;
    }
}
