/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.vss.commands;

import java.util.Hashtable;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Confirmation command is used for commands, that need a "test" command
 * to obtain a confirmation message before the actual command can be executed.<p>
 * A test command will be executed first. If there is some output produced,
 * it will be presented to the user for confirmation.
 * If confirmed, the real command will be executed.<p>
 * 
 * <code>ConfirmationCommand [options] &lt;command name&gt; [&lt;command name&gt;]</code>
 * <br>
 * Possible options are:
 * <ul>
 * <li><b>-e</b> - grab the question from the error data output (instead of
 *                 standard data output that is used by default)</li>
 * <li><b>-t</b> - in case that the test command does not provide any output,
 *                 do not run the real command</li>
 * </ul>
 * <p>
 * When only one command name is provided:<br>
 *     Run the provided command as a test command (variable TEST_CONFIRMATION
 *     is defined as "true"). If there is any output, it's presented to the user
 *     and the same command (variable TEST_CONFIRMATION is not defined) is executed
 *     if the user confirms the message.
 * <p>
 * When two command names are provided:<br>
 *     Run the first command (which is considered as the test command).
 *     If there is any output, it's presented to the user and the second command
 *     is executed if the user confirms the message.
 * <p>
 * Variable TEST_CONFIRMATION is defined if and only if only one command name
 * is provided.
 *
 * @author  Martin Entlicher
 */
public class ConfirmationCommand extends Object implements VcsAdditionalCommand {
    
    public static final String GRAB_ERROR_OUTPUT = "-e"; // NOI18N
    public static final String TEST_ONLY_WHEN_NO_OUTPUT = "-t"; // NOI18N
    public static final String VAR_TEST_CONFIRMATION = "TEST_CONFIRMATION"; // NOI18N
    
    private VcsFileSystem fileSystem;
    boolean errorOutput;
    boolean testOnly;
    boolean defineTestVar;
    
    /** Creates a new instance of ConfirmationCommand */
    public ConfirmationCommand() {
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    /**
     * Executes the confirmation command.
     * @param vars variables needed to run cvs commands
     * @param args the arguments,
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull,
     *         false if some error has occured.
     */
    public boolean exec(final Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        if (args.length == 0 || (GRAB_ERROR_OUTPUT.equals(args[0]) || TEST_ONLY_WHEN_NO_OUTPUT.equals(args[0])) && args.length <= 1) {
            stderrNRListener.outputLine("Expecting a command as an argument.\n"+      // NOI18N
                "ConfirmationCommand [-e] [-t] <command name> [<command name>]\n"+    // NOI18N
                "-e to grab error data output instead of standard data output,\n"+    // NOI18N
                "-t not to run the real command if test does not produce output.\n"); // NOI18N
            return true;
        }
        errorOutput = false;
        testOnly = false;
        boolean moreOptions;
        do {
            moreOptions = false;
            if (GRAB_ERROR_OUTPUT.equals(args[0])) {
                moreOptions = true;
                errorOutput = true;
            } else if (TEST_ONLY_WHEN_NO_OUTPUT.equals(args[0])) {
                moreOptions = true;
                testOnly = true;
            }
            if (moreOptions) {
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                args = newArgs;
            }
        } while (moreOptions);
        defineTestVar = (args.length == 1);
        String testCommandName = args[0];
        String realCommandName = (args.length > 1) ? args[1] : args[0];
        VcsCommand testCommand = fileSystem.getCommand(testCommandName);
        VcsCommand realCommand = fileSystem.getCommand(realCommandName);
        //System.out.println("ConfirmationCommand: errorOutput = "+errorOutput+", testOnly = "+testOnly+", testCommandName = "+testCommandName+", realCommandName = "+realCommandName);
        
        Hashtable testVars = new Hashtable(vars);
        if (defineTestVar) {
            testVars.put(VAR_TEST_CONFIRMATION, "true"); // NOI18N
        }
        String message;
        try {
            message = runTestCommand(testCommand, testVars);
        } catch (InterruptedException iexc) {
            return false;
        }
        if (message == null) {
            return false;
        }
        boolean confirmed = confirm(message);
        boolean success = true;
        if (confirmed && (!testOnly || message.length() > 0)) {
            success = runRealCommand(realCommand, new Hashtable(vars));
        }
        if (!confirmed) {
            // Disable the command notifications when the command is not confirmed:
            vars.put("COMMAND_NOTIFICATION_DISABLED", "true");
        }
        return success;
    }
    
    /**
     * @return null when command failed, the output message otherwise.
     */
    private String runTestCommand(VcsCommand cmd, Hashtable vars) throws InterruptedException {
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        final StringBuffer messageBuff = new StringBuffer();
        CommandDataOutputListener dataOutputListener = new CommandDataOutputListener() {
            public void outputData(String[] elements) {
                if (elements != null && elements.length > 0) {
                    messageBuff.append(elements[0]);
                }
            }
        };
        if (errorOutput) {
            vce.addDataErrorOutputListener(dataOutputListener);
        } else {
            vce.addDataOutputListener(dataOutputListener);
        }
        fileSystem.getCommandsPool().startExecutor(vce, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(vce);
            throw iexc;
        }
        if (vce.getExitStatus() != VcsCommandExecutor.SUCCEEDED &&
            !VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_IGNORE_FAIL)) {
            //E.err("exec failed "+ec.getExitStatus()); // NOI18N
            return null;
        }
        return messageBuff.toString();
    }
    
    private boolean runRealCommand(VcsCommand cmd, Hashtable vars) {
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        fileSystem.getCommandsPool().startExecutor(vce, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(vce);
            return false;
        }
        return (vce.getExitStatus() == VcsCommandExecutor.SUCCEEDED) ||
               VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_IGNORE_FAIL);
    }
    
    private static boolean confirm(String message) {
        if (message.length() == 0) return true;
        if (!NotifyDescriptor.Confirmation.YES_OPTION.equals (
                DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Confirmation (
                    message, NotifyDescriptor.Confirmation.YES_NO_OPTION)))) { // NOI18N
            return false;
        }
        return true;
    }
    
}
