

package org.netbeans.modules.fortranmodel;

import java.util.Collection;

/**
 * Represents derived type in Fortran
 * @author Andrey Gubichev
 */
public interface FDerivedType extends  FScope, FOffsetableDeclaration{

    /**
     * Returns  collection of this type' members.
     * Members migt be:
     *	fields
     *	type-bound procedures
     */
    Collection<FMember> getMembers();
    /** Returns the collection of base classes */  
    Collection<FInheritance> getBaseTypes();
}
