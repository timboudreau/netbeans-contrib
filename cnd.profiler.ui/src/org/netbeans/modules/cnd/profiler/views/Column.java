/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.cnd.profiler.views;

import java.lang.reflect.InvocationTargetException;

import org.netbeans.modules.cnd.profiler.data.Function;
import org.openide.nodes.PropertySupport;
import org.netbeans.modules.cnd.profiler.data.FunctionContainer;

/**
 *
 * @author eu155513
 */
public class Column extends PropertySupport.ReadOnly {
    public Column(String name, Class type, String displayName, String descr) {
        super(name, type, displayName, descr);
        setValue("ComparableColumnTTV", Boolean.TRUE);
    }

    @Override
    public Object getValue() throws IllegalAccessException, InvocationTargetException {
        return null;
    }
    
    public PropertySupport getPropertyFor(FunctionContainer fc) {
        return new SimpleColumnAdapter(this, getName(), fc);
    }
    
    public static final String TIME_NAME = "children";
    public static final String SELF_NAME = "self";
    
    public static Column createTimeColumn() {
        Column column = new TimeColumn();
        column.setValue("SortingColumnTTV", Boolean.TRUE);
        column.setValue("DescendingOrderTTV", Boolean.TRUE);
        return column;
    }
    
    public static Column createSelfTimeColumn() {
        return new SelfColumn();
    }
    
    public static Column createNameColumn() {
        Column column = new Column("name", String.class, "Name", "Name");
        column.setValue("TreeColumnTTV", Boolean.TRUE);
        return column;
    }
    
    private static class TimeColumn extends Column {
        public TimeColumn() {
            super(TIME_NAME, Double.class, "Time", "Time");
        }

        @Override
        public PropertySupport getPropertyFor(FunctionContainer fc) {
            return new SumColumnAdapter(this, TIME_NAME, SELF_NAME, fc);
        }
    }
    
    private static class SelfColumn extends Column {
        public SelfColumn() {
            super(SELF_NAME, Double.class, "Self Time", "Self Time");
        }
    }
    
    // STATIC
    private static abstract class ColumnPropertyAdapter extends PropertySupport.ReadOnly {
        private final FunctionContainer fc;
        
        public ColumnPropertyAdapter(Column column, FunctionContainer fc) {
            super(column.getName(), 
                  column.getValueType(), 
                  column.getDisplayName(), 
                  column.getShortDescription());
            this.fc = fc;
        }

        public abstract Object getValue(Function f);

        @Override
        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            if (fc != null) {
                return getValue(fc.getFunction());
            } else {
                return null;
            }
        }
    }

    private static class SimpleColumnAdapter extends ColumnPropertyAdapter {
        private final String propertyName;

        public SimpleColumnAdapter(Column column, String propertyName, FunctionContainer fc) {
            super(column, fc);
            this.propertyName = propertyName;
        }

        @Override
        public Object getValue(Function f) {
            return f.getProperty(propertyName);
        }
    }
    
    private static class SumColumnAdapter extends ColumnPropertyAdapter {
        private final String propertyName1;
        private final String propertyName2;

        public SumColumnAdapter(Column column, String propertyName1, String propertyName2, FunctionContainer fc) {
            super(column, fc);
            this.propertyName1 = propertyName1;
            this.propertyName2 = propertyName2;
        }

        @Override
        public Object getValue(Function f) {
            Double num1 = (Double)f.getProperty(propertyName1);
            Double num2 = (Double)f.getProperty(propertyName2);
            return num1 + num2;
        }
    }
}
