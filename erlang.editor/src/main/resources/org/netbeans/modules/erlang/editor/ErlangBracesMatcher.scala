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

package org.netbeans.modules.erlang.editor

import javax.swing.text.{AbstractDocument, BadLocationException}
import org.netbeans.api.lexer.{Token, TokenId, TokenSequence}
import org.netbeans.editor.BaseDocument
import org.netbeans.modules.csl.api.OffsetRange
import org.netbeans.modules.erlang.editor.lexer.{ErlangTokenId, LexUtil}
import org.netbeans.spi.editor.bracesmatching.{BracesMatcher, MatcherContext}

/**
 *
 * @author Caoyuan Deng
 */
object ErlangBracesMatcher {
  /** close to opens */
  private val PAIRS :Map[TokenId, Set[TokenId]] = Map(ErlangTokenId.RParen   -> Set(ErlangTokenId.LParen),
                                                      ErlangTokenId.RBrace   -> Set(ErlangTokenId.LBrace),
                                                      ErlangTokenId.RBracket -> Set(ErlangTokenId.LBracket),
                                                      ErlangTokenId.End      -> Set(ErlangTokenId.Begin,
                                                                                    ErlangTokenId.Case,
                                                                                    ErlangTokenId.If,
                                                                                    ErlangTokenId.Receive,
                                                                                    ErlangTokenId.Try))

}
class ErlangBracesMatcher(context: MatcherContext) extends BracesMatcher {
  import ErlangBracesMatcher._

  @throws(classOf[InterruptedException])
  @throws(classOf[BadLocationException])
  override def findOrigin: Array[Int] = {
    var offset = context.getSearchOffset
    val doc = context.getDocument.asInstanceOf[BaseDocument]

    doc.readLock
    try {
      LexUtil.tokenSequence(doc, offset) foreach {ts =>
        ts.move(offset)
        if (!ts.moveNext) {
          return null
        }

        var token = ts.token
        if (token == null) {
          return null
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
            return Array(ts.offset, ts.offset + token.length)
          case _ =>
        }

        for (opens <- PAIRS.get(id)) {
          return Array(ts.offset, ts.offset + token.length)
        }
      }

      null
    } finally {
      doc.readUnlock
    }
  }


  @throws(classOf[InterruptedException])
  @throws(classOf[BadLocationException])
  override def findMatches: Array[Int] = {
    var offset = context.getSearchOffset
    val doc = context.getDocument.asInstanceOf[BaseDocument]
    
    doc.readLock
    try {
      LexUtil.tokenSequence(doc, offset) foreach {ts =>
        ts.move(offset)
        if (!ts.moveNext) {
          return null
        }

        var token = ts.token
        if (token == null) {
          return null
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
              case x => return Array(x.getStart, x.getEnd)
            }
          case _ =>
        }

        for (opens <- PAIRS.get(id)) {
          LexUtil.findBwd(ts, opens, id) match {
            case OffsetRange.NONE =>
            case x => return Array(x.getStart, x.getEnd)
          }
        }
      }

      null
    } finally {
      doc.readUnlock
    }
  }

}
