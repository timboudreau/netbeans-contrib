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

package org.netbeans.signatures;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
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
 * Ant task to write out all public Java signatures for some JARs.
 * The output will be a Java source file which should be compilable
 * when those JARs are in the classpath.
 * It will attempt to exercise as much of the public API detected in those
 * JARs as possible.
 * Running the class (or classes) will not be possible; compilation is the test.
 * Order should be stable, so you may meaningfully use a diff tool to check for
 * syntactic API changes. Added lines are compatible; removed lines may be incompatible.
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
        final String sigclazz = m.group(1);
        if (filesets.isEmpty()) {
            throw new BuildException("Must specify <fileset>s", getLocation());
        }
        final Set<File> cp = new TreeSet<File>();
        for (FileSet fs : filesets) {
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            File basedir = ds.getBasedir();
            for (String file : ds.getIncludedFiles()) {
                cp.add(new File(basedir, file));
            }
        }
        try {
            final Collection<String> classes = ClassScanner.findTopLevelClasses(cp, true, true);
            OutputStream os = new FileOutputStream(out);
            try {
                final PrintWriter w = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
                w.printf("@SuppressWarnings(\"deprecation\")\nclass %s000 {\n\n", sigclazz);
                new Loader(cp) {
                    protected void run() {
                        SignatureWriter sigs = new SignatureWriter(w, elements(), types());
                        int cnt = 0;
                        int blocksize = 1000;
                        for (String clazz : classes) {
                            if (skipRegexp != null && skipRegexp.matcher(clazz).find()) {
                                continue;
                            }
                            try {
                                sigs.process(clazz);
                            } catch (RuntimeException e) {
                                log("Failed to create signature for " + clazz + " in " + cp + " due to " + e, Project.MSG_VERBOSE);
                            }
                            if (++cnt % blocksize == 0) {
                                log("Working on " + clazz, Project.MSG_INFO);
                                w.printf("}\n\n@SuppressWarnings(\"deprecation\")\nclass %s%03d {\n\n", sigclazz, cnt / blocksize);
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
                // XXX should redirect errors to Project.log, and should perhaps fail on any messages
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
