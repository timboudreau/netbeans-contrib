/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
