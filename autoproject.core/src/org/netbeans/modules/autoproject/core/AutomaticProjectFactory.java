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

package org.netbeans.modules.autoproject.core;

import java.io.File;
import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.autoproject.spi.Cache;
import org.netbeans.modules.autoproject.spi.ProjectDetector;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

/**
 * Recognizer for automatic projects.
 */
@ServiceProvider(service=ProjectFactory.class, position=999)
public class AutomaticProjectFactory implements ProjectFactory {

    private static final String AUTOMATIC_DETECTION_MODE = "automaticDetectionMode";

    public static boolean isAutomaticDetectionMode() {
        return NbPreferences.forModule(AutomaticProjectFactory.class).getBoolean(AUTOMATIC_DETECTION_MODE, false);
    }

    public static void setAutomaticDetectionMode(boolean enabled) {
        NbPreferences.forModule(AutomaticProjectFactory.class).putBoolean(AUTOMATIC_DETECTION_MODE, enabled);
    }

    public boolean isProject(FileObject projectDirectory) {
        File d = FileUtil.toFile(projectDirectory);
        if (d != null) {
            if (Boolean.parseBoolean(Cache.get(d + Cache.PROJECT))) {
                return true;
            }
        }
        if (!isAutomaticDetectionMode()) {
            return false;
        }
        boolean detected = false;
        for (ProjectDetector detector : Lookup.getDefault().lookupAll(ProjectDetector.class)) {
            if (detector.isProject(projectDirectory)) {
                detected = true;
                break;
            }
        }
        if (!detected) {
            return false;
        }
        for (FileObject parent = projectDirectory.getParent(); parent != null; parent = parent.getParent()) {
            if (ProjectManager.getDefault().isProject(parent)) {
                // Do not ever load a subdir of another project.
                // Otherwise you get effects like $module/test/unit/src being considered a separate project.
                return false;
            }
        }
        return true;
    }

    public Project loadProject(FileObject projectDirectory, ProjectState state) throws IOException {
        if (isProject(projectDirectory)) {
            return new AutomaticProject(projectDirectory);
        } else {
            return null;
        }
    }

    public void saveProject(Project project) throws IOException, ClassCastException {
        // no configuration, nothing to do...
    }

}
