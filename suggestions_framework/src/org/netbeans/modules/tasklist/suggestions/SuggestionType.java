/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.suggestions;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ResourceBundle;
import org.openide.util.NbBundle;

/**
 * This class represents a SuggestionType. This concept is described
 * in the TaskList api (org.netbeans.api.tasklist) documentation.
 *
 * @author Tor Norbye
 */

final public class SuggestionType {

    /**
     * Create a SuggestionType
     * @param name The name which identifies this Suggestion Type
     * @param bundle The file where the localized name for the type is found
     * @param key The key which holds the localized name in the bundle file
     * @param icon A url to the icon to be used by default for these suggestions */
    SuggestionType(String name, String bundle, String key, URL icon) {
        this.name = name;
        this.bundle = bundle;
        this.key = key;
        this.icon = icon;
    }

    /** @return The name which identifies this Suggestion Type */
    String getName() {
        return name;
    }
        
    /** @return The file where the localized name for the type is found */
    String getBundle() {
        return bundle;
    }

    /** @return The key which holds the localized name in the bundle file */
    String getKey() {
        return key;
    }

    /** @return A url to the icon to be used by default for these suggestions */
    URL getIcon() {
        return icon;
    }

    /** Gets Image which represents the icon. */
    Image getIconImage() {
        if ((img == null) && (icon != null)) {
            img = Toolkit.getDefaultToolkit().getImage(icon);
        }
        return img;
    }

    /** Return the name of the Suggestion type - localized. */
    String getLocalizedName() {
        if (localizedName == null) {
            ResourceBundle rb = NbBundle.getBundle(bundle);
            localizedName = rb.getString(key);
            if (localizedName == null) {
                localizedName = "";
            }
        }
        return localizedName;
    }
    
   /** Return a description of this object. Format may change any time
     * and is not localized. Do not depend on its content or format. */
    public String toString() {
        return "SuggestionType[name=" + name + ",bundle=" + bundle + // NOI18N
            ",key=" + key + ",icon=" + icon +"]"; // NOI18N
    }    
    
    private String name;
    private String bundle;
    private String key;
    private URL icon;
    private Image img = null;
    private String localizedName = null;
}
