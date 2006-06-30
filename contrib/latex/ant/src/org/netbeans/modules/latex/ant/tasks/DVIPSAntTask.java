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
public class DVIPSAntTask extends Task {

    private String mainFile  = null;
    private String command   = "dvips";
    private String arguments = "";

    /** Creates a new instance of LaTeXAntTask */
    public DVIPSAntTask() {
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

    public void execute() throws BuildException {
        log("DVIPSAntTask.execute start", Project.MSG_DEBUG);
        
        if (mainFile == null)
            throw new BuildException("The mainfile has to be set!");

        log("DVIPSAntTask: mainFile=" + mainFile, Project.MSG_DEBUG);
        log("DVIPSAntTask: command="  + command,  Project.MSG_DEBUG);
        
        Commandline cmdLine = new Commandline();
        
        cmdLine.setExecutable(command);
        
        File absoluteFile = Utilities.resolveFile(getProject(), mainFile).getAbsoluteFile();
        
        log("DVIPSAntTask: absoluteFile=" + absoluteFile.toString(),  Project.MSG_DEBUG);
        
        File dviFile = Utilities.replaceExtension(absoluteFile, ".dvi");
        File psFile  = Utilities.replaceExtension(absoluteFile, ".ps" );
        
        log("DVIPSAntTask: dviFile=" + dviFile.toString(), Project.MSG_DEBUG);
        log("DVIPSAntTask: psFile="  + psFile.toString(),  Project.MSG_DEBUG);
//        if (psFile.lastModified() >= dviFile.lastModified()) {
//            
//        }
        
        cmdLine.addArguments(Commandline.translateCommandline(arguments));
        cmdLine.addArguments(new String[] {dviFile.getAbsolutePath() , "-o", psFile.getAbsolutePath()});
        
        Execute exec = new Execute();
        
        exec.setCommandline(cmdLine.getCommandline());
        exec.setWorkingDirectory(absoluteFile.getParentFile());
        
        try {
            exec.execute();
        } catch (IOException e) {
            throw new BuildException(e);
        }
        log("DVIPSAntTask.execute done", Project.MSG_DEBUG);
    }
    
}
