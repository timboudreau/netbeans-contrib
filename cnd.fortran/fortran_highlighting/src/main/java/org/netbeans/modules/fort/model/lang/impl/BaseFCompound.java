
package org.netbeans.modules.fort.model.lang.impl;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.fort.model.lang.FCompoundStatement;

/**
 * Simple implementation of Fortran compound statement
 * @author Andrey Gubichev
 */
public class BaseFCompound extends AbstractFCompound {        
    private List<FCompoundStatement> compList;  
    
    /**
     * creates  a new instance of BaseFCompound
     */
    public BaseFCompound() {
        compList = new ArrayList<FCompoundStatement>();
    }
    
    /**
     * 
     * @return statements
     */
    public List<FCompoundStatement> getStatements() {
        return compList;
    }
    
    /**
     * add statement to list
     */
    public void add(FCompoundStatement c) {
        compList.add(c);
    }


}
