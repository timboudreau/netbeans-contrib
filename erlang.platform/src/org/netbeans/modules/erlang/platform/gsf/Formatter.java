/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.erlang.platform.gsf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.erlang.platform.options.CodeStyle;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;

/**
 * Formatting and indentation for Erlang.
 *
 * @todo Handle RHTML!
 *      - 4 space indents
 *      - conflicts with HTML formatter (HtmlIndentTask)
 *      - The Ruby indentation should be indented into the HTML level as well!
 *          (so I should look at the previous HTML level for my initial context
 *           whenever the balance is 0.)
 * @todo Use configuration object to pass in Ruby conventions
 * @todo Use the provided parse tree, if any, to for example check heredoc nodes
 *   and see if they are indentable.
 * @todo If you select a complete line, the endOffset is on a new line; adjust it back
 * @todo If line ends with \ I definitely have a line continuation!
 * @todo Use the Context.modifyIndent() method to change line indents instead of
 *   the current document/formatter method
 * @todo This line screws up formatting:
 *        alias __class__ class #:nodoc:
 * @todo Why doesn't this format correctly?
 * <pre>
class Module
alias_method :class?, :===
end
 * </pre>
 *
 * @author Tor Norbye
 * @author Caoyuan Deng
 */
public class Formatter implements org.netbeans.modules.gsf.api.Formatter {

    private CodeStyle codeStyle;
    private int rightMarginOverride = -1;

    public Formatter() {
        this.codeStyle = CodeStyle.getDefault(null);
    }

    public Formatter(CodeStyle codeStyle, int rightMarginOverride) {
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
        TokenSequence ts = LexUtilities.getTokenSequence(doc, offset);// getRubyTokenSequence(doc, offset);
        if (ts == null) {
            return 0;
        }

        ts.move(offset);

        if (!ts.movePrevious()) {
            return 0;
        }

        // Look backwards to find a suitable context - a class, module or method definition
        // which we will assume is properly indented and balanced
        int prevNonWhiteOffset = ts.offset();
        do {
            Token token = ts.token();
            String name = token.id().name();

            if (!name.equals("whitespace")) {
                if (name.equals("stop")) {
                    return prevNonWhiteOffset;
                } else {
                    prevNonWhiteOffset = ts.offset();
                }
            }

        } while (ts.movePrevious());

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
            return LexUtilities.getToken(doc, lineBegin);
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
                initialIndent = LexUtilities.getLineIndent(doc, initialOffset);
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

            boolean includeEnd = endOffset == doc.getLength();

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
                        int actualPrevIndent = LexUtilities.getLineIndent(doc, prevOffset);
                        if (actualPrevIndent != prevIndent) {
                            // For blank lines, indentation may be 0, so don't adjust in that case
                            if (!(Utilities.isRowEmpty(doc, prevOffset) || Utilities.isRowWhite(doc, prevOffset))) {
                                indent = actualPrevIndent + (indent - prevIndent);
                            }
                        }
                    }

                    // Adjust the indent at the given line (specified by offset) to the given indent
                    int currentIndent = LexUtilities.getLineIndent(doc, lineBegin);

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

            List<Brace> unresolvedBraces = new ArrayList<Brace>();

            while ((!includeEnd && offset < end) || (includeEnd && offset <= end)) {
                int lineBegin = Utilities.getRowFirstNonWhite(doc, offset);
                int lineEnd = Utilities.getRowEnd(doc, offset) + 1;

                if (lineBegin != -1) {
                    int[] results = computeLineIndent(indent, prevIndent, continueIndent,
                            unresolvedBraces, doc, lineBegin, lineEnd);

                    indent = results[0];
                    nextIndent = results[1];
                    continueIndent = results[2];
                }

                if (indent == -1) {
                    // Skip this line - leave formatting as it is prior to reformatting
                    indent = LexUtilities.getLineIndent(doc, offset);
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
                offset = lineEnd;
                prevIndent = indent;
                indent = nextIndent;
            }
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
        }
    }
    private static Map<String, Set<String>> BRACE_MATCH_MAP = null;

    private Map<String, Set<String>> getBraceMatchMap() {
        if (BRACE_MATCH_MAP == null) {
            BRACE_MATCH_MAP = new HashMap<String, Set<String>>();
            Set<String> tailSet = new HashSet<String>();
            BRACE_MATCH_MAP.put("(", tailSet);
            tailSet.add(")");

            tailSet = new HashSet<String>();
            BRACE_MATCH_MAP.put("{", tailSet);
            tailSet.add("}");

            tailSet = new HashSet<String>();
            BRACE_MATCH_MAP.put("[", tailSet);
            tailSet.add("]");

            tailSet = new HashSet<String>();
            BRACE_MATCH_MAP.put("<<", tailSet);
            tailSet.add(">>");

            tailSet = new HashSet<String>();
            BRACE_MATCH_MAP.put("if", tailSet);
            tailSet.add("end");

            tailSet = new HashSet<String>();
            BRACE_MATCH_MAP.put("case", tailSet);
            tailSet.add("end");

            tailSet = new HashSet<String>();
            BRACE_MATCH_MAP.put("begin", tailSet);
            tailSet.add("end");

            tailSet = new HashSet<String>();
            BRACE_MATCH_MAP.put("receive", tailSet);
            tailSet.add("end");
            tailSet.add("after");

            tailSet = new HashSet<String>();
            BRACE_MATCH_MAP.put("after", tailSet);
            tailSet.add("end");

            tailSet = new HashSet<String>();
            BRACE_MATCH_MAP.put("query", tailSet);
            tailSet.add("end");

            tailSet = new HashSet<String>();
            BRACE_MATCH_MAP.put("try", tailSet);
            tailSet.add("catch");
            tailSet.add("after");

            tailSet = new HashSet<String>();
            BRACE_MATCH_MAP.put("catch", tailSet);
            tailSet.add("end");
            tailSet.add("after");
            tailSet.add(",");

            tailSet = new HashSet<String>();
            BRACE_MATCH_MAP.put("fun", tailSet);
            tailSet.add("end");
            tailSet.add("/");

            tailSet = new HashSet<String>();
            BRACE_MATCH_MAP.put("->", tailSet);
            tailSet.add(";");
            tailSet.add("end");
            tailSet.add("catch");
            tailSet.add("after");
        }

        return BRACE_MATCH_MAP;
    }

    private class Brace {

        Token token;
        int offsetOnline; // offset on it's line after indent
        int ordinalOnline; // ordinal on its line (we only count non-white tokens 
        boolean isLastOnLine; // last one on this line?
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
            List<Brace> unresolvedBraces, BaseDocument doc, int lineBegin, int lineEnd) {

        // Well, a new line begin
        for (Brace brace : unresolvedBraces) {
            brace.onProcessingLine = false;
        }

        int balance = unresolvedBraces.size(); // balance according to previous line
        int indentSize = codeStyle.getIndentSize();

        // Compute new balance and adjust indent of this line
        Map<String, Set<String>> braceMatchMap = getBraceMatchMap();

        StringBuilder sb = new StringBuilder();
        // token ordinal on this line (we only count non-white tokens, 
        // if ordinal == 0, means the first non-white token on this line
        int ordinalNonWhite = -1;
        Token lastNonWhiteToken = null;

        TokenSequence ts = LexUtilities.getTokenSequence(doc, lineBegin);
        if (ts != null) {
            try {
                ts.move(lineBegin);
                do {
                    Token token = ts.token();
                    if (token != null) {

                        int offset = ts.offset();
                        String name = token.id().name();
                        String text = token.text().toString();

                        sb.append(text);

                        if (!name.equals("whitespace") &&
                                !name.equals("comment") &&
                                !name.equals("doc_tag")) {

                            ordinalNonWhite++;
                            lastNonWhiteToken = token;
                        }

                        if (name.equals("keyword") || name.equals("separator") || name.equals("operator")) {
                            int size = unresolvedBraces.size();
                            // we need to look forward 2 steps for some cases
                            Brace brace1 = size > 0 ? unresolvedBraces.get(size - 1) : null;
                            Brace brace2 = size > 1 ? unresolvedBraces.get(size - 2) : null;
                            String braceText1 = brace1 != null ? brace1.token.text().toString() : null;
                            String braceText2 = brace2 != null ? brace2.token.text().toString() : null;
                            int braceOffset1 = brace1 != null ? brace1.offsetOnline : 0;
                            int braceOffset2 = brace2 != null ? brace2.offsetOnline : 0;

                            Brace justResolvedBrace = null;
                            if (brace1 != null) {
                                Set<String> matchingTexts = braceMatchMap.get(braceText1);
                                assert matchingTexts != null;
                                if (matchingTexts.contains(text)) {
                                    int numMatched;

                                    // special cases:
                                    if (braceText1.equals("->")) {
                                        // if resolved is "->", we may have matched two braces:
                                        if (braceText2 != null && (text.equals("end") || text.equals("after") || (text.equals("catch") && braceText2.equals("try")))) {
                                            numMatched = 2;
                                        } else if (text.equals("catch")) {
                                            /** in case of: catch Expr
                                             *     test() ->
                                             *         catch 1+2.
                                             */
                                            numMatched = 0;
                                        } else {
                                            numMatched = 1;
                                        }
                                    } else if (text.equals(")") || text.equals("]") || text.equals("}") || text.equals(">>")) {
                                        numMatched = 1;
                                    } else {
                                        numMatched = 1;
                                    }

                                    switch (numMatched) {
                                        case 0:
                                            if (ordinalNonWhite == 0) {
                                                indent = prevIndent;
                                            }
                                            break;
                                        case 1:
                                            justResolvedBrace = unresolvedBraces.get(size - 1);
                                            unresolvedBraces.remove(size - 1);
                                            if (ordinalNonWhite == 0) {
                                                indent = braceOffset1;
                                                balance -= 1;
                                            }
                                            break;
                                        case 2:
                                            justResolvedBrace = unresolvedBraces.get(size - 2);
                                            unresolvedBraces.remove(size - 1);
                                            unresolvedBraces.remove(size - 2);
                                            if (ordinalNonWhite == 0) {
                                                indent = braceOffset2;
                                                balance -= 2;
                                            }
                                            break;
                                        default:
                                    }
                                }
                            }

                            // Add new unresolved brace
                            if (braceMatchMap.containsKey(text)) {
                                boolean isBrace = false;
                                if (text.equals("catch") &&
                                        (justResolvedBrace == null || justResolvedBrace != null && !justResolvedBrace.token.text().toString().equals("try"))) {
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
                                    Brace brace = new Brace();
                                    brace.token = token;
                                    // will add indent of this line later
                                    brace.offsetOnline = offset - lineBegin;
                                    brace.ordinalOnline = ordinalNonWhite;
                                    brace.onProcessingLine = true;
                                    unresolvedBraces.add(brace);
                                }
                            }
                        } else if ((name.equals("string") && (offset < lineBegin)) ||
                                name.equals("commnet") || name.equals("doc_tag")) {
                            /** 
                             * A literal string with more than one line is a whole token and when goes
                             * to second or followed lines, will has offset < lineBegin
                             */
                            if (ordinalNonWhite == 0 || ordinalNonWhite == -1) {
                                // No indentation for literal strings in Erlang, since they can
                                // contain newlines. Leave it as is. 
                                indent = -1;
                            }
                        } else if (name.equals("stop")) {
                            if (ordinalNonWhite == 0) {
                                // will to the begin of line (indent = 0)
                                balance = 0;
                                indent = 0;
                            }
                            unresolvedBraces.clear();
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
        for (Brace brace : unresolvedBraces) {
            if (brace.onProcessingLine) {
                brace.offsetOnline += indent;
                if (brace.ordinalOnline == ordinalNonWhite) {
                    brace.isLastOnLine = true;
                }
            }
        }

        // Compute indent for next line
        int nextIndent;
        int newBalance = unresolvedBraces.size();
        Brace lastUnresolved = newBalance > 0 ? unresolvedBraces.get(newBalance - 1) : null;
        String lastUnresolvedText = lastUnresolved != null ? lastUnresolved.token.text().toString() : null;

        // decide if next line is new or continued continute line        
        boolean continueLine;
        if (lastNonWhiteToken == null) {
            // empty line or comment line
            continueLine = false;
        } else {
            String name = lastNonWhiteToken.id().name();
            String text = lastNonWhiteToken.text().toString();

            if (name.equals("stop") ||
                    text.equals("->") ||
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
                    text.equals(";") ||
                    text.equals(")") ||
                    text.equals("]") ||
                    text.equals("}") ||
                    text.equals(">>")) {

                continueLine = false;
            } else if (text.equals(",")) {
                //we have special case
                if (lastUnresolved != null && lastUnresolved.isLastOnLine && (lastUnresolvedText.equals("(") ||
                        lastUnresolvedText.equals("[") ||
                        lastUnresolvedText.equals("{") ||
                        lastUnresolvedText.equals("<<"))) {

                    continueLine = true;
                } else {
                    // default
                    continueLine = false;
                }
            } else if (text.equals("(") ||
                    text.equals("[") ||
                    text.equals("{") ||
                    text.equals("<<")) {
                // the last unresolved brace is "(", "[", "{" , "<<", 
                // and it's of cource also the last non white token on this line
                continueLine = true;
            } else {
                // default
                continueLine = true;
            }
        }

        // Compute or reset continue indent
        if (continueLine) {
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

        if (continueLine) {
            // Continue line
            nextIndent = continueIndent;
        } else {
            if (lastUnresolved == null) {
                // All braces resolved
                nextIndent = 0;
            } else {
                int offset = lastUnresolved.offsetOnline;
                String text = lastUnresolved.token.text().toString();
                if (text.equals("->")) {
                    Brace nearestHangableBrace = null;
                    int depth = 0;
                    for (int i = unresolvedBraces.size() - 1; i >= 0; i--) {
                        Brace brace = unresolvedBraces.get(i);
                        depth++;
                        if (!brace.token.text().toString().equals("->")) {
                            nearestHangableBrace = brace;
                            break;
                        }
                    }

                    if (nearestHangableBrace != null) {
                        // Hang it from this brace
                        nextIndent = nearestHangableBrace.offsetOnline + depth * indentSize;
                    } else {
                        nextIndent = newBalance * indentSize;
                    }
                } else if (text.equals("(") || text.equals("{") || text.equals("[") || text.equals("<<")) {
                    nextIndent = offset + text.length();
                } else {
                    nextIndent = offset + indentSize;
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
