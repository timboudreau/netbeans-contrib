
package org.netbeans.modules.fort.model;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.fort.model.lang.FProcedure;
import org.netbeans.modules.fort.model.lang.FVariable;

/**
 * base implementation of fortran model
 * @author Andrey Gubichev
 */
public abstract class AbstractFModel implements FModel {
    
    private Map<String, FVariable> str2var;
    private Map<String, FProcedure> str2func;
    
    protected AbstractFModel() {
        str2var = new HashMap<String, FVariable>();
        str2func = new HashMap<String, FProcedure>();
    }
    
    protected void init() {
        str2var.clear();
        str2func.clear();
        
        for (FVariable instr : getVariables()) {
            splitAndAdd(instr.getName(), instr, str2var);
        }
        
        for (FProcedure reg : getProcedures()) {                                    
            splitAndAdd(reg.getName(), reg, str2func);
        }
    }  
    
    /**
     * @return variable with given name
     */
    public FVariable getVariableByName(String name) {
        return str2var.get(name);
    }
    /**
     * @return function with given name
     */   
    public FProcedure getFunctionByName(String name) {
        return str2func.get(name);
    }
    
    private static <T>void splitAndAdd(String name, T value, Map<String, T> mapa) {                  
         for (String el : name.split(";"))
             mapa.put(el, value);
    }
}
