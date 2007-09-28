/*
 * Theme.java
 *
 * Created on December 11, 2006, 8:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.themebuilder.wizard;

import java.util.Set;

/**
 * The theme class serves as a container for data and metadata specific to a version
 * of a theme.
 *
 * @author gjmurphy
 */
public abstract class Theme {
    
    public static enum Version { 
        
        J2EE1_4("J2EE 1.4"), J2EE5("J2EE 5");
        
        private final String displayName;
        
        Version(String displayName) {
            this.displayName = displayName;
        }
        
        public String toString() {
            return this.displayName;
        }
        
    };
    
    
    public static Theme getTheme(Theme.Version version) {
        switch (version) {
            case J2EE1_4:
                return J2EE1_4Theme.getTheme();
            default:
                return J2EE5Theme.getTheme();
        }
    }
    
    /**
     * Returns the name of the theme attributes section that contains the defining
     * attributes of a theme manifest. A manifest is considered to be a valid
     * theme manifest if it contains this names attributes section.
     */
    abstract public String getAttributeSectionName();

    
    private static final String VERSION_ATTRIBUTE_NAME = "X-SJWUIC-Theme-Version";
    
    public String getVersionAttributeName() {
        return VERSION_ATTRIBUTE_NAME;
    }

    
    private static final String NAME_ATTRIBUTE_NAME = "X-SJWUIC-Theme-Name";
    
    public String getNameAttributeName() {
        return NAME_ATTRIBUTE_NAME;
    }
    
    private static final String PREFIX_ATTRIBUTE_NAME = "X-SJWUIC-Theme-Prefix";

    public String getPrefixAttributeName() {
        return PREFIX_ATTRIBUTE_NAME;
    }
    
    /**
     * Returns a set of the theme attribute names for all properties files used
     * to reference theme data.
     */
    abstract public Set<String> getPropertiesAttributeNames();
    
    
}
