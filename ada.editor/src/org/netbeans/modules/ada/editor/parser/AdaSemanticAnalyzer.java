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
package org.netbeans.modules.ada.editor.parser;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.ada.editor.AdaMimeResolver;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.nodes.Block;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.visitors.DefaultVisitor;
import org.netbeans.modules.gsf.api.ColoringAttributes;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.modules.gsf.api.SemanticAnalyzer;
import org.netbeans.modules.gsf.api.TranslatedSource;

/**
 *
 * @author Andrea Lucarelli
 */
public class AdaSemanticAnalyzer implements SemanticAnalyzer {

    public static final EnumSet<ColoringAttributes> UNUSED_FIELD_SET = EnumSet.of(ColoringAttributes.UNUSED, ColoringAttributes.FIELD);
    public static final EnumSet<ColoringAttributes> UNUSED_STATIC_FIELD_SET = EnumSet.of(ColoringAttributes.UNUSED, ColoringAttributes.FIELD, ColoringAttributes.STATIC);
    public static final EnumSet<ColoringAttributes> UNUSED_METHOD_SET = EnumSet.of(ColoringAttributes.UNUSED, ColoringAttributes.METHOD);
    public static final EnumSet<ColoringAttributes> STATIC_METHOD_SET = EnumSet.of(ColoringAttributes.STATIC, ColoringAttributes.METHOD);
    public static final EnumSet<ColoringAttributes> UNUSED_STATIC_METHOD_SET = EnumSet.of(ColoringAttributes.STATIC, ColoringAttributes.METHOD, ColoringAttributes.UNUSED);
    private boolean cancelled;
    private Map<OffsetRange, Set<ColoringAttributes>> semanticHighlights;

    public AdaSemanticAnalyzer() {
        semanticHighlights = null;
    }

    public Map<OffsetRange, Set<ColoringAttributes>> getHighlights() {
        return semanticHighlights;
    }

    public void cancel() {
        cancelled = true;
    }

    public void run(CompilationInfo compilationInfo) throws Exception {
        resume();

        if (isCancelled()) {
            return;
        }

        AdaParseResult result = getParseResult(compilationInfo);
        Map<OffsetRange, Set<ColoringAttributes>> highlights =
                new HashMap<OffsetRange, Set<ColoringAttributes>>(100);

        if (result.getProgram() != null) {
            result.getProgram().accept(new SemanticHighlightVisitor(highlights, result.getTranslatedSource()));

            if (highlights.size() > 0) {
                semanticHighlights = highlights;
            } else {
                semanticHighlights = null;
            }
        }
    }

    protected final synchronized boolean isCancelled() {
        return cancelled;
    }

    protected final synchronized void resume() {
        cancelled = false;
    }

    private AdaParseResult getParseResult(CompilationInfo info) {
        ParserResult result = info.getEmbeddedResult(AdaMimeResolver.ADA_MIME_TYPE, 0);

        if (result == null) {
            return null;
        } else {
            return ((AdaParseResult) result);
        }
    }

    private class SemanticHighlightVisitor extends DefaultVisitor {

        private class IdentifierColoring {

            public Identifier identifier;
            public Set<ColoringAttributes> coloring;

            public IdentifierColoring(Identifier identifier, Set<ColoringAttributes> coloring) {
                this.identifier = identifier;
                this.coloring = coloring;
            }
        }
        Map<OffsetRange, Set<ColoringAttributes>> highlights;
        // for unused private fields: name, varible
        // if isused, then it's deleted from the list and marked as the field
        private final Map<String, IdentifierColoring> privateFieldsUsed;
        // for unsed private method: name, identifier
        private final Map<String, IdentifierColoring> privateMethod;
        // this is holder of blocks, which has to be scanned for usages in the class.
        private List<Block> needToScan = new ArrayList<Block>();
        private final TranslatedSource translatedSource;

        public SemanticHighlightVisitor(Map<OffsetRange, Set<ColoringAttributes>> highlights, TranslatedSource translatedSource) {
            this.highlights = highlights;
            privateFieldsUsed = new HashMap<String, IdentifierColoring>();
            privateMethod = new HashMap<String, IdentifierColoring>();
            this.translatedSource = translatedSource;
        }

        private void addOffsetRange(ASTNode node, Set<ColoringAttributes> coloring) {
            if (translatedSource == null) {
                highlights.put(new OffsetRange(node.getStartOffset(), node.getEndOffset()), coloring);
            } else {
                int start = translatedSource.getLexicalOffset(node.getStartOffset());
                if (start > -1) {
                    int end = start + node.getEndOffset() - node.getStartOffset();
                    highlights.put(new OffsetRange(start, end), coloring);
                }
            }
        }

        @Override
        public void visit(PackageSpecification node) {
            if (isCancelled()) {
                return;
            }
            Identifier name = node.getName();
            addOffsetRange(name, ColoringAttributes.CLASS_SET);
            node.getBody().accept(this);
        }

        @Override
        public void visit(PackageBody pkgbdy) {
            if (isCancelled()) {
                return;
            }
            Identifier name = pkgbdy.getName();
            addOffsetRange(name, ColoringAttributes.CLASS_SET);
            needToScan = new ArrayList<Block>();
            if (pkgbdy.getBody() != null) {
                pkgbdy.getBody().accept(this);

                // find all usages in the method bodies
                for (Block block : needToScan) {
                    block.accept(this);
                }
                // are there unused private fields?
                for (IdentifierColoring item : privateFieldsUsed.values()) {
                    if (item.coloring.contains(ColoringAttributes.STATIC)) {
                        addOffsetRange(item.identifier, UNUSED_STATIC_FIELD_SET);
                    } else {
                        addOffsetRange(item.identifier, UNUSED_FIELD_SET);
                    }

                }

                // are there unused private methods?
                for (IdentifierColoring item : privateMethod.values()) {
                    if (item.coloring.contains(ColoringAttributes.STATIC)) {
                        addOffsetRange(item.identifier, UNUSED_STATIC_METHOD_SET);
                    } else {
                        addOffsetRange(item.identifier, UNUSED_METHOD_SET);
                    }
                }
            }
        }
    }
}
