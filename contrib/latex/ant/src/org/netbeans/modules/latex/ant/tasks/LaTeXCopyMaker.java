/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
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
import java.io.Reader;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    private File baseDir;
    
    LaTeXCopyMaker(File baseDir, InputStream is, OutputStream os) {
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
                    //ignored...
                }
            }
            
            return false;
        }
    }
    
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
                    
                    while (!"".equals(line = is.readLine()) && !(line.charAt(0) != '[') && (line != null)) { //??sufficient
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
                            //                                System.err.println("adding file: " + file);
                            currentFile.push(file);
                            break;
                        case ')':
                            if (!currentFile.isEmpty())
                            /*System.err.println(*/currentFile.pop()/*.toString())*/;
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
