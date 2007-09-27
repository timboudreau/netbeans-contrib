/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ant.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.types.Commandline;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXAntTask extends Task implements TaskContainer {
    
    private String  mainFile  = null;
    private String  command   = "latex";
    private String  arguments = "";
    private boolean printLog = false;
    private boolean useSpecials = true;
    private String  specialsCommand = "-src-specials";
    
    private List innerTasks;
    
    /** Creates a new instance of LaTeXAntTask */
    public LaTeXAntTask() {
        innerTasks = new ArrayList();
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
    
    public void setPrintlog(boolean log) {
        printLog = log;
    }
    
    public void setUsespecials(boolean useSpecials) {
        this.useSpecials = useSpecials;
    }
    
    public void setSpecialsCommand(String specialsCommand) {
        if (specialsCommand == null || specialsCommand.length() == 0)
            return ;

        this.specialsCommand = specialsCommand;
    }
    
    private boolean isUpToDate() {
        File source = new File(mainFile);
        
        if (    !Utilities.isUpToDate(source, ".dvi")
             || !Utilities.isUpToDate(source, ".aux"))
            return false;
        
        File bbl = Utilities.replaceExtension(source, ".bbl");
        
        if (bbl.exists()) {
            if (!Utilities.isUpToDate(bbl, ".dvi"))
                return false;
        }
        
        return true;
    }
    
    public void execute() throws BuildException {
        if (mainFile == null)
            throw new BuildException("The mainfile has to be set!");
        
        log("LaTeX Ant Task impl version: 2", Project.MSG_VERBOSE);
        
        boolean forceReparse = false;
        
        for (int pass = 0; pass < 3; pass++) {
            boolean doLatex = true;
            
            if (isUpToDate()) {
                if (pass == 0) {
                    //The first pass is assured always, but there is no point in latexing...
                    log("Up-to-date mainfile, first pass, included files not checked, latexing forced.", Project.MSG_VERBOSE);
                    doLatex = true;
                } else {
                    if (forceReparse) {
                        log("Up-to-date, forceReparse == true.", Project.MSG_VERBOSE);
                        doLatex = true;
                    } else {
                        log("Up-to-date, no latexing, exit", Project.MSG_VERBOSE);
                        break;
                    }
                }
            }
            
            if (doLatex) {
                log("LaTeXing, mainfile:" + mainFile, Project.MSG_VERBOSE);
                
                File absoluteMainFile = Utilities.resolveFile(getProject(), mainFile);
                
                if (!absoluteMainFile.exists())
                    throw new BuildException("Mainfile does not exist!");
                
                forceReparse = false;
                
                Commandline cmdLine = new Commandline();
                
                cmdLine.setExecutable(command);
                cmdLine.addArguments(Commandline.translateCommandline(arguments));
                
                if (useSpecials) {
                    cmdLine.addArguments(new String[] {specialsCommand});
                }
                
                cmdLine.addArguments(new String[] {new File(mainFile).getName()});
                
                File baseDir = absoluteMainFile.getParentFile();
                
                Execute exec = new Execute(new org.netbeans.modules.latex.ant.tasks.LaTeXPumpStreamHandler(getProject(), baseDir));
                
                exec.setCommandline(cmdLine.getCommandline());
                exec.setWorkingDirectory(baseDir);
                
                try {
                    if (exec.execute() != 0)
                        throw new BuildException("LaTeXing the source did not succeeded. More information should be provided above.", getLocation());
                } catch (IOException e) {
                    throw new BuildException(e);
                }
            }
            
            if (pass == 0) {
                log("Running inner tasks.", Project.MSG_VERBOSE);
                
                for (Iterator i = innerTasks.iterator(); i.hasNext(); ) {
                    ((Task) i.next()).perform();
                }
            }
            
            if (pass == 1) //TODO: maybe not necessary, need to parse it from the output...
                forceReparse = true;
        }
    }
    
    public void addTask(Task task) {
        innerTasks.add(task);
    }    
    
}
