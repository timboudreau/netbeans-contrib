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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.vcs.profiles.pvcs.commands;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;

import org.netbeans.modules.vcscore.cmdline.exec.ExternalCommand;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
import org.netbeans.modules.vcscore.commands.OutputProgressListener;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;

/**
 * The efficient executor of PCLI commands.
 * It runs a script that performs serial execution of PCLI commands without
 * a necessity to start one JVM per each command.
 *
 * @author  Martin Entlicher
 */
public class PCLICommandExecutor implements Runnable {
    
    private static final String PVCS_FOLDER = "PVCS";
    private static final String PCLI_SCRIPT_NAME = "PCLIRunScript";
    private static final String PCLI_CMD_SUCCEEDED = PCLI_SCRIPT_NAME+" Command succeeded";
    private static final String PCLI_CMD_FAILED = PCLI_SCRIPT_NAME+" Command failed";
    private static final String PCLI_SCRIPT =
        "While Test 1=1\n"+
        "{\n"+
        //"  echo -n \"PCLI\"\n"+
        "  readline -vCMD\n"+
        "  run -ns $CMD\n"+
        "  set -vEXIT_CODE $?\n"+
        "  if test $EXIT_CODE=0\n"+
        "  {\n"+
        "    echo "+PCLI_CMD_SUCCEEDED+"\n"+
        "  }\n"+
        "  else\n"+
        "  {\n"+
        "    echo "+PCLI_CMD_FAILED+"\n"+
        "  }\n"+
        "}";
    private static final String[] PCLI_EXEC = { "pcli", "run", "-s" };
    private static final String PROMPT = ">";
    
    private static PCLICommandExecutor instance;
    
    private File pcliScript;
    private RequestProcessor.Task pcliTask;
    private Object processLock = new Object();
    private StructuredExec sexec;
    private ExternalCommand cmd;
    private Thread[] pcliThread = new Thread[] { null };
    private volatile PCLICommand commandToProcess;
    private volatile Boolean commandFinished;
    /** This is set to true after we have ">" prompt. */
    private boolean[] canSendInput = new boolean[] { false };
    private volatile boolean destroyed = false; // When the executor is destroyed and will not execute any more commands.
    
    /** Creates a new instance of PCLICommandExecutor */
    private PCLICommandExecutor() {
        initScript();
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }
    
    public static synchronized PCLICommandExecutor getDefault() {
        if (instance == null) {
            instance = new PCLICommandExecutor();
        }
        return instance;
    }
    
    private void initScript() {
        FileSystem dfs = org.openide.filesystems.Repository.getDefault().getDefaultFileSystem();
        FileObject vcsfo = dfs.findResource("vcs");
        FileObject pvcsfo = vcsfo.getFileObject(PVCS_FOLDER);
        if (pvcsfo == null) {
            try {
                pvcsfo = vcsfo.createFolder(PVCS_FOLDER);
            } catch (IOException ioex) {
                ErrorManager.getDefault().notify(ioex);
                return ;
            }
        }
        FileObject pclifo = pvcsfo.getFileObject(PCLI_SCRIPT_NAME);
        if (pclifo == null) {
            try {
                pclifo = pvcsfo.createData(PCLI_SCRIPT_NAME);
            } catch (IOException ioex) {
                ErrorManager.getDefault().notify(ioex);
                return ;
            }
            OutputStream out = null;
            FileLock lock = null;
            try {
                lock = pclifo.lock();
                out = pclifo.getOutputStream(lock);
                out.write(PCLI_SCRIPT.getBytes());
            } catch (IOException ioex) {
                if (lock != null) {
                    lock.releaseLock();
                    lock = null;
                }
                try {
                    pclifo.delete();
                } catch (IOException ioex2) {}
                return ;
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ioex) {}
                }
                if (lock != null) {
                    lock.releaseLock();
                }
            }
        }
        pcliScript = FileUtil.toFile(pclifo);
        PCLI_EXEC[2] += pcliScript.getAbsolutePath();
        sexec = new StructuredExec(null, PCLI_EXEC[0],
                                   new StructuredExec.Argument[] {
                                       new StructuredExec.Argument(PCLI_EXEC[1], false),
                                       new StructuredExec.Argument(PCLI_EXEC[2], false)
                                   });
    }
    
    public synchronized boolean runCommand(PCLICommand commandToProcess) throws InterruptedException {
        if (destroyed) {
            commandToProcess.setFailed();
            return false;
        }
        if (!isPCLIProcessLoopRunning()) {
            startPCLIProcessLoop();
        }
        synchronized (canSendInput) {
            if (!canSendInput[0]) {
                canSendInput.wait();
                if (!canSendInput[0]) {
                    // Still can not send input => something had to go wrong
                    stopPCLIProcessLoop(); // for sure
                    commandToProcess.setFailed();
                    return commandToProcess.succeeded();
                }
                try {
                    // wait a while for all the previous output to finish.
                    Thread.currentThread().sleep(100);
                } catch (InterruptedException iex) {
                    stopPCLIProcessLoop();
                    throw iex;
                }
            }
        }
        this.commandToProcess = commandToProcess;
        this.commandFinished = null;
        String cmdStr = commandToProcess.getStrExec();
        cmd.sendInput(cmdStr+"\n");
        try {
            commandToProcess.waitFinished();
        } catch (InterruptedException iex) {
            stopPCLIProcessLoop();
            throw iex;
        } finally {
            this.commandToProcess = null;
        }
        return commandToProcess.succeeded();
    }
    
    private void startPCLIProcessLoop() {
        //pcliTask = RequestProcessor.getDefault().post(this);
        synchronized (canSendInput) {
            canSendInput[0] = false;
        }
        runScript();
    }
    
    private void stopPCLIProcessLoop() throws InterruptedException {
        if (pcliThread[0] != null) {
            synchronized (canSendInput) {
                if (!canSendInput[0]) {
                    try {
                        canSendInput.wait(5000);
                    } catch (InterruptedException iex) {}
                }
                if (canSendInput[0]) {
                    cmd.sendInput("exit\n");
                }
            }
        }
        for (int i = 0; i < 10 && isPCLIProcessLoopRunning(); i++) {
            Thread.currentThread().sleep(100); // Give it some time (in case of standard exit).
            if (!isPCLIProcessLoopRunning()) break;
        }
        if (isPCLIProcessLoopRunning()) {
            kill();
        }
    }
    
    private void kill() {
        synchronized (pcliThread) {
            if (pcliThread[0] != null) {
                pcliThread[0].interrupt();
            }
        }
        // The interrupt might take some time...
        try {
            for (int i = 0; i < 50 && isPCLIProcessLoopRunning(); i++) {
                Thread.currentThread().sleep(10);
                if (!isPCLIProcessLoopRunning()) break;
            }
        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
        }
        if (commandToProcess != null) {
            commandToProcess.setFailed();
        }
    }
    
    private boolean isPCLIProcessLoopRunning() {
        return pcliTask != null && !pcliTask.isFinished();
    }
    
    private void runScript() {
        cmd = new ExternalCommand(sexec);
        PCLIStandardOutputListener stdOutListener = new PCLIStandardOutputListener();
        cmd.addImmediateTextOutputListener(stdOutListener);
        cmd.addImmediateTextErrorListener(new PCLIErrorOutputListener());
        cmd.addOutputProgressListener(stdOutListener);
        pcliTask = RequestProcessor.getDefault().post(this);
    }
    
    public void run() {
        synchronized (pcliThread) {
            pcliThread[0] = Thread.currentThread();
        }
        int exitStatus = VcsCommandExecutor.FAILED;
        try {
            exitStatus = cmd.exec();
        } finally {
            synchronized (pcliThread) {
                pcliThread[0] = null;
            }
            cmd = null;
            if (exitStatus != VcsCommandExecutor.SUCCEEDED) {
                synchronized (canSendInput) {
                    canSendInput[0] = false;
                    canSendInput.notify();
                }
            }
            if (commandToProcess != null && !commandToProcess.isFinished()) {
                commandToProcess.setFailed();
            }
        }
    }
    
    public void sendInput(String text) {
        if (cmd != null) {
            cmd.sendInput(text);
        }
    }
    
    private class PCLIStandardOutputListener implements TextOutputListener, OutputProgressListener {
        
        private String lastLine;
        
        // Gets immediate output
        public void outputLine(String text) {
            //System.out.println("PCLIStandardOutputListener.outputLine("+text+"), commandToProcess = "+commandToProcess);
            String[] lines = buildLines(text);
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (PCLI_CMD_SUCCEEDED.equals(line)) {
                    synchronized (canSendInput) {
                        canSendInput[0] = false;
                    }
                    text = removeLine(text, line);
                    commandFinished = Boolean.TRUE;
                } else if (PCLI_CMD_FAILED.equals(line)) {
                    synchronized (canSendInput) {
                        canSendInput[0] = false;
                    }
                    text = removeLine(text, line);
                    commandFinished = Boolean.FALSE;
                } else {
                    if (commandToProcess != null) {
                        commandToProcess.stdOutput(line);
                    }
                }
            }
            synchronized (canSendInput) {
                if (!canSendInput[0] && PROMPT.equals(lastLine)) {
                    lastLine = null;
                    canSendInput[0] = true;
                    text = text.substring(0, text.length() - PROMPT.length());
                    canSendInput.notify();
                }
            }
            if (commandToProcess != null) {
                commandToProcess.immediateStdOutput(text);
            }
        }
        
        public void outputFlushed() {
            if (commandFinished != null && commandToProcess != null) {
                if (commandFinished.booleanValue()) {
                    commandToProcess.setSucceeded();
                } else {
                    commandToProcess.setFailed();
                }
            }
        }
        
        private List linesBuffer = new ArrayList();
        
        private String[] buildLines(String text) {
            if (lastLine != null) {
                text = lastLine + text;
                lastLine = null;
            }
            String lastBufferedLine = null;
            for (int pos = 0; pos < text.length(); ) {
                int newline = text.indexOf('\n', pos);
                if (newline >= 0) {
                    String line = text.substring(pos, newline);
                    if (line.endsWith("\r")) line = line.substring(0, line.length() - 1);
                    if (line.startsWith("\r")) line = line.substring(1);
                    linesBuffer.add(line);
                    lastBufferedLine = line;
                    pos = newline + 1;
                } else {
                    lastLine = text.substring(pos);
                    break;
                }
            }
            if (lastBufferedLine != null) {
                if (lastBufferedLine.endsWith(PCLI_CMD_SUCCEEDED)) {
                    String line = lastBufferedLine.substring(0, lastBufferedLine.length() - PCLI_CMD_SUCCEEDED.length());
                    if (line.length() > 0) {
                        linesBuffer.remove(linesBuffer.size() - 1);
                        linesBuffer.add(line);
                        linesBuffer.add(PCLI_CMD_SUCCEEDED);
                    }
                }
                if (lastBufferedLine.endsWith(PCLI_CMD_FAILED)) {
                    String line = lastBufferedLine.substring(0, lastBufferedLine.length() - PCLI_CMD_FAILED.length());
                    if (line.length() > 0) {
                        linesBuffer.remove(linesBuffer.size() - 1);
                        linesBuffer.add(line);
                        linesBuffer.add(PCLI_CMD_FAILED);
                    }
                }
            }
            String[] lines = (String[]) linesBuffer.toArray(new String[0]);
            linesBuffer.clear();
            return lines;
        }
        
        private String removeLine(String text, String line) {
            int i = text.indexOf(line);
            if (i >= 0) {
                int j = i + line.length();
                if (text.length() > j + 1 && text.charAt(j) == '\n') j++;
                text = text.substring(0, i) + text.substring(j);
            }
            return text;
        }
        
    }
    
    private class PCLIErrorOutputListener implements TextOutputListener {
        
        private String lastLine;
        
        // Gets immediate output
        public void outputLine(String text) {
            //System.out.println("PCLIErrorOutputListener.outputLine("+text+"), commandToProcess = "+commandToProcess);
            if (commandToProcess == null) return ; // Throw the output if no one listens.
            String[] lines = buildLines(text);
            for (int i = 0; i < lines.length; i++) {
                commandToProcess.errOutput(lines[i]);
            }
            commandToProcess.immediateErrOutput(text);
        }
        
        private List linesBuffer = new ArrayList();
        
        private String[] buildLines(String text) {
            if (lastLine != null) {
                text = lastLine + text;
                lastLine = null;
            }
            for (int pos = 0; pos < text.length(); ) {
                int newline = text.indexOf('\n', pos);
                if (newline >= 0) {
                    String line = text.substring(pos, newline);
                    if (line.endsWith("\r")) line = line.substring(0, line.length() - 1);
                    if (line.startsWith("\r")) line = line.substring(1);
                    linesBuffer.add(line);
                    pos = newline + 1;
                } else {
                    lastLine = text.substring(pos);
                    break;
                }
            }
            String[] lines = (String[]) linesBuffer.toArray(new String[0]);
            linesBuffer.clear();
            return lines;
        }
        
    }
    
    /**
     * Shuts down the PCLI script.
     */
    private static class ShutdownHook extends Thread {
        
        public void run() {
            PCLICommandExecutor.getDefault().destroyed = true;// No more commands will be executed.
            try {
                PCLICommandExecutor.getDefault().stopPCLIProcessLoop();
            } catch (InterruptedException iex) {
                PCLICommandExecutor.getDefault().kill();
            }
        }
        
    }
    
}
