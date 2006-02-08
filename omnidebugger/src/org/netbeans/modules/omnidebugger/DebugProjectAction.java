/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
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
        SourceGroup[] javaRoots = s.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (int i = 0; i < javaRoots.length; i++) {
            FileObject f = javaRoots[i].getRootFolder().getFileObject(clazz.replace('.', '/') + ".java"); // NOI18N
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
            ErrorManager.getDefault().notify(x);
        }
    }
    
}
