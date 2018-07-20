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

package org.netbeans.modules.vcscore.util.table;

import org.openide.ErrorManager;
import org.openide.util.*;
//import org.netbeans.lib.cvsclient.command.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.lang.reflect.Method;
import java.io.IOException;

/**
 * Table model that translates @{link List} to 2D table structure.
 * Translation logic is based on reflection and defined
 * by {@link #setColumnDefinition} methods.
 *
 * @author  mkleint
 */
public class TableInfoModel extends AbstractTableModel implements Comparator {
    private int currentColumn;
    private int direction;
    
    public static final int ASCENDING = 1;
    public static final int DESCENDING = -1;
    
      
      protected HashMap columnLabels;
      protected HashMap columnValueSetters;
      protected HashMap columnValueParams;
      protected HashMap columnSorted;
      protected HashMap columnComparators;
      protected HashMap columnToolTipSetters;
      
      private List list;
      
      private static final long serialVersionUID = -794293494044050639L;

      /** If estimete exceeds this value, file list implementation is prefered. */
      private static final int SIZE_THRESHOLD = 237;

      /** Creates memory based implemnation. */
      public TableInfoModel() {
          this(SIZE_THRESHOLD);
      }

    /**
     * Creates new tarnslating table model
     * @param estimatedSize helps implementation to decide
     * about backend data storage: memory vs. file. The higher
     * value the higher chance that implementation chooses
     * file based list.
     */
      public TableInfoModel(int estimatedSize) {
          if (estimatedSize > SIZE_THRESHOLD) {
              list = new LinkedList();
          } else {
              list = new LinkedList();
          }
          columnLabels = new HashMap();
          columnSorted = new HashMap();
          columnValueSetters = new HashMap();
          columnValueParams = new HashMap();
          columnComparators = new HashMap();
          columnToolTipSetters = new HashMap();
          setDirection(true);
          setActiveColumn(0);
      }

      public int getRowCount() {
          return list.size();
      }
      
      
      public int getColumnCount() {
          return columnValueSetters.size();
      }
      
      public Object getValueAt(int row, int column) {
          if (row < 0 || row >= getRowCount()) return "";
          Integer columnInt = new Integer(column);
          Object info = list.get(row);
          Method getter = (Method)columnValueSetters.get(columnInt);
          Object[] params = (Object[])columnValueParams.get(columnInt);
          TableInfoComparator comparator = (TableInfoComparator)columnComparators.get(columnInt);
          if (getter == null) return "";
          try {
              Object value = getter.invoke(info, params);
              if (comparator != null) {
                  return comparator.getDisplayValue(value, info);
              }
              return value;
          } catch (IllegalAccessException exc) {
              ErrorManager.getDefault().notify(exc);
          } catch (IllegalArgumentException exc2) {
              ErrorManager.getDefault().notify(exc2);
          } catch (java.lang.reflect.InvocationTargetException exc3) {
              ErrorManager.getDefault().notify(exc3);
          }
          return ""; // NOI18N
      }
      
      /**
       * Get the tooltip text at the given position.
       * @return The tooltip text or <code>null</code>.
       */
      public String getTooltipTextAt(int row, int column) {
          if (row < 0 || row >= getRowCount()) return null;
          Integer columnInt = new Integer(column);
          Method toolTipGetter = (Method) columnToolTipSetters.get(columnInt);
          if (toolTipGetter == null) {
              return null;
          }
          Object info = list.get(row);
          Object[] params = (Object[])columnValueParams.get(columnInt);
          try {
              Object value = toolTipGetter.invoke(info, params);
              if (value == null) {
                  return null;
              } else {
                  return value.toString();
              }
          } catch (IllegalAccessException exc) {
              ErrorManager.getDefault().notify(exc);
          } catch (IllegalArgumentException exc2) {
              ErrorManager.getDefault().notify(exc2);
          } catch (java.lang.reflect.InvocationTargetException exc3) {
              ErrorManager.getDefault().notify(exc3);
          }
          return null;
      }
      
      
      public boolean isCellEditable(int rowIndex, int columnIndex)  {
          return false;
      }
      
      public java.lang.String getColumnName(int param) {
          Integer columnIndex = new Integer(param);
          Object label = columnLabels.get(columnIndex);
          if (label != null) {
            return label.toString();
          }
          return NbBundle.getBundle(TableInfoModel.class).getString("TableInfoModel.noColumnLabel");
      }

// ===============================================      
      
      public boolean isColumnSortable(int column) {
            Integer integ = new Integer(column);
            Boolean bvalue = (Boolean)columnSorted.get(integ);
            if (bvalue != null) {
                return bvalue.booleanValue();
            }
            return false;
      }
      
      
      public Object getElementAt(int row) {
          if (row < 0 || row >= getRowCount()) return null;
          return list.get(row);
      } 
      
      public int getElementRow(Object element) {
          return list.indexOf(element);
      }
      
      public void setColumnDefinition(int columnNumber, String label,  
                                      Method reflectionGetter, boolean sorted, TableInfoComparator comp) 
      {
            Integer integ = new Integer(columnNumber);
            columnLabels.put(integ, label);
            columnSorted.put(integ, sorted ? Boolean.TRUE : Boolean.FALSE);
            columnValueSetters.put(integ, reflectionGetter);
            columnValueParams.put(integ, null);
            columnComparators.put(integ, comp);
      }

      
      public void setColumnDefinition(int columnNumber, String label,  
                                      Method reflectionGetter, Object[] params, boolean sorted, TableInfoComparator comp) 
      {
            Integer integ = new Integer(columnNumber);
            columnLabels.put(integ, label);
            columnSorted.put(integ, sorted ? Boolean.TRUE : Boolean.FALSE);
            columnValueSetters.put(integ, reflectionGetter);
            columnValueParams.put(integ, params);
            columnComparators.put(integ, comp);
      }
      
      public void setColumnToolTipGetter(int columnNumber, Method reflectionGetter) {
          Integer integ = new Integer(columnNumber);
          columnToolTipSetters.put(integ, reflectionGetter);
      }
      
/*      public Method getColumnGetterMethod(int columnNumber) {
            Integer integ = new Integer(columnNumber);
            Method meth = (Method)columnValueSetters.get(integ);
            return meth;
      }
      
      public TableInfoComparator getColumnGetterComparator(int columnNumber) {
            Integer integ = new Integer(columnNumber);
            TableInfoComparator meth = (TableInfoComparator)columnComparators.get(integ);
            return meth;
      }
  */    
      public void addElement(Object object) {
          list.add(object);
      } 
      
      public void fireTableDataChanged() {
          super.fireTableDataChanged();
      }
      
      public boolean removeElement(Object object) {
          int row = list.indexOf(object);
          boolean toReturn = false;
          if (row >= 0) {
              toReturn = list.remove(object);
              fireTableRowsDeleted(row, row);
          }
          return toReturn;
      }
      
      public void clear() {
          list.clear();
      }

      public void prependElement(Object object) {
          list.add(0, object);
      }
      
    public void setActiveColumn(int index) {
        currentColumn = index;
    }    
    public int getActiveColumn() {
        return currentColumn;
    }    
    
    public void setDirection(boolean ascending) {
        if (ascending) direction = ASCENDING;
        else direction = DESCENDING;
    }    
    
    public int getDirection() {
        return direction;
    }    
      
    public boolean equals(Object obj) {
          if (obj instanceof TableInfoModel) {
              TableInfoModel obj2 = (TableInfoModel)obj;
              return (obj2.getActiveColumn() == getActiveColumn());
          }
          return false;
    }   
    
    public int compare(java.lang.Object obj1,java.lang.Object obj2) {
        Integer columnInt = new Integer(getActiveColumn());
        Method getter = (Method)columnValueSetters.get(columnInt);
        Object[] params = (Object[])columnValueParams.get(columnInt);
        TableInfoComparator comparator = (TableInfoComparator)columnComparators.get(columnInt);
        if (getter == null) return 0;
        Object value1 = null;
        Object value2 = null;
        try {
            value1 = getter.invoke(obj1, params);
            value2 = getter.invoke(obj2, params);
        } catch (IllegalAccessException exc) {
            return 0;
        } catch (IllegalArgumentException exc2) {
            return 0;
        } catch (java.lang.reflect.InvocationTargetException exc3) {
            return 0;
        }
        if (comparator == null) {
            comparator = new StringComparator();
        }
        return getDirection() * comparator.compare(value1, value2);
    }
    
    public List getList() {
        return list;
    }    
    

      
      private class StringComparator implements TableInfoComparator {
          public int compare(java.lang.Object obj, java.lang.Object obj1) {
              if (obj == null) return -1;
              if (obj1 == null) return 1;
              String str1 = obj.toString();
              String str2 = obj1.toString();
              return str1.compareTo(str2);
          }
          
          public String getDisplayValue(Object obj, Object rowObject) {
              return obj.toString();
          }
          
          
      }

}

