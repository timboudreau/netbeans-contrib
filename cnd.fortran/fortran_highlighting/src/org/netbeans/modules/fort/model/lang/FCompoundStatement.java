

package org.netbeans.modules.fort.model.lang;

import java.util.List;

/**
 * Represents compound statement;
 * @author Andrey Gubichev
 */
//XXX do right inheritance: Fstatement, FScope
public interface FCompoundStatement extends FOffsetable{
    /** Gets statements this one consists of*/
    List<FCompoundStatement> getStatements();

}
