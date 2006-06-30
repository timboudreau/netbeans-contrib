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
