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
 * Software is Nokia. Portions Copyright 1997-2006 Nokia. All Rights Reserved.
 */
package org.netbeans.modules.propertiestool;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport.ReadWrite;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.Lookup;

/**
 * Node used for testing or the properties tool.
 * @author David Strupl
 */
public class TestNode extends AbstractNode {
    public TestNode(Lookup lookup) {
        super(Children.LEAF, lookup);
        setName("TestNode");
    }
    
    public Sheet createSheet() {
        Sheet s = Sheet.createDefault ();
        Set ss = s.get (Sheet.PROPERTIES);
        Property[] props = createProperties();
        ss.put(props);
        return s;
    }
    
    private Node.Property[] createProperties() {
        return new Property[]  {new ReadWrite("boolean_prop", Boolean.TYPE, "boolean prop.", "Short desc") {  // NOI18N
            private Boolean val = Boolean.TRUE;
            public Object getValue() {
                return val;
            }
            public void setValue(Object newVal) {
                Boolean oldVal = val;
                val = (Boolean) newVal;
                firePropertyChange("boolean_prop", oldVal, val);
            }
        }, new ReadWrite("string_prop", String.class, "string prop", "Test string prop") {  // NOI18N
            String val = "string test";
            public Object getValue() {
                return val;  // NOI18N
            }
            public void setValue(Object newVal) {
                String oldVal = val;
                val = newVal.toString();
                firePropertyChange("string_prop", oldVal, val);
            }
            public boolean supportsDefaultValue() {
                return true;
            }
            public void restoreDefaultValue() {
                setValue("default");
            }
            public boolean isDefaultValue() {
                return "default".equals(getValue());
            }
        }, new ReadWrite("int_prop", Integer.TYPE, "Number", "Coooool") {  // NOI18N
            Integer val = new Integer(45);
            public Object getValue() {
                return val;  // NOI18N
            }
            public void setValue(Object newVal) {
                Integer oldVal = val;
                val = (Integer) newVal;
                firePropertyChange("int_prop", oldVal, val);
            }
        }};
    }
}
