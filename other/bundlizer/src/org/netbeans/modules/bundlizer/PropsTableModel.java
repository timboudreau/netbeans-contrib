/*
*                 Sun Public License Notice
*
* The contents of this file are subject to the Sun Public License
* Version 1.0 (the "License"). You may not use this file except in
* compliance with the License. A copy of the License is available at
* http://www.sun.com/
*
* The Original Code is NetBeans. The Initial Developer of the Original
* Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
* Microsystems, Inc. All Rights Reserved.
*/
/*
 * PropsTableModel.java
 *
 * Created on April 30, 2004, 3:36 PM
 */

package org.netbeans.modules.bundlizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
//import java.util.Properties;
import java.util.Set;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * A table model representing a bundle and the keys that should be deleted
 * from it
 *
 * @author  Tim Boudreau
 */
public class PropsTableModel implements TableModel {
    private Map keysToFiles;
    private Properties props;
    private ArrayList listeners = new ArrayList();
    /** Creates a new instance of PropsTableModel */
    public PropsTableModel(Properties props, Map keysToFiles) {
        this.props = props;
        this.keysToFiles = keysToFiles;
    }
    
    public synchronized void addTableModelListener(TableModelListener l) {
        removeTableModelListener(l);
    }
    
    public Class getColumnClass(int column) {
        if (column != 1) {
            return String.class;
        } else {
            return Boolean.class;
        }
    }
    
    public int getColumnCount() {
        return 4;
    }
    
    public String getColumnName(int param) {
        switch (param) {
            case 0 : return "Bundle keys";
            case 1 : return "Keep";
            case 2 : return "Used by";
            case 3 : return "Value";
            default :
                throw new IllegalArgumentException();
        }
    }
    
    public int getRowCount() {
        return props.size();
    }
    
    public Object getValueAt(int row, int col) {
        Object key = new ArrayList (props.keySet()).get(row);
        switch (col) {
            case 0 : return key;
            case 1 : 
                boolean result = keyIsUsed (key);
                if (setValues.contains(key)) {
                    result = !result;
                }
                return result ? Boolean.TRUE : Boolean.FALSE;
            case 2 : return keysToFiles.get(key);
            case 3 : return props.get(key);
            default :
                throw new IllegalArgumentException();
        }
    }
    
    public Set getKeysToRemove () {
        HashSet result = new HashSet();
        int size = getRowCount();
        for (int i=0; i < size; i++) {
            if (Boolean.FALSE.equals(getValueAt(i, 1))) {
                result.add (getValueAt(i, 0));
            }
        }
        return result;
    }
    
    private boolean keyIsUsed (Object key) {
        return keysToFiles.containsKey(key);
    }
    
    public boolean isCellEditable(int row, int column) {
        return column == 1;
    }
    
    public synchronized void removeTableModelListener(TableModelListener l) {
        listeners.add (l);
    }
    
    public boolean hasChanges() {
        boolean result = false;
        int size = getRowCount();
        for (int i=0; i < size; i++) {
            result |= Boolean.FALSE.equals(getValueAt(i, 1));
            if (result) break;
        }
        return result;
    }
    
    private void fire (TableModelEvent tme) {
        TableModelListener[] tml = null;
        synchronized (this) {
            tml = new TableModelListener[listeners.size()];
            tml = (TableModelListener[]) listeners.toArray(tml);
        }
        for (int i=0; i < tml.length; i++) {
            tml[i].tableChanged(tme);
        }
    }
    
    private Set setValues = new HashSet();
    public void setValueAt(Object obj, int row, int col) {
        if (col == 1) {
            Object key = getValueAt(row, 0);
            if (!obj.equals(getValueAt(row, col))) {
                if (setValues.contains(key)) {
                    setValues.remove(key);
                } else {
                    setValues.add(key);
                }
                TableModelEvent tme = new TableModelEvent (this, row, row, col, 
                    TableModelEvent.UPDATE);
                fire (tme);
            }
        }
    }
    
}
