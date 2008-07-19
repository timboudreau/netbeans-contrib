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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.scala.editing;

import org.netbeans.modules.scala.editing.ast.ScalaElement;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.swing.text.Document;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.DeclarationFinder;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.scala.editing.ast.AstDef;
import org.netbeans.modules.scala.editing.ast.AstItem;
import org.netbeans.modules.scala.editing.ast.AstScope;
import org.netbeans.modules.scala.editing.lexer.ScalaLexUtilities;
import org.netbeans.modules.scala.editing.lexer.ScalaTokenId;
import org.netbeans.modules.scala.editing.nodes.AstNode;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.netbeans.modules.scala.editing.nodes.FieldCall;
import org.netbeans.modules.scala.editing.nodes.FunctionCall;
import org.netbeans.modules.scala.editing.nodes.types.Type;
import org.openide.filesystems.FileObject;
import scala.tools.nsc.Global;

/**
 * 
 * @author Caoyuan Deng
 */
public class ScalaDeclarationFinder implements DeclarationFinder {

    private static final boolean CHOOSE_ONE_DECLARATION = Boolean.getBoolean("scala.choose_one_decl");

    public OffsetRange getReferenceSpan(Document document, int lexOffset) {
        TokenHierarchy<Document> th = TokenHierarchy.get(document);

        //BaseDocument doc = (BaseDocument)document;

        TokenSequence<ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(th, lexOffset);

        if (ts == null) {
            return OffsetRange.NONE;
        }

        ts.move(lexOffset);

        if (!ts.moveNext() && !ts.movePrevious()) {
            return OffsetRange.NONE;
        }

        // Determine whether the caret position is right between two tokens
        boolean isBetween = (lexOffset == ts.offset());

        OffsetRange range = getReferenceSpan(ts, th, lexOffset);

        if ((range == OffsetRange.NONE) && isBetween) {
            // The caret is between two tokens, and the token on the right
            // wasn't linkable. Try on the left instead.
            if (ts.movePrevious()) {
                range = getReferenceSpan(ts, th, lexOffset);
            }
        }

        return range;
    }

    private OffsetRange getReferenceSpan(TokenSequence<?> ts,
            TokenHierarchy<Document> th, int lexOffset) {
        Token<?> token = ts.token();
        TokenId id = token.id();

        if (id == ScalaTokenId.Identifier) {
            if (token.length() == 1 && id == ScalaTokenId.Identifier && token.text().toString().equals(",")) {
                return OffsetRange.NONE;
            }
        }

        // TODO: Tokens.SUPER, Tokens.THIS, Tokens.SELF ...
        if ((id == ScalaTokenId.Identifier) || (id == ScalaTokenId.GLOBAL_VAR) || (id == ScalaTokenId.CONSTANT)) {
            return new OffsetRange(ts.offset(), ts.offset() + token.length());
        }

        return OffsetRange.NONE;
    }

    public DeclarationLocation findDeclaration(CompilationInfo info, int lexOffset) {

        final BaseDocument doc = (BaseDocument) info.getDocument();
        if (doc == null) {
            return DeclarationLocation.NONE;
        }

        ScalaParserResult pResult = AstUtilities.getParserResult(info);
        Global global = ((ScalaParser) pResult.getParser()).getGlobal();

        doc.readLock();
        try {
            AstScope root = pResult.getRootScope();
            if (root == null) {
                return DeclarationLocation.NONE;
            }

            final int astOffset = AstUtilities.getAstOffset(info, lexOffset);
            if (astOffset == -1) {
                return DeclarationLocation.NONE;
            }

            final TokenHierarchy<Document> th = TokenHierarchy.get((Document) doc);

            ElementHandle foundElement = null;
            boolean isLocal = false;

            AstItem closest = root.findItemAt(th, astOffset);
            AstDef def = root.findDefOf(closest);
            if (def != null) {
                // is local
                int offset = def.getIdOffset(th);
                return new DeclarationLocation(info.getFileObject(), offset, def);
            } else {
                TokenSequence ts = ScalaLexUtilities.getTokenSequence(th, lexOffset);
                ts.move(lexOffset);
                if (!ts.moveNext() && !ts.movePrevious()) {
                    return DeclarationLocation.NONE;
                }
                Token token = ts.token();
                if (token.id() == ScalaTokenId.Identifier) {
                    AstItem item = root.findItemAt(th, token);
                    if (item != null) {
                        foundElement = new ScalaElement(item.getSymbol(), info, global);
                    }
                }
            }

            if (foundElement != null) {
                int offset = 0;
                if (isLocal) {
                    offset = ((AstNode) ((GsfElement) foundElement).getElement()).getPickOffset(th);
                } else {
                    if (foundElement instanceof ScalaElement) {
                        offset = ((ScalaElement) foundElement).getOffset();
                    }
                }

                FileObject fo = foundElement.getFileObject();
                if (fo != null) {
                    return new DeclarationLocation(fo, offset, foundElement);
                }
            }

            return DeclarationLocation.NONE;

        } finally {
            doc.readUnlock();
        }
    }

    /** Locate the method declaration for the given method call */
    GsfElement findMethodDeclaration(CompilationInfo info, FunctionCall funRef, Set<GsfElement>[] alternativesHolder) {
        ScalaParserResult pResult = AstUtilities.getParserResult(info);
        ScalaIndex index = ScalaIndex.get(info);

        GsfElement candidate = null;

        String callName = funRef.getCall().getSimpleName().toString();
        String in = null;
        AstNode base = funRef.getBase();
        if (base != null) {
            TypeMirror baseType = base.asType();
            if (baseType != null) {
                in = Type.qualifiedNameOf(baseType);
            }

            if (in != null) {
                Set<GsfElement> gsfElements = index.getMembers(callName, in, NameKind.PREFIX, ScalaIndex.ALL_SCOPE, pResult, false);
                for (GsfElement gsfElement : gsfElements) {
                    if (gsfElement.getElement() instanceof ExecutableElement) {
                        if (AstElement.isMirroredBy(gsfElement.getElement(), funRef)) {
                            candidate = gsfElement;
                            break;
                        }
                    }
                }
            }

        }

        return candidate;
    }

    GsfElement findFieldDeclaration(CompilationInfo info, FieldCall field, Set<GsfElement>[] alternativesHolder) {
        ScalaParserResult pResult = AstUtilities.getParserResult(info);
        ScalaIndex index = ScalaIndex.get(info);

        GsfElement candidate = null;

        String fieldSName = field.getField().getSimpleName().toString();
        String in = null;
        AstNode base = field.getBase();
        if (base != null) {
            TypeMirror baseType = base.asType();
            if (baseType != null) {
                in = Type.qualifiedNameOf(baseType);
            }

            if (in != null) {
                Set<GsfElement> gsfElements = index.getMembers(fieldSName, in, NameKind.PREFIX, ScalaIndex.ALL_SCOPE, pResult, false);
                for (GsfElement gsfElement : gsfElements) {
                    Element element = gsfElement.getElement();
                    if (!element.getSimpleName().toString().equals(fieldSName)) {
                        continue;
                    }

                    if (element instanceof ExecutableElement) {
                        if (((ExecutableElement) element).getParameters().size() == 0) {
                            candidate = gsfElement;
                            break;
                        }
                    } else if (element instanceof VariableElement) {
                        candidate = gsfElement;
                        break;
                    }
                }
            }

        }

        return candidate;
    }

    GsfElement findTypeDeclaration(CompilationInfo info, Type type) {
        ScalaParserResult pResult = AstUtilities.getParserResult(info);
        ScalaIndex index = ScalaIndex.get(info);

        GsfElement candidate = null;

        String qName = type.asElement().getQualifiedName().toString();

        int lastDot = qName.lastIndexOf('.');
        if (lastDot != -1) {
            // should include "." to narrow the search result?
            String pkgName = qName.substring(0, lastDot + 1);
            String sName = qName.substring(lastDot + 1, qName.length());
            Set<GsfElement> gsfTypes = index.getPackageContent(pkgName, NameKind.PREFIX, ScalaIndex.ALL_SCOPE);
            for (GsfElement gsfType : gsfTypes) {
                IndexedElement idxType = (IndexedElement) gsfType.getElement();
                if (idxType instanceof IndexedTypeElement) {
                    if (idxType.getSimpleName().toString().equals(sName)) {
                        candidate = new GsfElement(idxType, idxType.getFileObject(), info);
                    }
                }
            }
        }
        return candidate;
    }
}
