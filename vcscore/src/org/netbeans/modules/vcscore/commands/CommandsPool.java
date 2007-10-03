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

import java.util.*;

import org.openide.util.WeakSet;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;

/**
 * This class was used as a container of all external commands which are either
 * running or finished. Currently it's reimplemented as a bridge between the
 * old internal "API" and the new VCS APIs.
 * @deprecated Use {@link CommandProcessor} instead. This class is kept just for
 *             compatibility reasons. It delegates it's work to CommandProcessor.
 *
 * @author  Martin Entlicher
 */
public class CommandsPool extends Object {

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
    
    private static CommandsPool instance = null;
    
    /** Contains executors, that were cancelled and should not be executed.
     * All resources (command wrapper) were released for these executors. */
    private WeakSet cancelledCommandExecutors;

    /** Creates new CommandsPool */
    private CommandsPool() {
        cancelledCommandExecutors = new WeakSet();
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
    }
    
    /**
     * Get the command's ID. It's a unique command identification number.
     * @param vce the command's executor
     * @return the ID or -1 if the command does not have one.
     */
    public long getCommandID(VcsCommandExecutor vce) {
        if (!(vce instanceof ExecuteCommand)) return -1;
        CommandTask task = ((ExecuteCommand) vce).getTask();
        return CommandProcessor.getInstance().getTaskID(task);
    }

    /**
     * Perform preprocessing of a new command. It will perform any needed input
     * and update the execution string.
     * @param vce the command to preprocess
     * @return the preprocessing status, one of <code>CommandExecutorSupport.PREPROCESS_*</code> constants
     */
    public int preprocessCommand(VcsCommandExecutor vce, Hashtable vars,
                                 VcsFileSystem fileSystem) {
        //return preprocessCommand(vce, vars, fileSystem, null);
        if (vce instanceof ExecuteCommand) {
            Command cmd = ((ExecuteCommand) vce).getDescribedCommand();
            return VcsManager.getDefault().showCustomizer(cmd) ? PREPROCESS_DONE : PREPROCESS_CANCELLED;
        }
        return PREPROCESS_DONE;
    }

    /**
     * Start the executor. The method starts the executor in a separate thread.
     * @param vce the executor
     */
    public void startExecutor(final VcsCommandExecutor vce) {
        startExecutor(vce, null);
    }
    
    /**
     * Start the executor. The method starts the executor in a separate thread.
     * @param vce the executor
     * @param fileSystem the file system associated with the command. Can be <code>null</code>.
     */
    public void startExecutor(final VcsCommandExecutor vce,
                              final VcsFileSystem fileSystem) {
        // Do not start for cancelled executors !!
        if (cancelledCommandExecutors.contains(vce)) return ;
        if (!(vce instanceof ExecuteCommand)) return ;
        Command cmd = ((ExecuteCommand) vce).getDescribedCommand();
        cmd.execute();
    }
    
    /**
     * Tells whether the executor is waiting. It can either wait till preprocessing
     * finishes or till other commands which can not run in parallel with it finish.
     * @param vce the executor
     */
    public boolean isWaiting(VcsCommandExecutor vce) {
        if (!(vce instanceof ExecuteCommand)) return false;
        CommandTask task = ((ExecuteCommand) vce).getTask();
        return !task.isFinished() && !task.isRunning();
    }
    
    /**
     * Tells whether the executor is still running.
     * @param vce the executor
     */
    public boolean isRunning(VcsCommandExecutor vce) {
        if (!(vce instanceof ExecuteCommand)) return false;
        CommandTask task = ((ExecuteCommand) vce).getTask();
        return !task.isFinished();
    }

    /**
     * Wait to finish the executor.
     * This methods blocks the current thread untill the executor finishes.
     * This method ignores interrupts.
     * @param vce the executor
     */
    public void waitToFinish(VcsCommandExecutor vce) throws InterruptedException {
        if (!(vce instanceof ExecuteCommand)) return ;
        CommandTask task = ((ExecuteCommand) vce).getTask();
        task.waitFinished(0);
    }
    
    /**
     * Kill the executor if it is running. It tries to interrupt it, it is up to
     * executor implementation if it will terminate or not.
     */
    public void kill(VcsCommandExecutor vce) {
        if (!(vce instanceof ExecuteCommand)) return ;
        CommandTask task = ((ExecuteCommand) vce).getTask();
        task.stop();
    }
    
}

