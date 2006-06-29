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

package org.netbeans.modules.pmd.rules;

import java.text.MessageFormat;

import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.symboltable.*;

/** Searches for statically referenced bundles
 *
 * Taken from UnusedPrivateVariable
 * @author Radim Kubacki
 */
public class StaticBundleCodeRule extends AbstractRule {

    /**
     * Skip interfaces because they don't have instance variables.
     */
    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    public Object visit(ASTClassBody node, Object data) {
        RuleContext ctx = (RuleContext)data;

        for (int i=0;i<node.jjtGetNumChildren(); i++) {
            SimpleNode child = (SimpleNode)node.jjtGetChild(i);
            if (child instanceof ASTClassBodyDeclaration && child.jjtGetNumChildren() > 0 &&  child.jjtGetChild(0) instanceof ASTFieldDeclaration) {
                ASTFieldDeclaration field = (ASTFieldDeclaration)child.jjtGetChild(0);
                if (!field.isStatic()) {
                    continue;
                }
                // FieldDeclaration 
                //   Type
                //     Name
                //   VariableDeclaration
                //     VariableDeclarationId
                SimpleNode target = (SimpleNode)field.jjtGetChild(0).jjtGetChild(0);
                if (!isBundleType (target.getImage())) {
                    continue;
                }
                SimpleNode var = (SimpleNode)field.jjtGetChild(1).jjtGetChild(0);

                ctx.getReport().addRuleViolation(createRuleViolation(ctx, field.getBeginLine(), MessageFormat.format(getMessage(), new Object[] {var.getImage()})));
            }
        }
        super.visit(node, data);
        return data;
    }

    private boolean isBundleType (String value) {
        if (value == null) {
            return false;
        }
        if ("ResourceBundle".equals(value)  // NOI18N
        ||  "NbBundle".equals(value)        // NOI18N
        ||  "java.util.ResourceBundle".equals(value)    // NOI18N
        ||  "org.openide.util.NbBundle".equals(value))  // NOI18N
            return true;
        
        return false;
    }
}
