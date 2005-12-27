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

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import java.io.OutputStream;
import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;

/**
 *
 * @author Jan Lahoda
 */
/*package private*/ class LaTeXPumpStreamHandler extends PumpStreamHandler {

    private LaTeXCopyMaker output;
    private File baseDir;
    private Project project;
    
    /** Creates a new instance of LaTeXPumpStream */
    public LaTeXPumpStreamHandler(Project project, File baseDir) {
        this.project = project;
        this.baseDir = baseDir;
    }
    
    /**
     * Install a handler for the output stream of the subprocess.
     *
     * @param is input stream to read from the error stream from the subprocess
     */
    public void setProcessOutputStream(InputStream is) {
        try {
        PipedOutputStream out = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(out);
        
        output = new LaTeXCopyMaker(project, baseDir, is, out);
        
        super.setProcessOutputStream(in);
        } catch (IOException e) {
            e.printStackTrace();
            super.setProcessOutputStream(is);
        }
    }

    public void start() {
        output.start();
        super.start();
    }
    
    public void stop() {
        try {
            output.join();
        } catch (InterruptedException e) {
            //...ignored...
        }
        
        super.stop();
    }
}
