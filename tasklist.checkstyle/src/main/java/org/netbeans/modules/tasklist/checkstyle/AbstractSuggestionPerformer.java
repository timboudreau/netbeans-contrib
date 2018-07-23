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

package org.netbeans.modules.tasklist.checkstyle;


import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionPerformer;

import org.openide.ErrorManager;

/** Handles remembering (through changes as well) the line and column of the violation.
 *
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 * @version $Id$
 */
public abstract class AbstractSuggestionPerformer implements SuggestionPerformer {

    /** TODO comment me **/
    protected final Document doc;

    /** TODO comment me **/
    protected final int lineno;

    /** TODO comment me **/
    protected String columnOnwards;
    
    protected String originalLine;

    /**
     * Creates a new instance of AbstractSuggestionPerformer
     */
    AbstractSuggestionPerformer(
            final Document doc,
            final int lineno,
            final int column) {

        this.doc = doc;
        this.lineno = lineno;

        // instead of remembering the column remember the string from the column to the end of the line.
        final Element elm = getElement(doc, lineno -  1);
        if (elm == null) {
            ErrorManager.getDefault().log(ErrorManager.USER, "getElement was null");
            return;
        }
        final int offset = elm.getStartOffset();
        final int endOffset = elm.getEndOffset() - 1;
        final int columnOffset = offset + Math.max(0, column)  -   1;
        try {

            originalLine = doc.getText(columnOffset, endOffset);
            columnOnwards = doc.getText(columnOffset, endOffset - columnOffset);

        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }

    /** TODO comment me **/
    public void perform(final Suggestion suggestion)    {

        if (!performOnAdjacentLine(lineno, false) && !performOnAdjacentLine(lineno, true)) {

            ErrorManager.getDefault().log(ErrorManager.USER,
                                    "Lost position of violation, no fix performed");
        }
    }

    /** TODO comment me **/
    protected abstract void performImpl(int docPosition) throws BadLocationException;

    /** Such a simple operation there's no need to ask for confirmation.
     * Also a little tricky to display whitespace being deleted!
     **/
    public Object getConfirmation(final Suggestion suggestion) {
        return null;
    }

    /** TODO comment me **/
    public boolean hasConfirmation() {
        return false;
    }

    /** copied from ChangeCopyrightDatesPerformer **/
    protected final static Element getElement(final Document d, final int linenumber) {
        if (d == null) {
            ErrorManager.getDefault().log(ErrorManager.USER, "d was null");
            return null;
        }

        if (!(d instanceof StyledDocument)) {
            ErrorManager.getDefault().log(ErrorManager.USER, "Not a styleddocument");
            return null;
        }

        final StyledDocument doc = (StyledDocument) d;
        Element e = doc.getParagraphElement(0).getParentElement();
        if (e == null)  {
            // try default root (should work for text/plain)
            e = doc.getDefaultRootElement();
        }
        final Element elm = e.getElement(linenumber);
        return elm;
    }

    /** TODO comment me **/
    private boolean performOnAdjacentLine(final int lNumber, final boolean incrementLine)   {

        boolean result = false;
        if (lNumber > 0)  {
            final Element elm = getElement(doc, lNumber - 1);
            if (elm == null)     {
                ErrorManager.getDefault().log(ErrorManager.USER, "getElement was null");

            }      else  {
                final int offset = elm.getStartOffset();
                final int endOffset = elm.getEndOffset() - 1;
                try    {
                    final String line = doc.getText(offset, endOffset - offset);
                    final int idx = line.indexOf(columnOnwards);
                    if (line.equals(originalLine) && idx >= 0)    {
                        performImpl(offset + idx);
                        result = true;

                    }  else  {
                        // try preceding line
                        result = performOnAdjacentLine(incrementLine ? lNumber + 1 : lNumber - 1, incrementLine);
                    }
                } catch (BadLocationException ex) {
                    ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
                }
            }
        }
        return result;
    }
}
