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
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.netbeans.modules.vcs.advanced.variables.Condition;

/**
 * The class, that represents a String value under different conditions.
 *
 * @author  Martin Entlicher
 */
public class ConditionedString extends ConditionedObject {//implements Cloneable {
    
    /**
     * Creates the Conditioned String. Clone all conditions and wrap them by IfUnlessCondition.
     */
    public ConditionedString(String name, Map valuesByConditions) {
        super(name, valuesByConditions);
    }
    
    public String getValue(IfUnlessCondition iuc) {
        return (String) valuesByIfUnlessConditions.get(iuc);
    }
    
    public void setValue(IfUnlessCondition iuc, String value) {
        valuesByIfUnlessConditions.put(iuc, value);
    }
    
    /*
    public Object clone() {
        Map valuesByConditionsClone = new HashMap();
        for (Iterator it = valuesByConditions.keySet().iterator(); it.hasNext(); ) {
            Condition c = (Condition) it.next();
            valuesByConditionsClone.put(c.clone(), valuesByConditions.get(c));
        }
        return new ConditionedString(name, valuesByConditionsClone);
    }
     */
    
    public static final class ConditionedStringPropertyEditor extends PropertyEditorSupport {
        
        private ConditionedString cs;
        
        /*
        public ConditionedStringPropertyEditor(ConditionedString cs) {
            this.cs = cs;
        }
         */
        
        public Object getValue() {
            return cs;
        }

        public void setValue(Object value) {
            if (!(value instanceof ConditionedString)) {
                throw new IllegalArgumentException(""+value);
            }
            //cs = (ConditionedString) ((ConditionedString) value).clone();
            cs = (ConditionedString) value;
            firePropertyChange();
        }

        public Component getCustomEditor() {
            return new ConditionedStringPanel (cs);
        }

        public boolean supportsCustomEditor() {
            return true;
        }
        
        public String getAsText() {
            return cs.toString();
        }
        
        public void setAsText(String text) {
            IfUnlessCondition[] iucs = cs.getIfUnlessConditions();
            if (iucs.length == 1) {
                cs.setValue(iucs[0], text);
            } else {
                // Do nothing, there are different values.
            }
        }
    }
    
}
