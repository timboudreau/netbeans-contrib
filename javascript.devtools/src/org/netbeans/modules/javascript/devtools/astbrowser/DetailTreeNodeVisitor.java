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

import com.oracle.nashorn.ir.AccessNode;
import com.oracle.nashorn.ir.BinaryNode;
import com.oracle.nashorn.ir.Block;
import com.oracle.nashorn.ir.BreakNode;
import com.oracle.nashorn.ir.CallNode;
import com.oracle.nashorn.ir.CaseNode;
import com.oracle.nashorn.ir.CatchNode;
import com.oracle.nashorn.ir.ContinueNode;
import com.oracle.nashorn.ir.ExecuteNode;
import com.oracle.nashorn.ir.ForNode;
import com.oracle.nashorn.ir.FunctionNode;
import com.oracle.nashorn.ir.IdentNode;
import com.oracle.nashorn.ir.IfNode;
import com.oracle.nashorn.ir.IndexNode;
import com.oracle.nashorn.ir.LabelNode;
import com.oracle.nashorn.ir.LineNumberNode;
import com.oracle.nashorn.ir.LiteralNode;
import com.oracle.nashorn.ir.Node;
import com.oracle.nashorn.ir.NodeVisitor;
import com.oracle.nashorn.ir.ObjectNode;
import com.oracle.nashorn.ir.PropertyNode;
import com.oracle.nashorn.ir.ReferenceNode;
import com.oracle.nashorn.ir.ReturnNode;
import com.oracle.nashorn.ir.RuntimeNode;
import com.oracle.nashorn.ir.SwitchNode;
import com.oracle.nashorn.ir.Symbol;
import com.oracle.nashorn.ir.TernaryNode;
import com.oracle.nashorn.ir.ThrowNode;
import com.oracle.nashorn.ir.TryNode;
import com.oracle.nashorn.ir.UnaryNode;
import com.oracle.nashorn.ir.VarNode;
import com.oracle.nashorn.ir.WhileNode;
import com.oracle.nashorn.ir.WithNode;
import com.oracle.nashorn.parser.Token;
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
    
    
    private Node notComplete(com.oracle.nashorn.ir.Node iNode, boolean onset) {
    
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
    
    private void processInfoNode(String name, int start, int end, com.oracle.nashorn.ir.Node iNode) {
        TreeCreator.TreeASTNodeAdapter adapter = new TreeCreator.TreeASTNodeAdapter (parentNode,
                    "<font color='gray'>" + name + ":</font>", start, end);
            
        parentNode.addChild(adapter);
        parents.add(parentNode);
        parentNode = adapter;
        iNode.accept(this);
        parentNode = parents.remove(parents.size() - 1);
    }
    
    private Node visitSimpleNode (com.oracle.nashorn.ir.Node node, String additionalDescription, String tooltip, boolean onset) {
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
    public Node visit(AccessNode accessNode, boolean onset) {
        return notComplete(accessNode, onset);
    }

    @Override
    public Node visit(BinaryNode iNode, boolean onset) {
        if (onset) {
//            int[] offsets = OffsetUtils.getOffsets(iNode);
            int[] offsets = new int[]{iNode.getStart(), iNode.getFinish()};
            TreeCreator.TreeASTNodeAdapter adapter = new TreeCreator.TreeASTNodeAdapter(parentNode,
                iNode.getClass().getSimpleName() + " " + iNode.tokenType().getName(),
                offsets[0], offsets[1]);
            parentNode.addChild(adapter);
            parents.add(parentNode);
            parentNode = adapter;
            
        } else {
            parentNode = parents.remove(parents.size() - 1);
            
        }
        return iNode;
    }

    @Override
    public Node visit(Block iNode, boolean onset) {
        if (onset) {
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
        } else {
            return iNode;
        }
    }

    @Override
    public Node visit(BreakNode breakNode, boolean onset) {
        return notComplete(breakNode, onset);
    }

    @Override
    public Node visit(CallNode callNode, boolean onset) {
        return notComplete(callNode, onset);
    }

    @Override
    public Node visit(CaseNode caseNode, boolean onset) {
        return notComplete(caseNode, onset);
    }

    @Override
    public Node visit(CatchNode catchNode, boolean onset) {
        return notComplete(catchNode, onset);
    }

    @Override
    public Node visit(ContinueNode continueNode, boolean onset) {
        return notComplete(continueNode, onset);
    }

    @Override
    public Node visit(ExecuteNode executeNode, boolean onset) {
        return notComplete(executeNode, onset);
    }

//    @Override
//    public Node visit(ErrorNode iNode, boolean onset) {
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
    public Node visit(ForNode forNode, boolean onset) {
        return notComplete(forNode, onset);
    }

    @Override
    public Node visit(FunctionNode functionNode, boolean onset) {
        if (onset) {
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
        } else {
            parentNode = parents.remove(parents.size() - 1);
        }
        return functionNode;
    }

    @Override
    public Node visit(IdentNode identNode, boolean onset) {
        if (onset) {
            int[] offsets = OffsetUtils.getOffsets(identNode);
            TreeCreator.TreeASTNodeAdapter adapter = new TreeCreator.TreeASTNodeAdapter(parentNode,
                identNode.getClass().getSimpleName() + " <b>" + identNode.getName() + "</b>",
                offsets[0], offsets[1]);
            parentNode.addChild(adapter);
            parents.add(parentNode);
            parentNode = adapter;
//            return super.visit(identNode, onset);
        } else {
            parentNode = parents.remove(parents.size() - 1);
//            return null;
        }
        return identNode;
    }

    @Override
    public Node visit(IfNode iNode, boolean onset) {
        if (onset) {
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
        } else {
            return iNode;
        }
        
    }

    @Override
    public Node visit(IndexNode indexNode, boolean onset) {
        return notComplete(indexNode, onset);
    }

    @Override
    public Node visit(LabelNode labeledNode, boolean onset) {
        return notComplete(labeledNode, onset);
    }

    @Override
    public Node visit(LineNumberNode node, boolean onset) {
        return visitSimpleNode(node, "", "Line number: " + node.getLineNumber(), onset);
    }

    @Override
    public Node visit(LiteralNode literalNode, boolean onset) {
        return notComplete(literalNode, onset);
    }

    @Override
    public Node visit(ObjectNode objectNode, boolean onset) {
        return notComplete(objectNode, onset);
    }

    @Override
    public Node visit(PropertyNode propertyNode, boolean onset) {
        return notComplete(propertyNode, onset);
    }

    @Override
    public Node visit(ReferenceNode node, boolean onset) {
        if (onset) {
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
//            return node;
        } else {
            parentNode = parents.remove(parents.size() - 1);
//            return null;
        }
        return node;
    }

    @Override
    public Node visit(ReturnNode returnNode, boolean onset) {
        return notComplete(returnNode, onset);
    }

    @Override
    public Node visit(RuntimeNode runtimeNode, boolean onset) {
        return notComplete(runtimeNode, onset);
    }

    @Override
    public Node visit(SwitchNode switchNode, boolean onset) {
        return notComplete(switchNode, onset);
    }

    @Override
    public Node visit(TernaryNode ternaryNode, boolean onset) {
        return notComplete(ternaryNode, onset);
    }

    @Override
    public Node visit(ThrowNode throwNode, boolean onset) {
        return notComplete(throwNode, onset);
    }

    @Override
    public Node visit(TryNode tryNode, boolean onset) {
        return notComplete(tryNode, onset);
    }

    @Override
    public Node visit(UnaryNode unaryNode, boolean onset) {
        return notComplete(unaryNode, onset);
    }

    @Override
    public Node visit(VarNode varNode, boolean onset) {
        return notComplete(varNode, onset);
    }

    @Override
    public Node visit(WhileNode whileNode, boolean onset) {
        return notComplete(whileNode, onset);
    }

    @Override
    public Node visit(WithNode withNode, boolean onset) {
        return notComplete(withNode, onset);
    }
    
}
