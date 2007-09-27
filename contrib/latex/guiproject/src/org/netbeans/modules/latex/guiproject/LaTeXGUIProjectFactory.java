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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject;
import java.io.File;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import org.netbeans.api.project.Project;
import org.netbeans.modules.latex.model.command.DocumentNode;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXGUIProjectFactory implements ProjectFactory {
    
    /** Creates a new instance of LaTeXGUIProjectFactory */
    public LaTeXGUIProjectFactory() {
//        Thread.dumpStack();
//        mainFile2Project = new WeakHashMap(); /*is weak correct?*/
    }
    
    private static final String TAG = "nbproject-";
    
    public boolean isProject(FileObject projectDirectory) {
//        System.err.println("LaTeXGUIProjectFactory.isProject(" + projectDirectory + ")");
        
        return getMasterFile(projectDirectory) != null;
    }
    
    private FileObject getMasterFile(FileObject projectDirectory) {
//        System.err.println("getMasterFile(" + projectDirectory + ")");
        InputStream ins = null;
        
        try {
            FileObject settings = projectDirectory.getFileObject("build-settings.properties");
            
//            System.err.println("settings = " + settings );
            if (settings == null)
                return null;
            
            EditableProperties p = new EditableProperties();
            
            ins = settings.getInputStream();
            p.load(ins); //TODO: close the stream.
            String mainFileName = p.getProperty("mainfile");
            
            if (mainFileName == null)
                return null;
            
//            System.err.println("mainFileName = " + mainFileName );
            
            File projectDirectoryFile = FileUtil.toFile(projectDirectory);
            File mainFile = new File(projectDirectoryFile, mainFileName); //TODO: well, this forces projectDirectory to have File.
            
//            System.err.println("mainFile = " + mainFile );
//            System.err.println("mainFile = " + mainFile.exists() );
            if (!mainFile.exists())
                mainFile = new File(mainFileName);
            
            mainFile = mainFile.getCanonicalFile();
//            System.err.println("mainFile = " + mainFile );
//            System.err.println("mainFile = " + mainFile.exists() );
//            System.err.println("mainFile = " + FileUtil.toFileObject(mainFile) );
            return FileUtil.toFileObject(mainFile);
        } catch (IOException e) {
            return null;
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    //ignore?
                }
            }
        }
    }
    
    public Project loadProject(FileObject projectDirectory, ProjectState state) throws IOException {
        if (!isProject(projectDirectory))
            return null;
        
        FileObject master = getMasterFile(projectDirectory);
        LaTeXGUIProject p = new LaTeXGUIProject(projectDirectory, master);
        
        LaTeXGUIProjectFactorySourceFactory.get().projectLoad(p, master);

        return p;
//        throw new IOException("Xxxx");
    }
    
    public void saveProject(Project project) throws IOException, ClassCastException {
//        throw new ClassCastException("xxx");
//        System.err.println("saveProject called, project=" + project);
    }
    
}
