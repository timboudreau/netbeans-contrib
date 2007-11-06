/*
 * ConfigTableModel.java
 *
 * Created on 25. øíjen 2007, 12:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openoffice.config;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author sa
 */
public class ConfigTableModel extends AbstractTableModel {
    
    private ArrayList<ConfigValue> values = new ArrayList<ConfigValue>();
    
    public int getRowCount() {
        return values.size();
    }

    public int getColumnCount() {
        return 3;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if( rowIndex < 0 || rowIndex >= values.size() )
            return null;
        ConfigValue val = values.get( rowIndex );
        switch( columnIndex ) {
            case 0:
                return val.getConfigPath();
            case 1: 
                return val.getSharedValue();
            case 2: {
                Object shared = val.getSharedValue();
                Object user = val.getUserValue();
                if( null != shared && null != user && shared.toString().equals( user.toString() ) )
                    return null;
                return val.getUserValue();
            }
        }
        return null;
    }
    
    void add( String configPath, Object sharedValue, Object userValue ) {
        ConfigValue val = new ConfigValue( configPath, sharedValue, userValue );
        values.add( val );
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        JOptionPane.showMessageDialog( null, "Editing is not supported yet." );
    }

    public String getColumnName(int column) {
        switch( column ) {
            case 0:
                return "Configuration Path";
            case 1:
                return "Shared Value";
            case 2:
                return "User Value";
        }
        return super.getColumnName(column);
    }
}
