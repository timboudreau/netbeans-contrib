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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
package org.netbeans.modules.docbook.project;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.project.ProjectState;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall implements Runnable {
    private static final String PREVIOUSLY_OPENED_KEY =
            "org.netbeans.modules.docbook.project.previouslyOpenProjects";
    private static final String PREVIOUSLY_MAIN_KEY =
            "org.netbeans.modules.docbook.project.previouslyMainProject";

    //all this to make the module really reloadable... :-/

    public void restored() {
        //If reloading the module, reopen previously open projects.
        //Otherwise we'll end up with current nodes under a project
        //we can't recognize because it's the old DbProject.class from
        //the previously loaded module
        String s = System.getProperty(PREVIOUSLY_OPENED_KEY);
        if (s != null) {
            ProjectManager.mutex().writeAccess(this);
        }
    }

    public void run() {
        String s = System.getProperty(PREVIOUSLY_OPENED_KEY);
        if (s != null) {
            String[] paths = s.split(",");
            DbProjectFactory factory =
                    Lookup.getDefault().lookup(DbProjectFactory.class);
            assert factory != null;
            Set toOpen = new HashSet();
            for (int i = 0; i < paths.length; i++) {
                File f = new File (paths[i]);
                if (f.exists() && f.isDirectory()) {
                    FileObject ob = FileUtil.toFileObject(f);
                    if (ob != null) {
                        if (factory.isProject(ob)) {
                            Project p;
                            try {
                                ProjectManager.getDefault().clearNonProjectCache();
                                p = ProjectManager.getDefault().findProject(ob);
                                if ((p != null && p instanceof DbProject) ||
                                        (p.getLookup().lookup(DbProject.class) != null)) {
                                    toOpen.add (p);
                                } else if (p != null && !(p instanceof DbProject)) {
                                    //XXX how to get a real instance of
                                    //ProjectState - impossible?
                                    //After a reload projects won't be saved
                                    //correctly as a result of this.
                                    Project proj = factory.loadProject(ob, new FakeState());
                                    toOpen.add(proj);
                                }
                            } catch (IllegalArgumentException ex) {
                                ErrorManager.getDefault().notify(ex);
                            } catch (IOException ex) {
                                ErrorManager.getDefault().notify(ex);
                            }
                        }
                    }
                }
            }
            if (!toOpen.isEmpty()) {
                final Project[] open = (Project[]) toOpen.toArray(
                        new Project[toOpen.size()]);
                EventQueue.invokeLater (new Runnable() {
                    //get this out of the way of the mutex or it will
                    //deadlock
                    public void run() {
                        OpenProjects.getDefault().open(open, false);
                        String s = System.getProperty(PREVIOUSLY_MAIN_KEY);
                        if (s != null) {
                            for (int i = 0; i < open.length; i++) {
                                String path = open[i].getProjectDirectory().getPath();
                                if (path.equals(s)) {
                                    //XXX this throws an IAE because we
                                    //constructed our project ourselves rather
                                    //than go through project manager (which
                                    //would have returned the stale old module's
                                    //project).  Ugh.
//                                    OpenProjects.getDefault().setMainProject(open[i]);
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    public void uninstalled() {
        Project[] p = OpenProjects.getDefault().getOpenProjects();
        Set <DbProject> s = new HashSet <DbProject> ();
        for (int i = 0; i < p.length; i++) {
            DbProject proj = p[i] instanceof DbProject ?
                (DbProject) p[i] : p[i].getLookup().lookup(DbProject.class);
            if (proj != null) {
                s.add(proj);
            }
        }
        if (!s.isEmpty()) {
            final DbProject[] toClose = (DbProject[]) s.toArray (new
                    DbProject[s.size()]);
            Runnable r = new Runnable() {
                public void run() {
                    for (int i = 0; i < toClose.length; i++) {
                        FakeState state = (FakeState)
                                toClose[i].getLookup().lookup(
                                FakeState.class);
                        if (state != null && state.modified) {
                            try {
                                toClose[i].save();
                            } catch (IOException ex) {
                                ErrorManager.getDefault().notify(ex);
                            }
                        }
                    }
                    OpenProjects.getDefault().close(toClose);
                }
            };
            ProjectManager.mutex().writeAccess(r);
            StringBuilder b = new StringBuilder();
            for (Iterator <DbProject> i=s.iterator(); i.hasNext();) {
                DbProject proj = i.next();
                b.append (proj.getProjectDirectory().getPath());
                if (i.hasNext()) {
                    b.append (',');
                }
                Project main = OpenProjects.getDefault().getMainProject();
                if (main != null && main.getProjectDirectory().getPath().equals(proj.getProjectDirectory().getPath())) {
                    System.setProperty(PREVIOUSLY_MAIN_KEY, main.getProjectDirectory().getPath());
                }
            }
            System.setProperty(PREVIOUSLY_OPENED_KEY, b.toString());
        }
    }

    private static final class FakeState implements ProjectState {
        boolean modified;
        public void markModified() {
            //do nothing
            modified = true;
        }

        public void notifyDeleted() throws IllegalStateException {
            //do nothing
        }
    }
}
