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

package org.netbeans.modules.portalpack.portlets.spring.handlers.form;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.openide.util.NbBundle;

/**
 *
 * @author satyaranjan
 */
public class InputParamTableModel extends AbstractTableModel{
    private List inputParamList;
    private static String NAME = NbBundle.getMessage(InputParamTableModel.class, "LBL_NAME");
    private static String DESC = NbBundle.getMessage(InputParamTableModel.class, "LBL_DESC");
    private static String VALUES = NbBundle.getMessage(InputParamTableModel.class, "LBL_VALUES");
    private static String DATA_TYPE = NbBundle.getMessage(InputParamTableModel.class, "LBL_DATA_TYPE");
    private static String COMP_TYPE = NbBundle.getMessage(InputParamTableModel.class, "LBL_COMP_TYPE");
    
    public static int NAME_COL = 0;
    public static int DESC_COL = 1;
    public static int VALUES_COL = 2;
    public static int DATA_TYPE_COL = 3;
    public static int COMP_TYPE_COL = 4;
    
    private String[] COLUMN_NAME={NAME,DESC,VALUES, DATA_TYPE,COMP_TYPE}; //NO I18N
    /** Creates a new instance of InitParamTableModel */
    public InputParamTableModel() {
        inputParamList = new ArrayList();        
    }

    @Override
    public String getColumnName(int arg0) {
        return COLUMN_NAME[arg0];
    }
    
    public int getRowCount() {
        return inputParamList.size();
       // throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getColumnCount() {
        return COLUMN_NAME.length;
    }

    public Object getValueAt(int arg0, int arg1) {
        if(arg1 == 0)
            return ((InputParam)inputParamList.get(arg0)).getName();
        else if(arg1 == 1)
            return ((InputParam)inputParamList.get(arg0)).getLabel();
        else if(arg1 == 2)
            return ((InputParam)inputParamList.get(arg0)).getEncodedValues();
        else if(arg1 == 3)
            return ((InputParam)inputParamList.get(arg0)).getDataType();
        else 
            return ((InputParam)inputParamList.get(arg0)).getComponentType();
    }

    @Override
    public void setValueAt(Object arg0, int arg1, int arg2) {
        if(inputParamList.size() < arg1 + 1)
            return;
        if(arg2 == 0)
           ((InputParam)inputParamList.get(arg1)).setName((String)arg0);
        else if (arg2 == 1)
            ((InputParam)inputParamList.get(arg1)).setLabel((String)arg0);
        else if (arg2 == 2) {
          //  ((InputParam)inputParamList.get(arg1)).setValues((String[])arg0);
        } else if(arg2 == 3)
            ((InputParam)inputParamList.get(arg1)).setDataType((DataType)arg0);
        else
            ((InputParam)inputParamList.get(arg1)).setComponentType((String)arg0);
        
//        System.out.println(arg0);
        fireTableCellUpdated(arg1, arg2);
    }
       
    public void addRow()
    {
        inputParamList.add(new InputParam("","",new String[]{""},new DataType("Integer","Integer"),"text"));
        fireTableRowsInserted(0,inputParamList.size());
    }
    
    public void fireRowChange() {
        fireTableRowsInserted(0,inputParamList.size());
    }
    
    public InputParam getRowValue(int row) {
        
        if(row >= inputParamList.size())
            return null;
        
        return (InputParam)inputParamList.get(row);
    }
    public void addRow(InputParam ip) {
        inputParamList.add(ip);
        fireTableRowsInserted(0,inputParamList.size());
    }
    public void deleteRow(int row)
    {
        if(inputParamList.size() < row + 1)
            return;
        inputParamList.remove(row);
        fireTableRowsDeleted(0, inputParamList.size());
    }

    @Override
    public boolean isCellEditable(int arg0, int arg1) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex == DATA_TYPE_COL)
            return DataType.class;
        else
            return String.class;
                            
    }
    
    public List getInitParams()
    {
        return inputParamList;
    }
}
