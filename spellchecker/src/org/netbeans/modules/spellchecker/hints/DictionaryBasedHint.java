/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
