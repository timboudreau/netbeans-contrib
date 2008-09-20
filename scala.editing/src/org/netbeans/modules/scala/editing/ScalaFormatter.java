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

import org.netbeans.modules.scala.editing.options.CodeStyle;
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
import org.netbeans.modules.editor.indent.spi.Context;
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
        BRACE_MATCH_MAP.put(ScalaTokenId.DocCommentStart, matchingset);
        matchingset.add(ScalaTokenId.DocCommentEnd);

        matchingset = new HashSet<ScalaTokenId>();
        BRACE_MATCH_MAP.put(ScalaTokenId.BlockCommentStart, matchingset);
        matchingset.add(ScalaTokenId.BlockCommentEnd);

        matchingset = new HashSet<ScalaTokenId>();
        BRACE_MATCH_MAP.put(ScalaTokenId.XmlLt, matchingset);
        matchingset.add(ScalaTokenId.XmlSlashGt);
        matchingset.add(ScalaTokenId.XmlLtSlash);

//        matchingset = new HashSet<ScalaTokenId>();
//        BRACE_MATCH_MAP.put(ScalaTokenId.If, matchingset);
//        matchingset.add(ScalaTokenId.LBrace);
//        matchingset.add(ScalaTokenId.Nl);
//
//        matchingset = new HashSet<ScalaTokenId>();
//        BRACE_MATCH_MAP.put(ScalaTokenId.Else, matchingset);
//        matchingset.add(ScalaTokenId.LBrace);
//        matchingset.add(ScalaTokenId.Nl);
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

    public void reindent(Context context) {
        reindent(context, context.document(), context.startOffset(), context.endOffset(), null, true);
    }

    public void reformat(Context context, CompilationInfo info) {
        reindent(context, context.document(), context.startOffset(), context.endOffset(), info, false);
    }

    public int indentSize() {
        return codeStyle.getIndentSize();
    }

    public int hangingIndentSize() {
        return codeStyle.getContinuationIndentSize();
    }

    /** Compute the initial balance of brackets at the given offset. */
    private int getFormatStableStart(BaseDocument doc, int offset) {
        TokenSequence<ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(doc, offset);
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

            if (id == ScalaTokenId.Object || id == ScalaTokenId.Trait || id == ScalaTokenId.Class) {
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

    public void reindent(final Context context, Document document, int startOffset, int endOffset, CompilationInfo info, final boolean indentOnly) {


        try {
            final BaseDocument doc = (BaseDocument) document; // document.getText(0, document.getLength())

            syncOptions(doc, codeStyle);

            if (endOffset > doc.getLength()) {
                endOffset = doc.getLength();
            }

            startOffset = Utilities.getRowStart(doc, startOffset);
            final int lineStart = startOffset; //Utilities.getRowStart(doc, startOffset);

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
            final List<Integer> offsets = new ArrayList<Integer>();
            final List<Integer> indents = new ArrayList<Integer>();

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

            doc.runAtomic(new Runnable() {
                public void run() {
                    try {

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
                                if (context != null) {
                                    context.modifyIndent(lineBegin, indent);
                                } else {
                                    editorFormatter.changeRowIndent(doc, lineBegin, indent);
                                }
                            }
                        }

                        if (!indentOnly && codeStyle.reformatComments()) {
        //                    reformatComments(doc, startOffset, endOffset);
                        }
                    } catch (BadLocationException ble) {
                        Exceptions.printStackTrace(ble);
                    }
                }
            });
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
        int offsetOnline; // offset of this token on its line after indent
        int ordinalOnline; // ordinal of this token on its line (we only count non-white tokens)
        boolean isLatestOnLine; // last one on this line?
        boolean onProcessingLine; // on the processing line?
        Token lasestTokenOnLine; // lastest non-white token on this line 

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
            Stack<Brace> openBraces, BaseDocument doc, int lineBegin, int lineEnd) {

        // Well, a new line begin
        for (Brace brace : openBraces) {
            brace.onProcessingLine = false;
        }

        //StringBuilder sb = new StringBuilder(); // for debug
        // Compute new balance and adjust indent of this line

        // token index on this line (we only count not-white tokens, 
        // if notWhiteIdx == 0, means the first non-white token on this line
        int notWSIdx = -1;
        Token latestNotWSToken = null;

        TokenSequence ts = ScalaLexUtilities.getTokenSequence(doc, lineBegin);
        if (ts != null) {
            try {
                ts.move(lineBegin);
                do {
                    Token token = ts.token();
                    if (token != null) {

                        int offset = ts.offset();
                        TokenId id = token.id();

                        //sb.append(text); // for debug

                        if (!ScalaLexUtilities.isWsComment(id)) {
                            notWSIdx++;
                            latestNotWSToken = token;
                        }

                        // match/add brace
                        if (id.primaryCategory().equals("keyword") ||
                                id.primaryCategory().equals("separator") ||
                                id.primaryCategory().equals("operator") ||
                                id.primaryCategory().equals("xml") ||
                                id.primaryCategory().equals("comment")) {

                            Brace justClosedBrace = null;

                            if (!openBraces.isEmpty()) {
                                Brace brace = openBraces.peek();
                                TokenId braceId = brace.token.id();

                                Set<ScalaTokenId> matchingIds = BRACE_MATCH_MAP.get(braceId);
                                assert matchingIds != null;
                                if (matchingIds.contains(id)) { // matched                                   

                                    int numClosed = 1; // default

                                    // we may need to lookahead 2 steps for some cases:
                                    if (braceId == ScalaTokenId.Case) {
                                        Brace backup = openBraces.pop();

                                        if (!openBraces.isEmpty()) {
                                            //TokenId lookaheadId = openingBraces.peek().token.id();
                                            // if resolved is "=>", we may have matched two braces:
                                            if (id == ScalaTokenId.RBrace) {
                                                numClosed = 2;
                                            }
                                        }

                                        openBraces.push(backup);
                                    }

                                    for (int i = 0; i < numClosed; i++) {
                                        justClosedBrace = openBraces.pop();
                                    }

                                    if (notWSIdx == 0) {
                                        // At the beginning of this line, adjust this line's indent if necessary 
                                        if (id == ScalaTokenId.Case ||
                                                id == ScalaTokenId.RParen ||
                                                id == ScalaTokenId.RBracket ||
                                                id == ScalaTokenId.RBrace) {
                                            indent = openBraces.size() * indentSize();
                                        } else {
                                            indent = justClosedBrace.offsetOnline;
                                        }
                                    }

                                }
                            }

                            // Add new opening brace
                            if (BRACE_MATCH_MAP.containsKey(id)) {
                                boolean ignore = false;
                                // is it a case object or class?, if so, do not indent
                                if (id == ScalaTokenId.Case) {
                                    if (ts.moveNext()) {
                                        Token next = ScalaLexUtilities.findNextNonWs(ts);
                                        if (next.id() == ScalaTokenId.Object || next.id() == ScalaTokenId.Class) {
                                            ignore = true;
                                        }
                                        ts.movePrevious();
                                    }
                                }

                                if (!ignore) {
                                    Brace newBrace = new Brace();
                                    newBrace.token = token;
                                    // will add indent of this line to offsetOnline later
                                    newBrace.offsetOnline = offset - lineBegin;
                                    newBrace.ordinalOnline = notWSIdx;
                                    newBrace.onProcessingLine = true;
                                    openBraces.add(newBrace);
                                }
                            }
                        } else if (id == ScalaTokenId.XmlCDData ||
                                (id == ScalaTokenId.StringLiteral && offset < lineBegin)) {
                            /** 
                             * A literal string with more than one line is a whole token and when goes
                             * to second or following lines, will has offset < lineBegin
                             */
                            if (notWSIdx == 0 || notWSIdx == -1) {
                                // No indentation for literal strings from 2nd line. 
                                indent = -1;
                            }
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
        for (Brace brace : openBraces) {
            if (brace.onProcessingLine) {
                brace.offsetOnline += indent;
                if (brace.ordinalOnline == notWSIdx) {
                    brace.isLatestOnLine = true;
                }
                brace.lasestTokenOnLine = latestNotWSToken;
            }
        }

        // Compute indent for next line
        int nextIndent;
        Brace latestOpenBrace = openBraces.size() > 0 ? openBraces.get(openBraces.size() - 1) : null;
        TokenId latestOpenId = latestOpenBrace != null ? latestOpenBrace.token.id() : null;

        // decide if next line is new or continued continute line        
        boolean isContinueLine;
        if (latestNotWSToken == null) {
            // empty line or comment line
            isContinueLine = false;
        } else {
            isContinueLine = false; // default

            TokenId id = latestNotWSToken.id();

            if (id == ScalaTokenId.Comma) {
                //we have special case
                if (latestOpenBrace != null && latestOpenBrace.isLatestOnLine && (latestOpenId == ScalaTokenId.LParen ||
                        latestOpenId == ScalaTokenId.LBracket ||
                        latestOpenId == ScalaTokenId.LBrace)) {

                    isContinueLine = true;
                }
            }
        }

        if (isContinueLine) {
            // Compute or reset continue indent
            if (continueIndent == -1) {
                // new continue indent
                continueIndent = indent + codeStyle.getContinuationIndentSize();
            } else {
                // keep the same continue indent
            }

            // Continue line
            nextIndent = continueIndent;
        } else {
            // Reset continueIndent
            continueIndent = -1;

            if (latestOpenBrace == null) {
                // All braces resolved
                nextIndent = 0;
            } else {
                int offset = latestOpenBrace.offsetOnline;
                if (latestOpenId == ScalaTokenId.RArrow) {
                    Brace nearestHangableBrace = null;
                    int depth1 = 0;
                    for (int i = openBraces.size() - 1; i >= 0; i--) {
                        Brace brace = openBraces.get(i);
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
                        nextIndent = openBraces.size() * indentSize();
                    }
                } else if ((latestOpenId == ScalaTokenId.LParen ||
                        latestOpenId == ScalaTokenId.LBracket ||
                        latestOpenId == ScalaTokenId.LBrace) &&
                        !latestOpenBrace.isLatestOnLine &&
                        (latestOpenBrace.lasestTokenOnLine == null || latestOpenBrace.lasestTokenOnLine.id() != ScalaTokenId.RArrow)) {

                    nextIndent = offset + latestOpenBrace.token.text().toString().length();

                } else if (latestOpenId == ScalaTokenId.BlockCommentStart ||
                        latestOpenId == ScalaTokenId.DocCommentStart) {

                    nextIndent = offset + 1;

                } else {
                    // default
                    nextIndent = openBraces.size() * indentSize();
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
