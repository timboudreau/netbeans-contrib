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
    
    private WeakHashMap commandsIDs;
    
    /** Contains instances of VcsCommandExecutor, which are to be run.  */
    private ArrayList commandsToRun;
    /** Contains instances of VcsCommandExecutor, which are waiting to run. */
    private ArrayList commandsWaitQueue;
    /** Contains pairs of instances of VcsCommandExecutor and threads in which are running. */
    private Table commands;
    /** contains pairs of instances of VcsCommandExecutor and the RuntimeCommand that belongs to it */
    private Table runtimeCommands;
    /** The containers of output of commands. Contains pairs of instances of VcsCommandExecutor
     * and instances of CommandOutputCollector */
    private Hashtable outputContainers;
    /** The table of currently opened standard Visualizers */
    private Hashtable outputVisualizers;
    /** Contains finished instances of VcsCommandExecutor. */
    private ArrayList commandsFinished;
    private int numRunningListCommands;
    
    private ThreadGroup group;
    
    /** Whether to collect the whole output of commands. */
    private boolean collectOutput = true;
    /** Whether to collect error output of commands. */
    private boolean collectErrOutput = true;
    
    private ArrayList commandListeners = new ArrayList();
    
    private WeakReference fileSystem;
    
    private FSDisplayPropertyChangeListener fsDisplayPropertyChange;

    private boolean execStarterLoopStarted = false;
    private boolean execStarterLoopRunning = true;

    /** Creates new CommandsPool */
    public CommandsPool(final VcsFileSystem fileSystem) {
        this(fileSystem, true);
    }
    
    /** Creates new CommandsPool */
    public CommandsPool(final VcsFileSystem fileSystem, boolean createRuntimeCommands) {
        commandsToRun = new ArrayList();
        commands = new Table();
        runtimeCommands = new Table();
        commandsFinished = new ArrayList();
        commandsWaitQueue = new ArrayList();
        outputContainers = new Hashtable();
        outputVisualizers = new Hashtable();
        commandsIDs = new WeakHashMap();
        numRunningListCommands = 0;
        group = new ThreadGroup("VCS Commands Group");
        this.fileSystem = new WeakReference(fileSystem);
        fsDisplayPropertyChange = new FSDisplayPropertyChangeListener(fileSystem.getSystemName());
        fileSystem.addPropertyChangeListener(WeakListener.propertyChange(fsDisplayPropertyChange, fileSystem));
        /*
        org.openide.filesystems.Repository repo = TopManager.getDefault().getRepository();
        repo.addRepositoryListener(
            org.openide.util.WeakListener.repository(this, repo));
         */
        //executorStarterLoop();
    }
    
    private VcsFileSystem getVcsFileSystem() {
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        if (fileSystem == null) cleanup();
        return fileSystem;
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
/*  now happens in the RuntimeChildren instance..
 if (runtimeNode != null) {
            runtimeNode.removePropertyChangeListener(runtimeNodePropertyChange);
            try {
                runtimeNode.destroy();
                runtimeNode = null;
            } catch (java.io.IOException exc) {
                TopManager.getDefault().notifyException(exc);
            }
        }
 */
        synchronized (this) {
            //* The FS may still exist i.e. inside a MultiFileSystem => do not interrupt the loop now
            execStarterLoopRunning = false;
            notifyAll();
            // */
        }
    }
    
    public void setupRuntime(RuntimeFolderNode runNode) {
/*        if (runtimeNode != null) {
            runtimeNode = runNode;
            runtimeNode.setNumOfFinishedCmdsToCollect(collectFinishedCmdsNum);
            runtimeNode.addPropertyChangeListener(runtimeNodePropertyChange);
            execStarterLoopStarted = false;
            execStarterLoopRunning = true;
        }
 */
    }
    
    public void setCollectOutput(boolean collectOutput) {
        this.collectOutput = collectOutput;
    }
    
    public boolean isCollectOutput() {
        return collectOutput;
    }

    private void setCommandID(VcsCommandExecutor vce) {
        if (commandsIDs.get(vce) == null) {
            synchronized (CommandsPool.class) {
                Long id = new Long(++lastCommandID);
                commandsIDs.put(vce, id);
            }
        }
    }
    
    /**
     * Get the command's ID. It's a unique command identification number.
     * @param vce the command's executor
     * @return the ID or -1 if the command does not have one.
     */
    public long getCommandID(VcsCommandExecutor vce) {
        Long id = (Long) commandsIDs.get(vce);
        if (id != null) return id.longValue();
        else return -1;
    }

    /**
     * Perform preprocessing of a new command. It will perform any needed input
     * and update the execution string.
     * @param vce the command to preprocess
     * @return the preprocessing status, one of CommandExecutorSupport.PROPEROCESS_* constants
     */
    public int preprocessCommand(VcsCommandExecutor vce, Hashtable vars) {
        return preprocessCommand(vce, vars, null);
    }
    
    /**
     * Perform preprocessing of a new command. It will perform any needed input
     * and update the execution string.
     * @param vce the command to preprocess
     * @return the preprocessing status, one of CommandExecutorSupport.PROPEROCESS_* constants
     */
    public int preprocessCommand(VcsCommandExecutor vce, Hashtable vars, boolean[] askForEachFile) {
        setCommandID(vce);
        VcsFileSystem fileSystem = getVcsFileSystem();
        if (fileSystem == null) return PREPROCESS_CANCELLED;
        synchronized (commandsToRun) {
            commandsToRun.add(vce);
        }
        VcsCommand cmd = vce.getCommand();
        String name = cmd.getDisplayName();
        if (name == null || name.length() == 0) name = cmd.getName();
        String exec = vce.getExec();
        fileSystem.debug(g("MSG_Command_preprocessing", name, exec));
        RuntimeCommand rCom = new VcsRuntimeCommand(vce, this);
        synchronized (runtimeCommands) {
            runtimeCommands.put(vce, rCom);
        }
        rCom.setState(RuntimeCommand.STATE_WAITING);
        RuntimeSupport.getInstance().updateCommand(fileSystem.getSystemName(), rCom);
        int preprocessStatus = CommandExecutorSupport.preprocessCommand(fileSystem, vce, vars, askForEachFile);
        if (PREPROCESS_CANCELLED == preprocessStatus) {
            synchronized (this) {
                commandsToRun.remove(vce);
                runtimeCommands.remove(vce);
                //commandsFinished.add(vce);
                notifyAll();
            }
            fileSystem.debug(g("MSG_Command_cancelled", name));
            //RuntimeSupport.addCancelled(runtimeNode, vce, this);
            RuntimeSupport.getInstance().removeDone(fileSystem.getSystemName(), rCom);
        }
        return preprocessStatus;
    }
    
    private void commandStarted(final VcsCommandExecutor vce) {
        setCommandID(vce);
        final VcsFileSystem fileSystem = getVcsFileSystem();
        if (fileSystem == null) return ;
        VcsCommand cmd = vce.getCommand();
        //waitToRun(cmd, vce.getFiles());
        String name = cmd.getDisplayName();
        if (name == null || name.length() == 0) name = cmd.getName();
        final String finalName = name;
        RuntimeCommand rCom = (RuntimeCommand)runtimeCommands.get(vce);
        if (rCom == null) {
            rCom = new VcsRuntimeCommand(vce, CommandsPool.this);
            runtimeCommands.put(vce, rCom);
        }
        rCom.setState(RuntimeCommand.STATE_RUNNING);
        RuntimeSupport.getInstance().updateCommand(fileSystem.getSystemName(), rCom);
        RequestProcessor.postRequest(new Runnable() {
            public void run() {
                TopManager.getDefault().setStatusText(g("MSG_Command_name_running", finalName));
                fileSystem.debug(g("MSG_Command_started", finalName, vce.getExec()));
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
        FileSystemCache cache = CacheHandler.getInstance().getCache(fileSystem.getCacheIdStr());
        if (cache != null && cache instanceof FileReaderListener) {
            vce.addFileReaderListener((FileReaderListener) cache);
        }
    }
    
    private void commandDone(VcsCommandExecutor vce) {
        //System.out.println("command "+vce.getCommand()+" DONE.");
        VcsFileSystem fileSystem = getVcsFileSystem();
        if (fileSystem == null) return ;
        VcsCommand cmd = vce.getCommand();
        String name = cmd.getDisplayName();
        if (name == null || name.length() == 0) name = cmd.getName();
        synchronized (this) {
            if (isListCommand(cmd)) numRunningListCommands--;
            commands.remove(vce);
            commandsFinished.add(vce);
            notifyAll();
        }
        RuntimeCommand rCom = (RuntimeCommand)runtimeCommands.get(vce);
        if (rCom == null) {
            rCom = new VcsRuntimeCommand(vce, this);
            runtimeCommands.put(vce, rCom);
        }
        rCom.setState(RuntimeCommand.STATE_DONE);
        RuntimeSupport rSupport = RuntimeSupport.getInstance();
        rSupport.updateCommand(fileSystem.getSystemName(), rCom);
        int collectFinishedCmdsNum = rSupport.getCollectFinishedCmdsNum(fileSystem.getSystemName());
        synchronized (commandsFinished) {
            //commandsFinished.removeRange(0, commandsFinished.size() - collectFinishedCmdsNum);
            while (commandsFinished.size() > collectFinishedCmdsNum) {
                VcsCommandExecutor removedExecutor = (VcsCommandExecutor) commandsFinished.remove(0);
                outputContainers.remove(removedExecutor);
                RuntimeCommand runCom = (RuntimeCommand)runtimeCommands.remove(removedExecutor);
                rSupport.removeDone(fileSystem.getSystemName(), runCom);
            }
        }
        if (!isCollectOutput()) {
            outputContainers.remove(vce);
        }
        synchronized (commandListeners) {
            for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
                ((CommandListener) it.next()).commandDone(vce);
            }
        }
        CommandExecutorSupport.postprocessCommand(fileSystem, vce);
        //System.out.println("command "+vce.getCommand()+" DONE, LISTENERS DONE.");
        int exit = vce.getExitStatus();
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
            fileSystem.debugErr(message);
            printErrorOutput(vce);
            if (fileSystem.isCommandNotification()) {
                notification = (String) cmd.getProperty(VcsCommand.PROPERTY_NOTIFICATION_FAIL_MSG);
            }
        } else {
            fileSystem.debug(message);
            if (fileSystem.isCommandNotification()) {
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
    
    private void printErrorOutput(VcsCommandExecutor vce) {
        final VcsFileSystem fileSystem = getVcsFileSystem();
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
        setCommandID(vce);
        commandsToRun.remove(vce);
        commandsWaitQueue.add(vce);
        notifyAll(); // executorStarterLoop will start the command
        if (!execStarterLoopStarted) {
            runExecutorStarterLoop();
        }
    }
    
    private synchronized void executorStarter(final VcsCommandExecutor vce) {
        final Thread t = new Thread(group, vce, "VCS Command \""+vce.getCommand().getName()+"\" Execution Thread");
        commands.put(vce, t);
        if (isListCommand(vce.getCommand())) numRunningListCommands++;
        commandStarted(vce);
        t.start();
        //System.out.println("startExecutor, thread started.");
        new Thread(group, "VCS Command \""+vce.getCommand().getName()+"\" Execution Waiter") {
            public void run() {
                //System.out.println("startExecutor.Waiter: thread checking ...");
                while (t.isAlive()) {
                    //System.out.println("startExecutor.Waiter: thread is Alive");
                    try {
                        t.join();
                    } catch (InterruptedException exc) {
                        // Ignore
                    }
                }
                commandDone(vce);
            }
        }.start();
    }
    
    /*
    private void executorStarterLoop() {
        new Thread(group, new Runnable() {
            public void run() {
                do {
                    VcsCommandExecutor vce;
                    do {
                        vce = null;
                        synchronized (CommandsPool.this) {
                            for (Iterator it = commandsWaitQueue.iterator(); it.hasNext(); ) {
                                vce = (VcsCommandExecutor) it.next();
                                if (canRun(vce)) break;
                            }
                            if (vce != null) {
                                commandsWaitQueue.remove(vce);
                                executorStarter(vce);
                            }
                        }
                    } while (vce != null);
                    synchronized (CommandsPool.this) {
                        try {
                            wait();
                        } catch (InterruptedException intrexc) {
                            // silently ignored
                        }
                    }
                } while(execStarterLoopRunning);
            }
        }, "VCS Command Executor Starter Loop").start();
        execStarterLoopStarted = true;
    }
     */
    private synchronized void executorStarterLoop() {
        do {
            VcsCommandExecutor vce;
            do {
                vce = null;
                for (Iterator it = commandsWaitQueue.iterator(); it.hasNext(); ) {
                    VcsCommandExecutor vceTest = (VcsCommandExecutor) it.next();
                    if (canRun(vceTest)) {
                        vce = vceTest;
                        break;
                    }
                }
                if (vce != null) {
                    commandsWaitQueue.remove(vce);
                    executorStarter(vce);
                }
            } while (vce != null);
            try {
                wait();
            } catch (InterruptedException intrexc) {
                // silently ignored
            }
        } while(execStarterLoopRunning);
    }
    
    private void runExecutorStarterLoop() {
        Thread starterLoopThread = new Thread(group, new Runnable() {
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
        return (commands.size() > 0);
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
        return commandsToRun.contains(vce) || commandsWaitQueue.contains(vce);
    }
    
    /**
     * Tells whether the executor is still running.
     * @param vce the executor
     */
    public synchronized boolean isRunning(VcsCommandExecutor vce) {
        return (commands.get(vce) != null);
    }
    
    /**
     * Get display names of running commands.
     */
    public synchronized String[] getRunningCommandsLabels() {
        LinkedList names = new LinkedList();
        for(Enumeration enum = commands.keys(); enum.hasMoreElements(); ) {
            VcsCommandExecutor ec = (VcsCommandExecutor) enum.nextElement();
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
    
    /**
     * Say whether the command executor can be run now or not. It should be called
     * with a monitor lock on this object.
     * Check its concurrent property and other running or waiting commands.
     * @return true if the command can be run in the current monitor lock, false otherwise.
     */
    private synchronized boolean canRun(VcsCommandExecutor vce) {
        // at first check for the maximum number of running commands
        if (commands.size() >= MAX_NUM_RUNNING_COMMANDS) return false;
        VcsCommand cmd = vce.getCommand();
        //System.out.println("canRun("+cmd.getName()+")");
        if (isListCommand(cmd) && numRunningListCommands >= MAX_NUM_RUNNING_LISTS) return false;
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
        ArrayList commandsToTestAgainst = new ArrayList(commands.keySet());
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
            VcsCommandExecutor ec = (VcsCommandExecutor) iter.next();
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
            VcsCommandExecutor executor = (VcsCommandExecutor) it.next();
            if (name.equals(executor.getCommand().getName())) {
                executors.add(executor);
            }
        }
        it = commandsWaitQueue.iterator();
        while (it.hasNext()) {
            VcsCommandExecutor executor = (VcsCommandExecutor) it.next();
            if (name.equals(executor.getCommand().getName())) {
                executors.add(executor);
            }
        }
        //addExecutorsOfCommandFromIterator(executors, cmd, commandsToRun.iterator());
        Enumeration enum = commands.keys();
        while(enum.hasMoreElements()) {
            VcsCommandExecutor executor = (VcsCommandExecutor) enum.nextElement();
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
    public void waitToFinish(VcsCommand cmd, Set files) {
        boolean haveToWait = false;
        do {
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
                    try {
                        wait();
                    } catch (InterruptedException exc) {
                        // I was interrupted => check for wait again.
                    }
                }
            }
        } while (haveToWait);
    }
    
    /**
     * Wait to finish the executor.
     * This methods blocks the current thread untill the executor finishes.
     * This method ignores interrupts.
     * @param vce the executor
     */
    public void waitToFinish(VcsCommandExecutor vce) {
        Thread t;
        synchronized (this) {
            while (commandsToRun.contains(vce) || commandsWaitQueue.contains(vce)) {
                try {
                    wait();
                } catch (InterruptedException iexc) {}
            }
            t = (Thread) commands.get(vce);
        }
        if (t != null) {
            while(t.isAlive()) {
                try {
                    t.join();
                } catch (InterruptedException exc) {
                    // Ignore
                }
            }
        }
    }
    
    /**
     * Kill all running executors. It tries to interrupt them, it is up to
     * executor implementations if they will terminate or not.
     */
    public synchronized void killAll() {
        Set set = commands.entrySet();
        for(Iterator it = set.iterator(); it.hasNext(); ) {
            //VcsCommandExecutor ec = (VcsCommandExecutor) enum.nextElement();
            Thread t = (Thread) it.next();
            t.interrupt();
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
        Thread t = (Thread) commands.get(vce);
        if (t != null) t.interrupt();
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
    

    private class FSDisplayPropertyChangeListener implements java.beans.PropertyChangeListener {
        private String oldFsSystemName;
        public FSDisplayPropertyChangeListener(String initFSSystemName) {
            oldFsSystemName = initFSSystemName;
        }
        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            if (VcsFileSystem.PROP_SYSTEM_NAME.equals(evt.getPropertyName())) {
                VcsFileSystem fileSystem = getVcsFileSystem();
                if (fileSystem == null) return ;
                RuntimeSupport.getInstance().updateRuntime(fileSystem, oldFsSystemName);
                oldFsSystemName = fileSystem.getSystemName();
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

