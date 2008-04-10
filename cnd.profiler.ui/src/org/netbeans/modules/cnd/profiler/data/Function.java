/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author eu155513
 */
public class Function {
    private final String name;
    
    private final Map<String, Object> attribs = new HashMap<String, Object>();
    
    private final Set<Function> callers = new HashSet<Function>();
    private final Set<Function> callees = new HashSet<Function>();

    public Function(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean addCaller(Function foo) {
        return callers.add(foo);
    }
    
    public Collection<Function> getCallers() {
        return callers;
    }
    
    public boolean addCallee(Function foo) {
        return callees.add(foo);
    }
    
    public Collection<Function> getCallees() {
        return callees;
    }
    
    public void setAttrib(String name, Object value) {
        attribs.put(name, value);
    }
    
    public Object getAttrib(String name) {
        return attribs.get(name);
    }
    
    public Map getAttribs() {
        return attribs;
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
}
