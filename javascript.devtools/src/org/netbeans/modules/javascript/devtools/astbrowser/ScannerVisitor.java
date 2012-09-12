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
import com.oracle.nashorn.ir.visitor.NodeVisitor;

/**
 *
 * @author petr
 */
public abstract class ScannerVisitor extends  NodeVisitor {
    

    public abstract Node scanOnSet(Node node, boolean onset);
    
    @Override
    public Node enter(AccessNode accessNode) {
        return scanOnSet(accessNode, true);
    }

    @Override
    public Node enter(BinaryNode iNode) {
        return scanOnSet(iNode, true);
    }

    @Override
    public Node enter(Block block) {
        return scanOnSet(block, true);
    }

    @Override
    public Node enter(BreakNode breakNode) {
        return scanOnSet(breakNode, true);
    }

    @Override
    public Node enter(CallNode callNode) {
        return scanOnSet(callNode, true);
    }

    @Override
    public Node enter(CaseNode caseNode) {
        return scanOnSet(caseNode, true);
    }

    @Override
    public Node enter(CatchNode catchNode) {
        return scanOnSet(catchNode, true);
    }

    @Override
    public Node enter(ContinueNode continueNode) {
        return scanOnSet(continueNode, true);
    }

    @Override
    public Node enter(ExecuteNode executeNode) {
        return scanOnSet(executeNode, true);
    }

//    @Override
//    public Node enter(ErrorNode iNode) {
//        return scanOnSet(accessNode, true);
//    }
    
    @Override
    public Node enter(ForNode forNode) {
        return scanOnSet(forNode, true);
    }

    @Override
    public Node enter(FunctionNode functionNode) {
        return scanOnSet(functionNode, true);
    }

    @Override
    public Node enter(IdentNode identNode) {
        return scanOnSet(identNode, true);
    }

    @Override
    public Node enter(IfNode ifNode) {
        return scanOnSet(ifNode, true);
    }

    @Override
    public Node enter(IndexNode indexNode) {
        return scanOnSet(indexNode, true);
    }

    @Override
    public Node enter(LabelNode labeledNode) {
        return scanOnSet(labeledNode, true);
    }

    @Override
    public Node enter(LineNumberNode node) {
        return scanOnSet(node, true);
    }

    @Override
    public Node enter(LiteralNode literalNode) {
        return scanOnSet(literalNode, true);
    }

    @Override
    public Node enter(ObjectNode objectNode) {
        return scanOnSet(objectNode, true);
    }

    @Override
    public Node enter(PropertyNode propertyNode) {
        return scanOnSet(propertyNode, true);
    }

    @Override
    public Node enter(ReferenceNode node) {
        return scanOnSet(node, true);
    }

    @Override
    public Node enter(ReturnNode returnNode) {
        return scanOnSet(returnNode, true);
    }

    @Override
    public Node enter(RuntimeNode runtimeNode) {
        return scanOnSet(runtimeNode, true);
    }

    @Override
    public Node enter(SwitchNode switchNode) {
        return scanOnSet(switchNode, true);
    }

    @Override
    public Node enter(TernaryNode ternaryNode) {
        return scanOnSet(ternaryNode, true);
    }

    @Override
    public Node enter(ThrowNode throwNode) {
        return scanOnSet(throwNode, true);
    }

    @Override
    public Node enter(TryNode tryNode) {
        return scanOnSet(tryNode, true);
    }

    @Override
    public Node enter(UnaryNode unaryNode) {
        return scanOnSet(unaryNode, true);
    }

    @Override
    public Node enter(VarNode varNode) {
        return scanOnSet(varNode, true);
    }

    @Override
    public Node enter(WhileNode whileNode) {
        return scanOnSet(whileNode, true);
    }

    @Override
    public Node enter(WithNode withNode) {
        return scanOnSet(withNode, true);
    }

    @Override
    public Node leave(AccessNode accessNode) {
        return scanOnSet(accessNode, false);
    }

    @Override
    public Node leave(BinaryNode iNode) {
        return scanOnSet(iNode, false);
    }

    @Override
    public Node leave(Block block) {
        return scanOnSet(block, false);
    }

    @Override
    public Node leave(BreakNode breakNode) {
        return scanOnSet(breakNode, false);
    }

    @Override
    public Node leave(CallNode callNode) {
        return scanOnSet(callNode, false);
    }

    @Override
    public Node leave(CaseNode caseNode) {
        return scanOnSet(caseNode, false);
    }

    @Override
    public Node leave(CatchNode catchNode) {
        return scanOnSet(catchNode, false);
    }

    @Override
    public Node leave(ContinueNode continueNode) {
        return scanOnSet(continueNode, false);
    }

    @Override
    public Node leave(ExecuteNode executeNode) {
        return scanOnSet(executeNode, false);
    }

//    @Override
//    public Node leave(ErrorNode iNode) {
//        return scanOnSet(accessNode, false);
//    }

    @Override
    public Node leave(ForNode forNode) {
        return scanOnSet(forNode, false);
    }

    @Override
    public Node leave(FunctionNode functionNode) {
        return scanOnSet(functionNode, false);
    }

    @Override
    public Node leave(IdentNode identNode) {
        return scanOnSet(identNode, false);
    }

    @Override
    public Node leave(IfNode ifNode) {
        return scanOnSet(ifNode, false);
    }

    @Override
    public Node leave(IndexNode indexNode) {
        return scanOnSet(indexNode, false);
    }

    @Override
    public Node leave(LabelNode labeledNode) {
        return scanOnSet(labeledNode, false);
    }

    @Override
    public Node leave(LineNumberNode node) {
        return scanOnSet(node, false);
    }

    @Override
    public Node leave(LiteralNode literalNode) {
        return scanOnSet(literalNode, false);
    }

    @Override
    public Node leave(ObjectNode objectNode) {
        return scanOnSet(objectNode, false);
    }

    @Override
    public Node leave(PropertyNode propertyNode) {
        return scanOnSet(propertyNode, false);
    }

    @Override
    public Node leave(ReferenceNode node) {
        return scanOnSet(node, false);
    }

    @Override
    public Node leave(ReturnNode returnNode) {
        return scanOnSet(returnNode, false);
    }

    @Override
    public Node leave(RuntimeNode runtimeNode) {
        return scanOnSet(runtimeNode, false);
    }

    @Override
    public Node leave(SwitchNode switchNode) {
        return scanOnSet(switchNode, false);
    }

    @Override
    public Node leave(TernaryNode ternaryNode) {
        return scanOnSet(ternaryNode, false);
    }

    @Override
    public Node leave(ThrowNode throwNode) {
        return scanOnSet(throwNode, false);
    }

    @Override
    public Node leave(TryNode tryNode) {
        return scanOnSet(tryNode, false);
    }

    @Override
    public Node leave(UnaryNode unaryNode) {
        return scanOnSet(unaryNode, false);
    }

    @Override
    public Node leave(VarNode varNode) {
        return scanOnSet(varNode, false);
    }

    @Override
    public Node leave(WhileNode whileNode) {
        return scanOnSet(whileNode, false);
    }

    @Override
    public Node leave(WithNode withNode) {
        return scanOnSet(withNode, false);
    }
}
