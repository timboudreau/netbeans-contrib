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
