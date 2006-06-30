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
package org.netbeans.modules.spellchecker.hints;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.editor.highlights.spi.Highlight;
import org.netbeans.modules.editor.hints.spi.Hint;
import org.netbeans.modules.editor.hints.spi.ChangeInfo;
import org.openide.ErrorManager;
import org.openide.text.NbDocument;

/**
 *
 * @author Jan Lahoda
 */
public class DictionaryBasedHint extends Hint {

    private String original;
    private Document doc;
    private String proposal;
    private Highlight bounds;
    
    /**
     * Creates a new instance of DictionaryBasedHint 
     */
    public DictionaryBasedHint(String original, String proposal, Document doc, Highlight bounds) {
        this.original = original;
        this.doc = doc;
        this.proposal = proposal;
        this.bounds = bounds;
    }

    public String getText() {
        return "Change \"" + original + "\" to \"" + proposal + "\"";
    }

    public ChangeInfo implement() {
        try {
            NbDocument.runAtomicAsUser((StyledDocument) doc, new Runnable() {
                public void run() {
                    try {
                        doc.remove(bounds.getStart(), bounds.getEnd() - bounds.getStart());
                        doc.insertString(bounds.getStart(), proposal, null);
                    } catch (BadLocationException e) {
                        ErrorManager.getDefault().notify(e);
                    }
                }
            });
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
        }
	
	return null;
    }

    public int getType() {
        return ERROR;
    }
    
}
