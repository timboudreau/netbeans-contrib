/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * ModeComboRenderer.java
 *
 * Created on April 27, 2004, 11:24 AM
 */

package org.netbeans.modules.hexedit;

import org.netbeans.modules.hexedit.HexTableModel;

import javax.swing.*;
import java.awt.*;

/**
 * Cell renderer for the mode combo box which shows the names of the types
 *
 * @author  Tim Boudreau
 */
class ModeComboRenderer implements ListCellRenderer {
    private static final DefaultListCellRenderer ren = new DefaultListCellRenderer();
    private static final String BYTE = Util.getMessage("BYTE");
    private static final String INT = Util.getMessage("INT");
    private static final String SHORT = Util.getMessage("SHORT");
    private static final String LONG = Util.getMessage("LONG");
    private static final String CHAR = Util.getMessage("CHAR");

    /** Creates a new instance of ModeComboRenderer */
    public ModeComboRenderer() {
    }
    
    public Component getListCellRendererComponent(JList jList, Object obj, 
                                int param, boolean param3, boolean param4) {
        Component result = ren.getListCellRendererComponent (jList, obj, param, param3, param4);
        int i = ((Integer) obj).intValue();
        switch (i) {
            case HexTableModel.MODE_BYTE : ren.setText(BYTE);
                break;
            
            case HexTableModel.MODE_INT : ren.setText (INT);
                break;
                
            case HexTableModel.MODE_LONG : ren.setText (LONG);
                break;
            
            case HexTableModel.MODE_CHAR : ren.setText (CHAR);
                break;
            
            case HexTableModel.MODE_SHORT : ren.setText (SHORT);
                break;
                
            default :
                throw new IllegalStateException("Invalid mode: " + i);            
        }
        return result;
    }
    
}
