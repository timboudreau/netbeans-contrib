/*
 * Installer.java
 *
 * Install and register FocusTraversalPolicy module
 * with property "FocusTraversalPolicy"
 *
 * @author Michal Hapala, Pavel Stehlik
 */

package org.netbeans.modules.a11ychecker.traverse;

import java.beans.PropertyEditorManager;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle.
 * Remember that an installer is optional and often not needed at all.
 */
public class Installer extends ModuleInstall {
    
    @Override
    public void restored() {
        PropertyEditorManager.registerEditor(java.awt.FocusTraversalPolicy.class, FocusTraversalPolicyEditor.class);
    }
    
}
