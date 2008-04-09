/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.models;

import org.netbeans.spi.viewmodel.ModelListener;
import org.netbeans.spi.viewmodel.TableModel;
import org.netbeans.spi.viewmodel.TreeModel;
import org.netbeans.modules.cnd.profiler.data.Function;

/**
 *
 * @author eu155513
 */
public class TableModelImpl implements TableModel {

    public void addModelListener(ModelListener arg0) {
    }

    public void removeModelListener(ModelListener arg0) {
    }

    public Object getValueAt (Object node, String columnID) {
        try {
            if (node == TreeModel.ROOT) {
                return null;
            }
            if (columnID.equals ("timeID")) {
                if (node instanceof Function) {
                    return ((Function)node).getAttrib("time");
                }
            } else if (columnID.equals ("selftimeID")) {
                if (node instanceof Function) {
                    return ((Function)node).getAttrib("Self time");
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return "";
    }
    
    public boolean isReadOnly (Object node, String columnID) {
        return true;
    }
    
    public void setValueAt (Object node, String columnID, Object value) {
    }
}
