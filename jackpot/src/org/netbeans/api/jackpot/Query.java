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

import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;

/**
 * A Query is applied to a a set of source files (defined by a <code>
 * JavaSource</code> instance) and reports any matching results.  
 * 
 * @author Tom Ball
 */
public interface Query {

    /**
     * Initialize any data that is shared across source file processing
     * invocations.
     * 
     * @param context the application environment the query executes within.
     * @param source the JavaSource instance which this query is invoked with.
     */
    void init(QueryContext context, JavaSource source);

    /**
     * Attach this Query instance to the specified CompilationInfo prior
     * to processing one or more source files.
     * 
     * @param info the CompilationInfo associated with this set of source files.
     */
    void attach(CompilationInfo info);
    
    /**
     * Execute the query for the set of source files.
     */
    void run();
    
    /**
     * Release any instance data created by attach() or the processing of
     * a set of source files.  All references to the attached CompilationInfo
     * and its source files must be released.
     */
    void release();
    
    /**
     * Release any instance data shared by invocations, including all references
     * to the JavaSource instanced passed to <code>init()</code>.
     */
    void destroy();
    
    /**
     * Cancel (stop) query execution.
     */
    void cancel();
    
    /**
     * Returns the JavaSource this query was initialized with.
     * 
     * @return the JavaSource, or null if not initialized.
     */
    JavaSource getJavaSource();
    
    /**
     * Returns the CompilationInfo attached to this query.
     * 
     * @return the CompilationInfo, or null if not attached.
     */
    CompilationInfo getCompilationInfo();
}
