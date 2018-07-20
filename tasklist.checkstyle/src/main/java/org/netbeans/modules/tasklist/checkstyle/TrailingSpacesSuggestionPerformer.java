/*
 * TrailingSpacesSuggestionPerformer.java
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
import javax.swing.text.StyledDocument;

import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionPerformer;
import org.netbeans.modules.tasklist.providers.SuggestionContext;

import org.openide.ErrorManager;

/** Removes Trailing spaces from line.
 *
 * @author hair
 */
public final class TrailingSpacesSuggestionPerformer extends AbstractSuggestionPerformer{
    
    /** Creates a new instance of TrailingSpacesSuggestionPerformer */
    TrailingSpacesSuggestionPerformer(
            final Document doc,
            final int lineno) {
        
        super(doc,lineno,0);
    }

    public void perform(final Suggestion suggestion) {
        
        final Element elm = getElement(doc, lineno-1);
        if (elm == null) {
            ErrorManager.getDefault().log(ErrorManager.USER, "getElement was null");
            return;
        }
        final int offset = elm.getStartOffset();
        final int endOffset = elm.getEndOffset()-2; // lines finished with '\n' & ''
        int wsStart = endOffset;
        try {
            while( doc.getText(wsStart,1).charAt(0) == ' ' ){
                --wsStart;
            }
            doc.remove(wsStart+1,endOffset-wsStart);
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }
   
    protected void performImpl(int docPosition) throws BadLocationException {
        throw new UnsupportedOperationException("TrailingSpacesSuggestionProvider overrides perform(..) directly.");
    }
}
