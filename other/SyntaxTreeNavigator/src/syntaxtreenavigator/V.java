/*The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License. You can obtain a copy of the License at 
http://www.netbeans.org/cddl.html or http://www.netbeans.org/cddl.txt.
When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"  */
package syntaxtreenavigator;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.Visitor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Tim Boudreau
 */
public class V extends DefaultMutableTreeNode {
    private Object o;
    private String title;
    static boolean showLists = false;
    
    public V(Object o) {
        super (o);
        this.o = o;
        proc (o);
    }
    
    public V(String title, Object o) {
        super (o);
        this.o = o;
        this.title = title;
        if ("Class".equals(title)) {
            Thread.dumpStack();
        }
        proc (o);
    }
    
    public String toString() {
        String nm = title == null ? strippedClassName(o.getClass()) : title;
        StringBuffer result = new StringBuffer (nm.length() + 20);
        result.append (nm);
        if (o instanceof JCMethodDecl) {
            result.append (' ');
            result.append ('(');
            result.append (((JCMethodDecl) o).getName());
            result.append(')');
        } else if (o instanceof JCTree.JCVariableDecl) {
            result.append (' ');
            result.append ('(');
            result.append (((JCVariableDecl) o).getName());
            result.append(')');
        }
        return result.toString();
    }
    
    static String strippedClassName(Class clazz) {
        if (clazz == null) {
            return "[null]";
        }
        String s = clazz.getName();
        int ix1 = s.lastIndexOf('.');
        int ix2 = s.lastIndexOf('$');
        int ix = Math.max(ix1, ix2);
        if (ix != s.length() - 1) {
            return s.substring(ix + 1);
        } else {
            return s;
        }
    }
    
    private class TreeV extends Visitor {
        public void visitTopLevel(JCTree.JCCompilationUnit that) {
            proc(that);
        }

        public void visitImport(JCTree.JCImport that) {
            proc(that);
        }

        public void visitClassDef(JCTree.JCClassDecl that) {
            proc(that);
        }

        public void visitMethodDef(JCTree.JCMethodDecl that) {
            proc(that);
        }

        public void visitVarDef(JCTree.JCVariableDecl that) {
            proc(that);
        }

        public void visitSkip(JCTree.JCSkip that) {
            proc(that);
        }

        public void visitBlock(JCTree.JCBlock that) {
            proc(that);
        }

        public void visitDoLoop(JCTree.JCDoWhileLoop that) {
            proc(that);
        }

        public void visitWhileLoop(JCTree.JCWhileLoop that) {
            proc(that);
        }

        public void visitForLoop(JCTree.JCForLoop that) {
            proc(that);
        }

        public void visitForeachLoop(JCTree.JCEnhancedForLoop that) {
            proc(that);
        }

        public void visitLabelled(JCTree.JCLabeledStatement that) {
            proc(that);
        }

        public void visitSwitch(JCTree.JCSwitch that) {
            proc(that);
        }

        public void visitCase(JCTree.JCCase that) {
            proc(that);
        }

        public void visitSynchronized(JCTree.JCSynchronized that) {
            proc(that);
        }

        public void visitTry(JCTree.JCTry that) {
            proc(that);
        }

        public void visitCatch(JCTree.JCCatch that) {
            proc(that);
        }

        public void visitConditional(JCTree.JCConditional that) {
            proc(that);
        }

        public void visitIf(JCTree.JCIf that) {
            proc(that);
        }

        public void visitExec(JCTree.JCExpressionStatement that) {
            proc(that);
        }

        public void visitBreak(JCTree.JCBreak that) {
            proc(that);
        }

        public void visitContinue(JCTree.JCContinue that) {
            proc(that);
        }

        public void visitReturn(JCTree.JCReturn that) {
            proc(that);
        }

        public void visitThrow(JCTree.JCThrow that) {
            proc(that);
        }

        public void visitAssert(JCTree.JCAssert that) {
            proc(that);
        }

        public void visitApply(JCTree.JCMethodInvocation that) {
            proc(that);
        }

        public void visitNewClass(JCTree.JCNewClass that) {
            proc(that);
        }

        public void visitNewArray(JCTree.JCNewArray that) {
            proc(that);
        }

        public void visitParens(JCTree.JCParens that) {
            proc(that);
        }

        public void visitAssign(JCTree.JCAssign that) {
            proc(that);
        }

        public void visitAssignop(JCTree.JCAssignOp that) {
            proc(that);
        }

        public void visitUnary(JCTree.JCUnary that) {
            proc(that);
        }

        public void visitBinary(JCTree.JCBinary that) {
            proc(that);
        }

        public void visitTypeCast(JCTree.JCTypeCast that) {
            proc(that);
        }

        public void visitTypeTest(JCTree.JCInstanceOf that) {
            proc(that);
        }

        public void visitIndexed(JCTree.JCArrayAccess that) {
            proc(that);
        }

        public void visitSelect(JCTree.JCFieldAccess that) {
            proc(that);
        }

        public void visitIdent(JCTree.JCIdent that) {
            proc(that);
        }

        public void visitLiteral(JCTree.JCLiteral that) {
            proc(that);
        }

        public void visitTypeIdent(JCTree.JCPrimitiveTypeTree that) {
            proc(that);
        }

        public void visitTypeArray(JCTree.JCArrayTypeTree that) {
            proc(that);
        }

        public void visitTypeApply(JCTree.JCTypeApply that) {
            proc(that);
        }

        public void visitTypeParameter(JCTree.JCTypeParameter that) {
            proc(that);
        }

        public void visitWildcard(JCTree.JCWildcard that) {
            proc(that);
        }

        public void visitAnnotation(JCTree.JCAnnotation that) {
            proc(that);
        }

        public void visitModifiers(JCTree.JCModifiers that) {
            proc(that);
        }

        public void visitErroneous(JCTree.JCErroneous that) {
            proc(that);
        }

        public void visitLetExpr(JCTree.LetExpr that) {
            proc(that);
        }

        public void visitTree(JCTree that) {
            proc(that);
        }
    }
    
    static void clear() {
        visited.clear();
    }
    
    private static Set visited = new HashSet();
    private void proc (Object o) {
        if (o == null) {
            return;
        }
        if (visited.contains(o)) {
            return;
        }
        visited.add (o);
        if (o instanceof List) {
            procList ((List) o);
        } else {
            if (o instanceof JCTree) {
                ((JCTree) o).accept(new TreeV());
            }
            Class clazz = o.getClass();
            Method[] m = clazz.getMethods();
            for (int i = 0; i < m.length; i++) {
                boolean isCollection = Collection.class.isAssignableFrom(m[i].getReturnType());
                int paramCount = m[i].getParameterTypes().length;
                if (isCollection && paramCount == 0 && m[i].getName().startsWith("get")) {
                    String title = m[i].getName().substring(3);
                    Collection collection;
                    try {
                        collection = ((Collection) m[i].invoke(o));
                        if ((collection != null && !collection.isEmpty()) || showLists) {
                            add (new V (title, collection));
                        }
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                }
                boolean isTree = JCTree.class.isAssignableFrom(m[i].getReturnType());
                if (isTree && paramCount == 0 && m[i].getName().startsWith("get")) {
                    try {
                        JCTree tree = (JCTree) m[i].invoke(o);
                        if (!visited.contains(tree)) {
                            add (new V(m[i].getName().substring(3), tree));
                        }
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private void procList(Collection list) {
        if (!showLists && list.isEmpty()) {
            return;
        }
        for (Iterator i=list.iterator(); i.hasNext();) {
            add (new V(i.next()));
        }
    }
}
