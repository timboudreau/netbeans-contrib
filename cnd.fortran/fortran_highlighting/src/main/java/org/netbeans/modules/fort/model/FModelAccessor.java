

package org.netbeans.modules.fort.model;

/**
 *fortran model accessor
 * @author Andrey Gubichev
 */
public interface FModelAccessor {
    /**
     * @return lexer state
     */
    FState getState();  
    
    void addParseListener(ParseListener list);
    
    public interface ParseListener extends java.util.EventListener {
        void notifyParsed();
    }

    
}
