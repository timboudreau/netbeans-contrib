
package org.netbeans.modules.fort.model.lang.impl;

import java.util.Collections;
import java.util.List;
import org.netbeans.modules.fort.model.lang.FCompoundStatement;


/**
 * Very simple implementation of fortran compound statement
 * @author Andrey Gubichev
 */
public class EmptyFCompoundStatement extends AbstractFCompound {     
    private static final List<FCompoundStatement> emptyList = 
        Collections.<FCompoundStatement>emptyList();
    /**
     * @return empty list of statements
     */    
    public List<FCompoundStatement> getStatements() {
        return emptyList;
    }      
}
