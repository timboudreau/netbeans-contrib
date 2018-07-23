

package org.netbeans.modules.fort.model.lang;

/**
 * Represents type. Here 'type' means the type of variable etc
 * (not definition of derive type in fortran)
 * @author Andrey Gubichev
 */
public interface FType {
    boolean isPointer();
    boolean isDimension();
    String getCanonicalText();
    FDeclaration.Kind getKind();
}
