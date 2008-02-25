

package org.netbeans.modules.fortranmodel;

import java.util.List;

/**
 * Represents compound statement;
 * @author Andrey Gubichev
 */
public interface FCompoundStatement extends FStatement, FScope{
    /** Gets statements this one consists of*/
    List<FStatement> getStatements();

}
