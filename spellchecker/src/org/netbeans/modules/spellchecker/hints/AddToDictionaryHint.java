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

import org.netbeans.modules.spellchecker.ComponentPeer;
import org.netbeans.modules.spellchecker.DictionaryImpl;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.EnhancedFix;

/**
 *
 * @author Jan Lahoda
 */
public final class AddToDictionaryHint implements EnhancedFix {

    private DictionaryImpl d;
    private String         word;
    private String         text;
    private ComponentPeer  peer;
    private String         sortText;

    public AddToDictionaryHint(ComponentPeer peer, DictionaryImpl d, String word, String text, String sortText) {
        this.peer = peer;
        this.d = d;
        this.word = word;
        this.text = text;
        this.sortText = sortText;
    }
    
    public String getText() {
        return String.format(text, word);
    }

    public ChangeInfo implement() {
        d.addEntry(word);
	peer.reschedule();
        
	return null;
    }

    public CharSequence getSortText() {
        return sortText;
    }

}
