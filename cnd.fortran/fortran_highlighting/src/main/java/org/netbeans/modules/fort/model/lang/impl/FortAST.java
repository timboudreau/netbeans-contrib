
package org.netbeans.modules.fort.model.lang.impl;

import antlr.BaseAST;
import antlr.Token;
import antlr.collections.AST;
import org.netbeans.modules.cnd.apt.support.APTBaseToken;
import org.netbeans.modules.cnd.apt.support.APTToken;

/**
 * The class to store information about BaseAST and Position in file
 * @author Andrey Gubichev
 */
public class FortAST extends BaseAST {

    protected Token token = FToken.NIL;
    
    /**
     * initialize information about position in text
     */
    public void initialize(int i, String txt) {
        token = new APTBaseToken();
        token.setType(i);
        token.setText(txt);

    }

    /**
     * initialize information about given AST
     */
    public void initialize(AST a) {
        if (a instanceof FortAST)
            token = ((FortAST)a).token;
        else {
            token = new FToken();
            token.setType(a.getType());
            token.setText(a.getText());
        }
    }
   

    /**
     * initialize information about token
     */
    public void initialize(Token t) {
       token = t;
    }
    
    /**
     * @return line of token occurrence
     */
    public int getLine() {
        return token.getLine();
    }
    /**
     * @return column of token occurrence
     */
    public int getColumn() {
        return token.getColumn();
    }

    /**
     *@return token's offset in text
     */
    public int getOffset() {
        if (token instanceof APTToken) {
            return ((APTToken)token).getOffset();
        } else {
            return 0;
        }
    }

    
    /**
     * 
     * @return end offset of token
     */
    public int getEndOffset() {
        if (token instanceof APTToken) {
            return ((APTToken)token).getEndOffset();
        } else {
            return 0;
        }        
    }
    
    /**
     * 
     * @return end line of token
     */
    public int getEndLine() {
        if (token instanceof APTToken) {
            return ((APTToken)token).getEndLine();
        } else {
            return 0;
        }          
    }
    
    /**
     * 
     * @return end column of token
     */
    public int getEndColumn() {
        if (token instanceof APTToken) {
            return ((APTToken)token).getEndColumn();
        } else {
            return 0;
        }          
    }

}
