/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.cmdline;

import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.text.*;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.*;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.cmdline.exec.*;
import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.caching.RefreshCommandSupport;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.RegexOutputListener;
import org.netbeans.modules.vcscore.commands.TextInput;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
import org.netbeans.modules.vcscore.commands.VcsCommandVisualizer;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.util.*;

//import org.netbeans.modules.vcscore.revision.RevisionListener;

/** Execute command.
 * 
 * @author Michal Fadljevic, Martin Entlicher
 */
//-------------------------------------------
public class ExecuteCommand extends Object implements VcsCommandExecutor {
    private Debug E=new Debug("ExecuteCommand", true); // NOI18N
    private Debug D=E;
        
    public static final String DEFAULT_REGEX = "^(.*$)"; // Match the whole line by default.
    public static final String STATUS_USE_REG_EXP_PARSE_OUTPUT = "REG_EXP_PARSE_OUTPUT"; // Use the output of the parsing as the status
    
    private CommandExecutionContext executionContext = null;
    private VcsFileSystem fileSystem = null;
    private UserCommand cmd = null;
    private Hashtable vars = null;
    /**
     * The preferred execution string, that should be used instead of the
     * execution string defined by the command. This is usually pre-processed
     * command's execution string, where some values may be expanded.
     */
    private String preferredExec = null;
    /**
     * Fully expanded preferred execution string. This is the final execution
     * string, that is used to run the command.
     */
    private String preferredExecExpanded = null;
    
    /**
     * The preferred structured execution property, that should be used instead
     * of the structured execution property defined by the command.
     * This is usually pre-processed command's execution property,
     * where some values may be expanded.
     */
    private StructuredExec preferredStructuredExec = null;
    
    /** The command associated with this executor.
     * @deprecated For compatibility with the old VCS "API" only. */
    private VcsDescribedCommand command = null;
    /** The CommandTask associated with this executor. */
    private CommandTask task = null;
    
    /** The underlying text input command, if any. */
    private TextInput txtInputCommand = null;

    //private RegexListener stdoutListener = null;
    //private RegexListener stderrListener = null;

    //private NoRegexListener stdoutNoRegexListener = null;
    //private NoRegexListener stderrNoRegexListener = null;

    //private OutputContainer errorContainer = null;
    
    private ArrayList textOutputListeners = new ArrayList(); 
    private ArrayList textErrorListeners = new ArrayList(); 
    private ArrayList regexOutputListeners = new ArrayList(); 
    private ArrayList regexErrorListeners = new ArrayList(); 
    private ArrayList dataOutputListeners = new ArrayList(); // For compatibility only
    private ArrayList dataErrorListeners = new ArrayList(); // For compatibility only
    private ArrayList fileReaderListeners = new ArrayList();
    private ArrayList immediateOutputListeners = new ArrayList();
    private ArrayList immediateErrorListeners = new ArrayList();
    private boolean doFileRefresh; // Whether this command provides updated status of processed files
    private boolean doPostExecutionRefresh; // Whether this command refresh status of all processed files
    private boolean getFileRefreshFromErrOut = false; // Whether to read the refresh info from the error data output also
    private ArrayList filesToRefresh; // The list of files, that were not refreshed.
                                      // Files, that were not refreshed when the command finish
                                      // will be refreshed by the LIST_FILE command (if present).
    private ArrayList refreshInfoElements;
    /** The base folder, that will be prepended to the refreshed files, so that
     * the result will be relative to FS root (work + relative mount point) */
    private String refreshFilesBase = null;
    /** The folder that the refreshed files must start with. This is important
     * if there is a non-empty relative mount point, but the files are relative
     * to working dir (the files must start with the relative mount point in this
     * case). */
    private String refreshFilesMustStartWith = null;
    
    private boolean substituteStatuses = false;
    private Pattern[] substituitionRegExps;
    private String[] substituitionStatuses;

    private Collection processingFilesCollection = null;
    
    protected int exitStatus = 0;

    //private ArrayList commandListeners = new ArrayList();

    //-------------------------------------------
    public ExecuteCommand(CommandExecutionContext executionContext, UserCommand cmd, Hashtable vars) {
        this(executionContext, cmd, vars, null);
    }

    public ExecuteCommand(CommandExecutionContext executionContext, UserCommand cmd, Hashtable vars, String preferredExec) {
        //super("VCS-ExecuteCommand-"+cmd.getName()); // NOI18N
        this.executionContext = executionContext;
        if (executionContext instanceof VcsFileSystem) {
            this.fileSystem = (VcsFileSystem) executionContext;
        } else {
            this.fileSystem = null;
        }
        this.cmd = cmd;
        this.vars = vars;
        if (preferredExec == null) {
            preferredExec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        }
        this.preferredExec = preferredExec;
        this.doFileRefresh =
            (fileSystem != null) &&
            VcsCommandIO.getIntegerPropertyAssumeNegative(cmd, UserCommand.PROPERTY_LIST_INDEX_FILE_NAME) >= 0;
        this.doPostExecutionRefresh =
            (fileSystem != null) &&
            VcsCommandIO.getBooleanProperty(cmd, UserCommand.PROPERTY_REFRESH_PROCESSED_FILES);
        if (doFileRefresh) {
            refreshInfoElements = new ArrayList();
            String statusSubstitutions = (String) cmd.getProperty(UserCommand.PROPERTY_REFRESH_FILE_STATUS_SUBSTITUTIONS);
            substituteStatuses = (statusSubstitutions != null && statusSubstitutions.length() > 0);
            if (substituteStatuses) {
                parseStatusSubstitutions(statusSubstitutions);
            }
            getFileRefreshFromErrOut = VcsCommandIO.getBooleanProperty(cmd, UserCommand.PROPERTY_REFRESH_INFO_FROM_BOTH_DATA_OUTS);
        }
    }
    
    private void parseStatusSubstitutions(String statusSubstitutions) {
        String[] substitutions = VcsUtilities.getQuotedStrings(statusSubstitutions);
        int n = substitutions.length / 2;
        Pattern[] regExps = new Pattern[n];
        String[] statuses = new String[n];
        int nn = n;
        for (int i = 0; i < n; i++) {
            try {
                regExps[i] = Pattern.compile(substitutions[2*i]);
            } catch(PatternSyntaxException e) {
                //E.err(e,"RE failed regexp"); // NOI18N
                ErrorManager.getDefault().notify(
                    ErrorManager.getDefault().annotate(e,
                        NbBundle.getMessage(ExecuteCommand.class, "MSG_BadRegExpInStatusSubstitution", substitutions[2*i])));
                nn--;
                continue;
                //throw new BadRegexException("Bad regexp.", e); // NOI18N
            }
            statuses[i] = substitutions[2*i + 1];
        }
        if (n == nn) {
            substituitionRegExps = regExps;
            substituitionStatuses = statuses;
        } else {
            n = nn;
            substituitionRegExps = new Pattern[n];
            substituitionStatuses = new String[n];
            int j = 0;
            for (int i = 0; i < n; i++) {
                if (regExps[j] == null) {
                    i--;
                } else {
                    substituitionRegExps[i] = regExps[j];
                    substituitionStatuses[i] = statuses[j];
                }
                j++;
            }
        }
    }
    
    /**
     * Get the command associated with this ExecuteCommand.
     * @deprecated Needed for the compatibility with old "API" only.
     */
    public VcsDescribedCommand getDescribedCommand() {
        return command;
    }
    
    /**
     * Set the command associated with this ExecuteCommand.
     * @deprecated Needed for the compatibility with old "API" only.
     */
    public void setDescribedCommand(VcsDescribedCommand command) {
        this.command = command;
    }
    
    /**
     * Set the CommandTask, that is associated with this executor.
     */
    public CommandTask getTask() {
        return task;
    }
    
    /**
     * Get the CommandTask, that is associated with this executor.
     */
    public void setTask(CommandTask task) {
        this.task = task;
    }
    
    /**
     * Add the listener to the standard output of the command. The listeners are removed
     * when the command finishes.
     */
    public final synchronized void addTextOutputListener(TextOutputListener l) {
        if (textOutputListeners != null) textOutputListeners.add(l);
    }
    
    /**
     * Add the listener to the error output of the command. The listeners are removed
     * when the command finishes.
     */
    public final synchronized void addTextErrorListener(TextOutputListener l) {
        if (textErrorListeners != null) textErrorListeners.add(l);
    }
    
    /**
     * Add the listener to the data output of the command. This output may contain
     * a parsed information from its standard output or some other data provided
     * by this command. The listeners are removed when the command finishes.
     */
    public final synchronized void addRegexOutputListener(RegexOutputListener l) {
        if (regexOutputListeners != null) regexOutputListeners.add(l);
    }

    /**
     * Add the listener to the data error output of the command. This output may contain
     * a parsed information from its error output or some other data provided
     * by this command. If there are some data given to this listener, the command
     * is supposed to fail. The listeners are removed when the command finishes.
     */
    public final synchronized void addRegexErrorListener(RegexOutputListener l) {
        if (regexErrorListeners != null) regexErrorListeners.add(l);
    }

    /**
     * Add the listener to the standard output of the command. The listeners should be
     * released by the implementing class, when the command finishes.
     * @deprecated Kept for compatibility reasons only.
     *             Use {@link #addTextOutputListener} instead.
     */
    public void addOutputListener(CommandOutputListener l) {
        if (textOutputListeners != null) textOutputListeners.add(l);
    }
    
    /**
     * Add the listener to the error output of the command. The listeners should be
     * released by the implementing class, when the command finishes.
     * @deprecated Kept for compatibility reasons only.
     *             Use {@link #addTextErrorListener} instead.
     */
    public void addErrorOutputListener(CommandOutputListener l) {
        if (textErrorListeners != null) textErrorListeners.add(l);
    }
    
    /**
     * Add the listener to the data output of the command. This output may contain
     * a parsed information from its standard output or some other data provided
     * by this command. The listeners should be released by the implementing class,
     * when the command finishes.
     * @deprecated Kept for compatibility reasons only.
     *             Use {@link #addRegexOutputListener} instead.
     */
    public void addDataOutputListener(CommandDataOutputListener l) {
        if (dataOutputListeners != null) dataOutputListeners.add(l);
    }
    
    /**
     * Add the listener to the data error output of the command. This output may contain
     * a parsed information from its error output or some other data provided
     * by this command. If there are some data given to this listener, the command
     * is supposed to fail. The listeners should be released by the implementing class,
     * when the command finishes.
     * @deprecated Kept for compatibility reasons only.
     *             Use {@link #addRegexErrorListener} instead.
     */
    public void addDataErrorOutputListener(CommandDataOutputListener l) {
        if (dataErrorListeners != null) dataErrorListeners.add(l);
    }
    
    public void addImmediateTextOutputListener(TextOutputListener l) {
        if (immediateOutputListeners != null) immediateOutputListeners.add(l);
    }

    public void addImmediateTextErrorListener(TextOutputListener l) {
        if (immediateErrorListeners != null) immediateErrorListeners.add(l);
    }
    
    public void sendInput(String input) {
        if (txtInputCommand != null) {
            txtInputCommand.sendInput(input);
        }
    }
    

    public final VcsCommand getCommand() {
        return cmd;
    }
    
    /**
     * Get the variables used by this command execution.
     */
    public final Hashtable getVariables() {
        return vars;
    }

    //-------------------------------------------
    public final int getExitStatus(){
        return exitStatus;
    }
    
    private VcsCommandVisualizer createVisualizer() {
        String visualizerClassName = (String) cmd.getProperty(VcsCommand.PROPERTY_DISPLAY_VISUALIZER);
        if (visualizerClassName != null) {
            Class visualizerClass = null;
            try {
                visualizerClass =  Class.forName(visualizerClassName, true, VcsUtilities.getSFSClassLoader());
            } catch (ClassNotFoundException e) {}
            if (visualizerClass == null) {
                try {
                    visualizerClass =  Class.forName(visualizerClassName, true,
                                                     (ClassLoader) Lookup.getDefault().lookup(ClassLoader.class));
                } catch (ClassNotFoundException e) {}
            }
            if (visualizerClass == null) {
                try {
                    printErrorOutput("CLASS EXEC: " + g("ERR_ClassNotFound", visualizerClassName)); // NOI18N
                } catch(java.util.MissingResourceException mrexc) {
                    // Likely to be called when the module is being uninstalled
                    printErrorOutput("CLASS EXEC: Class " + visualizerClassName + " not found"); // NOI18N
                }
            } else {
                VcsCommandVisualizer visualizer = null;
                try {
                    visualizer = (VcsCommandVisualizer) visualizerClass.newInstance();
                } catch (InstantiationException e) {
                    printErrorOutput("CLASS EXEC: "+g("ERR_CanNotInstantiate", visualizerClass)); // NOI18N
                } catch (IllegalAccessException e) {
                    printErrorOutput("CLASS EXEC: "+g("ERR_IllegalAccessOnClass", visualizerClass)); // NOI18N
                }
                return visualizer;
            }
        }
        return null;
    }
    
    /**
     * Get the graphical visualization of the command.
     * The returned visualizer might need to be initialized in UserCommandTask,
     * that owns the CommandOutputCollector.
     * @return null no visualization is desired.
     */
    public VcsCommandVisualizer getVisualizer() {
        return createVisualizer();
    }
    
    private void commandFinished(String exec, boolean success) {
        textOutputListeners.clear();
        textErrorListeners.clear();
        regexOutputListeners.clear();
        regexErrorListeners.clear();
        dataOutputListeners.clear();
        dataErrorListeners.clear();
        immediateOutputListeners.clear();
        immediateErrorListeners.clear();
        if (doFileRefresh) {
            flushRefreshInfo();
            cleanupSendRefreshInfo();
        }
        textOutputListeners = null;
        textErrorListeners = null;
        regexOutputListeners = null;
        regexErrorListeners = null;
        dataOutputListeners = null;
        dataErrorListeners = null;
        immediateOutputListeners = null;
        immediateErrorListeners = null;
        if (success || VcsCommandIO.getIntegerPropertyAssumeNegative(cmd, VcsCommand.PROPERTY_REFRESH_ON_FAIL) == 1) {
            refreshRemainingFiles();
            /* Moved to CommandExecutorSupport
            String path = (String) vars.get("DIR") + "/" + (String) vars.get("FILE");
            path = path.replace(java.io.File.separatorChar, '/');
            if (VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_CHECK_FOR_MODIFICATIONS)) {
                //System.out.println(cmd.getName()+" finished successfully.");
                fileSystem.checkForModifications(path);
                /*
                org.openide.filesystems.FileObject fo = fileSystem.findResource(path);
                System.out.println("fo("+path+") = "+fo);
                if (fo != null) {
                    System.out.println("calling refresh(true)...");
                    fo.refresh(true);
                }
                 *
            }
             */
            int whatChanged = 0;
            Object changedInfo = null;
        }
        fileReaderListeners.clear();
        fileReaderListeners = null;
    }

    /**
     * This method can be used to do some preprocessing of the command which is to be run.
     * @param vc the command to be preprocessed.
     * @param vars the variables
     * @param exec the updated execution string. It may contain user input from variable input dialog
     * @return the updated exec property
     */
    public String preprocessCommand(VcsCommand vc, Hashtable vars, String exec, StructuredExec sexec) {
        this.preferredExec = exec;
        this.preferredStructuredExec = sexec;
        executionContext.getVarValueAdjustment().adjustVarValues(vars);
        boolean createTempFile = false;
        if (sexec != null) {
            StructuredExec.Argument[] args = sexec.getArguments();
            for (int i = 0; i < args.length; i++) {
                if (args[i].getArgument().indexOf(Variables.TEMPORARY_FILE) >= 0) {
                    createTempFile = true;
                    break;
                }
            }
        } else {
            createTempFile = (exec != null && exec.indexOf(Variables.TEMPORARY_FILE) >= 0);
        }
        if (createTempFile && vars.get(Variables.TEMPORARY_FILE) == null) {
            try {
                File tempFile = File.createTempFile("VCS", "tmp");
                tempFile.deleteOnExit();
                vars.put(Variables.TEMPORARY_FILE, tempFile.getAbsolutePath());
            } catch (IOException ioex) {}
        }
        this.vars = vars;
        if (exec != null) {
            this.preferredExecExpanded = Variables.expand(vars, exec, false);
        }
        /*
        if (!(vc instanceof UserCommand)) return "";
        UserCommand uc = (UserCommand) vc;
        PreCommandPerformer cmdPerf = new PreCommandPerformer(fileSystem, uc, vars);
        String exec = cmdPerf.process();
         */
        return exec;
    }
    
    /**
     * Get the updated execution string. It may contain user input now.
     */
    public String getExec() {
        return preferredExecExpanded;
    }
    
    protected final VcsFileSystem getFileSystem() {
        return fileSystem;
    }
    
    /**
     * Add the data and error regular expression listeners,
     * if some global regex is defined, return the global data
     * output in the first item and global error output in the second.
     * @param ec the command to add the listeners to
     * @param globalRegexs the array, that is filled with compiled data and error
     *        regular expressions
     * @return the array of length 2 with global data and error output
     * (either of them can be null) or null, when none of them are defined
     */
    private StringBuffer[] addRegexListeners(final ExternalCommand ec, final Pattern[] globalRegexs) {
        String dataRegex = (String) cmd.getProperty(UserCommand.PROPERTY_DATA_REGEX);
        if (dataRegex == null) dataRegex = DEFAULT_REGEX;
        String errorRegex = (String) cmd.getProperty(UserCommand.PROPERTY_ERROR_REGEX);
        if (errorRegex == null) errorRegex = DEFAULT_REGEX;
        String dataRegexGlobal = (String) cmd.getProperty(UserCommand.PROPERTY_DATA_REGEX_GLOBAL);
        String errorRegexGlobal = (String) cmd.getProperty(UserCommand.PROPERTY_ERROR_REGEX_GLOBAL);
        Pattern dataRegexGlobalRE = null;
        Pattern errorRegexGlobalRE = null;
        final StringBuffer dataOutput;
        final StringBuffer errorOutput;
        if (dataRegexGlobal != null) {
            try {
                dataRegexGlobalRE = Pattern.compile(dataRegexGlobal);
            } catch (PatternSyntaxException exc) {
                ErrorManager.getDefault().notify(
                    ErrorManager.getDefault().annotate(exc,
                        NbBundle.getMessage(ExternalCommand.class, "MSG_BadRegexMessageInfo", dataRegexGlobal)));
            }
            if (dataRegexGlobalRE != null) {
                globalRegexs[0] = dataRegexGlobalRE;
                dataOutput = new StringBuffer();
            } else {
                dataOutput = null;
            }
        } else {
            dataOutput = null;
        }
        if (errorRegexGlobal != null) {
            try {
                errorRegexGlobalRE = Pattern.compile(errorRegexGlobal);
            } catch (PatternSyntaxException exc) {
                ErrorManager.getDefault().notify(
                    ErrorManager.getDefault().annotate(exc,
                        NbBundle.getMessage(ExternalCommand.class, "MSG_BadRegexMessageInfo", errorRegexGlobal)));
            }
            if (errorRegexGlobalRE != null) {
                globalRegexs[1] = errorRegexGlobalRE;
                errorOutput = new StringBuffer();
            } else {
                errorOutput = null;
            }
        } else {
            errorOutput = null;
        }
        try {
            if (dataRegexGlobalRE == null) {
                ec.addRegexOutputListener(new RegexOutputListener() {
                                              public void outputMatchedGroups(String[] data) {
                                                  printDataOutput(data);
                                              }
                                          }, dataRegex);
            } else {
                ec.addRegexOutputListener(new RegexOutputListener() {
                                              public void outputMatchedGroups(String[] data) {
                                                  if (data != null) {
                                                      for (int i = 0; i < data.length; i++) {
                                                          dataOutput.append(data[i]);
                                                      }
                                                      dataOutput.append(" ");
                                                  }
                                              }
                                          }, dataRegex);
            }
        } catch (BadRegexException e) {
            ErrorManager.getDefault().notify(
                ErrorManager.getDefault().annotate(e,
                    NbBundle.getMessage(ExternalCommand.class, "MSG_BadRegexMessageInfo", dataRegex)));
        }
        try {
            if (errorRegexGlobalRE == null) {
                ec.addRegexErrorListener(new RegexOutputListener() {
                                              public void outputMatchedGroups(String[] data) {
                                                  printDataErrorOutput(data);
                                              }
                                          }, errorRegex);
            } else {
                ec.addRegexErrorListener(new RegexOutputListener() {
                                              public void outputMatchedGroups(String[] data) {
                                                  if (data != null) {
                                                      for (int i = 0; i < data.length; i++) {
                                                          errorOutput.append(data[i]);
                                                      }
                                                      errorOutput.append(" ");
                                                  }
                                              }
                                          }, errorRegex);
            }
        } catch (BadRegexException e) {
            ErrorManager.getDefault().notify(
                ErrorManager.getDefault().annotate(e,
                    NbBundle.getMessage(ExternalCommand.class, "MSG_BadRegexMessageInfo", errorRegex)));
        }
        if (dataOutput != null || errorOutput != null) {
            return new StringBuffer[] { dataOutput, errorOutput };
        } else {
            return null;
        }
    }
    
    private void printGlobalDataOutput(String globalDataOutput, Pattern globalDataRegex) {
        if (globalDataOutput.endsWith(" ")) {
            globalDataOutput = globalDataOutput.substring(0, globalDataOutput.length() - 1);
        }
        String[] parsed = ExternalCommand.matchToStringArray(globalDataRegex, globalDataOutput);
        printDataOutput(parsed);
    }
    
    private void printGlobalErrorDataOutput(String globalDataOutput, Pattern globalDataRegex) {
        if (globalDataOutput.endsWith(" ")) {
            globalDataOutput = globalDataOutput.substring(0, globalDataOutput.length() - 1);
        }
        String[] parsed = ExternalCommand.matchToStringArray(globalDataRegex, globalDataOutput);
        printDataErrorOutput(parsed);
    }
    
    //-------------------------------------------
    /**
     * Execute a command-line command.
     */
    protected void runCommand(String[] execs) {
        //E.deb("runCommand: "+exec); // NOI18N

        //exec = Variables.expand(vars,exec, true);
        preferredExec = preferredExecExpanded = VcsUtilities.array2stringNl(execs);
        StringBuffer[] globalDataOutputWhole = null;
        Pattern[] globalRegexs = new Pattern[2];
        for (int i = 0; i < execs.length; i++) {
            String exec = execs[i];
            ExternalCommand ec = new ExternalCommand(exec);
            setupExternalCommand(ec);
            StringBuffer[] globalDataOutput = addRegexListeners(ec, globalRegexs);

            //E.deb("ec="+ec); // NOI18N
            int status = ec.exec();
            if (status != exitStatus) {
                if (exitStatus == VcsCommandExecutor.SUCCEEDED ||
                        exitStatus == VcsCommandExecutor.FAILED &&
                        status == VcsCommandExecutor.INTERRUPTED) {
                    exitStatus = status;
                }
            }
            if (globalDataOutput != null) {
                if (globalDataOutputWhole == null) {
                    globalDataOutputWhole = globalDataOutput;
                } else {
                    if (globalDataOutputWhole[0] != null) {
                        globalDataOutputWhole[0].append(globalDataOutput[0]);
                    }
                    if (globalDataOutputWhole[1] != null) {
                        globalDataOutputWhole[1].append(globalDataOutput[1]);
                    }
                }
            }
        }
        if (globalDataOutputWhole != null) {
            if (globalDataOutputWhole[0] != null) {
                printGlobalDataOutput(globalDataOutputWhole[0].toString(), globalRegexs[0]);
            }
            if (globalDataOutputWhole[1] != null) {
                printGlobalErrorDataOutput(globalDataOutputWhole[1].toString(), globalRegexs[1]);
            }
        }
        E.deb("Command exited with exit status = "+exitStatus); // NOI18N
        //D.deb("errorContainer = "+errorContainer); // NOI18N
        switch (exitStatus) {
        case VcsCommandExecutor.SUCCEEDED:
            commandFinished(preferredExecExpanded, true);
            break;
        case VcsCommandExecutor.INTERRUPTED:
            //commandFinished(exec, false);
            //break;
            // Do the same as when the command fails.
        case VcsCommandExecutor.FAILED:
            commandFinished(preferredExecExpanded, false);
            if (fileSystem != null) fileSystem.removeNumDoAutoRefresh((String) vars.get("DIR")); // NOI18N
            break;
        }

        D.deb("run("+cmd.getName()+") finished"); // NOI18N
    }
    
    /**
     * Execute a command-line command.
     */
    protected void runCommand(StructuredExec exec) {
        preferredExec = preferredExecExpanded = VcsUtilities.array2stringNl(ExternalCommand.parseParameters(exec));
        StringBuffer[] globalDataOutputWhole = null;
        Pattern[] globalRegexs = new Pattern[2];
        ExternalCommand ec = new ExternalCommand(exec);
        setupExternalCommand(ec);
        StringBuffer[] globalDataOutput = addRegexListeners(ec, globalRegexs);

        //E.deb("ec="+ec); // NOI18N
        int status = ec.exec();
        exitStatus = status;
        if (globalDataOutput != null) {
            if (globalDataOutput[0] != null) {
                printGlobalDataOutput(globalDataOutput[0].toString(), globalRegexs[0]);
            }
            if (globalDataOutput[1] != null) {
                printGlobalErrorDataOutput(globalDataOutput[1].toString(), globalRegexs[1]);
            }
        }
        switch (exitStatus) {
        case VcsCommandExecutor.SUCCEEDED:
            commandFinished(preferredExecExpanded, true);
            break;
        case VcsCommandExecutor.INTERRUPTED:
            //commandFinished(exec, false);
            //break;
            // Do the same as when the command fails.
        case VcsCommandExecutor.FAILED:
            commandFinished(preferredExecExpanded, false);
            if (fileSystem != null) fileSystem.removeNumDoAutoRefresh((String) vars.get("DIR")); // NOI18N
            break;
        }

        D.deb("run("+cmd.getName()+") finished"); // NOI18N
    }
    
    private void setupExternalCommand(ExternalCommand ec) {
        this.txtInputCommand = (TextInput) ec;
        //ec.setTimeout(cmd.getTimeout());
        ec.setInput((String) cmd.getProperty(UserCommand.PROPERTY_INPUT),
                    VcsCommandIO.getBooleanProperty(cmd, UserCommand.PROPERTY_INPUT_REPEAT));
        String dynamicEnv = (String) getVariables().get("DYNAMIC_ENVIRONMENT_VARS");
        if (dynamicEnv != null && dynamicEnv.length() > 0) {
            ec.setEnv(getEnvironmentFromVars(getVariables()));
        } else {
            ec.setEnv(executionContext.getEnvironmentVars());
        }
        //D.deb(cmd.getName()+".getInput()='"+cmd.getInput()+"'"); // NOI18N

        for (Iterator it = textOutputListeners.iterator(); it.hasNext(); ) {
            ec.addTextOutputListener((TextOutputListener) it.next());
        }
        for (Iterator it = textErrorListeners.iterator(); it.hasNext(); ) {
            ec.addTextErrorListener((TextOutputListener) it.next());
        }

        for (Iterator it = immediateOutputListeners.iterator(); it.hasNext(); ) {
            ec.addImmediateTextOutputListener((TextOutputListener) it.next());
        }
        for (Iterator it = immediateErrorListeners.iterator(); it.hasNext(); ) {
            ec.addImmediateTextErrorListener((TextOutputListener) it.next());
        }
    }
    
    private static String[] getEnvironmentFromVars(Hashtable vars) {
        Map systemEnv = VcsUtilities.getSystemEnvVars();
        Map env = VcsUtilities.addEnvVars(systemEnv, vars,
                                          VcsFileSystem.VAR_ENVIRONMENT_PREFIX,
                                          VcsFileSystem.VAR_ENVIRONMENT_REMOVE_PREFIX);
        return VcsUtilities.getEnvString(env);
    }
    
    protected void printOutput(String line) {
        for (Iterator it = textOutputListeners.iterator(); it.hasNext(); ) {
            ((TextOutputListener) it.next()).outputLine(line);
        }
    }

    protected void printErrorOutput(String line) {
        for (Iterator it = textErrorListeners.iterator(); it.hasNext(); ) {
            ((TextOutputListener) it.next()).outputLine(line);
        }
    }

    protected void printDataOutput(String[] data) {
        for (Iterator it = regexOutputListeners.iterator(); it.hasNext(); ) {
            ((RegexOutputListener) it.next()).outputMatchedGroups(data);
        }
        for (Iterator it = dataOutputListeners.iterator(); it.hasNext(); ) {
            ((CommandDataOutputListener) it.next()).outputData(data);
        }
        if (doFileRefresh) {
            collectRefreshInfo(data);
        }
    }

    protected void printDataErrorOutput(String[] data) {
        for (Iterator it = regexErrorListeners.iterator(); it.hasNext(); ) {
            ((RegexOutputListener) it.next()).outputMatchedGroups(data);
        }
        for (Iterator it = dataErrorListeners.iterator(); it.hasNext(); ) {
            ((CommandDataOutputListener) it.next()).outputData(data);
        }
        if (getFileRefreshFromErrOut) {
            collectRefreshInfo(data);
        }
    }

    /**
     * Loads class of given name with some arguments and execute its exec() method.
     * @param className the name of the class to be loaded
     * @param args the arguments
     */
    protected void runClass(String exec, String className, String[] args) {

        E.deb("runClass: "+className); // NOI18N
        boolean success = true;
        Class execClass = null;
        preferredExec = preferredExecExpanded = exec;
        try {
            execClass =  Class.forName(className, true, VcsUtilities.getSFSClassLoader());
                                       //org.openide.TopManager.getDefault().currentClassLoader());
        } catch (ClassNotFoundException e) {}
        if (execClass == null) {
            try {
                printErrorOutput("CLASS EXEC: " + g("ERR_ClassNotFound", className)); // NOI18N
            } catch(java.util.MissingResourceException mrexc) {
                // Likely to be called when the module is being uninstalled
                printErrorOutput("CLASS EXEC: Class " + className + " not found"); // NOI18N
            }
            success = false;
        }
        VcsAdditionalCommand execCommand = null;
        if (success) {
            D.deb(execClass+" loaded"); // NOI18N
            try {
                execCommand = (VcsAdditionalCommand) execClass.newInstance();
            } catch (InstantiationException e) {
                printErrorOutput("CLASS EXEC: "+g("ERR_CanNotInstantiate", execClass)); // NOI18N
                /*
                fileSystem.debug ("EXEC: "+g("ERR_CanNotInstantiate", execClass)); // NOI18N
                if (stderrNoRegexListener != null)
                    stderrNoRegexListener.match("EXEC: "+g("ERR_CanNotInstantiate", execClass)); // NOI18N
                 */
                success = false;
            } catch (IllegalAccessException e) {
                printErrorOutput("CLASS EXEC: "+g("ERR_IllegalAccessOnClass", execClass)); // NOI18N
                /*
                fileSystem.debug ("EXEC: "+g("ERR_IllegalAccessOnClass", execClass)); // NOI18N
                if (stderrNoRegexListener != null)
                    stderrNoRegexListener.match("EXEC: "+g("ERR_IllegalAccessOnClass", execClass)); // NOI18N
                 */
                success = false;
            }
        }
        if (success) {
            E.deb("VcsAdditionalCommand created."); // NOI18N
            ExecuteCommand.setAdditionalParams(execCommand, fileSystem, executionContext);
            String dataRegex = (String) cmd.getProperty(UserCommand.PROPERTY_DATA_REGEX);
            String errorRegex = (String) cmd.getProperty(UserCommand.PROPERTY_ERROR_REGEX);
            String input = (String) cmd.getProperty(UserCommand.PROPERTY_INPUT);
            if (dataRegex != null) vars.put("DATAREGEX", dataRegex); // NOI18N
            if (errorRegex != null) vars.put("ERRORREGEX", errorRegex); // NOI18N
            if (input != null) vars.put("INPUT", input); // NOI18N
            //vars.put("TIMEOUT", new Long(cmd.getTimeout())); // NOI18N
            //TopManager.getDefault().setStatusText(g("MSG_Command_name_running", cmd.getName()));
            if (execCommand instanceof TextInput) {
                this.txtInputCommand = (TextInput) execCommand;
            }
            try {
                if (execCommand instanceof VcsAdditionalCommand.ImmediateOutput) {
                    VcsAdditionalCommand.ImmediateOutput io = (VcsAdditionalCommand.ImmediateOutput) execCommand;
                    for (Iterator it = immediateOutputListeners.iterator(); it.hasNext(); ) {
                        io.addImmediateTextOutputListener((TextOutputListener) it.next());
                    }
                    for (Iterator it = immediateErrorListeners.iterator(); it.hasNext(); ) {
                        io.addImmediateTextErrorListener((TextOutputListener) it.next());
                    }
                }
                success = execCommand.exec(vars, args,
                                       new CommandOutputListener() {
                                           public void outputLine(String line) {
                                               printOutput(line);
                                           }
                                       },
                                       new CommandOutputListener() {
                                           public void outputLine(String line) {
                                               printErrorOutput(line);
                                           }
                                       },
                                       new CommandDataOutputListener() {
                                           public void outputData(String[] data) {
                                               printDataOutput(data);
                                           }
                                       }, dataRegex,
                                       new CommandDataOutputListener() {
                                           public void outputData(String[] data) {
                                               printDataErrorOutput(data);
                                           }
                                       }, errorRegex
                                      );
            } catch (ThreadDeath td) {
                throw td; // re-throw the ThreadDeath
            } catch (Throwable thr) { // Something bad has happened in the called class!
                success = false;
                ErrorManager.getDefault().notify(
                    ErrorManager.getDefault().annotate(thr,
                        NbBundle.getMessage(ExecuteCommand.class, "ERR_EXC_IN_CLASS", className)));
            }
        }
        if (Thread.interrupted()) {
            exitStatus = VcsCommandExecutor.INTERRUPTED;
            commandFinished(exec, false);
            if (fileSystem != null) fileSystem.removeNumDoAutoRefresh((String) vars.get("DIR")); // NOI18N
        } else {
            if (success) {
                exitStatus = VcsCommandExecutor.SUCCEEDED;
                commandFinished(exec, true);
            } else {
                exitStatus = VcsCommandExecutor.FAILED;
                commandFinished(exec, false);
                if (fileSystem != null) fileSystem.removeNumDoAutoRefresh((String) vars.get("DIR")); // NOI18N
            }
        }
    }

    /**
     * Execute the command.
     */
    public void run() {
        //isRunning = true;
        //hasStarted = true;
        String exec = null;
        String[] execs = null;
        int maxCmdLength = 0;
        String maxCmdLengthStr = (String) vars.get(Variables.MAX_CMD_LENGTH);
        if (maxCmdLengthStr != null) {
            try {
                maxCmdLength = Integer.parseInt(maxCmdLengthStr);
            } catch (NumberFormatException nfex) {}
        }
        StructuredExec sexec = preferredStructuredExec;
        if (sexec == null) sexec = (StructuredExec) cmd.getProperty(VcsCommand.PROPERTY_EXEC_STRUCTURED);
        if (sexec != null) {
            sexec = sexec.getExpanded(vars, false);
        } else {
            if (preferredExec != null) exec = preferredExec;
            else exec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
            //System.out.println("ExecuteCommand.run(): exec = "+exec+"\npreferredExec = "+preferredExec);
            if (exec == null) return ; // Silently ignore null exec
            String execOrig = exec;
            exec = Variables.expand(vars, exec, false);
            exec = exec.trim();
            if (exec.trim().length() == 0) {
                preferredExec = "";
                return ; // Silently ignore empty exec
            }
            //System.out.println("Exec length = "+exec.length()+", MAX is "+maxCmdLengthStr);
            if (maxCmdLength > 0 && exec.length() > maxCmdLength) {
                execs = splitExec(execOrig, maxCmdLength);
            } else {
                execs = new String[] { exec };
            }
        }
        if (doPostExecutionRefresh) {
            filesToRefresh = new ArrayList(getFiles()); // All files should be refreshed at the end.
        } else if (doFileRefresh) {
            filesToRefresh = new ArrayList(); // Only some files (with unmatched status) should be refreshed.
        }
        // Initialize refreshFilesBase and refreshFilesMustStartWith fields:
        createRefreshFilesBase();
        
        boolean checkForModification = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_CHECK_FOR_MODIFICATIONS)
                                       && (fileSystem != null);
        Collection processingFiles = null;
        if (checkForModification) {
            processingFiles = getFiles();
            fileSystem.disableRefresh();
            for (Iterator it = processingFiles.iterator(); it.hasNext(); ) {
                fileSystem.lockFilesToBeModified((String) it.next(), true);
            }
        }
        try {
            if (checkForModification) {
                // It's crucial to once more save any possibly modified objects
                // after we have locked them.
                List fileObjects = new ArrayList(processingFiles.size());
                for (Iterator it = processingFiles.iterator(); it.hasNext(); ) {
                    FileObject fo = fileSystem.findResource((String) it.next());
                    if (fo != null) {
                        fileObjects.add(fo);
                    }
                }
                VcsAction.assureFilesSaved(fileObjects);
            }
            if (sexec != null) {
                String exe = sexec.getExecutable();
                if (exe.toLowerCase().endsWith(".class")) {
                    String[] eargs = ExternalCommand.parseParameters(sexec);
                    String[] args = new String[eargs.length - 1];
                    System.arraycopy(eargs, 1, args, 0, args.length);
                    runClass(VcsUtilities.array2stringNl(eargs), exe.substring(0, exe.length() - ".class".length()), args);
                } else {
                    runCommand(sexec);
                }
            } else {
                String[] allArgs = VcsUtilities.getQuotedArguments(exec);
                String first = allArgs[0];
                if (first != null && (first.toLowerCase().endsWith(".class"))) {// NOI18N
                    String[] args = new String[allArgs.length - 1];
                    System.arraycopy(allArgs, 1, args, 0, args.length);
                    runClass(exec, first.substring(0, first.length() - ".class".length()), args); // NOI18N
                } else {
                    runCommand(execs);
                }
            }
        } finally {
            if (checkForModification) {
                fileSystem.enableRefresh();
                for (Iterator it = processingFiles.iterator(); it.hasNext(); ) {
                    fileSystem.unlockFilesToBeModified((String) it.next(), true);
                }
            }
            String tempFilePath = (String) vars.get(Variables.TEMPORARY_FILE);
            if (tempFilePath != null) {
                String postCommands;
                if (getExitStatus() == VcsCommandExecutor.SUCCEEDED) {
                    postCommands = (String) cmd.getProperty(VcsCommand.PROPERTY_COMMANDS_AFTER_SUCCESS);
                } else {
                    postCommands = (String) cmd.getProperty(VcsCommand.PROPERTY_COMMANDS_AFTER_FAIL);
                }
                if (postCommands != null) {
                    postCommands = Variables.expand(vars, postCommands, false).trim();
                }
                if (postCommands == null || postCommands.length() == 0) {
                    new File(tempFilePath).delete();
                }
            }
        }
    }
    
    private String[] splitExec(String exec, int maxCmdLength) {
        //System.out.println("splitExec("+exec+", "+maxCmdLength+")");
        String[] varNames = new String[] { "FILES", "QFILES", "PATHS", "QPATHS", "MPATHS", "QMPATHS" };
        int nVARS = varNames.length;
        int[][] fileIndexes = new int[nVARS][];
        int nFiles = 0;
        for (int i = 0; i < nVARS; i++) {
            String indexesEncoded = (String) vars.get(varNames[i]+"_FILE_POS_INDEXES");
            if (indexesEncoded != null) {
                try {
                    fileIndexes[i] = (int[]) VcsUtilities.decodeValue(indexesEncoded);
                    nFiles = fileIndexes[i].length;
                } catch (java.io.IOException ioex) {}
            }
        }
        if (nFiles <= 1) {
            // We can not shrink this anyway, Therefore we simply return the original exec
            return new String[] { Variables.expand(vars, exec, false) };
        }
        String[] varValues = new String[nVARS];
        for (int i = 0; i < nVARS; i++) {
            varValues[i] = (String) vars.get(varNames[i]);
        }
        Hashtable tempVars = new Hashtable(vars);
        ArrayList execs = new ArrayList();
        int startFileIndex = 0;
        //System.out.println("nFiles = "+nFiles);
        do {
            int i = startFileIndex;
            int j = nFiles;
            String execTemp = null;
            int lastK = -1;
            while (i < j) {
                int k = (i + j + 1)/2;
                //System.out.println("  i = "+i+", j = "+j+", k = "+k);
                if (k == startFileIndex) k++;
                if (k == lastK) { // prevent an infinite loop.
                    if (i != k) {
                        if (i == startFileIndex) {
                            i = k; // We need to accept a longer execution string :-(
                        } else {
                            k = i; // We're in the loop and the exec string was too long
                            j = i;
                        }
                    } else {
                        break;
                    }
                }
                execTemp = getTempExec(startFileIndex, k, fileIndexes, exec,
                                       nVARS, varNames, varValues, tempVars);
                //System.out.print("  exec "+((execTemp.length() < maxCmdLength) ? "<" : ">=")+" MAX =>");
                if (execTemp.length() < maxCmdLength) {
                    i = k;
                    //j = k; // For simplicity stop it here. Do not look for longer exec.
                } else {
                    j = k;
                }
                lastK = k;
                //System.out.println("  i = "+i+", j = "+j+", k = "+k+", exec length ("+execTemp.length()+")"+((execTemp.length() < maxCmdLength) ? "<" : ">=")+maxCmdLength);
            }
            //System.out.println("  FINAL: i = "+i+", j = "+j+", k = "+lastK+", exec length ("+execTemp.length()+")"+((execTemp.length() < maxCmdLength) ? "<" : ">=")+maxCmdLength);
            execs.add(execTemp);
            startFileIndex = i;
        } while (startFileIndex < nFiles);
        /*
        System.out.println("return splitted:");
        for (int i = 0; i < execs.size(); i++) {
            System.out.println("  '"+execs.get(i)+"'");
        }
        System.out.println("========");
         */
        return (String[]) execs.toArray(new String[0]);
    }
    
    private String getTempExec(int j, int k, int[][] fileIndexes, String exec,
                               int nVARS, String[] varNames, String[] varValues,
                               Hashtable vars) {
        for (int i = 0; i < nVARS; i++) {
            String varTempValue;
            if (j < k) {
                if (k < fileIndexes[i].length) {
                    varTempValue = varValues[i].substring(fileIndexes[i][j], fileIndexes[i][k] - 1);
                } else {
                    varTempValue = varValues[i].substring(fileIndexes[i][j]);
                }
            } else {
                varTempValue = "";
            }
            vars.put(varNames[i], varTempValue);
            //System.out.println("  Temp var "+varNames[i]+" = "+varTempValue);
        }
        String execTemp = Variables.expand(vars, exec, false);
        execTemp = execTemp.trim();
        //System.out.println("tempExec("+j+", "+k+") = '"+execTemp+"'");
        return execTemp;
    }

    
    /**
     * Search for optional methods and set additional parameters.
     */
    protected static void setAdditionalParams(Object execCommand, VcsFileSystem fileSystem) {
        setAdditionalParams(execCommand, fileSystem, fileSystem);
    }
    
    /**
     * Search for optional methods and set additional parameters.
     */
    protected static void setAdditionalParams(Object execCommand, VcsFileSystem fileSystem,
                                              CommandExecutionContext executionContext) {
        Class clazz = execCommand.getClass();
        if (fileSystem != null) {
            Class[] paramClasses = new Class[] { VcsFileSystem.class };
            Method setFileSystemMethod = null;
            try {
                setFileSystemMethod = clazz.getDeclaredMethod("setFileSystem", paramClasses);
            } catch (Exception exc) {
                setFileSystemMethod = null;
            }
            if (setFileSystemMethod != null) {
                Object[] args = new Object[] { fileSystem };
                try {
                    setFileSystemMethod.invoke(execCommand, args);
                } catch (Exception iae) {
                    // silently ignored
                }
            }
        }
        Class[] paramClasses = new Class[] { CommandExecutionContext.class };
        Method setExecutionContextMethod = null;
        try {
            setExecutionContextMethod = clazz.getDeclaredMethod("setExecutionContext", paramClasses);
        } catch (Exception exc) {
            setExecutionContextMethod = null;
        }
        if (setExecutionContextMethod != null) {
            Object[] args = new Object[] { executionContext };
            try {
                setExecutionContextMethod.invoke(execCommand, args);
            } catch (Exception iae) {
                // silently ignored
            }
        }
    }
    
    /**
     * Get the set of files being processed by the command.
     * @return the set of files of type <code>String</code> relative
     * to the file system root.
     */
    public Collection getFiles() {
        if (processingFilesCollection == null) {
            processingFilesCollection = createProcessingFiles(executionContext, vars);
        }
        return processingFilesCollection;
    }
    
    /**
     * Get the set of files being processed by the command.
     * @return the set of files of type <code>String</code> relative
     * to the file system root.
     */
    public static Collection createProcessingFiles(CommandExecutionContext executionContext, Hashtable vars) {
        VariableValueAdjustment valueAdjustment = executionContext.getVarValueAdjustment();
        String separator = (String) vars.get("PS");
        char separatorChar = (separator != null && separator.length() == 1) ? separator.charAt(0) : java.io.File.separatorChar;
        String paths = (String) vars.get("PATHS");
        String commonParent = (String) vars.get("COMMON_PARENT");
        if (commonParent != null) commonParent = valueAdjustment.revertAdjustedVarValue(commonParent);
        paths = valueAdjustment.revertAdjustedVarValue(paths);
        if (paths != null && paths.length() > 0) {
            ArrayList files = new ArrayList();
            int len = paths.length();
            int begin = 0;
            do {
                int index = paths.indexOf(""+separatorChar + separatorChar, begin);
                if (index < 0) index = len;
                String file = paths.substring(begin, index);
                if (commonParent != null) {
                    file = commonParent + "/" + file;
                }
                files.add(file.replace(separatorChar, '/'));
                begin = index + 2;
            } while (begin < len);
            return Collections.unmodifiableList(files);
        } else {
            String path = (String) vars.get("DIR");
            String file = (String) vars.get("FILE");
            file = valueAdjustment.revertAdjustedVarValue(file);
            path = valueAdjustment.revertAdjustedVarValue(path);
            if (path != null) {
                if (commonParent != null) {
                    path = commonParent + "/" + path;
                }
                String fullPath = ((path.length() > 0) ? path.replace(separatorChar, '/') : "") + ((file == null) ? "" : "/" + file);
                return Collections.singleton(fullPath);
            } else {
                return Collections.EMPTY_SET;
            }
        }
    }
    
    /** Initialize refreshFilesBase and refreshFilesMustStartWith fields. */
    private void createRefreshFilesBase() {
        refreshFilesBase = (String) cmd.getProperty(
                                UserCommand.PROPERTY_REFRESH_FILE_RELATIVE_PATH);
        if (refreshFilesBase == null) {
            refreshFilesBase = (String) vars.get("COMMON_PARENT");
            refreshFilesMustStartWith = null;
        } else {
            refreshFilesBase = Variables.expand(vars, refreshFilesBase, false);
            String relMountPoint = fileSystem.getRelativeMountPoint();
            //System.out.println("createRefreshFilesBase(): base = '"+refreshFilesBase+"', rel. M. = '"+relMountPoint+"'");
            if (refreshFilesBase.length() <= relMountPoint.length()) {
                if (!relMountPoint.startsWith(refreshFilesBase)) {
                    doFileRefresh = false; // The base and rel. mount point do not match!
                    return ; // The file does not match with this relative mount point
                }
                //fileName = fileName.substring(commonParent.length() + 1);
                refreshFilesMustStartWith = relMountPoint.substring(refreshFilesBase.length());
                while (refreshFilesMustStartWith.startsWith("/")) {
                    refreshFilesMustStartWith = refreshFilesMustStartWith.substring(1);
                }
                if (refreshFilesMustStartWith.length() == 0) {
                    refreshFilesMustStartWith = null;
                }
                refreshFilesBase = null;
                //System.out.println("createRefreshFilesBase(): base < rel. M.: must start with '"+refreshFilesMustStartWith+"', base = null");
            } else {
                if (!refreshFilesBase.startsWith(relMountPoint)) {
                    doFileRefresh = false; // The base and rel. mount point do not match!
                    //System.out.println("createRefreshFilesBase(): base '"+refreshFilesBase+"' does not start with Rel Mount '"+relMountPoint+"'!");
                    return ; // The common parent does not match with this relative mount point
                }
                refreshFilesBase = refreshFilesBase.substring(relMountPoint.length());
                while (refreshFilesBase.startsWith("/")) {
                    refreshFilesBase = refreshFilesBase.substring(1);
                }
                if (refreshFilesBase.length() == 0) refreshFilesBase = null;
                refreshFilesMustStartWith = null;
                //System.out.println("createRefreshFilesBase(): base >= rel. M.: must start with '"+refreshFilesMustStartWith+"', base = '"+refreshFilesBase+"'");
            }
        }
    }
    
    private void collectRefreshInfo(String[] elements) {
        //System.out.println("collectRefreshInfo("+VcsUtilities.arrayToString(elements)+")");
        elements = CommandLineVcsDirReader.translateElements(elements, cmd);
        if (elements.length == 1) {
            flushRemoveFile(elements[0]);
            return ;
        }
        //This is inefficient,refreshing each time the status gets updated.
        //Just correct the elements here and do one refresh later
        //
        //System.out.println("  translated = "+VcsUtilities.arrayToString(elements));
        //if (elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME] != null &&
        //    elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME].trim().length() > 0) {
        //        
        //    flushRefreshInfo();
        //}
        refreshInfoElements.add(elements);
    }
    
    private void flushRemoveFile(String fileName) {
        String fileDir = "";
        String filePath;
        fileName.replace(java.io.File.separatorChar, '/');
        if (refreshFilesMustStartWith != null) {
            if (!fileName.startsWith(refreshFilesMustStartWith)) {
                // the file does not match our requirements
                return ;
            }
            fileName = fileName.substring(refreshFilesMustStartWith.length() + 1);
        }
        if (refreshFilesBase != null) {
            filePath = refreshFilesBase + "/" + fileName;
            fileDir = VcsUtilities.getDirNamePart(filePath);
            fileName = VcsUtilities.getFileNamePart(filePath);
        } else {
            int sepIndex = fileName.indexOf('/');
            if (sepIndex < 0 || sepIndex == (fileName.length() - 1)) {
                fileDir = findFileDir(fileName);
                if (fileName.startsWith(fileDir + "/")) {
                    //System.out.println("fileName = "+fileName+", fileDir = "+fileDir+", substring("+(fileDir.length() + 1)+")");
                    fileName = fileName.substring(fileDir.length() + 1);
                }
                if (fileDir.length() == 0) {
                    filePath = fileName;
                } else {
                    filePath = fileDir + "/" + fileName;
                }
            } else {
                fileDir = findFileDir(fileName);
                if (fileDir.length() == 0) {
                    filePath = fileName;
                } else {
                    filePath = fileDir + "/" + fileName;
                }
                fileDir = VcsUtilities.getDirNamePart(filePath);
                fileName = VcsUtilities.getFileNamePart(filePath);
            }
        }
        //System.out.println("readFileFinished("+fileDir+", [REMOVED:] "+fileName+")");
        sendRefreshInfoRemove(fileDir, new String[] { fileName });
        filesToRefresh.remove(filePath);
    }
    
    private void flushRefreshInfo() {
        //System.out.println("flushRefreshInfo()");
        String name = null;
        String[] elements = mergeInfoElements();
        //System.out.println("  merged elements = "+VcsUtilities.arrayToString(elements)+", commonParent = "+commonParent);
        for (; elements != null; elements = mergeInfoElements()) {
            if (elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME] != null &&
                elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME].trim().length() > 0) {
                
                elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME] = 
                    elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME].replace(java.io.File.separatorChar, '/');
                String fileName = elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME];
                String fileDir = "";
                String filePath;
                if (refreshFilesMustStartWith != null) {
                    if (!fileName.startsWith(refreshFilesMustStartWith)) {
                        // the file does not match our requirements
                        return ;
                    }
                    if (refreshFilesMustStartWith.length() < fileName.length()) {
                        fileName = fileName.substring(refreshFilesMustStartWith.length() + 1);
                    } else {
                        fileName = "";
                        // We do not refresh the FS root. A support for that would have to be made.
                        return ;
                    }
                }
                if (refreshFilesBase != null) {
                    filePath = refreshFilesBase + "/" + fileName;
                    fileDir = VcsUtilities.getDirNamePart(filePath);
                    fileName = VcsUtilities.getFileNamePart(filePath);
                    elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME] = fileName;
                } else {
                    int sepIndex = fileName.indexOf('/');
                    if (sepIndex < 0 || sepIndex == (fileName.length() - 1)) {
                        fileDir = findFileDir(fileName);
                        if (fileName.startsWith(fileDir + "/")) {
                            //System.out.println("fileName = "+fileName+", fileDir = "+fileDir+", substring("+(fileDir.length() + 1)+")");
                            fileName = fileName.substring(fileDir.length() + 1);
                        }
                        if (fileDir.length() == 0) {
                            filePath = fileName;
                        } else {
                            filePath = fileDir + "/" + fileName;
                        }
                    } else {
                        fileDir = findFileDir(fileName);
                        if (fileDir.length() == 0) {
                            filePath = fileName;
                        } else {
                            filePath = fileDir + "/" + fileName;
                        }
                        fileDir = VcsUtilities.getDirNamePart(filePath);
                        fileName = VcsUtilities.getFileNamePart(filePath);
                    }
                    elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME] = fileName;
                }
                if (substituteStatuses) {
                    elements = performStatusSubstitution(elements);
                    if (elements == null) {
                        if (!doPostExecutionRefresh) {
                            filesToRefresh.add(filePath);
                        }
                        continue;
                    }
                }
                fileName = elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME];
                if (!fileName.endsWith("/") && fileSystem.folder(fileDir+"/"+fileName)) {
                    fileName += "/";
                    elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME] = fileName;
                }
                //correct the directory and files pair so that we can refresh them
                //later at once.
                //
                if( !lastCollectedFolder.containsKey(fileDir)) 
                    lastCollectedFolder.put(fileDir, new HashSet());
                ((HashSet)lastCollectedFolder.get(fileDir)).add(elements);
                //System.out.println("readFileFinished("+fileDir+", "+VcsUtilities.arrayToString(elements)+")");
                filesToRefresh.remove(filePath);
            }
        }
    }
    
    private String[] mergeInfoElements() {
        String[] elements = null;
        while(refreshInfoElements.size() > 0) {
            String[] elements1 = (String[]) refreshInfoElements.get(0);
            if (elements == null) {
                elements = elements1;
                refreshInfoElements.remove(0);
            } else {
                if (elements1[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME] == null ||
                    elements1[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME].trim().length() == 0) {
                    elements = mergeElements(elements, elements1);
                    refreshInfoElements.remove(0);
                } else {
                    break;
                }
            }
        }
        return elements;
    }
    
    private String[] mergeElements(String[] e1, String[] e2) {
        for (int i = 0; i < e1.length && i < e2.length; i++) {
            if (i == RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME) continue;
            if (e1[i] == null || (e1[i].trim().length() == 0 && e2[i] != null && e2[i].trim().length() > 0)) {
                e1[i] = e2[i];
            }
        }
        return e1;
    }
    
    private String[] performStatusSubstitution(String[] elements) {
        String status = elements[RefreshCommandSupport.ELEMENT_INDEX_STATUS];
        if (status == null) return elements;
        for (int i = 0; i < substituitionRegExps.length; i++) {
            Matcher matcher = substituitionRegExps[i].matcher(status);
            if (matcher.matches()) {
                if (STATUS_USE_REG_EXP_PARSE_OUTPUT.equals(substituitionStatuses[i])) {
                    status = matcher.group();
                } else {
                    status = substituitionStatuses[i];
                }
                elements[RefreshCommandSupport.ELEMENT_INDEX_STATUS] = status;
                return elements;
            }
        }
        return null;
    }
    
    private String findFileDir(String name) {
        String dir = "";
        Collection files;
        if (doPostExecutionRefresh) {
            files = filesToRefresh;
        } else {
            files = getFiles();
        }
        char sepChar = '/';
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            String filePath = (String) it.next();
            int sepIdx = filePath.lastIndexOf(name);
            if (sepIdx > 0 ) {
                if (filePath.charAt(sepIdx-1) == sepChar
                        && (sepIdx + name.length()) == filePath.length()) {
                    dir = filePath.substring(0, filePath.length() - name.length() - 1);
                }
                break;
            } else if (sepIdx == 0)
                break;
        }
        return dir;
    }
    
    private void refreshRemainingFiles() {
        if (!UserCommand.NAME_REFRESH_FILE.equals(cmd.getName())) {
            if (filesToRefresh != null && filesToRefresh.size() > 0) {
                doRefreshFiles(fileSystem, filesToRefresh);
            } else if (VcsCommandIO.getBooleanProperty(cmd, UserCommand.PROPERTY_REFRESH_PROCESSED_FILES)) {
                doRefreshFiles(fileSystem, getFiles());
            }
        }
    }
    
    private void doRefreshFiles(VcsFileSystem fileSystem, Collection filesPaths) {
        CommandSupport cmdSupp = fileSystem.getCommandSupport(UserCommand.NAME_REFRESH_FILE);
        if (cmdSupp != null) {
            Command cmd = cmdSupp.createCommand();
            //if (cmd instanceof VcsDescribedCommand) {
            //    VcsDescribedCommand vcsCmd = (VcsDescribedCommand) cmd;
            //}
            List foFiles = new ArrayList();
            List diskFiles = new ArrayList();
            //Table files = new Table();
            for (Iterator it = filesPaths.iterator(); it.hasNext(); ) {
                String file = (String) it.next();
                FileObject fo = fileSystem.findFileObject(file);
                if (fo != null) {
                    foFiles.add(fo);
                } else {
                    diskFiles.add(fileSystem.getFile(file));
                }
                //files.put(file, fileSystem.findFileObject(file));
            }
            if (foFiles.size() > 0) {
                cmd.setFiles((FileObject[]) foFiles.toArray(new FileObject[foFiles.size()]));
            }
            if (cmd instanceof VcsDescribedCommand) {
                if (diskFiles.size() > 0) {
                    ((VcsDescribedCommand) cmd).setDiskFiles((File[]) diskFiles.toArray(new java.io.File[diskFiles.size()]));
                }
                for (Iterator it = fileReaderListeners.iterator(); it.hasNext(); ) {
                    ((VcsDescribedCommand) cmd).addFileReaderListener((FileReaderListener) it.next());
                }
            }
            VcsManager.getDefault().showCustomizer(cmd);
            cmd.execute();
        }
    }
    
    private HashMap lastCollectedFolder = new HashMap();
    private Set lastCollectedElements = null;
    
    /**
     * For delete_file command, we have no choice, but do refresh per each file.
     * @param folder directory where the file is
     * @param elements file to remove
     */
    private void sendRefreshInfoRemove(String folder, String [] elements) {
        //if (!folder.equals(lastCollectedFolder)) {
        //   if (lastCollectedFolder != null) {
        if (lastCollectedElements == null) lastCollectedElements = new HashSet();
            lastCollectedElements.add(elements);
        for (Iterator it = new ArrayList(fileReaderListeners).iterator();
            it.hasNext(); ) {
            ((FileReaderListener) it.next()).readFileFinished(folder,//lastCollectedFolder,
            lastCollectedElements);//lastCollectedElements);
        }
        lastCollectedElements.clear();
        //}
        //lastCollectedFolder = folder;
        //}
        //if (lastCollectedElements == null) lastCollectedElements = new HashSet();
        //lastCollectedElements.add(elements);

    }
    
    /**
     * This methods sends the remaining refresh information to the listeners
     * and cleanups the collected elements. LastCollectedFolder is a map
     * of directory name and the files in the directory which are corrected
     * while the command is running. 
     */
    private void cleanupSendRefreshInfo() {
        Iterator dirSet = lastCollectedFolder.entrySet().iterator();
        while (dirSet.hasNext()) {
            Map.Entry entry = ( Map.Entry)dirSet.next();
            for (Iterator it = new ArrayList(fileReaderListeners).iterator();
                 it.hasNext(); ) {
               
                ((FileReaderListener) it.next()).readFileFinished((String)entry.getKey(),
                                                                  (Set)entry.getValue());
            }
        }
        lastCollectedFolder.clear();
        lastCollectedElements = null;
    }

    /**
     * Add a file reader listener, that gets the updated attributes of the
     * processed file(s).
     */
    public void addFileReaderListener(FileReaderListener l) {
        if (fileReaderListeners != null) fileReaderListeners.add(l);
    }

    //-------------------------------------------
    private static final String g(String s) {
        return NbBundle.getMessage(ExecuteCommand.class, s);
    }
    private static final String  g(String s, Object obj) {
        return NbBundle.getMessage(ExecuteCommand.class, s, obj);
    }
    private static final String g(String s, Object obj1, Object obj2) {
        return NbBundle.getMessage(ExecuteCommand.class, s, obj1, obj2);
    }
    private static final String g(String s, Object obj1, Object obj2, Object obj3) {
        return NbBundle.getMessage(ExecuteCommand.class, s, obj1, obj2, obj3);
    }
    
}
