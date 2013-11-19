/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    private File workSpace;
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

    void configure(
            @NonNull final File workSpace,
            final int depth) {
        Parameters.notNull("workSpace", workSpace); //NOI18N
        if (depth < 1) {
            throw new IllegalArgumentException(Integer.toString(depth));
        }
        this.workSpace = workSpace;
        this.maxDepth = depth;
        LOG.log(
            Level.INFO,
            "Configuring workspace updater for: {0}",    //NOI18N
            this.workSpace.getAbsolutePath());
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
                @NonNull final File workSpace,
                final int maxDepth) {
            Parameters.notNull("workSpace", workSpace); //NOI18N
            this.workSpace = FileUtil.toFileObject(workSpace);
            if (this.workSpace == null) {
                throw new IllegalArgumentException(String.format(
                    "The % cannot be converted to FileObject.",
                    workSpace.getAbsolutePath()));
            }
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
