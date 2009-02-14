/*
 * LexUtil.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.erlang.editor.lexer

import javax.swing.text.{BadLocationException,Document}
import org.netbeans.api.lexer.{Token,TokenId,TokenHierarchy,TokenSequence}
import org.netbeans.modules.csl.api.OffsetRange
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.parsing.api.Snapshot
import org.netbeans.editor.{BaseDocument,Utilities}
import org.netbeans.modules.erlang.editor.lexer.ErlangTokenId._
import org.openide.filesystems.FileUtil
import org.openide.loaders.DataObject
import org.openide.util.Exceptions
import scala.collection.mutable.Stack

object LexUtil extends BaseLexUtil[TokenId]

/**
 * Special functions for ErlangTokenId
 */
trait LanguageLexUtil {
    protected val language = ErlangTokenId.language

    protected val LPAREN = ErlangTokenId.LParen
    protected val RPAREN = ErlangTokenId.RParen

    protected val WS = Set(ErlangTokenId.Ws,
                           ErlangTokenId.Nl
    )

    protected val WS_COMMENT = Set(ErlangTokenId.Ws,
                                   ErlangTokenId.Nl,
                                   ErlangTokenId.LineComment
    )

    protected val INDENT_TOKENS = Set(ErlangTokenId.Case,
                                      ErlangTokenId.After,
                                      ErlangTokenId.If,
                                      ErlangTokenId.Receive,
                                      ErlangTokenId.Begin
    )

    protected val END_PAIRS = Set(ErlangTokenId.Try)

    def isWs(id:TokenId) = id == ErlangTokenId.Ws
    def isNl(id:TokenId) = id == ErlangTokenId.Nl

    def isComment(id:TokenId) :Boolean = id match {
        case ErlangTokenId.LineComment => true
        case _ => false
    }

    def isLineComment(id:TokenId) :Boolean = id match {
        case ErlangTokenId.LineComment => true
        case _ => false
    }

    def isBeginToken(id:TokenId) = id match {
        case ErlangTokenId.Begin => true
        case ErlangTokenId.Case => true
        case ErlangTokenId.If => true
        case ErlangTokenId.Receive => true
        case ErlangTokenId.Try => true
        case _ => false
    }

    def isEndToken(id:TokenId) = id match {
        case ErlangTokenId.End => true
        case _ => false
    }

    def isIndentToken(id:TokenId) = id match {
        case ErlangTokenId.After => true
        case ErlangTokenId.Begin => true
        case ErlangTokenId.Case => true
        case ErlangTokenId.Catch => true
        case ErlangTokenId.If => true
        case ErlangTokenId.Receive => true
        case ErlangTokenId.Try => true
        case ErlangTokenId.RArrow => true
        case _ => false
    }
}

trait BaseLexUtil[T <: TokenId] extends LanguageLexUtil {

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

    def tokenHierarchy(snapshot:Snapshot) :Option[TokenHierarchy[_]] = document(snapshot, false) match {
        // * try get th from BaseDocument first, if it has been opened, th should has been there
        case doc:BaseDocument => TokenHierarchy.get(doc) match {
                case null => None
                case th => Some(th)
            }
        case _ => TokenHierarchy.create(snapshot.getText, language) match {
                case null => None
                case th => Some(th)
            }
    }

    def tokenHierarchy(pResult:ParserResult) :Option[TokenHierarchy[_]] = pResult match {
        case null => null
        case _ => tokenHierarchy(pResult.getSnapshot)
    }

    def tokenSequence(doc:BaseDocument, offset:Int) :Option[TokenSequence[T]] = {
        val th = TokenHierarchy.get(doc)
        tokenSequence(th, offset)
    }

    def tokenSequence(th:TokenHierarchy[_], offset:Int) :Option[TokenSequence[T]] = th.tokenSequence(language) match {
        case null =>
            // * Possibly an embedding scenario such as an RHTML file
            def find(itr:_root_.java.util.Iterator[TokenSequence[T]]) :Option[TokenSequence[T]] = itr.hasNext match {
                case true => itr.next match {
                        case ts if ts.language == language => Some(ts)
                        case _ => find(itr)
                    }
                case false => None
            }
         
            // * First try with backward bias true
            val itr1 = th.embeddedTokenSequences(offset, true).iterator.asInstanceOf[_root_.java.util.Iterator[TokenSequence[T]]]
            find(itr1) match {
                case None =>
                    val itr2 = th.embeddedTokenSequences(offset, false).iterator.asInstanceOf[_root_.java.util.Iterator[TokenSequence[T]]]
                    find(itr2)
                case x => x
            }
        case ts => Some(ts.asInstanceOf[TokenSequence[T]])
    }

    def rangeOfToken(th:TokenHierarchy[T], token:Token[T]) :OffsetRange = {
        val offset = token.offset(th)
        new OffsetRange(offset, offset + token.length)
    }

    /** For a possibly generated offset in an AST, return the corresponding lexing/true document offset */
    def lexerOffset(pResult:ParserResult, astOffset:Int) :Int = {
        pResult.getSnapshot.getOriginalOffset(astOffset)
    }

    def lexerOffsets(pResult:ParserResult, astRange:OffsetRange) :OffsetRange = {
        // * there has been  astRange, we can assume pResult not null
        val rangeStart = astRange.getStart
        val start = pResult.getSnapshot.getOriginalOffset(rangeStart)
        if (start == rangeStart) {
            astRange
        } else if (start == -1) {
            OffsetRange.NONE
        } else {
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
            val start = pResult.getSnapshot.getEmbeddedOffset(rangeStart)
            if (start == rangeStart) {
                lexRange
            } else if (start == -1) {
                OffsetRange.NONE
            } else {
                // Assumes the translated range maintains size
                new OffsetRange(start, start + lexRange.getLength())
            }
    }

    def positionedSequence(doc:BaseDocument, offset:Int) :Option[TokenSequence[T]] = {
        positionedSequence(doc, offset, true)
    }

    def positionedSequence(doc:BaseDocument, offset:Int, lookBack:Boolean) :Option[TokenSequence[T]] = {
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

    def token(doc:BaseDocument, offset:Int) :Option[Token[T]] = {
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
                return text.charAt(0)
            } else 0
    }
    

    def findNextNonWsNonComment(ts:TokenSequence[T]) :Token[T] = {
        findNext(ts, WS_COMMENT.asInstanceOf[Set[T]])
    }

    def findPreviousNonWsNonComment(ts:TokenSequence[T]) :Token[T] = {
        findPrevious(ts, WS_COMMENT.asInstanceOf[Set[T]])
    }

    def findNextNonWs(ts:TokenSequence[T]) :Token[T] = {
        findNext(ts, WS.asInstanceOf[Set[T]])
    }

    def findPreviousNonWs(ts:TokenSequence[T]) :Token[T] = {
        findPrevious(ts, WS.asInstanceOf[Set[T]])
    }

    def findNext(ts:TokenSequence[T], ignores:Set[T]) :Token[T] = {
        if (ignores.contains(ts.token.id)) {
            while (ts.moveNext && ignores.contains(ts.token.id)) {}
        }
        ts.token
    }

    def findPrevious(ts:TokenSequence[T], ignores:Set[T]) :Token[T] = {
        if (ignores.contains(ts.token.id)) {
            while (ts.movePrevious && ignores.contains(ts.token.id)) {}
        }
        ts.token
    }

    def findNext(ts:TokenSequence[T], id:T) :Token[T] = {
        if (ts.token.id != id) {
            while (ts.moveNext && ts.token.id != id) {}
        }
        ts.token
    }

    def findNextIn(ts:TokenSequence[T], includes:Set[T] ) :Token[T] = {
        if (!includes.contains(ts.token.id)) {
            while (ts.moveNext && !includes.contains(ts.token.id)) {}
        }
        ts.token
    }

    def findPrev(ts:TokenSequence[T], id:T) :Token[T] = {
        if (ts.token.id != id) {
            while (ts.movePrevious && ts.token.id != id) {}
        }
        ts.token
    }

    def findNextIncluding(ts:TokenSequence[T], includes:Set[T] ) :Token[T] = {
        while (ts.moveNext && !includes.contains(ts.token.id)) {}
        ts.token
    }

    def findPrevIncluding(ts:TokenSequence[T], includes:Set[T]) :Token[_] = {
        if (!includes.contains(ts.token.id)) {
            while (ts.movePrevious && !includes.contains(ts.token.id)) {}
        }
        ts.token
    }

    def skipParenthesis(ts:TokenSequence[T]) :Boolean = {
        skipParenthesis(ts, false)
    }

    /**
     * Tries to skip parenthesis
     */
    def skipParenthesis(ts:TokenSequence[T], back:Boolean) :Boolean = {
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
                        if (back) {
                            ts.movePrevious
                        } else {
                            ts.moveNext
                        }
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
    def skipPair(ts:TokenSequence[T], left:T, right:T, back:Boolean) :Boolean = {
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
                        if (back) {
                            ts.movePrevious
                        } else {
                            ts.moveNext
                        }
                        return true
                    case _ => balance -= 1
                }
            }
        } while (if (back) ts.movePrevious else ts.moveNext)

        false
    }

    /** Search forwards in the token sequence until a token of type <code>down</code> is found */
    def findFwd(ts:TokenSequence[T], up:T, downs:Set[T]) :OffsetRange = {
        var balance = 0
        while (ts.moveNext) {
            val token = ts.token
            val id = token.id

            if (id == up) {
                balance += 1
            } else if (downs.contains(id)) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset, ts.offset + token.length)
                }
                balance -= 1
            }
        }

        OffsetRange.NONE
    }

    /** Search backwards in the token sequence until a token of type <code>up</code> is found */
    def  findBwd(ts:TokenSequence[T], ups:Set[T], down:T) :OffsetRange = {
        var balance = 0
        while (ts.movePrevious) {
            val token = ts.token
            val id = token.id

            if (ups.contains(id)) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset, ts.offset + token.length)
                }
                balance -= 1
            } else if (id == down) {
                balance += 1
            }
        }

        OffsetRange.NONE
    }

    /** Search forwards in the token sequence until a token of type <code>down</code> is found */
    def findFwd(ts:TokenSequence[T], up:T, down:T) :OffsetRange = {
        var balance = 0
        while (ts.moveNext) {
            val token = ts.token
            val id = token.id

            if (id == up) {
                balance += 1
            } else if (id == down) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset, ts.offset + token.length)
                }
                balance -= 1
            }
        }

        OffsetRange.NONE
    }

    /** Search backwards in the token sequence until a token of type <code>up</code> is found */
    def  findBwd(ts:TokenSequence[T], up:T, down:T) :OffsetRange = {
        var balance = 0
        while (ts.movePrevious) {
            val token = ts.token
            val id = token.id

            if (id == up) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset, ts.offset + token.length)
                }
                balance += 1
            } else if (id == down) {
                balance -= 1
            }
        }

        OffsetRange.NONE
    }

    /** Search forwards in the token sequence until a token of type <code>down</code> is found */
    def  findFwd(ts:TokenSequence[T], up:String, down:String) :OffsetRange = {
        var balance = 0
        while (ts.moveNext) {
            val token = ts.token
            val id = token.id
            val text = token.text.toString

            if (text.equals(up)) {
                balance += 1
            } else if (text.equals(down)) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset, ts.offset + token.length)
                }
                balance -= 1
            }
        }

        OffsetRange.NONE
    }

    /** Search backwards in the token sequence until a token of type <code>up</code> is found */
    def findBwd(ts:TokenSequence[T], up:String, down:String) :OffsetRange = {
        var balance = 0
        while (ts.movePrevious) {
            val token = ts.token
            val id = token.id
            val text = token.text.toString

            if (text.equals(up)) {
                if (balance == 0) {
                    return new OffsetRange(ts.offset, ts.offset + token.length)
                }
                balance += 1
            } else if (text.equals(down)) {
                balance -= 1
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

    def isWsComment(id:T) :Boolean = isWs(id) || isNl(id) || isComment(id)

    /**
     * The same as braceBalance but generalized to any pair of matching
     * tokens.
     * @param open the token that increses the count
     * @param close the token that decreses the count
     */
    @throws(classOf[BadLocationException])
    def tokenBalance(doc:BaseDocument, open:T, close:T, offset:Int) :Int = {
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
            if (token.id == open) {
                balance += 1
            } else if (token.id == close) {
                balance -= 1
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
    def getTokenBalance(doc:BaseDocument, open:String, close:String, offset:int) :Int = {
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
            val text = token.text.toString

            if (text.equals(open)) {
                balance += 1
            } else if (text.equals(text)) {
                balance -= 1
            }
        } while (ts.moveNext)

        balance
    }


    /** Compute the balance of begin/end tokens on the line.
     * @param doc the document
     * @param offset The offset somewhere on the line
     * @param upToOffset If true, only compute the line balance up to the given offset (inclusive),
     *   and if false compute the balance for the whole line
     */
    def beginEndLineBalance(doc:BaseDocument, offset:Int, upToOffset:Boolean) :Int = {
        try {
            val begin = Utilities.getRowStart(doc, offset)
            val end = if (upToOffset) offset else Utilities.getRowEnd(doc, offset)

            val ts = tokenSequence(doc, begin) match {
                case None => return 0
                case Some(x) => x
            }

            ts.move(begin)
            if (!ts.moveNext) {
                return 0
            }

            var balance = 0
            do {
                val token = ts.token
                val id = token.id
                if (isBeginToken(id)) {
                    balance += 1
                } else if (isEndToken(id)) {
                    balance -= 1
                }
            } while (ts.moveNext && (ts.offset <= end))

            balance
        } catch {
            case ex:BadLocationException =>
                Exceptions.printStackTrace(ex)
                0
        }
    }

    /** Compute the balance of pair tokens on the line */
    def lineBalance(doc:BaseDocument, offset:Int, up:T, down:T) :Stack[Token[T]] = {
        val balanceStack = new Stack[Token[T]]
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
                val id = token.id
                
                if (id == up) {
                    balanceStack.push(token)
                    balance += 1
                } else if (id == down) {
                    if (!balanceStack.isEmpty) {
                        balanceStack.pop
                    }
                    balance -= 1
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


}

