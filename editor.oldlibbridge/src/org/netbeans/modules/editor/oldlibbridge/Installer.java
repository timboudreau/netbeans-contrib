package org.netbeans.modules.editor.oldlibbridge;

import java.util.logging.Logger;
import org.netbeans.modules.editor.lib2.DocumentsRegistry;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    private static final Logger LOG = Logger.getLogger(Installer.class.getName());
    
//    /** The flag that determines if the new highlighting API should be used. */
//    private static final boolean HIGHLIGHTING = Boolean.getBoolean("org-netbeans-spi-editor-highlighting");
    
    public void restored() {
//        LOG.warning("Highlighting is " + (HIGHLIGHTING ? "on" : "off") + ".");
//        if (HIGHLIGHTING) {
//            DocumentsRegistry.addPropertyChangeListener(HighlightingDrawLayer.INJECTOR);
//        }
    }
    
}
