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

package org.netbeans.modules.vcs.advanced.conditioned;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditorSupport;
import javax.swing.table.DefaultTableCellRenderer;
import org.openide.explorer.propertysheet.DefaultPropertyModel;
import org.openide.explorer.propertysheet.PropertyPanel;

/**
 *
 * @author  Martin Entlicher
 */
public class IfUnlessPropertyEditor extends PropertyEditorSupport {
    
    private IfUnlessCondition iuc;
    
    /** Creates a new instance of IfUnlessPropertyEditor */
    public IfUnlessPropertyEditor() {
    }
    
    public String getAsText() {
       if (iuc == null) return "";
       else return iuc.toString();
    }
    
    public Object getValue() {
        //cmd.setProperty(VcsCommand.PROPERTY_EXEC, execString);
        return iuc;
    }
    
    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        // Unimplemented
    }
    
    public void setValue(Object value) {
        this.iuc = (IfUnlessCondition) value;
    }
    
    public boolean supportsCustomEditor() {
        return true;
    }
    
    public java.awt.Component getCustomEditor() {
        return new IfUnlessConditionPanel(iuc, new String[0]);
    }
    
    public static class IUTableCellEditor extends javax.swing.DefaultCellEditor {
        
        private javax.swing.JTable table;
        private int row;
        private int column;
        
        public IUTableCellEditor() {
            super(new javax.swing.JTextField());
        }
    
        public Object getCellEditorValue() {
            return table.getValueAt(row, column);
        }

        public java.awt.Component getTableCellEditorComponent(final javax.swing.JTable table,
                                                              Object value,
                                                              boolean isSelected,
                                                              final int row,
                                                              final int column) {
            this.table = table;
            this.row = row;
            this.column = column;
            Object tableValue = new IUTableValueContainer(table, row, column);

            PropertyDescriptor pd;
            try {
                pd = new PropertyDescriptor("tableValue",
                    tableValue.getClass().getMethod("getTableValue", new Class[0]),
                    tableValue.getClass().getMethod("setTableValue", new Class[] { IfUnlessCondition.class }));
                pd.setPropertyEditorClass(IfUnlessPropertyEditor.class);
            } catch (NoSuchMethodException nsmex) {
                org.openide.ErrorManager.getDefault().notify(nsmex);
                return null;
            } catch (SecurityException sex) {
                org.openide.ErrorManager.getDefault().notify(sex);
                return null;
            } catch (java.beans.IntrospectionException iex) {
                org.openide.ErrorManager.getDefault().notify(iex);
                return null;
            }
            pd.setValue("canEditAsText", Boolean.FALSE);
            DefaultPropertyModel model = new DefaultPropertyModel(tableValue, pd);
            PropertyPanel pp = new PropertyPanel(model, PropertyPanel.PREF_TABLEUI);
            return pp;
        }
    }
    
    public static class IUTableCellRenderer extends DefaultTableCellRenderer {
            
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table,
                                                                Object value,
                                                                boolean isSelected,
                                                                boolean hasFocus,
                                                                int row,
                                                                int column) {
            //System.out.println("getTableCellRendererComponent("+row+", "+column+"), value = '"+table.getValueAt(row, column)+"', class = "+table.getValueAt(row, column).getClass());
            return new javax.swing.JLabel(((IfUnlessCondition) table.getValueAt(row, column)).toString());
        }
    }
    
    public static class IUTableValueContainer extends Object {
        
        private javax.swing.JTable table;
        private int row;
        private int column;
        
        public IUTableValueContainer(final javax.swing.JTable table,
                                     final int row, final int column) {
            this.table = table;
            this.row = row;
            this.column = column;
        }
        
        public void setTableValue(IfUnlessCondition value) {
            table.setValueAt(value, row, column);
            table.editingStopped(new javax.swing.event.ChangeEvent(table));
        }

        public IfUnlessCondition getTableValue() {
            return (IfUnlessCondition) table.getValueAt(row, column);
        }
    }
    
}
