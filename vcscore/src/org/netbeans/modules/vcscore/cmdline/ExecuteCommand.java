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

import org.openide.util.*;
import org.openide.TopManager;

import org.netbeans.modules.vcscore.cmdline.exec.*;
import org.netbeans.modules.vcscore.*;
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
        commandOutputListener = null;
        commandErrorOutputListener = null;
        commandDataOutputListener = null;
        commandDataErrorOutputListener = null;
        if (success) {
            String path = (String) vars.get("DIR") + "/" + (String) vars.get("FILE");
            path = path.replace(java.io.File.separatorChar, '/');
            if (VcsCommandIO.getBooleanProperty(cmd, UserCommand.PROPERTY_CHECK_FOR_MODIFICATIONS)) {
                //System.out.println(cmd.getName()+" finished successfully.");
                fileSystem.checkForModifications(path);
                /*
                org.openide.filesystems.FileObject fo = fileSystem.findResource(path);
                System.out.println("fo("+path+") = "+fo);
                if (fo != null) {
                    System.out.println("calling refresh(true)...");
                    fo.refresh(true);
                }
                 */
            }
            int whatChanged = 0;
            Object changedInfo = null;
            /*
            if (cmd.isChangingNumRevisions()) {
                whatChanged |= RevisionListener.NUM_REVISIONS_CHANGED;
            }
            if (cmd.isChangingRevision()) {
                whatChanged |= RevisionListener.ONE_REVISION_CHANGED;
                changedInfo = vars.get(cmd.getChangedRevisionVariableName());
            }
            if (whatChanged != 0) {
                org.openide.filesystems.FileObject fo = fileSystem.findFileObject(path);
                if (fo != null) fileSystem.fireRevisionsChanged(whatChanged, fo, changedInfo);
            }
             */
            //doRefresh(exec);
        }
        /*
        for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
            ((CommandListener) it.next()).commandDone(this);
        }
         */
    }

    /*
    private void doRefresh(String exec) {
        boolean doRefreshCurrent = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_CURRENT_FOLDER);
        boolean doRefreshParent = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_PARENT_FOLDER);
        if((doRefreshCurrent || doRefreshParent) && fileSystem.getDoAutoRefresh((String) vars.get("DIR"))) { // NOI18N
            //D.deb("Now refresh folder after CheckIn,CheckOut,Lock,Unlock... commands for convenience"); // NOI18N
            fileSystem.setAskIfDownloadRecursively(false); // do not ask if using auto refresh
            String refreshPath = (String) vars.get("DIR");
            refreshPath.replace(java.io.File.separatorChar, '/');
            String refreshPathFile = refreshPath + ((refreshPath.length() > 0) ? "/" : "") + (String) vars.get("FILE");
            if (!doRefreshParent && fileSystem.getCache().isDir(refreshPathFile)) refreshPath = refreshPathFile;
            String pattern = (String) cmd.getProperty(VcsCommand.PROPERTY_REFRESH_RECURSIVELY_PATTERN_MATCHED);
            if (pattern != null && pattern.length() > 0 && exec.indexOf(pattern) >= 0 && !fileSystem.getCache().isFile(refreshPathFile)) {
                fileSystem.getCache().refreshDirRecursive(refreshPath);
            } else {
                fileSystem.getCache().refreshDir(refreshPath); // NOI18N
            }
        }
        if (!(doRefreshCurrent || doRefreshParent)) fileSystem.removeNumDoAutoRefresh((String)vars.get("DIR")); // NOI18N
    }
     */

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
    
    /*
    public void updateExec(String exec) {
        this.preferredExec = exec;
    }
    
    /**
     * Get the updated execution string. It may contain user input now.
     */
    public String getExec() {
        return preferredExec;
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

        String dataRegex = (String) cmd.getProperty(UserCommand.PROPERTY_DATA_REGEX);
        if (dataRegex == null) dataRegex = DEFAULT_REGEX;
        try {
            for (Iterator it = commandDataOutputListener.iterator(); it.hasNext(); ) {
                ec.addStdoutRegexListener((CommandDataOutputListener) it.next(), dataRegex);
            }
            /*
            ec.addStdoutRegexListener(new CommandDataOutputListener () {
                                          public void outputData(String[] elements) {
                                              //D.deb("stdout match:"+VcsUtilities.arrayToString(elements)); // NOI18N
                                              //fileSystem.debug(cmd.getName()+":stdout: "+VcsUtilities.arrayToString(elements)); // NOI18N
                                              if (stdoutListener != null) {
                                                  stdoutListener.match(elements);
                                              }
                                          }
                                      }, dataRegex);
             */
        }
        catch (BadRegexException e) {
            TopManager.getDefault().notifyException(e);
            //E.err(e,"bad regex"); // NOI18N
        }

        String errorRegex = (String) cmd.getProperty(UserCommand.PROPERTY_ERROR_REGEX);
        if (errorRegex == null) errorRegex = DEFAULT_REGEX;
        try {
            for (Iterator it = commandDataErrorOutputListener.iterator(); it.hasNext(); ) {
                ec.addStderrRegexListener((CommandDataOutputListener) it.next(), errorRegex);
            }
            /*
            ec.addStderrRegexListener(new RegexListener () {
                                          public void match(String[] elements) {
                                              //D.deb("stderr match:"+VcsUtilities.arrayToString(elements)); // NOI18N
                                              if (!VcsCommandIO.getBooleanProperty(cmd, UserCommand.PROPERTY_DISPLAY)) {
                                                  fileSystem.debug(cmd.getName()+":stderr: "+VcsUtilities.arrayToString(elements)); // NOI18N
                                              }
                                              if (stderrListener != null) {
                                                  stderrListener.match(elements);
                                              }
                                          }
                                      }, errorRegex);
             */
        }
        catch (BadRegexException e) {
            E.err(e,"bad regex"); // NOI18N
        }

        for (Iterator it = commandOutputListener.iterator(); it.hasNext(); ) {
            ec.addStdoutListener((CommandOutputListener) it.next());
        }
        for (Iterator it = commandErrorOutputListener.iterator(); it.hasNext(); ) {
            ec.addStderrListener((CommandOutputListener) it.next());
        }

        E.deb("ec="+ec); // NOI18N
        exitStatus = ec.exec();
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
    }

    private void printDataErrorOutput(String[] data) {
        for (Iterator it = commandDataErrorOutputListener.iterator(); it.hasNext(); ) {
            ((CommandDataOutputListener) it.next()).outputData(data);
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
            printErrorOutput("CLASS EXEC: " + g("ERR_ClassNotFound", className)); // NOI18N
            /*
            if (stderrNoRegexListener != null)
                stderrNoRegexListener.match("EXEC: " + g("ERR_ClassNotFound", className)); // NOI18N
             */
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
        
        StringTokenizer tokens = new StringTokenizer(exec);
        String first = tokens.nextToken();
        E.deb("first = "+first); // NOI18N
        boolean disableRefresh = VcsCommandIO.getBooleanProperty(cmd, UserCommand.PROPERTY_CHECK_FOR_MODIFICATIONS);
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
            return files;
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
    
    /**
     * Add a file reader listener, that gets the updated attributes of the
     * processed file(s).
     */
    public void addFileReaderListener(FileReaderListener l) {
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
