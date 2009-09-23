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
package org.netbeans.modules.ada.editor.navigator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import javax.swing.text.Document;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.ASTUtils;
import org.netbeans.modules.ada.editor.ast.nodes.FormalParameter;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageName;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.Scalar;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramBody;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.TypeDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Variable;
import org.netbeans.modules.ada.editor.ast.nodes.With;
import org.netbeans.modules.ada.editor.ast.nodes.visitors.DefaultVisitor;
import org.netbeans.modules.ada.editor.navigator.SemiAttribute.AttributedElement;
import org.netbeans.modules.csl.spi.ParserResult;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 * Based on org.netbeans.modules.php.editor.nav.NavUtils
 *
 * @author Andrea Lucarelli
 */
public class NavUtils {

    public static List<ASTNode> underCaret(ParserResult info, final int offset) {
        class Result extends Error {

            private Stack<ASTNode> result;

            public Result(Stack<ASTNode> result) {
                this.result = result;
            }

            @Override
            public Throwable fillInStackTrace() {
                return this;
            }
        }
        try {
            new DefaultVisitor() {

                private Stack<ASTNode> s = new Stack<ASTNode>();

                @Override
                public void scan(ASTNode node) {
                    if (node == null) {
                        return;
                    }

                    if (node.getStartOffset() <= offset && offset <= node.getEndOffset()) {
                        s.push(node);
                        super.scan(node);
                        throw new Result(s);
                    }
                }
            }.scan(ASTUtils.getRoot(info));
        } catch (Result r) {
            return new LinkedList<ASTNode>(r.result);
        }

        return Collections.emptyList();
    }

    public static AttributedElement findElement(ParserResult info, List<ASTNode> path, int offset, SemiAttribute a) {
        if (path.size() == 0) {
            return null;
        }

        path = new LinkedList<ASTNode>(path);

        Collections.reverse(path);

        AttributedElement result = null;
        ASTNode previous = null;

        for (ASTNode leaf : path) {

            if (leaf instanceof FormalParameter) {
                FormalParameter param = (FormalParameter) leaf;
                Variable name = param.getParameterName();
                if (name != null && offset < name.getEndOffset()) {
                    return a.getElement(name);
                }
            }
            
            if (leaf instanceof Variable) {
                result = a.getElement(leaf);
                previous = leaf;
                continue;
            }

            if (leaf instanceof TypeDeclaration) {
                result = a.getElement(leaf);
                previous = leaf;
                continue;
            }

            if (leaf instanceof Scalar) {
                AttributedElement e = a.getElement(leaf);

                if (e != null) {
                    return e;
                }
            }

            if (leaf instanceof SubprogramSpecification && ((SubprogramSpecification) leaf).getSubprogramName() == previous) {
                return a.getElement(leaf);
            }

            if (leaf instanceof SubprogramBody && ((SubprogramBody) leaf).getSubprogramSpecification().getSubprogramName() == previous) {
                return a.getElement(leaf);
            }

            if (leaf instanceof PackageSpecification) {
                PackageSpecification cDeclaration = (PackageSpecification) leaf;
                //class declaration
                if (cDeclaration.getName() == previous) {
                    return a.getElement(leaf);
                }
            } else if (leaf instanceof PackageBody) {
                PackageBody iDeclaration = (PackageBody) leaf;
                //class declaration
                if (iDeclaration.getName() == previous) {
                    return a.getElement(leaf);
                }
            }

            if (result != null) {
                return result;
            }

            previous = leaf;
        }

        return null;
    }

    public static boolean isQuoted(String value) {
        return value.length() >= 2 &&
                (value.startsWith("\"") || value.startsWith("'")) &&
                (value.endsWith("\"") || value.endsWith("'"));
    }

    public static String dequote(String value) {
        assert isQuoted(value);

        return value.substring(1, value.length() - 1);
    }

    public static FileObject resolveInclude(ParserResult info, With with) {
        List<PackageName> packages = with.getPackages();

        // TODO: resolve packagename with file

        return null;
    }

    public static FileObject getFile(Document doc) {
        Object o = doc.getProperty(Document.StreamDescriptionProperty);

        if (o instanceof DataObject) {
            DataObject od = (DataObject) o;

            return od.getPrimaryFile();
        }

        return null;
    }
}
