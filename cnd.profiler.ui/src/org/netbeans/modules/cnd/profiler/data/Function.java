/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author eu155513
 */
public class Function extends PropertyContainer<String,Object> implements FunctionContainer {
    private final String name;
    
    private final Set<Call> callers = new HashSet<Call>();
    private final Set<Call> callees = new LinkedHashSet<Call>();

    public Function(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Function getFunction() {
        return this;
    }

    /*
     * Better use addCallee
     */
    public boolean addCaller(Call foo) {
        return callers.add(foo);
    }
    
    public Collection<Call> getCallers() {
        return Collections.unmodifiableCollection(callers);
    }
    
    public boolean addCallee(Call foo) {
        return callees.add(foo);
    }
    
    public Collection<Call> getCallees() {
        return Collections.unmodifiableCollection(callees);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return name.equals(((Function)obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Function " + name;
    }
}
