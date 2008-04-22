/*
 * Copyright (c) 2008, Your Corporation. All Rights Reserved.
 */

package org.netbeans.lib.javafx.lexer;

import org.netbeans.api.javafx.lexer.JFXStringTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lexical analyzer for java string language.
 *
 * @author Miloslav Metelka
 * @version 1.00
 */

public class JFXStringLexer implements Lexer<JFXStringTokenId> {
    private static Logger log = Logger.getLogger(JFXStringLexer.class.getName());
    private static final int EOF = LexerInput.EOF;

    private LexerInput input;

    private TokenFactory<JFXStringTokenId> tokenFactory;
    private final boolean forceStringLiteralOnly;
    private boolean rl_slStartPossible = false; // represent possible start of RBRACE_LBRACE_STRING_LITERAL.

    public JFXStringLexer(LexerRestartInfo<JFXStringTokenId> info, boolean forceStringLiteralOnly) {
        this.forceStringLiteralOnly = forceStringLiteralOnly;
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
        assert (info.state() == null); // passed argument always null
    }

    public Object state() {
        return null;
    }

    public Token<JFXStringTokenId> nextToken() {
        while (true) {
            int ch = input.read();
            if (log.isLoggable(Level.FINE))
                log.fine("Reading character: " + (ch == EOF ? "<EOF>" : Character.toString((char) ch)));
            switch (ch) {
                case EOF:
                    if (input.readLength() > 0)
                        return token(JFXStringTokenId.TEXT);
                    else
                        return null;
                case '\\': //NOI18N
                    if (input.readLength() > 1) {// already read some text
                        input.backup(1);
                        return tokenFactory.createToken(JFXStringTokenId.TEXT, input.readLength());
                    }
                    switch (ch = input.read()) {
                        case '{': //NOI18N
                            return token(JFXStringTokenId.CODE_OPENING_BRACE_ESCAPE);
                        case '}': //NOI18N
                            return token(JFXStringTokenId.CODE_ENCLOSING_BRACE_ESCAPE);
                        case 'b': //NOI18N
                            return token(JFXStringTokenId.BACKSPACE);
                        case 'f': //NOI18N
                            return token(JFXStringTokenId.FORM_FEED);
                        case 'n': //NOI18N
                            return token(JFXStringTokenId.NEWLINE);
                        case 'r': //NOI18N
                            return token(JFXStringTokenId.CR);
                        case 't': //NOI18N
                            return token(JFXStringTokenId.TAB);
                        case '\'': //NOI18N
                            return token(JFXStringTokenId.SINGLE_QUOTE);
                        case '"': //NOI18N
                            return token(JFXStringTokenId.DOUBLE_QUOTE);
                        case '\\': //NOI18N
                            return token(JFXStringTokenId.BACKSLASH);
                        case 'u': //NOI18N
                            while ('u' == (ch = input.read())) {
                            }//NOI18N

                            for (int i = 0; ; i++) {
                                ch = Character.toLowerCase(ch);

                                if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'f')) { //NOI18N
                                    input.backup(1);
                                    return token(JFXStringTokenId.UNICODE_ESCAPE_INVALID);
                                }

                                if (i == 3) { // four digits checked, valid sequence
                                    return token(JFXStringTokenId.UNICODE_ESCAPE);
                                }

                                ch = input.read();
                            }

                        case '0':
                        case '1':
                        case '2':
                        case '3': //NOI18N
                            switch (input.read()) {
                                case '0':
                                case '1':
                                case '2':
                                case '3': //NOI18N
                                case '4':
                                case '5':
                                case '6':
                                case '7': //NOI18N
                                    switch (input.read()) {
                                        case '0':
                                        case '1':
                                        case '2':
                                        case '3': //NOI18N
                                        case '4':
                                        case '5':
                                        case '6':
                                        case '7': //NOI18N
                                            return token(JFXStringTokenId.OCTAL_ESCAPE);
                                    }
                                    input.backup(1);
                                    return token(JFXStringTokenId.OCTAL_ESCAPE_INVALID);
                            }
                            input.backup(1);
                            return token(JFXStringTokenId.OCTAL_ESCAPE_INVALID);
                    }
                    input.backup(1);
                    return token(JFXStringTokenId.ESCAPE_SEQUENCE_INVALID);
                case '{':
                    if (input.readLength() > 1 || forceStringLiteralOnly) {// already read some text
                        input.backup(1);
                        return tokenFactory.createToken(JFXStringTokenId.TEXT, input.readLength());
                    }
                    rl_slStartPossible = false;
                    return tokenFactory.createToken(JFXStringTokenId.CODE_OPENING_BRACE, input.readLength());

                case '}':
                    if (input.readLength() > 1 || forceStringLiteralOnly || rl_slStartPossible) {
                        return tokenFactory.createToken(JFXStringTokenId.TEXT, input.readLength());
                    } else {
                        //this character is code enclosing bracket only if it is first read character!
                        rl_slStartPossible = true;
                        return tokenFactory.createToken(JFXStringTokenId.CODE_ENCLOSING_BRACE, input.readLength());
                    }

            } // end of switch (ch)
        } // end of while(true)
    }

    private Token<JFXStringTokenId> token(JFXStringTokenId id) {
        return tokenFactory.createToken(id);
    }

    public void release() {
    }

}