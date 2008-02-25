
package org.netbeans.modules.fort.model.lang.syntax;

import org.netbeans.modules.fort.model.lang.FOffsetable;



/**
 * Fortran token implementation
 * @author Andrey Gubichev
 */
public class FToken implements FOffsetable { 
    
    final private FTokenId id;
    final private int start, end;
    final private String text;
    /**
     *creates a new instance of FToken
     */
    public FToken(FTokenId id, String text, int start, int end) {
        this.id = id;
        this.text = text;
        this.start = start;
        this.end = end;
    } 
    
    /**
     * @return token id
     */
    public FTokenId getId() {
        return id;        
    }

    /**
     * @return token text
     */
    public String getText() {
        return text;
    }

    /**
     *@return token start offset
     */
    public int getStartOffset() {
        return start;
    }

    /**
     *@return token end offset
     */
    public int getEndOffset() {
        return end;
    }
}
