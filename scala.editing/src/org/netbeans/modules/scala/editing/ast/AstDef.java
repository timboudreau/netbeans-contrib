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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.gsf.api.Modifier;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.scala.editing.NbUtilities;
import org.netbeans.modules.scala.editing.ScalaUtils;
import org.openide.filesystems.FileObject;
import scala.tools.nsc.symtab.Symbols.Symbol;
import scala.tools.nsc.symtab.Symbols.TypeSymbol;
import scala.tools.nsc.symtab.Types.Type;

/**
 * AST Definition
 * 
 * Represents a program element such as a package, class, or method. Each element 
 * represents a static, language-level construct (and not, for example, a runtime 
 * construct of the virtual machine). 
 * 
 * @author Caoyuan Deng
 */
public class AstDef extends AstItem implements ScalaElementHandle {

    private ElementKind kind;
    private AstScope bindingScope;
    private Set<Modifier> modifiers;
    private FileObject fo;

    protected AstDef(Symbol symbol, Token pickToken, AstScope bindingScope, ElementKind kind, FileObject fo) {
        super(symbol, pickToken);
        this.kind = kind;
        if (bindingScope != null) {
            this.bindingScope = bindingScope;
            this.bindingScope.setBindingDef(this);
        }
        this.fo = fo;
    }

    public Type getType() {
        return getSymbol().tpe();
    }

    @Override
    public String getName() {
        if (getKind() == ElementKind.CONSTRUCTOR) {
            return getSymbol().enclClass().nameString();
        } else {
            return super.getName();
        }
    }

    public List<? extends AstDef> getEnclosedElements() {
        if (bindingScope != null) {
            return bindingScope.getDefs();
        } else {
            return Collections.<AstDef>emptyList();
        }
    }

    public AstDef getEnclosingDef() {
        return getEnclosingScope().getBindingElement();
    }

    public void setKind(ElementKind kind) {
        this.kind = kind;
    }

    public ElementKind getKind() {
        return kind;
    }

    @Override
    public String toString() {
        return "Def: " + getName() + " (idToken=" + getIdToken() + ", kind=" + getKind() + ", type=" + getSymbol().tpe() + ")";
    }

    public AstScope getBindingScope() {
        assert bindingScope != null : toString() + ": Each definition should set binding scope!";
        return bindingScope;
    }

    public int getBoundsOffset(TokenHierarchy th) {
        return getBindingScope().getBoundsOffset(th);
    }

    public int getBoundsEndOffset(TokenHierarchy th) {
        return getBindingScope().getBoundsEndOffset(th);
    }

    public OffsetRange getRange(TokenHierarchy th) {
        return getBindingScope().getRange(th);
    }

    public boolean isReferredBy(AstRef ref) {
        if (ref.getName().equals(getName())) {
            if ((getSymbol().isClass() || getSymbol().isModule()) && ref.isSameNameAsEnclClass()) {
                return true;
            }

            return ref.getSymbol() == getSymbol();
        }

        return false;
    }

    public boolean mayEqual(AstDef def) {
        return this == def;
    //return getName().equals(def.getName());
    }

    public String getDocComment() {
        BaseDocument srcDoc = getDoc();
        if (srcDoc == null) {
            return null;
        }
        
        TokenHierarchy th = TokenHierarchy.get(srcDoc);
        if (th == null) {
            return null;
        }

        return ScalaUtils.getDocComment(srcDoc, getIdOffset(th));
    }

    public BaseDocument getDoc() {
        FileObject srcFo = getFileObject();
        if (srcFo != null) {
            return NbUtilities.getDocument(srcFo, true);
        } else {
            return null;
        }
    }

    public void htmlFormat(HtmlFormatter formatter) {
        formatter.appendText(getName());
        //htmlFormat(formatter, this, false);
        switch (getKind()) {
            case PACKAGE:
            case CLASS:
            case MODULE:
                break;
            default:
                //Type resType = getType().resultType();
                formatter.appendText(" : ");
                formatter.appendText(ScalaElement.typeToString(getType()));
            //formatter.appendText(resType.toString());
            //htmlFormat(formatter, resType, true);
        }
    }

    public Set<Modifier> getModifiers() {
        if (modifiers != null) {
            return modifiers;
        }

        modifiers = new HashSet<Modifier>();

        Symbol symbol = getSymbol();
        if (symbol.isPublic()) {
            modifiers.add(Modifier.PUBLIC);
        }

        if (symbol.isPrivateLocal()) {
            modifiers.add(Modifier.PRIVATE);
        }

        if (symbol.isProtectedLocal()) {
            modifiers.add(Modifier.PROTECTED);
        }

        // java.lang.Error: no-symbol does not have owner
        // at scala.tools.nsc.symtab.Symbols$NoSymbol$.owner(Symbols.scala:1565)
        // at scala.tools.nsc.symtab.Symbols$Symbol.isStatic(Symbols.scala:312)
        //if (symbol.isStatic()) {
        //    modifiers.add(Modifier.STATIC);
        //}

        return modifiers;
    }

    public FileObject getFileObject() {
        return fo;
    }

    public String getPackageName() {
        return ScalaElement.symbolQualifiedName(getSymbol().enclosingPackage());
    }

    public String getQualifiedName() {
        return ScalaElement.symbolQualifiedName(getSymbol());
    }

    public boolean isInherited() {
        return false;
    }

    public boolean isDeprecated() {
        return false;
    }

    public boolean isEmphasize() {
        return false;
    }

    public static String htmlFormat(HtmlFormatter formatter, ScalaElementHandle handle, boolean withKind) {
        Symbol symbol = handle.getSymbol();

        boolean strike = handle.isDeprecated();
        boolean emphasize = !handle.isEmphasize();
        if (strike) {
            formatter.deprecated(true);
        }
        if (emphasize) {
            formatter.emphasis(true);
        }

        if (withKind) {
            ElementKind kind = handle.getKind();
            formatter.name(kind, true);
            formatter.appendText(handle.getName());
            formatter.name(kind, false);
        } else {
            formatter.appendText(handle.getName());
        }

        if (emphasize) {
            formatter.emphasis(false);
        }
        if (strike) {
            formatter.deprecated(false);
        }

        Type type = symbol.tpe();
        htmlFormat(formatter, type, false);

        return formatter.getText();
    }

    public static String htmlFormat(HtmlFormatter formatter, Type type, boolean alsoName) {
        if (alsoName) {
            formatter.appendText(type.typeSymbol().nameString());
        }

        scala.List typeParams = type.typeParams();
        if (!typeParams.isEmpty()) {
            formatter.appendHtml("[");
            int size = typeParams.size();
            for (int i = 0; i < size; i++) {
                TypeSymbol typeParam = (TypeSymbol) typeParams.apply(i);
                formatter.appendText(typeParam.nameString());

                if (i < size - 1) {
                    formatter.appendText(", "); // NOI18N
                }
            }

            formatter.appendHtml("]");
        }

        scala.List paramTypes = type.paramTypes();
        if (!paramTypes.isEmpty()) {
            formatter.appendHtml("("); // NOI18N

            int size = paramTypes.size();
            for (int i = 0; i < size; i++) {
                Type param = (Type) paramTypes.apply(i);

                formatter.parameters(true);
                formatter.appendText("a" + Integer.toString(i));
                formatter.parameters(false);
                formatter.appendText(": ");
                htmlFormat(formatter, param, true);

                if (i < size - 1) {
                    formatter.appendText(", "); // NOI18N
                }
            }

            formatter.appendHtml(")"); // NOI18N
        }

        return formatter.getText();
    }
}
