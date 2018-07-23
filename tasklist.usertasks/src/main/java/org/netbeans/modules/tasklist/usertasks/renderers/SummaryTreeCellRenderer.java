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

package org.netbeans.modules.tasklist.usertasks.renderers;

import java.awt.Graphics;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.table.UTBasicTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;

/**
 * Cell renderer for the summary attribute
 * 
 * @author tl
 */
public class SummaryTreeCellRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 1;

    private Font boldFont, normalFont;
    private ImageIcon icon = new ImageIcon();
    
    public SummaryTreeCellRenderer() {
        ImageIcon icon = new ImageIcon();
        
        // see TreeTable.TreeTableCellEditor.getTableCellEditorComponent
        setLeafIcon(icon);
        setOpenIcon(icon);
        setClosedIcon(icon);
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value,
				   boolean selected, boolean expanded,
				   boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded,
            leaf, row, hasFocus);
        if (normalFont == null || !normalFont.equals(tree.getFont())) {
            normalFont = tree.getFont();
            boldFont = normalFont.deriveFont(Font.BOLD);
        }
        if (value instanceof UTBasicTreeTableNode) {
            UTBasicTreeTableNode utl = (UTBasicTreeTableNode) value;
            boolean unmatched = utl instanceof UTTreeTableNode &&
                    ((UTTreeTableNode) utl).isUnmatched();
            UserTask ut = utl.getUserTask();
            setFont(ut.isStarted() ? boldFont : normalFont);
            setText(ut.getSummary());
            setIcon(UserTaskIconProvider.getUserTaskImage(ut, 
                    unmatched));
        } else if (value instanceof AdvancedTreeTableNode) {
            setFont(normalFont);
            if (expanded)
                setIcon(((AdvancedTreeTableNode) value).getOpenIcon());
            else
                setIcon(((AdvancedTreeTableNode) value).getClosedIcon());                
        } else {
            setFont(normalFont);
            setIcon(null);
        }
        return this;
    }
}
