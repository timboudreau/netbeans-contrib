

package org.netbeans.modules.fort.model.lang;

import org.netbeans.modules.cnd.api.model.CsmOffsetable;

/**
 * represents a condition
 * @author Andrey Gubichev
 */
public interface FCondition extends CsmOffsetable, FScope{
    /**return expression */
     FExpression getExpression();
}
