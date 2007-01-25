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

import org.netbeans.api.java.source.transform.Transformer;
import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import java.util.List;
import java.util.logging.Logger;

public class SimplifyLoops extends Transformer<Void,Object> {
    static final Logger logger = Logger.getLogger("org.netbeans.jackpot");
    
    { queryDescription = "Complex while loops"; }
    
    @Override
    public Void visitWhileLoop(WhileLoopTree tree, Object p) {
        pushLoop(tree);
        super.visitWhileLoop(tree, p);
        popLoop(tree);
        String resultMsg = null;
        ExpressionTree cond = tree.getCondition();
        if(eval(cond)==Boolean.TRUE)
            splitter.split(tree.getStatement());
        else { splitter.body = tree.getStatement(); splitter.postBody = null; }
        StatementTree first = getStatement(0,statement(splitter.body));
        if(first instanceof IfTree) {
            int parenCount = 0;
            while (cond instanceof ParenthesizedTree) {
                cond = ((ParenthesizedTree)cond).getExpression();
                parenCount++;
            }
            IfTree ist = (IfTree) first;
            if(getStatement(0,ist.getThenStatement()) instanceof BreakTree) {
                // while (true) { if(b) break; ... }
                splitter.body = block(ist.getElseStatement(),sublist(statement(splitter.body),1));
                cond = and(cond,not(ist.getCondition()));
                resultMsg = "Eliminated 'if break'";
                logger.info("SimplifyLoops: aR "+cond);
            } else if(getStatement(0,ist.getElseStatement()) instanceof BreakTree) {
                // while (true) { if(b) xxx; else break; ... }
                splitter.body = block(ist.getThenStatement(),sublist(statement(splitter.body),1));
                cond = and(cond,ist.getCondition());
                resultMsg = "Eliminated 'else break'";
                logger.info("SimplifyLoops: aR2 "+cond);
            }
            while(parenCount-- > 0)
                cond = make.Parenthesized(cond);
        }
        StatementTree last = lastStatement(statement(splitter.body));
        if(last instanceof ContinueTree && ((ContinueTree)last).getLabel()==null) {
            splitter.body = sublist(statement(splitter.body),0,blockLength(splitter.body)-1);
            resultMsg = "Removed trailing continue";
        }
        if(splitter.body != tree.getStatement() || 
           !cond.toString().equals(tree.getCondition().toString()) ||
           splitter.postBody != null) {
            StatementTree newTree = make.WhileLoop(cond,statement(splitter.body));
            if(splitter.postBody != null) {
                resultMsg = "while split body";
                newTree = block(newTree,statement(splitter.postBody));
                logger.info("SimplifyLoops: aR3 "+cond);
            }
            changes.rewrite(tree, newTree);
            addResult(newTree, resultMsg);
        }
        return null;
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
            changes.rewrite(tree, newTree);
        }
        return null;
    }
    
    @Override
    public Void visitBreak(BreakTree tree, Object p) {
        CharSequence label = resolvelabel(tree.getLabel());
        if (label != tree.getLabel()) {
            BreakTree newTree = make.Break(label);
            changes.rewrite(tree, newTree);
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
            changes.rewrite(tree, newTree);
            addResult(newTree, "Removed unnecessary label: " + tree.getLabel());
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
                        StatementTree newTree = block(make.LabeledStatement(tree.getLabel(),b2),sublist(body,1));
                        changes.rewrite(tree, newTree);
                        addResult(newTree, "Moved label into block: " + tree.getLabel());
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
                body = block(If(t.getCondition(),make.Break(null)),t.getElseStatement());
            } else
                if((ex & (FLOWEXIT | CONTINUEEXIT)) == 0) {
                    postBody = t.getElseStatement();
                    body = block(If(not(t.getCondition()),make.Break(null)),t.getThenStatement());
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
            int len = blockLength(tree);
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
                        body = block(sublist(tree,0,splitPos),statement(body));
                        postBody = block(statement(postBody),sublist(tree,splitPos+1,len-splitPos-1));
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
                        int plen = blockLength(postBody);
                        Tree plast = getStatement(plen-1,statement(postBody));
                        if(plast instanceof BreakTree && ((BreakTree)plast).getLabel()==null)
                            postBody = sublist(statement(postBody),0,plen-1);
                        body = block(sublist(tree,0,splitPos),
                                block(If(ifst.getCondition(),make.Break(null)),
                                sublist(tree,splitPos+1,len-splitPos-1)));
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
