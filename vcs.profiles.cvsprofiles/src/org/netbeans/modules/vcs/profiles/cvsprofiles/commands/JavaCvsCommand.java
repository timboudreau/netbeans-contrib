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
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.IOException;
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
    //private boolean[] stopOutputReading;
    
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
        PipedOutputStream stdout = new PipedOutputStream();
        PipedOutputStream stderr = new PipedOutputStream();
        //stopOutputReading = new boolean[] { false };
        try {
            stdin = new PipedInputStream(stdout);
            errin = new PipedInputStream(stderr);
        } catch (IOException ioex) {}
        RequestProcessor.Task task1 = RequestProcessor.getDefault().post(this);
        RequestProcessor.Task task2 = RequestProcessor.getDefault().post(this);
        boolean success = CVSCommand.processCommand(args, null, localDir, new PrintStream(stdout), new PrintStream(stderr));
        try {
            stdout.close();
            stderr.close();
        } catch (IOException ioex) {}
        //stopOutputReading[0] = true;
        task1.waitFinished();
        task2.waitFinished();
        return success;
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
        } while(/*!stopOutputReading[0] && */(stdLine != null));
    }
    
    private void readErrOut() {
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
        } while(/*!stopOutputReading[0] && */(errLine != null));
    }
    
}
