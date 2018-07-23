/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */

package org.netbeans.modules.remote.project.finder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tomas Zezula
 */
final class WorkSpaceUpdater implements Runnable {

    private static final Logger LOG = Logger.getLogger(WorkSpaceUpdater.class.getName());
    private static final RequestProcessor RP = new RequestProcessor(WorkSpaceUpdater.class);
    
    //@GuardedBy("WorkSpaceUpdater.class")
    private static WorkSpaceUpdater instance;
    private final BlockingQueue<Callable<Boolean>> requests;
    private final Object lock = new Object();
    private FileObject workSpace;
    private int maxDepth;
    //@GuardedBy("lock")
    private int state;

    private WorkSpaceUpdater() {
        this.requests = new LinkedBlockingDeque<>();
    }

    @NonNull
    static synchronized WorkSpaceUpdater getDefault() {
        if (instance == null) {
            instance = new WorkSpaceUpdater();
        }
        return instance;
    }

    @CheckForNull
    FileObject getRepository() {
        return workSpace;
    }

    void configure(
            @NonNull final File workSpace,
            final int depth) {
        Parameters.notNull("workSpace", workSpace); //NOI18N
        if (depth < 1) {
            throw new IllegalArgumentException(Integer.toString(depth));
        }
        this.workSpace = FileUtil.toFileObject(workSpace);
        if (this.workSpace == null) {
            throw new IllegalArgumentException(
                "Cannot resolve workspace: " + workSpace.getAbsolutePath());    //NOI18N
        }
        this.maxDepth = depth;
        LOG.log(
            Level.INFO,
            "Configuring workspace updater for: {0}",    //NOI18N
            FileUtil.getFileDisplayName(this.workSpace));
        moveState(1);
    }

    void start() {
        LOG.log(
            Level.INFO,
            "Starting workspace updater."); //NOI18N
        RP.execute(this);
        moveState(2);
    }

    void stop() {
        LOG.log(
            Level.INFO,
            "Stopping workspace updater."); //NOI18N
        moveState(0);
    }

    @NonNull
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                final Callable<Boolean> rq = requests.take();
                rq.call();
            } catch (InterruptedException _ex) {
                Thread.currentThread().interrupt();
            } catch (StopException stop) {
                break;
            }catch (Exception e) {
                Exceptions.printStackTrace(e);
            }
        }
    }

    private int moveState(int state) {
        if (state < 0 || state > 2) {
            throw new IllegalArgumentException("Invalid state: " + state);
        }
        synchronized (lock) {
            if (state == 0) {
                this.state = 0;
            } else {
                this.state |= state;
            }
            switch (this.state) {
                case 0:
                    requests.offer(new Stop());
                    break;
                case 3:
                    requests.offer(new OpenWorkspace(workSpace, maxDepth));
                    break;
            }
            return this.state;
        }
    }

    private static final class StopException extends Exception {
    }

    private static final class OpenWorkspace implements Callable<Boolean> {

        private final FileObject workSpace;
        private final int maxDepth;

        OpenWorkspace(
                @NonNull final FileObject workSpace,
                final int maxDepth) {
            Parameters.notNull("workSpace", workSpace); //NOI18N
            this.workSpace = workSpace;
            this.maxDepth = maxDepth;
        }

        @Override
        public Boolean call() throws Exception {
            final Collection<FileObject> projectDirs = new ArrayList<>();
            findProjects(workSpace, 0, projectDirs);
            open(projectDirs);
            return Boolean.TRUE;
        }

        private void findProjects(
            @NonNull final FileObject folder,
            final int depth,
            @NonNull final Collection<? super FileObject> collector) {
            if (depth == maxDepth) {
                return;
            }
            for (FileObject child : folder.getChildren()) {
                if (child.isFolder()) {
                    if (isProject(child)) {
                        collector.add(child);
                    } else {
                        findProjects(child, depth+1, collector);
                    }
                }
            }
        }

        private static boolean isProject(@NonNull final FileObject file) {
            return ProjectManager.getDefault().isProject(file);
        }

        private static void open(@NonNull final Collection<? extends FileObject> projectFolders) {
            final Collection<Project> projects = new ArrayList<>();
            final ProjectManager pm = ProjectManager.getDefault();
            for (FileObject projectFolder : projectFolders) {
                try {
                    final Project p = pm.findProject(projectFolder);
                    if (p != null) {
                        projects.add(p);
                    }
                } catch (IOException ioe) {
                    Exceptions.printStackTrace(ioe);
                }
            }
            OpenProjects.getDefault().open(
                projects.toArray(new Project[projects.size()]),
                false,
                false);
        }
    }

    private static final class Stop implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception {
            throw new StopException();
        }
    }
}
