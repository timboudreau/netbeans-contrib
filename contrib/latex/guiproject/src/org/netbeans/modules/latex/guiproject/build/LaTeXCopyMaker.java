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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.guiproject.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.Document;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.windows.OutputWriter;

/** This class may not use System.err! (it is redirected by the ant!)
 *
 * @author Jan Lahoda
 */
/*package private*/ class LaTeXCopyMaker extends Thread {
    
    final OutputWriter out;
    final BufferedReader is;
    /** while set to false at streams that writes to the OutputWindow it must be
     * true for a stream that reads from the window.
     */
    final boolean autoflush;
    private boolean done = false;
    private Stack<String> currentFile;
    private int lineNumber;
    private Map<Document, List<ErrorDescription>> errors;
    
    private File baseDir;
    
    LaTeXCopyMaker(File baseDir, InputStream is, OutputWriter out) {
        this.baseDir = baseDir;
        this.out = out;
        this.is = new BufferedReader(new InputStreamReader(/*new TypingInputStream*/(is)));
        autoflush = true;
        currentFile = new Stack<String>();
        lineNumber = 0;
        errors = new HashMap<Document, List<ErrorDescription>>();
    }

    private FileObject getCurrentFile() {
        String fileName = currentFile.peek();
        File file = FileUtil.normalizeFile(new File(baseDir, fileName));

        return FileUtil.toFileObject(file);
    }

    public Map<Document, List<ErrorDescription>> getErrors() {
        return errors;
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
//                project.log("Pattern: \"" + pattern.pattern() + "\" found on line \"" + line + "\"", Project.MSG_DEBUG);
                
                String lineNum = m.group(1);
                try {
                    int lineNumber = Integer.parseInt(lineNum);
                    FileObject file = getCurrentFile();

//                    os.println(new File(baseDir, (String) currentFile.peek()).getAbsolutePath() + ":" + lineNumber + ":" + line);
                    if (file != null) {
                        out.println(line, new OutputListenerImpl(file, lineNumber, line), true);
                    } else {
                        out.println(line);
                    }
                    if (autoflush) out.flush();
                    
                    for (int cntr = 0; cntr < skip; cntr++) {
                        out.println(is.readLine());
                        lineNumber++;
                        
                        if (autoflush) out.flush();
                    }
                    
                    return true;
                } catch (NumberFormatException e) {
//                    project.log("NumberFormatException: " + e.getMessage(), Project.MSG_VERBOSE);
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
                    FileObject file = getCurrentFile();
                    
//                    os.println(new File(baseDir, (String) currentFile.peek()).getAbsolutePath() + ":" + startLineNumber + ":0:" + endLineNumber  + ":0:" + line);
                    if (file != null) {
                        DataObject d = DataObject.find(file);
                        EditorCookie ec = (EditorCookie) d.getCookie(EditorCookie.class);
                        Document doc = ec.openDocument();
                        ErrorDescription err = ErrorDescriptionFactory.createErrorDescription(Severity.VERIFIER, line, doc, startLineNumber);
                        List<ErrorDescription> errorDescriptions = errors.get(doc);
                        
                        if (errorDescriptions == null) {
                            errors.put(doc, errorDescriptions = new ArrayList<ErrorDescription>());
                        }
                        
                        errorDescriptions.add(err);
                    }
                    
                    out.println(line);
                    if (autoflush) out.flush();
                    
                    while (!"".equals(line = is.readLine()) && line != null && !pageDef.matcher(line).find() && line.charAt(0) == ')') { //??sufficient
                        out.println(line);
                        lineNumber++;

                        if (autoflush) out.flush();
                    }
                    
                    lineNumber++;

                    out.println(line);
                    
                    if (autoflush) out.flush();
                    
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
                lineNumber++;
                for (int pNumber = 0; pNumber < patterns.length; pNumber++) {
                    if (patterns[pNumber].process(line)) {
                        continue MAIN_LOOP;
                    }
                }
                
                for (int cntr = 0; cntr < line.length(); cntr++) {
                    switch (line.charAt(cntr)) {
                        case '(':
                            String file = readFileName(line, cntr + 1);
                            
//                            project.log("adding file: " + file, Project.MSG_DEBUG);
                            
                            currentFile.push(file);
                            break;
                        case ')':
                            if (!currentFile.isEmpty())
                                currentFile.pop();
//                                project.log("removing file: " + currentFile.pop().toString(), Project.MSG_DEBUG);
                            
                            break;
                    }
                }
                
                out.println(line);
                
                if (autoflush) out.flush();
            }
        } catch (IOException ex) {
        } finally {
            out.flush();
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
    
    private static class TypingInputStream extends InputStream {
        
        private InputStream ins;
        
        public TypingInputStream(InputStream ins) {
            this.ins = ins;
        }
        
        public int read() throws IOException {
            int r = ins.read();
            
            System.err.print((char) r);
            return r;
        }
        
    }
    
} // end of CopyMaker
