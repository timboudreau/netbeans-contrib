/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * ScrollbarFactory.java
 *
 * Created on May 9, 2004, 7:19 PM
 */

package org.netbeans.swing.scrollbars.api;

import org.netbeans.swing.scrollbars.impl.GenericMarkedScrollbar;
import org.netbeans.swing.scrollbars.impl.MetalMarkedScrollBarUI;
import org.netbeans.swing.scrollbars.impl.WindowsMarkedScrollBarUI;
import org.netbeans.swing.scrollbars.spi.MarkingModel;

import javax.swing.*;

/**
 * Factory that will produce an appropriate scrollbar for a marking model.
 *
 * @author  Tim Boudreau
 */
public class ScrollbarFactory {
    /** Creates a new instance of ScrollbarFactory */
    private ScrollbarFactory() {
    }
    
    public static JScrollBar createScrollbar (MarkingModel mdl) {
        JScrollBar result;
        String id = UIManager.getLookAndFeel().getID();
        if ("Windows".equals(id)) {
            result = new JScrollBar();
            result.setUI (new WindowsMarkedScrollBarUI (mdl));
        } else if ("Metal".equals(id)) {
            result = new JScrollBar();
            result.setUI (new MetalMarkedScrollBarUI (mdl));
        } else {
            result = new GenericMarkedScrollbar(mdl);
        }
        return result;
    }
}
