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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.JavaSource.Priority;
import org.netbeans.api.java.source.JavaSourceTaskFactory;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.java.hints.infrastructure.Pair;
import org.netbeans.spi.tasklist.PushTaskScanner.Callback;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author lahvac
 */
public class TasksResolver {

    private static FileSystem temp = FileUtil.createMemoryFileSystem();
    private static int count;
    private static Map<FileObject, TaskImpl> fake2Task = new HashMap<FileObject, TaskImpl>();
    
    public static synchronized void enqueue(FileObject root, List<FileObject> files, Callback callback) throws IOException {
        if (HintsTaskScanner.LOG.isLoggable(Level.FINE)) {
            List<String> fileNames = new LinkedList<String>();
            
            for (FileObject f : files) {
                fileNames.add(FileUtil.getFileDisplayName(f));
            }
            
            HintsTaskScanner.LOG.log(Level.FINE, "enqueue, root={0}, files={1}", new Object[]{FileUtil.getFileDisplayName(root), fileNames});
        }
        
        //XXX: coalescing of work:
        FileObject fake = temp.getRoot().createData("temp" + count++, "java");
        TaskImpl t = new TaskImpl(root, files, callback);
        
        fake2Task.put(fake, t);
        
        Lookup.getDefault().lookup(FactoryImpl.class).fileObjectsChangedInt();
    }
    
    private static final class TaskImpl implements CancellableTask<CompilationInfo> {

        private final FileObject root;
        private final List<FileObject> toProcess;
        private final Callback callback;
        private List<FileObject> processedFiles = new LinkedList<FileObject>();
        private final AtomicBoolean cancel = new AtomicBoolean();

        public TaskImpl(FileObject root, List<FileObject> toProcess, Callback callback) {
            this.root = root;
            this.toProcess = toProcess;
            this.callback = callback;
        }
        
        public void run(CompilationInfo parameter) throws Exception {
            if (toProcess.isEmpty()) return ;
            
            final ProgressHandle h = ProgressHandleFactory.createHandle("Computing Editor Hints for Tasklist");
            
            h.start(toProcess.size());
            
            try {
            ClasspathInfo cpInfo = ClasspathInfo.create(root);
            
            if (HintsTaskScanner.LOG.isLoggable(Level.FINE)) {
                HintsTaskScanner.LOG.log(Level.FINE, "processing root={0}", FileUtil.getFileDisplayName(root));
            }

            JavaSource js = JavaSource.create(cpInfo, toProcess);
            final int[] i = new int[1];
            
            js.runUserActionTask(new Task<CompilationController>() {
                public void run(CompilationController parameter) throws Exception {
                    if (cancel.get()) return ;
                    
                    h.progress(parameter.getFileObject().getNameExt());
                    
                    Phase current = parameter.toPhase(Phase.RESOLVED);
                    
                    if (current != Phase.RESOLVED && current != Phase.UP_TO_DATE) {
                        return ;
                    }
                    
                    List<? extends org.netbeans.spi.tasklist.Task> tasks = ComputeTasks.computeTasks(parameter);
                    
                    callback.setTasks(parameter.getFileObject(), tasks);
                    
                    processedFiles.add(parameter.getFileObject());
                    
                    h.progress(++i[0]);
                }
            }, true);
            
            Collection<FileObject> remainingFiles = new HashSet<FileObject>(toProcess);
            
            remainingFiles.removeAll(processedFiles);
            
            if (HintsTaskScanner.LOG.isLoggable(Level.FINE)) {
                List<String> remainingFilesNames = new LinkedList<String>();

                for (FileObject f : remainingFiles) {
                    remainingFilesNames.add(FileUtil.getFileDisplayName(f));
                }
                HintsTaskScanner.LOG.log(Level.FINE, "remainingFiles {0}", remainingFilesNames);
            }
            
            if (!remainingFiles.isEmpty()) {
                enqueue(root, new LinkedList<FileObject>(remainingFiles), callback);
            }
            } finally {
                h.finish();
            }
        }
        
        public void cancel() {
            if (HintsTaskScanner.LOG.isLoggable(Level.FINE)) {
                HintsTaskScanner.LOG.log(Level.FINE, "cancelled", new Throwable());
            }
            cancel.set(true);
        }

    }
    
    @org.openide.util.lookup.ServiceProvider(service=org.netbeans.api.java.source.JavaSourceTaskFactory.class)
    public static final class FactoryImpl extends JavaSourceTaskFactory {

        public FactoryImpl() {
            super(Phase.PARSED, Priority.MIN);
        }

        @Override
        protected CancellableTask<CompilationInfo> createTask(FileObject file) {
            return fake2Task.get(file);
        }

        @Override
        protected Collection<FileObject> getFileObjects() {
            return new LinkedList<FileObject>(fake2Task.keySet());
        }
        
        void fileObjectsChangedInt() {
            fileObjectsChanged();
        }
    }
    
}
