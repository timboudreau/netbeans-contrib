/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
        for (Iterator it = valuesByConditions.values().iterator(); it.hasNext(); ) {
            Object value = it.next();
            if (value != null && !(value instanceof String)) throw new ClassCastException(value.getClass().toString());
        }
    }
    
    public String getValue(IfUnlessCondition iuc) {
        return (String) valuesByIfUnlessConditions.get(iuc);
    }
    
    public void setValue(IfUnlessCondition iuc, String value) {
        valuesByIfUnlessConditions.put(iuc, value);
    }
    
    public void setObjectValue(IfUnlessCondition iuc, Object value) {
        if (value != null && !(value instanceof String)) throw new ClassCastException(value.getClass().toString());
        super.setObjectValue(iuc, value);
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
