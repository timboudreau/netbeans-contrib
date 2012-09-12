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
public class DefaultScanner extends NodeVisitor {

    public void scan(Node node) {
        
    }
    
    @Override
    public Node enter(AccessNode accessNode) {
        scan(accessNode);
        return super.enter(accessNode);
    }

    @Override
    public Node enter(BinaryNode binaryNode) {
        scan(binaryNode);
        return super.enter(binaryNode);
    }

    @Override
    public Node enter(Block block) {
        scan(block);
        return super.enter(block);
    }

    @Override
    public Node enter(BreakNode breakNode) {
        scan(breakNode);
        return super.enter(breakNode);
    }

    @Override
    public Node enter(CallNode callNode) {
        scan(callNode);
        return super.enter(callNode);
    }

    @Override
    public Node enter(CaseNode caseNode) {
        scan(caseNode);
        return super.enter(caseNode);
    }

    @Override
    public Node enter(CatchNode catchNode) {
        scan(catchNode);
        return super.enter(catchNode);
    }

    @Override
    public Node enter(ContinueNode continueNode) {
        scan(continueNode);
        return super.enter(continueNode);
    }

    @Override
    public Node enter(ExecuteNode executeNode) {
        scan(executeNode);
        return super.enter(executeNode);
    }

    @Override
    public Node enter(ForNode forNode) {
        scan(forNode);
        return super.enter(forNode);
    }

    @Override
    public Node enter(FunctionNode functionNode) {
        scan(functionNode);
        return super.enter(functionNode);
    }

    @Override
    public Node enter(IdentNode identNode) {
        scan(identNode);
        return super.enter(identNode);
    }

    @Override
    public Node enter(IfNode ifNode) {
        scan(ifNode);
        return super.enter(ifNode);
    }

    @Override
    public Node enter(IndexNode indexNode) {
        scan(indexNode);
        return super.enter(indexNode);
    }

    @Override
    public Node enter(LabelNode labeledNode) {
        scan(labeledNode);
        return super.enter(labeledNode);
    }

    @Override
    public Node enter(LineNumberNode lineNumberNode) {
        scan(lineNumberNode);
        return super.enter(lineNumberNode);
    }

    @Override
    public Node enter(LiteralNode literalNode) {
        scan(literalNode);
        return super.enter(literalNode);
    }

    @Override
    public Node enter(ObjectNode objectNode) {
        scan(objectNode);
        return super.enter(objectNode);
    }

    @Override
    public Node enter(PropertyNode propertyNode) {
        scan(propertyNode);
        return super.enter(propertyNode);
    }

    @Override
    public Node enter(ReferenceNode referenceNode) {
        scan(referenceNode);
        return super.enter(referenceNode);
    }

    @Override
    public Node enter(ReturnNode returnNode) {
        scan(returnNode);
        return super.enter(returnNode);
    }

    @Override
    public Node enter(RuntimeNode runtimeNode) {
        scan(runtimeNode);
        return super.enter(runtimeNode);
    }

    @Override
    public Node enter(SwitchNode switchNode) {
        scan(switchNode);
        return super.enter(switchNode);
    }

    @Override
    public Node enter(TernaryNode ternaryNode) {
        scan(ternaryNode);
        return super.enter(ternaryNode);
    }

    @Override
    public Node enter(ThrowNode throwNode) {
        scan(throwNode);
        return super.enter(throwNode);
    }

    @Override
    public Node enter(TryNode tryNode) {
        scan(tryNode);
        return super.enter(tryNode);
    }

    @Override
    public Node enter(UnaryNode unaryNode) {
        scan(unaryNode);
        return super.enter(unaryNode);
    }

    @Override
    public Node enter(VarNode varNode) {
        scan(varNode);
        return super.enter(varNode);
    }

    @Override
    public Node enter(WhileNode whileNode) {
        scan(whileNode);
        return super.enter(whileNode);
    }

    @Override
    public Node enter(WithNode withNode) {
        scan(withNode);
        return super.enter(withNode);
    }
   
}
