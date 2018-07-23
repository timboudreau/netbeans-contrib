
package org.netbeans.modules.fort.core.editor;

import antlr.TokenStream;

/**
 * The class to represent scanner
 */
public interface AntlrScanner extends TokenStream {
    /**
     * different states of scaner
     */
    public enum PartState { 
        /**
         * default state
         */
        DEFAULT, IN_COMMENT, IN_STRING;        
    }
       
    /**
     * 
     * @return state
     */
    PartState getPartState();
    
    /**
     * 
     * @return state
     */
    Object getIntState();
    /**
     * set state
     */
    void setIntState(Object state);
    
    /**
     * 
     * @return offset of token
     */
    int getOffset();
}
