/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in 
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.i18n;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.tasklist.client.SuggestionPerformer;
import org.netbeans.modules.tasklist.core.TLUtils;
import org.openide.ErrorManager;
import org.openide.text.Line;
import org.openide.util.NbBundle;

/**
 * Inserts // NOI18N
 *
 * @author Tim Lebedkov
 */
public class AddI18NCommentPerformer implements SuggestionPerformer {
    /** 
     * Creates a new instance of AddI18NCommentPerformer 
     */
    public AddI18NCommentPerformer() {
    }
    
    public Object getConfirmation(org.netbeans.modules.tasklist.client.Suggestion suggestion) {
        return NbBundle.getMessage(AddI18NCommentPerformer.class, "Confirmation"); // NOI18N
    }
    
    public void perform(org.netbeans.modules.tasklist.client.Suggestion suggestion) {
        Line line = suggestion.getLine();
        
        Document doc = TLUtils.getDocument(line);
        if (doc == null)
            return;
        
        javax.swing.text.Element el = TLUtils.getElement(doc, line);
        if (el == null)
            return;
        
        try {
            doc.insertString(el.getEndOffset() - 1, " // NOI18N", null); // NOI18N
        } catch (BadLocationException ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }
    
    public boolean hasConfirmation() {
        return true;
    }
}
