package org.netbeans.lib.javafx.lexer;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRReaderStream;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Rastislav Komara (<a href="mailto:rastislav.komara@sun.com">RKo</a>)
 * @todo documentation
 */
public class JFXLexer implements Lexer<JFXTokenId> {
    private v3Lexer lexer;
    private TokenFactory<JFXTokenId> tokenFactory;
    private ANTLRReaderStream input;
    private LexerRestartInfo<JFXTokenId> info;
    protected LexerInput lexerInput;
    protected JFXTokenId lastType;
    private boolean released;

    public JFXLexer() {
        this.lexer = new v3Lexer();        
    }

    public void restart(LexerRestartInfo<JFXTokenId> info) throws IOException {
        this.info = info;
        released = false;
    }

    public Token<JFXTokenId> nextToken() {
        if (released) return null;
        if (info != null) {
            try {
                lexerInput = info.input();                
                final LexerInputStream reader = new LexerInputStream();
                reader.setLexerInput(lexerInput);

                input = new ANTLRInputStream(reader);
                lexer = new v3Lexer(input);
                final LexerState ls = (LexerState) info.state();
                if (ls != null) {
                    lexer.setBraceQuoteTracker(ls.tracker);                    
                }
                tokenFactory = info.tokenFactory();
                info = null;
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        final org.antlr.runtime.Token token = lexer.nextToken();
        if (token.getType() == v3Lexer.EOF) {
            if (lexerInput.readLength() > 0) {
                //TODO: [RKo] reconsider this!
                System.err.println("Fallback token jfxt:" + lastType);
                return tokenFactory.createToken(lastType != null ? lastType : JFXTokenId.UNKNOWN, lexerInput.readLength()); 
            }
            return null;
        }
        String text;
        text = token.getText();
        lastType = getId(token);
        return tokenFactory.createToken(lastType, text != null ? text.length() : 0);
    }

    private JFXTokenId getId(org.antlr.runtime.Token token) {
        return JFXTokenId.getId(token.getType());
    }

    public Object state() {
        return new LexerState(input.getLine(), lexer.getBraceQuoteTracker());
    }

    public void release() {
        released = true;
    }


    static class LexerInputStream extends InputStream {
        private LexerInput input;

        public void setLexerInput(LexerInput input) {
            this.input = input;
        }

        public int read() throws IOException {
            final int c = input.read();
            if (c == LexerInput.EOF) return -1;
            return c;
        }
    }

    private static class LexerState {
        private final int line;
        private final v3Lexer.BraceQuoteTracker tracker;

        private LexerState(int line, v3Lexer.BraceQuoteTracker tracker) {
            this.line = line;
            this.tracker = tracker;
        }

        public int getLine() {
            return line;
        }

        public v3Lexer.BraceQuoteTracker getTracker() {
            return tracker;
        }
    }
}
