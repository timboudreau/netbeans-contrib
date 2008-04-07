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
package org.netbeans.modules.scala.editing.nodes;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import xtc.tree.Annotation;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.util.Pair;

/**
 *
 * @author Caoyuan Deng
 */
public class AstVisitor extends Visitor {

    private int indentLevel;
    protected Stack<GNode> astPath = new Stack<GNode>();

    public AstVisitor() {
    }
    
    protected String getAstPathString() {
        StringBuilder sb = new StringBuilder();
        
        for (Iterator<GNode> itr = astPath.iterator(); itr.hasNext();) {
            sb.append(itr.next().getName());
            if (itr.hasNext()) {
                sb.append(".");
            }
        }
        
        return sb.toString();
    }
    
    protected GNode findNearsetNode(String name) {
        GNode result = null;
        
        for (Iterator<GNode> itr = astPath.iterator(); itr.hasNext();) {
            GNode node = itr.next();
            if (node.getName().equals(name)) {
                result = node;
            }
        }
        
        return result;
    }

    private String indent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    public void accept(Node node) {
        dispatch(node);
    }

    public void visit(GNode node) {
        astPath.push(node);
        //System.out.println(indent() + "{" + node.getName() + "}");
        indentLevel++;
        for (Iterator itr = node.iterator(); itr.hasNext();) {
            Object o = itr.next();
            if (o instanceof GNode) {
                dispatch((GNode) o);
            } else if (o instanceof Pair) {
                visit((Pair) o);
            }
        }
        indentLevel--;
        astPath.pop();
    }

    public void visit(Pair pair) {
        //System.out.println(indent() + "[");
        indentLevel++;
        for (Iterator itr = pair.iterator(); itr.hasNext();) {
            Object o = itr.next();
            if (o instanceof GNode) {
                dispatch((GNode) o);
            } else if (o instanceof Pair) {
                visit((Pair) o);
            }
        }
        indentLevel--;
        //System.out.println(indent() + "]");
    }

    @Override
    public Object visit(Annotation a) {
        System.out.println(indent() + "@" + a.toString());
        return null;
    }

    public void visitCompilationUnit(final GNode that) {
        visit(that);
    }
    
    public void visitPackage(final GNode that) {
        visit(that);
    }

    public List<AstElement> visitQualId(GNode that) {
        visit(that);
        return null;
    }

    public List<AstElement> visitIds(GNode that) {
        visit(that);
        return null;
    }

    public List<AstElement> visitPath(GNode that) {
        visit(that);
        return null;
    }

    public List<AstElement> visitStableId(GNode that) {
        visit(that);
        return null;
    }

    public AstElement visitId(GNode that) {
        visit(that);
        return null;
    }    
    
    public void visitClassDef(final GNode that) {
        visit(that);
    }

    public void visitTraitDef(final GNode that) {
        visit(that);
    }

    public void visitObjectDef(final GNode that) {
        visit(that);
    }
    
    public void visitFunDcl(final GNode that) {
        visit(that);
    }
    
    public void visitFunDef(final GNode that) {
        visit(that);
    }
    
    public Function visitFunSig(final GNode that) {
        visit(that);
        return null;
    }
    
    public List<Var> visitParamClauses(final GNode that) {
        visit(that);
        return null;
    }
    
    public List<Var> visitParamClause(final GNode that) {
        visit(that);
        return null;
    }

    public List<Var> visitParams(final GNode that) {
        visit(that);
        return null;
    }
    
    public Var visitParam(final GNode that) {
        visit(that);
        return null;
    }
    
    public void visitConstructorFunDef(final GNode that) {
        visit(that);
    }

    public List<AstElement> visitClassParams(final GNode that) {
        visit(that);
        return null;
    }
    
    public AstElement visitClassParam(final GNode that) {
        visit(that);
        return null;
    }

    public void visitValDcl(final GNode that) {
        visit(that);
    }
    
    public void visitVarDcl(final GNode that) {
        visit(that);
    }
    
    public void visitValDef(final GNode that) {
        visit(that);
    }

    public void visitVarDef(final GNode that) {
        visit(that);
    }
    
    public List<AstElement> visitIdPattern(GNode that) {
        visit(that);
        return null;
    }
    
    public String visitModifier(GNode that) {
        visit(that);
        return null;
    }
        
    public String visitLocalModifier(GNode that) {
        visit(that);
        return null;
    }

    public String visitAccessModifier(GNode that) {
        visit(that);
        return null;
    }
    
    public void visitSimpleIdExpr(GNode that) {
        visit(that);        
    }
    
    public TypeRef visitAnnotType(GNode that) {
        visit(that);
        return null;
    }
    
    public TypeRef visitSimpleIdType(GNode that) {
        visit(that);
        return null;
    }

}
