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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.modules.latex.model.command.SourcePosition;

/**
 *
 * @author Jan Lahoda
 */
public class BiBParser {
    
    /** Creates a new instance of BiBTest */
    public BiBParser() {
    }
    
    private static final int EOF        = (-1);
    private static final int AT_CHAR    = 1;
    private static final int OP_BRAC    = 2;
    private static final int CL_BRAC    = 3;
    private static final int TEXT       = 4;
    private static final int STRING     = 5;
    private static final int EQUALS     = 6;
    private static final int COMMA      = 7;
    private static final int WHITESPACE = 8;
    private static final int SPECIAL    = 9;
    
    private static final String STRING_TYPE = "STRING";
    private static final String COMMENT_TYPE = "COMMENT";
    private static final String PREAMBLE_TYPE = "PREAMBLE";
    
    private int currentOffset;
    private Document source;
    private String text;
//    private PushbackReader input;
    private String content;
    
    private SourcePosition getCurrentPosition() {
        return new SourcePosition(source, currentOffset);
    }
    
    private int readNext() {
        try {
//            int c = text.charAt(currentOffset++);
//            
//            content = String.valueOf(c);
            content = source.getText(currentOffset++, 1);
            return content.charAt(0);
        } catch (BadLocationException e) {
            return EOF; //TODO: make this sane, preferable using lexer...
        }
    }
    
    private int getToken() throws IOException {
        int c;
        
        while (true) {
            c = readNext();
        switch (c) {
            case (-1): return EOF;
            
            case '@': return AT_CHAR;
            
            case '{':
            case '(': return OP_BRAC;
            
            case '}':
            case ')': return CL_BRAC;
            
            case '=': return EQUALS;

            case ';':
            case ',': return COMMA;
            
            case '"': return STRING;
            
            case ' ':
            case '\t':
            case '\n': return WHITESPACE;
            
            case '%': while ((c = readNext()) != (-1) && c != '\n')
                          ;
                      if (c == (-1))
                          return (-1);
            
                      currentOffset--;
                      
                      continue;
                      
            default: if (Character.isLetter((char) c))
                          return readText((char) c);
                     return SPECIAL;
        }
        }
    }
    
    private int readText(char first) throws IOException {
        StringBuffer result = new StringBuffer();
        int c;
        
        result.append(first);
        
        while ((c = readNext()) != (-1) && Character.isLetterOrDigit((char) c)) {
            result.append((char) c);
        }
        
        currentOffset--;
        
        content = result.toString();
        
        return TEXT;
    }
    
//    public synchronized Collection/*<Entry>*/ parseBiBFile(Document doc) throws IOException {
//        //TODO: locking!!
//        source = doc;
////        text = doc.getText(0, doc.getLength());
//        currentOffset = 0;
//        Collection result = new ArrayList();
//        
//        Entry entry;
//        
//        while ((entry = parseEntry(-1)) != null) {
//            result.add(entry);
//        }
//        
//        return result;
//    }
    
    public Entry parseEntry(Document doc, int startOffset, int supposedEnd) throws IOException {
        source = doc;
        currentOffset = startOffset;
        
        return parseEntry(supposedEnd);
    }
    
    private boolean isStopParsingInsideEntryToken(int token) {
        return token == EOF || token == AT_CHAR;
    }
    
    private Entry parseEntry(int supposedEnd) throws IOException {
        int token;
        
        while ((token = getToken()) != AT_CHAR && token != EOF)
            ;
        
        if (token == EOF)
            return null;
        
        currentOffset--;
        SourcePosition start = getCurrentPosition();
        currentOffset++;
        
        token = getToken();
        
        if (isStopParsingInsideEntryToken(token))
            return null;
        
        if (token != TEXT)
            return null; //non-valid entry

        String type = content;
        Entry result;
        
        if (STRING_TYPE.equalsIgnoreCase(type)) {
            result = parseStringEntry(supposedEnd);
        } else {
            if (COMMENT_TYPE.equalsIgnoreCase(type) || PREAMBLE_TYPE.equalsIgnoreCase(type)) {
                result = parseFreeFormEntry(type, supposedEnd);
            } else {
                result = parsePublicationEntry(type, supposedEnd);
            }
        }
        
        if (result != null) {
            result.setStartPosition(start);
            result.setEndPosition(getCurrentPosition());
        }
        
        return result;
    }
    
    private Entry parseStringEntry(int supposedEnd) throws IOException {
        StringEntry entry = new StringEntry();
        int token = -2;
        
        while ((token = getToken()) != TEXT && !isStopParsingInsideEntryToken(token) && token != CL_BRAC)
            ;
        
        if (token != TEXT)
            return entry;
        
        String key = content;
        
        while ((token = getToken()) != EQUALS && !isStopParsingInsideEntryToken(token))
            ;
        
        if (isStopParsingInsideEntryToken(token))
            return null;

        //TODO: this is not finished !
        entry.setKey(key);
        entry.setValue(content);
        
        return entry;
    }

    private Entry parseFreeFormEntry(String type, int supposedEnd/*ignored..*/) throws IOException {
        FreeFormEntry entry = new FreeFormEntry();
        int token = -2;
        
        entry.setType(type);
        
        Stack bracketStack = new Stack();
        
        while ((token = getToken()) != OP_BRAC)
            ;
        
        bracketStack.push(content);
        
        while (!bracketStack.isEmpty()) {
            token = getToken();
            
            if (token == OP_BRAC)
                bracketStack.push(content);
            
            if (token == CL_BRAC)
                bracketStack.pop(); //TODO: check the type of the bracket!
        }
        
        return entry;
    }

    private Entry parsePublicationEntry(String type, int supposedEnd) throws IOException {
        PublicationEntry entry = new PublicationEntry();
        int token;
        
        entry.setType(type.toUpperCase()); //TODO: is this correct and wanted behaviour?
        
        while ((token = getToken()) != OP_BRAC && !isStopParsingInsideEntryToken(token))
            ;
        
        if (isStopParsingInsideEntryToken(token))
            return null;
        
        while ((token = getToken()) != TEXT && !isStopParsingInsideEntryToken(token))
            ;
        
        if (isStopParsingInsideEntryToken(token))
            return null;
        
        entry.setTag(content);
        
        while (parseMap(entry, supposedEnd))
            ;
        
        return entry;
    }
    
    private boolean parseMap(PublicationEntry entry, int supposedEnd) throws IOException {
        int token = -2;
        
        while ((token = getToken()) != TEXT && !isStopParsingInsideEntryToken(token) && token != CL_BRAC)
            ;
        
        if (token != TEXT)
            return false;
        
        String key = content;
        
        while ((token = getToken()) != EQUALS && !isStopParsingInsideEntryToken(token))
            ;
        
        if (isStopParsingInsideEntryToken(token))
            return false;

        
        return readValueString(entry, key, supposedEnd);
    }
    
    private boolean readValueString(PublicationEntry entry, String key, int supposedEnd) throws IOException {
        StringBuffer result = new StringBuffer();
        int token;
        
        while ((token = getToken()) != COMMA && !isStopParsingInsideEntryToken(token) && token != CL_BRAC) {
            if (token == OP_BRAC) {
                while ((token = getToken()) != CL_BRAC && !isStopParsingInsideEntryToken(token)) {
                    result.append(content);
                }
                continue;
            }

            if (token == STRING) {
                //TODO: describe the error detection and correction used here!
                int lastClosingBracket = -1;
                
                while ((token = getToken()) != STRING && !isStopParsingInsideEntryToken(token) && (supposedEnd == (-1) || currentOffset < supposedEnd)) {
                    if (token == CL_BRAC)
                        lastClosingBracket = currentOffset;
                    
                    result.append(content);
                }
                
                //TODO: well, not sure whether this error correction is correct and whether it will work correctly under all circumstaces.
                //Forcing a ".
                if ((supposedEnd != (-1) && currentOffset >= supposedEnd))
                    currentOffset--;
                
                if (isStopParsingInsideEntryToken(token)) {
                    if (lastClosingBracket != (-1))
                        currentOffset = lastClosingBracket;
                    break;
                }
                
                continue;
            }

            if (token != WHITESPACE)
                result.append(content);
        }
        
        entry.getContent().put(key.toLowerCase(), result.toString());
        
        boolean isStopParsingInsideEntryToken = isStopParsingInsideEntryToken(token);
        
        if (isStopParsingInsideEntryToken)
            currentOffset--;
        return token != CL_BRAC && !isStopParsingInsideEntryToken;
    }
}
