/*
 * DeleteLineSuggestionPerformer.java
 *
 * Created on December 30, 2005, 9:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.tasklist.checkstyle;


import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import org.netbeans.modules.tasklist.client.Suggestion;

import org.openide.ErrorManager;

/** Simple. Removes the whole line.
 *
 * @author hair
 */
public final class DeleteLineSuggestionPerformer extends AbstractSuggestionPerformer{
    
    /** Creates a new instance of TrailingSpacesSuggestionPerformer */
    DeleteLineSuggestionPerformer(
            final Document doc,
            final int lineno) {
        
        super(doc,lineno,-1);
    }

    public void perform(final Suggestion suggestion) {
        
        // check line hasn't changed already
        super.perform(suggestion);
        
        final Element elm = getElement(doc, lineno-1);
        if (elm == null) {
            ErrorManager.getDefault().log(ErrorManager.USER, "getElement was null");
            return;
        }
        final int offset = elm.getStartOffset();
        final int endOffset = elm.getEndOffset();
        try {
            doc.remove(offset,endOffset-offset);
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }

    /** Such a simple operation there's no need to ask for confirmation.
     * Also a little tricky to display whitespace being deleted!
     **/
    public Object getConfirmation(final Suggestion suggestion) {
        return null;
    }

    public boolean hasConfirmation() {
        return false;
    }

    protected void performImpl(int docPosition) throws BadLocationException {
    }

}
