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

package org.netbeans.modules.portalpack.portlets.genericportlets.filetype.filters;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Satyaranjan
 */
public class InitParamTableModel extends AbstractTableModel{
    
    private List initParamsList;
    private String[] COLUMN_NAME={"Name","Value"}; //NO I18N
    /** Creates a new instance of InitParamTableModel */
    public InitParamTableModel() {
        initParamsList = new ArrayList();
        
    }

    @Override
    public String getColumnName(int arg0) {
        return COLUMN_NAME[arg0];
    }
    
    public int getRowCount() {
        return initParamsList.size();
       // throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getColumnCount() {
        return COLUMN_NAME.length;
    }

    public Object getValueAt(int arg0, int arg1) {
        if(arg1 == 0)
            return ((InitParam)initParamsList.get(arg0)).getName();
        else
            return ((InitParam)initParamsList.get(arg0)).getValue();
    }

    @Override
    public void setValueAt(Object arg0, int arg1, int arg2) {
        if(initParamsList.size() < arg1 + 1)
            return;
        if(arg2 == 0)
           ((InitParam)initParamsList.get(arg1)).setName((String)arg0);
        else
            ((InitParam)initParamsList.get(arg1)).setValue((String)arg0);
        
        fireTableCellUpdated(arg1, arg2);
    }
       
    public void addRow()
    {
        initParamsList.add(new InitParam("",""));
        fireTableRowsInserted(0,initParamsList.size());
    }
    public void deleteRow(int row)
    {
        if(initParamsList.size() < row + 1)
            return;
        initParamsList.remove(row);
        fireTableRowsDeleted(0, initParamsList.size());
    }

    @Override
    public boolean isCellEditable(int arg0, int arg1) {
        return true;
    }
    
    public List getInitParams()
    {
        return initParamsList;
    }
   
}
