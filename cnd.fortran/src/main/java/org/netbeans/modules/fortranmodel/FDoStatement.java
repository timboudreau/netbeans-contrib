package org.netbeans.modules.fortranmodel;

/**
 * represents various forms of DO statement
 * @author Andrey Gubichev
 */
public interface FDoStatement extends FStatement {
    /** Gets a statement, which is performed in the case condition returns true */ 
    FStatement getBody();
    /** Gets condition */
    FCondition getCondition();
    /** gets label */
    FLabel getLabel();
    /** gets construction name, for example
     *  name: do 5 i =1,10
     *    ...
     */
    String getName();
}
