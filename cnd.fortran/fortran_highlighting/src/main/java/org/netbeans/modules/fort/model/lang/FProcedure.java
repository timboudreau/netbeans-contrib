

package org.netbeans.modules.fort.model.lang;

import java.util.List;

/**
 * represents a subroutine and a function
 * @author Andrey Gubichev
 */
public interface FProcedure extends FOffsetableDeclaration{
    /** Gets this procedure's declaration text */
    String getDeclarationText();

    /** Gets this procedure's body */
    FCompoundStatement getBody();
    
    /** return null, if it is a subroutine*/
    FType getReturnType();

    List<FVariable>  getParameters();
    
}
