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
package org.netbeans.modules.latex.guiproject.ui;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;

import java.util.Map;

import java.util.WeakHashMap;


import org.netbeans.modules.latex.guiproject.EditableProperties;

import org.netbeans.modules.latex.guiproject.LaTeXGUIProject;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;



import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public class ProjectSettings {
    
    private String latexCommand;
    private String sourceSpecialsCommand;
    private boolean useSourceSpecials;
    private String[] arguments;
    
    private boolean modified;
    
    private LaTeXGUIProject project;
    
    /** Creates a new instance of ProjectSettings */
    private ProjectSettings(LaTeXGUIProject project) {
        this.project = project;
        load();
    }
    
    private void loadFrom(InputStream ins) throws IOException {
        EditableProperties p = new EditableProperties();
        
        p.load(ins);
        
        latexCommand = p.getProperty("latex-command");
        
        if (latexCommand == null)
            latexCommand = "latex";
        
        useSourceSpecials = Boolean.valueOf(p.getProperty("latex-use-source-specials")).booleanValue();
        sourceSpecialsCommand = p.getProperty("latex-source-specials-argument");
        
        if (sourceSpecialsCommand == null)
            sourceSpecialsCommand = "";
        
        String argumentsString = p.getProperty("latex-arguments");
        
        if (argumentsString != null)
            arguments = argumentsString.split(" ");
        else
            arguments = new String[0];
    }
    
    private void load() {
        FileObject settings = project.getProjectDirectory().getFileObject("build-settings.properties");
        
        try {
            loadFrom(settings.getInputStream());
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
        private void save() {
        FileObject settings = project.getProjectDirectory().getFileObject("build-settings.properties");
        FileLock lock = null;
        
        try {
            EditableProperties p = new EditableProperties();
            
            p.load(settings.getInputStream());
            
            p.setProperty("latex-command", latexCommand);
            p.setProperty("latex-use-source-specials", Boolean.toString(useSourceSpecials));
            p.setProperty("latex-source-specials-argument", sourceSpecialsCommand);
            
            StringBuffer argumentsString = new StringBuffer();
            
            for (int cntr = 0; cntr < arguments.length; cntr++) {
                argumentsString.append(arguments[cntr]);
                argumentsString.append(' ');
            }
            p.setProperty("latex-arguments", argumentsString.toString());
            
            lock = settings.lock();
            
            p.store(settings.getOutputStream(lock));
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        } finally {
            lock.releaseLock();
        }
    }
    
    private static Map/*<LaTeXGUIProject, ProjectSettings>*/ project2Settings = new WeakHashMap();
    
    public static synchronized ProjectSettings getDefault(LaTeXGUIProject p) {
        ProjectSettings s = (ProjectSettings) project2Settings.get(p);
        
        if (s == null) {
            s = new ProjectSettings(p);
            project2Settings.put(p, s);
        }
        
        return s;
    }
    
    public String getLatexCommand() {
        return latexCommand;
    }
    
    public void setLatexCommand(String latexCommand) {
        this.latexCommand = latexCommand;
        this.modified = true;
    }
    
    public String getSourceSpecialsCommand() {
        return sourceSpecialsCommand;
    }
    
    public void setSourceSpecialsCommand(String sourceSpecialsCommand) {
        this.sourceSpecialsCommand = sourceSpecialsCommand;
        this.modified = true;
    }
    
    public boolean isUseSourceSpecials() {
        return useSourceSpecials;
    }
    
    public void setUseSourceSpecials(boolean useSourceSpecials) {
        this.useSourceSpecials = useSourceSpecials;
        this.modified = true;
    }
    
    public String[] getArguments() {
        return arguments;
    }
    
    public void setArguments(String[] arguments) {
        this.arguments = arguments;
        this.modified = true;
    }
    
    public boolean isModified() {
        return modified;
    }
    
    public void commit() {
        if (!isModified())
            return ;
        
        save();
    }
    
    public void rollBack() {
        load();
    }
}
