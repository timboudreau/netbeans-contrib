package org.netbeans.modules.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTNestedClassDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.AccessNode;

import java.util.ArrayList;
import java.util.List;

public class MissingPrivateInnerClassConstructorRule extends AbstractRule {

    private List constructors;

    public Object visit(ASTNestedClassDeclaration node, Object data) {
        if (!node.isPrivate()) {
            return super.visit(node, data);
        }
        constructors = new ArrayList();
        node.findChildrenOfType(ASTConstructorDeclaration.class, constructors);

        boolean ok = true;
        if (constructors.size() == 0) {
            ok = false;
        }/* else {  //these needs to ne tuned up
            for (java.util.Iterator i = constructors.iterator(); i.hasNext();) {
                ASTConstructorDeclaration c = (ASTConstructorDeclaration)i.next();
                if (!c.isPrivate()) {
                    ok = true;
                    break;
                }
            }
        }*/
        if (!ok) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }

        return super.visit(node, data);
    }
    
}
