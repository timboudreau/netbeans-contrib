

package org.netbeans.modules.fortranmodel;

/**
 * Represents member of derived type
 * @author Andrey Gubichev
 */
public interface FMember extends FOffsetableDeclaration{
    FDerivedType getContainingType();
    FVisibility getVisibility();
}
