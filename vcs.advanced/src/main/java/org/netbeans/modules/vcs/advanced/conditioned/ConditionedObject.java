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
