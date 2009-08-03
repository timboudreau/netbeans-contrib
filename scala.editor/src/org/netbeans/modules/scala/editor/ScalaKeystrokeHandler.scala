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

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.csl.api.OffsetRange
import org.netbeans.modules.csl.api.KeystrokeHandler
import org.netbeans.modules.csl.spi.GsfUtilities;
import org.netbeans.modules.editor.indent.api.IndentUtils;
import org.netbeans.api.lexer.{Language, Token, TokenHierarchy, TokenId, TokenSequence}
import org.netbeans.editor.BaseDocument
import org.netbeans.editor.Utilities
import org.netbeans.modules.scala.editor.lexer.{ScalaLexUtil, ScalaTokenId}

class ScalaKeystrokeHandler extends KeystrokeHandler {
    /**
     * (Based on BracketCompletion class in NetBeans' java editor support)
     *
     * A hook method called after a character was inserted into the
     * document. The function checks for special characters for
     * completion ()[]'"{} and other conditions and optionally performs
     * changes to the doc and or caret (complets braces, moves caret,
     * etc.)
     *
     * Return true if the character was already inserted (and the IDE
     * should not further insert anything)
     *
     * XXX Fix javadoc.
     */
    override def beforeCharInserted(doc: Document, caretOffset : Int, target: JTextComponent, ch : Char) : Boolean = {
        false //TODO
    }

    /** @todo Rip out the boolean return value? What does it mean? */
    override def afterCharInserted(doc : Document, caretOffset : Int, target : JTextComponent, ch : Char) : Boolean = {
        true //TODO
    }

    /**
     * (Based on KeystrokeHandler class in NetBeans' java editor support)
     *
     * Hook called after a character *ch* was backspace-deleted from
     * *doc*. The function possibly removes bracket or quote pair if
     * appropriate.
     * @todo Document why both caretOffset and caret is passed in!
     * Return the new offset, or -1
     */

    /** @todo Split into before and after? */
    override def charBackspaced(doc : Document, caretOffset : Int, target : JTextComponent, ch : Char) : Boolean = {
        true //TODO
    }

    /**
     * A line break is being called. Return -1 to do nothing.
     * If you want to modify the document first, you can do that, and then
     * return the new offset to assign the caret to AFTER the newline has been
     * inserted.
     *
     * @todo rip out return value
     * @todo Document why both caretOffset and caret is passed in!
     */
    override def beforeBreak(document : Document, offset : Int, target : JTextComponent) : Int = {

        val  caret = target.getCaret();
        val  doc = document.asInstanceOf[BaseDocument];

        val insertMatching =  true; //TODO  isInsertMatchingEnabled(doc);

        val lineBegin = Utilities.getRowStart(doc, offset);
        val lineEnd = Utilities.getRowEnd(doc, offset);

        if (lineBegin == offset && lineEnd == offset) {
            // Pressed return on a blank newline - do nothing
            -1;
        } else {
            val tsOpt = getTokenSequence(doc, offset)
            if (tsOpt == None) {
                -1
            } else {
                val ts = tsOpt.get
                val token = ts.token();
                val id = token.id();
                //TODO
                id match {
                    case ScalaTokenId.Identifier => { //TODO this should have been Error token??
                        if (token.text.toString.startsWith("/*") && ts.offset == Utilities.getRowFirstNonWhite(doc, offset)) {
                            val indent = GsfUtilities.getLineIndent(doc, offset)
                            val sb = new StringBuilder()
                            sb.append(IndentUtils.createIndentString(doc, indent))
                            sb.append(" * ")
                            val offsetDelta = sb.length + 1
                            sb.append("\n")
                            sb.append(IndentUtils.createIndentString(doc, indent))
                            sb.append(" */")
                            doc.insertString(offset, sb.toString(), null);
                            caret.setDot(offset);
                            offset + offsetDelta;
                        } else {
                            -1
                        }
                    }
//                    case ScalaTokenId.LineComment => {
//                      
//                    }
//                    case ScalaTokenId.Nl => {
//                      val isComment = ts.movePrevious() && ts.token().id() == ScalaTokenId.LineComment
//                      
//                    }


                    case t => {
                        -1
                    }
                }
            }
        }
    }

    /**
     * Compute a range matching the caret position. If no eligible range
     * is found, return {@link OffsetRange#NONE}.
     */
    override def findMatching(doc : Document, caretOffset : Int) : OffsetRange = {
        OffsetRange.NONE //TODO
    }

    /**
     * Compute set of selection ranges for the given parse tree (around the given offset),
     * in leaf-to-root order.
     */
    override def findLogicalRanges(info : ParserResult, caretOffset: Int) : java.util.List[OffsetRange] = {
        new java.util.ArrayList[OffsetRange]() //TODO
    }

    /**
     * Compute the previous word position, if any. Can be used to implement
     * camel case motion etc.
     *
     * @param doc The document to move in
     * @param caretOffset The caret position corresponding to the current word
     * @param reverse If true, move forwards, otherwise move backwards (e.g. "previous" word)
     * @return The next word boundary offset in the given direction, or -1 if this
     *   implementation doesn't want to compute word boundaries (the default will be used)
     */
    override def getNextWordOffset(doc : Document, caretOffset : Int, reverse : Boolean) : Int = {
        -1 //TODO
    }

    private def getTokenSequence(doc : BaseDocument, offset : Int) : Option[TokenSequence[TokenId]] = {
        val ts = ScalaLexUtil.getTokenSequence(doc, offset);
        if (ts == null) {
            None
        } else {
            ts.move(offset);
            if (!ts.moveNext() && !ts.movePrevious()) {
                None;
            } else {
                Some(ts)
            }
        }
    }
}
