/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author eu155513
 */
public class PropertyContainer<Key,Value> {
    private final Map<Key, Value> properties = new HashMap<Key, Value>();
    
    public void setProperty(Key name, Value value) {
        properties.put(name, value);
    }
    
    public Value getProperty(Key name) {
        return properties.get(name);
    }

    public Collection<Key> getPropertiesNames() {
        return properties.keySet();
    }
    
    public void addProperties(PropertyContainer<Key,Value> container) {
        for (Key propName : container.getPropertiesNames()) {
            setProperty(propName, container.getProperty(propName));
        }
    }
}
