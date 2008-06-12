/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.devtools.browser;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;

import org.netbeans.modules.php.editor.parser.astnodes.*;
import org.openide.util.Enumerations;

/**
 *
 * @author Petr Pisl
 */
public class TreeCreator implements Visitor {

    private TreeASTNodeAdapter parentNode;

    class TreeASTNodeAdapter implements TreeNode {

        TreeASTNodeAdapter parent;
        List<TreeASTNodeAdapter> children;
        String description;
        final int start;
        final int end;

        TreeASTNodeAdapter(TreeASTNodeAdapter parent, String description) {
            this(parent, description, -1, -1);
        }

        TreeASTNodeAdapter(TreeASTNodeAdapter parent, String description, int start, int end) {
            this.parent = parent;
            this.description = description;
            children = new ArrayList<TreeASTNodeAdapter>();
            this.start = start;
            this.end = end;
        }

        public void addChild(TreeASTNodeAdapter child) {
            children.add(child);
        }

        public TreeNode getChildAt(int childIndex) {
            return children.get(childIndex);
        }

        public int getChildCount() {
            return children.size();
        }

        public TreeNode getParent() {
            return parent;
        }

        public int getIndex(TreeNode node) {
            for (int i = 0; i < children.size(); i++) {
                if (children.get(i) == node) {
                    return i;
                }
            }

            return -1;
        }

        public boolean getAllowsChildren() {
            return children.size() > 0;
        }

        public boolean isLeaf() {
            return children.size() == 0;
        }

        public Enumeration children() {
            return Enumerations.array(children);
        }

        public String toString() {
            String location = "";

            if (start > -1) {
                location = "[" + start + ", " + end + "] ";
            }
            return location + description;
        }

        public int getStartOffset() {
            return start;
        }

        public int getEndOffset() {
            return end;
        }
    }

    public TreeNode createTree(ASTNode node) {
        parentNode = new TreeASTNodeAdapter(null, "Parser output");
        node.accept(this);
        return parentNode;
    }

    public void visit(ArrayAccess aa) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "ArrayAccess (array type: " + aa.getArrayType().name() + ")",
                aa.getStartOffset(), aa.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        aa.getName().accept(this);
        aa.getIndex().accept(this);
        parentNode = helpParent;
    }

    public void visit(ArrayCreation ac) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "ArrayCreation",
                ac.getStartOffset(), ac.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        for (ArrayElement el : ac.getElements()) {
            el.accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(ArrayElement ae) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "ArrayElement",
                ae.getStartOffset(), ae.getStartOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        if (ae.getKey() != null) {
            TreeASTNodeAdapter key = new TreeASTNodeAdapter(adapter, "Key");
            parentNode = key;
            ae.getKey().accept(this);
            adapter.addChild(key);
        }
        if (ae.getValue() != null) {
            TreeASTNodeAdapter value = new TreeASTNodeAdapter(adapter, "Value");
            parentNode = value;
            ae.getValue().accept(this);
            adapter.addChild(value);
        }
        parentNode = helpParent;
    }

    public void visit(Assignment assignment) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "Assignment (type: " + assignment.getOperator().name() + ")",
                assignment.getStartOffset(), assignment.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        TreeASTNodeAdapter left = new TreeASTNodeAdapter(adapter, "Left Side");
        parentNode = left;
        assignment.getLeftHandSide().accept(this);

        TreeASTNodeAdapter right = new TreeASTNodeAdapter(adapter, "Right Side");
        parentNode = right;
        assignment.getRightHandSide().accept(this);

        adapter.addChild(left);
        adapter.addChild(right);

        parentNode = helpParent;
    }

    public void visit(ASTError astError) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, "ASTError", 
                astError.getStartOffset(), astError.getStartOffset());
        parentNode.addChild(adapter);
    }

    public void visit(BackTickExpression te) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, 
                "BackTickExpression",
                te.getStartOffset(), te.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (te.getExpressions() != null) {
            for (Expression expr : te.getExpressions()) {
                expr.accept(this);
            }

        }
        parentNode = helpParent;
    }

    public void visit(Block block) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "Block (isCurly: " + block.isCurly() + ")",
                block.getStartOffset(), block.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        for (Statement statement : block.getStatements()) {
            statement.accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(BreakStatement bs) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "BreakStatement",
                bs.getStartOffset(), bs.getEndOffset());
        parentNode.addChild(adapter);
        if (bs.getExpression() != null) {
            TreeASTNodeAdapter helpParent = parentNode;
            parentNode = adapter;
            bs.getExpression().accept(this);
            parentNode = helpParent;
        }
    }

    public void visit(CastExpression ce) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "CastExpression (type: " + ce.getCastingType().name() + ")",
                ce.getStartOffset(), ce.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (ce.getExpression() != null) {
            ce.getExpression().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(CatchClause cc) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, 
                "CatchClause",
                cc.getStartOffset(), cc.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        cc.getClassName().accept(this);
        cc.getVariable().accept(this);
        cc.getBody().accept(this);
        parentNode = helpParent;
    }

    public void visit(ClassConstantDeclaration cd) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, 
                "ClassConstantDeclaration",
                cd.getStartOffset(), cd.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        if (cd.getNames() != null) {
            TreeASTNodeAdapter value = new TreeASTNodeAdapter(adapter, "Names");
            parentNode = value;
            for(Identifier iden : cd.getNames()){
                iden.accept(this);
            }
            adapter.addChild(value);
        }
        if (cd.getInitializers() != null) {
            TreeASTNodeAdapter value = new TreeASTNodeAdapter(adapter, "Initializers");
            parentNode = value;
            for(Expression expr : cd.getInitializers()){
                expr.accept(this);
            }
            adapter.addChild(value);
        }
        parentNode = helpParent;
    }

    public void visit(ClassDeclaration classDeclaration) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "ClassDeclaration (Modifier: " + classDeclaration.getModifier().name() + ")",
                classDeclaration.getStartOffset(), classDeclaration.getEndOffset());
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode.addChild(adapter);
        if (classDeclaration.getName() != null) {
            TreeASTNodeAdapter name = new TreeASTNodeAdapter(adapter, "Name");
            parentNode = name;
            classDeclaration.getName().accept(this);
            adapter.addChild(name);
        }

        if (classDeclaration.getSuperClass() != null) {
            TreeASTNodeAdapter superClass = new TreeASTNodeAdapter(adapter, "SuperClass");
            parentNode = superClass;
            classDeclaration.getSuperClass().accept(this);
            adapter.addChild(superClass);
        }

        if (classDeclaration.getInterfaes() != null) {
            TreeASTNodeAdapter interfeas = new TreeASTNodeAdapter(adapter, "Interfaes");
            parentNode = interfeas;
            for (Identifier identifier : classDeclaration.getInterfaes()) {
                identifier.accept(this);
            }
            adapter.addChild(interfeas);
        }

        if (classDeclaration.getBody() != null) {
            parentNode = adapter;
            classDeclaration.getBody().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(ClassInstanceCreation ic) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "ClassInstanceCreation",
                ic.getStartOffset(), ic.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        ic.getClassName().accept(this);

        if (ic.ctorParams() != null) {
            TreeASTNodeAdapter params = new TreeASTNodeAdapter(adapter, "Parameters");
            parentNode = params;
            for (Expression expr : ic.ctorParams()) {
                expr.accept(this);
            }
            adapter.addChild(params);
        }
        parentNode = helpParent;
    }

    public void visit(ClassName cl) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "ClassName",
                cl.getStartOffset(), cl.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        cl.getName().accept(this);
        parentNode = helpParent;
    }

    public void visit(CloneExpression ce) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "CloneExpression",
                ce.getStartOffset(), ce.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (ce.getExpression() != null) {
            ce.getExpression().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(Comment comment) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, 
                "" + comment.getCommentType(), comment.getStartOffset(), comment.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        parentNode = helpParent;
    }

    public void visit(ConditionalExpression ce) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "ConditionalExpression",
                ce.getStartOffset(), ce.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        if (ce.getCondition() != null) {
            TreeASTNodeAdapter con = new TreeASTNodeAdapter(adapter, "Condition");
            parentNode = con;
            ce.getCondition().accept(this);
            adapter.addChild(con);
        }
        if (ce.getIfTrue() != null) {
            TreeASTNodeAdapter name = new TreeASTNodeAdapter(adapter, "IfTrue");
            parentNode = name;
            ce.getIfTrue().accept(this);
            adapter.addChild(name);
        }
        if (ce.getIfFalse() != null) {
            TreeASTNodeAdapter name = new TreeASTNodeAdapter(adapter, "IfFalse");
            parentNode = name;
            ce.getIfFalse().accept(this);
            adapter.addChild(name);
        }
        parentNode = helpParent;
    }

    public void visit(ContinueStatement cs) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "ContinueStatement",
                cs.getStartOffset(), cs.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (cs.getExpression() != null) {
            cs.getExpression().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(DeclareStatement ds) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, 
                "DeclareStatement",
                ds.getStartOffset(), ds.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        if (ds.getDirectiveNames() != null) {
            TreeASTNodeAdapter names = new TreeASTNodeAdapter(adapter, "Names");
            parentNode = names;
            for (Identifier identifier : ds.getDirectiveNames()) {
                identifier.accept(this);
            }
            adapter.addChild(names);
        }
        if (ds.getDirectiveValues() != null) {
            TreeASTNodeAdapter values = new TreeASTNodeAdapter(adapter, "Values");
            parentNode = values;
            for (Expression val : ds.getDirectiveValues()) {
                val.accept(this);
            }
            adapter.addChild(values);
        }
        parentNode = adapter;
        if (ds.getBody() != null)
            ds.getBody().accept(this);
        parentNode = helpParent;
    }

    public void visit(DoStatement ds) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, 
                "DoStatement",
                ds.getStartOffset(), ds.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (ds.getCondition() != null) {
            ds.getCondition().accept(this);
        }
        if (ds.getBody() != null) {
            ds.getBody().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(EchoStatement echoStatement) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, "EchoStatement", echoStatement.getStartOffset(), echoStatement.getEndOffset());
        parentNode.addChild(adapter);
        List<Expression> expressions = echoStatement.getExpressions();
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        for (Expression expression : expressions) {
            expression.accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(EmptyStatement es) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "EmptyStatement",
                es.getStartOffset(), es.getEndOffset());
        parentNode.addChild(adapter);
    }

    public void visit(ExpressionStatement expressionStatement) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, "ExpressionStatement",
                expressionStatement.getStartOffset(), expressionStatement.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        expressionStatement.getExpression().accept(this);
        parentNode = helpParent;
    }

    public void visit(FieldAccess fa) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "FieldAccess",
                fa.getStartOffset(), fa.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        fa.getDispatcher().accept(this);
        fa.getField().accept(this);
        parentNode = helpParent;
    }

    public void visit(FieldsDeclaration fd) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "FieldsDeclaration (modifier: " + fd.getModifierString() + ")",
                fd.getStartOffset(), fd.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (fd.getFields() != null) {
            for (SingleFieldDeclaration decl : fd.getFields()) {
                decl.accept(this);
            }
        }
        parentNode = helpParent;
    }

    public void visit(ForEachStatement fe) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "ForEachStatement",
                fe.getStartOffset(), fe.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;

        if (fe.getExpression() != null) {
            TreeASTNodeAdapter ex = new TreeASTNodeAdapter(adapter, "Expression");
            parentNode = ex;
            fe.getExpression().accept(this);
            adapter.addChild(ex);
        }
        if (fe.getKey() != null) {
            TreeASTNodeAdapter key = new TreeASTNodeAdapter(adapter, "Key");
            parentNode = key;
            fe.getKey().accept(this);
            adapter.addChild(key);
        }
        if (fe.getValue() != null) {
            TreeASTNodeAdapter value = new TreeASTNodeAdapter(adapter, "Value");
            parentNode = value;
            fe.getValue().accept(this);
            adapter.addChild(value);
        }
        parentNode = adapter;
        if (fe.getStatement() != null) {
            fe.getStatement().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(FormalParameter fp) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "FormalParameter (isMandatory: " + fp.isMandatory() + ")",
                fp.getStartOffset(), fp.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (fp.getParameterName() != null) {
            TreeASTNodeAdapter name = new TreeASTNodeAdapter(adapter, "Name");
            parentNode = name;
            fp.getParameterName().accept(this);
            adapter.addChild(name);
        }

        if (fp.getParameterType() != null) {
            TreeASTNodeAdapter type = new TreeASTNodeAdapter(adapter, "Type");
            parentNode = type;
            fp.getParameterType().accept(this);
            adapter.addChild(type);
        }

        if (fp.getDefaultValue() != null) {
            TreeASTNodeAdapter value = new TreeASTNodeAdapter(adapter, "Default Value");
            parentNode = value;
            fp.getDefaultValue().accept(this);
            adapter.addChild(value);
        }

        parentNode = helpParent;
    }

    public void visit(ForStatement fs) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "ForStatement",
                fs.getStartOffset(), fs.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        if (fs.getInitializers() != null) {
            TreeASTNodeAdapter name = new TreeASTNodeAdapter(adapter, "Initializers");
            parentNode = name;
            for (Expression expr : fs.getInitializers()) {
                expr.accept(this);
            }
            adapter.addChild(name);
        }
        if (fs.getConditions() != null) {
            TreeASTNodeAdapter name = new TreeASTNodeAdapter(adapter, "Conditions");
            parentNode = name;
            for (Expression expr : fs.getConditions()) {
                expr.accept(this);
            }
            adapter.addChild(name);
        }
        if (fs.getUpdaters() != null) {
            TreeASTNodeAdapter name = new TreeASTNodeAdapter(adapter, "Updaters");
            parentNode = name;
            for (Expression expr : fs.getUpdaters()) {
                expr.accept(this);
            }
            adapter.addChild(name);
        }
        parentNode = adapter;
        if (fs.getBody() != null) {
            fs.getBody().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(FunctionDeclaration functionDeclaration) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "FunctionDeclaration (isReference: " + functionDeclaration.isReference() + ")",
                functionDeclaration.getStartOffset(), functionDeclaration.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;

        if (functionDeclaration.getFunctionName() != null) {
            TreeASTNodeAdapter name = new TreeASTNodeAdapter(adapter, "Name");
            parentNode = name;
            functionDeclaration.getFunctionName().accept(this);
            adapter.addChild(name);
        }
        if (functionDeclaration.getFormalParameters() != null) {
            TreeASTNodeAdapter parameters = new TreeASTNodeAdapter(adapter, "Parameters");
            parentNode = parameters;
            for (FormalParameter parameter : functionDeclaration.getFormalParameters()) {
                parameter.accept(this);
            }
            adapter.addChild(parameters);
        }
        if (functionDeclaration.getBody() != null) {
            parentNode = adapter;
            functionDeclaration.getBody().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(FunctionInvocation fi) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "FunctionInvocation",
                fi.getStartOffset(), fi.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        fi.getFunctionName().accept(this);
        if (fi.getParameters() != null) {
            TreeASTNodeAdapter params = new TreeASTNodeAdapter(adapter, "Parameters");
            parentNode = params;
            for (Expression expr : fi.getParameters()) {
                expr.accept(this);
            }
            adapter.addChild(params);
        }
        parentNode = helpParent;
    }

    public void visit(FunctionName fn) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "FunctionName",
                fn.getStartOffset(), fn.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        fn.getName().accept(this);
        parentNode = helpParent;
    }

    public void visit(GlobalStatement gs) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "GlobalStatement",
                gs.getStartOffset(), gs.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (gs.getVariables() != null) {
            for (Variable var : gs.getVariables()) {
                var.accept(this);
            }
        }
        parentNode = helpParent;
    }

    public void visit(Identifier identifier) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "Identifier (name: \"" + identifier.getName() + "\")",
                identifier.getStartOffset(), identifier.getEndOffset());
        parentNode.addChild(adapter);
    }

    public void visit(IfStatement is) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "IfStatement",
                is.getStartOffset(), is.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (is.getCondition() != null) {
            TreeASTNodeAdapter condition = new TreeASTNodeAdapter(adapter, "Condition");
            parentNode = condition;
            is.getCondition().accept(this);
            adapter.addChild(condition);
        }
        if (is.getTrueStatement() != null) {
            TreeASTNodeAdapter ts = new TreeASTNodeAdapter(adapter, "TrueStatement");
            parentNode = ts;
            is.getTrueStatement().accept(this);
            adapter.addChild(ts);
        }
        if (is.getFalseStatement() != null) {
            TreeASTNodeAdapter fs = new TreeASTNodeAdapter(adapter, "FalseStatement");
            parentNode = fs;
            is.getFalseStatement().accept(this);
            adapter.addChild(fs);
        }
        parentNode = helpParent;
    }

    public void visit(IgnoreError ir) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "IgnoreError",
                ir.getStartOffset(), ir.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (ir.getExpression() != null) {
            ir.getExpression().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(Include in) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "Include (type: " + in.getIncludeType().name() + ")",
                in.getStartOffset(), in.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        in.getExpression().accept(this);
        parentNode = helpParent;
    }

    public void visit(InfixExpression ie) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "InfixExpression (Operator: " + ie.getOperator().name() + ")",
                ie.getStartOffset(), ie.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        ie.getLeft().accept(this);
        ie.getRight().accept(this);
        parentNode = helpParent;
    }

    public void visit(InLineHtml inLineHtml) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "InLineHtml",
                inLineHtml.getStartOffset(), inLineHtml.getEndOffset());
        parentNode.addChild(adapter);
    }

    public void visit(InstanceOfExpression oe) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, 
                "InstanceOfExpression",
                oe.getStartOffset(), oe.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (oe.getExpression() != null) {
            oe.getExpression().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(InterfaceDeclaration id) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, 
                "InterfaceDeclaration",
                id.getStartOffset(), id.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        if (id.getName() != null) {
            TreeASTNodeAdapter name = new TreeASTNodeAdapter(adapter, "Name");
            parentNode = name;
            id.getName().accept(this);
            adapter.addChild(name);
        }
        if (id.getInterfaes() != null) {
            TreeASTNodeAdapter interfeas = new TreeASTNodeAdapter(adapter, "Interfaes");
            parentNode = interfeas;
            for (Identifier identifier : id.getInterfaes()) {
                identifier.accept(this);
            }
            adapter.addChild(interfeas);
        }
        parentNode = adapter;
        if (id.getBody() != null) {
            id.getBody().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(ListVariable lv) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "ListVariable",
                lv.getStartOffset(), lv.getEndOffset());
        parentNode.addChild(adapter);
        if (lv.getVariables() != null) {
            TreeASTNodeAdapter helpParent = parentNode;
            parentNode = adapter;
            for (VariableBase var : lv.getVariables()) {
                var.accept(this);
            }
            parentNode = helpParent;
        }
    }

    public void visit(MethodDeclaration methodDeclaration) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "MethodDeclaration (Modifier: " + methodDeclaration.getModifierString() + ")",
                methodDeclaration.getStartOffset(), methodDeclaration.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        methodDeclaration.getFunction().accept(this);
        parentNode = helpParent;
    }

    public void visit(MethodInvocation mi) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "MethodInvocation",
                mi.getStartOffset(), mi.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        mi.getDispatcher().accept(this);
        mi.getMethod().accept(this);
        parentNode = helpParent;
    }

    public void visit(ParenthesisExpression pe) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "ParenthesisExpression",
                pe.getStartOffset(), pe.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        pe.getExpression().accept(this);
        parentNode = helpParent;
    }

    public void visit(PostfixExpression pe) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "PostfixExpression (operator: " + pe.getOperator().name() + ")",
                pe.getStartOffset(), pe.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (pe.getVariable() != null) {
            pe.getVariable().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(PrefixExpression pe) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "PrefixExpression (operator: " + pe.getOperator().name() + ")",
                pe.getStartOffset(), pe.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        pe.getVariable().accept(this);
        parentNode = helpParent;
    }

    public void visit(Program program) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, "Program", program.getStartOffset(), program.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        
        if (program.getComments() != null) {
            TreeASTNodeAdapter comments = new TreeASTNodeAdapter(adapter, "Comments");
            adapter.addChild(comments);
            parentNode = comments;
            for (Comment comment : program.getComments()) {
                comment.accept(this);
            }
        }
        parentNode = adapter;
        List<Statement> statements = program.getStatements();
        for (Statement statement : statements) {
            statement.accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(Quote quote) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "Quote (type: " + quote.getQuoteType().name() + ")",
                quote.getStartOffset(), quote.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        for (Expression expression : quote.getExpressions()) {
            expression.accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(Reference re) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "Reference",
                re.getStartOffset(), re.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        re.getExpression().accept(this);
        parentNode = helpParent;
    }

    public void visit(ReflectionVariable rf) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, 
                "ReflectionVariable",
                rf.getStartOffset(), rf.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        rf.getName().accept(this);
        parentNode = helpParent;
    }

    public void visit(ReturnStatement rs) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "ReturnStatement",
                rs.getStartOffset(), rs.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (rs.getExpression() != null) {
            rs.getExpression().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(Scalar scalar) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "Scalar (type: " + scalar.getScalarType().name() + ", value: \"" + scalar.getStringValue() + "\")",
                scalar.getStartOffset(), scalar.getEndOffset());
        parentNode.addChild(adapter);
    }

    public void visit(SingleFieldDeclaration sf) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "SingleFieldDeclaration",
                sf.getStartOffset(), sf.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (sf.getName() != null) {
            sf.getName().accept(this);
        }
        if (sf.getValue() != null) {
            sf.getValue().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(StaticConstantAccess ca) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, 
                "StaticConstantAccess",
                ca.getStartOffset(), ca.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (ca.getClassName() != null) {
            ca.getClassName().accept(this);
        }
        if (ca.getConstant() != null) {
            ca.getConstant().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(StaticFieldAccess fa) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, 
                "StaticFieldAccess",
                fa.getStartOffset(), fa.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (fa.getClassName() != null) {
            fa.getClassName().accept(this);
        }
        if (fa.getField() != null) {
            fa.getField().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(StaticMethodInvocation mi) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "StaticMethodInvocation",
                mi.getStartOffset(), mi.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        mi.getClassName().accept(this);
        mi.getMethod().accept(this);
        parentNode = helpParent;
    }

    public void visit(StaticStatement ss) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "StaticStatement",
                ss.getStartOffset(), ss.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (ss.getExpressions() != null) {
            for (Expression ex : ss.getExpressions()) {
                ex.accept(this);
            }
        }
        parentNode = helpParent;
    }

    public void visit(SwitchCase sc) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "SwitchCase (isDefault: " + sc.isDefault() + ")",
                sc.getStartOffset(), sc.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        if (sc.getValue() != null) {
            TreeASTNodeAdapter value = new TreeASTNodeAdapter(adapter, "Value");
            parentNode = value;
            sc.getValue().accept(this);
            adapter.addChild(value);
        }
        parentNode = adapter;
        if (sc.getActions() != null) {
            for (Statement st : sc.getActions()) {
                st.accept(this);
            }
        }
        parentNode = helpParent;
    }

    public void visit(SwitchStatement ss) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "SwitchStatement",
                ss.getStartOffset(), ss.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        if (ss.getExpression() != null) {
            TreeASTNodeAdapter condition = new TreeASTNodeAdapter(adapter, "Condition");
            parentNode = condition;
            ss.getExpression().accept(this);
            adapter.addChild(condition);
        }
        parentNode = adapter;
        if (ss.getBody() != null) {
            ss.getBody().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(ThrowStatement ts) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, 
                "ThrowStatement",
                ts.getStartOffset(), ts.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (ts.getExpression() != null)
            ts.getExpression().accept(this);
        parentNode = helpParent;
    }

    public void visit(TryStatement ts) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, 
                "TryStatement",
                ts.getStartOffset(), ts.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        if (ts.getCatchClauses() != null) {
            for(CatchClause cc : ts.getCatchClauses()) {
                cc.accept(this);
            }
        }
        parentNode = adapter;
        if (ts.getBody() != null) 
            ts.getBody().accept(this);
        parentNode = helpParent;
    }

    public void visit(UnaryOperation uo) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "UnaryOperation (operator: " + uo.getOperator().name() + ")",
                uo.getStartOffset(), uo.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (uo.getExpression() != null) {
            uo.getExpression().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(Variable variable) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "Variable (isDollared: " + variable.isDollared() + ")",
                variable.getStartOffset(), variable.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        variable.getName().accept(this);
        parentNode = helpParent;
    }

    public void visit(WhileStatement ws) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "WhileStatement",
                ws.getStartOffset(), ws.getEndOffset());
        parentNode.addChild(adapter);
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        if (ws.getCondition() != null) {
            TreeASTNodeAdapter condition = new TreeASTNodeAdapter(adapter, "Condition");
            parentNode = condition;
            ws.getCondition().accept(this);
            adapter.addChild(condition);
            parentNode = adapter;
        }
        if (ws.getBody() != null) {
            ws.getBody().accept(this);
        }
        parentNode = helpParent;
    }

    public void visit(ASTNode node) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode,
                "ASTNode - this node shouldn't appear in the tree.",
                node.getStartOffset(), node.getEndOffset());
        parentNode.addChild(adapter);
    }

    public void visit(PHPDocBlock node) {
        TreeASTNodeAdapter adapter = new TreeASTNodeAdapter(parentNode, 
                "PHPDocComment", node.getStartOffset(), node.getEndOffset());
        parentNode.addChild(adapter);
        adapter.addChild(new TreeASTNodeAdapter(adapter, node.getDescription()));
        if (node.getTags() != null) {
            for (PHPDocTag tag : node.getTags()) {
                adapter.addChild(new TreeASTNodeAdapter(adapter, 
                        tag.getKind() + " " + tag.getValue()));
            }
        }
        TreeASTNodeAdapter helpParent = parentNode;
        parentNode = adapter;
        parentNode = helpParent;
    }
}
