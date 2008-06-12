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

import java.util.HashSet;
import java.util.Set;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.Modifier;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Caoyuan Deng
 */
public class GsfElement implements ElementHandle {

    private AstNode node;
    private ElementKind kind;
    private Set<Modifier> modifiers;

    public GsfElement(AstNode node) {
        this.node = node;
    }

    public GsfElement(ElementKind kind) {
        this.kind = kind;
    }

    public FileObject getFileObject() {
        return node.getFileObject();
    }

    public String getIn() {
        return node.getIn();
    }

    public ElementKind getKind() {
        if (kind == null) {
            kind = getGsfKind(node);
        }
        return kind;
    }

    public String getMimeType() {
        return node.getMimeType();
    }

    public Set<Modifier> getModifiers() {
        if (modifiers == null) {
            modifiers = getGsfModifiers(node);
        }

        return modifiers;
    }

    public String getName() {
        return node.getName().toString();
    }

    public boolean signatureEquals(ElementHandle handle) {
        return false;
    }

    public static ElementKind getGsfKind(AstNode node) {
        if (node instanceof AstDef) {
            switch (((AstDef) node).getKind()) {
                case CLASS:
                    return ElementKind.CLASS;
                case CONSTRUCTOR:
                    return ElementKind.CONSTRUCTOR;
                case ENUM:
                    return ElementKind.CLASS;
                case ENUM_CONSTANT:
                    return ElementKind.CONSTANT;
                case EXCEPTION_PARAMETER:
                    return ElementKind.OTHER;
                case FIELD:
                    return ElementKind.FIELD;
                case INTERFACE:
                    return ElementKind.MODULE;
                case LOCAL_VARIABLE:
                    return ElementKind.VARIABLE;
                case METHOD:
                    return ElementKind.METHOD;
                case OTHER:
                    return ElementKind.OTHER;
                case PACKAGE:
                    return ElementKind.PACKAGE;
                case PARAMETER:
                    return ElementKind.PARAMETER;
                case TYPE_PARAMETER:
                    return ElementKind.CLASS;
                default:
                    return ElementKind.OTHER;
            }
        } else if (node instanceof AstRef) {
            AstScope scope = node.getEnclosingScope();
            if (scope != null) {
                AstDef def = scope.findDef(node);
                if (def != null) {
                    return getGsfKind(def);
                }
            }
        }
        
        return ElementKind.OTHER;
    }

    public static Set<Modifier> getGsfModifiers(AstNode node) {
        Set<Modifier> modifiers = new HashSet<Modifier>();

        for (javax.lang.model.element.Modifier mod : node.getModifiers()) {
            switch (mod) {
                case PRIVATE:
                    modifiers.add(Modifier.PRIVATE);
                    continue;
                case PROTECTED:
                    modifiers.add(Modifier.PROTECTED);
                    continue;
                case PUBLIC:
                    modifiers.add(Modifier.PUBLIC);
                    continue;
                default:
                    continue;
            }
        }

        return modifiers;
    }
}
