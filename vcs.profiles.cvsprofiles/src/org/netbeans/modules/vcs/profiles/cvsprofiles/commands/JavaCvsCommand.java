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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.openide.util.RequestProcessor;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.exec.ExternalCommand;
import org.netbeans.modules.vcscore.commands.*;

import org.netbeans.lib.cvsclient.commandLine.CVSCommand;

/**
 * A command, that executes JavaCVS library commands.
 *
 * @author  Martin Entlicher
 */
public class JavaCvsCommand implements VcsAdditionalCommand, Runnable {
    
    private static final String WORK_DIR_OPTION = "dir="; // NOI18N
    
    private VcsFileSystem fileSystem;
    
    private CommandOutputListener stdoutListener;
    private CommandOutputListener stderrListener;
    private CommandDataOutputListener stdoutDataListener;
    private Pattern dataRegex;
    private CommandDataOutputListener stderrDataListener;
    private Pattern errorRegex;
    private PipedInputStream stdin;
    private PipedInputStream errin;
    private volatile boolean outputTasksInterrupted = false;
    
    /** Creates a new instance of JavaCvsCommandExecutor */
    public JavaCvsCommand() {
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    /** This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutListener listener of the standard output of the command
     * @param stderrListener listener of the error output of the command
     * @param stdoutDataListener listener of the standard output of the command which
     *                           satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrDataListener listener of the error output of the command which
     *                           satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *         false if some error occured.
     *
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {
        this.stdoutListener = stdoutListener;
        this.stderrListener = stderrListener;
        this.stdoutDataListener = stdoutDataListener;
        this.stderrDataListener = stderrDataListener;
        if (dataRegex != null) {
            try {
                this.dataRegex = Pattern.compile(dataRegex);
            } catch (PatternSyntaxException resex) {}
        }
        if (errorRegex != null) {
            try {
                this.errorRegex = Pattern.compile(errorRegex);
            } catch (PatternSyntaxException resex) {}
        }
        /*Collection filesCollection = ExecuteCommand.createProcessingFiles(fileSystem, vars);
        File[] files = new File[filesCollection.size()];
        Iterator it = filesCollection.iterator();
        for (int i = 0; i < files.length; i++) {
            files[i] = fileSystem.getFile((String) it.next());
            //System.out.println("File["+i+"] = "+files[i]);
        }
        String localDir = findBiggestParentFor(files);
        //System.out.println("Biggest Parent = '"+localDir+"'");
         */
        String localDir;
        if (args.length > 0 && args[0].startsWith(WORK_DIR_OPTION)) {
            localDir = args[0].substring(WORK_DIR_OPTION.length());
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            args = newArgs;
        } else {
            localDir = (String) vars.get("ROOTDIR");
        }
        /*
        stdoutDataListener.outputData(new String[] { "Working dir:", localDir });
        for (int i = 0; i < args.length; i++) {
            stdoutDataListener.outputData(new String[] { "Arg["+i+"] = ", args[i] });
        }
         */
        File[] outputFiles = new File[2];
        boolean[] errorIntoOutput = new boolean[1];
        args = findOutputRedirection(args, outputFiles, errorIntoOutput);
        OutputStream stdout = null;
        OutputStream stderr = null;
        if (outputFiles[0] != null) {
            try {
                outputFiles[0].createNewFile();
                stdout = new FileOutputStream(outputFiles[0]);
                stdin = null;
            } catch (IOException ioex) {
                stderrListener.outputLine(ioex.getLocalizedMessage());
            }
        }
        if (stdout == null) {
            PipedOutputStream pstdout = new PipedOutputStream();
            stdout = pstdout;
            try {
                stdin = new PipedInputStream(pstdout);
            } catch (IOException ioex) {}
        }
        if (errorIntoOutput[0]) {
            stderr = stdout;
            errin = null;
        } else {
            if (outputFiles[1] != null) {
                try {
                    outputFiles[1].createNewFile();
                    stderr = new FileOutputStream(outputFiles[1]);
                    errin = null;
                } catch (IOException ioex) {
                    stderrListener.outputLine(ioex.getLocalizedMessage());
                }
            }
            if (stderr == null) {
                PipedOutputStream pstderr = new PipedOutputStream();
                stderr = pstderr;
                try {
                    errin = new PipedInputStream(pstderr);
                } catch (IOException ioex) {}
            }
        }
        RequestProcessor.Task task1 = null;
        RequestProcessor.Task task2 = null;
        if (stdin != null || errin != null) {
            task1 = RequestProcessor.getDefault().post(this);
            task2 = RequestProcessor.getDefault().post(this);
        }
        boolean success;
        boolean interruptOutputTasks = true;
        boolean isInterrupted = false;
        try {
            transferEnvironment();
            PrintStream pout = new PrintStream(stdout);
            PrintStream perr = (stderr == stdout) ? pout : new PrintStream(stderr);
            String portStr = (String) vars.get("ENVIRONMENT_VAR_CVS_CLIENT_PORT");
            int port = 0;
            if (portStr != null) {
                try {
                    port = Integer.parseInt(portStr);
                } catch (NumberFormatException nfe) {}
            }
            success = CVSCommand.processCommand(args, null, localDir, port, pout, perr);
            interruptOutputTasks = false;
        } finally {
            // Remember whether the current thread is interrupted
            isInterrupted = Thread.interrupted();
            try {
                stdout.close();
                stderr.close();
            } catch (IOException ioex) {}
            if (interruptOutputTasks) {
                outputTasksInterrupted = true;
            }
            if (task1 != null && task2 != null) {
                task1.waitFinished();
                task2.waitFinished();
            }
        }
        // If the thread was interrupted, interrupt it again. The interrupt status might be cleared by wait.
        if (isInterrupted) Thread.currentThread().interrupt();
        return success;
    }
    
    /**
     * We have environment variables with Env- prefix in NetBeans.
     * JavaCVS library uses cvs.* system properties instead.
     * Therefore we need to transfer env-cvs* into cvs.*
     * cvs.passfile is defined in a special way from home property.
     */
    private static void transferEnvironment() {
        // CVS_PASSFILE
        String cvsPassfile = System.getProperty("env-cvs_passfile");
        if (cvsPassfile == null) {
            String home = null;
            if (org.openide.util.Utilities.isWindows()) {
                String drive = System.getProperty("Env-HOMEDRIVE");
                String path = System.getProperty("Env-HOMEPATH");
                if (drive != null && path != null) {
                    home = drive + path;
                }
            }
            if (home == null) {
                home = System.getProperty("Env-HOME");
            }
            if (home != null) {
                cvsPassfile = home + File.separator + ".cvspass";
            }
        }
        if (cvsPassfile != null) {
            System.setProperty("cvs.passfile", cvsPassfile);
        }
        // CVSROOT
        String cvsRoot = System.getProperty("env-cvsroot");
        if (cvsRoot != null) {
            System.setProperty("cvs.root", cvsRoot);
        }
        // CVSEDITOR, EDITOR, VISUAL
        String cvsEditor = System.getProperty("env-cvseditor");
        if (cvsEditor == null) {
            cvsEditor = System.getProperty("env-editor");
        }
        if (cvsEditor == null) {
            cvsEditor = System.getProperty("env-visual");
        }
        if (cvsEditor != null) {
            System.setProperty("cvs.editor", cvsEditor);
        }
    }
    
    /**
     * Detect the redirection of output to a file.
     * UNIX-like redirection is detected: '>', '2>' and '2>&1'.<p>
     * Possible variants are:<br>
     * args... > file-name&nbsp;&nbsp;-&nbsp;&nbsp;Will redirect standard output into file &lt;file-name&gt;<br>
     * args... 2> file-name&nbsp;&nbsp;-&nbsp;&nbsp;Will redirect error output into file &lt;file-name&gt;<br>
     * args... > file-name1 2> file-name2&nbsp;&nbsp;-&nbsp;&nbsp;Will redirect standard output into file &lt;file-name1&gt; and error output into file &lt;file-name2&gt;<br>
     * args... > file-name 2>&1&nbsp;&nbsp;-&nbsp;&nbsp;Will redirect both standard and error output into file &lt;file-name&gt;<br>
     * args... 2>&1&nbsp;&nbsp;-&nbsp;&nbsp;Will just redirect error output into standard output
     *
     * @param args The list of arguments
     * @param outputFiles The array of length 2 that will return references to
     *        output files, if any.
     * @param errorIntoStandard Will return true, when standard error stream
     *        should be redirected into standard output stream.
     * @return The new list of arguments whithout the redirection.
     */
    private static String[] findOutputRedirection(String[] args, File[] outputFiles, boolean [] errorIntoOutput) {
        errorIntoOutput[0] = false;
        for (int i = args.length - 1; i >= 0; i--) {
            if ("2>&1".equals(args[i])) {
                errorIntoOutput[0] = true;
                args = removeItem(args, i);
            } else if (">".equals(args[i])) {
                if (i < (args.length - 1)) {
                    String fileName = args[i+1];
                    args = removeItem(args, i+1);
                    args = removeItem(args, i);
                    outputFiles[0] = new File(fileName);
                }
            } else if ("2>".equals(args[i])) {
                if (i < (args.length - 1)) {
                    String fileName = args[i+1];
                    args = removeItem(args, i+1);
                    args = removeItem(args, i);
                    outputFiles[1] = new File(fileName);
                }
            }
        }
        return args;
    }
    
    private static String[] removeItem(String[] args, int index) {
        String[] newArgs = new String[args.length - 1];
        int j = 0;
        for (int i = 0; i < args.length; i++) {
            if (i != index) {
                newArgs[j++] = args[i];
            }
        }
        return newArgs;
    }
    
    /*
    private static final String findBiggestParentFor(File[] files) {
        String parent = null;
        for (int i = 0; i < files.length; i++) {
            String p;
            if (files[i].isFile()) {
                p = files[i].getParent();
            } else {
                p = files[i].getAbsolutePath();
            }
            if (parent == null) parent = p;
            else {
                if (p != null) {
                    StringBuffer newParent = new StringBuffer();
                    for (int pos = 0; pos < parent.length() && pos < p.length(); pos++) {
                        if (parent.charAt(pos) == p.charAt(pos)) newParent.append(p.charAt(pos));
                        else break;
                    }
                    while (newParent.charAt(newParent.length() - 1) == File.separatorChar) {
                        newParent.deleteCharAt(newParent.length() - 1);
                    }
                    parent = newParent.toString();
                }
            }
        }
        return parent;
    }
     */
    
    private boolean readingStdOut = false;
    
    /**
     * Run the output retrieval thread.
     */
    public void run() {
        boolean readStdOut;
        synchronized (this) {
            if (readingStdOut) readStdOut = false;
            else {
                readingStdOut = true;
                readStdOut = true;
            }
        }
        if (readStdOut) readStdOut();
        else readErrOut();
    }
    
    private void readStdOut() {
        if (stdin == null) return ;
        BufferedReader bin = new BufferedReader(new InputStreamReader(stdin));
        String stdLine;
        do {
            try {
                stdLine = bin.readLine();
            } catch (IOException ioex) {
                stdLine = null;
            }
            if (stdLine != null) {
                stdoutListener.outputLine(stdLine);
                if (dataRegex != null) {
                    String[] sa = ExternalCommand.matchToStringArray(dataRegex, stdLine);
                    if (sa != null && sa.length > 0) {
                        stdoutDataListener.outputData(sa);
                    }
                } else {
                    stdoutDataListener.outputData(new String[] { stdLine });
                }
            }
        } while((stdLine != null) && !outputTasksInterrupted);
    }
    
    private void readErrOut() {
        if (errin == null) return ;
        BufferedReader ber = new BufferedReader(new InputStreamReader(errin));
        String errLine;
        do {
            try {
                errLine = ber.readLine();
            } catch (IOException ioex) {
                errLine = null;
            }
            if (errLine != null) {
                stderrListener.outputLine(errLine);
                if (errorRegex != null) {
                    String[] sa = ExternalCommand.matchToStringArray(errorRegex, errLine);
                    if (sa != null && sa.length > 0) {
                        stderrDataListener.outputData(sa);
                    }
                } else {
                    stderrDataListener.outputData(new String[] { errLine });
                }
            }
        } while((errLine != null) && !outputTasksInterrupted);
    }
    
}
