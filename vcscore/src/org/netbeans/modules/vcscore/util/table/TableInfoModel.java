/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.util.table;

import org.openide.util.*;
//import org.netbeans.lib.cvsclient.command.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.lang.reflect.Method;

/**
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
      
      protected LinkedList list;
      
      private static final long serialVersionUID = -794293494044050639L;
      
      public TableInfoModel() {
          list = new LinkedList();
          columnLabels = new HashMap();
          columnSorted = new HashMap();
          columnValueSetters = new HashMap();
          columnValueParams = new HashMap();
          columnComparators = new HashMap();
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
          //          if (row < 0 || row >= getRowCount()) return "";
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
              return "";
          } catch (IllegalArgumentException exc2) {
              return "";
          } catch (java.lang.reflect.InvocationTargetException exc3) {
              return "";
          }
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
            columnSorted.put(integ, new Boolean(sorted));
            columnValueSetters.put(integ, reflectionGetter);
            columnValueParams.put(integ, null);
            columnComparators.put(integ, comp);
      }

      
      public void setColumnDefinition(int columnNumber, String label,  
                                      Method reflectionGetter, Object[] params, boolean sorted, TableInfoComparator comp) 
      {
            Integer integ = new Integer(columnNumber);
            columnLabels.put(integ, label);
            columnSorted.put(integ, new Boolean(sorted));
            columnValueSetters.put(integ, reflectionGetter);
            columnValueParams.put(integ, params);
            columnComparators.put(integ, comp);
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
          list.addFirst(object);
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

