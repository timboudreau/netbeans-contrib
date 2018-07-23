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

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column;
import org.openide.util.NbBundle;

/**
 *
 * @author satyaranjan
 */
public class ColumnsTableModel implements TableModel<Column>{

    private transient List<Column> columns;
    //private Entity entity;
    
    public ColumnsTableModel(List<Column> columns) {
        
        this.columns = columns;
    }
    
    public int getRowCount() {
        if(columns == null)
            return 0;
        return columns.size();
    }
    
    public int getColumnCount() {
        return 2;
    }
    
    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
        case 0:
            return NbBundle.getMessage(ServicesTableModel.class, "LBL_Column_Name");
        case 1:
            return NbBundle.getMessage(ServicesTableModel.class, "LBL_Column_Type");
        default:
            throw new IllegalArgumentException("");
        }
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch(columnIndex) {
        case 0:
            return false;
        case 1:
            return false;
        default:
            return false;
        }
    }
    
    public String getValueAt(int rowIndex, int columnIndex) {
        
        if(columns == null) return "";
        if (rowIndex>=0 && rowIndex<getRowCount()) {
            switch(columnIndex) {
            case 0:
                return ((Column)columns.get(rowIndex)).getName();//getUserObject(rowIndex).getName();
            case 1:
                return ((Column)columns.get(rowIndex)).getType();//getUserObject(rowIndex).getRemoteService();
            default:
                throw new IllegalArgumentException("");
            }
        }
        return null;
    }
    
    public void setValueAt(String aValue, int rowIndex, int columnIndex) {
        if(columns == null)
            return;
        if (rowIndex>=0 && rowIndex<getRowCount()) {
            switch(columnIndex) {
            case 0:
                //validate aValue
                //getUserObject(rowIndex).setName(aValue);
                ((Column)columns.get(rowIndex)).setName(aValue);
                
                break;
            case 1:
                ((Column)columns.get(rowIndex)).setType(aValue);
            default:
                throw new IllegalArgumentException("");
            }
        }
    }
    
    public void setColumns(List<Column> columns) {
        //TODO this.columns = columns;
        if(columns == null) {
            this.columns = columns;
            return;
        }
        if(this.columns == null)
            this.columns = new ArrayList();
        this.columns.clear();
        for(Column c:columns) {
            this.columns.add(c);
        }
    }
    public void addRow(Column object) {
        columns.add(object);
    }
    
    public void removeRow(int index) {
        if(columns == null)
            return;
        
        columns.remove(index);
    }

    public Column getUserObject(int rowIndex) {
        return (Column)columns.get(rowIndex);
    }
}
