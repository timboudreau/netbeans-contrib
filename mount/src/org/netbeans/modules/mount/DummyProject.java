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
