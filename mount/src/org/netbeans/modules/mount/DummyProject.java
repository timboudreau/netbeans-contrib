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

package org.netbeans.modules.mount;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.Icon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectManager;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * "Project" corresponding to mounts.
 * @author Jesse Glick
 */
final class DummyProject implements Project {

    /** Hold it permanently so that it is not collected; singleton. */
    private static Project INSTANCE;
    
    public static Project getInstance() {
        if (INSTANCE == null) {
            try {
                INSTANCE = ProjectManager.getDefault().findProject(WorkDir.get());
            } catch (IOException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        return INSTANCE;
    }
    
    private final Lookup lookup;
    private final FileObject dir;

    DummyProject(FileObject dir) {
        this.dir = dir;
        final Classpaths classpaths = new Classpaths();
        lookup = Lookups.fixed(new Object[] {
            this,
            new Info(),
            classpaths,
            new SourcesForBinary(),
            new MountSources(),
            new Actions(),
            // XXX SourceLevelQueryImplementation
        });
        ProjectManager.mutex().postWriteRequest(new Runnable() {
            public void run() {
                classpaths.register();
            }
        });
    }
    
    public Lookup getLookup() {
        return lookup;
    }

    public FileObject getProjectDirectory() {
        return dir;
    }
    
    private final class Info implements ProjectInformation {
        
        public Info() {}
        
        public String getName() {
            return DummyProject.class.getName();
        }
        
        public String getDisplayName() {
            return "Filesystems";
        }
        
        public Icon getIcon() {
            // XXX
            return null;
        }
        
        public Project getProject() {
            return DummyProject.this;
        }
        
        public void addPropertyChangeListener(PropertyChangeListener listener) {}
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {}
        
    }
    
}
