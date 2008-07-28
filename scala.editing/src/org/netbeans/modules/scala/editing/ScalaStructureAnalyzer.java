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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.gsf.api.Modifier;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.StructureItem;
import org.netbeans.modules.gsf.api.StructureScanner;
import org.netbeans.modules.scala.editing.ast.AstDef;
import org.netbeans.modules.scala.editing.ast.AstRootScope;
import org.netbeans.modules.scala.editing.ast.AstScope;
import org.netbeans.modules.scala.editing.lexer.ScalaLexUtilities;
import org.netbeans.modules.scala.editing.lexer.ScalaTokenId;
import org.openide.util.Exceptions;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaStructureAnalyzer implements StructureScanner {

    public static final String NETBEANS_IMPORT_FILE = "__netbeans_import__"; // NOI18N
    private static final String DOT_CALL = ".call"; // NOI18N

    public List<? extends StructureItem> scan(CompilationInfo info) {
        ScalaParserResult pResult = AstUtilities.getParserResult(info);
        if (pResult == null) {
            return Collections.emptyList();
        }

        AstRootScope rootScope = pResult.getRootScope();
        if (rootScope == null) {
            return Collections.emptyList();
        }

        List<StructureItem> items = new ArrayList<StructureItem>(rootScope.getDefs().size());
        scanTopTmpls(rootScope, items, info);

        return items;
    }

    private void scanTopTmpls(AstScope scope, List<StructureItem> items, CompilationInfo info) {
        for (AstDef def : scope.getDefs()) {
            ElementKind kind = def.getKind();
            if (kind == ElementKind.CLASS || kind == ElementKind.MODULE) {
                if (def.getEnclosingScope().isRoot() || def.getEnclosingDef().getKind() == ElementKind.PACKAGE) {
                    items.add(new ScalaStructureItem(def, info));
                }
            }

            scanTopTmpls(def.getBindingScope(), items, info);
        }
    }

    public Map<String, List<OffsetRange>> folds(CompilationInfo info) {
        ScalaParserResult pResult = AstUtilities.getParserResult(info);
        if (pResult == null) {
            return Collections.emptyMap();
        }

        AstRootScope rootScope = pResult.getRootScope();
        if (rootScope == null) {
            return Collections.emptyMap();
        }

        Map<String, List<OffsetRange>> folds = new HashMap<String, List<OffsetRange>>();
        List<OffsetRange> codefolds = new ArrayList<OffsetRange>();
        folds.put("codeblocks", codefolds); // NOI18N

        BaseDocument doc = (BaseDocument) info.getDocument();
        if (doc == null) {
            return Collections.emptyMap();
        }

        TokenHierarchy th = TokenHierarchy.get(doc);
        if (th == null) {
            return Collections.emptyMap();
        }

        // Read-lock due to Token hierarchy use
        doc.readLock();

        List<OffsetRange> commentfolds = new ArrayList<OffsetRange>();
        TokenSequence ts = ScalaLexUtilities.getTokenSequence(th, 1);

        int importStart = 0;
        int importEnd = 0;
        boolean startImportSet = false;

        Stack<Integer[]> comments = new Stack<Integer[]>();
        Stack<Integer> blocks = new Stack<Integer>();

        while (ts.isValid() && ts.moveNext()) {
            Token tk = ts.token();
            if (tk.id() == ScalaTokenId.Import) {
                int offset = ts.offset();
                if (!startImportSet) {
                    importStart = offset;
                    startImportSet = true;
                }
                importEnd = offset;
            } else if (tk.id() == ScalaTokenId.BlockCommentStart || tk.id() == ScalaTokenId.DocCommentStart) {
                int commentStart = ts.offset();
                int commentLines = 0;
                comments.push(new Integer[]{commentStart, commentLines});
            } else if (tk.id() == ScalaTokenId.BlockCommentData || tk.id() == ScalaTokenId.DocCommentData) {
                // does this block comment (per BlockCommentData/DocCommentData per line as lexer) span multiple lines?
                comments.peek()[1] = comments.peek()[1] + 1;
            } else if (tk.id() == ScalaTokenId.BlockCommentEnd || tk.id() == ScalaTokenId.DocCommentEnd) {
                if (!comments.empty()) {
                    Integer[] comment = comments.pop();
                    if (comment[1] > 1) {
                        // multiple lines
                        OffsetRange commentRange = new OffsetRange(comment[0], ts.offset() + tk.length());
                        commentfolds.add(commentRange);
                    }
                }
            } else if (tk.id() == ScalaTokenId.LBrace) {
                int blockStart = ts.offset();
                blocks.push(blockStart);
            } else if (tk.id() == ScalaTokenId.RBrace) {
                if (!blocks.empty()) {
                    int blockStart = blocks.pop();
                    OffsetRange blockRange = new OffsetRange(blockStart, ts.offset() + tk.length());
                    codefolds.add(blockRange);
                }
            }
        }

        doc.readUnlock();

        try {
            /** @see GsfFoldManager#addTree() for suitable fold names. */
            importEnd = Utilities.getRowEnd(doc, importEnd);

            // same strategy here for the import statements: We have to have
            // *more* than one line to fold them.

            if (Utilities.getRowCount(doc, importStart, importEnd) > 1) {
                List<OffsetRange> importfolds = new ArrayList<OffsetRange>();
                OffsetRange range = new OffsetRange(importStart, importEnd);
                importfolds.add(range);
                folds.put("imports", importfolds); // NOI18N
            }

            folds.put("comments", commentfolds); // NOI18N
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }

        return folds;
    }

    /**
     * Usage: addFolds(doc, rootScope.getDefs(), th, folds, codefolds);
     * @Note: needs precise end offset of each defs or scopes
     * Do we need folding code according to AST tree?, it seems lex LBrace/RBrace pair is enough */
    private void addFolds(BaseDocument doc, List<? extends AstDef> defs, TokenHierarchy th,
            Map<String, List<OffsetRange>> folds, List<OffsetRange> codeblocks) throws BadLocationException {

        for (AstDef def : defs) {
            if (def.getSymbol().isPrimaryConstructor()) {
                // don't fold primary constructor
                continue;
            }

            ElementKind kind = def.getKind();
            switch (kind) {
                case FIELD:
                case METHOD:
                case CONSTRUCTOR:
                case CLASS:
                case MODULE:

                    OffsetRange range = AstUtilities.getRange(th, def);
                    System.out.println("floder:" + range + "def: " + def);

                    //System.out.println("### range: " + element + ", " + range.getStart() + ", " + range.getLength());

                    if (kind == ElementKind.METHOD || kind == ElementKind.CONSTRUCTOR ||
                            (kind == ElementKind.FIELD) ||
                            // Only make nested classes/modules foldable, similar to what the java editor is doing
                            (range.getStart() > Utilities.getRowStart(doc, range.getStart())) && kind != ElementKind.FIELD) {

                        int start = range.getStart();
                        // Start the fold at the END of the line behind last non-whitespace, remove curly brace, if any
                        start = Utilities.getRowLastNonWhite(doc, start);
                        if (doc.getChars(start, 1)[0] != '{') {
                            start++;
                        }
                        int end = range.getEnd();
                        if (start != -1 && end != -1 && start < end && end <= doc.getLength()) {
                            range = new OffsetRange(start, end);
                            codeblocks.add(range);
                        }
                    }
                    break;
            }

            List<? extends AstDef> children = def.getBindingScope().getDefs();

            if (children != null && children.size() > 0) {
                addFolds(doc, children, th, folds, codeblocks);
            }
        }
    }

    public Configuration getConfiguration() {
        return null;
    }

    private class ScalaStructureItem implements StructureItem {

        private AstDef def;
        private CompilationInfo info;
        private Document doc;

        private ScalaStructureItem(AstDef def, CompilationInfo info) {
            this.def = def;
            this.info = info;
            this.doc = info.getDocument();

            if (doc == null) {
                ScalaLexUtilities.getDocument(info.getFileObject(), true);
            }
        }

        public String getName() {
            return def.getName().toString();
        }

        public String getSortText() {
            return getName();
        }

        public String getHtml(HtmlFormatter formatter) {
            def.htmlFormat(formatter);
            return formatter.getText();
        }

        public ElementHandle getElementHandle() {
            return def;
        }

        public ElementKind getKind() {
            return def.getKind();
        }

        public Set<Modifier> getModifiers() {
            return def.getModifiers();
        }

        public boolean isLeaf() {
            switch (def.getKind()) {
                case CONSTRUCTOR:
                case METHOD:
                case FIELD:
                case VARIABLE:
                case OTHER:
                case PARAMETER:
                    return true;

                case PACKAGE:
                case MODULE:
                case CLASS:
                    return false;

                default:
                    throw new RuntimeException("Unhandled kind: " + def.getKind());
            }
        }

        public List<? extends StructureItem> getNestedItems() {
            List<AstDef> nested = def.getBindingScope().getDefs();

            if (nested.size() > 0) {
                List<ScalaStructureItem> children = new ArrayList<ScalaStructureItem>(nested.size());

                for (AstDef child : nested) {
                    if (child.getKind() != ElementKind.PARAMETER && child.getKind() != ElementKind.VARIABLE && child.getKind() != ElementKind.OTHER) {
                        children.add(new ScalaStructureItem(child, info));
                    }
                }

                return children;
            } else {
                return Collections.emptyList();
            }
        }

        public long getPosition() {
            /** @Todo: TokenHierarchy.get(doc) may throw NPE, don't why, need further dig
             * NOTE - CompilationInfo.getDocument() can return null - this generally happens when documents
             * are closed or deleted while (a slower) parse tree related task such as navigation/folding
             * is performed. Therefore, you need to make sure doc != null. (TN)
             */
            try {
                TokenHierarchy th = TokenHierarchy.get(doc);
                return def.getBoundsOffset(th);
            } catch (Exception ex) {
                return 0;
            }
        }

        public long getEndPosition() {
            /** @Todo: TokenHierarchy.get(doc) may throw NPE, don't why, need further dig */
            try {
                TokenHierarchy th = TokenHierarchy.get(doc);
                return def.getBoundsEndOffset(th);
            } catch (Exception ex) {
                return 0;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }

            if (!(o instanceof ScalaStructureItem)) {
                return false;
            }

            ScalaStructureItem d = (ScalaStructureItem) o;

            if (def.getKind() != d.def.getKind()) {
                return false;
            }

            if (!getName().equals(d.getName())) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;

            hash = (29 * hash) + ((this.getName() != null) ? this.getName().hashCode() : 0);
            hash = (29 * hash) + ((this.def.getKind() != null) ? this.def.getKind().hashCode() : 0);

            return hash;
        }

        @Override
        public String toString() {
            return getName();
        }

        public ImageIcon getCustomIcon() {
            return null;
        }
    }
}
