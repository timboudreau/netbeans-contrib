/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.searchandreplace;

import java.awt.Component;
import java.awt.Image;
import java.awt.Point;

import java.beans.BeanInfo;

import java.io.File;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.netbeans.modules.searchandreplace.model.Item;
import org.netbeans.modules.searchandreplace.model.ItemStateObserver;

import org.openide.awt.HtmlRenderer;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.NbBundle;

final class ItemTableModel implements TableModel, ItemStateObserver, TableCellRenderer {
    private Item[] items;
    private boolean canReplace;
    ItemTableModel(Item[] items, boolean canReplace) {
        this.items = items;
        this.canReplace = canReplace;
        box.setToolTipText(NbBundle.getMessage(ItemTableModel.class,
                "TIP_CHECKBOX")); //NOI18N
        box.setHorizontalAlignment(SwingConstants.CENTER);
    }

    Item[] getItems() {
        return items;
    }

    public int getRowCount() {
        return items.length;
    }

    public int getColumnCount() {
        return canReplace ? 3 : 2;
    }

    private final String NAME_STRING =
            NbBundle.getMessage (SearchPreview.class, "LBL_FileName"); //NOI18N

    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
            case 0:
                return NAME_STRING;
            case 1:
                return canReplace ?
                    NbBundle.getMessage (SearchPreview.class,
                        "LBL_Replace") : //NOI18N
                    NbBundle.getMessage (SearchPreview.class, "LBL_Path"); //NOI18N
            case 2:
                if (canReplace) {
                    return NbBundle.getMessage (SearchPreview.class,
                            "LBL_Path"); //NOI18N
                }
            default:
                throw new IllegalArgumentException("" + columnIndex);
        }
    }

    public Class getColumnClass(int columnIndex) {
        switch(columnIndex) {
            case 0:
                return String.class;
            case 1:
                return canReplace ? Boolean.TYPE : String.class;
            case 2:
                if (canReplace) {
                    return String.class;
                }
            default:
                throw new IllegalArgumentException("" + columnIndex);
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        boolean result = canReplace && columnIndex == 1;
        if (result) {
            Item item = items[rowIndex];
            result = item.isValid() && !item.isReplaced();
        }
        return result;
    }

    private static String locString(Point p) {
        return NbBundle.getMessage(ItemTableModel.class, "FMT_Location", //NOI18N
                new Object[] { Integer.toString(p.x), Integer.toString(p.y) });
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Item item = items[rowIndex];
        String clrString = item.isValid() ? null :
            "<font color='#FF0000'>"; //NOI18N

        switch(columnIndex) {
            case 0:
                return (clrString == null ? "" : clrString) + item.getName()
                    + "<font color='!controlShadow'>" + locString ( //NOI18N
                    item.getLocation());
            case 1:
                return canReplace ? (Object) (item.isShouldReplace() ?
                        Boolean.TRUE : Boolean.FALSE)
                        : item.getDescription();
            case 2:
                if (canReplace) {
                    return (clrString == null ? "" : clrString) + //NOI18N
                            item.getDescription();
                }
            default:
                throw new IllegalArgumentException("" + columnIndex); //NOI18N
        }
    }

    private boolean shiftedEdit = false;
    void setShiftedEdit (boolean val) {
        shiftedEdit = val;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (canReplace && columnIndex == 1 && aValue instanceof Boolean) {
            Item item = items[rowIndex];
            if (!shiftedEdit) {
                item.setShouldReplace(Boolean.TRUE.equals(aValue));
            } else {
                item.setEntireFileShouldReplace(Boolean.TRUE.equals(aValue));
            }
        }  else {
            throw new IllegalArgumentException();
        }
    }

    private List listeners = Collections.synchronizedList (new LinkedList());
    public void addTableModelListener(TableModelListener l) {
        listeners.add (l);
    }

    public void removeTableModelListener(TableModelListener l) {
        listeners.remove (l);
    }

    private void fire (TableModelEvent e) {
        TableModelListener[] l = (TableModelListener[]) listeners.toArray(
                new TableModelListener[listeners.size()]);

        for (int i=0; i < l.length; i++) {
            l[i].tableChanged(e);
        }
    }

    private final JCheckBox box = new JCheckBox();
    private final HtmlRenderer.Renderer renderer = HtmlRenderer.createRenderer();
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component result;
        Item item = items[row];
        if (value instanceof Boolean) {
            result = box;
            box.setSelected (Boolean.TRUE.equals(value));
            box.setBackground (isSelected ? table.getSelectionBackground() :
                table.getBackground());
            if (hasFocus) {
                box.setBorder (BorderFactory.createLineBorder(
                        UIManager.getColor("textText"))); //NOI18N
            } else {
                box.setBorder (BorderFactory.createEmptyBorder(1,1,1,1));
            }
            box.setBorderPainted(true);
        }  else {
            result = renderer.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
            renderer.setHtml(true);

            File f = item.getFile();
            renderer.setIcon (null);
            if (f.exists()) {
                if (NAME_STRING.equals(table.getColumnName(column))) {
                    Image img;
                    try {
                        img = DataObject.find(FileUtil.toFileObject(f)).getNodeDelegate().getIcon(BeanInfo.ICON_COLOR_16x16);
                    }  catch (DataObjectNotFoundException ex) {
                        img = null;
                    }
                    if (img != null) {
                        renderer.setIcon (new ImageIcon(img));
                    }
                }
            }
        }
        result.setEnabled (!item.isReplaced());
        return result;
    }

    public void becameInvalid(File file, String reason) {
        int start = -1;
        int end = -1;
        for (int i=0; i < items.length; i++) {
            boolean fileMatch = file.equals(items[i].getFile());
            if (start == -1 && fileMatch) {
                start = i;
                continue;
            }
            if (start != -1 && end == -1 && (!fileMatch || i == items.length - 1)) {
                end = i;
                break;
            }
        }
        if (start != -1) {
            fire (new TableModelEvent (this, start, end));
        }
    }

    public void shouldReplaceChanged(Item item, boolean shouldReplace) {
        //do nothing, the search is expensive and a repaint will take care
        //of it
    }

    public void fileShouldReplaceChanged(File file, boolean fileShouldReplace) {
        becameInvalid (file, null);
    }

    public void replaced(Item item) {
        //do nothing for now (if we want to keep the table alive after a
        //successful replace, then we should fire here to update the
        //display)
    }
}