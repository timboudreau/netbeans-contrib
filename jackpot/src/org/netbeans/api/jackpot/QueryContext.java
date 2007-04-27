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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.jackpot;

import com.sun.source.util.TreePath;
import java.io.PrintWriter;
import org.openide.filesystems.FileObject;

/**
 * A QueryContext is the environment that a Query is executed in, which
 * provides methods for communicating with the user through query results,
 * status bar and log messages, and errors.
 * <p>
 * <em>Note:</em> Query developers should never implement QueryContext.
 * The Jackpot module creates the QueryContext instance which the Query
 * will reference during its execution.
 * 
 * @author Tom Ball
 */
public interface QueryContext {
    
    /**
     * Add a tree which matches this query to the results set.  If the query
     * extends TreePathScanner, its <code>getCurrentPath()</code> method 
     * returns the correct path for the visitor method which is currently visited.
     * 
     * @param path the path to the tree which matches this query.
     * @param file the source file
     * @param start the begin text position of the matched tree
     * @param end the end text position of the matched tree
     * @param label a short string describing the tree or an enclosing class member.
     * @param note an optional note with details about the match, or 
     *        <code>null</code> if there is no note.
     * @see   com.sun.source.util.TreePathScanner#getCurrentPath
     */
    void addResult(TreePath path, FileObject file, int start, int end, 
                   String label, String note);
    
    /**
     * Add a tree which matches this query to the results set, along with its
     * replacement source code.  The new tree text is not the actual replacement text
     * (which normally has additional formatting), but is a simplified version
     * suitable for displaying in a user interface.
     * 
     * @param path the path to the tree which matches this query.
     * @param file the source file
     * @param start the begin text position of the matched tree
     * @param end the end text position of the matched tree
     * @param label a short string describing the tree or an enclosing class member.
     * @param note an optional note with details about the match, or 
     *        <code>null</code> if there is no note.
     * @param newSource a simple version of the replacement text
     * @see   com.sun.source.util.TreePathScanner#getCurrentPath
     */
    void addChange(TreePath path, FileObject file, int start, int end, 
                   String label, String note, String newSource);

    /**
     * Send the application a status message.  A status message should
     * normally be short enough to be displayed in the IDE's status bar.  
     * Multiple status messages may be sent during a query.
     * 
     * @param message the message to be displayed in the status bar.
     */
    void sendStatusMessage(String message);
    
    /**
     * Send the application an error message regarding a query
     * execution.  A long message may be sent, and normally a single
     * error message is sent during a single query execution.  
     * 
     * @param message the error message to be displayed.
     * @param title the name of the query or a general error title.
     */
    void sendErrorMessage(String message, String title);

    /**
     * Opens an output writer for log-type messages similar to what
     * javac sends to System.out.
     * 
     * @return a java.io.PrintWriter to write log messages.
     * @see java.io.PrintWriter
     */
    PrintWriter getLogWriter();
}
