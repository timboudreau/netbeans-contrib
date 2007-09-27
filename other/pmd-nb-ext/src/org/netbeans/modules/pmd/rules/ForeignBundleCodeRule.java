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

package org.netbeans.modules.pmd.rules;

import java.text.MessageFormat;

import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.symboltable.*;

/** Searches for bundle use that refers to another class
 *
 * Taken from UnusedPrivateVariable
 * @author Radim Kubacki
 */
public class ForeignBundleCodeRule extends AbstractRule {

    /**
     * Skip interfaces because they don't have instance variables.
     */
    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    private boolean isBundleExpression (String value) {
        if (value == null) {
            return false;
        }

        if (value.indexOf("NbBundle.getMessage") >= 0   // NOI18N
        ||  value.indexOf("NbBundle.getBundle") >= 0)   // NOI18N
            return true;
        
        return false;
    }
    
    public Object visit(ASTPrimaryExpression node, Object data) {
        RuleContext ctx = (RuleContext)data;

//        for (int i=0;i<node.jjtGetNumChildren(); i++) {
        if (node.jjtGetNumChildren()>=2) {
            SimpleNode child = (SimpleNode)node.jjtGetChild(0);
            if (child instanceof ASTPrimaryPrefix && child.jjtGetNumChildren() > 0 &&  child.jjtGetChild(0) instanceof ASTName) {
                ASTName prefix = (ASTName)child.jjtGetChild(0);
                
                if (isBundleExpression (prefix.getImage())) {
                    SimpleNode suffix = (SimpleNode)node.jjtGetChild(1);
                    if (suffix instanceof ASTPrimarySuffix && suffix.jjtGetNumChildren() > 0 &&  suffix.jjtGetChild(0) instanceof ASTArguments) {
                        ASTArguments args = (ASTArguments)suffix.jjtGetChild(0);

                        ASTExpression av = (args.getArgumentCount()>0)? (ASTExpression)args.jjtGetChild(0).jjtGetChild(0):null;
                        ASTPrimaryExpression pExpr = null;
                        if (av != null && av.jjtGetNumChildren() == 1 && av.jjtGetChild(0) instanceof ASTPrimaryExpression) {
                            pExpr = (ASTPrimaryExpression)av.jjtGetChild(0);
                        }
                        ASTPrimaryPrefix pPre = null;
                        if (pExpr != null && pExpr.jjtGetNumChildren() == 1 && pExpr.jjtGetChild(0) instanceof ASTPrimaryPrefix) {
                            pPre = (ASTPrimaryPrefix)pExpr.jjtGetChild(0);
                        }
                        ASTResultType pRT = null;
                        if (pPre != null && pPre.jjtGetNumChildren() == 1 && pPre.jjtGetChild(0) instanceof ASTResultType) {
                            pRT = (ASTResultType)pPre.jjtGetChild(0);
                        }
                        ASTType pT = null;
                        if (pRT != null && pRT.jjtGetNumChildren() == 1 && pRT.jjtGetChild(0) instanceof ASTType) {
                            pT = (ASTType)pRT.jjtGetChild(0);
                        }
                        ASTName pN = null;
                        if (pT != null && pT.jjtGetNumChildren() == 1 && pT.jjtGetChild(0) instanceof ASTName) {
                            pN = (ASTName)pT.jjtGetChild(0);
                        }
                        if (pN != null && !isCorrectClass (node, pN)) {
                            ctx.getReport().addRuleViolation(createRuleViolation(ctx, child.getBeginLine(), MessageFormat.format(getMessage(), new Object[] {pN.getImage()})));
                        }
                    }
                }
            }
        }
        super.visit(node, data);
        return data;
    }
    
    /** checks if symbol in name matches to class definition encapsulating node
     */
    private boolean isCorrectClass (ASTPrimaryExpression node, ASTName name) {
        if (name == null || name.getImage() == null)
            return false;
        
        ASTUnmodifiedClassDeclaration clz = null;
        for (Node n = node; n != null; n = n.jjtGetParent()) {
            if (n instanceof ASTUnmodifiedClassDeclaration) {
                clz = (ASTUnmodifiedClassDeclaration)n;
                if (name.getImage ().indexOf (clz.getImage()) >= 0)
                    return true;
            }
        }
        return false;
    }
}
