package org.netbeans.modules.scala.project;

import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * Utility methods for the module.
 *
 * @author Martin Krauskopf
 */
public final class Util {
    
    public static final Logger LOG = Logger.getLogger("org.netbeans.modules.scala.project"); // NOI18N
    
    private Util() {}
    
    /** Returns preferences to be used within the project. */
    public static Preferences getPreferences() {
        return Preferences.userNodeForPackage(Util.class);
    }
    
}
