/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import org.netbeans.api.vcs.commands.CommandTask;

/**
 * Info, that is used to submit a task into CommandProcessor.
 *
 * @author  Martin Entlicher
 */
public abstract class CommandTaskInfo {
    
    private static long lastId = 0;
    
    private long id;
    private Reference submittingInfo;
    private Thread runningThread;
    private boolean interrupted = false;
    private long startTime = 0;
    private long finishTime = 0;
    
    public CommandTaskInfo() {
        synchronized (CommandTaskInfo.class) {
            id = lastId++;
        }
    }
    
    /**
     * Whether the task can be executed right now.
     */
    public abstract boolean canRun();
        
    /**
     * Execute the task.
     */
    public abstract void run();
    
    /**
     * Get the associated task.
     * @return The command task.
     */
    public abstract CommandTask getTask();
    
    /**
     * Register this task info in CommandProcessor.
     */
    public final void register() {
        CommandProcessor.getInstance().process(this);
    }
    
    public final long getCommandID() {
        return id;
    }
    
    void setSubmittingInfo(CommandTaskInfo submittingInfo) {
        this.submittingInfo = new WeakReference(submittingInfo);
    }
    
    CommandTaskInfo getSubmittingInfo() {
        return (CommandTaskInfo) submittingInfo.get();
    }
    
    void setRunningThread(Thread thread) {
        this.runningThread = thread;
    }
    
    Thread getRunningThread() {
        return runningThread;
    }
    
    void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }
    
    public boolean isInterrupted() {
        return interrupted;
    }
    
    void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public final long getStartTime() {
        return startTime;
    }
    
    void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }
    
    public final long getFinishTime() {
        return finishTime;
    }
    
    public final long getExecutionTime() {
        if (startTime != 0 && finishTime != 0) {
            return finishTime - startTime;
        } else {
            return 0;
        }
    }
}
