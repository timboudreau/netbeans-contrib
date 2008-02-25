
package org.netbeans.modules.fort.model.lang.syntax;

/**
 * Lexer for fortran highlighting
 * @author Andrey Gubichev
 */
public interface FHighlightLexer<T extends FTokenId>  {  
    /**
     * @return next token
     */
    FTokenId nextToken();    
    /**
     * @return last token length
     */
    int getLastLength();  
    /**
     * @return object state
     */
    Object getState();
}
