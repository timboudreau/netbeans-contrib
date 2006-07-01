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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.linetools.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.openide.ErrorManager;

/**
 *
 * @author Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class FilterProcess {
    
    private String[] filterCommand;
    private int expectedNumberOfOutputLines;
    
    private Process filterProcess;
    
    private PrintWriter printWriter;
    
    private List filterProcessStdOut;
    private List filterProcessStdErr;
    
    public FilterProcess(String[] filterCommand) {
        this(filterCommand, 100);
    }
    
    public FilterProcess(String[] filterCommand, int expectedNumberOfOutputLines) {
        this.filterCommand = filterCommand;
        this.expectedNumberOfOutputLines = expectedNumberOfOutputLines;
    }
    
    public PrintWriter exec() throws IOException {
        // Run the filter process
        filterProcess = Runtime.getRuntime().exec(filterCommand);
        
        // Setup STDOUT Reading
        filterProcessStdOut = new ArrayList();
        Thread filterProcessStdOutReader = new Thread(
                new InputStreamReaderThread(filterProcess.getInputStream(),
                    filterProcessStdOut),
                    filterCommand[0] + ":STDOUT Reader"); // NOI18N
        filterProcessStdOutReader.start();
        
        // Setup STDERR Reading
        filterProcessStdErr = new ArrayList(expectedNumberOfOutputLines);
        Thread filterProcessStdErrReader = new Thread(
                new InputStreamReaderThread(filterProcess.getErrorStream(),
                    filterProcessStdErr),
                    filterCommand[0] + ":STDERR Reader"); // NOI18N
        filterProcessStdErrReader.start();
        
        printWriter = new PrintWriter(filterProcess.getOutputStream());
        
        return printWriter;
    }
       
    public int waitFor() {
        if (filterProcess != null) {
            int exitStatus;
            try {
                return filterProcess.waitFor();
            } catch (InterruptedException ex) {
                ErrorManager.getDefault().notify(ErrorManager.USER, ex);
            }
        }
        return -1;
    }
    
    public String[] getStdOutOutput() {
        if (filterProcessStdOut != null) {
            return (String[]) filterProcessStdOut.toArray(new String[0]);
        }
        return null;
    }
    
    public String[] getStdErrOutput() {
        if (filterProcessStdErr != null) {
            return (String[]) filterProcessStdErr.toArray(new String[0]);
        }
        return null;
    }
    
    public void destroy() {
        if (filterProcess != null) {
            filterProcess.destroy();
            filterProcess = null;
            filterProcessStdOut = null;
            filterProcessStdErr = null;
        }
    }
    
    static class InputStreamReaderThread implements Runnable {
        private InputStream is;
        private List output;
        
        InputStreamReaderThread(InputStream is, List output) {
            this.is = is;
            this.output = output;
        }
        
        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line=null;
                while ((line = br.readLine()) != null) {
                    output.add(line);
                }
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ioe);
            }
        }
    }
}
