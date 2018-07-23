

package org.netbeans.modules.fort.model.lang;

/**
 * represents IF statement
 * @author Andrey Gubichev
 */
public interface FIfStatement extends FStatement{
    /** Gets condition */
    FCondition getCondition();

    /** Gets a statement, which is performed in the case condition returns true */
    FStatement getThen();

    /** Gets a statement, which is performed in the case condition returns false */
    FStatement getElse();
}
