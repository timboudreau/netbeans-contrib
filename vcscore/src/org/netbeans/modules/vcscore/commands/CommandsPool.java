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

import java.util.*;

import org.openide.TopManager;
import org.openide.nodes.Node;
//import org.openide.nodes.AbstractNode;
//import org.openide.nodes.Children;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.runtime.RuntimeSupport;

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
    
    /** Contains instances of VcsCommandExecutor, which are to be run.  */
    private ArrayList commandsToRun;
    /** Contains pairs of instances of VcsCommandExecutor and threads in which are running. */
    private Table commands;
    /** The containers of output of commands. Contains pairs of instances of VcsCommandExecutor
     * and instances of CommandOutputCollector */
    private Hashtable outputContainers;
    /** The table of currently opened standard Visualizers */
    private Hashtable outputVisualizers;
    /** Contains finished instances of VcsCommandExecutor. */
    private ArrayList commandsFinished;
    
    private ThreadGroup group;
    
    /** The number of finished commands to collect. */
    private int collectFinishedCmdsNum = 20;
    /** Whether to collect the whole output of commands. */
    private boolean collectOutput = true;
    /** Whether to collect error output of commands. */
    private boolean collectErrOutput = true;
    
    private ArrayList commandListeners = new ArrayList();
    
    private VcsFileSystem fileSystem;
    
    private Node runtimeNode;

    /** Creates new CommandsPool */
    public CommandsPool(VcsFileSystem fileSystem) {
        commandsToRun = new ArrayList();
        commands = new Table();
        commandsFinished = new ArrayList();
        outputContainers = new Hashtable();
        outputVisualizers = new Hashtable();
        group = new ThreadGroup("VCS Commands Goup");
        this.fileSystem = fileSystem;
        runtimeNode = RuntimeSupport.initRuntime(fileSystem.getDisplayName());
    }
    
    /*
    private void initRuntime() {
        Node runtime = TopManager.getDefault().getPlaces().nodes().environment();
        Children runtimeCh = runtime.getChildren();
        Node vcsRuntime = getVcsRuntime(runtimeCh);
    }
    
    private Node getVcsRuntime(Children ch) {
        Node[] nodes = ch.getNodes();
        for(int i = 0; i < nodes.length; i++) {
            if (VCS_RUNTIME_NODE_NAME.equals(nodes[i].getName())) return nodes[i];
        }
        AbstractNode vcsRuntime = new AbstractNode(new Children.Array());
        vcsRuntime.setName(VCS_RUNTIME_NODE_NAME);
        vcsRuntime.setDisplayName(g("CTL_VcsRuntime"));
        ch.add(new Node[] { vcsRuntime });
        return vcsRuntime;
    }
     */

    /**
     * Set the number of finished commands to collect.
     */
    public void setCollectFinishedCmdsNum(int collectFinishedCmdsNum) {
        this.collectFinishedCmdsNum = collectFinishedCmdsNum;
    }
    
    /**
     * Get the number of finished commands to collect.
     */
    public int getCollectFinishedCmds() {
        return collectFinishedCmdsNum;
    }
    
    public void setCollectOutput(boolean collectOutput) {
        this.collectOutput = collectOutput;
    }
    
    public boolean isCollectOutput() {
        return collectOutput;
    }

    /*
    public void setCollectErrOutput(boolean collectErrOutput) {
        this.collectErrOutput = collectErrOutput;
    }
    
    public boolean isCollectErrOutput() {
        return collectErrOutput;
    }
     */

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
        synchronized (commandsToRun) {
            commandsToRun.add(vce);
        }
        VcsCommand cmd = vce.getCommand();
        String name = cmd.getDisplayName();
        if (name == null) name = cmd.getName();
        String exec = vce.getExec();
        fileSystem.debug(g("MSG_Command_preprocessing", name, exec));
        RuntimeSupport.addWaiting(runtimeNode, vce);
        int preprocessStatus = CommandExecutorSupport.preprocessCommand(fileSystem, vce, vars, askForEachFile);
        if (PREPROCESS_CANCELLED == preprocessStatus) {
            fileSystem.debug(g("MSG_Command_cancelled", name));
        }
        return preprocessStatus;
    }
    
    private void commandStarted(VcsCommandExecutor vce) {
        VcsCommand cmd = vce.getCommand();
        waitToRun(cmd, vce.getFiles());
        String name = cmd.getDisplayName();
        if (name == null) name = cmd.getName();
        TopManager.getDefault().setStatusText(g("MSG_Command_name_running", name));
        fileSystem.debug(g("MSG_Command_started", name, vce.getExec()));
        RuntimeSupport.addRunning(runtimeNode, vce);
        //System.out.println("command "+vce.getCommand()+" STARTED.");
        synchronized (commandListeners) {
            for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
                ((CommandListener) it.next()).commandStarted(vce);
            }
        }
        CommandOutputCollector collector = new CommandOutputCollector(vce);
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
    }
    
    private void commandDone(VcsCommandExecutor vce) {
        //System.out.println("command "+vce.getCommand()+" DONE.");
        VcsCommand cmd = vce.getCommand();
        String name = cmd.getDisplayName();
        if (name == null) name = cmd.getName();
        synchronized (this) {
            commands.remove(vce);
            commandsFinished.add(vce);
            notifyAll();
        }
        RuntimeSupport.addDone(runtimeNode, vce);
        synchronized (commandsFinished) {
            //commandsFinished.removeRange(0, commandsFinished.size() - collectFinishedCmdsNum);
            while (commandsFinished.size() > collectFinishedCmdsNum) {
                VcsCommandExecutor removedExecutor = (VcsCommandExecutor) commandsFinished.remove(0);
                outputContainers.remove(removedExecutor);
                RuntimeSupport.removeDone(removedExecutor);
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
        //System.out.println("command "+vce.getCommand()+" DONE, LISTENERS DONE.");
        int exit = vce.getExitStatus();
        //String name = vce.getCommand().getDisplayName();
        String message = "";
        switch (exit) {
            case VcsCommandExecutor.SUCCEEDED:
                message = g("MSG_Command_name_finished", name);
                CommandExecutorSupport.doRefresh(fileSystem, vce);
                break;
            case VcsCommandExecutor.FAILED:
                message = g("MSG_Command_name_failed", name);
                break;
            case VcsCommandExecutor.INTERRUPTED:
                message = g("MSG_Command_name_interrupted", name);
                break;
        }
        TopManager.getDefault().setStatusText(message);
        fileSystem.debug(message);
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
     * Start the executor. The method starts the executor in a separate thread
     * and does not wait for it to finish.
     * @param vce the executor
     */
    public void startExecutor(final VcsCommandExecutor vce) {
        final Thread t = new Thread(group, vce, "VCS Command Execution Thread");
        //System.out.println("startExecutor("+vce.getCommand()+")");
        synchronized (this) {
            commandsToRun.remove(vce);
            commands.put(vce, t);
        }
        commandStarted(vce);
        t.start();
        //System.out.println("startExecutor, thread started.");
        new Thread(group, "VCS Command Execution Waiter") {
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
        //System.out.println("startExecutor, waiter started.");
    }

    /**
     * Open the default visualizer of the command.
     * @return true if the output was successfully opened, false otherwise
     * (i.e. output is not available)
     */
    public boolean openCommandOutput(VcsCommandExecutor vce) {
        if (outputVisualizers.get(vce) != null) return true;
        CommandOutputCollector outputCollector = (CommandOutputCollector) outputContainers.get(vce);
        if (outputCollector == null) return false;
        final CommandOutputVisualizer outputVisualizer = new CommandOutputVisualizer(vce);
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
    
    /**
     * Wait before the command can be run if necessary.
     * Check its concurrent property and other running commands.
     */
    public synchronized void waitToRun(VcsCommand cmd, Collection files) {
        int concurrency = VcsCommandIO.getIntegerPropertyAssumeZero(cmd,
                            VcsCommand.PROPERTY_CONCURRENT_EXECUTION);
        if (concurrency == VcsCommand.EXEC_CONCURRENT_ALL) return ;
        String name = cmd.getName();
        boolean haveToWait = true;
        do {
            for(Enumeration enum = commands.keys(); enum.hasMoreElements(); ) {
                VcsCommandExecutor ec = (VcsCommandExecutor) enum.nextElement();
                VcsCommand uc = ec.getCommand();
                String cmdName = uc.getName();
                if ((concurrency & VcsCommand.EXEC_SERIAL_OF_COMMAND) != 0) {
                    if (name.equals(cmdName)) {
                        try {
                            wait();
                        } catch (InterruptedException intrexc) {
                            // silently ignored
                        }
                        continue;
                    }
                }
                Collection cmdFiles = ec.getFiles();
            //int index = commands.indexOf(cmd);
            //if (index >= 0) {
            //    VcsCommandExecutor executor = (VcsCommandExecutor) commands.get(index);
                
            }
            haveToWait = false;
        } while (haveToWait);
        // TODO
    }
    
    /*
    private synchronized void addExecutorsOfCommandFromIterator(ArrayList executors, VcsCommand cmd, Iterator source) {
        while (source.hasNext()) {
            VcsCommandExecutor executor = (VcsCommandExecutor) source.next();
            if (cmd.getName().equals(executor.getCommand().getName())) {
                executors.add(executor);
            }
        }
    }
     */
    
    private synchronized void addExecutorsOfCommand(ArrayList executors, VcsCommand cmd) {
        String name = cmd.getName();
        Iterator it = commandsToRun.iterator();
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
        Thread t = (Thread) commands.get(vce);
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
    public synchronized void addCommandListener(CommandListener listener) {
        commandListeners.add(listener);
    }
    
    /**
     * Remove a command listener.
     */
    public synchronized void removeCommandListener(CommandListener listener) {
        commandListeners.remove(listener);
    }
    
    /**
     * This class stores the output of the command.
     */
    private class CommandOutputCollector implements CommandListener {
        
        //private VcsCommandExecutor vce;
        private ArrayList stdOutput = new ArrayList();
        private ArrayList errOutput = new ArrayList();
        private ArrayList stdDataOutput = new ArrayList();
        private ArrayList errDataOutput = new ArrayList();
        
        private ArrayList stdOutputListeners = new ArrayList();
        private ArrayList errOutputListeners = new ArrayList();
        private ArrayList stdDataOutputListeners = new ArrayList();
        private ArrayList errDataOutputListeners = new ArrayList();
        
        public CommandOutputCollector(VcsCommandExecutor vce/*, boolean errOnly*/) {
            //this.vce = vce;
            //if (!errOnly) {
                vce.addOutputListener(new CommandOutputListener() {
                    public void outputLine(String line) {
                        synchronized (stdOutput) {
                            stdOutput.add(line);
                            //if (stdOutputListeners != null) {
                                for(Iterator it = stdOutputListeners.iterator(); it.hasNext(); ) {
                                    ((CommandOutputListener) it.next()).outputLine(line);
                                }
                            //}
                        }
                    }
                });
                vce.addDataOutputListener(new CommandDataOutputListener() {
                    public void outputData(String[] elements) {
                        synchronized (stdDataOutput) {
                            stdDataOutput.add(elements);
                            //if (stdDataOutputListeners != null) {
                                for(Iterator it = stdDataOutputListeners.iterator(); it.hasNext(); ) {
                                    ((CommandDataOutputListener) it.next()).outputData(elements);
                                }
                            //}
                        }
                    }
                });
            //}
            vce.addErrorOutputListener(new CommandOutputListener() {
                public void outputLine(String line) {
                    synchronized (errOutput) {
                        errOutput.add(line);
                        //if (errOutputListeners != null) {
                            for(Iterator it = errOutputListeners.iterator(); it.hasNext(); ) {
                                ((CommandOutputListener) it.next()).outputLine(line);
                            }
                        //}
                    }
                }
            });
            vce.addDataErrorOutputListener(new CommandDataOutputListener() {
                public void outputData(String[] elements) {
                    synchronized (errDataOutput) {
                        errDataOutput.add(elements);
                        //if (errDataOutputListeners != null) {
                            for(Iterator it = errDataOutputListeners.iterator(); it.hasNext(); ) {
                                ((CommandDataOutputListener) it.next()).outputData(elements);
                            }
                        //}
                    }
                }
            });
            addCommandListener(this);
        }
        
        /**
         * This method is called when the command is just started.
         */
        public void commandStarted(VcsCommandExecutor vce) {
            //if (this.vce == vce)
        }
        
        /**
         * This method is called when the command is done.
         */
        public void commandDone(VcsCommandExecutor vce) {
            synchronized (stdOutput) {
                stdOutputListeners = null;
            }
            synchronized (stdDataOutput) {
                stdDataOutputListeners = null;
            }
            synchronized (errOutput) {
                errOutputListeners = null;
            }
            synchronized (errDataOutput) {
                errDataOutputListeners = null;
            }
        }
        
        /**
         * Add the listener to the standard output of the command. The listeners are removed
         * when the command finishes.
         */
        public void addOutputListener(CommandOutputListener l) {
            synchronized (stdOutput) {
                for (Iterator it = stdOutput.iterator(); it.hasNext(); ) {
                    l.outputLine((String) it.next());
                }
                if (stdOutputListeners != null) {
                    stdOutputListeners.add(l);
                }
            }
        }
        
        /**
         * Add the listener to the error output of the command. The listeners are removed
         * when the command finishes.
         */
        public void addErrorOutputListener(CommandOutputListener l) {
            synchronized (errOutput) {
                for (Iterator it = errOutput.iterator(); it.hasNext(); ) {
                    l.outputLine((String) it.next());
                }
                if (errOutputListeners != null) {
                    errOutputListeners.add(l);
                }
            }
        }
        
        /**
         * Add the listener to the data output of the command. This output may contain
         * a parsed information from its standard output or some other data provided
         * by this command. The listeners are removed when the command finishes.
         */
        public void addDataOutputListener(CommandDataOutputListener l) {
            synchronized (stdDataOutput) {
                for (Iterator it = stdDataOutput.iterator(); it.hasNext(); ) {
                    l.outputData((String[]) it.next());
                }
                if (stdDataOutputListeners != null) {
                    stdDataOutputListeners.add(l);
                }
            }
        }
        
        /**
         * Add the listener to the data error output of the command. This output may contain
         * a parsed information from its error output or some other data provided
         * by this command. If there are some data given to this listener, the command
         * is supposed to fail. The listeners are removed when the command finishes.
         */
        public synchronized void addDataErrorOutputListener(CommandDataOutputListener l) {
            synchronized (errDataOutputListeners) {
                for (Iterator it = errDataOutput.iterator(); it.hasNext(); ) {
                    l.outputData((String[]) it.next());
                }
                if (errDataOutputListeners != null) {
                    errDataOutputListeners.add(l);
                }
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

