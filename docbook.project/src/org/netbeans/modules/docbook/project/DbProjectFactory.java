/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.docbook.project;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Tim Boudreau
 */
public class DbProjectFactory implements ProjectFactory {

    public DbProjectFactory() {
    }

    public boolean isProject(FileObject projectDirectory) {
        return DbProject.isProject (projectDirectory);
    }

    public Project loadProject(FileObject projectDirectory, ProjectState state) throws IOException {
        if (isProject(projectDirectory)) {
            return new DbProject (projectDirectory, state);
        } else {
            return null;
        }
    }

    public void saveProject(Project project) throws IOException, ClassCastException {
        DbProject proj = (DbProject) project.getLookup().lookup (DbProject.class);
        if (proj != null) {
            proj.save();
        }
    }

}
