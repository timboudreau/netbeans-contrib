/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.cmdline;

import java.io.*;
import java.util.*;
import java.beans.*;
import java.text.*;
import java.lang.reflect.*;

import org.apache.regexp.*;

import org.openide.util.*;
import org.openide.TopManager;

import org.netbeans.modules.vcscore.cmdline.exec.*;
import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.caching.RefreshCommandSupport;
import org.netbeans.modules.vcscore.commands.*;
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

    private VcsFileSystem fileSystem = null;
    private UserCommand cmd = null;
    private Hashtable vars = null;
    private String preferredExec = null;

    //private RegexListener stdoutListener = null;
    //private RegexListener stderrListener = null;

    //private NoRegexListener stdoutNoRegexListener = null;
    //private NoRegexListener stderrNoRegexListener = null;

    //private OutputContainer errorContainer = null;
    
    private ArrayList commandOutputListener = new ArrayList(); 
    private ArrayList commandErrorOutputListener = new ArrayList(); 
    private ArrayList commandDataOutputListener = new ArrayList(); 
    private ArrayList commandDataErrorOutputListener = new ArrayList(); 
    private ArrayList fileReaderListeners = new ArrayList();
    private boolean doFileRefresh; // Whether this command provides updated status of processed files
    private boolean doPostExecutionRefresh; // Whether this command refresh status of all processed files
    private boolean getFileRefreshFromErrOut = false; // Whether to read the refresh info from the error data output also
    private ArrayList filesToRefresh; // The list of files, that were not refreshed.
                                      // Files, that were not refreshed when the command finish
                                      // will be refreshed by the LIST_FILE command (if present).
    private ArrayList refreshInfoElements;
    
    private boolean substituteStatuses = false;
    private RE[] substituitionRegExps;
    private String[] substituitionStatuses;

    private Collection processingFilesCollection = null;
    
    private int exitStatus = 0;

    //private ArrayList commandListeners = new ArrayList();

    //-------------------------------------------
    public ExecuteCommand(VcsFileSystem fileSystem, UserCommand cmd, Hashtable vars) {
        this(fileSystem, cmd, vars, null);
    }

    public ExecuteCommand(VcsFileSystem fileSystem, UserCommand cmd, Hashtable vars, String preferredExec) {
        //super("VCS-ExecuteCommand-"+cmd.getName()); // NOI18N
        this.fileSystem = fileSystem;
        this.cmd = cmd;
        this.vars = vars;
        if (preferredExec == null) {
            preferredExec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        }
        this.preferredExec = preferredExec;
        this.doFileRefresh =
            VcsCommandIO.getIntegerPropertyAssumeNegative(cmd, UserCommand.PROPERTY_LIST_INDEX_FILE_NAME) >= 0;
        this.doPostExecutionRefresh =
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
        RE[] regExps = new RE[n];
        String[] statuses = new String[n];
        int nn = n;
        for (int i = 0; i < n; i++) {
            try {
                regExps[i] = new RE(substitutions[2*i]);
            } catch(RESyntaxException e) {
                //E.err(e,"RE failed regexp"); // NOI18N
                TopManager.getDefault().notifyException(
                    TopManager.getDefault().getErrorManager().annotate(e,
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
            substituitionRegExps = new RE[n];
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
     * Add the listener to the standard output of the command. The listeners are removed
     * when the command finishes.
     */
    public synchronized void addOutputListener(CommandOutputListener l) {
        if (commandOutputListener != null) commandOutputListener.add(l);
    }
    
    /**
     * Add the listener to the error output of the command. The listeners are removed
     * when the command finishes.
     */
    public synchronized void addErrorOutputListener(CommandOutputListener l) {
        if (commandErrorOutputListener != null) commandErrorOutputListener.add(l);
    }
    
    /**
     * Add the listener to the data output of the command. This output may contain
     * a parsed information from its standard output or some other data provided
     * by this command. The listeners are removed when the command finishes.
     */
    public synchronized void addDataOutputListener(CommandDataOutputListener l) {
        if (commandDataOutputListener != null) commandDataOutputListener.add(l);
    }

    /**
     * Add the listener to the data error output of the command. This output may contain
     * a parsed information from its error output or some other data provided
     * by this command. If there are some data given to this listener, the command
     * is supposed to fail. The listeners are removed when the command finishes.
     */
    public synchronized void addDataErrorOutputListener(CommandDataOutputListener l) {
        if (commandDataErrorOutputListener != null) commandDataErrorOutputListener.add(l);
    }

    public VcsCommand getCommand() {
        return cmd;
    }
    
    /**
     * Get the variables used by this command execution.
     */
    public Hashtable getVariables() {
        return vars;
    }

    //-------------------------------------------
    public int getExitStatus(){
        return exitStatus;
    }
    
    /**
     * Get the graphical visualization of the command.
     * @return null no visualization is desired.
     */
    public VcsCommandVisualizer getVisualizer() {
        return null;
    }
    
    private void commandFinished(String exec, boolean success) {
        commandOutputListener.clear();
        commandErrorOutputListener.clear();
        commandDataOutputListener.clear();
        commandDataErrorOutputListener.clear();
        if (doFileRefresh) {
            flushRefreshInfo();
        }
        fileReaderListeners.clear();
        commandOutputListener = null;
        commandErrorOutputListener = null;
        commandDataOutputListener = null;
        commandDataErrorOutputListener = null;
        fileReaderListeners = null;
        if (success) {
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
    }

    /**
     * This method can be used to do some preprocessing of the command which is to be run.
     * @param vc the command to be preprocessed.
     * @param vars the variables
     * @param exec the updated execution string. It may contain user input from variable input dialog
     * @return the updated exec property
     */
    public String preprocessCommand(VcsCommand vc, Hashtable vars, String exec) {
        this.preferredExec = exec;
        fileSystem.getVarValueAdjustment().adjustVarValues(vars);
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
        return preferredExec;
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
    private StringBuffer[] addRegexListeners(final ExternalCommand ec, final RE[] globalRegexs) {
        String dataRegex = (String) cmd.getProperty(UserCommand.PROPERTY_DATA_REGEX);
        if (dataRegex == null) dataRegex = DEFAULT_REGEX;
        String errorRegex = (String) cmd.getProperty(UserCommand.PROPERTY_ERROR_REGEX);
        if (errorRegex == null) errorRegex = DEFAULT_REGEX;
        String dataRegexGlobal = (String) cmd.getProperty(UserCommand.PROPERTY_DATA_REGEX_GLOBAL);
        String errorRegexGlobal = (String) cmd.getProperty(UserCommand.PROPERTY_ERROR_REGEX_GLOBAL);
        RE dataRegexGlobalRE = null;
        RE errorRegexGlobalRE = null;
        final StringBuffer dataOutput;
        final StringBuffer errorOutput;
        if (dataRegexGlobal != null) {
            try {
                dataRegexGlobalRE = new RE(dataRegexGlobal);
            } catch (RESyntaxException exc) {
                TopManager.getDefault().notifyException(
                    TopManager.getDefault().getErrorManager().annotate(exc,
                        NbBundle.getMessage(ExternalCommand.class, "BadRegexMessageInfo", dataRegexGlobal)));
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
                errorRegexGlobalRE = new RE(errorRegexGlobal);
            } catch (RESyntaxException exc) {
                TopManager.getDefault().notifyException(
                    TopManager.getDefault().getErrorManager().annotate(exc,
                        NbBundle.getMessage(ExternalCommand.class, "BadRegexMessageInfo", errorRegexGlobal)));
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
                ec.addStdoutRegexListener(new CommandDataOutputListener() {
                                              public void outputData(String[] data) {
                                                  printDataOutput(data);
                                              }
                                          }, dataRegex);
            } else {
                ec.addStdoutRegexListener(new CommandDataOutputListener() {
                                              public void outputData(String[] data) {
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
            TopManager.getDefault().notifyException(
                TopManager.getDefault().getErrorManager().annotate(e,
                    NbBundle.getMessage(ExternalCommand.class, "BadRegexMessageInfo", dataRegex)));
        }
        try {
            if (errorRegexGlobalRE == null) {
                ec.addStderrRegexListener(new CommandDataOutputListener() {
                                              public void outputData(String[] data) {
                                                  printDataErrorOutput(data);
                                              }
                                          }, errorRegex);
            } else {
                ec.addStderrRegexListener(new CommandDataOutputListener() {
                                              public void outputData(String[] data) {
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
            TopManager.getDefault().notifyException(
                TopManager.getDefault().getErrorManager().annotate(e,
                    NbBundle.getMessage(ExternalCommand.class, "BadRegexMessageInfo", errorRegex)));
        }
        if (dataOutput != null || errorOutput != null) {
            return new StringBuffer[] { dataOutput, errorOutput };
        } else {
            return null;
        }
    }
    
    private void printGlobalDataOutput(String globalDataOutput, RE globalDataRegex) {
        if (globalDataOutput.endsWith(" ")) {
            globalDataOutput = globalDataOutput.substring(0, globalDataOutput.length() - 1);
        }
        String[] parsed = ExternalCommand.matchToStringArray(globalDataRegex, globalDataOutput);
        printDataOutput(parsed);
    }
    
    private void printGlobalErrorDataOutput(String globalDataOutput, RE globalDataRegex) {
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
    private void runCommand(String exec){
        E.deb("runCommand: "+exec); // NOI18N

        exec = Variables.expand(vars,exec, true);
        preferredExec = exec;
        ExternalCommand ec = new ExternalCommand(exec);
        //ec.setTimeout(cmd.getTimeout());
        ec.setInput((String) cmd.getProperty(UserCommand.PROPERTY_INPUT));
        ec.setEnv(fileSystem.getEnvironmentVars());
        //D.deb(cmd.getName()+".getInput()='"+cmd.getInput()+"'"); // NOI18N

        RE[] globalRegexs = new RE[2];
        StringBuffer[] globalDataOutput = addRegexListeners(ec, globalRegexs);

        for (Iterator it = commandOutputListener.iterator(); it.hasNext(); ) {
            ec.addStdoutListener((CommandOutputListener) it.next());
        }
        for (Iterator it = commandErrorOutputListener.iterator(); it.hasNext(); ) {
            ec.addStderrListener((CommandOutputListener) it.next());
        }

        E.deb("ec="+ec); // NOI18N
        exitStatus = ec.exec();
        if (globalDataOutput != null) {
            if (globalDataOutput[0] != null) {
                printGlobalDataOutput(globalDataOutput[0].toString(), globalRegexs[0]);
            }
            if (globalDataOutput[1] != null) {
                printGlobalErrorDataOutput(globalDataOutput[1].toString(), globalRegexs[1]);
            }
        }
        E.deb("Command exited with exit status = "+exitStatus); // NOI18N
        //D.deb("errorContainer = "+errorContainer); // NOI18N
        switch (exitStatus) {
        case VcsCommandExecutor.SUCCEEDED:
            commandFinished(exec, true);
            break;
        case VcsCommandExecutor.INTERRUPTED:
            //commandFinished(exec, false);
            //break;
            // Do the same as when the command fails.
        case VcsCommandExecutor.FAILED:
            commandFinished(exec, false);
            fileSystem.removeNumDoAutoRefresh((String) vars.get("DIR")); // NOI18N
            break;
        }

        D.deb("run("+cmd.getName()+") finished"); // NOI18N
    }
    
    private void printOutput(String line) {
        for (Iterator it = commandOutputListener.iterator(); it.hasNext(); ) {
            ((CommandOutputListener) it.next()).outputLine(line);
        }
    }

    private void printErrorOutput(String line) {
        for (Iterator it = commandErrorOutputListener.iterator(); it.hasNext(); ) {
            ((CommandOutputListener) it.next()).outputLine(line);
        }
    }

    private void printDataOutput(String[] data) {
        for (Iterator it = commandDataOutputListener.iterator(); it.hasNext(); ) {
            ((CommandDataOutputListener) it.next()).outputData(data);
        }
        if (doFileRefresh) {
            collectRefreshInfo(data);
        }
    }

    private void printDataErrorOutput(String[] data) {
        for (Iterator it = commandDataErrorOutputListener.iterator(); it.hasNext(); ) {
            ((CommandDataOutputListener) it.next()).outputData(data);
        }
        if (getFileRefreshFromErrOut) {
            collectRefreshInfo(data);
        }
    }

    /**
     * Loads class of given name with some arguments and execute its list() method.
     * @param className the name of the class to be loaded
     * @param tokens the arguments
     */
    private void runClass(String exec, String className, StringTokenizer tokens) {

        E.deb("runClass: "+className); // NOI18N
        boolean success = true;
        Class execClass = null;
        try {
            execClass =  Class.forName(className, true,
                                       org.openide.TopManager.getDefault().currentClassLoader());
        } catch (ClassNotFoundException e) {
            //fileSystem.debug ("EXEC: " + g("ERR_ClassNotFound", className)); // NOI18N
            try {
                printErrorOutput("CLASS EXEC: " + g("ERR_ClassNotFound", className)); // NOI18N
            } catch(java.util.MissingResourceException mrexc) {
                // Likely to be called when the module is being uninstalled
                printErrorOutput("CLASS EXEC: Class " + className + " not found"); // NOI18N
            }
            success = false;
            return;
        }
        D.deb(execClass+" loaded"); // NOI18N
        VcsAdditionalCommand execCommand = null;
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
            return;
        } catch (IllegalAccessException e) {
            printErrorOutput("CLASS EXEC: "+g("ERR_IllegalAccessOnClass", execClass)); // NOI18N
            /*
            fileSystem.debug ("EXEC: "+g("ERR_IllegalAccessOnClass", execClass)); // NOI18N
            if (stderrNoRegexListener != null)
                stderrNoRegexListener.match("EXEC: "+g("ERR_IllegalAccessOnClass", execClass)); // NOI18N
             */
            success = false;
            return;
        }
        E.deb("VcsAdditionalCommand created."); // NOI18N
        String[] args = new String[tokens.countTokens()];
        int i = 0;
        while(tokens.hasMoreTokens()) {
            args[i++] = tokens.nextToken();
        }
        if (success) {
            ExecuteCommand.setAdditionalParams(execCommand, fileSystem);
            String dataRegex = (String) cmd.getProperty(UserCommand.PROPERTY_DATA_REGEX);
            String errorRegex = (String) cmd.getProperty(UserCommand.PROPERTY_ERROR_REGEX);
            String input = (String) cmd.getProperty(UserCommand.PROPERTY_INPUT);
            if (dataRegex != null) vars.put("DATAREGEX", dataRegex); // NOI18N
            if (errorRegex != null) vars.put("ERRORREGEX", errorRegex); // NOI18N
            if (input != null) vars.put("INPUT", input); // NOI18N
            //vars.put("TIMEOUT", new Long(cmd.getTimeout())); // NOI18N
            //TopManager.getDefault().setStatusText(g("MSG_Command_name_running", cmd.getName()));
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
        }
        if (Thread.currentThread().interrupted()) {
            exitStatus = VcsCommandExecutor.INTERRUPTED;
            commandFinished(exec, false);
            fileSystem.removeNumDoAutoRefresh((String) vars.get("DIR")); // NOI18N
        } else {
            if (success) {
                exitStatus = VcsCommandExecutor.SUCCEEDED;
                commandFinished(exec, true);
            } else {
                exitStatus = VcsCommandExecutor.FAILED;
                commandFinished(exec, false);
                fileSystem.removeNumDoAutoRefresh((String) vars.get("DIR")); // NOI18N
            }
        }
    }

    /**
     * Execute the command.
     */
    public void run() {
        //isRunning = true;
        //hasStarted = true;
        String exec;
        //System.out.println("ExecuteCommand.run(): exec = "+exec+"\npreferredExec = "+preferredExec);
        if (preferredExec != null) exec = preferredExec;
        else exec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        if (exec == null) return ; // Silently ignore null exec
        exec = Variables.expand(vars, exec, true);
        exec = exec.trim();
        if (exec.trim().length() == 0) {
            preferredExec = "";
            return ; // Silently ignore empty exec
        }
        if (doPostExecutionRefresh) {
            filesToRefresh = new ArrayList(getFiles()); // All files should be refreshed at the end.
        } else if (doFileRefresh) {
            filesToRefresh = new ArrayList(); // Only some files (with unmatched status) should be refreshed.
        }
        
        StringTokenizer tokens = new StringTokenizer(exec);
        String first = tokens.nextToken();
        E.deb("first = "+first); // NOI18N
        boolean disableRefresh = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_CHECK_FOR_MODIFICATIONS);
        if (disableRefresh) fileSystem.disableRefresh();
        if (first != null && (first.toLowerCase().endsWith(".class"))) // NOI18N
            runClass(exec, first.substring(0, first.length() - ".class".length()), tokens); // NOI18N
        else
            runCommand(exec);
        if (disableRefresh) fileSystem.enableRefresh();
    }

    
    /**
     * Search for optional methods and set additional parameters.
     */
    static void setAdditionalParams(Object execCommand, VcsFileSystem fileSystem) {
        Class clazz = execCommand.getClass();
        Class[] paramClasses = new Class[] { VcsFileSystem.class };
        Method setFileSystemMethod = null;
        try {
            setFileSystemMethod = clazz.getDeclaredMethod("setFileSystem", paramClasses);
        } catch (NoSuchMethodException exc) {
            setFileSystemMethod = null;
        } catch (SecurityException excsec) {
            setFileSystemMethod = null;
        }
        if (setFileSystemMethod != null) {
            Object[] args = new Object[] { fileSystem };
            try {
                setFileSystemMethod.invoke(execCommand, args);
            } catch (IllegalAccessException iae) {
                // silently ignored
            } catch (IllegalArgumentException iare) {
                // silently ignored
            } catch (InvocationTargetException ite) {
                // silently ignored
            } catch (ExceptionInInitializerError eie) {
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
            processingFilesCollection = createProcessingFiles(fileSystem, vars);
        }
        return processingFilesCollection;
    }
    
    /**
     * Get the set of files being processed by the command.
     * @return the set of files of type <code>String</code> relative
     * to the file system root.
     */
    public static Collection createProcessingFiles(VcsFileSystem fileSystem, Hashtable vars) {
        VariableValueAdjustment valueAdjustment = fileSystem.getVarValueAdjustment();
        String separator = (String) vars.get("PS");
        char separatorChar = (separator != null && separator.length() == 1) ? separator.charAt(0) : java.io.File.separatorChar;
        String paths = (String) vars.get("PATHS");
        paths = valueAdjustment.revertAdjustedVarValue(paths);
        if (paths != null && paths.length() > 0) {
            ArrayList files = new ArrayList();
            int len = paths.length();
            int begin = 0;
            do {
                int index = paths.indexOf(""+separatorChar + separatorChar, begin);
                if (index < 0) index = len;
                String file = paths.substring(begin, index);
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
                String fullPath = ((path.length() > 0) ? path.replace(separatorChar, '/') + "/" : "") + ((file == null) ? "" : file);
                return Collections.singleton(fullPath);
            } else {
                return Collections.EMPTY_SET;
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
        //System.out.println("  translated = "+VcsUtilities.arrayToString(elements));
        if (elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME] != null &&
            elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME].length() > 0) {
                
            flushRefreshInfo();
        }
        refreshInfoElements.add(elements);
    }
    
    private void flushRemoveFile(String fileName) {
        String fileDir = "";
        String filePath;
        fileName.replace(java.io.File.separatorChar, '/');
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
            filePath = fileName;
            fileDir = VcsUtilities.getDirNamePart(filePath);
            fileName = VcsUtilities.getFileNamePart(filePath);
        }
        //System.out.println("readFileFinished("+fileDir+", [REMOVED:] "+fileName+")");
        for (Iterator it = new ArrayList(fileReaderListeners).iterator(); it.hasNext(); ) {
            ((FileReaderListener) it.next()).readFileFinished(fileDir, Collections.singleton(new String[] { fileName }));
        }
        filesToRefresh.remove(filePath);
    }
    
    private void flushRefreshInfo() {
        //System.out.println("flushRefreshInfo()");
        String name = null;
        String[] elements = mergeInfoElements();
        //System.out.println("  merged elements = "+VcsUtilities.arrayToString(elements));
        for (; elements != null; elements = mergeInfoElements()) {
            if (elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME] != null &&
                elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME].length() > 0) {
                
                elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME].replace(java.io.File.separatorChar, '/');
                String fileName = elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME];
                String fileDir = "";
                String filePath;
                int sepIndex = fileName.indexOf('/');
                if (sepIndex < 0 || sepIndex == (fileName.length() - 1)) {
                    fileDir = findFileDir(fileName);
                    if (fileName.startsWith(fileDir + "/")) {
                        //System.out.println("fileName = "+fileName+", fileDir = "+fileDir+", substring("+(fileDir.length() + 1)+")");
                        fileName = fileName.substring(fileDir.length() + 1);
                        elements[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME] = fileName;
                    }
                    if (fileDir.length() == 0) {
                        filePath = fileName;
                    } else {
                        filePath = fileDir + "/" + fileName;
                    }
                } else {
                    filePath = fileName;
                    fileDir = VcsUtilities.getDirNamePart(filePath);
                    fileName = VcsUtilities.getFileNamePart(filePath);
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
                //System.out.println("readFileFinished("+fileDir+", "+VcsUtilities.arrayToString(elements)+")");
                for (Iterator it = fileReaderListeners.iterator(); it.hasNext(); ) {
                    ((FileReaderListener) it.next()).readFileFinished(fileDir, Collections.singleton(elements));
                }
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
                    elements1[RefreshCommandSupport.ELEMENT_INDEX_FILE_NAME].length() == 0) {
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
            if (substituitionRegExps[i].match(status)) {
                if (STATUS_USE_REG_EXP_PARSE_OUTPUT.equals(substituitionStatuses[i])) {
                    status = substituitionRegExps[i].getParen(0);
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
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            String filePath = (String) it.next();
            if (filePath.endsWith(name)) {
                if (filePath.length() > name.length()) {
                    dir = filePath.substring(0, filePath.length() - name.length() - 1);
                }
                break;
            }
        }
        return dir;
    }
    
    private void refreshRemainingFiles() {
        if (!UserCommand.NAME_REFRESH_FILE.equals(cmd.getName())) {
            if (filesToRefresh != null && filesToRefresh.size() > 0) {
                ExecuteCommand.doRefreshFiles(fileSystem, filesToRefresh);
            } else if (VcsCommandIO.getBooleanProperty(cmd, UserCommand.PROPERTY_REFRESH_PROCESSED_FILES)) {
                ExecuteCommand.doRefreshFiles(fileSystem, getFiles());
            }
        }
    }
    
    private static void doRefreshFiles(VcsFileSystem fileSystem, Collection filesPaths) {
        VcsCommand cmd = fileSystem.getCommand(UserCommand.NAME_REFRESH_FILE);
        if (cmd != null) {
            Table files = new Table();
            for (Iterator it = filesPaths.iterator(); it.hasNext(); ) {
                String file = (String) it.next();
                files.put(file, fileSystem.findFileObject(file));
            }
            VcsAction.doCommand(files, cmd, null, fileSystem);
        }
    }

    /**
     * Add a file reader listener, that gets the updated attributes of the
     * processed file(s).
     */
    public void addFileReaderListener(FileReaderListener l) {
        if (fileReaderListeners != null) fileReaderListeners.add(l);
    }

    //-------------------------------------------
    String g(String s) {
        return NbBundle.getBundle
               ("org.netbeans.modules.vcscore.cmdline.Bundle").getString (s);
    }
    String  g(String s, Object obj) {
        return MessageFormat.format (g(s), new Object[] { obj });
    }
    String g(String s, Object obj1, Object obj2) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2 });
    }
    String g(String s, Object obj1, Object obj2, Object obj3) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2, obj3 });
    }
    
}
