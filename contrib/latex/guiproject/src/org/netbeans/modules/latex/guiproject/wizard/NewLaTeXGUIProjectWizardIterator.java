/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject.wizard;

import java.io.File;
import java.io.IOException;

import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeListener;

import org.netbeans.api.project.ProjectManager;

import org.netbeans.modules.latex.guiproject.CreateNewLaTeXProject;

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
        Thread.dumpStack();
    }

    public void addChangeListener(ChangeListener l) {
    }

    public org.openide.WizardDescriptor.Panel current() {
                Thread.dumpStack();

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

    public Set instantiate() throws IOException {
        System.err.println("Instantiate!");
        
        File metadataDir = new File((String) wizard.getProperty(DataNames.METADATA_DIR), (String) wizard.getProperty(DataNames.METADATA_NAME));
        File mainFile = new File((String) wizard.getProperty(DataNames.MAIN_FILE));
        FileObject metadataDirFO = CreateNewLaTeXProject.getDefault().createProject(metadataDir, mainFile);
        FileObject mainFileFO = FileUtil.toFileObject(mainFile);
        FileObject documentTexSource = Repository.getDefault().getDefaultFileSystem().findResource("latex/guiproject/document.tex");
        
        FileLock lock = null;
        
        try {
            lock = mainFileFO.lock();
            FileUtil.copy(documentTexSource.getInputStream(), mainFileFO.getOutputStream(lock));
        } finally {
            if (lock != null)
                lock.releaseLock();
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
