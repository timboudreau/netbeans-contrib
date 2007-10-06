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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import org.netbeans.modules.tasklist.core.table.SortingModel;

import org.netbeans.modules.tasklist.usertasks.util.UTUtils;

/**
 * A table header that can work together with SortingModel
 *
 * @author tl
 */
public class SortableTableHeader extends JTableHeader {
    private static final long serialVersionUID = 1;

    /**
     * Constructs a <code>SortableTableHeader</code> with a default 
     * <code>TableColumnModel</code>.
     *
     * @see #createDefaultColumnModel
     */
    public SortableTableHeader() {
	this(null);
    }

    /**
     * Constructs a <code>SortableTableHeader</code> which is initialized with
     * <code>cm</code> as the column model.  If <code>cm</code> is
     * <code>null</code> this method will initialize the table header
     * with a default <code>TableColumnModel</code>.
     *
     * @param cm	the column model for the table
     * @see #createDefaultColumnModel
     */
    public SortableTableHeader(TableColumnModel cm) {
	super(cm);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                mouseClick(e);
            }
        });
        setDefaultRenderer(new SortingHeaderRenderer());
    }
    
    /**
     * Mouse click handler
     *
     * @param e an event
     */
    private void mouseClick(MouseEvent e) {
        int col = SortableTableHeader.this.columnAtPoint(e.getPoint());
        if (col == -1) 
            return;
        
        JTable t = SortableTableHeader.this.getTable();
        if (!(t instanceof TreeTable)) 
            return;
        
        SortingModel sm = ((TreeTable) t).getSortingModel();
        if (sm == null)
            return;
        
        int index = getColumnModel().getColumn(col).getModelIndex();
        if (!sm.isColumnSortable(index))
            return;

        int cur = sm.getSortedColumn();
        if (index == cur) {
            if (!sm.isSortOrderDescending())
                sm.setSortOrderDescending(true);
            else
                sm.setSortedColumn(-1);
        } else {
            sm.setSortOrderDescending(false);
            sm.setSortedColumn(index);
        }
    }
}
