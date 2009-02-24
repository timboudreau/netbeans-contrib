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
package org.netbeans.modules.erlang.editor

import _root_.java.util.{ArrayList,Collections,List}
import javax.swing.text.BadLocationException
import javax.swing.text.Caret
import javax.swing.text.Document
import javax.swing.text.JTextComponent

import org.netbeans.api.lexer.{Token,TokenHierarchy,TokenId,TokenSequence}
import org.netbeans.modules.csl.api.{EditorOptions,KeystrokeHandler,OffsetRange}
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.csl.spi.GsfUtilities
import org.netbeans.editor.{BaseDocument,Utilities}
import org.netbeans.modules.editor.indent.api.IndentUtils
import org.netbeans.modules.erlang.editor.ast.AstScope
import org.netbeans.modules.erlang.editor.lexer.{ErlangTokenId,LexUtil}
import org.openide.util.Exceptions

/**
 *
 * @author Caoyuan Deng
 */
class ErlangKeystrokeHandler extends KeystrokeHandler {

    /** When true, automatically reflows comments that are being edited according to the rdoc
     * conventions as well as the right hand side margin
     */
    //private static final boolean REFLOW_COMMENTS = Boolean.getBoolean("js.autowrap.comments"); // NOI18N
    /** When true, continue comments if you press return in a line comment (that does not
     * also have code on the same line
     */
    val CONTINUE_COMMENTS = true//Boolean.getBoolean("erlang.cont.comment") // NOI18N

    /** Tokens which indicate that we're within a literal string */
    private val STRING_TOKENS = Array(ErlangTokenId.StringLiteral).asInstanceOf[Array[TokenId]]
    /** Tokens which indicate that we're within a regexp string */
    //private val REGEXP_TOKENS :Array[TokenId] = Array(ErlangTokenId.StringLiteral).asInstanceOf[Array[TokenId]]
    /** When != -1, this indicates that we previously adjusted the indentation of the
     * line to the given offset, and if it turns out that the user changes that token,
     * we revert to the original indentation
     */
    private var previousAdjustmentOffset = -1
    /** True iff we're processing bracket matching AFTER the key has been inserted rather than before  */
    private var isAfter = false
    
    /**
     * The indentation to revert to when previousAdjustmentOffset is set and the token
     * changed
     */
    private var previousAdjustmentIndent = 0

    def isInsertMatchingEnabled(doc:BaseDocument) :Boolean = {
        // The editor options code is calling methods on BaseOptions instead of looking in the settings map :(
        //Boolean b = ((Boolean)Settings.getValue(doc.getKitClass, SettingsNames.PAIR_CHARACTERS_COMPLETION));
        //return b == null || b.booleanValue;
        EditorOptions.get(ErlangMimeResolver.MIME_TYPE) match {
            case null => true
            case options => options.getMatchBrackets
        }
    }

    @throws(classOf[BadLocationException])
    override
    def beforeBreak(document:Document, _offset:Int, target:JTextComponent) :Int = {
        isAfter = false
        var offset = _offset
        val caret = target.getCaret
        val doc = document.asInstanceOf[BaseDocument]

        val insertMatching = isInsertMatchingEnabled(doc)

        val lineBegin = Utilities.getRowStart(doc, offset)
        val lineEnd = Utilities.getRowEnd(doc, offset)

        if (lineBegin == offset && lineEnd == offset) {
            // Pressed return on a blank newline - do nothing
            return -1
        }

        val ts = LexUtil.tokenSequence(doc, offset) match {
            case None => return -1
            case Some(x) => x
        }

        ts.move(offset)
        if (!ts.moveNext && !ts.movePrevious) {
            return -1
        }

        val token = ts.token
        var id = token.id

        // Insert an end statement? Insert a } marker?
        val insertEndResult = Array(false)
        val insertRBraceResult = Array(false)
        val indentResult = Array(0)
        val insert = insertMatching && isEndMissing(doc, offset, false, insertEndResult, insertRBraceResult, null, indentResult)

        if (insert) {
            val insertEnd = insertEndResult(0)
            val insertRBrace = insertRBraceResult(0)
            val indent = indentResult(0)

            val afterLastNonWhite = Utilities.getRowLastNonWhite(doc, offset)

            // We've either encountered a further indented line, or a line that doesn't
            // look like the end we're after, so insert a matching end.
            val sb = new StringBuilder
            if (offset > afterLastNonWhite) {
                sb.append("\n") // XXX On Windows, do \r\n?
                sb.append(IndentUtils.createIndentString(doc, indent))
            } else {
                // I'm inserting a newline in the middle of a sentence, such as the scenario in #118656
                // I should insert the end AFTER the text on the line
                val restOfLine = doc.getText(offset, Utilities.getRowEnd(doc, afterLastNonWhite) - offset)
                sb.append(restOfLine)
                sb.append("\n")
                sb.append(IndentUtils.createIndentString(doc, indent))
                doc.remove(offset, restOfLine.length)
            }

            if (insertEnd) {
                sb.append("end") // NOI18N
            } else {
                assert(insertRBrace)
                sb.append("}") // NOI18N
            }

            val insertOffset = offset
            doc.insertString(insertOffset, sb.toString, null)
            caret.setDot(insertOffset)

            return -1
        }

        if (id == ErlangTokenId.Error) {
            // See if it's a block comment opener
            val text = token.text.toString
            if (text.startsWith("/*") && ts.offset == Utilities.getRowFirstNonWhite(doc, offset)) {
                val indent = GsfUtilities.getLineIndent(doc, offset)
                val sb = new StringBuilder
                sb.append(IndentUtils.createIndentString(doc, indent))
                sb.append(" * ") // NOI18N
                val offsetDelta = sb.length + 1
                sb.append("\n") // NOI18N
                sb.append(IndentUtils.createIndentString(doc, indent))
                sb.append(" */") // NOI18N
                // TODO - possibly populate associated types in JS-doc style!
                //if (text.startsWith("/**")) {
                //
                //}
                doc.insertString(offset, sb.toString, null)
                caret.setDot(offset)
                return offset + offsetDelta
            }
        }

        if (id == ErlangTokenId.StringLiteral && offset < ts.offset + ts.token.length) {
            // Instead of splitting a string "foobar" into "foo"+"bar", just insert a \ instead!
            //int indent = GsfUtilities.getLineIndent(doc, offset);
            //int delimiterOffset = id == ErlangTokenId.STRING_END ? ts.offset : ts.offset-1;
            //char delimiter = doc.getText(delimiterOffset,1).charAt(0);
            //doc.insertString(offset, delimiter + " + " + delimiter, null);
            //caret.setDot(offset+3);
            //return offset + 5 + indent;
            val str = if (id != ErlangTokenId.StringLiteral || offset > ts.offset) "\\n\\" else "\\"
            doc.insertString(offset, str, null)
            caret.setDot(offset + str.length)
            return offset + 1 + str.length
        }

        // Special case: since I do hash completion, if you try to type
        //     y = Thread.start {
        //         code here
        //     }
        // you end up with
        //     y = Thread.start {|}
        // If you hit newline at this point, you end up with
        //     y = Thread.start {
        //     |}
        // which is not as helpful as it would be if we were not doing hash-matching
        // (in that case we'd notice the brace imbalance, and insert the closing
        // brace on the line below the insert position, and indent properly.
        // Catch this scenario and handle it properly.
        if ((id == ErlangTokenId.RBrace || id == ErlangTokenId.RBracket) && (Utilities.getRowLastNonWhite(doc, offset) == offset)) {
            for (prevToken <- LexUtil.token(doc, offset - 1)) {
                val prevTokenId = prevToken.id
                (id, prevTokenId) match {
                    case (ErlangTokenId.RBrace, ErlangTokenId.LBrace) | (ErlangTokenId.RBracket, ErlangTokenId.LBracket) =>
                        val indent = GsfUtilities.getLineIndent(doc, offset)
                        val sb = new StringBuilder
                        // XXX On Windows, do \r\n?
                        sb.append("\n") // NOI18N
                        sb.append(IndentUtils.createIndentString(doc, indent))
                        val insertOffset = offset // offset < length ? offset+1 : offset
                        doc.insertString(insertOffset, sb.toString, null)
                        caret.setDot(insertOffset)
                }
            }
        }

        if (id == ErlangTokenId.Ws) {
            // Pressing newline in the whitespace before a comment
            // should be identical to pressing newline with the caret
            // at the beginning of the comment
            val begin = Utilities.getRowFirstNonWhite(doc, offset)
            if (begin != -1 && offset < begin) {
                ts.move(begin)
                if (ts.moveNext) {
                    id = ts.token.id
                    if (id == ErlangTokenId.LineComment) {
                        offset = begin
                    }
                }
            }
        }

        val isComment = id match {
            case ErlangTokenId.LineComment => true
            case ErlangTokenId.Nl if ts.movePrevious && ts.token.id == ErlangTokenId.LineComment =>
                //ts.moveNext
                true
            case _ => false
        }

        if (isComment) {
            // Only do this if the line only contains comments OR if there is content to the right on this line,
            // or if the next line is a comment!

            var continueComment = false
            val begin = Utilities.getRowFirstNonWhite(doc, offset)

            // We should only continue comments if the previous line had a comment
            // (and a comment from the beginning, not a trailing comment)
            var previousLineWasComment = false
            var nextLineIsComment = false
            val rowStart = Utilities.getRowStart(doc, offset)
            if (rowStart > 0) {
                val prevBegin = Utilities.getRowFirstNonWhite(doc, rowStart - 1)
                if (prevBegin != -1) {
                    for (firstToken <-LexUtil.token(doc, prevBegin) if firstToken.id == ErlangTokenId.LineComment) {
                        previousLineWasComment = true
                    }
                }
            }
            val rowEnd = Utilities.getRowEnd(doc, offset)
            if (rowEnd < doc.getLength) {
                val nextBegin = Utilities.getRowFirstNonWhite(doc, rowEnd + 1)
                if (nextBegin != -1) {
                    for (firstToken <- LexUtil.token(doc, nextBegin) if firstToken.id == ErlangTokenId.LineComment) {
                        nextLineIsComment = true
                    }
                }
            }

            // See if we have more input on this comment line (to the right
            // of the inserted newline); if so it's a "split" operation on
            // the comment
            if (previousLineWasComment || nextLineIsComment ||
                (offset > ts.offset && offset < ts.offset + ts.token.length)) {
                if (ts.offset + token.length > offset + 1) {
                    // See if the remaining text is just whitespace
                    val trailing = doc.getText(offset, Utilities.getRowEnd(doc, offset) - offset)
                    if (trailing.trim.length != 0) {
                        continueComment = true
                    }
                } else if (CONTINUE_COMMENTS) {
                    // See if the "continue comments" options is turned on, and this is a line that
                    // contains only a comment (after leading whitespace)
                    for (firstToken <- LexUtil.token(doc, begin) if firstToken.id == ErlangTokenId.LineComment) {
                        continueComment = true
                    }
                }
                if (!continueComment) {
                    // See if the next line is a comment; if so we want to continue
                    // comments editing the middle of the comment
                    val nextLine = Utilities.getRowEnd(doc, offset) + 1
                    if (nextLine < doc.getLength) {
                        val nextLineFirst = Utilities.getRowFirstNonWhite(doc, nextLine)
                        if (nextLineFirst != -1) {
                            for (firstToken <- LexUtil.token(doc, nextLineFirst) if firstToken != null && firstToken.id == ErlangTokenId.LineComment) {
                                continueComment = true
                            }
                        }
                    }
                }
            }

            if (continueComment) {
                // Line comments should continue
                val indent = GsfUtilities.getLineIndent(doc, offset)
                val sb = new StringBuilder
                sb.append(IndentUtils.createIndentString(doc, indent))
                sb.append("%%") // NOI18N
                // Copy existing indentation
                val afterSlash = begin + 2
                val line = doc.getText(afterSlash, Utilities.getRowEnd(doc, afterSlash) - afterSlash)
                val l = line.length
                def loop(i:Int) :Unit = line.charAt(i) match {                        
                    case _ if i >= l =>
                    case c@(' ' | '\t') => 
                        sb.append(c)
                        loop(i + 1)
                    case _ =>               
                }
                loop(0)

                var insertOffset = offset // offset < length ? offset+1 : offset
                if (offset == begin && insertOffset > 0) {
                    insertOffset = Utilities.getRowStart(doc, offset)
                    val sp = Utilities.getRowStart(doc, offset) + sb.length
                    doc.insertString(insertOffset, sb.toString, null)
                    caret.setDot(sp)
                    return sp
                }
                doc.insertString(insertOffset, sb.toString, null)
                caret.setDot(insertOffset)
                return insertOffset + sb.length + 1
            }
        }

        return -1
    }

    /**
     * Determine if an "end" or "}" is missing following the caret offset.
     * The logic used is to check the text on the current line for block initiators
     * (e.g. "def", "for", "{" etc.) and then see if a corresponding close is
     * found after the same indentation level.
     *
     * @param doc The document to be checked
     * @param offset The offset of the current line
     * @param skipJunk If false, only consider the current line (of the offset)
     *   as the possible "block opener"; if true, look backwards across empty
     *   lines and comment lines as well.
     * @param insertEndResult Null, or a boolean 1-element array whose first
     *   element will be set to true iff this method determines that "end" should
     *   be inserted
     * @param insertRBraceResult Null, or a boolean 1-element array whose first
     *   element will be set to true iff this method determines that "}" should
     *   be inserted
     * @param startOffsetResult Null, or an integer 1-element array whose first
     *   element will be set to the starting offset of the opening block.
     * @param indentResult Null, or an integer 1-element array whose first
     *   element will be set to the indentation level "end" or "}" should be
     *   indented to when inserted.
     * @return true if something is missing; insertEndResult, insertRBraceResult
     *   and identResult will provide the more specific return values in their
     *   first elements.
     */
    @throws(classOf[BadLocationException])
    def isEndMissing(doc:BaseDocument, offset:Int, skipJunk:Boolean,
                     insertEndResult:Array[Boolean], insertRBraceResult:Array[Boolean], startOffsetResult:Array[Int],
                     indentResult:Array[Int]) :Boolean = {

        val th = TokenHierarchy.get(doc)

        val length = doc.getLength

        // Insert an end statement? Insert a } marker?
        // Do so if the current line contains an unmatched begin marker,
        // AND a "corresponding" marker does not exist.
        // This will be determined as follows: Look forward, and check
        // that we don't have "indented" code already (tokens at an
        // indentation level higher than the current line was), OR that
        // there is no actual end or } coming up.
        if (startOffsetResult != null) {
            startOffsetResult(0) = Utilities.getRowFirstNonWhite(doc, offset)
        }

        val beginEndBalance = LexUtil.beginEndLineBalance(doc, offset, true)
        val braceBalance = LexUtil.lineBalance(doc, offset, ErlangTokenId.LBrace, ErlangTokenId.RBrace)

        /** Do not try to guess the condition when offset is before the unbalanced brace */
        if (beginEndBalance == 1 ||
            braceBalance.size == 1 && offset > braceBalance.top.offset(th)) {
            // There is one more opening token on the line than a corresponding
            // closing token.  (If there's is more than one we don't try to help.)
            val indent = GsfUtilities.getLineIndent(doc, offset)

            // Look for the next nonempty line, and if its indent is > indent,
            // or if its line balance is -1 (e.g. it's an end) we're done
            var insertEnd = beginEndBalance > 0
            var insertRBrace = !braceBalance.isEmpty
            var next = Utilities.getRowEnd(doc, offset) + 1

            def loop(next:Int) :Unit = {
                if (Utilities.isRowEmpty(doc, next) || Utilities.isRowWhite(doc, next) ||
                    LexUtil.isCommentOnlyLine(doc, next)) {
                    loop(Utilities.getRowEnd(doc, next) + 1)
                }

                val nextIndent = GsfUtilities.getLineIndent(doc, next)
                if (nextIndent > indent) {
                    insertEnd = false
                    insertRBrace = false
                } else if (nextIndent == indent) {
                    if (insertEnd) {
                        if (LexUtil.beginEndLineBalance(doc, next, false) < 0) {
                            insertEnd = false
                        } else {
                            // See if I have a structure word like "else", "ensure", etc.
                            // (These are indent words that are not also begin words)
                            // and if so refrain from inserting the end
                            val lineBegin = Utilities.getRowFirstNonWhite(doc, next)

                            val token = LexUtil.token(doc, lineBegin)
                            for (token <- LexUtil.token(doc, lineBegin) if LexUtil.isIndentToken(token.id) &&
                                 !LexUtil.isBeginToken(token.id)) {
                                insertEnd = false
                            }
                        }
                    } else if (insertRBrace &&
                               LexUtil.lineBalance(doc, next, ErlangTokenId.LBrace, ErlangTokenId.RBrace).size < 0) {
                        insertRBrace = false
                    }
                }

            }
            loop(next)

            if (insertEndResult != null) {
                insertEndResult(0) = insertEnd
            }

            if (insertRBraceResult != null) {
                insertRBraceResult(0) = insertRBrace
            }

            if (indentResult != null) {
                indentResult(0) = indent
            }

            return insertEnd || insertRBrace
        }

        return false
    }

    @throws(classOf[BadLocationException])
    override
    def beforeCharInserted(document:Document, _caretOffset:Int, target:JTextComponent, ch:Char) :Boolean = {
        isAfter = false
        var caretOffset = _caretOffset
        val caret = target.getCaret
        val doc = document.asInstanceOf[BaseDocument]

        if (!isInsertMatchingEnabled(doc)) {
            return false
        }

        //dumpTokens(doc, caretOffset)

        if (target.getSelectionStart != -1) {
            var isCodeTemplateEditing = GsfUtilities.isCodeTemplateEditing(doc)
            if (isCodeTemplateEditing) {
                val start = target.getSelectionStart
                val end = target.getSelectionEnd
                if (start < end) {
                    target.setSelectionStart(start)
                    target.setSelectionEnd(start)
                    caretOffset = start
                    caret.setDot(caretOffset)
                    doc.remove(start, end - start)
                }
                // Fall through to do normal insert matching work
            } else ch match {
                case '"' | '\'' | '(' | '{' | '[' | '/' =>
                    // Bracket the selection
                    val selection = target.getSelectedText
                    if (selection != null && selection.length > 0) {
                        val firstChar = selection.charAt(0)
                        if (firstChar != ch) {
                            val start = target.getSelectionStart
                            val end = target.getSelectionEnd
                            for (ts <- LexUtil.positionedSequence(doc, start) if ts.token.id != ErlangTokenId.StringLiteral) { // Not inside strings!
                                var lastChar = selection.charAt(selection.length - 1)
                                // Replace the surround-with chars?
                                if (selection.length > 1) firstChar match {
                                    case '"' | '\'' | '(' | '{' | '[' | '/' if lastChar == matching(firstChar) =>
                                        doc.remove(end - 1, 1)
                                        doc.insertString(end - 1, "" + matching(ch), null)
                                        doc.remove(start, 1)
                                        doc.insertString(start, "" + ch, null)
                                        target.getCaret.setDot(end)
                                    case _ =>
                                } else {
                                    // No, insert around
                                    doc.remove(start, end - start)
                                    doc.insertString(start, ch + selection + matching(ch), null)
                                    target.getCaret.setDot(start + selection.length + 2)
                                }

                                return true
                            }
                        }
                    }
                case _ =>
            }
        }

        val ts = LexUtil.tokenSequence(doc, caretOffset) match {
            case None => return false
            case Some(x) => x
        }

        ts.move(caretOffset)

        if (!ts.moveNext && !ts.movePrevious) {
            return false
        }

        val token = ts.token
        val id = token.id
        var stringTokens :Array[TokenId] = null
        var beginTokenId :Array[TokenId] = null

        // "/" is handled AFTER the character has been inserted since we need the lexer's help
        //        if (ch == '\"' || ch == '\'') {
        //            stringTokens = STRING_TOKENS
        //            beginTokenId = ErlangTokenId.STRING_BEGIN
        //        } else if (id == ErlangTokenId.Error) {
        //            //String text = token.text.toString
        //
        //            ts.movePrevious
        //
        //            val prevId = ts.token.id
        //
        //            if (prevId == ErlangTokenId.STRING_BEGIN) {
        //                stringTokens = STRING_TOKENS
        //                beginTokenId = prevId
        //            } else if (prevId == ErlangTokenId.REGEXP_BEGIN) {
        //                stringTokens = REGEXP_TOKENS
        //                beginTokenId = ErlangTokenId.REGEXP_BEGIN
        //            }
        //        } else if ((id == ErlangTokenId.STRING_BEGIN) &&
        //                   (caretOffset == (ts.offset + 1))) {
        //            if (!Character.isLetter(ch)) { // %q, %x, etc. Only %[], %!!, %<space> etc. is allowed
        //                stringTokens = STRING_TOKENS
        //                beginTokenId = id
        //            }
        //        } else if (((id == ErlangTokenId.STRING_BEGIN) && (caretOffset == (ts.offset + 2))) ||
        //                   (id == ErlangTokenId.STRING_END)) {
        //            stringTokens = STRING_TOKENS
        //            beginTokenId = ErlangTokenId.STRING_BEGIN
        //        } else if (((id == ErlangTokenId.REGEXP_BEGIN) && (caretOffset == (ts.offset + 2))) ||
        //                   (id == ErlangTokenId.REGEXP_END)) {
        //            stringTokens = REGEXP_TOKENS
        //            beginTokenId = ErlangTokenId.REGEXP_BEGIN
        //        }
        //
        //        if (stringTokens != null) {
        //            val inserted = completeQuote(doc, caretOffset, caret, ch, stringTokens, beginTokenId)
        //
        //            if (inserted) {
        //                caret.setDot(caretOffset + 1)
        //
        //                return true
        //            } else {
        //                return false
        //            }
        //        }

        return false
    }

    // For debugging purposes
    // Probably obsolete - see the tokenspy utility in gsf debugging tools for better help
    //private void dumpTokens(BaseDocument doc, int dot) {
    //    TokenSequence< ?extends ErlangTokenId> ts = LexUtil.getTokenSequence(doc)
    //
    //    System.out.println("Dumping tokens for dot=" + dot)
    //    int prevOffset = -1;
    //    if (ts != null) {
    //        ts.moveFirst;
    //        int index = 0;
    //        do {
    //            Token<? extends ErlangTokenId> token = ts.token;
    //            int offset = ts.offset;
    //            String id = token.id.toString;
    //            String text = token.text.toString.replaceAll("\n", "\\\\n");
    //            if (prevOffset < dot && dot <= offset) {
    //                System.out.print(" ===> ");
    //            }
    //            System.out.println("Token " + index + ": offset=" + offset + ": id=" + id + ": text=" + text);
    //            index += 1
    //            prevOffset = offset;
    //        } while (ts.moveNext);
    //    }
    //}
    /**
     * A hook method called after a character was inserted into the
     * document. The function checks for special characters for
     * completion []'"{} and other conditions and optionally performs
     * changes to the doc and or caret (complets braces, moves caret,
     * etc.)
     * @param document the document where the change occurred
     * @param dotPos position of the character insertion
     * @param target The target
     * @param ch the character that was inserted
     * @return Whether the insert was handled
     * @throws BadLocationException if dotPos is not correct
     */
    @throws(classOf[BadLocationException])
    override
    def afterCharInserted(document:Document, dotPos:Int, target:JTextComponent, ch:Char) :Boolean = {
        isAfter = true
        val caret = target.getCaret
        val doc = document.asInstanceOf[BaseDocument]

        //        if (REFLOW_COMMENTS) {
        //            Token<?extends ErlangTokenId> token = LexUtil.getToken(doc, dotPos);
        //            if (token != null) {
        //                TokenId id = token.id;
        //                if (id == ErlangTokenId.LINE_COMMENT || id == ErlangTokenId.DOCUMENTATION) {
        //                    new ReflowParagraphAction.reflowEditedComment(target);
        //                }
        //            }
        //        }

        // See if our automatic adjustment of indentation when typing (for example) "end" was
        // premature - if you were typing a longer word beginning with one of my adjustment
        // prefixes, such as "endian", then put the indentation back.
        if (previousAdjustmentOffset != -1) {
            if (dotPos == previousAdjustmentOffset) {
                // Revert indentation iff the character at the insert position does
                // not start a new token (e.g. the previous token that we reindented
                // was not complete)
                for (ts <- LexUtil.tokenSequence(doc, dotPos)) {
                    ts.move(dotPos)
                    if (ts.moveNext && (ts.offset < dotPos)) {
                        GsfUtilities.setLineIndentation(doc, dotPos, previousAdjustmentIndent)
                    }
                }
            }

            previousAdjustmentOffset = -1
        }

        //dumpTokens(doc, dotPos)
        ch match {
            //        case '#': {
            //            // Automatically insert #{^} when typing "#" in a quoted string or regexp
            //            Token<?extends ErlangTokenId> token = LexUtil.getToken(doc, dotPos);
            //            if (token == null) {
            //                return true;
            //            }
            //            TokenId id = token.id;
            //
            //            if (id == ErlangTokenId.QUOTED_STRING_LITERAL || id == ErlangTokenId.REGEXP_LITERAL) {
            //                document.insertString(dotPos+1, "{}", null);
            //                // Skip the "{" to place the caret between { and }
            //                caret.setDot(dotPos+2);
            //            }
            //            break;
            //        }
            case '}' | '{' | ')' | ']' |'(' | '[' | 'd' =>
                if (!isInsertMatchingEnabled(doc)) {
                    return false
                }

                val token = LexUtil.token(doc, dotPos) match {
                    case None => return true
                    case Some(x) => x
                }
                token.id match {
                    case ErlangTokenId.LBracket | ErlangTokenId.RBracket
                        | ErlangTokenId.LBrace | ErlangTokenId.RBrace
                        | ErlangTokenId.LParen | ErlangTokenId.RParen =>
                        ch match {
                            case ']' =>
                                skipClosingBracket(doc, caret, ch, ErlangTokenId.RBracket)
                            case ')' =>
                                skipClosingBracket(doc, caret, ch, ErlangTokenId.RParen)
                            case '}' =>
                                skipClosingBracket(doc, caret, ch, ErlangTokenId.RBrace)
                            case '[' | '(' | '{' =>
                                completeOpeningBracket(doc, dotPos, caret, ch)
                            case _ =>
                        }
                    case _ =>
                }

                // Reindent blocks (won't do anything if } is not at the beginning of a line
                ch match {
                    case '}' =>
                        reindent(doc, dotPos, ErlangTokenId.RBrace, caret)
                    case ']' =>
                        reindent(doc, dotPos, ErlangTokenId.RBracket, caret)
                    case 'd' =>
                        // See if it's the end of an "end" or an "...." - if so, reindent
                        reindent(doc, dotPos, ErlangTokenId.End, caret)
                        //reindent(doc, dotPos, ErlangTokenId.ENSURE, caret);
                        //reindent(doc, dotPos, ErlangTokenId.RESCUE, caret);
                    case _ =>
                }
                //
                //        case 'f':
                //            // See if it's the end of an "else" - if so, reindent
                //            reindent(doc, dotPos, ErlangTokenId.ELSIF, caret);
                //
                //            break;
                //
                //        case 'n':
                //            // See if it's the end of an "when" - if so, reindent
                //            reindent(doc, dotPos, ErlangTokenId.WHEN, caret);
                //
                //            break;

            case '/' =>
                if (!isInsertMatchingEnabled(doc)) {
                    return false
                }

                // Bracket matching for regular expressions has to be done AFTER the
                // character is inserted into the document such that I can use the lexer
                // to determine whether it's a division (e.g. x/y) or a regular expression (/foo/)
                for (ts <- LexUtil.positionedSequence(doc, dotPos)) {
                    val token = ts.token
                    token.id match {
                        case ErlangTokenId.LineComment =>
                            // Did you just type "//" - make sure this didn't turn into ///
                            // where typing the first "/" inserted "//" and the second "/" appended
                            // another "/" to make "///"
                            if (dotPos == ts.offset + 1 && dotPos + 1 < doc.getLength &&
                                doc.getText(dotPos + 1, 1).charAt(0) == '/') {
                                doc.remove(dotPos, 1)
                                caret.setDot(dotPos + 1)
                                return true
                            }
                        
                            //                        case ErlangTokenId.REGEXP_BEGIN || ErlangTokenId.REGEXP_END =>
                            //                            TokenId[] stringTokens = REGEXP_TOKENS;
                            //                            TokenId beginTokenId = ErlangTokenId.REGEXP_BEGIN;
                            //
                            //                            boolean inserted =
                            //                            completeQuote(doc, dotPos, caret, ch, stringTokens, beginTokenId);
                            //
                            //                            if (inserted) {
                            //                                caret.setDot(dotPos + 1);
                            //                            }
                            //
                            //                            return inserted;
                            //
                            true
                    }
                }
            case _ =>
        }

        true
    }

    @throws(classOf[BadLocationException])
    private def reindent(doc:BaseDocument, offset:Int, id:TokenId, caret:Caret) :Unit = {
        for (ts <- LexUtil.tokenSequence(doc, offset)) {
            ts.move(offset)

            if (!ts.moveNext && !ts.movePrevious) {
                return
            }

            val token = ts.token

            if (token.id == id) {
                val rowFirstNonWhite = Utilities.getRowFirstNonWhite(doc, offset)
                // Ensure that this token is at the beginning of the line
                if (ts.offset > rowFirstNonWhite) {
                    //                    if (RubyUtils.isRhtmlDocument(doc)) {
                    //                        // Allow "<%[whitespace]*" to preceed
                    //                        String s = doc.getText(rowFirstNonWhite, ts.offset-rowFirstNonWhite);
                    //                        if (!s.matches("<%\\s*")) {
                    //                            return;
                    //                        }
                    //                    } else {
                    return
                    //                    }
                }

                val begin = id match {
                    case ErlangTokenId.RBrace =>
                        LexUtil.findBwd(ts, ErlangTokenId.LBrace, ErlangTokenId.RBrace)
                    case ErlangTokenId.RBracket =>
                        LexUtil.findBwd(ts, ErlangTokenId.LBracket, ErlangTokenId.RBracket)
                    case ErlangTokenId.End => 
                        LexUtil.findBwd(ts, PAIRS.get(ErlangTokenId.End).get, ErlangTokenId.End)
                    case _ => OffsetRange.NONE
                        //LexUtil.findBegin(doc, ts)
                }
                begin match {
                    case OffsetRange.NONE =>
                    case _ =>
                        val beginOffset = begin.getStart
                        val indent = GsfUtilities.getLineIndent(doc, beginOffset)
                        previousAdjustmentIndent = GsfUtilities.getLineIndent(doc, offset)
                        GsfUtilities.setLineIndentation(doc, offset, indent)
                        previousAdjustmentOffset = caret.getDot
                }
            }
        }
    }

    /** close to opens */
    private val PAIRS :Map[TokenId, Set[TokenId]] = Map(ErlangTokenId.RParen   -> Set(ErlangTokenId.LParen),
                                                        ErlangTokenId.RBrace   -> Set(ErlangTokenId.LBrace),
                                                        ErlangTokenId.RBracket -> Set(ErlangTokenId.LBracket),
                                                        ErlangTokenId.End      -> Set(ErlangTokenId.Begin,
                                                                                      ErlangTokenId.Case,
                                                                                      ErlangTokenId.If,
                                                                                      ErlangTokenId.Receive,
                                                                                      ErlangTokenId.Try))

    override
    def findMatching(document:Document, _offset:Int) :OffsetRange = {
        val doc = document.asInstanceOf[BaseDocument]
        var offset = _offset
        for (ts <- LexUtil.tokenSequence(doc, offset)) {
            ts.move(offset)
            if (!ts.moveNext) {
                return OffsetRange.NONE
            }

            var token = ts.token
            if (token == null) {
                return OffsetRange.NONE
            }

            var id = token.id
            if (id == ErlangTokenId.Ws) {
                // ts.move(offset) gives the token to the left of the caret.
                // If you have the caret right at the beginning of a token, try
                // the token to the right too - this means that if you have
                //  "   |def" it will show the matching "end" for the "def".
                offset += 1
                ts.move(offset)
                if (ts.moveNext && ts.offset <= offset) {
                    token = ts.token
                    id = token.id
                }
            }

            for (closeOpens <- PAIRS) closeOpens match {
                case (close, opens) if opens.contains(id) =>
                    LexUtil.findFwd(ts, opens, close) match {
                        case OffsetRange.NONE =>
                        case x => return x
                    }
                case _ =>
            }

            for (opens <- PAIRS.get(id)) {
                LexUtil.findBwd(ts, opens, id) match {
                    case OffsetRange.NONE =>
                    case x => return x
                }
            }
        }

        OffsetRange.NONE
    }

    /**
     * Hook called after a character *ch* was backspace-deleted from
     * *doc*. The function possibly removes bracket or quote pair if
     * appropriate.
     * @param doc the document
     * @param dotPos position of the change
     * @param caret caret
     * @param ch the character that was deleted
     */
    @throws(classOf[BadLocationException])
    override
    def charBackspaced(document:Document, dotPos:Int, target:JTextComponent, ch:Char) :Boolean = {
        val doc = document.asInstanceOf[BaseDocument]
        ch match {
            case ' ' =>
                // Backspacing over "// " ? Delete the "//" too!
                for (ts <- LexUtil.positionedSequence(doc, dotPos) if ts.token.id == ErlangTokenId.LineComment) {
                    if (ts.offset == dotPos - 2) {
                        doc.remove(dotPos - 2, 2)
                        target.getCaret.setDot(dotPos - 2)

                        return true
                    }
                }
            case '{' | '(' | '[' => // and '{' via fallthrough
                LexUtil.tokenChar(doc, dotPos) match {
                    case ']' if LexUtil.tokenBalance(doc, ErlangTokenId.LBracket, ErlangTokenId.RBracket, dotPos) != 0 =>
                        doc.remove(dotPos, 1)
                    case ')' if LexUtil.tokenBalance(doc, ErlangTokenId.LParen, ErlangTokenId.RParen, dotPos) != 0 =>
                        doc.remove(dotPos, 1)
                    case '}' if LexUtil.tokenBalance(doc, ErlangTokenId.LBrace, ErlangTokenId.RBrace, dotPos) != 0 =>
                        doc.remove(dotPos, 1)
                    case _ =>
                }
            case '|' | '\"' | '\'' =>
                val _match = doc.getChars(dotPos, 1) match {
                    case null =>
                    case _match if _match(0) == ch => doc.remove(dotPos, 1)
                    case _ =>
                }
            case _ =>
        }

        true
    }

    /**
     * A hook to be called after closing bracket ) or ] was inserted into
     * the document. The method checks if the bracket should stay there
     * or be removed and some exisitng bracket just skipped.
     *
     * @param doc the document
     * @param dotPos position of the inserted bracket
     * @param caret caret
     * @param bracket the bracket character ']' or ')'
     */
    @throws(classOf[BadLocationException])
    def skipClosingBracket(doc:BaseDocument, caret:Caret, bracket:Char, bracketId:TokenId) :Unit =  {
        val caretOffset = caret.getDot

        if (isSkipClosingBracket(doc, caretOffset, bracketId)) {
            doc.remove(caretOffset - 1, 1)
            caret.setDot(caretOffset) // skip closing bracket
        }
    }

    /**
     * Check whether the typed bracket should stay in the document
     * or be removed.
     * <br>
     * This method is called by <code>skipClosingBracket</code>.
     *
     * @param doc document into which typing was done.
     * @param caretOffset
     */
    @throws(classOf[BadLocationException])
    private def isSkipClosingBracket(doc:BaseDocument, caretOffset:Int,  bracketId:TokenId) :Boolean = {
        // First check whether the caret is not after the last char in the document
        // because no bracket would follow then so it could not be skipped.
        if (caretOffset == doc.getLength) {
            return false // no skip in this case
        }

        var skipClosingBracket = false // by default do not remove

        val ts = LexUtil.tokenSequence(doc, caretOffset) match {
            case None => return false
            case Some(x) => x
        }
        // XXX BEGIN TOR MODIFICATIONS
        //ts.move(caretOffset+1)
        ts.move(caretOffset)
        if (!ts.moveNext) {
            return false
        }

        var token = ts.token
        // Check whether character follows the bracket is the same bracket
        if (token != null && token.id == bracketId) {
            val bracketIntId = bracketId.ordinal
            val leftBracketIntId = if (bracketIntId == ErlangTokenId.RParen.ordinal) {
                ErlangTokenId.LParen.ordinal
            } else ErlangTokenId.LBracket.ordinal

            // Skip all the brackets of the same type that follow the last one
            ts.moveNext

            var nextToken = ts.token
            def loopToken :Unit = if (nextToken != null && nextToken.id == bracketId) {
                token = nextToken
                if (ts.moveNext) {
                    nextToken = ts.token
                    loopToken
                }
            }            
            loopToken

            // token var points to the last bracket in a group of two or more right brackets
            // Attempt to find the left matching bracket for it
            // Search would stop on an extra opening left brace if found
            var braceBalance = 0 // balance of '{' and '}'
            var bracketBalance = -1 // balance of the brackets or parenthesis
            var lastRBracket = token
            ts.movePrevious
            token = ts.token

            var finished = false
            def loop :Unit = if (!finished && token != null) {
                val tokenIntId = token.id.ordinal
                token.id match {
                    case ErlangTokenId.LParen | ErlangTokenId.LBracket =>
                        if (tokenIntId == bracketIntId) {
                            bracketBalance += 1

                            if (bracketBalance == 0) {
                                if (braceBalance != 0) {
                                    // Here the bracket is matched but it is located
                                    // inside an unclosed brace block
                                    // e.g. ... ->( } a|)
                                    // which is in fact illegal but it's a question
                                    // of what's best to do in this case.
                                    // We chose to leave the typed bracket
                                    // by setting bracketBalance to 1.
                                    // It can be revised in the future.
                                    bracketBalance = 1
                                }

                                finished = true
                            }
                        }
                    case ErlangTokenId.RParen | ErlangTokenId.RBracket =>
                        if (tokenIntId == bracketIntId) {
                            bracketBalance -= 1
                        }
                    case ErlangTokenId.LBrace =>
                        braceBalance += 1
                        if (braceBalance > 0) { // stop on extra left brace
                            finished = true
                        }
                    case ErlangTokenId.RBrace =>
                        braceBalance -= 1
                    case _ =>
                }

                if (ts.movePrevious) {
                    token = ts.token
                    loop
                }
            }
            loop

            if (bracketBalance != 0) { // not found matching bracket
                // Remove the typed bracket as it's unmatched
                skipClosingBracket = true
            } else { // the bracket is matched
                // Now check whether the bracket would be matched
                // when the closing bracket would be removed
                // i.e. starting from the original lastRBracket token
                // and search for the same bracket to the right in the text
                // The search would stop on an extra right brace if found
                braceBalance = 0
                bracketBalance = 1 // simulate one extra left bracket

                //token = lastRBracket.getNext
                val th = TokenHierarchy.get(doc)

                val ofs = lastRBracket.offset(th)

                ts.move(ofs)
                ts.moveNext
                
                token = ts.token
                finished = false
                def loop :Unit = if (!finished && token != null) {
                    token.id match {
                        //int tokenIntId = token.getTokenID.getNumericID
                        case ErlangTokenId.LParen | ErlangTokenId.LBracket =>
                            if (token.id.ordinal == leftBracketIntId) {
                                bracketBalance += 1
                            }
                        case ErlangTokenId.RParen | ErlangTokenId.RBracket =>
                            if (token.id.ordinal == bracketIntId) {
                                bracketBalance -= 1
                                if (bracketBalance == 0) {
                                    if (braceBalance != 0) {
                                        // Here the bracket is matched but it is located
                                        // inside an unclosed brace block
                                        // which is in fact illegal but it's a question
                                        // of what's best to do in this case.
                                        // We chose to leave the typed bracket
                                        // by setting bracketBalance to -1.
                                        // It can be revised in the future.
                                        bracketBalance = -1
                                    }

                                    finished = true
                                }
                            }
                        case ErlangTokenId.LBrace =>
                            braceBalance += 1
                        case ErlangTokenId.RBrace =>
                            braceBalance -= 1
                            if (braceBalance < 0) { // stop on extra right brace
                                finished = true
                            }
                        case _ =>
                    }

                    //token = token.getPrevious // done regardless of finished flag state
                    if (!ts.movePrevious) {
                        token = ts.token
                        loop
                    }
                }
                loop
                
                // If bracketBalance == 0 the bracket would be matched
                // by the bracket that follows the last right bracket.
                skipClosingBracket = (bracketBalance == 0)
            }
        }

        skipClosingBracket
    }

    /**
     * Check for various conditions and possibly add a pairing bracket
     * to the already inserted.
     * @param doc the document
     * @param dotPos position of the opening bracket (already in the doc)
     * @param caret caret
     * @param bracket the bracket that was inserted
     */
    @throws(classOf[BadLocationException])
    private def completeOpeningBracket(doc:BaseDocument, dotPos:Int, caret: Caret, bracket:Char) :Unit = {
        if (isCompletablePosition(doc, dotPos + 1)) {
            val matchingBracket = "" + matching(bracket)
            doc.insertString(dotPos + 1, matchingBracket, null)
            caret.setDot(dotPos + 1)
        }
    }

    // XXX TODO Use embedded string sequence here and see if it
    // really is escaped. I know where those are!
    // TODO Adjust for JavaScript
    @throws(classOf[BadLocationException])
    private def isEscapeSequence(doc:BaseDocument, dotPos:Int) :Boolean = {
        if (dotPos <= 0) {
            return false
        }

        val previousChar = doc.getChars(dotPos - 1, 1)(0)

        return previousChar == '\\'
    }

    /**
     * Check for conditions and possibly complete an already inserted
     * quote .
     * @param doc the document
     * @param dotPos position of the opening bracket (already in the doc)
     * @param caret caret
     * @param bracket the character that was inserted
     */
    @throws(classOf[BadLocationException])
    private def completeQuote(doc:BaseDocument, dotPos:Int, caret:Caret, bracket:Char,
                              stringTokens:Array[TokenId], beginToken: TokenId) :Boolean = {
        if (isEscapeSequence(doc, dotPos)) { // \" or \' typed
            return false
        }

        // Examine token at the caret offset
        if (doc.getLength < dotPos) {
            return false
        }

        val ts = LexUtil.tokenSequence(doc, dotPos) match {
            case None => return false
            case Some(x) => x
        }

        ts.move(dotPos)
        if (!ts.moveNext && !ts.movePrevious) {
            return false
        }

        var token = ts.token
        var previousToken:Token[TokenId] = if (ts.movePrevious) {
            ts.token
        } else null

        val lastNonWhite = Utilities.getRowLastNonWhite(doc, dotPos)

        // eol - true if the caret is at the end of line (ignoring whitespaces)
        val eol = lastNonWhite < dotPos

        if (LexUtil.isComment(token.id)) {
            return false
        } else if (token.id == ErlangTokenId.Ws && eol && (dotPos - 1) > 0) {
            // check if the caret is at the very end of the line comment
            token = LexUtil.token(doc, dotPos - 1).get

            if (token.id == ErlangTokenId.LineComment) {
                return false
            }
        }

        val completablePosition = isQuoteCompletablePosition(doc, dotPos)

        var insideString = false
        val id = token.id

        stringTokens.find{_ == id} match {
            case None =>
            case Some(x) => insideString = true
        }

        if (id == ErlangTokenId.Error && previousToken != null && previousToken.id == beginToken) {
            insideString = true
        }

        if (id == ErlangTokenId.Nl && previousToken != null) {
            if (previousToken.id == beginToken) {
                insideString = true
            } else if (previousToken.id == ErlangTokenId.Error) {
                if (ts.movePrevious) {
                    if (ts.token.id == beginToken) {
                        insideString = true
                    }
                }
            }
        }

        if (!insideString) {
            // check if the caret is at the very end of the line and there
            // is an unterminated string literal
            if (token.id == ErlangTokenId.Ws && eol) {
                if ((dotPos - 1) > 0) {
                    token = LexUtil.token(doc, dotPos - 1).get
                    // XXX TODO use language embedding to handle this
                    insideString = (token.id == ErlangTokenId.StringLiteral)
                }
            }
        }

        if (insideString) {
            if (eol) {
                return false // do not complete
            } else {
                //#69524
                val chr = doc.getChars(dotPos, 1)(0)
                if (chr == bracket) {
                    if (!isAfter) {
                        doc.insertString(dotPos, "" + bracket, null) //NOI18N
                    } else {
                        if (!(dotPos < doc.getLength - 1 && doc.getText(dotPos + 1, 1).charAt(0) == bracket)) {
                            return true
                        }
                    }

                    doc.remove(dotPos, 1)

                    return true
                }
            }
        }

        if (completablePosition && !insideString || eol) {
            doc.insertString(dotPos, "" + bracket + (if (isAfter) "" else matching(bracket)), null) //NOI18N

            return true
        }

        return false
    }

    /**
     * Checks whether dotPos is a position at which bracket and quote
     * completion is performed. Brackets and quotes are not completed
     * everywhere but just at suitable places .
     * @param doc the document
     * @param dotPos position to be tested
     */
    @throws(classOf[BadLocationException])
    private def isCompletablePosition(doc:BaseDocument, dotPos:Int) :Boolean = {
        if (dotPos == doc.getLength) { // there's no other character to test
            true
        } else {
            // test that we are in front of ) , " or '
            doc.getChars(dotPos, 1)(0) match {
                case ')' | ',' | '\"' | '\'' | ' ' | ']' | '}' | '\n' | '\t' | ';' => true
                case _ => false
            }
        }
    }

    @throws(classOf[BadLocationException])
    private def isQuoteCompletablePosition(doc:BaseDocument, dotPos:Int) :Boolean = {
        if (dotPos == doc.getLength) { // there's no other character to test
            return true
        } else {
            // test that we are in front of ) , " or ' ... etc.
            val eol = Utilities.getRowEnd(doc, dotPos)

            if ((dotPos == eol) || (eol == -1)) {
                return false
            }

            val firstNonWhiteFwd = Utilities.getFirstNonWhiteFwd(doc, dotPos, eol)

            if (firstNonWhiteFwd == -1) {
                return false
            }

            doc.getChars(firstNonWhiteFwd, 1)(0) match {
                case ')' | ',' | '+' | '}' | ';' | ']' | '/' => true
                case _ => false
            }

            //            if (chr == '%' && RubyUtils.isRhtmlDocument(doc)) {
            //                return true
            //            }
        }
    }

    /**
     * Returns for an opening bracket or quote the appropriate closing
     * character.
     */
    private def matching(pair:Char) :Char = pair match {
        case '(' => ')'
        case '/' => '/'
        case '[' => ']'
        case '{' => '}'
        case '}' => '{'
        case '\'' => '\''
        case '\"' => '\"' // NOI18N
        case _ => pair
    }

    override
    def findLogicalRanges(pResult:ParserResult, caretOffset:Int) :List[OffsetRange] = {
        val root = pResult.asInstanceOf[ErlangParserResult].rootScope match {
            case None => return Collections.emptyList[OffsetRange]
            case Some(x) => x
        }

        val astOffset = LexUtil.astOffset(pResult, caretOffset)
        if (astOffset == -1) {
            return Collections.emptyList[OffsetRange]
        }

        val ranges = new ArrayList[OffsetRange]

        /** Furthest we can go back in the buffer (in RHTML documents, this
         * may be limited to the surrounding &lt;% starting tag
         */
        var min = 0
        var max = Integer.MAX_VALUE
        var length = 0

        // Check if the caret is within a comment, and if so insert a new
        // leaf "node" which contains the comment line and then comment block
        try {
            val doc = LexUtil.document(pResult, true) match {
                case None => return Collections.emptyList[OffsetRange]
                case Some(x) => x
            }

            length = doc.getLength

            //            if (RubyUtils.isRhtmlDocument(doc)) {
            //                TokenHierarchy th = TokenHierarchy.get(doc);
            //                TokenSequence ts = th.tokenSequence;
            //                ts.move(caretOffset);
            //                if (ts.moveNext || ts.movePrevious) {
            //                    Token t = ts.token;
            //                    if (t.id.primaryCategory.startsWith("ruby")) { // NOI18N
            //                        min = ts.offset;
            //                        max = min+t.length;
            //                        // Try to extend with delimiters too
            //                        if (ts.movePrevious) {
            //                            t = ts.token;
            //                            if ("ruby-delimiter".equals(t.id.primaryCategory)) { // NOI18N
            //                                min = ts.offset;
            //                                if (ts.moveNext && ts.moveNext) {
            //                                    t = ts.token;
            //                                    if ("ruby-delimiter".equals(t.id.primaryCategory)) { // NOI18N
            //                                        max = ts.offset+t.length;
            //                                    }
            //                                }
            //                            }
            //                        }
            //                    }
            //                }
            //            }


            for (ts <- LexUtil.positionedSequence(doc, caretOffset)) {
                val token = ts.token
                if (token != null && token.id == ErlangTokenId.LineComment) {
                    // First add a range for the current line
                    var begin = Utilities.getRowStart(doc, caretOffset)
                    var end = Utilities.getRowEnd(doc, caretOffset)

                    if (LexUtil.isCommentOnlyLine(doc, caretOffset)) {
                        ranges.add(new OffsetRange(Utilities.getRowFirstNonWhite(doc, begin),
                                                   Utilities.getRowLastNonWhite(doc, end) + 1))

                        var lineBegin = begin
                        var lineEnd = end

                        def loop1 :Unit = if (begin > 0) {
                            val newBegin = Utilities.getRowStart(doc, begin - 1)
                            if (newBegin < 0 || !LexUtil.isCommentOnlyLine(doc, newBegin)) {
                                begin = Utilities.getRowFirstNonWhite(doc, begin)
                            } else {
                                begin = newBegin
                                loop1
                            }
                        }
                        loop1

                        def loop2 :Unit = {
                            val newEnd = Utilities.getRowEnd(doc, end + 1)
                            if ((newEnd >= length) || !LexUtil.isCommentOnlyLine(doc, newEnd)) {
                                end = Utilities.getRowLastNonWhite(doc, end) + 1
                            } else {
                                end = newEnd
                                loop2
                            }
                        }
                        loop2

                        if ((lineBegin > begin) || (lineEnd < end)) {
                            ranges.add(new OffsetRange(begin, end))
                        }
                    } else {
                        // It's just a line comment next to some code; select the comment
                        val th = TokenHierarchy.get(doc)
                        val offset = token.offset(th)
                        ranges.add(new OffsetRange(offset, offset + token.length))
                    }
                }
            }
        } catch {
            case ex:BadLocationException =>
                Exceptions.printStackTrace(ex)
                return ranges
        }
        return ranges
    }

    override
    def getNextWordOffset(document:Document, offset:Int, reverse:Boolean) :Int = {
        val doc = document.asInstanceOf[BaseDocument]
        val ts = LexUtil.tokenSequence(doc, offset) match {
            case None => return -1
            case Some(x) => x
        }

        ts.move(offset)
        if (!ts.moveNext && !ts.movePrevious) {
            return -1
        }

        if (reverse && ts.offset == offset) {
            if (!ts.movePrevious) {
                return -1
            }
        }

        var token = ts.token
        var id = token.id
        if (id == ErlangTokenId.Ws) {
            // Just eat up the space in the normal IDE way
            if (reverse && ts.offset < offset || !reverse && ts.offset > offset) {
                return ts.offset
            }
            while (id == ErlangTokenId.Ws) {
                if (reverse && !ts.movePrevious) {
                    return -1
                } else if (!reverse && !ts.moveNext) {
                    return -1
                }

                token = ts.token
                id = token.id
            }
            if (reverse) {
                val start = ts.offset + token.length
                if (start < offset) {
                    return start
                }
            } else {
                val start = ts.offset
                if (start > offset) {
                    return start
                }
            }
        }

        id match {
            case ErlangTokenId.Var | ErlangTokenId.Atom | ErlangTokenId.Rec =>
                val s = token.text.toString
                val length = s.length
                val wordOffset = offset - ts.offset
                if (reverse) {
                    // Find previous
                    val offsetInImage = offset - 1 - ts.offset
                    if (offsetInImage < 0) {
                        return -1
                    }
                    if (offsetInImage < length && Character.isUpperCase(s.charAt(offsetInImage))) {
                        var i = offsetInImage - 1
                        while (i >= 0) {
                            val charAtI = s.charAt(i)
                            if (charAtI == '_') {
                                // return offset of previous uppercase char in the identifier
                                return ts.offset + i + 1
                            } else if (!Character.isUpperCase(charAtI)) {
                                // return offset of previous uppercase char in the identifier
                                return ts.offset + i + 1
                            }
                            i -= 1
                        }
                        return ts.offset
                    } else {
                        var i = offsetInImage - 1
                        while (i >= 0) {
                            val charAtI = s.charAt(i)
                            if (charAtI == '_') {
                                return ts.offset + i + 1
                            }
                            if (Character.isUpperCase(charAtI)) {
                                // now skip over previous uppercase chars in the identifier
                                var j = i
                                while (j >= 0) {
                                    val charAtJ = s.charAt(j)
                                    if (charAtJ == '_') {
                                        return ts.offset + j + 1
                                    }
                                    if (!Character.isUpperCase(charAtJ)) {
                                        // return offset of previous uppercase char in the identifier
                                        return ts.offset + j + 1
                                    }
                                    j -= 1
                                }
                                return ts.offset
                            }
                            i -= 1
                        }

                        return ts.offset
                    }
                } else {
                    // Find next
                    var start = wordOffset + 1
                    if (wordOffset < 0 || wordOffset >= s.length) {
                        // Probably the end of a token sequence, such as this:
                        // <%s|%>
                        return -1
                    }
                    if (Character.isUpperCase(s.charAt(wordOffset))) {
                        // if starting from a Uppercase char, first skip over follwing upper case chars
                        for (i <- start until length;
                             charAtI = s.charAt(i) if !Character.isUpperCase(charAtI)) {
                            if (charAtI == '_') {
                                return ts.offset + i
                            }
                            start += 1
                        }
                    }
                    for (i <- start until length;
                         charAtI = s.charAt(i)) {
                        if (charAtI == '_' || Character.isUpperCase(charAtI)) {
                            return ts.offset + i
                        }
                    }
                }
            case _ =>
        }

        // Default handling in the IDE
        return -1
    }
}
