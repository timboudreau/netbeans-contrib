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
package org.netbeans.modules.erlang.editor.lexer

import _root_.java.util.Collection
import _root_.java.util.Collections
import _root_.java.util.HashMap
import _root_.java.util.HashSet
import _root_.java.util.Map
import _root_.java.util.Arrays

import org.netbeans.api.lexer.InputAttributes
import org.netbeans.api.lexer.Language
import org.netbeans.api.lexer.LanguagePath
import org.netbeans.api.lexer.Token
import org.netbeans.api.lexer.TokenId
import org.netbeans.spi.lexer.LanguageEmbedding
import org.netbeans.spi.lexer.LanguageHierarchy
import org.netbeans.spi.lexer.Lexer
import org.netbeans.spi.lexer.LexerRestartInfo

/**
 * 
 * @author Caoyuan Deng
 */
object ErlangTokenId extends Enumeration {
    // Let type of enum's value the same as enum itself
    type ErlangTokenId = V

    // Extends Enumeration.Val to get custom enumeration value
    class V(val name:String, val fixedText:String, val primaryCategory:String) extends Val(name) with TokenId {
        override
        def ordinal = id
    }
    object V {
        def apply(name:String, fixedText:String, primaryCategory:String) = new V(name, fixedText, primaryCategory)
    }
  
    val IGNORED = V("IGNORED", null, "ingore")
    val Error = V("Error", null, "error")

    // --- Spaces and comments
    val Ws = V("Ws", null, "whitespace")
    val Nl = V("Nl", null, "whitespace")
    val LineComment = V("LineComment", null, "comment")
    val CommentTag = V("CommentTag", null, "comment")
    val CommentData = V("CommentData", null, "comment")

    // --- Literals
    val IntegerLiteral = V("IntegerLiteral", null, "number")
    val FloatingPointLiteral = V("FloatingPointLiteral", null, "number")
    val CharacterLiteral = V("CharacterLiteral", null, "char")
    val StringLiteral = V("StringLiteral", null, "string")

    // --- Keywords
    val Andalso = V("Andalso", "andalso", "keyword")
    val After = V("After", "after", "keyword")
    val And = V("And", "and", "keyword")
    val Band = V("Band", "band", "keyword")
    val Begin = V("Begin", "begin", "keyword")
    val Bnot = V("Bnot", "bnot", "keyword")
    val Bor = V("Bor", "bor", "keyword")
    val Bsr = V("Bsr", "bsr", "keyword")
    val Bxor = V("Bxor", "bxor", "keyword")
    val Case = V("Case", "case", "keyword")
    val Catch = V("Catch", "catch", "keyword")
    val Cond = V("Cond", "cond", "keyword")
    val Div = V("Div", "div", "keyword")
    val End = V("End", "end", "keyword")
    val Fun = V("Fun", "fun", "keyword")
    val If = V("If", "if", "keyword")
    val Not = V("Not", "not", "keyword")
    val Of = V("Of", "of", "keyword")
    val Orelse = V("Orelse", "orelse", "keyword")
    val Or = V("Or", "or", "keyword")
    val Query = V("Query", "query", "keyword")
    val Receive = V("Receive", "receive", "keyword")
    val Rem = V("Rem", "rem", "keyword")
    val Try = V("Try", "try", "keyword")
    val Spec = V("Spec", "spec", "keyword")
    val When = V("When", "when", "keyword")
    val Xor = V("Xor", "xor", "keyword")

    // --- Identifiers
    val Macro = V("Macro", null, "identifier")
    val Atom = V("Atom", null, "identifier")
    val Var = V("Var", null, "identifier")

    // --- Stop
    val Stop = V("Stop", ".", "separator")

    // --- Symbols
    val LParen = V("LParen", "(", "separator")
    val RParen = V("RParan", ")", "separator")
    val LBrace = V("LBrace", "{", "separator")
    val RBrace = V("RBrace", "}", "separator")
    val LBracket = V("LBracket", "[", "separator")
    val RBracket = V("RBracket", "]", "separator")
    val Comma = V("Comma", ",", "separator")
    val Dot = V("Dot", ".", "separator")
    val Semicolon = V("Semicolon", ";", "separator")
    val DBar = V("DBar", "||", "separator")
    val Bar = V("Bar", "|",  "separator")
    val Question = V("Question", "?","separator")
    val DLt = V("DLt", "<<", "separator")
    val LArrow = V("LArrow", "<-", "separator")
    val Lt = V("Lt", "<", "separator")
    val DGt = V("DGt", ">>", "separator")
    val Ge = V("Ge", ">=", "separator")
    val Gt = V("Gt", ">", "separator")
    val ColonMinus = V("ColonMinus", ":-", "separator")
    val DColon = V("DColon", "::", "separator")
    val Colon = V("Colon", ":", "separator")
    val Hash = V("Hash", "#", "separator")
    val DPlus = V("DPlus", "++", "separator")
    val Plus = V("Plus", "+", "separator")
    val DMinus = V("DMinus", "--", "separator")
    val RArrow = V("RArrow", "->", "separator")
    val Minus = V("Minus", "-", "separator")
    val Star = V("Star", "*", "separator")
    val Ne = V("Ne", "/=", "separator")
    val Slash = V("Slash", "/", "separator")
    val EEq = V("EEq", "=:=", "separator")
    val ENe = V("ENe", "=/=", "separator")
    val DEq = V("DEq", "==", "separator")
    val Le = V("le", "=<", "separator")
    val Eq = V("Eq", "=", "separator")
    val Exclamation = V("Exclamation", "!", "separator")

  
    /**
     * MIME type for Erlang. Don't change this without also consulting the various XML files
     * that cannot reference this value directly.
     */
    val ERLANG_MIME_TYPE = "text/x-erlang"; // NOI18N

    /** should use def instead of val here, which will be called from instanceCreate of NetBeans' system  */
    def language = new LanguageHierarchy[ErlangTokenId] {
        protected def mimeType = ERLANG_MIME_TYPE

        protected def createTokenIds :Collection[ErlangTokenId] = {
            val ids = new HashSet[ErlangTokenId]
            elements.foreach{ids add _.asInstanceOf[ErlangTokenId]}
            ids
        }
    
        protected def createLexer(info:LexerRestartInfo[ErlangTokenId]) :Lexer[ErlangTokenId] = ErlangLexer.create(info) match {
            case None => null
            case Some(l) => l
        }

        override
        protected def createTokenCategories :Map[String, Collection[ErlangTokenId]] = {
            val cats = new HashMap[String, Collection[ErlangTokenId]]
            cats
        }

        override
        protected def embedding(token:Token[ErlangTokenId], languagePath:LanguagePath, inputAttributes:InputAttributes) = {
            null // No embedding
        }
    }.language

}
