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
package org.netbeans.modules.rats.editor.lexer

import _root_.java.util.Collection
import _root_.java.util.Collections
import _root_.java.util.HashMap
import _root_.java.util.HashSet
import _root_.java.util.Map
import _root_.java.util.Arrays

import org.netbeans.api.java.lexer.JavaTokenId;
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
object RatsTokenId extends Enumeration {
  // Let type of enum's value the same as enum itself
  type RatsTokenId = V

  // Extends Enumeration.Val to get custom enumeration value
  class V(val name:String, val fixedText:String, val primaryCategory:String) extends Val(name) with TokenId {
    override def ordinal = id
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
  val DocCommentStart = V("DocCommentStart", null, "comment")
  val DocCommentBody = V("DocCommentBody", null, "comment")
  val DocCommentEnd = V("DocCommentEnd", null, "comment")
  val BlockCommentStart = V("BlockCommentStart", null, "comment")
  val BlockCommentBody = V("BlockCommentBody", null, "comment")
  val BlockCommentEnd = V("BlockCommentEnd", null, "comment")

  // --- Literals
  val IntegerLiteral = V("IntegerLiteral", null, "number")
  val CharacterLiteral = V("CharacterLiteral", null, "char")
  val StringLiteral = V("StringLiteral", null, "string")

  val CharacterRange = V("CharacterRange", null, "char")

  // --- Action
  val ActionBody = V("ActionBody", null, "embedded")

  // --- Keywords
  val Public = V("Public", "public", "keyword")
  val Protected = V("Protected", "protected", "keyword")
  val Private = V("Private", "private", "keyword")
  val Generic = V("Generic", "generic", "keyword")
  val Void = V("Void", "void", "keyword")
  val Null = V("Null", "null", "keyword")
  val Import = V("Import", "import", "keyword")
  val Module = V("Module", "module", "keyword")
  val Instantiate = V("Instantiate", "instantiate", "keyword")
  val Header = V("Header", "header", "keyword")
  val Body = V("Body", "body", "keyword")
  val Footer = V("Footer", "footer", "keyword")
  val Option = V("Option", "option", "keyword")
  val Transient = V("Transient", "transient", "keyword")

  // --- Identifiers
  val Name = V("Name", null, "identifier")
  val Word = V("Word", null, "identifier")

  // --- Symbols
  val Ellipsis = V("Ellipsis", "...", "separator")
  val PlusEqual = V("PlusEqual", "+=", "separator")
  val MinusEqual = V("MinusEqual", "-=", "separator")
  val ColonEqual = V("ColonEqual", ":=", "separator")
  val Comma = V("Comma", ",", "separator")
  val Dot = V("Dot", ".", "separator")
  val Eq = V("Eq", "=", "separator")
  val Slash = V("Slash", "/", "separator")
  val And = V("And", "&", "separator")
  val Not = V("Not", "!", "separator")
  val Caret = V("Caret", "^", "separator")
  val Colon = V("Colon", ":", "separator")
  val Question = V("Question", "?", "separator")
  val Star = V("Star", "*", "separator")
  val Plus = V("Plus", "+", "separator")
  val LParen = V("LParen", "(", "separator")
  val RParen = V("RParen", ")", "separator")
  val LBrace = V("LBrace", "{", "separator")
  val RBrace = V("RBrace", "}", "separator")
  val Semicolon = V("Semicolon", ";", "separator")
  val Lt = V("Lt", "<", "separator")
  val Gt = V("Gt", ">", "separator")
  val At = V("At", "@", "separator")
  val Underscore = V("Underscore", "_", "separator")

  // - Do we need this token to separator the ActionBody ?
  val Delimiter = V("Delimiter", null, "separator")
  
  /**
   * MIME type for Erlang. Don't change this without also consulting the various XML files
   * that cannot reference this value directly.
   */
  val ERLANG_MIME_TYPE = "text/x-rats"; // NOI18N

  // * should use "val" instead of "def" here to get a singleton language val, which
  // * will be used to identity the token's language by "==" comparasion by other classes.
  // * Be aware of the init order! to get createTokenIds gathers all TokenIds, should
  // * be put after all token id val definition
  val language = new LanguageHierarchy[TokenId] {
    protected def mimeType = ERLANG_MIME_TYPE

    protected def createTokenIds :Collection[TokenId] = {
      val ids = new HashSet[TokenId]
      elements.foreach{ids add _.asInstanceOf[TokenId]}
      ids
    }
    
    protected def createLexer(info:LexerRestartInfo[TokenId]) :Lexer[TokenId] = RatsLexer.create(info)

    override protected def createTokenCategories :Map[String, Collection[TokenId]] = {
      val cats = new HashMap[String, Collection[TokenId]]
      cats
    }

    override protected def embedding(token:Token[TokenId], languagePath:LanguagePath, inputAttributes:InputAttributes) = {
      token.id match {
        case ActionBody => LanguageEmbedding.create(JavaTokenId.language, 0, 0, true);
        case _ => null
      }
    }
  }.language

}
