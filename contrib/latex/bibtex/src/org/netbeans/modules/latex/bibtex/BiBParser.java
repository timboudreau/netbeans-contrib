/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex;

import java.io.IOException;
import java.util.Stack;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.latex.editor.Utilities;
import org.netbeans.modules.latex.editor.bibtex.BiBTeXLanguage;

import org.netbeans.modules.latex.model.bibtex.Entry;
import org.netbeans.modules.latex.model.bibtex.FreeFormEntry;
import org.netbeans.modules.latex.model.bibtex.PublicationEntry;
import org.netbeans.modules.latex.model.bibtex.StringEntry;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.lexer.editorbridge.TokenRootElement;

/**
 *
 * @author Jan Lahoda
 */
public class BiBParser {
    
    /** Creates a new instance of BiBTest */
    public BiBParser() {
    }
    
    private static final String STRING_TYPE = "STRING";
    private static final String COMMENT_TYPE = "COMMENT";
    private static final String PREAMBLE_TYPE = "PREAMBLE";
    
    private Document source;
    private int index;
    
    private void setDocument(Document source, int offset) {
        this.source = source;
        index = Utilities.getTokenIndex(source, offset);
    }
    
    private Token getNextToken() {
        return Utilities.getTokenForIndex(source, index++);
    }
    
    private SourcePosition getCurrentPosition() {
        return new SourcePosition(source, Utilities.getTREImpl(source).getElementOffset(index - 1));
    }

    
    private boolean isEOF() {
        return index >= Utilities.getTREImpl(source).getElementCount();
    }
    
    public Entry parseEntry(Document doc, int startOffset) throws IOException {
        setDocument(doc, startOffset);
        
        return parseEntry();
    }
    
    private boolean isStopParsingInsideEntryToken(Token token) {
        return token.getId() == BiBTeXLanguage.TYPE;
    }
    
    private Entry parseEntry() throws IOException {
        Token token = null;
        
        while (!isEOF() && (token = getNextToken()).getId() != BiBTeXLanguage.TYPE)
            ;
        
        if (isEOF())
            return null;
        
        SourcePosition start = getCurrentPosition();
        
        String type = getTypeString(token);
        Entry result;
        
        if (STRING_TYPE.equalsIgnoreCase(type)) {
            result = parseStringEntry();
        } else {
            if (COMMENT_TYPE.equalsIgnoreCase(type) || PREAMBLE_TYPE.equalsIgnoreCase(type)) {
                result = parseFreeFormEntry(type);
            } else {
                result = parsePublicationEntry(type);
            }
        }
        
        if (result != null) {
            result.setStartPosition(start);
            result.setEndPosition(getCurrentPosition());
        }
        
        return result;
    }
    
    private String getTypeString(Token token) {
        String text = token.getText().toString();
                
        assert BiBTeXLanguage.TYPE == token.getId() && text.length() > 0;
        assert text.charAt(0) == '@';
        
        return text.substring(1);
    }
    
    private Entry parseStringEntry() throws IOException {
        StringEntry entry = new StringEntry();
        Token token = null;
        
        while (!isEOF() && (token = getNextToken()).getId() != BiBTeXLanguage.TEXT && !isStopParsingInsideEntryToken(token) && token.getId() != BiBTeXLanguage.CL_BRAC)
            ;
        
        if (isEOF())
            return entry;
        
        if (token.getId() != BiBTeXLanguage.TEXT)
            return entry;
        
        String key = token.getText().toString();
        
        while (!isEOF() && (token = getNextToken()).getId() != BiBTeXLanguage.EQUALS && !isStopParsingInsideEntryToken(token))
            ;
        
        if (isEOF() || isStopParsingInsideEntryToken(token))
            return null;

        //TODO: this is not finished !
        entry.setKey(key);
        entry.setValue(/*content.toString()*/"<incorrect value>");//TODO: this is not correct at all...
        
        return entry;
    }

    private Entry parseFreeFormEntry(String type) throws IOException {
        FreeFormEntry entry = new FreeFormEntry();
        Token token;
        
        entry.setType(type);
        
        Stack bracketStack = new Stack();
        
        while ((token = getNextToken()).getId() != BiBTeXLanguage.OP_BRAC)
            ;
        
        bracketStack.push(token.getText().charAt(0) == '{' ? "{" : "(");
        
        while (!bracketStack.isEmpty()) {
            token = getNextToken();
            
            if (token.getId() == BiBTeXLanguage.OP_BRAC)
                bracketStack.push(token.getText().charAt(0) == '{' ? "{" : "(");
            
            if (token.getId() == BiBTeXLanguage.CL_BRAC)
                bracketStack.pop(); //TODO: check the type of the bracket!
        }
        
        return entry;
    }

    private Entry parsePublicationEntry(String type) throws IOException {
        PublicationEntry entry = new PublicationEntry();
        Token token = null;
        
        entry.setType(type.toUpperCase()); //TODO: is this correct and wanted behaviour?
        
        while (!isEOF() && (token = getNextToken()).getId() != BiBTeXLanguage.OP_BRAC && !isStopParsingInsideEntryToken(token))
            ;
        
        if (isEOF() || isStopParsingInsideEntryToken(token))
            return null;
        
        while (!isEOF() && (token = getNextToken()).getId() != BiBTeXLanguage.TEXT && !isStopParsingInsideEntryToken(token))
            ;
        
        if (isEOF() || isStopParsingInsideEntryToken(token))
            return null;
        
        entry.setTag(token.getText().toString());
        
        while (parseMap(entry))
            ;
        
        return entry;
    }
    
    private boolean parseMap(PublicationEntry entry) throws IOException {
        Token token = null;
        
        while (!isEOF() && (token = getNextToken()).getId() != BiBTeXLanguage.TEXT && !isStopParsingInsideEntryToken(token) && token.getId() != BiBTeXLanguage.CL_BRAC)
            ;
        
        if (isEOF())
            return false;
        
        if (token.getId() != BiBTeXLanguage.TEXT)
            return false;
        
        String key = token.getText().toString();
        
        while (!isEOF() && (token = getNextToken()).getId() != BiBTeXLanguage.EQUALS && !isStopParsingInsideEntryToken(token))
            ;
        
        if (isStopParsingInsideEntryToken(token))
            return false;

        
        return readValueString(entry, key);
    }
    
    private void appendContent(StringBuffer buffer, Token token) {
        String text = token.getText().toString();
        
        if (BiBTeXLanguage.STRING == token.getId() && text.length() > 0) {
            int start = text.charAt(0) == '"' ? 1 : 0;
            int end = text.length() - (text.charAt(text.length() - 1) == '"' ? 1 : 0);
            
            text = text.substring(start, end);
        }
        
        buffer.append(text);
    }
    
    private boolean readValueString(PublicationEntry entry, String key) throws IOException {
        StringBuffer result = new StringBuffer();
        Token token = null;
        
        while (!isEOF() && (token = getNextToken()).getId() != BiBTeXLanguage.COMMA && !isStopParsingInsideEntryToken(token) && token.getId() != BiBTeXLanguage.CL_BRAC) {
            if (token.getId() == BiBTeXLanguage.OP_BRAC) {//TODO: balanced brackets:
                while ((token = getNextToken()).getId() != BiBTeXLanguage.CL_BRAC && !isStopParsingInsideEntryToken(token)) {
                    appendContent(result, token);
                }
                continue;
            }
            
            if (token.getId() != BiBTeXLanguage.WHITESPACE) {
                appendContent(result, token);
            }
        }
        
        entry.getContent().put(key.toLowerCase(), result.toString());
        
        boolean isStopParsingInsideEntryToken = isStopParsingInsideEntryToken(token);
        
        return token.getId() != BiBTeXLanguage.CL_BRAC && !isStopParsingInsideEntryToken;
    }
}
