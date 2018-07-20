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
