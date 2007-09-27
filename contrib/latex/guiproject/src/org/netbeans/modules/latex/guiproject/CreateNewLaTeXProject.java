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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
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
import java.io.OutputStream;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;

import org.openide.filesystems.FileObject;


import org.openide.filesystems.FileUtil;

import org.openide.filesystems.Repository;

/**
 *
 * @author Jan Lahoda
 */
public class CreateNewLaTeXProject {

    private boolean debug = Boolean.getBoolean("netbeans.latex.guiproject.create.new.project");
    
    /** Creates a new instance of CreateNewLaTeXProject */
    private CreateNewLaTeXProject() {
    }
    
    private static CreateNewLaTeXProject instance = null;
    
    public static synchronized CreateNewLaTeXProject getDefault() {
        if (instance == null) {
            instance = new CreateNewLaTeXProject();
        }
        
        return instance;
    }
    
    public FileObject createProject(File metadataDir, File mainFile) throws IOException {
        if (debug) {
            System.err.println("createProject(" + metadataDir + ", " + mainFile + ")");
            System.err.println("mainFile.exists()=" + mainFile.exists());
        }
        
        if (!mainFile.exists()) {
            File parent = mainFile.getParentFile();
            
            parent.mkdirs();
            
            /*refresh:*/
            
            FileObject toRefresh = null;
            
            if (debug)
                System.err.println("parent = " + parent );
            
            while (parent != null && !parent.equals(parent.getParentFile()) && (toRefresh = FileUtil.toFileObject(parent)) == null) {
                if (debug) {
                    System.err.println("parent = " + parent );
                    System.err.println("FileUtil.toFileObject(parent)=" + FileUtil.toFileObject(parent));
                }
                
                parent = parent.getParentFile();
                
                if (debug) {
                    System.err.println("parent = " + parent );
                    System.err.println("FileUtil.toFileObject(parent)=" + FileUtil.toFileObject(parent));
                }
            }
            
            if (toRefresh != null) {
                if (debug) {
                    System.err.println("refreshing:" + toRefresh);
                    System.out.println("refreshing:" + toRefresh);
                }
                toRefresh.refresh();
            }
            
            System.err.flush();
            
            mainFile.createNewFile();
            FileUtil.toFileObject(parent).refresh();
            
            FileObject mainFileFO = FileUtil.toFileObject(mainFile);
            
            if (mainFileFO == null)
                throw new IOException("Created mainfile not found on filesystems!");
            
//           mainFileFO.getParent().refresh();
        }
        
        metadataDir.mkdirs();
        
        FileUtil.toFileObject(metadataDir.getParentFile()).refresh();
        
        FileObject metadataDirFO = FileUtil.toFileObject(metadataDir);
        
        FileObject buildSettings = Repository.getDefault().getDefaultFileSystem().findResource("latex/guiproject/build-settings.properties");
        
        FileObject targetBuildSettings = FileUtil.copyFile(buildSettings, metadataDirFO, "build-settings", "properties");
        
        EditableProperties ep = new EditableProperties();
        
        FileLock lock = null;
        
        OutputStream out = null;
        InputStream  ins = null;
        
        try {
            ins = targetBuildSettings.getInputStream();
            ep.load(ins);
            ins.close();
            ep.setProperty("mainfile", Utilities.findShortestName(metadataDir, mainFile));
            lock = targetBuildSettings.lock();
            out = targetBuildSettings.getOutputStream(lock);
            ep.store(out);
        } finally {
            if (lock != null)
                lock.releaseLock();
            
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
            
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        }
        
        return metadataDirFO;
    }
}
