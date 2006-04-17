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
package org.netbeans.modules.latex.editor.spellchecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.latex.editor.TexLanguage;
import org.netbeans.modules.latex.editor.TokenAttributes;
import org.netbeans.modules.latex.editor.Utilities;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.LaTeXSource;
import org.netbeans.modules.latex.model.command.LaTeXSource.DocumentChangeEvent;
import org.netbeans.modules.latex.model.command.LaTeXSource.DocumentChangedListener;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.WeakSet;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXTokenList implements TokenList, DocumentChangedListener {
    
    private Document doc;
    private int currentOffset;
    private Token currentToken;
    
    private Set<Token> bannedTokens = new WeakSet();
    
    /** Creates a new instance of LaTeXTokenList */
    public LaTeXTokenList(Document doc) {
        this.doc = doc;
        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(doc));
        
        source.addDocumentChangedListener(this);
    }

    public void setStartOffset(int i) {
        this.currentOffset = i;
        this.currentToken = null;
    }

    public boolean nextWord() {
        LaTeXSource source = LaTeXSource.get(org.netbeans.modules.latex.model.Utilities.getDefault().getFile(doc));
        
        boolean expensiveCheck = !(source == null || !source.isUpToDate() || source.getDocument() == null);
        
        int offset = currentOffset;
        
        if (currentToken != null) {
            offset = Utilities.getTokenOffset(doc, currentToken) + currentToken.getText().length() + 1;
        }
        
        while (offset < doc.getLength()) {
            Token t = Utilities.getToken(doc, offset);
            
            if (t.getId() == TexLanguage.WORD) {
                if (expensiveCheck) {
                    if (accept(source, t)) {
                        currentToken = t;
                        bannedTokens.remove(t);
                        return true;
                    } else {
                        bannedTokens.add(t);
                    }
                } else {
                    if (!bannedTokens.contains(t)) {
                        currentToken = t;
                        return true;
                    }
                }
            }
            
            offset = Utilities.getTokenOffset(doc, t) + t.getText().length() + 1;
        }
        
        return false;
    }
    
    private boolean accept(LaTeXSource source, Token token) {
        try {
            //no spelling for tokens inside the math mode (temporary, until MathNode is created in structure):
            if (TokenAttributes.isInMathToken(token))
                return false;

            int  offset = Utilities.getTokenOffset(doc, token);
            Node node  = source.findNode(doc, offset);
            
            if (node != null) {
                if (node instanceof ArgumentNode) {
                    ArgumentNode anode = (ArgumentNode) node;
                    
                    if (anode.getArgument().isEnumerable()) {
                        return false;
                    } else {
                        CommandNode cnode = anode.getCommand();
                        
                        Node parent = cnode.getParent();
                        
                        if (parent instanceof BlockNode) {
                            return false;
                        } else {
                            return !anode.getArgument().isCodeLike();
                        }
                    }
                }
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        
        return true;
    }

    public int getCurrentWordStartOffset() {
        return Utilities.getTokenOffset(doc, currentToken);
    }

    public CharSequence getCurrentWordText() {
        return currentToken.getText();
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
    public void nodesAdded(DocumentChangeEvent evt) {
        fireChangeEvent();
    }

    public void nodesRemoved(DocumentChangeEvent evt) {
        fireChangeEvent();
    }

    public void nodesChanged(DocumentChangeEvent evt) {
        fireChangeEvent();
    }

    public synchronized void addChangeListener(ChangeListener changeListener) {
        listeners.add(changeListener);
    }

    public synchronized void removeChangeListener(ChangeListener changeListener) {
        listeners.remove(changeListener);
    }
    
}
