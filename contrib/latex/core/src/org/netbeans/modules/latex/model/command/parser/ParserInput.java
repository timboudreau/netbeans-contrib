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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command.parser;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;

import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;

import org.netbeans.api.lexer.Token;
import org.netbeans.modules.latex.editor.TexLanguage;
import org.netbeans.modules.latex.model.Utilities;

import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.lexer.editorbridge.TokenRootElement;

/**
 *
 * @author Jan Lahoda
 */
public class ParserInput implements DocumentListener {

    private boolean changed;
    
    private FileObject file;
    private TokenRootElement currentTRE;
    private Document document;
    private int index;
    private Set usedFiles;
    
    private Document getDocument(FileObject fo, Collection documents) throws IOException {
        changed = false;
        
        Document ad = (Document) Utilities.getDefault().openDocument(fo);
        
        if (ad == null) {
            throw new IOException("Cannot open document for file: " + FileUtil.getFileDisplayName(fo));
        }
        
        ad.addDocumentListener(this);
        
        documents.add(ad);
        
        return ad;
    }
    
    private TokenRootElement getTRE(Document doc) {
        //TODO:assure this is correct way to do it. (it is not, but...)
        return org.netbeans.modules.latex.editor.Utilities.getTREImpl(doc);
    }

    /** Creates a new instance of ParserInput */
    public ParserInput(FileObject file, Collection documents) throws IOException {
        assert file != null;
        this.file = file;
        document = getDocument(this.file, documents);
        
        if (document == null)
            throw new IOException("The document cannot be opened.");
        
        currentTRE = getTRE(document);
        usedFiles = new HashSet();
        usedFiles.add(file);
    }
    
    public SourcePosition getPosition() {
        if (changed)
            throw new ParsingAbortedException();

        int toUse = index > 0 ? index : 0;
        int offset = currentTRE.getElementOffset(toUse);
        
        return new SourcePosition(file, document, offset);
    }
    
    public int getIndex() {
        if (changed)
            throw new ParsingAbortedException();
        
        return index;
    }
    
    public Token getToken() throws IOException {
        if (changed)
            throw new ParsingAbortedException();
        
        return (Token) currentTRE.getElement(index);
    }
    
    private Token nextImpl() {
        Token current = (Token) currentTRE.getElement(++index);
        
        return current;
    }
    
    public synchronized Token next() throws IOException {
        if (changed)
            throw new ParsingAbortedException();
        
        Token token = nextImpl();
        
        return token;
    }
    
    public boolean hasNext() {
        if (changed)
            throw new ParsingAbortedException();

        return /*treStack.size() != 0 || */(index + 1) < currentTRE.getElementCount();
    }
    
    public Collection getUsedFiles() {
        return usedFiles;
    }
    
    public void changedUpdate(DocumentEvent e) {
    }
    
    public synchronized void insertUpdate(DocumentEvent e) {
        changed = true;
    }
    
    public synchronized void removeUpdate(DocumentEvent e) {
        changed = true;
    }
    
}
