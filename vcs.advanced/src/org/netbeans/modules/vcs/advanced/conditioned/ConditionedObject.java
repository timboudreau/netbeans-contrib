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

import java.awt.Component;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.netbeans.modules.vcs.advanced.variables.Condition;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;

/**
 * This class represents a property value under different conditions.
 *
 * @author  Martin Entlicher
 */
public class ConditionedObject extends Object {
    
    private String name;
    protected Map valuesByIfUnlessConditions;
    
    /**
     * Creates the conditioned property. Clone all conditions and wrap them by IfUnlessCondition.
     */
    public ConditionedObject(String name, Map valuesByConditions) {
        this.name = name;
        this.valuesByIfUnlessConditions = new HashMap();
        for (Iterator it = valuesByConditions.keySet().iterator(); it.hasNext(); ) {
            Condition c = (Condition) it.next();
            Condition cc = (c != null) ? (Condition) c.clone() : c;
            valuesByIfUnlessConditions.put(new IfUnlessCondition(cc), valuesByConditions.get(c));
        }
    }
    
    public String getName() {
        return name;
    }
    
    public IfUnlessCondition[] getIfUnlessConditions() {
        return (IfUnlessCondition[]) valuesByIfUnlessConditions.keySet().toArray(new IfUnlessCondition[0]);
    }
    
    public Object getObjectValue(IfUnlessCondition iuc) {
        return valuesByIfUnlessConditions.get(iuc);
    }
    
    public void setObjectValue(IfUnlessCondition iuc, Object value) {
        valuesByIfUnlessConditions.put(iuc, value);
    }
    
    public void removeValue(IfUnlessCondition iuc) {
        valuesByIfUnlessConditions.remove(iuc);
    }
    
    public Map getValuesByConditions() {
        Map valuesByConditions = new HashMap();
        for (Iterator it = valuesByIfUnlessConditions.keySet().iterator(); it.hasNext(); ) {
            IfUnlessCondition iuc = (IfUnlessCondition) it.next();
            valuesByConditions.put(iuc.getCondition(), valuesByIfUnlessConditions.get(iuc));
        }
        return valuesByConditions;
    }
    
    public String toString() {
        if (valuesByIfUnlessConditions.size() == 1) {
            Object value = valuesByIfUnlessConditions.values().iterator().next();
            return ""+value;
        } else {
            return org.openide.util.NbBundle.getMessage(ConditionedObject.class,
                                                        "ConditionedValue.text");
        }
    }
    
    public static ConditionedObject createConditionedObject(String name, Map valuesByConditions,
                                                            Class valueType) {
        if (valueType.equals(String.class)) {
            return new ConditionedString(name, valuesByConditions);
        } else if (valueType.equals(Integer.TYPE)) {
            return new ConditionedInteger(name, valuesByConditions);
        } else if (valueType.equals(Boolean.TYPE)) {
            return new ConditionedBoolean(name, valuesByConditions);
        } else {
            return new ConditionedObject(name, valuesByConditions);
        }
    }
    
    public static PropertyEditor getConditionedPropertyEditor(Class propertyClass) {
        if (propertyClass.equals(String.class)) {
            return new ConditionedString.ConditionedStringPropertyEditor();
        } else if (propertyClass.equals(Integer.TYPE)) {
            return new ConditionedInteger.ConditionedIntegerPropertyEditor();
        } else if (propertyClass.equals(Boolean.TYPE)) {
            return new ConditionedBoolean.ConditionedBooleanPropertyEditor();
        //} else if (propertyClass.equals(StructuredExec.class)) {
        //    return new ConditionedStructuredExecEditor();
        } else {
            throw new IllegalArgumentException("No property editor for class "+propertyClass);
        }
    }
    
    /*
    public static final class ConditionedObjectPropertyEditor extends PropertyEditorSupport {
        
        private ConditionedObject co;
        
        public Object getValue() {
            return co;
        }

        public void setValue(Object value) {
            if (!(value instanceof ConditionedObject)) {
                throw new IllegalArgumentException(""+value);
            }
            //cs = (ConditionedString) ((ConditionedString) value).clone();
            co = (ConditionedObject) value;
            firePropertyChange();
        }

        public Component getCustomEditor() {
            return new ConditionedObjectPanel (co);
        }

        public boolean supportsCustomEditor() {
            return true;
        }
        
        public String getAsText() {
            return co.toString();
        }
        
        public void setAsText(String text) {
            IfUnlessCondition[] iucs = co.getIfUnlessConditions();
            if (iucs.length == 1) {
                co.setObjectValue(iucs[0], text);
            } else {
                // Do nothing, there are different values.
            }
        }
    }
     */
    
}
