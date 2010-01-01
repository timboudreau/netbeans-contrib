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
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.openide.util.Exceptions;

/**
 *
 * @author Martin Fousek
 */
public class TmplCompletionQuery extends AsyncCompletionQuery {

    /**
     * intern type for code completion query
     */
    protected int type;
    /**
     * in which component is query created
     */
    protected JTextComponent component;
    /**
     * if the query is for inner or outer code of Fuse delimiters
     */
    protected QueryType queryType;

    public TmplCompletionQuery(JTextComponent component, int type, QueryType queryType) {
        this.type = type;
        this.component = component;
        this.queryType = queryType; 
    }

    @Override
    protected void query(final CompletionResultSet resultSet, final Document doc, final int caretOffset) {
        final StyledDocument bDoc = (StyledDocument) doc;
        class Operation implements Runnable {

            String filter = null;
            int startOffset = caretOffset;

            public void run() {
                try {
                    final int lineStartOffset = EditorUtils.getRowFirstNonWhite(bDoc, caretOffset);
                    if (lineStartOffset > -1 && caretOffset > lineStartOffset) {
                        final char[] line = bDoc.getText(lineStartOffset, caretOffset - lineStartOffset).toCharArray();
                        final int whiteOffset = EditorUtils.indexOfWhite(line);
                        filter = new String(line, whiteOffset + 1, line.length - whiteOffset - 1);
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                // taking appropriate iterator
                final Iterator it;
                if (queryType == QueryType.INNER_QUERY_TASK) {
                    ArrayList<String> variables = EditorUtils.getKeywordsForView(doc);
                    variables.addAll(innerKeywords);
                    it = variables.iterator();
                }
                else {
                    it = outerKeywords.iterator();
                    try {
                        filter = new String(bDoc.getText(startOffset-1, 1).toCharArray());
                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    if (filter.equals(">")) {
                        filter = null;
                    }
                    else if (filter.equals("<")) {
                        filter = "<";
                    }
                }
                // Here we use the filter, if it's not null:
                if (filter != null) {
                    while (it.hasNext()) {
                        final String entry = (String) it.next();
                        if (entry.startsWith(filter)) {
                            resultSet.addItem(new TmplCompletionItem(entry, startOffset - filter.length(), caretOffset));
                        }
                    }
                } else {
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

    private final static List<String> innerKeywords = new ArrayList<String>();

    static {
        innerKeywords.add("IF");
        innerKeywords.add("ELSE");
        innerKeywords.add("/IF");
        innerKeywords.add("WHILE");
        innerKeywords.add("/WHILE");
        innerKeywords.add("ITERATOR");
        innerKeywords.add("/ITERATOR");
        innerKeywords.add("LOOP");
        innerKeywords.add("/LOOP");
        innerKeywords.add("DB_LOOP");
        innerKeywords.add("/DB_LOOP");
    }

    private final static List<String> outerKeywords = new ArrayList<String>();

    static {
        outerKeywords.add("<{");
    }

    /**
     * Types of queries. 
     */
    public static enum QueryType  {
        /**
         * Query for code in Fuse delimiters.
         */
        INNER_QUERY_TASK,
        /**
         * Query for code within Fuse delimiters.
         */
        OUTER_QUERY_TASK
    }
}
