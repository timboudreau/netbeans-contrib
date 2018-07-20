

package org.netbeans.modules.fortranmodel;

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
