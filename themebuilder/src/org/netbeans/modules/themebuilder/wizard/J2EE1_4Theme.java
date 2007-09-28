/*
 * ThemeImpl.java
 *
 * Created on December 20, 2006, 5:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.themebuilder.wizard;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * A singleton implementation of theme for J2EE 1.4 projects.
 *
 * @author gjmurphy
 */
class J2EE1_4Theme extends Theme {
    
    final static private String[] propertiesAttributeNames = {
        "X-SJWUIC-Theme-Messages",
        "X-SJWUIC-Theme-Images",
        "X-SJWUIC-Theme-JavaScript",
        "X-SJWUIC-Theme-Stylesheets"
    };
    
    private static Theme theme;
    
    public static Theme getTheme() {
        if (theme == null)
            theme = new J2EE1_4Theme();
        return theme;
    }
    
    private J2EE1_4Theme() {
    }
    
    Set<String> propertiesAttributeNameSet;
    
    public Set<String> getPropertiesAttributeNames() {
        if (propertiesAttributeNameSet == null) {
            propertiesAttributeNameSet = new AbstractSet<String>() {
                
                Iterator<String> iterator;
                
                public Iterator<String> iterator() {
                    if (iterator == null) {
                        iterator = Arrays.asList(propertiesAttributeNames).iterator();
                    }
                    return iterator;
                }

                public int size() {
                    return propertiesAttributeNames.length;
                }
                
            };
        }
        return propertiesAttributeNameSet;
    }

    public String getAttributeSectionName() {
        return "com/sun/rave/web/ui/theme/";
    }
    
}
