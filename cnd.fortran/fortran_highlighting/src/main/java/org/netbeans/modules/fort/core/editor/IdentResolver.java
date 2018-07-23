
package org.netbeans.modules.fort.core.editor;

import org.netbeans.modules.fort.model.lang.FProcedure;
import org.netbeans.modules.fort.model.lang.FVariable;


/**
 * interface to represent indentifier's resolver
 */
public interface IdentResolver {
    
    /**
     * 
     * @return identifier's type
     */
    IdentType getIdentType(String name);
    
    //XXX add other tokens
    /**
     * 
     * @return variable from name
     */
    FVariable getVariable(String name);
    /**
     * 
     * @return procedure from name
     */
    FProcedure getProcedure(String name);
    
    //XXX add other types
    /**
     * type of identifier
     */
    enum IdentType {
        VARIABLE,
        FUNCTION,
        UNKN_IDENT;                        
    }            
            
    /**
     * default resolver (stub)
     */
    public static final IdentResolver DEFAULT = 
            new IdentResolver() {
                
                public IdentType getIdentType(String name) {
                    return null;
                }

                public FVariable getVariable(String name) {
                    return null;
                }

                public FProcedure getProcedure(String name) {
                    return null;
                }
            };
}
