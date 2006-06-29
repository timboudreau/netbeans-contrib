/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is the Accelerators module.
 * The Initial Developer of the Original Software is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.
 *
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.terminal;

import java.awt.Image;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Andrei Badea
 */
public class TerminalOptionsBeanInfo extends SimpleBeanInfo {

    // XXX not a very nice icon
    private static final String ACCELERATORS_ICON = "org/netbeans/modules/accelerators/terminal/resources/terminal.gif";// NOI18N
    
    public Image getIcon(int iconKind) {
        if (iconKind == ICON_COLOR_16x16) {
            return Utilities.loadImage(ACCELERATORS_ICON);
        }
        return null;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor[] props = new PropertyDescriptor[1];
            props[0] = new PropertyDescriptor(TerminalOptions.PROP_TERMINAL_COMMAND, TerminalOptions.class); // NOI18N
            props[0].setDisplayName(NbBundle.getMessage(TerminalOptionsBeanInfo.class, "LBL_TerminalCommand"));
            props[0].setShortDescription(NbBundle.getMessage(TerminalOptionsBeanInfo.class, "HINT_TerminalCommand"));
            return props;
        } catch (IntrospectionException e) {
            throw new AssertionError(e);
        }
    }
}
