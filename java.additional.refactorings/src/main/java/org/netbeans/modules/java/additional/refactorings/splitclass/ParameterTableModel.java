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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.splitclass;

import java.awt.Toolkit;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.openide.util.NbBundle;

/**
 * Table model for UIs which let users rearrange parameters.
 *
 * @author Tim Boudreau
 */
final class ParameterTableModel implements TableModel {

    final List <Parameter> descs;
    final UI ui;
    ParameterTableModel (List <Parameter> descs, UI ui) {
        this.descs = descs;
        this.ui = ui;
    }

    List <Parameter> getParameters() {
        return descs;
    }

    void add (Parameter param, int ix) {
        if (ix < descs.size()) ix++;
        descs.add(ix, param);
        TableModelEvent tme = new TableModelEvent (this, ix-1, descs.size());
        fire (tme);
        JTable table = ui.getTable();
        table.getSelectionModel().setAnchorSelectionIndex(ix);
        table.getSelectionModel().setLeadSelectionIndex(ix);
        table.requestFocus();
        table.editCellAt(ix, 1);
    }

    void remove (int ix) {
        descs.remove (ix);
        TableModelEvent tme = new TableModelEvent (this, ix, descs.size() + 1);
        fire (tme);
        if (descs.size() > 0) {
            ix = Math.max (0, ix);
            JTable table = ui.getTable();
            table.getSelectionModel().setAnchorSelectionIndex(ix);
            table.getSelectionModel().setLeadSelectionIndex(ix);
        }
    }

    void moveUp (int ix) {
        if (ix == 0) {
            return;
        }
        Parameter p = descs.remove (ix);
        descs.add (ix - 1, p);
        TableModelEvent evt = new TableModelEvent (this);
        fire (evt);
        JTable table = ui.getTable();
        table.getSelectionModel().setAnchorSelectionIndex(ix - 1);
        table.getSelectionModel().setLeadSelectionIndex(ix - 1);
    }

    void moveDown (int ix) {
        if (ix >= descs.size()) {
            return;
        }
        Parameter p = descs.remove (ix);
        descs.add (Math.min (descs.size(), ix + 1), p);
        TableModelEvent evt = new TableModelEvent (this);
        fire (evt);
        JTable table = ui.getTable();
        table.getSelectionModel().setAnchorSelectionIndex(ix + 1);
        table.getSelectionModel().setLeadSelectionIndex(ix + 1);
    }

    public int getRowCount() {
        return descs.size();
    }

    public int getColumnCount() {
        return 3;
    }

    public String getColumnName(int columnIndex) {
        String key = columnIndex == 0 ?
                "LBL_NAMES" : columnIndex == 1 ? "LBL_TYPES"
                : "LBL_DEFAULT_VALUE"; //NOI18N
        return NbBundle.getMessage(ParameterTableModel.class, key);
    }

    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? String.class :
            String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2 ? descs.get(rowIndex).isNew() : true;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex > descs.size()) {
            return null;
        }

        String result = columnIndex == 0 ? descs.get(rowIndex).getName() :
            columnIndex == 1 ?
            descs.get(rowIndex).getTypeName() : descs.get(rowIndex).getDefaultValue();
        if (result == null) {
            result = "";
        }
        return result;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String s = aValue.toString();
        Parameter p = descs.get(rowIndex);
        switch (columnIndex) {
        case 0 :
            p.setName(s);
            break;
        case 1 :
            p.setTypeName(s);
            break;
        case 2:
            if (p.isNew()) {
                p.setDefaultValue(s);
            } else {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            break;
        default :
            throw new IllegalArgumentException("" + columnIndex);
        }
        fire(rowIndex, columnIndex);
        if (ui.getProblemText() == null) {
            restoreOriginalsIfPossible(rowIndex);
        }
    }

    private void restoreOriginalsIfPossible(int ix) {
        //If the type name and type have become the same as one we originally
        //had, replce with that element
        Parameter p = descs.get(ix);
        Parameter orig = null;
        String typeName = p.getTypeName();
        String name = p.getName();
        if (name != null && typeName != null) {
            for (Parameter old : ui.getOriginals()) {
                if (name.equals(old.getName()) && typeName.equals(old.getTypeName())) {
                    orig = old;
                    break;
                }
            }
        }
        if (orig != null) {
            descs.remove (ix);
            descs.add (ix, orig);
        }
    }

    private void fire (int row, int col) {
        TableModelEvent evt = new TableModelEvent(this, row, row, col);
        fire (evt);
    }

    private void fire (TableModelEvent evt) {
        TableModelListener[] l = listeners.toArray (new TableModelListener[0]);
        for (int i = 0; i < l.length; i++) {
            l[i].tableChanged(evt);
        }
        JTable table = ui.getTable();
        table.invalidate();
        table.revalidate();
        table.repaint();
        ui.change();
    }

    private List <TableModelListener> listeners =
            Collections.<TableModelListener>synchronizedList (new
            LinkedList<TableModelListener>());

    public void addTableModelListener(TableModelListener l) {
        listeners.add (l);
    }

    public void removeTableModelListener(TableModelListener l) {
        listeners.remove (l);
    }
    
    public interface UI {
        JTable getTable();
        void change();
        String getProblemText();
        List <Parameter> getOriginals();
    }

}
