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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.rats.editor.embed;

import _root_.java.awt.Color;
import _root_.java.util.NoSuchElementException;
import _root_.java.util.logging.Level;
import _root_.java.util.logging.Logger;
import _root_.javax.swing.text.AttributeSet;
import _root_.javax.swing.text.BadLocationException;
import _root_.javax.swing.text.Document;
import _root_.javax.swing.text.StyleConstants;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.editor.settings.FontColorSettings;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenHierarchyEvent;
import org.netbeans.api.lexer.TokenHierarchyListener;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.modules.rats.editor.lexer.RatsTokenId;
import org.netbeans.spi.editor.highlighting.HighlightsLayer;
import org.netbeans.spi.editor.highlighting.HighlightsLayerFactory;
import org.netbeans.spi.editor.highlighting.HighlightsSequence;
import org.netbeans.spi.editor.highlighting.ZOrder;
import org.netbeans.spi.editor.highlighting.support.AbstractHighlightsContainer;
import org.openide.util.WeakListeners;

/**
 * Implementation of Highlighting SPI creating coloured background
 * for embedded java sections.
 *
 * @author Marek Fukala
 * @author Caoyuan Deng
 */
object EmbeddedSectionsHighlighting {
  val LOG = Logger.getLogger(classOf[EmbeddedSectionsHighlighting].getName)
  
  def getColoring(fcs:FontColorSettings, tokenName:String) = {
    fcs.getTokenFontColors(tokenName) match {
      case null => null
      case as => as.getAttribute(StyleConstants.Background)
    }
  }

  @throws(classOf[BadLocationException])
  def isWhitespace(document:Document,  startOffset:Int, endOffset:Int) :Boolean = {
    val chars = DocumentUtilities.getText(document, startOffset, endOffset - startOffset)
    for(i <- 0 until chars.length()) {
      if (!Character.isWhitespace(chars.charAt(i))) {
        return false
      }
    }

    true
  }
}

class EmbeddedSectionsHighlighting(document:Document) extends AbstractHighlightsContainer with TokenHierarchyListener {
  import EmbeddedSectionsHighlighting._
  import org.netbeans.spi.editor.highlighting.HighlightsContainer
    
  private var hierarchy :TokenHierarchy[_ <: Document] = null
  private var version:Long = 0
        
  // load the background color for the embedding token
  var attribs:AttributeSet = _
  val mimeType = document.getProperty("mimeType").asInstanceOf[String] //NOI18N
  val fcs = MimeLookup.getLookup(mimeType).lookup(classOf[FontColorSettings])

  if (fcs != null) {
    val javaBC = getColoring(fcs, RatsTokenId.ActionBody.primaryCategory)
    if (javaBC != null) {
      attribs = AttributesUtilities.createImmutable(
        StyleConstants.Background, javaBC,
        HighlightsContainer.ATTR_EXTENDS_EOL, Boolean.box(true))
    }
  }
  
  val javaBackground = attribs

  def  getHighlights(startOffset:Int, endOffset:Int) :HighlightsSequence = synchronized {
    if (javaBackground != null) {
      if (hierarchy == null) {
        hierarchy = TokenHierarchy.get(document)
        if (hierarchy != null) {
          hierarchy.addTokenHierarchyListener(WeakListeners.create(classOf[TokenHierarchyListener], this, hierarchy))
        }
      }

      if (hierarchy != null) {
        return new Highlights(version, hierarchy, startOffset, endOffset);
      }
    }
    return HighlightsSequence.EMPTY
    
  }

  // ----------------------------------------------------------------------
  //  TokenHierarchyListener implementation
  // ----------------------------------------------------------------------

  def tokenHierarchyChanged(evt:TokenHierarchyEvent) :Unit = {
    synchronized {
      version += 1
    }
    fireHighlightsChange(evt.affectedStartOffset, evt.affectedEndOffset)
  }
    
  // ----------------------------------------------------------------------
  //  Private implementation
  // ----------------------------------------------------------------------

  private class Highlights(version:Long, scanner:TokenHierarchy[_], startOffset:Int, endOffset:Int) extends HighlightsSequence {
    private var sequence:TokenSequence[_] = _
    private var sectionStart:Int = -1
    private var sectionEnd:Int = -1
    private var finished:Boolean = false

    def moveNext :Boolean = {
      EmbeddedSectionsHighlighting.this synchronized {
        if (checkVersion) {
          if (sequence == null) {
            if(!scanner.isActive()) {
              return false; //token hierarchy inactive already
            }
            sequence = scanner.tokenSequence;
            sequence.move(startOffset);
          }

          var delimiterSize = 0;
          while (sequence.moveNext && sequence.offset < endOffset) {
            sequence.token.id match {
              case RatsTokenId.Delimiter =>
                // opening delimiters can have different lenght
                delimiterSize = sequence.token.length
              case RatsTokenId.ActionBody =>
                sectionStart = sequence.offset
                sectionEnd = sequence.offset + sequence.token.length

                try {
                  val docLen = document.getLength()
                  val startLine = Utilities.getLineOffset(document.asInstanceOf[BaseDocument], Math.min(sectionStart, docLen))
                  val endLine = Utilities.getLineOffset(document.asInstanceOf[BaseDocument], Math.min(sectionEnd, docLen))

                  if (startLine != endLine) {
                    // multiline scriplet section
                    // adjust the sections start to the beginning of the firts line
                    val firstLineStartOffset = Utilities.getRowStartFromLineOffset(document.asInstanceOf[BaseDocument], startLine)
                    if (firstLineStartOffset < sectionStart - delimiterSize &&
                        isWhitespace(document, firstLineStartOffset, sectionStart - delimiterSize)) // always preceeded by the delimiter
                    {
                      sectionStart = firstLineStartOffset;
                    }

                    // adjust the sections end to the end of the last line
                    val lines = Utilities.getRowCount(document.asInstanceOf[BaseDocument]);
                    val lastLineEndOffset = if (endLine + 1 < lines) {
                      Utilities.getRowStartFromLineOffset(document.asInstanceOf[BaseDocument], endLine + 1)
                    } else {
                      document.getLength() + 1
                    }
                                    
                    if (sectionEnd + 2 >= lastLineEndOffset || // unclosed section
                        isWhitespace(document, sectionEnd + 2, lastLineEndOffset)) // always succeeded by '%>' hence +2
                    {
                      sectionEnd = lastLineEndOffset
                    }
                  }
                } catch {
                  case ble:BadLocationException => LOG.log(Level.WARNING, null, ble)
                }
                            
                return true
              case _ =>
            }
          }
        }
                
        sectionStart = -1
        sectionEnd = -1
        finished = true

        false
      }
    }

    def getStartOffset :Int = {
      EmbeddedSectionsHighlighting.this synchronized {
        if (finished) {
          throw new NoSuchElementException
        } else {
          assert(sequence != null, "Sequence not initialized, call moveNext() first.") //NOI18N
          Math.max(sectionStart, startOffset)
        }
      }
    }

    def getEndOffset :Int = {
      EmbeddedSectionsHighlighting.this synchronized {
        if (finished) {
          throw new NoSuchElementException
        } else {
          assert(sequence != null, "Sequence not initialized, call moveNext() first.") //NOI18N
          Math.min(sectionEnd, endOffset)
        }
      }
    }

    def getAttributes :AttributeSet = {
      EmbeddedSectionsHighlighting.this synchronized {
        if (finished) {
          throw new NoSuchElementException
        } else {
          assert(sequence != null, "Sequence not initialized, call moveNext() first.") //NOI18N
          javaBackground
        }
      }
    }
        
    private def checkVersion :Boolean = {
      this.version == EmbeddedSectionsHighlighting.this.version
    }
  } // End of Highlights class
    
}

class EmbeddingHighlightsLayerFactory extends HighlightsLayerFactory {
  def createLayers(context:HighlightsLayerFactory.Context) :Array[HighlightsLayer] = {
    Array(HighlightsLayer.create("rats-embedded-java-actions-highlighting-layer", //NOI18N
                                 ZOrder.BOTTOM_RACK.forPosition(100),
                                 true,
                                 new EmbeddedSectionsHighlighting(context.getDocument)))
  }
} // End of Factory class

