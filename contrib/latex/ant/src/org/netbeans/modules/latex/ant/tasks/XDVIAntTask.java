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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ant.tasks;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

/**
 *
 * @author Jan Lahoda
 */
public class XDVIAntTask extends Task {

    private String mainFile  = null;
    private String dviFile   = null;
    private String command   = "xdvi";
    private String arguments = "";
    
    private String fileName  = null;
    private int    line      = (-1);
    private int    column    = (-1);
    
    private String editorFormat = null;
    
    /** Creates a new instance of LaTeXAntTask */
    public XDVIAntTask() {
        mainFile = null;
        dviFile  = null;
    }
    
    public void setMainfile(String mainFile) {
        if (dviFile != null)
            throw new IllegalStateException("Cannot set mainfile, the dvifile has been already set!");
        
        this.mainFile = mainFile;
    }

    public void setDvifile(String dviFile) {
        if (mainFile != null)
            throw new IllegalStateException("Cannot set dvifile, the mainfile has been already set!");
        
        this.dviFile = dviFile;
    }

    public void setCommand(String command) {
        this.command = command;
    }
    
    public void setArguments(String arguments) {
        this.arguments = arguments;
    }
    
    public void setFilename(String fileName) {
        if (fileName == null || fileName.length() == 0)
            return ;
        this.fileName = fileName;
    }
    
    public void setLine(String line) throws NumberFormatException {
        if (line == null || line.length() == 0)
            return ;
        
        this.line = Integer.parseInt(line);
    }
    
    public void setColumn(String column) throws NumberFormatException {
        if (column == null || column.length() == 0)
            return ;
        
        this.column = Integer.parseInt(column);
    }
    
    public void setEditorformat(String format) {
        if (format == null || format.length() == 0)
            return ;
        
        this.editorFormat = format;
    }

    public void execute() throws BuildException {
        log("XDVIAntTask.execute: mainFile= " + mainFile + ", dviFile=" + dviFile, Project.MSG_DEBUG);
        
        if (mainFile == null && dviFile == null)
            throw new BuildException("Exactly one of mainfile or dvifile has to be set!");

        Commandline cmdLine = new Commandline();
        
        cmdLine.setExecutable(command);
        
        if (dviFile == null) {
            String baseName;
            
            if (mainFile.endsWith(".tex"))
                baseName = mainFile.substring(0, mainFile.length() - 4);
            else
                baseName = mainFile;
            
            dviFile = baseName + ".dvi";
            
            File absoluteDVIFile = Utilities.resolveFile(getProject(), dviFile);
            
            if (!absoluteDVIFile.exists())
                throw new BuildException("Mainfile does not exist!");

            dviFile = absoluteDVIFile.getAbsolutePath();
        }
        
        log("XDVIAntTask.execute: computed dviFile=" + dviFile, Project.MSG_DEBUG);
        
        cmdLine.addArguments(Commandline.translateCommandline(arguments));
        
        if (fileName != null) {
            String positionArgument = "";
            
            if (column != (-1)) {
                positionArgument = String.valueOf(line) + ":" + String.valueOf(column) + " " + fileName;
            } else {
                positionArgument = String.valueOf(line) + " " + fileName;
            }
            cmdLine.addArguments(new String[] {"-sourceposition", positionArgument});
        }
        
        if (editorFormat != null) {
            cmdLine.addArguments(new String[] {"-editor", editorFormat});
        }
        
        cmdLine.addArguments(new String[] {dviFile});
        
        Execute exec = new Execute();
        
        exec.setCommandline(cmdLine.getCommandline());
        exec.setWorkingDirectory(new File(dviFile).getParentFile());
        exec.setAntRun(getProject());
        
        try {
            exec./*spawn();//*/execute();
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

}
