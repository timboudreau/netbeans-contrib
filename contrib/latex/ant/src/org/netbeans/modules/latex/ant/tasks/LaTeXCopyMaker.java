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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ant.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.Project;

/** This class may not use System.err! (it is redirected by the ant!)
 *
 * @author Jan Lahoda
 */
/*package private*/ class LaTeXCopyMaker extends Thread {
    
    final PrintWriter os;
    final BufferedReader is;
    /** while set to false at streams that writes to the OutputWindow it must be
     * true for a stream that reads from the window.
     */
    final boolean autoflush;
    private boolean done = false;
    private Stack currentFile;
    
    private Project project;
    private File baseDir;
    
    LaTeXCopyMaker(Project project, File baseDir, InputStream is, OutputStream os) {
        this.project = project;
        this.baseDir = baseDir;
        this.os = new PrintWriter(new OutputStreamWriter(os));
        this.is = new BufferedReader(new InputStreamReader(is));
        autoflush = false;
        currentFile = new Stack();
    }
    
    private class ErrorPattern {
        public Pattern pattern;
        public int     skip;
        public ErrorPattern(Pattern pattern, int skip) {
            this.pattern = pattern;
            this.skip    = skip;
        }
        
        public boolean process(String line) throws IOException {
            Matcher m = pattern.matcher(line);

            if (m.find() && !currentFile.isEmpty()) {
                project.log("Pattern: \"" + pattern.pattern() + "\" found on line \"" + line + "\"", Project.MSG_DEBUG);
                
                String lineNum = m.group(1);
                try {
                    int lineNumber = Integer.parseInt(lineNum);
                    
                    os.println(new File(baseDir, (String) currentFile.peek()).getAbsolutePath() + ":" + lineNumber + ":" + line);
                    os.println(line);
                    if (autoflush) os.flush();
                    
                    for (int cntr = 0; cntr < skip; cntr++) {
                        os.println(is.readLine());
                        
                        if (autoflush) os.flush();
                    }
                    
                    return true;
                } catch (NumberFormatException e) {
                    project.log("NumberFormatException: " + e.getMessage(), Project.MSG_VERBOSE);
                }
            }
            
            return false;
        }
    }
    
    private static Pattern pageDef = Pattern.compile("^\\[[0-9]+\\]", Pattern.MULTILINE);
    
    private class OverfullErrorPattern extends ErrorPattern {
        public OverfullErrorPattern(Pattern pattern) {
            super(pattern, (-1));
        }
        
        public boolean process(String line) throws IOException {
            Matcher m = pattern.matcher(line);
            
            if (m.find() && !currentFile.isEmpty()) {
                String startLineNum = m.group(5);
                String endLineNum = m.group(6);
                try {
                    int startLineNumber = Integer.parseInt(startLineNum);
                    int endLineNumber   = Integer.parseInt(endLineNum);
                    
                    os.println(new File(baseDir, (String) currentFile.peek()).getAbsolutePath() + ":" + startLineNumber + ":0:" + endLineNumber  + ":0:" + line);
                    os.println(line);
                    if (autoflush) os.flush();
                    
                    while (!"".equals(line = is.readLine()) && line != null && !pageDef.matcher(line).find()) { //??sufficient
                        os.println(line);
                        
                        if (autoflush) os.flush();
                    }
                    
                    os.println(line);
                    
                    if (autoflush) os.flush();
                    
                    return true;
                } catch (NumberFormatException e) {
                    //ignored...
                }
            }
            
            return false;
        }
    }
    
    private final ErrorPattern[] patterns = new ErrorPattern[] {
        new ErrorPattern(Pattern.compile("LaTeX Warning: [^\n]* on input line ([0123456789]*)"), 0),
        new ErrorPattern(Pattern.compile("l\\.([0123456789]*)"), 1),
        new OverfullErrorPattern(Pattern.compile("((Over)|(Under))full \\\\hbox (.*) in paragraph at lines ([0123456789]*)--([0123456789]*)")),
    };
    
    /* Makes copy. */
    public void run() {
        try {
            String line;
            
            MAIN_LOOP: while ((line = is.readLine()) != null) {
                for (int pNumber = 0; pNumber < patterns.length; pNumber++) {
                    if (patterns[pNumber].process(line)) {
                        continue MAIN_LOOP;
                    }
                }
                
                for (int cntr = 0; cntr < line.length(); cntr++) {
                    switch (line.charAt(cntr)) {
                        case '(':
                            String file = readFileName(line, cntr + 1);
                            
                            project.log("adding file: " + file, Project.MSG_DEBUG);
                            
                            currentFile.push(file);
                            break;
                        case ')':
                            if (!currentFile.isEmpty())
                                project.log("removing file: " + currentFile.pop().toString(), Project.MSG_DEBUG);
                            
                            break;
                    }
                }
                
                os.println(line);
                
                if (autoflush) os.flush();
            }
        } catch (IOException ex) {
        } finally {
            os.flush();
        }
    }
    
//    public void interrupt() {
//        super.interrupt();
//        done = true;
//    }
    
    private static String readFileName(String line, int cntr) {
        StringBuffer sb = new StringBuffer();
        String fileSpecChars = "/\\._-";
        
        while (   (cntr < line.length())
        && (Character.isLetterOrDigit(line.charAt(cntr)) || fileSpecChars.indexOf(line.charAt(cntr)) != (-1))){
            sb.append(line.charAt(cntr));
            cntr++;
        }
        
        return sb.toString();
    }
    
} // end of CopyMaker
