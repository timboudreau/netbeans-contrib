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
package org.netbeans.modules.ada.project.ui.actions;

import java.io.File;
import java.io.IOException;
import org.netbeans.api.ada.platform.AdaPlatform;
import org.netbeans.modules.ada.platform.compiler.gnat.GnatCompilerCommand;
import org.netbeans.modules.ada.project.AdaActionProvider;
import org.netbeans.modules.ada.project.AdaProject;
import org.netbeans.modules.ada.project.AdaProjectUtil;
import org.netbeans.modules.ada.project.ui.properties.AdaProjectProperties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Andrea Lucarelli
 */
public class BuildCommand extends Command {

    private static final String COMMAND_ID = AdaActionProvider.COMMAND_BUILD;

    public BuildCommand(AdaProject project) {
        super(project);
    }

    @Override
    public String getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public void invokeAction(Lookup context) throws IllegalArgumentException {
        // Retrieve project and platform
        final AdaProject project = getProject();
        AdaPlatform platform = AdaProjectUtil.getActivePlatform(project);
        assert platform != null;

        // Retrieve main file
        final FileObject mainFile = findMainFile(project);
        assert mainFile != null;

        // Create Build/Dist folders
        try {
            createBuildRoot(project);
            createDistRoot(project);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        // Init compiler factory
        GnatCompilerCommand comp = new GnatCompilerCommand(
                platform,
                project.getProjectDirectory().getPath(),  // project location
                project.getSourcesDirectory().getPath(),  // sources location
                FileUtil.toFile(mainFile).getPath(),      // main file
                project.getName(),                        // executable file
                project.getName() + " (build)"); // NOI18N - display name

        // Start build
        comp.Build();
    }

    @Override
    public boolean isActionEnabled(Lookup context) throws IllegalArgumentException {
        final AdaProject project = getProject();
        AdaPlatform platform = AdaProjectUtil.getActivePlatform(project);
        if (platform == null) {
            return false;
        }
        return true;
    }

    protected static FileObject findMainFile(final AdaProject project) {
        final FileObject[] roots = project.getSourceRoots().getRoots();
        final String mainFile = project.getEvaluator().getProperty(AdaProjectProperties.MAIN_FILE);
        if (mainFile == null) {
            return null;
        }
        FileObject fo = null;
        for (FileObject root : roots) {
            fo = root.getFileObject(mainFile);
            if (fo != null) {
                break;
            }
        }
        return fo;
    }

    private void createBuildRoot(final AdaProject project) throws IOException {
        FileUtil.createFolder(new File(FileUtil.toFile(project.getProjectDirectory()), "build")); // NOI18N
    }

    private void createDistRoot(final AdaProject project) throws IOException {
        FileUtil.createFolder(new File(FileUtil.toFile(project.getProjectDirectory()), "dist")); // NOI18N
    }
}
