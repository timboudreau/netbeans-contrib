/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
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
import org.netbeans.modules.vcscore.commands.TextOutputListener;

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
    /** This is set to true after we have ">" prompt. */
    private boolean[] canSendInput = new boolean[] { false };
    
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
        if (!isPCLIProcessLoopRunning()) {
            startPCLIProcessLoop();
        }
        synchronized (canSendInput) {
            if (!canSendInput[0]) {
                canSendInput.wait();
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
    
    private void stopPCLIProcessLoop() {
        if (pcliThread != null) {
            synchronized (canSendInput) {
                if (!canSendInput[0]) {
                    try {
                        canSendInput.wait();
                    } catch (InterruptedException iex) {}
                }
            }
            cmd.sendInput("exit\n");
        }
        pcliTask.waitFinished();
    }
    
    private void kill() {
        synchronized (pcliThread) {
            if (pcliThread[0] != null) {
                pcliThread[0].interrupt();
            }
        }
    }
    
    private boolean isPCLIProcessLoopRunning() {
        return pcliTask != null && !pcliTask.isFinished();
    }
    
    private void runScript() {
        cmd = new ExternalCommand(sexec);
        cmd.addImmediateTextOutputListener(new PCLIStandardOutputListener());
        cmd.addImmediateTextErrorListener(new PCLIErrorOutputListener());
        pcliTask = RequestProcessor.getDefault().post(this);
    }
    
    public void run() {
        synchronized (pcliThread) {
            pcliThread[0] = Thread.currentThread();
        }
        try {
            cmd.exec();
        } finally {
            synchronized (pcliThread) {
                pcliThread[0] = null;
            }
        }
    }
    
    public void sendInput(String text) {
        if (cmd != null) {
            cmd.sendInput(text);
        }
    }
    
    private class PCLIStandardOutputListener implements TextOutputListener {
        
        private String lastLine;
        
        // Gets immediate output
        public void outputLine(String text) {
            //System.out.println("PCLIStandardOutputListener.outputLine("+text+"), commandToProcess = "+commandToProcess);
            String[] lines = buildLines(text);
            Boolean finished = null;
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (PCLI_CMD_SUCCEEDED.equals(line)) {
                    synchronized (canSendInput) {
                        canSendInput[0] = false;
                    }
                    text = removeLine(text, line);
                    finished = Boolean.TRUE;
                } else if (PCLI_CMD_FAILED.equals(line)) {
                    synchronized (canSendInput) {
                        canSendInput[0] = false;
                    }
                    text = removeLine(text, line);
                    finished = Boolean.FALSE;
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
                if (finished != null) {
                    if (finished.booleanValue()) {
                        commandToProcess.setSucceeded();
                    } else {
                        commandToProcess.setFailed();
                    }
                }
            }
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
            Thread stopThread = new Thread() {
                public void run() {
                    PCLICommandExecutor.getDefault().stopPCLIProcessLoop();
                }
            };
            stopThread.start();
            try {
                stopThread.join(5000);
            } catch (InterruptedException iex) {}
            if (stopThread.isAlive()) {
                PCLICommandExecutor.getDefault().kill();
            }
        }
        
    }
    
}
