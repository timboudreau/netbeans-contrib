/*
 * XMLRoot.java
 *
 * Created on March 31, 2007, 2:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.languages.xml.api;


/**
 *
 * @author Jan Jancura
 */
public class XMLAttribute {
    
    private String name;
    private String value;
    
    XMLAttribute (String name, String value) {
        this.name = name;
        if (value != null && value.startsWith("\""))
            value = value.substring (1, value.length () - 1);
        this.value = value;
    }
    
    public String getName () {
        return name;
    }
    
    public String getValue () {
        return value;
    }
}
