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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
import org.netbeans.modules.java.hints.infrastructure.Pair;
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
