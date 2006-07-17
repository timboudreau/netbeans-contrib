/*
The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License.

You can obtain a copy of the License at http://www.netbeans.org/cddl.html
or http://www.netbeans.org/cddl.txt.

When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]" */
package org.netbeans.modules.genericnavigator;

import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;

public final class GenericNavigatorOptionsAdvancedOption extends AdvancedOption {

    public String getDisplayName() {
        return NbBundle.getMessage(GenericNavigatorOptionsAdvancedOption.class,
                "AdvancedOption_DisplayName"); //NOI18N
    }

    public String getTooltip() {
        return NbBundle.getMessage(GenericNavigatorOptionsAdvancedOption.class,
                "AdvancedOption_Tooltip"); //NOI18N
    }

    public OptionsPanelController create() {
        return new GenericNavigatorOptionsOptionsPanelController();
    }

}
