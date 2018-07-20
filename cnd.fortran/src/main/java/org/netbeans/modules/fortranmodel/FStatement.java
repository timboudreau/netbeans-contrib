

package org.netbeans.modules.fortranmodel;

import org.netbeans.modules.cnd.api.model.CsmOffsetable;

/**
 *Represents some statement 
 * @author Andrey Gubichev
 */
public interface FStatement extends CsmOffsetable, FObject, FScopeElement {
  enum Kind  {
      LABEL,
      CASE,
      EXPRESSION,
      COMPOUND,
      IF,
      DO,
      CONTINUE,
      RETURN,
      GOTO,
      DECLARATION
  };
  Kind getKind();
}
