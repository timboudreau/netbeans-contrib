/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.java.highlightboxingunboxingvarargs.impl;

import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;

public final class HighlightboxingunboxingvarargsAdvancedOption extends AdvancedOption {

    public String getDisplayName() {
        return NbBundle.getMessage(HighlightboxingunboxingvarargsAdvancedOption.class, "AdvancedOption_DisplayName_Highlightboxingunboxingvarargs");
    }

    public String getTooltip() {
        return NbBundle.getMessage(HighlightboxingunboxingvarargsAdvancedOption.class, "AdvancedOption_Tooltip_Highlightboxingunboxingvarargs");
    }

    public OptionsPanelController create() {
        return new HighlightboxingunboxingvarargsOptionsPanelController();
    }
}
