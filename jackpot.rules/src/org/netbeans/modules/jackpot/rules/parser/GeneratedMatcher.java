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

package org.netbeans.modules.jackpot.rules.parser;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.api.JavacTaskImpl;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.jvm.ClassReader;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.parser.*;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.netbeans.api.jackpot.ConversionOperations;
import org.netbeans.api.jackpot.QueryException;
import org.netbeans.api.jackpot.QueryOperations;
import org.netbeans.api.jackpot.TreePathTransformer;
import org.netbeans.api.java.source.Comment;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementUtilities;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.spi.jackpot.RecursiveRuleException;
import org.netbeans.spi.jackpot.ScriptParsingException;
import org.openide.ErrorManager;
import static com.sun.tools.javac.code.Flags.*;
import static com.sun.tools.javac.code.TypeTags.*;

abstract public class GeneratedMatcher extends TreePathTransformer<Void,Object> {
    protected final static int MAX_DEPTH = 300;
    protected Tree[] meta = new Tree[20];
    protected Tree[] parentChain = new Tree[MAX_DEPTH];
    protected int depth = 0;
    protected int currentLine = -1;
    protected String script = "";
    private ElementUtilities elements;
    private ConversionOperations ops;
    private Trees trees;
    protected org.netbeans.api.java.source.TreeMaker make;
    private CompilationUnitTree currentTopLevel;
    protected String comment;
    private VariableCharacterization vcCache;
    private Element vcKey;
    private Tree vcTree;
    
    protected abstract Tree rewrite(Tree t);
    protected abstract void initializeKeywords() throws ClassNotFoundException;

    protected static final Object unknownValue = new Object();
    protected ClassReader classes;
    protected Name.Table names;
    protected Name nullName;
    protected Name trueName;
    protected Name falseName;
    protected Symtab symtab;
    private Context context;
    protected static final Integer intZero = new Integer(0);
    
    @Override
    public Void scan(Tree tree, Object p) {
        if (tree != null) {
            parentChain[depth++] = tree;
            super.scan(tree, p);
            depth--;
            if (tree.getKind() != Tree.Kind.EXPRESSION_STATEMENT) {
                comment = "";
                Tree ret = rewrite0(tree);
                if (ret != tree)
                    addChange(trees.getPath(currentTopLevel, tree), ret, comment);
                else if (comment != null && comment.length() > 0)
                    addResult(trees.getPath(currentTopLevel, tree), comment);                    
            }
        }
        return null;
    }

    /**
     * Depth checker to catch recursive rules.
     * @param t the tree to check
     */
    protected final Tree rewrite0(Tree t) {
        if (++depth >= MAX_DEPTH)
            throw new RecursiveRuleException(script, currentLine);
	parentChain[depth] = t;
	t = rewrite(t);
	depth--;
        return t;
    }

    public Tree parentOf(Tree t) {
	for(int p = depth; --p>0; )
	    if(parentChain[p]==t) return parentChain[p-1];
	return null;
    }
    public StatementTree containingStatement() {
	for(int p = depth; --p>=0; )
	    if(parentChain[p] instanceof StatementTree)
		return (StatementTree)parentChain[p];
	return null;
    }
    public void addComment(String s) {
        Comment cmt = Comment.create(s);
        make.addComment(containingStatement(), cmt, true);
    }
    public Tree parent() {
	return depth>1 ? parentChain[depth-2] : null;
    }

    @Override
    public void release() {
        super.release();
        context = null;
        elements = null;
        trees = null;
        ops = null;
        names = null;
        symtab = null;
        classes = null;
        make = null;
        jcmake = null;
        jctypes = null;
        nullName = null;
        trueName = null;
        falseName = null;
        currentTopLevel = null;
        vcCache = null;
        vcKey = null;
        vcTree = null;
    }
    
    @Override
    public void attach(CompilationInfo info) {
        super.attach(info);
        ops = new ConversionOperations(getWorkingCopy());
        elements = info.getElementUtilities();
        trees = info.getTrees();
        make = getWorkingCopy().getTreeMaker();
        context = getContext(info);
        names = Name.Table.instance(context);
        symtab = Symtab.instance(context);
        classes = ClassReader.instance(context);
        jcmake = com.sun.tools.javac.tree.TreeMaker.instance(context);
        jctypes = com.sun.tools.javac.code.Types.instance(context);
	nullName = names.fromString(Token.NULL.name);
	trueName = names.fromString(Token.TRUE.name);
	falseName = names.fromString(Token.FALSE.name);
        try {
            initializeKeywords();
        } catch (ClassNotFoundException e) {
            throw new ScriptParsingException("cannot find class: " + e.getMessage());
        }
    }
    
    private VariableCharacterization getCharacterization(Element s) {
        if(s!=vcKey|| vcCache==null) {
            vcTree = trees.getTree(s);
            vcCache = new VariableCharacterization(vcTree);
            vcKey= s;
        }
        return vcCache;
    }
    
    private static Context getContext(CompilationInfo info) {
        try {
            Field f = CompilationInfo.class.getDeclaredField("impl");
            f.setAccessible(true);
            Object impl = f.get(info);
            Method m = impl.getClass().getDeclaredMethod("getJavacTask");
            m.setAccessible(true);
            JavacTaskImpl task = (JavacTaskImpl) m.invoke(impl);
            return task.getContext();
        }
        catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Tree> T replaceclass(T tree, boolean explicitRef) {
        JCTree t = (JCTree)tree;
	if(t!=null) {
	    Symbol s = TreeInfo.symbol(t);
	    if(s!=null) {
		Symbol s2 = replacesymbol(s);
		if(s2 != s) {
                    if (explicitRef)
                        t = explicitQualIdent(s2);
                    else if (t.getTag() == JCTree.IDENT)
                        t = jcmake.Ident(s2);
                    else {
                        assert t.getTag() == JCTree.SELECT;
                        t = jcmake.Select(((JCFieldAccess)t).selected, s2.name);
                    }
                }
	    }
	}
    	return (T)t;
    }
    
    @Override
    public Void visitCompilationUnit(CompilationUnitTree tree, Object p) {
        currentTopLevel = tree;
        super.visitCompilationUnit(tree, p);
        currentTopLevel = null;
        return null;
    }
    
    @Override
    public Void visitWhileLoop(WhileLoopTree tree, Object p) {
        if (tree.getCondition().getKind() == Tree.Kind.PARENTHESIZED) {
            // a late Java 6 change, which hopefully will be eliminated soon
            scan(((ParenthesizedTree)tree.getCondition()).getExpression(), p);
            scan(tree.getStatement(), p);
        }
        else
            super.visitWhileLoop(tree, p);
        return null;
    }
    
    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree tree, Object p) {
        if (tree.getCondition().getKind() == Tree.Kind.PARENTHESIZED) {
            scan(tree.getStatement(), p);
            // a late Java 6 change, which hopefully will be eliminated soon
            scan(((ParenthesizedTree)tree.getCondition()).getExpression(), p);
        }
        else
            super.visitDoWhileLoop(tree, p);
        return null;
    }

    private JCExpression explicitQualIdent(Symbol sym) {
	return (sym.name == names.empty || sym.owner == null || 
            sym.owner == symtab.unnamedPackage || sym.owner == symtab.rootPackage ||
	    sym.owner.kind == Kinds.MTH || sym.owner.kind == Kinds.VAR)
	    ? jcmake.Ident(sym)
	    : jcmake.Select(explicitQualIdent(sym.owner), sym);
    }

    public Type replacetype(Type t) { return replacesymbol(t.tsym).type; }
    public Symbol replacesymbol(Symbol s) { return s; }
    public boolean isSubType(Type THIS, Type that) {
	Type rTHIS = replacetype(THIS);
	Type rthat = replacetype(that);
	if (THIS == that || rTHIS==rthat) return true;
	if (that.tag >= firstPartialTag) return jctypes.isSuperType(THIS, that);
	if (THIS.tsym == that.tsym)
	    return
		(!that.isParameterized() ||
		 jctypes.isSameTypes(THIS.getParameterTypes(), that.getParameterTypes())) &&
		isSubType(THIS.getEnclosingType(),that.getEnclosingType());
	if ((that.tsym.flags() & INTERFACE) != 0)
	    for (List<Type> is = jctypes.interfaces(THIS);
		 is.nonEmpty();
		 is = is.tail)
		if (isSubType(is.head,that)) return true;
	Type st = jctypes.supertype(THIS);
	if(st==null) return false;
	if (st.tag == CLASS && isSubType(st,that))
	    return true;
	return st.isErroneous();
    }
    
    @SuppressWarnings("unchecked")
    private int compare(Object a, Object b) {
        if(a==b) return 0;
        if(a==null) return -1;
        if(b==null) return 1;
        try {
            if(a.getClass()==b.getClass()) return ((Comparable)a).compareTo(b);
            if(a instanceof Double || b instanceof Double
                    || a instanceof Float || b instanceof Float) {
                double ad = ((Number)a).doubleValue();
                double bd = ((Number)b).doubleValue();
                return ad<bd ? -1 : ad>bd ? 1 : 0;
            }
            long al = ((Long)a).longValue();
            long bl = ((Long)b).longValue();
            return al<bl ? -1 : al>bl ? 1 : 0;
        } catch(Throwable t) { return -2; }
    }

    protected Object opGT(Object a, Object b) {
	return Boolean.valueOf(compare(a,b)>0);
    }
    protected Object opGE(Object a, Object b) {
	return Boolean.valueOf(compare(a,b)>=0);
    }
    protected Object opLT(Object a, Object b) {
	return Boolean.valueOf(compare(b,a)>0);
    }
    protected Object opLE(Object a, Object b) {
	return Boolean.valueOf(compare(b,a)>=0);
    }
    protected Object opEQ(Object a, Object b) {
	return Boolean.valueOf(compare(a,b)==0);
    }
    protected Object opNE(Object a, Object b) {
	return Boolean.valueOf(compare(a,b)!=0);
    }
    protected Object findClass(String jar, String clazz) {
	if("org.netbeans.api.jackpot.Query".equals(clazz) ||
           "org.netbeans.api.jackpot.TreePathQuery".equals(clazz) ||
           "org.netbeans.api.jackpot.Transformer".equals(clazz) ||
           "org.netbeans.api.jackpot.TreePathTransformer".equals(clazz) ||
	   "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher".equals(clazz)) return this;
	if(!"".equals(jar)) System.err.println("Unimplimented: "+jar);
	try {
	    return Class.forName(clazz).newInstance();
	} catch(Throwable t) {
	    getContext().sendErrorMessage(t.toString(), getClass().getName());
	    return null;
	}
    }
    
    protected JCExpression resolve(JCFieldAccess tree) {
	Symbol sym = TreeInfo.symbol(tree.selected);
	if (sym instanceof Symbol.ClassSymbol) {
	    Scope.Entry entry = ((Symbol.ClassSymbol)sym).members().lookup(tree.name);
	    if (entry.sym != null)
		return doResolve(tree, entry.sym);
	}
	Name n = TreeInfo.fullName(tree);
	sym = resolveIdent(n);
	if (sym != symtab.errSymbol)
	    return doResolve(tree, sym);
        return tree;
    }
    
    private JCExpression doResolve(JCFieldAccess tree, Symbol sym) {
	tree.sym = sym;
	tree.type = sym.asType();
	if (sym instanceof Symbol.ClassSymbol)
	    // discard the package and any outerclasses
	    return jcmake.Ident(sym);
	return tree;
    }
    
    protected Symbol.ClassSymbol resolveClass(Name name) throws ClassNotFoundException {
        Symbol sym = resolveIdent(name);
        if (sym instanceof Symbol.ClassSymbol)
            return (Symbol.ClassSymbol)sym;
        throw new ClassNotFoundException(name.toString());
    }
    
    protected Tree deblock(Tree t) {
        t = QueryOperations.deblock(t);
        return t instanceof ExpressionStatementTree ? ((ExpressionStatementTree)t).getExpression() : t;
    }
    
    protected static final <T extends JCTree> List<T> firstN(List<T> t, int n) {
	return n<=0 || t==null || t.isEmpty() ? List.<T>nil()
	    : firstN(t.tail,n-1).prepend(t.head);
    }
    
    protected boolean idMatches(JCFieldAccess t, Name fqn) {
        if (t.toString().endsWith(".INFORMATIONAL"))
            System.out.println();
        if (t.sym != null && (t.sym instanceof Symbol.ClassSymbol ? 
                                ((Symbol.ClassSymbol)t.sym).fullname :
                                Symbol.TypeSymbol.formFullName(t.sym.name, t.sym.owner)) == fqn)
            return true;
        return TreeInfo.fullName(t) == fqn;
    }
    
    /*
     * From JavacCompiler
     */
    protected Symbol.TypeSymbol resolveIdent(Name name) {
        Attr attr = Attr.instance(context);
        if (name == null || name == names.empty)
            return symtab.errSymbol;
        JCCompilationUnit toplevel =
            jcmake.TopLevel(List.<JCTree.JCAnnotation>nil(), null, List.<JCTree>nil());
        toplevel.packge = symtab.unnamedPackage;
        classes.complete(toplevel.packge);
        Scanner.Factory scannerFactory = Scanner.Factory.instance(context);
        Scanner scanner = scannerFactory.newScanner((name.toString()+"\u0000").toCharArray(),
                                                    name.length());
        Parser.Factory parserFactory = Parser.Factory.instance(context);
        Parser parser = parserFactory.newParser(scanner, false, false);
        JCTree tree = parser.qualident();
        if (tree != null && (tree instanceof JCFieldAccess || tree instanceof JCIdent)) {
	    Symbol sym = attr.attribIdent(tree, toplevel);
            return sym instanceof Symbol.TypeSymbol ? (Symbol.TypeSymbol)sym : symtab.errSymbol;
	}
        return symtab.errSymbol;
    }

    //FIXME: remove when TransformParser outputs com.sun.source.tree compatible files
    protected com.sun.tools.javac.tree.TreeMaker jcmake;
    protected com.sun.tools.javac.code.Types jctypes;
    protected JCBlock block(int flags, List<JCStatement> stats) {
	return jcmake.Block(flags, flatten(stats));
    }
    private List<JCStatement> flatten(List<JCStatement> l) {
	if(l==null || l.isEmpty()) 
            return List.<JCStatement>nil();
	List<JCStatement> tail = flatten(l.tail);
	if(l.head instanceof JCBlock) {
	    List<JCStatement> sub = flatten(((JCBlock)l.head).stats);

	    if(tail.isEmpty()) return sub;
	    findVarDef: {
		for(List<JCStatement> ss = sub; ss.nonEmpty(); ss = ss.tail)
		    if(ss.head instanceof JCVariableDecl) break findVarDef;
		return tail.prependList(sub);
	    }
	}
	if(QueryOperations.isEmpty(l.head)) return tail;
	if(l.tail==tail) return l;
	return tail.prepend(l.head);
    }

    public static <T extends Tree> List<T> slice(List<T> head, int len, List<T> tail) {
	return len<=0 || head.isEmpty() ? tail : slice(head.tail,len-1,tail).prepend(head.head);
    }

    public final boolean hasComment(Tree t) {
	TreeUtilities utils = getCompilationInfo().getTreeUtilities();
        boolean noComments = 
                t == null ||
                utils.getComments(t, true).isEmpty() ||
                utils.getComments(t, false).isEmpty();
        return !noComments;
    }
    
    public final boolean isConstant(Tree t) {
        TreePath path = trees.getPath(currentTopLevel, t);
        return ops.isConstant(path);
    }
    
    public final boolean isLiteral(Tree t) {
        TreePath path = trees.getPath(currentTopLevel, t);
        Element e = getCompilationInfo().getTrees().getElement(path);
        if (e == null)
            return false;
        if (e.asType().toString().equals("java.lang.String"))
            return true;
        return path.getLeaf() instanceof LiteralTree;
    }
    
    /**
     * Returns true if the tree represents a TypeElement, such as
     * "Object" or "java.util.String".  This method is not used for
     * actual ClassTree instances (where instanceof can be used).
     */
    public boolean isClassIdentifier(Tree t) {
        if (t == null) 
            return false;
        TreePath path = trees.getPath(currentTopLevel, t);
        Element el;
        switch (t.getKind()) {
            case IDENTIFIER:
                el = trees.getElement(path);
                break;
            case MEMBER_SELECT:
                MemberSelectTree ms = (MemberSelectTree) t;
                if(!ops.sideEffectFree(ms.getExpression()))
                    return false;
                el = trees.getElement(path);
                break;
            default:
                return false;
        }
        return el instanceof TypeElement;
    }

    /**
     * Returns true if the tree has variable declarations.  This can only be
     * true for VariableTree trees and blocks that contain them.
     * 
     * @param t the tree to inspect
     * @return true if the tree contains variable declarations
     */
    public boolean hasVariableDeclarations(Tree t) {
        if (t instanceof VariableTree)
            return true;
        if (t instanceof BlockTree)
            for (StatementTree stat : ((BlockTree)t).getStatements())
                if (stat != null && stat instanceof VariableTree)
                    return true;
	return false;
    }
    
    /**
     * Returns true if the tree is an IdentifierTree with the name "null".
     * Use isNullTree() to test for a tree instance being null.
     * 
     * @param t the tree to inspect
     * @return true if the tree defines a "null" identifier
     */
    public static boolean isNull(Tree t) {
	if(!(t instanceof LiteralTree)) 
            return false;
        return ((LiteralTree)t).getValue() == null;
    }
    
    public boolean isEmpty(Tree t) {
        return QueryOperations.isEmpty(t);
    }
    
    public boolean sideEffectFree(Tree t) {
        return QueryOperations.sideEffectFree(t);
    }
    
    /**
     * Returns true if the tree defines a static class or class member.
     * 
     * @param tree the tree to inspect
     * @return true if the tree is static
     */
    public boolean isStatic(Tree tree) {
        if (tree == null)
            return false;
        TreePath path = TreePath.getPath(currentTopLevel, tree);
        Element e = trees.getElement(path);
        if (e == null)
            return false;
        return e.getModifiers().contains(Modifier.STATIC);
    }
    
    public boolean isStatement(Tree t) {
        return t instanceof StatementTree;
    }
    
    public boolean isTrue(Tree t) {
        return QueryOperations.isTrue(t);
    }
    
    public boolean isFalse(Tree t) {
        return QueryOperations.isFalse(t);
    }
    
    public static boolean isNullTree(Tree t) {
        return t == null;
    }

    /**
     * Returns true if the specified tree either throws an exception or
     * invokes a method which is declared to throw exceptions.  No testing
     * is done to see whether a RuntimeException or Error can be thrown.
     */
    public boolean couldThrow(Tree tree) {
        // scan for a throw statement or method that throws exceptions.
        com.sun.source.util.TreeScanner<Tree,Tree> scanner = 
                new com.sun.source.util.TreeScanner<Tree,Tree>() {
            @Override
            public Tree scan(Tree tree, Tree aThrow) {
                return aThrow == null ? super.scan(tree, aThrow) : aThrow;
            }

            @Override
            public Tree visitThrow(ThrowTree tree, Tree aThrow) {
                return tree;
            }

            @Override
            public Tree visitMethodInvocation(MethodInvocationTree tree, Tree aThrow) {
                aThrow = super.visitMethodInvocation(tree, aThrow);
                if (aThrow == null) {
                    ExpressionTree method = tree.getMethodSelect();
                    TreePath path = trees.getPath(currentTopLevel, method);
                    ExecutableType type = (ExecutableType)trees.getTypeMirror(path);
                    if (type == null)
                        throw new QueryException("method not resolved: " + method);
                    else if (!type.getThrownTypes().isEmpty())
                        aThrow = tree;
                }
                return aThrow;
            }
        };
        return scanner.scan(tree, null) != null;
    }
    
    private Element getOwningMethod(Element e) {
        Element owner = e.getEnclosingElement();
        while (owner != null && !(owner instanceof ExecutableElement))
            owner = owner.getEnclosingElement();
        return owner;
    }
    
    public boolean referenced(Tree t) {
        TreePath path = trees.getPath(getCompilationInfo().getCompilationUnit(), t);
	return referenced(trees.getElement(path));
    }

    public boolean referenced(Element e) {
        Element method = getOwningMethod(e);
	return method != null ? getCharacterization(e).referenced(method) : false;
    }
    
    public boolean assigned(Element e) {
        if (e instanceof VariableElement) {
            Element method = getOwningMethod(e);
            return method != null ? getCharacterization(e).assigned(method) : false;
        }
        return false;
    }

    public boolean local(Element e) {
	return e==null || elements.isLocal(e);
    }
    
    public boolean parameter(Element e) {
        if (e instanceof VariableElement) {
            Element method = getOwningMethod(e);
            return method != null ? getCharacterization(e).parameter(method) : false;
        }
        return false;
    }

    public Element getElement(Tree tree) {
        TreePath path = TreePath.getPath(currentTopLevel, tree);
        return trees.getElement(path);
    }

    public boolean isInstance(Tree t, Element cs) {
	if(t==null || cs==null) return false;
        TreePath path = TreePath.getPath(currentTopLevel, t);
        if (path == null) return false;
	TypeMirror type = trees.getTypeMirror(path);
	return type!=null && isSubType(type, cs.asType());
    }
    
    private boolean isSubType(TypeMirror _this, TypeMirror _that) {
        if (_this instanceof NoType || _that instanceof NoType ||
            _this instanceof ExecutableType || _that instanceof ExecutableType ||
            _this instanceof NullType || _that instanceof NullType)
            return false;
        Types types = getCompilationInfo().getTypes();
	return types.isSubtype(_this, _that);
    }
    
    protected TreePath getPath(Tree t) {
        return trees.getPath(currentTopLevel, t);
    }
    
    protected Tree statement(Tree t) {
        return ops.statement(t);
    }
    
    /**
     * Returns true if two trees match.
     */
    public final boolean matches(Tree a, Tree b) {
	return matcher().matches(a,b);
    }

    /**
     * Returns true if two tree lists match.
     */
    public boolean matches(List<? extends Tree> a, List<? extends Tree> b) {
	return matcher().matches(a,b);
    }

    public final boolean matches(CharSequence a, Tree b) {
	return b instanceof IdentifierTree && ((IdentifierTree)b).getName().equals(a);
    }
    public final boolean matches(CharSequence a, CharSequence b) {
	return a.toString().equals(b.toString());
    }
    public final boolean matches(List<Tree> a, List<Tree> b, int len) {
	return len<=0     ? a.isEmpty() && b.isEmpty()
	    : a.isEmpty() ? b.isEmpty()
	    : !b.isEmpty() && matcher().matches((Tree)a, (Tree)b);
    }

    private TreeMatcher matcherObj;
    private final TreeMatcher matcher() {
	TreeMatcher t = matcherObj;
	if(t==null) matcherObj = t = new TreeMatcher();
	return t;
    }

    public boolean assignedIn(CharSequence n, List<? extends Tree> statements) {
        for (Tree t : statements)
	    if(assignedIn(n,t)) return true;
	return false;
    }
    private AssignChecker assignChecker;
    public boolean assignedIn(CharSequence n, Tree t) {
	if(assignChecker==null) assignChecker = new AssignChecker();
	return assignChecker.assignedIn(n,t);
    }

    public boolean declaredIn(CharSequence n, List<? extends Tree> statements) {
        for (Tree t : statements)
	    if(declaredIn(n,t)) return true;
	return false;
    }
    private DeclarationChecker declareChecker;
    public boolean declaredIn(CharSequence n, Tree t) {
	if(declareChecker==null) declareChecker = new DeclarationChecker();
	return declareChecker.declaredIn(n,t);
    }

    public boolean referencedIn(CharSequence n, List<? extends Tree> statements) {
        for (Tree t : statements)
	    if(referencedIn(n,t)) return true;
	return false;
    }
    private ReferenceChecker referenceChecker;
    public boolean referencedIn(CharSequence n, Tree t) {
	if(referenceChecker==null) referenceChecker = new ReferenceChecker();
	return referenceChecker.referencedIn(n,t);
    }
}
