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

import com.sun.source.tree.IfTree;
import com.sun.source.tree.StatementTree;
import org.netbeans.api.jackpot.ConversionOperations;
import org.netbeans.api.jackpot.TreeMatcher;
import org.netbeans.api.jackpot.TreePathTransformer;
import org.netbeans.api.java.source.CompilationInfo;

public class TailMerge extends TreePathTransformer<Void,Object> {
    private ConversionOperations ops;
    
    @Override
    public void attach(CompilationInfo info) {
        super.attach(info);
        ops = new ConversionOperations(getWorkingCopy());
    }
    
    @Override
    public Void visitIf(IfTree tree, Object p) {
        /* First we try merging before applying merging to child nodes.
           If that doesn't yield anything, apply merging to children and try again */
        StatementTree thenpart = (StatementTree)ops.deblock(tree.getThenStatement());
        StatementTree elsepart = (StatementTree)ops.deblock(tree.getElseStatement());
        int elselen = ops.blockLength(elsepart);
        if(elselen != 0) {
            int thenlen = ops.blockLength(thenpart);
            if(thenlen!=0) {
                int tlen = elselen<thenlen ? elselen : thenlen;
                int matchlen = 0;
                while(matchlen<tlen) {
                    StatementTree T = ops.getStatement(thenlen-matchlen-1,thenpart);
                    StatementTree E = ops.getStatement(elselen-matchlen-1,elsepart);
                    if(!TreeMatcher.matches(T,E)) break;
                    matchlen++;
                }
                if(matchlen > 0) {
                    StatementTree newIf = ops.If(tree.getCondition(),
                                    ops.sublist(thenpart,0,thenlen-matchlen),
                                    ops.sublist(elsepart,0,elselen-matchlen));
                    StatementTree newTree = ops.block(newIf, ops.sublist(thenpart,thenlen-matchlen,matchlen));
                    ops.copyComments(tree,newTree);
                    addChange(getCurrentPath(), newTree);
                    return null;
                }
            }
        }
        super.visitIf(tree, p);
        return null;
    }
}
