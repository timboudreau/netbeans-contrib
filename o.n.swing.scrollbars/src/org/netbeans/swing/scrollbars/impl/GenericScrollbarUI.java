/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.swing.scrollbars.impl;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;

/**
 * A ScrollBarUI intentionally devoid of any painting or other logic.  GenericMarkedScrollbar will delegate
 * to its embedded scrollbar for scrolling functionality.
 */
public class GenericScrollbarUI extends ScrollBarUI {
    private static ScrollBarUI instance;

    public static ComponentUI createUI (JComponent c) {
        if (instance == null) {
            instance = new GenericScrollbarUI();
        }
        return instance;
    }

}
