
package org.netbeans.modules.fort.core.editor;

import org.netbeans.editor.Settings;
import org.openide.modules.ModuleInstall;

/**
 * restore coloring in highlighting
 */
public class FRestoreColoring extends ModuleInstall {

    /**
     * restore coloring
     */
    public void restored() {
          Settings.addInitializer(new FSettingsInitializer());
    }                
}