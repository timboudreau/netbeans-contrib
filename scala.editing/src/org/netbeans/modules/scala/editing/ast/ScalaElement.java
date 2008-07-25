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
package org.netbeans.modules.scala.editing.ast;

import org.netbeans.modules.scala.editing.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.swing.text.BadLocationException;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.Modifier;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import scala.Option;
import scala.tools.nsc.CompilationUnits.CompilationUnit;
import scala.tools.nsc.Global;
import scala.tools.nsc.ast.Trees.Tree;
import scala.tools.nsc.io.AbstractFile;
import scala.tools.nsc.symtab.Symbols.Symbol;
import scala.tools.nsc.symtab.Types.Type;
import scala.tools.nsc.util.BatchSourceFile;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaElement implements ScalaElementHandle {

    private Symbol symbol;
    private final Global global;
    private CompilationInfo info;
    private ElementKind kind;
    private Set<Modifier> modifiers;
    private boolean inherited;
    private boolean smart;
    private FileObject fo;
    private String path;
    private BaseDocument doc;
    private int offset = 0;
    private Element javaElement;
    private boolean loaded;

    /**
     * @param element, that to be wrapped
     * @param info, CompilationInfo
     */
    public ScalaElement(Symbol symbol, CompilationInfo info, Global global) {
        this.symbol = symbol;
        this.info = info;
        this.global = global;
    }

    public ScalaElement(ElementKind kind) {
        this.kind = kind;
        this.global = null;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public Type getType() {
        return getSymbol().tpe();
    }

    public FileObject getFileObject() {
        if (fo == null) {
            AbstractFile srcFile = symbol.sourceFile();
            if (srcFile != null) {
                String srcPath = srcFile.path();
                // Check the strange behavior of Scala's compiler, which may omit the beginning File.separator ("/")
                if (!srcPath.startsWith(File.separator)) {
                    srcPath = File.separator + srcPath;
                }
                File file = new File(srcPath);
                if (file != null && file.exists()) {
                    // it's a real file and not archive file
                    fo = FileUtil.toFileObject(file);
                }
            }

            if (fo == null) {
                fo = ScalaUtils.getFileObject(info, symbol);
            }

            if (fo != null) {
                path = fo.getPath();
            }
        }

        return fo;
    }

    public String getIn() {
        return symbol.owner().nameString();
    }

    public ElementKind getKind() {
        return getKind(symbol);
    }

    public String getMimeType() {
        return ScalaMimeResolver.MIME_TYPE;
    }

    public Set<Modifier> getModifiers() {
        if (modifiers == null) {
            modifiers = getModifiers(symbol);
        }

        return modifiers;
    }

    public String getName() {
        return symbol.nameString();
    }

    public boolean signatureEquals(ElementHandle handle) {
        return false;
    }

    public String getDocComment() {
        if (!isLoaded()) {
            load();
        }

        BaseDocument srcDoc = getDoc();
        if (srcDoc != null) {
            if (!isJava()) {
                return ScalaUtils.getDocComment(srcDoc, getOffset());
            } else {
                if (javaElement != null) {
                    try {
                        String docComment = JavaUtilities.getDocComment(JavaUtilities.getCompilationInfoForScalaFile(info.getFileObject()), javaElement);
                        if (docComment != null) {
                            return new StringBuilder(docComment.length() + 5).append("/**").append(docComment).append("*/").toString();
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }

        return null;
    }

    public int getOffset() {
        if (!isLoaded()) {
            load();
        }

        if (isJava()) {
            if (javaElement != null) {
                try {
                    return JavaUtilities.getOffset(JavaUtilities.getCompilationInfoForScalaFile(info.getFileObject()), javaElement);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } else {
            Option offsetOpt = symbol.pos().offset();
            if (offsetOpt.isDefined()) {
                return (Integer) offsetOpt.get();
            }
        }

        return offset;
    }

    public BaseDocument getDoc() {
        FileObject srcFo = getFileObject();
        if (srcFo != null) {
            return doc = doc == null ? NbUtilities.getDocument(srcFo, true) : doc;
        } else {
            return null;
        }
    }

    private boolean isLoaded() {
        if (loaded) {
            return true;
        }

        if (isJava()) {
            return javaElement != null;
        } else {
            return symbol.pos().offset().isDefined();
        }
    }

    private void load() {
        if (isLoaded()) {
            return;
        }

        if (isJava()) {
            javaElement = JavaUtilities.getJavaElement(JavaUtilities.getCompilationInfoForScalaFile(info.getFileObject()), symbol);
        } else {
            BaseDocument srcDoc = getDoc();
            if (srcDoc != null) {
                assert path != null;
                try {
                    char[] text = srcDoc.getChars(0, srcDoc.getLength());
                    BatchSourceFile srcFile = new BatchSourceFile(path, text);
                    TokenHierarchy th = TokenHierarchy.get(doc);
                    if (th == null) {
                        return;
                    }

                    /**
                     * @Note by compiling the related source file, this symbol will
                     * be automatically loaded next time, but the position/sourcefile
                     * info is not updated for this symbol yet. But we can find the
                     * position via the AST Tree, or use a tree visitor to update
                     * all symbols Position
                     */
                    CompilationUnit unit = ScalaGlobal.compileSource(global, srcFile);
                    if (unit != null) {
                        final Tree tree = unit.body();
                        AstRootScope root = new AstTreeVisitor(tree, th, srcFile).getRootScope();
                        AstDef def = root.findDefMatched(symbol);
                        if (def != null) {
                            offset = def.getIdOffset(th);
                        }
                    }
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        loaded = true;
    }

    public boolean isJava() {
        if (path == null) {
            getFileObject();
        }

        if (path != null) {
            return path.endsWith(".java");
        }

        return false;
    }

    public boolean isDeprecated() {
        return symbol.isDeprecated();
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public boolean isInherited() {
        return inherited;
    }

    public boolean isEmphasize() {
        return !isInherited();
    }

    public void setSmart(boolean smart) {
        this.smart = smart;
    }

    @Override
    public String toString() {
        return symbol.toString();
    }

    public static ElementKind getKind(
            Symbol symbol) {
        if (symbol.isClass()) {
            return ElementKind.CLASS;
        } else if (symbol.isConstructor()) {
            return ElementKind.CONSTRUCTOR;
        } else if (symbol.isConstant()) {
            return ElementKind.CONSTANT;
        } else if (symbol.isValue()) {
            return ElementKind.FIELD;
        } else if (symbol.isModule()) {
            return ElementKind.MODULE;
        } else if (symbol.isLocal() && symbol.isVariable()) {
            return ElementKind.VARIABLE;
        } else if (symbol.isMethod()) {
            return ElementKind.METHOD;
        } else if (symbol.isPackage()) {
            return ElementKind.PACKAGE;
        } else if (symbol.isValueParameter()) {
            return ElementKind.PARAMETER;
        } else if (symbol.isTypeParameter()) {
            return ElementKind.CLASS;
        } else {
            return ElementKind.OTHER;
        }

    }

    public static Set<Modifier> getModifiers(Symbol symbol) {
        Set<Modifier> modifiers = new HashSet<Modifier>();

        if (symbol.isPublic()) {
            modifiers.add(Modifier.PUBLIC);
        }

        if (symbol.isPrivateLocal()) {
            modifiers.add(Modifier.PRIVATE);
        }

        if (symbol.isProtectedLocal()) {
            modifiers.add(Modifier.PROTECTED);
        }

        if (symbol.isStatic()) {
            modifiers.add(Modifier.STATIC);
        }

        return modifiers;
    }

    public static String symbolQualifiedName(Symbol symbol) {
        return symbolQualifiedName(symbol, true);
    }

    /**
     * Due to the ugly implementation of scala's Symbols.scala, Symbol#fullNameString() 
     * may cause: 
     * java.lang.Error: no-symbol does not have owner
     *        at scala.tools.nsc.symtab.Symbols$NoSymbol$.owner(Symbols.scala:1565)
     * We should bypass it via symbolQualifiedName
     */
    public static String symbolQualifiedName(Symbol symbol, boolean forScala) {
        if (symbol.isError()) {
            return "<error>";
        } else if (symbol.nameString().equals("<none>")) {
            return "<none>";
        } else {
            List<String> paths = new LinkedList<String>();
            Symbol owner = symbol.owner();
            // remove type parameter part at the beginnig, for example: scala.Array[T0] will be: scala.Array.T0
            if (!symbol.isTypeParameterOrSkolem()) {
                paths.add(symbol.nameString());
            }
            while (!owner.nameString().equals("<none>") && !owner.nameString().equals("<root>")) {
                if (!symbol.isTypeParameterOrSkolem()) {
                    paths.add(owner.nameString());
                }
                owner = owner.owner();
            }

            Collections.reverse(paths);
            StringBuilder sb = new StringBuilder();
            for (Iterator<String> itr = paths.iterator(); itr.hasNext();) {
                sb.append(itr.next());
                if (itr.hasNext()) {
                    sb.append('.');
                }
            }

            if (sb.length() == 0) {
                if (symbol.isPackage()) {
                    return "";
                } else {
                    return forScala ? symbol.nameString() : "Object"; // it maybe a TypeParameter likes: T0
                }
            } else {
                return sb.toString();
            }
        }
    }

    public static String typeQualifiedName(Type type, boolean forScala) {
        return symbolQualifiedName(type.typeSymbol(), forScala);
    }

    public static boolean isInherited(Symbol template, Symbol member) {
        return !symbolQualifiedName(template).equals(symbolQualifiedName(member.enclClass()));
    }

    public static String typeToString(Type type) {
        String str = null;
        try {
            str = type.toString();
        } catch (java.lang.AssertionError ex) {
            // ignore assert ex from scala
            ScalaGlobal.reset();
        } catch (Throwable ex) {
            ScalaGlobal.reset();
        }

        if (str == null) {
            str = type.termSymbol().nameString();
        }

        return str;
    }
}
