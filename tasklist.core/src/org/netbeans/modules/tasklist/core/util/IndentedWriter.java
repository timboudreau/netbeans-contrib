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
