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

import org.netbeans.modules.vcscore.util.Table;

/**
 * This class is used as a container of all external commands which are either running or finished.
 * @author  Martin Entlicher
 */
public class CommandsPool extends Object /*implements CommandListener */{

    /**
     * Contains instances of VcsCommandExecutor, which are to be run.
     */
    private ArrayList commandsToRun;
    /**
     * Contains pairs of instances of VcsCommandExecutor and threads in which are running.
     */
    private Table commands;
    /**
     * Contains finished instances of VcsCommandExecutor.
     */
    private ArrayList commandsFinished;
    
    private ThreadGroup group;
    
    /**
     * Whether to collect finished commands.
     */
    private boolean collectFinishedCmds = true;
    /**
     * Whether to collect standard output from commands.
     */
    private boolean collectStdOut = true;
    /**
     * Whether to collect error output from commands.
     */
    private boolean collectErrOut = true;
    /**
     * The maximun number of finished commands stored.
     */
    private int collectMax = 10;
    
    private ArrayList commandListeners = new ArrayList();

    /** Creates new CommandsPool */
    public CommandsPool() {
        commandsToRun = new ArrayList();
        commands = new Table();
        commandsFinished = new ArrayList();
        group = new ThreadGroup("VCS Commands Goup");
    }

    public void setCollectFinishedCmds(boolean collectFinishedCmds) {
        this.collectFinishedCmds = collectFinishedCmds;
    }
    
    public boolean isCollectFinishedCmds() {
        return collectFinishedCmds;
    }
    
    public void setCollectStdOut(boolean collectStdOut) {
        this.collectStdOut = collectStdOut;
    }
    
    public boolean isCollectStdOut() {
        return collectStdOut;
    }
    
    public void setCollectErrOut(boolean collectErrOut) {
        this.collectErrOut = collectErrOut;
    }
    
    public boolean isCollectErrOut() {
        return collectErrOut;
    }

    /**
     * Add a new command.
     * @param cmd The command to add
     */
    public synchronized void add(VcsCommandExecutor cmd) {
        commandsToRun.add(cmd);
    }
    
    private void commandStarted(VcsCommandExecutor vce) {
        TopManager.getDefault().setStatusText(g("MSG_Command_name_running", vce.getCommand().getDisplayName()));
        System.out.println("command "+vce.getCommand()+" STARTED.");
        synchronized (commandListeners) {
            for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
                ((CommandListener) it.next()).commandStarted(vce);
            }
        }
        System.out.println("command "+vce.getCommand()+" STARTED, LISTENERS DONE.");
    }
    
    private void commandDone(VcsCommandExecutor vce) {
        System.out.println("command "+vce.getCommand()+" DONE.");
        synchronized (this) {
            commands.remove(vce);
            commandsFinished.add(vce);
            notifyAll();
        }
        synchronized (commandListeners) {
            for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
                ((CommandListener) it.next()).commandDone(vce);
            }
        }
        System.out.println("command "+vce.getCommand()+" DONE, LISTENERS DONE.");
        int exit = vce.getExitStatus();
        String name = vce.getCommand().getDisplayName();
        String message = "";
        switch (exit) {
            case VcsCommandExecutor.SUCCEEDED:
                message = g("MSG_Command_name_finished", name);
                break;
            case VcsCommandExecutor.FAILED:
                message = g("MSG_Command_name_failed", name);
                break;
            case VcsCommandExecutor.INTERRUPTED:
                message = g("MSG_Command_name_interrupted", name);
                break;
        }
        TopManager.getDefault().setStatusText(message);
    }
    
    public void startExecutor(final VcsCommandExecutor vce) {
        final Thread t = new Thread(group, vce, "VCS Command Execution Thread");
        System.out.println("startExecutor("+vce.getCommand()+")");
        synchronized (this) {
            commandsToRun.remove(vce);
            commands.put(vce, t);
        }
        t.start();
        System.out.println("startExecutor, thread started.");
        commandStarted(vce);
        new Thread(group, "VCS Command Execution Waiter") {
            public void run() {
                System.out.println("startExecutor.Waiter: thread checking ...");
                while (t.isAlive()) {
                    System.out.println("startExecutor.Waiter: thread is Alive");
                    try {
                        t.join();
                    } catch (InterruptedException exc) {
                        // Ignore
                    }
                }
                commandDone(vce);
            }
        }.start();
        System.out.println("startExecutor, waiter started.");
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
    
    public synchronized boolean isRunning(VcsCommandExecutor vce) {
        return (commands.get(vce) != null);
    }
    
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
    public synchronized void waitToRun(VcsCommand cmd, String filePath) {
        boolean haveToWait = false;
        do {
            for(Enumeration enum = commands.keys(); enum.hasMoreElements(); ) {
                VcsCommandExecutor ec = (VcsCommandExecutor) enum.nextElement();
                VcsCommand uc = ec.getCommand();
            //int index = commands.indexOf(cmd);
            //if (index >= 0) {
            //    VcsCommandExecutor executor = (VcsCommandExecutor) commands.get(index);
                
            }
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
                Set execFiles = executor.getFiles();
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
     * Kill all running commands.
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
     * Kill the executor if it is running.
     */
    public synchronized void kill(VcsCommandExecutor vce) {
        Thread t = (Thread) commands.get(vce);
        if (t != null) t.interrupt();
    }
    
    /*
     * This method is called when the command is just started.
     *
    public synchronized void commandStarted(VcsCommandExecutor vce) {
        commandsToRun.remove(vce);
        commands.add(vce);
    }
    
    /*
     * This method is called when the command is done.
     *
    public synchronized void commandDone(VcsCommandExecutor vce) {
        if (isCollectFinishedCmds()) commandsFinished.add(vce);
        commands.remove(vce);
        notifyAll();
    }
     */

    public synchronized void addCommandListener(CommandListener listener) {
        commandListeners.add(listener);
    }
    
    public synchronized void removeCommandListener(CommandListener listener) {
        commandListeners.remove(listener);
    }
    
    private String g(String s) {
        return org.openide.util.NbBundle.getBundle(CommandsPool.class).getString(s);
    }
    
    private String  g(String s, Object obj) {
        return java.text.MessageFormat.format (g(s), new Object[] { obj });
    }

}

