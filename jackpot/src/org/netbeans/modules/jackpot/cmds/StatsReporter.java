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

package org.netbeans.modules.jackpot.cmds;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import org.openide.util.NbBundle;
import java.io.*;
import java.text.ChoiceFormat;
import java.text.MessageFormat;
import org.netbeans.api.jackpot.QueryContext;
import org.netbeans.api.jackpot.TreePathQuery;
import org.netbeans.api.java.source.JavaSource;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * Reports basic usage numbers for the current session.
 */
public class StatsReporter extends TreePathQuery {
    private TreeCounter counter;

    @Override
    public void init(QueryContext context, JavaSource source) {
        counter = new TreeCounter();
    }
    
    @Override
    public void release() {
        String title = getString("StatsReporter.title");  //NOI18N
        InputOutput io = IOProvider.getDefault().getIO(title, true);
        io.select();
        PrintWriter log = io.getOut();
        log.println(statLine(counter.sourceFiles, "StatsReporter.sources"));     //NOI18N
        log.println(statLine(counter.classes, "StatsReporter.classes"));         //NOI18N
        log.println(statLine(counter.methods, "StatsReporter.methods"));         //NOI18N
        log.println(statLine(counter.variables, "StatsReporter.variables"));     //NOI18N
	log.println(statLine(counter.statements, "StatsReporter.statements"));   //NOI18N
	log.println(statLine(counter.expressions, "StatsReporter.expressions")); //NOI18N
        log.println(statLine(counter.nodes, "StatusReporter.nodes")); //NOI18N
        log.close();
        counter = null;
        log = null;
    }
    
    private static String getString(String key) {
        return NbBundle.getBundle(StatsReporter.class).getString(key);
    }
    
    private String statLine(int c, String statKey) {
        String statFormat = getString("StatsReporter.status.line");              //NOI18N
        ChoiceFormat cf = new ChoiceFormat(getString(statKey));
        String statValue = cf.format(c);
	String s = MessageFormat.format(statFormat, c, statValue);
        return s;
    }

    static class TreeCounter extends TreeScanner<Void,Object> {
	int sourceFiles = 0;
	int classes = 0;
	int methods = 0;
	int variables = 0;
	int statements = 0;
	int expressions = 0;
	int nodes = 0;

        @Override
        public Void scan(Tree tree, Object p) {
            if (tree!=null) {
                nodes++;
		if (tree instanceof StatementTree &&
		    !(tree instanceof VariableTree) && 
                    !(tree instanceof ClassTree))
		    statements++;
		else if (tree instanceof ExpressionTree)
		    expressions++;
                tree.accept(this, p);
            }
            return null;
        }
        
        @Override
	public Void visitCompilationUnit(CompilationUnitTree tree, Object p) {
	    sourceFiles++;
	    super.visitCompilationUnit(tree, p);
            return null;
	}

        @Override
	public Void visitClass(ClassTree tree, Object p) {
	    classes++;
	    super.visitClass(tree, p);
            return null;
	}

        @Override
	public Void visitMethod(MethodTree tree, Object p) {
	    methods++;
	    super.visitMethod(tree, p);
            return null;
	}
	
        @Override
	public Void visitVariable(VariableTree tree, Object p) {
	    variables++;
	    super.visitVariable(tree, p);
            return null;
	}
    }	
}
