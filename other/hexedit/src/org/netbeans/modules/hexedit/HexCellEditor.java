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
 * HexCellEditor.java
 *
 * Created on April 28, 2004, 12:00 AM
 */

package org.netbeans.modules.hexedit;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author  tim
 */
class HexCellEditor implements TableCellEditor, ActionListener {
    private ArrayList listeners = new ArrayList();
    private JTextField ed = new JTextField();
    
    /** Creates a new instance of HexCellEditor */
    public HexCellEditor() {
        ed.setHorizontalAlignment(SwingConstants.TRAILING);
        ed.addActionListener (this);
        ed.getInputMap(JComponent.WHEN_FOCUSED).put (
            KeyStroke.getKeyStroke (KeyEvent.VK_ESCAPE, 0), "cancel");  //NOI18N
        ed.getActionMap().put("cancel", new CancelAction()); //NOI18N
    }

    public void focusEditor() {
        String text = ed.getText();
        if (text != null && text.length() > 0) {
            ed.setSelectionStart(0);
            ed.setSelectionEnd(text.length());
        }
        ed.requestFocus();
    }

    private class CancelAction extends AbstractAction {
        public void actionPerformed(ActionEvent actionEvent) {
            cancelCellEditing();
        }
    }
    
    public void cancelCellEditing() {
        fire (true);
        ed.setText ("");
        editingClass = null;
    }
    
    public boolean stopCellEditing() {
        fire (false);
        ed.setText ("");
        editingClass = null;
        return true;
    }
    
    private void fire (boolean cancelled) {
        ChangeEvent ce = new ChangeEvent (this);
        List l = null;
        synchronized (this) {
            l = new ArrayList(listeners);
        }
        for (Iterator i=l.iterator(); i.hasNext();) {
            CellEditorListener e = (CellEditorListener) i.next();
            if (cancelled) {
                e.editingCanceled(ce);
            } else {
                e.editingStopped(ce);
            }
        }
    }
    
    public Object getCellEditorValue() {
        String s = ed.getText();
        Object result = null;
        try {
            result = Util.convertFromString (s, editingClass);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(ed, e.getLocalizedMessage(),
                Util.getMessage("TITLE_BAD_VALUE"), JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }
    
    private Class editingClass = null;
    public Component getTableCellEditorComponent(JTable jTable, Object o, boolean sel, int row, int col) {
        if (o == null || o == HexTableModel.PARTIAL_VALUE) {
            return null;
        }
        editingClass = o.getClass();
        String txt = Util.convertToString(o);
        ed.setText (txt);
        ed.setForeground (jTable.getForeground());
        ed.setBackground (jTable.getBackground());
        ed.setFont (jTable.getFont());
        return ed;
    }
    
    public void actionPerformed (ActionEvent ae) {
        stopCellEditing();
    }
    
    public boolean isCellEditable(EventObject o) {
        if (o instanceof MouseEvent) {
            int clickCount = ((MouseEvent) o).getClickCount();
            return clickCount > 0 && clickCount % 2 == 0;
        }
        return true;
    }
    
    public synchronized void removeCellEditorListener(CellEditorListener l) {
        listeners.remove(l);
    }
    
    public boolean shouldSelectCell(EventObject eventObject) {
        return true;
    }
    
    public synchronized void addCellEditorListener(CellEditorListener l) {
        listeners.add(l);
    }
}
