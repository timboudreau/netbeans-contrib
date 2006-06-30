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

package org.netbeans.api.vcs.commands;

/**
 *
 * @author  Martin Entlicher
 */
class CommandTaskInfoImpl extends org.netbeans.modules.vcscore.commands.CommandTaskInfo {

    private CommandTask task;

    CommandTaskInfoImpl(CommandTask task) {
        this.task = task;
    }

    public CommandTask getTask() {
        return task;
    }

    public boolean canRun() {
        return task.canExecute();
    }

    /**
     * Execute the task.
     */
    public void run() {
        task.runCommandTask();
    }

    protected void cancel() {
        task.cancel();
    }
    
}
