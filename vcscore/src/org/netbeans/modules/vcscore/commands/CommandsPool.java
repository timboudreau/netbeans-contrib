/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.*;

import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.filesystems.RepositoryListener;
import org.openide.filesystems.RepositoryEvent;
import org.openide.filesystems.RepositoryReorderedEvent;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListener;
//import org.openide.nodes.AbstractNode;
//import org.openide.nodes.Children;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.FileReaderListener;
import org.netbeans.modules.vcscore.runtime.*;
import org.netbeans.modules.vcscore.cache.FileSystemCache;
import org.netbeans.modules.vcscore.cache.CacheHandler;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.TopComponentCloseListener;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * This class is used as a container of all external commands which are either running or finished.
 * @author  Martin Entlicher
 */
public class CommandsPool extends Object /*implements CommandListener */{

    /**
     * The preprocessing of the command was cancelled. The command will not be executed.
     */
    public static final int PREPROCESS_CANCELLED = 0;
    
    /**
     * When there are more files selected, the preprocessing needs to be done for
     * next files again. The command will run on the first file, preprocessing will be
     * done for the rest.
     */
    public static final int PREPROCESS_NEXT_FILE = 1;
    
    /**
     * The preprocessing is done. When more files are selected, the command
     * will not be preprocessed for the rest of them.
     */
    public static final int PREPROCESS_DONE = 2;
    
    /** The maximum number of running commands in the system. This prevents overwhelming
     * the system with too many commands running concurrently */
    private static final int MAX_NUM_RUNNING_COMMANDS = 10;
    /** The maximum number of running refresh commands in the system. This prevents
     * overwhelming the system with too many refresh commands with the ability
     * to still run other commands concurrently */
    private static final int MAX_NUM_RUNNING_LISTS = 7;
    
    private static long lastCommandID = 0;
    
    private static CommandsPool instance = null;
    
    /** The table of instances of VcsCommandExecutor and associated VcsCommandWrapper */
    private Hashtable commandsWrappers;
    /** Contains instances of VcsCommandWrappers, which are to be run.  */
    private ArrayList commandsToRun;
    /** Contains instances of VcsCommandWrappers, which are waiting to run. */
    private ArrayList commandsWaitQueue;
    /** Contains instances of VcsCommandWrappers, which are running. */
    private ArrayList commandsRunning;
    /** Contains instances of VcsCommandWrappers, which are running as an exception.
     *  they are executed to prevent deadlock. The deadlock can occure if there
     *  were executed the maximum number of commands and they need to run some
     *  sub-commands. These subcommands can not be executed without an introduction
     *  of these exceptional commands. */
    private ArrayList commandsExceptionallyRunning;
    /** The containers of output of commands. Contains pairs of instances of VcsCommandExecutor
     * and instances of CommandOutputCollector */
    private Hashtable outputContainers;
    /** The table of currently opened standard Visualizers */
    private Hashtable outputVisualizers;
    /** Contains finished instances of VcsCommandWrapper. */
    private ArrayList commandsFinished;
    /** Contains filesystems, whose commands will be released only upon a special request. */
    private ArrayList fsWithCmdsRemovedOnRequest;
    private int numRunningListCommands;
    
    //private ThreadGroup group;
    private CommandsThreadsPool threadsPool;
    
    /** Whether to collect the whole output of commands. */
    private boolean collectOutput = true;
    /** Whether to collect error output of commands. */
    private boolean collectErrOutput = true;
    
    private ArrayList commandListeners = new ArrayList();
    
    private boolean execStarterLoopStarted = false;
    private boolean execStarterLoopRunning = true;

    /** Creates new CommandsPool */
    private CommandsPool() {
        commandsToRun = new ArrayList();
        commandsRunning = new ArrayList();
        commandsExceptionallyRunning = new ArrayList();
        commandsFinished = new ArrayList();
        commandsWaitQueue = new ArrayList();
        commandsWrappers = new Hashtable();
        outputContainers = new Hashtable();
        outputVisualizers = new Hashtable();
        fsWithCmdsRemovedOnRequest = new ArrayList();
        numRunningListCommands = 0;
        threadsPool = new CommandsThreadsPool();
        //group = new ThreadGroup("VCS Commands Group");
        //executorStarterLoop();
    }
    
    public static synchronized CommandsPool getInstance() {
        if (instance == null) {
            instance = new CommandsPool();
        }
        return instance;
    }
    
    protected void finalize () {
        cleanup();
    }
    
    /**
     * Destroy the FS node under VCS Commands node on the Runtime tab.
     * This also stops the execution starter loop.
     * You will not be able to execute any command by CommandsPool after this method finishes !
     */
    public void cleanup() {
        synchronized (this) {
            //* The FS may still exist i.e. inside a MultiFileSystem => do not interrupt the loop now
            execStarterLoopRunning = false;
            notifyAll();
            // */
        }
    }
    
    public void setCollectOutput(boolean collectOutput) {
        this.collectOutput = collectOutput;
    }
    
    public boolean isCollectOutput() {
        return collectOutput;
    }

    /**
     * Get the command's ID. It's a unique command identification number.
     * @param vce the command's executor
     * @return the ID or -1 if the command does not have one.
     */
    public long getCommandID(VcsCommandExecutor vce) {
        VcsCommandWrapper cw = (VcsCommandWrapper) commandsWrappers.get(vce);
        if (cw != null) return cw.getCommandID();
        else return -1;
    }

    /**
     * Perform preprocessing of a new command. It will perform any needed input
     * and update the execution string.
     * @param vce the command to preprocess
     * @return the preprocessing status, one of <code>CommandExecutorSupport.PROPEROCESS_*</code> constants
     */
    public int preprocessCommand(VcsCommandExecutor vce, Hashtable vars,
                                 VcsFileSystem fileSystem) {
        return preprocessCommand(vce, vars, fileSystem, null);
    }
    
    /**
     * Perform preprocessing of a new command. It will perform any needed input
     * and update the execution string.
     * @param vce the command to preprocess
     * @param vars the variables for this command
     * @param fileSystem the file system this command is associated with. Can be <code>null</code>
     *        if the command is not associated with any file system
     * @param askForEachFile whether the user should be asked for options for each file
     *        separately. If false, then the user is asked only once when the command acts
     *        on multiple files. Only the first item in the array is used, new value
     *        will be returned.
     * @return the preprocessing status, one of <code>CommandExecutorSupport.PREPROCESS_*</code> constants
     */
    public int preprocessCommand(VcsCommandExecutor vce, Hashtable vars,
                                 VcsFileSystem fileSystem, boolean[] askForEachFile) {
        //setCommandID(vce);
        VcsCommandWrapper cw = new VcsCommandWrapper(vce, fileSystem);
        commandsWrappers.put(vce, cw);
        synchronized (commandsToRun) {
            commandsToRun.add(cw);
        }
        VcsCommand cmd = vce.getCommand();
        String name = cmd.getDisplayName();
        if (name == null || name.length() == 0) name = cmd.getName();
        String exec = vce.getExec();
        RuntimeCommand rCom = null;
        if (fileSystem != null) {
            fileSystem.debug(g("MSG_Command_preprocessing", name, exec));
        }
        synchronized (commandListeners) {
            for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
                ((CommandListener) it.next()).commandPreprocessing(vce);
            }
        }
        int preprocessStatus = CommandExecutorSupport.preprocessCommand(fileSystem, vce, vars, askForEachFile);
        if (PREPROCESS_CANCELLED == preprocessStatus) {
            synchronized (this) {
                commandsToRun.remove(cw);
                commandsWrappers.remove(vce);
                //commandsFinished.add(vce);
                notifyAll();
            }
            if (fileSystem != null) {
                fileSystem.debug(g("MSG_Command_canceled", name));
            }
        }
        return preprocessStatus;
    }
    
    private void commandStarted(final VcsCommandWrapper cw) {
        final VcsCommandExecutor vce = cw.getExecutor();
        //setCommandID(vce);
        final VcsFileSystem fileSystem = cw.getFileSystem();
        //if (fileSystem == null) return ;
        VcsCommand cmd = vce.getCommand();
        //waitToRun(cmd, vce.getFiles());
        String name = cmd.getDisplayName();
        if (name == null || name.length() == 0) name = cmd.getName();
        final String finalName = name;
        RequestProcessor.postRequest(new Runnable() {
            public void run() {
                TopManager.getDefault().setStatusText(g("MSG_Command_name_running", finalName));
                if (fileSystem != null) {
                    fileSystem.debug(g("MSG_Command_started", finalName, vce.getExec()));
                }
            }
        });
        //System.out.println("command "+vce.getCommand()+" STARTED.");
        synchronized (commandListeners) {
            for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
                ((CommandListener) it.next()).commandStarted(vce);
            }
        }
        CommandOutputCollector collector = new CommandOutputCollector(vce, this);
        outputContainers.put(vce, collector);
        VcsCommandVisualizer visualizer = vce.getVisualizer();
        if (visualizer != null) {
            if (!visualizer.openAfterCommandFinish()) visualizer.open();
        }
        if (VcsCommandIO.getBooleanProperty(vce.getCommand(), VcsCommand.PROPERTY_DISPLAY_PLAIN_OUTPUT)) {
            openCommandOutput(vce);
            //CommandOutputVisualizer outputVisualizer = new CommandOutputVisualizer(vce);
            //outputVisualizer.open();
            //outputVisualizers.put(vce, outputVisualizer);
        }
        //System.out.println("command "+vce.getCommand()+" STARTED, LISTENERS DONE.");
        if (fileSystem != null) {
            FileSystemCache cache = CacheHandler.getInstance().getCache(fileSystem.getCacheIdStr());
            if (cache != null && cache instanceof FileReaderListener) {
                vce.addFileReaderListener((FileReaderListener) cache);
            }
        }
        cw.setStartTime(System.currentTimeMillis());
    }
    
    private void commandDone(VcsCommandWrapper cw) {
        cw.setFinishTime(System.currentTimeMillis());
        VcsCommandExecutor vce = cw.getExecutor();
        //System.out.println("commandDone("+cw.getExecutor().getCommand().getName()+")");
        VcsFileSystem fileSystem = cw.getFileSystem();
        VcsCommand cmd = vce.getCommand();
        String name = cmd.getDisplayName();
        if (name == null || name.length() == 0) name = cmd.getName();
        synchronized (this) {
            if (isListCommand(cmd)) numRunningListCommands--;
            commandsRunning.remove(cw);
            commandsFinished.add(cw);
            commandsExceptionallyRunning.remove(cw);
            notifyAll();
        }
        //System.out.println("  commandsFinished.size() = "+commandsFinished.size());
        if (!isCollectOutput()) {
            outputContainers.remove(vce);
        }
        synchronized (commandListeners) {
            for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
                ((CommandListener) it.next()).commandDone(vce);
            }
        }
        if (!fsWithCmdsRemovedOnRequest.contains(fileSystem)) {
            synchronized (this) {
                commandsFinished.remove(cw);
                commandsWrappers.remove(vce);
                outputContainers.remove(vce);
            }
        }
        if (fileSystem != null) {
            CommandExecutorSupport.postprocessCommand(fileSystem, vce);
        }
        //System.out.println("command "+vce.getCommand()+" DONE, LISTENERS DONE.");
        int exit = vce.getExitStatus();
        if (cw.isInterrupted()) exit = VcsCommandExecutor.INTERRUPTED;
        //String name = vce.getCommand().getDisplayName();
        String message = "";
        switch (exit) {
            case VcsCommandExecutor.SUCCEEDED:
                message = g("MSG_Command_name_finished", name);
                break;
            case VcsCommandExecutor.FAILED:
                if (VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_IGNORE_FAIL)) {
                    message = g("MSG_Command_name_finished", name);
                } else {
                    message = g("MSG_Command_name_failed", name);
                }
                break;
            case VcsCommandExecutor.INTERRUPTED:
                message = g("MSG_Command_name_interrupted", name);
                break;
        }
        final String finalMessage = message;
        RequestProcessor.postRequest(new Runnable() {
            public void run() {
                TopManager.getDefault().setStatusText(finalMessage);
            }
        });
        String notification = null;
        if (exit != VcsCommandExecutor.SUCCEEDED && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_IGNORE_FAIL)) {
            if (fileSystem != null) fileSystem.debugErr(message);
            printErrorOutput(vce, fileSystem);
            if (fileSystem == null || fileSystem.isCommandNotification()) {
                notification = (String) cmd.getProperty(VcsCommand.PROPERTY_NOTIFICATION_FAIL_MSG);
            }
        } else {
            if (fileSystem != null) fileSystem.debug(message);
            if (fileSystem == null || fileSystem.isCommandNotification()) {
                notification = (String) cmd.getProperty(VcsCommand.PROPERTY_NOTIFICATION_SUCCESS_MSG);
            }
        }
        if (notification != null) {
            CommandExecutorSupport.commandNotification(vce, notification, fileSystem);
        }
        VcsCommandVisualizer visualizer = vce.getVisualizer();
        if (visualizer != null) {
            visualizer.setExitStatus(exit);
            if (visualizer.openAfterCommandFinish()) visualizer.open();
        }
        VcsCommandVisualizer outputVisualizer = (VcsCommandVisualizer) outputVisualizers.get(vce);
        if (outputVisualizer != null) {
            outputVisualizer.setExitStatus(exit);
        }
    }
    
    /**
     * Do remove finished commands only when explicitly requested by {@link #removeFinishedCommand} method.
     * This is used e.g. in situations, when we need to open output of a command
     * after the command has finished.
     * @param removeUponRequest If true, removed finished commands only upon the request,
     *                          if false, remove immediately.
     * @param fs The filesystem, whose commands should (or should not) be removed only
     *           upon the request.
     */
    public void removeFinishedCommandsUponRequest(boolean removeUponRequest, VcsFileSystem fs) {
        if (removeUponRequest) fsWithCmdsRemovedOnRequest.add(fs);
        else fsWithCmdsRemovedOnRequest.remove(fs);
    }
    
    /**
     * Remove the finished executor from the internal database. After the command
     * is removed it's no longer possible to open it's output.
     * This method is used with {@link #removeFinishedCommandsUponRequest}.
     */
    public void removeFinishedCommand(VcsCommandExecutor removedExecutor) {
        VcsCommandWrapper removedWrapper = (VcsCommandWrapper) commandsWrappers.get(removedExecutor);
        CommandOutputCollector collector = null;
        synchronized (this) {
            if (removedWrapper == null) commandsFinished.remove(removedWrapper);
            //System.out.println("commandDone("+removedWrapper.getExecutor().getCommand().getName()+"): removing command "+removedExecutor.getCommand().getName());
            commandsWrappers.remove(removedExecutor);
            collector =  (CommandOutputCollector) outputContainers.remove(removedExecutor);
        }
        if (collector != null) {
            CommandOutputVisualizer visualizer = (CommandOutputVisualizer) outputVisualizers.get(removedExecutor.getCommand());
            if (visualizer == null) {
                collector.finalize();
            }
        }
        if (removedWrapper == null) return ;
        RuntimeCommand runCom = removedWrapper.getRuntimeCommand();
        removedWrapper.setRuntimeCommand(null);
        //VcsFileSystem fs = removedWrapper.getFileSystem();
        //if (runCom != null) rSupport.removeDone(fileSystem.getSystemName(), runCom);
    }
    
    private void printErrorOutput(VcsCommandExecutor vce, final VcsFileSystem fileSystem) {
        //final VcsFileSystem fileSystem = getVcsFileSystem();
        if (fileSystem == null) return ;
        fileSystem.debugErr(g("MSG_Check_whole_output"));
        CommandOutputCollector collector = (CommandOutputCollector) outputContainers.get(vce);
        //boolean isErrorOutput = false;
        if (collector != null) {
            collector.addErrorOutputListener(new CommandOutputListener() {
                public void outputLine(String line) {
                    //isErrorOutput = true;
                    fileSystem.debugErr(line);
                }
            });
        }
        /*
        if (!isErrorOutput) {
            fileSystem.debugErr(g("MSG_No_error_output"));
        }
         */
    }
    
    /**
     * Start the executor. The method starts the executor in a separate thread.
     * @param vce the executor
     */
    public synchronized void startExecutor(final VcsCommandExecutor vce) {
        startExecutor(vce, null);
    }
    
    /**
     * Start the executor. The method starts the executor in a separate thread.
     * @param vce the executor
     * @param fileSystem the file system associated with the command. Can be <code>null</code>.
     */
    public synchronized void startExecutor(final VcsCommandExecutor vce,
                                           final VcsFileSystem fileSystem) {
        VcsCommandWrapper cw = (VcsCommandWrapper) commandsWrappers.get(vce);
        if (cw == null) {
            cw = new VcsCommandWrapper(vce, fileSystem);
            commandsWrappers.put(vce, cw);
        }
        cw.setSubmittingThread(Thread.currentThread());
        //setCommandID(vce);
        commandsToRun.remove(cw);
        commandsWaitQueue.add(cw);
        notifyAll(); // executorStarterLoop will start the command
        if (!execStarterLoopStarted) {
            runExecutorStarterLoop();
        }
    }
    
    private synchronized void executorStarter(final VcsCommandWrapper cw) {
        commandsRunning.add(cw);
        threadsPool.processCommand(new Runnable() {
            public void run() {
                VcsCommandExecutor vce;
                synchronized (CommandsPool.this) {
                    vce = cw.getExecutor();
                    cw.setRunningThread(Thread.currentThread());
                    if (isListCommand(vce.getCommand())) numRunningListCommands++;
                    commandStarted(cw);
                }
                Error err = null;
                try {
                    vce.run();
                } catch (RuntimeException rexc) {
                    TopManager.getDefault().notifyException(rexc);
                } catch (ThreadDeath tderr) {
                    err = tderr;
                } catch (Throwable t) {
                    TopManager.getDefault().notifyException(t);
                }
                commandDone(cw);
                if (err != null) throw err;
            }
        });
    }
    
    private synchronized void executorStarterLoop() {
        do {
            VcsCommandWrapper cw;
            do {
                cw = null;
                for (Iterator it = commandsWaitQueue.iterator(); it.hasNext(); ) {
                    VcsCommandWrapper cwTest = (VcsCommandWrapper) it.next();
                    if (canRun(cwTest)) {
                        cw = cwTest;
                        break;
                    }
                }
                if (cw != null) {
                    commandsWaitQueue.remove(cw);
                    executorStarter(cw);
                }
            } while (cw != null);
            try {
                wait();
            } catch (InterruptedException intrexc) {
                // silently ignored
            }
        } while(execStarterLoopRunning);
    }
    
    private void runExecutorStarterLoop() {
        Thread starterLoopThread = new Thread(threadsPool.getThreadGroup(), new Runnable() {
            public void run() {
                executorStarterLoop();
            }
        }, "VCS Command Executor Starter Loop");
        starterLoopThread.setDaemon(true);
        starterLoopThread.start();
        execStarterLoopStarted = true;
    }

    /**
     * Open the default visualizer of the command.
     * @return true if the output was successfully opened, false otherwise
     * (i.e. output is not available)
     */
    public boolean openCommandOutput(final VcsCommandExecutor vce) {
        CommandOutputVisualizer visualizer = (CommandOutputVisualizer) outputVisualizers.get(vce);
        if (visualizer != null) {
            visualizer.requestFocus();
            return true;
        }
        CommandOutputCollector outputCollector = (CommandOutputCollector) outputContainers.get(vce);
        if (outputCollector == null) return false;
        final CommandOutputVisualizer outputVisualizer = new CommandOutputVisualizer(vce);
        outputVisualizer.addCloseListener(new TopComponentCloseListener() {
            public void closing() {
                outputVisualizers.remove(vce);
            }
        });
        if (isRunning(vce)) outputVisualizer.setCommandsPool(this);
        outputVisualizer.open();
        outputVisualizers.put(vce, outputVisualizer);
        outputCollector.addOutputListener(new CommandOutputListener() {
            public void outputLine(String line) {
                outputVisualizer.stdOutputLine(line);
            }
        });
        outputCollector.addErrorOutputListener(new CommandOutputListener() {
            public void outputLine(String line) {
                outputVisualizer.errOutputLine(line);
            }
        });
        outputCollector.addDataOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] data) {
                outputVisualizer.stdOutputData(data);
            }
        });
        outputCollector.addDataErrorOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] data) {
                outputVisualizer.errOutputData(data);
            }
        });
        if (!isRunning(vce) && !isWaiting(vce)) outputVisualizer.setExitStatus(vce.getExitStatus());
        return true;
    }
        
    /**
     * Whether some command is still running.
     * @return true when at least one command is running, false otherwise.
     */
    public synchronized boolean isSomeRunning() {
        return (commandsRunning.size() > 0);
        /*
        boolean running = false;
        for(int i = 0; i < commands.size(); i++) {
            VcsCommandExecutor ec = (VcsCommandExecutor) commands.get(i);
            if (ec.isAlive()) {
                running = true;
                break;
            } else {
                commandsFinished.add(ec);
                commands.remove(i);
                i--;
            }
        }
        return running;
         */
    }
    
    /**
     * Tells whether the executor is waiting. It can either wait till preprocessing
     * finishes or till other commands which can not run in parallel with it finish.
     * @param vce the executor
     */
    public synchronized boolean isWaiting(VcsCommandExecutor vce) {
        VcsCommandWrapper cw = (VcsCommandWrapper) commandsWrappers.get(vce);
        if (cw == null) return false;
        return commandsToRun.contains(cw) || commandsWaitQueue.contains(cw);
    }
    
    /**
     * Tells whether the executor is still running.
     * @param vce the executor
     */
    public synchronized boolean isRunning(VcsCommandExecutor vce) {
        VcsCommandWrapper cw = (VcsCommandWrapper) commandsWrappers.get(vce);
        return (cw != null && commandsRunning.contains(cw));
    }
    
    /**
     * Get display names of running commands.
     */
    public synchronized String[] getRunningCommandsLabels() {
        LinkedList names = new LinkedList();
        for(Iterator it = commandsRunning.iterator(); it.hasNext(); ) {
            VcsCommandWrapper cw = (VcsCommandWrapper) it.next();
            VcsCommandExecutor ec = cw.getExecutor();
            VcsCommand uc = ec.getCommand();
            String label = uc.getDisplayName();
            names.add(label);
        }
        return (String[]) names.toArray(new String[0]);
    }
    
    /** @return true if there are two files contained in the same package folder, false otherwise.
     */
    private static boolean areFilesInSamePackage(Collection files1, Collection files2) {
        for(Iterator it1 = files1.iterator(); it1.hasNext(); ) {
            String file1 = (String) it1.next();
            String dir1 = VcsUtilities.getDirNamePart(file1);
            for(Iterator it2 = files2.iterator(); it2.hasNext(); ) {
                String file2 = (String) it2.next();
                String dir2 = VcsUtilities.getDirNamePart(file2);
                if (dir1.equals(dir2)) return true;
            }
        }
        return false;
    }
    
    /** @return true if a file or folder from <code>files1</code> is contained in a folder
     * from <code>files2</code>
     */
    private static boolean isParentFolder(Collection files1, Collection files2) {
        for(Iterator it1 = files1.iterator(); it1.hasNext(); ) {
            String file1 = (String) it1.next();
            for(Iterator it2 = files2.iterator(); it2.hasNext(); ) {
                String file2 = (String) it2.next();
                if (file1.startsWith(file2)) return true;
            }
        }
        return false;
    }
    
    /**
     * @param concurrencyWith the pairs of command names and concurrent execution integer value
     *   <p> i.e.: "ADD 4", "STATUS 1"
     * @return the map of command names and associated integer values
     */
    private HashMap createConcurrencyMap(String concurrencyWith) {
        HashMap map = new HashMap();
        if (concurrencyWith != null) {
            String[] items = VcsUtilities.getQuotedStrings(concurrencyWith);
            for (int i = 0; i < items.length; i++) {
                int index = items[i].lastIndexOf(' ');
                if (index < 0) continue;
                String cmdName = items[i].substring(0, index);
                String concStr = items[i].substring(index + 1);
                int conc;
                try {
                    conc = Integer.parseInt(concStr);
                } catch (NumberFormatException nfexc) {
                    continue;
                }
                map.put(cmdName, new Integer(conc));
            }
        }
        return map;
    }
    
    private boolean isListCommand(VcsCommand cmd) {
        return cmd.getName().startsWith("LIST");
    }
    
    private Collection getRunningThreadsFromCommands(Collection commands) {
        HashSet threads = new HashSet();
        for (Iterator it = commands.iterator(); it.hasNext(); ) {
            VcsCommandWrapper cw = (VcsCommandWrapper) it.next();
            Thread t = cw.getRunningThread();
            if (t != null) threads.add(t);
        }
        return threads;
    }
    
    /**
     * Returns true iff all exceptionally running commands are
     * predecessors of the given command.
     */
    private boolean areExcRunningPredecessorsOf(VcsCommandWrapper cw) {
        HashSet exceptionallyRunning = new HashSet(commandsExceptionallyRunning);
        boolean is;
        do {
            is = false;
            Thread t = cw.getSubmittingThread();
            for (Iterator it = exceptionallyRunning.iterator(); it.hasNext(); ) {
                VcsCommandWrapper testCw = (VcsCommandWrapper) it.next();
                if (t.equals(testCw.getRunningThread())) {
                    cw = testCw;
                    exceptionallyRunning.remove(testCw);
                    is = true;
                    break;
                }
            }
        } while (is);
        return exceptionallyRunning.size() == 0;
    }
    
    /**
     * Say whether the command executor can be run now or not. It should be called
     * with a monitor lock on this object.
     * Check its concurrent property and other running or waiting commands.
     * @return true if the command can be run in the current monitor lock, false otherwise.
     */
    private synchronized boolean canRun(VcsCommandWrapper cw) {
        VcsCommandExecutor vce = cw.getExecutor();
        // at first check for the maximum number of running commands
        VcsCommand cmd = vce.getCommand();
        //System.out.println("canRun("+cmd.getName()+")");
        if (commandsRunning.size() >= MAX_NUM_RUNNING_COMMANDS ||
            isListCommand(cmd) && numRunningListCommands >= MAX_NUM_RUNNING_LISTS) {
            
            //System.out.println("canRun("+vce.getCommand().getName()+") - limit reached.");
            Thread submitter = cw.getSubmittingThread();
            //System.out.println("  submitter = "+submitter);
            //System.out.println("  runningThreads = "+getRunningThreadsFromCommands(commandsRunning));
            if (getRunningThreadsFromCommands(commandsRunning).contains(submitter)) {
                //System.out.println("  commandsExceptionallyRunning = "+commandsExceptionallyRunning);
                if (commandsExceptionallyRunning.size() == 0 || areExcRunningPredecessorsOf(cw)) {
                    commandsExceptionallyRunning.add(cw);
                    return true;
                }
            }
            return false;
        }
        Collection files = vce.getFiles();
        int concurrency = VcsCommandIO.getIntegerPropertyAssumeZero(cmd,
                            VcsCommand.PROPERTY_CONCURRENT_EXECUTION);
        String concurrencyWith = (String) cmd.getProperty(VcsCommand.PROPERTY_CONCURRENT_EXECUTION_WITH);
        //System.out.println("  concurrency = "+concurrency+", concurrencyWith = "+concurrencyWith);
        if ((concurrency == VcsCommand.EXEC_CONCURRENT_ALL
             && concurrencyWith == null)
            || concurrency == VcsCommand.EXEC_SERIAL_INERT) return true;
        HashMap concurrencyMap = createConcurrencyMap(concurrencyWith);
        String name = cmd.getName();
        boolean haveToWait = false;
        boolean serialOnFile = (concurrency & VcsCommand.EXEC_SERIAL_ON_FILE) != 0;
        boolean serialOnPackage = (concurrency & VcsCommand.EXEC_SERIAL_ON_PACKAGE) != 0;
        boolean serialWithParent = (concurrency & VcsCommand.EXEC_SERIAL_WITH_PARENT) != 0;
        boolean serialOfCommand = (concurrency & VcsCommand.EXEC_SERIAL_OF_COMMAND) != 0;
        boolean serialOfAll = (concurrency & VcsCommand.EXEC_SERIAL_ALL) != 0;
        boolean matchOnFile = false;
        boolean matchOnPackage = false;
        boolean matchWithParent = false;
        boolean matchOfCommand = false;
        ArrayList commandsToTestAgainst = new ArrayList(commandsRunning);
        //System.out.println("  serialOnFile = "+serialOnFile);
        //System.out.println("  serialOnPackage = "+serialOnPackage);
        //System.out.println("  serialWithParent = "+serialWithParent);
        //System.out.println("  serialOfCommand = "+serialOfCommand);
        //System.out.println("  serialOfAll = "+serialOfAll);
        //System.out.println("  commandsToTestAgainst = "+commandsToTestAgainst);
        //if (serialOfAll && commandsToTestAgainst.size() > 0) return false;
        //commandsToTestAgainst.addAll(commandsToRun);
        //commandsToTestAgainst.addAll(commandsWaitQueue);
        //commandsToTestAgainst.remove(vce);
        for(Iterator iter = commandsToTestAgainst.iterator(); iter.hasNext(); ) {
            VcsCommandWrapper cwTest = (VcsCommandWrapper) iter.next();
            VcsCommandExecutor ec = cwTest.getExecutor();
            Collection cmdFiles = ec.getFiles();
            VcsCommand uc = ec.getCommand();
            //System.out.println("  testing with cmd = "+uc.getName());
            int cmdConcurrency = VcsCommandIO.getIntegerPropertyAssumeZero(uc, VcsCommand.PROPERTY_CONCURRENT_EXECUTION);
            //System.out.println("  cmdConcurrency = "+cmdConcurrency);
            if (VcsCommand.EXEC_SERIAL_INERT == cmdConcurrency) continue;
            if (serialOfAll) {
                haveToWait = true;
                break;
            }
            String cmdName = uc.getName();
            if (serialOnFile) {
                for(Iterator it = files.iterator(); it.hasNext(); ) {
                    String file = (String) it.next();
                    if (cmdFiles.contains(file)) {
                        matchOnFile = true;
                        break;
                    }
                }
            }
            if (serialOnPackage) {
                if (areFilesInSamePackage(files, cmdFiles)) {
                    matchOnPackage = true;
                }
            }
            if (serialWithParent) {
                if (isParentFolder(files, cmdFiles)) {
                    matchWithParent = true;
                }
            }
            if (serialOfCommand) {
                matchOfCommand = name.equals(cmdName);
            }
            if (matchOnFile || matchOnPackage || matchWithParent || matchOfCommand) {
                //System.out.println("  matchOnFile = "+matchOnFile+", matchOnPackage = "+matchOnPackage+", matchWithParent = "+matchWithParent+", matchOfCommand = "+matchOfCommand);
                haveToWait = true;
                break;
            }
            Integer concurrencyWithNum = (Integer) concurrencyMap.get(cmdName);
            if (concurrencyWithNum != null) {
                if (haveToWaitFor(files, cmdFiles, concurrencyWithNum.intValue())) {
                    haveToWait = true;
                    break;
                }
            }
        }
        //System.out.println("haveToWait = "+haveToWait);
        return !haveToWait;
    }
    
    private boolean haveToWaitFor(Collection files, Collection cmdFiles, int concurrency) {
        boolean serialOnFile = (concurrency & VcsCommand.EXEC_SERIAL_ON_FILE) != 0;
        boolean serialOnPackage = (concurrency & VcsCommand.EXEC_SERIAL_ON_PACKAGE) != 0;
        boolean serialWithParent = (concurrency & VcsCommand.EXEC_SERIAL_WITH_PARENT) != 0;
        if (serialOnFile) {
            for(Iterator it = files.iterator(); it.hasNext(); ) {
                String file = (String) it.next();
                if (cmdFiles.contains(file)) {
                    return true;
                }
            }
        }
        if (serialOnPackage) {
            if (areFilesInSamePackage(files, cmdFiles)) {
                return true;
            }
        }
        if (serialWithParent) {
            if (isParentFolder(files, cmdFiles)) {
                return true;
            }
        }
        return false;
    }
        
    private synchronized void addExecutorsOfCommand(ArrayList executors, VcsCommand cmd) {
        String name = cmd.getName();
        Iterator it = commandsToRun.iterator();
        while (it.hasNext()) {
            VcsCommandWrapper cw = (VcsCommandWrapper) it.next();
            VcsCommandExecutor executor = cw.getExecutor();
            if (name.equals(executor.getCommand().getName())) {
                executors.add(executor);
            }
        }
        it = commandsWaitQueue.iterator();
        while (it.hasNext()) {
            VcsCommandWrapper cw = (VcsCommandWrapper) it.next();
            VcsCommandExecutor executor = cw.getExecutor();
            if (name.equals(executor.getCommand().getName())) {
                executors.add(executor);
            }
        }
        //addExecutorsOfCommandFromIterator(executors, cmd, commandsToRun.iterator());
        for (Iterator runIt = commandsRunning.iterator(); runIt.hasNext(); ) {
            VcsCommandWrapper cw = (VcsCommandWrapper) runIt.next();
            VcsCommandExecutor executor = cw.getExecutor();
            if (name.equals(executor.getCommand().getName())) {
                executors.add(executor);
            }
        }
        //addExecutorsOfCommandFromIterator(executors, cmd, commands.iterator());
    }
    
    /**
     * Wait to finish the execution of command on a set of files.
     * This methods blocks the current thread untill no instance of the command is running on any of provided files.
     * @param cmd the command we wait for to finish
     * @param files the set of files 
     */
    public void waitToFinish(VcsCommand cmd, Set files) throws InterruptedException {
        boolean haveToWait = false;
        //do {
            ArrayList executors = new ArrayList();
            addExecutorsOfCommand(executors, cmd);
            for (Iterator itExecutors = executors.iterator(); itExecutors.hasNext(); ) {
                VcsCommandExecutor executor = (VcsCommandExecutor) itExecutors.next();
                Collection execFiles = executor.getFiles();
                for (Iterator itFiles = execFiles.iterator(); itFiles.hasNext(); ) {
                    String file = (String) itFiles.next();
                    if (files.contains(file)) {
                        haveToWait = true;
                        break;
                    }
                }
            }
            if (haveToWait) {
                synchronized (this) {
                    wait();
                }
            }
        //} while (haveToWait);
    }
    
    /**
     * Wait to finish the executor.
     * This methods blocks the current thread untill the executor finishes.
     * This method ignores interrupts.
     * @param vce the executor
     */
    public void waitToFinish(VcsCommandExecutor vce) throws InterruptedException {
        VcsCommandWrapper cw = (VcsCommandWrapper) commandsWrappers.get(vce);
        if (cw == null) return ;
        //Thread t;
        synchronized (this) {
            while (commandsToRun.contains(cw) || commandsWaitQueue.contains(cw) ||
                   commandsRunning.contains(cw)) {
                //try {
                wait();
                //} catch (InterruptedException iexc) {}
            }
            //t = cw.getRunningThread();
        }
        /*
        if (t != null) {
            if (t.isAlive()) {
                //try {
                t.join();
                //} catch (InterruptedException exc) {
                    // Ignore
                //}
            }
        }
         */
    }
    
    /**
     * Kill all running executors. It tries to interrupt them, it is up to
     * executor implementations if they will terminate or not.
     */
    public synchronized void killAll() {
        for(Iterator it = commandsRunning.iterator(); it.hasNext(); ) {
            VcsCommandWrapper cw = (VcsCommandWrapper) it.next();
            Thread t = cw.getRunningThread();
            if (t != null) t.interrupt();
            //if (ec.isAlive()) ec.interrupt();
            //commandsFinished.add(ec);
            //commands.remove(i);
            //i--;
        }
    }
    
    /**
     * Kill the executor if it is running. It tries to interrupt it, it is up to
     * executor implementation if it will terminate or not.
     */
    public synchronized void kill(VcsCommandExecutor vce) {
        VcsCommandWrapper cw = (VcsCommandWrapper) commandsWrappers.get(vce);
        if (cw != null) {
            Thread t = cw.getRunningThread();
            if (t != null) t.interrupt();
            else {
                commandsWaitQueue.remove(cw);
                cw.setInterrupted(true);
                commandDone(cw);
            }
        }
    }
    
    /**
     * Add a command listener.
     */
    public void addCommandListener(CommandListener listener) {
        synchronized (commandListeners) {
            commandListeners.add(listener);
        }
    }
    
    /**
     * Lazily remove a command listener.
     */
    public void removeCommandListener(final CommandListener listener) {
        org.openide.util.RequestProcessor.postRequest(new Runnable() {
            public void run() {
                synchronized (commandListeners) {
                    commandListeners.remove(listener);
                }
            }
        });
    }
    
    /**
     * A helper method for those who have registered a command listener
     * to be able to recognize commands, that belongs to different filesystems.
     */
    public VcsFileSystem getFileSystemForExecutor(VcsCommandExecutor vce) {
        VcsCommandWrapper cw = (VcsCommandWrapper) commandsWrappers.get(vce);
        if (cw == null) return null;
        else return cw.getFileSystem();
    }
    
    /** The start time of the command or zero, when the command was not started yet
     * or can not be found.
     */
    public long getStartTime(VcsCommandExecutor vce) {
        VcsCommandWrapper cw = (VcsCommandWrapper) commandsWrappers.get(vce);
        if (cw == null) return 0;
        return cw.getStartTime();
    }
    
    /** The finish time of the command or zero, when the command did not finish yet
     * or can not be found.
     */
    public long getFinishTime(VcsCommandExecutor vce) {
        VcsCommandWrapper cw = (VcsCommandWrapper) commandsWrappers.get(vce);
        if (cw == null) return 0;
        return cw.getFinishTime();
    }
    
    /** The execution time of the command or zero, when the command did not finish yet
     * or can not be found.
     */
    public long getExecutionTime(VcsCommandExecutor vce) {
        VcsCommandWrapper cw = (VcsCommandWrapper) commandsWrappers.get(vce);
        if (cw == null) return 0;
        return cw.getExecutionTime();
    }
    
    /**
     * Get the localized string representation of the command exit status.
     * @param exit the exit status, that will be converted to the string.
     */
    public static String getExitStatusString(int exit) {
        String status;
        if (VcsCommandExecutor.SUCCEEDED == exit) {
            status = org.openide.util.NbBundle.getBundle(CommandsPool.class).getString("CommandExitStatus.success");
        } else if (VcsCommandExecutor.FAILED == exit) {
            status = org.openide.util.NbBundle.getBundle(CommandsPool.class).getString("CommandExitStatus.failed");
        } else if (VcsCommandExecutor.INTERRUPTED == exit) {
            status = org.openide.util.NbBundle.getBundle(CommandsPool.class).getString("CommandExitStatus.interrupted");
        } else {
            status = org.openide.util.NbBundle.getBundle(CommandsPool.class).getString("CommandExitStatus.unknown");
        }
        return status;
    }
    
    private static class VcsCommandWrapper extends Object {
        
        private static long lastId = 0;
        
        private VcsCommandExecutor vce;
        private VcsFileSystem fileSystem;
        private long id;
        private Reference submittingThread;
        private Thread runningThread;
        private RuntimeCommand runtimeCommand;
        private boolean interrupted = false;
        private long startTime = 0;
        private long finishTime = 0;
        
        public VcsCommandWrapper(VcsCommandExecutor vce, VcsFileSystem vfs) {
            this.vce = vce;
            this.fileSystem = vfs;
            synchronized (VcsCommandWrapper.class) {
                this.id = lastId++;
            }
        }
        
        public VcsCommandExecutor getExecutor() {
            return vce;
        }
        
        public VcsFileSystem getFileSystem() {
            return fileSystem;
        }
        
        public long getCommandID() {
            return id;
        }
        
        public void setSubmittingThread(Thread thread) {
            this.submittingThread = new WeakReference(thread);
        }
        
        public Thread getSubmittingThread() {
            return (Thread) submittingThread.get();
        }
        
        public void setRunningThread(Thread thread) {
            this.runningThread = thread;
        }
        
        public Thread getRunningThread() {
            return runningThread;
        }
        
        public void setRuntimeCommand(RuntimeCommand command) {
            this.runtimeCommand = command;
        }
        
        public RuntimeCommand getRuntimeCommand() {
            return runtimeCommand;
        }
        
        public void setInterrupted(boolean interrupted) {
            this.interrupted = interrupted;
        }
        
        public boolean isInterrupted() {
            return interrupted;
        }
        
        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public void setFinishTime(long finishTime) {
            this.finishTime = finishTime;
        }
        
        public long getFinishTime() {
            return finishTime;
        }
        
        public long getExecutionTime() {
            if (startTime != 0 && finishTime != 0) {
                return finishTime - startTime;
            } else {
                return 0;
            }
        }
    }

    
    private String g(String s) {
        return org.openide.util.NbBundle.getBundle(CommandsPool.class).getString(s);
    }
    
    private String  g(String s, Object obj) {
        return java.text.MessageFormat.format (g(s), new Object[] { obj });
    }

    private String  g(String s, Object obj, Object obj2) {
        return java.text.MessageFormat.format (g(s), new Object[] { obj, obj2 });
    }

}

