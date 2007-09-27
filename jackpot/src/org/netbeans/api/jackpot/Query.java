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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
