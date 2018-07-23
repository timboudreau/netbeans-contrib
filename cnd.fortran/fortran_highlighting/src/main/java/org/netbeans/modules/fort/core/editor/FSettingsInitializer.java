
package org.netbeans.modules.fort.core.editor;

import java.util.Map;
import org.netbeans.editor.Settings;

/**
 * Class to initialize settings in fortran support module
 * @author Andrey Gubichev
 */
public class FSettingsInitializer extends Settings.AbstractInitializer {
    /**
     * initializer's name
     */
    public static final String NAME = "fortran-settings-initializer"; 

    /**
     * creates a new instance of FSettingsInitializer
     */
    public FSettingsInitializer() {
        super(NAME);
    }

    /**
     * updates map of settings
     */
    public void updateSettingsMap(Class kitClass, Map settingsMap) {
    }        
}