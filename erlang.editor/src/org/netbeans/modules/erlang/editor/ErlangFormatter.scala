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
package org.netbeans.modules.erlang.editor

import javax.swing.text.{BadLocationException,Document}
import org.netbeans.api.lexer.{Token,TokenId,TokenSequence}
import org.netbeans.editor.{BaseDocument,Utilities}
import org.netbeans.modules.editor.indent.spi.Context
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.csl.api.Formatter
import org.openide.filesystems.FileUtil
import org.openide.loaders.DataObject
import org.openide.util.Exceptions

import org.netbeans.modules.erlang.editor.lexer.{ErlangTokenId,LexUtil}

import scala.collection.mutable.{ArrayBuffer,Stack}

/**
 * Formatting and indentation for Erlang.
 *
 *
 * @author Caoyuan Deng
 */
class ErlangFormatter extends Formatter {

    //private val codeStyle = CodeStyle.getDefault(null)
    private var rightMarginOverride = -1

    //    public Formatter(CodeStyle codeStyle, int rightMarginOverride) {
    //        assert codeStyle != null;
    //        this.codeStyle = codeStyle;
    //        this.rightMarginOverride = rightMarginOverride;
    //    }

    override
    def needsParserResult = false

    override
    def reindent(context:Context) :Unit = {
        reindent(context, context.document, context.startOffset, context.endOffset, null, true)
    }

    override
    def reformat(context:Context,  pResult:ParserResult) :Unit = {
        reindent(context, context.document, context.startOffset, context.endOffset, pResult, false)
    }

    override
    def indentSize = 4//codeStyle.getIndentSize

    override
    def hangingIndentSize = 4//codeStyle.getContinuationIndentSize();

    /** Compute the initial balance of brackets at the given offset. */
    private def getFormatStableStart(doc:BaseDocument, offset:Int) :Int = {
        val ts = LexUtil.tokenSequence(doc, offset) match {
            case None => return 0
            case Some(x) => x
        }

        ts.move(offset)
        if (!ts.movePrevious) {
            return 0
        }

        // Look backwards to find a suitable context - a class, module or method definition
        // which we will assume is properly indented and balanced
        var prevNonWhiteOffset = ts.offset
        do {
            ts.token.id match {
                case ErlangTokenId.Stop =>
                    return prevNonWhiteOffset
                case id if !LexUtil.isWsComment(id) =>
                    prevNonWhiteOffset = ts.offset
                case _ =>
            }
        } while (ts.movePrevious)

        ts.offset
    }

    /**
     * Get the first token on the given line. Similar to LexUtil.getToken(doc, lineBegin)
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
    @throws(classOf[BadLocationException])
    private def getFirstTokenOnLine(doc:BaseDocument, offset:Int) :Option[Token[_]] = {
        val lineBegin = Utilities.getRowFirstNonWhite(doc, offset)
        if (lineBegin != -1) {
            return LexUtil.token(doc, lineBegin)
        }

        return None
    }

    def reindent(context:Context, document:Document, _startOffset:Int, _endOffset:Int, pResult:ParserResult, indentOnly:Boolean) :Unit = {
        try {
            var endOffset = _endOffset
            var startOffset = _startOffset
            val doc = document.asInstanceOf[BaseDocument]
            //syncOptions(doc, codeStyle)

            if (endOffset > doc.getLength) {
                endOffset = doc.getLength
            }

            startOffset = Utilities.getRowStart(doc, startOffset)
            var lineStart = startOffset 
            var initialOffset = 0
            var initialIndent = 0
            if (startOffset > 0) {
                val prevOffset = Utilities.getRowStart(doc, startOffset - 1)
                initialOffset = getFormatStableStart(doc, prevOffset)
                initialIndent = LexUtil.lineIndent(doc, initialOffset)
            }

            // Build up a set of offsets and indents for lines where I know I need
            // to adjust the offset. I will then go back over the document and adjust
            // lines that are different from the intended indent. By doing piecemeal
            // replacements in the document rather than replacing the whole thing,
            // a lot of things will work better: breakpoints and other line annotations
            // will be left in place, semantic coloring info will not be temporarily
            // damaged, and the caret will stay roughly where it belongs.
            val offsets = new ArrayBuffer[Int]
            val indents = new ArrayBuffer[Int]

            // When we're formatting sections, include whitespace on empty lines; this
            // is used during live code template insertions for example. However, when
            // wholesale formatting a whole document, leave these lines alone.
            val indentEmptyLines = startOffset != 0 || endOffset != doc.getLength

            val includeEnd = (endOffset == doc.getLength || indentOnly)

            // TODO - remove initialbalance etc.
            computeIndents(doc, initialIndent, initialOffset, endOffset, pResult, offsets, indents, indentEmptyLines, includeEnd)

            try {
                doc.atomicLock

                // Iterate in reverse order such that offsets are not affected by our edits
                assert(indents.size == offsets.size)
                val editorFormatter = doc.getFormatter
                def loop(i:Int) :Unit = if (i >= 0) {
                    var indent = indents(i)
                    val lineBegin = offsets(i)

                    if (lineBegin >= lineStart) {
                        if (lineBegin == lineStart && i > 0) {
                            // Look at the previous line, and see how it's indented
                            // in the buffer.  If it differs from the computed position,
                            // offset my computed position (thus, I'm only going to adjust
                            // the new line position relative to the existing editing.
                            // This avoids the situation where you're inserting a newline
                            // in the middle of "incorrectly" indented code (e.g. different
                            // size than the IDE is using) and the newline position ending
                            // up "out of sync"
                            val prevOffset = offsets(i - 1)
                            val prevIndent = indents(i - 1)
                            val actualPrevIndent = LexUtil.lineIndent(doc, prevOffset);
                            if (actualPrevIndent != prevIndent) {
                                // For blank lines, indentation may be 0, so don't adjust in that case
                                if (!(Utilities.isRowEmpty(doc, prevOffset) || Utilities.isRowWhite(doc, prevOffset))) {
                                    indent = actualPrevIndent + (indent - prevIndent)
                                }
                            }
                        }

                        // Adjust the indent at the given line (specified by offset) to the given indent
                        val currentIndent = LexUtil.lineIndent(doc, lineBegin)
                        if (currentIndent != indent) {
                            if (currentIndent != indent) {
                                if (context != null) {
                                    context.modifyIndent(lineBegin, indent)
                                } else {
                                    editorFormatter.changeRowIndent(doc, lineBegin, indent)
                                }
                            }
                        }
                        loop(i - 1)
                    }
                }
                loop(indents.size - 1)

                //                if (!indentOnly && codeStyle.reformatComments()) {
                //                     reformatComments(doc, startOffset, endOffset);
                //                }
            } finally {
                doc.atomicUnlock
            }
        } catch {case ex:BadLocationException => Exceptions.printStackTrace(ex)}
    }

    def computeIndents(doc:BaseDocument, initialIndent:Int, startOffset:Int, endOffset:Int, pResulet:ParserResult,
                       offsets:ArrayBuffer[Int], indents:ArrayBuffer[Int],
                       indentEmptyLines:Boolean, includeEnd:Boolean) :Unit = {
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
            var offset = Utilities.getRowStart(doc, startOffset) // The line's offset
            var end = endOffset

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
            var indent = 0 // The indentation to be used for the current line
            var prevIndent = 0
            var nextIndent = 0
            var continueIndent = -1

            val unresolvedBraces = new ArrayBuffer[Brace]

            while (!includeEnd && offset < end || includeEnd && offset <= end) {
                val lineBegin = Utilities.getRowFirstNonWhite(doc, offset)
                val lineEnd = Utilities.getRowEnd(doc, offset) + 1

                if (lineBegin != -1) {
                    val results = computeLineIndent(indent, prevIndent, continueIndent,
                                                    unresolvedBraces, doc, lineBegin, lineEnd);

                    indent = results(0)
                    nextIndent = results(1)
                    continueIndent = results(2)
                }

                if (indent == -1) {
                    // Skip this line - leave formatting as it is prior to reformatting
                    indent = LexUtil.lineIndent(doc, offset)
                }

                if (indent < 0) {
                    indent = 0
                }

                // Insert whitespace on empty lines too -- needed for abbreviations expansion
                if (lineBegin != -1 || indentEmptyLines) {
                    indents += indent
                    offsets += offset
                }

                // Shift to next line
                offset = lineEnd
                prevIndent = indent
                indent = nextIndent
            }
        } catch {case ex:BadLocationException => Exceptions.printStackTrace(ex)}
    }

    private val BRACE_MATCH_MAP :Map[TokenId, Set[TokenId]] = Map(ErlangTokenId.LParen   -> Set(ErlangTokenId.RParen),
                                                                  ErlangTokenId.LBrace   -> Set(ErlangTokenId.RBrace),
                                                                  ErlangTokenId.LBracket -> Set(ErlangTokenId.RBracket),
                                                                  ErlangTokenId.DLt      -> Set(ErlangTokenId.DGt),
                                                                  ErlangTokenId.If       -> Set(ErlangTokenId.End),
                                                                  ErlangTokenId.Case     -> Set(ErlangTokenId.End),
                                                                  ErlangTokenId.Begin    -> Set(ErlangTokenId.End),
                                                                  ErlangTokenId.Receive  -> Set(ErlangTokenId.End,
                                                                                                ErlangTokenId.After),
                                                                  ErlangTokenId.After    -> Set(ErlangTokenId.End),
                                                                  ErlangTokenId.Query    -> Set(ErlangTokenId.End),
                                                                  ErlangTokenId.Try      -> Set(ErlangTokenId.Catch,
                                                                                                ErlangTokenId.After),
                                                                  ErlangTokenId.Catch    -> Set(ErlangTokenId.End,
                                                                                                ErlangTokenId.After,
                                                                                                ErlangTokenId.Comma),
                                                                  ErlangTokenId.Fun      -> Set(ErlangTokenId.End,
                                                                                                ErlangTokenId.Slash),
                                                                  ErlangTokenId.RArrow   -> Set(ErlangTokenId.End,
                                                                                                ErlangTokenId.Catch,
                                                                                                ErlangTokenId.After,
                                                                                                ErlangTokenId.Semicolon)
    )

    private case class Brace(var token:Token[TokenId],
                             var offsetOnline:Int, // offset on it's line after indent
                             var ordinalOnline:Int, // ordinal on its line (we only count non-white tokens
                             var isLastOnLine:Boolean, // last one on this line?
                             var onProcessingLine :Boolean // on the processing line?
    ) {
        def this(token:Token[TokenId]) = this(token, 0, 0, false, false)
        
        override
        def toString = token.text.toString
    }

    /**
     * Compute indent for next line, and adjust this line's indent if necessary
     * @return int[]
     *      int[0] - adjusted indent of this line
     *      int[1] - indent for next line
     */
    def computeLineIndent(_indent:Int, prevIndent:Int, _continueIndent:Int,
                          unresolvedBraces:ArrayBuffer[Brace], doc:BaseDocument, lineBegin:Int, lineEnd:Int) :Array[Int] = {

        var indent = _indent
        // Well, a new line begin
        unresolvedBraces.foreach{_.onProcessingLine = false}

        var balance = unresolvedBraces.size // balance according to previous line

        // Compute new balance and adjust indent of this line

        val sb = new StringBuilder
        // token ordinal on this line (we only count non-white tokens,
        // if ordinal == 0, means the first non-white token on this line
        var ordinalNonWhite = -1
        var lastNonWhiteToken :Token[TokenId] = null

        for (ts <- LexUtil.tokenSequence(doc, lineBegin)) {
            try {
                ts.move(lineBegin)
                do {
                    val token :Token[TokenId] = ts.token
                    if (token != null) {

                        val offset = ts.offset
                        val id = token.id
                        val name = id.name
                        val category = id.primaryCategory
                        val text = token.text.toString

                        //sb.append(text) // used for debugging

                        if (!category.equals("whitespace") && !category.equals("comment")) {
                            ordinalNonWhite += 1
                            lastNonWhiteToken = token
                        }

                        if (id == ErlangTokenId.Stop) {
                            if (ordinalNonWhite == 0) {
                                // will to the begin of line (indent = 0)
                                balance = 0
                                indent = 0
                            }
                            unresolvedBraces.clear
                        } else if (category.equals("keyword") || category.equals("separator") || category.equals("operator")) {
                            val size = unresolvedBraces.size
                            // we need to look forward 2 steps for some cases
                            val brace1 = if (size > 0) unresolvedBraces(size - 1) else null
                            val brace2 = if (size > 1) unresolvedBraces(size - 2) else null
                            val braceId1 = if (brace1 != null) brace1.token.id else null
                            val braceId2 = if (brace2 != null) brace2.token.id else null
                            val braceOffset1 = if (brace1 != null) brace1.offsetOnline else 0
                            val braceOffset2 = if (brace2 != null) brace2.offsetOnline else 0

                            var justResolvedBrace :Brace = null
                            if (brace1 != null) {
                                val matchings = BRACE_MATCH_MAP.get(brace1.token.id).get
                                assert(matchings != None)
                                if (matchings.contains(id)) {
                                    val numMatched = (braceId1, braceId1, id) match {
                                        // if resolved is "->", we may have matched two braces:
                                        case (ErlangTokenId.RArrow, ErlangTokenId.Try, ErlangTokenId.Catch) => 2
                                        case (ErlangTokenId.RArrow, _, ErlangTokenId.End | ErlangTokenId.After) => 2
                                            /** in case of: catch Expr
                                             *     test() ->
                                             *         catch 1+2.
                                             */
                                        case (ErlangTokenId.RArrow, _, ErlangTokenId.Catch) => 0
                                        case (ErlangTokenId.RArrow, _, _) => 1
                                        case (_, _, ErlangTokenId.RParen | ErlangTokenId.RBracket | ErlangTokenId.RBrace | ErlangTokenId.DGt) => 1
                                        case _ => 1
                                    }
                                    
                                    numMatched match {
                                        case 0 =>
                                            if (ordinalNonWhite == 0) {
                                                indent = prevIndent
                                            }
                                        case 1 =>
                                            justResolvedBrace = unresolvedBraces(size - 1)
                                            unresolvedBraces.remove(size - 1)
                                            if (ordinalNonWhite == 0) {
                                                indent = braceOffset1
                                                balance -= 1
                                            }
                                        case 2 =>
                                            justResolvedBrace = unresolvedBraces(size - 2)
                                            unresolvedBraces.remove(size - 1)
                                            unresolvedBraces.remove(size - 2)
                                            if (ordinalNonWhite == 0) {
                                                indent = braceOffset2
                                                balance -= 2
                                            }
                                        case _ =>
                                    }
                                }
                            }

                            // Add new unresolved brace
                            if (BRACE_MATCH_MAP.contains(id)) {
                                val isBrace = if (id == ErlangTokenId.Catch &&
                                                  (justResolvedBrace == null || justResolvedBrace.token.id != ErlangTokenId.Try)) {
                                    // don't add this catch as brace. Example:
                                    //     case catch ets:update_counter(Ets, Key, 1) of
                                    //         {'EXIT', {badarg, _}} -> ets:insert(Ets, {Key, 1});
                                    //         _ -> ok
                                    //     end.
                                    false
                                } else {
                                    true
                                }

                                if (isBrace) {
                                    val brace = new Brace(token)
                                    // will add indent of this line later
                                    brace.offsetOnline = offset - lineBegin
                                    brace.ordinalOnline = ordinalNonWhite
                                    brace.onProcessingLine = true
                                    unresolvedBraces += brace
                                }
                            }
                        } else if (category.equals("string") && (offset < lineBegin) ||
                                   LexUtil.isComment(id) ||
                                   id == ErlangTokenId.CommentTag) {
                            /**
                             * A literal string with more than one line is a whole token and when goes
                             * to second or followed lines, will has offset < lineBegin
                             */
                            if (ordinalNonWhite == 0 || ordinalNonWhite == -1) {
                                // No indentation for literal strings in Erlang, since they can
                                // contain newlines. Leave it as is.
                                indent = -1
                            }
                        }
                    }
                } while (ts.moveNext && ts.offset < lineEnd)
            } catch {case e:Throwable => e.printStackTrace}
        }

        // Now we've got the final indent of this line, adjust offset for new added
        // braces (which should be on this line)
        for (brace <- unresolvedBraces if brace.onProcessingLine) {
            brace.offsetOnline += indent
            if (brace.ordinalOnline == ordinalNonWhite) {
                brace.isLastOnLine = true
            }
        }

        // Compute indent for next line
        val newBalance = unresolvedBraces.size
        val lastUnresolved = if (newBalance > 0) unresolvedBraces(newBalance - 1) else null
        val lastUnresolvedId = if (lastUnresolved != null) lastUnresolved.token.id else null
        val lastUnresolvedText = if (lastUnresolved != null) lastUnresolved.token.text.toString else null

        // decide if next line is new or continued continute line
        val continueLine = if (lastNonWhiteToken == null) {
            // empty line or comment line
            false
        } else {
            val id = lastNonWhiteToken.id
            val name = id.name
            val text = lastNonWhiteToken.text.toString
            id match {
                case
                    ErlangTokenId.Stop | ErlangTokenId.RArrow | ErlangTokenId.If | ErlangTokenId.Case | ErlangTokenId.Of |
                    ErlangTokenId.Try | ErlangTokenId.Catch | ErlangTokenId.After | ErlangTokenId.Begin | ErlangTokenId.Receive |
                    ErlangTokenId.Fun | ErlangTokenId.End | ErlangTokenId.Semicolon |
                    ErlangTokenId.RParen | ErlangTokenId.RBracket | ErlangTokenId.RBrace | ErlangTokenId.DGt =>
                    false
                    //we have special case:
                case ErlangTokenId.Comma => lastUnresolvedId match {
                        case ErlangTokenId.LParen | ErlangTokenId.LBracket | ErlangTokenId.LBrace | ErlangTokenId.DLt if lastUnresolved.isLastOnLine => true
                        case _ => false // default
                    }
                case ErlangTokenId.LParen | ErlangTokenId.LBracket | ErlangTokenId.LBrace | ErlangTokenId.DLt =>
                    // the last unresolved brace is "(", "[", "{" , "<<",
                    // and it's of cource also the last non white token on this line
                    true
                case _ =>
                    // default
                    true
            }
        }

        // Compute or reset continue indent
        val continueIndent = if (continueLine) {
            if (_continueIndent == -1) {
                // new continue indent
                indent + hangingIndentSize
            } else {
                // keep the same continue indent
                _continueIndent
            }
        } else {
            // Reset continueIndent
            -1
        }

        val nextIndent = if (continueLine) {
            // Continue line
            continueIndent
        } else {
            if (lastUnresolved == null) {
                // All braces resolved
                0
            } else {
                val offset = lastUnresolved.offsetOnline
                lastUnresolved.token.id match {
                    case ErlangTokenId.RArrow =>
                        var nearestHangableBrace :Option[Brace] = None
                        var depth = 0
                        var continue = true
                        for (i <- unresolvedBraces.size - 1 to 0 if continue) {
                            val brace = unresolvedBraces(i)
                            depth += 1
                            if (brace.token.id != ErlangTokenId.RArrow) {
                                nearestHangableBrace = Some(brace)
                                continue = false
                            }
                        }

                        nearestHangableBrace match {
                            case None => newBalance * indentSize
                            case Some(x) => x.offsetOnline + depth * indentSize
                        }
                    case ErlangTokenId.LParen | ErlangTokenId.LBrace | ErlangTokenId.LBracket | ErlangTokenId.DLt =>
                        offset + lastUnresolved.token.text.toString.length
                    case _ =>
                        offset + indentSize
                }
            }
        }

        return Array(indent, nextIndent, continueIndent)
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
    //    private def syncOptions(doc:BaseDocument, style:CodeStyle) :Unit = {
    //        val formmatter = doc.getFormatter
    //        if (formatter.getSpacesPerTab != style.getIndentSize) {
    //            formatter.setSpacesPerTab(style.getIndentSize)
    //        }
    //    }
}
