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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2006.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject.ui;

import java.awt.Dialog;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.latex.guiproject.EditableProperties;
import org.netbeans.modules.latex.guiproject.LaTeXGUIProject;
import org.netbeans.modules.latex.guiproject.build.RunTypes;
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
    
    private String   buildConfigurationName;
    private String   showConfigurationName;
    private RunTypes bibTeXRunType;
    private Locale   locale;

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

        buildConfigurationName = getProperty(p, "build-configuration-name", "latexdvips");
        showConfigurationName = getProperty(p, "show-configuration-name", "gv");

        try {
            bibTeXRunType = RunTypes.valueOf(getProperty(p, "bibtex-run-type", RunTypes.AUTO.name()).trim());
        } catch (IllegalArgumentException e) {
            bibTeXRunType = RunTypes.AUTO;
        }
        
        try {
            String localeString = getProperty(p, "locale", "en");
            
            locale = new Locale(localeString);
        } catch (IllegalArgumentException e) {
            bibTeXRunType = RunTypes.AUTO;
        }
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
            
            p.setProperty("build-configuration-name", buildConfigurationName);
            p.setProperty("show-configuration-name", showConfigurationName);
            p.setProperty("bibtex-run-type", bibTeXRunType.name());
            p.setProperty("locale", locale.toString());
            
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

    public String getBuildConfigurationName() {
        return buildConfigurationName;
    }

    public synchronized void setBuildConfigurationName(String buildConfigurationName) {
        this.buildConfigurationName = buildConfigurationName;
        this.modified = true;
    }
    
    public String getShowConfigurationName() {
        return showConfigurationName;
    }

    public synchronized void setShowConfigurationName(String showConfigurationName) {
        this.showConfigurationName = showConfigurationName;
        this.modified = true;
    }

    public RunTypes getBiBTeXRunType() {
        return bibTeXRunType;
    }

    public void setBiBTeXRunType(RunTypes bibTeXRunType) {
        this.bibTeXRunType = bibTeXRunType;
        this.modified = true;
    }
    
    public Locale getLocale() {
        return locale;
    }
    
    public void setLocale(Locale locale) {
        this.locale = locale;
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
