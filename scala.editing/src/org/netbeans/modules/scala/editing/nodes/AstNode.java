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
package org.netbeans.modules.scala.editing.nodes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.scala.editing.ScalaMimeResolver;
import org.netbeans.modules.scala.editing.nodes.tmpls.Template;
import org.netbeans.modules.scala.editing.nodes.types.TypeRef;

/**
 *
 * @author Caoyuan Deng
 */
public abstract class AstNode {

    protected final static String NO_MEANING_NAME = "-1";
    /** 
     * @Note: 
     * 1. Not all AstNode has pickToken, such as Expr etc.
     * 2. Due to strange behavior of StructureAnalyzer, we can not rely on 
     *    pickToken's text as name, pickToken may be <null> and pickToken.text() 
     *    will return null when an Identifier token modified, seems sync issue
     */
    private Token pickToken;
    private AstScope enclosingScope;
    private Set<Modifier> mods;
    private Name simpleName;
    protected TypeRef type;
    protected Name qualifiedName;

    protected AstNode() {
        this(null, null);
    }

    protected AstNode(CharSequence simpleName) {
        this(simpleName, null);
    }

    protected AstNode(Token pickToken) {
        this(null, pickToken);
    }

    protected AstNode(CharSequence simpleName, Token pickToken) {
        this.pickToken = pickToken;
        setSimpleName(simpleName);
    }

    public void setSimpleName(CharSequence sName) {
        if (sName != null) {
            if (sName instanceof Name) {
                this.simpleName = (Name) sName;
            } else {
                this.simpleName = new AstName(sName);
            }
        } else {
            this.simpleName = null;
        }
    }

    /** @Todo Importing, Packaging are with null simpleName, any better solution? */
    public Name getSimpleName() {
        return simpleName;
//        if (name == null) {
//            assert false : "Should implement getSimpleName()";
//            throw new UnsupportedOperationException();
//        } else {
//            return name;
//        }
    }

    public void setPickToken(Token pickToken) {
        this.pickToken = pickToken;
    }

    public Token getPickToken() {
        return pickToken;
    }

    public int getPickOffset(TokenHierarchy th) {
        if (pickToken != null) {
            return pickToken.offset(th);
        } else {
            assert false : getSimpleName() + ": Should implement getPickOffset(th)";
            return -1;
        }
    }

    public int getPickEndOffset(TokenHierarchy th) {
        if (pickToken != null) {
            return pickToken.offset(th) + pickToken.length();
        } else {
            assert false : getSimpleName() + ": Should implement getPickEndOffset(th)";
            return -1;
        }
    }

    public String getBinaryName() {
        return getSimpleName().toString();
    }

    public void setQualifiedName(CharSequence qName) {
        if (qName != null) {
            if (qName instanceof Name) {
                this.qualifiedName = (Name) qName;
            } else {
                this.qualifiedName = new AstName(qName);
            }
        } else {
            this.qualifiedName = null;
        }
    }

    public Name getQualifiedName() {
        if (qualifiedName == null) {
            Packaging packaging = getPackageElement();
            qualifiedName = packaging == null ? getSimpleName() : new AstName(packaging.getQualifiedName() + "." + getSimpleName());
        }

        return qualifiedName;
    }

    public Packaging getPackageElement() {
        return getEnclosingDef(Packaging.class);
    }

    public void setType(TypeRef type) {
        this.type = type;
    }

    public TypeRef asType() {
        return type;
    }

    public <T extends AstDef> T getEnclosingDef(Class<T> clazz) {
        return getEnclosingScope().getEnclosingDef(clazz);
    }

    /**
     * @Note: enclosingScope will be set when call
     *   {@link AstScope#addDef(Def)} or {@link AstScope#addUsage(Usage)}
     */
    public void setEnclosingScope(AstScope enclosingScope) {
        this.enclosingScope = enclosingScope;
    }

    /**
     * @return the scope that encloses this item 
     */
    public AstScope getEnclosingScope() {
        assert enclosingScope != null : getSimpleName() + ": Each element should set enclosing scope!, except native TypeRef";
        return enclosingScope;
    }

    public void htmlFormat(HtmlFormatter formatter) {
    }

    public String getMimeType() {
        return ScalaMimeResolver.MIME_TYPE;
    }

    public boolean signatureEquals(ElementHandle handle) {
        // XXX TODO
        return false;
    }

    public void addModifier(String modifier) {
        if (mods == null) {
            mods = new HashSet<Modifier>();
        }
        Modifier mod = null;
        if (modifier.equals("private")) {
            mod = Modifier.PRIVATE;
        } else if (modifier.equals("protected")) {
            mod = Modifier.PROTECTED;
        } else {
            mod = Modifier.PUBLIC;
        }
        mods.add(mod);

    }

    public Set<Modifier> getModifiers() {
        return mods == null ? Collections.<Modifier>emptySet() : mods;
    }

    public String getIn() {
        Template enclosingTemplate = getEnclosingDef(Template.class);
        if (enclosingTemplate != null) {
            return enclosingTemplate.getQualifiedName().toString();
        } else {
            return "";
        }
    }

    protected static class AstName implements Name {

        private CharSequence name;

        public AstName(CharSequence name) {
            assert name != null : "AstName should not be null";
            this.name = name;
        }

        public char charAt(int index) {
            return name.charAt(index);
        }

        public boolean contentEquals(CharSequence arg0) {
            return name.toString().contentEquals(arg0.toString());
        }

        public int length() {
            return name.length();
        }

        public CharSequence subSequence(int start, int end) {
            return name.subSequence(start, end);
        }

        /** 
         * @Notice it work for AstName.equals(Name), but we can not sure Name.equals(AstName), 
         * so, we should use Name.toString().equals(Name) instead */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Name) {
                return contentEquals((Name) obj);
            } else if (obj instanceof CharSequence) {
                return contentEquals((CharSequence) obj);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return name.toString().hashCode();
        }

        @Override
        public String toString() {
            return name.toString();
        }
    }
}
