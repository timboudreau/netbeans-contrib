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

    protected Debug E=new Debug("AbstractListCommand",true);
    protected Debug D=E;

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
        D.deb("dataRegex = "+dataRegex+", errorRegex = "+errorRegex);
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
        D.deb("prepared = "+prepared);
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