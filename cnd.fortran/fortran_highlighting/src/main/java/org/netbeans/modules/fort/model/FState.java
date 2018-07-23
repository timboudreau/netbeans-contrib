
package org.netbeans.modules.fort.model;

import org.netbeans.modules.fort.model.lang.FCompoundStatement;
import org.netbeans.modules.fort.model.util.Pair;

/**
 * interface for fortran project state
 * @author Andrey Gubichev
 */
public interface FState {
    /**
     * @return statements from compound statement
     */
    FCompoundStatement getElements();
    /**
     * 
     * @return true if model needn't be updated
     */
    boolean isActual();
    /**
     * 
     * @return property
     */
    Object getProperty(String name);
    /**
     * 
     * @return find compound statement with given position
     */
    Pair<FCompoundStatement, FCompoundStatement> resolveLink(int pos);
   
}
