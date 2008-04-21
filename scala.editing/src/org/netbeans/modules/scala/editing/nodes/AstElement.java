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
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.gsf.api.Modifier;
import org.netbeans.modules.scala.editing.ScalaMimeResolver;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Caoyuan Deng
 */
public class AstElement implements ElementHandle {

    /** 
     * @Note: 
     * 1. Not all Element has idToken, such as Expr etc.
     * 2. Due to strange behavior of StructureAnalyzer, we can not rely on 
     *    idToken's text as name, idToken may be <null> and idToken.text() 
     *    will return null when an Identifier token modified, seems sync issue
     */
    private Token idToken;
    private String name;
    private ElementKind kind;
    private AstScope enclosingScope;
    private Set<Modifier> mods;
    private TypeRef type;
    protected String qualifiedName;
    private String in;
    
    public AstElement( ElementKind kind) {
        this(null, kind);
    }

    public AstElement(Token idToken, ElementKind kind) {
        this(null, idToken, kind);
    }

    public AstElement(String name, Token idToken, ElementKind kind) {
        this.idToken = idToken;
        this.name = name;
        this.kind = kind;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        if (name == null) {
            assert false : "Should implement getName()";
            throw new UnsupportedOperationException();
        } else {
            return name;
        }
    }

    public void setIdToken(Token idToken) {
        this.idToken = idToken;
    }

    public Token getIdToken() {
        return idToken;
    }
    
    public ElementKind getKind() {
        return kind;
    }

    public String getBinaryName() {
        return getName();
    }

    public String getQualifiedName() {
        if (qualifiedName == null) {
            Packaging packaging = getPackageElement();
            qualifiedName = packaging == null ? getName() : packaging.getName() + "." + getName();
        }
        return qualifiedName;
    }

    public Packaging getPackageElement() {
        return getEnclosingDef(Packaging.class);
    }

    public void setType(TypeRef type) {
        this.type = type;
    }

    public TypeRef getType() {
        return type;
    }

    public <T extends AstDef> T getEnclosingDef(Class<T> clazz) {
        return enclosingScope.getEnclosingDef(clazz);
    }

    /**
     * @Note: enclosingScope will be set when call
     *   {@link AstScope#addDefinition(Definition)} or {@link AstScope#addUsage(Usage)}
     */
    protected void setEnclosingScope(AstScope enclosingScope) {
        this.enclosingScope = enclosingScope;
    }

    /**
     * @return the scope that encloses this item 
     */
    public AstScope getEnclosingScope() {
        assert enclosingScope != null : "Each element should set enclosing scope!";
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

    public FileObject getFileObject() {
        return null;
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
    
    public void setIn(String in) {
        this.in = in;
    }

    public String getIn() {
        return in;
    }

    @Override
    public String toString() {
        return getName() + "(kind=" + getKind() + ", type=" + getType() + ")";
    }
}
