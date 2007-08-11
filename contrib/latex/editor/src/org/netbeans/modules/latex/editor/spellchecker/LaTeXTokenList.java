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
package org.netbeans.modules.latex.editor.spellchecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.latex.model.lexer.TexTokenId;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.openide.util.WeakSet;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXTokenList implements TokenList {
    
    private Document doc;
    private TokenSequence ts;
    
    private Set<Token> acceptedTokens = new WeakSet<Token>();
    
    /** Creates a new instance of LaTeXTokenList */
    public LaTeXTokenList(Document doc) {
        this.doc = doc;
    }

    public void setStartOffset(int i) {
        ts = TokenHierarchy.get(doc).tokenSequence();
        ts.move(i);
    }

    public synchronized boolean nextWord() {
        if (acceptedTokens.isEmpty())
            return false;
        
        while (ts.moveNext()) {
            Token t = ts.token();
            
            if (t.id() == TexTokenId.WORD) {
                if (acceptedTokens.contains(t))
                    return true;
            }
        }
        
        return false;
    }
    
    public int getCurrentWordStartOffset() {
        return ts.offset();
    }

    public CharSequence getCurrentWordText() {
        return ts.token().text();
    }
    
    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
    
    private void fireChangeEvent() {
        List<ChangeListener> copy;
        
        synchronized (this) {
            copy = new ArrayList<ChangeListener>(listeners);
        }
        
        ChangeEvent e = new ChangeEvent(this);
        
        for (ChangeListener l : copy) {
            l.stateChanged(e);
        }
    }

    public synchronized void addChangeListener(ChangeListener changeListener) {
        listeners.add(changeListener);
    }

    public synchronized void removeChangeListener(ChangeListener changeListener) {
        listeners.remove(changeListener);
    }
    
    void setAcceptedTokens(Set<Token> acceptedTokens) {
        synchronized (this) {
            this.acceptedTokens = acceptedTokens;
        }
        fireChangeEvent();
    }
    
}
