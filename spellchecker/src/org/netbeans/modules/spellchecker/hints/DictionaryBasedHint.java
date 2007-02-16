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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.spellchecker.hints;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.ErrorManager;
import org.openide.text.NbDocument;

/**
 *
 * @author Jan Lahoda
 */
public final class DictionaryBasedHint implements Fix {

    private String original;
    private Document doc;
    private String proposal;
    private Position[] span;
    
    public DictionaryBasedHint(String original, String proposal, Document doc, Position[] span) {
        this.original = original;
        this.doc = doc;
        this.proposal = proposal;
        this.span = span;
    }

    public String getText() {
        return "Change \"" + original + "\" to \"" + proposal + "\"";
    }

    public ChangeInfo implement() {
        try {
            NbDocument.runAtomicAsUser((StyledDocument) doc, new Runnable() {
                public void run() {
                    try {
                        doc.remove(span[0].getOffset(), span[1].getOffset() - span[0].getOffset());
                        doc.insertString(span[0].getOffset(), proposal, null);
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

}
