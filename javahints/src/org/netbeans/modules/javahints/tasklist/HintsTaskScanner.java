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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints.tasklist;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.spi.tasklist.PushTaskScanner;
import org.netbeans.spi.tasklist.TaskScanningScope;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;

/**
 *
 * @author lahvac
 */
public class HintsTaskScanner extends PushTaskScanner {

    public HintsTaskScanner() {
        super("HintsTaskScanner", "HintsTaskScanner", null);
    }
    
    static final Logger LOG = Logger.getLogger("org.netbeans.modules.javahints.tasklist");
    
    private TaskScanningScope scope;
    private Callback callback;
    
    @Override
    public synchronized void setScope(TaskScanningScope scope, Callback callback) {
        //cancel all current operations:
        cancelAllCurrent();
        
        this.scope = scope;
        this.callback = callback;
        
        if (scope == null || callback == null)
            return ;
        
        for (FileObject file : scope.getLookup().lookupAll(FileObject.class)) {
            enqueue(new Work(file, callback));
        }
        
        for (Project p : scope.getLookup().lookupAll(Project.class)) {
            for (SourceGroup sg : ProjectUtils.getSources(p).getSourceGroups("java")) {
                enqueue(new Work(sg.getRootFolder(), callback));
            }
        }
    }
    
    private static final Set<RequestProcessor.Task> TASKS = new HashSet<RequestProcessor.Task>();
    private static boolean clearing;
    private static final RequestProcessor WORKER = new RequestProcessor("Java Task Provider");
    private static Map<FileObject, Set<FileObject>> root2FilesWithAttachedErrors = new WeakHashMap<FileObject, Set<FileObject>>();
    
    private static void enqueue(Work w) {
        synchronized (TASKS) {
            final RequestProcessor.Task task = WORKER.post(w);
            
            TASKS.add(task);
            task.addTaskListener(new TaskListener() {
                public void taskFinished(org.openide.util.Task task) {
                    synchronized (TASKS) {
                        if (!clearing) {
                            TASKS.remove(task);
                        }
                    }
                }
            });
            if (task.isFinished()) {
                TASKS.remove(task);
            }
        }
    }
    
    private static void cancelAllCurrent() {
        synchronized (TASKS) {
            clearing = true;
            try {
                for (RequestProcessor.Task t : TASKS) {
                    t.cancel();
                }
                TASKS.clear();
            } finally {
                clearing = false;
            }
        }
        
        synchronized (HintsTaskScanner.class) {
            root2FilesWithAttachedErrors.clear();
        }
        
    }
    
    
    private static final class Work implements Runnable {
        private FileObject fileOrRoot;
        private Callback callback;

        public Work(FileObject fileOrRoot, Callback callback) {
            this.fileOrRoot = fileOrRoot;
            this.callback = callback;
        }
        
        public FileObject getFileOrRoot() {
            return fileOrRoot;
        }

        public Callback getCallback() {
            return callback;
        }
        
        public void run() {
            FileObject file = getFileOrRoot();

            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "dequeued work for: {0}", FileUtil.getFileDisplayName(file));
            }

            ClassPath cp = ClassPath.getClassPath(file, ClassPath.SOURCE);

            if (cp == null) {
                LOG.log(Level.FINE, "cp == null");
                return;
            }

            FileObject root = cp.findOwnerRoot(file);

            if (file.isData()) {
                if ("text/x-java".equals(FileUtil.getMIMEType(file))) {
                    try {
                        TasksResolver.enqueue(root, Collections.singletonList(file), callback);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            } else {
                try {
                    Queue<FileObject> q = new LinkedList<FileObject>();
                    List<FileObject> files = new LinkedList<FileObject>();

                    q.offer(file);

                    while (!q.isEmpty()) {
                        FileObject f = q.poll();

                        if (f.isData()) {
                            if ("text/x-java".equals(FileUtil.getMIMEType(f))) {
                                files.add(f);
                            }
                        } else {
                            for (FileObject fo : f.getChildren()) {
                                q.offer(fo);
                            }
                        }
                    }

                    TasksResolver.enqueue(root, files, callback);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
    
}
