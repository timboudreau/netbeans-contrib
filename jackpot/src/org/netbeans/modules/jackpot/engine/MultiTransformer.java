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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jackpot.engine;

import org.netbeans.api.jackpot.Query;
import org.netbeans.api.jackpot.QueryContext;
import org.netbeans.api.jackpot.TreePathTransformer;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.WorkingCopy;

/**
 * Execute multiple Query and Transformer instances as a single command.
 */
final class MultiTransformer extends TreePathTransformer {
    Query[] commands;
    int done;
    boolean cancelled;
    
    public MultiTransformer(String name, Query[] queries) throws Exception {
        super();
        commands = queries;
    }
    
    @Override
    public void init(QueryContext context, JavaSource js) {
        super.init(context, js);
        for (Query q : commands)
            q.init(context, js);
    }
    
    @Override
    public void attach(CompilationInfo info) {
        super.attach(info);
        for (Query q : commands)
            q.attach(info);
    }
    
    @Override
    public void release() {
        for (Query q : commands)
            q.release();
        super.release();
    }
    
    @Override
    public void destroy() {
        for (Query q : commands)
            q.destroy();
        super.destroy();
    }
    
    @Override
    public void run() {
        for (Query q : commands) {
            if (cancelled)
                return;
            q.run();
        }
    }
}
