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

package org.netbeans.modules.apisupport.beanbrowser;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collections;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/** Children representing bean-like properties, unfiltered by type
 * or by known BeanInfo information.
 * <P>Keys may be PropertyDescriptor (a property)
 * or Exception (error during introspection).
 * @author Jesse Glick
 */
public class RawBeanPropKids extends Children.Keys {
    
    protected Object thing;
    
    public RawBeanPropKids(Object thing) {
        this.thing = thing;
    }
    
    protected void addNotify() {
        try {
            BeanInfo bi = Introspector.getBeanInfo(thing.getClass(), Introspector.IGNORE_ALL_BEANINFO);
            setKeys(bi.getPropertyDescriptors());
            // XXX listen to changes in thing if it has a property change listener event set
        } catch (Exception e) {
            setKeys(Collections.singleton(e));
        }
    }
    
    protected void removeNotify() {
        setKeys(Collections.EMPTY_SET);
    }
    
    protected Node[] createNodes(Object key) {
        if (key instanceof PropertyDescriptor) {
            PropertyDescriptor pd = (PropertyDescriptor) key;
            if (pd instanceof IndexedPropertyDescriptor) {
                // We can't show it, don't try.
                return null;
            }
            Method meth = pd.getReadMethod();
            if (meth == null) {
                return new Node[] {PropSetKids.makePlainNode("[unreadable: " + pd.getName() + "]")};
            }
            try {
                Object val;
                // Be brutal: we are showing *raw* bean properties here,
                // after all, and it is frequently useful to inspect e.g.
                // package-private stuff too.
                meth.setAccessible(true);
                try {
                    val = meth.invoke(thing, new Object[] { });
                } finally {
                    meth.setAccessible(false);
                }
                Node n = PropSetKids.makeObjectNode(val);
                n.setDisplayName(pd.getName() + " = " + n.getDisplayName());
                return new Node[] { n };
            } catch (Exception e) {
                Node n = PropSetKids.makeErrorNode(e);
                n.setDisplayName("[property: " + pd.getName() +  "] " + n.getDisplayName());
                return new Node[] { n };
            }
        } else {
            Node n = PropSetKids.makeErrorNode((Exception) key);
            n.setDisplayName("[during introspection] " + n.getDisplayName());
            return new Node[] { n };
        }
    }
    
}
