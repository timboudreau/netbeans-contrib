/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

// @todo The current version does not handle comment tokens inside "" or ''
//       correct! (remember that such a section may span multiple lines!!!
// @todo When we drop support for older java vm's (<1.4) I would really like
//       to modify this class to return a StringBuffer to the SourceScanner,
//       since the regexp-support in java 1.4 can work directly on a
//       StringBuffer

package org.netbeans.modules.tasklist.docscan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * The Source Code Comment Parser allows you to read the comments in a source
 * code line by line.
 *
 * @author Trond Norbye
 */
public class SourceCodeCommentParser {
    
    /**
     * Default instance, treat all lines as comments!!
     */
    public SourceCodeCommentParser() {
        parser = new SourceParser();
    }
    
    /**
     * Create a new instance of the SourceCodeCommentParser that only supports
     * singe line comments
     * @param lineComment The start tag of a "single line comment"
     */
    public SourceCodeCommentParser(String lineComment) {
        parser = new CommentParser(lineComment);
    }
    
    /**
     * Create a new instance of SourceCodeCommentParser that only supports
     * block comments (like C)
     * @param blockStart the start tag of a block comment
     * @param blockEnd the end tag of a block comment
     */
    public SourceCodeCommentParser(String blockStart, String blockEnd) {
        parser = new CommentParser(null, blockStart, blockEnd);
    }
    
    /**
     * Create a new instance of SourceCodeCommentParser that supports single-
     * line comments, and multiline comments
     * @param lineComment the start tag for a single-line comment
     * @param blockStart the start tag of a block comment
     * @param blockEnd the end tag of a block comment
     */
    public SourceCodeCommentParser(String lineComment,
                                   String blockStart,
                                   String blockEnd) {
        parser = new CommentParser(lineComment, blockStart, blockEnd);
    }
    
    /**
     * Set the document to parse
     * @param doc the document to parse
     * @throws javax.swing.text.BadLocationException if anythings goes wrong
     */
    public void setDocument(Document doc) throws BadLocationException {
        parser.setDocument(doc);
    }
    
    /**
     * Set the document to parse
     * @param doc the document to parse
     * @throws java.io.IOException if anything goes wrong...
     */
    public void setDocument(File f) throws IOException {
        parser.setDocument(f);
    }
    
    /**
     * get the next line of text from the file
     * @param ret Where to store the result
     * @return false when EOF, true otherwise
     */
    public boolean getNextLine(CommentLine ret) throws IOException {
        return parser.getNextLine(ret);
    }
    
    /**
     * I don't know if this was a smart thing to do, but instead of testing
     * each time if I should skip comments or not, I decided to create an
     * an internal parser that I could extend to my needs... The most generic
     * parser treats everything as comments, and should hence "work" for all
     * unknown file types ;)
     * Since I was to lazy to override the setDocument method in the
     * Comment parser, I inserted the variables rest and inComment into the
     * base class so I could initialize them here....
     */
    private class SourceParser {
        
        /**
         * Create a new instance of the SourceParser
         */
        public SourceParser() {
            doc = null;
            lineno = 0;
            rest = null;
            inComment = false;
            buffer = new StringBuffer(400);
            rest = new StringBuffer();
        }
        
        /**
         * Get the next line of text from the file.
         * @param ret Where to store the result
         * @return false when EOF, true otherwise
         * @throws java.io.IOException if a read error occurs on the input
         *         stream.
         */
        public boolean getNextLine(CommentLine ret) throws IOException {
            if (doc.readTrimmedLine(buffer)) {
                ret.line = buffer.toString();
            } else {
                ret.line = null; // ret.line = doc.readLine();
            }
            ++lineno;
            ret.lineno = lineno;
            return ret.line != null;
        }
        
        /**
         * Set the document to parse
         * @param doc the document to parse
         * @throws javax.swing.text.BadLocationException when....
         */
        public void setDocument(Document doc) throws BadLocationException {
            String text = doc.getText(0, doc.getLength());
            this.doc = new SourceReader(new BufferedReader(new StringReader(text)));
            lineno = 0;
            rest.setLength(0);;
            inComment = false;
        }
        
        /**
         * Set the document to parse
         * @param doc the document to parse
         */
        public void setDocument(File f) throws IOException {
            this.doc = new SourceReader(new BufferedReader(new FileReader(f)));
            lineno = 0;
            rest.setLength(0);
            inComment = false;
        }
        
        /**
         * A StringBuffer that I use towards the source reader to avoid the
         * creation of a lot of strings...
         */
        protected StringBuffer buffer;
        
        /** The reader I use to read the document content from */
        protected SourceReader doc;
        /** Current line in the file */
        protected int lineno;
        /**
         * If I had to split the line i just read (mix of comment and code),
         * rest contains the rest of the line I just read.
         */
        protected StringBuffer rest;
        /** Am I currently inside a comment? */
        protected boolean inComment;
    }
    
    /**
     * The comment parser exstend the source parser with functionality to
     * create single line comments, and a block of lines that are treated as
     * a comment.
     */
    private class CommentParser extends SourceParser {
        /**
         * Create a new instance of the comment parser that only supports
         * a "single-line" comments
         * @param lineComment the token to start a line comment
         */
        public CommentParser(String lineComment) {
            this(lineComment, null, null);
        }
        
        /**
         * Create a new instance of the comment parser that supports:
         * @param lineComment the token for a single line comment
         * @param blockStart the start token for a multiline comment block
         * @param blockEnd the end token for a multiline comment block
         */
        public CommentParser(String lineComment,
        String blockStart,
        String blockEnd) {
            super();
            this.lineComment = lineComment;
            this.blockStart = blockStart;
            this.blockEnd = blockEnd;
            inComment = false;
        }
        
        /**
         * Get the next line of text from the file.
         * @param ret Where to store the result
         * @return false when EOF, true otherwise
         * @throws java.io.IOException if a read error occurs on the input
         *         stream.
         */
        public boolean getNextLine(CommentLine ret) throws IOException {
            StringBuffer active;
            
            // Read next line of input!
            while (rest.length() != 0 || doc.readTrimmedLine(buffer)) {
                if (rest.length() > 0) {
                    // I should operate on the rest of this line...
                    active = rest;
                } else {
                    // I should operate on the line I just read from the file..
                    active = buffer;
                    ++lineno;
                }
                
                if (!inComment) {
                    // I'm not inside a comment, but this line might contain one
                    int lineIdx = java.lang.Integer.MAX_VALUE;
                    if (lineComment != null) {
                        lineIdx = active.indexOf(lineComment);
                        if (lineIdx == -1) {
                            lineIdx = java.lang.Integer.MAX_VALUE;
                        }
                    }
                    
                    int blockIdx = java.lang.Integer.MAX_VALUE;
                    if (blockStart != null) {
                        blockIdx = active.indexOf(blockStart);
                        if (blockIdx == -1) {
                            blockIdx = java.lang.Integer.MAX_VALUE;
                        }
                    }
                    
                    int idx = java.lang.Math.min(lineIdx, blockIdx);
                    if (idx != java.lang.Integer.MAX_VALUE) {
                        // This line contains the start of a comment!!!!
                        if (idx == lineIdx) {
                            ret.line = active.substring(idx + lineComment.length());
                            ret.lineno = lineno;
                            active.setLength(0);
                            return true;
                        } else {
                            // delete all characters up to the beginning of the comment
                            active.delete(0, idx + blockStart.length());
                            inComment = true;
                        }
                    } else {
                        // Get the next line of source code!!
                        continue;
                    }
                }
                
                // We're inside a comment... search for the end of the
                // comment... The _only_ way to get here is if blockStart !=
                // null, and it should _not_ be possible to configure a
                // setup where you have a block start and not a block end!
                
                int idx = active.indexOf(blockEnd);
                
                if (idx != -1) {
                    rest.append(active.substring(idx + blockEnd.length()));
                    active.delete(idx, active.length());
                    inComment = false;
                }
                
                ret.line = active.toString();
                ret.lineno = lineno;
                return true;
            }
            
            return false;
        }
        
        /** The string that indicates the start of a single line comment */
        protected String  lineComment;
        /** The string that indicates the start of a multiline comment */
        protected String  blockStart;
        /** The string that indicates the end of a multiline comment */
        protected String  blockEnd;
    }
    
    /**
     * A small holder-class for the source code line and line number...
     */
    public static class CommentLine {
        /** The current line number in the source code file */
        public int    lineno;
        /** The comment part of that source line... */
        public String line;
    }
    
    /** The parser used by this SourceCodeCommentParser */
    private SourceParser parser;
    
    /**
     * Tor told me once that there is a "big" performance penalty when I create
     * a lot of string objects, caused by the garbage collector. I still like
     * to be able to just scan the comment sections of my files, so instead of
     * creating one string for each line in the file, i now create one String
     * for each _line_ in a _comment_ section.
     */
    private class SourceReader {
        private java.io.Reader input;
        private char buffer[];
        private int current;
        private int buffsz;
        
        /**
         * Create a new source reader from the input stream...
         * @param in The input stream to read from
         */
        public SourceReader(java.io.Reader in) {
            input = in;
            buffer = new char[4096];
            current = buffsz = buffer.length;
        }
        
        /**
         * Read a line of text into the buffer...
         * @param buf the stringbuffer to insert the new trimmed line into
         * @return false if EOF and buf should be ignored
         */
        private boolean readTrimmedLine(StringBuffer buf) throws IOException {
            buf.setLength(0);
            boolean start = true;
            int     lastspace = Integer.MAX_VALUE;
            int     min = current;
            boolean done = false;
            
            do {
                if (current == buffsz) {
                    if (min != current) {
                        buf.append(buffer, min, (current - min));
                    }
                    
                    buffsz = input.read(buffer);
                    current = 0;
                    min = 0;
                    
                    if (buffsz == -1) {
                        if (buf.length() > 0) {
                            return true;
                        }
                        return false;
                    }
                }
                
                if (buffer[current] == '\r' || buffer[current] == '\n') {
                    buf.append(buffer, min, current - min);
                    ++current;
                    done = true;
                } else {
                    if (buffer[current] == ' ') {
                        if (start) {
                            ++min;
                        } else {
                            if (lastspace == Integer.MAX_VALUE) {
                                lastspace = current;
                            }
                        }
                    } else {
                        start = false;
                        lastspace = Integer.MAX_VALUE;
                    }
                    ++current;
                }
            } while (!done);
            if (lastspace != Integer.MAX_VALUE) {
                buf.setLength(lastspace - min);
            }
            return true;
        }
    }
}

