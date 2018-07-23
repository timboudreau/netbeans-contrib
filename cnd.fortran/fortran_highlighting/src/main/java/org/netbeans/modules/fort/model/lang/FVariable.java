

package org.netbeans.modules.fort.model.lang;

/**
 * Represents a variable
 * @author Andrey Gubichev
 */
public interface FVariable extends FOffsetableDeclaration{
       /** Gets this variable type */ 
       FType getType();
       
       /** Gets this variable initial value */     
       FExpression getInitialValue();

       String getDeclarationText();
}
