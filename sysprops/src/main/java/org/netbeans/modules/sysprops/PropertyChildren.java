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
 *
 * Contributor(s): Jesse Glick, Michael Ruflin
 */

package org.netbeans.modules.sysprops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/** Children for a PropertyNode.
 * Manages deciding which nodes should be displayed beneath it.
 * <p>The keys are as follows: one key per node, being the full
 * property name of the subnode. If that node should itself have
 * any children, the name is prefixed by an asterisk. The asterisk
 * does not in itself change the behavior of the subnode, however
 * this means that if one of the subnodes newly receives subsubnodes,
 * or newly loses all of them, then the list of keys will correspondingly
 * change, triggering the creation of a distinct new subnode. It is
 * necessary to create a brand new node if the subproperty switches the
 * state of having subsubproperties, because if there are no subsubproperties
 * then Children.LEAF is used to make the node a leaf node in the Explorer,
 * and a given node can never change its children object (though the nodes
 * contained in the children object can change, provided it is a not a leaf).
 *
 * @author Jesse Glick
 * @author Michael Ruflin
 */
public class PropertyChildren extends Children.Keys {
    
    /** Name of the Property. */
    protected String property;
    
    /** An associated listener to changes in system properties. */
    private ChangeListener listener = null;
    
    /** Creates new PropertyChildren
     *
     * @param property the Name of the Property (of the Node); may be null for root.
     */
    public PropertyChildren(String property) {
        this.property = property;
    }
    
    /**
     * Activates this Children.
     */
    protected void addNotify() {
        updateKeys();
        PropertiesNotifier.getDefault().addChangeListener
                (listener = new ChangeListener() {
            public void stateChanged(ChangeEvent ev) {
                updateKeys();
            }
        });
    }
    
    /**
     * Deactivates this Children.
     */
    protected void removeNotify() {
        if (listener != null) {
            PropertiesNotifier.getDefault().removeChangeListener(listener);
            listener = null;
        }
        setKeys(Collections.EMPTY_SET);
    }
    
    /**
     * Creates a Node of an Object key.
     * @param the key to use. In this case it will be a property name for a subproperty;
     *        a prefixed asterisk indicates that there are in fact sub-sub-properties
     * @return one node for the subproperty
     */
    protected Node[] createNodes(Object key) {
        String prop = (String) key;
        if (prop.startsWith("*")) prop = prop.substring(1);
        return new Node[] { new PropertyNode(prop, findSubProperties(prop)) };
    }
    
    /** Find all subproperties based on a given
     * property.
     * @param the starting property (or pseudo-property)
     * @return a (possibly empty) list
     */
    public static List/*<String>*/ findSubProperties(String prop) {
        List/*<String>*/ subprops = new ArrayList();
        Iterator it = new TreeSet(System.getProperties().keySet()).iterator();
        while (it.hasNext()) {
            String subprop = (String) it.next();
            if (subprop.startsWith("Env-") || subprop.startsWith("env-")) {
                continue;
            }
            if (subprop.startsWith(prop + ".")) {
                subprops.add(subprop);
            }
        }
        return subprops;
    }
    
    /**
     * Searchs all subkeys of this Node.
     * Note that only one level of dot-separation is considered,
     * so the resulting property names are not necessarily real
     * system properties. Asterisks are prepended where there
     * are subsubproperties.
     */
    private void updateKeys() {
        Collection keys = new TreeSet();
        Enumeration e = System.getProperties().propertyNames();
        while (e.hasMoreElements()) {
            String prop = (String) e.nextElement();
            if (prop.startsWith("Env-") || prop.startsWith("env-")) {
                continue;
            }
            if (property != null && ! prop.startsWith(property + '.')) {
                continue;
            }
            int idx;
            if (property == null) {
                idx = prop.indexOf((int) '.');
            } else {
                idx = prop.indexOf((int) '.', property.length() + 1);
            }
            if (idx == -1) {
                keys.add(prop);
            } else {
                keys.add("*" + prop.substring(0, idx));
            }
        }
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String prop = (String) it.next();
            if (! prop.startsWith("*") && keys.contains("*" + prop)) {
                it.remove();
            }
        }
        setKeys(keys);
    }
    
}
