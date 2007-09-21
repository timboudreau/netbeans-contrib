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
package org.netbeans.modules.spellchecker.plain;

import java.util.regex.Pattern;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.openide.ErrorManager;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Lahoda
 */
public class PlainTokenList implements TokenList {

    private Document doc;
    private String currentWord;
    private int currentStartOffset;
    private int nextSearchOffset;

    /** Creates a new instance of JavaTokenList */
    public PlainTokenList(Document doc) {
        this.doc = doc;
    }

    
    public void setStartOffset(int offset) {
        currentWord = null;
        currentStartOffset = (-1);
        this.nextSearchOffset = offset;
    }

    public int getCurrentWordStartOffset() {
        return currentStartOffset;
    }

    public CharSequence getCurrentWordText() {
        return currentWord;
    }

    public boolean nextWord() {
        try {
            int offset = nextSearchOffset;
            boolean searching = true;

            while (offset < doc.getLength()) {
                String t = doc.getText(offset, 1);
                char c = t.charAt(0);

                if (searching) {
                    if (Character.isLetter(c)) {
                        searching = false;
                        currentStartOffset = offset;
                    }
                } else {
                    if (!Character.isLetter(c)) {
                        nextSearchOffset = offset;
                        currentWord = doc.getText(currentStartOffset, offset - currentStartOffset);
                        return true;
                    }
                }
                
                offset++;
            }

            nextSearchOffset = doc.getLength();

            if (searching) {
                return false;
            }
            currentWord = doc.getText(currentStartOffset, doc.getLength() - currentStartOffset);

            return true;
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    public void addChangeListener(ChangeListener l) {
    }

    public void removeChangeListener(ChangeListener l) {
    }

}
