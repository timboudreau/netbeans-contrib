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

import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/** A list of all properties in a property set.
 * The keys are of type Node.Property.
 */
public class PropSetKids extends Children.Keys {
    
    private Collection keys;
    
    private Node original;
    private Node.PropertySet ps;
    private PropertyChangeListener pcListener = null;
    
    public PropSetKids(Node original, Node.PropertySet ps) {
        this.ps = ps;
        this.original = original;
    }
    
    private void setKeys0(Collection c) {
        setKeys(c);
        keys = c;
    }
    
    /** Update the key list.
     * Looks for all properties which are readable, and not primitive or String or Class.
     */
    private void updateKeys() {
        Collection newKeys = new ArrayList();
        Node.Property[] props = ps.getProperties();
        for (int j = 0; j < props.length; j++) {
            Node.Property prop = props[j];
            if (prop.canRead()) {
                Class type = prop.getValueType();
                if (! (type.isPrimitive() || type == String.class || type == Class.class)) {
                    newKeys.add(prop);
                }
            }
        }
        setKeys0(newKeys);
    }
    
    /** Set the keys.
     * Also attach a listener to the original node so that if one of its
     * properties (node properties, not meta-properties of the node itself)
     * changes, the children can be recalculated.
     */
    protected void addNotify() {
        updateKeys();
        if (pcListener == null) {
            pcListener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent ev) {
                    String prop = ev.getPropertyName();
                    Iterator it = getKeysIterator();
                    Object found = null;
                    while (it.hasNext()) {
                        Object key = it.next();
                        if (key instanceof Node.Property && ((Node.Property) key).getName().equals(prop)) {
                            found = key;
                        }
                    }
                    if (found == null) {
                        // Should not happen.
                        updateKeys();
                    } else {
                        refreshKey0(found);
                    }
                }
            };
            original.addPropertyChangeListener(pcListener);
        }
    }
    // Inner class access methods:
    private void refreshKey0(Object key) {
        refreshKey(key);
    }
    private Iterator getKeysIterator() {
        return keys.iterator();
    }
    
    protected void removeNotify() {
        if (pcListener != null) {
            original.removePropertyChangeListener(pcListener);
            pcListener = null;
        }
        setKeys0(Collections.EMPTY_SET);
    }
    
    /** Create the node for this property.
     * @param key the property
     * @return the (one) node to represent it
     */
    protected Node[] createNodes(Object key) {
        return new Node[] { makePropertyNode((Node.Property) key) };
    }
    
    /** Make a node for a property and its value.
     * @param prop the property to represent
     * @return a node to represent it
     */
    private static Node makePropertyNode(Node.Property prop) {
        Class type = prop.getValueType();
        Node node;
        try {
            node = makeObjectNode(prop.getValue());
        } catch (Throwable t) {
            node = makeErrorNode(t);
        }
        node.setDisplayName(Utilities.getClassName(type) + " " + prop.getDisplayName() + " = " + node.getDisplayName());
        return node;
    }
    
    /** Make a node to meaningfully represent some object.
     * Special treatment for null; arrays or generalized collections; String and Class objects.
     * All else gets a RefinedBeanNode.
     * The name and tooltip are set to something helpful.
     * @param val the object to represent
     * @return a node displaying it
     */
    public static Node makeObjectNode(Object val) {
        if (val == null) {
            return makePlainNode("null");
        } else if (val instanceof Object[]) {
            return makeCollectionNode(Collections.enumeration(Arrays.asList((Object[]) val)));
        } else if (val.getClass().isArray()) {
            return makeCollectionNode(Collections.enumeration(Arrays.asList(Utilities.toObjectArray(val))));
        } else if (val instanceof Lookup) {
            Node n = new LookupNode((Lookup)val);
            n.setShortDescription("String value: `" + val + "'");
            n.setDisplayName(n.getDisplayName() + " (class " + val.getClass().getName() + ")");
            return n;
        } else if (val instanceof Enumeration) {
            return makeCollectionNode((Enumeration) val);
        } else if (val instanceof Collection) {
            return makeCollectionNode(Collections.enumeration((Collection) val));
        } else if (val instanceof String) {
            return makePlainNode("\"" + (String) val + "\"");
        } else if (val instanceof Class) {
            return makePlainNode("class " + ((Class) val).getName());
        } else if ((val instanceof Boolean) || (val instanceof Number)) {
            return makePlainNode(val.toString());
        } else if (val instanceof Character) {
            return makePlainNode("(char) '" + val.toString() + "'");
        } else {
            Node objnode;
            try {
                objnode = new RefinedBeanNode(val);
            } catch (IntrospectionException e) {
                objnode = makeErrorNode(e);
            }
            objnode.setShortDescription("String value: `" + val + "'; short description: " + objnode.getShortDescription());
            objnode.setDisplayName(objnode.getDisplayName() + " (class " + val.getClass().getName() + ")");
            return Wrapper.make(objnode);
        }
    }
    
    /** Make a leaf node just displaying some text.
     * @param name the text
     * @return the node
     */
    static Node makePlainNode(String name) {
        AbstractNode toret = new AbstractNode(Children.LEAF) {
            public HelpCtx getHelpCtx() {
                return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser");
            }
        };
        toret.setName(name);
        toret.setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
        return toret;
    }
    
    /** Make a node representing an error condition and describing the error.
     * @param t the error
     * @return a node displaying it (as a Bean)
     */
    static Node makeErrorNode(Throwable t) {
        Node node = makeObjectNode(t);
        node.setDisplayName("[thrown] " + node.getDisplayName());
        return node;
    }
    
    /** Make a node representing an array or list or somesuch.
     * Safety valve warns the user before creating a huge array.
     * @param val an Enuemration of Object's
     * @return a node displaying the objects as children
     */
    private static Node makeCollectionNode(final Enumeration val) {
        final Node[] _base = new Node[] { null };
        final String defaultName = "<list of objects>";
        Children kids = new Children.Array() {
            protected void addNotify() {
                new Thread(new Runnable() {
                    public void run() {
                        int count = 0;
                        while (val.hasMoreElements()) {
                            Node n = makeObjectNode(val.nextElement());
                            n.setDisplayName("[" + count + "] " + n.getDisplayName());
                            add(new Node[] { n });
                            if (count++ == 50) {
                                if (! NotifyDescriptor.OK_OPTION.equals(
                                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Confirmation(new String[] {
                                    "There were over 50 elements in this array.",
                                    "Actually show all of them?"
                                })))) {
                                    break;
                                }
                            }
                        }
                        if (defaultName.equals(_base[0].getDisplayName())) {
                            _base[0].setDisplayName("A list of " + count + " children...");
                            _base[0].setShortDescription(_base[0].getDisplayName());
                        } else {
                            _base[0].setShortDescription("[" + count + " children] " + _base[0].getShortDescription());
                        }
                    }
                }, "making collection node").start();
            }
        };
        AbstractNode base = new AbstractNode(kids) {
            public HelpCtx getHelpCtx() {
                return new HelpCtx("org.netbeans.modules.apisupport.beanbrowser");
            }
        };
        _base[0] = base;
        base.setName("collection");
        base.setDisplayName(defaultName);
        base.setIconBaseWithExtension("org/netbeans/modules/apisupport/beanbrowser/BeanBrowserIcon.gif");
        return base;
    }
}
