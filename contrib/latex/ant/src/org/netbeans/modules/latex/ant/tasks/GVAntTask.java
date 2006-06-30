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
public class GVAntTask extends Task {

    private String mainFile  = null;
    private String psFile   = null;
    private String command   = "gv";
    private String arguments = "";
    
    private String fileName  = null;
    private int    line      = (-1);
    private int    column    = (-1);
    
    private String editorFormat = null;
    
    /** Creates a new instance of LaTeXAntTask */
    public GVAntTask() {
        mainFile = null;
        psFile  = null;
    }
    
    public void setMainfile(String mainFile) {
        if (psFile != null)
            throw new IllegalStateException("Cannot set mainfile, the dvifile has been already set!");
        
        this.mainFile = mainFile;
    }

    public void setPSfile(String psFile) {
        if (mainFile != null)
            throw new IllegalStateException("Cannot set dvifile, the mainfile has been already set!");
        
        this.psFile = psFile;
    }
    
    public void setPDFfile(String pdfFile) {
        if (mainFile != null)
            throw new IllegalStateException("Cannot set dvifile, the mainfile has been already set!");
        
        this.psFile = pdfFile;
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
        log("GVAntTask.execute start", Project.MSG_DEBUG);
        
        if (mainFile == null && psFile == null)
            throw new BuildException("Exactly one of mainfile or dvifile has to be set!");

        log("GVAntTask.execute mainFile=" + mainFile, Project.MSG_DEBUG);
        log("GVAntTask.execute psFile="   + psFile,   Project.MSG_DEBUG);
        log("GVAntTask.execute command="  + command,  Project.MSG_DEBUG);

        Commandline cmdLine = new Commandline();
        
        cmdLine.setExecutable(command);
        
        if (psFile == null) {
            File absoluteFile = Utilities.resolveFile(getProject(), mainFile);
            log("GVAntTask.execute absoluteFile="  + absoluteFile.toString(),  Project.MSG_DEBUG);
            
            File psFile  = Utilities.replaceExtension(absoluteFile, ".ps");
            File pdfFile = Utilities.replaceExtension(absoluteFile, ".pdf");
            boolean psFileExists = psFile.exists();
            boolean pdfFileExists = pdfFile.exists();
            
            log("GVAntTask.execute psFile="                + psFile.toString(),      Project.MSG_DEBUG);
            log("GVAntTask.execute pdfFile="               + pdfFile.toString(),     Project.MSG_DEBUG);
            log("GVAntTask.execute psFileExists="          + psFileExists,           Project.MSG_DEBUG);
            log("GVAntTask.execute pdfFileExists="         + pdfFileExists,          Project.MSG_DEBUG);
            log("GVAntTask.execute psFile.lastModified()=" + psFile.lastModified(),  Project.MSG_DEBUG);
            log("GVAntTask.execute pdfFile.lastModified()="+ pdfFile.lastModified(), Project.MSG_DEBUG);
            
            if (!psFileExists && !pdfFileExists)
                throw new BuildException("No PS or PDF file corresponding to the main file " + mainFile + " has been found.");

            if (psFileExists && pdfFileExists) {
                if (psFile.lastModified() > pdfFile.lastModified()) {
                    this.psFile = psFile.getAbsolutePath();
                } else {
                    this.psFile = pdfFile.getAbsolutePath();
                }
            } else {
                if (psFileExists) {
                    this.psFile = psFile.getAbsolutePath();
                } else {
                    this.psFile = pdfFile.getAbsolutePath();
                }
            }
            
            log("GVAntTask.execute this.psFile=" + this.psFile,  Project.MSG_DEBUG);
        }
        
        cmdLine.addArguments(Commandline.translateCommandline(arguments));
        
        //Not valid for GV (leaving here for the possibility that we will be able to manage it in the future):
//        if (fileName != null) {
//            String positionArgument = "";
//            
//            if (column != (-1)) {
//                positionArgument = String.valueOf(line) + ":" + String.valueOf(column) + " " + fileName;
//            } else {
//                positionArgument = String.valueOf(line) + " " + fileName;
//            }
//            cmdLine.addArguments(new String[] {"-sourceposition", positionArgument});
//        }
//        
//        if (editorFormat != null) {
//            cmdLine.addArguments(new String[] {"-editor", editorFormat});
//        }
        
        cmdLine.addArguments(new String[] {psFile});
        
        Execute exec = new Execute();
        
        exec.setCommandline(cmdLine.getCommandline());
        exec.setWorkingDirectory(new File(psFile).getParentFile());
        exec.setAntRun(getProject());
        
        try {
            exec.spawn();//execute();
        } catch (IOException e) {
            throw new BuildException(e);
        }
        log("GVAntTask.execute end",  Project.MSG_DEBUG);
    }

}
