package org.netbeans.lib.javafx.lexer;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.RecognizerSharedState;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;
import static org.netbeans.lib.javafx.lexer.v3Lexer.BraceQuoteTracker;

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
//                    input.setLine(ls.getLine());
                    System.err.print("State in: ");
                    BraceQuoteTracker.printStack(System.err, ls.getTracker());
                    lexer.setBraceQuoteTracker(ls.getTracker());
//                    lexer.setSharedState(ls.getSharedState());
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
                System.err.println("Fallback token jfxt:" + lastType);
                return tokenFactory.createToken(JFXTokenId.UNKNOWN, lexerInput.readLength()); 
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
        final BraceQuoteTracker bqt = lexer.getBraceQuoteTracker();
        System.err.print("StateOut: ");
        BraceQuoteTracker.printStack(System.err, bqt);
        if (bqt == null) {
            return null;
        }
        return new LexerState(input.getLine(), bqt, lexer.getSharedState());
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
        private final BQLexerContainer state;
        private final RecognizerSharedState sharedState;

        private LexerState(int line, v3Lexer.BraceQuoteTracker tracker, RecognizerSharedState sharedState) {
            this.line = line;
            this.state = BQLexerContainer.freezeState(tracker);
            this.sharedState = sharedState;
        }

        public int getLine() {
            return line;
        }

        public v3Lexer.BraceQuoteTracker getTracker() {
            return BQLexerContainer.unfreezeState(state);
        }

        public RecognizerSharedState getSharedState() {
            return sharedState;
        }
    }

    private static class BQLexerContainer {
        private final int braceDepth;
        private final char quote;
        private final boolean percentIsFormat;
        private final BQLexerContainer previous;

        private BQLexerContainer(int braceDepth, char quote, boolean percentIsFormat, BQLexerContainer previous) {
            this.braceDepth = braceDepth;
            this.quote = quote;
            this.percentIsFormat = percentIsFormat;
            this.previous = previous;
        }


        static BQLexerContainer freezeState(BraceQuoteTracker t) {
            BQLexerContainer root = null;
            while ( t != null) {
                root = new BQLexerContainer(t.getBraceDepth(), t.getQuote(), t.isPercentIsFormat(), root);
                t = t.getNext();
            }
            return root;
        }

        static BraceQuoteTracker unfreezeState(BQLexerContainer c) {
            BraceQuoteTracker root = null;
            while (c != null) {
                root = new BraceQuoteTracker(root, c.quote, c.percentIsFormat);
                c = c.previous;
            }
            return root;
        }
    }
}
