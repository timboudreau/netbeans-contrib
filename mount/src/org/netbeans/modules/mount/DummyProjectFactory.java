/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.mount;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;

/**
 * Loads DummyProject.
 * @author Jesse Glick
 */
public final class DummyProjectFactory implements ProjectFactory {
    
    /** Default instance for lookup. */
    public DummyProjectFactory() {}

    public boolean isProject(FileObject projectDirectory) {
        try {
            return projectDirectory == WorkDir.get();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
            return false;
        }
    }

    public Project loadProject(FileObject projectDirectory, ProjectState state) throws IOException {
        if (projectDirectory == WorkDir.get()) {
            return new DummyProject(projectDirectory);
        } else {
            return null;
        }
    }

    public void saveProject(Project project) throws IOException, ClassCastException {
        throw new UnsupportedOperationException();
    }
    
}
