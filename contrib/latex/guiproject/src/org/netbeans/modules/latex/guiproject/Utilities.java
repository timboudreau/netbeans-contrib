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
package org.netbeans.modules.latex.guiproject;

import java.io.File;
import org.netbeans.api.project.Project;
import org.netbeans.modules.latex.guiproject.build.BuildConfigurationProvider;
import org.netbeans.modules.latex.model.platform.LaTeXPlatform;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author Jan Lahoda
 */
public final class Utilities {
    
    /** Creates a new instance of Utilities */
    private Utilities() {
    }
    
    private static boolean isParent(File dir, File file) {
        if (dir == null)
            return false;
        
        if (file == null)
            return false;
        
        if (dir.equals(file.getParentFile()))
            return true;
        
        return isParent(dir, file.getParentFile());
    }
    
    /**Computes "shortest" relative path from baseFile to fileInQuestion.
     * 
     * @param baseFile base directory to find relative file name (if baseFile is a file,
     *                 its parent is used).
     * @param fileInQuestion file from which the relative file name should be found
     * @return A string such that new File(baseFile, findShortestName(baseFile, fileInQuestion)).equals(fileInQuestion)
     *         This string should be as short as possible, but callers should not depend on a particular form.
     */
    public static String findShortestName(File baseFile, File fileInQuestion) {
        if (!baseFile.isDirectory())
            baseFile = baseFile.getParentFile();
        
        if (baseFile.equals(fileInQuestion.getParentFile()))
            return fileInQuestion.getName();
        
        if (isParent(baseFile, fileInQuestion)) {
            //fileInQuestion is deeper in the directory structure:
            StringBuffer result = new StringBuffer();
            
            result.append(fileInQuestion.getName());
            
            fileInQuestion = fileInQuestion.getParentFile();
            
            while (!baseFile.equals(fileInQuestion)) {
                result.insert(0, '/');
                result.insert(0, fileInQuestion.getName());
                fileInQuestion = fileInQuestion.getParentFile();
            }
            
            return result.toString();
        }
        
        if (isParent(fileInQuestion.getParentFile(), baseFile)) {
            //fileInQuestion is higher in the directory structure:
            StringBuffer result = new StringBuffer();
            
            while (!baseFile.equals(fileInQuestion.getParentFile())) {
                result.append("..");
                result.append('/');
                baseFile = baseFile.getParentFile();
            }
            
            result.append(fileInQuestion.getName());
            
            return result.toString();
        }
        
        return FileUtil.normalizeFile(fileInQuestion).getAbsolutePath();
    }
    
    public static LaTeXPlatform getPlatform(Project p) {
        //TODO: verify that only LaTeXGUIProjects work in this method:
        return (LaTeXPlatform) Lookup.getDefault().lookup(LaTeXPlatform.class);
    }
    
    public static BuildConfigurationProvider getBuildConfigurationProvider(Project p) {
        //TODO: verify that only LaTeXGUIProjects work in this method:
        return (BuildConfigurationProvider) p.getLookup().lookup(BuildConfigurationProvider.class);
    }

}
