/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
