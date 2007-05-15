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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.javahints;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.JavaSource.Priority;
import org.netbeans.api.java.source.JavaSourceTaskFactory;
import org.netbeans.modules.java.hints.Pair;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Jan Lahoda
 */
public class RunOnceFactory extends JavaSourceTaskFactory {

    private static final Logger LOG = Logger.getLogger(RunOnceFactory.class.getName());
    
    static {
        LOG.setLevel(Level.ALL);
    }
    
    private static RunOnceFactory INSTANCE;
    
    private List<Pair<FileObject, CancellableTask<CompilationInfo>>> work = new LinkedList<Pair<FileObject, CancellableTask<CompilationInfo>>>();
    private FileObject currentFile;
    private CancellableTask<CompilationInfo> task;
    
    public RunOnceFactory() {
        super(Phase.RESOLVED, Priority.BELOW_NORMAL);
        INSTANCE = this;
    }

    protected synchronized CancellableTask<CompilationInfo> createTask(FileObject file) {
        final CancellableTask<CompilationInfo> task = this.task;
        return new CancellableTask<CompilationInfo>() {
            public void cancel() {
                task.cancel();
            }
            public void run(CompilationInfo parameter) throws Exception {
                task.run(parameter);
                next();
            }
        };
    }

    protected synchronized Collection<FileObject> getFileObjects() {
        if (currentFile == null)
            return Collections.<FileObject>emptyList();
        
        return Collections.<FileObject>singletonList(currentFile);
    }

    private synchronized void addImpl(FileObject file, CancellableTask<CompilationInfo> task) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "addImpl({0}, {1})", new Object[] {FileUtil.getFileDisplayName(file), task.getClass().getName()});
        }
        
        work.add(new Pair<FileObject, CancellableTask<CompilationInfo>>(file, task));
        
        if (currentFile == null)
            next();
    }
    
    private synchronized void next() {
        LOG.fine("next, phase 1");
        
        if (currentFile != null) {
            currentFile = null;
            task = null;
            fileObjectsChanged();
        }
        
        LOG.fine("next, phase 1 done");
        
        if (work.isEmpty())
            return ;
        
        LOG.fine("next, phase 2");
        
        Pair<FileObject, CancellableTask<CompilationInfo>> p = work.remove(0);
        
        currentFile = p.getA();
        task = p.getB();
        
        fileObjectsChanged();
        
        LOG.fine("next, phase 2 done");
    }
    

    public static void add(FileObject file, CancellableTask<CompilationInfo> task) {
        if (INSTANCE == null)
            return ;
        
        INSTANCE.addImpl(file, task);
    }
}
