
package org.netbeans.modules.fort.core.editor;

import org.netbeans.api.lexer.Token;
import org.netbeans.modules.fort.model.FSyntax;
import org.netbeans.modules.fort.model.lang.syntax.FBaseTokenId;
import org.netbeans.modules.fort.model.lang.syntax.FHighlightLexer;
import org.netbeans.modules.fort.model.lang.syntax.FTokenId;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 * The class to represent Lexer (used in highlighting)
 */
public class FLexer implements Lexer<FTokenId> {
    
    private FHighlightLexer lexer;
    private LexerRestartInfo info;
    private FSyntax syntax;
    private LexerInput input;
    
    private TokenFactory<FTokenId> tokenFactory;

    /**
     * creates a new instance of FLexer
     */
    public FLexer(LexerRestartInfo<FTokenId> info, FSyntax syntax) {
        this.syntax = syntax;
        this.info = info;
        
        tokenFactory = info.tokenFactory();
        input = info.input();
    }
    
    /**
     * 
     * @return next token
     */
    public Token<FTokenId> nextToken() {        
        if (lexer == null)
            lexer = syntax.createHighlightLexer(new LexerInputReader(input), 
                                                info.state());      
                
        FTokenId tokId = lexer.nextToken();
        int length = lexer.getLastLength();
                        
        if (tokId == FBaseTokenId.EOF) {                        
            return null;
        }
        
        if (length == 0) {
            return tokenFactory.createToken(FBaseTokenId.EMPTY, 
                                     input.readLength());
        }
               
        return tokenFactory.createToken(tokId, length);
    }

    /**
     * 
     * @return lexer's state
     */
    public Object state() {
        if (lexer == null) {
            return null;            
        }
        return lexer.getState();
    }

    /**
     * stub
     */
    public void release() {
        
    }

}

