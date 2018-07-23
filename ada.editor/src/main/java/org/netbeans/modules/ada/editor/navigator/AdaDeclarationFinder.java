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

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.nodes.With;
import org.netbeans.modules.ada.editor.indexer.IndexedElement;
import org.netbeans.modules.ada.editor.lexer.AdaTokenId;
import org.netbeans.modules.ada.editor.navigator.SemiAttribute.AttributedElement.Kind;
import org.netbeans.modules.csl.api.DeclarationFinder;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.spi.ParseException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * Based on org.netbeans.modules.php.editor.nav.DeclarationFinderImpl (Jan Lahoda)
 *
 * @author Andrea Lucarelli
 */
public class AdaDeclarationFinder implements DeclarationFinder {

    public DeclarationLocation findDeclaration(ParserResult info, int caretOffset) {
        return findDeclarationImpl(info, caretOffset);
    }

    @Override
    public OffsetRange getReferenceSpan(Document doc, final int caretOffset) {
        List<TokenSequence<?>> ets = TokenHierarchy.get(doc).embeddedTokenSequences(caretOffset, false);
        boolean inDocComment = false;
        ets = new LinkedList<TokenSequence<?>>(ets);

        Collections.reverse(ets);

        for (TokenSequence<?> ts : ets) {
            if (ts.language() == AdaTokenId.language()) {
                Token<?> t = ts.token();

                if (t.id() == AdaTokenId.IDENTIFIER || t.id() == AdaTokenId.STRING_LITERAL) {
                    return new OffsetRange(ts.offset(), ts.offset() + t.length());
                }
            }
        }

        //XXX: to find out includes, we need to parse - but this means we are parsing on mouse move in AWT!:
        FileObject file = NavUtils.getFile(doc);
        final OffsetRange[] result = new OffsetRange[1];

        if (!inDocComment) {
            if (file != null) {
                try {
                    ParserManager.parse(Collections.singleton(Source.create(doc)), new UserTask() {

                        public void cancel() {
                        }

                        public void run(ResultIterator resultIterator) throws Exception {
                            ParserResult parameter = (ParserResult) resultIterator.getParserResult();
                            List<ASTNode> path = NavUtils.underCaret(parameter, caretOffset);

                            if (path.size() == 0) {
                                return;
                            }

                            path = new LinkedList<ASTNode>(path);

                            Collections.reverse(path);

                            // TODO: see base file
                        }
                    });
                } catch (ParseException e) {
                    Exceptions.printStackTrace(e);
                }

                if (result[0] != null) {
                    return result[0];
                }
            }
        }

        return OffsetRange.NONE;
    }

    static DeclarationLocation findDeclarationImpl(ParserResult info, final int offset) {
        List<ASTNode> path = NavUtils.underCaret(info, offset);
        SemiAttribute a = SemiAttribute.semiAttribute(info);//, offset);

        if (path.size() == 0) {
            return DeclarationLocation.NONE;
        }

        path = new LinkedList<ASTNode>(path);

        Collections.reverse(path);

        for (ASTNode n : path) {
            if (n instanceof With) {
                FileObject file = NavUtils.resolveInclude(info, (With) n);

                if (file != null) {
                    return new DeclarationLocation(file, 0);
                }

                break;
            }
        }

        return DeclarationLocation.NONE;
    }

    private static final class AlternativeLocationImpl implements AlternativeLocation {

        private final IndexedElement el;
        private final Kind k;
        private final DeclarationLocation l;

        public AlternativeLocationImpl(IndexedElement el, Kind k, DeclarationLocation l) {
            this.el = el;
            this.k = k;
            this.l = l;
        }

        public ElementHandle getElement() {
            return (ElementHandle) el;
        }

        public String getDisplayHtml(HtmlFormatter formatter) {
            formatter.reset();
            ElementKind ek = null;
            switch (k) {
                case SUBPROG_SPEC:
                case SUBPROG_BODY:
                    ek = ElementKind.METHOD;
                    break;
                case PACKAGE_SPEC:
                case PACKAGE_BODY:
                    ek = ElementKind.CLASS;
            }

            if (ek != null) {
                formatter.name(ek, true);
                formatter.appendText(el.getName());
                formatter.name(ek, false);
            } else {
                formatter.appendText(el.getName());
            }

            if (l.getFileObject() != null) {
                formatter.appendText(" in ");
                formatter.appendText(FileUtil.getFileDisplayName(l.getFileObject()));
            }

            return formatter.getText();
        }

        public DeclarationLocation getLocation() {
            return l;
        }

        public int compareTo(AlternativeLocation o) {
            AlternativeLocationImpl i = (AlternativeLocationImpl) o;

            return this.el.getName().compareTo(i.el.getName());
        }
    }
}
