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
