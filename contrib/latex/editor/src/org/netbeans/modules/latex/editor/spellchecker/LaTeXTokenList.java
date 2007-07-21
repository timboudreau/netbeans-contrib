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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.latex.model.command.ArgumentContainingNode;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.model.lexer.TexTokenId;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.WeakSet;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXTokenList implements TokenList {
    
    private Document doc;
    private TokenSequence ts;
    
    private Set<Token> bannedTokens = new WeakSet<Token>();
    
    /** Creates a new instance of LaTeXTokenList */
    public LaTeXTokenList(Document doc) {
        this.doc = doc;
//        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(doc));
        
//        source.addDocumentChangedListener(this);
    }

    public void setStartOffset(int i) {
        ts = TokenHierarchy.get(doc).tokenSequence();
        ts.move(i);
        ts.movePrevious();
    }

    public boolean nextWord() {
//        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(doc));
        
        boolean expensiveCheck = false;//!(source == null || !source.isUpToDate() || source.getDocument() == null);
        
        while (ts.moveNext()) {
            Token t = ts.token();
            
            if (t.id() == TexTokenId.WORD) {
                if (expensiveCheck) {
                    if (accept()) {
                        bannedTokens.remove(t);
                        return true;
                    } else {
                        bannedTokens.add(t);
                    }
                } else {
                    if (!bannedTokens.contains(t)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    private boolean accept() {
//        try {
            //no spelling for tokens inside the math mode (temporary, until MathNode is created in structure):
            //TODO:
//            if (TokenAttributes.isInMathToken(token))
//                return false;

            int  offset = ts.offset();
            Node node  = null;//source.findNode(doc, offset);  XXX
            
            if (node != null) {
                if (node instanceof ArgumentNode) {
                    ArgumentNode anode = (ArgumentNode) node;
                    
                    if (anode.getArgument().isEnumerable()) {
                        return false;
                    } else {
                        ArgumentContainingNode cnode = anode.getCommand();
                        
                        if (cnode instanceof CommandNode && cnode.getParent() instanceof BlockNode) {
                            return false;
                        } else {
                            return !anode.getArgument().isCodeLike();
                        }
                    }
                }
            }
//        } catch (IOException e) {
//            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
//        }
        
        return true;
    }

    public int getCurrentWordStartOffset() {
        return ts.offset();
    }

    public CharSequence getCurrentWordText() {
        return ts.token().text();
    }
    
    private FileObject getFile(Document doc) {
        DataObject d = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
        
        if (d == null)
            return null;
        
        return d.getPrimaryFile();
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
    
}
