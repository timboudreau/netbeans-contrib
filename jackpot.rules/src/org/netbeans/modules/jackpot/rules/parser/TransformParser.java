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

import com.sun.source.tree.Tree.Kind;
import org.openide.util.NbBundle;
import com.sun.tools.javac.tree.*;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.parser.*;
import com.sun.tools.javac.util.*;
import java.io.*;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.netbeans.api.jackpot.ConversionOperations;
import static com.sun.tools.javac.parser.Token.*;

public class TransformParser extends ScriptParser {
    final Name impliesToken = names.fromString("=>");
    final Name suchthatToken = names.fromString("::");
    final Name doSomethingToken = names.fromString("=:");
    final Collection<Name> newTokens = 
        Arrays.asList(impliesToken, suchthatToken, doSomethingToken);
    final Name nullName = tokenName(Token.NULL);
    final Name trueName = tokenName(Token.TRUE);
    final Name falseName = tokenName(Token.FALSE);
    String fileName;
    String javacpath;
    long fileLastModified;
    protected Position.LineMap lineMap;

    public TransformParser(Reader in, String javacpath) {
        this("Rules", in, 0L, javacpath);
    }
    public TransformParser(String scriptPath, Reader in, long lastModified, String javacpath) {
        super();
	setIn(scriptPath, in);
        fileName = scriptPath;
        fileLastModified = lastModified;
        this.javacpath = javacpath;
        lineMap = scanner.getLineMap();
	nameConstMap = new HashMap<Name,String>();
	nameConstMap.put(trueName,"names._true");
	nameConstMap.put(falseName,"names._false");
    }

    protected Context createContext() {
        Context ctx = super.createContext();
        new Keywords(ctx) {
            public Token key(Name name) {
                if (newTokens.contains(name))
                    return CUSTOM;
                return super.key(name);
            }
        };
        Factory.instance(ctx); // register subclass in context
        return ctx;
    }

    protected String baseType() { return "GeneratedMatcher"; }

    public Rule parseRules() {
	if(scanner.token() == EOF || scanner.token() == RBRACE) return null;
	if(scanner.token() == IDENTIFIER && scanner.name() == mapclassName) {
	    scanner.nextToken();
	    JCTree original = parser.qualident();
	    if(!isToken(impliesToken)) 
		logError(scanner.pos(), "no.suchthat.for.mapclass");
	    else {
		scanner.nextToken();
		JCTree rep = parser.qualident();
		parser.accept(SEMI);
		if(!hasErrors())
		    mapClass.put(original,rep);
	    }
	    return parseRules();
	}
        Rule ret = Rule.invalidRule;
        int rulePos = scanner.pos();
        JCTree pat = javaFragment();
        if (pat instanceof JCErroneous) {
            // skip to next rule on error
            do {
                scanner.nextToken();
            } while (scanner.token() != EOF && scanner.token() != SEMI);
            scanner.nextToken();
        } 
        else {
            JCTree replacement = null;
            JCExpression suchthat = null;
            while(true)
                if(isToken(impliesToken)) {
                    scanner.nextToken();
                    if (scanner.token() == ASSERT) {
                        scanner.nextToken();
                        JCExpression assertion = expression();
                        JCExpression message = null;
                        if (scanner.token() == COLON) {
                            scanner.nextToken();
                            message = expression();
                        }
                        replacement = make.Assert(assertion, message);
                        parser.accept(SEMI);
                    }
                    else
                        replacement = javaFragment();
                    if(scanner.token() == IDENTIFIER && scanner.name() == inlineName) {
                        if(replacement instanceof JCBlock)
                            ((JCBlock)replacement).flags |= Flags.BLOCK;
                        else logError(replacement.pos, "illegal.inline");
                        scanner.nextToken();
                    }
                    if(!isToken(suchthatToken) && !isToken(doSomethingToken)) break;
                }
                else if(isToken(suchthatToken))
                    while(true) {
                        scanner.nextToken();
                        JCExpression t = expression();
                        suchthat = suchthat==null ? t : make.Binary(JCTree.AND, suchthat, t);
                        if(scanner.token() != COMMA) break;
                    }
                else if(scanner.token() == SEMI) {
                    scanner.nextToken();
                    break;
                }
                else {
                    if (suchthat == null)
                        logError(rulePos, "no.suchthat", pat);
                    else
                        logError(rulePos, "bad.syntax");
                    break;
                }
            if (!hasErrors())
                ret = new Rule(pat,replacement,suchthat,lineMap.getLineNumber(rulePos));
        }
        if( scanner.token() != EOF && scanner.token() != RBRACE)
            ret.next = parseRules();
        return rules = ret;
    }

    private JCTree javaFragment() {
        JCTree pat;
        switch (scanner.token()) {
	    case LBRACE: case IF: case FOR: case WHILE: case DO: case TRY:
	    case SWITCH: case SYNCHRONIZED: case RETURN: case THROW: case BREAK:
	    case CONTINUE: case SEMI: case ELSE: case FINALLY: case CATCH:
                pat = statement();
                break;
            default:
                pat = expression();
                break;
        }
        if (scanner.token() != SEMI &&
                (pat instanceof JCIdent || pat instanceof JCFieldAccess) && 
                scanner.token() == IDENTIFIER && scanner.name() != inlineName) {
            JCModifiers mods = make.at(Position.NOPOS).Modifiers(0);
            ListBuffer<JCStatement> stats = 
                    parser.variableDeclarators(mods, (JCExpression)pat, new ListBuffer<JCStatement>());
            assert stats.length() == 1;
            pat = stats.first();
        }
        if(scanner.token() == SEMI)
            scanner.nextToken();
        return pat;
    }
    
    private void logError(int pos, String key, Object... args) {
	String format = NbBundle.getBundle(TransformParser.class).getString("TransformParser." + key);
	String msg = MessageFormat.format(format, args);
	log.rawError(pos, msg);
    }
    public boolean hasErrors() {
	return super.hasErrors() || pc!=null && pc.hasErrors();
    }
    public String getErrors() {
	return super.hasErrors() ? super.getErrors()
	    : pc!=null ? pc.getErrors()
	    : null;
    }
    public boolean hasRules() {
	return rules != null || mapClass.size() > 0;
    }
    
    private JCTree deblock(JCTree t) {
        t = (JCTree)ConversionOperations.deblock(t);
        return t instanceof JCExpressionStatement ? ((JCExpressionStatement)t).expr : t;
    }

    private static final boolean ENABLE_CACHE = false;
    
    public Rule rules;
    protected PluginCompiler pc;
    public Class codeRules() throws IOException {
	if(!hasRules()) return null;
	rootHead = null;
	rootTail = null;
	pc = new PluginCompiler();
	if(pc.needsGeneration(fileName, fileLastModified, !ENABLE_CACHE)) {
	    pc.startGeneration();
            pc.write("import com.sun.source.tree.*;\n");
	    pc.write("import com.sun.tools.javac.code.*;\n"); 
	    pc.write("import com.sun.tools.javac.tree.JCTree;\n"); 
	    pc.write("import com.sun.tools.javac.tree.JCTree.*;\n"); 
            pc.write("import com.sun.tools.javac.tree.TreeInfo;\n");
	    pc.write("import com.sun.tools.javac.util.*;\n"); 
            pc.write("import javax.lang.model.element.*;\n");
	    pc.write("import org.netbeans.api.jackpot.*;\n");
            pc.write("import org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher;\n\n");
	    pc.write("public class ");
	    pc.writeClassName();
	    pc.write(" extends ");
	    pc.write(baseType());
	    pc.write(" {\n\n    public Tree rewrite(Tree t) {\n");
	    for(Rule r = rules; r!=null; r = r.next) {
		metaslot = 0;
		tempslot = 0;
		head = null;
		tail = null;
		genMatch("t", r.pattern);
		genSuchThat(r.suchthat, false);
		addReplacement(r);
		if(head!=null) {
		    if(rootHead==null) rootHead = head;
		    else rootTail.ifFail = head;
		    rootTail = head;
		}
	    }
	    if(rootHead!=null) {
		rootHead = optimize(rootHead);
		rootTail.ifFail = new Guard("", 0, Guard.DECLARATION);
		rootHead.writeAll(pc,0);
	    }
            pc.write("\treturn t;\n");
	    pc.write("    }\n");
	    dumpNameConsts(pc);
	    pc.write("}\n");
            pc.getWriter().flush();
	    if(hasErrors())
		return null;
	}
	return pc.loadClass(javacpath);
    }
    
    Guard rootHead, rootTail;
    Guard head, tail;
    int metaslot = 0;
    int tempslot = 0;
    Name[] metakeys = new Name[20];
    boolean[] isName = new boolean[20];
    int[] isList = new int[20];
    String[] metavalues = new String[20];

    Constructor constructor = makeConstructor();
    protected Constructor makeConstructor() { return new RewriteConstructor(); }
    public class Constructor extends Visitor {
	public void generate(JCTree t) {
	    try {
		if(t==null) pc.write("null");
		else if(t.getTag()==JCTree.IDENT) {
		    t.accept(this);
		} else {
		    pc.write("rewrite0(");
		    t.accept(this);
		    pc.write(")");
		}
	    } catch(IOException ioe) {}
	}
	public void generate(long l) throws IOException {
	    pc.write(l);
	    if(l>(1L<<31)-1 || l<-(1L<<31)) pc.write('L');
	}
	public <T extends JCTree> void generate(List<T> t) {
	    generate(t,false);
	}
	public <T extends JCTree> void generate(List<T> t, boolean statements) {
	    generate(t, statements, statements ? "JCStatement" : "JCTree");
	}
	public <T extends JCTree> void generate2(List<T> t, boolean statements, String listType) {
	    generate(t,statements,listType);
	}
	public <T extends JCTree> void generate(List<T> t, boolean statements, String listType) {
	    try {
		if(t==null) pc.write("null");
		else if(t.isEmpty()) {
		    pc.write("List.<");
		    pc.write(listType);
                    pc.write(">nil()");
		}
		else {
		    JCTree head = deblock(t.head);
		    if(head.getTag()==JCTree.IDENT) {
			Name id = ((JCIdent)head).name;
			for(int i = metavars.length; --i>=0; )
			    if(id==metavars[i]){
				if(isList[i]==0) pc.write(metavals[i]);
				else if(isList[i]>0) {
				    pc.write("slice(");
				    pc.write(metavals[i]);
				    pc.write(", len_");
				    pc.write(i);
				    pc.write(",");
				    generate2(t.tail,statements,listType);
				    pc.write(")");
				} else break;
				return;
			    }
		    }
		    generate2(t.tail,statements,listType);
		    pc.write(".prepend(");
		    if("JCTree"!=listType) {
			pc.write('(');
			pc.write(listType);
			pc.write(')');
		    }
		    if(statements) pc.write("statement(");
		    generate(head);
		    if(statements) pc.write(")");
		    pc.write(")");
		}
	    } catch(IOException ioe) {}
	}
	PluginCompiler pc;
	protected Name[] metavars;
	protected String[] metavals;
	boolean[] isName;
	int[] isList;
    }
    class RewriteConstructor extends Constructor {
	public void generate(Name n) throws IOException {
	    if(n==null) pc.write("null");
	    else {
		pc.write("jcmake.Ident(");
		pc.write(nameConst(n));
		pc.write(")");
	    }
	}
	public void generateName(Name n) throws IOException {
	    if(n==null) pc.write("null");
	    else
		pc.write(nameConst(n));
	}

	public void visitTopLevel(JCCompilationUnit that) { visitTree(that); }
	public void visitImport(JCImport that) { 
	    try {
		pc.write("jcmake.Import(");
		generate(that.qualid);
		pc.write(",");
		pc.write(litName(that.staticImport));
		pc.write(')');
	    } catch(IOException ioe) {}
        }
	public void visitClassDef(JCClassDecl def) {
	    try {
	    	if(def==null) pc.write("null");
		else {
		    pc.write("\n\tjcmake.ClassDef((JCModifiers)");
		    generate(def.mods);
		    pc.write(' ');
		    generateName(def.name);
		    pc.write(",\n\t\t");
		    generate(def.typarams,false,"JCTypeParameter");
		    pc.write(",\n\t\t");
		    generate(def.extending);
		    pc.write(",\n\t\t");
		    generate(def.implementing,false,"JCExpression");
		    pc.write(",\n\t\t");
		    generate(def.defs);
		    pc.write(')');
		}
	    } catch(IOException ioe) {}
	}
	public void visitMethodDef(JCMethodDecl def) {
	    try {
	    	if(def==null) pc.write("null");
		else {
		    pc.write("\n\t\tjcmake.MethodDef((JCModifiers)");
		    generate(def.mods);
		    pc.write(' ');
		    generateName(def.name);
		    pc.write(",\n\t\t\t(JCExpression)");
		    generate(def.restype);
		    pc.write(",\n\t\t\t");
		    generate(def.typarams,false,"JCTypeParameter");
		    pc.write(",\n\t\t\t");
		    generate(def.params,false,"JCVariableDecl");
		    pc.write(",\n\t\t\t");
		    generate(def.thrown,false,"JCExpression");
		    pc.write(",\n\t\t\tblock(");
		    generate(def.body);
                    pc.write(",(JCExpression)");
                    generate(def.defaultValue);
		    pc.write("))");
		}
	    } catch(IOException ioe) {}
	}
	public void visitSkip(JCSkip that) {
	    try {
		pc.write("jcmake.Skip()");
	    } catch(IOException ioe) {}
	}
	public void visitBlock(JCBlock that) {
	    try {
		pc.write("block(");
		pc.write(that.flags);
		pc.write(",");
		generate(that.stats, true);
		pc.write(")");
	    } catch(IOException ioe) {}
	}
	public void visitDoLoop(JCDoWhileLoop that) { 
	    try {
		pc.write("jcmake.DoLoop((JCStatement)");
		generate(that.body);
		pc.write(",(JCExpression)");
		generate(that.cond);
		pc.write(')');
	    } catch(IOException ioe) {}
        }
	public void visitWhileLoop(JCWhileLoop that) { 
	    try {
		pc.write("jcmake.WhileLoop((JCExpression)");
		generate(that.cond);
		pc.write(",(JCStatement)");
		generate(that.body);
		pc.write(')');
	    } catch(IOException ioe) {}
        }
	public void visitForLoop(JCForLoop that) { 
	    try {
		pc.write("jcmake.ForLoop((JCExpression)");
		generate(that.init, true);
		pc.write(",(JCExpression)");
		generate(that.cond);
                pc.write(",(JCStatement)");
                generate(that.body);
		pc.write(')');
	    } catch(IOException ioe) {}
        }
        public void visitForeachLoop(JCEnhancedForLoop that) { 
	    try {
		pc.write("jcmake.ForeachLoop((JCVariableDecl)");
		generate(that.var);
		pc.write(",(JCExpression)");
		generate(that.expr);
                pc.write(",(JCStatement)");
                generate(that.body);
		pc.write(')');
	    } catch(IOException ioe) {}
        }
	public void visitLabelled(JCLabeledStatement that) { 
	    try {
		pc.write("jcmake.Labelled(");
		referenceName(that.label, true);
                pc.write(",(JCStatement)");
                generate(that.body);
		pc.write(')');
	    } catch(IOException ioe) {}
        }
	public void visitSwitch(JCSwitch that) { 
	    try {
		pc.write("jcmake.Switch((JCExpression)");
		generate(that.selector);
                pc.write(",");
                generate(that.cases, false, "JCCase");
		pc.write(')');
	    } catch(IOException ioe) {}
        }
	public void visitCase(JCCase that) { 
	    try {
		pc.write("jcmake.Switch((JCExpression)");
		generate(that.pat);
                pc.write(",");
                generate(that.stats, true, "JCStatement");
		pc.write(')');
	    } catch(IOException ioe) {}
        }
	public void visitSynchronized(JCSynchronized that) { 
	    try {
		pc.write("jcmake.Synchronized((JCExpression)");
		generate(that.lock);
                pc.write(",(JCBlock)");
                generate(that.body);
		pc.write(')');
	    } catch(IOException ioe) {}
        }
	public void visitTry(JCTry that) { 
	    try {
		pc.write("jcmake.Try((JCBlock)");
                generate(that.body);
                pc.write(",");
                generate(that.catchers, false, "JCCatch");
                pc.write(",(JCBlock)");
                generate(that.finalizer);
		pc.write(')');
	    } catch(IOException ioe) {}
        }
	public void visitCatch(JCCatch that) { 
	    try {
		pc.write("jcmake.Catch((JCVariableDecl)");
		generate(that.param);
                pc.write(",(JCBlock)");
                generate(that.body);
		pc.write(')');
	    } catch(IOException ioe) {}
        }
	public void visitTypeCast(JCTypeCast that) {
	    try {
		pc.write("jcmake.TypeCast(");
		generate(that.clazz);
		pc.write(",(JCExpression)");
		generate(that.expr);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitTypeTest(JCInstanceOf that) {
	    try {
		pc.write("jcmake.TypeTest((JCExpression)");
		generate(that.expr);
		pc.write(',');
		generate(that.clazz);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitConditional(JCConditional that) {
	    try {
		pc.write("jcmake.Conditional((JCExpression)");
		generate(that.cond);
		pc.write(",(JCExpression)");
		generate(that.truepart);
		pc.write(",(JCExpression)");
		generate(that.falsepart);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitIf(JCIf that) {
	    try {
		pc.write("jcmake.If(");
		generate(that.cond);
		pc.write(",(JCStatement)");
		generate(that.thenpart);
		pc.write(",(JCStatement)");
		generate(that.elsepart);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitApply(JCMethodInvocation that) {
	    try {
		pc.write("jcmake.Apply(");
                generate(that.typeargs,false,"JCExpression");
                pc.write(",(JCExpression)");
		generate(that.meth);
		pc.write(',');
		generate(that.args,false,"JCExpression");
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitExec(JCExpressionStatement that) {
	    try {
		pc.write("jcmake.Exec((JCExpression)");
		generate(that.expr);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitBreak(JCBreak that) { 
	    try {
		pc.write("jcmake.Break(");
		referenceName(that.label, true);
		pc.write(")");
	    } catch(IOException ioe) {}
        }
	public void visitContinue(JCContinue that) { 
	    try {
		pc.write("jcmake.Continue(");
		referenceName(that.label, true);
		pc.write(")");
	    } catch(IOException ioe) {}
        }
	public void visitReturn(JCReturn that) {
	    try {
		pc.write("jcmake.Return((JCExpression)");
		generate(that.expr);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitThrow(JCThrow that) { 
	    try {
                pc.write("jcmake.Throw(");
                generate(that.expr);
		pc.write(')');
	    } catch(IOException ioe) {}
        }
	public void visitAssert(JCAssert that) {
	    try {
		pc.write("jcmake.Assert((JCJCExpression)");
		generate(that.cond);
		pc.write(",(JCExpression)");
		generate(that.detail);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitNewClass(JCNewClass that) {
	    try {
		pc.write("jcmake.NewClass((JCExpression)");
		generate(that.encl);
		pc.write(',');
                generate(that.typeargs,false,"JCExpression");
		pc.write(",(JCExpression)");
                generate(that.clazz);
		pc.write(',');
		generate(that.args,false,"JCExpression");
		pc.write(",(JCClassDecl)");
		generate(that.def);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitNewArray(JCNewArray that) {
	    try {
		pc.write("jcmake.NewArray((JCExpression)");
		generate(that.elemtype);
		pc.write(',');
		generate(that.dims,false,"JCExpression");
		pc.write(',');
		generate(that.elems,false,"JCExpression");
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitParens(JCParens that) { 
            that.expr.accept(this); 
        }
	public void visitAssign(JCAssign that) {
	    try {
		pc.write("jcmake.Assign((JCExpression)");
		generate(that.lhs);
		pc.write(",(JCExpression)");
		generate(that.rhs);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitAssignop(JCAssignOp that) {
	    try {
		pc.write("jcmake.AssignOp(");
		pc.write(that.getTag());
		pc.write(',');
		generate(that.lhs);
		pc.write(',');
		generate(that.rhs);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitUnary(JCUnary that) {
	    try {
		pc.write("jcmake.Unary(");
		pc.write(that.getTag());
		pc.write(",(JCExpression)");
		generate(that.arg);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitBinary(JCBinary that) {
	    try {
		pc.write("jcmake.Binary(");
		pc.write(that.getTag());
		pc.write(",(JCExpression)");
		generate(that.lhs);
		pc.write(",(JCExpression)");
		generate(that.rhs);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitIndexed(JCArrayAccess that) {
	    try {
		pc.write("jcmake.Indexed(");
		generate(that.indexed);
		pc.write(",(JCExpression)");
		generate(that.index);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitSelect(JCFieldAccess that) {
	    try {
		pc.write("resolve(jcmake.Select((JCExpression)");
		generate(that.selected);
		pc.write(',');
		referenceName(that.name,true);
		pc.write("))");
	    } catch(IOException ioe) {}
	}
	private void referenceName(Name id, boolean asName) {
	    try {
		for(int i = metavars.length; --i>=0; )
		    if(id==metavars[i]) {
			if(isName[i]==asName) pc.write(metavals[i]);
			else if(asName) {
			    pc.write("TreeInfo.name(");
			    pc.write(metavals[i]);
			    pc.write(")");
			} else {
			    pc.write("jcmake.Ident(");
			    pc.write(metavals[i]);
			    pc.write(")");
			}
			return;
		    }
		if(asName) pc.write(nameConst(id));
		else generate(id);
	    } catch(IOException ioe) {}
	}
	public void visitIdent(JCIdent that) {
	    referenceName(that.name, false);
	}
	public void visitVarDef(JCVariableDecl that) {
	    try {
		pc.write("jcmake.VarDef((JCModifiers)");
		generate(that.mods);
		pc.write(",");
		referenceName(that.name, true);
		pc.write(",(JCExpression)");
		generate(that.vartype);
		pc.write(",(JCExpression)");
		generate(that.init);
		pc.write(")");
	    } catch(IOException ioe) {}
	}
	public void visitLiteral(JCLiteral that) {
	    try {
		pc.write("jcmake.Literal(");
		pc.write(that.typetag);
		pc.write(", ");
		if(that.value instanceof String)
		    pc.writeQuoted((String)that.value);
                else
		    pc.write(litName(that.value));
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitTypeIdent(JCPrimitiveTypeTree that) {
	    try {
		pc.write("jcmake.TypeIdent(");
		generate(that.typetag);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitTypeArray(JCArrayTypeTree that) {
	    try {
		pc.write("jcmake.TypeArray((JCExpression)");
		generate(that.elemtype);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitTypeApply(JCTypeApply that) { 
	    try {
		pc.write("jcmake.TypeApply((JCExpression)");
		generate(that.clazz);
                pc.write(", ");
                generate(that.arguments);
		pc.write(')');
	    } catch(IOException ioe) {}
	}
	public void visitTypeParameter(JCTypeParameter that) {
	    try {
		pc.write("jcmake.TypeParameter(");
		generate(that.name);
                pc.write(", ");
                generate(that.bounds,false,"JCExpression");
		pc.write(')');
	    } catch(IOException ioe) {}
	}
        public void visitWildcard(JCWildcard that) { 
	    try {
		pc.write("jcmake.Wildcard(");
                pc.write("jcmake.TypeBoundKind(BoundKind.");
                pc.write(that.kind.toString());
                pc.write("), ");
                generate(that.inner);
		pc.write(')');
	    } catch(IOException ioe) {}
        }
        public void visitAnnotation(JCAnnotation that) {
            try {
                pc.write("jcmake.Annotation(");
                generate(that.annotationType);
                pc.write(", ");
                generate(that.args);
                pc.write(')');
            } catch(IOException ioe) {}
        }
        public void visitModifiers(JCModifiers that) {
            try {
                pc.write("jcmake.Modifiers(");
                generate(that.flags);
                pc.write(", ");
                generate(that.annotations, false, "JCAnnotation");
                pc.write(')');
            } catch(IOException ioe) {}
        }
	public void visitErroneous(JCErroneous that) { visitTree(that); }

	public void visitTree(JCTree that) {
	    try {
		pc.write("/* can't generate code for "+that.getClass().getSimpleName()+"\n\t"+that+" */");
	    } catch(IOException ioe) {}
	}
    }

    final HashMap<JCTree,JCTree> mapClass = new HashMap<JCTree,JCTree>();
    void dumpMapclass() throws IOException {
	if(mapClass.size()<=0) return;
	pc.write("    public Symbol replacesymbol(Symbol s) {\n");
	for(Iterator<JCTree> in = mapClass.keySet().iterator(); in.hasNext(); ) {
	    JCTree t0 = in.next();
	    JCTree t1 = mapClass.get(t0);
	    pc.write("\tif(s==");
	    pc.write(classSymConst(TreeInfo.fullName(t0)));
	    pc.write(") {\n\t\tcomment = \"");
	    pc.write(TreeInfo.name(t0).toString());
	    pc.write("=>");
	    pc.write(TreeInfo.name(t1).toString());
	    pc.write("\";\n\t\treturn ");
	    pc.write(classSymConst(TreeInfo.fullName(t1)));
	    pc.write(";\n\t}\n");
	}
	pc.write("\treturn s;\n    }\n");
    }

    final HashMap<Name,String> nameConstMap;
    String nameConst(Name n) {
	if(n==null) return "null";
	String s = nameConstMap.get(n);
	if(s==null) {
	    s = ("nameConstant_"+n).replace('.','_');
	    nameConstMap.put(n,s);
	}
	return s;
    }
    
    final HashMap<Name,String> classSymConstMap = new HashMap<Name,String>();
    String classSymConst(Name n) {
	if(n==null) return "null";
	String s = classSymConstMap.get(n);
	if(s==null) {
	    s = ("classSymConstant_"+n).replace('.','_');
	    classSymConstMap.put(n,s);
	    nameConst(n);
	}
	return s;
    }
    
    private final HashMap<Object,String> litNameMap = new HashMap<Object,String>();
    private int litIndex = 1;
    String litName(Object o) {
	if(o==null) return "null";
	String s = litNameMap.get(o);
	if(s==null) {
	    s = "litConstant_"+litIndex++;
	    litNameMap.put(o,s);
	}
	return s;
    }

    public void dumpNameConsts(PluginCompiler pc) throws IOException {
	dumpMapclass();
	for(Iterator<Name> in = nameConstMap.keySet().iterator(); in.hasNext(); ) {
	    Name n = in.next();
	    String s = nameConstMap.get(n);
	    if(!s.startsWith("names.")) {
		pc.write("    private com.sun.tools.javac.util.Name ");
		pc.write(s);
		pc.write(";\n");
	    }
	}
	for(Iterator<String> in = classMap.keySet().iterator(); in.hasNext(); ) {
	    pc.write("    private ");
	    String key = in.next();
	    String var = classMap.get(key);
	    int sep = key.indexOf(':');
	    assert(sep>=0);
	    String jar = key.substring(0,sep);
	    String clazz = key.substring(sep+1);
	    pc.write(clazz);
	    pc.write(' ');
	    pc.write(var);
	    pc.write(" = (");
	    pc.write(clazz);
	    pc.write(") findClass(\"");
	    pc.write(jar);
	    pc.write("\",\"");
	    pc.write(clazz);
	    pc.write("\");\n");
	}
	for(Iterator<Name> in = classSymConstMap.keySet().iterator(); in.hasNext(); ) {
	    Name n = in.next();
	    String s = classSymConstMap.get(n);
	    pc.write("    private Symbol.ClassSymbol ");
	    pc.write(s);
	    pc.write(";\n");
	}
	for(Iterator<Object> io = litNameMap.keySet().iterator(); io.hasNext(); ) {
	    Object o = io.next();
	    pc.write("    private static final Object ");
	    pc.write(litNameMap.get(o));
	    pc.write(" = new ");
	    pc.write(o.getClass().getName());
	    pc.write("(");
	    pc.write(o.toString());
	    pc.write(");\n");
	}
	pc.write("    protected void initializeKeywords() throws ClassNotFoundException {\n");
	for(Iterator<Name> in = nameConstMap.keySet().iterator(); in.hasNext(); ) {
	    Name n = in.next();
	    String s = nameConstMap.get(n);
	    if(!s.startsWith("names.")) {
		pc.write("\t");
		pc.write(s);
		pc.write(" = names.fromString(");
		pc.writeQuoted(n.toString());
		pc.write(");\n");
                pc.write("\tif("+s+"==null)\n\t    System.err.println(\"NULL name "+s+"\");\n");
	    }
	}
	for(Iterator<Name> in = classSymConstMap.keySet().iterator(); in.hasNext(); ) {
	    Name n = in.next();
	    String s = classSymConstMap.get(n);
	    pc.write("\t");
	    pc.write(s);
	    pc.write(" = resolveClass(");
	    pc.write(nameConst(n));
	    pc.write(");\n");
            pc.write("\tif("+s+"==null)\n\t    System.err.println(\"NULL sym "+s+"\");\n");
	}
	pc.write("    }\n");
    }

    private void genSuchThat(JCTree t, boolean invert) throws IOException {
	if(t==null) return;
	switch(t.getTag()) {
	default: 
	    if(funcNames[t.getTag()]!=null && !invert) {
		addKnown(t);
		addBoolean(knownValue(t)+"==Boolean.TRUE");
	    } else
	    	addBoolean(genSuchThatTerm(t,invert));
	    break;
	case JCTree.AND:
	    if(invert) {
		addBoolean(genSuchThatTerm(t,invert));
	    } else {
		genSuchThat(((JCBinary)t).lhs, false);
		genSuchThat(((JCBinary)t).rhs, false);
	    }
	    break;
	case JCTree.OR:
	    if(!invert) {
		addBoolean(genSuchThatTerm(t,invert));
	    } else {
		genSuchThat(((JCBinary)t).lhs, true);
		genSuchThat(((JCBinary)t).rhs, true);
	    }
	    break;
	case JCTree.NOT:
	    genSuchThat(((JCUnary)t).arg, !invert);
	    break;
	case JCTree.PARENS:
	    genSuchThat(((JCParens)t).expr, invert);
	    break;
	}
    }
    private String genSuchThatTerm(JCTree t,boolean invert) {
	String result = "true";
	if(t!=null)
	switch(t.getTag()) {
	case JCTree.NOT:
	    return genSuchThatTerm(((JCUnary)t).arg,!invert);
	case JCTree.PARENS:
	    return genSuchThatTerm(((JCParens)t).expr, invert);
	case JCTree.AND:
	    return "("+genSuchThatTerm(((JCBinary)t).lhs,invert)
		    +(invert?"||":"&&")
		    +genSuchThatTerm(((JCBinary)t).rhs,invert)+")";
	case JCTree.OR:
	    return "("+genSuchThatTerm(((JCBinary)t).lhs,invert)
		    +(invert?"&&":"||")
		    +genSuchThatTerm(((JCBinary)t).rhs,invert)+")";
	case JCTree.TYPETEST: // instanceof
	    {
		JCInstanceOf tt = (JCInstanceOf) t;
                result = "isInstance(" + metaValue(tt.expr) + ", ";
                if (isMetaValue(tt.clazz))
                    result += "getElement(" + metaValue(tt.clazz) + "))";
                else 
                    // tt.expr and tt.clazz
                    result += classSymConst(TreeInfo.fullName(tt.clazz)) + ")";
	    }
	    break;
	default:
	    if(funcNames[t.getTag()]!=null) return genKnown(t, invert);
	    logError(t.pos, "illegal.guard", t);
	    break;
	case JCTree.APPLY:
	    JCMethodInvocation mcall = (JCMethodInvocation) t;
	    try {
		if(mcall.meth.getTag()==JCTree.IDENT) {
		    Name nm = ((JCIdent)mcall.meth).name;
		    if(nm==assignedInName) {
			checkLen(mcall.args,2);
			result = "assignedIn("+arg(mcall.args,0)+","+arg(mcall.args,1)+")";
			break;
		    }
                    else if(nm==declaredInName) {
			checkLen(mcall.args,2);
			result = "declaredIn("+arg(mcall.args,0)+","+arg(mcall.args,1)+")";
			break;
		    }
                    else if(nm==referencedInName) {
			checkLen(mcall.args,2);
			result = "referencedIn("+arg(mcall.args,0)+","+arg(mcall.args,1)+")";
			break;
		    }
		    Method m = methodRegistry.get(nm.toString());
		    if(m!=null) {
			checkLen(mcall.args,1);
			result = m.genInvoke(this,arg(mcall.args,0));
			break;
		    }
		}
		logError(t.pos, "illegal.guard", t);
	    } catch(ArgError ae) {
		ae.printStackTrace();
		logError(t.pos, "illegal.argument.in.guard", ae.getMessage(), t);
	    }
	    break;
	case JCTree.IDENT:
	    Name idn = ((JCIdent)t).name;
	    if(idn==statementcontextName)
		result="isStatement(parent())";
	    else
		logError(t.pos, "illegal.guard", t);
	    break;
	}
	if(invert) result = "!"+result;
	return result;
    }
    private String genKnown(JCTree t, boolean invert) {
	return (invert ? "!(" : "(") + isKnown(t) +
		knownValue(t)+"==Boolean.TRUE)";
    }
    private String isKnown(JCTree t) {
	if(t==null) return "";
	if(funcNames[t.getTag()]!=null) 
	    return isKnown(((JCBinary)t).lhs)+isKnown(((JCBinary)t).rhs);
	if(t instanceof JCLiteral)
	    return "";
	if(t instanceof JCIdent) {
	    String s = metaValue(t);
	    if(s!=null) return "constant("+s+") && ";
	}
	logError(t.pos, "guaranteed.unknown", t);
	return "false &&";
    }
    private void addKnown(JCTree t) {
	if(t==null) return;
	int tag = t.getTag();
	if(funcNames[tag] != null) {
	    addKnown(((JCBinary)t).lhs);
	    addKnown(((JCBinary)t).rhs);
	    return;
	}
	if(tag==JCTree.LITERAL) return;
	if(tag==JCTree.IDENT) {
	    addBoolean("constant("+metaValue(t)+")");
	    return;
	}
	logError(t.pos, "unhandled.op", t);
	addBoolean("false");
    }
    private static final String[] funcNames = new String[JCTree.MOD_ASG+1];
    static {
	funcNames[JCTree.BITOR] = "opBITOR";
	funcNames[JCTree.BITXOR] = "opBITXOR";
	funcNames[JCTree.BITAND] = "opBITAND";
	funcNames[JCTree.SL] = "opSL";
	funcNames[JCTree.SR] = "opSR";
	funcNames[JCTree.USR] = "opUSR";
	funcNames[JCTree.PLUS] = "opPLUS";
	funcNames[JCTree.MINUS] = "opMINUS";
	funcNames[JCTree.MUL] = "opMUL";
	funcNames[JCTree.DIV] = "opDIV";
	funcNames[JCTree.MOD] = "opMOD";
	funcNames[JCTree.EQ] = "opEQ";
	funcNames[JCTree.NE] = "opNE";
	funcNames[JCTree.LE] = "opLE";
	funcNames[JCTree.GE] = "opGE";
	funcNames[JCTree.LT] = "opLT";
	funcNames[JCTree.GT] = "opGT";
    }
    
    private String knownValue(JCTree t) {
	if(t==null) return "intZero";
	if(funcNames[t.getTag()]!=null) 
	    return funcNames[t.getTag()]+"("+knownValue(((JCBinary)t).lhs)+","+knownValue(((JCBinary)t).rhs)+")";
	if(t instanceof JCLiteral) {
	    Object o = ((JCLiteral)t).value;
	    if(o instanceof String) {
		StringBuffer sb = new StringBuffer();
		String s = o.toString();
		int limit = s.length();
		sb.append('"');
		for(int i = 0; i<limit; i++) {
		    char c = s.charAt(i);
		    if(c<0177 && c>=040 && c!='"' && c!='\\') sb.append(c);
		    else if(c<=0377) {
			sb.append('\\');
			sb.append('0'+((c>>6)&7));
			sb.append('0'+((c>>3)&7));
			sb.append('0'+((c   )&7));
		    } else {
			sb.append('\\');
			sb.append('u');
			sb.append(Character.forDigit((c>>12)&15,16));
			sb.append(Character.forDigit((c>> 8)&15,16));
			sb.append(Character.forDigit((c>> 4)&15,16));
			sb.append(Character.forDigit((c    )&15,16));
		    }
		}
		sb.append('"');
		o = sb;
		return o.toString();
	    }
	    return litName(o);
	}
	if(t instanceof JCIdent)
	    return "valueOf("+metaValue(t)+")";
	return "0";
    }
    public static class ArgError extends Exception {
	public ArgError(String s) { super(s); }
    }
    private void checkLen(List<? extends JCTree> t, int len) throws ArgError {
	while(len>0) {
	    if(t==null || t.isEmpty()) throw new ArgError(len+" too few arguments");
	    t = t.tail;
	    len--;
	}
	if(!t.isEmpty()) throw new ArgError("too many arguments.  Excess: "+t.head);
    }
    private String arg(List<? extends JCTree> l, int index) throws ArgError {
	while(index>0) {
	    if(l==null || l.isEmpty()) throw new ArgError("Missing argument");
	    l = l.tail;
	    index--;
	}
	if(l==null || l.isEmpty()) throw new ArgError("Missing argument");
	return arg(l.head);
    }
    private String arg(JCTree t) throws ArgError {
	if(t==null) return "null";
	switch(t.getTag()) {
	    case JCTree.IDENT: {
		Name idname = ((JCIdent)t).name;
		for(int i = metaslot; --i>=0; )
		    if(idname==metakeys[i]) {
			String ret = metavalues[i];
			if(isList[i]>0)
			    ret = "firstN("+ret+",len_"+i+")";
			return ret;
		    }
		return idname.toString();
	    }
	    case JCTree.SELECT: {
		JCFieldAccess sel = (JCFieldAccess) t;
		Name idname = sel.name;
		String base = arg(sel.selected);
	    	if(idname==parentName) {
		    int ix = base.lastIndexOf('.');
		    logger.fine("SEL "+t+"  "+base+"  ix="+ix);
		    if(ix>0) return base.substring(0,ix);
		}
		return base+"."+idname;
	    }
	}
	throw new ArgError("Unacceptable argument: "+t);
		
    }
    String metaValue(JCTree t) {
        int idx = metaValueIndex(t);
	if(idx == -1) {
            logError(t==null?0:t.pos, "metavar.expected", t);
            return "error";
        }
        return metavalues[idx];
    }
    int metaValueIndex(JCTree t) {
	if(t instanceof JCIdent) {
	    Name idname = ((JCIdent)t).name;
	    for(int i = metaslot; --i>=0; )
		if(idname==metakeys[i])
		    return i;
	}
	return -1;
    }
    boolean isMetaValue(JCTree t) {
        return metaValueIndex(t) > -1;
    }
    void addGuard(String t, long v, int k) {
	for(Guard g = head; g!=null; g = g.ifSucceed)
	    if(g.test.equals(t) && g.value==v && g.kind==k)
		return; // eliminate duplicate
	Guard n = new Guard(t, v, k);
	if(head==null) head = n;
	else tail.ifSucceed = n;
	tail = n;
    }
    void addBoolean(String s) {
	addGuard(s, 0, Guard.BOOLEAN);
    }
    void addLoop(String val, int mv) {
	addGuard(val, mv, Guard.LOOP);
    }
    void addInt(String s, long v) {
	addGuard(s, v, Guard.INT);
    }
    static final String pfx = "com.sun.tools.javac.tree.JCTree.";
    static final int pfxl = pfx.length();
    void addDeclaration(String type, String vname, String value) {
	if(type.startsWith(pfx)) type = type.substring(pfxl);
	addGuard(type+" "+vname+" = ("+type+") ("+value+")", 0, Guard.DECLARATION);
    }
    void addReplacement(Rule r) {
	Guard n = new Replacement(r, metaslot, metakeys, metavalues, isName, isList);
	if(head==null) head = n;
	else tail.ifSucceed = n;
	tail = n;
    }
    private <T extends JCTree> void genMatch(String prefix, List<T> t) throws IOException {
	genMatch(prefix, t, false);
    }
    private <T extends JCTree> void genMatch(String prefix, List<T> t, boolean mightBeNull) throws IOException {
	if(t==null) {
	    addBoolean(prefix+"==null || "+prefix+".isEmpty()");
	    return;
	}
	if(mightBeNull) addBoolean(prefix+"!=null");
	for( ; t.nonEmpty(); t = t.tail) {
	    JCTree head = deblock(t.head);
	    if(head.getTag()==JCTree.IDENT) {
		Name idname = ((JCIdent)head).name;
		MetaKind meta = metaKind(idname);
		if(meta == MetaKind.LIST) {
		    for(int i = metaslot; --i>=0; )
			if(idname==metakeys[i]) {
			    if(isList[i]==0)
				addBoolean("matches("+metavalues[i]+","+prefix+")");
			    else
				addBoolean("matches("+metavalues[i]+","+prefix+",len_"+i+")");
			    return;
			}
		    metakeys[metaslot]=idname;
		    isList[metaslot]=0;
		    metavalues[metaslot] = prefix;
		    if(t.tail.isEmpty()) {
			metaslot++;
			return;
		    }
		    isList[metaslot]=1;
		    addLoop(metavalues[metaslot],metaslot);
		    prefix = "p_"+metaslot;
		    metaslot++;
		    continue;
		}
	    }
	    addBoolean(prefix+".nonEmpty()");
	    genMatch("deblock("+prefix+".head)", head);
	    prefix = prefix+".tail";
	}
	addBoolean(prefix+".isEmpty()");
    }
    private void genMatch(String prefix, Name n) throws IOException {
	if(n==null) addBoolean(prefix+"==null");
	else addBoolean(prefix+"=="+nameConst(n));
    }
    private void genMatch(String prefix, long v) {
	addInt(prefix,v);
    }
    private void genMatch(String prefix, JCTree t) throws IOException {
	genMatch(prefix, t, false);
    }
    enum MetaKind { 
        // Normal identifier
        NONE, 
        // Metavariable, such as $n
        VARIABLE, 
        // Metalist, such as $list$
        LIST 
    };
    private static MetaKind metaKind(Name n) {
	if (n != null && n.len > 1) {
            byte[] names = n.table.names;
            if (names[n.index] == '$')
                return (n.len > 2 && names[n.index+n.len-1] == '$') ? 
                    MetaKind.LIST : MetaKind.VARIABLE;
        }
        return MetaKind.NONE;
    }
    private boolean metavar(Name idname,String prefix, boolean nameContext) {
	if(metaKind(idname) != MetaKind.NONE) {
	    for(int i = metaslot; --i>=0; )
		if(idname==metakeys[i]) {
		    addBoolean("matches("+metavalues[i]+","+prefix+")");
		    return true;
		}
	    metakeys[metaslot]=idname;
	    isName[metaslot]=nameContext;
	    isList[metaslot]=-1;
	    metavalues[metaslot++] = prefix;
	    return true;
	}
	return false;
    }
    private boolean containsMeta(JCTree t) {
	while(t!=null)
	    switch(t.getTag()) {
		default: return false;
		case JCTree.IDENT: return metaKind(((JCIdent)t).name) != MetaKind.NONE;
		case JCTree.SELECT: t = ((JCFieldAccess)t).selected; continue;
                case JCTree.TYPEAPPLY: return containsMeta(((JCTypeApply)t).clazz);
                case JCTree.APPLY: t = ((JCMethodInvocation)t).meth; continue;
	    }
	return false;
    }
    private void genMatch(String prefix, JCTree t, boolean couldBeNull) throws IOException {
	t = deblock(t);
	if(t==null) {
	    addBoolean("isEmpty("+prefix+")");
	    return;
	}
	switch(t.getTag()) {
	case JCTree.IDENT:
	    Name idname = ((JCIdent)t).name;
	    if(idname==trueName) {
		addBoolean("isTrue("+prefix+")");
		return;
	    }
	    if(idname==falseName) {
		addBoolean("isFalse("+prefix+")");
		return;
	    }
	    if(idname==nullName) {
		addBoolean("isNull("+prefix+")");
		return;
	    }
	    if(metavar(idname, prefix, false)) {
                if (couldBeNull) // it could be null but wasn't (first test), so verify it's not
                    addBoolean("!isEmpty("+prefix+")");
                return;
            }
	    break;
	case JCTree.SKIP:
	    addBoolean("isEmpty("+prefix+")");
	    return;
	}
	String tname0 = "T"+ ++tempslot;
	addDeclaration("JCTree", tname0, prefix);
	prefix = tname0;
	if(couldBeNull) addBoolean(prefix+"!=null");
	addInt(prefix+".getTag()", t.getTag());
	String cname = t.getClass().getName().replace('$','.');
	String tname = "T"+ ++tempslot;
	addDeclaration(cname,tname,prefix);
	int tag = t.getTag();
	switch(tag) {
	default:
	    if(tag>=JCTree.BITOR_ASG) {
		genMatch(tname+".lhs", ((JCAssignOp)t).lhs);
		genMatch(tname+".rhs", ((JCAssignOp)t).rhs);
	    } else if(tag>=JCTree.OR) {
		genMatch(tname+".lhs", ((JCBinary)t).lhs);
		genMatch(tname+".rhs", ((JCBinary)t).rhs);
	    } else if(tag>=JCTree.POS)
		genMatch(tname+".arg", ((JCUnary)t).arg);
	    else logError(t.pos, "no.matcher", t.getClass(), t);
	    break;
	case JCTree.BLOCK:
	    genMatch(tname+".stats", ((JCBlock)t).stats);
	    break;
	case JCTree.ASSIGN:
	    genMatch(tname+".lhs", ((JCAssign)t).lhs);
	    genMatch(tname+".rhs", ((JCAssign)t).rhs);
	    break;
	case JCTree.NEWCLASS: {
	    JCNewClass nc = (JCNewClass) t;
	    JCTree clazz = nc.clazz;
	    if(containsMeta(clazz)) genMatch(tname+".clazz", clazz);
	    else addBoolean("isInstance("+tname+".clazz, "
		+ classSymConst(TreeInfo.fullName(clazz))+")");
	    genMatch(tname+".args", nc.args);
	}
	    break;
	case JCTree.NEWARRAY: {
	    JCNewArray na = (JCNewArray) t;
	    JCTree clazz = na.elemtype;
	    if(containsMeta(clazz)) genMatch(tname+".elemtype", clazz);
            else if (na.elemtype instanceof JCPrimitiveTypeTree)
                addBoolean("(" + tname + ".elemtype instanceof JCPrimitiveTypeTree) && ((JCPrimitiveTypeTree)" +
                           tname + ".elemtype).typetag == " + ((JCPrimitiveTypeTree)na.elemtype).typetag);
	    else addBoolean("isInstance("+tname+".elemtype, "
		+ classSymConst(TreeInfo.fullName(clazz))+")");
	    genMatch(tname+".dims", na.dims);
	    genMatch(tname+".elems", na.elems, true);
	}
	    break;
	case JCTree.TYPECAST:
	    genMatch(tname+".clazz", ((JCTypeCast)t).clazz);
	    genMatch(tname+".expr", ((JCTypeCast)t).expr);
	    break;
	case JCTree.VARDEF:
	    {	JCVariableDecl vd = (JCVariableDecl) t;
                //FIXME genMatch(tname+".mods", vd.mods);
		metavar(vd.name, tname+".name", true);
	    	if(containsMeta(vd.vartype)) genMatch(tname+".vartype", vd.vartype);
                else if (vd.vartype instanceof JCPrimitiveTypeTree)
                    addBoolean("(" + tname + ".vartype instanceof JCPrimitiveTypeTree) && ((JCPrimitiveTypeTree)" +
                               tname + ".vartype).typetag == " + ((JCPrimitiveTypeTree)vd.vartype).typetag);
		else addBoolean("isInstance("+tname+".vartype, "
		    	+ classSymConst(TreeInfo.fullName(vd.vartype))+")");
	    	genMatch(tname+".init", vd.init, true);
	    }
	    break;
	case JCTree.INDEXED:
	    genMatch(tname+".indexed", ((JCArrayAccess)t).indexed);
	    genMatch(tname+".index", ((JCArrayAccess)t).index);
	    break;
	case JCTree.SELECT:
	    {
		JCFieldAccess sel = (JCFieldAccess) t;
		String selid = tname+".name";
                if (containsMeta(sel)){
                    genMatch(tname+".selected", sel.selected);
                    if(!metavar(sel.name, selid, true))
                        genMatch(selid, sel.name);
                }
                else 
                    addBoolean("idMatches(" + tname + ", " + nameConst(TreeInfo.fullName(sel)) + ")");
	    }
	    break;
	case JCTree.TYPETEST:
	    genMatch(tname+".clazz", ((JCInstanceOf)t).clazz);
	    genMatch(tname+".expr", ((JCInstanceOf)t).expr);
	    break;
	case JCTree.RETURN:
	    genMatch(tname+".expr", ((JCReturn)t).expr, true);
	    break;
	case JCTree.CONDEXPR:
	    genMatch(tname+".cond", ((JCConditional)t).cond);
	    genMatch(tname+".truepart", ((JCConditional)t).truepart);
	    genMatch(tname+".falsepart", ((JCConditional)t).falsepart);
	    break;
	case JCTree.IF:
	    genMatch(tname+".cond", ((JCIf)t).cond);
	    genMatch(tname+".thenpart", ((JCIf)t).thenpart, true);
	    genMatch(tname+".elsepart", ((JCIf)t).elsepart, true);
	    break;
	case JCTree.DOLOOP:
	    genMatch(tname+".cond", ((JCDoWhileLoop)t).cond);
	    genMatch(tname+".body", ((JCDoWhileLoop)t).body, true);
	    break;
	case JCTree.WHILELOOP:
	    genMatch(tname+".cond", ((JCWhileLoop)t).cond);
	    genMatch(tname+".body", ((JCWhileLoop)t).body, true);
	    break;
	case JCTree.FORLOOP:
	    genMatch(tname+".init", ((JCForLoop)t).init);
	    genMatch(tname+".cond", ((JCForLoop)t).cond, true);
	    genMatch(tname+".step", ((JCForLoop)t).step);
	    genMatch(tname+".body", ((JCForLoop)t).body, true);
	    break;
	case JCTree.LABELLED:
	    genMatch(tname+".label", ((JCLabeledStatement)t).label);
	    genMatch(tname+".body", ((JCLabeledStatement)t).body, true);
	    break;
	case JCTree.SWITCH:
	    genMatch(tname+".selector", ((JCSwitch)t).selector);
	    genMatch(tname+".cases", ((JCSwitch)t).cases);
	    break;
	case JCTree.CASE:
	    genMatch(tname+".pat", ((JCCase)t).pat);
	    genMatch(tname+".stats", ((JCCase)t).stats);
	    break;
	case JCTree.SYNCHRONIZED:
	    genMatch(tname+".lock", ((JCSynchronized)t).lock);
	    genMatch(tname+".body", ((JCSynchronized)t).body, true);
	    break;
	case JCTree.TRY:
	    genMatch(tname+".body", ((JCTry)t).body, true);
	    genMatch(tname+".catchers", ((JCTry)t).catchers);
	    genMatch(tname+".finalizer", ((JCTry)t).finalizer, true);
	    break;
	case JCTree.CATCH:
	    genMatch(tname+".param", ((JCCatch)t).param);
	    genMatch(tname+".body", ((JCCatch)t).body, true);
	    break;
	case JCTree.APPLY:
	    genMatch(tname+".meth", ((JCMethodInvocation)t).meth);
	    genMatch(tname+".args", ((JCMethodInvocation)t).args);
	    break;
	case JCTree.BREAK:
	    genMatch(tname+".label", ((JCBreak)t).label);
	    break;
	case JCTree.CONTINUE:
	    genMatch(tname+".label", ((JCContinue)t).label);
	    break;
	case JCTree.THROW:
	    genMatch(tname+".expr", ((JCThrow)t).expr, true);
	    break;
	case JCTree.ASSERT:
	    genMatch(tname+".cond", ((JCAssert)t).cond, true);
	    genMatch(tname+".detail", ((JCAssert)t).detail, true);
	    break;
        case JCTree.TYPEAPPLY:
            genMatch(tname+".clazz", ((JCTypeApply)t).clazz, false);
	    genMatch(tname+".arguments", ((JCTypeApply)t).arguments);
            break;
	case JCTree.IDENT:
	    addBoolean(tname+".name=="+nameConst(((JCIdent)t).name));
	    break;
	case JCTree.LITERAL:
            JCLiteral lit = (JCLiteral)t;
	    Object v = lit.value;
            Kind kind = lit.getKind();
            if (kind == Kind.NULL_LITERAL) {  // true for the null keyword as of Mustang b80
                addBoolean(tname + ".value==null");
            } else {
                addBoolean(tname + ".getKind() == Tree.Kind." + kind);
                String subname = "O"+tname;
                String subtype = v.getClass().getName();
                if(subtype.startsWith("java.lang.")) subtype = subtype.substring(10);
                addDeclaration("Object", subname, tname+".value");
                if(kind == Kind.INT_LITERAL)
                    addInt("(("+subtype+")"+subname+").intValue()",((Integer)v).intValue());
                else {
                    String test;
                    if(lit.getKind() == Kind.STRING_LITERAL)
                        test = "equals(\""+quoteString(v.toString())+"\")";
                    else if (lit.getKind() == Kind.BOOLEAN_LITERAL) {
                        if (v instanceof Integer) // true for b78-b85
                            test = "intValue() == " + ((Integer)v).intValue();
                        else
                            test = "booleanValue() == " + ((Boolean)v).booleanValue();
                    }
                    else test = "Missing("+v.getClass().getName()+")";
                    addBoolean("(("+subtype+")"+subname+")."+test);
                }
            }
	    break;
        case JCTree.WILDCARD:
            genMatch(tname+".kind.ordinal()", ((JCWildcard)t).kind.kind.ordinal());
            genMatch(tname+".inner", ((JCWildcard)t).inner);
            break;
	}
    }
    public static String quoteString(String s) {
	if(s.indexOf('"')<0 && s.indexOf('\\')<0 && s.indexOf('\n')<0) return s;
	StringBuffer sb = new StringBuffer();
	int limit = s.length();
	for(int i = 0; i<limit; i++) {
	    char c = s.charAt(i);
	    switch(c) {
	    case '"':
	    case '\\':
		sb.append('\\');
		break;
	    case '\n':
		sb.append("\\n");
		continue;
	    }
	    sb.append(c);
	}
	return sb.toString();
    }
    private static class Factory extends Parser.Factory {
        public static Factory instance(Context context) {
	    Factory instance = (Factory)context.get(parserFactoryKey);
	    if (instance == null)
		instance = new Factory(context);
	    return instance;
        }
        protected Factory(Context context) {
            super(context);
        }
        public Parser newParser(final Scanner S, final TransformParser tp) {
            return new Parser(this, S, false, null) {
		protected JCExpression checkExprStat(JCExpression t) { // be more forgiving
		    return t;
		}
		public void accept(Token token) { //allow statements to be ended by =>
		    if(token!=SEMI || (!tp.isToken(tp.impliesToken) && !tp.isToken(tp.suchthatToken) && !tp.isToken(tp.doSomethingToken)))
			super.accept(token);
		}
		public Name ident() { // allow asserts as identifiers
		    if (S.token() == IDENTIFIER || S.token() == ASSERT) {
			Name name = S.name();
			S.nextToken();
			return name;
		    } else {
			accept(IDENTIFIER);
			return tp.names.error;
		    }
		}
            };
        }
    }
    public Parser makeParser() {
	return Factory.instance(context).newParser(scanner, this);
    }
    Guard optimize(Guard g) {
	if(g==null || g.kind==Guard.REPLACEMENT) return g;
	Guard f0 = g.ifFail;
	Guard f = optimize(f0);
	if(f!=null && g.sameTest(f) && g.ifSucceed.ifFail==null) {
	    /* ooooh! a redundant guard that can be eliminated! */
	    g.ifSucceed.ifFail = f.ifSucceed;
	    g.ifSucceed = optimize(g.ifSucceed);
	    g.ifFail = f = f.ifFail;
	} else g.ifFail = f;
	if(f!=null && g.kind==Guard.INT && f.kind==Guard.INT && g.test.equals(f.test) && g.value>f.value) {
	    /* Interchange this guard with the fail successor if they're both integer tests on the
	       same field but are out-of-order.  This in combination with the recursive optimize calls
	       that follow implement a bubble sort.  This is for eventual generation as a switch statement. */
	    g.ifFail = f.ifFail;
	    f.ifFail = g;
	    return optimize(f);
	} else if(f != f0) return optimize(g);
	else return g;
    }
    public String stringIfy(List<? extends JCTree> t) {
	if(t==null||t.isEmpty()) return "";
	return t.tail.isEmpty() ? stringIfy(t.head) : stringIfy(t.head)+stringIfy(t.tail);
    }
    public String stringIfy(JCTree t) {
	if(t==null) return "";
	else if(t instanceof JCLiteral) return String.valueOf(((JCLiteral)t).value);
	else if(t instanceof JCIdent) return ((JCIdent)t).name.toString();
	else return t.toString();
    }
    private class Guard {
	static final int INT = 0;
	static final int BOOLEAN = 1;
	static final int LOOP = 2;
	static final int DECLARATION = 3;
	static final int REPLACEMENT = 4;
	Guard ifSucceed;
	Guard ifFail;
	final String test;
	final long value;
	final int kind;
	Guard(String t, long v, int k) {
	    test = t;
	    value = v;
	    kind = k;
	}
	boolean sameTest(Guard g) {
	    return g.kind==kind && (test==null ? g.test==null : test.equals(g.test)) && value==g.value;
	}
	boolean canFlowOut() {
	    return kind<=LOOP || 
	        kind==DECLARATION && ifSucceed!=null && ifSucceed.canFlowOut();
	}
	void writeAll(PluginCompiler pc, int gdepth) throws IOException {
	    if(ifFail==null) {
		writeWrapped(pc,gdepth);
		//		write(pc, gdepth);
		//		if(ifSucceed!=null) ifSucceed.writeAll(pc,gdepth);
	    } else if(kind==INT && ifFail!=null && ifFail.kind==INT && test.equals(ifFail.test)) {
		// Yahoo!  Generate switch statement
		pc.write("\tswitch(");
		pc.write(test);
		pc.write(") {\n");
		long lastsw = value-1;
		Guard c = this;
		boolean reachable = false;
		while(c!=null && c.kind==INT && test.equals(c.test)) {
		    if(c.value != lastsw) {
			if(reachable) pc.write("\t\tbreak;\n");
			pc.write("\t    case ");
			pc.write(lastsw = c.value);
			pc.write(":\n");
		    }
		    c.ifSucceed.writeWrapped(pc, gdepth+1);
		    reachable = c.ifSucceed.canFlowOut() || c.ifSucceed.kind==LOOP;
		    c = c.ifFail;
		}
		pc.write("\t} //end switch\n");
		if(c!=null) c.writeAll(pc, gdepth);
	    } else writeWrapped(pc, gdepth);
	}
	void writeWrapped(PluginCompiler pc, int gdepth) throws IOException {
	    if(kind==LOOP) {
		pc.write("\t\t{ int len_"+value+"=0;\n\t\tfor(List<JCStatement> p_"+value+" = "+test+"; p_"
			 +value+".nonEmpty(); p_"+value+" = p_"+value+".tail, len_"+value+"++) {\n");
		ifSucceed.writeWrapped(pc, gdepth+1);
		//pc.write("\t\tbreak test");
		//pc.write(gdepth);
		//pc.write(";\n");
		pc.write("\t\t} }\n");
	    } else {
		pc.write("\t    test");
		pc.write(gdepth+1);
		pc.write(": {\n");
		write(pc, gdepth+1);
		if(ifSucceed!=null) ifSucceed.writeAll(pc,gdepth+1);
		pc.write("\t    }\n");
	    }
	    if(ifFail!=null) ifFail.writeAll(pc,gdepth);
	}
	void write(PluginCompiler pc,int gdepth) throws IOException {
	    switch(kind) {
	    default:
		pc.write("BOGUS "+kind+": "+test);
		break;
	    case DECLARATION:
		if(test==null || test.length()<=0) return;
		pc.write("\t\t");
		pc.write(test);
		break;
	    case BOOLEAN:
		if(gdepth>debugDepth) {
		    pc.write("\t\tif(!(");
		    pc.write(test);
		    pc.write(")) System.err.println(\""+ debugTag++ +": Failed: \"+");
		    pc.writeQuoted(test);
		    if(test.startsWith("isFalse"))
			pc.write("+\": \"+"+test.substring(7)+"+\" @\"+"+test.substring(7)+".getTag()");
		    pc.write(");\n");
		}
		pc.write("\t\tif(!(");
		pc.write(test);
		pc.write(")) break test");
		pc.write(gdepth);
		break;
	    case INT:
		if(gdepth>debugDepth) {
		    pc.write("\t\tif(");
		    pc.write(test);
		    pc.write("!=");
		    pc.write(value);
		    pc.write(") System.err.println(\""+ debugTag++ +": Failed: \"+");
		    pc.writeQuoted(test);
		    pc.write("+\"    Wanted: "+value+" Got: \"+"+test);
		    pc.write(");\n");
		}
		pc.write("\t\tif(");
		pc.write(test);
		pc.write("!=");
		pc.write(value);
		pc.write(") break test");
		pc.write(gdepth);
		break;
	    }
	    pc.write(";\n");
	}
    }
    final int debugDepth = 999;
    final Name parentName = names.fromString("parent");
    final Name assignedInName = names.fromString("assignedIn");
    final Name declaredInName = names.fromString("declaredIn");
    final Name referencedInName = names.fromString("referencedIn");
    final Name noteName = names.fromString("note");
    final Name commentName = names.fromString("comment");
    final Name inlineName = names.fromString("inline");
    final Name mapclassName = names.fromString("mapclass");
    final Name statementcontextName = names.fromString("statementcontext");
    final Name transformationFailureName = names.fromString("transformationFailure");
    class Replacement extends Guard {
	final Rule rule;
	final Name[] metavars;
	final String[] metavals;
	final boolean[] isName;
	final int[] isList;
	Replacement(Rule r, int nmeta, Name[] vars, String[] vals, boolean isn[], int[] isl) {
	    super(null, 0, DECLARATION);
	    rule = r;
	    metavars = new Name[nmeta];
	    metavals = new String[nmeta];
	    isName = new boolean[nmeta];
	    isList = new int[nmeta];
	    System.arraycopy(vars,0,metavars,0,nmeta);
	    System.arraycopy(vals,0,metavals,0,nmeta);
	    System.arraycopy(isn,0,isName,0,nmeta);
	    System.arraycopy(isl,0,isList,0,nmeta);
	}
	boolean sameTest(Guard g) {
	    return false;
	}
	void writeAll(PluginCompiler pc, int gdepth) throws IOException {
	    write(pc, gdepth);
	    if(ifFail!=null)
		logError(rule.pattern.pos, "pattern.hides.followers", rule.pattern);
	}
	void write(PluginCompiler pc, int gdepth) throws IOException {
	    pc.write("\t\tcurrentLine = " + rule.startLine);
	    pc.write(";\n\t\t");
	    JCTree replacement = rule.replacement;
	    if(replacement!=null) {
                JCMethodInvocation a = null;
		if(replacement.getTag()==JCTree.APPLY)
		    a = (JCMethodInvocation) replacement;
                else if(replacement.getTag()==JCTree.EXEC) {
                    JCTree t = deblock(replacement);
                    if (t.getTag() == JCTree.APPLY)
                        a = (JCMethodInvocation) t;
                }
                if (a != null) {
		    if(a.meth.getTag()==JCTree.IDENT) {
			Name nm = ((JCIdent)a.meth).name;
			if(nm==noteName) {
			    pc.write("addResult(getCurrentPath(), ");
			    pc.writeQuoted(stringIfy(a.args));
			    pc.write(");\n\t\treturn t;\n");
			    return;
			} else if(nm==commentName) {
			    String comment = stringIfy(a.args);
			    pc.write("addResult(getCurrentPath(), ");
			    pc.writeQuoted(comment);
			    pc.write(");\n\t\tattach(containingStatement(), ");
			    pc.writeQuoted(comment);
			    pc.write(");\n\t\treturn t;\n");
			    return;
			} else if(nm==transformationFailureName) {
                            pc.write("transformationFailure(");
                            pc.writeQuoted(stringIfy(a.args));
                            pc.write(");\n");
                            return;
                        }
		    }
		}
            }
	    pc.write("{ Tree result = ");
	    if(replacement!=null) {
		constructor.metavars = metavars;
		constructor.metavals = metavals;
		constructor.isName = isName;
		constructor.isList = isList;
		constructor.pc = pc;
		constructor.generate(replacement);
	    } else pc.write("t");
	    pc.write(";\n");
	    pc.write("\t\treturn result; }\n");
	}
    }
    private int debugTag = 0;
    final private HashMap<String,String> classMap = new HashMap<String,String>();
    public String classRef(String jar, String clazz) {
	if(jar==null) jar = "";
	if("org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher".equals(clazz) ||
           "org.netbeans.api.jackpot.Query".equals(clazz) ||
           "org.netbeans.api.jackpot.TreePathQuery".equals(clazz) ||
           "org.netbeans.api.jackpot.Transformer".equals(clazz) ||
           "org.netbeans.api.jackpot.TreePathTransformer".equals(clazz)) 
            return "this";
	String key = jar+':'+clazz;
	String v = classMap.get(key);
	if(v == null) {
	    v = "preloadedClass_"+classMap.size();
	    classMap.put(key,v);
	}
	return v;
    }
    private static final HashMap<String,Method> methodRegistry = new HashMap<String,Method>();
    
    public static synchronized void registerBoolean(String jar, String clazz, String method) {
	methodRegistry.put(method, new BooleanMethod(jar,clazz,method));
    }
    public static synchronized void registerBSym(String jar, String clazz, String method) {
	methodRegistry.put(method, new BSymMethod(jar,clazz,method));
    }
    static {
	registerBoolean("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "hasComment");
	registerBoolean("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "isConstant");
	registerBoolean("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "isLiteral");
	registerBoolean("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "isClassIdentifier");
	registerBoolean("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "isEmpty");
	registerBoolean("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "sideEffectFree");
	registerBoolean("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "hasVariableDeclarations");
	registerBoolean("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "isStatement");
	registerBoolean("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "isTrue");
	registerBoolean("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "isFalse");
	registerBoolean("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "isNull");
	registerBoolean("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "isNullTree");
        registerBoolean("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "isStatic");
 	registerBoolean("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "couldThrow");
 	registerBSym("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "referenced");
 	registerBSym("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "assigned");
 	registerBSym("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "parameter");
 	registerBSym("", "org.netbeans.modules.jackpot.rules.parser.GeneratedMatcher", "local");
    }

    abstract static class Method {
	String jar;
	String clazz;
	String method;
	Method(String j, String c, String m) {
	    jar = j; clazz = c; method = m;
	}
	abstract String genInvoke(TransformParser tp, String arg);
    }
    static class BooleanMethod extends Method {
	BooleanMethod(String j, String c, String m) { super(j,c,m); }
	String genInvoke(TransformParser tp, String arg) {
	    return tp.classRef(jar,clazz)+"."+method+"("+arg+")";
	}
    }
    static class BSymMethod extends BooleanMethod {
	BSymMethod(String j, String c, String m) { super(j,c,m); }
	String genInvoke(TransformParser tp, String arg) {
	    return super.genInvoke(tp, arg.endsWith(".name")
	    		? arg.substring(0,arg.length()-5)+".sym" : arg);
	}
    }
}
