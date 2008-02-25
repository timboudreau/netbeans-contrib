

package org.netbeans.modules.fort.model.lang;

/**
 *
 * @author Andrey Gubichev
 */
public interface FTypeParamValue {
     /**
      * -1, if not integer('*' or ':')
      * */
     int getKind();
     boolean isAsterisc();
     boolean isColon();
}
