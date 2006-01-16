/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public Licensense is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.checkstyle;


import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionPerformer;
import org.netbeans.modules.tasklist.providers.SuggestionContext;

import org.openide.ErrorManager;

/** Handles remembering (through changes as well) the line and column of the violation.
 *
 * @author hair
 * @version $Id$
 */
public abstract class AbstractSuggestionPerformer implements SuggestionPerformer {

    protected final Document doc;

    protected final int lineno;
    
    protected String columnOnwards;
    
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
        final Element elm = getElement(doc, lineno -1  );
        if (elm == null) {
            ErrorManager.getDefault().log(ErrorManager.USER, "getElement was null");
            return;
        }
        final int offset = elm.getStartOffset();
        final int endOffset = elm.getEndOffset()-1;
        if( column >= 0 ){
            try {

                columnOnwards = doc.getText(offset + column -1, endOffset-(offset+column -1));

            } catch (BadLocationException ex) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
            }
        }
    }

    public void perform(final Suggestion suggestion) {
        
        final Element elm = getElement(doc, lineno -1  );
        if (elm == null) {
            ErrorManager.getDefault().log(ErrorManager.USER, "getElement was null");
            return;
        }
        final int offset = elm.getStartOffset();
        final int endOffset = elm.getEndOffset()-1;
        try {
            final String line = doc.getText(offset,endOffset-offset);
            final int idx = line.indexOf(columnOnwards);
            if( idx >= 0 ){
                performImpl(offset+idx);
            }else{
                ErrorManager.getDefault().log(ErrorManager.USER, "Lost position of violation, fix not performed.");
            }
            
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }
    
    protected abstract void performImpl(int docPosition) throws BadLocationException;

    /** Such a simple operation there's no need to ask for confirmation.
     * Also a little tricky to display whitespace being deleted!
     **/
    public Object getConfirmation(final Suggestion suggestion) {
        return null;
    }

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
        if (e == null) {
            // try default root (should work for text/plain)
            e = doc.getDefaultRootElement();
        }
        final Element elm = e.getElement(linenumber);
        return elm;
    }    
}
