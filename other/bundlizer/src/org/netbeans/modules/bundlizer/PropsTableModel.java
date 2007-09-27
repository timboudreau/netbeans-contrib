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
