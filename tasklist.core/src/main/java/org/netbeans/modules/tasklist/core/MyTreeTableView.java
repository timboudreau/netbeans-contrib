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

package org.netbeans.modules.tasklist.core;

import java.awt.Dimension;
import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.openide.explorer.view.TreeTableView;

/**
 * Contains fixes that need access to final protected methods.
 */
public final class MyTreeTableView extends TreeTableView {

    private static final long serialVersionUID = 1;

    public MyTreeTableView() {

        JTable table = treeTable;
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // No, I can use TreeTableView.setTableAutoResizeMode(int) for this
        
        // No white clipping lines on selected table rows: reduce separator
        // to 0. That means text may touch but HIE prefers this.
        table.setIntercellSpacing(new Dimension(0, table.getRowMargin()));
        double height = new JLabel("Z").getPreferredSize().getHeight();  // NOI18N
        int intheight = (int) height;
        table.setRowHeight(intheight);

            /* Issue 23993 was fixed which probably makes this unnecessary:
// Grid color: HIE's asked for (230,230,230) but that seems troublesome
// since we'd have to make a GUI for customizing it. Instead, go
// with Metal's secondary2, since for alternative UIs this will continue
// to look good (and it's customizable by the user). And secondary2
// is close to the request valued - it's (204,204,204).
table.setGridColor((java.awt.Color)javax.swing.UIManager.getDefaults().get("Label.background")); // NOI18N
             */
    }
    
    public JTree getTree() {
        return tree;
    }
    
    public JTable getTable() {
        return treeTable;
    }
    
    public TableModel getModel() {
        return treeTable.getModel();
    }
    
    public TableColumnModel getHeaderModel() {
        return treeTable.getTableHeader().getColumnModel();
    }
}

