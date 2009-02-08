/*
 * LexUtil.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.erlang.editor.lexer

import org.netbeans.api.lexer.{Token, TokenId, TokenHierarchy, TokenSequence}

import org.netbeans.modules.erlang.editor.lexer.ErlangTokenId._

object LexUtil {

    def tokenSequence(th:TokenHierarchy[ErlangTokenId], offset:Int) :TokenSequence[ErlangTokenId] = th.tokenSequence(ErlangTokenId.language) match {
        case null =>
            // * Possibly an embedding scenario such as an RHTML file
            // * First try with backward bias true
            var list = th.embeddedTokenSequences(offset, true)

            var itr = list.iterator
            while (itr.hasNext) {
                val t = itr.next
                if (t.language == ErlangTokenId.language) {
                    return t.asInstanceOf[TokenSequence[ErlangTokenId]]
                }
            }

            list = th.embeddedTokenSequences(offset, false)

            itr = list.iterator
            while (itr.hasNext) {
                val t = itr.next
                if (t.language == ErlangTokenId.language) {
                    return t.asInstanceOf[TokenSequence[ErlangTokenId]]
                }
            }
        
            null
        case ts => ts.asInstanceOf[TokenSequence[ErlangTokenId]]
    }

    private def WS_COMMENT :List[ErlangTokenId] = List(ErlangTokenId.Ws,
                                                       ErlangTokenId.Nl,
                                                       ErlangTokenId.LineComment)

    def findNextNonWsNonComment(ts:TokenSequence[ErlangTokenId]) :Token[ErlangTokenId] = {
        findNext(ts, WS_COMMENT)
    }

    def findPreviousNonWsNonComment(ts:TokenSequence[ErlangTokenId]) :Token[ErlangTokenId] = {
        findPrevious(ts, WS_COMMENT)
    }

    private val WS :List[ErlangTokenId] = List(ErlangTokenId.Ws,
                                               ErlangTokenId.Nl)

    def findNextNonWs(ts:TokenSequence[ErlangTokenId]) :Token[ErlangTokenId] = {
        findNext(ts, WS)
    }

    def findPreviousNonWs(ts:TokenSequence[ErlangTokenId]) :Token[ErlangTokenId] = {
        findPrevious(ts, WS)
    }

    def findNext(ts:TokenSequence[ErlangTokenId], ignores:List[ErlangTokenId]) :Token[ErlangTokenId] = {
        if (ignores.contains(ts.token.id)) {
            while (ts.moveNext && ignores.contains(ts.token.id)) {}
        }
        ts.token
    }

    def findPrevious(ts:TokenSequence[ErlangTokenId], ignores:List[ErlangTokenId]) :Token[ErlangTokenId] = {
        if (ignores.contains(ts.token.id)) {
            while (ts.movePrevious && ignores.contains(ts.token.id)) {}
        }
        ts.token
    }

    def findNext(ts:TokenSequence[ErlangTokenId], id:ErlangTokenId) :Token[ErlangTokenId] = {
        if (ts.token.id != id) {
            while (ts.moveNext && ts.token.id != id) {}
        }
        ts.token
    }

    def findNextIn(ts:TokenSequence[ErlangTokenId], includes:List[ErlangTokenId] ) :Token[ErlangTokenId] = {
        if (!includes.contains(ts.token.id)) {
            while (ts.moveNext && !includes.contains(ts.token.id)) {}
        }
        ts.token
    }

    def findPrev(ts:TokenSequence[ErlangTokenId], id:ErlangTokenId) :Token[ErlangTokenId] = {
        if (ts.token.id != id) {
            while (ts.movePrevious && ts.token.id != id) {}
        }
        ts.token
    }

    def findNextIncluding(ts:TokenSequence[ErlangTokenId], includes:List[ErlangTokenId] ) :Token[ErlangTokenId] = {
        while (ts.moveNext && !includes.contains(ts.token.id)) {}
        ts.token
    }

    def findPrevIncluding(ts:TokenSequence[ErlangTokenId], includes:List[ErlangTokenId]) :Token[ErlangTokenId] = {
        if (!includes.contains(ts.token.id)) {
            while (ts.movePrevious && !includes.contains(ts.token.id)) {}
        }
        ts.token
    }

    def skipParenthesis(ts:TokenSequence[ErlangTokenId]) :Boolean = {
        skipParenthesis(ts, false)
    }

    /**
     * Tries to skip parenthesis
     */
    def skipParenthesis(ts:TokenSequence[ErlangTokenId], back:Boolean) :Boolean = {
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
    def skipPair(ts:TokenSequence[ErlangTokenId], left:ErlangTokenId, right:ErlangTokenId, back:Boolean) :Boolean = {
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
        case _ if isComment(id) => true
        case ErlangTokenId.Ws | ErlangTokenId.Nl => true
        case _ => false
    }

    def isComment(id:TokenId) :Boolean = id match {
        case ErlangTokenId.LineComment => true
        case _ => false
    }

}
