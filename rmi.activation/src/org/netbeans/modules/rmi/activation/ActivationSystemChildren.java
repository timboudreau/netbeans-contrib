/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.rmi.activation;

import java.beans.*;
import java.util.*;

import org.openide.nodes.*;
import org.openide.util.*;

/** Children of ActivationSystemNode.
 *
 * @author  mryzl, Jan Pokorsky
 * @version 
 */
public class ActivationSystemChildren extends org.openide.nodes.Children.Keys implements PropertyChangeListener {

    private ActivationSystemItem item;
    /** Weak reference to ActivationSystemChildren. */
    private PropertyChangeListener listener;
    
    /** Creates new ActivationSystemChildren */
    public ActivationSystemChildren(ActivationSystemItem item) {
        this.item = item;
    }

    public void addNotify() {
        setKeysImpl(item.getActivationGroupItems());
        listener = WeakListener.propertyChange(this, item);
        item.addPropertyChangeListener(listener);
    }
    
    public void removeNotify() {
        setKeys(java.util.Collections.EMPTY_SET);
        item.removePropertyChangeListener(listener);
        listener = null;
    }
    
    private void setKeysImpl(Collection col) {
        if (col == null) col = Collections.EMPTY_LIST;
        List l = new ArrayList(col);
        Collections.sort(l);
        setKeys(l);
    }
    
    
    
    /** Create nodes for a given key.
     * @param key the key
     * @return child nodes for this key
     */
    protected Node[] createNodes(Object key) {
        if (key instanceof ActivationObjectItem) {
            return new Node[] { new ActivationObjectNode((ActivationObjectItem) key) };
        }
        if (key instanceof ActivationGroupItem) {
            return new Node[] { new ActivationGroupNode((ActivationGroupItem) key) };
        }
        return new Node[] { new ActivationNode(Children.LEAF, (ActivationItem) key) };
    }
    
    /** Activation items changed.
     * @param pce event
     */
    public void propertyChange(java.beans.PropertyChangeEvent pce) {
        if (ActivationSystemItem.PROP_ACTIVATION_ITEMS.equals(pce.getPropertyName())) {
            setKeysImpl(item.getActivationGroupItems());
        }
    }
    
}
