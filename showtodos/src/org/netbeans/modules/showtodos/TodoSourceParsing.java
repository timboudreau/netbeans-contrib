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
package org.netbeans.modules.showtodos;

import java.util.LinkedList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.FinderFactory;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.tasklist.todo.settings.Settings;
import org.openide.text.Annotatable;
import org.openide.text.Line;
import org.openide.util.Exceptions;

/**
 *
 * @author Michel Graciano
 */
final class TodoSourceParsing {
   private TodoSourceParsing() {
   }

   static List<int[]> parse(final BaseDocument doc, int startOffset,
         int endOffset) {
      List<int[]> pairsPosition = new LinkedList<int[]>();
      final TokenHierarchy th = TokenHierarchy.get(doc);

      if (th != null && th.isActive()) {
         TokenSequence ts = th.tokenSequence();
         try {
            startOffset = Utilities.getRowStart(doc, startOffset);
            endOffset = Math.min(doc.getLength(), endOffset);

            for (String word : Settings.getDefault().getPatterns()) {
               int pos = startOffset;

               // Search from pos to endPos for TODO markers.
               while (pos < endOffset) {
                  FinderFactory.WholeWordsFwdFinder finder =
                        new FinderFactory.WholeWordsFwdFinder(doc, word, true);
                  int next = doc.find(finder, pos, endOffset);

                  if ((next >= startOffset) && (next < endOffset)) {
                     // See if it looks like a token we care about (comments)
                     if (ts != null && ts.isValid()) {
                        ts.move(next);

                        if (ts.moveNext()) {
                           Token token = ts.token();
                           pos = Math.min(Utilities.getRowEnd(doc, next),
                                 ts.offset() + token.length());

                           if (token != null) {
                              String category = token.id().primaryCategory();
                              final boolean commentsOnly = Settings.getDefault().
                                    isScanCommentsOnly();
                              final boolean valid = !commentsOnly || ("comment".
                                    equals(category) && commentsOnly); // NOI18N
                              if (valid) {
                                 pairsPosition.add(new int[] {next, pos});
                              }
                           }
                        } else {
                           pos = next + word.length();
                        }
                     } else {
                        pos = next + word.length();
                     }
                  } else {
                     break;
                  }
               }
            }
         } catch (BadLocationException e) {
            Exceptions.printStackTrace(e);
         }
      }
      return pairsPosition;
   }

   static List<Annotatable> parseForAnnotatable(final BaseDocument doc) throws BadLocationException {
      final List<Annotatable> lines = new LinkedList<Annotatable>();
      for (int[] pairs : parse(doc, doc.getStartPosition().getOffset(), doc.
            getEndPosition().getOffset())) {
         final String text = doc.getText(pairs[0], pairs[1] - pairs[0]);
         final Line line = NbEditorUtilities.getLine((Document)doc, pairs[1],
               false);
         final int initialIndex = line.getText().indexOf(text);
         lines.add(line.createPart(initialIndex, text.length()));
      }
      return lines;
   }
}
