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

package org.netbeans.modules.latex.guiproject;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.modules.gsf.api.CancellableTask;
import org.netbeans.napi.gsfret.source.CompilationInfo;
import org.netbeans.napi.gsfret.source.Phase;
import org.netbeans.napi.gsfret.source.Source.Priority;
import org.netbeans.napi.gsfret.source.SourceTaskFactory;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class ProjectReparsedTaskFactory extends SourceTaskFactory {

    public ProjectReparsedTaskFactory() {
        super(Phase.RESOLVED, Priority.BELOW_NORMAL);
    }
    
    protected CancellableTask<CompilationInfo> createTask(final FileObject file) {
        return new CancellableTask<CompilationInfo>() {
            public void cancel() {}
            public void run(CompilationInfo parameter) throws Exception {
                LaTeXParserResult lpr = LaTeXParserResult.get(parameter);
                
                LaTeXGUIProject p = (LaTeXGUIProject) LaTeXGUIProjectFactorySourceFactory.get().mainFile2Project.get(file);
                
                if (p != null) {
                    p.setContainedFile(lpr.getDocument().getFiles());
                }
            }
        };
    }

    protected synchronized Collection<FileObject> getFileObjects() {
        return Collections.unmodifiableList(registeredFiles);
    }
    
    static ProjectReparsedTaskFactory get() {
        for (SourceTaskFactory f : Lookup.getDefault().lookupAll(SourceTaskFactory.class)) {
            if (f.getClass() == ProjectReparsedTaskFactory.class) {
                return (ProjectReparsedTaskFactory) f;
            }
        }
        
        return null;
    }
    
    synchronized void registerFile(FileObject main) {
        registeredFiles.add(main);
        fileObjectsChanged();
    }
    
    synchronized void unregisterFile(FileObject main) {
        registeredFiles.remove(main);
        fileObjectsChanged();
    }

    private List<FileObject> registeredFiles = new LinkedList<FileObject>();
    
}
