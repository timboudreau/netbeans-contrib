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
package org.netbeans.modules.latex.ant.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

/**
 *
 * @author Jan Lahoda
 */
public class BiBTeXAntTask extends Task {
    
    private String mainFile;
    private String command   = "bibtex";
    private String arguments = "";
    
    /** Creates a new instance of LaTeXAntTask */
    public BiBTeXAntTask() {
        mainFile = null;
    }
    
    public void setMainfile(String mainFile) {
        this.mainFile = mainFile;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    private boolean isUpToDate(File source) {
        File result = Utilities.replaceExtension(source, ".bbl");
        
        if (!Utilities.isUpToDate(source, result))
            return false;
        
        File aux = Utilities.replaceExtension(source, ".aux");
        
        if (!aux.exists())
            return false;
        
        try {
            InputStream ins = new BufferedInputStream(new FileInputStream(aux));
            StringBuffer content = new StringBuffer();
            int read;
            
            while ((read = ins.read()) != (-1)) {
                content.append((char) read);
            }
            
            String contentString = content.toString();
            
            Pattern p = Pattern.compile("\\\\bibdata\\{([^}]*)\\}");
            Matcher m = p.matcher(contentString);
            
            while (m.find()) {
                String database = m.group(1);
                File   databaseFile = new File(source.getParentFile(), database);
                
                if (!databaseFile.exists())
                    databaseFile = new File(source.getParentFile(), database + ".bib");
                
                if (databaseFile.exists()) {
                    if (!Utilities.isUpToDate(databaseFile, result))
                        return false;
                }
            }
        } catch (IOException e) {
            log("BiBTeX: During checking for up-to-date, IOException=" + e.getLocalizedMessage(), Project.MSG_VERBOSE);
            return false;
        }
        
        return true;
    }
    
    public void execute() throws BuildException {
        if (mainFile == null)
            throw new BuildException("The mainfile has to be set!");
        
        File source = Utilities.resolveFile(getProject(), mainFile);

        log("BiBTeX: " + source, Project.MSG_VERBOSE);
        
        if (isUpToDate(source)) {
            log("BiBTeX: Up-to-date, no processing.", Project.MSG_VERBOSE);
        } else {
            Commandline cmdLine = new Commandline();
            
            cmdLine.setExecutable(command);
            cmdLine.addArguments(Commandline.translateCommandline(arguments));
            
            File aux = Utilities.replaceExtension(source, ".aux");
            
            cmdLine.addArguments(new String[] {aux.getName()});
            
            Execute exec = new Execute();
            
            exec.setCommandline(cmdLine.getCommandline());
            exec.setWorkingDirectory(aux.getParentFile());
            
            try {
                exec.execute();
            } catch (IOException e) {
                throw new BuildException(e);
            }
        }
    }
    
}
