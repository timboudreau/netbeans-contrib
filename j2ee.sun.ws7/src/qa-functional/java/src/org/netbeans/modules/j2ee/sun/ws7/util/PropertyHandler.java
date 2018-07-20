/*
 * PropertyHandler.java
 *
 * Created on May 17, 2007, 12:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.j2ee.sun.ws7.util;

import java.util.HashMap;
import org.netbeans.jellytools.properties.Property;
import org.netbeans.jellytools.properties.PropertySheetOperator;
import org.netbeans.jemmy.operators.JTableOperator;

/**
 *
 * @author Prabushankar.Chinnasamy
 */
public class PropertyHandler {

    /** Creates a new instance of PropertyHandler */
    public PropertyHandler() {
    }

    /** Creates a new instance of PropertyHandler */
    public PropertyHandler(PropertySheetOperator pso) {
        this.pso=pso;
    }

    public void setProperty(PropertySheetOperator pso, String name, String value) {
        this.pso = pso;
        setProperty(name,value);
    }

    public void setProperty(String name, String value) {
                Property p = new Property(pso, name);
        p.setValue(value);
    }

    public String getProperty(PropertySheetOperator pso, String name) {
        this.pso = pso;
        return getProperty(name);
    }

    public String getProperty(String name) {
        String value = null;
        Property p = new Property(this.pso, name);
        return p.getValue();
    }

    public HashMap getDump() {
        HashMap dump = new HashMap();
        Property px = null;
        for(int i = 0; i < new JTableOperator(pso).getRowCount(); i++) {
            px = new Property(this.pso, i);
            dump.put(px.getName(), px.getValue());
        }
        return dump;
    }

    public HashMap getDump(PropertySheetOperator pso) {
        this.pso = pso;
        return getDump();
    }
    private PropertySheetOperator pso = null;
}
