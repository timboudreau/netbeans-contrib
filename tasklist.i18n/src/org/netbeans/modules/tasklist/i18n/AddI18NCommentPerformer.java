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
 * @author tl
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
