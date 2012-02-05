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
import com.oracle.nashorn.ir.ObjectNode;
import com.oracle.nashorn.ir.PropertyNode;
import com.oracle.nashorn.ir.ReferenceNode;
import com.oracle.nashorn.ir.ReturnNode;
import com.oracle.nashorn.ir.RuntimeNode;
import com.oracle.nashorn.ir.SwitchNode;
import com.oracle.nashorn.ir.TernaryNode;
import com.oracle.nashorn.ir.ThrowNode;
import com.oracle.nashorn.ir.TryNode;
import com.oracle.nashorn.ir.UnaryNode;
import com.oracle.nashorn.ir.VarNode;
import com.oracle.nashorn.ir.WhileNode;
import com.oracle.nashorn.ir.WithNode;
import com.oracle.nashorn.parser.Token;

/**
 *
 * @author petr
 */
public class OffsetUtils {
    
    public static int[] getOffsets (Node node) {
       long start = System.currentTimeMillis();
        OffsetFinder visitor = new OffsetFinder();
        node.accept(visitor);
        long end = System.currentTimeMillis();
        if ((end - start) > 0)
            System.out.println("findOffsets for : " + node.getClass().getSimpleName() + " : " + (end - start));
        return new int[]{visitor.minOffset, visitor.maxOffset};
    }
    
    private static class OffsetFinder extends com.oracle.nashorn.ir.NodeVisitor {
        protected int minOffset = Integer.MAX_VALUE;
        protected int maxOffset = -1;
        
        private Node findOffsets(Node node, boolean onset) {
            if(onset) {
                int min = node.position();
                int max = min + node.length();
                if (minOffset > min) {
                    minOffset = min;
                }
                if (maxOffset < max) {
                    maxOffset = max;
                }
            } 
            return node;
            
        }
        
        @Override
        public Node visit(AccessNode accessNode, boolean onset) {
            return findOffsets(accessNode, onset);
        }

        @Override
        public Node visit(BinaryNode binaryNode, boolean onset) {
            return findOffsets(binaryNode, onset);
        }

        @Override
        public Node visit(Block block, boolean onset) {
            return findOffsets(block, onset);
        }

        @Override
        public Node visit(BreakNode breakNode, boolean onset) {
            return findOffsets(breakNode, onset);
        }

        @Override
        public Node visit(CallNode callNode, boolean onset) {
            return findOffsets(callNode, onset);
        }

        @Override
        public Node visit(CaseNode caseNode, boolean onset) {
            return findOffsets(caseNode, onset);
        }

        @Override
        public Node visit(CatchNode catchNode, boolean onset) {
            return findOffsets(catchNode, onset);
        }

        @Override
        public Node visit(ContinueNode continueNode, boolean onset) {
            return findOffsets(continueNode, onset);
        }

        @Override
        public Node visit(ExecuteNode executeNode, boolean onset) {
            return findOffsets(executeNode, onset);
        }

        @Override
        public Node visit(ForNode forNode, boolean onset) {
            return findOffsets(forNode, onset);
        }

        @Override
        public Node visit(FunctionNode functionNode, boolean onset) {
            if (onset) {
                int min = Token.descPosition(functionNode.getFirstToken());
                int max = Token.descPosition(functionNode.getLastToken()) + Token.descLength(functionNode.getLastToken());
                if (minOffset > min) {
                    minOffset = min;
                }
                if (maxOffset < max) {
                    maxOffset = max;
                } 
            }
            return findOffsets(functionNode, onset);
        }

        @Override
        public Node visit(IdentNode identNode, boolean onset) {
            return findOffsets(identNode, onset);
        }

        @Override
        public Node visit(IfNode ifNode, boolean onset) {
            return findOffsets(ifNode, onset);
        }

        @Override
        public Node visit(IndexNode indexNode, boolean onset) {
            return findOffsets(indexNode, onset);
        }

        @Override
        public Node visit(LabelNode labeledNode, boolean onset) {
            return findOffsets(labeledNode, onset);
        }

        @Override
        public Node visit(LineNumberNode lineNumberNode, boolean onset) {
            return findOffsets(lineNumberNode, onset);
        }

        @Override
        public Node visit(LiteralNode literalNode, boolean onset) {
            return findOffsets(literalNode, onset);
        }

        @Override
        public Node visit(ObjectNode objectNode, boolean onset) {
            return findOffsets(objectNode, onset);
        }

        @Override
        public Node visit(PropertyNode propertyNode, boolean onset) {
            return findOffsets(propertyNode, onset);
        }

        @Override
        public Node visit(ReferenceNode referenceNode, boolean onset) {
            return findOffsets(referenceNode, onset);
        }

        @Override
        public Node visit(ReturnNode returnNode, boolean onset) {
            return findOffsets(returnNode, onset);
        }

        @Override
        public Node visit(RuntimeNode runtimeNode, boolean onset) {
            return findOffsets(runtimeNode, onset);
        }

        @Override
        public Node visit(SwitchNode switchNode, boolean onset) {
            return findOffsets(switchNode, onset);
        }

        @Override
        public Node visit(TernaryNode ternaryNode, boolean onset) {
            return findOffsets(ternaryNode, onset);
        }

        @Override
        public Node visit(ThrowNode throwNode, boolean onset) {
            return findOffsets(throwNode, onset);
        }

        @Override
        public Node visit(TryNode tryNode, boolean onset) {
            return findOffsets(tryNode, onset);
        }

        @Override
        public Node visit(UnaryNode unaryNode, boolean onset) {
            return findOffsets(unaryNode, onset);
        }

        @Override
        public Node visit(VarNode varNode, boolean onset) {
            return findOffsets(varNode, onset);
        }

        @Override
        public Node visit(WhileNode whileNode, boolean onset) {
            return findOffsets(whileNode, onset);
        }

        @Override
        public Node visit(WithNode withNode, boolean onset) {
            return findOffsets(withNode, onset);
        }
        
        
        
    }
}