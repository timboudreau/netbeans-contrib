

package org.netbeans.modules.fortranmodel;

/**
 * Represents inheritance 
 * @author Andrey Gubichev
 */
public interface FInheritance {
    /**   Gets base class information*/
    FDerivedType getBaseType();
    /** return true if base class is abstract*/
    boolean isAbstract();
}
