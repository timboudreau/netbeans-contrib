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
package org.netbeans.modules.php.prado.embedding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenHierarchyEvent;
import org.netbeans.api.lexer.TokenHierarchyListener;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.gsf.api.EditHistory;
import org.netbeans.modules.gsf.api.IncrementalEmbeddingModel;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.prado.lexer.PageTokenId;
import org.openide.util.Exceptions;

/**
 *
 * @author Petr Pisl
 */
public class PradoPhpModel {

    private static final Logger LOGGER = Logger.getLogger(PradoPhpModel.class.getName());
    private static final boolean LOG = LOGGER.isLoggable(Level.FINE);
    protected final Document doc;
    protected final ArrayList<CodeBlockData> codeBlocks = new ArrayList<CodeBlockData>();
    protected String code;
    protected boolean documentDirty = true;
    /** Caching */
    protected int prevAstOffset; // Don't need to initialize: the map 0 => 0 is correct
    /** Caching */
    protected int prevLexOffset;

    public static PradoPhpModel get(Document doc) {
        PradoPhpModel model = (PradoPhpModel) doc.getProperty(PradoPhpModel.class);
        if (model == null) {
            model = new PradoPhpModel(doc);
            doc.putProperty(PradoPhpModel.class, model);
        }

        return model;
    }

    protected PradoPhpModel(Document doc) {
        this.doc = doc;

        if (doc != null) { // null in some unit tests
            TokenHierarchy hi = TokenHierarchy.get(doc);
            hi.addTokenHierarchyListener(new TokenHierarchyListener() {

                public void tokenHierarchyChanged(TokenHierarchyEvent evt) {
                    documentDirty = true;
                }
            });
        }
    }

    public String getCode() {
        if (documentDirty) {
            documentDirty = false;

            codeBlocks.clear();
            StringBuilder buffer = new StringBuilder();

            BaseDocument d = (BaseDocument) doc;
            try {
                d.readLock();
                TokenHierarchy<Document> tokenHierarchy = TokenHierarchy.get(doc);
                TokenSequence ts = tokenHierarchy.tokenSequence();
                HashMap<String, Object> state = new HashMap<String, Object>(6);
                extractPHP(ts, buffer, state);
            } finally {
                d.readUnlock();
            }
            code = buffer.toString();
        }

        //System.out.println(dumpCode());

        if (LOG) {
            LOGGER.log(Level.FINE, dumpCode());

        }

        return code;
    }

    protected void extractPHP(TokenSequence<PageTokenId> ts, StringBuilder buffer, HashMap<String, Object> state) {
        buffer.append("<?php\n");
        buffer.append("class ");
        buffer.append("DocName");
        buffer.append(" extends TPage {\n");
        buffer.append("/**\n* @var TActiveLabel\n*/\n");
        buffer.append("private $total;\n\n");

        int phpMethodCounter = 1;
        while (ts.moveNext()) {
            Token<PageTokenId> pageToken = ts.token();
            TokenId pageId = pageToken.id();
            if (pageId == PageTokenId.T_TEMPLATE_CONTROL) {
                //System.out.println("%%%%TemplateCOntrol: " + pageToken.text());
            }
            if (pageId == PageTokenId.T_PHP) {
                //System.out.println("%%%%php: " + pageToken.text());
                buffer.append("protected function phpMethod");   //NOI18N
                buffer.append(phpMethodCounter);
                buffer.append("() {\n");        //NOI18N
                int sourceStart = ts.offset();
                String text = pageToken.text().toString();
                int sourceEnd = sourceStart + text.length();
                int generatedStart = buffer.length();
                buffer.append(text);
                int generatedEnd = buffer.length();
                CodeBlockData blockData = new CodeBlockData(sourceStart, sourceEnd, generatedStart,
                        generatedEnd);
                codeBlocks.add(blockData);
                if (!text.trim().endsWith(";")) {   //NOI18N
                    buffer.append(";"); //NOI18N
                }
                buffer.append("\n}\n"); //NOI18N
                phpMethodCounter++;
            }
        }
        buffer.append("}\n");   //NOI18N
    }

    //TODO: move this out to a generic suppport
    public int sourceToGeneratedPos(int sourceOffset) {
        // Caching
        if (prevLexOffset == sourceOffset) {
            return prevAstOffset;
        }
        prevLexOffset = sourceOffset;

        // TODO - second level of caching on the code block to catch
        // nearby searches

        // Not checking dirty flag here; sourceToGeneratedPos() should apply
        // to the positions as they were when we generated the ruby code

        CodeBlockData codeBlock = getCodeBlockAtSourceOffset(sourceOffset);

        if (codeBlock == null) {
            return -1; // no embedded java code at the offset
        }

        int offsetWithinBlock = sourceOffset - codeBlock.sourceStart;
        int generatedOffset = codeBlock.generatedStart + offsetWithinBlock;
        if (generatedOffset <= codeBlock.generatedEnd) {
            prevAstOffset = generatedOffset;
        } else {
            prevAstOffset = codeBlock.generatedEnd;
        }

        return prevAstOffset;
    }

    public int generatedToSourcePos(int generatedOffset) {
        // Caching
        if (prevAstOffset == generatedOffset) {
            return prevLexOffset;
        }
        prevAstOffset = generatedOffset;
        // TODO - second level of caching on the code block to catch
        // nearby searches


        // Not checking dirty flag here; generatedToSourcePos() should apply
        // to the positions as they were when we generated the ruby code

        CodeBlockData codeBlock = getCodeBlockAtGeneratedOffset(generatedOffset);

        if (codeBlock == null) {
            return -1; // no embedded java code at the offset
        }

        int offsetWithinBlock = generatedOffset - codeBlock.generatedStart;
        int sourceOffset = codeBlock.sourceStart + offsetWithinBlock;
        if (sourceOffset <= codeBlock.sourceEnd) {
            prevLexOffset = sourceOffset;
        } else {
            prevLexOffset = codeBlock.sourceEnd;
        }

        return prevLexOffset;
    }

    private CodeBlockData getCodeBlockAtSourceOffset(int offset) {
        for (int i = 0; i < codeBlocks.size(); i++) {
            CodeBlockData codeBlock = codeBlocks.get(i);
            if (codeBlock.sourceStart <= offset && codeBlock.sourceEnd > offset) {
                return codeBlock;
            } else if (codeBlock.sourceEnd == offset) {
                //test if there the following code blocks starts with the same offset
                if (i < codeBlocks.size() - 1) {
                    CodeBlockData next = codeBlocks.get(i + 1);
                    if (next.sourceStart == offset) {
                        return next;
                    } else {
                        return codeBlock;
                    }
                } else {
                    //the code block is last element, return it
                    return codeBlock;
                }
            }
        }


        return null;
    }

    private CodeBlockData getCodeBlockAtGeneratedOffset(int offset) {
        for (int i = 0; i < codeBlocks.size(); i++) {
            CodeBlockData codeBlock = codeBlocks.get(i);
            if (codeBlock.generatedStart <= offset && codeBlock.generatedEnd > offset) {
                return codeBlock;
            } else if (codeBlock.generatedEnd == offset) {
                //test if there the following code blocks starts with the same offset
                if (i < codeBlocks.size() - 1) {
                    CodeBlockData next = codeBlocks.get(i + 1);
                    if (next.generatedStart == offset) {
                        return next;
                    } else {
                        return codeBlock;
                    }
                } else {
                    //the code block is last element, return it
                    return codeBlock;
                }
            }
        }


        return null;
    }

    IncrementalEmbeddingModel.UpdateState incrementalUpdate(EditHistory history) {
        // Clear cache
        // prevLexOffset = prevAstOffset = 0;
        prevLexOffset = history.convertOriginalToEdited(prevLexOffset);

        int offset = history.getStart();
        int limit = history.getOriginalEnd();
        int delta = history.getSizeDelta();

        boolean codeOverlaps = false;
        for (CodeBlockData codeBlock : codeBlocks) {
            // Block not affected by move
            if (codeBlock.sourceEnd <= offset) {
                continue;
            }
            if (codeBlock.sourceStart >= limit) {
                codeBlock.sourceStart += delta;
                codeBlock.sourceEnd += delta;
                continue;
            }
            if (codeBlock.sourceStart <= offset && codeBlock.sourceEnd >= limit) {
                codeBlock.sourceEnd += delta;
                codeOverlaps = true;
                continue;
            }
            return IncrementalEmbeddingModel.UpdateState.FAILED;
        }

        return codeOverlaps ? IncrementalEmbeddingModel.UpdateState.UPDATED : IncrementalEmbeddingModel.UpdateState.COMPLETED;
    }

    protected String dumpCode() {
        StringBuilder sb = new StringBuilder();
        sb.append("PHP Page Virtual Source:\n");
        sb.append("-------------------\n");
        sb.append(code);
        sb.append('\n');
        sb.append("-------------------\n");
        return sb.toString();
    }

    protected class CodeBlockData {

        /** Start of section in RHTML file */
        private int sourceStart;
        /** End of section in RHTML file */
        private int sourceEnd;
        /** Start of section in generated Js */
        private int generatedStart;
        /** End of section in generated Js */
        private int generatedEnd;

        public CodeBlockData(int sourceStart, int sourceEnd, int generatedStart, int generatedEnd) {
            this.sourceStart = sourceStart;
            this.generatedStart = generatedStart;
            this.sourceEnd = sourceEnd;
            this.generatedEnd = generatedEnd;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("CodeBlockData[");
            sb.append("\n  SOURCE(" + sourceStart + "," + sourceEnd + ")");
            sb.append("=\"");
            try {
                sb.append(doc.getText(sourceStart, sourceEnd - sourceStart));
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            sb.append("\"");
            sb.append(",\n  CSS(" + generatedStart + "," + generatedEnd + ")");
            sb.append("=\"");
            sb.append(code.substring(generatedStart, generatedEnd));
            sb.append("\"");
            sb.append("]");

            return sb.toString();
        }
    }
}
