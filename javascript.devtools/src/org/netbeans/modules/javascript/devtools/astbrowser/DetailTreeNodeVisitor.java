/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript.devtools.astbrowser;

import jdk.nashorn.internal.ir.AccessNode;
import jdk.nashorn.internal.ir.BinaryNode;
import jdk.nashorn.internal.ir.Block;
import jdk.nashorn.internal.ir.BreakNode;
import jdk.nashorn.internal.ir.CallNode;
import jdk.nashorn.internal.ir.CaseNode;
import jdk.nashorn.internal.ir.CatchNode;
import jdk.nashorn.internal.ir.ContinueNode;
import jdk.nashorn.internal.ir.ExecuteNode;
import jdk.nashorn.internal.ir.ForNode;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.ir.IfNode;
import jdk.nashorn.internal.ir.IndexNode;
import jdk.nashorn.internal.ir.LabelNode;
import jdk.nashorn.internal.ir.LineNumberNode;
import jdk.nashorn.internal.ir.LiteralNode;
import jdk.nashorn.internal.ir.Node;
import jdk.nashorn.internal.ir.ObjectNode;
import jdk.nashorn.internal.ir.PropertyNode;
import jdk.nashorn.internal.ir.ReferenceNode;
import jdk.nashorn.internal.ir.ReturnNode;
import jdk.nashorn.internal.ir.RuntimeNode;
import jdk.nashorn.internal.ir.SwitchNode;
import jdk.nashorn.internal.ir.Symbol;
import jdk.nashorn.internal.ir.TernaryNode;
import jdk.nashorn.internal.ir.ThrowNode;
import jdk.nashorn.internal.ir.TryNode;
import jdk.nashorn.internal.ir.UnaryNode;
import jdk.nashorn.internal.ir.VarNode;
import jdk.nashorn.internal.ir.WhileNode;
import jdk.nashorn.internal.ir.WithNode;
import jdk.nashorn.internal.ir.visitor.NodeVisitor;
import jdk.nashorn.internal.parser.Token;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.javascript.devtools.astbrowser.TreeCreator.TreeASTNodeAdapter;

/**
 *
 * @author petr
 */
public class DetailTreeNodeVisitor extends NodeVisitor {
    
    private TreeCreator.TreeASTNodeAdapter parentNode;
    
    private List<TreeCreator.TreeASTNodeAdapter> parents = new ArrayList<TreeCreator.TreeASTNodeAdapter>();

    public DetailTreeNodeVisitor(TreeASTNodeAdapter parentNode) {
        this.parentNode = parentNode;
    }
    
    
    private Node notComplete(jdk.nashorn.internal.ir.Node iNode, boolean onset) {
    
        if (onset) {
//            int[] offsets = OffsetUtils.getOffsets(iNode);
            int[] offsets = new int[]{iNode.getStart(), iNode.getFinish()};
            TreeCreator.TreeASTNodeAdapter adapter = new TreeCreator.TreeASTNodeAdapter(parentNode,
                "<font color='red'>" +iNode.getClass().getSimpleName() + "</font>",
                offsets[0], offsets[1]);
            parentNode.addChild(adapter);
            parents.add(parentNode);
            parentNode = adapter;
            
        } else {
            parentNode = parents.remove(parents.size() - 1);
            
        }
        return iNode;
    }
    
    private void processInfoNode(String name, int start, int end, jdk.nashorn.internal.ir.Node iNode) {
        TreeCreator.TreeASTNodeAdapter adapter = new TreeCreator.TreeASTNodeAdapter (parentNode,
                    "<font color='gray'>" + name + ":</font>", start, end);
            
        parentNode.addChild(adapter);
        parents.add(parentNode);
        parentNode = adapter;
        iNode.accept(this);
        parentNode = parents.remove(parents.size() - 1);
    }
    
    private Node visitSimpleNode (jdk.nashorn.internal.ir.Node node, String additionalDescription, String tooltip, boolean onset) {
        if (onset) {
            TreeCreator.TreeASTNodeAdapter adapter = new TreeCreator.TreeASTNodeAdapter(parentNode,
                "<html>" + node.getClass().getSimpleName() + (additionalDescription != null ? additionalDescription : "") + "</html>",
                tooltip == null ? null : "<html>" + tooltip + "</html>",
                node.position(), node.position() + node.length());
            parentNode.addChild(adapter);
            parents.add(parentNode);
            parentNode = adapter;
//            return node;
        } else {
            parentNode = parents.remove(parents.size() - 1);
//            return null;
        }
        return node;
    }
    
    
    @Override
    public Node enter(AccessNode accessNode) {
        return notComplete(accessNode, true);
    }

    @Override
    public Node leave(AccessNode accessNode) {
        return notComplete(accessNode, false);
    }

    @Override
    public Node enter(BinaryNode iNode) {
//            int[] offsets = OffsetUtils.getOffsets(iNode);
        int[] offsets = new int[]{iNode.getStart(), iNode.getFinish()};
        TreeCreator.TreeASTNodeAdapter adapter = new TreeCreator.TreeASTNodeAdapter(parentNode,
            iNode.getClass().getSimpleName() + " " + iNode.tokenType().getName(),
            offsets[0], offsets[1]);
        parentNode.addChild(adapter);
        parents.add(parentNode);
        parentNode = adapter;
        return iNode;
    }

    @Override
    public Node leave(BinaryNode iNode) {
        parentNode = parents.remove(parents.size() - 1);
        return iNode;
    }

    @Override
    public Node enter(Block iNode) {
        int[] offsets = new int[]{iNode.getStart(), iNode.getFinish()};
        TreeCreator.TreeASTNodeAdapter adapter = new TreeCreator.TreeASTNodeAdapter(parentNode,
            "<font>" +iNode.getClass().getSimpleName() + "</font>",
            offsets[0], offsets[1]);

        parentNode.addChild(adapter);
        parents.add(parentNode);
        parentNode = adapter;

        TreeCreator.TreeASTNodeAdapter frame = new TreeCreator.TreeASTNodeAdapter (parentNode,
                "<font color='gray'>Frame:</font>", iNode.getStart(), iNode.getFinish());
        parentNode.addChild(frame);
        parents.add(parentNode);
        parentNode = frame;

        TreeCreator.TreeASTNodeAdapter variables = new TreeCreator.TreeASTNodeAdapter (parentNode,
                "<font color='gray'>Variables:</font>", iNode.getStart(), iNode.getFinish());
        parentNode.addChild(variables);
        parents.add(parentNode);
        parentNode = frame;

        if (iNode.getFrame() != null) {
            TreeCreator.TreeASTNodeAdapter symbolAdapter;
            for (Symbol symbol: iNode.getFrame().getSymbols()) {
                variables.addChild(new TreeCreator.TreeASTNodeAdapter(variables, 
                        "<font color='gray'>" + symbol.getName() + "</font>"));
            }
        }

        parentNode = parents.remove(parents.size() - 1);

        parentNode = parents.remove(parents.size() - 1);

        parentNode = parents.remove(parents.size() - 1);
        return null;
    }

    @Override
    public Node enter(BreakNode breakNode) {
        return notComplete(breakNode, true);
    }

    @Override
    public Node leave(BreakNode breakNode) {
        return notComplete(breakNode, false);
    }

    @Override
    public Node enter(CallNode callNode) {
        return notComplete(callNode, true);
    }

    @Override
    public Node leave(CallNode callNode) {
        return notComplete(callNode, false);
    }

    @Override
    public Node enter(CaseNode caseNode) {
        return notComplete(caseNode, true);
    }

    @Override
    public Node leave(CaseNode caseNode) {
        return notComplete(caseNode, false);
    }

    @Override
    public Node enter(CatchNode catchNode) {
        return notComplete(catchNode, true);
    }

    @Override
    public Node leave(CatchNode catchNode) {
        return notComplete(catchNode, false);
    }

    @Override
    public Node enter(ContinueNode continueNode) {
        return notComplete(continueNode, true);
    }

    @Override
    public Node leave(ContinueNode continueNode) {
        return notComplete(continueNode, false);
    }

    @Override
    public Node enter(ExecuteNode executeNode) {
        return notComplete(executeNode, true);
    }

    @Override
    public Node leave(ExecuteNode executeNode) {
        return notComplete(executeNode, false);
    }

//    @Override
//    public Node enter(ErrorNode iNode) {
//        if (onset) {
//            int[] offsets = new int[]{iNode.getStart(), iNode.getFinish()};
//            TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
//                "<font color='red'>" +iNode.getClass().getSimpleName() + "</font>",
//                iNode.getMessage() != null ? "<html>" + iNode.getMessage() + "</html>" : null,
//                offsets[0], offsets[1]);
//            parentNode.addChild(adapter);
//            parents.add(parentNode);
//            parentNode = adapter;
//            
//        } else {
//            parentNode = parents.remove(parents.size() - 1);
//            
//        }
//        return iNode;
//    }
    
    

    @Override
    public Node enter(ForNode forNode) {
        return notComplete(forNode, true);
    }

    @Override
    public Node leave(ForNode forNode) {
        return notComplete(forNode, false);
    }

    @Override
    public Node enter(FunctionNode functionNode) {
        TreeCreator.TreeASTNodeAdapter adapter = new TreeCreator.TreeASTNodeAdapter(parentNode,
            functionNode.getClass().getSimpleName() + " <b>" + functionNode.getName() + "</b>",
            "<html>Name: " + functionNode.getName() +"<br/>"
                + "Kind: " + functionNode.getKind().toString() +"<br/>"
                + "</html>",
               Token.descPosition(functionNode.getFirstToken()), Token.descPosition(functionNode.getLastToken()) + Token.descLength(functionNode.getLastToken()));
            //(int)functionNode.position(), (int)(functionNode.position() + functionNode.length()));
        parentNode.addChild(adapter);
        parents.add(parentNode);
        parentNode = adapter;
//            parentNode.addChild(new TreeASTNodeAdapter(parentNode,
//                    "Name: " + functionNode.getName(),
//                    0,0));
            
        return functionNode;
    }

    @Override
    public Node leave(FunctionNode functionNode) {
        parentNode = parents.remove(parents.size() - 1);
        return functionNode;
    }

    @Override
    public Node enter(IdentNode identNode) {
        int[] offsets = OffsetUtils.getOffsets(identNode);
        TreeCreator.TreeASTNodeAdapter adapter = new TreeCreator.TreeASTNodeAdapter(parentNode,
            identNode.getClass().getSimpleName() + " <b>" + identNode.getName() + "</b>",
            offsets[0], offsets[1]);
        parentNode.addChild(adapter);
        parents.add(parentNode);
        parentNode = adapter;
//            return super.visit(identNode, true);
//            return null;
        return identNode;
    }

    @Override
    public Node leave(IdentNode identNode) {
        parentNode = parents.remove(parents.size() - 1);
        return identNode;
    }

    @Override
    public Node enter(IfNode iNode) {
        int[] offsets = new int[]{iNode.getStart(), iNode.getFinish()};
        TreeCreator.TreeASTNodeAdapter adapter = new TreeCreator.TreeASTNodeAdapter(parentNode,
            "<font>" +iNode.getClass().getSimpleName() + "</font>",
            offsets[0], offsets[1]);

        parentNode.addChild(adapter);
        parents.add(parentNode);
        parentNode = adapter;

        processInfoNode("Condition", iNode.getTest().getStart(), iNode.getTest().getFinish(), iNode.getTest());
        processInfoNode("Pass", iNode.getPass().getStart(), iNode.getPass().getFinish(), iNode.getPass());            
        if (iNode.getFail() != null) {
            processInfoNode("Fail", iNode.getFail().getStart(), iNode.getFail().getFinish(), iNode.getFail());            
        }

        parentNode = parents.remove(parents.size() - 1);
        return null;
    }

    @Override
    public Node enter(IndexNode indexNode) {
        return notComplete(indexNode, true);
    }

    @Override
    public Node leave(IndexNode indexNode) {
        return notComplete(indexNode, false);
    }

    @Override
    public Node enter(LabelNode labeledNode) {
        return notComplete(labeledNode, true);
    }

    @Override
    public Node leave(LabelNode labeledNode) {
        return notComplete(labeledNode, false);
    }

    @Override
    public Node enter(LineNumberNode node) {
        return visitSimpleNode(node, "", "Line number: " + node.getLineNumber(), true);
    }

    @Override
    public Node leave(LineNumberNode node) {
        return visitSimpleNode(node, "", "Line number: " + node.getLineNumber(), false);
    }

    @Override
    public Node enter(LiteralNode literalNode) {
        return notComplete(literalNode, true);
    }

    @Override
    public Node leave(LiteralNode literalNode) {
        return notComplete(literalNode, false);
    }

    @Override
    public Node enter(ObjectNode objectNode) {
        return notComplete(objectNode, true);
    }

    @Override
    public Node leave(ObjectNode objectNode) {
        return notComplete(objectNode, false);
    }

    @Override
    public Node enter(PropertyNode propertyNode) {
        return notComplete(propertyNode, true);
    }

    @Override
    public Node leave(PropertyNode propertyNode) {
        return notComplete(propertyNode, false);
    }

    @Override
    public Node enter(ReferenceNode node) {
        int[] offsets = OffsetUtils.getOffsets(node);
        TreeCreator.TreeASTNodeAdapter adapter = new TreeCreator.TreeASTNodeAdapter(parentNode,
            node.getClass().getSimpleName(),
            offsets[0], offsets[1]);
        parentNode.addChild(adapter);
        parents.add(parentNode);
        parentNode = adapter;
        Object ref = node.getReference();
        if (ref != null) {
            if (ref instanceof FunctionNode) {
                //((FunctionNode)ref).accept(this);
            }
            else 
                adapter.addChild(new TreeCreator.TreeASTNodeAdapter(parentNode,
                    "<font color='gray'>" + ref.getClass().getSimpleName() + "</font>"));
        }
        return node;
    }

    @Override
    public Node leave(ReferenceNode node) {
        parentNode = parents.remove(parents.size() - 1);
        return node;
    }

    @Override
    public Node enter(ReturnNode returnNode) {
        return notComplete(returnNode, true);
    }

    @Override
    public Node leave(ReturnNode returnNode) {
        return notComplete(returnNode, false);
    }

    @Override
    public Node enter(RuntimeNode runtimeNode) {
        return notComplete(runtimeNode, true);
    }

    @Override
    public Node leave(RuntimeNode runtimeNode) {
        return notComplete(runtimeNode, false);
    }

    @Override
    public Node enter(SwitchNode switchNode) {
        return notComplete(switchNode, true);
    }

    @Override
    public Node leave(SwitchNode switchNode) {
        return notComplete(switchNode, false);
    }

    @Override
    public Node enter(TernaryNode ternaryNode) {
        return notComplete(ternaryNode, true);
    }

    @Override
    public Node leave(TernaryNode ternaryNode) {
        return notComplete(ternaryNode, false);
    }

    @Override
    public Node enter(ThrowNode throwNode) {
        return notComplete(throwNode, true);
    }

    @Override
    public Node leave(ThrowNode throwNode) {
        return notComplete(throwNode, false);
    }

    @Override
    public Node enter(TryNode tryNode) {
        return notComplete(tryNode, true);
    }

    @Override
    public Node leave(TryNode tryNode) {
        return notComplete(tryNode, false);
    }

    @Override
    public Node enter(UnaryNode unaryNode) {
        return notComplete(unaryNode, true);
    }

    @Override
    public Node leave(UnaryNode unaryNode) {
        return notComplete(unaryNode, false);
    }

    @Override
    public Node enter(VarNode varNode) {
        return notComplete(varNode, true);
    }

    @Override
    public Node leave(VarNode varNode) {
        return notComplete(varNode, false);
    }

    @Override
    public Node enter(WhileNode whileNode) {
        return notComplete(whileNode, true);
    }

    @Override
    public Node leave(WhileNode whileNode) {
        return notComplete(whileNode, false);
    }

    @Override
    public Node enter(WithNode withNode) {
        return notComplete(withNode, true);
    }

    @Override
    public Node leave(WithNode withNode) {
        return notComplete(withNode, false);
    }
    
}
