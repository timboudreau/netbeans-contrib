/*
 *                         Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is the Simple Edit Module.
 * The Initial Developer of the Original Code is Internet Solutions s.r.o.
 * Portions created by Internet Solutions s.r.o. are
 * Copyright (C) Internet Solutions s.r.o..
 * All Rights Reserved.
 * 
 * Contributor(s): David Strupl.
 */
package cz.solutions.simpleedit;

import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;

/**
 * @author David Strupl
 */
public final class ClosingTabsAdvancedOption extends AdvancedOption {
    
    public String getDisplayName() {
        return NbBundle.getMessage(ClosingTabsAdvancedOption.class, "AdvancedOption_DisplayName");
    }
    
    public String getTooltip() {
        return NbBundle.getMessage(ClosingTabsAdvancedOption.class, "AdvancedOption_Tooltip");
    }
    
    public OptionsPanelController create() {
        return new ClosingTabsOptionsPanelController();
    }
    
}
