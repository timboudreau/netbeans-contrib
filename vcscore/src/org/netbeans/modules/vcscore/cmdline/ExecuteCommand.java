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
    
    private static final String DEFAULT_REGEX = "^(.*$)"; // Match the whole line by default.

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
        //super("VCS-ExecuteCommand-"+cmd.getName()); // NOI18N
        //this.fileSystem=fileSystem;
        //this.cmd=cmd;
        //this.vars=vars;
    }

    public ExecuteCommand(VcsFileSystem fileSystem, UserCommand cmd, Hashtable vars, String preferredExec) {
        //super("VCS-ExecuteCommand-"+cmd.getName()); // NOI18N
        this.fileSystem = fileSystem;
        this.cmd = cmd;
        this.vars = vars;
        //this(fileSystem, cmd, vars);
        if (preferredExec == null) {
            preferredExec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        }
        this.preferredExec = preferredExec;
        //fileSystem.getCommandsPool().add(this);
    }

    /*
    //-------------------------------------------
    public void setErrorContainer(OutputContainer errorContainer) {
        this.errorContainer = errorContainer;
    }

    //-------------------------------------------
    public OutputContainer getErrorContainer() {
        return this.errorContainer;
    }
     */

    /*
    //-------------------------------------------
    public void setOutputListener(RegexListener listener){
        stdoutListener=listener;
    }


    //-------------------------------------------
    public void setErrorListener(RegexListener listener){
        stderrListener=listener;
    }

    //-------------------------------------------
    public void setOutputNoRegexListener(NoRegexListener listener){
        stdoutNoRegexListener=listener;
    }


    //-------------------------------------------
    public void setErrorNoRegexListener(NoRegexListener listener){
        stderrNoRegexListener=listener;
    }
     */
    
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
    
    /*
    public synchronized void addCommandListener(CommandListener listener) {
        commandListeners.add(listener);
    }
    
    public synchronized void removeCommandListener(CommandListener listener) {
        commandListeners.remove(listener);
    }
     */
    
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
        //D.deb("run("+cmd.getName()+")"); // NOI18N
        //String exec=cmd.getExec();
        //fileSystem.debug(cmd.getName()+": "+exec); // NOI18N

        exec = Variables.expand(vars,exec, true);

        //fileSystem.debugClear();
        //fileSystem.debug(cmd.getName()+": "+exec); // NOI18N
        //if (stdoutNoRegexListener != null) stdoutNoRegexListener.match(cmd.getName()+": "+exec); // NOI18N

        ExternalCommand ec = new ExternalCommand(exec);
        //ec.setTimeout(cmd.getTimeout());
        ec.setInput((String) cmd.getProperty(UserCommand.PROPERTY_INPUT));
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

        //TopManager.getDefault().setStatusText(g("MSG_Command_name_running", cmd.getName())); -- moved to CommandsPool
        E.deb("ec="+ec); // NOI18N
        exitStatus = ec.exec();
        E.deb("Command exited with exit status = "+exitStatus); // NOI18N
        //D.deb("errorContainer = "+errorContainer); // NOI18N
        switch (exitStatus) {
        case VcsCommandExecutor.SUCCEEDED:
            commandFinished(exec, true);
            
            // !! ALL  DEBUGGING REMOVED, IT SHOULD BE IMPLEMENTED IN COMMANDS POOL !!
            
            //fileSystem.debug(cmd.getName()+": "+g("MSG_Command_succeeded")+"\n"); // NOI18N
            //TopManager.getDefault().setStatusText(g("MSG_Command_name_succeeded", cmd.getName()));
            //if( fileSystem.isAdditionalCommand(cmd.getName())==false ){
            /*
            if(cmd.getDoRefresh() && fileSystem.getDoAutoRefresh((String)vars.get("DIR"))) { // NOI18N
                //D.deb("Now refresh folder after CheckIn,CheckOut,Lock,Unlock... commands for convenience"); // NOI18N
                fileSystem.setAskIfDownloadRecursively(false); // do not ask if using auto refresh
                fileSystem.getCache().refreshDir((String)vars.get("DIR")); // NOI18N
            }
            if (!cmd.getDoRefresh()) fileSystem.removeNumDoAutoRefresh((String)vars.get("DIR")); // NOI18N
             */
            //fileSystem.setLastCommandState(true);
            //if (errorContainer != null) errorDialog.removeCommandOut(); //cancelDialog(); -- not necessary
            break;
        case VcsCommandExecutor.INTERRUPTED:
            commandFinished(exec, false);
            //fileSystem.debug(cmd.getName()+": "+g("MSG_Timeout")+"\n"); // NOI18N
            //if (errorContainer != null) errorContainer.match(cmd.getName()+": "+g("MSG_Timeout")); // NOI18N
            break;
        case VcsCommandExecutor.FAILED:
            commandFinished(exec, false);
            //D.deb("exec failed "+ec.getExitStatus()); // NOI18N
            //fileSystem.debug(cmd.getName()+": "+g("MSG_Command_failed")+"\n"); // NOI18N
            //TopManager.getDefault().setStatusText(g("MSG_Command_name_failed", cmd.getName()));
            //if (errorContainer != null) errorContainer.match(cmd.getName()+": "+g("MSG_Command_failed")); // NOI18N
            /*
            ErrorCommandDialog errorDialog = fileSystem.getErrorDialog();
            if (errorDialog != null && errorContainer != null) {
                errorDialog.putCommandOut(errorContainer.getMessages());
                errorDialog.showDialog();
            }
             */
            fileSystem.removeNumDoAutoRefresh((String) vars.get("DIR")); // NOI18N
            //fileSystem.setLastCommandState(false);
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
        //D.deb("class finished with "+success+", errorContainer = "+errorContainer); // NOI18N
        if (success) {
            exitStatus = VcsCommandExecutor.SUCCEEDED;
            //fileSystem.debug(cmd.getName()+": "+g("MSG_Command_succeeded")+"\n"); // NOI18N
            //TopManager.getDefault().setStatusText(g("MSG_Command_name_succeeded", cmd.getName()));
            //if( fileSystem.isAdditionalCommand(cmd.getName())==false ){
            /*
            if(cmd.getDoRefresh() && fileSystem.getDoAutoRefresh((String)vars.get("DIR"))) { // NOI18N
                //D.deb("Now refresh folder after CheckIn,CheckOut,Lock,Unlock... commands for convenience"); // NOI18N
                fileSystem.setAskIfDownloadRecursively(false); // do not ask if using auto refresh
                String refreshPath = (String) vars.get("DIR");
                if (!cmd.getRefreshParent()) refreshPath += (String) vars.get("PS") + (String) vars.get("FILE");
                String pattern = cmd.getRefreshRecursivelyPattern();
                if (pattern != null && pattern.length() > 0 && exec.indexOf(pattern)) {
                    fileSystem.getCache().refreshDirRecursive(refreshPath);
                } else {
                    fileSystem.getCache().refreshDir(refreshPath); // NOI18N
                }
            }
            if (!cmd.getDoRefresh()) fileSystem.removeNumDoAutoRefresh((String)vars.get("DIR")); // NOI18N
             */
            commandFinished(exec, true);
            //fileSystem.setLastCommandState(true);
            //if (errorDialog != null) errorDialog.removeCommandOut();  //cancelDialog();  -- not necessary
        } else {
            if (Thread.currentThread().interrupted()) {
                exitStatus = VcsCommandExecutor.INTERRUPTED;
            } else {
                exitStatus = VcsCommandExecutor.FAILED;
            }
            commandFinished(exec, false);
            /*
            fileSystem.debug(cmd.getName()+": "+g("MSG_Command_failed")+"\n"); // NOI18N
            //TopManager.getDefault().setStatusText(g("MSG_Command_name_failed", cmd.getName()));
            if (errorContainer != null) errorContainer.match(cmd.getName()+": "+g("MSG_Command_failed")); // NOI18N
            ErrorCommandDialog errorDialog = fileSystem.getErrorDialog();
            if (errorDialog != null && errorContainer != null) {
                errorDialog.putCommandOut(errorContainer.getMessages());
                errorDialog.showDialog();
            }
             */
            fileSystem.removeNumDoAutoRefresh((String) vars.get("DIR")); // NOI18N
            //fileSystem.setLastCommandState(false);
        }
    }

    /**
     * Execute the command.
     */
    public void run() {
        /*
        for(Iterator it = commandListeners.iterator(); it.hasNext(); ) {
            ((CommandListener) it.next()).commandStarted(this);
        }
         */
        //isRunning = true;
        //hasStarted = true;
        String exec;
        if (preferredExec != null) exec = preferredExec;
        else exec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        if (exec != null) exec = exec.trim();
        //fileSystem.setLastCommandFinished(false);
        //fileSystem.debug(cmd.getName()+": "+exec); // NOI18N
        //if (stdoutNoRegexListener != null) stdoutNoRegexListener.match(cmd.getName()+": "+exec); // NOI18N

        StringTokenizer tokens = new StringTokenizer(exec);
        String first = tokens.nextToken();
        E.deb("first = "+first); // NOI18N
        boolean disableRefresh = VcsCommandIO.getBooleanProperty(cmd, UserCommand.PROPERTY_CHECK_FOR_MODIFICATIONS);
        if (disableRefresh) fileSystem.disableRefresh();
        if (first != null && (first.toLowerCase().endsWith(".class"))) // NOI18N
            runClass(exec, first.substring(0, first.length() - ".class".length()), tokens); // NOI18N
        else
            runCommand(exec);
        //fileSystem.setLastCommandFinished(true);
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
        String path = (String) vars.get("DIR");
        String file = (String) vars.get("FILE");
        String fullPath = ((path.length() > 0) ? path.replace(File.separatorChar, '/') + "/" : "") + ((file == null) ? "" : file);
        return Collections.singleton(fullPath);
        //HashSet set = new HashSet(1);
        //set.add(file);
        //return set;
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

/*
 * Log
 *  18   Jaga      1.16.1.0    3/8/00   Martin Entlicher Do not debug output when
 *       written to the output window.
 *  17   Gandalf   1.16        2/10/00  Martin Entlicher 
 *  16   Gandalf   1.15        1/18/00  Martin Entlicher 
 *  15   Gandalf   1.14        1/15/00  Ian Formanek    NOI18N
 *  14   Gandalf   1.13        1/7/00   Martin Entlicher 
 *  13   Gandalf   1.12        1/6/00   Martin Entlicher 
 *  12   Gandalf   1.11        1/5/00   Martin Entlicher 
 *  11   Gandalf   1.10        12/29/99 Martin Entlicher 
 *  10   Gandalf   1.9         12/28/99 Martin Entlicher 
 *  9    Gandalf   1.8         12/16/99 Martin Entlicher 
 *  8    Gandalf   1.7         12/14/99 Martin Entlicher Output Listener added
 *  7    Gandalf   1.6         11/30/99 Martin Entlicher 
 *  6    Gandalf   1.5         11/23/99 Martin Entlicher Changed for 
 *       VcsFilesystem instead of CvsFileSystem
 *  5    Gandalf   1.4         10/25/99 Pavel Buzek     
 *  4    Gandalf   1.3         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  3    Gandalf   1.2         10/7/99  Martin Entlicher Fixed runClass
 *  2    Gandalf   1.1         10/5/99  Pavel Buzek     VCS at least can be 
 *       mounted
 *  1    Gandalf   1.0         9/30/99  Pavel Buzek     
 * $
 */
