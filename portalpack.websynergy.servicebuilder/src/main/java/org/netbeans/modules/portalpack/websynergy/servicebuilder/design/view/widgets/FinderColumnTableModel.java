/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.websynergy.servicebuilder.design.view.widgets;

import java.util.List;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.FinderColumn;
import org.openide.util.NbBundle;

/**
 *
 * @author satyaranjan
 */
public class FinderColumnTableModel implements TableModel<FinderColumn> {
    
     //private transient List<FinderColumn> finderColumns;
    private transient Finder finder;

    public FinderColumnTableModel(Finder finder) {
        this.finder = finder;
        
    }
    
    public int getRowCount() {
        return finder.getFinderColumn().length;
    }
    
    public int getColumnCount() {
        return 3;
    }
    
    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
        case 0:
            return NbBundle.getMessage(FinderColumnTableModel.class, "LBL_Column_Name");
        case 1:
            return NbBundle.getMessage(FinderColumnTableModel.class, "LBL_Comparator_Type");
        case 2:
               return NbBundle.getMessage(FinderColumnTableModel.class, "LBL_Case_Sensitive");
        default:
            throw new IllegalArgumentException("");
        }
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    
    public String getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex>=0 && rowIndex<getRowCount()) {
            switch(columnIndex) {
            case 0:
                return finder.getFinderColumn(rowIndex).getName();
            case 1:
                return finder.getFinderColumn(rowIndex).getComparator();
            case 2:
                return finder.getFinderColumn(rowIndex).getCaseSensitive();
            default:
                throw new IllegalArgumentException("");
            }
        }
        return null;
    }
    
    public void setValueAt(String aValue, int rowIndex, int columnIndex) {
        if (rowIndex>=0 && rowIndex<getRowCount()) {
            switch(columnIndex) {
            case 0:
                finder.getFinderColumn(rowIndex).setName(aValue);
                break;
            case 1:
                finder.getFinderColumn(rowIndex).setComparator(aValue);
            case 2:
                finder.getFinderColumn(rowIndex).setCaseSensitive(aValue);
            default:
                throw new IllegalArgumentException("");
            }
        }
    }

    public FinderColumn getUserObject(int rowIndex) {
        if(rowIndex == -1 || rowIndex > getRowCount())
            return null;
        return finder.getFinderColumn(rowIndex);
    }

    public void addRow(FinderColumn finderColumn) {
        finder.addFinderColumn(finderColumn);
    }

    public void removeRow(int index) {
        if(finder == null)
            return;
        if(index == -1 || index > getRowCount())
            return;
        FinderColumn finderCol = finder.getFinderColumn(index);
        if(finderCol != null)
            finder.removeFinderColumn(finderCol);
    }
}
