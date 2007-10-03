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

    private final Exception origin;

    public CommandTaskInfo() {
        synchronized (CommandTaskInfo.class) {
            id = lastId++;
        }
        if (Boolean.getBoolean("netbeans.vcsdebug")) { // NOI18N
            origin = new Exception("Allocation stack trace");  // NOI18N
        } else {
            origin = null;
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
    
    protected abstract void cancel();
    
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

    /** In debug mode returns allocation stack trace. */
    public final Exception getOrigin() {
        assert Boolean.getBoolean("netbeans.vcsdebug");  // NOI18N
        return origin;
    }
}
