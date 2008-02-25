
package org.netbeans.modules.fort.core.editor;

import antlr.CommonToken;
import org.netbeans.modules.fort.model.lang.FOffsetable;
import org.netbeans.modules.fort.model.lang.syntax.FBaseTokenId;
import org.netbeans.modules.fort.model.lang.syntax.FToken;


/**
 * The class to store information about fortran token
 */
public class AntlrToken extends CommonToken implements FOffsetable {
    
    private int start;
    private int end;
        
    /**
     * creates a new instance of AntlrToken
     */
    public AntlrToken() {
        
    }
    
    /**
     * creates new FToken
     */
    public FToken createFToken(IdentResolver resolver) {     
        //XXX add token from grammar
        return tokenHelper(FBaseTokenId.UNKWN_ID);
    }

    /**
     * set start offset of token
     */
    public void setStartOffset(int start) {
        this.start = start;
    }
    
    /**
     * set end offset of token
     */
    public void setEndOffset(int end) {
        this.end = end;
    }
    
    /**
     * 
     * @return start offset of token
     */
    public int getStartOffset() {
        return start;
    }

    /**
     * 
     * @return end offset of token
     */
    public int getEndOffset() {
        return end;
    }
    
    private FToken tokenHelper(FBaseTokenId id) {
        return new FToken(id, getText(), getStartOffset(), getEndOffset());
    }
  
}
