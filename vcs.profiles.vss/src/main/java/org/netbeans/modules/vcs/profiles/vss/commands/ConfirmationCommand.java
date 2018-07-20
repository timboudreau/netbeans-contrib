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

package org.netbeans.modules.vcs.profiles.vss.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.commands.RegexErrorListener;
import org.netbeans.modules.vcscore.commands.RegexOutputListener;
import org.netbeans.modules.vcscore.commands.TextErrorListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.spi.vcs.commands.CommandSupport;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
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
    public static final String REPORT_ERROR_OF_TEST = "-te"; // NOI18N
    public static final String TEST_ONLY_WHEN_NO_OUTPUT = "-t"; // NOI18N
    public static final String MULTI_FILES = "-m"; // NOI18N
    public static final String VAR_TEST_CONFIRMATION = "TEST_CONFIRMATION"; // NOI18N
    
    private VcsFileSystem fileSystem;
    private CommandExecutionContext executionContext;
    private boolean errorOutput;
    private boolean testOnly;
    private boolean multiFiles;
    private boolean reportErrorTest;
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
                "ConfirmationCommand [-e] [-m[EOM]] [-t] [-te] <command name> [<command name>]\n"+    // NOI18N
                "-e to grab error data output instead of standard data output,\n"+    // NOI18N
                "-m[EOM] to handle command that acts on multiple files, EOM - optional End Of Message,\n"+    // NOI18N
                "-t not to run the real command if test does not produce output.\n"+ // NOI18N
                "-te to report error output of test command if it fails and provides no messages.\n");    // NOI18N
            return true;
        }
        errorOutput = false;
        testOnly = false;
        multiFiles = false;
        reportErrorTest = false;
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
            } else if (REPORT_ERROR_OF_TEST.equals(args[0])) {
                moreOptions = true;
                reportErrorTest = true;
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
        if (testCommand == null) {
            stderrNRListener.outputLine("Command '"+testCommandName+"' is not defined.");
            return false;
        }
        if (realCommand == null) {
            stderrNRListener.outputLine("Command '"+realCommandName+"' is not defined.");
            return false;
        }
        
        Hashtable testVars = new Hashtable(vars);
        if (defineTestVar) {
            testVars.put(VAR_TEST_CONFIRMATION, "true"); // NOI18N
        }
        boolean confirmed;
        boolean success = true;
        boolean[] testSuccess = new boolean[] { true };
        List errorOutputList = null;
        if (reportErrorTest) errorOutputList = new ArrayList();
        if (multiFiles) {
            String[] messages;
            try {
                messages = runTestMultiCommand(testCommand, testVars, endOfMessage,
                                               testSuccess, errorOutputList);
            } catch (InterruptedException iexc) {
                return false;
            }
            if (messages == null) {
                return false;
            }
            if (messages.length == 0 && !testSuccess[0] && reportErrorTest) {
                for (int i = 0; i < errorOutputList.size(); i++) {
                    stderrNRListener.outputLine((String) errorOutputList.get(i));
                }
                return false;
            }
            String[] confirmedFiles = confirm(messages, rootDir, getSelectedFiles(rootDir, vars));
            confirmed = confirmedFiles != null;
            if (confirmed && (!testOnly || confirmedFiles.length > 0)) {
                success = runRealMultiCommand(realCommand, new Hashtable(vars), confirmedFiles);
            }
        } else {
            String message;
            try {
                message = runTestCommand(testCommand, testVars, testSuccess, errorOutputList);
            } catch (InterruptedException iexc) {
                return false;
            }
            if (message == null) {
                return false;
            }
            if (message.length() == 0 && !testSuccess[0] && reportErrorTest) {
                for (int i = 0; i < errorOutputList.size(); i++) {
                    stderrNRListener.outputLine((String) errorOutputList.get(i));
                }
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
    
    private String[] getSelectedFiles(String rootDir, Hashtable vars) {
        if (fileSystem == null) {
            String root = (String) vars.get("ROOTDIR");
            return new String[] { root };
        }
        Collection filePaths = ExecuteCommand.createProcessingFiles(fileSystem, vars);
        String[] files = new String[filePaths.size()];
        int i = 0;
        for (Iterator it = filePaths.iterator(); it.hasNext(); i++) {
            files[i] = rootDir + File.separator + ((String) it.next()).replace('/', File.separatorChar);
        }
        return files;
    }
    
    /**
     * @return null when command failed, the output message otherwise.
     */
    private String runTestCommand(CommandSupport cmdSupp, Hashtable vars,
                                  boolean[] testSuccess, final List errorOutputList) throws InterruptedException {
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
        if (errorOutputList != null) {
            vcmd.addTextErrorListener(new TextErrorListener() {
                public void outputLine(String line) {
                    errorOutputList.add(line);
                }
            });
        }
        CommandTask task = cmd.execute();
        try {
            task.waitFinished(0);
        } catch (InterruptedException iexc) {
            task.stop();
            throw iexc;
        }
        testSuccess[0] = (task.getExitStatus() == CommandTask.STATUS_SUCCEEDED);
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
    private String[] runTestMultiCommand(CommandSupport cmdSupp, Hashtable vars,
                                         final String eom, boolean[] testSuccess,
                                         final List errorOutputList) throws InterruptedException {
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
        if (errorOutputList != null) {
            vcmd.addTextErrorListener(new TextErrorListener() {
                public void outputLine(String line) {
                    errorOutputList.add(line);
                }
            });
        }
        CommandTask task = cmd.execute();
        try {
            task.waitFinished(0);
        } catch (InterruptedException iexc) {
            task.stop();
            throw iexc;
        }
        testSuccess[0] = (task.getExitStatus() == CommandTask.STATUS_SUCCEEDED);
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
    
    private boolean runRealMultiCommand(CommandSupport cmdSupp, Hashtable vars, String[] files) {
        Command command = cmdSupp.createCommand();
        VcsDescribedCommand vcmd = (VcsDescribedCommand) command;
        vcmd.setAdditionalVariables(vars);
        boolean haveFiles = setFiles(command, rootDir, files);
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
    
    private boolean setFiles(Command command, String rootDir, String[] filePaths) {
        List fileObjects = new ArrayList();
        List files = new ArrayList();
        int rdl = rootDir.length();
        for (int i = 0; i < filePaths.length; i++) {
            FileObject fo = (fileSystem != null) ? fileSystem.findResource(filePaths[i]) : null;
            if (fo != null) {
                fileObjects.add(fo);
            } else {
                files.add(new File(rootDir + File.separator + filePaths[i].replace('/', File.separatorChar)));
            }
        }
        if (fileObjects.size() > 0) {
            FileObject[] fos = (FileObject[]) fileObjects.toArray(new FileObject[0]);
            fos = command.getApplicableFiles(fos);
            if (fos != null && fos.length > 0) {
                command.setFiles(fos);
                fileObjects = java.util.Arrays.asList(fos);
            } else {
                fileObjects.clear();
            }
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
        // If we have found some file, check whether there is not a better match with a longer name
        String longerFile = file;
        while(new File(longerFile).exists()) {
            file = longerFile;
            if (end >= l) break;
            end = message.indexOf(' ', end + 1);
            if (end < 0) end = l;
            else {
                if (end > 0 && message.charAt(end - 1) == endChar) {
                    return message.substring(0, end - 1);
                }
            }
            longerFile = message.substring(0, end);
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
    
    /**
     * Confirm (asking the user) the questions provided.
     * @return The files the questions were confirmed on, relative to rootDir.
     */
    private static String[] confirm(String[] messages, String rootDir, String[] selectedFiles) {
        if (messages.length == 0) return messages;
        List confirmedFiles = new ArrayList();
        List confirmedPatterns = new ArrayList();
        List deniedPatterns = new ArrayList();
        Object yesAllOption = NbBundle.getMessage(ConfirmationCommand.class, "CTL_YesAll");
        Object noAllOption = NbBundle.getMessage(ConfirmationCommand.class, "CTL_NoAll");
        Object skipOption = NbBundle.getMessage(ConfirmationCommand.class, "CTL_Skip");
        Object skipAllOption = NbBundle.getMessage(ConfirmationCommand.class, "CTL_SkipAll");
        List patternItems = new ArrayList();
        String[] ambiguousFile = new String[] { null };
        Set ambiguousFiles = new HashSet();
        boolean doSkipAll = false;
        for (int i = 0; i < messages.length; i++) {
            String file = checkPatterns(confirmedPatterns, messages[i]);
            if (file != null) {
                if (!file.startsWith(rootDir)) {
                    //System.out.println("Searching for a unique file '"+file+"' among "+java.util.Arrays.asList(selectedFiles)+" ...");
                    file = uniqueFilePath(file, selectedFiles);
                    //System.out.println("  Unique file = '"+file+"'");
                }
                if (file != null) {
                    file = file.substring(rootDir.length()).replace(File.separatorChar, '/');
                    while (file.startsWith("/")) file = file.substring(1);
                    confirmedFiles.add(file);
                    continue;
                }
            }
            if (checkPatterns(deniedPatterns, messages[i]) != null) {
                continue;
            }
            patternItems.clear();
            file = getUnambiguousFile(messages[i], rootDir, selectedFiles, patternItems, ambiguousFile);
            //System.out.println("  file = '"+file+"', patternItems = "+patternItems);
            if (file == null) {
                if (doSkipAll || ambiguousFiles.contains(ambiguousFile[0])) {
                    continue;
                }
                ambiguousFiles.add(ambiguousFile[0]);
                String msg = reduceBigMessage(messages[i]) + "\n\n" +
                             NbBundle.getMessage(ConfirmationCommand.class, "MSG_Ambiguous");
                NotifyDescriptor confirmation = new NotifyDescriptor(msg,
                    NbBundle.getMessage(ConfirmationCommand.class, "TTL_Warning"), 
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.WARNING_MESSAGE,
                    new Object[] { skipOption, skipAllOption,
                                   NotifyDescriptor.CANCEL_OPTION },
                    skipOption);
                Object result = DialogDisplayer.getDefault().notify(confirmation);
                if (skipOption.equals(result)) {
                    continue;
                } if (skipAllOption.equals(result)) {
                    doSkipAll = true;
                    continue;
                } else {
                    return null;
                }
            }
            NotifyDescriptor confirmation = new NotifyDescriptor(reduceBigMessage(messages[i]),
                NbBundle.getMessage(ConfirmationCommand.class, "TTL_Confirmation"),
                NotifyDescriptor.YES_NO_CANCEL_OPTION,
                NotifyDescriptor.QUESTION_MESSAGE,
                new Object[] { NotifyDescriptor.YES_OPTION, yesAllOption,
                               NotifyDescriptor.NO_OPTION, noAllOption,
                               NotifyDescriptor.CANCEL_OPTION },
                NotifyDescriptor.YES_OPTION);
            Object result = DialogDisplayer.getDefault().notify(confirmation);
            if (NotifyDescriptor.YES_OPTION.equals(result)) {
                confirmedFiles.add(file);
            } else if (yesAllOption.equals(result)) {
                confirmedFiles.add(file);
                confirmedPatterns.add(patternItems.toArray(new String[0]));
            } else if (NotifyDescriptor.NO_OPTION.equals(result)) {
                continue;
            } else if (noAllOption.equals(result)) {
                deniedPatterns.add(patternItems.toArray(new String[0]));
            } else {
                // canceled
                return null;
            }
        }
        return (String[]) confirmedFiles.toArray(new String[0]);
    }
    
    /**
     * Get the file relative to rootDir.
     */
    private static String getUnambiguousFile(String message, String rootDir,
                                             String[] selectedFiles, List patternItems,
                                             String[] ambiguousFile) {
        int begin = 0;
        int fileIndex;
        String file = null;
        int rdl = rootDir.length();
        //System.out.println("getUnambiguousFile("+message+")");
        while((fileIndex = message.indexOf(rootDir, begin)) >= 0) {
            String rfile = retrieveFile(message.substring(fileIndex), getEndChar(message, fileIndex));
            patternItems.add(message.substring(begin, fileIndex));
            begin = fileIndex + rfile.length();
            rfile = rfile.substring(rdl).replace(File.separatorChar, '/');
            while (rfile.startsWith("/")) rfile = rfile.substring(1);
            if (file == null) {
                file = rfile;
            } else {
                //System.out.println("MULTILPLE DIFFERENT FILES found: "+file+" <> "+rfile);
                if (!file.equals(rfile)) return null;
            }
        }
        if (file != null) {
            if (begin < message.length()) {
                patternItems.add(message.substring(begin));
            } else {
                patternItems.add("");
            }
        } else { // The file was not found
            int sentenceEnd = message.lastIndexOf(". ");
            if (sentenceEnd > 0) {
                sentenceEnd += 2;
            } else {
                sentenceEnd = 0;
            }
            int firstSpace = message.indexOf(' ', sentenceEnd);
            if (firstSpace > 0) {
                file = message.substring(sentenceEnd, firstSpace);
                ambiguousFile[0] = file;
                file = uniqueFilePath(file, selectedFiles);
                if (file == null) {
                    return null;
                }
                file = file.substring(rdl).replace(File.separatorChar, '/');
                while (file.startsWith("/")) file = file.substring(1);
                patternItems.add("");//message.substring(0, sentenceEnd));
                patternItems.add(message.substring(firstSpace + 1));
            }
        }
        return file;
    }
    
    /**
     * Check whether the message match at least one of the patterns.
     * @return The file from the matched pattern.
     */
    private static String checkPatterns(List patterns, String message) {
        int n = patterns.size();
        for (int i = 0; i < n; i++) {
            String[] pattern = (String[]) patterns.get(i);
            //System.out.println("Checking pattern: "+java.util.Arrays.asList(pattern)+"\n in message = '"+message+"'");
            if (!message.startsWith(pattern[0])) continue;
            int l = pattern.length - 1;
            if (!message.endsWith(pattern[l])) continue;
            for (l-- ; l > 0; l--) {
                if (message.indexOf(pattern[l]) < 0) break;
            }
            if (l == 0) {
                int beginIndex = message.indexOf(pattern[pattern.length - 2]);
                int endIndex = message.length() - pattern[pattern.length - 1].length();
                String fileName = message.substring(beginIndex, endIndex).trim();
                if (pattern.length == 2 && pattern[0].length() == 0) {
                    int sentenceEnd = fileName.lastIndexOf(". ");
                    if (sentenceEnd > 0) {
                        fileName = fileName.substring(sentenceEnd + 2).trim();
                    }
                }
                //System.out.println("  returning file '"+fileName+"'");
                return fileName;
            }
        }
        //System.out.println("  returning NOTHING.");
        return null;
    }
    
    private static String reduceBigMessage(String msg) {
        if (msg.length() > 512) {
            int sentenceEnd = msg.lastIndexOf(". ");
            if (sentenceEnd >= 0) {
                if (msg.length() - sentenceEnd < 256) {
                    int nextSentenceLength = msg.lastIndexOf(". ", sentenceEnd - 1);
                    if (nextSentenceLength >= 0 && msg.length() - nextSentenceLength < 512) {
                        sentenceEnd = nextSentenceLength; // Take the last two sentences.
                    }
                }
                msg = msg.substring(sentenceEnd + 2);
            } // else We didn't find an end of the sentence, we have to disply it all
        }
        return msg;
    }
    
    private static String uniqueFilePath(String file, String[] rootDirs) {
        String upath = null;
        String[] path = new String[] { null };
        Stack tempPath = new Stack();
        int count = 0;
        for (int i = 0; i < rootDirs.length; i++) {
            FileObject root = FileUtil.toFileObject(FileUtil.normalizeFile(new File(rootDirs[i])));
            if (root == null) {
                return null;
            } else {
                //System.out.println("num occurrences = "+countFileOccurences(file, root));
                tempPath.clear();
                count += countFileOccurences(file, root, tempPath, path);
                if (path[0] != null) {
                    if (upath == null) {
                        upath = rootDirs[i]+path[0];
                    } else {
                        return null; // We have > 1 paths => no unique path
                    }
                }
            }
        }
        if (count == 1) {
            return upath;
        } else {
            return null;
        }
    }
    
    private static int countFileOccurences(String file, FileObject folder,
                                           Stack tempPath, String[] path) {
        int count = 0;
        FileObject[] children = folder.getChildren();
        for (int i = 0; i < children.length; i++) {
            if (children[i].isFolder()) {
                tempPath.push(children[i].getNameExt());
                count += countFileOccurences(file, children[i], tempPath, path);
                tempPath.pop();
            } else {
                if (file.equals(children[i].getNameExt())) {
                    count++;
                    if (path[0] == null) {
                        StringBuffer bPath = new StringBuffer();
                        for (Iterator it = tempPath.iterator(); it.hasNext(); ) {
                            bPath.append(File.separator);
                            bPath.append((String) it.next());
                        }
                        bPath.append(File.separator);
                        bPath.append(children[i].getNameExt());
                        path[0] = bPath.toString();
                    }
                }
            }
        }
        return count;
    }
    
}
