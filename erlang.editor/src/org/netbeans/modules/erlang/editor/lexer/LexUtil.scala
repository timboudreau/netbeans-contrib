/*
 * LexUtil.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.erlang.editor.lexer

import org.netbeans.api.lexer.{Token, TokenId, TokenHierarchy, TokenSequence}
import org.netbeans.modules.csl.spi.ParserResult
import org.netbeans.modules.parsing.api.Snapshot
import org.netbeans.editor.BaseDocument
import org.netbeans.modules.erlang.editor.lexer.ErlangTokenId._

trait LexUtil {
    implicit def TokenId2ErlangTokenId(ts:TokenSequence[_]) :TokenSequence[ErlangTokenId] = ts.asInstanceOf[TokenSequence[ErlangTokenId]]
    implicit def ErlangTokenId2TokenId(ts:TokenSequence[ErlangTokenId]) :TokenSequence[TokenId] = ts.asInstanceOf[TokenSequence[TokenId]]
}

object LexUtil {

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
        case _ => TokenHierarchy.create(snapshot.getText, ErlangTokenId.language) match {
                case null => None
                case th => Some(th)
            }
    }

    def tokenHierarchy(pResult:ParserResult) :Option[TokenHierarchy[_]] = pResult match {
        case null => null
        case _ => tokenHierarchy(pResult.getSnapshot)
    }

    def tokenSequence(th:TokenHierarchy[_], offset:Int) :Option[TokenSequence[_]] = th.tokenSequence(ErlangTokenId.language) match {
        case null =>
            // * Possibly an embedding scenario such as an RHTML file
            def find(itr:_root_.java.util.Iterator[TokenSequence[_]]) :Option[TokenSequence[_]] = itr.hasNext match {
                case true => itr.next match {
                        case ts if ts.language == ErlangTokenId.language => Some(ts)
                        case _ => find(itr)
                    }
                case false => None
            }
         
            // * First try with backward bias true
            find(th.embeddedTokenSequences(offset, true).iterator) match {
                case None => find(th.embeddedTokenSequences(offset, true).iterator)
                case x => x
            }
        case ts => Some(ts)
    }

    private def WS_COMMENT :List[TokenId] = List(ErlangTokenId.Ws,
                                                 ErlangTokenId.Nl,
                                                 ErlangTokenId.LineComment)

    def findNextNonWsNonComment(ts:TokenSequence[_]) :Token[_] = {
        findNext(ts, WS_COMMENT)
    }

    def findPreviousNonWsNonComment(ts:TokenSequence[_]) :Token[_] = {
        findPrevious(ts, WS_COMMENT)
    }

    private val WS :List[TokenId] = List(ErlangTokenId.Ws,
                                         ErlangTokenId.Nl)

    def findNextNonWs(ts:TokenSequence[_]) :Token[_] = {
        findNext(ts, WS)
    }

    def findPreviousNonWs(ts:TokenSequence[_]) :Token[_] = {
        findPrevious(ts, WS)
    }

    def findNext(ts:TokenSequence[_], ignores:List[TokenId]) :Token[_] = {
        if (ignores.contains(ts.token.id)) {
            while (ts.moveNext && ignores.contains(ts.token.id)) {}
        }
        ts.token
    }

    def findPrevious(ts:TokenSequence[_], ignores:List[TokenId]) :Token[_] = {
        if (ignores.contains(ts.token.id)) {
            while (ts.movePrevious && ignores.contains(ts.token.id)) {}
        }
        ts.token
    }

    def findNext(ts:TokenSequence[_], id:TokenId) :Token[_] = {
        if (ts.token.id != id) {
            while (ts.moveNext && ts.token.id != id) {}
        }
        ts.token
    }

    def findNextIn(ts:TokenSequence[_], includes:List[TokenId] ) :Token[_] = {
        if (!includes.contains(ts.token.id)) {
            while (ts.moveNext && !includes.contains(ts.token.id)) {}
        }
        ts.token
    }

    def findPrev(ts:TokenSequence[_], id:TokenId) :Token[_] = {
        if (ts.token.id != id) {
            while (ts.movePrevious && ts.token.id != id) {}
        }
        ts.token
    }

    def findNextIncluding(ts:TokenSequence[_], includes:List[TokenId] ) :Token[_] = {
        while (ts.moveNext && !includes.contains(ts.token.id)) {}
        ts.token
    }

    def findPrevIncluding(ts:TokenSequence[_], includes:List[TokenId]) :Token[_] = {
        if (!includes.contains(ts.token.id)) {
            while (ts.movePrevious && !includes.contains(ts.token.id)) {}
        }
        ts.token
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
        if (ts.token.id != (if (back) ErlangTokenId.RParen else ErlangTokenId.LParen)) {
            return false
        }

        do {
            token = ts.token
            id = token.id

            if (id == (if (back) ErlangTokenId.RParen else ErlangTokenId.LParen)) {
                balance += 1
            } else if (id == (if (back) ErlangTokenId.LParen else ErlangTokenId.RParen)) {
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

    def isWsComment(id:TokenId) :Boolean = id match {
        case ErlangTokenId.Ws | ErlangTokenId.Nl => true
        case _ if isComment(id) => true
        case _ => false
    }

    def isComment(id:TokenId) :Boolean = id match {
        case ErlangTokenId.LineComment => true
        case _ => false
    }

}
