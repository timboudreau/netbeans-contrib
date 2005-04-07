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

package org.netbeans.modules.tasklist.core.util;

import java.io.*;

/**
 * Indented writer. Provides methods to increase and decrease indentation
 * of each line.
 *
 * @author tl
 */
public final class IndentedWriter extends PrintWriter {
    private boolean needIndent = true;
    private String indent = "";

    /**
     * Create a new PrintWriter.
     *
     * @param out a character-output stream
     * @param autoFlush a boolean; if true, the println() methods will flush
     * the output buffer
     */
    public IndentedWriter(Writer out, boolean autoFlush) {
    	super(out, autoFlush);
    }

    /** 
     * Constructor
     *
     * @param out output writer
     */
    public IndentedWriter(Writer out) {
    	super(out);
    }

    /**
     * Create a new PrintWriter from an existing OutputStream.  This
     * convenience constructor creates the necessary intermediate
     * OutputStreamWriter, which will convert characters into bytes using the
     * default character encoding.
     *
     * @param out An output stream
     * @param autoFlush A boolean; if true, the println() methods will flush
     * the output buffer
     *
     * @see java.io.OutputStreamWriter#OutputStreamWriter(java.io.OutputStream)
     */
    public IndentedWriter(OutputStream out, boolean autoFlush) {
    	super(out, autoFlush);
    }

    /**
     * Create a new PrintWriter, without automatic line flushing, from an
     * existing OutputStream.  This convenience constructor creates the
     * necessary intermediate OutputStreamWriter, which will convert characters
     * into bytes using the default character encoding.
     *
     * @param out an output stream
     *
     * @see java.io.OutputStreamWriter#OutputStreamWriter(java.io.OutputStream)
     */
    public IndentedWriter(OutputStream out) {
        super(out);
    }

    public void println() {
        super.println();
        needIndent = true;
    }

    public void write(String s) {
    	if(needIndent) {
            super.write(indent);
            needIndent = false;
    	}
    	super.write(s);
    }

    /**
     * Increases the indentation
     */
    public void indent() {
        indent += "    ";
    }

    /**
     * Decrease the indentation
     */
    public void unindent() {
        indent = indent.substring(4);
    }
}
