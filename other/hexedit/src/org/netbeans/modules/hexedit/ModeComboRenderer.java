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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
    private static final StringINT = Util.getMessage("INT");
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
