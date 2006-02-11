/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
