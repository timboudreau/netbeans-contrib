package org.netbeans.swing.scrollbars.impl;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;

/**
 * A ScrollBarUI intentionally devoid of any painting or other logic.  GenericMarkedScrollbar will delegate
 * to its embedded scrollbar for scrolling functionality.
 */
class GenericScrollbarUI extends ScrollBarUI {
    private static ScrollBarUI instance;

    public static ComponentUI createUI (JComponent c) {
        if (instance == null) {
            instance = new GenericScrollbarUI();
        }
        return instance;
    }

}
