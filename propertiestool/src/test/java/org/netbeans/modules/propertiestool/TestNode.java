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
 * Software is Nokia. Portions Copyright 1997-2006 Nokia. All Rights Reserved.
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
