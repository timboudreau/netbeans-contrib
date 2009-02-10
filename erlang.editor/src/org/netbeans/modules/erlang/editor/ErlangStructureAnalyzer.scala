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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.erlang.editor;

import _root_.java.util.{ArrayList, Collections, HashMap, List, Map, Set}
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.csl.api.{ElementHandle,
                                     ElementKind,
                                     HtmlFormatter,
                                     Modifier,
                                     OffsetRange,
                                     StructureItem,
                                     StructureScanner}
import org.netbeans.modules.csl.api.StructureScanner._
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.erlang.editor.ast.{AstDfn, AstRootScope, AstScope}
import org.netbeans.modules.erlang.editor.lexer.{ErlangTokenId, LexUtil}
import org.openide.util.Exceptions


/**
 *
 * @author Caoyuan Deng
 */
class ErlangStructureAnalyzer extends StructureScanner {

    val NETBEANS_IMPORT_FILE = "__netbeans_import__"; // NOI18N
    val DOT_CALL = ".call"; // NOI18N

    override
    def scan(result:ParserResult) :List[StructureItem] = result match {
        case null => Collections.emptyList[StructureItem]
        case pResult:ErlangParserResult => pResult.rootScope match {
                case null => Collections.emptyList[StructureItem]
                case rootScope =>
                    val items = new ArrayList[StructureItem](rootScope.dfns.size)
                    scanTopForms(rootScope, items, pResult)

                    items;
            }
    }

    override
    def folds(result:ParserResult) :Map[String, List[OffsetRange]] = {
        Collections.emptyMap[String, List[OffsetRange]]
    }

    override
    def getConfiguration :Configuration = null

    private def scanTopForms(scope:AstScope, items:List[StructureItem], pResult:ErlangParserResult) :Unit = {
        for (dfn <- scope.dfns) {
            dfn.getKind match {
                case ElementKind.ATTRIBUTE | ElementKind.METHOD => items.add(new ErlangStructureItem(dfn, pResult))
                case _ =>
            }
            scanTopForms(dfn.bindingScope, items, pResult)
        }
    }

    //    public Map<String, List<OffsetRange>> folds(CompilationInfo info) {
    //        ScalaParserResult pResult = AstUtilities.getParserResult(info);
    //        if (pResult == null) {
    //            return Collections.emptyMap();
    //        }
    //
    //        AstRootScope rootScope = pResult.getRootScope();
    //        if (rootScope == null) {
    //            return Collections.emptyMap();
    //        }
    //
    //        Map<String, List<OffsetRange>> folds = new HashMap<String, List<OffsetRange>>();
    //        List<OffsetRange> codefolds = new ArrayList<OffsetRange>();
    //        folds.put("codeblocks", codefolds); // NOI18N
    //
    //        BaseDocument doc = (BaseDocument) info.getDocument();
    //        if (doc == null) {
    //            return Collections.emptyMap();
    //        }
    //
    //        TokenHierarchy th = TokenHierarchy.get(doc);
    //        if (th == null) {
    //            return Collections.emptyMap();
    //        }
    //
    //        // Read-lock due to Token hierarchy use
    //        doc.readLock();
    //
    //        List<OffsetRange> commentfolds = new ArrayList<OffsetRange>();
    //        TokenSequence ts = ScalaLexUtilities.getTokenSequence(th, 1);
    //
    //        int importStart = 0;
    //        int importEnd = 0;
    //        boolean startImportSet = false;
    //
    //        Stack<Integer[]> comments = new Stack<Integer[]>();
    //        Stack<Integer> blocks = new Stack<Integer>();
    //
    //        while (ts.isValid() && ts.moveNext()) {
    //            Token tk = ts.token();
    //            if (tk.id() == ScalaTokenId.Import) {
    //                int offset = ts.offset();
    //                if (!startImportSet) {
    //                    importStart = offset;
    //                    startImportSet = true;
    //                }
    //                importEnd = offset;
    //            } else if (tk.id() == ScalaTokenId.BlockCommentStart || tk.id() == ScalaTokenId.DocCommentStart) {
    //                int commentStart = ts.offset();
    //                int commentLines = 0;
    //                comments.push(new Integer[]{commentStart, commentLines});
    //            } else if (tk.id() == ScalaTokenId.BlockCommentData || tk.id() == ScalaTokenId.DocCommentData) {
    //                // does this block comment (per BlockCommentData/DocCommentData per line as lexer) span multiple lines?
    //                comments.peek()[1] = comments.peek()[1] + 1;
    //            } else if (tk.id() == ScalaTokenId.BlockCommentEnd || tk.id() == ScalaTokenId.DocCommentEnd) {
    //                if (!comments.empty()) {
    //                    Integer[] comment = comments.pop();
    //                    if (comment[1] > 1) {
    //                        // multiple lines
    //                        OffsetRange commentRange = new OffsetRange(comment[0], ts.offset() + tk.length());
    //                        commentfolds.add(commentRange);
    //                    }
    //                }
    //            } else if (tk.id() == ScalaTokenId.LBrace) {
    //                int blockStart = ts.offset();
    //                blocks.push(blockStart);
    //            } else if (tk.id() == ScalaTokenId.RBrace) {
    //                if (!blocks.empty()) {
    //                    int blockStart = blocks.pop();
    //                    OffsetRange blockRange = new OffsetRange(blockStart, ts.offset() + tk.length());
    //                    codefolds.add(blockRange);
    //                }
    //            }
    //        }
    //
    //        doc.readUnlock();
    //
    //        try {
    //            /** @see GsfFoldManager#addTree() for suitable fold names. */
    //            importEnd = Utilities.getRowEnd(doc, importEnd);
    //
    //            // same strategy here for the import statements: We have to have
    //            // *more* than one line to fold them.
    //
    //            if (Utilities.getRowCount(doc, importStart, importEnd) > 1) {
    //                List<OffsetRange> importfolds = new ArrayList<OffsetRange>();
    //                OffsetRange range = new OffsetRange(importStart, importEnd);
    //                importfolds.add(range);
    //                folds.put("imports", importfolds); // NOI18N
    //            }
    //
    //            folds.put("comments", commentfolds); // NOI18N
    //        } catch (BadLocationException ex) {
    //            Exceptions.printStackTrace(ex);
    //        }
    //
    //        return folds;
    //    }

    /**
     * Usage: addFolds(doc, rootScope.getDefs(), th, folds, codefolds);
     * @Note: needs precise end offset of each defs or scopes
     * Do we need folding code according to AST tree?, it seems lex LBrace/RBrace pair is enough */
    //    private void addFolds(BaseDocument doc, List<? extends AstDef> defs, TokenHierarchy th,
    //                          Map<String, List<OffsetRange>> folds, List<OffsetRange> codeblocks) throws BadLocationException {
    //
    //        for (AstDef def : defs) {
    //            if (def.getSymbol().isPrimaryConstructor()) {
    //                // don't fold primary constructor
    //                continue;
    //            }
    //
    //            ElementKind kind = def.getKind();
    //            switch (kind) {
    //                case FIELD:
    //                case METHOD:
    //                case CONSTRUCTOR:
    //                case CLASS:
    //                case MODULE:
    //
    //                    OffsetRange range = AstUtilities.getRange(th, def);
    //                    System.out.println("floder:" + range + "def: " + def);
    //
    //                    //System.out.println("### range: " + element + ", " + range.getStart() + ", " + range.getLength());
    //
    //                    if (kind == ElementKind.METHOD || kind == ElementKind.CONSTRUCTOR ||
    //                        (kind == ElementKind.FIELD) ||
    //                        // Only make nested classes/modules foldable, similar to what the java editor is doing
    //                        (range.getStart() > Utilities.getRowStart(doc, range.getStart())) && kind != ElementKind.FIELD) {
    //
    //                        int start = range.getStart();
    //                        // Start the fold at the END of the line behind last non-whitespace, remove curly brace, if any
    //                        start = Utilities.getRowLastNonWhite(doc, start);
    //                        if (doc.getChars(start, 1)[0] != '{') {
    //                            start++;
    //                        }
    //                        int end = range.getEnd();
    //                        if (start != -1 && end != -1 && start < end && end <= doc.getLength()) {
    //                            range = new OffsetRange(start, end);
    //                            codeblocks.add(range);
    //                        }
    //                    }
    //                    break;
    //            }
    //
    //            List<? extends AstDef> children = def.getBindingScope().getDefs();
    //
    //            if (children != null && children.size() > 0) {
    //                addFolds(doc, children, th, folds, codeblocks);
    //            }
    //        }
    //    }

    private class ErlangStructureItem(val dfn:AstDfn, info:ErlangParserResult) extends StructureItem {
        import ElementKind._

        private val doc:Document = info.snapshot.getSource.getDocument(false)

        override
        def getName :String = dfn.getName

        override
        def getSortText :String = getName

        override
        def getHtml(formatter:HtmlFormatter) :String = {
            formatter.reset
            formatter.appendText(getName);
            formatter.getText
        }

        override
        def getElementHandle :ElementHandle = dfn

        override
        def getKind :ElementKind = dfn.getKind
        

        override
        def getModifiers :Set[Modifier] = dfn.getModifiers

        override
        def isLeaf :Boolean = dfn.getKind match {
            case CONSTRUCTOR | METHOD | FIELD | VARIABLE | OTHER | PARAMETER | ATTRIBUTE => true
            case PACKAGE => true // the enclosed defs should has been processed in scanTopTmpls
            case MODULE | CLASS => false
            case _ => throw new RuntimeException("Unhandled kind: " + dfn.getKind)
        }

        override
        def getNestedItems : List[StructureItem] = {
            val nested = dfn.bindingScope.dfns

            if (nested.size > 0) {
                val children = new ArrayList[StructureItem](nested.size)

                for (child <- nested) {
                    child.kind match {
                        case PARAMETER | VARIABLE | OTHER =>
                        case _ => children.add(new ErlangStructureItem(child, info))
                    }
                }

                children
            } else {
                Collections.emptyList[StructureItem]
            }
        }

        override
        def getPosition :Long = {
            /**
             * @Todo: TokenHierarchy.get(doc) may throw NPE, don't know why, need further dig
             * NOTE - CompilationInfo.getDocument() can return null - this generally happens when documents
             * are closed or deleted while (a slower) parse tree related task such as navigation/folding
             * is performed. Therefore, you need to make sure doc != null. (TN)
             */
            try {
                val th = TokenHierarchy.get(doc)
                dfn.boundsOffset(th)
            } catch {
                case ex:Exception => 0
            }
        }

        override
        def getEndPosition :Long = {
            /** @Todo: TokenHierarchy.get(doc) may throw NPE, don't why, need further dig */
            try {
                val th = TokenHierarchy.get(doc)
                dfn.boundsEndOffset(th)
            } catch {
                case ex:Exception => 0
            }
        }

        override
        def equals(o:Any) :Boolean = o match {
            case null => false
            case x:ErlangStructureItem if dfn.getKind == x.dfn.getKind && getName.equals(x.getName) => true
            case _ => false
        }

        override
        def hashCode :Int = {
            var hash = 7

            hash = (29 * hash) + (if (getName != null) getName.hashCode else 0)
            hash = (29 * hash) + (if (dfn.getKind != null) dfn.getKind.hashCode else 0)

            hash
        }

        override
        def toString = getName

        override
        def getCustomIcon :ImageIcon = null
    }
}
