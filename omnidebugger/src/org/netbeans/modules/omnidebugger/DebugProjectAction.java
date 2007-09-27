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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.omnidebugger;

import java.io.IOException;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.project.ui.support.MainProjectSensitiveActions;
import org.netbeans.spi.project.ui.support.ProjectActionPerformer;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 * Action to debug the main class of a project.
 * @author Jesse Glick
 */
public class DebugProjectAction implements ProjectActionPerformer {
    
    public static Action createProjectSensitiveAction() {
        return ProjectSensitiveActions.projectSensitiveAction(
                new DebugProjectAction(),
                "Omniscient Debug {0,choice,0#Project|1#\"{1}\"}", // XXX I18N
                null);
    }
    
    public static Action createMainProjectSensitiveAction() {
        String icon = "org/netbeans/modules/omnidebugger/omnidebugProject.gif"; // NOI18N
        Action a = MainProjectSensitiveActions.mainProjectSensitiveAction(
                new DebugProjectAction(),
                "Omniscient Debug Main Project", // XXX I18N
                new ImageIcon(Utilities.loadImage(icon, true)));
        // Make sure 24x24 variant is available:
        a.putValue("iconBase", icon); // NOI18N
        return a;
    }
    
    private DebugProjectAction() {
    }
    
    private FileObject findMainClass(Project project) {
        assert project != null;
        String clazz = Debug.evaluatorFor(project).getProperty("main.class"); // NOI18N
        if (clazz == null) {
            return null;
        }
        Sources s = ProjectUtils.getSources(project);
        for (SourceGroup g : s.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA)) {
            FileObject f = g.getRootFolder().getFileObject(clazz.replace('.', '/') + ".java"); // NOI18N
            if (f != null) {
                return f;
            }
        }
        return null;
    }

    public boolean enable(Project project) {
        if (project == null) {
            return false;
        }
        return findMainClass(project) != null;
    }

    public void perform(Project project) {
        try {
            Debug.start(findMainClass(project));
        } catch (IOException x) {
            Exceptions.printStackTrace(x);
        }
    }
    
}
