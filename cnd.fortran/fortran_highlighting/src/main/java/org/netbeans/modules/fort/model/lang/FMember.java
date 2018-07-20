

package org.netbeans.modules.fort.model.lang;

/**
 * Represents member of derived type
 * @author Andrey Gubichev
 */
public interface FMember extends FOffsetableDeclaration{
    FDerivedType getContainingType();
    FVisibility getVisibility();
}
