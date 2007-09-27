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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
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
package org.netbeans.modules.latex.guiproject.wizard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeListener;

import org.netbeans.api.project.ProjectManager;

import org.netbeans.modules.latex.guiproject.CreateNewLaTeXProject;
import org.openide.ErrorManager;

import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.InstantiatingIterator;
import org.openide.WizardDescriptor.Panel;

import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;

/**
 *
 * @author Jan Lahoda
 */
public class NewLaTeXGUIProjectWizardIterator implements org.openide.WizardDescriptor.InstantiatingIterator {
    
    private org.openide.WizardDescriptor.Panel[] panels;
    private int current;
    private WizardDescriptor wizard;
    
    /** Creates a new instance of NewLaTeXGUIProjectWizardIterator */
    public NewLaTeXGUIProjectWizardIterator() {
//        Thread.dumpStack();
    }

    public void addChangeListener(ChangeListener l) {
    }

    public org.openide.WizardDescriptor.Panel current() {
//        Thread.dumpStack();

        return panels[current];
    }

    public boolean hasNext() {
        return current + 1 < panels.length;
    }

    public boolean hasPrevious() {
        return current > 0;
    }

    public void initialize(WizardDescriptor wizard) {
        current = 0;
        panels = constructPanels(wizard);
        this.wizard = wizard;
    }

    static String constructRealMainFileName(String proposedMainFileName) {
        if (proposedMainFileName.endsWith(".tex") || proposedMainFileName.endsWith(".latex"))
            return proposedMainFileName;
        
        return proposedMainFileName + ".tex";
    }
    
    public Set instantiate() throws IOException {
//        System.err.println("Instantiate!");
        
        File metadataDir = new File((String) wizard.getProperty(DataNames.METADATA_DIR), (String) wizard.getProperty(DataNames.METADATA_NAME));
        File mainFile = new File(constructRealMainFileName((String) wizard.getProperty(DataNames.MAIN_FILE)));
        FileObject metadataDirFO = CreateNewLaTeXProject.getDefault().createProject(metadataDir, mainFile);
        FileObject mainFileFO = FileUtil.toFileObject(mainFile);
        FileObject documentTexSource = Repository.getDefault().getDefaultFileSystem().findResource("latex/guiproject/document.tex");
        
        if (mainFileFO.getSize() == 0) {
            FileLock     lock = null;
            InputStream  ins  = null;
            OutputStream outs = null;
            
            try {
                lock = mainFileFO.lock();
                ins  = documentTexSource.getInputStream();
                outs = mainFileFO.getOutputStream(lock);
                FileUtil.copy(ins, outs);
            } finally {
                if (lock != null)
                    lock.releaseLock();
                
                if (ins != null) {
                    try {
                        ins.close();
                    } catch (IOException e) {
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                    }
                }
                
                if (outs != null) {
                    try {
                        outs.close();
                    } catch (IOException e) {
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                    }
                }
            }
        }
        
        Set results = new HashSet();
        
        results.add(mainFileFO);
                
        ProjectManager.getDefault().findProject(metadataDirFO);
        
        results.add(metadataDirFO);
        
        return results;
    }

    public String name() {
        return "New LaTeX Project";
    }

    public void nextPanel() {
        assert current + 1 < panels.length;
        
        current++;
    }

    public void previousPanel() {
        assert current > 0;
        
        current--;
    }

    public void removeChangeListener(ChangeListener l) {
    }

    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }
    
    private org.openide.WizardDescriptor.Panel[] constructPanels(WizardDescriptor wizard) {
        return new Panel[] {new NewLaTeXProjectTargetPanel(wizard)};
    }
    
}
