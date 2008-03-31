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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.scala.editing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.scala.editing.lexer.ScalaLexUtilities;
import org.netbeans.modules.scala.editing.lexer.ScalaTokenId;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;

/**
 * Formatting and indentation.
 *
 *
 * @author Caoyuan Deng
 */
public class ScalaFormatter implements org.netbeans.modules.gsf.api.Formatter {

    private CodeStyle codeStyle;
    private int rightMarginOverride = -1;
    private boolean embeddedJavaScript;
    private int embeddededIndent = 0;
    private static final Map<ScalaTokenId, Set<ScalaTokenId>> BRACE_MATCH_MAP =
            new HashMap<ScalaTokenId, Set<ScalaTokenId>>();
    

    static {
        Set<ScalaTokenId> matchingset = new HashSet<ScalaTokenId>();
        BRACE_MATCH_MAP.put(ScalaTokenId.LParen, matchingset);
        matchingset.add(ScalaTokenId.RParen);

        matchingset = new HashSet<ScalaTokenId>();
        BRACE_MATCH_MAP.put(ScalaTokenId.LBrace, matchingset);
        matchingset.add(ScalaTokenId.RBrace);

        matchingset = new HashSet<ScalaTokenId>();
        BRACE_MATCH_MAP.put(ScalaTokenId.LBracket, matchingset);
        matchingset.add(ScalaTokenId.RBracket);

        matchingset = new HashSet<ScalaTokenId>();
        BRACE_MATCH_MAP.put(ScalaTokenId.Case, matchingset);
        matchingset.add(ScalaTokenId.Case);
        matchingset.add(ScalaTokenId.RBrace);

        matchingset = new HashSet<ScalaTokenId>();
        BRACE_MATCH_MAP.put(ScalaTokenId.If, matchingset);
        matchingset.add(ScalaTokenId.Else);
        matchingset.add(ScalaTokenId.LBrace);
    }

    public ScalaFormatter() {
        this.codeStyle = CodeStyle.getDefault(null);
    }

    public ScalaFormatter(CodeStyle codeStyle, int rightMarginOverride) {
        assert codeStyle != null;
        this.codeStyle = codeStyle;
        this.rightMarginOverride = rightMarginOverride;
    }

    public boolean needsParserResult() {
        return false;
    }

    public void reindent(Document document, int startOffset, int endOffset) {
        reindent(document, startOffset, endOffset, null, true);
    }

    public void reformat(Document document, int startOffset, int endOffset, CompilationInfo info) {
        reindent(document, startOffset, endOffset, info, false);
    }

    public int indentSize() {
        return codeStyle.getIndentSize();
    }

    public int hangingIndentSize() {
        return codeStyle.getContinuationIndentSize();
    }

    /** Compute the initial balance of brackets at the given offset. */
    private int getFormatStableStart(BaseDocument doc, int offset) {
        TokenSequence<? extends ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(doc, offset);
        if (ts == null) {
            return 0;
        }

        ts.move(offset);

        if (!ts.movePrevious()) {
            return 0;
        }

        // Look backwards to find a suitable context - a class, module or method definition
        // which we will assume is properly indented and balanced
        do {
            Token<? extends ScalaTokenId> token = ts.token();
            TokenId id = token.id();

            if (id == ScalaTokenId.Def || id == ScalaTokenId.Object || id == ScalaTokenId.Trait || id == ScalaTokenId.Class) {
                return ts.offset();
            }
        } while (ts.movePrevious());

        if (embeddedJavaScript && !ts.movePrevious()) {
            // I may have moved to the front of an embedded JavaScript area, e.g. in
            // an attribute or in a <script> tag. If this is the end of the line,
            // go to the next line instead since the reindent code will go to the beginning
            // of the stable formatting start.
            int sequenceBegin = ts.offset();
            try {
                int lineTextEnd = Utilities.getRowLastNonWhite(doc, sequenceBegin);
                if (lineTextEnd == -1 || sequenceBegin > lineTextEnd) {
                    return Math.min(doc.getLength(), Utilities.getRowEnd(doc, sequenceBegin) + 1);
                }

            } catch (BadLocationException ble) {
                Exceptions.printStackTrace(ble);
            }
        }

        return ts.offset();
    }

    /**
     * Get the first token on the given line. Similar to LexUtilities.getToken(doc, lineBegin)
     * except (a) it computes the line begin from the offset itself, and more importantly,
     * (b) it handles RHTML tokens specially; e.g. if a line begins with
     * {@code
     *    <% if %>
     * }
     * then the "if" embedded token will be returned rather than the RHTML delimiter, or even
     * the whitespace token (which is the first Ruby token in the embedded sequence).
     *
     * </pre>
     */
    private Token getFirstTokenOnLine(BaseDocument doc, int offset) throws BadLocationException {
        int lineBegin = Utilities.getRowFirstNonWhite(doc, offset);

        if (lineBegin != -1) {
            return ScalaLexUtilities.getToken(doc, lineBegin);
        }

        return null;
    }

    private void reindent(Document document, int startOffset, int endOffset, CompilationInfo info, boolean indentOnly) {


        try {
            BaseDocument doc = (BaseDocument) document; // document.getText(0, document.getLength())

            syncOptions(doc, codeStyle);

            if (endOffset > doc.getLength()) {
                endOffset = doc.getLength();
            }

            startOffset = Utilities.getRowStart(doc, startOffset);
            int lineStart = startOffset; //Utilities.getRowStart(doc, startOffset);

            int initialOffset = 0;
            int initialIndent = 0;
            if (startOffset > 0) {
                int prevOffset = Utilities.getRowStart(doc, startOffset - 1);
                initialOffset = getFormatStableStart(doc, prevOffset);
                initialIndent = ScalaLexUtilities.getLineIndent(doc, initialOffset);
            }

            // Build up a set of offsets and indents for lines where I know I need
            // to adjust the offset. I will then go back over the document and adjust
            // lines that are different from the intended indent. By doing piecemeal
            // replacements in the document rather than replacing the whole thing,
            // a lot of things will work better: breakpoints and other line annotations
            // will be left in place, semantic coloring info will not be temporarily
            // damaged, and the caret will stay roughly where it belongs.
            List<Integer> offsets = new ArrayList<Integer>();
            List<Integer> indents = new ArrayList<Integer>();

            // When we're formatting sections, include whitespace on empty lines; this
            // is used during live code template insertions for example. However, when
            // wholesale formatting a whole document, leave these lines alone.
            boolean indentEmptyLines = startOffset != 0 || endOffset != doc.getLength();

            // In case of indentOnly (use press <enter>), the endOffset will be the 
            // position of newline inserted, to compute the new added line's indent,
            // we need includeEnd.
            boolean includeEnd = endOffset == doc.getLength() || indentOnly;

            // TODO - remove initialbalance etc.
            computeIndents(doc, initialIndent, initialOffset, endOffset, info, offsets, indents, indentEmptyLines, includeEnd);

            try {
                doc.atomicLock();

                // Iterate in reverse order such that offsets are not affected by our edits
                assert indents.size() == offsets.size();
                org.netbeans.editor.Formatter editorFormatter = doc.getFormatter();
                for (int i = indents.size() - 1; i >= 0; i--) {
                    int indent = indents.get(i);
                    int lineBegin = offsets.get(i);

                    if (lineBegin < lineStart) {
                        // We're now outside the region that the user wanted reformatting;
                        // these offsets were computed to get the correct continuation context etc.
                        // for the formatter
                        break;
                    }

                    if (lineBegin == lineStart && i > 0) {
                        // Look at the previous line, and see how it's indented
                        // in the buffer.  If it differs from the computed position,
                        // offset my computed position (thus, I'm only going to adjust
                        // the new line position relative to the existing editing.
                        // This avoids the situation where you're inserting a newline
                        // in the middle of "incorrectly" indented code (e.g. different
                        // size than the IDE is using) and the newline position ending
                        // up "out of sync"
                        int prevOffset = offsets.get(i - 1);
                        int prevIndent = indents.get(i - 1);
                        int actualPrevIndent = ScalaLexUtilities.getLineIndent(doc, prevOffset);
                        if (actualPrevIndent != prevIndent) {
                            // For blank lines, indentation may be 0, so don't adjust in that case
                            if (!(Utilities.isRowEmpty(doc, prevOffset) || Utilities.isRowWhite(doc, prevOffset))) {
                                indent = actualPrevIndent + (indent - prevIndent);
                            }
                        }
                    }

                    // Adjust the indent at the given line (specified by offset) to the given indent
                    int currentIndent = ScalaLexUtilities.getLineIndent(doc, lineBegin);

                    if (currentIndent != indent) {
                        editorFormatter.changeRowIndent(doc, lineBegin, indent);
                    }
                }

                if (!indentOnly && codeStyle.reformatComments()) {
//                    reformatComments(doc, startOffset, endOffset);
                }
            } finally {
                doc.atomicUnlock();
            }
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
        }
    }

    public void computeIndents(BaseDocument doc, int initialIndent, int startOffset, int endOffset, CompilationInfo info, List<Integer> offsets, List<Integer> indents, boolean indentEmptyLines, boolean includeEnd) {
        // PENDING:
        // The reformatting APIs in NetBeans should be lexer based. They are still
        // based on the old TokenID apis. Once we get a lexer version, convert this over.
        // I just need -something- in place until that is provided.
        try {
            // Algorithm:
            // Iterate over the range.
            // Accumulate a token balance ( {,(,[, and keywords like class, case, etc. increases the balance,
            //      },),] and "end" decreases it
            // If the line starts with an end marker, indent the line to the level AFTER the token
            // else indent the line to the level BEFORE the token (the level being the balance * indentationSize)
            // Compute the initial balance and indentation level and use that as a "base".
            // If the previous line is not "done" (ends with a comma or a binary operator like "+" etc.
            // add a "hanging indent" modifier.
            // At the end of the day, we're recording a set of line offsets and indents.
            // This can be used either to reformat the buffer, or indent a new line.
            // State:
            int offset = Utilities.getRowStart(doc, startOffset); // The line's offset

            int end = endOffset;

            // Pending - apply comment formatting too?
            // XXX Look up RHTML too
            //int indentSize = EditorOptions.get(RubyInstallation.RUBY_MIME_TYPE).getSpacesPerTab();
            //int hangingIndentSize = indentSize;
            // Build up a set of offsets and indents for lines where I know I need
            // to adjust the offset. I will then go back over the document and adjust
            // lines that are different from the intended indent. By doing piecemeal
            // replacements in the document rather than replacing the whole thing,
            // a lot of things will work better: breakpoints and other line annotations
            // will be left in place, semantic coloring info will not be temporarily
            // damaged, and the caret will stay roughly where it belongs.
            // The token balance at the offset
            int indent = 0; // The indentation to be used for the current line

            int prevIndent = 0;
            int nextIndent = 0;
            int continueIndent = -1;

            Stack<Brace> openingBraces = new Stack<Brace>();

            while ((!includeEnd && offset < end) || (includeEnd && offset <= end)) {
                int lineBegin = Utilities.getRowFirstNonWhite(doc, offset);
                int lineEnd = Utilities.getRowEnd(doc, offset);

                if (lineBegin != -1) {
                    int[] results = computeLineIndent(indent, prevIndent, continueIndent,
                            openingBraces, doc, lineBegin, lineEnd);

                    indent = results[0];
                    nextIndent = results[1];
                    continueIndent = results[2];
                }

                if (indent == -1) {
                    // Skip this line - leave formatting as it is prior to reformatting
                    indent = ScalaLexUtilities.getLineIndent(doc, offset);
                }

                if (indent < 0) {
                    indent = 0;
                }

                // Insert whitespace on empty lines too -- needed for abbreviations expansion
                if (lineBegin != -1 || indentEmptyLines) {
                    indents.add(Integer.valueOf(indent));
                    offsets.add(Integer.valueOf(offset));
                }

                // Shift to next line
                offset = lineEnd + 1;
                prevIndent = indent;
                indent = nextIndent;
            }
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
        }
    }

    private class Brace {

        Token token;
        int offsetOnline; // offset on it's line after indent

        int ordinalOnline; // ordinal on its line (we only count non-white tokens 

        boolean isLatestOnLine; // last one on this line?

        boolean onProcessingLine; // on the processing line?


        @Override
        public String toString() {
            return token.text().toString();
        }
    }

    /**
     * Compute indent for next line, and adjust this line's indent if necessary 
     * @return int[]
     *      int[0] - adjusted indent of this line
     *      int[1] - indent for next line
     */
    private int[] computeLineIndent(int indent, int prevIndent, int continueIndent,
            Stack<Brace> openingBraces, BaseDocument doc, int lineBegin, int lineEnd) {

        // Well, a new line begin
        for (Brace brace : openingBraces) {
            brace.onProcessingLine = false;
        }

        //StringBuilder sb = new StringBuilder(); // for debug
        // Compute new balance and adjust indent of this line

        // token ordinal on this line (we only count non-white tokens, 
        // if ordinal == 0, means the first non-white token on this line
        int notWhiteIdx = -1;
        Token latestNotWhiteToken = null;

        TokenSequence ts = ScalaLexUtilities.getTokenSequence(doc, lineBegin);
        if (ts != null) {
            try {
                ts.move(lineBegin);
                do {
                    Token token = ts.token();
                    if (token != null) {

                        int offset = ts.offset();
                        TokenId id = token.id();
                        String name = token.id().name();
                        String text = token.text().toString();

                        //sb.append(text); // for debug

                        if (!(id == ScalaTokenId.Ws || id == ScalaTokenId.Nl ||
                                id == ScalaTokenId.LineComment ||
                                id == ScalaTokenId.DocComment ||
                                id == ScalaTokenId.BlockComment)) {

                            notWhiteIdx++;
                            latestNotWhiteToken = token;
                        }

                        if (id.primaryCategory().equals("keyword") || id.primaryCategory().equals("separator") || id.primaryCategory().equals("operator")) {
                            Brace justClosedBrace = null;

                            if (!openingBraces.isEmpty()) {
                                Brace brace = openingBraces.peek();
                                TokenId braceId = brace.token.id();

                                Set<ScalaTokenId> matchingIds = BRACE_MATCH_MAP.get(braceId);
                                assert matchingIds != null;
                                if (matchingIds.contains(id)) {
                                    // matched

                                    int numClosed;

                                    // we may need to lookahead 2 steps for some cases:
                                    if (braceId == ScalaTokenId.Case) {
                                        Brace backup = openingBraces.pop();

                                        numClosed = 1; // default

                                        if (!openingBraces.isEmpty()) {
                                            TokenId lookaheadId = openingBraces.peek().token.id();
                                            // if resolved is "=>", we may have matched two braces:
                                            if (id == ScalaTokenId.RBrace) {
                                                numClosed = 2;
                                            }
                                        } else if (braceId == ScalaTokenId.Catch) {
                                            /** in case of: catch Expr
                                             *     test() ->
                                             *         catch 1+2.
                                             */
                                            numClosed = 0;
                                        }

                                        openingBraces.push(backup);
                                    } else if (id == ScalaTokenId.RParen || id == ScalaTokenId.RBracket || id == ScalaTokenId.RBrace) {
                                        numClosed = 1;
                                    } else {
                                        numClosed = 1;
                                    }


                                    for (int i = 0; i < numClosed; i++) {
                                        justClosedBrace = openingBraces.pop();
                                    }

                                    if (notWhiteIdx == 0) {
                                        // At the beginning of this line
                                        if (id == ScalaTokenId.Case ||
                                                id == ScalaTokenId.RParen ||
                                                id == ScalaTokenId.RBracket ||
                                                id == ScalaTokenId.RBrace) {
                                            indent = openingBraces.size() * indentSize();
                                        } else {
                                            indent = justClosedBrace.offsetOnline;
                                        }
                                    }
                                }
                            }

                            // Add new unresolved brace
                            if (BRACE_MATCH_MAP.containsKey(id)) {
                                boolean isBrace = false;
                                if (text.equals("catch") &&
                                        (justClosedBrace == null || justClosedBrace != null && !justClosedBrace.token.text().toString().equals("try"))) {
                                    // don't add this catch as brace. Example:
                                    //     case catch ets:update_counter(Ets, Key, 1) of
                                    //         {'EXIT', {badarg, _}} -> ets:insert(Ets, {Key, 1});
                                    //         _ -> ok
                                    //     end.                  
                                    isBrace = false;
                                } else {
                                    isBrace = true;
                                }

                                if (isBrace) {
                                    Brace newBrace = new Brace();
                                    newBrace.token = token;
                                    // will add indent of this line later
                                    newBrace.offsetOnline = offset - lineBegin;
                                    newBrace.ordinalOnline = notWhiteIdx;
                                    newBrace.onProcessingLine = true;
                                    openingBraces.add(newBrace);
                                }
                            }
                        } else if ((id == ScalaTokenId.StringLiteral && offset < lineBegin) ||
                                id == ScalaTokenId.LineComment || id == ScalaTokenId.DocComment || id == ScalaTokenId.BlockComment ||
                                id == ScalaTokenId.XmlCDData || id == ScalaTokenId.XmlCharData) {
                            /** 
                             * A literal string with more than one line is a whole token and when goes
                             * to second or followed lines, will has offset < lineBegin
                             */
                            if (notWhiteIdx == 0 || notWhiteIdx == -1) {
                                // No indentation for literal strings in Erlang, since they can
                                // contain newlines. Leave it as is. 
                                indent = -1;
                            }
                        } else if (name.equals("stop")) {
                            if (notWhiteIdx == 0) {
                                // will to the begin of line (indent = 0)
                                indent = 0;
                            }
                            openingBraces.empty();
                        }
                    }
                } while (ts.moveNext() && ts.offset() < lineEnd);
            } catch (AssertionError e) {
                DataObject dobj = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
                if (dobj != null) {
                    Exceptions.attachMessage(e, FileUtil.getFileDisplayName(dobj.getPrimaryFile()));
                }

                throw e;
            }
        }

        // Now we've got the final indent of this line, adjust offset for new added
        // braces (which should be on this line)
        for (Brace brace : openingBraces) {
            if (brace.onProcessingLine) {
                brace.offsetOnline += indent;
                if (brace.ordinalOnline == notWhiteIdx) {
                    brace.isLatestOnLine = true;
                }
            }
        }

        // Compute indent for next line
        int nextIndent;
        Brace latestOpening = openingBraces.size() > 0 ? openingBraces.get(openingBraces.size() - 1) : null;
        TokenId latestOpeningId = latestOpening != null ? latestOpening.token.id() : null;

        // decide if next line is new or continued continute line        
        boolean isContinueLine;
        if (latestNotWhiteToken == null) {
            // empty line or comment line
            isContinueLine = false;
        } else {
            TokenId id = latestNotWhiteToken.id();
            String text = latestNotWhiteToken.text().toString();

            if (id == ScalaTokenId.RArrow ||
                    text.equals("if") ||
                    text.equals("case") ||
                    text.equals("of") ||
                    text.equals("try") ||
                    text.equals("catch") ||
                    text.equals("after") ||
                    text.equals("begin") ||
                    text.equals("receive") ||
                    text.equals("fun") ||
                    text.equals("end") ||
                    id == ScalaTokenId.RParen ||
                    id == ScalaTokenId.RBracket ||
                    id == ScalaTokenId.RBrace) {

                isContinueLine = false;
            } else if (id == ScalaTokenId.Comma) {
                //we have special case
                if (latestOpening != null && latestOpening.isLatestOnLine && (latestOpeningId == ScalaTokenId.LParen ||
                        latestOpeningId == ScalaTokenId.LBracket ||
                        latestOpeningId == ScalaTokenId.LBrace)) {

                    isContinueLine = true;
                } else {
                    // default
                    isContinueLine = false;
                }
            } else if (id == ScalaTokenId.LParen ||
                    id == ScalaTokenId.LBracket ||
                    id == ScalaTokenId.LBrace) {
                // the last unresolved brace is "(", "[", "{" 
                // and it's of cource also the lastest non white token on this line
                isContinueLine = false;
            } else {
                // default
                isContinueLine = false;
            }
        }

        // Compute or reset continue indent
        if (isContinueLine) {
            if (continueIndent == -1) {
                // new continue indent
                continueIndent = indent + codeStyle.getContinuationIndentSize();
            } else {
                // keep the same continue indent
                continueIndent = continueIndent;
            }
        } else {
            // Reset continueIndent
            continueIndent = -1;
        }

        if (isContinueLine) {
            // Continue line
            nextIndent = continueIndent;
        } else {
            if (latestOpening == null) {
                // All braces resolved
                nextIndent = 0;
            } else {
                int offset = latestOpening.offsetOnline;
                TokenId id = latestOpening.token.id();
                if (id == ScalaTokenId.RArrow) {
                    Brace nearestHangableBrace = null;
                    int depth1 = 0;
                    for (int i = openingBraces.size() - 1; i >= 0; i--) {
                        Brace brace = openingBraces.get(i);
                        depth1++;
                        if (brace.token.id() != ScalaTokenId.RArrow) {
                            nearestHangableBrace = brace;
                            break;
                        }
                    }

                    if (nearestHangableBrace != null) {
                        // Hang it from this brace
                        nextIndent = nearestHangableBrace.offsetOnline + depth1 * indentSize();
                    } else {
                        nextIndent = openingBraces.size() * indentSize();
                    }
                } else if ((id == ScalaTokenId.LParen ||
                        id == ScalaTokenId.LBracket ||
                        id == ScalaTokenId.LBrace) && !latestOpening.isLatestOnLine) {
                    nextIndent = offset + latestOpening.token.text().toString().length();
                } else {
                    // default
                    nextIndent = openingBraces.size() * indentSize();
                }
            }
        }

        return new int[]{indent, nextIndent, continueIndent};
    }

//    void reformatComments(BaseDocument doc, int start, int end) {
//        int rightMargin = rightMarginOverride;
//        if (rightMargin == -1) {
//            CodeStyle style = codeStyle;
//            if (style == null) {
//                style = CodeStyle.getDefault(null);
//            }
//
//            rightMargin = style.getRightMargin();
//        }
//
//        ReflowParagraphAction action = new ReflowParagraphAction();
//        action.reflowComments(doc, start, end, rightMargin);
//    }
    /**
     * Ensure that the editor-settings for tabs match our code style, since the
     * primitive "doc.getFormatter().changeRowIndent" calls will be using
     * those settings
     */
    private static void syncOptions(BaseDocument doc, CodeStyle style) {
        org.netbeans.editor.Formatter formatter = doc.getFormatter();
        if (formatter.getSpacesPerTab() != style.getIndentSize()) {
            formatter.setSpacesPerTab(style.getIndentSize());
        }
    }
}
