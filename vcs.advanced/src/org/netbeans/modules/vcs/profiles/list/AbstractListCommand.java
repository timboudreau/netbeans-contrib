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

package org.netbeans.modules.vcs.profiles.list;

import java.util.Hashtable;
import java.io.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.util.*;
//import org.netbeans.modules.vcscore.cmdline.exec.*;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsListCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommand;

/**
 * This class implements the most used methods of list commands for different VCS filesystems.
 *
 * @author  Martin Entlicher
 */
public abstract class AbstractListCommand extends VcsListCommand implements CommandDataOutputListener {

    //protected String[] args=null;
    protected Hashtable filesByName=null;

    protected CommandOutputListener stdoutNRListener = null;
    protected CommandOutputListener stderrNRListener = null;
    protected CommandDataOutputListener stdoutListener = null;
    protected CommandDataOutputListener stderrListener = null;

    protected String dataRegex = null;
    protected String errorRegex = null;
    protected String input = null;
    //protected long timeout = 0;
    private VcsFileSystem fileSystem = null;

    protected boolean shouldFail=false;
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * Initialize <code>DIR</code> variable and copy some values from <code>vars</code>
     * to local variables.
     * @param vars the variables passed from the VCS filesystem
     */
    protected void initVars(Hashtable vars) {
        String dataRegex = (String) vars.get("DATAREGEX");
        if (dataRegex != null) this.dataRegex = dataRegex;
        String errorRegex = (String) vars.get("ERRORREGEX");
        if (errorRegex != null) this.errorRegex = errorRegex;
        this.input = (String) vars.get("INPUT");
        if (this.input == null) this.input = "";
        //this.timeout = ((Long) vars.get("TIMEOUT")).longValue();
    }

    /**
     * Run the LIST command given in the <code>args</code> array.
     * @param vars the variables passed from the VCS filesystem
     * @param args the LIST command to execute
     * @param addErrOut whether to add error output to the output listener
     */
    protected void runCommand(Hashtable vars, String[] args, boolean addErrOut) throws InterruptedException {
        String cmdStr = array2string(args);
        String prepared = Variables.expand(vars,cmdStr, true);

        /*
        if (stderrListener != null) {
            String[] command = { "LIST: "+prepared };
            stderrListener.match(command);
        }
        if (stderrNRListener != null) stderrNRListener.match("LIST: "+prepared);
         */
        UserCommand cmd = new UserCommand();
        cmd.setName("LIST_COMMAND");
        cmd.setDisplayName(null);
        //cmd.setDisplayName("Refresh Support");
        cmd.setProperty(VcsCommand.PROPERTY_EXEC, prepared);
        cmd.setProperty(UserCommand.PROPERTY_DATA_REGEX, dataRegex);
        cmd.setProperty(UserCommand.PROPERTY_ERROR_REGEX, errorRegex);
        // The user should be warned by the wrapper class and not the command itself.
        cmd.setProperty(VcsCommand.PROPERTY_IGNORE_FAIL, Boolean.TRUE);
        VcsCommandExecutor ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        ec.addDataOutputListener(this);
        if (addErrOut) ec.addDataErrorOutputListener(this);
        ec.addOutputListener(stdoutNRListener);
        ec.addErrorOutputListener(stderrNRListener);
        fileSystem.getCommandsPool().startExecutor(ec, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(ec);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(ec);
            throw iexc;
        }
        if (ec.getExitStatus() != VcsCommandExecutor.SUCCEEDED) {
            //E.err("exec failed "+ec.getExitStatus());
            shouldFail=true;
        }
    }

    /**
     * Run the LIST command given by its name.
     * @param vars the variables passed from the VCS filesystem
     * @param cmdName the LIST command to execute
     * @param addErrOut whether to add error output to the output listener
     */
    protected void runCommand(Hashtable vars, String cmdName, boolean addErrOut) throws InterruptedException {
        runCommand(vars, cmdName, this, (addErrOut) ? this : null);
    }

    /**
     * Run the LIST command given by its name.
     * @param vars the variables passed from the VCS filesystem
     * @param cmdName the LIST command to execute
     * @param addErrOut whether to add error output to the output listener
     */
    protected void runCommand(Hashtable vars, String cmdName, CommandDataOutputListener dataOutputListener,
                              CommandDataOutputListener errorOutputListener) throws InterruptedException {
        VcsCommand cmd = fileSystem.getCommand(cmdName);
        if (cmd == null) return ;
        VcsCommandExecutor ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        if (dataOutputListener != null) ec.addDataOutputListener(dataOutputListener);
        if (errorOutputListener != null) ec.addDataErrorOutputListener(errorOutputListener);
        ec.addOutputListener(stdoutNRListener);
        ec.addErrorOutputListener(stderrNRListener);
        fileSystem.getCommandsPool().preprocessCommand(ec, vars, fileSystem);
        fileSystem.getCommandsPool().startExecutor(ec, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(ec);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(ec);
            throw iexc;
        }
        if (ec.getExitStatus() != VcsCommandExecutor.SUCCEEDED) {
            //E.err("exec failed "+ec.getExitStatus());
            shouldFail=true;
        }
    }

    /**
     * List files of the VCS Repository.
     * @param vars Variables used by the command
     * @param args Command-line arguments
     * filesByName listing of files with statuses
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     */
    public abstract boolean list(Hashtable vars, String[] args, Hashtable filesByName,
                                 CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                                 CommandDataOutputListener stdoutListener, String dataRegex,
                                 CommandDataOutputListener stderrListener, String errorRegex);
    /**
     * Match the command output.
     * @param elements the string line read from the command output.
     */
    public abstract void outputData(String[] elements);
}