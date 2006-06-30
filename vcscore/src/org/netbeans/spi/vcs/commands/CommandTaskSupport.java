/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.spi.vcs.commands;

import org.openide.filesystems.FileObject;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

/**
 * The supported command task. This task delegates all methods to it's
 * command support.
 *
 * @author  Martin Entlicher
 */
public class CommandTaskSupport extends CommandTask {

    private CommandSupport cmdSupport;
    private Command cmd;

    /**
     * Creates a new instance of CommandTaskSupport.
     * @param cmdSupport the CommandSupport instance, that created this task.
     * @param cmd The copy of customized command, that will not change any more.
     */
    public CommandTaskSupport(CommandSupport cmdSupport, Command cmd) {
        this.cmdSupport = cmdSupport;
        this.cmd = cmd;
    }

    public final Command getCommand() {
        return cmd;
    }
    
    /**
     * Get the name of the command.
     */
    public String getName() {
        return cmdSupport.getName();
    }

    /**
     * Get the display name of the command. It will be visible on the popup menu under this name.
     * When <code>null</code>, the command will not be visible on the popup menu.
     */
    public String getDisplayName() {
        return cmdSupport.getDisplayName();
    }

    /**
     * Get files this task acts on.
     */
    public FileObject[] getFiles() {
        return cmd.getFiles();
    }

    /**
     * Put the actual execution of this task here.
     * This method will be called automatically after process() call. Do NOT call this
     * method.
     */
    protected int execute() {
        return cmdSupport.execute(this);
    }

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
        return cmdSupport.canExecute(this);
    }

    /**
     * Stop the command's execution. The default implementation kills
     * the command's thread by hard.
     */
    public void stop() {
        cmdSupport.stop(this);
        //killHard();
    }

    void killMeHard() {
        super.stop();
    }

}
