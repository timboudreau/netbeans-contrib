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
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex;

import java.io.IOException;
import java.util.Stack;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;

import org.netbeans.modules.latex.model.bibtex.Entry;
import org.netbeans.modules.latex.model.bibtex.FreeFormEntry;
import org.netbeans.modules.latex.model.bibtex.PublicationEntry;
import org.netbeans.modules.latex.model.bibtex.StringEntry;
import org.netbeans.modules.latex.model.command.SourcePosition;
import org.netbeans.modules.latex.model.lexer.BiBTeXTokenId;

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
    private TokenSequence ts;
    
    private void setDocument(Document source, int offset) {
        this.source = source;
        TokenHierarchy h = TokenHierarchy.get(source);
        ts = h.tokenSequence();
        ts.move(offset);
    }
    
    private Token getNextToken() {
        ts.moveNext();
        
        return ts.token();
    }
    
    private SourcePosition getCurrentPosition() {
        return new SourcePosition(source, ts.offset());
    }

    
    private boolean isEOF() {
        if (!ts.moveNext())
            return true;
        
        ts.movePrevious();
        
        return false;
    }
    
    public Entry parseEntry(Document doc, int startOffset) throws IOException {
        setDocument(doc, startOffset);
        
        return parseEntry();
    }
    
    private boolean isStopParsingInsideEntryToken(Token token) {
        return token.id() == BiBTeXTokenId.TYPE;
    }
    
    private Entry parseEntry() throws IOException {
        Token token = null;
        
        while (!isEOF() && (token = getNextToken()).id() != BiBTeXTokenId.TYPE)
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
        String text = token.text().toString();
                
        assert BiBTeXTokenId.TYPE == token.id() && text.length() > 0;
        assert text.charAt(0) == '@';
        
        return text.substring(1);
    }
    
    private Entry parseStringEntry() throws IOException {
        StringEntry entry = new StringEntry();
        Token token = null;
        
        while (!isEOF() && (token = getNextToken()).id() != BiBTeXTokenId.TEXT && !isStopParsingInsideEntryToken(token) && token.id() != BiBTeXTokenId.CL_BRAC)
            ;
        
        if (isEOF())
            return entry;
        
        if (token.id() != BiBTeXTokenId.TEXT)
            return entry;
        
        String key = token.text().toString();
        
        while (!isEOF() && (token = getNextToken()).id() != BiBTeXTokenId.EQUALS && !isStopParsingInsideEntryToken(token))
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
        
        Stack<String> bracketStack = new Stack<String>();
        
        while ((token = getNextToken()).id() != BiBTeXTokenId.OP_BRAC)
            ;
        
        bracketStack.push(token.text().charAt(0) == '{' ? "{" : "(");
        
        while (!bracketStack.isEmpty()) {
            token = getNextToken();
            
            if (token.id() == BiBTeXTokenId.OP_BRAC)
                bracketStack.push(token.text().charAt(0) == '{' ? "{" : "(");
            
            if (token.id() == BiBTeXTokenId.CL_BRAC)
                bracketStack.pop(); //TODO: check the type of the bracket!
        }
        
        return entry;
    }

    private Entry parsePublicationEntry(String type) throws IOException {
        PublicationEntry entry = new PublicationEntry();
        Token token = null;
        
        entry.setType(type.toUpperCase()); //TODO: is this correct and wanted behaviour?
        
        while (!isEOF() && (token = getNextToken()).id() != BiBTeXTokenId.OP_BRAC && !isStopParsingInsideEntryToken(token))
            ;
        
        if (isEOF() || isStopParsingInsideEntryToken(token))
            return null;
        
        StringBuilder tag = new StringBuilder();
        
        while (!isEOF() && (token = getNextToken()).id() != BiBTeXTokenId.TEXT && !isStopParsingInsideEntryToken(token))
            ;
        
        if (isEOF() || isStopParsingInsideEntryToken(token))
            return null;
        
        entry.setTag(readTag(token));
        
        while (parseMap(entry))
            ;
        
        return entry;
    }
    
    private String readTag(Token token) {
        StringBuilder sb = new StringBuilder();
        
        while (   token.id() == BiBTeXTokenId.TEXT
               || token.id() == BiBTeXTokenId.DASH
               || token.id() == BiBTeXTokenId.UNDERSCORE) {
            sb.append(token.text());
            
            token = getNextToken();
            
            if (isEOF()) {
                break;
            }
        }
        
        return sb.toString();
    }
    
    private boolean parseMap(PublicationEntry entry) throws IOException {
        Token token = null;
        
        while (!isEOF() && (token = getNextToken()).id() != BiBTeXTokenId.TEXT && !isStopParsingInsideEntryToken(token) && token.id() != BiBTeXTokenId.CL_BRAC)
            ;
        
        if (isEOF())
            return false;
        
        if (token.id() != BiBTeXTokenId.TEXT)
            return false;
        
        String key = token.text().toString();
        
        while (!isEOF() && (token = getNextToken()).id() != BiBTeXTokenId.EQUALS && !isStopParsingInsideEntryToken(token))
            ;
        
        if (isStopParsingInsideEntryToken(token))
            return false;

        
        return readValueString(entry, key);
    }
    
    private void appendContent(StringBuffer buffer, Token token) {
        String text = token.text().toString();
        
        if (BiBTeXTokenId.STRING == token.id() && text.length() > 0) {
            int start = text.charAt(0) == '"' ? 1 : 0;
            int end = text.length() - (text.charAt(text.length() - 1) == '"' ? 1 : 0);
            
            text = text.substring(start, end);
        }
        
        buffer.append(text);
    }
    
    private boolean readValueString(PublicationEntry entry, String key) throws IOException {
        StringBuffer result = new StringBuffer();
        Token token = null;
        
        while (!isEOF() && (token = getNextToken()).id() != BiBTeXTokenId.COMMA && !isStopParsingInsideEntryToken(token) && token.id() != BiBTeXTokenId.CL_BRAC) {
            if (token.id() == BiBTeXTokenId.OP_BRAC) {//TODO: balanced brackets:
                int brackets = 1;
                while (!isStopParsingInsideEntryToken(token = getNextToken())) {
                    if (token.id() == BiBTeXTokenId.OP_BRAC)
                        brackets++;
                    if (token.id() == BiBTeXTokenId.CL_BRAC)
                        brackets--;
                    
                    if (brackets == 0)
                        break;
                    
                    appendContent(result, token);
                }
                continue;
            }
            
            if (token.id() != BiBTeXTokenId.WHITESPACE) {
                appendContent(result, token);
            }
        }
        
        entry.getContent().put(key.toLowerCase(), result.toString());
        
        boolean isStopParsingInsideEntryToken = isStopParsingInsideEntryToken(token);
        
        return token.id() != BiBTeXTokenId.CL_BRAC && !isStopParsingInsideEntryToken;
    }
}
