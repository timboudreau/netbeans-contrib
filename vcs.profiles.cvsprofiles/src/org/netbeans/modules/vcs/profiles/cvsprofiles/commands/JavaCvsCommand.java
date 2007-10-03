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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.io.File;
import java.io.FileOutputStream;
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
import org.netbeans.modules.vcscore.util.VariableValueAdjustment;

/**
 * A command, that executes JavaCVS library commands.
 *
 * @author  Martin Entlicher
 */
public class JavaCvsCommand implements VcsAdditionalCommand {
    
    private static final String WORK_DIR_OPTION = "dir="; // NOI18N
    
    private CommandExecutionContext executionContext;
    
    private CommandOutputListener stdoutListener;
    private CommandOutputListener stderrListener;
    private CommandDataOutputListener stdoutDataListener;
    private Pattern dataRegex;
    private CommandDataOutputListener stderrDataListener;
    private Pattern errorRegex;
    
    /** Creates a new instance of JavaCvsCommandExecutor */
    public JavaCvsCommand() {
    }
    
    public void setExecutionContext(CommandExecutionContext executionContext) {
        this.executionContext = executionContext;
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
        PrintStream stdout = null;
        PrintStream stderr = null;
        if (outputFiles[0] != null) {
            try {
                outputFiles[0].createNewFile();
                stdout = new PrintStream(new FileOutputStream(outputFiles[0]));
            } catch (IOException ioex) {
                stderrListener.outputLine(ioex.getLocalizedMessage());
            }
        }
        if (stdout == null) {
            stdout = new OutputPrintStream(stdoutListener, this.dataRegex, stdoutDataListener);
        }
        if (errorIntoOutput[0]) {
            stderr = stdout;
        } else {
            if (outputFiles[1] != null) {
                try {
                    outputFiles[1].createNewFile();
                    stderr = new PrintStream(new FileOutputStream(outputFiles[1]));
                } catch (IOException ioex) {
                    stderrListener.outputLine(ioex.getLocalizedMessage());
                }
            }
            if (stderr == null) {
                stderr = new OutputPrintStream(stderrListener, this.errorRegex, stderrDataListener);
            }
        }
        VariableValueAdjustment vva = executionContext.getVarValueAdjustment();
        for (int i = 0; i < args.length; i++) {
            args[i] = vva.revertAdjustedVarValue(args[i]);
        }
        boolean success;
        boolean isInterrupted = false;
        try {
            transferEnvironment();
            String portStr = (String) vars.get("ENVIRONMENT_VAR_CVS_CLIENT_PORT");
            int port = 0;
            if (portStr != null) {
                try {
                    port = Integer.parseInt(portStr);
                } catch (NumberFormatException nfe) {}
            }
            success = CVSCommand.processCommand(args, null, localDir, port, stdout, stderr);
        } finally {
            // Remember whether the current thread is interrupted
            isInterrupted = Thread.interrupted();
            stdout.close();
            stderr.close();
        }
        // If the thread was interrupted, interrupt it again. The interrupt status might be cleared by wait.
        if (isInterrupted) Thread.currentThread().interrupt();
        return success;
    }
    
    /**
     * Converts system env vars to properties used by JavaCVS.
     * JavaCVS library uses cvs.* system properties instead of
     * environment variables.
     * cvs.passfile is defined in a special way from home property.
     */
    private static void transferEnvironment() {
        // CVS_PASSFILE
        String cvsPassfile = System.getenv("cvs_passfile");
        if (cvsPassfile == null) {
            String home = System.getenv("HOME");
            if (home == null && org.openide.util.Utilities.isWindows()) {
                String drive = System.getenv("HOMEDRIVE");
                String path = System.getenv("HOMEPATH");
                if (drive != null && path != null) {
                    home = drive + path;
                }
            }
            if (home != null) {
                cvsPassfile = home + File.separator + ".cvspass";
            }
        }
        if (cvsPassfile != null) {
            System.setProperty("cvs.passfile", cvsPassfile);
        }
        // CVSROOT
        String cvsRoot = System.getenv("cvsroot");
        if (cvsRoot != null) {
            System.setProperty("cvs.root", cvsRoot);
        }
        // CVSEDITOR, EDITOR, VISUAL
        String cvsEditor = System.getenv("cvseditor");
        if (cvsEditor == null) {
            cvsEditor = System.getenv("editor");
        }
        if (cvsEditor == null) {
            cvsEditor = System.getenv("visual");
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
    
    /**
     * The print stream that gets output from the library and redistribute
     * it to the listeners.
     */
    private static class OutputPrintStream extends PrintStream {
        
        private CommandOutputListener lineOut;
        private Pattern regex;
        private CommandDataOutputListener dataOut;
        private StringBuffer buf;
        
        public OutputPrintStream(CommandOutputListener lineOut, Pattern regex,
                                 CommandDataOutputListener dataOut) {
            super(new PipedOutputStream()); // A dummy output stream
            this.lineOut = lineOut;
            this.regex = regex;
            this.dataOut = dataOut;
            buf = new StringBuffer();
        }
        
        public void println(String s) {
            lineOut.outputLine(s);
            if (regex != null) {
                String[] sa = ExternalCommand.matchToStringArray(regex, s);
                if (sa != null && sa.length > 0) {
                    dataOut.outputData(sa);
                }
            } else {
                dataOut.outputData(new String[] { s });
            }
        }
        
        public void write(byte[] buf, int off, int len) {
            this.buf.append(new String(buf, off, len));
            processBuffer();
        }
        
        private void processBuffer() {
            char[] chars = buf.toString().toCharArray();
            int i1 = 0;
            for (int i2 = findNL(chars, i1); i2 >= 0; i2 = findNL(chars, i1)) {
                String line = new String(chars, i1, i2 - i1);
                println(line);
                i2 = skipNL(chars, i2);
                i1 = i2;
            }
            if (i1 > chars.length) i1 = chars.length;
            buf.delete(0, i1);
        }
        
        private static int findNL(char[] chars, int offset) {
            for (int i = offset; i < chars.length; i++) {
                if (chars[i] == '\n' || chars[i] == '\r') {
                    return i;
                }
            }
            return -1; // Newline not found
        }
        
        private static int skipNL(char[] chars, int offset) {
            if (chars[offset] == '\r' && chars.length > (offset + 1) && chars[offset + 1] == '\n') {
                return offset + 2;
            } else {
                return offset + 1;
            }
        }
        
        public void close() {
            super.close();
            if (buf.length() > 0 && buf.charAt(buf.length() - 1) != '\n' && buf.charAt(buf.length() - 1) != '\r') {
                buf.append('\n');
            }
            processBuffer();
        }
        
        // ### Other methods are unsupported ###
        
        public void print(boolean b) {
            throw new UnsupportedOperationException();
        }
        
        public void print(char c) {
            this.buf.append(c);
            processBuffer();
        }
        
        public void print(char[] s) {
            throw new UnsupportedOperationException();
        }
        
        public void print(double d) {
            throw new UnsupportedOperationException();
        }
        
        public void print(float f) {
            throw new UnsupportedOperationException();
        }
        
        public void print(int i) {
            throw new UnsupportedOperationException();
        }
        
        public void print(long l) {
            throw new UnsupportedOperationException();
        }
        
        public void print(Object obj) {
            print(obj.toString());
        }
        
        public void print(String s) {
            this.buf.append(s);
            processBuffer();
        }
        
        public void println() {
            this.buf.append('\n');
            processBuffer();
        }
        
        public void println(boolean b) {
            throw new UnsupportedOperationException();
        }
        
        public void println(char c) {
            throw new UnsupportedOperationException();
        }
        
        public void println(char[] s) {
            throw new UnsupportedOperationException();
        }
        
        public void println(double d) {
            throw new UnsupportedOperationException();
        }
        
        public void println(float f) {
            throw new UnsupportedOperationException();
        }
        
        public void println(int i) {
            throw new UnsupportedOperationException();
        }
        
        public void println(long l) {
            throw new UnsupportedOperationException();
        }
        
        public void println(Object obj) {
            println(obj.toString());
        }
        
        public void write(int b) {
            throw new UnsupportedOperationException();
        }
        
    }
    
}
