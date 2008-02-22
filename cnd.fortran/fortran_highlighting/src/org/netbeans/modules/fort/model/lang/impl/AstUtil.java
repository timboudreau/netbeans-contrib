

package org.netbeans.modules.fort.model.lang.impl;

import antlr.ASTVisitor;
import antlr.collections.AST;
import java.io.PrintStream;

/**
 * Miscellaneous AST-related static utility functions
 * implemented after CSM AstUtil
 */
public class AstUtil {

  
    /**
     * find method with given name
     */
    public static AST findMethodName(AST ast){
        AST type = ast.getFirstChild(); // type
        return type;
    }

    /**
     * 
     * @return if AST has child with given type
     */
    public static boolean hasChildOfType(AST ast, int type) {
        for( AST token = ast.getFirstChild(); token != null; token = token.getNextSibling() ) {
            if( token.getType() == type ) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * finds AST's child with given type
     */
    public static AST findChildOfType(AST ast, int type) {
        for( AST token = ast.getFirstChild(); token != null; token = token.getNextSibling() ) {
            if( token.getType() == type ) {
                return token;
            }
        }
        return null;
    }
    
    /**
     * finds AST's sibling with given type
     */
    public static AST findSiblingOfType(AST ast, int type) {
        for( AST token = ast; token != null; token = token.getNextSibling() ) {
            if( token.getType() == type ) {
                return token;
            }
        }
        return null;
    }
    
    /**
     * 
     * @return last child of given AST
     */
    public static AST getLastChild(AST token) {
        if( token == null ) {
            return null;
        }
        AST child = token.getFirstChild();
        if( child != null ) {
            while( child.getNextSibling() != null ) {
                child = child.getNextSibling();
            }
            return child;
        }
        return null;
    }
    
    /**
     * utility function
     */
    public static AST getLastChildRecursively(AST token) {
        if( token == null ) {
            return null;
        }
        if( token.getFirstChild() == null ) {
            return token;
        }
        else {
            AST child = getLastChild(token);
            return getLastChildRecursively(child);
        }
    }
    
    /**
     * AST to stream
     */
    public static void toStream(AST ast, final PrintStream ps) {
        ASTVisitor impl = new ASTVisitor() {
            public void visit(AST node) {
		print(node, ps);
                for( AST node2 = node; node2 != null; node2 = node2.getNextSibling() ) {
                    if (node2.getFirstChild() != null) {
			ps.print('>');
                        visit(node2.getFirstChild());
			ps.print('<');
                    }
                }
            }
        };
        impl.visit(ast);
    }    
    
    private static void print(AST ast, PrintStream ps) {
        ps.print('[');
        ps.print(ast.getText());
        ps.print('(');
        ps.print(ast.getType());
        ps.print(')');
        ps.print(ast.getLine());
        ps.print(':');
        ps.print(ast.getColumn());
        ps.print(']');
        //ps.print('\n');
    }
    
}
 
