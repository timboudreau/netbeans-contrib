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
