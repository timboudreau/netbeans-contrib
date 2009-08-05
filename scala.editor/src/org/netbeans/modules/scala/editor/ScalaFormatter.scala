/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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

package org.netbeans.modules.scala.editor

//import org.netbeans.modules.scala.editing.options.CodeStyle;
import javax.swing.text.{BadLocationException, Document}
import org.netbeans.api.lexer.{Token, TokenId, TokenSequence}
import org.netbeans.editor.{BaseDocument, Utilities}
import org.netbeans.modules.csl.api.Formatter
import org.netbeans.modules.csl.spi.{GsfUtilities, ParserResult}
import org.netbeans.modules.editor.indent.spi.Context
import org.netbeans.modules.scala.editor.lexer.{ScalaLexUtil, ScalaTokenId}
import org.openide.filesystems.FileUtil
import org.openide.loaders.DataObject
import org.openide.util.Exceptions

import scala.collection.mutable.{ArrayBuffer, Stack}

/**
 * Formatting and indentation.
 *
 *
 * @author Caoyuan Deng
 */
object ScalaFormatter {
  val BRACE_MATCH_MAP: Map[TokenId, Set[TokenId]] = Map(ScalaTokenId.LParen            -> Set(ScalaTokenId.RParen),
                                                        ScalaTokenId.LBrace            -> Set(ScalaTokenId.RBrace),
                                                        ScalaTokenId.LBracket          -> Set(ScalaTokenId.RBracket),
                                                        ScalaTokenId.Case              -> Set(ScalaTokenId.Case,
                                                                                              ScalaTokenId.RBrace),
                                                        ScalaTokenId.DocCommentStart   -> Set(ScalaTokenId.DocCommentEnd),
                                                        ScalaTokenId.BlockCommentStart -> Set(ScalaTokenId.BlockCommentEnd),
                                                        ScalaTokenId.XmlLt             -> Set(ScalaTokenId.XmlSlashGt,
                                                                                              ScalaTokenId.XmlLtSlash)
  )


}

class ScalaFormatter(/* acodeStyle: CodeStyle ,*/ rightMarginOverride: Int) extends Formatter {
  import ScalaFormatter._

  def this() = this(-1)
  
  //private var codeStyle = if (acodeStyle != null) acodeStyle else CodeStyle.getDefault(null)

  def needsParserResult: Boolean = {
    false
  }

  override def reindent(context: Context): Unit = {
    reindent(context, context.document, context.startOffset, context.endOffset, null, true);
  }

  override def reformat(context: Context, info: ParserResult): Unit =  {
    reindent(context, context.document, context.startOffset, context.endOffset, info, false);
  }

  def indentSize: Int = {
    2 //codeStyle.getIndentSize
  }

  def hangingIndentSize: Int = {
    0 // codeStyle.getContinuationIndentSize
  }

  /** Compute the initial balance of brackets at the given offset. */
  private def getFormatStableStart(doc: BaseDocument, offset: Int): Int = {
    val ts = ScalaLexUtil.getTokenSequence(doc, offset) match {
      case null => return 0
      case x => x
    }

    ts.move(offset)
    if (!ts.movePrevious) {
      return 0
    }

    // Look backwards to find a suitable context - a class, module or method definition
    // which we will assume is properly indented and balanced
    do {
      val token = ts.token
      token.id match {
        case ScalaTokenId.Object | ScalaTokenId.Trait | ScalaTokenId.Class => return ts.offset
        case _ =>
      }
    } while (ts.movePrevious)

    ts.offset
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
  @throws(classOf[BadLocationException])
  private def getFirstTokenOnLine(doc: BaseDocument, offset: Int): Token[_] = {
    val lineBegin = Utilities.getRowFirstNonWhite(doc, offset)
    if (lineBegin != -1) {
      return ScalaLexUtil.getToken(doc, lineBegin)
    }

    null
  }

  def reindent(context: Context, document:Document, astartOffset: Int, aendOffset: Int, info: ParserResult, indentOnly: Boolean): Unit = {
    var startOffset = astartOffset
    var endOffset = aendOffset
    try {
      val doc = document.asInstanceOf[BaseDocument] // document.getText(0, document.getLength())
      //syncOptions(doc, codeStyle)

      if (endOffset > doc.getLength) {
        endOffset = doc.getLength
      }

      startOffset = Utilities.getRowStart(doc, startOffset);
      val lineStart = startOffset //Utilities.getRowStart(doc, startOffset);

      var initialOffset = 0
      var initialIndent = 0
      if (startOffset > 0) {
        val prevOffset = Utilities.getRowStart(doc, startOffset - 1)
        initialOffset = getFormatStableStart(doc, prevOffset)
        initialIndent = GsfUtilities.getLineIndent(doc, initialOffset)
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

      // In case of indentOnly (use press <enter>), the endOffset will be the
      // position of newline inserted, to compute the new added line's indent,
      // we need includeEnd.
      val includeEnd = endOffset == doc.getLength || indentOnly

      // TODO - remove initialbalance etc.
      computeIndents(doc, initialIndent, initialOffset, endOffset, info, offsets, indents, indentEmptyLines, includeEnd);

      doc.runAtomic(new Runnable {
          def run {
            try {

              // Iterate in reverse order such that offsets are not affected by our edits
              assert(indents.size == offsets.size)
              val editorFormatter = doc.getFormatter
              var break = false
              var i = indents.size - 1
              while (i >= 0 && !break) {
                val lineBegin = offsets(i)
                if (lineBegin < lineStart) {
                  // We're now outside the region that the user wanted reformatting;
                  // these offsets were computed to get the correct continuation context etc.
                  // for the formatter
                  break = true
                } else {
                  var indent = indents(i)
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
                    val actualPrevIndent = GsfUtilities.getLineIndent(doc, prevOffset)
                    if (actualPrevIndent != prevIndent) {
                      // For blank lines, indentation may be 0, so don't adjust in that case
                      if (!(Utilities.isRowEmpty(doc, prevOffset) || Utilities.isRowWhite(doc, prevOffset))) {
                        indent = actualPrevIndent + (indent - prevIndent);
                      }
                    }
                  }

                  if (indent >= 0) { // @todo why? #150319
                    // Adjust the indent at the given line (specified by offset) to the given indent
                    val currentIndent = GsfUtilities.getLineIndent(doc, lineBegin)

                    if (currentIndent != indent) {
                      if (context != null) {
                        context.modifyIndent(lineBegin, indent)
                      } else {
                        editorFormatter.changeRowIndent(doc, lineBegin, indent)
                      }
                    }
                  }
                }
                
                i -= 1
              }

              if (!indentOnly /* && codeStyle.reformatComments */) {
                //                    reformatComments(doc, startOffset, endOffset);
              }
            } catch {case ble: BadLocationException => Exceptions.printStackTrace(ble)}
          }
        })
    } catch {case ble: BadLocationException => Exceptions.printStackTrace(ble)}
  }

  def computeIndents(doc: BaseDocument, initialIndent: Int, startOffset: Int, endOffset: Int, info: ParserResult, offsets: ArrayBuffer[Int], indents: ArrayBuffer[Int], indentEmptyLines: Boolean, includeEnd: Boolean): Unit = {
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

      val end = endOffset

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

      val openingBraces = new Stack[Brace]

      while ((!includeEnd && offset < end) || (includeEnd && offset <= end)) {
        val lineBegin = Utilities.getRowFirstNonWhite(doc, offset)
        val lineEnd = Utilities.getRowEnd(doc, offset)

        if (lineBegin != -1) {
          val results = computeLineIndent(indent, prevIndent, continueIndent,
                                          openingBraces, doc, lineBegin, lineEnd);

          indent = results(0)
          nextIndent = results(1)
          continueIndent = results(2)
        }

        if (indent == -1) {
          // Skip this line - leave formatting as it is prior to reformatting
          indent = GsfUtilities.getLineIndent(doc, offset)
        }

        if (indent < 0) {
          indent = 0
        }

        // Insert whitespace on empty lines too -- needed for abbreviations expansion
        if (lineBegin != -1 || indentEmptyLines) {
          indents + indent
          offsets + offset
        }

        // Shift to next line
        offset = lineEnd + 1
        prevIndent = indent
        indent = nextIndent
      }
    } catch {case ble: BadLocationException => Exceptions.printStackTrace(ble)}
  }

  private class Brace {

    var token: Token[TokenId] = _
    var offsetOnline: int = _ // offset of this token on its line after indent
    var ordinalOnline: int = _ // ordinal of this token on its line (we only count non-white tokens)
    var isLatestOnLine: Boolean = _ // last one on this line?
    var onProcessingLine: boolean = _ // on the processing line?
    var lasestTokenOnLine: Token[TokenId] = _ // lastest non-white token on this line

    override def toString = {
      token.text.toString
    }
  }

  /**
   * Compute indent for next line, and adjust this line's indent if necessary
   * @return int[]
   *      int[0] - adjusted indent of this line
   *      int[1] - indent for next line
   */
  private def computeLineIndent(aindent: Int, prevIndent: Int, acontinueIndent: Int,
                                openBraces: Stack[Brace], doc: BaseDocument, lineBegin: Int, lineEnd: Int): Array[Int] = {

    // Well, a new line begin
    openBraces foreach {_.onProcessingLine = false}

    //StringBuilder sb = new StringBuilder(); // for debug
    // Compute new balance and adjust indent of this line

    var indent = aindent
    var continueIndent = acontinueIndent
    // token index on this line (we only count not-white tokens,
    // if notWhiteIdx == 0, means the first non-white token on this line
    var notWSIdx = -1
    var latestNotWSToken: Token[TokenId] = null

    val ts = ScalaLexUtil.getTokenSequence(doc, lineBegin)
    if (ts != null) {
      try {
        ts.move(lineBegin)
        do {
          val token = ts.token
          if (token != null) {

            val offset = ts.offset
            val id = token.id

            //sb.append(text); // for debug

            if (!ScalaLexUtil.isWsComment(id)) {
              notWSIdx += 1
              latestNotWSToken = token
            }

            // match/add brace
            if (id.primaryCategory.equals("keyword") ||
                id.primaryCategory.equals("separator") ||
                id.primaryCategory.equals("operator") ||
                id.primaryCategory.equals("xml") ||
                id.primaryCategory.equals("comment")) {

              var justClosedBrace: Brace = null

              if (!openBraces.isEmpty) {
                val brace = openBraces.top
                val braceId = brace.token.id

                val matchingIds = BRACE_MATCH_MAP.get(braceId)
                assert(matchingIds.isDefined)
                if (matchingIds.get.contains(id)) { // matched

                  var numClosed = 1 // default

                  // we may need to lookahead 2 steps for some cases:
                  if (braceId == ScalaTokenId.Case) {
                    val backup = openBraces.pop

                    if (!openBraces.isEmpty) {
                      //TokenId lookaheadId = openingBraces.peek().token.id();
                      // if resolved is "=>", we may have matched two braces:
                      if (id == ScalaTokenId.RBrace) {
                        numClosed = 2
                      }
                    }

                    openBraces.push(backup)
                  }

                  for (i <- 0 until numClosed) {
                    justClosedBrace = openBraces.pop
                  }

                  if (notWSIdx == 0) {
                    // At the beginning of this line, adjust this line's indent if necessary
                    indent = id match {
                      case ScalaTokenId.Case | ScalaTokenId.RParen | ScalaTokenId.RBracket | ScalaTokenId.RBrace =>
                        openBraces.size * indentSize
                      case _ =>
                        justClosedBrace.offsetOnline
                    }
                  }

                }
              }

              // Add new opening brace
              if (BRACE_MATCH_MAP.contains(id)) {
                var ignore = false
                // is it a case object or class?, if so, do not indent
                if (id == ScalaTokenId.Case) {
                  if (ts.moveNext) {
                    val next = ScalaLexUtil.findNextNonWs(ts)
                    next.id match {
                      case ScalaTokenId.Object | ScalaTokenId.Class => ignore = true
                      case _ =>
                    }
                    ts.movePrevious
                  }
                }

                if (!ignore) {
                  val newBrace = new Brace
                  newBrace.token = token
                  // will add indent of this line to offsetOnline later
                  newBrace.offsetOnline = offset - lineBegin
                  newBrace.ordinalOnline = notWSIdx
                  newBrace.onProcessingLine = true
                  openBraces push newBrace
                }
              }
            } else if (id == ScalaTokenId.XmlCDData || (id == ScalaTokenId.StringLiteral && offset < lineBegin)) {
              /**
               * A literal string with more than one line is a whole token and when goes
               * to second or following lines, will has offset < lineBegin
               */
              if (notWSIdx == 0 || notWSIdx == -1) {
                // No indentation for literal strings from 2nd line.
                indent = -1
              }
            }
          }
        } while (ts.moveNext && ts.offset < lineEnd)
      } catch {case e: AssertionError =>
          doc.getProperty(Document.StreamDescriptionProperty).asInstanceOf[DataObject] match {
            case null =>
            case dobj =>  Exceptions.attachMessage(e, FileUtil.getFileDisplayName(dobj.getPrimaryFile))
          }
        
          throw e
      }
    }

    // Now we've got the final indent of this line, adjust offset for new added
    // braces (which should be on this line)
    for (brace <- openBraces) {
      if (brace.onProcessingLine) {
        brace.offsetOnline += indent
        if (brace.ordinalOnline == notWSIdx) {
          brace.isLatestOnLine = true
        }
        brace.lasestTokenOnLine = latestNotWSToken
      }
    }

    // Compute indent for next line
    var nextIndent = 0
    val latestOpenBrace = if (openBraces.size > 0) openBraces.toList(openBraces.size - 1) else null
    val latestOpenId = if (latestOpenBrace != null) latestOpenBrace.token.id else null

    // decide if next line is new or continued continute line
    var isContinueLine = false
    if (latestNotWSToken == null) {
      // empty line or comment line
      isContinueLine = false
    } else {
      isContinueLine = false // default

      val id = latestNotWSToken.id

      if (id == ScalaTokenId.Comma) {
        //we have special case
        if (latestOpenBrace != null && latestOpenBrace.isLatestOnLine && (latestOpenId == ScalaTokenId.LParen ||
                                                                          latestOpenId == ScalaTokenId.LBracket ||
                                                                          latestOpenId == ScalaTokenId.LBrace)) {

          isContinueLine = true
        }
      }
    }

    if (isContinueLine) {
      // Compute or reset continue indent
      if (continueIndent == -1) {
        // new continue indent
        continueIndent = indent + hangingIndentSize
      } else {
        // keep the same continue indent
      }

      // Continue line
      nextIndent = continueIndent
    } else {
      // Reset continueIndent
      continueIndent = -1

      if (latestOpenBrace == null) {
        // All braces resolved
        nextIndent = 0
      } else {
        val offset = latestOpenBrace.offsetOnline;
        if (latestOpenId == ScalaTokenId.RArrow) {
          var nearestHangableBrace: Brace = null
          var depth1 = 0
          var break = false
          val openBraces1 = openBraces.toList
          for (i <- openBraces.size - 1 to 0 if !break) {
            val brace = openBraces1(i)
            depth1 += 1
            if (brace.token.id != ScalaTokenId.RArrow) {
              nearestHangableBrace = brace
              break = true
            }
          }

          if (nearestHangableBrace != null) {
            // Hang it from this brace
            nextIndent = nearestHangableBrace.offsetOnline + depth1 * indentSize
          } else {
            nextIndent = openBraces.size * indentSize
          }
        } else if ((latestOpenId == ScalaTokenId.LParen ||
                    latestOpenId == ScalaTokenId.LBracket ||
                    latestOpenId == ScalaTokenId.LBrace) &&
                   !latestOpenBrace.isLatestOnLine &&
                   (latestOpenBrace.lasestTokenOnLine == null || latestOpenBrace.lasestTokenOnLine.id != ScalaTokenId.RArrow)) {

          nextIndent = offset + latestOpenBrace.token.text.toString.length;

        } else if (latestOpenId == ScalaTokenId.BlockCommentStart ||
                   latestOpenId == ScalaTokenId.DocCommentStart) {

          nextIndent = offset + 1;

        } else {
          // default
          nextIndent = openBraces.size * indentSize
        }
      }
    }

    Array(indent, nextIndent, continueIndent)
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
  //  private def syncOptions(doc: BaseDocument, style: CodeStyle) {
  //    val formatter = doc.getFormatter
  //    if (formatter.getSpacesPerTab != style.getIndentSize) {
  //      formatter.setSpacesPerTab(style.getIndentSize)
  //    }
  //  }
}
