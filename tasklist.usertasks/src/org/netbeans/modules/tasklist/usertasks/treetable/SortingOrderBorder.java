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

package org.netbeans.modules.tasklist.usertasks.treetable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.netbeans.modules.tasklist.core.table.SortingModel;

/**
 * Cell renderer for sorting column header.
 * Originally copied from org.openide.explorer.view.TreeTableView
 */
public class SortingOrderBorder implements Border {
    private static final long serialVersionUID = 1;

    /** 0 - not sorted, 1 - ascending, 2 - descending */
    public int status = 0;
    
    private static Image SORT_DESC_ICON =
        org.openide.util.Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/treetable/columnsSortedDesc.gif"); // NOI18N
    private static Image SORT_ASC_ICON = 
        org.openide.util.Utilities.loadImage(
        "org/netbeans/modules/tasklist/usertasks/treetable/columnsSortedAsc.gif"); // NOI18N

    public void paintBorder(Component c, Graphics g, int x, int y, 
            int width, int height) {
        Image img = null;
        switch (status) {
            case 1: 
                img = SORT_ASC_ICON;
                break;
            case 2: 
                img = SORT_DESC_ICON;
                break;
        }
        if (img != null)
            g.drawImage(img, width - img.getWidth(c), 
                    (height - img.getHeight(c)) / 2, null);
    }

    public Insets getBorderInsets(Component cmp) {
        if (status == 0)
            return new Insets(0, 0, 0, 0);
        else
            return new Insets(0, 0, 0, 16);
    }

    public boolean isBorderOpaque() {
        return false;
    }
}


