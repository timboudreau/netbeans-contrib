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

import java.awt.Dialog;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.WeakHashMap;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.latex.guiproject.EditableProperties;
import org.netbeans.modules.latex.guiproject.LaTeXGUIProject;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;


/**
 *
 * @author Jan Lahoda
 */
public class ProjectSettings implements FileChangeListener {
    
    private String   latexCommand;
    private String   sourceSpecialsCommand;
    private boolean  useSourceSpecials;
    private String[] latexArguments;
    
    private String   bibtexCommand;
    private String[] bibtexArguments;
    
    private String   defaultBuildCommand;
    private String   defaultShowCommand;
    
    private boolean  modified;
    
    private LaTeXGUIProject project;
    
    /** Creates a new instance of ProjectSettings */
    /*package private, for tests!*/ ProjectSettings(LaTeXGUIProject project, /*only for tests, for other purposes default to true:*/boolean listenOnFileChanges) {
        this.project = project;
        
        if (listenOnFileChanges) {
            //install listener to the settings file, so we are notified on changes:
            FileObject settingsFile = getSettingsFile();
            
            settingsFile.addFileChangeListener(FileUtil.weakFileChangeListener(this, settingsFile));
        }
        
        load();
    }
    
    private String[] parseArguments(String arguments) {
        if (arguments != null)
            return arguments.split(" ");
        else
            return new String[0];
    }
    
    private String getProperty(EditableProperties p, String name, String def) {
        String value = p.getProperty(name);
        
        if (value == null)
            return def;
        else
            return value;
    }
    
    private void loadFrom(InputStream ins) throws IOException {
        EditableProperties p = new EditableProperties();
        
        p.load(ins);
        
        latexCommand = getProperty(p, "latex-command", "latex");
        useSourceSpecials = Boolean.valueOf(getProperty(p, "latex-use-source-specials", "false")).booleanValue();
        sourceSpecialsCommand = getProperty(p, "latex-source-specials-argument", "");
        latexArguments = parseArguments(p.getProperty("latex-arguments"));
        
        bibtexCommand = getProperty(p, "bibtex-command", "bibtex");
        bibtexArguments = parseArguments(p.getProperty("bibtex-arguments"));
        
        defaultBuildCommand = getProperty(p, "default-build-target", "build");
        defaultShowCommand  = getProperty(p, "default-show-target", "show");
    }
    
    private FileObject getSettingsFile() {
        return project.getProjectDirectory().getFileObject("build-settings.properties");
    }
    
    private synchronized void load() {
        FileObject settings = getSettingsFile();
        
        InputStream ins = null;
        try {
            ins = settings.getInputStream();
            loadFrom(ins);
            modified = false;
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
        }
    }
    
    private String toPlainString(String[] arguments) {
        StringBuffer argumentsString = new StringBuffer();
        
        for (int cntr = 0; cntr < arguments.length; cntr++) {
            argumentsString.append(arguments[cntr]);
            argumentsString.append(' ');
        }
        
        return argumentsString.toString();
    }
    
    private boolean writing = false;
    
    private synchronized void save() {
        FileObject settings = getSettingsFile();
        FileLock lock = null;
        
        InputStream ins = null;
        OutputStream outs = null;
        try {
            writing = true;
            
            EditableProperties p = new EditableProperties();
            
            ins = settings.getInputStream();
            
            p.load(ins);
            
            ins.close();
            
            ins = null;
            
            p.setProperty("latex-command", latexCommand);
            p.setProperty("latex-use-source-specials", Boolean.toString(useSourceSpecials));
            p.setProperty("latex-source-specials-argument", sourceSpecialsCommand);
            p.setProperty("latex-arguments", toPlainString(latexArguments));
            
            p.setProperty("bibtex-command", bibtexCommand);
            p.setProperty("bibtex-arguments", toPlainString(bibtexArguments));
            
            p.setProperty("default-build-target", getDefaultBuildCommand());
            p.setProperty("default-show-target", getDefaultShowCommand());
            
            lock = settings.lock();
            
            outs = settings.getOutputStream(lock);
            
            p.store(outs);
            
            outs.close();
            
            outs = null;
            
            modified = false;
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
            if (outs != null) {
                try {
                    outs.close();
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                }
            }
            
            if (lock != null)
                lock.releaseLock();
            
            writing = false;
        }
    }
    
    private static Map/*<LaTeXGUIProject, ProjectSettings>*/ project2Settings = new WeakHashMap();
    
    public static synchronized ProjectSettings getDefault(LaTeXGUIProject p) {
        ProjectSettings s = (ProjectSettings) project2Settings.get(p);
        
        if (s == null) {
            s = new ProjectSettings(p, true);
            project2Settings.put(p, s);
        }
        
        return s;
    }
    
    public String getLatexCommand() {
        return latexCommand;
    }
    
    public synchronized void setLatexCommand(String latexCommand) {
        this.latexCommand = latexCommand;
        this.modified = true;
    }
    
    public String getBiBTeXCommand() {
        return bibtexCommand;
    }
    
    public synchronized void setBiBTeXCommand(String bibtexCommand) {
        this.bibtexCommand = bibtexCommand;
        this.modified = true;
    }
    
    public String getSourceSpecialsCommand() {
        return sourceSpecialsCommand;
    }
    
    public synchronized void setSourceSpecialsCommand(String sourceSpecialsCommand) {
        this.sourceSpecialsCommand = sourceSpecialsCommand;
        this.modified = true;
    }
    
    public boolean isUseSourceSpecials() {
        return useSourceSpecials;
    }
    
    public synchronized void setUseSourceSpecials(boolean useSourceSpecials) {
        this.useSourceSpecials = useSourceSpecials;
        this.modified = true;
    }
    
    public String[] getLaTeXArguments() {
        return latexArguments;
    }
    
    public synchronized void setLaTeXArguments(String[] arguments) {
        this.latexArguments = arguments;
        this.modified = true;
    }
    
    public String[] getBiBTeXArguments() {
        return bibtexArguments;
    }
    
    public synchronized void setBiBTeXArguments(String[] arguments) {
        this.bibtexArguments = arguments;
        this.modified = true;
    }
    
    public synchronized boolean isModified() {
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

    public String getDefaultBuildCommand() {
        return defaultBuildCommand;
    }

    public synchronized void setDefaultBuildCommand(String defaultBuildCommand) {
        this.defaultBuildCommand = defaultBuildCommand;
        modified = true;
    }

    public String getDefaultShowCommand() {
        return defaultShowCommand;
    }

    public synchronized void setDefaultShowCommand(String defaultShowCommand) {
        this.defaultShowCommand = defaultShowCommand;
        modified = true;
    }

    public void fileRenamed(FileRenameEvent fe) {
        //don't know how to react on this
    }

    public void fileAttributeChanged(FileAttributeEvent fe) {
        //ignored
    }

    public void fileFolderCreated(FileEvent fe) {
        //ignored
    }

    public void fileDeleted(FileEvent fe) {
        //don't know how to react on this
    }

    public void fileDataCreated(FileEvent fe) {
        //should not happen?
    }

    public synchronized void fileChanged(FileEvent fe) {
        System.err.println("fileChanged(" + fe + ")");
        
        if (writing)
            return ;
        
        if (!isModified()) {
            load();
            return ;
        }
        
        DialogDescriptor dd = new DialogDescriptor("The settings for LaTeX project: " + ProjectUtils.getInformation(project).getDisplayName() + " has been changed on disk and are also changed in memory. Drop the in-memory changes?", "External modification");
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
        
        dialog.show();
        
        if (dd.getValue() == DialogDescriptor.OK_OPTION) {
            load();
        }
    }
}
