/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.properties.rbe.ui.options;

import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;

public final class ResourceBundleEditorAdvancedOption extends AdvancedOption {

    public String getDisplayName() {
        return NbBundle.getMessage(ResourceBundleEditorAdvancedOption.class, "AdvancedOption_DisplayName_ResourceBundleEditor");
    }

    public String getTooltip() {
        return NbBundle.getMessage(ResourceBundleEditorAdvancedOption.class, "AdvancedOption_Tooltip_ResourceBundleEditor");
    }

    public OptionsPanelController create() {
        return new ResourceBundleEditorOptionsPanelController();
    }
}
