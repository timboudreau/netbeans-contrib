
package org.netbeans.modules.fort.core.editor;

import java.util.logging.Logger;
import java.util.logging.Level;

import antlr.TokenStreamException;
import org.netbeans.modules.fort.model.lang.syntax.FBaseTokenId;
import org.netbeans.modules.fort.model.lang.syntax.FHighlightLexer;
import org.netbeans.modules.fort.model.lang.syntax.FToken;
import org.netbeans.modules.fort.model.lang.syntax.FTokenId;


/**
 * The class to represent Lexer for fortran
 */
public class AntlrLexer implements FHighlightLexer {
    
    private AntlrScanner scanner;
    private IdentResolver resolver;
    
    private int length;
    
    /**
     * Creates a new instance of AntlrLexer
     */
    public AntlrLexer(AntlrScanner scanner, IdentResolver resolver) {        
        this.scanner = scanner;
        this.resolver = resolver;
        
        length = 0;
    }
    
    /**
     * gets next token
     */
    public FTokenId nextToken() {
     
        int start = scanner.getOffset();                    
        FTokenId tokId = null;
        
        try {           
            AntlrToken antlrTok = (AntlrToken) scanner.nextToken();
            

            FToken fTok = antlrTok.createFToken(resolver);
            tokId = fTok.getId();
               
            length = antlrTok.getEndOffset() -
                     antlrTok.getStartOffset();
   
            return tokId;

        } catch (TokenStreamException ex) {            
            if (scanner.getPartState() == AntlrScanner.PartState.IN_COMMENT ||
                scanner.getPartState() == AntlrScanner.PartState.IN_STRING) {
                length = scanner.getOffset() - start;
                
            } else {
                Logger.getLogger(this.getClass().   getName()).
                    log(Level.SEVERE, "error with antlr highlight lexer");

                length = 0;
            }
            tokId = FBaseTokenId.EMPTY;
        }
        
        return tokId;
    }
  

    /**
     * get length of token
     */
    public int getLastLength() {     
        return length;
    }
    
    /**
     * get state of lexer
     */
    public Object getState() {
        return scanner.getIntState();
    }
}
