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
package org.netbeans.modules.scala.editor.lexer

import javax.swing.text.{BadLocationException,Document}
import org.netbeans.api.lexer.{Token,
                               TokenId,
                               TokenHierarchy,
                               TokenSequence}
import org.netbeans.modules.csl.api.OffsetRange
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.parsing.api.Snapshot
import org.netbeans.modules.parsing.spi.Parser.Result
import org.netbeans.editor.{BaseDocument,Utilities}
//import org.netbeans.modules.scala.editor.lexer.ScalaTokenId._
import org.openide.filesystems.{FileUtil,FileObject}
import org.openide.cookies.EditorCookie
import org.openide.loaders.DataObject
import org.openide.util.Exceptions
import _root_.scala.collection.mutable.Stack

/**
 * Special functions for ScalaTokenId
 */
trait LanguageLexUtil {
  protected val language = ScalaTokenId.language

  protected val LPAREN = ScalaTokenId.LParen
  protected val RPAREN = ScalaTokenId.RParen
  protected val LINE_COMMENT = ScalaTokenId.LineComment

  protected val Ws = ScalaTokenId.Ws

  protected val WS = Set(ScalaTokenId.Ws,
                         ScalaTokenId.Nl
  )

  protected val WS_COMMENT = Set(ScalaTokenId.Ws,
                                 ScalaTokenId.Nl,
                                 ScalaTokenId.LineComment,
                                 ScalaTokenId.DocCommentStart,
                                 ScalaTokenId.DocCommentData,
                                 ScalaTokenId.DocCommentEnd,
                                 ScalaTokenId.BlockCommentStart,
                                 ScalaTokenId.DocCommentData,
                                 ScalaTokenId.BlockCommentEnd,
                                 ScalaTokenId.CommentTag
  )


  def isWs(id:TokenId) = {id == ScalaTokenId.Ws}
  def isNl(id:TokenId) = {id == ScalaTokenId.Nl}

  def isComment(id:TokenId) :Boolean = id match {
    case ScalaTokenId.LineComment => true
    case ScalaTokenId.DocCommentStart => true
    case ScalaTokenId.DocCommentData => true
    case ScalaTokenId.DocCommentEnd => true
    case ScalaTokenId.BlockCommentStart => true
    case ScalaTokenId.DocCommentData => true
    case ScalaTokenId.BlockCommentEnd => true
    case ScalaTokenId.CommentTag  => true
    case _ => false
  }

  def isLineComment(id:TokenId) :Boolean = id match {
    case ScalaTokenId.LineComment => true
    case _ => false
  }


}

object LexUtil1 extends LanguageLexUtil {

  def fileObject(pResult:ParserResult) :Option[FileObject] = pResult match {
    case null => None
    case _ => pResult.getSnapshot.getSource.getFileObject match {
        case null => None
        case fo => Some(fo)
      }
  }

  def fileObject(pResult:Result) :Option[FileObject] = fileObject(pResult.asInstanceOf[ParserResult])

  def document(pResult:ParserResult, forceOpen:Boolean) :Option[BaseDocument] = pResult match {
    case null => None
    case _ => document(pResult.getSnapshot, forceOpen)
  }

  def document(snapshot:Snapshot, forceOpen:Boolean) :Option[BaseDocument] = snapshot match {
    case null => None
    case _ => snapshot.getSource.getDocument(forceOpen) match {
        case doc:BaseDocument => Some(doc)
        case _ => None
      }
  }

  def document(fo:FileObject, forceOpen:Boolean) :Option[BaseDocument] = {
    try {
      DataObject.find(fo) match {
        case null => None
        case dobj => dobj.getCookie(classOf[EditorCookie]) match {
            case null => None
            case ec => (if (forceOpen) ec.openDocument else ec.getDocument) match {
                case x:BaseDocument => Some(x)
                case _ => None
              }
          }
      }
    } catch {case ex:Exception => None}
  }

  def tokenHierarchy(snapshot:Snapshot) :Option[TokenHierarchy[_]] = snapshot.getTokenHierarchy match {
    case null => None
    case th => Some(th)
  }

  def tokenHierarchy(pResult:ParserResult) :Option[TokenHierarchy[_]] = pResult match {
    case null => None
    case _ => tokenHierarchy(pResult.getSnapshot)
  }

  def tokenSequence(doc:BaseDocument, offset:Int) :Option[TokenSequence[TokenId]] = {
    val th = TokenHierarchy.get(doc)
    tokenSequence(th, offset)
  }

  def tokenSequence(th:TokenHierarchy[_], offset:Int) :Option[TokenSequence[TokenId]] = th.tokenSequence(language) match {
    case null =>
      // * Possibly an embedding scenario such as an RHTML file
      def find(itr:_root_.java.util.Iterator[TokenSequence[TokenId]]) :Option[TokenSequence[TokenId]] = itr.hasNext match {
        case true => itr.next match {
            case ts if ts.language == language => Some(ts)
            case _ => find(itr)
          }
        case false => None
      }
         
      // * First try with backward bias true
      val itr1 = th.embeddedTokenSequences(offset, true).iterator.asInstanceOf[_root_.java.util.Iterator[TokenSequence[TokenId]]]
      find(itr1) match {
        case None =>
          val itr2 = th.embeddedTokenSequences(offset, false).iterator.asInstanceOf[_root_.java.util.Iterator[TokenSequence[TokenId]]]
          find(itr2)
        case x => x
      }
    case ts => Some(ts)
  }

  def rangeOfToken(th:TokenHierarchy[_], token:Token[TokenId]) :OffsetRange = {
    val offset = token.offset(th)
    val endOffset = offset + token.length
    if (offset >= 0 && endOffset >= offset) {
      new OffsetRange(offset, offset + token.length)
    } else OffsetRange.NONE
  }

  /** For a possibly generated offset in an AST, return the corresponding lexing/true document offset */
  def lexerOffset(pResult:ParserResult, astOffset:Int) :Int = {
    pResult.getSnapshot.getOriginalOffset(astOffset)
  }

  def lexerOffsets(pResult:ParserResult, astRange:OffsetRange) :OffsetRange = {
    // * there has been  astRange, we can assume pResult not null
    val rangeStart = astRange.getStart
    pResult.getSnapshot.getOriginalOffset(rangeStart) match {
      case -1 => OffsetRange.NONE
      case `rangeStart` => astRange
      case start =>
        // Assumes the translated range maintains size
        new OffsetRange(start, start + astRange.getLength)
    }
  }

  def astOffset(pResult:ParserResult, lexOffset:Int) :Int = pResult match {
    case null => lexOffset
    case _ => pResult.getSnapshot.getEmbeddedOffset(lexOffset)
  }

  def astOffsets(pResult:ParserResult, lexRange:OffsetRange) :OffsetRange = pResult match {
    case null => lexRange
    case _ =>
      val rangeStart = lexRange.getStart
      pResult.getSnapshot.getEmbeddedOffset(rangeStart) match {
        case -1 => OffsetRange.NONE
        case `rangeStart` => lexRange
        case start =>
          // Assumes the translated range maintains size
          new OffsetRange(start, start + lexRange.getLength())
      }
  }

  def positionedSequence(doc:BaseDocument, offset:Int) :Option[TokenSequence[TokenId]] = {
    positionedSequence(doc, offset, true)
  }

  def positionedSequence(doc:BaseDocument, offset:Int, lookBack:Boolean) :Option[TokenSequence[TokenId]] = {
    for (ts <- tokenSequence(doc, offset)) {
      try {
        ts.move(offset)
      } catch {
        case e:AssertionError =>
          doc.getProperty(Document.StreamDescriptionProperty) match {
            case null =>
            case dobj:DataObject => Exceptions.attachMessage(e, FileUtil.getFileDisplayName(dobj.getPrimaryFile))
          }
          throw e
      }

      if (!lookBack && !ts.moveNext) {
        return None
      } else if (lookBack && !ts.moveNext && !ts.movePrevious) {
        return None
      }

      return Some(ts)
    }

    return None
  }

  def token(doc:BaseDocument, offset:Int) :Option[Token[TokenId]] = {
    for (ts <- positionedSequence(doc, offset)) {
      return Some(ts.token)
    }

    return None
  }

  def tokenChar(doc:BaseDocument, offset:Int) :Char = token(doc, offset) match {
    case None => 0
    case Some(x) =>
      val text = x.text.toString
            
      if (text.length > 0) { // Usually true, but I could have gotten EOF right?
        text.charAt(0)
      } else 0
  }
    

  def findNextNonWsNonComment(ts:TokenSequence[TokenId]) :Token[TokenId] = {
    findNext(ts, WS_COMMENT.asInstanceOf[Set[TokenId]])
  }

  def findPreviousNonWsNonComment(ts:TokenSequence[TokenId]) :Token[TokenId] = {
    findPrevious(ts, WS_COMMENT.asInstanceOf[Set[TokenId]])
  }

  def findNextNonWs(ts:TokenSequence[TokenId]) :Token[TokenId] = {
    findNext(ts, WS.asInstanceOf[Set[TokenId]])
  }

  def findPreviousNonWs(ts:TokenSequence[TokenId]) :Token[TokenId] = {
    findPrevious(ts, WS.asInstanceOf[Set[TokenId]])
  }

  def findNext(ts:TokenSequence[TokenId], ignores:Set[TokenId]) :Token[TokenId] = {
    if (ignores.contains(ts.token.id)) {
      while (ts.moveNext && ignores.contains(ts.token.id)) {}
    }
    ts.token
  }

  def findPrevious(ts:TokenSequence[TokenId], ignores:Set[TokenId]) :Token[TokenId] = {
    if (ignores.contains(ts.token.id)) {
      while (ts.movePrevious && ignores.contains(ts.token.id)) {}
    }
    ts.token
  }

  def findNext(ts:TokenSequence[TokenId], id:TokenId) :Token[TokenId] = {
    if (ts.token.id != id) {
      while (ts.moveNext && ts.token.id != id) {}
    }
    ts.token
  }

  def findNextIn(ts:TokenSequence[TokenId], includes:Set[TokenId] ) :Token[TokenId] = {
    if (!includes.contains(ts.token.id)) {
      while (ts.moveNext && !includes.contains(ts.token.id)) {}
    }
    ts.token
  }

  def findPrev(ts:TokenSequence[TokenId], id:TokenId) :Token[TokenId] = {
    if (ts.token.id != id) {
      while (ts.movePrevious && ts.token.id != id) {}
    }
    ts.token
  }

  def findNextIncluding(ts:TokenSequence[TokenId], includes:Set[TokenId] ) :Token[TokenId] = {
    while (ts.moveNext && !includes.contains(ts.token.id)) {}
    ts.token
  }

  def findPrevIncluding(ts:TokenSequence[TokenId], includes:Set[TokenId]) :Token[TokenId] = {
    if (!includes.contains(ts.token.id)) {
      while (ts.movePrevious && !includes.contains(ts.token.id)) {}
    }
    ts.token
  }

  /**
   * Back up to the first space character prior to the given offset - as long as
   * it's on the same line!  If there's only leading whitespace on the line up
   * to the lex offset, return the offset itself
   * @todo Rewrite this now that I have a separate newline token, EOL, that I can
   *   break on - no need to call Utilities.getRowStart.
   */
  def findSpaceBegin(doc:BaseDocument, lexOffset:Int) :Int = {
    val ts = tokenSequence(doc, lexOffset) match {
      case None => return lexOffset
      case Some(x) => x
    }
    var allowPrevLine = false;
    var lineStart = 0
    try {
      lineStart = Utilities.getRowStart(doc, Math.min(lexOffset, doc.getLength))
      var prevLast = lineStart - 1;
      if (lineStart > 0) {
        prevLast = Utilities.getRowLastNonWhite(doc, lineStart - 1)
        if (prevLast != -1) {
          val c = doc.getText(prevLast, 1).charAt(0)
          if (c == ',') {
            // Arglist continuation? // TODO : check lexing
            allowPrevLine = true
          }
        }
      }
      if (!allowPrevLine) {
        val firstNonWhite = Utilities.getRowFirstNonWhite(doc, lineStart)
        if (lexOffset <= firstNonWhite || firstNonWhite == -1) {
          return lexOffset
        }
      } else {
        // Make lineStart so small that Math.max won't cause any problems
        val firstNonWhite = Utilities.getRowFirstNonWhite(doc, lineStart)
        if (prevLast >= 0 && (lexOffset <= firstNonWhite || firstNonWhite == -1)) {
          return prevLast + 1
        }
        lineStart = 0
      }
    } catch {case ble:BadLocationException => Exceptions.printStackTrace(ble); return lexOffset}

    ts.move(lexOffset)
    if (ts.moveNext) {
      if (lexOffset > ts.offset) {
        // We're in the middle of a token
        return Math.max(if (ts.token.id == Ws) ts.offset else lexOffset, lineStart)
      }
      while (ts.movePrevious) {
        val token = ts.token
        if (token.id != Ws) {
          return Math.max(ts.offset + token.length, lineStart)
        }
      }
    }

    return lexOffset
  }


  def skipParenthesis(ts:TokenSequence[TokenId]) :Boolean = {
    skipParenthesis(ts, false)
  }

  /**
   * Tries to skip parenthesis
   */
  def skipParenthesis(ts:TokenSequence[TokenId], back:Boolean) :Boolean = {
    var balance = 0

    var token = ts.token
    if (token == null) {
      return false
    }

    var id = token.id

    // skip whitespace and comment
    if (isWsComment(id)) {
      while ((if (back) ts.movePrevious else ts.moveNext) && isWsComment(id)) {}
    }

    // if current token is not parenthesis
    if (ts.token.id != (if (back) RPAREN else LPAREN)) {
      return false
    }

    do {
      token = ts.token
      id = token.id

      if (id == (if (back) RPAREN else LPAREN)) {
        balance += 1
      } else if (id == (if (back) LPAREN else RPAREN)) {
        balance match {
          case 0 =>
            return false
          case 1 =>
            if (back) ts.movePrevious else ts.moveNext
            return true
          case _ => balance -= 1
        }
      }
    } while (if (back) ts.movePrevious else ts.moveNext)

    false
  }

  /**
   * Tries to skip parenthesis
   */
  def skipPair(ts:TokenSequence[TokenId], left:TokenId, right:TokenId, back:Boolean) :Boolean = {
    var balance = 0

    var token = ts.token
    if (token == null) {
      return false
    }

    var id = token.id

    // * skip whitespace and comment
    if (isWsComment(id)) {
      while ((if (back) ts.movePrevious else ts.moveNext) && isWsComment(id)) {}
    }

    // * if current token is not parenthesis
    if (ts.token.id != (if (back) right else left)) {
      return false
    }

    do {
      token = ts.token
      id = token.id

      if (id == (if (back) right else left)) {
        balance += 1
      } else if (id == (if (back) left else right)) {
        balance match {
          case 0 =>
            return false
          case 1 =>
            if (back) ts.movePrevious else ts.moveNext
            return true
          case _ => balance -= 1
        }
      }
    } while (if (back) ts.movePrevious else ts.moveNext)

    false
  }

  /** Search forwards in the token sequence until a token of type <code>down</code> is found */
  def findFwd(ts:TokenSequence[TokenId], ups:Set[TokenId], down:TokenId) :OffsetRange = {
    var balance = 0
    while (ts.moveNext) {
      val token = ts.token
      val id = token.id
      (id, ups.contains(id), balance) match {
        case (`down`, _, 0) => return new OffsetRange(ts.offset, ts.offset + token.length)
        case (`down`, _, _) => balance -= 1
        case (_, true,   _) => balance += 1
        case _ =>
      }
    }

    OffsetRange.NONE
  }

  /** Search backwards in the token sequence until a token of type <code>up</code> is found */
  def  findBwd(ts:TokenSequence[TokenId], ups:Set[TokenId], down:TokenId) :OffsetRange = {
    var balance = 0
    while (ts.movePrevious) {
      val token = ts.token
      val id = token.id
      (id, ups.contains(id), balance) match {
        case (_, true,   0) => return new OffsetRange(ts.offset, ts.offset + token.length)
        case (_, true,   _) => balance -= 1
        case (`down`, _, _) => balance += 1
        case _ =>
      }
    }

    OffsetRange.NONE
  }

  /** Search forwards in the token sequence until a token of type <code>down</code> is found */
  def findFwd(ts:TokenSequence[TokenId], up:TokenId, down:TokenId) :OffsetRange = {
    var balance = 0
    while (ts.moveNext) {
      val token = ts.token
      (token.id, balance) match {
        case (`up`,   _) => balance += 1
        case (`down`, 0) => return new OffsetRange(ts.offset, ts.offset + token.length)
        case (`down`, _) => balance -= 1
        case _ =>
      }
    }

    OffsetRange.NONE
  }

  /** Search backwards in the token sequence until a token of type <code>up</code> is found */
  def  findBwd(ts:TokenSequence[TokenId], up:TokenId, down:TokenId) :OffsetRange = {
    var balance = 0
    while (ts.movePrevious) {
      val token = ts.token
      (token.id, balance) match {
        case (`up`,   0) => return new OffsetRange(ts.offset, ts.offset + token.length)
        case (`up`,   _) => balance += 1
        case (`down`, _) => balance -= 1
        case _ =>
      }
    }

    OffsetRange.NONE
  }

  /** Search forwards in the token sequence until a token of type <code>down</code> is found */
  def  findFwd(ts:TokenSequence[TokenId], up:String, down:String) :OffsetRange = {
    var balance = 0
    while (ts.moveNext) {
      val token = ts.token
      (token.text.toString, balance) match {
        case (`up`,   _) => balance += 1
        case (`down`, 0) => return new OffsetRange(ts.offset, ts.offset + token.length)
        case (`down`, _) => balance -= 1
        case _ =>
      }
    }

    OffsetRange.NONE
  }

  /** Search backwards in the token sequence until a token of type <code>up</code> is found */
  def findBwd(ts:TokenSequence[TokenId], up:String, down:String) :OffsetRange = {
    var balance = 0
    while (ts.movePrevious) {
      val token = ts.token
      (token.text.toString, balance) match {
        case (`up`,   0) => return new OffsetRange(ts.offset, ts.offset + token.length)
        case (`up`,   _) => balance += 1
        case (`down`, _) => balance -= 1
        case _ =>
      }
    }

    OffsetRange.NONE
  }

  def lineIndent(doc:BaseDocument, offset:Int) :Int = {
    try {
      val start = Utilities.getRowStart(doc, offset)
      var end = if (Utilities.isRowWhite(doc, start)) {
        Utilities.getRowEnd(doc, offset)
      } else {
        Utilities.getRowFirstNonWhite(doc, start)
      }

      val indent = Utilities.getVisualColumn(doc, end);

      indent
    } catch {
      case ex:BadLocationException =>
        Exceptions.printStackTrace(ex)
        0
    }
  }

  def isWsComment(id:TokenId) :Boolean = isWs(id) || isNl(id) || isComment(id)

  /**
   * The same as braceBalance but generalized to any pair of matching
   * tokens.
   * @param open the token that increses the count
   * @param close the token that decreses the count
   */
  @throws(classOf[BadLocationException])
  def tokenBalance(doc:BaseDocument, open:TokenId, close:TokenId, offset:Int) :Int = {
    val ts = tokenSequence(doc, 0) match {
      case None => return 0
      case Some(x) => x
    }

    // XXX Why 0? Why not offset?
    ts.moveIndex(0)
    if (!ts.moveNext) {
      return 0
    }

    var balance = 0
    do {
      ts.token.id match {
        case `open` => balance += 1
        case `close` => balance -= 1
        case _ =>
      }
    } while (ts.moveNext)

    balance
  }

  /**
   * The same as braceBalance but generalized to any pair of matching
   * tokens.
   * @param open the token that increses the count
   * @param close the token that decreses the count
   */
  @throws(classOf[BadLocationException])
  def tokenBalance(doc:BaseDocument, open:String, close:String, offset:int) :Int = {
    val ts = tokenSequence(doc, 0) match {
      case None => return 0
      case Some(x) => x
    }

    // XXX Why 0? Why not offset?
    ts.moveIndex(0)
    if (!ts.moveNext) {
      return 0
    }

    var balance = 0
    do {
      val token = ts.token
      token.text.toString match {
        case `open` => balance += 1
        case `close` => balance -= 1
        case _ =>
      }
    } while (ts.moveNext)

    balance
  }


  /** Compute the balance of pair tokens on the line */
  def lineBalance(doc:BaseDocument, offset:Int, up:TokenId, down:TokenId) :Stack[Token[TokenId]] = {
    val balanceStack = new Stack[Token[TokenId]]
    try {
      val begin = Utilities.getRowStart(doc, offset)
      val end = Utilities.getRowEnd(doc, offset)

      val ts = tokenSequence(doc, begin) match {
        case None => return balanceStack
        case Some(x) => x
      }

      ts.move(begin)
      if (!ts.moveNext) {
        return balanceStack
      }

      var balance = 0
      do {
        val token = ts.offsetToken
        token.id match {
          case `up` =>
            balanceStack.push(token)
            balance += 1
          case `down` =>
            if (!balanceStack.isEmpty) {
              balanceStack.pop
            }
            balance -= 1
          case _ =>
        }
      } while (ts.moveNext && (ts.offset <= end))

      balanceStack
    } catch {
      case ex:BadLocationException =>
        Exceptions.printStackTrace(ex)
        balanceStack
    }
  }

  /**
   * Return true iff the line for the given offset is a JavaScript comment line.
   * This will return false for lines that contain comments (even when the
   * offset is within the comment portion) but also contain code.
   */
  @throws(classOf[BadLocationException])
  def isCommentOnlyLine(doc:BaseDocument, offset:Int) :Boolean = {
    val begin = Utilities.getRowFirstNonWhite(doc, offset)
    if (begin == -1) {
      return false // whitespace only
    }

    for (token <- token(doc, begin)) {
      return isLineComment(token.id)
    }

    return false
  }

  def docCommentRangeBefore(th:TokenHierarchy[_], lexOffset:Int) :OffsetRange = {
    val ts = tokenSequence(th, lexOffset) match {
      case None => return OffsetRange.NONE
      case Some(x) => x
    }
    ts.move(lexOffset)

    var startLineCommentSet = false
    var offset = -1
    var endOffset = -1
    var done = false
    while (ts.movePrevious && !done) {
      val token = ts.token
      token.id match {
        case id if isWsComment(id) =>
          offset = ts.offset
          if (!startLineCommentSet) {
            endOffset = offset + token.length
            startLineCommentSet = true
          }
        case _ => done = true
      }
    }

    if (offset >= 0 && endOffset >= offset) {
      new OffsetRange(offset, endOffset)
    } else {
      OffsetRange.NONE
    }
  }
}

