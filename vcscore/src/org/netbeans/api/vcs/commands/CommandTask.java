/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.vcs.commands;

//import java.io.InputStream;
//import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.List;

import org.openide.filesystems.FileObject;
import org.openide.util.Task;

import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.commands.CommandTaskInfo;

/**
 * This class represents the actual command, that is executed to acually perform a VCS action.
 *
 * @author  Martin Entlicher
 */
public abstract class CommandTask extends Task {
    
    public static final int STATUS_NOT_STARTED = -2;
    public static final int STATUS_RUNNING = -1;
    public static final int STATUS_SUCCEEDED = 0;
    public static final int STATUS_FAILED = 1;
    public static final int STATUS_INTERRUPTED = 2;
    
    private volatile int status = STATUS_NOT_STARTED;
    
    private final Object notifyLock = new Object();
    private CommandTaskInfo taskInfo;
    
    //private EventListenerList outputListeners = new EventListenerList();
    /**
     * Get the name of the command.
     */
    public abstract String getName();
    
    /**
     * Get the display name of the command. It will be visible on the popup menu under this name.
     * When <code>null</code>, the command will not be visible on the popup menu.
     */
    public abstract String getDisplayName();
    
    /**
     * Get the command associated with this task.
     *
     // The command can change it's properties right after execute().
     // It has no sense to get the command instance then.
     // The Command would have to be clonned if this method is required.
    public abstract Command getCommand();
     */
    
    /**
     * Get files this task acts on.
     */
    public abstract FileObject[] getFiles();
    
    //protected abstract int preprocess();
    
    /**
     * Tell, whether the task can be executed now. The task may wish to aviod parallel
     * execution with other tasks or other events.
     * @return <code>true</code> if the task is to be executed immediately. This is the
     *                           default implementation.
     *         <code>false</code> if the task should not be executed at this time.
     *                            In this case the method will be called later to check
     *                            whether the task can be executed already.
     */
    protected boolean canExecute() {
        return true;
    }
    
    /**
     * Schedules the command task for being executed. The actual execution
     * might be done asynchronously. In this case this method returns immediately.
     */
    public final void run() {
        taskInfo = new CommandTaskInfoImpl(this);
        taskInfo.register();
        //throw new UnsupportedOperationException("Do not execute the task by this method. Put it into the CommandProcessor instead.");
    }
    
    final void runCommandTask() {
        status = STATUS_RUNNING;
        notifyRunning();
        try {
            status = execute();
        } finally {
            if (taskInfo.isInterrupted()) {
                status = STATUS_INTERRUPTED;
            } else {
                if (status == STATUS_RUNNING) {
                    status = STATUS_FAILED;
                }
            }
            notifyFinished();
            synchronized (notifyLock) {
                notifyLock.notifyAll();
            }
        }
        // Free all listeners after the command finished.
        //outputListeners = null;
    }
    
    /**
     * Put the actual execution of this task here.
     * This method will be called automatically after process() call. Do NOT call this
     * method.
     * @return The exit status. One of STATUS_* constants.
     */
    protected abstract int execute();
    
    /**
     * Say whether the command is running just now.
     * @return True if the command is running, false if not.
     */
    public final boolean isRunning() {
        return STATUS_RUNNING == status;
    }
    
    /**
     * Stop the command's execution. The default implementation kills
     * the command's thread by hard.
     */
    public void stop() {
        killHard();
    }
    
    /**
     * Kill the command's thread by hard.
     */
    final void killHard() {
        CommandProcessor.getInstance().kill(this);
    }
    
    /**
     * Get the exit status of the command.
     */
    public final int getExitStatus() {
        return status;
    }
    
    /**
     * Wait for the task to finish while allowing interruption. Because the
     * method Task.waitFinished() ignores InterruptedException, this method was
     * created. If the current thread is interrupted while waiting for the task
     * to finish, the InterruptedException is thrown.
     * @param timeout The maximum time to wait in milliseconds. 
     * @throws InterruptedException when the current thread is interrupted while
     * waiting.
     */
    public void waitFinished(int timeout) throws InterruptedException {
        synchronized (notifyLock) {
            if (!isFinished()) {
                notifyLock.wait(timeout);
            }
        }
    }
    
}

