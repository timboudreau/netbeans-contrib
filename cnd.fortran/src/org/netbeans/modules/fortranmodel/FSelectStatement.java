

package org.netbeans.modules.fortranmodel;

/**
 * represents Select statement
 * @author Andrey Gubichev
 */
public interface FSelectStatement {
    /** gets switch condition */
    FCondition getCondition();
    
    /** gets swithc body */
    FStatement getBody();

}
