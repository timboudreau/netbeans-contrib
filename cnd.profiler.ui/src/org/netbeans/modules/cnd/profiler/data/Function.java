/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author eu155513
 */
public class Function {
    private final String name;
    
    private final Map<String, Object> properties = new HashMap<String, Object>();
    
    private final Set<Function> callers = new HashSet<Function>();
    private final Set<Function> callees = new LinkedHashSet<Function>();

    public Function(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /*
     * Better use addCallee
     */
    public boolean addCaller(Function foo) {
        return foo.addCallee(this);
    }
    
    public Collection<Function> getCallers() {
        return Collections.unmodifiableCollection(callers);
    }
    
    public boolean addCallee(Function foo) {
        foo.callers.add(this);
        return callees.add(foo);
    }
    
    public Collection<Function> getCallees() {
        return Collections.unmodifiableCollection(callees);
    }
    
    public void setAttrib(String name, Object value) {
        properties.put(name, value);
    }
    
    public Object getAttrib(String name) {
        return properties.get(name);
    }
    
    public Map getAttribs() {
        return properties;
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
