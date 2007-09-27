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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
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
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex.table;
import java.awt.Component;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author Jan Lahoda
 */
public class SortingTable extends JTable {

    private boolean initialized;

    /** Creates a new instance of SortingTable */
    public SortingTable() {
        super();
        getTableHeader().setDefaultRenderer(new SortingHeaderRenderer(getTableHeader().getDefaultRenderer()));
        initialized = true;
        setModel(getModel());
    }
    
    public SortingTable(TableModel model) {
        this();
        setModel(model);
    }
    
    public void setModel(TableModel model) {
        if (!initialized) {
            super.setModel(model);
            return ;
        }
        
        SortingTableModel stm = new SortingTableModel(model);
        if (super.getModel() instanceof SortingTableModel) {
            ((SortingTableModel) super.getModel()).removeMouseListenerFromTable(this);
        }
        super.setModel(stm);
        stm.addMouseListenerToTable(this);
    }
    
    public int[] getSelectedRows() {
        int[] result = super.getSelectedRows();
        int[] rows   = ((SortingTableModel) getModel()).getRows();
        
        if (rows != null) {
            for (int cntr = 0; cntr < result.length; cntr++) {
                result[cntr] = rows[result[cntr]];
            }
        }
        
        return result;
    }
    
    public int getSelectedRow() {
        int result = super.getSelectedRow();
        
        if (result == (-1))
            return result;
        
        int[] rows   = ((SortingTableModel) getModel()).getRows();
        
        if (rows != null)
            return rows[result];
        else
            return result;
    }
    
    public class SortingHeaderRenderer extends DefaultTableCellRenderer {
        
        private static final java.lang.String SORT_ASC_ICON = "org/openide/resources/columnsSortedAsc.gif";
        private static final java.lang.String SORT_DESC_ICON = "org/openide/resources/columnsSortedDesc.gif";
        private javax.swing.table.TableCellRenderer defaultRenderer;
        private SortingHeaderRenderer(TableCellRenderer defaultRenderer) {
            this.defaultRenderer = defaultRenderer;
        }
        /**
         * Overrides superclass method.
         */
        public Component getTableCellRendererComponent(JTable table, java.lang.Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            TableModel tm = table.getModel();
            
            if (!(tm instanceof SortingTableModel))
                return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            SortingTableModel stm = (SortingTableModel) tm;
            
            Component comp = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if ( comp instanceof JLabel ) {
                if ( stm.getSortingColumn() == column ) {
                    ((JLabel)comp).setIcon( getProperIcon( stm.isAscendingSort() ) );
                    ((JLabel)comp).setHorizontalTextPosition( SwingConstants.LEFT );
                    comp.setFont( comp.getFont().deriveFont( Font.BOLD ) );
                }
                else
                    ((JLabel)comp).setIcon( null );
            }
            return comp;
        }
        private ImageIcon getProperIcon(boolean ascending) {
            
            if ( ascending )
                return new ImageIcon( org.openide.util.Utilities.loadImage( SORT_ASC_ICON ) );
            else
                return new ImageIcon( org.openide.util.Utilities.loadImage( SORT_DESC_ICON ) );
        }}
    
}

