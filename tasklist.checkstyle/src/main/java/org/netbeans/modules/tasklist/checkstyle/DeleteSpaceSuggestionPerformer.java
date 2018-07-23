/* 
 *
 * InsertSpaceSuggestionPerformer.java
 *
 * Created on 8 January 2006, 22:28
 *
 */

package org.netbeans.modules.tasklist.checkstyle;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * @version $Id$
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 */
public final class DeleteSpaceSuggestionPerformer extends AbstractSuggestionPerformer{
    
    /** Creates a new instance of InsertSpaceSuggestionPerformer */
    public DeleteSpaceSuggestionPerformer(
            final Document doc,
            final int lineno,
            final int column) {
        super(doc,lineno,column);
    }

    protected void performImpl(final int docPosition) throws BadLocationException {
        while( doc.getText(docPosition,1).charAt(0) == ' ' ){
            doc.remove(docPosition,1);
        }
    }
    
}
