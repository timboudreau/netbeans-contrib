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
package org.netbeans.modules.php.fuse.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.php.fuse.utils.EditorUtils;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

/**
 *
 * @author cawe
 */
public class TmplCompletionProvider implements CompletionProvider {

    public CompletionTask createTask(int queryType, final JTextComponent component) {
        return new AsyncCompletionTask(new AsyncCompletionQuery() {

            protected void query(final CompletionResultSet resultSet, Document doc, final int caretOffset) {
                final StyledDocument bDoc = (StyledDocument) doc;
                class Operation implements Runnable {

                    String filter = null;
                    int startOffset = caretOffset - 1;

                    public void run() {
                        try {
                            final int lineStartOffset = EditorUtils.getRowFirstNonWhite(bDoc, caretOffset);
                            startOffset = lineStartOffset;
                            if (lineStartOffset > -1 && caretOffset > lineStartOffset) {
                                final char[] line = bDoc.getText(lineStartOffset, caretOffset - lineStartOffset).toCharArray();
                                final int whiteOffset = EditorUtils.indexOfWhite(line);
                                filter = new String(line, whiteOffset + 1, line.length - whiteOffset - 1);
                            }
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                        // Here we use the filter, if it's not null:
                        if (filter != null) {
                            final Iterator it = keywords.iterator();
                            while (it.hasNext()) {
                                final String entry = (String) it.next();
                                if (entry.startsWith(filter)) {
                                    resultSet.addItem(new TmplCompletionItem(entry, startOffset, caretOffset));
                                }
                            }
                        } else {
                            final Iterator it = keywords.iterator();
                            while (it.hasNext()) {
                                final String entry = (String) it.next();
                                resultSet.addItem(new TmplCompletionItem(entry, startOffset, caretOffset));
                            }
                        }
                        resultSet.setAnchorOffset(caretOffset);
                        resultSet.finish();
                    }
                }
                Operation oper = new Operation();
                bDoc.render(oper);
            }
        }, component);
    }

    public int getAutoQueryTypes(JTextComponent component,
            String typedText) {
        return 0;
    }
    private final static List keywords = new ArrayList();

    static {
        keywords.add("IF");
        keywords.add("ELSE");
        keywords.add("/IF");
        keywords.add("WHILE");
        keywords.add("/WHILE");
        keywords.add("ITERATOR");
        keywords.add("/ITERATOR");
        keywords.add("LOOP");
        keywords.add("/LOOP");
        keywords.add("DB_LOOP");
        keywords.add("/DB_LOOP");

    }
}



