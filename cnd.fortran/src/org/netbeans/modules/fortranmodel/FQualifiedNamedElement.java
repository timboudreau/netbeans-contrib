

package org.yourorghere.codemodel;

/**
 * An element, which has qualified name:
 * type, field,  etc
 * @author Andrey Gubichev
 */
public interface FQualifiedNamedElement extends FNamedElement{
    /** Gets this element qualified name */
    String getQualifiedName();

}
