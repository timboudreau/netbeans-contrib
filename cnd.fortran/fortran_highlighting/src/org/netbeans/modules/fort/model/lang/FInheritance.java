

package org.netbeans.modules.fort.model.lang;

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
