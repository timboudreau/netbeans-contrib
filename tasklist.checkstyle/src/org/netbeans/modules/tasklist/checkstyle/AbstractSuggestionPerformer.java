/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
