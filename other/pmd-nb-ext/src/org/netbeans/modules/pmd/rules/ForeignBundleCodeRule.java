package org.netbeans.modules.pmd.rules;

import java.util.Iterator;
import java.util.Stack;
import java.util.HashSet;
import java.util.Set;
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
