/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.signatures;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * Ant task to write out all signatures for some JARs.
 * @author Jesse Glick
 */
public class SignatureTask extends Task {
    
    private Collection<FileSet> filesets = new LinkedList<FileSet>();
    private Pattern skipRegexp = Pattern.compile("^com\\.sun\\.");
    private File out;
    
    public SignatureTask() {}
    
    /**
     * Add a set of JAR files to be processed.
     */
    public void addFileSet(FileSet fs) {
        filesets.add(fs);
    }
    
    /**
     * Set the output file to generate signatures into.
     * Must be named <code><i>something</i>.java</code>.
     */
    public void setOut(File f) {
        out = f;
    }
    
    /**
     * Configure a regular expression of class names to skip for processing.
     * By default, it is: <pre>^com\.sun\.</pre>
     * This avoids warnings of the form
     * <pre>warning: com.sun.x.y.z is Sun proprietary API and may be removed in a future release</pre>
     * which may occur if you accidentally include some such classes among your JAR(s).
     */
    public void setSkipRegexp(String s) {
        skipRegexp = Pattern.compile(s);
    }
    
    @Override
    public void execute() throws BuildException {
        if (out == null) {
            throw new BuildException("Must specify out='...'", getLocation());
        }
        Matcher m = Pattern.compile("(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)\\.java").matcher(out.getName());
        if (!m.matches()) {
            throw new BuildException("Illegal Java source file name " + out.getName(), getLocation());
        }
        String sigclazz = m.group(1);
        if (filesets.isEmpty()) {
            throw new BuildException("Must specify <fileset>s", getLocation());
        }
        final List<File> cp = new ArrayList<File>();
        for (FileSet fs : filesets) {
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            File basedir = ds.getBasedir();
            for (String file : ds.getIncludedFiles()) {
                cp.add(new File(basedir, file));
            }
        }
        try {
            final Collection<String> classes = ClassScanner.findTopLevelClasses(true, cp);
            OutputStream os = new FileOutputStream(out);
            try {
                final PrintWriter w = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
                w.println("@SuppressWarnings(\"deprecation\")");
                w.println("class " + sigclazz + " {");
                new Loader(cp) {
                    protected void run() {
                        SignatureWriter sigs = new SignatureWriter(w, "", elements(), types());
                        int cnt = 0;
                        for (String clazz : classes) {
                            if (skipRegexp != null && skipRegexp.matcher(clazz).find()) {
                                continue;
                            }
                            try {
                                sigs.process(clazz);
                            } catch (RuntimeException e) {
                                log("Failed to create signature for " + clazz + " in " + cp + " due to " + e, Project.MSG_VERBOSE);
                            }
                            if (cnt++ % 1000 == 0) {
                                log("Working on " + clazz, Project.MSG_INFO);
                            }
                        }
                    }
                };
                w.println("}");
                w.flush();
            } finally {
                os.close();
            }
        } catch (IOException x) {
            throw new BuildException(x, getLocation());
        }
        StringBuilder cps = new StringBuilder();
        for (File p : cp) {
            if (cps.length() > 0) {
                cps.append(File.pathSeparatorChar);
            }
            cps.append(p);
        }
        log("Running a test compile of " + out, Project.MSG_INFO);
        JavaCompiler.CompilationTask task = ToolProvider.getSystemJavaCompiler().getTask(
                null,
                Loader.nullOutputFileManager(),
                null,
                Arrays.asList("-source", "1.6", "-encoding", "UTF-8", "-classpath", cps.toString(), "-Xlint:unchecked"),
                null,
                ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null).getJavaFileObjects(out));
        boolean ok = task.call();
        if (!ok) {
            throw new BuildException("Compilation of generated signature class failed", getLocation());
        }
        log("Test compile succeeded.", Project.MSG_INFO);
    }
    
}
