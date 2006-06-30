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
