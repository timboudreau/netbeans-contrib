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
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.api.jackpot.ConversionOperations;
import org.netbeans.api.jackpot.TreePathTransformer;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TreeMaker;

public class SimplifyLoops extends TreePathTransformer<Void,Object> {
    static final Logger logger = Logger.getLogger("org.netbeans.modules.jackpot");
    private TreeMaker make;
    private ConversionOperations ops;
    
    @Override
    public void attach(CompilationInfo info) {
        super.attach(info);
        make = getWorkingCopy().getTreeMaker();
        ops = new ConversionOperations(getWorkingCopy());
    }

    @Override
    public void release() {
        make = null;
        ops = null;
        super.release();
    }
        
    @Override
    public Void visitWhileLoop(WhileLoopTree tree, Object p) {
        pushLoop(tree);
        super.visitWhileLoop(tree, p);
        popLoop(tree);
        String resultMsg = null;
        ExpressionTree cond = tree.getCondition();
        if(ops.eval(cond)==Boolean.TRUE)
            splitter.split(tree.getStatement());
        else { splitter.body = tree.getStatement(); splitter.postBody = null; }
        StatementTree first = getStatement(0,ops.statement(splitter.body));
        if(first instanceof IfTree) {
            int parenCount = 0;
            while (cond instanceof ParenthesizedTree) {
                cond = ((ParenthesizedTree)cond).getExpression();
                parenCount++;
            }
            IfTree ist = (IfTree) first;
            if(getStatement(0,ist.getThenStatement()) instanceof BreakTree) {
                // while (true) { if(b) break; ... }
                splitter.body = ops.block(ist.getElseStatement(),ops.sublist(ops.statement(splitter.body),1));
                cond = ops.and(cond, ops.not((ExpressionTree)ops.deblock(ist.getCondition())));
                resultMsg = "Eliminated 'if break'";
                logger.info("SimplifyLoops: aR "+cond);
            } else if(getStatement(0,ist.getElseStatement()) instanceof BreakTree) {
                // while (true) { if(b) xxx; else break; ... }
                splitter.body = ops.block(ist.getThenStatement(),ops.sublist(ops.statement(splitter.body),1));
                cond = ops.and(cond,ist.getCondition());
                resultMsg = "Eliminated 'else break'";
                logger.info("SimplifyLoops: aR2 "+cond);
            }
            while(parenCount-- > 0)
                cond = make.Parenthesized(cond);
        }
        StatementTree last = lastStatement(ops.statement(splitter.body));
        if(last instanceof ContinueTree && ((ContinueTree)last).getLabel()==null) {
            splitter.body = ops.sublist(ops.statement(splitter.body),0,ops.blockLength(splitter.body)-1);
            resultMsg = "Removed trailing continue";
        }
        if(splitter.body != tree.getStatement() || 
           !cond.toString().equals(tree.getCondition().toString()) ||
           splitter.postBody != null) {
            StatementTree newTree = make.WhileLoop(cond,ops.statement(splitter.body));
            if(splitter.postBody != null) {
                resultMsg = "while split body";
                newTree = ops.block(newTree,ops.statement(splitter.postBody));
                logger.info("SimplifyLoops: aR3 "+cond);
            }
            addChange(getCurrentPath(), newTree, resultMsg);
        }
        return null;
    }
    
    private static StatementTree getStatement(int index, StatementTree a) {
        if (a != null)
            if (a instanceof BlockTree) {
            java.util.List<? extends StatementTree> stats = ((BlockTree)a).getStatements();
            return index < stats.size() ? stats.get(index) : null;
            } else
                return index != 0 ? null : a;
        return null;
    }
    
    private static StatementTree lastStatement(StatementTree a) {
        if(a instanceof BlockTree) {
            java.util.List<? extends StatementTree> stats = ((BlockTree)a).getStatements();
            int n = stats.size();
            return n > 0 ? stats.get(n - 1) : a;
        } else
            return a;
    }
    
    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree tree, Object p) {
        pushLoop(tree);
        super.visitDoWhileLoop(tree, p);
        popLoop(tree);
        return null;
    }
    
    @Override
    public Void visitForLoop(ForLoopTree tree, Object p) {
        pushLoop(tree);
        super.visitForLoop(tree, p);
        popLoop(tree);
        return null;
    }
    
    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree tree, Object p) {
        pushLoop(tree);
        super.visitEnhancedForLoop(tree, p);
        popLoop(tree);
        return null;
    }
    
    @Override
    public Void visitSwitch(SwitchTree tree, Object p) {
        pushLoop(tree);
        super.visitSwitch(tree, p);
        popLoop(tree);
        return null;
    }
    
    @Override
    public Void visitContinue(ContinueTree tree, Object p) {
        CharSequence label = resolvelabel(tree.getLabel());
        if (label != tree.getLabel()) {
            ContinueTree newTree = make.Continue(label);
            addChange(getCurrentPath(), newTree);
        }
        return null;
    }
    
    @Override
    public Void visitBreak(BreakTree tree, Object p) {
        CharSequence label = resolvelabel(tree.getLabel());
        if (label != tree.getLabel()) {
            BreakTree newTree = make.Break(label);
            addChange(getCurrentPath(), newTree);
        }
        return null;
    }
    
    @Override
    public Void visitLabeledStatement(LabeledStatementTree tree, Object p) {
        LabelStack mylabel = new LabelStack(tree.getLabel(),tree.getStatement());
        tree.getStatement().accept(this, null); // look for label references
        mylabel.pop();
        if(!mylabel.used) {
            StatementTree newTree = tree.getStatement();
            addChange(getCurrentPath(), newTree,
                      "Removed unnecessary label: " + tree.getLabel());
        } else {
            Tree.Kind kind = tree.getStatement().getKind();
            switch(kind) {
                case WHILE_LOOP:
                case FOR_LOOP:
                case DO_WHILE_LOOP:
                    StatementTree body = tree.getStatement();
                    StatementTree b2;
                    if(body.getKind() != kind && (b2=(StatementTree)getStatement(0,body))!=null && b2.getKind()==kind) {
                        //  loop broke into a block; move the label into the block
                        StatementTree newTree = ops.block(make.LabeledStatement(tree.getLabel(),b2),ops.sublist(body,1));
                        addChange(getCurrentPath(), newTree,
                                  "Moved label into block: " + tree.getLabel());
                    }
                    break;
            }
        }
        return null;
    }
    
    class LabelStack {
        CharSequence label;
        LabelStack prev;
        boolean used;
        Tree body;
        LabelStack(CharSequence l, Tree b) {
            label = l;
            body = b;
            used = false;
            prev = labels;
            labels = this;
        }
        public void pop() {
            labels = prev;
        }
    }
    LabelStack labels;
    public CharSequence resolvelabel(CharSequence l) {
        if(l==null || labels==null)
            return l;
        
        // if label use is on same frame as declaration, remove it
        if(labels.label==l)
            return null;
        
        // mark label use in any previous frames
        for(LabelStack ls = labels.prev; ls!=null; ls = ls.prev)
            if(ls.label==l) {
                ls.used = true;
                break;
            }
        return l;
    }
    public void pushLoop(Tree t) {
        if(labels==null || labels.body != t) new LabelStack(null,t);
    }
    public void popLoop(Tree t) {
        if(labels!=null && labels.body==t && labels.label==null)
            labels.pop();
    }
    
    final LoopBodySplitter splitter = new LoopBodySplitter();
    class LoopBodySplitter extends TreeScanner<Void,Object> {
        Tree body;
        Tree postBody;
        public boolean split(StatementTree t) {
            if(t==null) return false;
            body = t;
            postBody = null;
            t.accept(this, null);
            return body!=t || postBody!=null;
        }
        @Override
        public Void scan(Tree t, Object p) {
            if (t  == null)
                return null;
            if(t instanceof StatementTree && (howExits(t) & (FLOWEXIT|CONTINUEEXIT|BREAKEXIT))==0) {
                body=null;
                postBody=t;
            } else {
                body=t;
                postBody=null;
            }
            return t.accept(this, p);
        }
        
        @Override
        public Void visitIf(IfTree t, Object p) {
            int tx = howExits(t.getThenStatement());
            int ex = howExits(t.getElseStatement());
            if((tx & (FLOWEXIT | CONTINUEEXIT)) == 0) {
                postBody = t.getThenStatement();
                body = ops.block(ops.If(t.getCondition(),make.Break(null),null),t.getElseStatement());
            } else
                if((ex & (FLOWEXIT | CONTINUEEXIT)) == 0) {
                    postBody = t.getElseStatement();
                    body = ops.block(ops.If(ops.not(t.getCondition()),make.Break(null),null),t.getThenStatement());
                } else {
                body = t;
                postBody = null;
                }
            return null;
        }
        
        @Override
        public Void visitBlock(BlockTree tree, Object p) {
            List<? extends StatementTree> stats = tree.getStatements();
            if(stats.isEmpty())
                return null;
            int len = ops.blockLength(tree);
            if(len == 1) {
                stats.get(0).accept(this, p);
                return null;
            }
            Tree last = getStatement(len-1,tree);
            int howLast = howExits(last);
            if((howLast&(FLOWEXIT|CONTINUEEXIT))==0) {
                for(int splitPos = len-1; --splitPos>=0; ) {
                    StatementTree splitTree = getStatement(splitPos,tree);
                    if((howExits(splitTree)&CONTINUEEXIT)!=0) {
                        logger.info("SimplifyLoops: splitting Block");
                        body = splitTree;
                        postBody = null;
                        splitTree.accept(this, p);
                        body = ops.block(ops.sublist(tree,0,splitPos),ops.statement(body));
                        postBody = ops.block(ops.statement(postBody),ops.sublist(tree,splitPos+1,len-splitPos-1));
                    }
                }
            } else {
                for(int splitPos = 0; splitPos<len; splitPos++) {
                    Tree splitTree = getStatement(splitPos,tree);
                    if(!(splitTree instanceof IfTree)) continue;
                    IfTree ifst = (IfTree)splitTree;
                    logger.fine("SimplifyLoops: " + splitPos +
                            " exits " + exit2String(howExits(splitTree)) +
                            ":  st="+splitTree);
                    if ((howExits(ifst.getThenStatement()) & (CONTINUEEXIT | FLOWEXIT))==0 &&
                            !(ifst.getThenStatement() instanceof BreakTree)) {
                        postBody = ifst.getThenStatement();
                        int plen = ops.blockLength(postBody);
                        Tree plast = getStatement(plen-1,ops.statement(postBody));
                        if(plast instanceof BreakTree && ((BreakTree)plast).getLabel()==null)
                            postBody = ops.sublist(ops.statement(postBody),0,plen-1);
                        body = ops.block(ops.sublist(tree,0,splitPos),
                                ops.block(ops.If(ifst.getCondition(),make.Break(null),null),
                                ops.sublist(tree,splitPos+1,len-splitPos-1)));
                        logger.info("SimplifyLoops: extracted" + postBody + " from loop");
                    }
                }
            }
            return null;
        }
    }
    
    static final public int RETURNEXIT   = 1;
    static final public int THROWEXIT    = 2;
    static final public int BREAKEXIT    = 4;
    static final public int CONTINUEEXIT = 8;
    static final public int FLOWEXIT     =16;
    static final private String[] exitStrings = { "return", "throw", "break", "continue", "flow" };
    
    static protected final String exit2String(int exitMask) {
        StringBuffer ret = null;
        for(int i = exitStrings.length; --i>=0; ) {
            int m = 1<<i;
            if(m==exitMask) return exitStrings[i];
            if((m&exitMask)!=0) {
                if(ret==null) ret = new StringBuffer();
                else ret.append('|');
                ret.append(exitStrings[i]);
            }
        }
        return ret==null ? "" : ret.toString();
    }
    
    static int howExits(Tree tree) {
        if(tree==null)
            return FLOWEXIT;
        switch(tree.getKind()) {
            default:
                return FLOWEXIT;
            case RETURN:
                return RETURNEXIT;
            case BREAK:
                return BREAKEXIT;
            case CONTINUE:
                return CONTINUEEXIT;
            case THROW:
                return THROWEXIT;
            case LABELED_STATEMENT:
            {
                int r = howExits(((LabeledStatementTree)tree).getStatement());
                return (r&BREAKEXIT)!=0 ? r&~BREAKEXIT | FLOWEXIT : r;
            }
            case IF:
            {
                IfTree s = (IfTree) tree;
                return howExits(s.getThenStatement()) | howExits(s.getElseStatement());
            }
            case WHILE_LOOP:
                return howLoopExits(((WhileLoopTree)tree).getStatement());
            case DO_WHILE_LOOP:
                return howLoopExits(((DoWhileLoopTree)tree).getStatement());
            case FOR_LOOP:
                return howLoopExits(((ForLoopTree)tree).getStatement());
            case ENHANCED_FOR_LOOP:
                return howLoopExits(((EnhancedForLoopTree)tree).getStatement());
            case BLOCK:
                return howExits(((BlockTree)tree).getStatements());
            case SWITCH: {
                int R = 0;
                for(CaseTree caseTree : ((SwitchTree)tree).getCases())
                    R |= howExits(caseTree.getStatements());
                return (R&BREAKEXIT)==0 ? R : R&~BREAKEXIT | FLOWEXIT;
            }
        }
    }
    
    private static int howExits(List<? extends Tree> statements) {
        int R = 0;
        for (Tree t : statements) {
            int r = howExits(t);
            R |= r;
            if((r&FLOWEXIT)==0) return R&~FLOWEXIT;
        }
        return R==0 ? FLOWEXIT : R;
    }
    
    private static int howLoopExits(Tree body) {
        int r = howExits(body);
        if((r&BREAKEXIT)!=0) r = r&~BREAKEXIT | FLOWEXIT;
        return r&~CONTINUEEXIT;
    }
}
