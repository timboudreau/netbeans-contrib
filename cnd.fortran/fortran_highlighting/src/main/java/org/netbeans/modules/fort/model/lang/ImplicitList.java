

package org.netbeans.modules.fort.model.lang;

/**
 * Represents the mapping for the letters A, B, ..., Z and types 
 * used in IMPLICIT statement (p.93 of standard's working draft)
 * @author Andrey Gubichev
 */
public interface ImplicitList {
   /**
    * for IMPLICIT NONE
    **/
   boolean isNullMaping();
//TODO add method like FType getType(String s)
}
