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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command.parser;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.netbeans.api.lexer.Token;
import org.netbeans.modules.latex.editor.TexLanguage;
import org.netbeans.modules.latex.model.Utilities;

import org.netbeans.modules.latex.model.command.SourcePosition;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Lahoda
 */
public class ParserInput {

    private FileObject file;
    private TokenSequence ts;
    private Document document;
    private int index;
    private Set usedFiles;
    private final boolean firstMoveNextSucceeded;
    
    private Document getDocument(FileObject fo, Collection documents) throws IOException {
        Document ad = (Document) Utilities.getDefault().openDocument(fo);
        
        if (ad == null) {
            throw new IOException("Cannot open document for file: " + FileUtil.getFileDisplayName(fo));
        }
        
        documents.add(ad);
        
        return ad;
    }
    
    /** Creates a new instance of ParserInput */
    public ParserInput(FileObject file, Collection documents) throws IOException {
        Logger.getLogger("TIMER").log(Level.FINE, "ParserInput", new Object[] {file, this});
        assert file != null;
        this.file = file;
        document = getDocument(this.file, documents);
        
        if (document == null)
            throw new IOException("The document cannot be opened.");
        
        final String[] text = new String[1];
        
        document.render(new Runnable() {
            public void run() {
                try {
                    text[0] = document.getText(0, document.getLength());
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
        
        TokenHierarchy h = TokenHierarchy.create(text[0], TexLanguage.description());
        
        ts = h.tokenSequence();
        firstMoveNextSucceeded = ts.moveNext();
        usedFiles = new HashSet();
        usedFiles.add(file);
    }
    
    public SourcePosition getPosition() {
        int toUse = firstMoveNextSucceeded ? ts.offset() : 0;
        
        return new SourcePosition(file, document, toUse);
    }
    
    public int getIndex() {
        return index;
    }
    
    public Token getToken() throws IOException {
        return ts.token();
    }
    
    private Token nextImpl() {
        ts.moveNext();
        
        return ts.token();
    }
    
    public synchronized Token next() throws IOException {
        Token token = nextImpl();
        
        return token;
    }
    
    public boolean hasNext() {
        if (!firstMoveNextSucceeded)
            return false;
        
        if (!ts.moveNext())
            return false;
        
        ts.movePrevious();
        
        return true;
    }
    
    public Collection getUsedFiles() {
        return usedFiles;
    }
    
    public void goBack(int howMany) {
        while (howMany > 0) {
            howMany -= ts.token().length();
            if (!ts.movePrevious()) {
                return ;
            }
        }
    }
    
}
