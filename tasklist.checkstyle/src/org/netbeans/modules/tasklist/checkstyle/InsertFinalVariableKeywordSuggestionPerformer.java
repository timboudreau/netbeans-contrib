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
import javax.swing.text.Element;
import org.openide.ErrorManager;

/** TODO rename to InsertStringBeforeVariableDeclarationSuggestionPerformer and refactor accordingly.
 *
 * @version $Id$
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 */
public final class InsertFinalVariableKeywordSuggestionPerformer extends AbstractSuggestionPerformer{
    
    /** Creates a new instance of InsertSpaceSuggestionPerformer */
    public InsertFinalVariableKeywordSuggestionPerformer(
            final Document doc,
            final int lineno,
            final int column) {
        
        super(doc,lineno,column);
    }

    protected void performImpl(final int docPosition) throws BadLocationException {
        
        final Element elm = getElement(doc, lineno-1);
        if (elm == null) {
            ErrorManager.getDefault().log(ErrorManager.USER, "getElement was null");
            return;
        }

        int wsStart = docPosition - 2;
        try {
            // hit identifier first (covers arrays and generics).
            while( !Character.isJavaIdentifierPart( doc.getText(wsStart,1).charAt(0) ) ){
                --wsStart;
            }
            // look for beginning of identitifer
            while( Character.isJavaIdentifierStart( doc.getText(wsStart,1).charAt(0) ) ){
                --wsStart;
            }
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }        
        doc.insertString(wsStart+1 ,"final ",null); 
    }
    
}
