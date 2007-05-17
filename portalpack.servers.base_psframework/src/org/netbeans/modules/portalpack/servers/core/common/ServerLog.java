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

package org.netbeans.modules.portalpack.servers.core.common;


import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.ErrorManager;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;


/**
 * Server log reads from the standard and error output and 
 * writes to output window.
 */ 
class ServerLog extends Thread {
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private InputOutput io;
    private OutputWriter writer;
    private OutputWriter errorWriter;
    private BufferedReader inReader;
    private BufferedReader errReader;
    private final boolean autoFlush;
    private final boolean takeFocus;
    private volatile boolean done = false;
    private LogSupport logSupport;
    private String displayName;

    /**
     *  server log reads from the standard and error output and 
     * writes to output window
     * @param displayName output window display name.
     * @param in  standard output reader.
     * @param err  error output reader.
     * @param autoFlush should we flush after a change?
     * @param takeFocus should be the output window made visible after each
     *        changed?
     */
    public ServerLog(String url, String displayName, Reader in, Reader err, boolean autoFlush,
            boolean takeFocus,LogSupport logSupport) {
        super("PS ServerLog - Thread"); // NOI18N
        setDaemon(true);
        inReader = new BufferedReader(in);
        errReader = new BufferedReader(err);
        this.autoFlush = autoFlush;
        this.takeFocus = takeFocus;
        this.displayName = displayName;
        io = UISupport.getServerIO(url);
        try {
            io.getOut().reset();
        } 
        catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        writer = io.getOut();
        errorWriter = io.getErr();
        io.select();
        this.logSupport = logSupport;
    }

    private void processLine(String line) {
        LogSupport.LineInfo lineInfo = logSupport.analyzeLine(line);
        if (lineInfo.isError()) {
            if (lineInfo.isAccessible()) {
                try {
                    errorWriter.println(line, logSupport.getLink(lineInfo.message() , lineInfo.path(), lineInfo.line()));
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            } else {
                errorWriter.println(line);
            }
        } else {
            writer.println(line);
        }
    }

    public void run() {
        try {
            while(!done) {                    
                boolean isInReaderReady = false;
                boolean isErrReaderReady = false;
                boolean updated = false;
                int count = 0;
                // take a nap after 1024 read cycles, this should ensure responsiveness
                // even if log file is growing fast
                while (((isInReaderReady = inReader.ready()) || (isErrReaderReady = errReader.ready())) 
                        && count++ < 1024) {
                    if (done) return;
                    updated = true;
                    if (isInReaderReady) {
                        String line = inReader.readLine();
                        // finish, if we have reached the end of the stream
                        if (line == null) return;
                        processLine(line);
                    }
                    if (isErrReaderReady) {
                        String line = errReader.readLine();
                        // finish, if we have reached the end of the stream
                        if (line == null) return;
                        processLine(line);
                    }
                }
                if (updated) {
                    if (autoFlush) {
                        writer.flush();
                        errorWriter.flush();
                    }
                    if (takeFocus) {
                        io.select();
                    }
                }
                sleep(100); // take a nap
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            logSupport.detachAnnotation();
        }
        
        logger.log(Level.INFO,"Logger stopped ..");
    }
    
    /**
     * Test whether ServerLog thread is still running.
     *
     * @return <code>true</code> if the thread is still running, <code>false</code>
     *         otherwise.
     */
    public boolean isRunning() {
        return !(done);
    }
    
    /**
     * Make the log tab visible.
     */
    public void takeFocus() {
        io.select();
    }

    public void interrupt() {
        super.interrupt();
        done = true;
    }
    
    public boolean isDone()
    {
        return done;
    }
    
    public org.netbeans.modules.portalpack.servers.core.common.LogSupport getLogSupport() {
        return logSupport;
    }
}