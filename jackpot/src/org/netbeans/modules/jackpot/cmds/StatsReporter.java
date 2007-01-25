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

package org.netbeans.modules.jackpot.cmds;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import org.netbeans.api.java.source.query.Query;
import org.openide.util.NbBundle;
import java.io.*;
import java.text.ChoiceFormat;
import java.text.MessageFormat;

/**
 * Reports basic usage numbers for the current session.
 */
public class StatsReporter extends Query<Void,Object> {
    private TreeCounter counter;

    @Override
    public void apply() {
        apply(getRootNode());
        PrintWriter log = env.getOutputWriter(getString("StatsReporter.title")); //NOI18N
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
    
    @Override
    public void apply(Tree t) {
	if(t != null) {
            counter = new TreeCounter();
	    t.accept(counter, null);
        }
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
