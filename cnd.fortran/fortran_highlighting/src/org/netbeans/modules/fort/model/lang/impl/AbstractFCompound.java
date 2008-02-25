
package org.netbeans.modules.fort.model.lang.impl;

import java.util.List;
import org.netbeans.modules.fort.model.lang.FCompoundStatement;
import org.netbeans.modules.fort.model.lang.FOffsetable;


/**
 * Class to represent Fortran compound statement
 * @author Andrey Gubichev
 */
public abstract class AbstractFCompound implements FCompoundStatement {
    
    private int startOffset;
    private int endOffset;      

    /**
     * 
     * @return statements
     */
    abstract public List<FCompoundStatement> getStatements();
                

    /**
     * set offset
     */
    public void setOffset(FOffsetable off) {
        startOffset = off.getStartOffset();
        endOffset = off.getEndOffset();
    }
    
    /**
     * set start offset
     */
    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }
    
    /**
     * set end offset
     */
    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }
    
    /**
     * 
     * @return start offset of token
     */
    public int getStartOffset() {
        return startOffset;
    }

    /**
     * 
     * @return end offset of token
     */
    public int getEndOffset() {
        return endOffset;
    }       
}
