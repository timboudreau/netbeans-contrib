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

package org.netbeans.modules.latex.guiproject;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.api.retouche.source.CompilationInfo;
import org.netbeans.api.retouche.source.Phase;
import org.netbeans.api.retouche.source.Source.Priority;
import org.netbeans.api.retouche.source.SourceTaskFactory;
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
                LaTeXParserResult lpr = (LaTeXParserResult) parameter.getParserResult();
                
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
        return Lookup.getDefault().lookup(ProjectReparsedTaskFactory.class);
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
