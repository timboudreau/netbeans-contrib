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

package org.netbeans.modules.vcscore.cmdline;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.spi.vcs.commands.CommandTaskSupport;

/**
 * This class ias a wrapper of several <code>CommandTask</code>s.
 *
 * @author  Martin Entlicher
 */
public class WrappingCommandTask extends CommandTaskSupport {
    
    private UserCommandTask[] tasks;
    
    /** Creates a new instance of WrappingCommandTask */
    public WrappingCommandTask(UserCommandSupport cmdSupport, VcsDescribedCommand cmd) {//, List files) {
        super(cmdSupport, cmd);
        createSubTasks(cmdSupport, cmd);//, files);
    }
    
    private void createSubTasks(UserCommandSupport cmdSupport, VcsDescribedCommand cmd) {
        List tasksList = new ArrayList();
        for (VcsDescribedCommand c = cmd;
             c != null;
             c = (VcsDescribedCommand) c.getNextCommand()) {
            
            tasksList.add(new UserCommandTask(cmdSupport, c));
        }
        tasks = (UserCommandTask[]) tasksList.toArray(new UserCommandTask[tasksList.size()]);
    }
    
    /**
     * Run all wrapped tasks.
     */
    public void runTasks() {
        for (int i = 0; i < tasks.length; i++) {
            tasks[i].run();
        }
    }
    
    /**
     * Get all wrapped tasks.
     */
    public UserCommandTask[] getTasks() {
        UserCommandTask[] utasks = new UserCommandTask[tasks.length];
        System.arraycopy(tasks, 0, utasks, 0, tasks.length);
        return utasks;
    }
    
    /**
     * Wait for all wrapped tasks to finish.
     * @return the status of wrapped tasks. If one fails, this also fail.
     */
    public int waitForTasks() {
        int status = STATUS_SUCCEEDED;
        try {
            for (int i = 0; i < tasks.length; i++) {
                tasks[i].waitFinished(0);
                if (STATUS_SUCCEEDED != tasks[i].getExitStatus()) {
                    status = tasks[i].getExitStatus();
                }
            }
        } catch (InterruptedException intex) {
            for (int i = 0; i < tasks.length; i++) {
                if (!tasks[i].isFinished()) tasks[i].stop();
            }
            status = STATUS_INTERRUPTED;
        }
        return status;
    }
    
}
