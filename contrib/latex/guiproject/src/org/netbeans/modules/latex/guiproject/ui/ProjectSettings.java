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
    private ProjectSettings(LaTeXGUIProject project) {
        this.project = project;
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
        
        defaultBuildCommand = getProperty(p, "default-build-command", "build");
        defaultShowCommand  = getProperty(p, "default-show-command", "show");
    }
    
    private void load() {
        FileObject settings = project.getProjectDirectory().getFileObject("build-settings.properties");
        
        InputStream ins = null;
        try {
            ins = settings.getInputStream();
            loadFrom(ins);
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
    
    private void save() {
        FileObject settings = project.getProjectDirectory().getFileObject("build-settings.properties");
        FileLock lock = null;
        
        InputStream ins = null;
        OutputStream outs = null;
        try {
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
            
            p.setProperty("default-build-command", getDefaultBuildCommand());
            p.setProperty("default-show-command", getDefaultShowCommand());
            
            lock = settings.lock();
            
            outs = settings.getOutputStream(lock);
            
            p.store(outs);
            
            outs.close();
            
            outs = null;
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
    
    public String getBiBTeXCommand() {
        return bibtexCommand;
    }
    
    public void setBiBTeXCommand(String bibtexCommand) {
        this.bibtexCommand = bibtexCommand;
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
    
    public String[] getLaTeXArguments() {
        return latexArguments;
    }
    
    public void setLaTeXArguments(String[] arguments) {
        this.latexArguments = latexArguments;
        this.modified = true;
    }
    
    public String[] getBiBTeXArguments() {
        return bibtexArguments;
    }
    
    public void setBiBTeXArguments(String[] arguments) {
        this.bibtexArguments = bibtexArguments;
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

    public String getDefaultBuildCommand() {
        return defaultBuildCommand;
    }

    public void setDefaultBuildCommand(String defaultBuildCommand) {
        this.defaultBuildCommand = defaultBuildCommand;
    }

    public String getDefaultShowCommand() {
        return defaultShowCommand;
    }

    public void setDefaultShowCommand(String defaultShowCommand) {
        this.defaultShowCommand = defaultShowCommand;
    }
}
