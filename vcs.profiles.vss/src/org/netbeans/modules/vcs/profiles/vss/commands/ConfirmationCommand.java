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

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.commands.RegexErrorListener;
import org.netbeans.modules.vcscore.commands.RegexOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.spi.vcs.commands.CommandSupport;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

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
 * <li><b>-m[EOM]</b> - handle multiple messages for multiple files.
 *                      EOM - optional "End Of Message".
 *                      The messages have to contain the appropriate file so that
 *                      the real commmand can run on the specfic files.
 *                      This options implies -t option as well.</li>
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
    public static final String MULTI_FILES = "-m"; // NOI18N
    public static final String VAR_TEST_CONFIRMATION = "TEST_CONFIRMATION"; // NOI18N
    
    private VcsFileSystem fileSystem;
    private CommandExecutionContext executionContext;
    private boolean errorOutput;
    private boolean testOnly;
    private boolean multiFiles;
    private boolean defineTestVar;
    private String rootDir;
    
    /** Creates a new instance of ConfirmationCommand */
    public ConfirmationCommand() {
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    public void setExecutionContext(CommandExecutionContext executionContext) {
        this.executionContext = executionContext;
        if (executionContext instanceof VcsFileSystem) {
            fileSystem = (VcsFileSystem) executionContext;
        }
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
                "-m[EOM] to handle command that acts on multiple files, EOM - optional End Of Message,\n"+    // NOI18N
                "-t not to run the real command if test does not produce output.\n"); // NOI18N
            return true;
        }
        errorOutput = false;
        testOnly = false;
        multiFiles = false;
        String endOfMessage = null;
        if (fileSystem != null) {
            rootDir = fileSystem.getRootDirectory().getAbsolutePath();
        } else {
            rootDir = (String) vars.get("ROOTDIR");
        }
        boolean moreOptions;
        do {
            moreOptions = false;
            if (GRAB_ERROR_OUTPUT.equals(args[0])) {
                moreOptions = true;
                errorOutput = true;
            } else if (TEST_ONLY_WHEN_NO_OUTPUT.equals(args[0])) {
                moreOptions = true;
                testOnly = true;
            } else if (args[0].startsWith(MULTI_FILES)) {
                moreOptions = true;
                multiFiles = true;
                if (args[0].length() > MULTI_FILES.length()) {
                    endOfMessage = args[0].substring(MULTI_FILES.length());
                }
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
        CommandSupport testCommand = executionContext.getCommandSupport(testCommandName);
        CommandSupport realCommand = executionContext.getCommandSupport(realCommandName);
        //System.out.println("ConfirmationCommand: errorOutput = "+errorOutput+", testOnly = "+testOnly+", multiFiles = "+multiFiles+", EOM = "+endOfMessage+", testCommandName = "+testCommandName+", realCommandName = "+realCommandName);
        
        Hashtable testVars = new Hashtable(vars);
        if (defineTestVar) {
            testVars.put(VAR_TEST_CONFIRMATION, "true"); // NOI18N
        }
        boolean confirmed;
        boolean success = true;
        if (multiFiles) {
            String[] messages;
            try {
                messages = runTestMultiCommand(testCommand, testVars, endOfMessage);
            } catch (InterruptedException iexc) {
                return false;
            }
            if (messages == null) {
                return false;
            }
            messages = confirm(messages, rootDir);
            confirmed = messages != null;
            if (confirmed && (!testOnly || messages.length > 0)) {
                success = runRealMultiCommand(realCommand, new Hashtable(vars), messages);
            }
        } else {
            String message;
            try {
                message = runTestCommand(testCommand, testVars);
            } catch (InterruptedException iexc) {
                return false;
            }
            if (message == null) {
                return false;
            }
            confirmed = confirm(message);
            if (confirmed && (!testOnly || message.length() > 0)) {
                success = runRealCommand(realCommand, new Hashtable(vars));
            }
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
    private String runTestCommand(CommandSupport cmdSupp, Hashtable vars) throws InterruptedException {
        final StringBuffer messageBuff = new StringBuffer();
        Command cmd = cmdSupp.createCommand();
        VcsDescribedCommand vcmd = (VcsDescribedCommand) cmd;
        vcmd.setAdditionalVariables(vars);
        RegexErrorListener regexOutputListener = new RegexErrorListener() {
            public void outputMatchedGroups(String[] elements) {
		if (elements != null && elements.length > 0) {
                    messageBuff.append(elements[0]);
                }
            }
        };
        if (errorOutput) {
            vcmd.addRegexErrorListener(regexOutputListener);
        } else {
            vcmd.addRegexOutputListener(regexOutputListener);
        }
        CommandTask task = cmd.execute();
        try {
            task.waitFinished(0);
        } catch (InterruptedException iexc) {
            task.stop();
            throw iexc;
        }
        if (task.getExitStatus() != CommandTask.STATUS_SUCCEEDED &&
            !VcsCommandIO.getBooleanProperty(vcmd.getVcsCommand(), VcsCommand.PROPERTY_IGNORE_FAIL)) {
            //E.err("exec failed "+ec.getExitStatus()); // NOI18N
            return null;
        }
        return messageBuff.toString();
    }
    
    /**
     * @return null when command failed, the output messages otherwise.
     */
    private String[] runTestMultiCommand(CommandSupport cmdSupp, Hashtable vars, final String eom) throws InterruptedException {
        final List messages = new ArrayList();
        final StringBuffer messageBuff = new StringBuffer();
        Command cmd = cmdSupp.createCommand();
        VcsDescribedCommand vcmd = (VcsDescribedCommand) cmd;
        vcmd.setAdditionalVariables(vars);
        RegexErrorListener regexOutputListener = new RegexErrorListener() {
            public void outputMatchedGroups(String[] elements) {
                if (elements != null && elements.length > 0) {
                    if (messageBuff.length() > 0) messageBuff.append(' ');
                    messageBuff.append(elements[0]);
                    if (eom == null || elements[0].endsWith(eom)) {
                        if (eom != null) {
                            messageBuff.delete(messageBuff.length() - eom.length(),  messageBuff.length());
                        }
                        messages.add(messageBuff.toString());
                        messageBuff.delete(0, messageBuff.length());
                    }
                }
            }
        };
        if (errorOutput) {
            vcmd.addRegexErrorListener(regexOutputListener);
        } else {
            vcmd.addRegexOutputListener(regexOutputListener);
        }
        CommandTask task = cmd.execute();
        try {
            task.waitFinished(0);
        } catch (InterruptedException iexc) {
            task.stop();
            throw iexc;
        }
        if (task.getExitStatus() != CommandTask.STATUS_SUCCEEDED &&
            !VcsCommandIO.getBooleanProperty(vcmd.getVcsCommand(), VcsCommand.PROPERTY_IGNORE_FAIL)) {
            //E.err("exec failed "+ec.getExitStatus()); // NOI18N
            return null;
        }
        return (String[]) messages.toArray(new String[0]);
    }
    
    private boolean runRealCommand(CommandSupport cmdSupp, Hashtable vars) {
        Command cmd = cmdSupp.createCommand();
        VcsDescribedCommand vcmd = (VcsDescribedCommand) cmd;
        vcmd.setAdditionalVariables(vars);
        CommandTask task = cmd.execute();
        try {
            task.waitFinished(0);
        } catch (InterruptedException iexc) {
            task.stop();
            return false;
        }
        return (task.getExitStatus() == CommandTask.STATUS_SUCCEEDED) ||
               VcsCommandIO.getBooleanProperty(vcmd.getVcsCommand(), VcsCommand.PROPERTY_IGNORE_FAIL);
    }
    
    private boolean runRealMultiCommand(CommandSupport cmdSupp, Hashtable vars, String[] messages) {
        Command command = cmdSupp.createCommand();
        VcsDescribedCommand vcmd = (VcsDescribedCommand) command;
        vcmd.setAdditionalVariables(vars);
        boolean haveFiles = setFiles(command, messages);
        if (!haveFiles) return true;
        CommandTask task = command.execute();
        try {
            CommandProcessor.getInstance().waitToFinish(task);
        } catch (InterruptedException iexc) {
            CommandProcessor.getInstance().kill(task);
            return false;
        }
        return (task.getExitStatus() == CommandTask.STATUS_SUCCEEDED) ||
               VcsCommandIO.getBooleanProperty(vcmd.getVcsCommand(), VcsCommand.PROPERTY_IGNORE_FAIL);
    }
    
    private boolean setFiles(Command command, String[] messages) {
        List fileObjects = new ArrayList();
        List files = new ArrayList();
        int rdl = rootDir.length();
        for (int i = 0; i < messages.length; i++) {
            int begin = messages[i].indexOf(rootDir);
            if (begin >= 0) {
                String file = retrieveFile(messages[i].substring(begin), getEndChar(messages[i], begin));
                String path = file.substring(rdl).replace(File.separatorChar, '/');
                while (path.startsWith("/")) path = path.substring(1);
                FileObject fo = (fileSystem != null) ? fileSystem.findResource(path) : null;
                if (fo != null) {
                    fileObjects.add(fo);
                } else {
                    files.add(new File(file));
                }
            }
        }
        if (fileObjects.size() > 0) {
            command.setFiles((FileObject[]) fileObjects.toArray(new FileObject[0]));
        }
        if (files.size() > 0) {
            ((VcsDescribedCommand) command).setDiskFiles((File[]) files.toArray(new File[0]));
        }
        return (fileObjects.size() > 0 || files.size() > 0);
    }
    
    private static String retrieveFile(String message, char endChar) {
        int l = message.length();
        int end = message.indexOf(' ');
        if (end < 0) end = l;
        else {
            if (end > 0 && message.charAt(end - 1) == endChar) {
                return message.substring(0, end - 1);
            }
        }
        String file = message.substring(0, end);
        while (end < l && !(new File(file).exists())) {
            end = message.indexOf(' ', end + 1);
            if (end < 0) end = l;
            else {
                if (end > 0 && message.charAt(end - 1) == endChar) {
                    return message.substring(0, end - 1);
                }
            }
            file = message.substring(0, end);
        }
        // Take the first space if the file was not found
        if (end >= l && !(new File(file).exists())) {
            end = message.indexOf(' ');
            if (end < 0) end = l;
            file = message.substring(0, end);
        }
        return file;
    }
    
    private static char getEndChar(String message, int pos) {
        if (pos > 0) {
            char c = message.charAt(pos - 1);
            if (c == '"' || c == '\'') return c;
        }
        return (char) 0;
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
    
    private static String[] confirm(String[] messages, String rootDir) {
        if (messages.length == 0) return messages;
        List confirmedMessages = new ArrayList();
        List confirmedPatters = new ArrayList();
        List deniedPatters = new ArrayList();
        Object yesAllOption = NbBundle.getMessage(ConfirmationCommand.class, "CTL_YesAll");
        Object noAllOption = NbBundle.getMessage(ConfirmationCommand.class, "CTL_NoAll");
        for (int i = 0; i < messages.length; i++) {
            if (checkPatterns(confirmedPatters, messages[i])) {
                confirmedMessages.add(messages[i]);
                continue;
            }
            if (checkPatterns(deniedPatters, messages[i])) {
                continue;
            }
            NotifyDescriptor confirmation = new NotifyDescriptor(messages[i],
                NbBundle.getMessage(ConfirmationCommand.class, "TTL_Confirmation"),
                NotifyDescriptor.YES_NO_CANCEL_OPTION,
                NotifyDescriptor.QUESTION_MESSAGE,
                new Object[] { NotifyDescriptor.YES_OPTION, yesAllOption,
                               NotifyDescriptor.NO_OPTION, noAllOption,
                               NotifyDescriptor.CANCEL_OPTION },
                NotifyDescriptor.YES_OPTION);
            Object result = DialogDisplayer.getDefault().notify(confirmation);
            if (NotifyDescriptor.YES_OPTION.equals(result)) {
                confirmedMessages.add(messages[i]);
            } else if (yesAllOption.equals(result)) {
                confirmedMessages.add(messages[i]);
                addPattern(confirmedPatters, messages[i], rootDir);
            } else if (NotifyDescriptor.NO_OPTION.equals(result)) {
                continue;
            } else if (noAllOption.equals(result)) {
                addPattern(deniedPatters, messages[i], rootDir);
            } else {
                // canceled
                return null;
            }
        }
        return (String[]) confirmedMessages.toArray(new String[0]);
    }
    
    /**
     * Add a pattern, that will match the provided message type.
     */
    private static void addPattern(List patterns, String message, String rootDir) {
        ArrayList patternItems = new ArrayList();
        int begin = 0;
        int fileIndex;
        while((fileIndex = message.indexOf(rootDir, begin)) >= 0) {
            String file = retrieveFile(message.substring(fileIndex), getEndChar(message, fileIndex));
            patternItems.add(message.substring(begin, fileIndex));
            begin = fileIndex + file.length();
        }
        if (begin < message.length()) {
            patternItems.add(message.substring(begin));
        } else {
            patternItems.add("");
        }
        patterns.add(patternItems.toArray(new String[0]));
    }
    
    /**
     * Check whether the message match at least one of the patterns.
     */
    private static boolean checkPatterns(List patterns, String message) {
        int n = patterns.size();
        for (int i = 0; i < n; i++) {
            String[] pattern = (String[]) patterns.get(i);
            if (!message.startsWith(pattern[0])) continue;
            int l = pattern.length - 1;
            if (!message.endsWith(pattern[l])) continue;
            for (l-- ; l > 0; l--) {
                if (message.indexOf(pattern[l]) < 0) break;
            }
            if (l == 0) return true;
        }
        return false;
    }
    
}
