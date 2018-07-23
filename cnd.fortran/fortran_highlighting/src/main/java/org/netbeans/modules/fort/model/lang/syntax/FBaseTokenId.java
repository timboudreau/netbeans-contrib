
package org.netbeans.modules.fort.model.lang.syntax;

/**
 * Base implementation of fortran token id interface
 * @author Andrey Gubichev
 */
public enum FBaseTokenId implements FTokenId {
    EMPTY("whitespace"),
    EOF,
    
    COMMENT,
    
    UNKWN_ID, 
      

    LABEL,
            
    STRING,
    CHARACTER;          
    
    private String primaryCategory;
    
    private FBaseTokenId(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }
    
    private FBaseTokenId() {
        this.primaryCategory = null;
    }
    
    /**
     * @return name of category
     */
    public String primaryCategory() {
        return primaryCategory;
    }       
}
